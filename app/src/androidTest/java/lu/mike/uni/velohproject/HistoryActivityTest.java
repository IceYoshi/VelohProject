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

@RunWith(AndroidJUnit4.class)
public class HistoryActivityTest {
    @Rule
    public ActivityTestRule<HistoryActivity> activityTest = new ActivityTestRule<>(HistoryActivity.class);

    @Test
    public void visibility_test() {
        onView(withId(R.id.idHistorySpinner)).check(matches(isDisplayed()));
        onView(withId(R.id.idListViewHistory)).check(matches(isDisplayed()));
    }
}
