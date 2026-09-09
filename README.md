# Trello Selenium Automation - Login & Authentication Module

Selenium WebDriver + TestNG automation framework for testing Trello. This repository currently contains the **framework core** and the **Login & Authentication module**. Teammates add their own test modules (boards, cards, lists, etc.) on top of the shared base.

## How It Works

The project follows the **Page Object Model** with a shared test base. Responsibilities are split so each layer has exactly one reason to change:

| Layer | Location | Responsibility |
|-------|----------|----------------|
| Tests | `src/test/java/tests/` | What to verify - scenarios and assertions only |
| Page Objects | `src/main/java/pages/` | How to drive a page - locators and actions, one class per page |
| Test Base | `src/main/java/utils/BaseTest.java` | Browser lifecycle, login helpers, shared config |
| Config | `src/main/java/utils/TestConfig.java` | Reads settings: environment variables first, then `config.properties` |
| Failure Evidence | `src/main/java/utils/ScreenshotListener.java` | Saves a screenshot on every failed test |

Execution flow of a single test:

1. `@BeforeMethod` in `BaseTest` starts a fresh browser (configured via `browser` key, default Firefox) and initializes the page objects.
2. The test calls `performLogin()` - credentials are preflighted, the two-step Trello login runs, and dashboard load is verified.
3. The test asserts state through page objects.
4. `@AfterMethod` closes the browser, so every test runs in a clean session with no state bleed.
5. If the test fails, `ScreenshotListener` writes a PNG to `test-output/screenshots/` before the browser closes.

There is no `testng.xml`. Maven auto-discovers test classes whose names end in `Tests`, and IntelliJ runs any class containing `@Test` directly - both workflows behave identically.

## Prerequisites

