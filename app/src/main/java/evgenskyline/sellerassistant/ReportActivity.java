package evgenskyline.sellerassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import evgenskyline.sellerassistant.asynktasks.OnTaskComplite;
import evgenskyline.sellerassistant.asynktasks.OverallReportTask;
import evgenskyline.sellerassistant.dbwork.DB_seller;
import evgenskyline.sellerassistant.dbwork.UnitFromDB;

public class ReportActivity extends AppCompatActivity {
    public static Spinner mSpinnerMonths;
    public static Spinner yearSpinner;
    public static TextView mTV_Report;
    private RadioButton mRB_Overall;
    private RadioButton mRB_Each;
    private static ScrollView mSV;
    private ListView mListView;

    public static ArrayAdapter<String> arrayAdapterMonth;

    private SharedPreferences mSPreference;

    private String seller;

    private OverallReportTask reportTask;
    private int flag;
    private static final int OVERALL_REPORT = 1;//общий отчёт
    private static final int EACH_POINT_REPORT = 2;//по каждой точке
    private ArrayList<UnitFromDB> tableFromDB;
    private ArrayAdapter<String> arrayAdapter;
    private AdapterView.AdapterContextMenuInfo info;

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
        mListView = (ListView)findViewById(R.id.reportActivityListView);
        dateCalendar = Calendar.getInstance();

        mSPreference = PreferenceManager.getDefaultSharedPreferences(this);
        //наполнение спинера месяца
        arrayAdapterMonth = new ArrayAdapter<String>(this, R.layout.spinner_layout, DayEdit.monthArr);
        mSpinnerMonths.setAdapter(arrayAdapterMonth);
        //установка последнего выбраного месяца
        if(mSPreference.contains(DayEdit.LAST_SELECTED_MONTH)){
            mSpinnerMonths.setSelection(arrayAdapterMonth.getPosition(mSPreference.getString(DayEdit.LAST_SELECTED_MONTH, null)));
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
        mTV_Report.setText("");
        makeReportFromDB();
    }

    private RadioButton.OnCheckedChangeListener checkedChangeListener = new RadioButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mTV_Report.setText("");
            makeReportFromDB();
        }
    };

    private OnTaskComplite onTaskCompliteListener = new OnTaskComplite() {
        @Override
        public void onTaskComplite(ArrayList<UnitFromDB> dbTable, double termSum, double termCash, int countWorkDay) {
            //сортируем по дате
            Collections.sort(dbTable, new Comparator<UnitFromDB>() {
                @Override
                public int compare(UnitFromDB lhs, UnitFromDB rhs) {
                    return lhs.getDate().compareTo(rhs.getDate());
                }
            });
            tableFromDB = new ArrayList<UnitFromDB>(dbTable.size());
            tableFromDB = dbTable;//для обработки в контекстном меню
            switch (flag){ //тип отчёта: общий или подробный
                case OVERALL_REPORT:
                    String report = stringForOveralReport(dbTable, termSum, termCash, countWorkDay);
                    ReportActivity.mTV_Report.setText(/*test + "\n" + */report);
                    break;
                case EACH_POINT_REPORT:
                    ArrayList<String> arrayList = new ArrayList<String>(dbTable.size()+1);
                    for (int i=0; i<dbTable.size(); i++){
                        arrayList.add(dbTable.get(i).toString());//для arrayAdapter
                    }

                    arrayAdapter = new ArrayAdapter<String>(ReportActivity.this,
                            R.layout.listview_layout, arrayList);
                    mListView.setAdapter(arrayAdapter);
                    registerForContextMenu(mListView);//вешаем на ListView контекстное меню
                    break;
                default: break;
            }
        }
    };

    /*
    контекстное меню для элементов ListView(отчёт за день)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_report_day_item, menu);
    }

    /*
    обработка выбора из контекстного меню
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.menuReportEdit:
                //открывается активность редактирования для выбраного дня
                Intent intent = new Intent(ReportActivity.this, DayChangeActivity.class);
                intent.putExtra(SellerMenu.DATE_FOR_EXTRA, tableFromDB.get(info.position).getDate());
                intent.putExtra(MainActivity.KEY_USER, seller);
                startActivity(intent);
                return true;
            case R.id.menuReportDelete:
                //диалог на удаление выбраного дня
                final Long mDate = tableFromDB.get(info.position).getDate();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Подтвердите");
                alertDialogBuilder.setMessage("Вы уверены, что хотите удалить этот день\n"
                        + DateUtils.formatDateTime(ReportActivity.this, mDate,
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
                alertDialogBuilder.setPositiveButton(R.string.mYES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DB_seller db_seller = new DB_seller(ReportActivity.this, seller);
                        SQLiteDatabase sl_db = db_seller.getReadableDatabase();
                        sl_db.execSQL(DB_seller.CREATE_USER_TABLE);

                        String where = DB_seller.DB_COLUMN_DATE + " = "
                                + String.valueOf(mDate);
                        int returnedResult = sl_db.delete(seller,  where, null);
                        sl_db.close();
                        db_seller.close();
                        if (returnedResult>0){
                            Toast.makeText(ReportActivity.this, R.string.mWasDelete, Toast.LENGTH_LONG).show();
                            makeReportFromDB();//сразу обновить список
                        }else {
                            Toast.makeText(ReportActivity.this, R.string.mNotDelete, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        return;
                    }
                });
                alertDialogBuilder.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /*
    подготовка строки для общего отчёта
     */
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

    /*
    меню для activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    обработка выбора в меню activity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menuSettings://вызов настроек из меню
                Intent intent = new Intent(ReportActivity.this, SettingsActivityPF.class);
                startActivity(intent);
                return true;
            case R.id.menuExit://ВЫХОД ИЗ ПРИЛОЖЕНИЯ
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeReportFromDB();//обновление данных
    }
}

//Toast.makeText(ReportActivity.this, "Listener is working ", Toast.LENGTH_LONG).show();