package pages;

import org.openqa.selenium.By;
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
    private By accountMenuLocator = By.id("header-member-menu-avatar");
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
     * Perform logout operation
     * Two-step process: 
     * 1. Click account menu -> logout option
     * 2. Click logout-submit button on confirmation page
     */
    public void logout() {
        try {
            // Step 1: Click on account menu button
            WebElement accountMenu = wait.until(ExpectedConditions.elementToBeClickable(accountMenuLocator));
            accountMenu.click();
            
            // Step 2: Click logout button in menu
            WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(logoutButtonLocator));
            logoutButton.click();
            
            // Step 3: Click final logout submit button on confirmation page
            WebElement logoutSubmitButton = wait.until(ExpectedConditions.elementToBeClickable(logoutSubmitButtonLocator));
            logoutSubmitButton.click();
        } catch (Exception e) {
            System.out.println("Logout failed: " + e.getMessage());
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
}
