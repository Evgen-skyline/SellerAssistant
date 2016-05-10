package evgenskyline.sellerassistant;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import evgenskyline.sellerassistant.dbwork.DB_seller;

public class SellerMenu extends AppCompatActivity {

    private TextView mTextViewSeller;
    TextView mTV_dialogDate;
    private String seller;
    private Calendar dateCalendar;
    private String dateStr;
    Dialog dialog;

    public static final String DATE_FOR_EXTRA = "dateForExtra";

    //private Button mButtonAddDayResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_menu);
        mTextViewSeller = (TextView)findViewById(R.id.textViewOnSellerMenu);
        seller = getIntent().getStringExtra(MainActivity.KEY_INTENT_EXTRA_USER);
        mTextViewSeller.setText(seller);
        dateCalendar = Calendar.getInstance();

       /* String query;
        query = "Select * from " + DayEdit.reverseName(seller) + " where " + DB_seller.DB_COLUMN_DATE
                + " = " + String.valueOf(System.currentTimeMillis());
        mTextViewSeller.setText(query);*/
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

    /*
    редактирование определённого дня
     */
    public void sellerMenuClickDayChange(View view) {
        dialog = new Dialog(SellerMenu.this);//диалог для запроса даты на редактирование
        dialog.setTitle("Укажите день для редактирования");
        dialog.setContentView(R.layout.dialog_view);//кастомная разметка для диалога
        //элементы из этой разметки
        mTV_dialogDate = (TextView)dialog.findViewById(R.id.SellerMenuDialogTextView);
        Button mButton_DialogNext = (Button)dialog.findViewById(R.id.SellerMenuDialogButtonNext);
        Button mButton_DialogCancel = (Button)dialog.findViewById(R.id.SellerMenuDialogButtonCancel);

        mTV_dialogDate.setText("\nВведите дату\n");

        mTV_dialogDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //диалог ввода даты
                DatePickerDialog dpd = new DatePickerDialog(SellerMenu.this, dateListener,
                        dateCalendar.get(Calendar.YEAR),
                        dateCalendar.get(Calendar.MONTH),
                        dateCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
        dialog.show();

        mButton_DialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mButton_DialogNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //проверка на существование даты!!!
                if(DB_seller.ifDateExist(SellerMenu.this, DayEdit.reverseName(seller), dateCalendar.getTimeInMillis())) {
                    Intent intent = new Intent(SellerMenu.this, DayChangeActivity.class);
                    intent.putExtra(DATE_FOR_EXTRA, dateCalendar.getTimeInMillis());
                    intent.putExtra(MainActivity.KEY_INTENT_EXTRA_USER, seller);
                    startActivity(intent);
                }else {
                    Toast.makeText(SellerMenu.this, "В базе нет такой даты"
                            + String.valueOf(dateCalendar.getTimeInMillis()), Toast.LENGTH_LONG).show();
                }

                dialog.dismiss();
            }
        });
    }
    //листенер диалога ввода даты
    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCalendar.set(Calendar.YEAR, year);
            dateCalendar.set(Calendar.MONTH, monthOfYear);
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateCalendar.set(Calendar.HOUR, 8);
            dateCalendar.set(Calendar.MINUTE, 1);
            dateCalendar.set(Calendar.SECOND, 1);
            dateCalendar.set(Calendar.MILLISECOND, 1);
            dateStr = DateUtils.formatDateTime(dialog.getContext(), dateCalendar.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
            mTV_dialogDate.setText("\n" + dateStr + "\n");//при вводе даты, сразу записываем это в TextView
        }
    };

   /* public static boolean ifDateExist(Context __context, String userTable, Long date){
        DB_seller db_seller = new DB_seller(__context, userTable);    //БД с таблицей юзера(userName)
        SQLiteDatabase sl_db = db_seller.getReadableDatabase();
        String query;
        query = "Select * from " + userTable + " where " + DB_seller.DB_COLUMN_DATE + " = " + String.valueOf(date);
        Cursor cursor = sl_db.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            sl_db.close();
            db_seller.close();
            return false;
        }else {
            cursor.close();
            sl_db.close();
            db_seller.close();
            return true;
        }
    }*/

}
