package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object Model for the open Trello card split-screen detail panel.
 * Covers assigning members, posting comments (plain and @mention), watching,
 * and reading the card's activity feed.
 */
public class CardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private By cardBackPanelLocator = By.cssSelector("[data-testid='card-back-panel']");

    private By addToCardButtonLocator = By.cssSelector("button[aria-label='Add to card']");
    private By addMembersMenuItemLocator = By.cssSelector("button[data-testid='card-back-members-button']");
    private By memberSearchInputLocator = By.cssSelector("input[aria-label='Search members']");
    private By memberSearchResultLocator =
            By.cssSelector("button[data-testid='choose-member-item-add-member-button']");
    private By assignedMemberAvatarLocator = By.cssSelector("button[data-testid='card-back-member-avatar']");

    private By newCommentSkeletonButtonLocator =
            By.cssSelector("button[data-testid='card-back-new-comment-input-skeleton']");
    private By commentEditorLocator = By.cssSelector("[data-testid='editor-content-container'] .ProseMirror");
    private By commentSaveButtonLocator = By.cssSelector("button[data-testid='card-back-comment-save-button']");

    private By actionsButtonLocator = By.cssSelector("button[data-testid='card-back-actions-button']");
    private By subscribedButtonLocator = By.cssSelector("button[data-testid='card-back-subscribed-button']");

    private By activityFeedItemLocator = By.cssSelector("[data-testid='card-back-action']");

    public CardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Check whether the card detail panel is currently open.
     *
     * @return true if the card back panel is visible
     */
    public boolean isCardOpen() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(cardBackPanelLocator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Assign a member to the currently open card via the "Add to card" > Members popover.
     * The search box filters candidates server-side by email/username/display name, so the
     * first result after searching is taken as the match.
     *
     * @param emailOrUsernameFragment Email (or email local-part/username) fragment to search for
     */
    public void addMember(String emailOrUsernameFragment) {
        WebElement addToCardButton = wait.until(ExpectedConditions.elementToBeClickable(addToCardButtonLocator));
        addToCardButton.click();

        WebElement membersItem = wait.until(ExpectedConditions.elementToBeClickable(addMembersMenuItemLocator));
        membersItem.click();

        if (isMemberAssigned(emailOrUsernameFragment)) {
            // Already assigned (e.g. from a prior run) - clicking the search result again
            // would toggle them off since the member picker is a toggle, not an add-only
            // control. Nothing more to do.
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            return;
        }

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(memberSearchInputLocator));
        searchInput.sendKeys(emailOrUsernameFragment);

        WebElement memberButton = wait.until(ExpectedConditions.elementToBeClickable(memberSearchResultLocator));
        memberButton.click();

        new Actions(driver).sendKeys(Keys.ESCAPE).perform();
        // The avatar can take longer than the default wait to render after the toggle
        // click, especially under repeated test load - give it extra room here rather
        // than failing the whole flow over a slow render.
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> isMemberAssigned(emailOrUsernameFragment));
    }

    /**
     * Check whether a member is assigned to the currently open card.
     *
     * @param nameOrUsernameFragment Fragment of the member's name or username to look for
     * @return true if a matching assigned member avatar is found
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
}
