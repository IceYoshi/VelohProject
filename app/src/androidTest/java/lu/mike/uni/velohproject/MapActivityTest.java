package lu.mike.uni.velohproject;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    public void map_test() {
        MapActivity activity = activityTest.getActivity();

        assertNotNull("mMap initialized", activity.getMap());
        assertNotNull("mClusterManager initialized", activity.getClusterManager());
    }

    @Test
    public void drawer_test() {
        MapActivity activity = activityTest.getActivity();

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

        // Test if navigation drawer is initially closed
        assertFalse("Drawer initially closed", drawer.isDrawerOpen(GravityCompat.START));

        // Open navigation drawer and test if it is open
        openDrawer(R.id.drawer_layout);
        assertTrue("Drawer is open", drawer.isDrawerOpen(GravityCompat.START));

        // Request all VELOH stations
        onView(withText(activity.getResources().getString(R.string.NAV_ITEM_STATIONS_VELOH))).perform(click());
        assertTrue("Last request of type ALL_VELOH_STATIONS", activity.getLastRequest().getRequestType().equals(RequestObject.RequestType.REQUEST_ALL_VELOH_STATIONS));
    }
}
