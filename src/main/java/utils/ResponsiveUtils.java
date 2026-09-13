package utils;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Shared helpers for UI/UX and cross-browser layout validation.
 * Selenium's built-in waits/conditions cover "is this element there and clickable" -
 * these fill the gap for "did the page layout actually break at this viewport size".
 */
public class ResponsiveUtils {

    private ResponsiveUtils() {
    }

    /**
     * Resizes the browser viewport. Overrides whatever size BaseTest set in @BeforeMethod.
     */
    public static void resizeViewport(WebDriver driver, int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
    }

    /**
     * Detects unwanted horizontal scroll - a common symptom of a broken responsive layout
     * (an element wider than the viewport forcing the whole page to scroll sideways).
     * A 1px tolerance absorbs sub-pixel rounding differences between browsers.
     */
    public static boolean hasHorizontalOverflow(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long scrollWidth = ((Number) js.executeScript("return document.documentElement.scrollWidth;")).longValue();
        long clientWidth = ((Number) js.executeScript("return document.documentElement.clientWidth;")).longValue();
        return scrollWidth > clientWidth + 1;
    }

    /**
     * Checks the element's bounding box is entirely within the current viewport.
     * isDisplayed() alone would still report true for an element pushed off-screen -
     * Selenium auto-scrolls to click it, which hides exactly the kind of clipping bug
     * this module exists to catch.
     */
    public static boolean isVisibleWithinViewport(WebDriver driver, WebElement element) {
        String script =
                "var r = arguments[0].getBoundingClientRect();" +
                "var vw = window.innerWidth || document.documentElement.clientWidth;" +
                "var vh = window.innerHeight || document.documentElement.clientHeight;" +
                "return r.top >= 0 && r.left >= 0 && r.bottom <= vh && r.right <= vw;";
        Object result = ((JavascriptExecutor) driver).executeScript(script, element);
        return Boolean.TRUE.equals(result);
    }
}
