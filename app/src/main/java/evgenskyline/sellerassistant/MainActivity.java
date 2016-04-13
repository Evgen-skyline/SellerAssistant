package evgenskyline.sellerassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button mNewSellerBut;
    private Spinner mChoiseSellerSpin;
    private Button mNextBut;
    private Button mExRtBut;
    private Button mAboutBut;
    private DB_seller mDBseller;
    private SQLiteDatabase mSQLiteDB;
    private static final int KEY_INTENT_DB_ADD = 1;//код возврата для intent
    //private static final int KEY_INTENT_NEXT_BUTTON = 2;
    public static final String KEY_INTENT_EXTRA_USER = "user";
    private String newSaller;//возвращаемое имя из "добавить продавца"
    private static final String APP_PREFERENCES = "usersPreferences";//имя файла настроек
    private static final String APP_PREFERENCES_SET = "usersSet";
    private SharedPreferences mSettings;//отвечает за работу с настройками
    private Set<String> usersSet = new HashSet<String>();
    private SharedPreferences.Editor editor;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;

    private TextView mTV; //DEBUG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNewSellerBut = (Button)findViewById(R.id.buttonAddSeller);
        mNextBut = (Button)findViewById(R.id.buttonNextActivity);
        mExRtBut = (Button)findViewById(R.id.buttonExchangeRates);
        mAboutBut = (Button)findViewById(R.id.buttonAbout);
        mChoiseSellerSpin = (Spinner)findViewById(R.id.spinner);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);//инициализация
        editor = mSettings.edit();
        mTV = (TextView)findViewById(R.id.textViewDebug); //DEBUG  DELETE

        if(mSettings.contains(APP_PREFERENCES_SET)){
            usersSet = mSettings.getStringSet(APP_PREFERENCES_SET, null);
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
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        mChoiseSellerSpin.setAdapter(arrayAdapter);
        mChoiseSellerSpin.setPrompt("Выберите продавца");
    }

    public void clickForAddSeller(View view) {
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

    public void clickOnNextButton(View view) {
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

    public String reverseName(String srcName){
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
