package stepdefinitions;


import attributes.*;
import attributes.Comparator;
import com.microsoft.playwright.*;

import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.PendingException;
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
import utils.LocatorUtils;
import utils.MethodUtils;
import utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
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

    @Type(name = "Handlers")
    @Type(name = "Validators")
    @Type(name = "Locators")
    @Given("User launched SwagLabs application")
    public void user_launched_swaglabs_application(List<Map<String, String>> table) {


        try {
            page.navigate("https://innovation-fun-3318.lightning.force.com/");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Given("Navigate to url {dataString}")
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
    public void fillText(Locator locator, String value) {
        locator.fill(value);
    }

    @Handler
    public boolean text(Locator locator, String value) {
        locator.fill(value);
        return true;
    }

    @Handler
    public static void dropdown(Locator locator, String value) {

        locator.selectOption(value);

    }

    @Handler
    public static Locator checkbox(Locator locator, boolean value) {
        if (value) {
            locator.click();
        } else {
            locator.uncheck();
        }

        return null;
    }

    @Handler
    public static boolean radio(Locator locator, String value) {
        locator.selectOption(value);
        return true;
    }

    @Handler
    public static boolean fold(Locator locator, String value) {
        locator.click();
        return true;
    }

    @Host
    public Locator heading(String val) {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(val));
    }

    @Host
    public Locator text(String val) {
        return page.getByText(val);
    }

    @Host
    public Locator byText(String val) {
        return page.getByText(val);
    }


    @Host
    public Locator byRole(String role, String val) {
        return page.getByRole(AriaRole.valueOf(role.toUpperCase()), new Page.GetByRoleOptions().setName(val));
    }
//    
//    public Locator frame(String val)
//    {
//        
//    }

    @Host
    public static String link(String name) {
        return "//a[contains(normalize-space(),'%s')]";
    }

    @Host
    public static String button = "//button[contains(normalize-space(),'%s')]";

    @Host
    public Locator input(String label) {
        return page.getByLabel(label)
                .or(page.getByPlaceholder(label))
                .or(page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(label)));
    }

    @Host
    public Locator clickable(String label) {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(label)).or(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(label))).or(page.locator("//input[@value='%s']".formatted(label)));
    }

    @Host
    public Locator form(String label) {
//        return page.getByRole(AriaRole.FORM,new Page.GetByRoleOptions().setName(label));
        return page.locator("//form[.//*[contains(normalize-space(),'%s')]]".formatted(label));
    }

    @Host
    public Locator textBox(String label) {
        return page.getByLabel(label);
    }

    @Host
    public Locator select(String label) {
        return page.getByLabel(label).or(page.getByPlaceholder(label));
    }

    @Host
    public Locator checkBox(String label) {
        return page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName(label));
    }

    @Validator
    public static void notVisible(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
    }

    @Validator
    public static void visible(Locator locator) {
        locator.last().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
    
    


    @Validator
    public static void errorText(Locator locator) {
        visible(locator);
        String val = locator.evaluate("element => window.getComputedStyle(element).color").toString();

        List<Integer> color = StringUtils.getAllTextByRegex(val, "\\d+").stream().map(Integer::parseInt).toList();
        if (!ColorUtils.isRed(color.get(0), color.get(1), color.get(2))) {
            throw new RuntimeException("Color is not red");
        }
    }

    @Validator
    public static void successText(Locator locator) {
        visible(locator);
        String val = locator.evaluate("element => window.getComputedStyle(element).color").toString();

        List<Integer> color = StringUtils.getAllTextByRegex(val, "\\d+").stream().map(Integer::parseInt).toList();
        if (!ColorUtils.isGreen(color.get(0), color.get(1), color.get(2))) {
            throw new RuntimeException("Color is not red");
        }
    }

    @Validator
    public static void hyperLink(Locator locator) {
        Locator loc = locator.and(locator.page().locator("a").or(locator.page().locator("button")));
        visible(loc);
        loc.hover();
        page().waitForTimeout(1000);
        loc.evaluate("element => window.getComputedStyle(element).cursor").toString().equalsIgnoreCase("pointer");
    }

    @Validator
    public static void clickable(Locator locator) {

        visible(locator);
        if (!locator.isEnabled()) {
            throw new RuntimeException("Element is not enabled");
        }
//        locator.click();
    }

    @Validator
    public static void editable(Locator locator) {
        visible(locator);
        if (!locator.isEditable()) {
            throw new RuntimeException("Element is not editable");
        }
//        locator.click();
    }
    
    @Validator
    public static void inputValue(Locator locator, String value) {
        locator = !LocatorUtils.tag(locator).equalsIgnoreCase("input") ? locator.locator("//input"):locator;
        assert locator.inputValue().equals(value);
    }


    @Validator
    public static void notHyperLink(Locator locator) {
        notVisible(locator.and(locator.page().locator("a").or(locator.page().locator("button"))));

    }

    @And("Validate {locator} with value {resultString} using {validator} validator{timeout}{wait}")
    public void validateUsingValidator(Locator locator,String value, ValidatorInfo validatorInfo, float timeout, float wait) throws InvocationTargetException, IllegalAccessException {
        validatorInfo.method.invoke(validatorInfo.object, locator, value);
        page.waitForTimeout(wait * 1000);
        logger.logSuccess("Validate " + locator + " using " + validatorInfo.method.getName() + " validator");
    }


    @And("Validate {locator} using {validator} validator{timeout}{wait}")
    public void validateUsingValidator(Locator locator, ValidatorInfo validatorInfo, float timeout, float wait) throws InvocationTargetException, IllegalAccessException {
        validatorInfo.method.invoke(validatorInfo.object, locator, timeout);
        page.waitForTimeout(wait * 1000);
        logger.logSuccess("Validate " + locator + " using " + validatorInfo.method.getName() + " validator");
    }

    @And("Fill {locator}{inside} with {string} using {handler} handler{wait}{optional}")
    public void fillUsingHandler(Locator locator, Locator parent, String value, HandlerInfo handlerInfo, float wait, boolean optional) throws InvocationTargetException, IllegalAccessException {

        if (parent != null) {
            locator = parent.locator(locator);
        }

        Locator finalLocator = locator;
        MethodUtils.tryRun(() -> {
            try {
                handlerInfo.method.invoke(handlerInfo.object, finalLocator, value);
            } catch (Exception ignored) {
            }
            page.waitForTimeout(wait * 1000);
            logger.logSuccess("Fill " + finalLocator + " with " + value + " using " + handlerInfo.method.getName() + " handler");
        }, !optional);
    }

    @And("Click on {locator}{inside}{clickType}{timeout}{optional}{wait}")
    public void clickOn(Locator locator, Locator parent, String type, float timeout, boolean optional, int wait) throws InvocationTargetException, IllegalAccessException {
        if (parent != null) {
            locator = parent.locator(locator);
        }
        Locator finalLocator = locator;
        MethodUtils.tryRun(() -> {
            if (type == null || type.trim().equalsIgnoreCase("normal") || type.trim().equalsIgnoreCase("force")) {

                finalLocator.click(new Locator.ClickOptions().setTimeout(timeout * 1000).setForce(type != null && type.trim().equalsIgnoreCase("force")));
            } else if (type.trim().equalsIgnoreCase("js")) {
                finalLocator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout * 1000));
                finalLocator.evaluate("element => element.click()");
            }

            page.waitForTimeout(wait * 1000);
            logger.logSuccess("Click on " + finalLocator);
        }, !optional);

    }

    @And("Click on {locator} {locator}{inside}{clickType}{timeout}{optional}{wait}")
    public void clickOn(Locator locator, Locator loc, Locator parent, String type, float timeout, boolean optional, int wait) throws InvocationTargetException, IllegalAccessException {
        if (parent != null) {
            locator = parent.locator(locator);
        }
        Locator finalLocator = locator;
        MethodUtils.tryRun(() -> {
            if (type == null || type.trim().equalsIgnoreCase("normal") || type.trim().equalsIgnoreCase("force")) {

                finalLocator.click(new Locator.ClickOptions().setTimeout(timeout * 1000).setForce(type != null && type.trim().equalsIgnoreCase("force")));
            } else if (type.trim().equalsIgnoreCase("js")) {
                finalLocator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout * 1000));
                finalLocator.evaluate("element => element.click()");
            }

            page.waitForTimeout(wait * 1000);
            logger.logSuccess("Click on " + finalLocator);
        }, !optional);

    }

    @And("Waiting for {locator}{timeout}{optional}")
    public void waitingFor(Locator locator, float timeout, boolean optional) {

        MethodUtils.tryRun(() -> {
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
    public void waitingForDismiss(Locator locator, float timeout, boolean optional) {
        MethodUtils.tryRun(() -> {
            page.waitForCondition(() -> {
                try {

                    return locator.count() == 0 || locator.all().stream().noneMatch(Locator::isVisible);
                } catch (Exception e) {
                    return false;
                }
            }, new Page.WaitForConditionOptions().setTimeout(timeout * 1000));
        }, !optional);

        logger.logSuccess("Waiting for " + locator);
    }

    @And("Waiting for {loadState} state")
    public void waitingForState(LoadState state) {
        page.waitForLoadState(state);
    }


    @Type(name = "Validators")
    @Type(name = "Handlers")
    @Type(name = "Locators")
    @And("Fill the values for fields {result}")
    public void fillTheValuesForFields(String result, List<Map<String, String>> table) {
        table.forEach(map -> {
            Locator locator = CustomParameters.getLocator(map.get("Field"));
            Optional<HandlerInfo> handler = handlerService.get(map.get("Handler"));
            String value = map.get("Value");

            HandlerInfo handlerInfo = handler.get();
            try {
                handlerInfo.method.invoke(handlerInfo.object, locator, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Type(name = "Comparators")
    @Type(name = "Locators")
    @And("Validate using comparators")
    public void validateComparators(List<Map<String, String>> table) {
        table.forEach(map -> {
            Locator locator = CustomParameters.getLocator(map.get("Field"));
            Optional<HandlerInfo> handler = handlerService.get(map.get("Handler"));
            String value = map.get("Value");

            HandlerInfo handlerInfo = handler.get();
            try {
                handlerInfo.method.invoke(handlerInfo.object, locator, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @And("Store the result string {resultString}")
    public void storeResultStringData(String data) {
        page.pause();
    }

    @And("Store the data {dataString}")
    public void storeData(String data) {
        page.pause();
    }

    @And("Pause the process")
    public void pauseTheProcess() {
        page.pause();
    }

    @Function
    public String hello(String name, String test) {
        return "hello";
    }

    @Function
    public String dateFromToday(String days) {
        return LocalDate.now().plusDays(Integer.parseInt(days)).toString();
    }

    @Function
    public String test() {
        return "hello";
    }

    @Comparator
    public boolean isEqual(String name, String test) {
        return name.equals(test);
    }

    @Type(name = "Locators")
    @Type(name = "Handlers")
    @And("Fill the values for fields")
    public void fillTheValuesForFields() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("Set {locator} value {resultString} using {handler} handler")
    public void setValueUsingHandler(Locator locator, String value, HandlerInfo handler) throws InvocationTargetException, IllegalAccessException {
        handler.method.invoke(handler.object, locator, value);
    }

    @And("Wait {int} seconds")
    public void waitSeconds(int seconds) {
        // Write code here that turns the phrase above into concrete actions
        page.waitForTimeout(seconds * 1000);
    }
}
