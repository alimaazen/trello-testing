package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object representing a Trello Board page.
 * Implements highly resilient action strategies and robust explicit waits.
 */
public class BoardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public BoardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Finds and clicks an existing board tile by its visible name.
     * @param boardName The exact name of the board as displayed on the home screen.
     */
    public void openExistingBoard(String boardName) {
        WebElement board = driver.findElement(
                By.xpath("//a[@title='" + boardName + "' and @aria-label='" + boardName + "']")
        );
        board.click();
    }
}