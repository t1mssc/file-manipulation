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
                "html:target/cucumber-reports/file-manipulation-report.html",
                "json:target/cucumber-reports/file-manipulation-report.json"
        ],
        //tags = "@intermediate and @beginner",
        monochrome = true
)
class FileRunner {
}
