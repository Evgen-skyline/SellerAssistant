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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import evgenskyline.sellerassistant.asynktasks.OverallReportTask;

public class ReportActivity extends AppCompatActivity {
    private Spinner mSpinnerMonths;
    public static TextView mTV_Report;
    private RadioButton mRB_Overall;
    private RadioButton mRB_Each;

    private SharedPreferences mSPreference;

    private String seller;

    private OverallReportTask reportTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //инициализации
        String tmpUser = getIntent().getExtras().getString(MainActivity.KEY_INTENT_EXTRA_USER);
        seller = DayEdit.reverseName(tmpUser);//Имя юзера на латинице
        mSpinnerMonths = (Spinner)findViewById(R.id.spinnerInMonthReport);
        mTV_Report = (TextView)findViewById(R.id.textViewInMonthReport);
        mRB_Overall = (RadioButton)findViewById(R.id.radioButtonOverallInReport);
        mRB_Each = (RadioButton)findViewById(R.id.radioButtonEachInReport);

        mSPreference = PreferenceManager.getDefaultSharedPreferences(this);

        //наполнение спинера
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, DayEdit.monthArr);
        mSpinnerMonths.setAdapter(arrayAdapter);
        //установка последнего выбраного месяца
        if(mSPreference.contains(DayEdit.LAST_SELECTED_MONTH)){
            mSpinnerMonths.setSelection(arrayAdapter.getPosition(mSPreference.getString(DayEdit.LAST_SELECTED_MONTH, null)));
        }
    }

    public void clickToShowReport(View view) {
        int flag = OverallReportTask.OVERALL_REPORT;
        if(mRB_Overall.isChecked()){
            flag = OverallReportTask.OVERALL_REPORT;
        }else if(mRB_Each.isChecked()){
            flag = OverallReportTask.EACH_POINT_REPORT;
        }
        reportTask = new OverallReportTask(seller, mSpinnerMonths.getSelectedItem().toString(),
                ReportActivity.this, flag);
        reportTask.execute();
    }
}