- Java 21+ (project targets 26)
- Firefox (default browser; Chrome/Edge optional via config)
- Maven 3.6+ (or IntelliJ's bundled Maven)
- A Trello test account without 2FA

## Quick Start

1. **Clone the repository**

   ```bash
   git clone git@github.com:alimaazen/trello-testing.git
   cd trello-testing
   ```

2. **Set your credentials**

   ```cmd
   setx TRELLO_EMAIL "your_email@example.com"
   setx TRELLO_PASSWORD "your_password"
   ```

   Or run the helper script:

   ```cmd
   setup-env.bat
   ```

3. **Restart your IDE and terminals** - environment variables only load on startup.

4. **Run the tests**

   ```bash
   mvn clean test
   ```

## Project Structure

```
trello-testing/
├── pom.xml                          # Maven configuration
├── setup-env.bat                    # Credential setup helper
├── verify-env.bat                   # Verify environment variables
├── src/
│   ├── main/java/
│   │   ├── pages/
│   │   │   ├── LoginPage.java       # Login page object
│   │   │   └── DashboardPage.java   # Dashboard page object
│   │   └── utils/
│   │       ├── BaseTest.java        # Base test: driver setup + login helpers
│   │       ├── TestConfig.java      # Configuration reader (env vars > config file)
│   │       └── ScreenshotListener.java  # Screenshot on test failure
│   └── test/
│       ├── java/tests/
│       │   └── LoginTests.java      # Login test cases
│       └── resources/
│           ├── config.properties    # Local config (gitignored)
│           └── config.properties.example  # Template
```

## Login Module Coverage

| Test | Scenario |
|------|----------|
| `testValidLogin` | Valid credentials reach the dashboard |
| `testInvalidEmailLogin` | Unregistered email never reaches the password step; signup redirect or error |
| `testInvalidPasswordLogin` | Wrong password keeps the user on the login page / shows an error |
| `testEmptyCredentialsLogin` | Empty email cannot advance to the password step |
| `testLogout` | Logout completes and the session is gone |

## For Teammates - Building Your Module

Extend `BaseTest` to inherit browser setup, config, and login:

```java
package tests;

import org.testng.annotations.Test;
import utils.BaseTest;
import pages.DashboardPage;

public class CardTests extends BaseTest {

    @Test
    public void testCreateCard() {
        // Verified login - safe to proceed when this returns
        DashboardPage dashboard = performLogin();

        // Your page objects and assertions here
    }
}
```

### Available Methods

- `performLogin()` - Login with your configured credentials. **Verifies the dashboard actually loaded** and fails fast with a clear message if credentials are missing or login does not work.
- `performLogin(email, password)` - Login with explicit credentials, **no success verification**. Use for negative tests where failing is the expected outcome.
- `navigateToLoginPage()` - Navigate to the login page without logging in.
- `waitUntil(condition)` - Wait up to 15s for a condition; returns false on timeout instead of throwing.
- `getDriver()` - Access the WebDriver instance.

### Available Objects

- `driver` - WebDriver instance
- `loginPage` - LoginPage object
- `dashboardPage` - DashboardPage object
- `config` - TestConfig object

Failed tests automatically save a screenshot to `test-output/screenshots/` (path printed to the console).

### Rules

1. Test class names must end in `Tests` (e.g. `CardTests`, `BoardTests`) - this is what Maven auto-discovery matches.
2. Never hardcode credentials - use environment variables or `config.properties` (gitignored).
3. Locators live in page objects, never in test methods. If a locator is missing, add a page object method.
4. Branch from `master`, submit your work as a pull request. Do not create parallel repositories or zip files.

## Configuration

Settings resolve in priority order: **environment variable** > **config.properties** > built-in default. Key mapping is automatic: `trello.url` -> `TRELLO_URL`.

### Environment Variables (recommended for credentials)

- `TRELLO_EMAIL` - test account email
- `TRELLO_PASSWORD` - test account password

Why environment variables: never committed to git, each member uses their own account, no credential conflicts, CI/CD friendly.

### Config File

Copy `config.properties.example` to `config.properties` and customize:

```properties
trello.url=https://trello.com/login
browser=firefox
headless=false
```

- `browser` - `firefox` (default), `chrome`, or `edge`
- `headless` - `true` for CI/no-window runs, `false` (default) otherwise

A missing `config.properties` is not an error - the framework falls back to environment variables alone. A missing *required key* fails fast with a message naming the exact variable to set.

## Running Tests

```bash
# All tests
mvn clean test

# Specific test class
mvn test -Dtest=LoginTests

# Specific test method
mvn test -Dtest=LoginTests#testValidLogin
```

In IntelliJ IDEA: right-click a test class or method and select Run - no suite file needed.

## Writing New Page Objects

Follow the existing pattern - locators as fields, actions as methods, explicit waits throughout:

```java
package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CardPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private By addCardButton = By.cssSelector("button[data-testid='add-card']");

    public CardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void createCard(String title) {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(addCardButton));
        button.click();
        // ... enter title, submit
    }
}
```

Guidelines:

- Prefer `data-testid` locators over generated ids (`username-uid1`-style ids can shift between Trello builds).
- Never use `Thread.sleep` - use explicit waits (`WebDriverWait`, or `waitUntil()` from `BaseTest`).
- Methods that probe state return booleans instead of throwing; action methods fail loudly.

## Troubleshooting

### "Cannot find Firefox binary"
- Install Firefox, or switch browsers in `config.properties`: `browser=chrome` or `browser=edge`.

### "Environment variable null" error
- Set the variables, then restart your terminal/IDE: `setx TRELLO_EMAIL "..."`.
- Verify: `echo %TRELLO_EMAIL%`, or run `verify-env.bat`.

### "Missing configuration for ..." error
- The framework names the exact key that is missing. Set the matching environment variable (e.g. `TRELLO_URL`) or add the key to `config.properties`.

### "Cannot resolve symbol" in IntelliJ
- Maven dependencies not loaded: Maven panel -> Reload, or run `mvn clean install`.

### Login tests fail with a 2FA prompt
- Disable 2FA on your Trello test account.

### Java version issues
- Project targets Java 26; Java 21+ works. IntelliJ: File -> Project Structure -> Project SDK.

## Security Practices

- Store credentials as environment variables, never in code or git.
- `config.properties` is gitignored - keep real passwords out of `config.properties.example` too.
- Use a dedicated test account, not a personal one. Disable 2FA on test accounts only.
- One account per person - avoids conflicts when tests run in parallel.
- Never share credentials in Slack, email, or commit messages.

## Contributing

1. Set up your test account and environment variables (see Quick Start).
2. Branch from `master`: `git checkout -b feature/board-tests`.
3. Write tests following the existing patterns (extend `BaseTest`, name the class `*Tests`).
4. Run `mvn clean test` before pushing.
5. Open a pull request - do not push branches with unrelated history or share code as zip files.

## Resources

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [TestNG Documentation](https://testng.org/doc/documentation-main.html)
- [Page Object Model](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)
- [Trello API Documentation](https://developer.atlassian.com/cloud/trello/)


