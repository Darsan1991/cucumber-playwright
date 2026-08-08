Feature: Test

  Scenario:And Click on "clickable::Get Started Now" and wait 1 seconds

    And Store the data "projectManager"
    And Click on "CheckBox::Hello->Input::Test->Heading::Test->Link::Test"
    And Fill the values for fields "=dateFromTodayWithFormat(test,44)"
      | Field                                             | Value    | Handler      |
      | $textBox::First Name->ByText::->Heading::->Text:: | Sample   | ((fillText)) |
      | $Heading::First Name                              | Sample   | ((fillText)) |
      | $ByRole::BUTTON::First Name                       | Sample   | ((fillText)) |
      | [[notHyperLink::{{data}}]]                        | {{data}} | =hello(2,3)  |


    And Store the result string "=dateFromToday({{data}})"
    And Validate using comparators
      | Field             | Value                                | Handler        |
      | $text::First Name | Sample                               | fill text      |
      | [[isEqual]]       | =dateFromTodayWithFormat(1,M/d/yyyy) | =hello(test,s) |
    

    And Store the data "projectManager"
    And Store the result string "=dateFromTodayWithFormat({{data}},23)"



    And Click on "textBox::hello"
    And Fill the values for fields "=hello(test,2)"
      | Field        | Value       | Handler  |
      | ((dropdown)) | Hospitality | dropdown |
    And Click on "clickable::Next"
    And Fill the values for fields
      | Field                                 | Value          | Handler  |
      | $textBox::{{data}}->Text::Hello world | Any Department | dropdown |
    And Click on "clickable::Hello World" and wait 5 seconds
    And Click on "CheckBox::Hello World" "Clickable::text" and wait 5 seconds
    And Click on "textBox::hello"
    And Fill the values for fields "=hello(test,2)"
      | Field        | Value       | Handler  |
      | ((dropdown)) | Hospitality | dropdown |
    And Click on "clickable::Next"
    And Fill the values for fields
      | Field              | Value          | Handler  |
      | $textBox::{{data}} | Any Department | dropdown |
    And Click on "clickable::Hello World" and wait 5 seconds


  Scenario Outline: Login to SwagLabs Application with Correct credentials - <UserName>
    Given User launched SwagLabs application
      | Hello        | Field                           |
      | ((fillText)) | [[visible::{{projectManager}}]] |
    And Waiting for dom content loaded state
    And Validate "text::test1233333444444444" using "notVisible" validator
    And Validate "textBox::world" using "notVisible" validator

    And Validate "TEXT::© 2025 Salesforce, Inc. All rights reserved." using "visible" validator
    And Validate "text::2025 Salesforce, Inc. All rights reserved." using "Visible" validator
    And Validate "text::2025 Salesforce, Inc. All rights reserved." using "visible" validator
    And Validate "text::Forgot Your Password?" using "hyper link" validator
    And Validate "text::Username" using "NotHyperLink" validator
    And Validate "link::Forgot Your Password?" using "clickable" validator
    And Validate "textBox::Username" using "editable" validator
    And Validate "checkBox::Remember Me" using "editable" validator
    And Validate "Link::Test Link" using "notVisible" validator
    And Validate "Link::Test Link" using "notVisible" validator
#    And Fill "textBox::Password" inside "form::Username" with "TestPassword" using "fill-Text" handler
#    And Fill "textBox::Username" inside "form::Username" with "TesUsername@gamil.com" using "fillText" handler


    And Fill the values for fields
      | Field             | Value          | Handler      |
      | $ByText::Username | test@gmail.com | ((dropdown)) |
      | =hello(1,2)       | {{data}}       | ((checkbox)) |

    And Click on "clickable::Log In" inside "form::Username" type js and wait 1 seconds
    And Validate "text::Error: Please check your username and password. If you still can't log in, contact your Salesforce administrator." using "errorText" validator
    And Click on "clickable::Forgot Your Password?" and wait 1 seconds
    And Validate "heading::Reset Your Password" using "visible" validator
    And Validate "text::How do I verify my identity?" using "Hyper link" validator
    And Validate "clickable::Continue" using "clickable" validator
    And Fill "textbox::Enter your username..." with "hello" using "checkbox" handler
    And Click on "clickable::Continue" and wait 1 seconds
    And Validate "text::Enter a valid username. Your username is in the format of an email address, such as username@company.com." using "errorText" validator
    And Fill "textbox::Enter your username..." with "darsan1991@gmail.com" using "fill text" handler and wait 10 seconds
    And Click on "clickable::Continue" and wait 1 seconds
    And Waiting for "text::Check Your Email!!!" with timeout 20 seconds not throw
    And Click on "clickable::Log In" with timeout 20 seconds if there
    And Waiting for "TextBox::Check Your Email" with timeout 10 seconds

    And Fill the values for fields
      | Field        | Value          | Handler      |
      | $text::Hello | test@gmail.com | ((fillText)) |
    
  
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
      | Hello                                       | Field            |
      | [[notVisible && notHyperLink && hyperLink]] | $clickable::test |

    And Validate "text::hello world" using "NotVisible" validator
    And Validate "text::hello world" using "notVisible" validator

    And Validate "text::© 2025 Salesforce, Inc. All rights reserved." using "visible" validator
    And Validate "text::© 2025 Salesforce, Inc. All rights reserved.!" using "notVisible" validator
    And Validate "text::Forgot Your Password?" using "hyperLink" validator
    And Validate "select::Username" using "NotHyperLink" validator
    And Validate "link::Forgot Your Password?" using "clickable" validator
    And Validate "textBox::Username" using "editable" validator
    And Validate "checkBox::Remember Me" using "hyperLink" validator
    And Fill "textBox::Username" inside "text::hello" with "Hello World" using "fold" handler
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

    
    
  