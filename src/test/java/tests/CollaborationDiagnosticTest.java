package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import pages.BoardPage;
import pages.DashboardPage;
import utils.BaseTest;

import java.util.List;

/**
 * Diagnostic test to investigate collaboration setup issues.
 * Run this FIRST before running the full CollaborationTests suite.
 */
public class CollaborationDiagnosticTest extends BaseTest {

    private String secondAccountEmail() {
        return config.getProperty("trello.email.second");
    }

    private String secondAccountName() {
        return "Rashmi";
    }

    @Test(description = "Diagnostic: Check workspace membership and invitation flow")
    public void testDiagnosticInvite() {
        System.out.println("========================================");
        System.out.println("COLLABORATION DIAGNOSTIC TEST");
        System.out.println("========================================");
        
        // Step 1: Login with main account
        System.out.println("\n[STEP 1] Logging in with main account...");
        DashboardPage dashboard = performLogin();
        System.out.println("[STEP 1] ✓ Main account logged in");

        // Step 2: Login with second account
        System.out.println("\n[STEP 2] Logging in with second account...");
        try {
            performSecondLogin();
            System.out.println("[STEP 2] ✓ Second account logged in");
        } catch (Exception e) {
            System.out.println("[STEP 2] ✗ Second account login failed: " + e.getMessage());
            throw e;
        }

        // Step 3: Create diagnostic board
        System.out.println("\n[STEP 3] Creating diagnostic board...");
        String boardName = "Diagnostic-Board-" + System.currentTimeMillis();
        BoardPage board = new BoardPage(driver);
        board.createNewBoard(boardName);
        System.out.println("[STEP 3] ✓ Board created: " + boardName);

        // Step 4: Open Share dialog
        System.out.println("\n[STEP 4] Opening Share dialog...");
        board.openShareDialog();
        System.out.println("[STEP 4] ✓ Share dialog opened");

        // Step 5: Check current members
        System.out.println("\n[STEP 5] Checking current board members...");
        List<WebElement> currentMembers = driver.findElements(By.cssSelector("[data-testid='member-item']"));
        System.out.println("[STEP 5] Current member count: " + currentMembers.size());
        for (int i = 0; i < currentMembers.size(); i++) {
            System.out.println("[STEP 5] Member[" + i + "]: " + currentMembers.get(i).getText());
        }

        // Step 6: Type email and check for typeahead
        System.out.println("\n[STEP 6] Typing second account email: " + secondAccountEmail());
        WebElement searchInput = driver.findElement(By.cssSelector("input[data-testid='add-members-input']"));
        searchInput.clear();
        searchInput.sendKeys(secondAccountEmail());
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check for typeahead suggestions
        List<WebElement> suggestions = driver.findElements(By.cssSelector("[data-testid='team-invitee-option']"));
        System.out.println("[STEP 6] Typeahead suggestions found: " + suggestions.size());
        
        if (suggestions.isEmpty()) {
            System.out.println("[STEP 6] ⚠ WARNING: No typeahead suggestions!");
            System.out.println("[STEP 6] This means accounts are likely NOT in the same workspace.");
            System.out.println("[STEP 6] See COLLABORATION_SETUP.md for how to fix this.");
            
            // Try to find ANY elements that might be suggestions
            List<WebElement> allButtons = driver.findElements(By.cssSelector("button"));
            System.out.println("[STEP 6] Total buttons visible: " + allButtons.size());
            
        } else {
            System.out.println("[STEP 6] ✓ Typeahead suggestions found!");
            for (int i = 0; i < suggestions.size(); i++) {
                WebElement suggestion = suggestions.get(i);
                System.out.println("[STEP 6] Suggestion[" + i + "]: " + suggestion.getText());
            }
        }

        // Step 7: Try to invite
        System.out.println("\n[STEP 7] Attempting to invite member...");
        try {
            board.inviteMemberByEmail(secondAccountEmail());
            System.out.println("[STEP 7] ✓ Invite method completed");
        } catch (Exception e) {
            System.out.println("[STEP 7] ✗ Invite method failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Step 8: Check members again
        System.out.println("\n[STEP 8] Checking board members after invite...");
        List<WebElement> membersAfterInvite = driver.findElements(By.cssSelector("[data-testid='member-item']"));
        System.out.println("[STEP 8] Member count after invite: " + membersAfterInvite.size());
        for (int i = 0; i < membersAfterInvite.size(); i++) {
            WebElement member = membersAfterInvite.get(i);
            String memberText = member.getText();
            System.out.println("[STEP 8] Member[" + i + "]: " + memberText);
            
            // Check if this matches what we're looking for
            if (memberText.contains(secondAccountName())) {
                System.out.println("[STEP 8] ✓ FOUND second account in member list!");
            }
            if (memberText.toLowerCase().contains(secondAccountName().toLowerCase())) {
                System.out.println("[STEP 8] ✓ FOUND (case-insensitive match)!");
            }
            if (memberText.contains(secondAccountEmail())) {
                System.out.println("[STEP 8] ✓ FOUND by email!");
            }
        }

        // Step 9: Summary
        System.out.println("\n========================================");
        System.out.println("DIAGNOSTIC SUMMARY");
        System.out.println("========================================");
        System.out.println("Looking for member name: '" + secondAccountName() + "'");
        System.out.println("Looking for email: '" + secondAccountEmail() + "'");
        System.out.println("Typeahead suggestions: " + suggestions.size());
        System.out.println("Members before invite: " + currentMembers.size());
        System.out.println("Members after invite: " + membersAfterInvite.size());
        
        if (membersAfterInvite.size() > currentMembers.size()) {
            System.out.println("✓ Member count increased - invitation worked!");
        } else if (suggestions.isEmpty()) {
            System.out.println("✗ No typeahead - accounts not in same workspace");
            System.out.println("ACTION REQUIRED: Add both accounts to same workspace");
        } else {
            System.out.println("✗ Unknown issue - check member names above");
        }
        
        System.out.println("========================================");

        // Keep browser open for manual inspection
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
