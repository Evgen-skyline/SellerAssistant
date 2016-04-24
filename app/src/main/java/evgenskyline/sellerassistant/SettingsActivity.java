package evgenskyline.sellerassistant;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/*
НЕ ИСПОЛЬЗУЕМЫЙ КЛАСС(жалко удалить)
 */
public class SettingsActivity extends AppCompatActivity {
    private TextView mTV_Card;
    private TextView mTV_STP;
    private TextView mTV_Phone;
    private TextView mTV_Flash;
    private TextView mTV_Acces;
    private TextView mTV_Foto;
    private TextView mTV_Term;
    private EditText mET_Card;
    private EditText mET_STP;
    private EditText mET_Phone;
    private EditText mET_Flash;
    private EditText mET_Acces;
    private EditText mET_Foto;
    private EditText mET_Term;
    private Spinner mSpin;

    private DB_seller mDBseller;
    private SQLiteDatabase sdb;

    private SharedPreferences mSet;
    private SharedPreferences.Editor editor;
    private static final String DB_PREF_SET = "DBpreference";
    private Set<String> tradePoints = new HashSet<String>();
    private static final String TP_SET = "tradePoints";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mTV_Card = (TextView)findViewById(R.id.textViewInSettingsCard);
        mTV_Card.setText("Карточки %: ");
        mTV_STP = (TextView)findViewById(R.id.textViewInSettingsSTP);
        mTV_STP.setText("Ст. пакеты %: ");
        mTV_Phone = (TextView)findViewById(R.id.textViewInSettingsPhone);
        mTV_Phone.setText("Телефоны %: ");
        mTV_Flash = (TextView)findViewById(R.id.textViewInSettingsFlash);
        mTV_Flash.setText("Флешки и т.д. %: ");
        mTV_Acces = (TextView)findViewById(R.id.textViewInSettingsAcces);
        mTV_Acces.setText("Аксессуары %: ");
        mTV_Foto = (TextView)findViewById(R.id.textViewInSettingsFoto);
        mTV_Foto.setText("Фото %: ");
        mTV_Term = (TextView)findViewById(R.id.textViewInSettingsTerm);
        mTV_Term.setText("Терминал %:");
        mET_Card = (EditText)findViewById(R.id.editTextCardInSettings);
        mET_STP = (EditText)findViewById(R.id.editTextSTPInSettings);
        mET_Phone = (EditText)findViewById(R.id.editTextPhoneInSettings);
        mET_Flash = (EditText)findViewById(R.id.editTextFlashInSettings);
        mET_Acces = (EditText)findViewById(R.id.editTextAccesoriesInSettings);
        mET_Foto = (EditText)findViewById(R.id.editTextFotoInSettings);
        mET_Term = (EditText)findViewById(R.id.editTextTerminalInSettings);
        mSpin = (Spinner)findViewById(R.id.spinnerInSettings);

        mSet = getSharedPreferences(DB_PREF_SET, Context.MODE_PRIVATE);
        editor = mSet.edit();

        mDBseller = new DB_seller(this);
        sdb = mDBseller.getReadableDatabase();
        ContentValues values = new ContentValues();

