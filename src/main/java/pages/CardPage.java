package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.TestData;

import java.time.Duration;
import java.util.List;
import utils.TestData;

public class CardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ─────────────────────────────────────────────────────────────────────────
    // LOCATORS — Anirudh
    // ─────────────────────────────────────────────────────────────────────────

    private By myTrelloBoard = By.xpath(
            "//a[@href='/b/d8xcq3jv/my-trello-board'" +
                    " and @title='My Trello Board']"
    );

    private By addCardButton = By.xpath(
            "//button[@data-testid='list-add-card-button'" +
                    " and @aria-label='Add a card in To Do']"
    );

    private By cardTitleTextarea = By.xpath(
            "//div[@data-testid='list-card-composer-textarea']"
    );

    private By addCardSubmitButton = By.xpath(
            "//button[@data-testid='list-card-composer-add-card-button']"
    );

    // ─────────────────────────────────────────────────────────────────────────
    // LOCATORS — Card Back Modal
    // ─────────────────────────────────────────────────────────────────────────
    private final By cardBackModal =
            By.cssSelector("[data-testid='card-back-panel']");
    private final By cardBackHeader =
            By.cssSelector("[data-testid='card-back-header']");
    private final By cardBackTitleInput =
            By.cssSelector("[data-testid='card-back-title-input']");
    private final By actionsButton =
            By.cssSelector("[data-testid='card-back-actions-button']");
    private final By cardDoneStateButton =
            By.cssSelector("[data-testid='card-done-state-completion-button']");
    private final By archiveCardOption = By.xpath(
            "//button[contains(.,'Archive')" +
                    " or .//span[normalize-space()='Archive']]"
    );

    // Dynamic locators — Anirudh
    private By cardNameLocator(String cardTitle) {
        return By.xpath(
                "//a[@data-testid='card-name' and text()='" + cardTitle + "']"
        );
    }

    private By archivedCardLocator(String cardTitle) {
        return By.xpath(
                "//div[@data-testid='archived-card']" +
                        "//a[@data-testid='card-name' and normalize-space()='" + cardTitle + "']"
        );
    }

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

    // Collaboration locators (for comments, members, watching)
    private By cardBackPanelLocator = By.cssSelector("[data-testid='card-back-panel']");
    private By addToCardButtonLocator = By.cssSelector("button[aria-label='Add to card']");
    private By addMembersMenuItemLocator = By.cssSelector("button[data-testid='card-back-members-button']");
    private By memberSearchInputLocator = By.cssSelector("input[aria-label='Search members']");
    private By memberSearchResultLocator = By.cssSelector("button[data-testid='choose-member-item-add-member-button']");
    private By assignedMemberAvatarLocator = By.cssSelector("button[data-testid='card-back-member-avatar']");
    private By newCommentSkeletonButtonLocator = By.cssSelector("button[data-testid='card-back-new-comment-input-skeleton']");
    private By commentEditorLocator = By.cssSelector("[data-testid='editor-content-container'] .ProseMirror");
    private By commentSaveButtonLocator = By.cssSelector("button[data-testid='card-back-comment-save-button']");
    private By actionsButtonLocator = By.cssSelector("button[data-testid='card-back-actions-button']");
    private By subscribedButtonLocator = By.cssSelector("button[data-testid='card-back-subscribed-button']");
    private By activityFeedItemLocator = By.cssSelector("[data-testid='card-back-action']");

    // Dynamic locators — Harshit
    private By checklistItemCheckboxInput(String itemName) {
        return By.xpath(
                "//input[@type='checkbox' and @aria-label='" + itemName + "']"
        );
    }

    /**
     * The checkbox <input> is visually hidden behind its wrapping label - Chrome redirects
     * any click aimed at the input to this label ("element would receive the click"), and
     * clicking the input directly via JS does not trigger Trello's React toggle handler at
     * all. This label is the real, reliable click target.
     */
    private By checklistItemCheckboxLabel(String itemName) {
        return By.xpath(
                "//label[@data-testid='clickable-checkbox']" +
                        "[.//input[@aria-label='" + itemName + "']]"
        );
    }


    // ─────────────────────────────────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────────────────

    public CardPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ─────────────────────────────────────────────
    // COLLABORATION METHODS (Members, Comments, Watching)
    // ─────────────────────────────────────────────

    /**
     * Check whether the card detail panel is currently open.
     */
    public boolean isCardOpen() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(cardBackPanelLocator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Assign a member to the currently open card via the Add to card > Members popover.
     */
    public void addMember(String emailOrUsernameFragment) {
        WebElement addToCardBtn = wait.until(ExpectedConditions.elementToBeClickable(addToCardButtonLocator));
        addToCardBtn.click();

        WebElement membersItem = wait.until(ExpectedConditions.elementToBeClickable(addMembersMenuItemLocator));
        membersItem.click();

        if (isMemberAssigned(emailOrUsernameFragment)) {
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            return;
        }

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(memberSearchInputLocator));
        searchInput.sendKeys(emailOrUsernameFragment);
        
        // Wait for search results
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        WebElement memberButton = wait.until(ExpectedConditions.elementToBeClickable(memberSearchResultLocator));
        memberButton.click();

        new Actions(driver).sendKeys(Keys.ESCAPE).perform();
        
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> isMemberAssigned(emailOrUsernameFragment));
    }

    /**
     * Check whether a member is assigned to the currently open card.
     */
    public boolean isMemberAssigned(String nameOrUsernameFragment) {
        try {
            List<WebElement> avatars = driver.findElements(assignedMemberAvatarLocator);
            for (WebElement avatar : avatars) {
                String title = avatar.getAttribute("title");
                if (title != null && title.contains(nameOrUsernameFragment)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    // METHODS

    /**
     * Clicks on "My Trello Board" from the dashboard
     */
    public void clickMyTrelloBoard() {
        System.out.println("STEP: Clicking on 'My Trello Board'...");

        WebElement board = wait.until(
                ExpectedConditions.elementToBeClickable(myTrelloBoard)
        );

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", board);

        board.click();

        // Wait until the board URL is loaded
        wait.until(ExpectedConditions.urlContains("my-trello-board"));
        System.out.println("STEP: 'My Trello Board' opened successfully.");
    }

    /**
     * Clicks on "Add a card" button in the list defined by TestData.listName.
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
     * Enters the card title in the composer textarea.
     *
     * @param cardTitle Title of the card to be created.
     */
    public void enterCardTitle(String cardTitle) {
        System.out.println("STEP: Entering card title: " + cardTitle);

        WebElement titleBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardTitleTextarea)
        );

        titleBox.clear();
        titleBox.sendKeys(cardTitle);
        System.out.println("STEP: Card title entered: " + cardTitle);
    }

    /**
     * Clicks the "Add card" submit button to save the card.
     */
    public void clickAddCardSubmit() {
        System.out.println("STEP: Clicking 'Add card' submit button...");

        wait.until(
                ExpectedConditions.elementToBeClickable(addCardSubmitButton)
        ).click();

        System.out.println("STEP: Card submitted successfully.");
    }

    /**
     * Verifies if the card is visible on the board.
     *
     * @param cardTitle Title of the card to verify.
     * @return true if card is found and visible, false otherwise.
     */
    public boolean isCardCreated(String cardTitle) {
        System.out.println("STEP: Verifying card '" + cardTitle + "' is created...");

        try {
            WebElement card = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath(
                                    "//a[@data-testid='card-name'" +
                                            " and contains(text(),'" + cardTitle + "')]"
                            )
                    )
            );

            boolean isVisible = card.isDisplayed();
            System.out.println("STEP: Card visible on board: " + isVisible);
            return isVisible;

        } catch (Exception e) {
            System.out.println("❌ Card NOT found: " + e.getMessage());
            return false;
        }
    }

    /**
     * Waits until the card detail modal is fully open.
     */
    public void waitForCardModalToOpen() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardBackModal)
        );
    }

    /**
     * Waits until the card detail modal has fully closed.
     */
    public void waitForCardModalToClose() {
        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(cardBackModal)
        );
    }

    /**
     * Clicks the Actions button inside the open card detail modal.
     */
    public void clickActionsButton() {
        WebElement actionsBtn = wait.until(
                ExpectedConditions.elementToBeClickable(actionsButton)
        );
        actionsBtn.click();
    }

    /**
     * Clicks the Archive option from inside the Actions menu.
     */
    public void clickArchiveFromActions() {
        WebElement archiveOption = wait.until(
                ExpectedConditions.elementToBeClickable(archiveCardOption)
        );
        archiveOption.click();
    }

    /**
     * Returns true if the card is currently visible on the board.
     *
     * @param cardTitle Title of the card to check.
     * @return true if visible, false otherwise.
     */
    public boolean isCardVisibleOnBoard(String cardTitle) {
        List<WebElement> cards = driver.findElements(cardNameLocator(cardTitle));
        return !cards.isEmpty() && cards.get(0).isDisplayed();
    }

    /**
     * Returns true if the archived card appears in the Archived Items panel.
     *
     * @param cardTitle Title of the archived card to look for.
     * @return true if listed, false otherwise.
     */
    public boolean isArchivedCardListed(String cardTitle) {

        By anyArchivedCard = By.xpath("//div[@data-testid='archived-card']");

        try {
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(anyArchivedCard)
            );
        } catch (TimeoutException e) {
            System.out.println(
                    "❌ No archived cards loaded in panel within timeout."
            );
            return false;
        }

        List<WebElement> archivedCards =
                driver.findElements(archivedCardLocator(cardTitle));

        for (WebElement card : archivedCards) {
            if (card.isDisplayed()) {
                return true;
            }
        }
        return false;
    }



    // ─────────────────────────────────────────────────────────────────────────
    // METHODS — Harshit
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Opens a card by clicking its title on the board.
     * The click occasionally doesn't open the modal (board re-render timing) -
     * retry with a JS-click fallback rather than failing outright, same pattern
     * used for DashboardPage.openBoard().
     *
     * @param cardTitle Exact title of the card to open.
     */
    public void openCard(String cardTitle) {

        By card = By.xpath(
                "//*[@data-testid='card-name'" +
                        " and normalize-space()='" + cardTitle + "']"
        );
        By closeDialogButton = By.cssSelector("button[aria-label='Close dialog']");

        // The click occasionally doesn't open the modal (board re-render timing) -
        // retry with a JS-click fallback rather than failing outright, same pattern
        // used for DashboardPage.openBoard().
        for (int attempt = 1; attempt <= 2; attempt++) {
            WebElement cardElement = wait.until(
                    ExpectedConditions.elementToBeClickable(card)
            );
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});",
                    cardElement
            );
            try {
                cardElement.click();
            } catch (StaleElementReferenceException e) {
                System.out.println(
                        "STEP: Element went stale before click, re-fetching..."
                );
                cardElement = wait.until(               // ← re-fetch fresh reference
                        ExpectedConditions.elementToBeClickable(card)
                );
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].click();",
                        cardElement                     // ← uses live element
                );
            }catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].click();",
                        cardElement
                );
            }
            try {
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(closeDialogButton)
                );
                System.out.println("STEP: Card opened successfully: " + cardTitle);
                return;
            } catch (TimeoutException e) {
                if (attempt == 2) {
                    throw e;
                }
                System.out.println(
                        "STEP: Card modal did not open on attempt " + attempt + ", retrying..."
                );
            }
        }
    }

    /**
     * Clicks the Description button inside the open card modal.
     * Once a description is already saved, the "Add a more detailed description"
     * button is replaced by an "Edit description" button — picks whichever is present.
     */
    public void clickDescription() {

        System.out.println("STEP: Clicking Description...");

        // Once a description is already saved, the "Add a more detailed description"
        // button is replaced by an "Edit description" button - pick whichever is present.
        List<WebElement> addButton = driver.findElements(descriptionButton);
        By target = (!addButton.isEmpty() && addButton.get(0).isDisplayed())
                ? descriptionButton
                : editDescriptionButton;

        WebElement description = wait.until(
                ExpectedConditions.elementToBeClickable(target)
        );
        description.click();

        System.out.println("STEP: Description opened.");
    }

    /**
     * Types the given description text into the description editor field.
     * The field may already contain previously-saved text (Edit flow on a reused
     * fixture card) — select-all + delete first so new text does not interleave.
     *
     * @param description The description text to enter.
     */
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

    /**
     * Returns true if the description editor field contains the expected text.
     *
     * @param expectedDescription The description text to verify.
     * @return true if text matches, false otherwise.
     */
    public boolean isDescriptionEntered(String expectedDescription) {

        WebElement field = wait.until(
                ExpectedConditions.visibilityOfElementLocated(descriptionField)
        );

        String actualDescription = field.getText();

        System.out.println("Expected Description: " + expectedDescription);
        System.out.println("Actual Description:   " + actualDescription);

        return actualDescription.trim().equals(expectedDescription.trim());
    }

    /**
     * Clicks the Save button to persist the description.
     */
    public void savedescription() {
        System.out.println("Button clicked and yet to save");
        WebElement saveButton = wait.until(
                ExpectedConditions.elementToBeClickable(descriptionSaveButton)
        );
        saveButton.click();
        System.out.println("the button gotclicked and save");
    }

    /**
     * Returns true if the saved description content area displays the expected text.
     *
     * @param expectedDescription The description text to verify after saving.
     * @return true if text matches, false otherwise.
     */
    public boolean isSavedDescriptionDisplayed(String expectedDescription) {

        By savedDescription = By.cssSelector("[data-testid='description-content-area']");

        try {
            WebElement description = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(savedDescription)
            );

            String actualDescription = description.getText();

            System.out.println("Expected saved description: " + expectedDescription);
            System.out.println("Actual saved description:   " + actualDescription);

            return actualDescription.trim().equals(expectedDescription.trim());

        } catch (Exception e) {
            System.out.println("Saved description not found: " + e.getMessage());
            return false;
        }
    }

    /**
     * Post a plain comment on the currently open card.
     *
     * @param text Comment text
     */
    public void postComment(String text) {
        WebElement editor = openCommentEditor();
        editor.sendKeys(text);
        saveComment();
    }

    /**
     * Post a comment that @mentions another member, then appends the rest of the comment.
     *
     * @param mentionFragment Fragment of the mentioned member's name or username (without "@")
     * @param commentText     Remaining comment text to append after the mention
     */
    public void postCommentMentioning(String mentionFragment, String commentText) {
        WebElement editor = openCommentEditor();
        editor.sendKeys("@");

        WebElement mentionOption = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//div[@data-testid='popup-wrapper']//div[@role='option'][contains(@aria-label,'"
                        + mentionFragment + "')]")));
        mentionOption.click();

        editor.sendKeys(" " + commentText);
        saveComment();
    }

    private WebElement openCommentEditor() {
        WebElement skeletonButton = wait.until(ExpectedConditions.elementToBeClickable(newCommentSkeletonButtonLocator));
        skeletonButton.click();

        WebElement editor = wait.until(ExpectedConditions.visibilityOfElementLocated(commentEditorLocator));
        editor.click();
        return editor;
    }

    private void saveComment() {
        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(commentSaveButtonLocator));
        saveButton.click();
    }

    /**
     * Check whether a comment containing the given text is present on the card.
     *
     * @param text Text (or fragment) to look for among posted comments
     * @return true if a matching comment is found
     */
    public boolean isCommentPresent(String text) {
        return isActivityEntryPresent(text);
    }

    /**
     * Toggle the Watch/Subscribe state of the currently open card via the "..." actions menu.
     */
    public void toggleWatch() {
        WebElement actionsButton = wait.until(ExpectedConditions.elementToBeClickable(actionsButtonLocator));
        actionsButton.click();

        WebElement subscribeButton = wait.until(ExpectedConditions.elementToBeClickable(subscribedButtonLocator));
        subscribeButton.click();

        new Actions(driver).sendKeys(Keys.ESCAPE).perform();
    }

    /**
     * Ensure the currently open card is watched, toggling it on only if it isn't
     * already - toggleWatch() flips state regardless of current state, so calling it
     * blindly can turn watching OFF if a prior run left the card already watched.
     */
    public void ensureWatched() {
        if (!isWatched()) {
            toggleWatch();
        }
    }

    /**
     * Check whether the currently open card is being watched.
     *
     * @return true if the Watch/Subscribe control reflects a watching state
     */
    public boolean isWatched() {
        try {
            WebElement actionsButton = wait.until(ExpectedConditions.elementToBeClickable(actionsButtonLocator));
            actionsButton.click();

            WebElement subscribeButton =
                    wait.until(ExpectedConditions.visibilityOfElementLocated(subscribedButtonLocator));
            boolean watching = "true".equals(subscribeButton.getAttribute("aria-pressed"));

            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            return watching;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check whether the card's activity feed (comments and system actions) contains an
     * entry matching the given text.
     *
     * @param textFragment Text (or fragment) to look for among activity entries
     * @return true if a matching activity entry is found
     */
    public boolean isActivityEntryPresent(String textFragment) {
        try {
            return wait.until(d -> d.findElements(activityFeedItemLocator).stream()
                    .anyMatch(entry -> entry.getText().contains(textFragment)));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clicks the Labels button inside the open card modal using JS click
     * to avoid overlay interception issues.
     */
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

    /**
     * Selects a label by its color name from the Labels menu.
     *
     * @param color The color name (e.g. "green", "red") of the label to select.
     */
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
    /**
     * Get the "Add a card" button element for the current fixture list, for layout/rendering assertions.
     */
    public WebElement getAddCardButtonElement() {
        By addCardBtn = By.xpath("//button[@aria-label='Add a card in " + TestData.listName + "']");
        return wait.until(ExpectedConditions.elementToBeClickable(addCardBtn));
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

    /**
     * Clicks the Dates button inside the open card modal.
     * Once a due date is already set, the "Dates" quick-action button is replaced by
     * a due-date badge button — picks whichever is actually present.
     */
    public void clickDates() {

        System.out.println("STEP: Clicking Dates...");

        // Once a due date is already set, the "Dates" quick-action button is replaced by
        // a due-date badge button (e.g. "Sep 15, 8:21 PM") - clicking it reopens the same
        // date editor. Pick whichever is actually present.
        By datesQuickButton = By.xpath("//button[normalize-space()='Dates']");
        By dueDateBadge     = By.cssSelector(
                "button[data-testid='due-date-badge-with-date-range-picker']"
        );

        List<WebElement> quick = driver.findElements(datesQuickButton);
        By target = (!quick.isEmpty() && quick.get(0).isDisplayed())
                ? datesQuickButton
                : dueDateBadge;

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

    /**
     * Clears and enters the given due date string into the due date input field.
     *
     * @param dueDate The due date string to enter (e.g. "09/15/2026").
     */
    public void enterDueDate(String dueDate) {

        System.out.println("STEP: Entering due date: " + dueDate);

        WebElement dateField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(dueDateField)
        );

        dateField.click();
        
        // Clear more thoroughly - use Ctrl+A and then type
        dateField.sendKeys(Keys.CONTROL + "a");
        dateField.sendKeys(Keys.BACK_SPACE);
        
        // Small pause to ensure field is cleared
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
        
        dateField.sendKeys(dueDate);

        System.out.println("STEP: Due date entered: " + dueDate);
    }

    /**
     * Clicks the Save button to persist the due date.
     */
    public void saveDueDate() {
        System.out.println("STEP: Saving due date...");

        WebElement saveButton = wait.until(
                ExpectedConditions.elementToBeClickable(saveDateButton)
        );

        saveButton.click();

        System.out.println("STEP: Due date saved successfully.");
        
        // Wait for the date picker popup to close and the date badge to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {}
        
        // Wait for the due date badge to be visible with the new date
        By dueDateBadge = By.cssSelector("button[data-testid='due-date-badge-with-date-range-picker']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(dueDateBadge));
    }

    /**
     * Returns true if the due date badge on the card contains the expected date string.
     *
     * @param expectedDate The date string to look for in the badge text.
     * @return true if the badge text contains the expected date, false otherwise.
     */
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
            System.out.println("Actual Due Date:   " + actualDate);

            return actualDate.contains(expectedDate);

        } catch (Exception e) {
            System.out.println("Due date badge not found: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clicks the Checklist button inside the open card modal.
     */
    public void clickChecklist() {
        System.out.println("STEP: Clicking Checklist...");

        WebElement checklist = wait.until(
                ExpectedConditions.elementToBeClickable(checklistButton)
        );

        checklist.click();
        
        // Wait for the checklist popup/menu to appear
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
        
        // Ensure the checklist title field is visible (popup is loaded)
        wait.until(ExpectedConditions.visibilityOfElementLocated(checklistTitleField));

        System.out.println("STEP: Checklist menu opened.");
    }

    /**
     * Clears and enters the checklist name in the checklist title field.
     *
     * @param checklistName The name to give the new checklist.
     */
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

    /**
     * Clicks the Add button to create the checklist.
     */
    public void addChecklist() {

        System.out.println("STEP: Clicking Add checklist...");

        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(checklistAddButton)
        );

        addButton.click();

        System.out.println("STEP: Checklist added successfully.");
        
        // Wait for the checklist to be created and the item input field to appear
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {}
        
        // Ensure the checklist item input field is now available
        wait.until(ExpectedConditions.presenceOfElementLocated(checklistItemInput));
    }

    /**
     * Clears and enters the given item name into the checklist item input field.
     *
     * @param itemName The name of the checklist item to add.
     */
    public void enterChecklistItem(String itemName) {

        System.out.println("STEP: Entering checklist item: " + itemName);

        WebElement itemField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(checklistItemInput)
        );

        itemField.click();
        itemField.clear();
        itemField.sendKeys(itemName);

        System.out.println("STEP: Checklist item entered: " + itemName);
    }

    /**
     * Clicks the Add button to submit the checklist item.
     */
    public void addChecklistItem() {

        System.out.println("STEP: Clicking Add checklist item...");

        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(checklistItemAddButton)
        );

        addButton.click();

        System.out.println("STEP: Checklist item added successfully.");
    }

    /**
     * Returns true if the checklist item with the given name is visible.
     *
     * @param expectedItem The checklist item name to look for.
     * @return true if the item is displayed, false otherwise.
     */


    public boolean isChecklistItemDisplayed(String expectedItem) {

        By checkboxInput = checklistItemCheckboxInput(expectedItem);

        try {
            WebElement item = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(checkboxInput)
            );
            return item.isDisplayed();
        } catch (Exception e) {
            System.out.println(
                    "Checklist item '" + expectedItem + "' not found: " + e.getMessage()
            );
            return false;
        }
    }

    /**
     * Checks whether a checklist with the given name already exists on the card.
     * Used to avoid creating duplicate checklists when the fixture card is reused across runs.
     * Uses contains() rather than an exact match — the title element's accessible text
     * includes a hidden "Checklist" prefix concatenated with the visible name.
     *
     * @param checklistName The checklist name to check for.
     * @return true if the checklist heading is present, false otherwise.
     */
    public boolean isChecklistPresent(String checklistName) {
        By checklistHeading = By.xpath(
                "//h3[@data-testid='checklist-title']" +
                        "[contains(normalize-space(), '" + checklistName + "')]"
        );
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            return shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(checklistHeading)
            ).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks the checklist item only if it is not already checked.
     * Re-clicking an already-checked checkbox toggles it back off — this guard
     * prevents that from happening when the fixture card is reused across test reruns.
     *
     * @param itemName The checklist item name to check.
     */
    public void checkChecklistItem(String itemName) {

        if (isChecklistItemChecked(itemName)) {
            System.out.println(
                    "STEP: Checklist item '" + itemName + "' already checked, skipping."
            );
            return;
        }

        System.out.println("STEP: Checking checklist item: " + itemName);

        WebElement label = wait.until(
                ExpectedConditions.elementToBeClickable(
                        checklistItemCheckboxLabel(itemName)
                )
        );

        label.click();

        // The click is optimistic-UI, not instant — wait for the checkbox to actually
        // flip rather than assuming the click landed by the time we return.
        wait.until(
                d -> d.findElement(checklistItemCheckboxInput(itemName)).isSelected()
        );

        System.out.println("STEP: Checklist item checked: " + itemName);
    }

    /**
     * Returns true if the checklist item with the given name is currently checked.
     * Uses isSelected() to reflect the live checked property reliably.
     *
     * @param itemName The checklist item name to inspect.
     * @return true if checked, false otherwise.
     */
    public boolean isChecklistItemChecked(String itemName) {

        WebElement checkbox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        checklistItemCheckboxInput(itemName)
                )
        );

        // .isSelected() reflects the live checked property - unlike scraping class names
        // or innerHTML, which are unreliable.
        boolean checked = checkbox.isSelected();

        System.out.println(
                "STEP: Checklist item '" + itemName + "' checked: " + checked
        );

        return checked;
    }

    /**
     * Waits until the Labels button is clickable again after a previous interaction.
     */
    public void waitForLabelsButton() {
        By labelsButton = By.xpath("//button[normalize-space()='Labels']");

        wait.until(ExpectedConditions.elementToBeClickable(labelsButton));

        System.out.println("STEP: Labels button is ready again.");
    }

    /**
     * Debug helper — prints all accessible attributes of the green label element.
     */
    public void debugGreenLabel() {

        By greenLabel = By.cssSelector(
                "span[data-testid='card-label'][data-color='green']"
        );

        WebElement label = wait.until(
                ExpectedConditions.visibilityOfElementLocated(greenLabel)
        );

        System.out.println("===== GREEN LABEL DEBUG =====");
        System.out.println("Tag:         " + label.getTagName());
        System.out.println("Text:        " + label.getText());
        System.out.println("Class:       " + label.getAttribute("class"));
        System.out.println("Aria-label:  " + label.getAttribute("aria-label"));
        System.out.println("Aria-checked:" + label.getAttribute("aria-checked"));
        System.out.println("Data-color:  " + label.getAttribute("data-color"));
        System.out.println("Data-testid: " + label.getAttribute("data-testid"));
        System.out.println("Role:        " + label.getAttribute("role"));
        System.out.println("HTML:        " + label.getAttribute("outerHTML"));
        System.out.println("============================");
    }


    // ─────────────────────────────────────────────────────────────────────────
    // METHODS — Harshit: Attachments (link attachment)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Opens the "Add to card" menu and then clicks the Attachment option.
     */
    public void clickAttachment() {
        System.out.println("STEP: Opening 'Add to card' menu...");
        clickWithRetry(addToCardButton);

        System.out.println("STEP: Clicking Attachment...");
        clickWithRetry(attachmentButton);
        System.out.println("STEP: Attachment menu opened.");
    }

    /**
     * Scrolls the element into view and clicks it, retrying once with a JS-click fallback
     * if the first attempt throws (e.g. a transient overlay/re-render intercepts the click).
     * Same defensive pattern used for openBoard()/openCard() to survive React re-render timing.
     *
     * @param locator The By locator of the element to click.
     */
    private void clickWithRetry(By locator) {
        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();",
                    element
            );
        }
    }

    /**
     * Types the given URL into the attachment link input field.
     *
     * @param url The URL to attach.
     */
    public void attachLink(String url) {
        System.out.println("STEP: Attaching link: " + url);

        WebElement field = wait.until(
                ExpectedConditions.visibilityOfElementLocated(attachLinkInput)
        );

        field.click();
        field.clear();
        field.sendKeys(url);
    }

    /**
     * Clicks the submit button to confirm the link attachment.
     */
    public void confirmAttachLink() {
        System.out.println("STEP: Confirming link attachment...");

        WebElement submit = wait.until(
                ExpectedConditions.elementToBeClickable(attachLinkSubmitButton)
        );

        submit.click();
        System.out.println("STEP: Link attachment submitted.");
    }

    /**
     * Returns true if an attachment matching the given URL or name fragment is already present.
     * Used to avoid re-attaching the same link when the fixture card is reused across runs.
     *
     * @param urlOrNameFragment A substring of the URL or display name to search for.
     * @return true if a matching attachment is found, false otherwise.
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

    /**
     * Attaches the given link only if it is not already present on the card.
     *
     * @param url The URL to attach.
     */
    public void ensureLinkAttached(String url) {
        if (isAttachmentPresent(url)) {
            System.out.println("STEP: Attachment '" + url + "' already present, skipping.");
            return;
        }
        clickAttachment();
        attachLink(url);
        confirmAttachLink();
    }


    // ─────────────────────────────────────────────────────────────────────────
    // METHODS — Harshit: Cover (solid color)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Clicks the Cover button inside the open card modal.
     */
    public void clickCover() {
        System.out.println("STEP: Clicking Cover...");

        WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(coverButton)
        );

        button.click();
        System.out.println("STEP: Cover menu opened.");
    }

    /**
     * Selects the first available cover color swatch and dismisses the popover.
     * The cover popover stays open after picking a color and overlaps the card
     * dialog's own controls — dismiss with Escape after clicking.
     */
    public void selectCoverColor() {
        System.out.println("STEP: Selecting a cover color...");

        WebElement swatch = wait.until(
                ExpectedConditions.elementToBeClickable(coverColorSwatch)
        );

        swatch.click();
        System.out.println("STEP: Cover color applied.");

        new org.openqa.selenium.interactions.Actions(driver)
                .sendKeys(org.openqa.selenium.Keys.ESCAPE)
                .perform();
    }

    /**
     * Returns true if the cover indicator element is visible on the open card modal.
     *
     * @return true if a cover is applied, false otherwise.
     */
    public boolean isCoverApplied() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            return shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(coverAppliedIndicator)
            ).isDisplayed();
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



    // Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
    // DRAG AND DROP
    // Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬

    /**
     * Drags a card by name from a source list to a target list.
     *
     * @param cardName   visible text of the card to drag
     * @param sourceList name of the list the card currently lives in
     * @param targetList name of the list to drop the card into
     */
    /**
     * Drags a card by name from a source list to a target list.
     * Optimized with CSS selectors + Java filtering (much faster than XPath text matching).
     *
     * @param cardName   visible text of the card to drag
     * @param sourceList name of the list the card currently lives in
     * @param targetList name of the list to drop the card into
     */
    public void dragCardToList(String cardName, String sourceList, String targetList) {
        // Find source card <li> element (has draggable="true")
        WebElement sourceCard = findCardInList(cardName, sourceList);
        if (sourceCard == null) {
            throw new RuntimeException("Card '" + cardName + "' not found in list '" + sourceList + "'");
        }

        // Find target list's <ol> card container
        WebElement targetListEl = findListCardContainer(targetList);
        if (targetListEl == null) {
            throw new RuntimeException("List '" + targetList + "' not found");
        }

        System.out.println("DEBUG: Dragging card '" + cardName + "' from '" + sourceList + "' to '" + targetList + "'");

        // Chrome WebDriver properly synthesizes HTML5 drag events
        new Actions(driver)
            .dragAndDrop(sourceCard, targetListEl)
            .perform();

        // Wait for Trello's optimistic UI to settle
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Find a card element by name within a specific list.
     * Uses fast CSS selectors + Java text filtering instead of slow XPath.
     */
    private WebElement findCardInList(String cardName, String listName) {
        // Find the list wrapper by filtering all lists
        WebElement listWrapper = findListWrapper(listName);
        if (listWrapper == null) {
            System.out.println("DEBUG: List wrapper '" + listName + "' not found");
            return null;
        }

        // Wait a moment for cards to load (Trello lazy-loads card tiles)
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Get the list-cards container first
        WebElement cardsContainer;
        try {
            cardsContainer = listWrapper.findElement(By.cssSelector("[data-testid='list-cards']"));
        } catch (Exception e) {
            System.out.println("DEBUG: list-cards container not found in list wrapper");
            return null;
        }

        // Get all card elements from the container
        List<WebElement> cards = cardsContainer.findElements(By.cssSelector("[data-testid='list-card']"));
        System.out.println("DEBUG: Found " + cards.size() + " cards in list '" + listName + "'");

        // Filter by card name text (use trim and equals for exact match)
        for (WebElement card : cards) {
            try {
                WebElement cardNameEl = card.findElement(By.cssSelector("[data-testid='card-name']"));
                String actualCardName = cardNameEl.getText().trim();
                System.out.println("DEBUG: Checking card '" + actualCardName + "' against '" + cardName + "'");

                if (actualCardName.equals(cardName)) {
                    System.out.println("DEBUG: Found matching card!");
                    return card;
                }
            } catch (Exception e) {
                // Card doesn't have card-name element, skip
                System.out.println("DEBUG: Skipping card without card-name element");
            }
        }

        System.out.println("DEBUG: Card '" + cardName + "' not found in list '" + listName + "'");
        return null;
    }

    /**
     * Find a list wrapper by name.
     * Uses CSS selectors + Java filtering for speed.
     */
    private WebElement findListWrapper(String listName) {
        List<WebElement> lists = driver.findElements(By.cssSelector("[data-testid='list-wrapper']"));
        System.out.println("DEBUG: Found " + lists.size() + " list wrappers on board");

        for (WebElement list : lists) {
            try {
                WebElement listNameEl = list.findElement(By.cssSelector("[data-testid='list-name']"));
                String actualListName = listNameEl.getText().trim();
                System.out.println("DEBUG: Checking list '" + actualListName + "' against '" + listName + "'");

                if (actualListName.equals(listName)) {
                    System.out.println("DEBUG: Found matching list!");
                    return list;
                }
            } catch (Exception e) {
                // List doesn't have list-name element, skip
                System.out.println("DEBUG: Skipping list without list-name element");
            }
        }

        System.out.println("DEBUG: List '" + listName + "' not found");
        return null;
    }

    /**
     * Find the card container (ol element) of a list by name.
     */
    private WebElement findListCardContainer(String listName) {
        WebElement listWrapper = findListWrapper(listName);
        if (listWrapper == null) return null;

        try {
            return listWrapper.findElement(By.cssSelector("[data-testid='list-cards']"));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fallback drag implementation using JavaScript when Selenium Actions fail.
     * Simulates full HTML5 drag-and-drop with proper DataTransfer object and mouse events.
     */
    private void dragCardViaJavaScript(WebElement source, WebElement target) {
        String dragDropScript =
            "function simulateDragDrop(source, target) {" +
            "  var dataTransfer = {" +
            "    data: {}," +
            "    dropEffect: 'move'," +
            "    effectAllowed: 'all'," +
            "    files: []," +
            "    items: []," +
            "    types: []," +
            "    setData: function(format, data) {" +
            "      this.data[format] = data;" +
            "      if (this.types.indexOf(format) === -1) this.types.push(format);" +
            "    }," +
            "    getData: function(format) { return this.data[format]; }," +
            "    clearData: function(format) { if (format) delete this.data[format]; else this.data = {}; }," +
            "    setDragImage: function() {}" +
            "  };" +
            "" +
            "  var rect = source.getBoundingClientRect();" +
            "  var clientX = rect.left + rect.width / 2;" +
            "  var clientY = rect.top + rect.height / 2;" +
            "" +
            "  // Dragstart" +
            "  var dragstart = document.createEvent('DragEvent');" +
            "  dragstart.initMouseEvent('dragstart', true, true, window, 0, 0, 0, clientX, clientY, false, false, false, false, 0, null);" +
            "  Object.defineProperty(dragstart, 'dataTransfer', { value: dataTransfer, enumerable: true });" +
            "  source.dispatchEvent(dragstart);" +
            "" +
            "  var targetRect = target.getBoundingClientRect();" +
            "  var targetX = targetRect.left + targetRect.width / 2;" +
            "  var targetY = targetRect.top + targetRect.height / 2;" +
            "" +
            "  // Dragover on target" +
            "  var dragover = document.createEvent('DragEvent');" +
            "  dragover.initMouseEvent('dragover', true, true, window, 0, 0, 0, targetX, targetY, false, false, false, false, 0, null);" +
            "  Object.defineProperty(dragover, 'dataTransfer', { value: dataTransfer, enumerable: true });" +
            "  target.dispatchEvent(dragover);" +
            "" +
            "  // Drop on target" +
            "  var drop = document.createEvent('DragEvent');" +
            "  drop.initMouseEvent('drop', true, true, window, 0, 0, 0, targetX, targetY, false, false, false, false, 0, null);" +
            "  Object.defineProperty(drop, 'dataTransfer', { value: dataTransfer, enumerable: true });" +
            "  target.dispatchEvent(drop);" +
            "" +
            "  // Dragend on source" +
            "  var dragend = document.createEvent('DragEvent');" +
            "  dragend.initMouseEvent('dragend', true, true, window, 0, 0, 0, targetX, targetY, false, false, false, false, 0, null);" +
            "  Object.defineProperty(dragend, 'dataTransfer', { value: dataTransfer, enumerable: true });" +
            "  source.dispatchEvent(dragend);" +
            "}" +
            "simulateDragDrop(arguments[0], arguments[1]);";

        ((JavascriptExecutor) driver).executeScript(dragDropScript, source, target);

        // Wait for Trello's optimistic UI to settle
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Drags a card within the same list to reorder it.
     *
     * @param cardName    name of the card to drag
     * @param targetCard  name of the card to drop onto (new position)
     * @param listName    list both cards belong to
     */
    /**
     * Drags a card within the same list to reorder it.
     * Optimized with CSS selectors + Java filtering.
     *
     * @param cardName    name of the card to drag
     * @param targetCard  name of the card to drop onto (new position)
     * @param listName    list both cards belong to
     */
    public void dragCardInList(String cardName, String targetCard, String listName) {
        WebElement source = findCardInList(cardName, listName);
        WebElement target = findCardInList(targetCard, listName);

        if (source == null) {
            throw new RuntimeException("Source card '" + cardName + "' not found in list '" + listName + "'");
        }
        if (target == null) {
            throw new RuntimeException("Target card '" + targetCard + "' not found in list '" + listName + "'");
        }

        System.out.println("DEBUG: Reordering card '" + cardName + "' to position of '" + targetCard + "' in list '" + listName + "'");

        // Chrome WebDriver properly synthesizes HTML5 drag events
        new Actions(driver)
            .dragAndDrop(source, target)
            .perform();

        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Returns the index of a card within its list, or -1 if not found.
     * Waits for the list to be visible before scanning so this is safe to
     * call immediately after navigating to the board.
     *
     * @param cardName name of the card to locate
     * @param listName list to search in
     */
    public int getCardIndexInList(String cardName, String listName) {
        By listBy = By.xpath(
                "//li[@data-testid='list-wrapper']" +
                "[.//h2[@data-testid='list-name']//span[text()='" + listName + "']]" +
                "//ol[@data-testid='list-cards']");

        // Wait for the list container to be visible before reading card order
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(listBy));
        } catch (Exception e) {
            return -1; // list does not exist on this board
        }

        By allCardsBy = By.xpath(
                "//li[@data-testid='list-wrapper']" +
                "[.//h2[@data-testid='list-name']//span[text()='" + listName + "']]" +
                "//ol[@data-testid='list-cards']//li[@data-testid='list-card']");

        List<WebElement> cards = driver.findElements(allCardsBy);
        for (int i = 0; i < cards.size(); i++) {
            String text = cards.get(i)
                    .findElement(By.xpath(".//a[@data-testid='card-name']")).getText();
            if (text.equals(cardName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds which list a card currently lives in by searching every list on the board.
     * Returns the list name, or null if the card is not found on the board.
     *
     * @param cardName name of the card to locate
     */
    public String findListContainingCard(String cardName) {
        By allListNames = By.xpath(
                "//li[@data-testid='list-wrapper']//h2[@data-testid='list-name']//span");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(allListNames));
        } catch (Exception e) {
            return null;
        }
        List<WebElement> lists = driver.findElements(allListNames);
        for (WebElement list : lists) {
            String listName = list.getText();
            if (getCardIndexInList(cardName, listName) >= 0) {
                return listName;
            }
        }
        return null;
    }

    /**
     * Returns true if a card with the given name is visible inside the specified list.
     *
     * @param cardName visible text of the card
     * @param listName name of the list to look in
     */
    public boolean isCardInList(String cardName, String listName) {
        By cardBy = By.xpath(
                "//li[@data-testid='list-wrapper']" +
                "[.//h2[@data-testid='list-name']//span[text()='" + listName + "']]" +
                "//li[@data-testid='list-card'][.//a[@data-testid='card-name'][text()='" + cardName + "']]"
        );
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(cardBy)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the number of card elements matching the given card name inside the specified list.
     * A result of 0 means the card is no longer present in that list.
     *
     * @param cardName visible text of the card
     * @param listName name of the list to check
     */
    public int countCardsInList(String cardName, String listName) {
        By cardBy = By.xpath(
                "//li[@data-testid='list-wrapper']" +
                "[.//h2[@data-testid='list-name']//span[text()='" + listName + "']]" +
                "//li[@data-testid='list-card'][.//a[@data-testid='card-name'][text()='" + cardName + "']]"
        );
        return driver.findElements(cardBy).size();
    }




    /**
     * Get every cover color swatch in the open cover popover, for layout/rendering
     * assertions (a color grid is exactly the kind of element that clips at narrow
     * viewport widths without necessarily causing page-level horizontal overflow).
     */
    public List<WebElement> getCoverColorSwatchElements() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(coverColorSwatch));
        return driver.findElements(coverColorSwatch);
    }

    /**
     * Dismisses the cover color popover without picking a color - same Escape-key
     * approach used internally by selectCoverColor() once a color has been applied.
     */
    public void closeCoverPopover() {
        new org.openqa.selenium.interactions.Actions(driver).sendKeys(Keys.ESCAPE).perform();
    }

    /**
     * Returns the close-dialog button element, for layout/rendering assertions.
     */
    public WebElement getCloseCardButtonElement() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(closeCardButton));
    }

    /**
     * Returns the card description content area element, for layout/rendering assertions.
     * Falls back to the "Add a more detailed description" button if no description has been
     * saved yet (both represent the description zone on the card back).
     */
    public WebElement getDescriptionAreaElement() {
        By savedArea = By.cssSelector("[data-testid='description-content-area']");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(savedArea));
        } catch (Exception e) {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionButton));
        }
    }

    /**
     * Returns the checkbox label element for the given checklist item,
     * for layout/rendering assertions.
     *
     * @param itemName visible text / aria-label of the checklist item
     */
    public WebElement getChecklistItemCheckboxElement(String itemName) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                checklistItemCheckboxInput(itemName)));
    }

    /**
     * Drags a card to an invalid drop target (the page header) to verify it
     * snaps back to its original position in the list.
     *
     * @param cardName name of the card to drag
     * @param listName list the card currently belongs to
     */
    public void dragCardToInvalidTarget(String cardName, String listName) {
        By sourceCardBy = By.xpath(
                "//li[@data-testid='list-wrapper']" +
                "[.//h2[@data-testid='list-name']//span[text()='" + listName + "']]" +
                "//ol[@data-testid='list-cards']" +
                "//li[@data-testid='list-card'][.//a[@data-testid='card-name'][text()='" + cardName + "']]");

        WebElement sourceCard   = wait.until(ExpectedConditions.visibilityOfElementLocated(sourceCardBy));
        WebElement invalidTarget = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//nav[@data-testid='authenticated-header']")));

        Duration pause = Duration.ofMillis(1000);
        new Actions(driver)
                .moveToElement(sourceCard).pause(pause)
                .clickAndHold(sourceCard).pause(pause)
                .moveByOffset(5, 5).pause(pause)
                .moveToElement(invalidTarget).pause(pause)
                .release().pause(pause)
                .build().perform();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIXTURE HELPERS — Ensure card prerequisites exist
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Ensures a card with the given name exists, creating it if necessary.
     * Returns true if the card was created, false if it already existed.
     *
     * @param cardName Name of the card to ensure exists
     * @return true if card was created, false if already existed
     */
    public boolean ensureCardExists(String cardName) {
        if (isCardCreated(cardName)) {
            System.out.println("[CardPage] Card '" + cardName + "' already exists.");
            return false;
        }

        System.out.println("[CardPage] Creating card: " + cardName);
        clickAddCardButton();
        enterCardTitle(cardName);
        clickAddCardSubmit();
        
        // Wait and verify creation
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
        
        if (!isCardCreated(cardName)) {
            throw new IllegalStateException(
                "Failed to create card '" + cardName + "' - card not found after creation attempt."
            );
        }
        
        System.out.println("[CardPage] Card '" + cardName + "' created successfully.");
        return true;
    }
}