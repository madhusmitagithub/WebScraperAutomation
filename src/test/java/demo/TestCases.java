package demo;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

//import demo.wrapper;

public class TestCases {
    static ChromeDriver driver;
    private static WebDriverWait wait;
    
    @BeforeSuite
    public void browserSetUp(){
        System.out.println("Constructor: TestCases");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        
    }
    
    @AfterSuite
    public void endTest()
    {
        System.out.println("End Test: TestCases");
        driver.close();
        driver.quit();

    }

    @Test
    public void testCase01(){
        List<HashMap<String, String>> results = wrapper.hockeyTeamDetails(driver);

        for (HashMap<String, String> result : results) {
            System.out.println(result);
            
            Assert.assertTrue(Float.parseFloat(result.get("WinPercentage")) < 0.40, "The Win % is not less than 40%");
        }
        
        // Write results to JSON file in the output folder
        String outputDirectory = "output";
        String fileName = "hockey-team-data.json";
        wrapper.writeResultsToJson(results, outputDirectory, fileName);

        // Assert that the file exists and is not empty
        File outputFile = new File(outputDirectory, fileName);
        Assert.assertTrue(outputFile.exists(), "JSON file does not exist in the output folder");
        Assert.assertTrue(outputFile.length() > 0, "JSON file is empty");
    }

    @Test
    public void testCase02() {
        List<HashMap<String, String>> results = wrapper.oscarWinningFilms(driver);

        // Print and optionally assert results
        for (HashMap<String, String> result : results) {
            System.out.println(result);
            
        }

        // Write results to JSON file in the output folder
        String outputDirectory = "output";
        String fileName = "oscar-winner-data.json";
        wrapper.writeResultsToJson(results, outputDirectory, fileName);

        // Assert that the file exists and is not empty
        File outputFile = new File(outputDirectory, fileName);
        Assert.assertTrue(outputFile.exists(), "JSON file does not exist in the output folder");
        Assert.assertTrue(outputFile.length() > 0, "JSON file is empty");
    }

}
