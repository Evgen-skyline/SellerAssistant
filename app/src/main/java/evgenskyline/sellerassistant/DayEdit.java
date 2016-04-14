package evgenskyline.sellerassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DayEdit extends AppCompatActivity {
    private TextView mTVsum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_edit);
        mTVsum = (TextView)findViewById(R.id.textViewSummInDayEdit);
        mTVsum.setText(getIntent().getStringExtra(MainActivity.KEY_INTENT_EXTRA_USER));
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
                return true;
            case R.id.menuExit:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
