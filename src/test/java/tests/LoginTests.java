package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import utils.BaseTest;

/**
 * Test class for Trello Login and Authentication functionality
 * Contains test cases for valid login, invalid login, and logout scenarios
 */
public class LoginTests extends BaseTest {

    /**
     * Test Case: Valid Login
     * Verifies that user can successfully login with valid credentials
     */
    @Test(priority = 1, description = "Verify successful login with valid credentials")
    public void testValidLogin() {
        performLogin();

        Assert.assertTrue(dashboardPage.isUserLoggedIn(),
                "User should be logged in successfully");

        Assert.assertTrue(dashboardPage.isDashboardLoaded(),
                "Dashboard should be loaded after successful login");

        String currentUrl = dashboardPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("trello.com"),
                "URL should contain trello.com after login");
    }

    /**
     * Test Case: Invalid Login - Unregistered Email
     * Verifies that login fails with an unregistered email.
     * Note: Trello redirects unregistered emails to the signup page.
     */
    @Test(priority = 2, description = "Verify login fails with an unregistered email")
    public void testInvalidEmailLogin() {
        navigateToLoginPage();

        loginPage.enterEmail("invalid_email@test.com");
        loginPage.clickContinueAfterEmail();

        // Trello either redirects unregistered emails to signup or shows an error.
        boolean redirectedToSignupOrErrorShown = waitUntil(d ->
                d.getCurrentUrl().contains("signup") || loginPage.isErrorMessageDisplayed());

        Assert.assertFalse(loginPage.isPasswordStepDisplayed(),
                "Password step must not be reachable with an unregistered email");

        Assert.assertTrue(redirectedToSignupOrErrorShown,
                "Expected a redirect to signup or an error message for an unregistered email");
    }

    /**
     * Test Case: Invalid Login - Wrong Password
     * Verifies that login fails with invalid password
     */
    @Test(priority = 3, description = "Verify login fails with invalid password")
    public void testInvalidPasswordLogin() {
        String validEmail = config.getProperty("trello.email");

        performLogin(validEmail, "WrongPassword123!");

        // A successful login would navigate away from the login page, so the form
        // still being shown (or an error) proves the login was rejected.
        Assert.assertTrue(loginPage.isLoginFormDisplayed() || loginPage.isErrorMessageDisplayed(),
                "Login should fail with invalid password");

        if (loginPage.isErrorMessageDisplayed()) {
            String errorMessage = loginPage.getErrorMessage();
            Assert.assertFalse(errorMessage.isEmpty(),
                    "Error message should be displayed for invalid credentials");
            System.out.println("Error message displayed: " + errorMessage);
        }
    }

    /**
     * Test Case: Empty Credentials
     * Verifies that the password step cannot be reached with an empty email
     */
    @Test(priority = 4, description = "Verify login cannot proceed with an empty email")
    public void testEmptyCredentialsLogin() {
        navigateToLoginPage();

        loginPage.enterEmail("");

        boolean reachedPasswordStep = false;
        if (loginPage.isContinueButtonEnabled()) {
            loginPage.clickContinueAfterEmail();
            reachedPasswordStep = loginPage.isPasswordStepDisplayed();
        }

        Assert.assertFalse(reachedPasswordStep,
                "Password step must not be reachable with an empty email");
    }

    /**
     * Test Case: Logout Functionality
     * Verifies that user can successfully logout
     */
    @Test(priority = 5, description = "Verify user can logout successfully")
    public void testLogout() {
        performLogin();

        Assert.assertTrue(dashboardPage.isUserLoggedIn(),
                "User should be logged in before logout");

        dashboardPage.logout();

        Assert.assertTrue(dashboardPage.isLoggedOut(),
                "User should be logged out successfully");
    }
}
