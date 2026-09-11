package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import utils.TestData;

public class CardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ─────────────────────────────────────────────
    // LOCATORS
    // ─────────────────────────────────────────────

    // "My Trello Board" link
    private By myTrelloBoard =
            By.xpath("//a[@href='/b/d8xcq3jv/my-trello-board'" +
                    " and @title='My Trello Board']");

    // "Add a card" button inside "This Week" list
    private By addCardButton =
            By.xpath("//button[@data-testid='list-add-card-button'" +
                    " and @aria-label='Add a card in To Do']");

    // Card title textarea (composer)
    private By cardTitleTextarea =
            By.xpath("//div[@data-testid='list-card-composer-textarea']");

    // "Add card" submit button inside composer
    private By addCardSubmitButton =
            By.xpath("//button[@data-testid='list-card-composer-add-card-button']");


    //these are the declarations which are made by harshit
    private By descriptionButton = By.cssSelector("button[data-testid='description-button']");
    private By editDescriptionButton = By.cssSelector("button[aria-label='Edit description']");
    private By descriptionField = By.id("ak-editor-textarea");
    private By descriptionSaveButton = By.cssSelector("button[data-testid='description-save-button']");
    private By labelsButton = By.xpath("//button[normalize-space()='Labels']");
    private By closeCardButton = By.cssSelector("button[aria-label='Close dialog']");
    private By datesButton = By.cssSelector("button[data-testid='card-back-due-date-button']");
    private By saveDateButton = By.cssSelector("button[data-testid='save-date-button']");
    private By dueDateField = By.cssSelector("input[data-testid='due-date-field']");
    private By checklistButton = By.xpath("//button[normalize-space()='Checklist']");
    private By checklistTitleField = By.id("id-checklist");
    private By checklistAddButton = By.cssSelector("button[data-testid='checklist-add-button']");
    private By checklistItemInput = By.cssSelector("textarea[data-testid='check-item-name-input']");
    private By checklistItemAddButton = By.cssSelector("button[data-testid='check-item-add-button']");

    private By addToCardButton = By.xpath("//button[@aria-label='Add to card']");
    private By attachmentButton = By.cssSelector("[data-testid='card-back-attachment-button']");
    private By attachLinkInput = By.cssSelector("input[data-testid='link-url']");
    private By attachLinkSubmitButton = By.cssSelector("[data-testid='link-picker-insert-button']");
    private By attachmentsListItem = By.cssSelector("[data-testid='attachment-links-list'] li");

    private By coverButton = By.cssSelector("[data-testid='card-back-cover-button']");
    private By coverColorSwatch = By.cssSelector("[data-testid^='color-tile-']");
    private By coverAppliedIndicator = By.cssSelector("[data-testid='card-cover']");


    // ─────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────
    public CardPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ─────────────────────────────────────────────
    // METHODS
    // ─────────────────────────────────────────────

    /**
     * Clicks on "My Trello Board" from the dashboard
     */
    public void clickMyTrelloBoard() {
        System.out.println("STEP: Clicking on 'My Trello Board'...");

        WebElement board = wait.until(
                ExpectedConditions.elementToBeClickable(myTrelloBoard));

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", board);

        board.click();

        // Wait until the board URL is loaded
        wait.until(ExpectedConditions.urlContains("my-trello-board"));
        System.out.println("STEP: 'My Trello Board' opened successfully.");
    }

    /**
     * Clicks on "Add a card" button in "This Week" list
     */
    public void clickAddCardButton() {
        By addCardButton = By.xpath(
                "//button[@aria-label='Add a card in " + TestData.listName + "']"
        );

        WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(addCardButton)
        );

        button.click();
        System.out.println("Button Got Clicked");
    }

    /**
     * Enters the card title in the composer textarea
     *
     * @param cardTitle - Title of the card to be created
     */
    public void enterCardTitle(String cardTitle) {
        System.out.println("STEP: Entering card title: " + cardTitle);

        WebElement titleBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardTitleTextarea));

        titleBox.clear();
        titleBox.sendKeys(cardTitle);
        System.out.println("STEP: Card title entered: " + cardTitle);
    }

    /**
     * Clicks the "Add card" submit button to save the card
     */
    public void clickAddCardSubmit() {
        System.out.println("STEP: Clicking 'Add card' submit button...");

        wait.until(
                ExpectedConditions.elementToBeClickable(addCardSubmitButton)
        ).click();

        System.out.println("STEP: Card submitted successfully.");
    }

    /**
     * Verifies if the card is visible on the board
     *
     * @param cardTitle - Title of the card to verify
     * @return true if card is found, false otherwise
     */
    /**
     * Verifies if the card is visible on the board
     * ✅ CORRECT: Clean XPath string concatenation
     *
     * @param cardTitle - Title of the card to verify
     * @return true if card is found, false otherwise
     */
    public boolean isCardCreated(String cardTitle) {
        System.out.println("STEP: Verifying card '" + cardTitle + "' is created...");

        try {
            WebElement card = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//a[@data-testid='card-name'" +
                                    " and contains(text(),'" + cardTitle + "')]")));

            boolean isVisible = card.isDisplayed();
            System.out.println("STEP: Card visible on board: " + isVisible);
            return isVisible;

        } catch (Exception e) {
            System.out.println("❌ Card NOT found: " + e.getMessage());
            return false;
        }

    }







    //Methods are created by harshith for the card attributes and properites
    public void openCard(String cardTitle) {

        By card = By.xpath(
                "//*[@data-testid='card-name' and normalize-space()='" + cardTitle + "']"
        );
        By closeDialogButton = By.cssSelector("button[aria-label='Close dialog']");

        // The click occasionally doesn't open the modal (board re-render timing) -
        // retry with a JS-click fallback rather than failing outright, same pattern
        // used for DashboardPage.openBoard().
        for (int attempt = 1; attempt <= 2; attempt++) {
            WebElement cardElement = wait.until(ExpectedConditions.elementToBeClickable(card));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", cardElement);
            try {
                cardElement.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cardElement);
            }
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(closeDialogButton));
                System.out.println("STEP: Card opened successfully: " + cardTitle);
                return;
            } catch (TimeoutException e) {
                if (attempt == 2) {
                    throw e;
                }
                System.out.println("STEP: Card modal did not open on attempt " + attempt + ", retrying...");
            }
        }
    }
    public void clickDescription() {

        System.out.println("STEP: Clicking Description...");

        // Once a description is already saved, the "Add a more detailed description"
        // button is replaced by an "Edit description" button - pick whichever is present.
        List<WebElement> addButton = driver.findElements(descriptionButton);
        By target = (!addButton.isEmpty() && addButton.get(0).isDisplayed())
                ? descriptionButton
                : editDescriptionButton;

        WebElement description = wait.until(ExpectedConditions.elementToBeClickable(target));
        description.click();

        System.out.println("STEP: Description opened.");
    }
    public void addDescription(String description) {

        WebElement field = wait.until(
                ExpectedConditions.elementToBeClickable(descriptionField)
        );

        field.click();
        // The field may already contain previously-saved text (Edit flow on a reused
        // fixture card) - it's a rich-text editor, not a plain input, so .clear() is a
        // no-op; select-all + delete first or the new text gets interleaved with the old.
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        field.sendKeys(description);
    }
    public boolean isDescriptionEntered(String expectedDescription) {

        WebElement field = wait.until(
                ExpectedConditions.visibilityOfElementLocated(descriptionField)
        );

        String actualDescription = field.getText();

        System.out.println("Expected Description: " + expectedDescription);
        System.out.println("Actual Description: " + actualDescription);

        return actualDescription.trim().equals(expectedDescription.trim());
    }
    public void savedescription(){
        System.out.println("Button clicked and yet to save");
        WebElement saveButton=wait.until(ExpectedConditions.elementToBeClickable(descriptionSaveButton));
        saveButton.click();
        System.out.println("the button gotclicked and save");
    }
    public boolean isSavedDescriptionDisplayed(String expectedDescription) {

        By savedDescription = By.cssSelector(
                "[data-testid='description-content-area']"
        );

        try {
            WebElement description = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(savedDescription)
            );

            String actualDescription = description.getText();

            System.out.println("Expected saved description: " + expectedDescription);
            System.out.println("Actual saved description: " + actualDescription);

            return actualDescription.trim().equals(expectedDescription.trim());
        } catch (Exception e) {
            System.out.println("Saved description not found: " + e.getMessage());
            return false;
        }
    }

    public void clickLabels() {

        System.out.println("STEP: Clicking Labels...");

        WebElement labels = wait.until(
                ExpectedConditions.presenceOfElementLocated(labelsButton)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                labels
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                labels
        );

        System.out.println("STEP: Labels menu opened.");
    }
    public void selectLabel(String color) {

        System.out.println("STEP: Selecting label: " + color);

        By label = By.cssSelector(
                "span[data-testid='card-label'][data-color='" + color + "']"
        );

        WebElement labelElement = wait.until(
                ExpectedConditions.elementToBeClickable(label)
        );

        labelElement.click();

        System.out.println("STEP: Label selected: " + color);
    }
    public void closeCard() {

        System.out.println("STEP: Closing card...");

        WebElement closeButton = wait.until(
                ExpectedConditions.elementToBeClickable(closeCardButton)
        );

        closeButton.click();

        // Wait for the dialog to actually finish closing - otherwise the overlay can
        // still intercept the very next click (e.g. reopening the same card immediately).
        wait.until(ExpectedConditions.invisibilityOfElementLocated(closeCardButton));

        System.out.println("STEP: Card closed successfully.");
    }
    public boolean isLabelApplied(String color) {

        By appliedLabel = By.cssSelector(
                "button[data-testid='compact-card-label'][data-color='" + color + "']"
        );

        try {
            WebElement label = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(appliedLabel)
            );

            boolean displayed = label.isDisplayed();

            System.out.println(
                    "STEP: Label '" + color + "' displayed on card: " + displayed
            );

            return displayed;
        } catch (Exception e) {
            System.out.println("STEP: Label '" + color + "' not applied.");
            return false;
        }
    }

    /**
     * Applies the label only if it is not already applied. Selecting an already-applied
     * label toggles it OFF in Trello, so a plain selectLabel() is not safe to call twice
     * (e.g. on a reused fixture card across test reruns).
     */
    public void ensureLabelApplied(String color) {
        if (isLabelApplied(color)) {
            System.out.println("STEP: Label '" + color + "' already applied, skipping.");
            return;
        }
        clickLabels();
        selectLabel(color);
    }
    public void clickAppliedLabel(String color) {

        System.out.println("STEP: Clicking applied label: " + color);

        By appliedLabel = By.cssSelector(
                "button[data-testid='compact-card-label'][data-color='" + color + "']"
        );

        WebElement label = wait.until(
                ExpectedConditions.elementToBeClickable(appliedLabel)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                label
        );

        System.out.println("STEP: Applied label clicked: " + color);
    }
    public void clickDates() {

        System.out.println("STEP: Clicking Dates...");

        // Once a due date is already set, the "Dates" quick-action button is replaced by
        // a due-date badge button (e.g. "Sep 15, 8:21 PM") - clicking it reopens the same
        // date editor. Pick whichever is actually present.
        By datesQuickButton = By.xpath("//button[normalize-space()='Dates']");
        By dueDateBadge = By.cssSelector("button[data-testid='due-date-badge-with-date-range-picker']");

        List<WebElement> quick = driver.findElements(datesQuickButton);
        By target = (!quick.isEmpty() && quick.get(0).isDisplayed()) ? datesQuickButton : dueDateBadge;

        WebElement dates = wait.until(
                ExpectedConditions.presenceOfElementLocated(target)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                dates
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                dates
        );

        System.out.println("STEP: Dates menu opened.");
    }
    public void enterDueDate(String dueDate) {

        System.out.println("STEP: Entering due date: " + dueDate);

        WebElement dateField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(dueDateField)
        );

        dateField.click();
        dateField.clear();
        dateField.sendKeys(dueDate);

        System.out.println("STEP: Due date entered: " + dueDate);
    }
    public void saveDueDate() {

        System.out.println("STEP: Saving due date...");

        WebElement saveButton = wait.until(
                ExpectedConditions.elementToBeClickable(saveDateButton)
        );

        saveButton.click();

        System.out.println("STEP: Due date saved successfully.");
    }
    public boolean isDueDateDisplayed(String expectedDate) {

        By savedDueDate = By.cssSelector(
                "button[data-testid='due-date-badge-with-date-range-picker']"
        );

        try {
            WebElement dueDate = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(savedDueDate)
            );

            String actualDate = dueDate.getText();

            System.out.println("Expected Due Date: " + expectedDate);
            System.out.println("Actual Due Date: " + actualDate);

            return actualDate.contains(expectedDate);
        } catch (Exception e) {
            System.out.println("Due date badge not found: " + e.getMessage());
            return false;
        }
    }
    public void clickChecklist() {

        System.out.println("STEP: Clicking Checklist...");

        WebElement checklist = wait.until(
                ExpectedConditions.elementToBeClickable(checklistButton)
        );

        checklist.click();

        System.out.println("STEP: Checklist menu opened.");
    }
    public void enterChecklistName(String checklistName) {

        System.out.println("STEP: Entering checklist name: " + checklistName);

        WebElement field = wait.until(
                ExpectedConditions.visibilityOfElementLocated(checklistTitleField)
        );

        field.click();
        field.clear();
        field.sendKeys(checklistName);

        System.out.println("STEP: Checklist name entered: " + checklistName);
    }
    public void addChecklist() {

        System.out.println("STEP: Clicking Add checklist...");

        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(checklistAddButton)
        );

        addButton.click();

        System.out.println("STEP: Checklist added successfully.");
    }
    public void enterChecklistItem(String itemName) {

        System.out.println("STEP: Entering checklist item: " + itemName);

        WebElement itemField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        checklistItemInput
                )
        );

        itemField.click();
        itemField.clear();
        itemField.sendKeys(itemName);

        System.out.println(
                "STEP: Checklist item entered: " + itemName
        );
    }
    public void addChecklistItem() {

        System.out.println("STEP: Clicking Add checklist item...");

        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        checklistItemAddButton
                )
        );

        addButton.click();

        System.out.println("STEP: Checklist item added successfully.");
    }
    /**
     * The checklist item's name is only exposed via the aria-label of its checkbox input
     * in this Trello UI - there is no separate visible text element carrying the name, so
     * this is the one reliable locator for a specific item.
     */
    private By checklistItemCheckboxInput(String itemName) {
        return By.xpath("//input[@type='checkbox' and @aria-label='" + itemName + "']");
    }

    /**
     * The checkbox <input> is visually hidden behind its wrapping label - Chrome redirects
     * any click aimed at the input to this label ("element would receive the click"), and
     * clicking the input directly via JS does not trigger Trello's React toggle handler at
     * all. This label is the real, reliable click target.
     */
    private By checklistItemCheckboxLabel(String itemName) {
        return By.xpath("//label[@data-testid='clickable-checkbox'][.//input[@aria-label='" + itemName + "']]");
    }

    public boolean isChecklistItemDisplayed(String expectedItem) {

        By checkboxInput = checklistItemCheckboxInput(expectedItem);

        try {
            WebElement item = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(checkboxInput)
            );
            return item.isDisplayed();
        } catch (Exception e) {
            System.out.println("Checklist item '" + expectedItem + "' not found: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks whether a checklist with the given name already exists on the card.
     * Used to avoid creating duplicate checklists when the fixture card is reused across runs.
     * Uses contains() rather than an exact match - the title element's accessible text
     * includes a hidden "Checklist" prefix concatenated with the visible name.
     */
    public boolean isChecklistPresent(String checklistName) {
        By checklistHeading = By.xpath(
                "//h3[@data-testid='checklist-title'][contains(normalize-space(), '" + checklistName + "')]"
        );
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(checklistHeading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks the item only if not already checked - re-clicking an already-checked
     * checkbox toggles it back off, which is exactly what happened across reruns
     * of the fixture card before this guard existed.
     */
    public void checkChecklistItem(String itemName) {

        if (isChecklistItemChecked(itemName)) {
            System.out.println("STEP: Checklist item '" + itemName + "' already checked, skipping.");
            return;
        }

        System.out.println("STEP: Checking checklist item: " + itemName);

        WebElement label = wait.until(
                ExpectedConditions.elementToBeClickable(checklistItemCheckboxLabel(itemName))
        );

        label.click();

        // The click is optimistic-UI, not instant - wait for the checkbox to actually
        // flip rather than assuming the click landed by the time we return.
        wait.until(d -> d.findElement(checklistItemCheckboxInput(itemName)).isSelected());

        System.out.println(
                "STEP: Checklist item checked: " + itemName
        );
    }

    public boolean isChecklistItemChecked(String itemName) {
        WebElement checkbox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(checklistItemCheckboxInput(itemName))
        );

        // .isSelected() reflects the live checked property - unlike scraping class names
        // or innerHTML, which are unreliable (e.g. the literal attribute name
        // "aria-checked" itself contains the substring "checked" regardless of its value).
        boolean checked = checkbox.isSelected();

        System.out.println("STEP: Checklist item '" + itemName + "' checked: " + checked);

        return checked;
    }
    public void waitForLabelsButton() {
        By labelsButton = By.xpath("//button[normalize-space()='Labels']");

        wait.until(
                ExpectedConditions.elementToBeClickable(labelsButton)
        );

        System.out.println("STEP: Labels button is ready again.");
    }

    public void debugGreenLabel() {

        By greenLabel = By.cssSelector(
                "span[data-testid='card-label'][data-color='green']"
        );

        WebElement label = wait.until(
                ExpectedConditions.visibilityOfElementLocated(greenLabel)
        );

        System.out.println("===== GREEN LABEL DEBUG =====");
        System.out.println("Tag: " + label.getTagName());
        System.out.println("Text: " + label.getText());
        System.out.println("Class: " + label.getAttribute("class"));
        System.out.println("Aria-label: " + label.getAttribute("aria-label"));
        System.out.println("Aria-checked: " + label.getAttribute("aria-checked"));
        System.out.println("Data-color: " + label.getAttribute("data-color"));
        System.out.println("Data-testid: " + label.getAttribute("data-testid"));
        System.out.println("Role: " + label.getAttribute("role"));
        System.out.println("HTML: " + label.getAttribute("outerHTML"));
        System.out.println("============================");
    }

    // ─────────────────────────────────────────────
    // ATTACHMENTS (link attachment)
    // ─────────────────────────────────────────────

    public void clickAttachment() {
        System.out.println("STEP: Opening 'Add to card' menu...");
        clickWithRetry(addToCardButton);

        System.out.println("STEP: Clicking Attachment...");
        clickWithRetry(attachmentButton);
        System.out.println("STEP: Attachment menu opened.");
    }

    /**
     * Scrolls the element into view and clicks it, retrying once with a JS-click fallback
     * if the first attempt throws (e.g. a transient overlay/re-render intercepts the click) -
     * same defensive pattern used for openBoard()/openCard() to survive React re-render timing.
     */
    private void clickWithRetry(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    public void attachLink(String url) {
        System.out.println("STEP: Attaching link: " + url);
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(attachLinkInput));
        field.click();
        field.clear();
        field.sendKeys(url);
    }

    public void confirmAttachLink() {
        System.out.println("STEP: Confirming link attachment...");
        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(attachLinkSubmitButton));
        submit.click();
        System.out.println("STEP: Link attachment submitted.");
    }

    /**
     * Checks whether an attachment matching the given URL/name fragment is already present.
     * Used to avoid re-attaching the same link when the fixture card is reused across runs.
     */
    public boolean isAttachmentPresent(String urlOrNameFragment) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            return shortWait.until(d -> {
                for (WebElement el : d.findElements(attachmentsListItem)) {
                    String text = el.getText();
                    String href = el.getAttribute("href");
                    if ((text != null && text.contains(urlOrNameFragment))
                            || (href != null && href.contains(urlOrNameFragment))) {
                        return true;
                    }
                }
                return false;
            });
        } catch (Exception e) {
            return false;
        }
    }

    public void ensureLinkAttached(String url) {
        if (isAttachmentPresent(url)) {
            System.out.println("STEP: Attachment '" + url + "' already present, skipping.");
            return;
        }
        clickAttachment();
        attachLink(url);
        confirmAttachLink();
    }

    // ─────────────────────────────────────────────
    // COVER (solid color)
    // ─────────────────────────────────────────────

    public void clickCover() {
        System.out.println("STEP: Clicking Cover...");
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(coverButton));
        button.click();
        System.out.println("STEP: Cover menu opened.");
    }

    public void selectCoverColor() {
        System.out.println("STEP: Selecting a cover color...");
        WebElement swatch = wait.until(ExpectedConditions.elementToBeClickable(coverColorSwatch));
        swatch.click();
        System.out.println("STEP: Cover color applied.");

        // The cover popover stays open after picking a color and overlaps the
        // card dialog's own controls (e.g. Close dialog) - dismiss it with Escape.
        new org.openqa.selenium.interactions.Actions(driver).sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
    }

    public boolean isCoverApplied() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(coverAppliedIndicator)).isDisplayed();
        } catch (Exception e) {
            System.out.println("Cover indicator not found: " + e.getMessage());
            return false;
        }
    }

    public void ensureCoverApplied() {
        if (isCoverApplied()) {
            System.out.println("STEP: Cover already applied, skipping.");
            return;
        }
        clickCover();
        selectCoverColor();
    }
}