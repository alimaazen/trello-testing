package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CardPage;
import utils.TestData;

public class CardDetailsTest extends BaseTest {

    private CardPage cardPage;

    @BeforeMethod
    public void initializePage() {
        cardPage = new CardPage(driver);
    }

    @Test
    public void addDescription() {

        cardPage.openCard(TestData.cardName);

        cardPage.clickDescription();

        String description = "This is an automated card description. and Script was automatically updated over thier.";

        cardPage.addDescription(description);
        cardPage.savedescription();

        Assert.assertTrue(
                cardPage.isSavedDescriptionDisplayed(description),
                "Saved description was not displayed correctly."
        );
        cardPage.closeCard();
    }

    //opening the label and adding the geen label to the card which was created
    @Test
    public void addGreenLabel() {

        cardPage.openCard(TestData.cardName);
        cardPage.clickLabels();
        cardPage.selectLabel("green");
        Assert.assertTrue(
                cardPage.isLabelApplied("green"),
                "Green label was not applied to the card."
        );
        cardPage.closeCard();
    }

    @Test
    public void setDueDate() {

        cardPage.openCard(TestData.cardName);

        cardPage.clickDates();

        cardPage.enterDueDate("9/15/2026");

        cardPage.saveDueDate();

        Assert.assertTrue(
                cardPage.isDueDateDisplayed("Sep 15"),
                "Due date was not displayed correctly."
        );

        cardPage.closeCard();
    }
    @Test
    public void addChecklist() {

        cardPage.openCard(TestData.cardName);

        cardPage.clickChecklist();

        String checklistName = "Automation Checklist";
        cardPage.enterChecklistName(checklistName);
        cardPage.addChecklist();

        String checklistItem = "Verify Trello automation";

        cardPage.enterChecklistItem(checklistItem);
        cardPage.addChecklistItem();

        Assert.assertTrue(
                cardPage.isChecklistItemDisplayed(checklistItem),
                "Checklist item was not displayed correctly."
        );

        cardPage.checkChecklistItem(checklistItem);

        Assert.assertTrue(
                cardPage.isChecklistItemChecked(checklistItem),
                "Checklist item was not checked successfully."
        );

        cardPage.closeCard();
    }
    /*@Test
    public void removeCardLabel() {

        cardPage.openCard(TestData.cardName);

        String labelColor = "green";

        // Verify the green label is already present
        Assert.assertTrue(
                cardPage.isLabelApplied(labelColor),
                "Green label is not present on the card."
        );

        // Click the already-applied label
        cardPage.clickAppliedLabel(labelColor);

        // Select the green label to remove it
        cardPage.selectLabel(labelColor);

        Assert.assertFalse(
                cardPage.isLabelApplied(labelColor),
                "Green label was not removed from the card."
        );

        cardPage.closeCard();
    }*/

}