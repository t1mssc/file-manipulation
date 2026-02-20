package steps

import io.cucumber.docstring.DocString
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CsvProcessingSteps {

    def csvFile
    def currentUser
    def loginResult
    def reportContent
    def csvData = []
    def csvHeaders = []
    def filteredData = []
    def statistics = [:]

    @Given("the user have CSV file {string} with content:")
    void the_user_have_csv_file_with_content(String csvFilePath, DocString content) {
        csvFile = new File(csvFilePath)
        csvFile.parentFile.mkdirs()
        csvFile.text = content.getContent().trim()
        println "Created CSV File: ${csvFile.absolutePath}"
    }

    @When("the user load the CSV file {string}")
    void the_user_load_the_csv_file(String csvFilePath) {
        csvFile = new File(csvFilePath)
        def lines = csvFile.readLines()

        csvHeaders = lines[0].split(',').collect { it.trim() }
        println "CSV Headers: ${csvHeaders}"

        csvData = lines.drop(1).collect {line ->
            def values = line.split(',')
            def record = [:]
            csvHeaders.eachWithIndex{ header, index -> record[header] = values[index].trim() }
            return record
        }
        println "Loaded ${csvData.size()} records"
    }

    @When("the user filter users where {string} equals {string}")
    void the_user_filter_users_where_equals(String status, String value) {
        filteredData = csvData.findAll { record -> record[status] == value }
        println "Filtered to ${filteredData.size()} records"
    }

    @When("the user find the user with username {string}")
    void the_user_find_the_user_with_username(String username) {
        currentUser = csvData.find { it.username == username }
        assert currentUser != null, "User not found: ${currentUser}"
        println "Found user: ${currentUser}"
    }

    @When("the user attempt to login with the users credentials")
    void the_user_attempt_to_login_with_the_users_credentials() {
        if (currentUser.active == 'true') {
            loginResult = 'success'
            println "Login successful for ${currentUser.username}"
        }

        if (currentUser.active == 'false') {
            loginResult = 'account_inactive'
            println "Login failed: account_inactive for ${currentUser}"
        }
    }

    @When("the user process all user records")
    void the_user_process_all_user_records() {
        def activeCount = csvData.count { it.active == 'true' }
        def inactiveCount = csvData.count { it.active == 'false' }
        def roles = csvData.collect { it.role }.unique()

        def formattedDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss'))

        reportContent = """
User Statistics Report
======================
Generated: ${formattedDate}
                       
Total Users: ${csvData.size()}
Active Users Count: ${activeCount}
Inactive Users Count: ${inactiveCount}
Unique Roles: ${roles.join(', ')}

User Details: 
${csvData.collect { "- ${it.username} (${it.role}): active=${it.active}" }.join('\n')}
                       
Role Distribution: 
${csvData.groupBy {it.role}
                .collect {role, user -> "- ${role}: ${user.size()}" }
                .join('\n')}
""".stripIndent()

        println 'Generated report'
    }

    @Then("the user should have {int} records")
    void the_user_should_have_records(Integer expectedCount) {
        assert csvData.size() == expectedCount, "User record is not equal to ${expectedCount}"
        println "✓ ${expectedCount} users record loaded"
    }

    @Then("each record should have {int} fields")
    void each_record_should_have_fields(Integer expectedFields) {
        csvData.each { record -> assert record.size() == expectedFields }
        println '✓ Each record has ${expectedFields} fields'
    }

    @Then("the CSV header should be {string}")
    void the_csv_header_should_be(String expectedHeaders) {
        def expected = expectedHeaders.split(',').collect { it.trim() }
        def actual = csvFile.readLines().first().split(',').collect { it.trim() }
        assert actual == expected, "${actual} is not matched to ${expected}"
        println '✓ CSV headers match'
    }

    @Then("the user should have {int} filtered records")
    void the_user_should_have_filtered_records(Integer expectedCount) {
        assert filteredData.size() == expectedCount, "Filtered data size: ${filteredData} is not equal to ${expectedCount}"
        println "✓ ${expectedCount} filtered records"
    }

    @Then("all filtered records should have active status true")
    void all_filtered_records_should_have_active_status_true() {
        assert filteredData.each { record -> record.active == 'true' }
        println '✓ All filtered records are active'
    }

    @Then("the login result should be {string}")
    void the_login_result_should_be(String expectedResult) {
        assert loginResult == expectedResult, "${loginResult} is not equal to ${expectedResult}"
        println "✓ Login Result: ${expectedResult}"
    }

    @Then("the user role should be {string}")
    void the_user_role_should_be(String expectedRole) {
        assert currentUser.role == expectedRole, "${currentUser.role} is not the expected ${expectedRole} role"
        println "✓ User Role: ${expectedRole}"
    }

    @Then("the user should generate a summary reports")
    void the_user_should_generate_a_summary_reports() {
        assert reportContent != null && reportContent.length() > 0
        println '✓ Summary Report generated'
    }

    @Then("the report should contain user statistics")
    void the_report_should_contain_user_statistics() {
        assert reportContent.contains('Total Users:')
        assert reportContent.contains('Active Users Count:')
        println '✓ Report contains stat'
    }

    @Then("the report should contain {string}")
    void the_report_should_contain(String expectedText) {
        assert reportContent.contains(expectedText)
        println "✓ Report contains: ${expectedText}"
    }

    @Then("the report should be saved to {string}")
    void the_report_should_be_saved_to(String reportPath) {
        def reportFile = new File(reportPath)
        reportFile.parentFile.mkdirs()
        reportFile.text = reportContent
        assert reportFile.exists()
        println "✓ Report saved to: ${reportPath}"
    }
}
