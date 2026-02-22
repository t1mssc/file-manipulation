Feature: CSV File Processing for Data-Driven Testing
  As a QA engineer
  I want to process CSV files
  So that I can use real data for testing

  Background:
    Given the user have CSV file "src/test/resources/testdata/csv/users.csv" with content:
      """
      username,password,role,active
      admin,Admin@123,administrator,true
      john.doe,Pass123,user,true
      jane.smith,Test1456,user,false
      guest,Guest99,guest,true
      manager,Mgr@456,manager,true
      """

  @intermediate @csv @data-driven
  Scenario: Load and parse CSV file
    When the user load the CSV file "src/test/resources/testdata/csv/users.csv"
    Then the user should have 5 records
    And each record should have 4 fields
    And the CSV header should be "username,password,role,active"

  @intermediate @csv
  Scenario: Filter active users from CSV
    When the user load the CSV file "src/test/resources/testdata/csv/users.csv"
    And the user filter users where "active" equals "true"
    Then the user should have 4 filtered records
    And all filtered records should have active status true

  @intermediate @csv @data-driven
  Scenario Outline: Login with CSV data
    When the user load the CSV file "src/test/resources/testdata/csv/users.csv"
    And the user find the user with username "<username>"
    And the user attempt to login with the users credentials
    Then the login result should be "<expectedResult>"
    And the user role should be "<expectedRole>"
    Examples:
      |    username    |    expectedResult    |    expectedRole    |
      |    admin       |    success           |    administrator   |
      |    john.doe    |    success           |    user            |
      |    jane.smith  |    account_inactive  |    user            |
      |    guest       |    success           |    guest           |
      |    manager     |    success           |    manager         |

  @intermediate @csv
  Scenario: Generate report from CSV processing
    When the user load the CSV file "src/test/resources/testdata/csv/users.csv"
    And the user process all user records
    Then the user should generate a summary reports
    And the report should contain user statistics
    And the report should contain "Total Users: 5"
    And the report should be saved to "src/test/resources/reports/user_summary.txt"
