package de.ameyering.wgplaner.wgplaner;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ameyering.wgplaner.wgplaner.section.home.Home;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/**
 * Created by D067867 on 17.10.2017.
 */

@RunWith(AndroidJUnit4.class)
public class HomeInstrumentedTest {

    @Rule
    public ActivityTestRule<Home> mActivityRule =
        new ActivityTestRule<>(Home.class);

    @Test
    public void testNavigation() throws Exception{
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_dashboard));
    }
}
