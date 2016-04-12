package evgenskyline.sellerassistant;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button mNewSellerBut;
    private Spinner mChoiseSellerSpin;
    private Button mNextBut;
    private Button mExRtBut;
    private Button mAboutBut;
    private DB_seller mDBseller;
    private SQLiteDatabase mSQLiteDB;
    private static final int KEY_INTENT_DB_ADD = 1;
    private String newSaller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNewSellerBut = (Button)findViewById(R.id.buttonAddSeller);
        mNextBut = (Button)findViewById(R.id.buttonNextActivity);
        mExRtBut = (Button)findViewById(R.id.buttonExchangeRates);
        mAboutBut = (Button)findViewById(R.id.buttonAbout);
        mChoiseSellerSpin = (Spinner)findViewById(R.id.spinner);

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
                Toast.makeText(getApplicationContext(), newSaller, Toast.LENGTH_LONG).show();
            }
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
