Feature: Basic File Reading and Validation
  As a test automation engineer
  I want to read and validate file contents
  So that I can verify file-based test data

  Background:
    Given the test directory exists

  @beginner @file-reading
  Scenario: Read and validate a simple text file
    Given the user have a text file "src/test/resources/testdata/sample/sample.txt" with content:
      """
      username=admin
      password=secret123
      environment=staging
      """
    When the user read the file contents
    Then the file should contain "username=admin"
    And the file should contain "environment=staging"
    And the file should have 3 lines

  @beginner @file-reading
  Scenario: Verify file existence and properties
    Given the user have a file "src/test/resources/testdata/sample/config.txt"
    When the user check the file properties
    Then the file should exists
    And the file should be readable
    And the file size should have greater than 0 bytes

  @beginner @file-reading @negative
  Scenario: Handle missing file gracefully
    Given the user have file path "src/test/resources/testdata/sample/nonexistent.txt"
    When the user check if the file exists
    Then the file should not exists
    And the file should be handled without errors

  @beginner @file-reading
  Scenario: Read file and parse key-value pairs
    Given the user have properties file "src/test/resources/testdata/sample/app.properties" with content:
      """
      app.name=TestApp
      app.version=1.0.0
      app.port=8080
      """
    When the user read and parse the properties file
    Then the file should have property "app.name" with value "TestApp"
    And the file should have property "app.version" with value "1.0.0"
    And the file should have property "app.port" with value "8080"