package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import utils.BaseTest;

public class SeleniumTest extends BaseTest {

    @Test
    public void verifyTrelloLogin() {
        performLogin();

        System.out.println("Login verification test");

        Assert.assertTrue(
                driver.getCurrentUrl().contains("trello"),
                "Trello login verification failed"
        );

        System.out.println("Login verification PASSED.");
    }
}
