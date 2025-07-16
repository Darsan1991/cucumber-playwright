Feature: Buy Product

  Background:

    And cache basic project details for "Hello world"
    And cache basic project details for "" with:
      |  |
      |  |
      |  |

    And validate project info
      | Field         | Value        |
      | Hello world   | Test         |
      | Schedule Name | Design Build |

    And Update values of project ""
      | Field    | Value       |
      | status_c | hello world |


  @End2End
  Scenario Outline: Buy a SwagLabs product successfully
    Given User launched SwagLabs application
    When User logged in the app using username "<UserName>" and password "<Password>" and wait 10 seconds if "hello world"
    And User adds "<Product>" product to the cart
    And User enters Checkout details with "<FirstName>", "<LastName>", "<Zipcode>"
    And User completes Checkout process
    Then User should get the Confirmation of Order

    Examples:
      | UserName      | Password     | Product                  | FirstName | LastName | Zipcode |
      | standard_user | secret_sauce | Sauce Labs Fleece Jacket | Ashish    | Ghosh    | 1181    |


    And Save details activity "" to "Test1"
      | Field  | Key    |
      | Custom | Custom |


    And Save details activity "" to "Test2"
      | Field  | Key    |
      | Custom | Custom |

    And Compare variables "Test1" and "Test2":
      | Custom |


    And Store result of "SAMPLE_QUERY" query to "Something" in "test" session:
      | From     | To    |
      | testData | testt |
      | dddd     | ffff  |


    And Compare values "Test" and "Hello":
      | Field | Comparator |
      | Test  | MapDate    |