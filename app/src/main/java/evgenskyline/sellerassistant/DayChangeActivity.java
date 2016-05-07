package evgenskyline.sellerassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class DayChangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_change);

    }

    public void clickOnSaveButtonInDayEdit(View view) {

    }

    @Override
    protected void onResume() {
        super.onResume();

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
}
