package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
}