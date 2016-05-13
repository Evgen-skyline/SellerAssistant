package evgenskyline.sellerassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import evgenskyline.sellerassistant.dbwork.DB_seller;

public class MainActivity extends AppCompatActivity {

    //UI
    private Button mNewSellerBut;
    private Spinner mChoiseSellerSpin;
    private Button mNextBut;
    private Button mExRtBut;
    private Button mAboutBut;
    private TextView mTV; //DEBUG

    //константы
    private static final int KEY_INTENT_DB_ADD = 1;//код возврата для intent
    public static final String KEY_INTENT_EXTRA_USER = "user";
    private static final String APP_PREFERENCES = "usersPreferences";//имя файла настроек
    private static final String APP_PREFERENCES_SET = "usersSet";
    public static final String APP_PREFERENCES_TP_SET = "tradePointsSet";
    public static final String TP_CARD = "Card";
    public static final String TP_STP = "Stp";
    public static final String TP_PHONE = "Phone";
    public static final String TP_FLASH = "Flash";
    public static final String TP_ACCESORIES = "Accesories";
    public static final String TP_FOTO = "Foto";
    public static final String TP_TERM = "Term";

    private DB_seller mDBseller;
    private SQLiteDatabase mSQLiteDB;

    private String newSaller;//возвращаемое имя из "добавить продавца"
    private SharedPreferences mUserSettings;//отвечает за работу с настройками
    private SharedPreferences.Editor editor;//editor для пользователей
    private SharedPreferences tradePointsPref;
    private SharedPreferences.Editor editorTP;
    private Set<String> usersSet = new HashSet<String>();//Список пользователей
    private Set<String> tradePoints = new HashSet<String>();//Список торговых точек

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNewSellerBut = (Button)findViewById(R.id.buttonAddSeller);
        mNextBut = (Button)findViewById(R.id.buttonNextActivity);
        mExRtBut = (Button)findViewById(R.id.buttonExchangeRates);
        mAboutBut = (Button)findViewById(R.id.buttonAbout);
        mChoiseSellerSpin = (Spinner)findViewById(R.id.spinner);
        mUserSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);//инициализация
        tradePointsPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mUserSettings.edit();
        mTV = (TextView)findViewById(R.id.textViewDebug); //DEBUG  DELETE

        if(mUserSettings.contains(APP_PREFERENCES_SET)){
            usersSet = mUserSettings.getStringSet(APP_PREFERENCES_SET, null);
        }
        if(!(tradePointsPref.contains(APP_PREFERENCES_TP_SET))){
            setDefaultTradePoints();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putStringSet(APP_PREFERENCES_SET, usersSet);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        arrayList = new ArrayList<String>(usersSet);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, arrayList);
        mChoiseSellerSpin.setAdapter(arrayAdapter);
        mChoiseSellerSpin.setPrompt("Выберите продавца");
    }

    public void clickForAddSeller(View view) {//кнопка добавление продавца
        Intent intent = new Intent(MainActivity.this, AddSeller.class);
        startActivityForResult(intent, KEY_INTENT_DB_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == KEY_INTENT_DB_ADD){
            if(resultCode == RESULT_OK){
                newSaller = data.getStringExtra(AddSeller.KEY_ANSWER);
                usersSet.add(newSaller);
            }
        }
    }

    public void clickOnNextButton(View view) {//кнопка далее
        try {
            String selectedUser = mChoiseSellerSpin.getSelectedItem().toString();
            if (selectedUser != null || !selectedUser.equals("")) {
                Intent intent = new Intent(MainActivity.this, SellerMenu.class);
                intent.putExtra(KEY_INTENT_EXTRA_USER, mChoiseSellerSpin.getSelectedItem().toString());
                startActivity(intent);
            }
        }catch (Exception e){
            mTV.setText(e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//обработка выбора в контекстном меню
        int id = item.getItemId();

        switch (id){
            case R.id.menuSettings://вызов настроек из меню
                Intent intent = new Intent(MainActivity.this, SettingsActivityPF.class);
                startActivity(intent);
                return true;
            case R.id.menuExit://ВЫХОД ИЗ ПРИЛОЖЕНИЯ
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clickAboutProg(View view) {//кнопка "о программе"

    }

    private void setDefaultTradePoints(){
        tradePoints.add("КР7");
        tradePoints.add("ХТЗ");
        tradePoints.add("ХТЗ-Н");
        tradePoints.add("МАГАЗИН");
        tradePoints.add("МАРИЯ");
        tradePoints.add("СМАК");
        tradePoints.add("СИНТЕЗ");

        editorTP = tradePointsPref.edit();
        editorTP.putStringSet(APP_PREFERENCES_TP_SET, tradePoints);

        Iterator<String> itr = tradePoints.iterator();
        while (itr.hasNext()){
            String nameTP = itr.next().toString();
            switch (nameTP){
                case "КР7":
                    editorTP.putString(nameTP + TP_CARD, "1.4");
                    editorTP.putString(nameTP + TP_STP, "7");
                    editorTP.putString(nameTP + TP_FLASH, "7");
                    editorTP.putString(nameTP + TP_PHONE, "2");
                    editorTP.putString(nameTP + TP_ACCESORIES, "15");
                    editorTP.putString(nameTP + TP_FOTO, "15");
                    editorTP.putString(nameTP + TP_TERM, "3");
                    break;
                case "ХТЗ":
                    editorTP.putString(nameTP + TP_CARD, "1.4");
                    editorTP.putString(nameTP + TP_STP, "7");
                    editorTP.putString(nameTP + TP_FLASH, "7");
                    editorTP.putString(nameTP + TP_PHONE, "2");
                    editorTP.putString(nameTP + TP_ACCESORIES, "12");
                    editorTP.putString(nameTP + TP_FOTO, "12");
                    editorTP.putString(nameTP + TP_TERM, "3");
                    break;
                case "ХТЗ-Н":
                    editorTP.putString(nameTP + TP_CARD, "1.4");
                    editorTP.putString(nameTP + TP_STP, "7");
                    editorTP.putString(nameTP + TP_FLASH, "7");
                    editorTP.putString(nameTP + TP_PHONE, "2");
                    editorTP.putString(nameTP + TP_ACCESORIES, "12");
                    editorTP.putString(nameTP + TP_FOTO, "12");
                    editorTP.putString(nameTP + TP_TERM, "3");
                    break;
                case "МАГАЗИН":
                    editorTP.putString(nameTP + TP_CARD, "1.4");
                    editorTP.putString(nameTP + TP_STP, "7");
                    editorTP.putString(nameTP + TP_FLASH, "7");
                    editorTP.putString(nameTP + TP_PHONE, "2");
                    editorTP.putString(nameTP + TP_ACCESORIES, "12");
                    editorTP.putString(nameTP + TP_FOTO, "12");
                    editorTP.putString(nameTP + TP_TERM, "3");
                    break;
                case "МАРИЯ":
                    editorTP.putString(nameTP + TP_CARD, "1.4");
                    editorTP.putString(nameTP + TP_STP, "7");
                    editorTP.putString(nameTP + TP_FLASH, "7");
                    editorTP.putString(nameTP + TP_PHONE, "2");
                    editorTP.putString(nameTP + TP_ACCESORIES, "15");
                    editorTP.putString(nameTP + TP_FOTO, "15");
                    editorTP.putString(nameTP + TP_TERM, "3");
                    break;
                case "СМАК":
                    editorTP.putString(nameTP + TP_CARD, "1.4");
                    editorTP.putString(nameTP + TP_STP, "7");
                    editorTP.putString(nameTP + TP_FLASH, "7");
                    editorTP.putString(nameTP + TP_PHONE, "2");
                    editorTP.putString(nameTP + TP_ACCESORIES, "15");
                    editorTP.putString(nameTP + TP_FOTO, "15");
                    editorTP.putString(nameTP + TP_TERM, "3");
                    break;
                case "СИНТЕЗ":
                    editorTP.putString(nameTP + TP_CARD, "1.4");
                    editorTP.putString(nameTP + TP_STP, "7");
                    editorTP.putString(nameTP + TP_FLASH, "7");
                    editorTP.putString(nameTP + TP_PHONE, "2");
                    editorTP.putString(nameTP + TP_ACCESORIES, "25");
                    editorTP.putString(nameTP + TP_FOTO, "25");
                    editorTP.putString(nameTP + TP_TERM, "3");
                    break;
                default: break;
            }
        }
        editorTP.apply();
    }
}

/*
case "КР7":
        editorTP.putString(nameTP + TP_CARD, (float)1.4);
        editorTP.putString(nameTP + TP_STP, (float)7);
        editorTP.putFloat(nameTP + TP_FLASH, (float)7);
        editorTP.putFloat(nameTP + TP_PHONE, (float)2);
        editorTP.putFloat(nameTP + TP_ACCESORIES, (float)15);
        editorTP.putFloat(nameTP + TP_FOTO, (float)15);
        editorTP.putFloat(nameTP + TP_TERM, (float)3);
        break;
        case "ХТЗ":
        editorTP.putFloat(nameTP + TP_CARD, (float)1.4);
        editorTP.putFloat(nameTP + TP_STP, (float)7);
        editorTP.putFloat(nameTP + TP_FLASH, (float)7);
        editorTP.putFloat(nameTP + TP_PHONE, (float)2);
        editorTP.putFloat(nameTP + TP_ACCESORIES, (float)12);
        editorTP.putFloat(nameTP + TP_FOTO, (float)12);
        editorTP.putFloat(nameTP + TP_TERM, (float)3);
        break;
        case "ХТЗ-Н":
        editorTP.putFloat(nameTP + TP_CARD, (float)1.4);
        editorTP.putFloat(nameTP + TP_STP, (float)7);
        editorTP.putFloat(nameTP + TP_FLASH, (float)7);
        editorTP.putFloat(nameTP + TP_PHONE, (float)2);
        editorTP.putFloat(nameTP + TP_ACCESORIES, (float)12);
        editorTP.putFloat(nameTP + TP_FOTO, (float)12);
        editorTP.putFloat(nameTP + TP_TERM, (float)3);
        break;
        case "МАГАЗИН":
        editorTP.putString(nameTP + TP_CARD, "1.4");
        editorTP.putFloat(nameTP + TP_STP, (float)7);
        editorTP.putFloat(nameTP + TP_FLASH, (float)7);
        editorTP.putFloat(nameTP + TP_PHONE, (float)2);
        editorTP.putFloat(nameTP + TP_ACCESORIES, (float)12);
        editorTP.putFloat(nameTP + TP_FOTO, (float)12);
        editorTP.putFloat(nameTP + TP_TERM, (float)3);
        break;
        case "МАРИЯ":
        editorTP.putFloat(nameTP + TP_CARD, (float)1.4);
        editorTP.putFloat(nameTP + TP_STP, (float)7);
        editorTP.putFloat(nameTP + TP_FLASH, (float)7);
        editorTP.putFloat(nameTP + TP_PHONE, (float)2);
        editorTP.putFloat(nameTP + TP_ACCESORIES, (float)15);
        editorTP.putFloat(nameTP + TP_FOTO, (float)15);
        editorTP.putFloat(nameTP + TP_TERM, (float)3);
        break;
        case "СМАК":
        editorTP.putFloat(nameTP + TP_CARD, (float)1.4);
        editorTP.putFloat(nameTP + TP_STP, (float)7);
        editorTP.putFloat(nameTP + TP_FLASH, (float)7);
        editorTP.putFloat(nameTP + TP_PHONE, (float)2);
        editorTP.putFloat(nameTP + TP_ACCESORIES, (float)15);
        editorTP.putFloat(nameTP + TP_FOTO, (float)15);
        editorTP.putFloat(nameTP + TP_TERM, (float)3);
        break;
        case "СИНТЕЗ":
        editorTP.putFloat(nameTP + TP_CARD, (float)1.4);
        editorTP.putFloat(nameTP + TP_STP, (float)7);
        editorTP.putFloat(nameTP + TP_FLASH, (float)7);
        editorTP.putFloat(nameTP + TP_PHONE, (float)2);
        editorTP.putFloat(nameTP + TP_ACCESORIES, (float)25);
        editorTP.putFloat(nameTP + TP_FOTO, (float)25);
        editorTP.putFloat(nameTP + TP_TERM, (float)3);
        break;
default: break;*/
