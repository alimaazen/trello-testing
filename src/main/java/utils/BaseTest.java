package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import pages.DashboardPage;
import pages.LoginPage;

import java.time.Duration;
import java.util.function.Predicate;

/**
 * BaseTest provides WebDriver setup/teardown and login helpers for all test classes.
 * Extend this class and call performLogin() when your tests need a logged-in session.
 */
@Listeners(ScreenshotListener.class)
public class BaseTest {

    private static final ThreadLocal<WebDriver> CURRENT_DRIVER = new ThreadLocal<>();

    protected WebDriver driver;
    protected LoginPage loginPage;
    protected DashboardPage dashboardPage;
    protected TestConfig config;

    @BeforeClass
    public void setUp() {
        config = new TestConfig();
        driver = createDriver();
        CURRENT_DRIVER.set(driver);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().window().maximize();

        loginPage = new LoginPage(driver);
        dashboardPage = new DashboardPage(driver);
    }

    /**
     * Creates the WebDriver for the browser configured via the "browser" key
     * (firefox | chrome | edge, default firefox). Set "headless=true" for CI runs.
     */
    private WebDriver createDriver() {
        String browser = config.getProperty("browser", "firefox").toLowerCase();
        boolean headless = Boolean.parseBoolean(config.getProperty("headless", "false"));

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new", "--window-size=1920,1080");
                }
                return new ChromeDriver(chromeOptions);
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless=new");
                }
                return new EdgeDriver(edgeOptions);
            case "firefox":
            default:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("-headless");
                }
                firefoxOptions.addPreference("dom.webnotifications.enabled", false);
                firefoxOptions.addPreference("dom.push.enabled", false);
                return new FirefoxDriver(firefoxOptions);
        }
    }

    /**
     * Logs in with the configured test account and verifies the dashboard actually loaded.
     * Fails fast with a clear message if credentials are missing or login does not work.
     *
     * @return DashboardPage for further actions
     */
    public DashboardPage performLogin() {
        if (!config.hasCredentials()) {
            throw new IllegalStateException(
                    "Trello credentials are not configured. Set TRELLO_EMAIL and TRELLO_PASSWORD "
                            + "as environment variables (see README), or add trello.email / "
                            + "trello.password to src/test/resources/config.properties.");
        }
        performLogin(config.getProperty("trello.email"), config.getProperty("trello.password"));
        dismissCookieBannerIfPresent();
        if (!dashboardPage.isUserLoggedIn()) {
            throw new IllegalStateException(
                    "Login failed: dashboard did not load after submitting credentials. "
                            + "Check TRELLO_EMAIL / TRELLO_PASSWORD and make sure the test account has no 2FA.");
        }
        return dashboardPage;
    }

    /**
     * Logs in with explicit credentials WITHOUT verifying success.
     * Intended for negative tests (wrong email/password) where failing is the expected outcome.
     *
     * @param email    Email to submit
     * @param password Password to submit
     * @return DashboardPage object (only meaningful if login actually succeeded)
     */
    public DashboardPage performLogin(String email, String password) {
        driver.get(config.getProperty("trello.url"));
        loginPage.login(email, password);
        return dashboardPage;
    }

    /**
     * Navigate to the Trello login page without logging in.
     */
    public void navigateToLoginPage() {
        driver.get(config.getProperty("trello.url"));
    }

    /**
     * Dismisses the cookie-consent banner if it appears (fresh sessions only).
     * Left unhandled, the banner can intercept clicks meant for board tiles / buttons.
     */
    protected void dismissCookieBannerIfPresent() {
        try {
            java.util.List<WebElement> acceptAll = driver.findElements(By.cssSelector("[data-testid='accept-all-button']"));
            if (!acceptAll.isEmpty() && acceptAll.get(0).isDisplayed()) {
                acceptAll.get(0).click();
                Thread.sleep(500);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Waits up to 15 seconds for the condition to become true.
     * Returns false on timeout instead of throwing - useful for
     * "eventually X should happen" style assertions in tests.
     *
     * @param condition Condition evaluated repeatedly against the driver
     * @return true if the condition was met in time, false on timeout
     */
    protected boolean waitUntil(Predicate<WebDriver> condition) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(15)).until(condition::test);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Get the WebDriver instance.
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Current thread's driver, used by ScreenshotListener. Do not use in tests - use getDriver().
     */
    public static WebDriver currentDriver() {
        return CURRENT_DRIVER.get();
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        CURRENT_DRIVER.remove();
    }
}
