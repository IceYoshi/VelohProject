package lu.mike.uni.velohproject;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {
    @Rule
    public ActivityTestRule<MapActivity> activityTest = new ActivityTestRule<>(MapActivity.class);

    @Test
    public void visibility_test() {
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void initial_config_test() {
        MapActivity activity = activityTest.getActivity();

        assertNotNull("mMap initialized", activity.mMap);
        assertNotNull("mClusterManager initialized", activity.mClusterManager);
    }
}
