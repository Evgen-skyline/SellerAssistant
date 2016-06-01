package evgenskyline.sellerassistant;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
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
import android.widget.Button;
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
import evgenskyline.sellerassistant.dbwork.UnitFromDB;

public class DayChangeActivity extends AppCompatActivity {
    //UI
    private TextView mTVsum;
    private Spinner spinnerTradePoint;
    private Spinner spinnerMonth;
    private Spinner spinnerYear;
    private EditText mET_card;
    private EditText mET_stp;
    private EditText mET_flash;
    private EditText mET_phone;
    private EditText mET_accesories;
    private EditText mET_foto;
    private EditText mET_terminal;
    private TextView mTV_date;

    private ResultsOfTheDay resultsOfTheDay;

    private SharedPreferences mSPreferences;
    private Set tradePoints = new HashSet<String>();
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> monthAdapter;
    ArrayAdapter<Integer> yearAdapter;

    Calendar dateCalendar;
    String dateStr;
    String dateSQL;

    //for DB
    private String userName;
    private DB_seller db_seller;    //БД с таблицей юзера(userName)
    private SQLiteDatabase sl_db;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_change);

        //инициализация
        mTVsum = (TextView)findViewById(R.id.DayChangeSummTextView);
        mET_card = (EditText)findViewById(R.id.DayChangeCardEditText);
        mET_card.addTextChangedListener(textWatcher);
        mET_stp = (EditText)findViewById(R.id.DayChangeStpEditText);
        mET_stp.addTextChangedListener(textWatcher);
        mET_flash = (EditText)findViewById(R.id.DayChangeFlashEditText);
        mET_flash.addTextChangedListener(textWatcher);
        mET_phone = (EditText)findViewById(R.id.DayChangePhoneEditText);
        mET_phone.addTextChangedListener(textWatcher);
        mET_accesories = (EditText)findViewById(R.id.DayChangeAccesoriesEditText);
        mET_accesories.addTextChangedListener(textWatcher);
        mET_foto = (EditText)findViewById(R.id.DayChangeFotoEditText);
        mET_foto.addTextChangedListener(textWatcher);
        mET_terminal = (EditText)findViewById(R.id.DayChangeTerminalEditText);
        mET_terminal.addTextChangedListener(textWatcher);
        mTV_date = (TextView) findViewById(R.id.DayChangeDateTextView);
        mTV_date.setText(R.string.mDATE);
        spinnerTradePoint = (Spinner)findViewById(R.id.DayChangePointsSpinner);
        spinnerMonth = (Spinner)findViewById(R.id.DayChangeMonthSpinner);
        spinnerYear = (Spinner)findViewById(R.id.DayChangeYearSpinner);

        resultsOfTheDay = new ResultsOfTheDay(this);

        //так надо, чтоб время в Long совпадало
        dateCalendar = Calendar.getInstance();
        dateCalendar.set(Calendar.HOUR, 8);
        dateCalendar.set(Calendar.AM_PM, Calendar.AM);
        dateCalendar.set(Calendar.MINUTE, 1);
        dateCalendar.set(Calendar.SECOND, 1);
        dateCalendar.set(Calendar.MILLISECOND, 1);

        dateCalendar.setTimeInMillis(getIntent().getLongExtra(SellerMenu.DATE_FOR_EXTRA, System.currentTimeMillis()));
        mTV_date.setText(DateUtils.formatDateTime(DayChangeActivity.this,
                dateCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));

        userName = getIntent().getStringExtra(MainActivity.KEY_USER);
        userName = DayEdit.reverseName(userName);//перевод на латинские

        mSPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //наполнение спинера торговых точек
        tradePoints = mSPreferences.getStringSet(MainActivity.APP_PREFERENCES_TP_SET, null);
        arrayList = new ArrayList<String>(tradePoints);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, arrayList);
        spinnerTradePoint.setAdapter(arrayAdapter);

        //наполнение спинера месяцев
        monthAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, DayEdit.monthArr);
        spinnerMonth.setAdapter(monthAdapter);

        //наполнение спинера года
        ArrayList yearList = new ArrayList<Integer>();
        Calendar tmpCalendar = Calendar.getInstance();
        Integer currentYear = tmpCalendar.get(Calendar.YEAR);
        for (int y = 2016; y <= currentYear+1; y++){
            yearList.add(y);
        }
        yearAdapter = new ArrayAdapter<Integer>(this, R.layout.spinner_layout, yearList);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(yearAdapter.getPosition(currentYear));

        /*
        если меняется точка, сразу считываются на неё % и пересчитывается сумма за день
         */
        spinnerTradePoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRunTimeCount();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mSetDataFromDbToEditText();
    }

     /*
     установка значений из базы в эдит-поля
     */
    private void mSetDataFromDbToEditText(){
        DB_seller db_seller = new DB_seller(this, userName);    //БД с таблицей юзера(userName)
        SQLiteDatabase sl_db = db_seller.getReadableDatabase();
        String query;
        query = "Select * from " + userName + " where " + DB_seller.DB_COLUMN_DATE + " = "
                + String.valueOf(dateCalendar.getTimeInMillis());
        Cursor mCursor = sl_db.rawQuery(query, null);
        mCursor.moveToFirst();
        spinnerTradePoint.setSelection(arrayAdapter.getPosition(mCursor.getString(mCursor.getColumnIndex(
                DB_seller.DB_COLUMN_TRADE_POINT))));

        //в БД месяц записан в формате "март2016", т.е. месяц+год
        String monthFromDB = mCursor.getString(mCursor.getColumnIndex(
                DB_seller.DB_COLUMN_MONTH));
        StringBuilder yearBuilder = new StringBuilder();
        StringBuilder monthBuilder = new StringBuilder();
        int i = monthFromDB.indexOf("2");
        yearBuilder.append(monthFromDB.substring(i));
        monthBuilder.append(monthFromDB.substring(0,i));
        int year = Integer.parseInt(yearBuilder.toString());
        monthFromDB = monthBuilder.toString();
        spinnerMonth.setSelection(monthAdapter.getPosition(monthFromDB));
        spinnerYear.setSelection(yearAdapter.getPosition(year));

        //Toast.makeText(this, monthFromDB, Toast.LENGTH_LONG).show();
        mET_card.setText(String.valueOf(mCursor.getDouble(mCursor.getColumnIndex(
                DB_seller.DB_COLUMN_SALES_CARD))));
        mET_stp.setText(String.valueOf(mCursor.getDouble(mCursor.getColumnIndex(
                DB_seller.DB_COLUMN_SALES_STP))));
        mET_phone.setText(String.valueOf(mCursor.getDouble(mCursor.getColumnIndex(
                DB_seller.DB_COLUMN_SALES_PHONE))));
        mET_flash.setText(String.valueOf(mCursor.getDouble(mCursor.getColumnIndex(
                DB_seller.DB_COLUMN_SALES_FLASH))));
        mET_accesories.setText(String.valueOf(mCursor.getDouble(mCursor.getColumnIndex(
                DB_seller.DB_COLUMN_SALES_ACCESORIES))));
        mET_foto.setText(String.valueOf(mCursor.getDouble(mCursor.getColumnIndex(
                DB_seller.DB_COLUMN_SALES_FOTO))));
        mET_terminal.setText(String.valueOf(mCursor.getDouble(mCursor.getColumnIndex(
                DB_seller.DB_COLUMN_SALES_TERM))));

        id = String.valueOf(mCursor.getString(mCursor.getColumnIndex(BaseColumns._ID)));

        mCursor.close();
        sl_db.close();
        db_seller.close();
        mRunTimeCount();
    }

    /*
    обработка клика по дате
     */
    public void clickOnDateTextView(View view) {
        DatePickerDialog dpd = new DatePickerDialog(DayChangeActivity.this, dateListener,
                dateCalendar.get(Calendar.YEAR),
                dateCalendar.get(Calendar.MONTH),
                dateCalendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    /*
    Listener для DatePickerDialog
     */
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
            dateStr = DateUtils.formatDateTime(DayChangeActivity.this,
                    dateCalendar.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
            mTV_date.setText(dateStr);
            dateSQL = String.valueOf(dateCalendar.getTimeInMillis());
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //mInicializationPercents();
        mRunTimeCount();
    }

    /*
    прикручиваем меню
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    обработка выбора в меню
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menuSettings://вызов настроек из меню
                Intent intent = new Intent(DayChangeActivity.this, SettingsActivityPF.class);
                startActivity(intent);
                return true;
            case R.id.menuExit://ВЫХОД ИЗ ПРИЛОЖЕНИЯ
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    кнопка "Сохранить"
     */
    public void clickOnSaveButtonInDayChange(View view) {
         /*
        запрос на подтверждение сохранения
         */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Вы всё правильно ввели?");
        alertDialogBuilder.setMessage("Сохранить данные?");
        alertDialogBuilder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!(mTV_date.getText().toString().equals("Дата: "))) {
                    mSaveDataInDB();
                }else {
                    Toast.makeText(DayChangeActivity.this, "А дату кто будет указывать???", Toast.LENGTH_LONG).show();
                    return;
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
    }

    /*
    непосредственное сохранение в базу
     */
    private void mSaveDataInDB(){
        db_seller = new DB_seller(this, userName);
        sl_db = db_seller.getReadableDatabase();
        sl_db.execSQL(DB_seller.CREATE_USER_TABLE);//create table for current user, if not exist

        mRunTimeCount();

        int returnedResult =0;
        try {
            String where = BaseColumns._ID + " = " + id;
            returnedResult = sl_db.update(userName, resultsOfTheDay.getContentValuesForDbSeller(), where, null);
        }catch (Exception e){
            Toast.makeText(this, "Какая-то ошибка при добавлении в базу" + "\n"
                    + e.toString(), Toast.LENGTH_LONG).show();
        }
        finally {
            sl_db.close();
            db_seller.close();
        }
        if(returnedResult > 0){
            Toast.makeText(this, "Сохранено", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Такая дата уже существует, НЕ СОХРАНЕНО" + "\n"
                    + "Попробуйте другую дату", Toast.LENGTH_LONG).show();
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
    подсчёт суммы за день рантайм
     */
    private void mRunTimeCount(){

        resultsOfTheDay.setNameOfTradePoint(spinnerTradePoint.getSelectedItem().toString());
        resultsOfTheDay.setDate(dateCalendar.getTimeInMillis());
        resultsOfTheDay.setMonth(spinnerMonth.getSelectedItem().toString() + spinnerYear.getSelectedItem().toString());
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
    Кнопка "Удалить день"
     */
    public void clickForDayDelete(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Подтвердите");
        alertDialogBuilder.setMessage("Вы уверены, что хотите удалить этот день\n"
                +DateUtils.formatDateTime(DayChangeActivity.this, dateCalendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        alertDialogBuilder.setPositiveButton(R.string.mYES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!(mTV_date.getText().toString().equals(R.string.mDATE))) {
                    db_seller = new DB_seller(DayChangeActivity.this, userName);
                    sl_db = db_seller.getReadableDatabase();
                    sl_db.execSQL(DB_seller.CREATE_USER_TABLE);

                    String where = DB_seller.DB_COLUMN_DATE + " = " + String.valueOf(dateCalendar.getTimeInMillis());
                    int returnedResult = sl_db.delete(userName,  where, null);
                    sl_db.close();
                    db_seller.close();
                    if (returnedResult>0){
                        Toast.makeText(DayChangeActivity.this, R.string.mWasDelete, Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(DayChangeActivity.this, R.string.mNotDelete, Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(DayChangeActivity.this, "А дату кто будет указывать???", Toast.LENGTH_LONG).show();
                    return;
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
    }
}
