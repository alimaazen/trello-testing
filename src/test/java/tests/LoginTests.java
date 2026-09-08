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
        // Perform login using credentials from config.properties
        performLogin();
        
        // Verify user is logged in successfully
        Assert.assertTrue(dashboardPage.isUserLoggedIn(), 
            "User should be logged in successfully");
        
        // Verify dashboard is loaded
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), 
            "Dashboard should be loaded after successful login");
        
        // Verify URL contains expected pattern
        String currentUrl = dashboardPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("trello.com"), 
            "URL should contain trello.com after login");
        
        System.out.println("Test Passed: User logged in successfully");
    }
    
    /**
     * Test Case: Invalid Login - Wrong Email
     * Verifies that login fails with invalid email
     * Note: Trello redirects to signup page for unregistered emails
     */
    @Test(priority = 2, description = "Verify login fails with invalid email")
    public void testInvalidEmailLogin() {
        // Navigate to login page
        navigateToLoginPage();
        
        // Enter invalid email
        loginPage.enterEmail("invalid_email@test.com");
        loginPage.clickContinueAfterEmail();
        
        // Wait a bit for redirect or error
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Verify we're either on signup page or see an error
        String currentUrl = driver.getCurrentUrl();
        boolean isLoginFailed = currentUrl.contains("signup") || 
                                loginPage.isErrorMessageDisplayed() ||
                                loginPage.isLoginFormDisplayed();
        
        Assert.assertTrue(isLoginFailed, 
            "Login should fail with invalid email - redirected to signup or error shown");
        
        System.out.println("Test Passed: Login failed as expected with invalid email");
    }
    
    /**
     * Test Case: Invalid Login - Wrong Password
     * Verifies that login fails with invalid password
     */
    @Test(priority = 3, description = "Verify login fails with invalid password")
    public void testInvalidPasswordLogin() {
        // Get valid email from config
        String validEmail = config.getProperty("trello.email");
        
        // Attempt login with valid email but wrong password
        performLogin(validEmail, "WrongPassword123!");
        
        // Verify error message is displayed or login form is still visible
        Assert.assertTrue(loginPage.isLoginFormDisplayed() || loginPage.isErrorMessageDisplayed(), 
            "Login should fail with invalid password");
        
        // Optionally verify error message text
        if (loginPage.isErrorMessageDisplayed()) {
            String errorMessage = loginPage.getErrorMessage();
            Assert.assertFalse(errorMessage.isEmpty(), 
                "Error message should be displayed for invalid credentials");
            System.out.println("Error message displayed: " + errorMessage);
        }
        
        System.out.println("Test Passed: Login failed as expected with invalid password");
    }
    
    /**
     * Test Case: Empty Credentials
     * Verifies that login fails when credentials are empty
     */
    @Test(priority = 4, description = "Verify login fails with empty credentials")
    public void testEmptyCredentialsLogin() {
        // Navigate to login page
        navigateToLoginPage();
        
        // Attempt to login with empty credentials
        loginPage.enterEmail("");
        
        // Verify login form is still displayed
        Assert.assertTrue(loginPage.isLoginFormDisplayed(), 
            "Login form should still be visible with empty credentials");
        
        System.out.println("Test Passed: Login prevented with empty credentials");
    }
    
    /**
     * Test Case: Logout Functionality
     * Verifies that user can successfully logout
     */
    @Test(priority = 5, description = "Verify user can logout successfully")
    public void testLogout() {
        // First login with valid credentials
        performLogin();
        
        // Verify user is logged in
        Assert.assertTrue(dashboardPage.isUserLoggedIn(), 
            "User should be logged in before logout");
        
        // Perform logout
        dashboardPage.logout();
        
        // Wait a bit for logout to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Verify user is redirected (URL should change or login form should appear)
        String currentUrl = driver.getCurrentUrl();
        boolean isLoggedOut = currentUrl.contains("login") || 
                             currentUrl.contains("logged-out") ||
                             !dashboardPage.isUserAvatarDisplayed();
        
        Assert.assertTrue(isLoggedOut, 
            "User should be logged out successfully");
        
        System.out.println("Test Passed: User logged out successfully");
    }
}
