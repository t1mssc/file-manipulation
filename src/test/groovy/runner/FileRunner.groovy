package runner

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber.class)
@CucumberOptions (
        features = "src/test/resources/features/",
        glue = ["steps"],
        plugin = [
                "pretty",
                "html:src/test/resources/cucumber-reports/file-manipulation-report.html",
                "json:src/test/resources/cucumber-reports/file-manipulation-report.json"
        ],
        tags = "@intermediate",
        monochrome = true
)
class FileRunner {
}
