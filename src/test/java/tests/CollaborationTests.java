package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BoardPage;
import pages.CardPage;
import pages.DashboardPage;
import utils.BaseTest;

/**
 * Test class for Trello Collaboration Features.
 * Multi-user product features: inviting members to a board, roles
 * (admin/observer), assigning members to cards, @mentions in comments,
 * posting comments, watching cards, and activity logs.
 *
 * Tests marked "Uses 2nd driver" call performSecondLogin(), which skips the
 * test (via SkipException) instead of failing it when TRELLO_EMAIL_SECOND /
 * TRELLO_PASSWORD_SECOND are not configured - see README for setup.
 */
public class CollaborationTests extends BaseTest {

    private String secondAccountEmail() {
        return config.getProperty("trello.email.second");
    }

    /**
     * Display name shown for the second account in board member lists, card avatars,
     * and mention popovers. Trello usernames aren't derived from email local-parts, so
     * this (not the email) is what all member-matching lookups must search for.
     */
    private String secondAccountName() {
        return "Rashmi";
    }

    /**
     * Test Case: Invite Member to Board
     * Verifies that inviting the second account adds them to the board, and
     * the board becomes visible to that account.
     */
    @Test(priority = 1, description = "Verify inviting a member adds them to the board")
    public void testInviteMemberToBoard() {
        DashboardPage dashboard = performLogin();
        performSecondLogin();

        String boardName = "Collab-Invite";
        dashboard.openBoard(boardName);

        BoardPage board = new BoardPage(driver);
        board.openShareDialog();
        board.inviteMemberByEmail(secondAccountEmail());

        Assert.assertTrue(board.isMemberOnBoard(secondAccountName()),
                "Invited member should appear in the board's member list");

        secondDriver.get(config.getProperty("trello.url"));
        Assert.assertTrue(secondDashboardPage.isBoardVisible(boardName),
                "Invited member should see the shared board on their dashboard");
    }

    /**
     * Test Case: Admin Role
     * Verifies that a member set as Admin has full board access (can open Share).
     */
    @Test(priority = 2, description = "Verify Admin role grants full board access")
    public void testAdminRoleGrantsFullAccess() {
        DashboardPage dashboard = performLogin();
        performSecondLogin();

        String boardName = "Collab-Admin";
        dashboard.openBoard(boardName);

        String secondNameAdmin = secondAccountName();

        BoardPage board = new BoardPage(driver);
        board.openShareDialog();
        board.inviteMemberByEmail(secondAccountEmail());
        board.setMemberRole(secondNameAdmin, "Admin");

        Assert.assertEquals(board.getMemberRole(secondNameAdmin), "Admin",
                "Member's role should be updated to Admin");

        secondDriver.get(config.getProperty("trello.url"));
        Assert.assertTrue(secondDashboardPage.isBoardVisible(boardName),
                "Invited member should see the shared board before checking Admin access");
        secondDashboardPage.openBoard(boardName);

        BoardPage secondUserBoard = new BoardPage(secondDriver);
        Assert.assertTrue(secondUserBoard.isShareButtonEnabled(),
                "Admin should be able to access the Share dialog");
    }

    /**
     * Test Case: Observer Role
     * Verifies that a member set as Observer cannot add cards.
     */
    @Test(priority = 3, description = "Verify Observer role restricts editing actions")
    public void testObserverRoleRestrictsEditing() {
        DashboardPage dashboard = performLogin();
        performSecondLogin();

        String boardName = "Collab-Observer";
        dashboard.openBoard(boardName);

        String secondNameObserver = secondAccountName();

        BoardPage board = new BoardPage(driver);
        board.openShareDialog();
        board.inviteMemberByEmail(secondAccountEmail());
        board.setMemberRole(secondNameObserver, "Observer");

        Assert.assertEquals(board.getMemberRole(secondNameObserver), "Observer",
                "Member's role should be updated to Observer");

        secondDriver.get(config.getProperty("trello.url"));
        Assert.assertTrue(secondDashboardPage.isBoardVisible(boardName),
                "Invited member should see the shared board before checking Observer restrictions");
        secondDashboardPage.openBoard(boardName);

        BoardPage secondUserBoard = new BoardPage(secondDriver);
        Assert.assertFalse(secondUserBoard.isAddCardAvailable(),
                "Observer should not be able to add cards");
    }

