# Trello Selenium Automation - Login & Authentication Module

Selenium WebDriver + TestNG automation framework for testing Trello's login and authentication functionality.

## 🚀 Quick Start

### Prerequisites
- **Java 21+** (tested with Java 26)
- **Firefox browser** installed
- **Maven 3.6+** (or use IntelliJ's bundled Maven)
- **Trello test account** (create one without 2FA)

### Setup (5 Minutes)

1. **Clone the repository**
   ```bash
   git clone git@github.com:alimaazen/trello-testing.git
   cd trello-testing
   ```

2. **Set up your credentials**
   
   **Windows:**
   ```cmd
   setx TRELLO_EMAIL "your_email@example.com"
   setx TRELLO_PASSWORD "your_password"
   ```
   
   **Mac/Linux:**
   ```bash
   export TRELLO_EMAIL="your_email@example.com"
   export TRELLO_PASSWORD="your_password"
   # Add to ~/.bashrc or ~/.zshrc for persistence
   ```
   
   **Or use the helper script (Windows):**
   ```cmd
   setup-env.bat
   ```

3. **Restart your IDE** (critical - environment variables only load on startup)

4. **Run tests**
   ```bash
   mvn clean test
   ```

## 📁 Project Structure

```
trello-selenium-automation/
├── pom.xml                          # Maven configuration
├── testng.xml                       # TestNG suite configuration
├── setup-env.bat                    # Windows credential setup helper
├── verify-env.bat                   # Verify environment variables
├── src/
│   ├── main/java/
│   │   ├── pages/
│   │   │   ├── LoginPage.java       # Login page object
│   │   │   └── DashboardPage.java   # Dashboard page object
│   │   └── utils/
│   │       ├── BaseTest.java        # Base test with login functionality
│   │       └── TestConfig.java      # Configuration reader
│   └── test/
│       ├── java/tests/
│       │   └── LoginTests.java      # Login test cases
│       └── resources/
│           ├── config.properties    # Local config (gitignored)
│           └── config.properties.example  # Template
```

## 🧪 Test Cases

| Test | Description | Status |
|------|-------------|--------|
| `testValidLogin` | Login with valid credentials | ✅ Pass |
| `testInvalidEmailLogin` | Login with unregistered email (redirects to signup) | ✅ Pass |
| `testInvalidPasswordLogin` | Login with wrong password | ✅ Pass |
| `testEmptyCredentialsLogin` | Prevent login with empty fields | ✅ Pass |
| `testLogout` | Complete logout flow | ✅ Pass |

## 👥 For Teammates - Using This Module

If you're testing Cards, Boards, Lists, etc., extend `BaseTest` to get automatic login:

```java
package tests;

import org.testng.annotations.Test;
import utils.BaseTest;

public class CardTests extends BaseTest {
    
    @Test
    public void testCreateCard() {
        // Automatically logged in
        performLogin();
        
        // Create your page objects
        CardPage cardPage = new CardPage(driver);
        cardPage.createNewCard("My Test Card");
        
        // Your assertions
    }
}
```

### Available Methods

- `performLogin()` - Login with your environment variables
- `performLogin(email, password)` - Login with custom credentials
- `navigateToLoginPage()` - Navigate without logging in
- `getDriver()` - Access WebDriver instance

### Available Objects

- `driver` - WebDriver instance
- `loginPage` - LoginPage object
- `dashboardPage` - DashboardPage object
- `config` - TestConfig object

## 🔧 Configuration

### Environment Variables (Recommended)

Credentials are stored as environment variables for security:
- `TRELLO_EMAIL` - Your Trello test account email
- `TRELLO_PASSWORD` - Your Trello test account password

**Why environment variables?**
- ✅ Never committed to git
- ✅ Each person uses their own account
- ✅ No credential conflicts
- ✅ CI/CD friendly

### Config File (Fallback)

Copy `config.properties.example` to `config.properties` and customize:
```properties
trello.url=https://trello.com/login
browser=firefox
implicit.wait=10
explicit.wait=15
page.load.timeout=30
```

## 🏃 Running Tests

```bash
# All tests
mvn clean test

# Specific test class
mvn test -Dtest=LoginTests

# Specific test method
mvn test -Dtest=LoginTests#testValidLogin

# With debug output
mvn test -X
```

### In IntelliJ IDEA

1. Right-click `LoginTests.java` → Run
2. Or click the green play button next to `@Test` methods

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 26 | Programming language |
| Selenium WebDriver | 4.15.0 | Browser automation |
| TestNG | 7.8.0 | Testing framework |
| WebDriverManager | 5.6.2 | Automatic driver management |
| Maven | 3.6+ | Build & dependency management |
| Firefox | Latest | Test browser |

## 📝 Creating New Page Objects

Follow the Page Object Model pattern:

```java
package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CardPage {
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Locators
    private By createCardButton = By.id("create-card-btn");
    private By cardTitleInput = By.id("card-title");
    
    // Constructor
    public CardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }
    
    // Methods
    public void createCard(String title) {
        driver.findElement(createCardButton).click();
        driver.findElement(cardTitleInput).sendKeys(title);
    }
}
```

## 🐛 Troubleshooting

### Tests fail with "Cannot find Firefox binary"
- Install Firefox browser
- Or change `BaseTest.java` to use Chrome/Edge

### "Environment variable null" error
- Set environment variables: `setx TRELLO_EMAIL "..."`
- **Restart IntelliJ/terminal** after setting variables
- Verify: `echo %TRELLO_EMAIL%` (Windows) or `echo $TRELLO_EMAIL` (Mac/Linux)

### "Cannot resolve symbol" in IntelliJ
- Maven dependencies not loaded
- IntelliJ → Maven panel → Reload (🔄 icon)
- Or: `mvn clean install`

### Login tests fail with 2FA prompt
- Disable 2FA on your Trello test account
- Test accounts don't need 2FA for security

### Java version issues
- This project uses Java 26
- IntelliJ: File → Project Structure → Project SDK → 26
- Or install Java 21 LTS if preferred

## 🔐 Security Best Practices

- ✅ Use environment variables for credentials
- ✅ Never commit `config.properties` with real passwords
- ✅ Create separate test accounts (not personal accounts)
- ✅ Disable 2FA on test accounts
- ✅ Each team member uses their own test account
- ❌ Don't share credentials in Slack/email
- ❌ Don't hardcode credentials in test files

## 📚 Additional Resources

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [TestNG Documentation](https://testng.org/doc/documentation-main.html)
- [Page Object Model Pattern](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)
- [Trello API Documentation](https://developer.atlassian.com/cloud/trello/)

## 🤝 Contributing

1. Create your test account
2. Set up environment variables
3. Create your feature branch: `git checkout -b feature/board-tests`
4. Write tests following existing patterns
5. Run tests: `mvn clean test`
6. Commit: `git commit -m "Add board creation tests"`
7. Push: `git push origin feature/board-tests`
8. Create Pull Request

## 📞 Team Contact

- **Login/Auth Module**: [Your Name]
- **Team Lead**: [Team Lead Name]
- **Issues**: Open a GitHub issue

---

**Built with ❤️ for reliable Trello testing**
