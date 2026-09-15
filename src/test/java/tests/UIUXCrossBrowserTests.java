package tests;

import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.BoardPage;
import pages.CardPage;
import pages.ListPage;
import utils.BaseTest;
import utils.ResponsiveUtils;
import utils.TestData;

import java.util.List;

/**
 * UI/UX & Cross-Browser Validation module.
 *
 * Scope: rendering and layout validation at three representative viewport widths -
 * 1920 (desktop), 1280 (laptop/tablet), 375 (mobile) - verifying key elements stay
 * visible, clickable, and that the layout does not break (no unintended horizontal
 * scroll) as the viewport shrinks.
 *
 * Cross-browser: BaseTest already picks the browser from the "browser" key (env var
 * BROWSER, or config.properties). Run this class once per browser - e.g.
 * `setx BROWSER chrome && mvn test -Dtest=UIUXCrossBrowserTests`, then again with
 * firefox / edge - to cover the full matrix.
 */
public class UIUXCrossBrowserTests extends BaseTest {

    private static final String FIXED_BOARD_NAME = "UI UX Cross Browser Test Board";
    private static final String FIXED_LIST_NAME = "To Do";
    private static final String FIXED_CARD_NAME = "UI UX Test Card";
    private static final String FIXED_CHECKLIST_NAME = "UI UX Checklist";
    private static final String FIXED_CHECKLIST_ITEM = "Verify layout";

    private BoardPage boardPage;
    private ListPage listPage;
    private CardPage cardPage;

    @DataProvider(name = "viewports")
    public Object[][] viewports() {
        return new Object[][]{
                {"Desktop-1920x1080", 1920, 1080},
                {"Laptop-1280x800", 1280, 800},
                {"Mobile-375x812", 375, 812}
        };
    }

    /**
     * Logs in and makes sure the fixture board/list/card exist, reusing them across runs
     * instead of creating new ones every time - same pattern as CardDetailsTest. Only
     * called by the tests that actually need a board open (not the login/dashboard ones).
     */
    private void ensureFixtureReady() {
        performLogin();

        boardPage = new BoardPage(driver);
        listPage = new ListPage(driver);
        cardPage = new CardPage(driver);

        if (dashboardPage.isBoardPresent(FIXED_BOARD_NAME)) {
            dashboardPage.openBoard(FIXED_BOARD_NAME);
        } else {
            boardPage.createNewBoard(FIXED_BOARD_NAME);
        }
        dismissCookieBannerIfPresent();

        TestData.boardName = FIXED_BOARD_NAME;
        TestData.listName = FIXED_LIST_NAME;
        TestData.cardName = FIXED_CARD_NAME;

        if (!listPage.isListCreated(FIXED_LIST_NAME)) {
            clickWithRetry(listPage::clickAddListButton, "add-list button");
            listPage.enterListName(FIXED_LIST_NAME);
            listPage.clickAddListSubmit();
            Assert.assertTrue(listPage.isListCreated(FIXED_LIST_NAME), "Fixture list could not be created.");
        }

        if (!cardPage.isCardCreated(FIXED_CARD_NAME)) {
            clickWithRetry(cardPage::clickAddCardButton, "add-card button");
            cardPage.enterCardTitle(FIXED_CARD_NAME);
            cardPage.clickAddCardSubmit();
            Assert.assertTrue(cardPage.isCardCreated(FIXED_CARD_NAME), "Fixture card could not be created.");
        }
    }

    /**
     * Trello's empty-list placeholder illustration fades in/out right where the
     * "Add a card"/"Add a list" buttons render, so a click can land mid-transition
     * and get intercepted even though the element was reported clickable a moment
     * earlier. Re-locating and retrying (via the page object's own method, so the
     * locator logic stays in the page object) clears it within a couple of tries.
     */
    private void clickWithRetry(Runnable click, String description) {
        int attempts = 0;
        while (true) {
            try {
                click.run();
                return;
            } catch (ElementClickInterceptedException e) {
                attempts++;
                if (attempts >= 4) {
                    throw e;
                }
                System.out.println("Click on " + description + " was intercepted, retrying (attempt " + (attempts + 1) + ")...");
            }
        }
    }

