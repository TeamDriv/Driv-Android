package accenture.driv;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by DJ on 6/28/2015.
 */
public class PrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String category = getArguments().getString("category");
        if (category != null) {
            if (category.equals("category_settings")) {
                addPreferencesFromResource(R.xml.pref_settings);
            } else if (category.equals("category_phoneprefs")) {
                addPreferencesFromResource(R.xml.pref_phone);
            }
        }
    }
}