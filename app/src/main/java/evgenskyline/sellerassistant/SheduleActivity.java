package evgenskyline.sellerassistant;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import evgenskyline.sellerassistant.dbwork.DB_seller;
import evgenskyline.sellerassistant.dbwork.EventDecorator;
import evgenskyline.sellerassistant.dbwork.FutureWorkDays;
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
    private HashMap<CalendarDay, FutureWorkDays> mapWithFutureDays;
    private HashSet<CalendarDay> hCalDays;
    private HashSet<CalendarDay> hSetFutureWorkDays;

    private SharedPreferences mSPreferences;
    private Set tradePoints = new HashSet<String>();
    private ArrayAdapter<String> arrayAdapter;

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
                    if (mapWithFutureDays.containsKey(date)){
                        mB_setWorkDay.setEnabled(true);
                        Toast.makeText(SheduleActivity.this, mapWithFutureDays.get(date).getTradePointName(),
                                Toast.LENGTH_SHORT).show();
                        mB_setWorkDay.setText("Удалить запись");
                    }else {
                        mB_setWorkDay.setEnabled(true);
                        mB_setWorkDay.setText(R.string.mButtonSetWorkDay);
                    }
                }

            }
        });
        materialCalendar.setDateTextAppearance(R.style.CustomDayTextAppearance);
        materialCalendar.setTileSizeDp(45);
    }

    /**
     * получение данных из БД и запись в календарь отработаных дней
     */
    private void getDataFromDB(){

        mapWithWorkDays = new HashMap<CalendarDay, ResultsOfTheDay>();
        mapWithFutureDays = new HashMap<CalendarDay, FutureWorkDays>();
        hSetFutureWorkDays = new HashSet<CalendarDay>();
        hCalDays = new HashSet<CalendarDay>();
        TaskForWorkDays taskForWorkDays = new TaskForWorkDays(userName, this);
        //выполниться по завершению asyncTask`a
        taskForWorkDays.setOnTaskCompliteListener(new OnWorkDaysTaskComplite() {
            @Override
            public void onTaskComplite(ArrayList<ResultsOfTheDay> dbTable, ArrayList<FutureWorkDays> futureDays) {
                for (int i=0; i<dbTable.size(); i++){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(dbTable.get(i).getDate());
                    CalendarDay calendarDay = new CalendarDay(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    mapWithWorkDays.put(calendarDay, dbTable.get(i));
                    hCalDays.add(calendarDay);
                }
                DB_seller db_seller = new DB_seller(SheduleActivity.this, userName);
                SQLiteDatabase sl_db = db_seller.getReadableDatabase();
                for (int i = 0; i < futureDays.size(); i++){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(futureDays.get(i).getDate());
                    CalendarDay calendarDay = new CalendarDay(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    if (!hCalDays.contains(calendarDay)) {
                        mapWithFutureDays.put(calendarDay, futureDays.get(i));
                        hSetFutureWorkDays.add(calendarDay);
                    }else {
                        sl_db.delete(userName + DB_seller.FUTURE_DAYS, BaseColumns._ID
                                + " = " + String.valueOf(futureDays.get(i).getId()), null);
                    }
                }
                sl_db.close();
                db_seller.close();
                materialCalendar.removeDecorators();
                materialCalendar.addDecorators(new EventDecorator(hSetFutureWorkDays,
                        getApplicationContext().getResources().getDrawable(R.drawable.my_drawable_yellow)));
                materialCalendar.addDecorators(new EventDecorator(hCalDays,
                        getApplicationContext().getResources().getDrawable(R.drawable.my_drawable)));
            }
        });
        taskForWorkDays.execute();
    }

    /**
     * кнопка "Подробнее"
     * @param view
     */
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

    /**
     * а тут конкретный говнокод....
     * кнопка добавить/удалить запланированый день
     * @param view
     */
    public void clickSetWorkDay(View view) {
        /**
         * диалог удаления метки
         */
        if (mapWithFutureDays.containsKey(materialCalendar.getSelectedDate())){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Подтвердите");
            alertDialogBuilder.setMessage("Вы уверены, что хотите удалить эту запись?");
            alertDialogBuilder.setPositiveButton(R.string.mYES, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        db_seller = new DB_seller(SheduleActivity.this, userName);
                        sl_db = db_seller.getReadableDatabase();

                        String where = BaseColumns._ID + " = " + mapWithFutureDays.get(materialCalendar.getSelectedDate())
                                .getId();
                        int returnedResult = sl_db.delete(userName + DB_seller.FUTURE_DAYS,  where, null);
                        sl_db.close();
                        db_seller.close();
                        if (returnedResult>0){
                            Toast.makeText(SheduleActivity.this, R.string.mWasDelete, Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(SheduleActivity.this, R.string.mNotDelete, Toast.LENGTH_LONG).show();
                        }
                    getDataFromDB();
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
        }else{ //====================================================================
            /**
             * диалог добавления метки
             */
            final Dialog dialog = new Dialog(this);
            dialog.setTitle("Где вы будете?");
            dialog.setContentView(R.layout.dialog_with_spinner);

            mSPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            tradePoints = mSPreferences.getStringSet(MainActivity.APP_PREFERENCES_TP_SET, null);
            ArrayList arrayList = new ArrayList<String>(tradePoints);
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, arrayList);
            final Spinner spin = (Spinner)dialog.findViewById(R.id.mDialogWithSpinnerSpin);
            spin.setAdapter(arrayAdapter);
            dialog.show();
            Button mButCancel = (Button)dialog.findViewById(R.id.mDialogWithSpinnerButtonCancel);
            mButCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Button mButOk = (Button)dialog.findViewById(R.id.mDialogWithSpinnerButtonOk);
            mButOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DB_seller db_seller = new DB_seller(SheduleActivity.this, userName);
                    SQLiteDatabase sl_db = db_seller.getReadableDatabase();
                    long result = 0L;
                    ContentValues values = new ContentValues();
                    values.put(DB_seller.DB_COLUMN_DATE, materialCalendar.getSelectedDate().getCalendar().getTimeInMillis());
                    values.put(DB_seller.DB_COLUMN_TRADE_POINT, spin.getSelectedItem().toString());
                    try {
                        result = sl_db.insert(userName + DB_seller.FUTURE_DAYS, null, values);
                    }catch (Exception e){
                        Toast.makeText(SheduleActivity.this, "Ошибка добавления в базу" + "\n"
                            + e.toString(), Toast.LENGTH_LONG).show();
                        Log.e(MainActivity.TAG,"Ошибка добавления в базу "  + e.toString());
                    }
                    finally {
                        sl_db.close();
                        db_seller.close();
                    }
                    if(result > 0){
                        Toast.makeText(SheduleActivity.this, "Сохранено", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(SheduleActivity.this, "Такая дата уже существует, НЕ СОХРАНЕНО" + "\n"
                            + "Попробуйте другую дату", Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                    getDataFromDB();
            }
        });
        }

    }
}
