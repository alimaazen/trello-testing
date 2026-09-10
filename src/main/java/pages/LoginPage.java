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
    // Trello uses a two-step login: email first, then password appears.
    // The email field is matched by id OR name - Atlassian's form library
    // generates the id ("username") and uid-suffixed variants that can shift.
    private By emailInputLocator = By.xpath("//input[@id='username' or @name='username']");
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
     * @param email    User email address
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
     * Instant check whether the continue button is enabled.
     * Lets tests distinguish "button disabled by validation" from "button clicked, nothing happened".
     *
     * @return true if the continue button is present and enabled
     */
    public boolean isContinueButtonEnabled() {
        try {
            return driver.findElement(continueButtonLocator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check whether the flow advanced past the email step (password field visible).
     * Used by negative tests: with an invalid/empty email this must stay false.
     *
     * @return true if the password step is displayed
     */
    public boolean isPasswordStepDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInputLocator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get error message displayed on login page
     * Useful for negative test scenarios
     *
     * @return Error message text, or empty string if none appears
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
