package steps

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import helpers.LogAnalysisHelper

class LogAnalysisSteps {

    def logFile
    def errorReport
    def recommendations = []
    def logEntries = []
    def perfIssue = [:]
    def errorEntries = [:]

    LogAnalysisHelper helper = new LogAnalysisHelper()

    @Given("the user have an application log file {string}:")
    void the_user_have_an_application_log_file(String filePath, String docString) {
        logFile = new File(filePath)

        if (!logFile.exists()) {
            logFile.parentFile.mkdirs()
            logFile.text = docString
            println "Created Log file: ${logFile.absolutePath}"
        } else {
            println "Using existing Log file: ${logFile.absolutePath}"
        }
    }

    @When("the user parse the log file {string}")
    void the_user_parse_the_log_file(String filePath) {
        logEntries = helper.parseLogs(new File(filePath))
        println "Parsed ${logEntries.size()} log entries"
    }

    @When("the user extract all ERROR and FATAL messages")
    void the_user_extract_all_error_and_fatal_messages() {
        errorEntries = logEntries.findAll { it.level in ["ERROR", "FATAL" ] }
        println "Extracted ${errorEntries.size()} ERROR/FATAl entries"

    }
    @When("the user analyze the performance warnings")
    void the_user_analyze_the_performance_warnings() {
        perfIssue = helper.perfAnalyzer(logEntries)
        println """Identified performance issues:
- ${perfIssue}
"""
    }

    @Then("the user identify {int} INFO messages")
    void the_user_identify_info_messages(Integer expectedCount) {
        def infoMessages = logEntries.findAll { it.level == 'INFO' }
        assert infoMessages.size() == expectedCount, "Expected ${expectedCount} INFO messages but found ${infoMessages}"
        println "✓ Identified ${expectedCount} INFO messages"
    }

    @Then("the user identify {int} WARN messages")
    void the_user_identify_warn_messages(Integer expectedCount) {
        def warnMessages = logEntries.findAll { it.level == 'WARN' }
        assert warnMessages.size() == expectedCount, "Expected ${expectedCount} INFO messages but found ${warnMessages}"
        println "✓ Identified ${expectedCount} WARN messages"
    }

    @Then("the user identify {int} ERROR messages")
    void the_user_identify_error_messages(Integer expectedCount) {
        def errorMessages = logEntries.findAll { it.level == 'ERROR' }
        assert errorMessages.size() == expectedCount, "Expected ${expectedCount} INFO messages but found ${errorMessages}"
        println "✓ Identified ${expectedCount} ERROR messages"
    }

    @Then("the user identify {int} FATAL messages")
    void the_user_identify_fatal_messages(Integer expectedCount) {
        def fatalMessages = logEntries.findAll { it.level == "FATAL" }
        assert fatalMessages.size() == expectedCount, "Expected ${expectedCount} INFO messages but found ${fatalMessages}"
        println "✓ Identified ${expectedCount} FATAL messages"
    }

    @Then("the user should generate an error report")
    void the_user_should_generate_an_error_report() {
        errorReport = helper.generateReport(logEntries)
        assert errorReport != null, 'Error report should not be null'
        println '✓ Error report generated successfully'
    }

    @Then("the error report should contain timestamps")
    void the_error_report_should_contain_timestamps() {
        assert errorReport != null, 'Error report should not be null'
        assert logEntries.every { it.date != null && !it.date.isEmpty() }, 'All entries should have a date'
        assert logEntries.every { it.timestamp != null && !it.timestamp.isEmpty() }, 'All entries should have a timestamp'
        assert errorReport.contains('Date'), 'Report should contain Date field'
        assert errorReport.contains('Timestamp'), 'Report should contain Timestamp field'
        assert errorReport.contains('Generated at'), 'Report should contain Generated at field'
        println '✓ Error report contains timestamps'
    }

    @Then("the error report should contain error details")
    void the_error_report_should_contain_error_details() {
        assert errorReport != null, 'Error report should not be null'
        assert errorReport.contains('Thread    :'), 'Report should contain Thread field'
        assert errorReport.contains("Message   :"), 'Report should contain Message field'


        def logLevel = ['FATAL', 'ERROR', 'WARN', 'INFO']
        logLevel.each { level ->
            def entriesForLevel = logEntries.findAll { it.level == level }
            if (entriesForLevel.size() > 0) {
                assert errorReport.contains("[ ${level} ]"), "Report should contain ${level} section"
                assert errorReport.contains("${level}: ${entriesForLevel.size()}"), "Report summary should show correct ${level} count"
            }
        }

        logEntries.each { entry ->
            assert errorReport.contains(entry.message), "Report should contain ${entry.message}"
            assert errorReport.contains(entry.thread), "Report should contain ${entry.thread}"
        }
        println '✓ Error report contains error details'
    }

