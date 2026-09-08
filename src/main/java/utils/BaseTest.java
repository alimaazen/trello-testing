package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;
import pages.DashboardPage;

import java.time.Duration;

/**
 * BaseTest class provides common setup and teardown for all test classes.
 * Teammates should extend this class to inherit WebDriver setup and login functionality.
 */
public class BaseTest {
    
    protected WebDriver driver;
    protected LoginPage loginPage;
    protected DashboardPage dashboardPage;
    protected TestConfig config;
    
    @BeforeMethod
    public void setUp() {
        // Setup WebDriverManager to handle FirefoxDriver automatically
        WebDriverManager.firefoxdriver().setup();
        
        // Configure Firefox options
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--start-maximized");
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("dom.push.enabled", false);
        
        // Initialize WebDriver
        driver = new FirefoxDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().window().maximize();
        
        // Initialize configuration
        config = new TestConfig();
        
        // Initialize Page Objects
        loginPage = new LoginPage(driver);
        dashboardPage = new DashboardPage(driver);
    }
    
    /**
     * Performs login with credentials from config.properties
     * Teammates can call this method at the start of their tests
     * 
     * @return DashboardPage object for further actions
     */
    public DashboardPage performLogin() {
        String email = config.getProperty("trello.email");
        String password = config.getProperty("trello.password");
        
        driver.get(config.getProperty("trello.url"));
        loginPage.login(email, password);
        
        return dashboardPage;
    }
    
    /**
     * Overloaded method to perform login with custom credentials
     * Useful for negative testing scenarios
     * 
     * @param email User email
     * @param password User password
     * @return DashboardPage object
     */
    public DashboardPage performLogin(String email, String password) {
        driver.get(config.getProperty("trello.url"));
        loginPage.login(email, password);
        
        return dashboardPage;
    }
    
    /**
     * Navigate to Trello login page without performing login
     * Useful when teammates need to test from login page
     */
    public void navigateToLoginPage() {
        driver.get(config.getProperty("trello.url"));
    }
    
    /**
     * Get the WebDriver instance
     * 
     * @return WebDriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }
    
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
