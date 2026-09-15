package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BoardPage;
import pages.ListPage;
import utils.BaseTest;


public class ListTests extends BaseTest {

    private static String boardUrl;

    private BoardPage boardPage;
    private ListPage listPage;

    @BeforeMethod
    public void initPages() {
        // Runs after the parent BaseTest's @BeforeMethod (driver is already created).
        performLogin();
        boardPage = new BoardPage(driver);
        listPage = new ListPage(driver);

        if (boardUrl == null) {
            // First test in this class - create the shared board once.
            boardPage.createNewBoard("List-Test-Board-" + System.currentTimeMillis());
            boardUrl = driver.getCurrentUrl();
        } else {
            // Subsequent tests - just navigate straight to the already-created board.
            driver.get(boardUrl);
        }
    }

    @Test
    public void TC01_createList() {
        String listName = "Test-List-" + System.currentTimeMillis();

        listPage.createList(listName);

        Assert.assertTrue(
                listPage.isListCreated(listName),
                "The new list was not displayed on the board"
        );
    }

    @Test
    public void TC02_renameList() {
        String oldName = "Test-List-" + System.currentTimeMillis();
        String newName = "Renamed-List-" + System.currentTimeMillis();

        listPage.createList(oldName);
        listPage.renameList(oldName, newName);

        Assert.assertTrue(
                listPage.isListCreated(newName),
                "The list was not renamed successfully"
        );
    }

    @Test
    public void TC03_archiveList() {
        String listName = "Archive-List-" + System.currentTimeMillis();

        listPage.createList(listName);
        listPage.archiveList(listName);

        Assert.assertFalse(
                listPage.isListCreated(listName),
                "The list was not archived"
        );
    }


    @Test
    public void TC04_emptyTitleValidation() {
        // Wait for the board's lists to actually finish rendering before counting -
        // otherwise countBefore can be measured before the DOM paints (React SPA).
        listPage.waitForBoardToLoad();

        int countBefore = listPage.getListCount();

        listPage.attemptEmptyListSubmit();

        int countAfter = listPage.getListCount();

        Assert.assertEquals(
                countAfter, countBefore,
                "A new list should NOT be created when the list name is left empty"
        );
    }


    @Test
    public void TC05_copyList() {
        String listName = "Copy-Source-" + System.currentTimeMillis();
        listPage.createList(listName);

        int countBefore = listPage.getListCount();

        listPage.copyList(listName);

        int countAfter = listPage.getListCount();

        Assert.assertEquals(
                countAfter, countBefore + 1,
                "Copying a list should increase the total list count by exactly one"
        );
    }
}