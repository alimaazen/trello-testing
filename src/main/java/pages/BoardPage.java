package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
        WebElement openComposer = wait.until(ExpectedConditions.elementToBeClickable(listComposerOpenButtonLocator));
        openComposer.click();

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(addListButtonLocator));
        input.sendKeys(listName);

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(listComposerAddButtonLocator));
        addButton.click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(listHeaderLocator, listName));
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
        List<WebElement> lists = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(listLocator));
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
     */
    public void openShareDialog() {
        WebElement shareButton = wait.until(ExpectedConditions.elementToBeClickable(shareButtonLocator));
        shareButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(shareSearchInputLocator));
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

        try {
            WebElement suggestion = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(typeaheadSuggestionLocator));
            suggestion.click();
        } catch (Exception e) {
            // No typeahead suggestion - fall through and let the Share button send an
            // external invite for the typed email.
        }

        WebElement sendInviteButton =
                wait.until(ExpectedConditions.elementToBeClickable(sendInviteButtonLocator));
        sendInviteButton.click();
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
            List<WebElement> members = driver.findElements(memberItemLocator);
            for (WebElement member : members) {
                if (member.getText().contains(emailOrName)) {
                    return true;
                }
            }
            return false;
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
            if (member.getText().contains(emailOrName)) {
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
}
