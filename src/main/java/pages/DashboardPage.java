package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for Trello Dashboard/Home Page
 * This page appears after successful login
 * Contains methods to verify login success and access common elements
 */
public class DashboardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By userAvatarLocator = By.id("header-member-menu-avatar");
    private By boardsHeaderLocator = By.cssSelector("h1[data-testid='home-sidebar-title']");
    private By createBoardButtonLocator = By.cssSelector("button[data-testid='create-board-tile']");
    private By headerLocator = By.id("header");
    private By logoutButtonLocator = By.cssSelector("button[data-testid='account-menu-logout']");
    private By logoutSubmitButtonLocator = By.id("logout-submit");

    // Constructor
    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Verify if user is logged in by checking for user avatar
     *
     * @return true if user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(userAvatarLocator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verify if dashboard page is loaded by checking header
     *
     * @return true if dashboard is loaded, false otherwise
     */
    public boolean isDashboardLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(headerLocator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the current page title
     *
     * @return Page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Get current URL
     *
     * @return Current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Check if create board button is visible
     *
     * @return true if create board button is visible
     */
    public boolean isCreateBoardButtonVisible() {
        try {
            return driver.findElement(createBoardButtonLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Perform logout operation.
     * Two-step process:
     * 1. Click account menu (avatar) -> logout option
     * 2. Click logout-submit button on confirmation page
     *
     * Failures propagate to the test - a broken logout must fail the test,
     * not disappear into a log line.
     */
    public void logout() {
        WebElement accountMenu = wait.until(ExpectedConditions.elementToBeClickable(userAvatarLocator));
        accountMenu.click();

        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(logoutButtonLocator));
        logoutButton.click();

        WebElement logoutSubmitButton = wait.until(ExpectedConditions.elementToBeClickable(logoutSubmitButtonLocator));
        logoutSubmitButton.click();
    }

    /**
     * Wait until the logout flow completes: redirected to a login/logged-out page
     * or the user avatar is gone.
     *
     * @return true if logged out in time, false on timeout
     */
    public boolean isLoggedOut() {
        try {
            return wait.until(d -> {
                String url = d.getCurrentUrl();
                return url.contains("logged-out")
                        || url.contains("login")
                        || !isUserAvatarDisplayed();
            });
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if user avatar is displayed
     *
     * @return true if avatar is visible
     */
    public boolean isUserAvatarDisplayed() {
        try {
            return driver.findElement(userAvatarLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for dashboard to fully load
     * Useful for teammates to ensure page is ready before performing actions
     */
    public void waitForDashboardToLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(headerLocator));
        wait.until(ExpectedConditions.visibilityOfElementLocated(userAvatarLocator));
    }

    /**
     * Check whether a board with the given name is already present on the dashboard.
     * Used for idempotent test fixtures - reuse an existing board instead of creating a duplicate.
     *
     * @param boardName Board name to look for
     * @return true if a board tile with this name is visible
     */
    public boolean isBoardPresent(String boardName) {
        try {
            By boardTile = By.xpath("//a[normalize-space()='" + boardName + "']");
            return wait.until(ExpectedConditions.visibilityOfElementLocated(boardTile)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Opens an existing board by name from the dashboard.
     *
     * @param boardName Board name to open
     */
    public void openBoard(String boardName) {
        By boardTile = By.xpath("//a[normalize-space()='" + boardName + "']");
        for (int attempt = 1; attempt <= 2; attempt++) {
            WebElement board = wait.until(ExpectedConditions.elementToBeClickable(boardTile));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", board);
            try {
                board.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", board);
            }
            try {
                wait.until(ExpectedConditions.urlContains("/b/"));
                return;
            } catch (TimeoutException e) {
                if (attempt == 2) {
                    throw e;
                }
            }
        }
    }
}
