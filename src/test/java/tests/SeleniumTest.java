package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SeleniumTest extends BaseTest {

    @Test
    public void verifyTrelloLogin() {

        System.out.println("Login verification test");

        Assert.assertTrue(
                driver.getCurrentUrl().contains("trello"),
                "Trello login verification failed"
        );

        System.out.println("Login verification PASSED.");
    }
}