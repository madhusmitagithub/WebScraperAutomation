package demo;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class wrapper {
    //private static ChromeDriver driver;
    private static WebDriverWait wait;

    public static boolean navigate(WebDriver driver, String url){
        try {
            if (!driver.getCurrentUrl().equals(url)) {
                driver.navigate().to(url);
            }
            return driver.getCurrentUrl().equals(url);
        } catch (Exception e) {
                return false;
        }
    }

    public static List<HashMap<String, String>> hockeyTeamDetails(WebDriver driver){
        List<HashMap<String, String>> results = new ArrayList<>();
        try{
            //driver.get("https://www.scrapethissite.com/pages/");
            navigate(driver, "https://www.scrapethissite.com/pages/");

            // Click on the "Hockey Teams: Forms, Searching and Pagination" link
            WebElement hockeyTeamsElement = driver.findElement(By.partialLinkText("Hockey Teams"));
            hockeyTeamsElement.click();

            //wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("table")));
            Thread.sleep(2000);
        
            int pageCount = 0;

            while(pageCount < 4){
                //Locate the table
                //WebElement table = driver.findElement(By.xpath("//table[@class='table']"));

                // Locate the rows of the table--//table/tbody/tr[@class='team'] ---//tr[@class='team']
                List<WebElement> rows = driver.findElements(By.cssSelector("tr.team"));
                // Iterate through rows and collect data
                for (WebElement row : rows){
                    WebElement teamElement = row.findElement(By.cssSelector("td.name"));
                    String teamName = teamElement.getText();
                    String year = row.findElement(By.cssSelector("td.year")).getText();
                    String winPercentageText = row.findElement(By.cssSelector("td.pct")).getText();
                    //System.out.println("The details are>>>>>>>>>>>>>>>>>>> :" + teamName);
                    try {
                        float winPercentage = Float.parseFloat(winPercentageText);
                        //if Win % less than 40% (0.40), store it in a HashMap
                        if(winPercentage < 0.40){
                            
                            HashMap<String, String> teamDetails = new HashMap<>();
                            teamDetails.put("EpochTime", String.valueOf(System.currentTimeMillis()));
                            teamDetails.put("Team", teamName);
                            teamDetails.put("Year", year);
                            teamDetails.put("WinPercentage", winPercentageText);
                            results.add(teamDetails);
                            //System.out.println("Details added>>>>>>>>>>>>>>>>>>>>>");
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                // Move to the next page
                if(pageCount < 3){
                    WebElement nextPageButton = driver.findElement(By.xpath("//a[@aria-label='Next']"));
                    nextPageButton.click();
                    //wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//div[@class='container'])[2]")));
                    Thread.sleep(3000);
                }
                pageCount++;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return results;

    }

    public static List<HashMap<String, String>> oscarWinningFilms(WebDriver driver) {
        List<HashMap<String, String>> results = new ArrayList<>();
        try {
            navigate(driver, "https://www.scrapethissite.com/pages/");

            // Click on the "Oscar Winning Films" link
            WebElement oscarWinningLink = driver.findElement(By.xpath("//a[@href='/pages/ajax-javascript/']"));
            oscarWinningLink.click();
            Thread.sleep(2000);
            //wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='container']/div[4]")));

            // Find all year links
            List<WebElement> yearLinks = driver.findElements(By.className("year-link"));

            // Iterate over each year
            for(int i = 0; i < yearLinks.size(); i++){
                yearLinks = driver.findElements(By.className("year-link"));
                WebElement yearLink = yearLinks.get(i);
                String year = yearLink.getText();
                yearLink.click();
                Thread.sleep(4000);

                // Get the top 5 movies
                List<WebElement> topFiveMovies = driver.findElements(By.xpath("//tbody/tr/td[@class='film-title']"));
                Thread.sleep(3000);
                
                for (int j = 0; j < Math.min(5, topFiveMovies.size()); j++) {
                    topFiveMovies = driver.findElements(By.xpath("//tbody/tr"));
                    WebElement movieRow = topFiveMovies.get(j);
                    String movieTitle = movieRow.findElement(By.className("film-title")).getText();
                    String nomination = movieRow.findElement(By.className("film-nominations")).getText();
                    String awards = movieRow.findElement(By.className("film-awards")).getText();
                    boolean isWinner = (j == 0);  
                    //System.out.println("Scraping movie from year >>>>>>" + year + " and title >>>>>: " + movieTitle);

                    HashMap<String, String> movieDetails = new HashMap<>();
                    movieDetails.put("EpochTime", String.valueOf(Instant.now().getEpochSecond()));
                    movieDetails.put("Year", year);
                    movieDetails.put("Title", movieTitle);
                    movieDetails.put("Nomination", nomination);
                    movieDetails.put("Awards", awards);
                    movieDetails.put("isWinner", String.valueOf(isWinner));
                    results.add(movieDetails);

                }


                // Navigate back to the list of years
                driver.navigate().back();
                Thread.sleep(2000);
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }


public static void writeResultsToJson(List<HashMap<String, String>> results, String outputDirectory, String fileName) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    try {
        Path outputPath = Paths.get(outputDirectory, fileName);
        Files.createDirectories(outputPath.getParent());
        mapper.writeValue(outputPath.toFile(), results);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}
