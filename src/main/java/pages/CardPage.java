package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
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

        WebElement cardElement = wait.until(
                ExpectedConditions.elementToBeClickable(card)
        );

        cardElement.click();

        // Wait until the card modal is actually open
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("button[aria-label='Close dialog']")
                )
        );

        System.out.println("STEP: Card opened successfully: " + cardTitle);
    }
    public void clickDescription() {

        System.out.println("STEP: Clicking Description...");

        WebElement description =
                wait.until(
                        ExpectedConditions.elementToBeClickable(descriptionButton)
                );

        description.click();

        System.out.println("STEP: Description opened.");
    }
    public void addDescription(String description) {

        WebElement field = wait.until(
                ExpectedConditions.elementToBeClickable(descriptionField)
        );

        field.click();
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
                "div[aria-label='Edit description']"
        );

        WebElement description = wait.until(
                ExpectedConditions.visibilityOfElementLocated(savedDescription)
        );

        String actualDescription = description.getText();

        System.out.println("Expected saved description: " + expectedDescription);
        System.out.println("Actual saved description: " + actualDescription);

        return actualDescription.trim().equals(expectedDescription.trim());
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

        System.out.println("STEP: Card closed successfully.");
    }
    public boolean isLabelApplied(String color) {

        By appliedLabel = By.cssSelector(
                "button[data-testid='compact-card-label'][data-color='" + color + "']"
        );

        WebElement label = wait.until(
                ExpectedConditions.visibilityOfElementLocated(appliedLabel)
        );

        boolean displayed = label.isDisplayed();

        System.out.println(
                "STEP: Label '" + color + "' displayed on card: " + displayed
        );

        return displayed;
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

        By datesButton = By.xpath("//button[normalize-space()='Dates']");

        WebElement dates = wait.until(
                ExpectedConditions.presenceOfElementLocated(datesButton)
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

        WebElement dueDate = wait.until(
                ExpectedConditions.visibilityOfElementLocated(savedDueDate)
        );

        String actualDate = dueDate.getText();

        System.out.println("Expected Due Date: " + expectedDate);
        System.out.println("Actual Due Date: " + actualDate);

        return actualDate.contains(expectedDate);
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
    public boolean isChecklistItemDisplayed(String expectedItem) {

        By checklistItem = By.cssSelector(
                "[data-testid='check-item-name']"
        );

        WebElement item = wait.until(
                ExpectedConditions.visibilityOfElementLocated(checklistItem)
        );

        String actualItem = item.getText();

        System.out.println("Expected Checklist Item: " + expectedItem);
        System.out.println("Actual Checklist Item: " + actualItem);

        return actualItem.trim().equals(expectedItem.trim());
    }
    public void checkChecklistItem(String itemName) {

        System.out.println("STEP: Checking checklist item: " + itemName);

        By checkboxLabel = By.xpath(
                "//li[@data-testid='check-item-container']" +
                        "[.//*[@data-testid='check-item-name' and normalize-space()='" + itemName + "']]" +
                        "//label[@data-testid='clickable-checkbox']"
        );

        WebElement label = wait.until(
                ExpectedConditions.elementToBeClickable(checkboxLabel)
        );

        label.click();

        System.out.println(
                "STEP: Checklist item checked: " + itemName
        );
    }
    public boolean isChecklistItemChecked(String itemName) {
        By checkboxLabel = By.xpath(
                "//li[@data-testid='check-item-container']" +
                        "[.//*[@data-testid='check-item-name' and normalize-space()='" + itemName + "']]" +
                        "//label[@data-testid='clickable-checkbox']"
        );

        WebElement label = wait.until(
                ExpectedConditions.visibilityOfElementLocated(checkboxLabel)
        );

        String className = label.getAttribute("class");
        String innerHtml = label.getAttribute("innerHTML");

        boolean checked =
                (className != null && className.toLowerCase().contains("checked")) ||
                        (innerHtml != null && innerHtml.toLowerCase().contains("checked"));

        System.out.println("DEBUG Checkbox class: " + className);
        System.out.println("DEBUG Checkbox HTML: " + innerHtml);
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
}