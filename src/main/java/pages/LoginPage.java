package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for Trello Login Page
 * Contains all locators and methods related to login functionality
 */
public class LoginPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Locators
    // Trello uses a two-step login: email first, then password appears
    private By emailInputLocator = By.id("username-uid1");
    private By continueButtonLocator = By.id("login-submit");
    private By passwordInputLocator = By.id("password");
    private By loginButtonLocator = By.id("login-submit");
    private By errorMessageLocator = By.id("login-error");
    private By loginFormLocator = By.id("form-login");
    
    // Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }
    
    /**
     * Performs complete login flow for Trello
     * Handles the two-step login process (email first, then password)
     * 
     * @param email User email address
     * @param password User password
     */
    public void login(String email, String password) {
        enterEmail(email);
        clickContinueAfterEmail();
        enterPassword(password);
        clickLogin();
    }
    
    /**
     * Enter email in the email field
     * 
     * @param email User email address
     */
    public void enterEmail(String email) {
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInputLocator));
        emailInput.clear();
        emailInput.sendKeys(email);
    }
    
    /**
     * Click continue button after entering email
     */
    public void clickContinueAfterEmail() {
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(continueButtonLocator));
        continueButton.click();
    }
    
    /**
     * Enter password in the password field
     * 
     * @param password User password
     */
    public void enterPassword(String password) {
        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInputLocator));
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }
    
    /**
     * Click login button after entering password
     */
    public void clickLogin() {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(loginButtonLocator));
        loginButton.click();
    }
    
    /**
     * Get error message displayed on login page
     * Useful for negative test scenarios
     * 
     * @return Error message text
     */
    public String getErrorMessage() {
        try {
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessageLocator));
            return errorMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Check if login form is displayed
     * 
     * @return true if login form is visible, false otherwise
     */
    public boolean isLoginFormDisplayed() {
        try {
            return driver.findElement(loginFormLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if error message is displayed
     * 
     * @return true if error message is visible, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        try {
            return driver.findElement(errorMessageLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
