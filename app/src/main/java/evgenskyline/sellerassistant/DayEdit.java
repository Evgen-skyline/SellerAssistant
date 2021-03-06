package evgenskyline.sellerassistant;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import evgenskyline.sellerassistant.dbwork.DB_seller;
import evgenskyline.sellerassistant.dbwork.ResultsOfTheDay;

public class DayEdit extends AppCompatActivity {
    //ui
    private TextView mTVsum;
    private Spinner spinnerInDayEdit;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private EditText mET_card;
    private EditText mET_stp;
    private EditText mET_flash;
    private EditText mET_phone;
    private EditText mET_accesories;
    private EditText mET_foto;
    private EditText mET_terminal;
    private TextView mTV_date;

    private ResultsOfTheDay resultsOfTheDay;

    //для даты
    Calendar dateCalendar;
    String dateStr;
    String dateSQL; //строка для записи в БД
    String startMessageDate;

    //for DB
    private String userName;
    private DB_seller db_seller;    //БД с таблицей юзера(userName)
    private SQLiteDatabase sl_db;

    private SharedPreferences mSPreferences;
    private Set tradePoints = new HashSet<String>();
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList;
    //private ArrayList<String> months = new ArrayList<String>();
    public static final String[] monthArr = new String[]{"Январь", "Февраль", "Март", "Апрель",
    "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    public static final String LAST_SELECTED_TP_IN_SPINNER = "lastSelectedItemInSpinner";
    public static final String LAST_SELECTED_MONTH = "lastSelectedMonth";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //инициализации
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_edit);
        mTVsum = (TextView)findViewById(R.id.textViewSummInDayEdit);
        mET_card = (EditText)findViewById(R.id.editTextCardInDayEdit);
        mET_card.addTextChangedListener(textWatcher);
        mET_stp = (EditText)findViewById(R.id.editTextSTPInDayEdit);
        mET_stp.addTextChangedListener(textWatcher);
        mET_flash = (EditText)findViewById(R.id.editTextFlashInDayEdit);
        mET_flash.addTextChangedListener(textWatcher);
        mET_phone = (EditText)findViewById(R.id.editTextPhoneInDayEdit);
        mET_phone.addTextChangedListener(textWatcher);
        mET_accesories = (EditText)findViewById(R.id.editTextAccesoriesInDayEdit);
        mET_accesories.addTextChangedListener(textWatcher);
        mET_foto = (EditText)findViewById(R.id.editTextFotoInDayEdit);
        mET_foto.addTextChangedListener(textWatcher);
        mET_terminal = (EditText)findViewById(R.id.editTextTerminalInDayEdit);
        mET_terminal.addTextChangedListener(textWatcher);
        mTV_date = (TextView) findViewById(R.id.textViewDateInDayEdit);
        startMessageDate = getString(R.string.mDATE);
        mTV_date.setText(startMessageDate);
        spinnerInDayEdit = (Spinner)findViewById(R.id.spinnerInDayEdit);
        monthSpinner = (Spinner)findViewById(R.id.mMonthSpinnerInDayEdit);
        yearSpinner = (Spinner)findViewById(R.id.DayEditYearSpinner);

        resultsOfTheDay = new ResultsOfTheDay(this);

        //так надо, чтоб время в Long совпадало
        dateCalendar = Calendar.getInstance();
        dateCalendar.set(Calendar.HOUR, 8);
        dateCalendar.set(Calendar.AM_PM, Calendar.AM);
        dateCalendar.set(Calendar.MINUTE, 1);
        dateCalendar.set(Calendar.SECOND, 1);
        dateCalendar.set(Calendar.MILLISECOND, 1);

        mSPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tmp = getIntent().getStringExtra(MainActivity.KEY_USER);
        userName =reverseName(tmp);

