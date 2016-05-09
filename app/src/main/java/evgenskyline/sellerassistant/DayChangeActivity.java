package evgenskyline.sellerassistant;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
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

    //% по позициям
    private String TPname;
    private double cardPercent;
    private double stpPercent;
    private double phonePercent;
    private double flashPercent;
    private double accesPercent;
    private double fotoPercent;
    private double termPercent;

    //для содержимого EditText
    private double card_D=0;
    private double stp_D=0;
    private double phone_D=0;
    private double flash_D=0;
    private double acces_D=0;
    private double foto_D=0;
    private double term_D=0;

    private SharedPreferences mSPreferences;
    private Set tradePoints = new HashSet<String>();
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList;

    Calendar dateCalendar;
    String dateStr;
    String dateSQL;

    //for DB
    private String userName;
    private DB_seller db_seller;    //БД с таблицей юзера(userName)
    private SQLiteDatabase sl_db;

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
        spinnerTradePoint = (Spinner)findViewById(R.id.DayChangePointsSpinner);
        spinnerMonth = (Spinner)findViewById(R.id.DayChangeMonthSpinner);
        spinnerYear = (Spinner)findViewById(R.id.DayChangeYearSpinner);

        dateCalendar = Calendar.getInstance();
        dateCalendar.setTimeInMillis(getIntent().getLongExtra(SellerMenu.dateForExtra, System.currentTimeMillis()));
        mTV_date.setText(DateUtils.formatDateTime(DayChangeActivity.this,
                dateCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));

        userName = getIntent().getStringExtra(MainActivity.KEY_INTENT_EXTRA_USER);
        userName = DayEdit.reverseName(userName);//перевод на латинские

        mSPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //наполнение спинера торговых точек
        tradePoints = mSPreferences.getStringSet(MainActivity.APP_PREFERENCES_TP_SET, null);
        arrayList = new ArrayList<String>(tradePoints);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        spinnerTradePoint.setAdapter(arrayAdapter);
        //установка последней выбраной точки
        if(mSPreferences.contains(DayEdit.LAST_SELECTED_TP_IN_SPINNER)) {
            String lastSelected = mSPreferences.getString(DayEdit.LAST_SELECTED_TP_IN_SPINNER, null);
            if(lastSelected != null) {
                spinnerTradePoint.setSelection(arrayAdapter.getPosition(lastSelected));//значение по умолчанию
            }
        }

        //наполнение спинера месяцев
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, DayEdit.monthArr);
        spinnerMonth.setAdapter(monthAdapter);
        //установка последнего выбраного месяца
        if(mSPreferences.contains(DayEdit.LAST_SELECTED_MONTH)){
            String lstSlctd = mSPreferences.getString(DayEdit.LAST_SELECTED_MONTH, null);
            if(lstSlctd != null){
                spinnerMonth.setSelection(monthAdapter.getPosition(lstSlctd));
            }
        }

        //наполнение спинера года
        ArrayList yearList = new ArrayList<Integer>();
        Calendar tmpCalendar = Calendar.getInstance();
        Integer currentYear = tmpCalendar.get(Calendar.YEAR);
        for (int y = 2016; y <= currentYear; y++){
            yearList.add(y);
        }
        ArrayAdapter yearAdapter = new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item,
                yearList);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(yearAdapter.getPosition(currentYear));

        /*
        если меняется точка, сразу считываются на неё % и пересчитывается сумма за день
         */
        spinnerTradePoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mInicializationPercents();
                mRunTimeCount();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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
    //Listener для DatePickerDialog
    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCalendar.set(Calendar.YEAR, year);
            dateCalendar.set(Calendar.MONTH, monthOfYear);
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
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
        mInicializationPercents();
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
    обработка выбора в контекстном меню
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
        double resultSum;
        TPname = spinnerTradePoint.getSelectedItem().toString();
        try {
            String cardTMP = mET_card.getText().toString();
            String stpTMP = mET_stp.getText().toString();
            String phoneTMP = mET_phone.getText().toString();
            String flashTMP = mET_flash.getText().toString();
            String accesTMP = mET_accesories.getText().toString();
            String fotoTMP = mET_foto.getText().toString();
            String termTMP = mET_terminal.getText().toString();

            card_D = cardTMP.equals("") ? 0 : Double.parseDouble(cardTMP);
            stp_D = stpTMP.equals("") ? 0 : Double.parseDouble(stpTMP);
            phone_D = phoneTMP.equals("") ? 0 : Double.parseDouble(phoneTMP);
            flash_D = flashTMP.equals("") ? 0 : Double.parseDouble(flashTMP);
            acces_D = accesTMP.equals("") ? 0 : Double.parseDouble(accesTMP);
            foto_D = fotoTMP.equals("") ? 0 : Double.parseDouble(fotoTMP);
            term_D = termTMP.equals("") ? 0 : Double.parseDouble(termTMP);

            resultSum = (card_D * (cardPercent/100))
                    +(stp_D * (stpPercent/100))
                    +(phone_D * (phonePercent/100))
                    +(flash_D * (flashPercent/100))
                    +(acces_D * (accesPercent/100))
                    +(foto_D * (fotoPercent/100))
                    +(term_D * (termPercent/100));

            mTVsum.setText(String.valueOf(resultSum));
        }catch (Exception e){
            //просто чтоб не выкидывало
            mTVsum.setText("ERROR in runtime count");
        }
    }

    /*
    считывание % из настроек
     */
    private void mInicializationPercents(){
        TPname = spinnerTradePoint.getSelectedItem().toString();
        try {
            cardPercent = Double.parseDouble(mSPreferences.getString(TPname + MainActivity.TP_CARD, null));
            stpPercent = Double.parseDouble(mSPreferences.getString(TPname + MainActivity.TP_STP, null));
            phonePercent = Double.parseDouble(mSPreferences.getString(TPname + MainActivity.TP_PHONE, null));
            flashPercent = Double.parseDouble(mSPreferences.getString(TPname + MainActivity.TP_FLASH, null));
            accesPercent = Double.parseDouble(mSPreferences.getString(TPname + MainActivity.TP_ACCESORIES, null));
            fotoPercent = Double.parseDouble(mSPreferences.getString(TPname + MainActivity.TP_FOTO, null));
            termPercent = Double.parseDouble(mSPreferences.getString(TPname + MainActivity.TP_TERM, null));
        }catch (Exception e){
            Toast.makeText(this, "Проблеммы с чтением настроек %" + "\n"
                    + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
