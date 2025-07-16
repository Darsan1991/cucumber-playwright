package stepdefinitions;


import attributes.Handler;
import attributes.Host;
import attributes.Inject;
import attributes.Validator;
import com.microsoft.playwright.*;

import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import models.HandlerInfo;
import models.ThreadLocal;
import models.ValidatorInfo;
import pages.*;
import reflection.*;
import reflection.base.LoggerService;
import utils.ColorUtils;
import utils.MethodUtils;
import utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class steps extends BaseStep {


    private static Page _page;
    @Inject
    LoginPage loginPage;
    @Inject
    ItemsPage itemsPage;
    @Inject
    CheckoutPage checkoutPage;

    @Inject
    HostService hostService;
    @Inject
    steps steps;

    @Inject
    HandlerService handlerService;
    
    @Inject
    LoggerService logger;

    @Host
    public static Page page() {
        if (_page == null || _page.isClosed()) {
            _page = createPlaywrightPageInstance("Chromium");
            threadPage.set(_page);
        }

        return _page;
    }

    @Inject
    Page page;

    @Host
    public static ThreadLocal<Page> threadPage = new ThreadLocal<>();

    @Given("User launched SwagLabs application")
    public void user_launched_swaglabs_application(List<Map<String, String>> table) {


        try {
            page.navigate("https://innovation-fun-3318.lightning.force.com/");
  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Given("Navigate to url {string}")
    public void navigateToUrl(String url) {


        try {
            page.navigate(url);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @When("User logged in the app using username {locator} and password {string}{wait}{ifCondition}")

    public void user_logged_in_the_app_using_username_and_password(Locator hello, String password, int wait, boolean success) {

        Optional<HandlerInfo> checkbox = handlerService.get("dropdown");

        Optional<Page> page = Objects.requireNonNull(Services.get(HostService.class)).getByType(Thread.currentThread().threadId(), Page.class);
        loginPage.login("", password);
    }

    @Then("^user should be able to log in$")
    public void logInSuccessful() {
        itemsPage.loginSuccessful();
    }

    @Given("^the user logs in with username \"([^\"]*)\"$")
    public void login(String username, DataTable table) {
        Map<String, String> map = new HashMap<>();

        if (table != null && !table.isEmpty()) {
            map = table.asMap(String.class, String.class);
        }
        String password = map.getOrDefault("password", "default");
        System.out.println("Logging in: " + username + " with password: " + password);
    }

    @Then("^User should not get logged in$")
    public void logInFailed() {
        loginPage.loginFailed();
    }

    @When("User adds {string} product to the cart")
    public void user_adds_product_to_the_cart(String product) {
        itemsPage.orderProduct(product);
    }

    @When("User enters Checkout details with {string}, {string}, {string}")
    public void user_enters_Checkout_details_with(String FirstName, String LastName, String Zipcode) {
        checkoutPage.fillCheckoutDetails(FirstName, LastName, Zipcode);
    }

    @When("User completes Checkout process")
    public void user_completes_checkout_process() {
        checkoutPage.completeCheckout();
    }

    @Then("User should get the Confirmation of Order")
    public void user_should_get_the_Confirmation_of_Order() {
        checkoutPage.checkoutSuccessful();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (browser != null) {
            browser.close();
        }
        if (page != null) {
            page.close();
        }
    }


    public static void main(String[] args) {

        Object itemPage = Services.get(CreationService.class).create(ItemsPage.class);
        Services.get(ExecuteService.class).get("sampleExecute");
//		Optional<Page> page = Services.get(HostService.class).getByType(Thread.currentThread().threadId(), Page.class);
    }

   @Handler 
    public void fillTextHandler(Locator locator, String value) {
        locator.fill(value);
    }

    @Handler
    public boolean textHandler(Locator locator, String value) {
        locator.fill(value);
        return true;
    }

    @Handler
    public static void dropdownHandler(Locator locator, String value) {

        locator.selectOption(value);

    }

    @Handler
    public static Locator checkboxHandler(Locator locator, boolean value) {
        if (value) {
            locator.click();
        } else {
            locator.uncheck();
        }

        return null;
    }

    @Handler
    public static boolean radioHandler(Locator locator, String value) {
        locator.selectOption(value);
        return true;
    }

    @Handler
    public static boolean foldHandler(Locator locator, String value) {
        locator.click();
        return true;
    }

    @Host
    public  Locator heading(String val){
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(val));
    }
    @Host
    public  Locator text(String val){
        return page.getByText(val);
    }
    
//    
//    public Locator frame(String val)
//    {
//        
//    }
    
    @Host
    public static String link = "//a[contains(normalize-space(),'%s')]";
    @Host
    public static String button = "//button[contains(normalize-space(),'%s')]";
    @Host
    public Locator input(String label) {
        return page.getByLabel(label)
                .or(page.getByPlaceholder(label))
                .or(page.getByRole(AriaRole.TEXTBOX,new Page.GetByRoleOptions().setName(label)));
    }
    
    @Host
    public Locator clickableLocator(String label) {
        return page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName(label)).or(page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName(label))).or(page.locator("//input[@value='%s']".formatted(label)));
    }
    
    @Host
    public Locator formLocator(String label) {
//        return page.getByRole(AriaRole.FORM,new Page.GetByRoleOptions().setName(label));
        return page.locator("//form[.//*[contains(normalize-space(),'%s')]]".formatted(label));
    }
    
    @Host
    public Locator textBox(String label) {
        return page.getByLabel(label);
    }

    @Host
    public Locator selectLocator(String label) {
        return page.getByLabel(label).or(page.getByPlaceholder(label));
    }
    @Host
    public Locator checkBox(String label) {
        return page.getByRole(AriaRole.CHECKBOX,new Page.GetByRoleOptions().setName(label));
    }
    @Validator
    public static void notVisibleValidator(Locator locator, float wait) {
        locator.waitFor(new Locator.WaitForOptions().setTimeout(wait * 1000).setState(WaitForSelectorState.HIDDEN));
    }

    @Validator
    public static void visibleValidator(Locator locator, float wait) {
        locator.last().waitFor(new Locator.WaitForOptions().setTimeout(wait * 1000).setState(WaitForSelectorState.VISIBLE));
    }


    @Validator
    public static void errorTextValidator(Locator locator, float wait) {
      visibleValidator(locator, wait);
        String val = locator.evaluate("element => window.getComputedStyle(element).color").toString();

        List<Integer> color = StringUtils.getAllTextByRegex(val, "\\d+").stream().map(Integer::parseInt).toList();
        if (!ColorUtils.isRed(color.get(0), color.get(1), color.get(2))) {
            throw new RuntimeException("Color is not red");
        }
    }
    @Validator
    public static void successText(Locator locator, float wait) {
        visibleValidator(locator, wait);
        String val = locator.evaluate("element => window.getComputedStyle(element).color").toString();

        List<Integer> color = StringUtils.getAllTextByRegex(val, "\\d+").stream().map(Integer::parseInt).toList();
        if (!ColorUtils.isGreen(color.get(0), color.get(1), color.get(2))) {
            throw new RuntimeException("Color is not red");
        }
    }
    @Validator
    public static void hyperLinkValidator(Locator locator, float wait) {
        Locator loc = locator.and(locator.page().locator("a").or(locator.page().locator("button")));
        visibleValidator(loc, wait);
        loc.hover();
        page().waitForTimeout(1000);
        loc.evaluate("element => window.getComputedStyle(element).cursor").toString().equalsIgnoreCase("pointer");
    }

    @Validator
    public static void clickableValidator(Locator locator, float wait) {

        visibleValidator(locator, wait);
        if (!locator.isEnabled()) {
            throw new RuntimeException("Element is not enabled");
        }
//        locator.click();
    }

    @Validator
    public static void editable(Locator locator, float wait) {
        visibleValidator(locator, wait);
        if (!locator.isEditable()) {
            throw new RuntimeException("Element is not editable");
        }
//        locator.click();
    }


    @Validator
    public static void notHyperLink(Locator locator, float wait) {
        notVisibleValidator(locator.and(locator.page().locator("a").or(locator.page().locator("button"))), wait);

    }

    @And("Validate {locator} using {validator} validator{timeout}{wait}")
    public void validateUsingValidator(Locator locator, ValidatorInfo validatorInfo, float timeout, float wait) throws InvocationTargetException, IllegalAccessException {
        validatorInfo.method.invoke(validatorInfo.object, locator, timeout);
        page.waitForTimeout(wait * 1000);
        logger.logSuccess("Validate "+locator+" using "+validatorInfo.method.getName()+" validator");
    }

    @And("Fill {locator}{inside} with {string} using {handler} handler{wait}{optional}")
    public void fillUsingHandler(Locator locator,Locator parent,String value, HandlerInfo handlerInfo,float wait,boolean optional) throws InvocationTargetException, IllegalAccessException {

        if (parent!=null) { locator = parent.locator(locator);}

        Locator finalLocator = locator;
        MethodUtils.tryRun(()->{
            try {
                handlerInfo.method.invoke(handlerInfo.object, finalLocator, value);
            } 
            catch (Exception ignored) {}
            page.waitForTimeout(wait * 1000);
          logger.logSuccess("Fill " + finalLocator + " with " + value + " using " + handlerInfo.method.getName() + " handler");
      }, !optional);
    }

    @And("Click on {locator}{inside}{clickType}{timeout}{optional}{wait}")
    public void clickOn(Locator locator,Locator parent,String type,float timeout,boolean optional,int wait) throws InvocationTargetException, IllegalAccessException {
        if (parent!=null) { locator = parent.locator(locator);}
        Locator finalLocator = locator;
        MethodUtils.tryRun(()->{
            if(type == null || type.trim().equalsIgnoreCase("normal") || type.trim().equalsIgnoreCase("force")) {
                
            finalLocator.click(new Locator.ClickOptions().setTimeout(timeout * 1000).setForce(type != null && type.trim().equalsIgnoreCase("force")));
            }
            else if(type.trim().equalsIgnoreCase("js")) {
                finalLocator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout * 1000));
                finalLocator.evaluate("element => element.click()");
            }
            
            page.waitForTimeout(wait * 1000);
            logger.logSuccess("Click on " + finalLocator);
        },!optional);
     
    }

    @And("Waiting for {locator}{timeout}{optional}")
    public void waitingFor(Locator locator,float timeout,boolean optional) {

        MethodUtils.tryRun(()->{
            page.waitForCondition(() -> {
                try {
                    return locator.count() > 0 && locator.all().stream().anyMatch(Locator::isVisible);
                } catch (Exception e) {
                    return false;
                }
            }, new Page.WaitForConditionOptions().setTimeout(timeout * 1000));
        }, !optional);
    }

    @And("Waiting for {locator} to dismiss{timeout}{optional}")
    public void waitingForDismiss(Locator locator,float timeout,boolean optional) {
        MethodUtils.tryRun(()->{
            page.waitForCondition(()-> {
                try {

                    return locator.count() == 0 ||locator.all().stream().noneMatch(Locator::isVisible);
                }
                catch (Exception e) {
                    return false;
                }
            },new Page.WaitForConditionOptions().setTimeout(timeout * 1000));
        },!optional);
        
        logger.logSuccess("Waiting for "+locator);
    }

    @And("Waiting for {loadState} state")
    public void waitingForState(LoadState state) {
        page.waitForLoadState(state);
    }

    @And("Fill the values for fields")
    public void fillTheValuesForFields(List<Map<String, String>> table) {
        table.forEach(map -> {
            Locator locator = CustomParameters.getLocator(map.get("Field"));
            Optional<HandlerInfo> handler = handlerService.get(map.get("Handler"));
            String value = map.get("Value");

            HandlerInfo handlerInfo = handler.get();
            try {
                handlerInfo.method.invoke(handlerInfo.object,locator,value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        });
    }
}
