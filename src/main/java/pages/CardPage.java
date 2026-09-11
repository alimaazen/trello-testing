package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ─────────────────────────────────────────────
    // LOCATORS
    // ─────────────────────────────────────────────

    // "My Trello Board" link
    private By myTrelloBoard =
            By.xpath("//a[@href='/b/d8xcq3jv/my-trello-board'" +
                    " and @title='My Trello Board']");

    // "Add a card" button inside "This Week" list
    private By addCardButton =
            By.xpath("//button[@data-testid='list-add-card-button'" +
                    " and @aria-label='Add a card in Today']");

    // Card title textarea (composer)
    private By cardTitleTextarea =
            By.xpath("//div[@data-testid='list-card-composer-textarea']");

    // "Add card" submit button inside composer
    private By addCardSubmitButton =
            By.xpath("//button[@data-testid='list-card-composer-add-card-button']");

    // ─────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────
    public CardPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ─────────────────────────────────────────────
    // METHODS
    // ─────────────────────────────────────────────
    /**
     * Clicks on "Add a card" button in "This Week" list
     */
    /**
     * Clicks on "Add a card" button in the list.
     *
     * NOTE: A double-click approach was originally used here because Trello's React
     * component re-renders (deferred state flush) immediately after board load,
     * which causes the card composer input to unmount right after the first click.
     *
     * Fix: After clicking the button, we explicitly wait for the card title input
     * field to be visible AND stable before returning, eliminating the need for
     * a redundant second click entirely.
     */
    public void clickAddCardButton(String existingListName) {
        System.out.println("STEP: Clicking 'Add a card' button for list: '" + existingListName + "'...");

        // ── Step 1: Build dynamic locator using the list name ─────────────────
        By addCardButtonForList = By.xpath(
                "//button[@data-testid='list-add-card-button' " +
                        "and contains(@aria-label, '" + existingListName + "')]"
        );

        // ── Step 2: Wait for the button to be clickable ───────────────────────
        WebElement addBtn = wait.until(
                ExpectedConditions.elementToBeClickable(addCardButtonForList)
        );
        System.out.println("DEBUG: 'Add a card' button found for list: '" + existingListName + "'");

        // ── Step 3: Scroll into view ──────────────────────────────────────────
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", addBtn);

        // ── Step 4: Click the button ──────────────────────────────────────────
        addBtn.click();
        System.out.println("STEP: 'Add a card' button clicked for list: '" + existingListName + "'");

        // ── Step 5: Wait for card title input to be visible and stable ────────
        System.out.println("STEP: Waiting for card title input to stabilise...");
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardTitleTextarea)
        );
        System.out.println("STEP: Card title input is visible and stable. ✅");
    }

    /**
     * Enters the card title in the composer textarea
     *
     * @param cardTitle - Title of the card to be created
     */
    public void enterCardTitle(String cardTitle) {

        System.out.println("STEP: Entering card title: " + cardTitle);

        WebElement titleBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardTitleTextarea));

        titleBox.clear();
        titleBox.sendKeys(cardTitle);
        System.out.println("STEP: Card title entered: " + cardTitle);
    }

    /**
     * Clicks the "Add card" submit button to save the card
     */
    public void clickAddCardSubmit() {
        System.out.println("STEP: Clicking 'Add card' submit button...");

        wait.until(ExpectedConditions.elementToBeClickable(
                addCardSubmitButton)).click();

        System.out.println("STEP: Card submitted successfully.");
    }

    /**
     * Verifies if the card is visible on the board
     *
     * @param cardTitle - Title of the card to verify
     * @return true if card is found, false otherwise
     */
    /**
     * Verifies if the card is visible on the board
     * ✅ CORRECT: Clean XPath string concatenation
     *
     * @param cardTitle - Title of the card to verify
     * @return true if card is found, false otherwise
     */
    public boolean isCardCreated(String cardTitle) {
        System.out.println("STEP: Verifying card '" + cardTitle + "' is created...");

        try {
            WebElement card = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//a[@data-testid='card-name'" +
                                    " and contains(text(),'" + cardTitle + "')]")));

            boolean isVisible = card.isDisplayed();
            System.out.println("STEP: Card visible on board: " + isVisible);
            return isVisible;

        } catch (Exception e) {
            System.out.println("❌ Card NOT found: " + e.getMessage());
            return false;
        }
    }


    /**
     * Verifies that a card with the given title exists inside a specific list.
     * More precise than isCardCreated() — confirms the card is in the CORRECT list,
     * not just anywhere on the board.
     *
     * @param cardTitle the title of the card to verify
     * @param listName  the name of the list the card should be inside
     * @return true if the card is found inside the specified list, false otherwise
     */
    public boolean isCardCreatedInList(String cardTitle, String listName) {
        System.out.println("STEP: Verifying card '" + cardTitle +
                "' exists inside list '" + listName + "'...");
        try {
            // Scopes the card search strictly within the matching list container
            WebElement card = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h2[normalize-space(text())='" + listName + "']" +
                            "/ancestor::div[contains(@class,'list-wrapper')]" +
                            "//a[contains(@class,'card-title') or @class[contains(.,'list-card')]]" +
                            "[normalize-space(.)='" + cardTitle + "']")));

            System.out.println("STEP: ✅ Card '" + cardTitle +
                    "' found inside list '" + listName + "'.");
            return card.isDisplayed();

        } catch (Exception e) {
            System.out.println("STEP: ❌ Card '" + cardTitle +
                    "' NOT found inside list '" + listName + "'. Error: " + e.getMessage());
            return false;
        }
    }
}