package utilities;

import attributes.Inject;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.Page;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.*;
import reflection.HostService;
import reflection.Services;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class CucumberLogger implements EventListener {

   

    private ExtentReports extent;
    private ExtentTest test;

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestRunStarted.class, event -> {
            ExtentSparkReporter spark = new ExtentSparkReporter("extent.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        });
        eventPublisher.registerHandlerFor(TestCaseStarted.class, event -> {
             test = extent.createTest(event.getTestCase().getName());

            System.out.println("Test Case Started: " + event.getTestCase().getName());
        });
        eventPublisher.registerHandlerFor(TestStepFinished.class, testStepFinished -> {
            if (testStepFinished.getTestStep() instanceof PickleStepTestStep pickleStepTestStep) {
                System.out.println(pickleStepTestStep.getStep().getText());

                Optional<Page> page = Services.get(HostService.class).getByType(Page.class);
                String base64 = Base64.getEncoder().encodeToString(page.get().screenshot());
                Media media = MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build();
                StepArgument argument = pickleStepTestStep.getStep().getArgument();

                Status status = switch (testStepFinished.getResult().getStatus()) {
                    case PASSED -> Status.PASS;
                    case SKIPPED -> Status.SKIP;
                    case PENDING -> Status.WARNING;
                    case UNDEFINED -> Status.INFO;
                    case AMBIGUOUS -> Status.INFO;
                    case FAILED -> Status.FAIL;
                    case UNUSED -> Status.WARNING;
                };
                if(argument instanceof DataTableArgument)
                {

                    List<List<String>> cells = ((DataTableArgument) argument).cells();
                    String[][] data = cells.stream().map(i -> i.toArray(new String[0])).toArray(String[][]::new);
                    System.out.println(cells);
                    Markup markUp = createTableWithInfo(pickleStepTestStep.getStep().getText(), data);
                    test.log(status, markUp.getMarkup(),media  );
                    
                }
                else {
                    test.log(status, pickleStepTestStep.getStep().getText(),media );
                }
                

            }
        });
        
        eventPublisher.registerHandlerFor(TestRunFinished.class, testCaseFinished -> {
            extent.flush();
        });
    }


    public static Markup createTableWithInfo(String info, String[][] data)
    {
        Markup m = MarkupHelper.createTable(data);
        String markup = m.getMarkup();
        String fullHtml = "<p>%s</p>".formatted(info)+markup;
        return ()-> fullHtml;
    }
}