        //наполнение спинера торговых точек
        tradePoints = mSPreferences.getStringSet(MainActivity.APP_PREFERENCES_TP_SET, null);
        arrayList = new ArrayList<String>(tradePoints);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, arrayList);
        spinnerInDayEdit.setAdapter(arrayAdapter);
        //установка последней выбраной точки
        if(mSPreferences.contains(LAST_SELECTED_TP_IN_SPINNER)) {
            String lastSelected = mSPreferences.getString(LAST_SELECTED_TP_IN_SPINNER, null);
            if(lastSelected != null) {
                spinnerInDayEdit.setSelection(arrayAdapter.getPosition(lastSelected));//значение по умолчанию
            }
        }
        //наполнение спинера месяцев
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, monthArr);
        monthSpinner.setAdapter(monthAdapter);
        //установка последнего выбраного месяца
        if(mSPreferences.contains(LAST_SELECTED_MONTH)){
            String lstSlctd = mSPreferences.getString(LAST_SELECTED_MONTH, null);
            if(lstSlctd != null){
                monthSpinner.setSelection(monthAdapter.getPosition(lstSlctd));
            }
        }
        //наполнение спинера года
        ArrayList yearList = new ArrayList<Integer>();
        Integer currentYear = dateCalendar.get(Calendar.YEAR);
        for (int y = 2016; y <= currentYear + 1; y++){
            yearList.add(y);
        }
        ArrayAdapter yearAdapter = new ArrayAdapter<Integer>(this, R.layout.spinner_layout,
                yearList);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setSelection(yearAdapter.getPosition(currentYear));
        /*
        если меняется точка, сразу считываются на неё % и пересчитывается сумма за день
         */
        spinnerInDayEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRunTimeCount();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //а тут немного извращений
        //тестовый диалог на ввод даты
        mTV_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(DayEdit.this, dateListener,
                        dateCalendar.get(Calendar.YEAR),
                        dateCalendar.get(Calendar.MONTH),
                        dateCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

    }

    //Listener для DatePickerDialog
    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCalendar.set(Calendar.YEAR, year);
            dateCalendar.set(Calendar.MONTH, monthOfYear);
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateCalendar.set(Calendar.HOUR, 8);
            dateCalendar.set(Calendar.AM_PM, Calendar.AM);
            dateCalendar.set(Calendar.MINUTE, 1);
            dateCalendar.set(Calendar.SECOND, 1);
            dateCalendar.set(Calendar.MILLISECOND, 1);
            dateStr = DateUtils.formatDateTime(DayEdit.this,
                    dateCalendar.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
            mTV_date.setText(dateStr);
            dateSQL = String.valueOf(dateCalendar.getTimeInMillis());
        }
    };

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

    /*
    для подсчёта з/п за день рантайм,
    срабатывает после каждого изменения в любом EditText
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            mRunTimeCount();
        }
    };

    /*
    Кнопка "Сохранить"
     */
    public void clickOnSaveButtonInDayEdit(View view) { //кнопка "Сохранить"
        /*
        запрос на подтверждение сохранения
         */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.mDayChangeTitleForDayDelete);
        alertDialogBuilder.setMessage(R.string.mDayChangeMessageForDayDelete);
        alertDialogBuilder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!(mTV_date.getText().toString().equals(startMessageDate))) {
                    mSaveDataInDB();
                }else {
                    Toast.makeText(DayEdit.this, "А дату кто будет указывать???", Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences.Editor editor = mSPreferences.edit();
                editor.putString(LAST_SELECTED_TP_IN_SPINNER, spinnerInDayEdit.getSelectedItem().toString());
                editor.putString(LAST_SELECTED_MONTH, monthSpinner.getSelectedItem().toString());
                editor.apply();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRunTimeCount();
    }

    /**
     * перевод кирилицы в латинские
     * @param srcName
     * @return translit name
     */
    public static String reverseName(String srcName){
        String result = "";
        String alpha = new String("абвгдеёжзиыйклмнопрстуфхцчшщьэюя");
        String[] _alpha = {"a","b","v","g","d","e","yo","g","z","i","y","i",
                "k","l","m","n","o","p","r","s","t","u",
                "f","h","tz","ch","sh","sh","'","e","yu","ya"};
        if(srcName != "" || srcName != null){
            srcName = srcName.toLowerCase();
            StringBuilder nname = new StringBuilder("");
            char[] chs = srcName.toCharArray();
            for (int i = 0; i < chs.length; i++) {
                int k = alpha.indexOf(chs[i]);
                if (k != -1)
                    nname.append(_alpha[k]);
                else {
                    nname.append(chs[i]);
                }
            }
            result = nname.toString();
        }
        return result;
    }

    /**
     * подсчёт суммы за день рантайм и считывание сумм в ResultsOfTheDay
     */
    private void mRunTimeCount(){
        resultsOfTheDay.setNameOfTradePoint(spinnerInDayEdit.getSelectedItem().toString());
        resultsOfTheDay.setDate(dateCalendar.getTimeInMillis());
        resultsOfTheDay.setMonth(monthSpinner.getSelectedItem().toString() + yearSpinner.getSelectedItem().toString());
        resultsOfTheDay.initializePercentageFromSharedPreference();
        resultsOfTheDay.setCardSum(
                mET_card.getText().toString().equals("") ? 0 : Double.parseDouble(mET_card.getText().toString()));
        resultsOfTheDay.setStpSum(
                mET_stp.getText().toString().equals("") ? 0 : Double.parseDouble(mET_stp.getText().toString()));
        resultsOfTheDay.setPhoneSum(
                mET_phone.getText().toString().equals("")? 0 : Double.parseDouble(mET_phone.getText().toString()));
        resultsOfTheDay.setFlashSum(
                mET_flash.getText().toString().equals("")? 0 : Double.parseDouble(mET_flash.getText().toString()));
        resultsOfTheDay.setAccesSum(
                mET_accesories.getText().toString().equals("")? 0 : Double.parseDouble(mET_accesories.getText().toString()));
        resultsOfTheDay.setFotoSum(
                mET_foto.getText().toString().equals("")? 0 : Double.parseDouble(mET_foto.getText().toString()));
        resultsOfTheDay.setTermSum(
                mET_terminal.getText().toString().equals("")? 0 : Double.parseDouble(mET_terminal.getText().toString()));

        mTVsum.setText(String.valueOf(resultsOfTheDay.getSumOfZp()));
    }

    /*
    отправка данных в БД
     */
    private void mSaveDataInDB(){
        db_seller = new DB_seller(this, userName);
        sl_db = db_seller.getReadableDatabase();
        sl_db.execSQL(DB_seller.CREATE_USER_TABLE);//create table for current user, if not exist

        mRunTimeCount();//обновляем данные в resultsOfTheDay перед записью в БД

        Long returnedResult =0L;
        try {
            returnedResult = sl_db.insert(userName, null, resultsOfTheDay.getContentValuesForDbSeller());
        }catch (Exception e){
            Toast.makeText(this, "Ошибка добавления в базу" + "\n"
                    + e.toString(), Toast.LENGTH_LONG).show();
        }
        finally {
            sl_db.close();
            db_seller.close();
        }
        if(returnedResult > 0){
            Toast.makeText(this, "Сохранено", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Такая дата уже существует, НЕ СОХРАНЕНО" + "\n"
                    + "Попробуйте другую дату", Toast.LENGTH_LONG).show();
        }
    }
}