    @Then("the error report should be saved to {string}")
    void the_error_report_should_be_saved_to(String filePath) {
        assert errorReport != null, 'Error report should not be null before saving'

        def reportFile = new File(filePath)
        reportFile.parentFile.mkdirs()
        reportFile.text = errorReport

        assert reportFile.exists(), "Report file should exists at: ${filePath}"

        println "✓ Error report saved to: ${reportFile.absolutePath}"
    }

    @Then("the user should identify the slow queries")
    void the_user_should_identify_the_slow_queries() {
        assert perfIssue != null, 'Performance issue should not be null'
        assert perfIssue.slowQueries != null, 'Slow Queries should not be null'
        assert perfIssue.slowQueries.size() > 0, 'Should identify at least one slow query'

        perfIssue.slowQueries.each { entry ->
            assert entry.date != null && !entry.date.isEmpty(), "Slow query entry should have a date"
            assert entry.timestamp != null && !entry.timestamp.isEmpty(), "Slow query entry should have a timestamp"
            assert entry.thread != null && !entry.thread.isEmpty(), "Slow query entry should have a thread"
            assert entry.message.contains("Slow query"), "Entry message should contain 'Slow query': ${entry.message}"
        }

        println "✓ Identified ${perfIssue.slowQueries.size()} slow quer(ies):"
        perfIssue.slowQueries.each { entry ->
            println "  • [${entry.date} ${entry.timestamp}] [${entry.thread}] ${entry.message}"
        }
    }

    @Then("the user should identify the high memory usage")
    void the_user_should_identify_the_high_memory_usage() {
        assert perfIssue != null, "Performance issues should not be null"
        assert perfIssue.memoryWarnings != null, "Memory warnings list should not be null"
        assert perfIssue.memoryWarnings.size() > 0, "Should identify at least one high memory usage entry"

        perfIssue.memoryWarnings.each { entry ->
            assert entry.date != null && !entry.date.isEmpty(), "Memory warning entry should have a date"
            assert entry.timestamp != null && !entry.timestamp.isEmpty(), "Memory warning entry should have a timestamp"
            assert entry.thread != null && !entry.thread.isEmpty(), "Memory warning entry should have a thread"

            def matcher = entry.message =~ /Memory usage: (\d+)%/
            assert matcher.find(), "Entry should contain memory usage percentage: ${entry.message}"
            assert matcher[0][1].toInteger() > 80, "Memory usage should exceed 80%: ${entry.message}"
        }

        println "✓ Identified ${perfIssue.memoryWarnings.size()} high memory usage entrie(s):"
        perfIssue.memoryWarnings.each { entry ->
            println "  • [${entry.date} ${entry.timestamp}] [${entry.thread}] ${entry.message}"
        }
    }

    @Then("the user should identify the API timeouts")
    void the_user_should_identify_the_api_timeouts() {
        assert perfIssue != null, "Performance issues should not be null"
        assert perfIssue.apiTimeouts != null, "API timeouts list should not be null"
        assert perfIssue.apiTimeouts.size() > 0, "Should identify at least one API timeout"

        perfIssue.apiTimeouts.each { entry ->
            assert entry.date != null && !entry.date.isEmpty(), "API timeout entry should have a date"
            assert entry.timestamp != null && !entry.timestamp.isEmpty(), "API timeout entry should have a timestamp"
            assert entry.thread != null && !entry.thread.isEmpty(), "API timeout entry should have a thread"
            assert entry.message.toLowerCase().contains("timeout"), "Entry message should contain 'timeout': ${entry.message}"
        }

        println "✓ Identified ${perfIssue.apiTimeouts.size()} API timeout(s):"
        perfIssue.apiTimeouts.each { entry ->
            println "  • [${entry.date} ${entry.timestamp}] [${entry.thread}] ${entry.message}"
        }
    }

    @Then("the user should generate performance recommendations")
    void the_user_should_generate_performance_recommendations() {
        recommendations = helper.makeRecommendations(perfIssue)
        assert recommendations.size() > 0
        println "✓ Generated ${recommendations.size()} recommendations: "
        recommendations.each { rec -> println " • ${rec}" }
    }
}
