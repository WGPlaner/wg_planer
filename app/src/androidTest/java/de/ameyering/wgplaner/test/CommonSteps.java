package de.ameyering.wgplaner.test;

import android.app.Activity;
import android.content.Context;
import android.support.test.espresso.ViewInteraction;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.Locale;

import de.ameyering.wgplaner.wgplaner.structure.Money;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.ImageStore;
import de.ameyering.wgplaner.wgplaner.utils.ServerCalls;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class CommonSteps {
    public CommonSteps() {
    }

    @When("^I click button with id \"([^\"]*)\"$")
    public void i_click_button_with_text(String buttonID) throws IllegalAccessException,
        InterruptedException {
        Thread.sleep(1000);
        ViewInteraction appCompatButton = onView(withResourceName(buttonID));
        appCompatButton.perform(click());
    }

    @When("^I enter \"([^\"]*)\" into input field with id \"([^\"]*)\"$")
    public void i_enter_text_into_field_id(String text, String fieldId) throws NoSuchFieldException {
        ViewInteraction appCompatButton = onView(withResourceName(fieldId));
        appCompatButton.perform(replaceText(text), closeSoftKeyboard());
    }

    @Then("^I should see text \"([^\"]*)\"")
    public void text_is_visible(String text) throws Exception {
        onView(withText(text)).check(matches(isDisplayed()));
    }

    @Then("^Success$")
    public void is_success() throws Exception {
        // Placeholder for testing the cucumber feature files
    }
}
