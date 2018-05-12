package de.ameyering.wgplaner.test;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

public class CommonSteps {
    @When("^I click button with id \"([^\"]*)\"$")
    public void iClickButtonWithText(String buttonID) throws IllegalAccessException,
        InterruptedException {
        Thread.sleep(1000);
        ViewInteraction appCompatButton = onView(withResourceName(buttonID));
        appCompatButton.perform(click());
    }

    @When("^I enter \"([^\"]*)\" into input field with id \"([^\"]*)\"$")
    public void iEnterTextIntoFieldId(String text, String fieldId) throws NoSuchFieldException {
        ViewInteraction appCompatButton = onView(withResourceName(fieldId));
        appCompatButton.perform(replaceText(text), closeSoftKeyboard());
    }

    @Then("^I should see text \"([^\"]*)\"")
    public void textIsVisible(String text) throws Exception {
        onView(withText(text)).check(matches(isDisplayed()));
    }

    @Then("^Success$")
    public void isSuccess() throws Exception {
        // Placeholder for testing the cucumber feature files
    }

    @Then("^Toast with text \"([^\"]*)\" is displayed")
    public void toastIsDisplayed(String text) throws Exception {
        final Activity[] activity = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity[0] = (Activity) ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                        Stage.RESUMED).toArray()[0];
            }
        });

        onView(withText(text)).inRoot(withDecorView(not(is(activity[0].getWindow().getDecorView())))).check(
            matches(isDisplayed()));
    }
}
