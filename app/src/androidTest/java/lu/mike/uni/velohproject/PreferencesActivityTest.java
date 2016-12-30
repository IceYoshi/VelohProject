package lu.mike.uni.velohproject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PreferencesActivityTest {

    @Rule
    public ActivityTestRule<MapActivity> mapTest = new ActivityTestRule<>(MapActivity.class);
    @Rule
    public ActivityTestRule<HistoryActivity> historyTest = new ActivityTestRule<>(HistoryActivity.class);


    @Test
    public void history_clearing_test() {
        MapActivity mapActivity = mapTest.getActivity();
        HistoryActivity historyActivity = historyTest.getActivity();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mapActivity);

        boolean shouldClear = pref.getBoolean(mapActivity.getResources().getString(R.string.PREF_HISTORY_CLEAR_KEY), false);
        if(shouldClear) {
            assertTrue("History is empty", historyActivity.getJsonHistoryString() == null || historyActivity.getJsonHistoryString().isEmpty());
        }
    }

    @Test
    public void history_size_test() {
        MapActivity mapActivity = mapTest.getActivity();
        HistoryActivity historyActivity = historyTest.getActivity();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mapActivity);

        String historySize = pref.getString(mapActivity.getResources().getString(R.string.PREF_HISTORY_SIZE_KEY), "-1");
        if(!historySize.equals("-1")) {
            try {
                if(historyActivity.getJsonHistoryString() != null) {
                    int actualSize = new JSONArray(historyActivity.getJsonHistoryString()).length();
                    assertFalse("Too many history items", actualSize > Integer.parseInt(historySize));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
