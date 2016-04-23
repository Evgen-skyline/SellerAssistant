package evgenskyline.sellerassistant;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivityPF extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.pref_general);
            //setHasOptionsMenu(true);
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));
            //==================================================================
            SharedPreferences mTP_Pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = mTP_Pref.edit();
            Set<String> tradePointsSet = new HashSet<String>();
            tradePointsSet = mTP_Pref.getStringSet(MainActivity.APP_PREFERENCES_TP_SET, null);
            if (tradePointsSet == null){
                return;
            }

            PreferenceScreen rootPS = getPreferenceManager().createPreferenceScreen(getActivity());

            Iterator<String> itr = tradePointsSet.iterator();
            while (itr.hasNext()){
                String tpName = itr.next().toString();
                PreferenceScreen ps = getPreferenceManager().createPreferenceScreen(getActivity());
                ps.setTitle(tpName.toUpperCase());
                ps.setSummary("настройки % для " + tpName);
                ps.setKey(tpName + "_KEY");

                EditTextPreference etpCard = new EditTextPreference(getActivity());
                etpCard.setKey(tpName + MainActivity.TP_CARD);
                etpCard.setTitle("Карточки");
                etpCard.setSummary("% для карточек");
                //etpCard.setDefaultValue((float)1.4);
                ps.addPreference(etpCard);

                EditTextPreference etpStp = new EditTextPreference(getActivity());
                etpStp.setKey(tpName + MainActivity.TP_STP);
                etpStp.setTitle("Стартовые пакеты");
                etpStp.setSummary("% для стратовых пакетов");
                //etpStp.setDefaultValue((float)7);
                ps.addPreference(etpStp);

                EditTextPreference etpFlash = new EditTextPreference(getActivity());
                etpFlash.setKey(tpName + MainActivity.TP_FLASH);
                etpFlash.setTitle("Флешки");
                etpFlash.setSummary("% для флешек и карт памяти");
                //etpFlash.setDefaultValue((float)7);
                ps.addPreference(etpFlash);

                EditTextPreference etpPhone = new EditTextPreference(getActivity());
                etpPhone.setKey(tpName + MainActivity.TP_PHONE);
                etpPhone.setTitle("Телефоны");
                etpPhone.setSummary("% для телефонов");
                //etpPhone.setDefaultValue((float)2);
                ps.addPreference(etpPhone);

                EditTextPreference etpAcces = new EditTextPreference(getActivity());
                etpAcces.setKey(tpName + MainActivity.TP_ACCESORIES);
                etpAcces.setTitle("Аксессуары");
                etpAcces.setSummary("% для аксессуаров");
                //etpAcces.setDefaultValue((float)15);
                ps.addPreference(etpAcces);

                EditTextPreference etpFoto = new EditTextPreference(getActivity());
                etpFoto.setKey(tpName + MainActivity.TP_FOTO);
                etpFoto.setTitle("Фото");
                etpFoto.setSummary("% для фото-товаров");
                //etpFoto.setDefaultValue((float)15);
                ps.addPreference(etpFoto);

                EditTextPreference etpTerm = new EditTextPreference(getActivity());
                etpTerm.setKey(tpName + MainActivity.TP_TERM);
                etpTerm.setTitle("Терминал");
                etpTerm.setSummary("% для терминала");
                //etpTerm.setDefaultValue((float)3);
                ps.addPreference(etpTerm);

                rootPS.addPreference(ps);
            }
            this.setPreferenceScreen(rootPS);

            /*CheckBoxPreference cbp = new CheckBoxPreference(getActivity());
            cbp.setKey("key");
            cbp.setTitle("CheckBox");
            cbp.setSummary("Check Box for example");
            rootPS.addPreference(cbp);

            PreferenceScreen ps2 = getPreferenceManager().createPreferenceScreen(getActivity());
            ps2.setKey("ps2");
            ps2.setTitle("TesScreen");
            ps2.setSummary("Заготовка под торговые точки");

            CheckBoxPreference cbp2 = new CheckBoxPreference(getActivity());
            cbp2.setKey("key2");
            cbp2.setTitle("CheckBox2");
            cbp2.setSummary("Check Box in new Screen");
            ps2.addPreference(cbp2);

            rootPS.addPreference(ps2);*/
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivityPF.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
