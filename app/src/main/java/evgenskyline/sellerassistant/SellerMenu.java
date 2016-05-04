package evgenskyline.sellerassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SellerMenu extends AppCompatActivity {

    private TextView mTextViewSeller;
    private String seller;

    //private Button mButtonAddDayResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_menu);
        mTextViewSeller = (TextView)findViewById(R.id.textViewOnSellerMenu);
        seller = getIntent().getStringExtra(MainActivity.KEY_INTENT_EXTRA_USER);
        mTextViewSeller.setText(seller);
    }

    /*
    кнопка "Ввод итогов дня"
     */
    public void clickAddDayResult(View view) {
        Intent intent = new Intent(this, DayEdit.class);
        intent.putExtra(MainActivity.KEY_INTENT_EXTRA_USER, seller);
        startActivity(intent);
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
                Intent intent = new Intent(SellerMenu.this, SettingsActivityPF.class);
                startActivity(intent);
                return true;
            case R.id.menuExit:
                finishAffinity(); //exit from application
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    кнопка "отобразить отчёт за месяц"
     */
    public void clickReportForMonth(View view) {
        Intent intent = new Intent(SellerMenu.this, ReportActivity.class);
        intent.putExtra(MainActivity.KEY_INTENT_EXTRA_USER, seller);
        startActivity(intent);
    }
}