        if(!(mSet.contains(TP_SET))){//точки по умолчанию, добавляются припервом запуске
            tradePoints.add("КР7");
            tradePoints.add("ХТЗ");
            tradePoints.add("ХТЗ-Н");
            tradePoints.add("МАГАЗИН");
            tradePoints.add("МАРИЯ");
            tradePoints.add("СМАК");
            editor.putStringSet(TP_SET, tradePoints);
            editor.apply();

            values.put(DB_seller.DB_SET_COL_NAME_TP, "КР7");
            values.put(DB_seller.DB_SET_COL_CARD, 1.4);
            values.put(DB_seller.DB_SET_COL_STP, 7);
            values.put(DB_seller.DB_SET_COL_PHONE, 2);
            values.put(DB_seller.DB_SET_COL_FLASH, 7);
            values.put(DB_seller.DB_SET_COL_ACCES, 15);
            values.put(DB_seller.DB_SET_COL_FOTO, 15);
            values.put(DB_seller.DB_SET_COL_TERM, 3);
            sdb.insert(DB_seller.DB_SETTINGS_TABLE_NAME, null, values);
            values.clear();

            values.put(DB_seller.DB_SET_COL_NAME_TP, "ХТЗ");
            values.put(DB_seller.DB_SET_COL_CARD, 1.4);
            values.put(DB_seller.DB_SET_COL_STP, 7);
            values.put(DB_seller.DB_SET_COL_PHONE, 2);
            values.put(DB_seller.DB_SET_COL_FLASH, 7);
            values.put(DB_seller.DB_SET_COL_ACCES, 12);
            values.put(DB_seller.DB_SET_COL_FOTO, 12);
            values.put(DB_seller.DB_SET_COL_TERM, 3);
            sdb.insert(DB_seller.DB_SETTINGS_TABLE_NAME, null, values);
            values.clear();

            values.put(DB_seller.DB_SET_COL_NAME_TP, "ХТЗ-Н");
            values.put(DB_seller.DB_SET_COL_CARD, 1.4);
            values.put(DB_seller.DB_SET_COL_STP, 7);
            values.put(DB_seller.DB_SET_COL_PHONE, 2);
            values.put(DB_seller.DB_SET_COL_FLASH, 7);
            values.put(DB_seller.DB_SET_COL_ACCES, 12);
            values.put(DB_seller.DB_SET_COL_FOTO, 12);
            values.put(DB_seller.DB_SET_COL_TERM, 3);
            sdb.insert(DB_seller.DB_SETTINGS_TABLE_NAME, null, values);
            values.clear();

            values.put(DB_seller.DB_SET_COL_NAME_TP, "МАГАЗИН");
            values.put(DB_seller.DB_SET_COL_CARD, 1.4);
            values.put(DB_seller.DB_SET_COL_STP, 7);
            values.put(DB_seller.DB_SET_COL_PHONE, 2);
            values.put(DB_seller.DB_SET_COL_FLASH, 7);
            values.put(DB_seller.DB_SET_COL_ACCES, 12);
            values.put(DB_seller.DB_SET_COL_FOTO, 12);
            values.put(DB_seller.DB_SET_COL_TERM, 3);
            sdb.insert(DB_seller.DB_SETTINGS_TABLE_NAME, null, values);
            values.clear();

            values.put(DB_seller.DB_SET_COL_NAME_TP, "МАРИЯ");
            values.put(DB_seller.DB_SET_COL_CARD, 1.4);
            values.put(DB_seller.DB_SET_COL_STP, 7);
            values.put(DB_seller.DB_SET_COL_PHONE, 2);
            values.put(DB_seller.DB_SET_COL_FLASH, 7);
            values.put(DB_seller.DB_SET_COL_ACCES, 15);
            values.put(DB_seller.DB_SET_COL_FOTO, 15);
            values.put(DB_seller.DB_SET_COL_TERM, 3);
            sdb.insert(DB_seller.DB_SETTINGS_TABLE_NAME, null, values);
            values.clear();

            values.put(DB_seller.DB_SET_COL_NAME_TP, "СМАК");
            values.put(DB_seller.DB_SET_COL_CARD, 1.4);
            values.put(DB_seller.DB_SET_COL_STP, 7);
            values.put(DB_seller.DB_SET_COL_PHONE, 2);
            values.put(DB_seller.DB_SET_COL_FLASH, 7);
            values.put(DB_seller.DB_SET_COL_ACCES, 15);
            values.put(DB_seller.DB_SET_COL_FOTO, 15);
            values.put(DB_seller.DB_SET_COL_TERM, 3);
            sdb.insert(DB_seller.DB_SETTINGS_TABLE_NAME, null, values);
            values.clear();

        }else {
            tradePoints = mSet.getStringSet(TP_SET, null);
        }
        ArrayList<String> list= new ArrayList<String>(tradePoints);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        mSpin.setAdapter(adapter);
        mSpin.setPrompt("Выберите точку");


        //реализовать: добавление и удаление торг.точек
        //реализовать: наполнение спинера торг. точками
        //реализовать запись/считывание % по позициям
    }
}
