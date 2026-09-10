package base;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import pages.LoginPage;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    @BeforeClass
    public void setUp() {

        System.out.println("STEP 1: Starting Chrome (fresh session, no cached profile)...");

        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        // No user-data-dir — each run starts with a clean, cookie-free session
        options.addArguments("--disable-blink-features=AutomationControlled");

        driver = new ChromeDriver(options);

        System.out.println("STEP 2: Chrome started.");

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        System.out.println("STEP 3: Opening Trello...");

        driver.get("https://trello.com/");

        System.out.println("STEP 4: Trello opened.");

        // Dismiss the cookie consent banner if present
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement acceptBtn = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[data-testid='accept-all-button']")));
            acceptBtn.click();
            System.out.println("Dismissed cookie consent banner.");
        } catch (Exception e) {
            System.out.println("Cookie banner not found or already dismissed.");
        }

        System.out.println("STEP 5: Performing fresh login...");
        try {
            LoginPage loginPage = new LoginPage(driver);
            String email = "goudaakash677@gmail.com";
            String password = "Akash@2003";

            String currentUrl = driver.getCurrentUrl();

            // If we are not already on the login page, click 'Log in' from the home page
            if (!currentUrl.contains("login")) {
                loginPage.clickLogin();
            }

            loginPage.enterEmail(email);
            loginPage.clickContinue();
            loginPage.enterPassword(password);
            loginPage.clickLoginSubmit();

            System.out.println("Login submitted. Waiting for dashboard...");
            // Wait for redirect to the boards/home dashboard
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.or(
                            ExpectedConditions.urlContains("/boards"),
                            ExpectedConditions.urlContains("/u/"),
                            ExpectedConditions.urlContains("/home")
                    ));
            System.out.println("STEP 6: Login successful. Current URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("Login sequence failed: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver == null) return;

        System.out.println("TEARDOWN: Logging out and clearing session...");

        try {
            // Navigate to Trello's logout endpoint to invalidate the server-side session
            driver.get("https://trello.com/logout");
            System.out.println("Logout request sent.");
        } catch (Exception e) {
            System.out.println("Logout navigation failed: " + e.getMessage());
        }

        try {
            // Delete all cookies so no session data persists in the browser
            driver.manage().deleteAllCookies();
            System.out.println("All cookies cleared.");
        } catch (Exception e) {
            System.out.println("Cookie deletion failed: " + e.getMessage());
        }

        driver.quit();
        System.out.println("TEARDOWN: Browser closed.");
    }
}