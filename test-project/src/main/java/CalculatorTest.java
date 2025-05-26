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
    public static void main(String[] args) throws Exception {

        new PrintWriter("test-results.txt").close(); // Clear the file at the start

        try (PrintWriter writer = new PrintWriter(new FileWriter("test-results.txt", true))) {
            String[] browsers = { "chrome", "firefox" };
            for (String browser : browsers) {
                runOnBrowsers(browser, writer);
            }
        } catch (Exception e) {
            System.err.println("Failed to write to file: " + e.getMessage());
        }

        System.exit(0);
    }

    public static void runOnBrowsers(String browser, PrintWriter writer) {
        WebDriver driver = null;
        try {
            // initialize driver
            driver = getDriver(browser);
            driver.get("https://www.calculator.net/carbohydrate-calculator.html");

            // run tests on multiple scenarios
            runMetricUnitScenario(driver, browser, writer);

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
}