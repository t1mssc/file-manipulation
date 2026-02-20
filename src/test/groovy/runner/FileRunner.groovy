package runner

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber.class)
@CucumberOptions (
        features = "src/test/resources/features/file-reading.feature",
        glue = ["steps"],
        plugin = [
                "pretty",
                "html:target/cucumber-reports/file-reading.html",
                "json:target/cucumber-reports/file-reading.json"
        ],
        tags = "@beginner",
        monochrome = true
)
class FileRunner {
}