    // ─────────────────────────────────────────────
    // Login page
    // ─────────────────────────────────────────────
    @Test(dataProvider = "viewports", description = "Login page stays usable and doesn't overflow at every viewport width")
    public void testLoginPageLayout(String viewportName, int width, int height) {
        navigateToLoginPage();
        ResponsiveUtils.resizeViewport(driver, width, height);

        WebElement emailInput = loginPage.getEmailInputElement();
        WebElement continueButton = loginPage.getContinueButtonElement();

        Assert.assertTrue(emailInput.isDisplayed(), viewportName + ": email input should be visible");
        Assert.assertTrue(continueButton.isDisplayed() && continueButton.isEnabled(),
                viewportName + ": continue button should be visible and clickable");
        Assert.assertFalse(ResponsiveUtils.hasHorizontalOverflow(driver),
                viewportName + ": login page should not have horizontal overflow");
    }

    // ─────────────────────────────────────────────
    // Dashboard
    // ─────────────────────────────────────────────
    @Test(dataProvider = "viewports", description = "Dashboard header and create-board CTA stay visible/clickable at every viewport width")
    public void testDashboardLayout(String viewportName, int width, int height) {
        performLogin();
        ResponsiveUtils.resizeViewport(driver, width, height);
        dismissCookieBannerIfPresent();

        WebElement header = dashboardPage.getHeaderElement();
        WebElement createBoardButton = dashboardPage.getCreateBoardButtonElement();

        Assert.assertTrue(header.isDisplayed(), viewportName + ": dashboard header should be visible");
        Assert.assertTrue(createBoardButton.isDisplayed() && createBoardButton.isEnabled(),
                viewportName + ": create-board button should be visible and clickable");
        Assert.assertFalse(ResponsiveUtils.hasHorizontalOverflow(driver),
                viewportName + ": dashboard should not have horizontal overflow");
    }

    // ─────────────────────────────────────────────
    // Board page
    // ─────────────────────────────────────────────
    @Test(dataProvider = "viewports", description = "Board title and add-list control stay visible/clickable at every viewport width")
    public void testBoardPageLayout(String viewportName, int width, int height) {
        ensureFixtureReady();
        ResponsiveUtils.resizeViewport(driver, width, height);

        // getBoardTitle() already waits for visibility internally, so a successful
        // return (matching the fixture board's name) proves the title is rendered -
        // no WebElement getter needed here.
        String boardTitle = boardPage.getBoardTitle();
        WebElement addListButton = listPage.getAddListButtonElement();

        Assert.assertEquals(boardTitle, FIXED_BOARD_NAME, viewportName + ": board title should be visible and correct");
        Assert.assertTrue(addListButton.isDisplayed() && addListButton.isEnabled(),
                viewportName + ": add-list control should be visible and clickable");
        Assert.assertFalse(ResponsiveUtils.hasHorizontalOverflow(driver),
                viewportName + ": board page should not have unintended horizontal overflow");
    }

    // ─────────────────────────────────────────────
    // Card modal
    // ─────────────────────────────────────────────
    @Test(dataProvider = "viewports", description = "Card modal stays fully usable (closeable) at every viewport width")
    public void testCardModalLayout(String viewportName, int width, int height) {
        ensureFixtureReady();
        ResponsiveUtils.resizeViewport(driver, width, height);

        cardPage.openCard(FIXED_CARD_NAME);

        WebElement closeButton = cardPage.getCloseCardButtonElement();
        WebElement descriptionArea = cardPage.getDescriptionAreaElement();

        Assert.assertTrue(descriptionArea.isDisplayed(), viewportName + ": card description area should be visible");
        Assert.assertTrue(closeButton.isDisplayed() && closeButton.isEnabled(),
                viewportName + ": close button should be visible and clickable");
        Assert.assertTrue(ResponsiveUtils.isVisibleWithinViewport(driver, closeButton),
                viewportName + ": close button should be within the visible viewport, not clipped off-screen");

        cardPage.closeCard();
    }

