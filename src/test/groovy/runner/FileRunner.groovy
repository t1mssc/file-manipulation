package runner

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber.class)
@CucumberOptions (
        features = "src/test/resources/features",
        glue = ["steps"],
        plugin = [
                "pretty",
                "html:src/test/resources/testing-reports/file-manipulation-report.html",
                "json:src/test/resources/testing-reports/file-manipulation-report.json"
        ],
        //tags = "@intermediate and @beginner",
        monochrome = true
)
class FileRunner {
}
