package runner;


import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;


@RunWith(Cucumber.class)
@CucumberOptions(strict = true, monochrome = true,
        features = "src/test/resources/features/SampleTest.feature",
        glue = {"stepdefinitions"},
        stepNotifications = true,
        tags = "@UseTable",
        plugin = {
//        "pretty",
                "junit:target/junitreport.xml","json:target/jsonreport.json","html:reports/cucumber-reports.html","utilities.CucumberLogger"}
        
)
public class trSampleTest {

    private trSampleTest() {
        
    }
	 
}