    // ─────────────────────────────────────────────
    // Checklist panel
    // ─────────────────────────────────────────────
    @Test(dataProvider = "viewports", description = "Checklist item input and add button stay usable, and the panel doesn't overflow, at every viewport width")
    public void testChecklistPanelLayout(String viewportName, int width, int height) {
        ensureFixtureReady();
        ResponsiveUtils.resizeViewport(driver, width, height);

        cardPage.openCard(FIXED_CARD_NAME);

        if (!cardPage.isChecklistPresent(FIXED_CHECKLIST_NAME)) {
            clickWithRetry(cardPage::clickChecklist, "checklist button");
            cardPage.enterChecklistName(FIXED_CHECKLIST_NAME);
            cardPage.addChecklist();
        }

        if (!cardPage.isChecklistItemDisplayed(FIXED_CHECKLIST_ITEM)) {
            cardPage.enterChecklistItem(FIXED_CHECKLIST_ITEM);
            cardPage.addChecklistItem();
        }

        WebElement checklistItemRow = cardPage.getChecklistItemCheckboxElement(FIXED_CHECKLIST_ITEM);

        Assert.assertTrue(checklistItemRow.isDisplayed(), viewportName + ": checklist item row should be visible");
        Assert.assertTrue(checklistItemRow.isEnabled(),
                viewportName + ": checklist item row should be clickable");
        Assert.assertTrue(ResponsiveUtils.isVisibleWithinViewport(driver, checklistItemRow),
                viewportName + ": checklist item row should be within the visible viewport, not clipped off-screen");
        Assert.assertFalse(ResponsiveUtils.hasHorizontalOverflow(driver),
                viewportName + ": card modal should not have horizontal overflow with a checklist panel open");

        cardPage.closeCard();
    }

    // ─────────────────────────────────────────────
    // Cover color picker
    // ─────────────────────────────────────────────
    @Test(dataProvider = "viewports", description = "Cover color swatch grid stays fully visible/clickable and doesn't clip at every viewport width")
    public void testCoverColorPickerLayout(String viewportName, int width, int height) {
        ensureFixtureReady();
        ResponsiveUtils.resizeViewport(driver, width, height);

        cardPage.openCard(FIXED_CARD_NAME);
        clickWithRetry(cardPage::clickCover, "cover button");

        List<WebElement> swatches = cardPage.getCoverColorSwatchElements();
        Assert.assertFalse(swatches.isEmpty(), viewportName + ": cover color swatches should be present");

        WebElement firstSwatch = swatches.get(0);
        WebElement lastSwatch = swatches.get(swatches.size() - 1);

        Assert.assertTrue(firstSwatch.isDisplayed() && firstSwatch.isEnabled(),
                viewportName + ": first cover color swatch should be visible and clickable");
        Assert.assertTrue(lastSwatch.isDisplayed() && lastSwatch.isEnabled(),
                viewportName + ": last cover color swatch should be visible and clickable");
        Assert.assertTrue(ResponsiveUtils.isVisibleWithinViewport(driver, firstSwatch),
                viewportName + ": first cover color swatch should be within the visible viewport, not clipped off-screen");
        Assert.assertTrue(ResponsiveUtils.isVisibleWithinViewport(driver, lastSwatch),
                viewportName + ": last cover color swatch should be within the visible viewport, not clipped off-screen");
        Assert.assertFalse(ResponsiveUtils.hasHorizontalOverflow(driver),
                viewportName + ": card modal should not have horizontal overflow with the cover picker open");

        cardPage.closeCoverPopover();
        cardPage.closeCard();
    }
}
