package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;

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
    private By addListButton    = By.xpath("//button[contains(.,'Add another list') or contains(.,'Add list') or contains(.,'Add a list')]");
    private By listNameInput    = By.xpath("//textarea[@data-testid='list-name-textarea']");
    private By submitListButton = By.xpath("//button[@data-testid='list-composer-add-list-button']");
    private By listHeader       = By.xpath("//div[@data-testid='list-header']");

    // ──────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────
    public ListPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
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
    // Get the "Add a list" element (layout/rendering assertions)
    // ──────────────────────────────────────────
    public WebElement getAddListButtonElement() {
        return wait.until(ExpectedConditions.elementToBeClickable(addListButton));
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


    // ──────────────────────────────────────────
    // Create a List (convenience wrapper around the 3 granular steps above)
    // ──────────────────────────────────────────
    public void createList(String listName) {
        clickAddListButton();
        enterListName(listName);
        clickAddListSubmit();
    }

    // ──────────────────────────────────────────
    // Rename a List
    // ──────────────────────────────────────────
    public void renameList(String oldName, String newName) {
        System.out.println("ListPage: Renaming list '" + oldName + "' to '" + newName + "'...");

        WebElement titleButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//span[normalize-space(text())='" + oldName + "']/ancestor::button[1]")
                )
        );

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", titleButton);

        new Actions(driver)
                .moveToElement(titleButton)
                .pause(Duration.ofMillis(200))
                .click()
                .perform();

        By renameInputLocator = By.xpath(
                "//span[normalize-space(text())='" + oldName + "']" +
                        "/ancestor::div[@data-testid='list-header'][1]" +
                        "//textarea[@data-testid='list-name-textarea']"
        );

        WebElement input;
        try {
            input = wait.until(webDriver -> {
                try {
                    WebElement el = webDriver.findElement(renameInputLocator);
                    return el.isDisplayed() ? el : null;
                } catch (Exception e) {
                    return null;
                }
            });
        } catch (Exception e) {
            // First click didn't register - fall back to a JS-dispatched click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", titleButton);
            input = wait.until(webDriver -> {
                try {
                    WebElement el = webDriver.findElement(renameInputLocator);
                    return el.isDisplayed() ? el : null;
                } catch (Exception ex) {
                    return null;
                }
            });
        }

        input.clear();
        input.sendKeys(newName);
        input.sendKeys(Keys.ENTER);
    }

    // ──────────────────────────────────────────
    // Archive a List
    // ──────────────────────────────────────────
    public void archiveList(String listName) {
        System.out.println("ListPage: Archiving list '" + listName + "'...");

        WebElement menuButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//*[normalize-space(text())='" + listName + "']" +
                                        "/ancestor::div[@data-testid='list-header'][1]" +
                                        "//button[@data-testid='list-edit-menu-button']"
                        )
                )
        );
        menuButton.click();

        WebElement archiveButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("[data-testid='list-actions-archive-list-button']")
                )
        );
        archiveButton.click();

        WebElement confirmButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[normalize-space()='Archive list']")
                )
        );
        confirmButton.click();

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.xpath(
                                "//div[@data-testid='list-header']//*[normalize-space(text())='" + listName + "']"
                        )
                )
        );
    }

    // ──────────────────────────────────────────
    // Copy a List
    // ──────────────────────────────────────────
    public void copyList(String originalName) {
        System.out.println("ListPage: Copying list '" + originalName + "'...");

        List<WebElement> cookieAcceptButtons = driver.findElements(
                By.cssSelector("[data-testid='accept-all-button']")
        );
        if (!cookieAcceptButtons.isEmpty() && cookieAcceptButtons.get(0).isDisplayed()) {
            cookieAcceptButtons.get(0).click();
        }

        int countBeforeCopy = getListCount();

        WebElement menuButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//*[normalize-space(text())='" + originalName + "']" +
                                        "/ancestor::div[@data-testid='list-header'][1]" +
                                        "//button[@data-testid='list-edit-menu-button']"
                        )
                )
        );
        menuButton.click();

        WebElement copyButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("[data-testid='list-actions-copy-list-button']")
                )
        );
        copyButton.click();

        WebElement popup = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-testid='list-actions-copy-list-popover']")
                )
        );

        WebElement createButton = popup.findElement(
                By.xpath(".//button[normalize-space()='Create list']")
        );
        createButton.click();

        wait.until(ExpectedConditions.invisibilityOf(popup));
        wait.until(webDriver -> getListCount() > countBeforeCopy);
    }

    // ──────────────────────────────────────────
    // Empty Title Validation
    // ──────────────────────────────────────────
    public void attemptEmptyListSubmit() {
        System.out.println("ListPage: Attempting to submit an empty list name...");

        clickAddListButton();

        WebElement input = wait.until(webDriver -> {
            List<WebElement> textareas = webDriver.findElements(
                    By.cssSelector("[data-testid='list-name-textarea']")
            );
            for (WebElement t : textareas) {
                if (t.isDisplayed()) {
                    return t;
                }
            }
            return null;
        });
        input.clear();

        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(submitListButton)
        );
        addButton.click();
    }

    // ──────────────────────────────────────────
    // Wait for the board to finish rendering its lists
    // ──────────────────────────────────────────
    // Trello's board URL loads before its list DOM elements paint (React SPA).
    // Call this before reading getListCount() right after navigating to a board,
    // otherwise you can read a count of 0 before any lists have rendered.
    public void waitForBoardToLoad() {
        wait.until(ExpectedConditions.presenceOfElementLocated(addListButton));
    }

    // ──────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────
    public int getListCount() {
        return driver.findElements(listHeader).size();
    }

    public List<String> getListOrder() {
        List<WebElement> headers = driver.findElements(
                By.xpath("//div[@data-testid='list-header']//h2[@data-testid='list-name']//span")
        );
        List<String> names = new ArrayList<>();
        for (WebElement el : headers) {
            names.add(el.getText());
        }
        return names;
    }
}