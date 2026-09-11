package tests;

import utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BoardPage;
import pages.CardPage;
import pages.ListPage;
import utils.TestData;

public class CardDetailsTest extends BaseTest {

    private static final String FIXED_BOARD_NAME = "Card Attributes Test Board";
    private static final String FIXED_LIST_NAME = "To Do";
    private static final String FIXED_CARD_NAME = "Card Attributes Test Card";
    private static final String ATTACHMENT_URL = "https://www.atlassian.com/software/trello";

    private CardPage cardPage;
    private BoardPage boardPage;
    private ListPage listPage;

    /**
     * Ensures a logged-in session with the fixture board/list/card ready before every test.
     * Reuses the board/list/card if they already exist in the account instead of recreating
     * them on every run - the framework hands each @Test a brand-new browser, so this has to
     * run before every single test method, not just once per class.
     */
    @BeforeMethod
    public void ensureFixtureReady() {
        performLogin();

        boardPage = new BoardPage(driver);
        listPage = new ListPage(driver);
        cardPage = new CardPage(driver);

        if (dashboardPage.isBoardPresent(FIXED_BOARD_NAME)) {
            System.out.println("FIXTURE: Board already exists, opening it.");
            dashboardPage.openBoard(FIXED_BOARD_NAME);
        } else {
            System.out.println("FIXTURE: Board not found, creating it.");
            boardPage.createNewBoard(FIXED_BOARD_NAME);
        }
        dismissCookieBannerIfPresent();

        TestData.boardName = FIXED_BOARD_NAME;
        TestData.listName = FIXED_LIST_NAME;
        TestData.cardName = FIXED_CARD_NAME;

        if (!listPage.isListCreated(FIXED_LIST_NAME)) {
            System.out.println("FIXTURE: List not found, creating it.");
            listPage.clickAddListButton();
            listPage.enterListName(FIXED_LIST_NAME);
            listPage.clickAddListSubmit();
            Assert.assertTrue(listPage.isListCreated(FIXED_LIST_NAME), "Fixture list could not be created.");
        }

        if (!cardPage.isCardCreated(FIXED_CARD_NAME)) {
            System.out.println("FIXTURE: Card not found, creating it.");
            cardPage.clickAddCardButton();
            cardPage.enterCardTitle(FIXED_CARD_NAME);
            cardPage.clickAddCardSubmit();
            Assert.assertTrue(cardPage.isCardCreated(FIXED_CARD_NAME), "Fixture card could not be created.");
        }
    }

    @Test
    public void addDescription() {

        String description = "This is an automated card description. and Script was automatically updated over thier.";

        cardPage.openCard(TestData.cardName);
        cardPage.clickDescription();
        cardPage.addDescription(description);
        cardPage.savedescription();

        Assert.assertTrue(
                cardPage.isSavedDescriptionDisplayed(description),
                "Saved description was not displayed correctly."
        );
        cardPage.closeCard();

        // Reopen and verify persistence
        cardPage.openCard(TestData.cardName);
        Assert.assertTrue(
                cardPage.isSavedDescriptionDisplayed(description),
                "Description did not persist after reopening the card."
        );
        cardPage.closeCard();
    }

    //opening the label and adding the geen label to the card which was created
    @Test
    public void addGreenLabel() {

        cardPage.openCard(TestData.cardName);
        cardPage.ensureLabelApplied("green");
        Assert.assertTrue(
                cardPage.isLabelApplied("green"),
                "Green label was not applied to the card."
        );
        cardPage.closeCard();

        // Reopen and verify persistence
        cardPage.openCard(TestData.cardName);
        Assert.assertTrue(
                cardPage.isLabelApplied("green"),
                "Green label did not persist after reopening the card."
        );
        cardPage.closeCard();
    }

    @Test
    public void setDueDate() {

        cardPage.openCard(TestData.cardName);

        cardPage.clickDates();
        cardPage.enterDueDate("9/15/2026");
        cardPage.saveDueDate();

        Assert.assertTrue(
                cardPage.isDueDateDisplayed("Sep 15"),
                "Due date was not displayed correctly."
        );
        cardPage.closeCard();

        // Reopen and verify persistence
        cardPage.openCard(TestData.cardName);
        Assert.assertTrue(
                cardPage.isDueDateDisplayed("Sep 15"),
                "Due date did not persist after reopening the card."
        );
        cardPage.closeCard();
    }

    @Test
    public void addChecklist() {

        String checklistName = "Automation Checklist";
        String checklistItem = "Verify Trello automation";

        cardPage.openCard(TestData.cardName);
        cardPage.clickChecklist();

        if (!cardPage.isChecklistPresent(checklistName)) {
            cardPage.enterChecklistName(checklistName);
            cardPage.addChecklist();
        }

        if (!cardPage.isChecklistItemDisplayed(checklistItem)) {
            cardPage.enterChecklistItem(checklistItem);
            cardPage.addChecklistItem();
        }

        Assert.assertTrue(
                cardPage.isChecklistItemDisplayed(checklistItem),
                "Checklist item was not displayed correctly."
        );

        cardPage.checkChecklistItem(checklistItem);

        Assert.assertTrue(
                cardPage.isChecklistItemChecked(checklistItem),
                "Checklist item was not checked successfully."
        );
        cardPage.closeCard();

        // Reopen and verify persistence
        cardPage.openCard(TestData.cardName);
        Assert.assertTrue(
                cardPage.isChecklistItemDisplayed(checklistItem),
                "Checklist item did not persist after reopening the card."
        );
        Assert.assertTrue(
                cardPage.isChecklistItemChecked(checklistItem),
                "Checklist item's checked state did not persist after reopening the card."
        );
        cardPage.closeCard();
    }

    @Test
    public void addLinkAttachment() {

        cardPage.openCard(TestData.cardName);
        cardPage.ensureLinkAttached(ATTACHMENT_URL);

        Assert.assertTrue(
                cardPage.isAttachmentPresent(ATTACHMENT_URL),
                "Link attachment was not displayed correctly."
        );
        cardPage.closeCard();

        // Reopen and verify persistence
        cardPage.openCard(TestData.cardName);
        Assert.assertTrue(
                cardPage.isAttachmentPresent(ATTACHMENT_URL),
                "Link attachment did not persist after reopening the card."
        );
        cardPage.closeCard();
    }

    @Test
    public void setCoverColor() {

        cardPage.openCard(TestData.cardName);
        cardPage.ensureCoverApplied();

        Assert.assertTrue(
                cardPage.isCoverApplied(),
                "Cover color was not applied to the card."
        );
        cardPage.closeCard();

        // Reopen and verify persistence
        cardPage.openCard(TestData.cardName);
        Assert.assertTrue(
                cardPage.isCoverApplied(),
                "Cover color did not persist after reopening the card."
        );
        cardPage.closeCard();
    }

    /*@Test
    public void removeCardLabel() {

        cardPage.openCard(TestData.cardName);

        String labelColor = "green";

        // Verify the green label is already present
        Assert.assertTrue(
                cardPage.isLabelApplied(labelColor),
                "Green label is not present on the card."
        );

        // Click the already-applied label
        cardPage.clickAppliedLabel(labelColor);

        // Select the green label to remove it
        cardPage.selectLabel(labelColor);

        Assert.assertFalse(
                cardPage.isLabelApplied(labelColor),
                "Green label was not removed from the card."
        );

        cardPage.closeCard();
    }*/

}
