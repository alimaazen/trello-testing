package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object representing a Trello Board page.
 * Implements highly resilient action strategies and robust explicit waits.
 */
public class BoardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Resilient Locators
    private final By headerCreateMenuBtn = By.cssSelector("button[data-testid='header-create-menu-button']");
    private final By headerCreateBoardBtn = By.cssSelector("button[data-testid='create-board-button']");
    private final By boardTitleInput = By.cssSelector("[data-testid='create-board-title-input'], [placeholder='Add board title'], [placeholder*='board title'], [placeholder*='title'], [placeholder*='Title']");
    private final By finalCreateBtn = By.cssSelector("button[data-testid='create-board-submit-button']");

    private final By boardTitleDisplay = By.cssSelector("h1[data-testid='board-name-display']");
    private final By boardTitleInputField = By.cssSelector("input[data-testid='board-name-input']");
    private final By boardStarBtn = By.cssSelector("button[aria-label='Star or unstar board']");

    // Board Menu & Operations
    private final By showMenuBtn = By.cssSelector("button[aria-label='Show menu'], button[data-testid='overflow-menu-button'], button[class*='board-header-btn-menu']");
    private final By changeBackgroundBtn = By.xpath("//*[contains(@data-testid, 'change-background') or contains(text(), 'Change background') or contains(@class, 'change-background')]");
    private final By backgroundColorsOption = By.xpath("//*[contains(@data-testid, 'background-colors') or contains(text(), 'Colors') or contains(@class, 'colors')]");
    private final By colorTile = By.cssSelector("[class*='board-menu'] button[style*='background'], [class*='popover'] button[style*='background'], button[style*='background'], [class*='background-box']");

    private final By closeBoardMenuLink = By.xpath("//*[contains(@data-testid, 'close-board') or contains(text(), 'Close board') or contains(@class, 'js-close-board')]");
    private final By closeConfirmBtn = By.xpath("//button[normalize-space(.)='Close'] | //input[@value='Close'] | //*[contains(@data-testid, 'confirm-button')] | //*[contains(@class, 'js-confirm')]");
    private final By closedBoardMessage = By.xpath("//*[contains(text(), 'This board is closed') or contains(text(), 'board is closed') or @data-testid='close-board-big-message' or @data-testid='close-board-message']");
    private final By reopenBoardBtn = By.xpath("//button[@data-testid='workspace-chooser-trigger-button' and contains(., 'Reopen')] | //button[@data-testid='workspace-chooser-reopen-button'] | //button[normalize-space(.)='Reopen board']");
    private final By permanentDeleteLink = By.xpath("//*[contains(@data-testid, 'delete-board') or contains(text(), 'Permanently delete board') or contains(@class, 'js-delete-board')]");
    private final By deleteConfirmBtn = By.xpath("//button[normalize-space(.)='Delete'] | //input[@value='Delete'] | //*[contains(@data-testid, 'confirm-button')]");

    // Visibility
    private final By boardVisibilityBtn = By.cssSelector("button[data-testid*='visibility'], button[aria-label*='Visibility'], button[id*='permission'], button[class*='vis']");
    private final By privateVisibilityOption = By.xpath("//*[contains(@data-testid, 'private') or contains(text(), 'Private') or contains(@class, 'private')]");

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
    /**
            * Resilient click helper that falls back to Actions and JavascriptExecutor if standard click is intercepted.
            */
    private void safeClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        try {
            element.click();
        } catch (Exception e) {
            try {
                org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
                actions.moveToElement(element).click().perform();
            } catch (Exception ex) {
                org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", element);
            }
        }
    }

    /**
     * Resilient sendKeys helper that waits for element visibility and focus.
     */
    private void safeType(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        try {
            element.clear();
        } catch (Exception e) {
            // Some input elements do not support clear()
        }
        element.sendKeys(text);
    }

    /**
     * Creates a new Trello board with the specified name.
     */
    public void createNewBoard(String name) {
        // Sleep briefly to let SPAs fully initialize event handlers on EAGER load
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        // Click create button, and retry if the menu doesn't appear
        int retries = 3;
        boolean menuOpened = false;
        while (retries > 0 && !menuOpened) {
            safeClick(headerCreateMenuBtn);
            try {
                // Wait briefly for the dropdown option to be visible
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(headerCreateBoardBtn));
                menuOpened = true;
            } catch (Exception e) {
                System.out.println("Dropdown menu didn't open. Retrying click... (Retries left: " + (retries - 1) + ")");
                retries--;
            }
        }

        safeClick(headerCreateBoardBtn);

        try {
            safeType(boardTitleInput, name);
        } catch (Exception e) {
            System.out.println("DEBUG: Diagnostics dump for all matching input/textarea/testid/button elements currently in the DOM:");
            try {
                for (WebElement el : driver.findElements(By.cssSelector("input, textarea, button, [data-testid]"))) {
                    String tag = el.getTagName();
                    String id = el.getAttribute("id");
                    String placeholder = el.getAttribute("placeholder");
                    String testid = el.getAttribute("data-testid");
                    String nameAttr = el.getAttribute("name");
                    if ((testid != null && !testid.isEmpty()) || (placeholder != null && !placeholder.isEmpty()) || "input".equals(tag) || "textarea".equals(tag)) {
                        System.out.println(" -> TAG: " + tag + " | ID: " + id + " | TESTID: " + testid + " | NAME: " + nameAttr + " | PLACEHOLDER: " + placeholder);
                    }
                }
            } catch (Exception ex) {
                System.out.println("Diagnostics dump failed: " + ex.getMessage());
            }
            throw e;
        }

        safeClick(finalCreateBtn);

        // Wait until redirected to the board URL
        wait.until(ExpectedConditions.urlContains("/b/"));
    }

    /**
     * Gets the displayed board title.
     */
    public String getBoardTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(boardTitleDisplay)).getText().trim();
    }

    /**
     * Updates/Renames the board title.
     */
    public void updateBoardTitle(String newName) {
        safeClick(boardTitleDisplay);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        WebElement titleInput = wait.until(ExpectedConditions.elementToBeClickable(boardTitleInputField));
        titleInput.click(); // Focus explicitly
        titleInput.sendKeys(Keys.CONTROL + "a");
        titleInput.sendKeys(Keys.BACK_SPACE);
        titleInput.sendKeys(newName);
        titleInput.sendKeys(Keys.ENTER);

        // Wait for the display title to update
        wait.until(ExpectedConditions.textToBePresentInElementLocated(boardTitleDisplay, newName));

        // Click the board canvas/background to release focus from the edit field
        try {
            driver.findElement(By.cssSelector("div.board-canvas, #board, .board-main-content")).click();
        } catch (Exception ignored) {}

        // Let the React UI fully settle after closing the input focus
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }

    /**
     * Stars or Unstars the board.
     */
    public void toggleStarBoard() {
        safeClick(boardStarBtn);
    }

    /**
     * Checks if the board is starred by examining the button's aria-label or classes.
     */
    public boolean isBoardStarred() {
        WebElement starButton = wait.until(ExpectedConditions.visibilityOfElementLocated(boardStarBtn));
        String label = starButton.getAttribute("aria-label");
        // "Unstar board" means it's currently starred, "Star board" means it is not starred.
        return label != null && label.contains("Unstar");
    }

    /**
     * Changes the background of the board to a solid color.
     */
    public void changeBackgroundToColor() {
        try {
            safeClick(showMenuBtn);
        } catch (Exception e) {
            // Menu might already be open or hidden
        }

        safeClick(changeBackgroundBtn);
        safeClick(backgroundColorsOption);
        safeClick(colorTile);
    }

    /**
     * Toggles board visibility to Private.
     */
    public void changeVisibilityToPrivate() {
        safeClick(boardVisibilityBtn);
        safeClick(privateVisibilityOption);
    }

    /**
     * Gets the text of the visibility button to verify state.
     */
    public String getVisibilityText() {
        WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(boardVisibilityBtn));
        String ariaLabel = btn.getAttribute("aria-label");
        if (ariaLabel != null && !ariaLabel.isEmpty()) {
            return ariaLabel;
        }
        return btn.getText().trim();
    }

    /**
     * Closes (archives) the board.
     */
    public void closeBoard() {
        System.out.println("[DEBUG closeBoard] Starting closeBoard procedure...");
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));

        // 1. Try to see if the board is already closed
        try {
            if (driver.findElement(closedBoardMessage).isDisplayed()) {
                System.out.println("[DEBUG closeBoard] Board is already closed!");
                return;
            }
        } catch (Exception ignored) {}

        // 2. Open Menu if not already open
        try {
            System.out.println("[DEBUG closeBoard] Checking if menu is already open...");
            // If closeBoardMenuLink is already present/visible, menu is open
            driver.findElement(closeBoardMenuLink);
            System.out.println("[DEBUG closeBoard] Menu is already open.");
        } catch (Exception e) {
            System.out.println("[DEBUG closeBoard] Menu not open. Attempting to click showMenuBtn...");
            try {
                WebElement menuBtn = wait.until(ExpectedConditions.elementToBeClickable(showMenuBtn));
                System.out.println("[DEBUG closeBoard] showMenuBtn found! text: '" + menuBtn.getText() + "', aria-label: '" + menuBtn.getAttribute("aria-label") + "'");
                safeClick(showMenuBtn);
                System.out.println("[DEBUG closeBoard] clicked showMenuBtn.");
            } catch (Exception ex) {
                System.out.println("[DEBUG closeBoard] showMenuBtn click failed: " + ex.getMessage());
            }
        }

        // Wait briefly for menu content to load
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // 3. Try to click 'More' sub-menu (optional, only if Close Board is not directly visible)
        boolean closeLinkVisible = false;
        try {
            closeLinkVisible = driver.findElement(closeBoardMenuLink).isDisplayed();
        } catch (Exception ignored) {}

        if (!closeLinkVisible) {
            System.out.println("[DEBUG closeBoard] Close Board link not visible. Attempting to click moreBtn...");
            try {
                By moreBtn = By.cssSelector("a.js-open-more, button[class*='open-more'], [data-testid='more-menu-button'], li.js-open-more button");
                WebElement mb = shortWait.until(ExpectedConditions.elementToBeClickable(moreBtn));
                System.out.println("[DEBUG closeBoard] moreBtn found! Clicking it.");
                safeClick(moreBtn);
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            } catch (Exception e) {
                System.out.println("[DEBUG closeBoard] moreBtn not clickable/found: " + e.getMessage());
            }
        }

        // 4. Click Close Board Link
        try {
            System.out.println("[DEBUG closeBoard] Attempting to click closeBoardMenuLink...");
            WebElement closeLink = wait.until(ExpectedConditions.elementToBeClickable(closeBoardMenuLink));
            System.out.println("[DEBUG closeBoard] closeBoardMenuLink found! text: '" + closeLink.getText() + "'");
            safeClick(closeBoardMenuLink);
            System.out.println("[DEBUG closeBoard] clicked closeBoardMenuLink.");
        } catch (Exception e) {
            System.out.println("[DEBUG closeBoard] closeBoardMenuLink click failed: " + e.getMessage());
            // Log DOM links for diagnostic
            System.out.println("[DEBUG closeBoard] Printing visible menu buttons/links:");
            try {
                for (WebElement el : driver.findElements(By.cssSelector("a, button, li"))) {
                    String txt = el.getText().trim();
                    if (!txt.isEmpty() && (txt.contains("Close") || txt.contains("Delete") || txt.contains("More"))) {
                        System.out.println("  -> tag: " + el.getTagName() + " | text: " + txt);
                    }
                }
            } catch (Exception ignored) {}
            throw e;
        }

        // Wait briefly for confirmation dialog/popover
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // 5. Click Close Confirm
        try {
            System.out.println("[DEBUG closeBoard] Attempting to click closeConfirmBtn...");
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(closeConfirmBtn));
            System.out.println("[DEBUG closeBoard] closeConfirmBtn found! text: '" + confirmBtn.getText() + "'");
            safeClick(closeConfirmBtn);
            System.out.println("[DEBUG closeBoard] clicked closeConfirmBtn.");
        } catch (Exception e) {
            System.out.println("[DEBUG closeBoard] closeConfirmBtn click failed: " + e.getMessage());
            throw e;
        }

        // 6. Wait for closed message
        System.out.println("[DEBUG closeBoard] Waiting for closedBoardMessage...");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(closedBoardMessage));
            System.out.println("[DEBUG closeBoard] closedBoardMessage is now visible! Board closed successfully.");
        } catch (Exception e) {
            System.out.println("[DEBUG closeBoard] Timeout waiting for closedBoardMessage: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Checks if the closed board screen is displayed.
     */
    public boolean isClosedScreenDisplayed() {
        System.out.println("[DEBUG isClosedScreenDisplayed] Checking if closed screen is displayed...");
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement closedMsg = shortWait.until(ExpectedConditions.visibilityOfElementLocated(closedBoardMessage));
            boolean displayed = closedMsg.isDisplayed();
            System.out.println("[DEBUG isClosedScreenDisplayed] Closed screen message found! text: '" + (closedMsg.getText().length() > 30 ? closedMsg.getText().substring(0, 30) : closedMsg.getText()) + "...' | displayed: " + displayed);
            return displayed;
        } catch (Exception e) {
            System.out.println("[DEBUG isClosedScreenDisplayed] Closed screen message not visible/found (board is likely active).");
            return false;
        }
    }

    /**
     * Reopens the currently closed board.
     */
    public void reopenBoard() {
        System.out.println("[DEBUG reopenBoard] Starting reopen board procedure...");

        System.out.println("[DEBUG reopenBoard] Printing all buttons or testid elements in DOM before searching:");
        try {
            for (WebElement el : driver.findElements(By.cssSelector("button, [data-testid]"))) {
                String txt = el.getText().trim();
                String testid = el.getAttribute("data-testid");
                if (testid != null || txt.contains("Reopen") || txt.contains("reopen")) {
                    System.out.println("  -> tag: " + el.getTagName() + " | text: '" + txt + "' | testid: " + testid + " | displayed: " + el.isDisplayed());
                }
            }
        } catch (Exception ignored) {}

        try {
            WebElement reopenBtn = wait.until(ExpectedConditions.presenceOfElementLocated(reopenBoardBtn));
            System.out.println("[DEBUG reopenBoard] Found reopenBoardBtn! text: '" + reopenBtn.getText() + "'");
            safeClick(reopenBoardBtn);
            System.out.println("[DEBUG reopenBoard] Clicked reopenBoardBtn.");
        } catch (Exception e) {
            System.out.println("[DEBUG reopenBoard] Failed to click reopenBoardBtn: " + e.getMessage());
            throw e;
        }

        // Wait a brief moment to see if a confirmation popover/button appears
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // Diagnostics: Print all visible buttons to find the confirmation button!
        System.out.println("[DEBUG reopenBoard] Printing all visible buttons currently on screen:");
        try {
            for (WebElement el : driver.findElements(By.cssSelector("button, input[type='button'], input[type='submit']"))) {
                if (el.isDisplayed()) {
                    System.out.println("  -> tag: " + el.getTagName() + " | text: '" + el.getText() + "' | data-testid: " + el.getAttribute("data-testid") + " | class: " + el.getAttribute("class"));
                }
            }
        } catch (Exception ignored) {}

        // Check if there is a confirmation button in a popover (like button with text "Reopen" or "Reopen board")
        try {
            By reopenConfirmBtn = By.xpath("//button[@data-testid='workspace-chooser-reopen-button'] | //div[contains(@class, 'popover')]//button[normalize-space(.)='Reopen'] | //input[@value='Reopen'] | //button[@data-testid='close-board-reopen-button-confirm'] | //button[normalize-space(.)='Reopen']");
            WebElement confirm = driver.findElement(reopenConfirmBtn);
            if (confirm.isDisplayed()) {
                System.out.println("[DEBUG reopenBoard] Found confirmation button! text: '" + confirm.getText() + "'. Clicking it...");
                confirm.click();
                System.out.println("[DEBUG reopenBoard] Clicked reopen confirmation.");
            }
        } catch (Exception e) {
            System.out.println("[DEBUG reopenBoard] No confirmation button detected (or already reopened directly).");
        }

        // Wait for the Reopen button itself to disappear (become invisible), ensuring the closed overlay is gone
        System.out.println("[DEBUG reopenBoard] Waiting for reopenBoardBtn to disappear...");
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            longWait.until(ExpectedConditions.invisibilityOfElementLocated(reopenBoardBtn));
            System.out.println("[DEBUG reopenBoard] reopenBoardBtn is now invisible!");
        } catch (Exception e) {
            System.out.println("[DEBUG reopenBoard] Warning: reopenBoardBtn did not become invisible: " + e.getMessage());
        }

        // Wait for the board title to show up again, indicating active board
        wait.until(ExpectedConditions.visibilityOfElementLocated(boardTitleDisplay));

        // Let the React SPA transition settle completely
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        System.out.println("[DEBUG reopenBoard] Board reopened successfully and settled.");
    }

    /**
     * Permanently deletes a closed board.
     */
    public void deleteBoardPermanently() {
        System.out.println("[DEBUG deleteBoardPermanently] Starting permanent delete procedure...");

        System.out.println("[DEBUG deleteBoardPermanently] Printing all elements in DOM containing delete/permanently:");
        try {
            for (WebElement el : driver.findElements(By.cssSelector("a, button, p, span, div"))) {
                String txt = el.getText().trim();
                if (txt.toLowerCase().contains("delete") || txt.toLowerCase().contains("permanently")) {
                    System.out.println("  -> tag: " + el.getTagName() + " | text: '" + txt + "' | testid: " + el.getAttribute("data-testid") + " | class: " + el.getAttribute("class"));
                }
            }
        } catch (Exception ignored) {}

        try {
            WebElement deleteLink = wait.until(ExpectedConditions.presenceOfElementLocated(permanentDeleteLink));
            System.out.println("[DEBUG deleteBoardPermanently] Found permanentDeleteLink! text: '" + deleteLink.getText() + "'");
            safeClick(permanentDeleteLink);
            System.out.println("[DEBUG deleteBoardPermanently] Clicked permanentDeleteLink.");
        } catch (Exception e) {
            System.out.println("[DEBUG deleteBoardPermanently] Failed to click permanentDeleteLink: " + e.getMessage());
            throw e;
        }

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        try {
            WebElement confirmBtn = wait.until(ExpectedConditions.presenceOfElementLocated(deleteConfirmBtn));
            System.out.println("[DEBUG deleteBoardPermanently] Found deleteConfirmBtn! text: '" + confirmBtn.getText() + "'");
            safeClick(deleteConfirmBtn);
            System.out.println("[DEBUG deleteBoardPermanently] Clicked deleteConfirmBtn.");
        } catch (Exception e) {
            System.out.println("[DEBUG deleteBoardPermanently] Failed to click deleteConfirmBtn: " + e.getMessage());
            throw e;
        }
    }
}