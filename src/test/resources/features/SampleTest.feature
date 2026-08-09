Feature: Test


  @PBI_1
  Scenario:And Click on "clickable::Get Started Now" and wait 1 seconds

    And Navigate to url "sampleSite"
    And Click on "Link::Input Fields"
    And Pause the process


  @Handler
  Scenario:And Click on "clickable::Get Started Now" and wait 1 seconds
    And Navigate to url "sampleSite"
    And Click on "Link::Log in / Sign up" inside "ByClass::__actions"
    And Set "ByPlaceholder::you@example.com" value "darsan0091@gmail.com" using "fillText" handler
    And Set "ById::password" value "Test1234" using "fillText" handler
    And Click on "TextButton::Sign in"
    And Waiting for "ByClass::profile"
    And Wait 3 seconds
    And Pause the process



  @Validator
  Scenario:And Click on "clickable::Get Started Now" and wait 1 seconds
    And Navigate to url "sampleSite"
    And Click on "Link::Log in / Sign up" inside "ByClass::__actions"
    And Set "ByPlaceholder::you@example.com" value "darsan0091@gmail.com" using "fillText" handler
    And Set "ById::password" value "=decrypt({{password}})" using "fillText" handler
    And Click on "TextButton::Sign in"
    And Waiting for "ByClass::profile"
    And Click on "Link::Settings"
    And Validate "InputGroup::First Name" with value "DDDD" using "inputValue" validator
    And Pause the process
