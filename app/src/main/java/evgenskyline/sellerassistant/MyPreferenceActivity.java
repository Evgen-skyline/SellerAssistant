package evgenskyline.sellerassistant;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by evgen on 14.04.2016.
 */
public class MyPreferenceActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
