Feature: Login

  Scenario: Login to SwagLabs Application with Correct credentials - <UserName>
    Given Navigate to url "https://www.blueprism.com/resources/blog/forms-automation/"

    And Click on "clickable::Get Started" and wait 2 seconds
    And Click on "clickable::Get Started Now" and wait 1 seconds

    And Click on "clickable::Accept All Cookies" with timeout 3 seconds if there 
    
    And Fill the values for fields
      | Field               | Value  | Handler   |
      | textBox::First Name | Sample | fill text |
      | textBox::Last Name  | Text   | fill text |
    And Click on "clickable::Next"
    And Fill the values for fields
      | Field               | Value  | Handler   |
      | select::Industry Select | Hospitality | dropdown|
    And Click on "clickable::Next"
    And Fill the values for fields
      | Field               | Value  | Handler   |
      | select::Department Select | Any Department | dropdown|
    And Click on "clickable::Next" and wait 5 seconds
    


  Scenario Outline: Login to SwagLabs Application with Correct credentials - <UserName>
    Given User launched SwagLabs application
      | Hello | Field |
      | World | Test  |
    And Waiting for dom content loaded state
    And Validate "text::hello world" using "Not Visible" validator
    And Validate "text::hello world" using not visible validator

    And Validate "TEXT::© 2025 Salesforce, Inc. All rights reserved." using visible validator
    And Validate "text::2025 Salesforce, Inc. All rights reserved." using "Visible" validator
    And Validate "text::Forgot Your Password?" using "Hyperlink" validator
    And Validate "text::Username" using "Not HyperLink" validator
    And Validate "link::Forgot Your Password?" using "clickable" validator
    And Validate "textBox::Username" using "editable" validator
    And Validate "checkBox::Remember Me" using editable validator
#    And Fill "textBox::Password" inside "form::Username" with "TestPassword" using "fill-Text" handler
#    And Fill "textBox::Username" inside "form::Username" with "TesUsername@gamil.com" using "fillText" handler


    And Fill the values for fields
      | Field             | Value          | Handler   |
      | textBox::Username | test@gmail.com | fill text |
      | textBox::Password | password       | fill text |

    And Click on "clickable::Log In" inside "form::Username" type js and wait 1 seconds
    And Validate "text::Error: Please check your username and password. If you still can't log in, contact your Salesforce administrator." using "errorText" validator
    And Click on "clickable::Forgot Your Password?" and wait 1 seconds
    And Validate "heading::Reset Your Password" using visible validator
    And Validate "text::How do I verify my identity?" using "Hyperlink" validator
    And Validate "clickable::Continue" using "clickable" validator
    And Fill "textbox::Enter your username..." with "hello" using "fill text" handler
    And Click on "clickable::Continue" and wait 1 seconds
    And Validate "text::Enter a valid username. Your username is in the format of an email address, such as username@company.com." using "errorText" validator
    And Fill "textbox::Enter your username..." with "darsan1991@gmail.com" using "fill text" handler and wait 10 seconds
    And Click on "clickable::Continue" and wait 1 seconds
    And Waiting for "text::Check Your Email!!!" with timeout 20 seconds not throw
    And Click on "clickable::Log In" with timeout 20 seconds if there
    And Waiting for "text::Check Your Email" with timeout 10 seconds

    And Fill the values for fields
      | Field       | Value          | Handler   |
      | text::Hello | test@gmail.com | fill text |
    
  
#    And Validate "text::Error: Please check your username and password. If you still can't log in, contact your Salesforce administrator." using "successText" validator
    


#    When User logged in the app using username "locator Item123::name -> locator Format::Hello World" and password "<Password>" and wait 10 seconds
##    When User logged in the app using username "locator Item123 -> locator Format::Hello World" and password "<Password>" and wait 10 seconds
#    
##    When User logged in the app using username "locator Checkout" and password "<Password>"
##    When User logged in the app using username "locator Format::Hello World" and password "<Password>"
#    Then user should be able to log in
#    And the user logs in with username "hello"

    Examples:
      | UserName      | Password     |
      | standard_user | secret_sauce |
#      | standard_user test| secret_sauce |


  Scenario: Login to SwagLabs Application with Correct credentials - TEST ----------------
    Given User launched SwagLabs application
      | Hello | Field |
      | World | Test  |

    And Validate "text::hello world" using "Not Visible" validator
    And Validate "text::hello world" using not visible validator



    And Validate "text::© 2025 Salesforce, Inc. All rights reserved." using visible validator
    And Validate "text::© 2025 Salesforce, Inc. All rights reserved.!" using "Not Visible" validator
    And Validate "text::Forgot Your Password?" using "Hyperlink" validator
    And Validate "text::Username" using "Not HyperLink" validator
    And Validate "link::Forgot Your Password?" using "clickable" validator
    And Validate "textBox::Username" using "editable" validator
    And Validate "checkBox::Remember Me" using editable validator
    And Fill "textBox::Username" with "Hello World" using "fillText" handler
    And Fill "textBox::Password" with "TestPassword" using "fillText" handler
    And Click on "clickable::Log In" and wait 1 seconds


    And Validate "text::Error: Please check your username and password. If you still can't log in, contact your Salesforce administrator." using "errorText" validator

#  @SmokeTest
#  Scenario Outline: Login to SwagLabs Application with Wrong credentials
#    Given User launched SwagLabs application
##    When User logged in the app using username "<UserName>" and password "<Password>"
#    And User launched SwagLabs application if "hello world"
#    Then User should not get logged in
#    
#    And Validate "Hello" matching "" using "Text" validator
#    And Validate "Hello" using "HyperText" validator  
#
#    And fill values of fields
#      | Field        | Value | Handler   |
#      | Field::Hello | Test  | InputFill |
#      |              |       |           |
#
#
#    And click in series
#      | Button::Hello | Dealy |
#      | Text::Test    | 10    |
#      | Link::Test    | 4     |
#
#    And fill the values
#      | Field | Value       |
#      | Name  | Hello World |
#
#
#    
#    
#    
#
#    Examples:
#      | UserName        | Password     |
#      | locked_out_user | secret_sauce |

    
    