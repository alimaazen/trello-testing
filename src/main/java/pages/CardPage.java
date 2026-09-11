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
}