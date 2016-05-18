package evgenskyline.sellerassistant;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import evgenskyline.sellerassistant.asynktasks.OnTaskComplite;
import evgenskyline.sellerassistant.asynktasks.OverallReportTask;
import evgenskyline.sellerassistant.asynktasks.ReportFragment;
import evgenskyline.sellerassistant.dbwork.UnitFromDB;

public class ReportActivity extends AppCompatActivity {
    public static Spinner mSpinnerMonths;
    public static Spinner yearSpinner;
    public static TextView mTV_Report;
    private RadioButton mRB_Overall;
    private RadioButton mRB_Each;
    private static ScrollView mSV;

    public static ArrayAdapter<String> arrayAdapter;

    private SharedPreferences mSPreference;

    private String seller;

    private OverallReportTask reportTask;
    private int flag;
    private static final int OVERALL_REPORT = 1;//общий отчёт
    private static final int EACH_POINT_REPORT = 2;//по каждой точке

    private Calendar dateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //инициализации
        String tmpUser = getIntent().getExtras().getString(MainActivity.KEY_USER);
        seller = DayEdit.reverseName(tmpUser);//Имя юзера на латинице
        mSpinnerMonths = (Spinner)findViewById(R.id.spinnerInMonthReport);
        yearSpinner = (Spinner)findViewById(R.id.ReportActivityYearSpinner);
        mTV_Report = (TextView)findViewById(R.id.textViewInMonthReport);
        mRB_Overall = (RadioButton)findViewById(R.id.radioButtonOverallInReport);
        mRB_Each = (RadioButton)findViewById(R.id.radioButtonEachInReport);
        mRB_Overall.setOnCheckedChangeListener(checkedChangeListener);
        mRB_Each.setOnCheckedChangeListener(checkedChangeListener);
        mSV = (ScrollView)findViewById(R.id.ReportActivityScrollView);
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
        ArrayAdapter yearAdapter = new ArrayAdapter<Integer>(this, R.layout.spinner_layout, yearList);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setSelection(yearAdapter.getPosition(currentYear));
    }

    private void makeReportFromDB(){
        flag = OVERALL_REPORT;
        if(mRB_Overall.isChecked()){
            flag = OVERALL_REPORT;
        }else if(mRB_Each.isChecked()){
            flag = EACH_POINT_REPORT;
        }
        String monthAndYear = mSpinnerMonths.getSelectedItem().toString() + yearSpinner.getSelectedItem().toString();
        reportTask = new OverallReportTask(seller, monthAndYear, ReportActivity.this);
        reportTask.setOnTaskCompliteListener(onTaskCompliteListener);
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

    private OnTaskComplite onTaskCompliteListener = new OnTaskComplite() {
        @Override
        public void onTaskComplite(ArrayList<UnitFromDB> dbTable, double termSum, double termCash, int countWorkDay) {
            switch (flag){
                case OVERALL_REPORT:
                    String report = stringForOveralReport(dbTable, termSum, termCash, countWorkDay);
                    ReportActivity.mTV_Report.setText(/*test + "\n" + */report);
                    break;
                case EACH_POINT_REPORT:
                    try {
                        StringBuffer result = new StringBuffer();
                        for (int i = 0; i < dbTable.size(); i++) {
                            result.append(dbTable.get(i).toString());
                        }
                        ReportActivity.mTV_Report.setText(result.toString());
                    }catch (Exception e){
                        ReportActivity.mTV_Report.setText(e.toString());
                    };

                   /* LinearLayout linearLayout = (LinearLayout)findViewById(R.id.reportActivityLinearLayoutMain);

                    for (int i = 0; i < dbTable.size(); i++) {
                        android.app.FragmentTransaction mFTrans = getFragmentManager().beginTransaction();
                        //FrameLayout mFL = new FrameLayout(ReportActivity.this);
                        //linearLayout.addView(mFL);

                        ReportFragment reportFragment = new ReportFragment();
                        reportFragment.setText(dbTable.get(i).toString());

                        mFTrans.add(R.id.reportActivityLinearLayoutMain, reportFragment);
                        mFTrans.commit();

                    }*/
                    //mSV.addView(linearLayout);
                    break;
                default: break;
            }
        }
    };

    private String stringForOveralReport(ArrayList<UnitFromDB> dbTable, double termSum, double termCash, int countWorkDay){
        double cardResult=0;
        double stpResult=0;
        double phoneResult=0;
        double flashResult=0;
        double accesResult=0;
        double fotoResult=0;
        double termResult=0;
        double cardZpResult=0;
        double stpZpResult=0;
        double phoneZpResult=0;
        double flashZpResult=0;
        double accesZpResult=0;
        double fotoZpResult=0;
        double termZpResult=0;
        for (int i = 0; i < dbTable.size(); i++) {
            cardResult += dbTable.get(i).getCardSum();
            stpResult += dbTable.get(i).getStpSum();
            phoneResult += dbTable.get(i).getPhoneSum();
            flashResult += dbTable.get(i).getFlashSum();
            accesResult += dbTable.get(i).getAccesSum();
            fotoResult += dbTable.get(i).getFotoSum();
            termResult += dbTable.get(i).getTermSum();
            cardZpResult += dbTable.get(i).getCardZP();
            stpZpResult += dbTable.get(i).getStpZP();
            phoneZpResult += dbTable.get(i).getPhoneZP();
            flashZpResult += dbTable.get(i).getFlashZP();
            accesZpResult += dbTable.get(i).getAccesZP();
            fotoZpResult += dbTable.get(i).getFotoZP();
            termZpResult += dbTable.get(i).getTermZP();
        }
        double zpSumWithoutTerm = cardZpResult + stpZpResult + phoneZpResult
                + flashZpResult + accesZpResult + fotoZpResult;
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("З/П без терминала: " + String.valueOf(zpSumWithoutTerm) + "\n");
        strBuilder.append("Терминал за " + ReportActivity.mSpinnerMonths.getSelectedItem().toString()
                + ": " + String.valueOf(termSum) + "\n");
        strBuilder.append("Всего: " + String.valueOf((zpSumWithoutTerm+termSum)) + "\n\n");
        strBuilder.append("Кол-во рабочих дней: " + countWorkDay + "\n");
        strBuilder.append("Средняя з/п за день: " + String.valueOf((zpSumWithoutTerm+termSum)/countWorkDay)+"\n\n");
        strBuilder.append("Товаров продано: \n");
        strBuilder.append("Карточек на: " + String.valueOf(cardResult) + "\n");
        strBuilder.append("Ст.п. на: " + String.valueOf(stpResult) + "\n");
        strBuilder.append("Телефонов на: " + String.valueOf(phoneResult) + "\n");
        strBuilder.append("Флешек и microSD на: " + String.valueOf(flashResult) + "\n");
        strBuilder.append("Аксессуаров на: " + String.valueOf(accesResult) + "\n");
        strBuilder.append("Фото на: " + String.valueOf(fotoResult) + "\n");
        strBuilder.append("Терминал: " + String.valueOf(termCash) + "\n");

        return strBuilder.toString();
    }

}

