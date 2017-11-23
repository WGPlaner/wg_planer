package de.ameyering.wgplaner.wgplaner;

import android.test.ActivityInstrumentationTestCase2;

import org.junit.After;
import org.junit.Before;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;

@CucumberOptions(
    features = "features"
)
public class TestCucumberSteps extends ActivityInstrumentationTestCase2<HomeActivity> {

    public TestCucumberSteps() {
        super(HomeActivity.class);
    }

    @Before
    public void before() {}

    @After
    public void after() {}

    @Given("^.*$")
    public void allGiven() {}

    @Then("^.*$")
    public void allThen() {}

    @When("^.*$")
    public void allWhen() {}
}
