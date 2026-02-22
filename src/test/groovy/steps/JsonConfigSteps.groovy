package steps

import groovy.json.JsonSlurper
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import helpers.JsonConfigHelper

class JsonConfigSteps {

    def jsonFile
    def jsonData

    Map<String, Object> extractedData = [:]
    JsonConfigHelper helper = new JsonConfigHelper()

    @Given("the user have a JSON config file {string}:")
    void the_user_have_a_json_config_file(String filePath, String content) {
        jsonFile = new File(filePath)

        if (!jsonFile.exists()) {
            jsonFile.parentFile.mkdirs()
            jsonFile.text = content
            println "Created JSON file : ${jsonFile.absolutePath}"
        } else println "Using existing JSON file: ${jsonFile.absolutePath}"
    }

    @When("the user load the JSON file {string}")
    void the_user_load_the_json_file(String filePath) {
        def slurper = new JsonSlurper()
        jsonData = new HashMap(slurper.parse(new File(filePath)) as Map)
        jsonData = helper.makeMutable(jsonData)
        println "Loaded JSON with ${jsonData.size()} top-level keys"
    }

    @When("the user update {string} to {string}")
    void the_user_update_to(String key, String value) {
        helper.updateNestedValue(jsonData, key, value)
        println "Updated successfully for ${key} to '${value}'"
    }

    @When("the user save the JSON to {string}")
    void the_user_save_the_json_to(String filePath) {
        File outputFile = new File(filePath)
        outputFile.parentFile.mkdirs()
        helper.saveJsonFile(outputFile, jsonData)
    }

    @When("the user extract database credentials")
    void the_user_extract_database_credentials() {
        Map credentials = jsonData.database.credentials as Map
        Map database = jsonData.database as Map

        extractedData["username"] = credentials["username"] as String
        extractedData["password"] = credentials["password"] as String
        extractedData["host"] = database["host"] as String
        extractedData["port"] = database["port"] as Integer
        extractedData["name"] = database["name"] as String

        println "Extracted credentials for host: ${extractedData['host']}"
    }

    @Then("the JSON should have key {string}")
    void the_json_should_have_key(String key) {
        assert jsonData.containsKey(key), "${key} is not present in the JSON file"
        println "✓ JSON contains key: ${key}"
    }

    @Then("{string} should equal {string}")
    void should_equal(String key, String expectedValue) {
        def actualValue = helper.getNestedValue(jsonData, key)
        assert actualValue == expectedValue, "Expected: ${expectedValue}, but got ${actualValue}"
        println "✓ ${actualValue} = ${expectedValue}"
    }

    @Then("{string} should equal {int}")
    void should_equal(String key, Integer port) {
        def actualValue = helper.getNestedValue(jsonData, key)
        assert actualValue == port as String, "Expected: ${port}, but got ${actualValue}"
        println "✓ ${actualValue} = ${port}"
    }

    @Then("the file {string} should exist")
    void the_file_should_exist(String filePath) {
        def dir = new File(filePath)
        assert dir.exists(), "${dir} is not existing"
    }

    @Then("the user should have username {string}")
    void the_user_should_have_username(String expectedUsername) {
        def actualUsername = extractedData["username"] as String

        assert actualUsername == expectedUsername, "Expected: ${expectedUsername}, but got ${actualUsername}"
        println "✓ ${actualUsername} is expected to be the value"
    }

    @Then("the user should have password {string}")
    void the_user_should_have_password(String expectedPassword) {
        def actualPassword = extractedData["password"] as String

        assert actualPassword == expectedPassword, "Expected: ${expectedPassword}, but got ${actualPassword}"
        println "✓ ${actualPassword} is expected to be the value"
    }

    @Then("the user can construct connection string {string}")
    void the_user_can_construct_connection_string(String expected) {
        Map database = jsonData.database as Map

        extractedData["host"] = database["host"] as String
        extractedData["port"] = database["port"] as Integer
        extractedData["name"] = database["name"] as String

        def connString = "jdbc:postgresql://${extractedData['host']}:${extractedData['port']}/${extractedData['name']}"

        assert connString == expected

        println "✓ Connection string: ${connString}"
    }
}
