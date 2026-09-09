package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Saves a browser screenshot to test-output/screenshots/ whenever a test fails.
 * Registered via @Listeners on BaseTest, so every test class inherits it.
 */
public class ScreenshotListener implements ITestListener {

    private static final Logger LOG = Logger.getLogger(ScreenshotListener.class.getName());
    private static final Path SCREENSHOT_DIR = Path.of("test-output", "screenshots");
    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = BaseTest.currentDriver();
        if (!(driver instanceof TakesScreenshot)) {
            return;
        }
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            String fileName = "%s_%s_%s.png".formatted(
                    result.getTestClass().getRealClass().getSimpleName(),
                    result.getMethod().getMethodName(),
                    TIMESTAMP.format(LocalDateTime.now()));
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path target = SCREENSHOT_DIR.resolve(fileName);
            Files.copy(screenshot.toPath(), target);
            LOG.warning("Test failed - screenshot saved: " + target);
        } catch (IOException e) {
            LOG.warning("Could not save screenshot: " + e.getMessage());
        }
    }
}
