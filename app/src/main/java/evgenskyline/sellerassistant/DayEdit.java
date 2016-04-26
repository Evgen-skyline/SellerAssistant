package evgenskyline.sellerassistant;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //инициализации
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_edit);
        mTVsum = (TextView)findViewById(R.id.textViewSummInDayEdit);
        mET_card = (EditText)findViewById(R.id.editTextCardInDayEdit);
        //mET_card.addTextChangedListener(textWatcher);
        mET_stp = (EditText)findViewById(R.id.editTextSTPInDayEdit);
        mET_flash = (EditText)findViewById(R.id.editTextFlashInDayEdit);
        mET_phone = (EditText)findViewById(R.id.editTextPhoneInDayEdit);
        mET_accesories = (EditText)findViewById(R.id.editTextAccesoriesInDayEdit);
        mET_foto = (EditText)findViewById(R.id.editTextFotoInDayEdit);
        mET_terminal = (EditText)findViewById(R.id.editTextTerminalInDayEdit);
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

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mET_card.getText().toString().equals("")){
                saveButton.setEnabled(false);
            }else {
                saveButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void clickOnSaveButtonInDayEdit(View view) { //кнопка "Сохранить"
        if(mET_date.getText().toString().equals("")
                || mET_card.getText().toString().equals("")
                || mET_stp.getText().toString().equals("")
                || mET_flash.getText().toString().equals("")
                || mET_phone.getText().toString().equals("")
                || mET_accesories.getText().toString().equals("")
                || mET_foto.getText().toString().equals("")
                || mET_terminal.getText().toString().equals("")){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show();
        }else{
            ContentValues values = new ContentValues();
            values.put(DB_seller.DB_COLUMN_TRADE_POINT, spinnerInDayEdit.getSelectedItem().toString());
            //values.put(DB_seller.DB_COLUMN_DATE, mET_date.getText().toString().);

        }

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
}
