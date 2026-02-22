package steps

import io.cucumber.docstring.DocString
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class FileReadingSteps {

    def testFile
    def fileContent
    def fileExists
    def fileLines
    def properties = [:]

    @Given("the test directory exists")
    void the_test_directory_exists() {
        def dir = new File("src/test/resources/testdata")
        if (!dir.exists()) dir.mkdirs()
        assert dir.exists(), "Test data directory could not be created"
    }

    @Given("the user have a text file {string} with content:")
    void the_user_have_a_text_file_with_content(String filePath, DocString content) {
        testFile = new File(filePath)
        testFile.parentFile.mkdirs()
        testFile.text = content.getContent().trim()
        def testFilePath = testFile.absolutePath
        println "Created file: ${testFilePath}"
    }

    @Given("the user have a file {string}")
    void the_user_have_a_file(String filePath) {
        testFile = new File(filePath)
        testFile.parentFile.mkdirs()
        testFile.text = 'Sample test content\nLine2\nLine3'
        println 'Created file with sample content'
    }

    @Given("the user have file path {string}")
    void the_user_have_file_path(String filePath) {
        testFile = new File(filePath)
        // Do not create file - testing non-existence
    }

    @Given("the user have properties file {string} with content:")
    void the_user_have_properties_file_with_content(String filePath, DocString content) {
        testFile = new File(filePath)
        testFile.parentFile.mkdirs()
        testFile.text = content.getContent().trim()
        println 'Create properties file'
    }

    @When("the user read the file contents")
    void the_user_read_the_file_contents() {
        fileContent = testFile.text
        fileLines = fileContent.readLines()
        println "Read ${fileLines} lines from file"
    }

    @When("the user check the file properties")
    void the_user_check_the_file_properties() {
        println "File size: ${testFile.length()} bytes"
        println "Can read ${testFile.canRead()}"
    }

    @When("the user check if the file exists")
    void the_user_check_if_the_file_exists() {
        fileExists = testFile.exists()
        println "File exists ${fileExists}"
    }

    @When("the user read and parse the properties file")
    void the_user_read_and_parse_the_properties_file() {
        testFile.eachLine { String line ->
            if (line.trim() && !line.startsWith('#')) {
                def parts = line.split('=', 2)
                if (parts.length == 2) {
                    def key = parts[0].trim()
                    def value = parts[1].trim()
                    properties[key] = value
                }
            }
        }
        println "Parsed ${properties.size()} properties"
    }

    @Then("the file should contain {string}")
    void the_file_should_contain(String expected) {
        assert fileContent.contains(expected), "File does not contains: ${expected}"
        println "✓ File contains: ${expected}"
    }

    @Then("the file should have {int} lines")
    void the_file_should_have_lines(Integer expectedLines) {
        assert fileLines.size() == expectedLines, "Expected lines: ${expectedLines}, got ${fileLines.size()}"
        println "✓ File have ${expectedLines} lines"
    }

    @Then("the file should exists")
    void the_file_should_exists() {
        assert testFile.exists(), "File does not exists: ${testFile.path}"
        println '✓ File exists'
    }

    @Then("the file should be readable")
    void the_file_should_be_readable() {
        assert testFile.canRead(), 'File is not readable'
        println '✓ File is readable'
    }

    @Then("the file size should have greater than {int} bytes")
    void the_file_size_should_have_greater_than_bytes(Integer minSize) {
        assert testFile.length() > minSize, "File size ${testFile.length()} is not > ${minSize}"
        println "✓ File size: ${testFile.length()} bytes"
    }

    @Then("the file should not exists")
    void the_file_should_not_exists() {
        assert !fileExists, "File does exists but expected to be not existing"
        println '✓ File does not exists (as expected)'
    }

    @Then("the file should be handled without errors")
    void the_file_should_be_handled_without_errors() {
        println '✓ Missing file handled without errors'
    }

    @Then("the file should have property {string} with value {string}")
    void the_file_should_have_property_with_value(String key, String expectedValue) {
        assert properties.containsKey(key), "Property ${key} not found"
        assert properties[key] == expectedValue, "Property ${key}: expected '${expectedValue}', " +
                "got '${properties[key]}'"

        println "✓ ${key} = ${expectedValue}"
    }
}
