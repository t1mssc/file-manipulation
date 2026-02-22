Feature: JSON Configuration File Management
  As a test automation engineer
  I want to manage JSON configuration files
  So that I can handle complex test data structures

  Background:
    Given the user have a JSON config file "src/test/resources/testdata/json/app-config.json":
      """
      {
         "application": {
           "name": "TestApp",
           "version": "2.1.0",
           "environment": "staging"
        },
        "database": {
           "host": "localhost",
           "port": 5432,
           "name": "testdb",
           "credentials": {
             "username": "dbuser",
             "password": "dbpass123"
            }
        },
        "api": {
            "baseUrl": "https://api.staging.example.com",
            "timeout": 30000,
            "endpoints": {
              "login": "/auth/login",
              "users": "/api/v1/users"
             }
          }
        }
        """

  @intermediate-advanced @json
  Scenario: Read and validate JSON structure
    When the user load the JSON file "src/test/resources/testdata/json/app-config.json"
    Then the JSON should have key "application"
    And the JSON should have key "database"
    And the JSON should have key "api"

  @intermediate-advanced @json
  Scenario: Access nested JSON data
    When the user load the JSON file "src/test/resources/testdata/json/app-config.json"
    Then "application.name" should equal "TestApp"
    And "database.port" should equal 5432
    And "database.credentials.username" should equal "dbuser"
    And "api.baseUrl" should equal "https://api.staging.example.com"

  @intermediate-advanced @json
  Scenario: Modify and save JSON configuration
    When the user load the JSON file "src/test/resources/testdata/json/app-config.json"
    And the user update "application.environment" to "production"
    And the user update "api.baseUrl" to "https://api.prod.example.com"
    And the user save the JSON to "src/test/resources/testdata/json/app-config-updated.json"
    Then the file "src/test/resources/testdata/json/app-config-updated.json" should exist

  @intermediate-advanced @json
  Scenario: Extract and use nested JSON data
    When the user load the JSON file "src/test/resources/testdata/json/app-config.json"
    And the user extract database credentials
    Then the user should have username "dbuser"
    And the user should have password "dbpass123"
    And the user can construct connection string "jdbc:postgresql://localhost:5432/testdb"
