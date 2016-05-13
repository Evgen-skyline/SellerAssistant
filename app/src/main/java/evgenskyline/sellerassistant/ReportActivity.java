package evgenskyline.sellerassistant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import evgenskyline.sellerassistant.asynktasks.OverallReportTask;

public class ReportActivity extends AppCompatActivity {
    public static Spinner mSpinnerMonths;
    public static Spinner yearSpinner;
    public static TextView mTV_Report;
    private RadioButton mRB_Overall;
    private RadioButton mRB_Each;

    public static ArrayAdapter<String> arrayAdapter;

    private SharedPreferences mSPreference;

    private String seller;

    private OverallReportTask reportTask;

    private Calendar dateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //инициализации
        String tmpUser = getIntent().getExtras().getString(MainActivity.KEY_INTENT_EXTRA_USER);
        seller = DayEdit.reverseName(tmpUser);//Имя юзера на латинице
        mSpinnerMonths = (Spinner)findViewById(R.id.spinnerInMonthReport);
        yearSpinner = (Spinner)findViewById(R.id.ReportActivityYearSpinner);
        mTV_Report = (TextView)findViewById(R.id.textViewInMonthReport);
        mRB_Overall = (RadioButton)findViewById(R.id.radioButtonOverallInReport);
        mRB_Each = (RadioButton)findViewById(R.id.radioButtonEachInReport);
        mRB_Overall.setOnCheckedChangeListener(checkedChangeListener);
        mRB_Each.setOnCheckedChangeListener(checkedChangeListener);
        dateCalendar = Calendar.getInstance();

        mSPreference = PreferenceManager.getDefaultSharedPreferences(this);

        //наполнение спинера месяца
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, DayEdit.monthArr);
        mSpinnerMonths.setAdapter(arrayAdapter);
        //установка последнего выбраного месяца
        if(mSPreference.contains(DayEdit.LAST_SELECTED_MONTH)){
            mSpinnerMonths.setSelection(arrayAdapter.getPosition(mSPreference.getString(DayEdit.LAST_SELECTED_MONTH, null)));
        }

        //наполнение спинера года
        ArrayList yearList = new ArrayList<Integer>();
        Integer currentYear = dateCalendar.get(Calendar.YEAR);
        for (int y = 2016; y <= currentYear+1; y++){
            yearList.add(y);
        }
        //Toast.makeText(this, String.valueOf(currentYear), Toast.LENGTH_LONG).show();
        ArrayAdapter yearAdapter = new ArrayAdapter<Integer>(this, R.layout.spinner_layout, yearList);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setSelection(yearAdapter.getPosition(currentYear));
    }

    private void makeReportFromDB(){
        int flag = OverallReportTask.OVERALL_REPORT;
        if(mRB_Overall.isChecked()){
            flag = OverallReportTask.OVERALL_REPORT;
        }else if(mRB_Each.isChecked()){
            flag = OverallReportTask.EACH_POINT_REPORT;
        }
        String monthAndYear = mSpinnerMonths.getSelectedItem().toString() + yearSpinner.getSelectedItem().toString();
        reportTask = new OverallReportTask(seller, monthAndYear, ReportActivity.this, flag);
        reportTask.execute();
    }

    public void clickToShowReport(View view) {
        makeReportFromDB();
    }

    private RadioButton.OnCheckedChangeListener checkedChangeListener = new RadioButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            makeReportFromDB();
        }
    };
}
