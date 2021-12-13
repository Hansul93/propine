package crypto;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Wrapper {

	protected static RemoteWebDriver driver;
	WebElement element;

	/**
	 * Invoke Chrome
	 * 
	 */
	public void invokeChrome() {
			System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().window().maximize();
			driver.manage().deleteAllCookies();
		}
	/**
	 * Type string into element
	 * 
	 * @param elementelement
	 * @param sstring
	 */
	public void typeIntoByXpath(String locator, String s) {
		driver.findElement(By.xpath(locator)).clear();
		driver.findElement(By.xpath(locator)).sendKeys(s);
	}

	/**
	 * Click on a element
	 * 
	 * @param element
	 */
	protected void clickElementByXpath(String locator) {
		driver.findElement(By.xpath(locator)).click();
	}

	/**
	 * To get the text of an element
	 * 
	 * @param element
	 * @return
	 */
	public String returnTextByXpath(String locator) {
		String text = driver.findElement(By.xpath(locator)).getAttribute("value").replace(",", "");
		return text;
	}

	/**
	 * To scroll to specific element
	 */
	public void scrollToView(String locator) {
		element = driver.findElement(By.xpath(locator));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	/**
	 * To scroll and click on specific element
	 */
	public void scrollAndClick(String locator) {
		element = driver.findElement(By.xpath(locator));
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", element);
	}
}