    /**
     * Test Case: Assign Member to Card
     * Verifies that assigning a member to a card shows their avatar to both users.
     */
    @Test(priority = 4, description = "Verify assigning a member to a card shows their avatar to both users")
    public void testAssignMemberToCard() {
        DashboardPage dashboard = performLogin();
        performSecondLogin();

        String boardName = "Collab-Assign";
        dashboard.openBoard(boardName);

        BoardPage board = new BoardPage(driver);
        board.openShareDialog();
        board.inviteMemberByEmail(secondAccountEmail());
        board.closeDialog();

        String secondName = secondAccountName();

        board.ensureCardExists("To Do", "Card to assign");
        board.openCard("Card to assign");
        CardPage card = new CardPage(driver);
        card.addMember(secondName);

        Assert.assertTrue(card.isMemberAssigned(secondName),
                "Assigned member should be visible to the assigning user");

        secondDriver.get(config.getProperty("trello.url"));
        Assert.assertTrue(secondDashboardPage.isBoardVisible(boardName),
                "Invited member should see the shared board before checking the assigned card");
        secondDashboardPage.openBoard(boardName);

        BoardPage secondUserBoard = new BoardPage(secondDriver);
        secondUserBoard.openCard("Card to assign");
        CardPage secondUserCard = new CardPage(secondDriver);

        Assert.assertTrue(secondUserCard.isMemberAssigned(secondName),
                "Assigned member should be visible to the invited user too");
    }

    /**
     * Test Case: @Mention in Comment
     * Verifies that mentioning a member in a comment notifies them.
     */
    @Test(priority = 5, description = "Verify mentioning a member in a comment notifies them")
    public void testMentionInCommentNotifiesMember() {
        DashboardPage dashboard = performLogin();
        performSecondLogin();

        String secondEmail = secondAccountEmail();
        String boardName = "Collab-Mention";
        dashboard.openBoard(boardName);

        BoardPage board = new BoardPage(driver);
        board.openShareDialog();
        board.inviteMemberByEmail(secondEmail);
        board.closeDialog();

        board.ensureCardExists("To Do", "Card with mention");
        board.openCard("Card with mention");
        CardPage card = new CardPage(driver);
        card.postCommentMentioning(secondAccountName(), "please review");

        Assert.assertTrue(card.isCommentPresent("please review"),
                "Comment with mention should be posted on the card");

        Assert.assertTrue(waitUntil(d -> secondDashboardPage.hasUnreadNotifications()),
                "Mentioned member should receive an unread notification");
    }

    /**
     * Test Case: Post Comment
     * Verifies that a posted comment appears in the card's comment list.
     */
    @Test(priority = 6, description = "Verify a posted comment appears on the card")
    public void testPostComment() {
        DashboardPage dashboard = performLogin();

        String boardName = "Collab-Comment";
        dashboard.openBoard(boardName);

        BoardPage board = new BoardPage(driver);
        board.ensureCardExists("To Do", "Card for comment");
        board.openCard("Card for comment");

        CardPage card = new CardPage(driver);
        card.postComment("This is a test comment");

        Assert.assertTrue(card.isCommentPresent("This is a test comment"),
                "Posted comment should appear in the card's comment list");
    }

    /**
     * Test Case: Watch Card
     * Verifies that a watcher is notified when the card they watch is updated.
     */
    @Test(priority = 7, description = "Verify watching a card notifies the watcher on updates")
    public void testWatchCardNotifiesOnUpdate() {
        DashboardPage dashboard = performLogin();
        performSecondLogin();

        String secondEmail = secondAccountEmail();
        String boardName = "Collab-Watch";
        dashboard.openBoard(boardName);

        BoardPage board = new BoardPage(driver);
        board.openShareDialog();
        board.inviteMemberByEmail(secondEmail);
        board.closeDialog();
        board.ensureCardExists("To Do", "Card to watch");

        secondDriver.get(config.getProperty("trello.url"));
        Assert.assertTrue(secondDashboardPage.isBoardVisible(boardName),
                "Invited member should see the shared board before watching a card on it");
        secondDashboardPage.openBoard(boardName);

        BoardPage secondUserBoard = new BoardPage(secondDriver);
        secondUserBoard.openCard("Card to watch");
        CardPage secondUserCard = new CardPage(secondDriver);
        secondUserCard.ensureWatched();

        Assert.assertTrue(secondUserCard.isWatched(), "Card should be marked as watched");

        board.openCard("Card to watch");
        CardPage card = new CardPage(driver);
        card.postComment("Update for watchers");

        Assert.assertTrue(waitUntil(d -> secondDashboardPage.hasUnreadNotifications()),
                "Watcher should receive an unread notification after the card updates");
    }

    /**
     * Test Case: Activity Log
     * Verifies that card actions (e.g. posting a comment) are recorded in the activity log.
     */
    @Test(priority = 8, description = "Verify card actions are recorded in the activity log")
    public void testActivityLogRecordsCardActions() {
        DashboardPage dashboard = performLogin();

        String boardName = "Collab-Activity";
        dashboard.openBoard(boardName);

        BoardPage board = new BoardPage(driver);
        board.ensureCardExists("To Do", "Card for activity log");
        board.openCard("Card for activity log");

        CardPage card = new CardPage(driver);
        card.postComment("Logged comment");

        Assert.assertTrue(card.isActivityEntryPresent("Logged comment"),
                "Posting a comment should be recorded in the card's activity log");
    }
}
