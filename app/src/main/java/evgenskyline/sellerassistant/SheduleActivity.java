package evgenskyline.sellerassistant;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import evgenskyline.sellerassistant.dbwork.DB_seller;
import evgenskyline.sellerassistant.dbwork.EventDecorator;
import evgenskyline.sellerassistant.dbwork.OnWorkDaysTaskComplite;
import evgenskyline.sellerassistant.dbwork.ResultsOfTheDay;
import evgenskyline.sellerassistant.dbwork.TaskForWorkDays;

public class SheduleActivity extends AppCompatActivity {
    private MaterialCalendarView materialCalendar;
    private Button mB_details;
    private Button mB_setWorkDay;
    private DB_seller db_seller;    //БД с таблицей юзера(userName)
    private SQLiteDatabase sl_db;
    private String userName;
    private ArrayList<ResultsOfTheDay> dayContainer;
    private HashMap<CalendarDay, ResultsOfTheDay> mapWithWorkDays;
    private HashMap<CalendarDay, String> mapWithFutureDays;
    private HashSet<CalendarDay> hCalDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shedule);
        materialCalendar = (MaterialCalendarView)findViewById(R.id.mMaterialCalendar);
        mB_details = (Button)findViewById(R.id.mSheduleActivityDetails);
        mB_setWorkDay = (Button)findViewById(R.id.mSheduleActivitySetWorkDay);
        mB_setWorkDay.setEnabled(false);
        mB_details.setEnabled(false);

        userName = DayEdit.reverseName(getIntent().getStringExtra(MainActivity.KEY_USER));
        db_seller = new DB_seller(this, userName);
        sl_db = db_seller.getReadableDatabase();
        dayContainer = new ArrayList<ResultsOfTheDay>();
        getDataFromDB();
    }

    /**
     * получение данных из БД и запись в календарь отработаных дней
     */
    private void getDataFromDB(){

        mapWithWorkDays = new HashMap<CalendarDay, ResultsOfTheDay>();
        hCalDays = new HashSet<CalendarDay>();
        TaskForWorkDays taskForWorkDays = new TaskForWorkDays(userName, this);
        //выполниться по завершению asyncTask`a
        taskForWorkDays.setOnTaskCompliteListener(new OnWorkDaysTaskComplite() {
            @Override
            public void onTaskComplite(ArrayList<ResultsOfTheDay> dbTable) {
                for (int i=0; i<dbTable.size(); i++){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(dbTable.get(i).getDate());
                    CalendarDay calendarDay = new CalendarDay(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    mapWithWorkDays.put(calendarDay, dbTable.get(i));
                    hCalDays.add(calendarDay);
                }
                materialCalendar.addDecorators(new EventDecorator(/*R.color.colorForCalendar,*/ hCalDays,
                        getApplicationContext().getResources().getDrawable(R.drawable.my_drawable)));
                materialCalendar.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                        if (mapWithWorkDays.containsKey(date)){
                            mB_setWorkDay.setEnabled(false);
                            mB_details.setEnabled(true);
                            Toast.makeText(SheduleActivity.this, mapWithWorkDays.get(date).getNameOfTradePoint(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e(MainActivity.TAG, String.valueOf(date.getCalendar().getTimeInMillis()));
                        }else {
                            mB_details.setEnabled(false);
                        }
                    }
                });
            }
        });
        materialCalendar.setDateTextAppearance(R.style.CustomDayTextAppearance);
        materialCalendar.setTileSizeDp(45);
        taskForWorkDays.execute();
    }

    public void clickForDetails(View view) {
        CalendarDay calDay = materialCalendar.getSelectedDate();

        final Dialog dialog = new Dialog(SheduleActivity.this);
        dialog.setTitle(R.string.mButtonDetails);
        dialog.setContentView(R.layout.dialog_about);
        TextView tv = (TextView)dialog.findViewById(R.id.aboutTextView);
        tv.setText(mapWithWorkDays.get(calDay).toString());
        dialog.show();
        Button mButOk = (Button)dialog.findViewById(R.id.mDialogAboutButtonOk);
        mButOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
