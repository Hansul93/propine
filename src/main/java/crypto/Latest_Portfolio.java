package crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.opencsv.exceptions.CsvValidationException;

public class Latest_Portfolio extends Wrapper {

	BufferedReader bufRdr;
	static String CSV_PATH = "./transactions.csv";
	String[] csvCell;
	Properties prop;
	String USDamount = null;
	float f = 0;

	public Latest_Portfolio() {
		prop = new Properties();
		try {
			prop.load(new FileInputStream(new File("./object.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@BeforeTest
	public void launchCryptoExchangerApplication() {
		invokeChrome();
		driver.get("https://www.coindesk.com/calculator");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
	}

	@Test
	public void dataRead_CSV()
			throws IOException, CsvValidationException, NumberFormatException, InterruptedException, ParseException {

		String line;
		float depositAmount = 0;
		float withdrawalAmount = 0;
		Path path = Paths.get(CSV_PATH);
		File file = new File(CSV_PATH);
		long lines = 0;
		bufRdr = new BufferedReader(new FileReader(file));
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter("./output.csv"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		lines = Files.lines(path).count();
		int dateCount = (int) lines;
		String[] dateArray = new String[dateCount];
		System.out.println("Number of Rows present: " + lines);
		for (int i = 0; i < lines; i++) {
			while ((line = bufRdr.readLine()) != null) {
				String[] cell = line.split(",");
				String timeMS = cell[0].replace("\"", "");
				String transactType = cell[1];
				String token = cell[2];
				String amount = cell[3].replace("\"", "");
				long num = Long.parseLong(timeMS);
				Timestamp stamp = new Timestamp(num);
				Date date = stamp;

				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
				formatter.setTimeZone(TimeZone.getDefault());
				String strDate = formatter.format(date);

				if (token.equals("BTC")) {
					conversionUSDAmount("btc.token.xpath", amount);

				} else if (token.equals("ETH")) {
					conversionUSDAmount("eth.token.xpath", amount);
				} else if (token.equals("XRP")) {
					conversionUSDAmount("xrp.token.xpath", amount);
				} else {
					System.out.println(token + "is not a match");
				}

				System.out.print(strDate + ", ");
				System.out.print(transactType + ", ");
				System.out.print(token + ", ");
				System.out.print(amount + ", ");
				System.out.print(USDamount);
				System.out.println();

				out.println(strDate + "," + transactType + "," + token + "," + amount + "," + USDamount);

				dateArray[i] = strDate;

				if (transactType.equals("DEPOSIT")) {
					depositAmount = depositAmount + f;
				}

				if (transactType.equals("WITHDRAWAL")) {
					withdrawalAmount = withdrawalAmount + f;
				}
			}
		}
		Arrays.sort(dateArray, Collections.reverseOrder());
		System.out.println(Arrays.toString(dateArray));
	
		float LatestPortfolio = depositAmount - withdrawalAmount;
		System.out.println("Total Deposit Amount: " + depositAmount + " USD");
		System.out.println("Total Withdrawl Amount: " + withdrawalAmount + " USD");
		System.out.println("Balance Amount: " + LatestPortfolio + " USD");
		out.close();
	}

	@AfterTest
	public void closeOpenConnections() throws IOException {
		driver.quit();
		bufRdr.close();
	}

	public void conversionUSDAmount(String tokenType, String amount) {
		scrollToView(prop.getProperty("token.amount.xpath"));
		scrollAndClick(prop.getProperty("token.dropdown.xpath"));
		scrollAndClick(prop.getProperty(tokenType));
		typeIntoByXpath(prop.getProperty("usd.amount.xpath"), amount);
		USDamount = returnTextByXpath(prop.getProperty("token.amount.xpath"));
		f = Float.parseFloat(USDamount);
	}
}