package evgenskyline.sellerassistant;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DayEdit extends AppCompatActivity {
    //ui
    private TextView mTVsum;
    private Spinner spinnerInDayEdit;
    private EditText mET_card;
    private EditText mET_stp;
    private EditText mET_flash;
    private EditText mET_phone;
    private EditText mET_accesories;
    private EditText mET_foto;
    private EditText mET_terminal;
    private EditText mET_date;
    private TextView mTV_date;
    private Button saveButton;

    //for DB
    private String userName;
    private DB_seller db_seller;    //БД с таблицей юзера(userName)
    private SQLiteDatabase sl_db;

    private SharedPreferences mSPreferences;
    private Set<String> tradePoints = new HashSet<String>();
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;

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
        mET_date = (EditText)findViewById(R.id.editTextDateInDayEdit);
        mTV_date = (TextView) findViewById(R.id.textViewDateInDayEdit);
        saveButton = (Button)findViewById(R.id.buttonSaveInDayEdit);
        spinnerInDayEdit = (Spinner)findViewById(R.id.spinnerInDayEdit);

        mSPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        userName =reverseName(getIntent().getStringExtra(MainActivity.KEY_INTENT_EXTRA_USER));
        mTVsum.setText(userName);
        db_seller = new DB_seller(this, userName);
        sl_db = db_seller.getReadableDatabase();

        //наполнение спинера
        tradePoints = mSPreferences.getStringSet(MainActivity.APP_PREFERENCES_TP_SET, null);
        arrayList = new ArrayList<String>(tradePoints);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        spinnerInDayEdit.setAdapter(arrayAdapter);
        spinnerInDayEdit.setPrompt("КР7");

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
    сохранение данных в базу
     */
    public void clickOnSaveButtonInDayEdit(View view) { //кнопка "Сохранить"
        //boolean confirmFlag = false;

        if(mET_date.getText().toString().length() != 8) {
            Toast.makeText(this, "Неправильный формат даты", Toast.LENGTH_LONG).show();
            return;
        }
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
        mInicializationPercents();
        mRunTimeCount();
    }

    public String reverseName(String srcName){//перевод кирилицы в латинские
        String result = "";
        String alpha = new String("абвгдеёжзиыйклмнопрстуфхцчшщьэюя");
        String[] _alpha = {"a","b","v","g","d","e","yo","g","z","i","y","i",
                "k","l","m","n","o","p","r","s","t","u",
                "f","h","tz","ch","sh","sh","'","e","yu","ya"};
        if(srcName != "" || srcName != null){
            srcName.toLowerCase();
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
            return;
        }
    }

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

    private void mSaveDataInDB(){
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
        values.put(DB_seller.DB_COLUMN_TRADE_POINT, TPname);
        values.put(DB_seller.DB_COLUMN_DATE, Integer.parseInt(mET_date.getText().toString()));
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
        }catch (Exception e){
            Toast.makeText(this, "Ошибка добавления в базу" + "\n"
                    + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
