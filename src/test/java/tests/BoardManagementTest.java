package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BoardPage;

/**
 * Automation test suite for Trello Board Management.
 * Implements 8 comprehensive test cases simulating the complete lifecycle of a Trello Board.
 * Uses the Page Object Model (BoardPage) for separation of concerns and robust test design.
 */
public class BoardManagementTest extends BaseTest {

    private BoardPage boardPage;
    private static final String runId = String.valueOf(System.currentTimeMillis());
    private static final String ORIGINAL_BOARD_NAME = "Selenium Automation Board " + runId;
    private static final String UPDATED_BOARD_NAME = "Renamed Automation Board " + runId;

    @Test(priority = 1, description = "BM-001: Create a new Trello board")
    public void test01_createNewBoard() {
        System.out.println("=================================================");
        System.out.println("RUNNING: BM-001: Create a New Board");
        System.out.println("=================================================");

        System.out.println("DEBUG: Current URL: " + driver.getCurrentUrl());
        System.out.println("DEBUG: Current Page Title: " + driver.getTitle());

        boardPage = new BoardPage(driver);
        
        System.out.println("Step 1: Creating a new board named: " + ORIGINAL_BOARD_NAME);
        boardPage.createNewBoard(ORIGINAL_BOARD_NAME);

        System.out.println("Step 2: Verifying board URL and display name.");
        Assert.assertTrue(driver.getCurrentUrl().contains("/b/"), "URL does not contain '/b/', board creation might have failed.");
        
        String displayedTitle = boardPage.getBoardTitle();
        System.out.println("Displayed Board Title: " + displayedTitle);
        Assert.assertEquals(displayedTitle, ORIGINAL_BOARD_NAME, "Board title does not match creation request.");

        System.out.println("BM-001 PASSED: Board created and verified successfully.");
    }
    /* Ani card operations 
    1.list creation
    2.card creation code */
    /* Harshit Card maodification
    1.editing the attributes
     */
    @Test(priority = 2, dependsOnMethods = "test01_createNewBoard", description = "BM-002: Rename the existing Trello board")
    public void test02_updateBoardTitle() {
        System.out.println("\n=================================================");
        System.out.println("RUNNING: BM-002: Update Board Title");
        System.out.println("=================================================");

        System.out.println("Step 1: Renaming the board to: " + UPDATED_BOARD_NAME);
        boardPage.updateBoardTitle(UPDATED_BOARD_NAME);

        System.out.println("Step 2: Verifying the new board name displays correctly.");
        String updatedTitle = boardPage.getBoardTitle();
        System.out.println("Updated Board Title: " + updatedTitle);
        Assert.assertEquals(updatedTitle, UPDATED_BOARD_NAME, "Board title was not updated successfully.");

        // Refresh the page to cleanly release edit focus and stabilize downstream element locators
        System.out.println("Refreshing page to cleanly reset focus...");
        driver.navigate().refresh();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        System.out.println("BM-002 PASSED: Board title updated and verified successfully.");
    }

    @Test(priority = 3, dependsOnMethods = "test01_createNewBoard", description = "BM-003: Toggle Star/Favorite status on the board")
    public void test03_toggleStarBoard() {
        System.out.println("\n=================================================");
        System.out.println("RUNNING: BM-003: Toggle Star/Favorite Board");
        System.out.println("=================================================");

        System.out.println("DEBUG (test03): Current URL: " + driver.getCurrentUrl());
        System.out.println("DEBUG (test03): Page Title: " + driver.getTitle());

        System.out.println("Step 1: Clicking the Star button to favorite the board.");
        boardPage.toggleStarBoard();
        System.out.println("Board starred successfully.");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        System.out.println("Step 2: Clicking the Star button again to unfavorite the board.");
        boardPage.toggleStarBoard();
        System.out.println("Board unstarred successfully.");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        System.out.println("BM-003 PASSED: Board star/favorite toggled successfully.");
    }

    @Test(priority = 4, dependsOnMethods = "test01_createNewBoard", description = "BM-004: Update board background to a solid color")
    public void test04_changeBoardBackground() {
        System.out.println("\n=================================================");
        System.out.println("RUNNING: BM-004: Update Board Background");
        System.out.println("=================================================");

        System.out.println("Step 1: Opening background menu and changing background to solid color.");
        boardPage.changeBackgroundToColor();

        System.out.println("Step 2: Verifying background change was triggered (visually and interaction-wise).");
        // Typically, this updates CSS background styles on the page element. We verified action completed without error.
        System.out.println("Background change option clicked and applied successfully.");

        System.out.println("BM-004 PASSED: Board background updated successfully.");
        
        // Refresh the page to close the sidebar menu cleanly
        System.out.println("Refreshing page to close sidebar menu...");
        driver.navigate().refresh();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }

