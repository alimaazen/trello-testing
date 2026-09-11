package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.BoardPage;
import pages.ListPage;
import pages.CardPage;
import utils.BaseTest;

import java.time.Duration;


public class CardTests extends BaseTest {

    private BoardPage boardPage;
    private ListPage listPage;
    private CardPage cardPage;
    private WebDriverWait wait;

    // ─────────────────────────────────────────────────────
    // CONFIG: Update these to match your existing Trello data
    // ─────────────────────────────────────────────────────
    private static final String EXISTING_BOARD_NAME = "My Trello Board";
    private static final String EXISTING_LIST_NAME  = "Today";

    // ─────────────────────────────────────────────────────
    // LOCATORS
    // ─────────────────────────────────────────────────────
    private By boardTileLocator(String boardName) {
        return By.xpath(
                "//a[@title='" + boardName + "' and @aria-label='" + boardName + "']"
        );
    }

    private By listAddCardButtonLocator(String listName) {
        return By.xpath(
                "//button[@data-testid='list-add-card-button' " +
                        "and contains(@aria-label, 'Add a card in " + listName + "')]"
        );
    }

    private By listHeaderLocator(String listName) {
        return By.xpath(
                "//span[normalize-space(text())='" + listName + "']"
        );
    }


    // ─────────────────────────────────────────────────────
    // SETUP: Login → Navigate to Board → Navigate to List
    // Runs ONCE before all tests in this class
    // ─────────────────────────────────────────────────────
    @BeforeClass
    public void setup() {
        System.out.println("=================================================");
        System.out.println("SETUP: Logging in to Trello...");
        System.out.println("=================================================");

        // ── Step 1: Login ─────────────────────────────────────────────────
        performLogin();
        System.out.println("SETUP: Login successful!");

        // ── Step 2: Initialize wait and page objects ──────────────────────
        wait      = new WebDriverWait(driver, Duration.ofSeconds(20));
        boardPage = new BoardPage(driver);
        listPage  = new ListPage(driver);
        cardPage  = new CardPage(driver);

        // ── Step 3: Wait until board tile is VISIBLE before clicking ──────
        System.out.println("SETUP: Waiting for board '" + EXISTING_BOARD_NAME + "' to appear...");
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        boardTileLocator(EXISTING_BOARD_NAME)
                )
        );
        System.out.println("SETUP: Board tile is visible!");

        // ── Step 4: Wait until board tile is CLICKABLE then open it ───────
        System.out.println("SETUP: Navigating to existing board: " + EXISTING_BOARD_NAME);
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        boardTileLocator(EXISTING_BOARD_NAME)
                )
        );
        boardPage.openExistingBoard(EXISTING_BOARD_NAME);
        System.out.println("SETUP: Board clicked!");

        // ── Step 5: Wait until URL confirms we are on the board page ──────
        System.out.println("SETUP: Waiting for board URL to load...");
        wait.until(ExpectedConditions.urlContains("/b/"));

        // ── Step 6: Verify we landed on the correct board ─────────────────
        Assert.assertTrue(
                driver.getCurrentUrl().contains("/b/"),
                "SETUP FAILED: URL does not contain '/b/' — board navigation may have failed."
        );
        System.out.println("SETUP: Successfully opened board. URL: " + driver.getCurrentUrl());

        // ── Step 7: Wait until the list HEADER is visible ─────────────────
        System.out.println("SETUP: Waiting for list header '" + EXISTING_LIST_NAME + "' to appear...");
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        listHeaderLocator(EXISTING_LIST_NAME)
                )
        );
        System.out.println("SETUP: List header '" + EXISTING_LIST_NAME + "' is visible!");

        // ── Step 8: Wait until the "Add a card" button of the list is visible
        System.out.println("SETUP: Waiting for 'Add a card' button of list '" + EXISTING_LIST_NAME + "' to appear...");

        System.out.println("SETUP: List '" + EXISTING_LIST_NAME + "' is now visible on the board!");

        // ── Step 9: Verify the list exists on the board ───────────────────
        System.out.println("SETUP: Verifying list: " + EXISTING_LIST_NAME);
        listPage.openExistingList(EXISTING_LIST_NAME);
        System.out.println("SETUP: List '" + EXISTING_LIST_NAME + "' located. Ready for card tests.");

        System.out.println("=================================================");
        System.out.println("SETUP COMPLETE: All tests will now run.");
        System.out.println("=================================================");
    }


    // ─────────────────────────────────────────────────────
    // BM-003: Create a Single Card
    // ─────────────────────────────────────────────────────
    @Test(priority = 1, description = "BM-003: Create a new card inside the existing list")
    public void test01_createCard() {
        String cardTitle = "My Automated Card";

        System.out.println("=================================================");
        System.out.println("RUNNING: BM-003: Create a Single Card");
        System.out.println("=================================================");

        // ── Step 1: Wait for "Add a card" button then click ───────────────
//        System.out.println("STEP 1: Waiting for 'Add a card' button for list: " + EXISTING_LIST_NAME);
//        wait.until(
//                ExpectedConditions.elementToBeClickable(
//                        listAddCardButtonLocator(EXISTING_LIST_NAME)
//                )
//        );
//        System.out.println("STEP 1: Clicking 'Add a card' button...");
//        cardPage.clickAddCardButton(EXISTING_LIST_NAME);

        // ── Step 2: Wait for title input then enter title ─────────────────
        System.out.println("STEP 2: Waiting for card title input to appear...");
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@data-testid='list-card-composer-textarea']")
                ));
        System.out.println("STEP 2: Entering card title: " + cardTitle);
        cardPage.enterCardTitle(cardTitle);

        // ── Step 3: Wait for submit button then click ─────────────────────
        System.out.println("STEP 3: Waiting for submit button to be clickable...");
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@data-testid='list-card-composer-add-card-button']")
                )
        );
        System.out.println("STEP 3: Submitting card...");
        cardPage.clickAddCardSubmit();

        // ── Step 4: Wait for card to appear then verify ───────────────────
        System.out.println("STEP 4: Waiting for card '" + cardTitle + "' to appear on board...");
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[@data-testid='card-name' and text()='" + cardTitle + "']")
                )
        );
        System.out.println("STEP 4: Verifying card creation...");
        boolean isCreated = cardPage.isCardCreated(cardTitle);
        Assert.assertTrue(isCreated,
                "❌ Card '" + cardTitle + "' was NOT found on the board!");

        System.out.println("✅ BM-003 PASSED: Card '" + cardTitle + "' created successfully!");
    }


    // ─────────────────────────────────────────────────────
    // KAN-31: Create Multiple Cards (3 Cards)
    // ─────────────────────────────────────────────────────
    @Test(priority = 2,
            description = "KAN-31: Create multiple cards (3 cards) inside the existing list",
            dependsOnMethods = "test01_createCard")
    public void test02_createMultipleCards() {
        System.out.println("=================================================");
        System.out.println("RUNNING: KAN-31: Create Multiple Cards (3 Cards)");
        System.out.println("=================================================");

        // ── Step 1: Define the 3 card titles ──────────────────────────────
        String[] cardTitles = {
                "Automated Card 1",
                "Automated Card 2",
                "Automated Card 3"
        };

        // ── Step 2: Loop through each title and create a card ─────────────
        for (int i = 0; i < cardTitles.length; i++) {
            String cardTitle = cardTitles[i];

            System.out.println("-------------------------------------------");
            System.out.println("Creating Card " + (i + 1) + " of " + cardTitles.length + ": " + cardTitle);

            // ── Step 2a: Wait for "Add a card" button then click ──────────
//            System.out.println("STEP 2a: Waiting for 'Add a card' button for list: " + EXISTING_LIST_NAME);
//            wait.until(
//                    ExpectedConditions.elementToBeClickable(
//                            listAddCardButtonLocator(EXISTING_LIST_NAME)
//                    )
//            );
//            System.out.println("STEP 2a: Clicking 'Add a card' button...");
//            cardPage.clickAddCardButton(EXISTING_LIST_NAME);

            // ── Step 2b: Wait for title input then enter title ────────────
            System.out.println("STEP 2b: Waiting for card title input to appear...");
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[@data-testid='list-card-composer-textarea']")
                    )
            );
            System.out.println("STEP 2b: Entering card title: " + cardTitle);
            cardPage.enterCardTitle(cardTitle);

            // ── Step 2c: Wait for submit button then click ────────────────
            System.out.println("STEP 2c: Waiting for submit button to be clickable...");
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[@data-testid='list-card-composer-add-card-button']")
                    )
            );
            System.out.println("STEP 2c: Submitting card...");
            cardPage.clickAddCardSubmit();

            // ── Step 2d: Wait for card to appear then verify ──────────────
            System.out.println("STEP 2d: Waiting for card '" + cardTitle + "' to appear on board...");
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//a[@data-testid='card-name' and text()='" + cardTitle + "']")
                    )
            );
            System.out.println("STEP 2d: Verifying card '" + cardTitle + "' exists on board...");
            boolean isCreated = cardPage.isCardCreated(cardTitle);
            Assert.assertTrue(isCreated,
                    "❌ Card '" + cardTitle + "' (Card " + (i + 1) + ") was NOT found on the board!");

            System.out.println("✅ Card " + (i + 1) + ": '" + cardTitle + "' created successfully!");
        }

        System.out.println("-------------------------------------------");
        System.out.println("✅ KAN-31 PASSED: All " + cardTitles.length + " cards created and verified!");
        System.out.println("=================================================");
    }
}