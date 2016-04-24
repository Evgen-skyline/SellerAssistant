package evgenskyline.sellerassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DayEdit extends AppCompatActivity {
    private TextView mTVsum;
    private Spinner spinnerInDayEdit;

    private SharedPreferences mSPreferences;
    private Set<String> tradePoints = new HashSet<String>();
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //инициализации
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_edit);
        mTVsum = (TextView)findViewById(R.id.textViewSummInDayEdit);
        //mTVsum.setText(getIntent().getStringExtra(MainActivity.KEY_INTENT_EXTRA_USER));
        spinnerInDayEdit = (Spinner)findViewById(R.id.spinnerInDayEdit);
        mSPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //наполнение спинера
        tradePoints = mSPreferences.getStringSet(MainActivity.APP_PREFERENCES_TP_SET, null);
        arrayList = new ArrayList<String>(tradePoints);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        spinnerInDayEdit.setAdapter(arrayAdapter);
        spinnerInDayEdit.setPrompt("КР7");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menuSettings:
                Intent intent = new Intent(DayEdit.this, SettingsActivityPF.class);
                startActivity(intent);
                return true;
            case R.id.menuExit:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
