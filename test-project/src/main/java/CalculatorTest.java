import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;

import java.net.URL;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;

public class CalculatorTest {
    /**
     * Main method to run the tests on multiple browsers and log results to a file.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws Exception {

        new PrintWriter("test-results.txt").close(); // Clear the file at the start

        try (PrintWriter writer = new PrintWriter(new FileWriter("test-results.txt", true))) {
            String[] browsers = { "chrome", "firefox", "MicrosoftEdge" };
            for (String browser : browsers) {
                runOnBrowsers(browser, writer);
            }
        } catch (Exception e) {
            System.err.println("Failed to write to file: " + e.getMessage());
        }

        System.exit(0);
    }

    /**
     * Runs the test scenarios on the specified browser and logs results.
     *
     * @param browser the browser to run the tests on
     * @param writer  PrintWriter to log results
     */
    public static void runOnBrowsers(String browser, PrintWriter writer) {
        WebDriver driver = null;
        try {
            // initialize driver
            driver = getDriver(browser);
            driver.get("https://www.calculator.net/carbohydrate-calculator.html");

            // run tests on multiple scenarios
            runMetricUnitScenario(driver, browser, writer);
            runUSUnitScenario(driver, browser, writer);
            runBodyFatScenario(driver, browser, writer);

        } catch (Exception e) {
            // System.err.println("[" + browser + "] Failed to run test: " +
            // e.getMessage());
            String error = "[" + browser + "] Failed to run test: " + e.getMessage();
            System.err.println(error);
            writer.println(" - " + error);

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * Initializes the WebDriver for the specified browser.
     *
     * @param browser the browser to initialize
     * @return WebDriver instance for the specified browser
     * @throws Exception if there is an error initializing the driver
     */
    private static WebDriver getDriver(String browser) throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setBrowserName(browser);

        if (browser.equals("chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--start-maximized");
            caps.merge(options);
        } else if (browser.equals("firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            caps.merge(options);
        } else if (browser.equals("MicrosoftEdge")) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            caps.merge(options);
        }

        return new RemoteWebDriver(new URL("http://selenium-hub:4444/wd/hub"), caps);
    }

    /**
     * Runs the Metric Unit scenario and logs results.
     *
     * @param driver  WebDriver instance
     * @param browser the browser being used
     * @param writer  PrintWriter to log results
     */
    private static void runMetricUnitScenario(WebDriver driver, String browser, PrintWriter writer) {
        try {
            writer.println("Running Metric Unit scenario on " + browser + " at " + LocalDateTime.now());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement metricLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#menuon a")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", metricLink);
            writer.println("[" + browser + "] Clicked 'Metric Units' tab");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("metricheightweight")));
            writer.println("[" + browser + "] Metric Form loaded");

            WebElement ageInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("cage")));
            ageInput.clear();
            ageInput.sendKeys("30");
            writer.println("[" + browser + "] Filled in age");

            WebElement femaleLabel = driver.findElement(By.cssSelector("label[for='csex2']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", femaleLabel);
            writer.println("[" + browser + "] Selected gender: female");

            WebElement heightInput = driver.findElement(By.name("cheightmeter"));
            heightInput.clear();
            heightInput.sendKeys("170");
            writer.println("[" + browser + "] Filled in height in cm");

            WebElement weightInput = driver.findElement(By.name("ckg"));
            weightInput.clear();
            weightInput.sendKeys("60");
            writer.println("[" + browser + "] Filled in weight in kg");

            WebElement activityDropdown = driver.findElement(By.name("cactivity"));
            Select activitySelect = new Select(activityDropdown);
            activitySelect.selectByVisibleText("Active: daily exercise or intense exercise 3-4 times/week");
            writer.println("[" + browser + "] Selected activity level: Active");

            WebElement calculateButton = driver.findElement(By.xpath("//input[@value='Calculate']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", calculateButton);
            writer.println("[" + browser + "] Clicked 'Calculate' button");

            WebElement result = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//b[contains(text(), 'grams')]")));
            String resultText = result.getText();
            // String log = "[" + browser + "] Metric Units Result: " + resultText;
            String message = String.format(
                    "[%s] For 'Weight Maintenance' goal at 40%% carb intake, recommended carbohydrate: %s.",
                    browser, resultText);
            System.out.println(message);
            writer.println(message);

        } catch (Exception e) {
            String error = "[" + browser + "] Failed to run Metric Unit scenario: " + e.getMessage();
            System.err.println(error);
            writer.println(" - " + error);
        }
    }

    /**
     * Runs the US Unit scenario and logs results.
     *
     * @param driver  WebDriver instance
     * @param browser the browser being used
     * @param writer  PrintWriter to log results
     */
    private static void runUSUnitScenario(WebDriver driver, String browser, PrintWriter writer) {
        try {
            writer.println("Running US/Unit scenario on " + browser + " at " + LocalDateTime.now());
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement usLink = wait
                    .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'US Units')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", usLink);
            writer.println("[" + browser + "] Clicked 'US Units' tab");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("standardheightweight")));
            writer.println("[" + browser + "] US Form loaded");

            WebElement ageInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("cage")));
            ageInput.clear();
            ageInput.sendKeys("35");
            writer.println("[" + browser + "] Filled in age");

            WebElement maleLabel = driver.findElement(By.cssSelector("label[for='csex1']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", maleLabel);
            writer.println("[" + browser + "] Selected gender: male");

            driver.findElement(By.name("cheightfeet")).clear();
            driver.findElement(By.name("cheightfeet")).sendKeys("5");
            driver.findElement(By.name("cheightinch")).clear();
            driver.findElement(By.name("cheightinch")).sendKeys("10");
            writer.println("[" + browser + "] Filled in height in feet and inches");

            WebElement weightInput = driver.findElement(By.name("cpound"));
            weightInput.clear();
            weightInput.sendKeys("180");
            writer.println("[" + browser + "] Filled in weight in pounds");

            WebElement activityDropdown = driver.findElement(By.name("cactivity"));
            Select activitySelect = new Select(activityDropdown);
            activitySelect.selectByIndex(2);
            // activitySelect.selectByVisibleText("Moderate: exercise 4–5 times/week");
            writer.println("[" + browser + "] Selected activity level: Moderate");

            WebElement calculateButton = driver.findElement(By.xpath("//input[@value='Calculate']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", calculateButton);
            writer.println("[" + browser + "] Clicked 'Calculate' button");

            WebElement result = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//b[contains(text(), 'grams')]")));
            String resultText = result.getText();
            String message = String.format(
                    "[%s] For 'Weight Maintenance' goal at 40%% carb intake, recommended carbohydrate: %s.",
                    browser, resultText);
            System.out.println(message);
            writer.println(message);
        } catch (Exception e) {
            String error = "[" + browser + "] Failed to run US Unit scenario: " + e.getMessage();
            System.err.println(error);
            writer.println(" - " + error);
        }
    }

    /**
     * Runs the Body Fat scenario and logs results.
     *
     * @param driver  WebDriver instance
     * @param browser the browser being used
     * @param writer  PrintWriter to log results
     */
    private static void runBodyFatScenario(WebDriver driver, String browser, PrintWriter writer) {
        try {
            writer.println("Running Body Fat scenario on " + browser + " at " + LocalDateTime.now());
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement usLink = wait
                    .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'US Units')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", usLink);
            writer.println("[" + browser + "] Clicked 'US Units' tab");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("standardheightweight")));
            writer.println("[" + browser + "] US Form loaded");

            WebElement ageInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("cage")));
            ageInput.clear();
            ageInput.sendKeys("35");
            writer.println("[" + browser + "] Filled in age");

            WebElement maleLabel = driver.findElement(By.cssSelector("label[for='csex1']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", maleLabel);
            writer.println("[" + browser + "] Selected gender: male");

            driver.findElement(By.name("cheightfeet")).clear();
            driver.findElement(By.name("cheightfeet")).sendKeys("5");
            driver.findElement(By.name("cheightinch")).clear();
            driver.findElement(By.name("cheightinch")).sendKeys("10");
            writer.println("[" + browser + "] Filled in height in feet and inches");

            WebElement weightInput = driver.findElement(By.name("cpound"));
            weightInput.clear();
            weightInput.sendKeys("180");
            writer.println("[" + browser + "] Filled in weight in pounds");

            WebElement activityDropdown = driver.findElement(By.name("cactivity"));
            Select activitySelect = new Select(activityDropdown);
            activitySelect.selectByIndex(2);
            // activitySelect.selectByVisibleText("Moderate: exercise 4–5 times/week");
            writer.println("[" + browser + "] Selected activity level: Moderate");

            WebElement settingsToggle = driver.findElement(By.xpath("//a[contains(text(), 'Settings')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", settingsToggle);
            writer.println("[" + browser + "] Clicked 'Settings' toggle");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ccsettingcontent")));

            WebElement katchOption = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cformula2")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", katchOption);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", katchOption);
            writer.println("[" + browser + "] Selected Katch-McArdle formula");

            WebElement fatInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("cfatpct")));
            fatInput.clear();
            fatInput.sendKeys("15");
            writer.println("[" + browser + "] Filled in body fat percentage");

            WebElement calculateButton = driver.findElement(By.xpath("//input[@value='Calculate']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", calculateButton);
            writer.println("[" + browser + "] Clicked 'Calculate' button");

            WebElement result = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//b[contains(text(), 'grams')]")));
            String resultText = result.getText();
            String message = String.format(
                    "[%s] Using Katch-McArdle with 15%% body fat, recommended carbohydrate: %s.",
                    browser, resultText);
            System.out.println(message);
            writer.println(message);
        } catch (Exception e) {
            String error = "[" + browser + "] Failed to run US Unit scenario: " + e.getMessage();
            System.err.println(error);
            writer.println(" - " + error);
        }
    }
}