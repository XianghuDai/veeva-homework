import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;

import java.net.URL;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class CalculatorTest {
    public static void main(String[] args) throws Exception {

        PrintWriter writer = new PrintWriter(new FileWriter("test-results.log", true));
        String[] browsers = { "chrome", "firefox", "MicrosoftEdge" };

        for (String browser : browsers) {
            System.out.println("Running tests on " + browser);
            WebDriver driver = null;
            try {
                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setBrowserName(browser);

                if (browser.equals("chrome")) {
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
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

                driver = new RemoteWebDriver(
                        new URL("http://selenium-hub:4444/wd/hub"), caps);

                driver.get("https://www.calculator.net/carbohydrate-calculator.html");

                System.out.println("[" + browser + "] Page Title: " + driver.getTitle());
                writer.println(LocalDateTime.now() + " - [" + browser + "] Page Title: " + driver.getTitle());

                driver.quit();

            } catch (Exception e) {
                System.err.println("[" + browser + "] Failed to run test: " + e.getMessage());
                writer.println(LocalDateTime.now() + " - [" + browser + "] Failed to run test: " + e.getMessage());
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        }
        writer.close();
        System.exit(0);
    }
}