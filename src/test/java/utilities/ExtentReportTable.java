package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.Test;

public class ExtentReportTable {

    @Test
    public void addTableToExtentReport() {
        // Initialize ExtentReports and ExtentSparkReporter
        ExtentSparkReporter spark = new ExtentSparkReporter("extent.html");
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(spark);

        // Create a test
        ExtentTest test = extent.createTest("Table Test");

        // Define the table data
        String[][] data = new String[2][2];
        data[0][0] = "Header 1";
        data[0][1] = "Header 2";
        data[1][0] = "Data 1";
        data[1][1] = "Data 2";
        

        // Create the table markup
        Markup m = MarkupHelper.createTable(data);

        String markup = m.getMarkup();
        System.out.println(markup);
        
        String fullHtml = "<p>%s</p>".formatted("Testing table markup")+markup; 

        // Log the table to the report
        test.pass(()-> fullHtml);

        // Create a test
         test = extent.createTest("Table Test 1");
         test.pass("Table Test Passed");


        // Flush the report
        extent.flush();
    }
    

}