package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

/**
 * Page Object Model for Trello List operations.
 * Handles all list-related interactions on the Trello board.
 */
public class ListPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ──────────────────────────────────────────
    // Locators
    // ──────────────────────────────────────────
    private By addListButton    = By.xpath("//button[contains(.,'Add another list') or contains(.,'Add list')]");
    private By listNameInput    = By.xpath("//textarea[@data-testid='list-name-textarea']");
    private By submitListButton = By.xpath("//button[@data-testid='list-composer-add-list-button']");

    // ──────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────
    public ListPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Locates an existing list on the board by its header name.
     * This does NOT click anything — it simply confirms the list is visible
     * so subsequent card actions target the correct list.
     * @param listName The exact name of the list header.
     */
    public void openExistingList(String listName) {
        WebElement list = driver.findElement(
                By.xpath("//button[@data-testid='list-add-card-button' and contains(@aria-label, 'Add a card in " + listName + "')]")
        );

        Assert.assertTrue(list.isDisplayed(),
                "SETUP FAILED: List '" + listName + "' was not found on the board!");

        list.click();
    }
    // ──────────────────────────────────────────
    // Click the "Add a list" Button
    // ──────────────────────────────────────────
    public void clickAddListButton() {
        System.out.println("ListPage: Clicking 'Add a list' button...");
        WebElement addBtn = wait.until(
                ExpectedConditions.elementToBeClickable(addListButton)
        );
        addBtn.click();
    }

    // ──────────────────────────────────────────
    // Enter the List Name
    // ──────────────────────────────────────────
    public void enterListName(String listName) {
        System.out.println("ListPage: Entering list name: " + listName);
        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(listNameInput)
        );
        nameInput.clear();
        nameInput.sendKeys(listName);
    }

    // ──────────────────────────────────────────
    // Click the Submit / "Add list" Button
    // ──────────────────────────────────────────
    public void clickAddListSubmit() {
        System.out.println("ListPage: Clicking submit button...");
        driver.findElement(submitListButton).click();
    }

    // ──────────────────────────────────────────
    // Verify the List was Created
    // ──────────────────────────────────────────
    public boolean isListCreated(String listName) {
        try {
            By specificList = By.xpath(
                    "//span[contains(text(),'" + listName + "')]"
            );
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(specificList)
            );
            System.out.println("ListPage: List '" + listName + "' found on board!");
            return true;
        } catch (Exception e) {
            System.out.println("ListPage: List '" + listName + "' NOT found!");
            return false;
        }
    }
}