import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class DragAndDrop_Functionality {
    WebDriver driver;
    WebDriverWait wait;
    Actions actions;


    @BeforeSuite
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://trello.com/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));

        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("username-_r0_")
                )
        );

        email.sendKeys("YOUR_EMAIL");

        // Click Continue
        driver.findElement(By.id("login-submit"))
                .click();

        // Enter password
        WebElement pass = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("password")
                )
        );
        driver.findElement(By.id("password"))
                .sendKeys("YOUR_PASSWORD");

        // Click Log in
        driver.findElement(By.id("login-submit"))
                .click();

        // Verify successful login
        Assert.assertTrue(
                driver.getCurrentUrl().contains("trello.com"),
                "Login was not successful"
        );
    }

    @BeforeMethod
    public void initializeObjects() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        actions = new Actions(driver);
    }

    @AfterMethod
    public void waitBetweenTests() throws InterruptedException{
        Thread.sleep(1000);
    }

    void Action(WebElement source, WebElement target){
        Point sl = source.getLocation();
        Point tl = target.getLocation();
        Duration wt = Duration.ofMillis(1000);

        int x_offs = tl.getX()-sl.getX();
        int y_offs = tl.getY()-sl.getY();
        actions.moveToElement(source).pause(wt).clickAndHold(source).pause(wt).moveByOffset(5,5).pause(wt).moveToElement(target)
                .pause(wt).release().pause(wt).build().perform();
    }

    @Test(priority = 1)
    public void openBoard() {
        WebElement board = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href,'/b/3gSxQuKl/trello-project')]")));
        board.click();
    }

    @Test(priority = 2,enabled=true)
    public void DragAndDrop_CardFromOneListToOther(){
        String cardname = "Create test cases";
        String Sourcelist = "To Do";
        String Targetlist = "Doing";

        String cardsContainerXpath =
                "//li[@data-testid='list-wrapper']" +
                        "[.//h2[@data-testid='list-name']//span[text()='%s']]" +
                        "//ol[@data-testid='list-cards']";

        // Target the LI (draggable="true"), not the <a> inside it
        String cardLiXpath = cardsContainerXpath +
                "//li[@data-testid='list-card'][.//a[@data-testid='card-name'][text()='" + cardname + "']]";

        By sourceCardBy = By.xpath(String.format(cardLiXpath, Sourcelist));
        By targetListBy = By.xpath(String.format(cardsContainerXpath, Targetlist));

        WebElement sourceCard = wait.until(ExpectedConditions.visibilityOfElementLocated(sourceCardBy));
        WebElement targetList = wait.until(ExpectedConditions.visibilityOfElementLocated(targetListBy));

        Action(sourceCard,targetList);


        wait.until(ExpectedConditions.invisibilityOfElementLocated(sourceCardBy));

        // Assertion 1: card no longer exists in List A
        Assert.assertEquals(
                driver.findElements(sourceCardBy).size(), 0,
                "Card '" + cardname + "' should no longer appear in '" + Sourcelist + "'"
        );

        By cardInListBBy = By.xpath(String.format(cardLiXpath, Targetlist));
        WebElement cardInListB = wait.until(ExpectedConditions.visibilityOfElementLocated(cardInListBBy));

        Assert.assertTrue(
                cardInListB.isDisplayed(),
                "Card '" + cardname + "' should now appear in '" + Targetlist + "'"
        );

    }

    @Test(priority = 3, enabled = true)
    public void DragAndDrop_CardInAList() {
        String sourcecard = "implement test cases";
        String destcard = "Prepare test script";
        String Listname = "To Do";
        // Scope to the specific list containing both cards, e.g. "To Do"
        String listCardsXpath =
                "//li[@data-testid='list-wrapper']" +
                        "[.//h2[@data-testid='list-name']//span[text()='" + Listname + "']]" +
                        "//ol[@data-testid='list-cards']";

        String cardInListXpath = listCardsXpath +
                "//li[@data-testid='list-card'][.//a[@data-testid='card-name'][text()='%s']]";

        By sourceCardBy = By.xpath(String.format(cardInListXpath, sourcecard));
        By targetCardBy = By.xpath(String.format(cardInListXpath, destcard));

        WebElement sourceCard = wait.until(ExpectedConditions.visibilityOfElementLocated(sourceCardBy));
        WebElement targetCard = wait.until(ExpectedConditions.visibilityOfElementLocated(targetCardBy));

        // Capture original order before dragging
        java.util.List<WebElement> cardsBefore = driver.findElements(By.xpath(listCardsXpath + "//li[@data-testid='list-card']"));
        int sourceIndexBefore = cardsBefore.indexOf(sourceCard);
        int targetIndexBefore = cardsBefore.indexOf(targetCard);

        Action(sourceCard, targetCard);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        // Capture new order after dragging
        java.util.List<WebElement> cardsAfter = driver.findElements(By.xpath(listCardsXpath + "//li[@data-testid='list-card']"));
        java.util.List<String> namesAfter = cardsAfter.stream()
                .map(li -> li.findElement(By.xpath(".//a[@data-testid='card-name']")).getText())
                .collect(java.util.stream.Collectors.toList());

        int sourceIndexAfter = namesAfter.indexOf(sourcecard);

        Assert.assertNotEquals(sourceIndexAfter, sourceIndexBefore,
                "Card '" + sourcecard + "' should have changed position within the list");
    }

    @Test(priority = 4,enabled = true)
    public void DragAndDrop_EntireList() {
        String sourcelis = "Doing";
        String destlis = "Done";
        WebElement list1 = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h2[@data-testid='list-name'][.//span[text()='" + sourcelis + "']]")
                )
        );
        WebElement list2 = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h2[@data-testid='list-name'][.//span[text()='" + destlis + "']]")
                )
        );

        List<WebElement> listsBefore = driver.findElements(
                By.xpath("//li[@data-testid='list-wrapper']//h2[@data-testid='list-name']//span")
        );
        List<String> namesBefore = listsBefore.stream().map(WebElement::getText).collect(Collectors.toList());
        int sourceIndexBefore = namesBefore.indexOf(sourcelis);

        Action(list1,list2);

        wait.until(driver1 -> {
            List<WebElement> current = driver1.findElements(
                    By.xpath("//li[@data-testid='list-wrapper']//h2[@data-testid='list-name']//span")
            );
            List<String> names = current.stream().map(WebElement::getText).collect(Collectors.toList());
            int idx = names.indexOf(sourcelis);
            return idx >= 0 && idx != sourceIndexBefore;
        });

        List<WebElement> listsAfter = driver.findElements(
                By.xpath("//li[@data-testid='list-wrapper']//h2[@data-testid='list-name']//span")
        );
        List<String> namesAfter = listsAfter.stream().map(WebElement::getText).collect(Collectors.toList());
        int sourceIndexAfter = namesAfter.indexOf(sourcelis);

        Assert.assertTrue(namesAfter.contains(sourcelis),
                "List '" + sourcelis + "' should still exist after reordering");
        Assert.assertTrue(namesAfter.contains(destlis),
                "List '" + destlis + "' should still exist after reordering");
        Assert.assertNotEquals(sourceIndexAfter, sourceIndexBefore,
                "List '" + sourcelis + "' should have changed position on the board");
    }

    @Test(priority = 5,enabled = true)
    //this test case is for negative test case for out of list and returning to its original position
    public void DragCard_OutsideScope_ReturnsToPosition() {
        String cardText = "Prepare test script";
        String listName = "To Do";

        String listCardsXpath =
                "//li[@data-testid='list-wrapper']" +
                        "[.//h2[@data-testid='list-name']//span[text()='" + listName + "']]" +
                        "//ol[@data-testid='list-cards']";


        String cardInListXpath = listCardsXpath +
                "//li[@data-testid='list-card'][.//a[@data-testid='card-name'][text()='%s']]";

        By sourceCardBy = By.xpath(String.format(cardInListXpath, cardText));
        By listCardsBy = By.xpath(listCardsXpath + "//li[@data-testid='list-card']");

        WebElement sourceCard = wait.until(ExpectedConditions.visibilityOfElementLocated(sourceCardBy));

        List<WebElement> cardsBefore = driver.findElements(listCardsBy);
        int indexBefore = -1;
        for (int i = 0; i < cardsBefore.size(); i++) {
            String text = cardsBefore.get(i).findElement(By.xpath(".//a[@data-testid='card-name']")).getText();
            if (text.equals(cardText)) {
                indexBefore = i;
                break;
            }
        }
        Assert.assertTrue(indexBefore >= 0, "Card was not found before drag");

        WebElement invalidTarget = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav[@data-testid='authenticated-header']"))
        );
        Action(sourceCard, invalidTarget);

        wait.until(driver1 -> {
            List<WebElement> current = driver1.findElements(listCardsBy);
            return current.stream()
                    .anyMatch(li -> li.findElement(By.xpath(".//a[@data-testid='card-name']")).getText().equals(cardText));
        });

        List<WebElement> cardsAfter = driver.findElements(listCardsBy);
        int indexAfter = -1;
        for (int i = 0; i < cardsAfter.size(); i++) {
            String text = cardsAfter.get(i).findElement(By.xpath(".//a[@data-testid='card-name']")).getText();
            if (text.equals(cardText)) {
                indexAfter = i;
                break;
            }
        }

        Assert.assertTrue(indexAfter >= 0, "Card should still be present in the original list");
        Assert.assertEquals(indexAfter, indexBefore,
                "Card should return to its original position after invalid drag");
    }
}





