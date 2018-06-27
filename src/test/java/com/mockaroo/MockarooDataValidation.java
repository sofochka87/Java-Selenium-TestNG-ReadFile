package com.mockaroo;
/*
	• Goto https://mockaroo.com/
	• Specify Field names, Type, Rows count, format
	• Download data in CSV format
	• Read all data from the file and load to collection
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MockarooDataValidation {
	WebDriver driver;

	@BeforeClass
	public void setUp() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	}

	public static boolean isPresent(WebDriver driver, By locator) {
		try {
			driver.findElement(locator);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Test(priority = 1)
	public void test() {

		driver.get("https://mockaroo.com/");
		driver.getTitle();
		Assert.assertTrue(driver.getTitle()
				.contains("Mockaroo - Random Data Generator and API Mocking Tool | JSON / CSV / SQL / Excel"));
		String brand = driver.findElement(By.xpath("//div[@class='brand']")).getText();
		Assert.assertTrue(brand.equalsIgnoreCase("Mockaroo"));
		String tagLine = driver.findElement(By.xpath("//div[@class='tagline']")).getText();
		Assert.assertTrue(tagLine.equalsIgnoreCase("realistic data generator"));
		List<WebElement> elements = driver
				.findElements(By.xpath("//a[@class='close remove-field remove_nested_fields']"));

		for (WebElement webElement : elements) {
			webElement.click();
		}

		Assert.assertTrue(driver.findElement(By.xpath("//div[@class='column column-header column-name']")).getText()
				.equalsIgnoreCase("Field name"));
		Assert.assertTrue(driver.findElement(By.xpath("//div[@class='column column-header column-type']")).getText()
				.equalsIgnoreCase("Type"));
		Assert.assertTrue(driver.findElement(By.xpath("//div[@class='column column-header column-options']")).getText()
				.equalsIgnoreCase("Options"));

		Assert.assertTrue(driver.findElement(By.xpath("//a[contains(text(),'Add another field')]")).isEnabled());
		Assert.assertTrue(driver.findElement(By.xpath("//input[@value='1000']")).getAttribute("value").equals("1000"));

		Select format = new Select(driver.findElement(By.xpath("//select[@id='schema_file_format']")));
		Assert.assertTrue(format.getFirstSelectedOption().getText().equalsIgnoreCase("csv"));

		Select lineEnding = new Select(driver.findElement(By.xpath("//select[@id='schema_line_ending']")));
		Assert.assertTrue(lineEnding.getFirstSelectedOption().getText().equalsIgnoreCase("Unix (LF)"));

		Assert.assertTrue(driver.findElement(By.xpath("//input[@id='schema_include_header']")).isSelected());
		Assert.assertFalse(driver.findElement(By.xpath("//input[@id='schema_bom']")).isSelected());

	}

	@Test(priority = 2)
	public void create() throws InterruptedException {
		driver.findElement(By.xpath("//a[contains(text(),'Add another field')]")).click();
		driver.findElement(By.xpath("(//input[@placeholder='enter name...'])[7]")).sendKeys("City");
		driver.findElement(By.xpath("(//input[@class='btn btn-default'])[7]")).click();

		Assert.assertTrue(isPresent(driver, (By.xpath("//h3[@class='modal-title']"))));
		WebElement web = driver.findElement(By.xpath("//input[@id='type_search_field']"));
		Thread.sleep(3000);
		web.sendKeys("City");
		driver.findElement(By.xpath("//div[@class='type-name']")).click();
		// ------------------------------------------------------------------
		Thread.sleep(1000);

		driver.findElement(By.xpath("//a[contains(text(),'Add another field')]")).click();
		driver.findElement(By.xpath("(//input[@placeholder='enter name...'])[8]")).clear();
		driver.findElement(By.xpath("(//input[@placeholder='enter name...'])[8]")).sendKeys("Country");
		driver.findElement(By.xpath("(//input[@class='btn btn-default'])[8]")).click();
		Thread.sleep(1000);
		Assert.assertTrue(isPresent(driver, (By.xpath("//h3[@class='modal-title']"))));
		WebElement web2 = driver.findElement(By.xpath("//input[@id='type_search_field']"));
		Thread.sleep(3000);
		web2.sendKeys("Country");
		driver.findElement(By.xpath("//div[@class='type-name']")).click();

		Thread.sleep(1000);

		driver.findElement(By.xpath("//button[@id='download']")).click();

		Thread.sleep(5000);
	}

	@Test(priority = 3)
	public void runner() throws IOException {

		// 17. Open the downloaded file using BufferedReader.

		List<String> countries = new ArrayList<>();
		FileReader fr = new FileReader("C:\\Users\\pizza\\Downloads\\MOCK_DATA.csv");
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		while ((line = br.readLine()) != null) {
			countries.add(line);
		}
		br.close();
		fr.close();
		deleteFile();

		// 18. Assert that first row is matching with Field names that we selected.

		String[] arr = countries.get(0).split(",");
		System.out.println(arr[0] + " " + arr[1]);

		Assert.assertTrue((arr[0] + " " + arr[1]).equals("City Country"));

		countries.remove(0);

		// 19. Assert that there are 1000 records

		Assert.assertTrue(countries.size() == 1000);

		// 20. Add all countries to Countries ArrayList

		List<String> countriesList = new ArrayList<>();
		for (int i = 1; i < countries.size(); i++) {
			String[] arr1 = countries.get(i).split(",");
			countriesList.add(arr1[1]);
		}

		// 21. From file add all Cities to Cities ArrayList

		List<String> citiesList = new ArrayList<>();
		for (int i = 1; i < countries.size(); i++) {
			String[] arr1 = countries.get(i).split(",");
			citiesList.add(arr1[0]);
		}

		// 22. Sort all cities and find the city with the longest name and shortest name
		Collections.sort(citiesList, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.length() - s2.length();
			}
		});
		System.out.println();
		String longCity = citiesList.get(citiesList.size() - 1);
		int MAX = longCity.length();

		String shortCity = citiesList.get(0);
		int MIN = shortCity.length();

		System.out.println("Long city name length: " + longCity + "  " + MAX);
		System.out.println("Short city name length: " + shortCity + " " + MIN);

		// 23. In Countries ArrayList, find how many times each Country is mentioned.
		// and print out

		System.out.println("Countries frequency by cities: ");
		HashSet<String> Countries = new HashSet<>(countriesList);
		for (String country : Countries) {
			System.out.println(country + "-" + Collections.frequency(countriesList, country));
		}

		// 24. From file add all Cities to citiesSet HashSet

		HashSet<String> citySet = new HashSet<>(citiesList);

		// 25. Count how many unique cities are in Cities ArrayList and ...

		List<String> uniqueCities = new ArrayList<>();
		for (String city : citiesList) {
			if (!uniqueCities.contains(city))
				uniqueCities.add(city);
		}

		// .. assert that it is matching with the count of citiesSet HashSet.

		Assert.assertEquals(uniqueCities.size(), citySet.size());
		System.out.println("uniqueCities numbers: " + uniqueCities.size());
		System.out.println("citySet numbers: " + citySet.size());

		// 26. Add all Countries to countrySet HashSet

		HashSet<String> countrySet = new HashSet<>(countriesList);

		// 27. Count how many unique countries are in Countries ArrayList ...

		List<String> uniqueCountries = new ArrayList<>();
		for (String country : countriesList) {
			if (!uniqueCountries.contains(country))
				uniqueCountries.add(country);
		}

		// ...and assert that it is matching with the count of countrySet HashSet.

		Assert.assertEquals(uniqueCountries.size(), countrySet.size());
		System.out.println("uniqueCountries number : " + uniqueCountries.size());
		System.out.println("Countries number: " + countrySet.size());
	}

	public void deleteFile() {

		File file = new File("C:\\Users\\Zghiban\\Downloads\\MOCK_DATA.csv");

		if (file.delete()) {
			System.out.println(file.getName() + " is deleted!");
		} else {
			System.out.println("Delete operation is failed.");
		}
	}

	@AfterClass
	public void clean() throws InterruptedException {
		Thread.sleep(3000);
		driver.close();
	}

}