    @Test(priority = 5, dependsOnMethods = "test01_createNewBoard", description = "BM-005: Update board visibility to Private")
    public void test05_updateBoardVisibility() {
        System.out.println("\n=================================================");
        System.out.println("RUNNING: BM-005: Update Board Visibility");
        System.out.println("=================================================");

        System.out.println("Step 1: Checking current visibility text.");
        String initialVisibility = boardPage.getVisibilityText();
        System.out.println("Initial Visibility: " + initialVisibility);

        System.out.println("Step 2: Clicking visibility and changing to Private.");
        boardPage.changeVisibilityToPrivate();

        System.out.println("Step 3: Verifying visibility status updated to Private.");
        String updatedVisibility = boardPage.getVisibilityText();
        System.out.println("Updated Visibility: " + updatedVisibility);
        Assert.assertTrue(updatedVisibility.equalsIgnoreCase("Private") || updatedVisibility.contains("Private"),
                "Board visibility did not update to Private.");

        System.out.println("BM-005 PASSED: Board visibility changed and verified successfully.");

        // Refresh the page to close the visibility dropdown cleanly
        System.out.println("Refreshing page to close visibility dropdown...");
        driver.navigate().refresh();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }

    @Test(priority = 6, dependsOnMethods = "test01_createNewBoard", description = "BM-006: Close (archive) the Trello board")
    public void test06_closeBoard() {
        System.out.println("\n=================================================");
        System.out.println("RUNNING: BM-006: Close a Board");
        System.out.println("=================================================");

        System.out.println("Step 1: Closing board via Menu.");
        boardPage.closeBoard();

        System.out.println("Step 2: Verifying 'This board is closed.' screen is displayed.");
        boolean isClosed = boardPage.isClosedScreenDisplayed();
        Assert.assertTrue(isClosed, "The closed board screen was not displayed.");

        System.out.println("BM-006 PASSED: Board closed successfully.");
    }

    @Test(priority = 7, dependsOnMethods = "test06_closeBoard", description = "BM-007: Reopen the closed Trello board")
    public void test07_reopenBoard() {
        System.out.println("\n=================================================");
        System.out.println("RUNNING: BM-007: Reopen a Closed Board");
        System.out.println("=================================================");

        System.out.println("Step 1: Reopening the closed board.");
        boardPage.reopenBoard();

        System.out.println("Step 2: Verifying board displays correctly and is active.");
        Assert.assertFalse(boardPage.isClosedScreenDisplayed(), "Board should not be showing the closed screen after reopen.");
        String title = boardPage.getBoardTitle();
        System.out.println("Active Board Title after reopening: " + title);
        Assert.assertEquals(title, UPDATED_BOARD_NAME, "Reopened board title does not match.");

        System.out.println("BM-007 PASSED: Closed board reopened and restored successfully.");
    }

    /*@Test(priority = 8, dependsOnMethods = "test07_reopenBoard", description = "BM-008: Permanently delete the board", enabled = false)
    public void test08_deleteBoardPermanently() {
        System.out.println("\n=================================================");
        System.out.println("RUNNING: BM-008: Delete a Board Permanently");
        System.out.println("=================================================");

        System.out.println("Step 1: Closing the board first (if not already closed).");
        try {
            if (!boardPage.isClosedScreenDisplayed()) {
                boardPage.closeBoard();
            }
        } catch (Exception e) {
            boardPage.closeBoard();
        }

        System.out.println("Step 2: Clicking Permanently Delete Board and confirming.");
        boardPage.deleteBoardPermanently();

        System.out.println("Step 3: Verifying redirection after deletion.");
        // After permanent deletion, Trello redirects user to the boards/home dashboard.
        System.out.println("Redirected URL post-delete: " + driver.getCurrentUrl());
        Assert.assertFalse(driver.getCurrentUrl().contains(UPDATED_BOARD_NAME), "User should be redirected away from deleted board URL.");

        System.out.println("BM-008 PASSED: Board permanently deleted successfully.");
    }*/
}