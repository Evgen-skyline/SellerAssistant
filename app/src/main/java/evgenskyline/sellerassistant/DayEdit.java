package evgenskyline.sellerassistant;

import android.app.DatePickerDialog;
import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import evgenskyline.sellerassistant.dbwork.DB_seller;

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
    private Button saveButton;

    //для даты
    Calendar dateCalendar;
    String dateStr;
    String dateSQL; //строка для записи в БД

    //for DB
    private String userName;
    private DB_seller db_seller;    //БД с таблицей юзера(userName)
    private SQLiteDatabase sl_db;

    private SharedPreferences mSPreferences;
    private Set<String> tradePoints = new HashSet<String>();
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList;
    //private ArrayList<String> months = new ArrayList<String>();
    public static String[] monthArr = new String[]{"Январь", "Февраль", "Март", "Апрель",
    "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    private static final String LAST_SELECTED_TP_IN_SPINNER = "lastSelectedItemInSpinner";
    public static final String LAST_SELECTED_MONTH = "lastSelectedMonth";

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

    //private boolean confirmFlag = false;

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
        //mET_date = (EditText)findViewById(R.id.editTextDateInDayEdit);
        mTV_date = (TextView) findViewById(R.id.textViewDateInDayEdit);
        saveButton = (Button)findViewById(R.id.buttonSaveInDayEdit);
        spinnerInDayEdit = (Spinner)findViewById(R.id.spinnerInDayEdit);
        monthSpinner = (Spinner)findViewById(R.id.mMonthSpinnerInDayEdit);
        yearSpinner = (Spinner)findViewById(R.id.DayEditYearSpinner);
        dateCalendar = Calendar.getInstance();

        mSPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String tmp = getIntent().getStringExtra(MainActivity.KEY_INTENT_EXTRA_USER);
        userName =reverseName(tmp);
        //mTVsum.setText(userName);

        //наполнение спинера торговых точек
        tradePoints = mSPreferences.getStringSet(MainActivity.APP_PREFERENCES_TP_SET, null);
        arrayList = new ArrayList<String>(tradePoints);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        spinnerInDayEdit.setAdapter(arrayAdapter);
        //установка последней выбраной точки
        if(mSPreferences.contains(LAST_SELECTED_TP_IN_SPINNER)) {
            String lastSelected = mSPreferences.getString(LAST_SELECTED_TP_IN_SPINNER, null);
            if(lastSelected != null) {
                spinnerInDayEdit.setSelection(arrayAdapter.getPosition(lastSelected));//значение по умолчанию
            }
        }
        //наполнение спинера месяцев
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, monthArr);
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
        for (int y = 2016; y <= currentYear; y++){
            yearList.add(y);
        }
        //Toast.makeText(this, String.valueOf(currentYear), Toast.LENGTH_LONG).show();
        ArrayAdapter yearAdapter = new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item,
                yearList);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setSelection(yearAdapter.getPosition(currentYear));
        /*
        если меняется точка, сразу считываются на неё % и пересчитывается сумма за день
         */
        spinnerInDayEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mInicializationPercents();
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
    //==============================================================================================
    //Listener для DatePickerDialog
    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCalendar.set(Calendar.YEAR, year);
            dateCalendar.set(Calendar.MONTH, monthOfYear);
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateStr = DateUtils.formatDateTime(DayEdit.this,
                    dateCalendar.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
            mTV_date.setText(dateStr);
            dateSQL = String.valueOf(dateCalendar.getTimeInMillis());
        }
    };
    //==============================================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //==============================================================================================
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
    //==============================================================================================
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
    //==============================================================================================
    /*
    сохранение данных в базу
     */
    public void clickOnSaveButtonInDayEdit(View view) { //кнопка "Сохранить"
        /*
        запрос на подтверждение сохранения
         */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Вы всё правильно ввели?");
        alertDialogBuilder.setMessage("Сохранить данные?");
        alertDialogBuilder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSaveDataInDB();
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
    //==============================================================================================
    @Override
    protected void onResume() {
        super.onResume();
        mInicializationPercents();
        mRunTimeCount();
    }
    //==============================================================================================
    public static String reverseName(String srcName){//перевод кирилицы в латинские
        String result = "";
        String alpha = new String("абвгдеёжзиыйклмнопрстуфхцчшщьэюя");
        String[] _alpha = {"a","b","v","g","d","e","yo","g","z","i","y","i",
                "k","l","m","n","o","p","r","s","t","u",
                "f","h","tz","ch","sh","sh","'","e","yu","ya"};
        if(srcName != "" || srcName != null){
            srcName = srcName.toLowerCase();
            StringBuffer nname = new StringBuffer("");
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
    //==============================================================================================
    /*
    считывание % из настроек
     */
    private void mInicializationPercents(){
        TPname = spinnerInDayEdit.getSelectedItem().toString();
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
    //==============================================================================================
    /*
    подсчёт суммы за день рантайм
     */
    private void mRunTimeCount(){
        double resultSum;
        TPname = spinnerInDayEdit.getSelectedItem().toString();
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
    //==============================================================================================
    /*
    отправка данных в БД
     */
    private void mSaveDataInDB(){
        db_seller = new DB_seller(this, userName);
        sl_db = db_seller.getReadableDatabase();
        sl_db.execSQL(DB_seller.CREATE_USER_TABLE);//create table for current user, if not exist

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

        ContentValues values = new ContentValues();

        //суём в базу суммы продаж
        String monthForDB = monthSpinner.getSelectedItem().toString() + yearSpinner.getSelectedItem().toString();
        Toast.makeText(this, monthForDB, Toast.LENGTH_LONG).show();
        values.put(DB_seller.DB_COLUMN_MONTH, monthForDB);//месяц з/п
        values.put(DB_seller.DB_COLUMN_TRADE_POINT, TPname);
        values.put(DB_seller.DB_COLUMN_DATE, dateSQL);
        values.put(DB_seller.DB_COLUMN_SALES_CARD, card_D);
        values.put(DB_seller.DB_COLUMN_SALES_STP, stp_D);
        values.put(DB_seller.DB_COLUMN_SALES_PHONE, phone_D);
        values.put(DB_seller.DB_COLUMN_SALES_FLASH, flash_D);
        values.put(DB_seller.DB_COLUMN_SALES_ACCESORIES, acces_D);
        values.put(DB_seller.DB_COLUMN_SALES_FOTO, foto_D);
        values.put(DB_seller.DB_COLUMN_SALES_TERM, term_D);

        //кладём в базу з/п от этих сумм
        values.put(DB_seller.DB_COLUMN_SALES_CARD_R, card_D * (cardPercent/100));
        values.put(DB_seller.DB_COLUMN_SALES_STP_R, stp_D * (stpPercent/100));
        values.put(DB_seller.DB_COLUMN_SALES_PHONE_R, phone_D * (phonePercent/100));
        values.put(DB_seller.DB_COLUMN_SALES_FLASH_R, flash_D * (flashPercent/100));
        values.put(DB_seller.DB_COLUMN_SALES_ACCESORIES_R, acces_D * (accesPercent/100));
        values.put(DB_seller.DB_COLUMN_SALES_FOTO_R, foto_D * (fotoPercent/100));
        values.put(DB_seller.DB_COLUMN_SALES_TERM_R, term_D * (termPercent/100));
        try {
            sl_db.insert(userName, null, values);
            values.clear();
            sl_db.close();
            db_seller.close();
        }catch (Exception e){
            sl_db.close();
            db_seller.close();
            Toast.makeText(this, "Ошибка добавления в базу" + "\n"
                    + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    //==============================================================================================
    /*
    метод исключительно для дебага
    надо придумать что-то с датой
     */
    public void mRandomizeForDebug(View view) {
        Random random = new Random();
        for (int y=0; y<12; y++) {
            monthSpinner.setSelection(y);
            for (int i = 0; i < 5; i++) {
                long date = random.nextLong();
                date = date > 0? date : date * -1;
                mET_card.setText(String.valueOf(random.nextInt(3000)));
                mET_stp.setText(String.valueOf(random.nextInt(250)));
                mET_flash.setText(String.valueOf(random.nextInt(400)));
                mET_phone.setText(String.valueOf(random.nextInt(1700)));
                mET_accesories.setText(String.valueOf(random.nextInt(1000)));
                mET_foto.setText(String.valueOf(random.nextInt(800)));
                mET_terminal.setText(String.valueOf(random.nextInt(3000)));

                db_seller = new DB_seller(this, userName);
                sl_db = db_seller.getReadableDatabase();
                sl_db.execSQL(DB_seller.CREATE_USER_TABLE);//create table for current user, if not exist

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

                ContentValues values = new ContentValues();
                //суём в базу суммы продаж
                values.put(DB_seller.DB_COLUMN_MONTH, monthSpinner.getSelectedItem().toString());//месяц з/п
                values.put(DB_seller.DB_COLUMN_TRADE_POINT, arrayList.get(random.nextInt(6)));
                values.put(DB_seller.DB_COLUMN_DATE, date);
                values.put(DB_seller.DB_COLUMN_SALES_CARD, card_D);
                values.put(DB_seller.DB_COLUMN_SALES_STP, stp_D);
                values.put(DB_seller.DB_COLUMN_SALES_PHONE, phone_D);
                values.put(DB_seller.DB_COLUMN_SALES_FLASH, flash_D);
                values.put(DB_seller.DB_COLUMN_SALES_ACCESORIES, acces_D);
                values.put(DB_seller.DB_COLUMN_SALES_FOTO, foto_D);
                values.put(DB_seller.DB_COLUMN_SALES_TERM, term_D);

                //кладём в базу з/п от этих сумм
                values.put(DB_seller.DB_COLUMN_SALES_CARD_R, card_D * (cardPercent / 100));
                values.put(DB_seller.DB_COLUMN_SALES_STP_R, stp_D * (stpPercent / 100));
                values.put(DB_seller.DB_COLUMN_SALES_PHONE_R, phone_D * (phonePercent / 100));
                values.put(DB_seller.DB_COLUMN_SALES_FLASH_R, flash_D * (flashPercent / 100));
                values.put(DB_seller.DB_COLUMN_SALES_ACCESORIES_R, acces_D * (accesPercent / 100));
                values.put(DB_seller.DB_COLUMN_SALES_FOTO_R, foto_D * (fotoPercent / 100));
                values.put(DB_seller.DB_COLUMN_SALES_TERM_R, term_D * (termPercent / 100));
                try {
                    sl_db.insert(userName, null, values);
                    values.clear();
                    sl_db.close();
                    db_seller.close();
                } catch (Exception e) {
                    sl_db.close();
                    db_seller.close();
                    Toast.makeText(this, "Ошибка добавления в базу" + "\n"
                            + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
