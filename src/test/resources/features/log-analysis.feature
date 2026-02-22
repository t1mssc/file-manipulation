Feature: File Analysis and Reporting
  As a senior test automation engineer
  I want to analyze application log files
  So that I can identify issues and generate insights

  Background:
    Given the user have an application log file "src/test/resources/testdata/logs/app.log":
      """
      2024-02-19 10:15:23 INFO [main] Application started
      2024-02-19 10:15:24 INFO [main] Database connection established
      2024-02-19 10:15:25 WARN [worker-1] Slow query detected: 2.5s
      2024-02-19 10:15:30 ERROR [worker-2] Failed to process order #12345
      2024-02-19 10:15:31 ERROR [worker-2] NullPointerException at OrderProcessor.java:145
      2024-02-19 10:15:35 INFO [main] Processing batch job
      2024-02-19 10:15:40 WARN [worker-3] Memory usage: 85%
      2024-02-19 10:15:45 ERROR [worker-1] API timeout: /api/products
      2024-02-19 10:15:50 INFO [main] Batch completed: 1000 records
      2024-02-19 10:15:55 FATAL [main] System shutdown due to critical error
      """

  Scenario: Parse and analyze log levels
    When the user parse the log file "src/test/resources/testdata/logs/app.log"
    Then the user identify 4 INFO messages
    And the user identify 2 WARN messages
    And the user identify 3 ERROR messages
    And the user identify 1 FATAL messages

  Scenario: Extract and report errors with context
    When the user parse the log file "src/test/resources/testdata/logs/app.log"
    And the user extract all ERROR and FATAL messages
    Then the user should generate an error report
    And the error report should contain timestamps
    And the error report should contain error details
    And the error report should be saved to "src/test/resources/log-reports/error-analysis.txt"

  Scenario: Identify performance issues from logs
    When the user parse the log file "src/test/resources/testdata/logs/app.log"
    And the user analyze the performance warnings
    Then the user should identify the slow queries
    And the user should identify the high memory usage
    And the user should identify the API timeouts
    And the user should generate performance recommendations
