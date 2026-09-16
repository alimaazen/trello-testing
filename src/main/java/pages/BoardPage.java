package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object Model for an open Trello board.
 * Covers list/card creation and board-level collaboration actions: sharing,
 * inviting members, and managing member roles.
 *
 * Locators were written against Trello's known data-testid conventions without
 * live DOM access and may need small adjustments on the first real run.
 */
public class BoardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Board Menu / Archive Panel

    private By archivedCardItemLocator(String cardTitle) {
        return By.xpath(
                "//div[@data-testid='archived-card']" +
                        "[.//a[@data-testid='card-name' and normalize-space(text())='" + cardTitle + "']]"
        );
    }

    private By deleteButtonInsideArchivedCard(String cardTitle) {
        return By.cssSelector(
                "button[aria-label='Delete " + cardTitle + "']"
        );
    }

    private final By boardMenuButton = By.cssSelector(
            "button[aria-label='Show menu']"
    );

    private final By closePanelButton = By.cssSelector(
            "button[aria-label='Close popover']"
    );

    private final By archivedItemsOption = By.xpath(
            "//button[.//div[normalize-space(text())='Archived items']]"
    );


    private final By archivedItemsPanel = By.cssSelector(
            "div[data-testid='board-menu-container']"
    );

    private By addListButtonLocator = By.cssSelector("[data-testid='list-name-textarea'][placeholder]");
    private By listComposerOpenButtonLocator = By.cssSelector("[data-testid='list-composer-button']");
    private By listComposerAddButtonLocator = By.cssSelector("button[data-testid='list-composer-add-list-button']");
    private By listHeaderLocator = By.cssSelector("[data-testid='list-name']");
    private By listLocator = By.cssSelector("[data-testid='list']");
    private By addCardButtonLocator = By.cssSelector("[data-testid='list-add-card-button']");
    private By addCardTextareaLocator = By.cssSelector("[data-testid='list-card-composer-textarea']");
    private By addCardConfirmButtonLocator =
            By.cssSelector("button[data-testid='list-card-composer-add-card-button']");
    private By cardTileLocator = By.cssSelector("[data-testid='trello-card']");

    private By shareButtonLocator = By.cssSelector("button[data-testid='board-share-button']");
    private By shareSearchInputLocator = By.cssSelector("input[data-testid='add-members-input']");
    private By typeaheadSuggestionLocator = By.cssSelector("[data-testid='team-invitee-option']");
    private By sendInviteButtonLocator = By.cssSelector("button[data-testid='team-invite-submit-button']");
    private By memberItemLocator = By.cssSelector("[data-testid='member-item']");
    private By memberRoleSelectLocator = By.cssSelector("[data-testid='board-permission-selector-dropdown--trigger']");
    private By closeDialogButtonLocator = By.cssSelector("button[data-testid='board-invite-modal-close-button']");
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
     * Add a new list to the board.
     *
     * @param listName Name for the new list
     */
    public void addList(String listName) {
        // Count existing lists before adding
        int listCountBefore = driver.findElements(listLocator).size();

        WebElement openComposer = wait.until(ExpectedConditions.elementToBeClickable(listComposerOpenButtonLocator));
        openComposer.click();

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(addListButtonLocator));
        input.sendKeys(listName);

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(listComposerAddButtonLocator));
        addButton.click();

        // Wait for list count to increase (new list added)
        wait.until(d -> d.findElements(listLocator).size() > listCountBefore);

        // Give Trello a moment to finish rendering the new list's title
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Add a card with the given title to the named list.
     *
     * @param listName  Name of the list to add the card to
     * @param cardTitle Title for the new card
     */
    public void addCard(String listName, String cardTitle) {
        WebElement targetList = findListByName(listName);
        WebElement addCardButton = targetList.findElement(addCardButtonLocator);
        addCardButton.click();

        WebElement textarea = wait.until(ExpectedConditions.visibilityOfElementLocated(addCardTextareaLocator));
        textarea.sendKeys(cardTitle);

        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(addCardConfirmButtonLocator));
        confirmButton.click();

        wait.until(d -> d.findElements(cardTileLocator).stream()
                .anyMatch(card -> cardTileMatchesTitle(card, cardTitle)));

        new org.openqa.selenium.interactions.Actions(driver).sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
    }

    /**
     * Open the card detail modal for the card with the given title.
     *
     * @param cardTitle Title of the card to open
     */
    public void openCard(String cardTitle) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        boolean everFound = false;

        for (int attempt = 0; attempt < 15; attempt++) {
            // A second/invited user's board view can lag behind a card just created by
            // another user - refresh periodically to force a re-sync rather than relying
            // solely on live updates reaching this session.
            if (attempt > 0 && attempt % 4 == 0) {
                driver.navigate().refresh();
            }
            List<WebElement> cards = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(cardTileLocator));
            WebElement match = null;
            for (WebElement card : cards) {
                if (cardTileMatchesTitle(card, cardTitle)) {
                    match = card;
                    break;
                }
            }
            if (match == null) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }
            everFound = true;

            WebElement nameLink = match.findElement(By.cssSelector("[data-testid='card-name']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nameLink);

            try {
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-testid='card-back-panel']")));
                return;
            } catch (org.openqa.selenium.TimeoutException e) {
                // Card panel didn't open in time - the click may have raced the list's
                // optimistic-UI update. Re-fetch the card element and retry.
            }
        }
        if (!everFound) {
            throw new IllegalStateException("No card found with title: " + cardTitle);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='card-back-panel']")));
    }

    /**
     * Ensure a card with the given title exists on the named list, creating the list
     * and/or the card first if either is missing. Used by tests so they don't depend
     * on fixture cards having been created manually ahead of time.
     *
     * @param listName  Name of the list the card should be on
     * @param cardTitle Title of the card to ensure exists
     */
    public void ensureCardExists(String listName, String cardTitle) {
        // Wait a moment for lists to appear if board just loaded, but don't fail if none exist yet
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<WebElement> lists = driver.findElements(listLocator);
        boolean listFound = false;
        for (WebElement list : lists) {
            if (list.findElement(listHeaderLocator).getText().trim().equals(listName)) {
                listFound = true;
                break;
            }
        }
        if (!listFound) {
            addList(listName);
        }

        // Give the list's card tiles a moment to finish rendering after board/list load
        // before deciding the card doesn't exist yet - otherwise this races and creates
        // duplicate cards on repeated runs.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (WebElement card : driver.findElements(cardTileLocator)) {
            if (cardTileMatchesTitle(card, cardTitle)) {
                return;
            }
        }
        addCard(listName, cardTitle);
    }

    /**
     * Check whether a card tile's name matches the given title. Compares against the
     * nested card-name element rather than the tile's full text, since Trello appends
     * badges (comment count, member count, due date, etc.) as extra text within the
     * same tile.
     */
    private boolean cardTileMatchesTitle(WebElement card, String cardTitle) {
        try {
            return card.findElement(By.cssSelector("[data-testid='card-name']"))
                    .getText().trim().equals(cardTitle);
        } catch (Exception e) {
            return false;
        }
    }

    private WebElement findListByName(String listName) {
        List<WebElement> lists = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(listLocator));
        for (WebElement list : lists) {
            if (list.findElement(listHeaderLocator).getText().trim().equals(listName)) {
                return list;
            }
        }
        throw new IllegalStateException("No list found with name: " + listName);
    }

    /**
     * Open the board's Share dialog.
     * Retries if element becomes stale (page refresh/reload).
     */
    public void openShareDialog() {
        int attempts = 0;
        int maxAttempts = 3;
        
        while (attempts < maxAttempts) {
            try {
                WebElement shareButton = wait.until(ExpectedConditions.elementToBeClickable(shareButtonLocator));
                shareButton.click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(shareSearchInputLocator));
                return; // Success
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                attempts++;
                if (attempts >= maxAttempts) {
                    throw e;
                }
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     * Close whichever dialog is currently open (e.g. the Share dialog).
     */
    public void closeDialog() {
        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(closeDialogButtonLocator));
        closeButton.click();
    }

    /**
     * Invite a member to the board by email. Assumes the Share dialog is already open.
     * If the invitee is already a Workspace member, Trello shows a typeahead suggestion
     * that adds them instantly; otherwise falls back to sending an external invite.
     *
     * @param email Email address of the member to invite
     */
    public void inviteMemberByEmail(String email) {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(shareSearchInputLocator));
        searchInput.clear();
        searchInput.sendKeys(email);

        // Brief pause to let typeahead populate
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        boolean suggestionClicked = false;
        try {
            WebElement suggestion = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(typeaheadSuggestionLocator));
            suggestion.click();
            suggestionClicked = true;
            
            // Wait for member to be staged after clicking suggestion
            Thread.sleep(2000);
            
            // Check if Share button still visible (dialog didn't auto-close)
            List<WebElement> shareButtons = driver.findElements(sendInviteButtonLocator);
            if (shareButtons.isEmpty() || !shareButtons.get(0).isDisplayed()) {
                // Member was added immediately, dialog auto-closed
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                return;
            }
            
        } catch (Exception e) {
            // No typeahead suggestion - will send external invite
        }

        WebElement sendInviteButton =
                wait.until(ExpectedConditions.elementToBeClickable(sendInviteButtonLocator));
        sendInviteButton.click();

        // Wait dynamically for member count to increase (handles billing processing delay)
        int memberCountBefore = driver.findElements(memberItemLocator).size();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> {
                int currentCount = d.findElements(memberItemLocator).size();
                return currentCount > memberCountBefore;
            });
        } catch (Exception e) {
            // Timeout is OK - member might already be on board
        }
        
        // Let UI stabilize
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    /**
     * Check whether a member is listed on the board. Assumes the Share dialog (or
     * member list) is open.
     *
     * @param emailOrName Email or display name of the member to look for
     * @return true if a matching member is found
     */
    public boolean isMemberOnBoard(String emailOrName) {
        try {
            // Wait up to 10 seconds for the member to appear in the list
            return wait.until(driver -> {
                List<WebElement> members = driver.findElements(memberItemLocator);
                
                for (WebElement member : members) {
                    String memberText = member.getText();
                    // Try case-insensitive match
                    if (memberText.toLowerCase().contains(emailOrName.toLowerCase())) {
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
     * Set a board member's role. Assumes the Share dialog (or member list) is open.
     *
     * @param emailOrName Email or display name of the member
     * @param role        "Admin" or "Observer"
     */
    public void setMemberRole(String emailOrName, String role) {
        WebElement memberRow = findMemberRow(emailOrName);
        WebElement roleSelect = memberRow.findElement(memberRoleSelectLocator);
        roleSelect.click();

        WebElement roleOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//*[@data-item-title='true'][normalize-space(text())='" + role + "']")));
        roleOption.click();
    }

    /**
     * Get a board member's current role. Assumes the Share dialog (or member list) is open.
     *
     * @param emailOrName Email or display name of the member
     * @return Role text (e.g. "Admin", "Observer"), or empty string if not found
     */
    public String getMemberRole(String emailOrName) {
        try {
            WebElement memberRow = findMemberRow(emailOrName);
            return memberRow.findElement(memberRoleSelectLocator).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private WebElement findMemberRow(String emailOrName) {
        List<WebElement> members = driver.findElements(memberItemLocator);
        
        for (WebElement member : members) {
            String memberText = member.getText();
            // Try case-insensitive match
            if (memberText.toLowerCase().contains(emailOrName.toLowerCase())) {
                return member;
            }
        }
        
        throw new IllegalStateException("No board member found matching: " + emailOrName);
    }

    /**
     * Check whether the currently logged-in user can add a card - used to verify an
     * Observer is restricted from editing.
     *
     * @return true if the add-card control is available on at least one list
     */
    public boolean isAddCardAvailable() {
        try {
            return !driver.findElements(addCardButtonLocator).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check whether the Share button is available and enabled - used to verify an
     * Admin has full board access.
     *
     * @return true if the Share button is present and enabled
     */
    public boolean isShareButtonEnabled() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(shareButtonLocator)).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Confirm button for DELETING A CARD from the Archived Items panel.
     * Trello shows a popup — this targets the confirm button inside that popup.
     */
    private final By archivedCardDeleteConfirmBtn (String cardTitle) {
        return By.xpath(
                "//button[@aria-label='Permanently delete " + cardTitle + "']"
        );
    }

    /**
     * Verifies the archived card is GONE from the archived items panel after deletion.
     */
    private By archivedCardByTitle(String cardTitle) {
        return By.xpath(
                "//*[@data-testid='archived-card-list-item']" +
                        "//*[normalize-space(text())='" + cardTitle + "']"
        );
    }


    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS — Resilient Click / Type
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Resilient click helper that falls back to Actions then JavascriptExecutor
     * if a standard click is intercepted (e.g. by a React overlay).
     *
     * @param locator The By locator of the element to click.
     */
    private void safeClick(By locator) {
        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );
        try {
            element.click();
        } catch (Exception e) {
            try {
                new org.openqa.selenium.interactions.Actions(driver)
                        .moveToElement(element).click().perform();
            } catch (Exception ex) {
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].click();", element);
            }
        }
    }

    /**
     * Resilient sendKeys helper that waits for visibility and clears before typing.
     *
     * @param locator The By locator of the input element.
     * @param text    The text to type.
     */
    private void safeType(By locator, String text) {
        WebElement element = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
        try {
            element.clear();
        } catch (Exception e) {
            // Some input elements do not support clear()
        }
        element.sendKeys(text);
    }


    // ─────────────────────────────────────────────────────────────────────────
    // METHODS — Board Navigation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Finds and clicks an existing board tile by its visible name.
     *
     * @param boardName The exact name of the board as displayed on the home screen.
     */
    public void openExistingBoard(String boardName) {
        WebElement board = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//a[@title='" + boardName + "'" +
                                        " and @aria-label='" + boardName + "']"
                        )
                )
        );
        board.click();
    }


    // ─────────────────────────────────────────────────────────────────────────
    // METHODS — Board Creation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a new Trello board with the specified name.
     * Retries opening the create dropdown up to 3 times to survive SPA timing.
     *
     * @param name The name to give the new board.
     */
    public void createNewBoard(String name) {
        // Brief pause to let SPAs fully initialize event handlers on EAGER load
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        // Click create button and retry if the menu doesn't appear
        int retries    = 3;
        boolean menuOpened = false;

        while (retries > 0 && !menuOpened) {
            safeClick(headerCreateMenuBtn);
            try {
                // Wait briefly for the dropdown option to be visible
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
                shortWait.until(
                        ExpectedConditions.visibilityOfElementLocated(headerCreateBoardBtn)
                );
                menuOpened = true;
            } catch (Exception e) {
                System.out.println(
                        "Dropdown menu didn't open. Retrying... (Retries left: " + (retries - 1) + ")"
                );
                retries--;
            }
        }

        safeClick(headerCreateBoardBtn);

        try {
            safeType(boardTitleInput, name);
        } catch (Exception e) {
            System.out.println("DEBUG: Diagnostics dump for all matching elements in DOM:");
            try {
                for (WebElement el : driver.findElements(
                        By.cssSelector("input, textarea, button, [data-testid]"))
                ) {
                    String tag         = el.getTagName();
                    String id          = el.getAttribute("id");
                    String placeholder = el.getAttribute("placeholder");
                    String testid      = el.getAttribute("data-testid");
                    String nameAttr    = el.getAttribute("name");

                    if ((testid != null && !testid.isEmpty())
                            || (placeholder != null && !placeholder.isEmpty())
                            || "input".equals(tag)
                            || "textarea".equals(tag)) {
                        System.out.println(
                                " -> TAG: " + tag +
                                        " | ID: " + id +
                                        " | TESTID: " + testid +
                                        " | NAME: " + nameAttr +
                                        " | PLACEHOLDER: " + placeholder
                        );
                    }
                }
            } catch (Exception ex) {
                System.out.println("Diagnostics dump failed: " + ex.getMessage());
            }
            throw e;
        }

        safeClick(finalCreateBtn);

        // Wait until redirected to the new board URL
        wait.until(ExpectedConditions.urlContains("/b/"));
        
        // Wait for board title to be displayed and match the expected name
        // This ensures the board is fully created before returning
        wait.until(ExpectedConditions.visibilityOfElementLocated(boardTitleDisplay));
        
        // Give Trello time to fully render and update the board title
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        
        // Wait for the board title to actually contain or match our board name
        // Using a custom wait condition to handle long names that might be truncated
        wait.until(driver -> {
            String displayedTitle = getBoardTitle();
            // For very long names, Trello might truncate, so check if it starts with first 50 chars
            String namePrefix = name.length() > 50 ? name.substring(0, 50) : name;
            return displayedTitle.contains(namePrefix) || displayedTitle.equals(name);
        });
    }


    // ─────────────────────────────────────────────────────────────────────────
    // METHODS — Board Title / Star
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the currently displayed board title text.
     *
     * @return The board title as a trimmed string.
     */
    public String getBoardTitle() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(boardTitleDisplay)
        ).getText().trim();
    }

    /**
     * Get the board title element, for layout/rendering assertions.
     */
    public WebElement getBoardTitleElement() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(boardTitleDisplay));
    }

    /**
     * Renames the board to the given new name.
     *
     * @param newName The new board title to set.
     */
    public void updateBoardTitle(String newName) {
        safeClick(boardTitleDisplay);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        WebElement titleInput = wait.until(
                ExpectedConditions.elementToBeClickable(boardTitleInputField)
        );
        titleInput.click();
        titleInput.sendKeys(Keys.CONTROL + "a");
        titleInput.sendKeys(Keys.BACK_SPACE);
        titleInput.sendKeys(newName);
        titleInput.sendKeys(Keys.ENTER);

        wait.until(
                ExpectedConditions.textToBePresentInElementLocated(boardTitleDisplay, newName)
        );

        // Click the board canvas to release focus from the edit field
        try {
            driver.findElement(
                    By.cssSelector("div.board-canvas, #board, .board-main-content")
            ).click();
        } catch (Exception ignored) {}

        // Let the React UI fully settle
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }

    /**
     * Stars or unstars the board by clicking the star button.
     */
    public void toggleStarBoard() {
        safeClick(boardStarBtn);
    }

    /**
     * Returns true if the board is currently starred.
     * "Unstar board" in aria-label means it IS starred; "Star board" means it is not.
     *
     * @return true if starred, false otherwise.
     */
    public boolean isBoardStarred() {
        WebElement starButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(boardStarBtn)
        );
        String label = starButton.getAttribute("aria-label");
        return label != null && label.contains("Unstar");
    }


    // ─────────────────────────────────────────────────────────────────────────
    // METHODS — Board Background / Visibility
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Changes the board background to a solid color via the board menu.
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
     * Changes the board visibility to Private.
     */
    public void changeVisibilityToPrivate() {
        safeClick(boardVisibilityBtn);
        safeClick(privateVisibilityOption);
    }

    /**
     * Returns the current visibility label text of the board.
     *
     * @return The aria-label or text of the visibility button.
     */
    public String getVisibilityText() {
        WebElement btn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(boardVisibilityBtn)
        );
        String ariaLabel = btn.getAttribute("aria-label");
        if (ariaLabel != null && !ariaLabel.isEmpty()) {
            return ariaLabel;
        }
        return btn.getText().trim();
    }


    // ─────────────────────────────────────────────────────────────────────────
    // METHODS — Close / Reopen / Permanently Delete Board
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Closes (archives) the board via the board menu.
     */
    public void closeBoard() {
        System.out.println("[DEBUG closeBoard] Starting closeBoard procedure...");
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));

        // Step 1: Check if already closed
        try {
            if (driver.findElement(closedBoardMessage).isDisplayed()) {
                System.out.println("[DEBUG closeBoard] Board is already closed!");
                return;
            }
        } catch (Exception ignored) {}

        // Step 2: Open menu if not already open
        try {
            System.out.println("[DEBUG closeBoard] Checking if menu is already open...");
            driver.findElement(closeBoardMenuLink);
            System.out.println("[DEBUG closeBoard] Menu is already open.");
        } catch (Exception e) {
            System.out.println("[DEBUG closeBoard] Menu not open. Clicking showMenuBtn...");
            try {
                WebElement menuBtn = wait.until(
                        ExpectedConditions.elementToBeClickable(showMenuBtn)
                );
                System.out.println(
                        "[DEBUG closeBoard] showMenuBtn found — text: '" +
                                menuBtn.getText() +
                                "', aria-label: '" +
                                menuBtn.getAttribute("aria-label") + "'"
                );
                safeClick(showMenuBtn);
                System.out.println("[DEBUG closeBoard] showMenuBtn clicked.");
            } catch (Exception ex) {
                System.out.println("[DEBUG closeBoard] showMenuBtn click failed: " + ex.getMessage());
            }
        }

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // Step 3: Click 'More' sub-menu if Close Board is not directly visible
        boolean closeLinkVisible = false;
        try {
            closeLinkVisible = driver.findElement(closeBoardMenuLink).isDisplayed();
        } catch (Exception ignored) {}

        if (!closeLinkVisible) {
            System.out.println("[DEBUG closeBoard] Close Board link not visible — trying moreBtn...");
            try {
                By moreBtn = By.cssSelector(
                        "a.js-open-more," +
                                "button[class*='open-more']," +
                                "[data-testid='more-menu-button']," +
                                "li.js-open-more button"
                );
                WebElement mb = shortWait.until(
                        ExpectedConditions.elementToBeClickable(moreBtn)
                );
                System.out.println("[DEBUG closeBoard] moreBtn found. Clicking...");
                safeClick(moreBtn);
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            } catch (Exception e) {
                System.out.println("[DEBUG closeBoard] moreBtn not found: " + e.getMessage());
            }
        }

        // Step 4: Click Close Board
        try {
            System.out.println("[DEBUG closeBoard] Clicking closeBoardMenuLink...");
            WebElement closeLink = wait.until(
                    ExpectedConditions.elementToBeClickable(closeBoardMenuLink)
            );
            System.out.println(
                    "[DEBUG closeBoard] closeBoardMenuLink found — text: '" +
                            closeLink.getText() + "'"
            );
            safeClick(closeBoardMenuLink);
            System.out.println("[DEBUG closeBoard] closeBoardMenuLink clicked.");
        } catch (Exception e) {
            System.out.println("[DEBUG closeBoard] closeBoardMenuLink click failed: " + e.getMessage());
            System.out.println("[DEBUG closeBoard] Printing visible menu elements:");
            try {
                for (WebElement el : driver.findElements(By.cssSelector("a, button, li"))) {
                    String txt = el.getText().trim();
                    if (!txt.isEmpty() && (
                            txt.contains("Close") ||
                                    txt.contains("Delete") ||
                                    txt.contains("More"))
                    ) {
                        System.out.println(
                                "  -> tag: " + el.getTagName() + " | text: " + txt
                        );
                    }
                }
            } catch (Exception ignored) {}
            throw e;
        }

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // Step 5: Confirm close
        try {
            System.out.println("[DEBUG closeBoard] Clicking closeConfirmBtn...");
            WebElement confirmBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(closeConfirmBtn)
            );
            System.out.println(
                    "[DEBUG closeBoard] closeConfirmBtn found — text: '" +
                            confirmBtn.getText() + "'"
            );
            safeClick(closeConfirmBtn);
            System.out.println("[DEBUG closeBoard] closeConfirmBtn clicked.");
        } catch (Exception e) {
            System.out.println("[DEBUG closeBoard] closeConfirmBtn click failed: " + e.getMessage());
            throw e;
        }

        // Step 6: Wait for closed message
        System.out.println("[DEBUG closeBoard] Waiting for closedBoardMessage...");
        try {
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(closedBoardMessage)
            );
            System.out.println("[DEBUG closeBoard] Board closed successfully.");
        } catch (Exception e) {
            System.out.println("[DEBUG closeBoard] Timeout waiting for closedBoardMessage: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Returns true if the "This board is closed" screen is currently displayed.
     *
     * @return true if the closed board message is visible, false otherwise.
     */
    public boolean isClosedScreenDisplayed() {
        System.out.println("[DEBUG isClosedScreenDisplayed] Checking for closed screen...");
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement closedMsg = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(closedBoardMessage)
            );
            boolean displayed = closedMsg.isDisplayed();
            System.out.println(
                    "[DEBUG isClosedScreenDisplayed] Closed message found — displayed: " + displayed
            );
            return displayed;
        } catch (Exception e) {
            System.out.println("[DEBUG isClosedScreenDisplayed] Closed screen not visible.");
            return false;
        }
    }

    /**
     * Reopens the currently closed board.
     */
    public void reopenBoard() {
        System.out.println("[DEBUG reopenBoard] Starting reopen board procedure...");

        System.out.println("[DEBUG reopenBoard] Printing all buttons/testid elements in DOM:");
        try {
            for (WebElement el : driver.findElements(
                    By.cssSelector("button, [data-testid]"))
            ) {
                String txt    = el.getText().trim();
                String testid = el.getAttribute("data-testid");
                if (testid != null ||
                        txt.contains("Reopen") ||
                        txt.contains("reopen")) {
                    System.out.println(
                            "  -> tag: " + el.getTagName() +
                                    " | text: '" + txt +
                                    "' | testid: " + testid +
                                    " | displayed: " + el.isDisplayed()
                    );
                }
            }
        } catch (Exception ignored) {}

        try {
            WebElement reopenBtn = wait.until(
                    ExpectedConditions.presenceOfElementLocated(reopenBoardBtn)
            );
            System.out.println(
                    "[DEBUG reopenBoard] reopenBoardBtn found — text: '" +
                            reopenBtn.getText() + "'"
            );
            safeClick(reopenBoardBtn);
            System.out.println("[DEBUG reopenBoard] reopenBoardBtn clicked.");
        } catch (Exception e) {
            System.out.println("[DEBUG reopenBoard] reopenBoardBtn click failed: " + e.getMessage());
            throw e;
        }

        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        System.out.println("[DEBUG reopenBoard] Printing all visible buttons after clicking reopen:");
        try {
            for (WebElement el : driver.findElements(
                    By.cssSelector("button, input[type='button'], input[type='submit']"))
            ) {
                if (el.isDisplayed()) {
                    System.out.println(
                            "  -> tag: " + el.getTagName() +
                                    " | text: '" + el.getText() +
                                    "' | data-testid: " + el.getAttribute("data-testid") +
                                    " | class: " + el.getAttribute("class")
                    );
                }
            }
        } catch (Exception ignored) {}

        // Check for a confirmation button in a popover
        try {
            By reopenConfirmBtn = By.xpath(
                    "//button[@data-testid='workspace-chooser-reopen-button']" +
                            " | //div[contains(@class,'popover')]//button[normalize-space(.)='Reopen']" +
                            " | //input[@value='Reopen']" +
                            " | //button[@data-testid='close-board-reopen-button-confirm']" +
                            " | //button[normalize-space(.)='Reopen']"
            );
            WebElement confirm = driver.findElement(reopenConfirmBtn);
            if (confirm.isDisplayed()) {
                System.out.println(
                        "[DEBUG reopenBoard] Confirm button found — text: '" +
                                confirm.getText() + "'. Clicking..."
                );
                confirm.click();
                System.out.println("[DEBUG reopenBoard] Reopen confirmed.");
            }
        } catch (Exception e) {
            System.out.println(
                    "[DEBUG reopenBoard] No confirmation button detected (likely reopened directly)."
            );
        }

        // Wait for reopen button to disappear — confirms closed overlay is gone
        System.out.println("[DEBUG reopenBoard] Waiting for reopenBoardBtn to disappear...");
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            longWait.until(
                    ExpectedConditions.invisibilityOfElementLocated(reopenBoardBtn)
            );
            System.out.println("[DEBUG reopenBoard] reopenBoardBtn is now invisible.");
        } catch (Exception e) {
            System.out.println(
                    "[DEBUG reopenBoard] Warning: reopenBoardBtn did not become invisible: " +
                            e.getMessage()
            );
        }

        // Wait for board title to confirm active board
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(boardTitleDisplay)
        );

        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        System.out.println("[DEBUG reopenBoard] Board reopened and settled.");
    }

    /**
     * Permanently deletes a closed board.
     */
    public void deleteBoardPermanently() {
        System.out.println("[DEBUG deleteBoardPermanently] Starting permanent delete...");

        System.out.println("[DEBUG deleteBoardPermanently] Printing elements containing 'delete'/'permanently':");
        try {
            for (WebElement el : driver.findElements(
                    By.cssSelector("a, button, p, span, div"))
            ) {
                String txt = el.getText().trim();
                if (txt.toLowerCase().contains("delete") ||
                        txt.toLowerCase().contains("permanently")) {
                    System.out.println(
                            "  -> tag: " + el.getTagName() +
                                    " | text: '" + txt +
                                    "' | testid: " + el.getAttribute("data-testid") +
                                    " | class: " + el.getAttribute("class")
                    );
                }
            }
        } catch (Exception ignored) {}

        try {
            WebElement deleteLink = wait.until(
                    ExpectedConditions.presenceOfElementLocated(permanentDeleteLink)
            );
            System.out.println(
                    "[DEBUG deleteBoardPermanently] permanentDeleteLink found — text: '" +
                            deleteLink.getText() + "'"
            );
            safeClick(permanentDeleteLink);
            System.out.println("[DEBUG deleteBoardPermanently] permanentDeleteLink clicked.");
        } catch (Exception e) {
            System.out.println(
                    "[DEBUG deleteBoardPermanently] permanentDeleteLink click failed: " +
                            e.getMessage()
            );
            throw e;
        }

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        try {
            WebElement confirmBtn = wait.until(
                    ExpectedConditions.presenceOfElementLocated(deleteConfirmBtn)
            );
            System.out.println(
                    "[DEBUG deleteBoardPermanently] boardDeleteConfirmBtn found — text: '" +
                            confirmBtn.getText() + "'"
            );
            safeClick(deleteConfirmBtn);
            System.out.println("[DEBUG deleteBoardPermanently] boardDeleteConfirmBtn clicked.");
        } catch (Exception e) {
            System.out.println(
                    "[DEBUG deleteBoardPermanently] boardDeleteConfirmBtn click failed: " +
                            e.getMessage()
            );
            throw e;
        }
    }


    // ─────────────────────────────────────────────────────────────────────────
    // METHODS — Board Menu (Archive flow)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Opens the board side menu by clicking the menu button in the board header.
     */
    public void openBoardMenu() {
        WebElement menuBtn = wait.until(
                ExpectedConditions.elementToBeClickable(boardMenuButton)
        );
        menuBtn.click();
    }

    /**
     * Closes the Board Menu / Archived Items side panel by clicking
     * the "Close popover" button.
     * Uses invisibility wait after clicking to ensure the panel is
     * fully dismissed before the next action runs — prevents the
     * panel from blocking the "Show menu" button on the next call
     * to openBoardMenu().
     */
    public void closeBoardMenu() {
        try {
            WebElement closeButton = wait.until(
                    ExpectedConditions.elementToBeClickable(closePanelButton)
            );
            closeButton.click();
            System.out.println("[BoardPage] Board menu / panel closed.");

            // Wait for the panel to fully disappear before returning —
            // otherwise the very next openBoardMenu() call can still be
            // intercepted by the partially-visible panel.
            wait.until(
                    ExpectedConditions.invisibilityOfElementLocated(archivedItemsPanel)
            );
            System.out.println("[BoardPage] Panel fully dismissed.");

        } catch (Exception e) {
            // Panel was already closed or never opened — safe to continue
            System.out.println(
                    "[BoardPage] closeBoardMenu: panel already closed or not found — skipping."
            );
        }
    }

    /**
     * Clicks the "Archived items" option from the open board menu.
     */
    public void openArchivedItems() {
        WebElement archivedItemsBtn = wait.until(
                ExpectedConditions.elementToBeClickable(archivedItemsOption)
        );
        archivedItemsBtn.click();
    }

    /**
     * Waits until the Archived Items panel is fully visible.
     */
    public void waitForArchivedItemsPanel() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(archivedItemsPanel)
        );
    }


    // ─────────────────────────────────────────────────────────────────────────
    // METHODS — Delete from Archived Items
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Waits until the specific archived card row appears inside the Archived Items panel.
     *
     * @param cardTitle The exact title of the card to wait for.
     */
    public void waitForArchivedCardToAppear(String cardTitle) {
        System.out.println(
                "  [BoardPage] Waiting for archived card '" + cardTitle + "' to appear in panel..."
        );
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        archivedCardItemLocator(cardTitle)
                )
        );
        System.out.println(
                "  [BoardPage] Archived card '" + cardTitle + "' is visible in panel."
        );
    }

    /**
     * Clicks the Delete button inside the archived card row for the given card title.
     * This triggers the confirmation popup.
     *
     * @param cardTitle The exact title of the card to delete.
     */
    public void clickDeleteButtonForArchivedCard(String cardTitle) {
        System.out.println(
                "  [BoardPage] Clicking Delete button for archived card: " + cardTitle
        );
        WebElement deleteBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        deleteButtonInsideArchivedCard(cardTitle)
                )
        );
        deleteBtn.click();
        System.out.println("  [BoardPage] Delete button clicked.");
    }

    /**
     * Clicks the confirm Delete button inside the confirmation popup
     * to permanently delete the archived card.
     */
    public void confirmCardDeletion(String cardTitle) {
        System.out.println("  [BoardPage] Waiting for delete confirmation button...");
        WebElement confirmBtn = wait.until(
                ExpectedConditions.elementToBeClickable(archivedCardDeleteConfirmBtn(cardTitle))
        );
        confirmBtn.click();
        System.out.println("  [BoardPage] Delete confirmed.");
    }

    /**
     * Returns true if the deleted card is NO LONGER listed in the Archived Items panel.
     *
     * @param cardTitle The exact title of the card to verify is gone.
     * @return true if card is absent from Archived Items; false if still present.
     */
    public boolean isCardDeletedFromArchivedItems(String cardTitle) {
        System.out.println(
                "  [BoardPage] Verifying card '" + cardTitle +
                        "' is deleted from Archived Items..."
        );
        List<WebElement> items = driver.findElements(archivedCardByTitle(cardTitle));

        // Case 1: Card is completely gone from the DOM
        if (items.isEmpty()) {
            System.out.println("  [BoardPage] Card not found in DOM — confirmed deleted.");
            return true;
        }

        // Case 2: Card element exists in DOM but is not displayed
        boolean notDisplayed = !items.get(0).isDisplayed();
        System.out.println(
                "  [BoardPage] Card in DOM — displayed: " + items.get(0).isDisplayed()
        );
        return notDisplayed;
    }

    // ─────────────────────────────────────────────
    // DRAG AND DROP
    // ─────────────────────────────────────────────

    /**
     * Drags a list header to reorder it on the board.
     *
     * @param sourceListName name of the list to drag
     * @param targetListName name of the list to drop onto
     */
    /**
     * Drags a list (by its header) to a new position relative to another list.
     * Optimized with CSS selectors + Java filtering for speed.
     *
     * @param sourceListName name of the list to drag
     * @param targetListName name of the list to drop near
     */
    public void dragListToPosition(String sourceListName, String targetListName) {
        WebElement source = findListHeader(sourceListName);
        WebElement target = findListHeader(targetListName);

        if (source == null) {
            throw new RuntimeException("Source list '" + sourceListName + "' not found");
        }
        if (target == null) {
            throw new RuntimeException("Target list '" + targetListName + "' not found");
        }

        // Scroll source into center to ensure it's fully in viewport
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'instant', block: 'center', inline: 'center'});",
                source
        );
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Re-locate after scroll
        source = findListHeader(sourceListName);
        target = findListHeader(targetListName);

        // Check if target is within reasonable distance (avoid out-of-bounds)
        int sourceX = source.getLocation().getX();
        int targetX = target.getLocation().getX();
        int distance = Math.abs(targetX - sourceX);

        // If lists are far apart (>800px), scroll target partially into view
        if (distance > 800) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'instant', block: 'center', inline: 'start'});",
                    target
            );
            try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            // Re-locate both after scroll
            source = findListHeader(sourceListName);
            target = findListHeader(targetListName);
        }

        Duration pause = Duration.ofMillis(1500);

        try {
            new org.openqa.selenium.interactions.Actions(driver)
                    .moveToElement(source).pause(pause)
                    .clickAndHold(source).pause(pause)
                    .moveByOffset(5, 5).pause(pause)
                    .moveToElement(target).pause(pause)
                    .release().pause(pause)
                    .build().perform();
        } catch (org.openqa.selenium.interactions.MoveTargetOutOfBoundsException e) {
            // If still out of bounds, try offset-based drag instead
            System.out.println("WARNING: List drag out of bounds, attempting offset-based drag");
            int offsetX = Math.min(Math.max(targetX - sourceX, -500), 500);  // Clamp to ±500px
            new org.openqa.selenium.interactions.Actions(driver)
                    .moveToElement(source).pause(pause)
                    .clickAndHold(source).pause(pause)
                    .moveByOffset(offsetX, 0).pause(pause)
                    .release().pause(pause)
                    .build().perform();
        } catch (Exception e) {
            System.out.println("WARNING: List drag failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Find a list header element by name using CSS selectors + Java filtering.
     * Much faster than XPath text matching.
     */
    private WebElement findListHeader(String listName) {
        java.util.List<WebElement> headers = driver.findElements(By.cssSelector("[data-testid='list-name']"));

        for (WebElement header : headers) {
            try {
                if (header.getText().contains(listName)) {
                    return header;
                }
            } catch (Exception ignored) {
                // Element stale or not visible, skip
            }
        }
        return null;
    }

    /**
     * Returns the names of all lists on the board in their current order.
     */
    public java.util.List<String> getListOrder() {
        return driver.findElements(
                        By.xpath("//li[@data-testid='list-wrapper']//h2[@data-testid='list-name']//span"))
                .stream()
                .map(WebElement::getText)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Waits until the given list name has moved to a different index than the one provided.
     *
     * @param listName      list whose position is expected to change
     * @param previousIndex the index it held before the drag
     */
    public void waitForListReorder(String listName, int previousIndex) {
        wait.until(d -> {
            java.util.List<String> names = d.findElements(
                            By.xpath("//li[@data-testid='list-wrapper']//h2[@data-testid='list-name']//span"))
                    .stream().map(WebElement::getText).collect(java.util.stream.Collectors.toList());
            int idx = names.indexOf(listName);
            return idx >= 0 && idx != previousIndex;
        });
    }


    // ─────────────────────────────────────────────────────────────────────────
    // FIXTURE HELPERS — Ensure board/list/card prerequisites exist
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Ensures a board with the given name exists, creating it if necessary.
     * Returns true if the board was created, false if it already existed.
     *
     * @param boardName Name of the board to ensure exists
     * @return true if board was created, false if already existed
     */
    public boolean ensureBoardExists(String boardName) {
        // Assume we're on the dashboard - check if board tile is present
        try {
            By boardTile = By.xpath(
                    "//a[@title='" + boardName + "' and @aria-label='" + boardName + "']"
            );
            List<WebElement> boards = driver.findElements(boardTile);
            if (!boards.isEmpty() && boards.get(0).isDisplayed()) {
                System.out.println("[BoardPage] Board '" + boardName + "' already exists.");
                return false;
            }
        } catch (Exception e) {
            // Board not found, will create below
        }

        // Create the board
        System.out.println("[BoardPage] Creating board: " + boardName);
        createNewBoard(boardName);
        System.out.println("[BoardPage] Board '" + boardName + "' created successfully.");
        return true;
    }

    /**
     * Opens a board by name if it exists.
     * Should be called when you're on the dashboard.
     *
     * @param boardName Name of the board to open
     */
    public void openBoardByName(String boardName) {
        By boardTile = By.xpath(
                "//a[@title='" + boardName + "' and @aria-label='" + boardName + "']"
        );
        WebElement board = wait.until(
                ExpectedConditions.elementToBeClickable(boardTile)
        );
        board.click();

        // Wait for board to load
        wait.until(ExpectedConditions.urlContains("/b/"));
    }
}
