package evgenskyline.sellerassistant.dbwork;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import evgenskyline.sellerassistant.DayEdit;

/**
 * Created by evgen on 12.04.2016.
 */
public class DB_seller extends SQLiteOpenHelper implements BaseColumns {


    private static final String DB_NAME = "SellersData.db";
    private static final int DB_VERSION = 1;

    //поля для таблицы с данными про з/п
    public String DB_TABLE_NAME = "";//передаём через конструктор транслитное имя пользователя
    public static final String DB_COLUMN_MONTH = "month";
    public static final String DB_COLUMN_DATE = "date";
    public static final String DB_COLUMN_TRADE_POINT = "tradePoint";
    public static final String DB_COLUMN_SALES_CARD = "card";
    public static final String DB_COLUMN_SALES_STP = "stp";
    public static final String DB_COLUMN_SALES_PHONE = "phone";
    public static final String DB_COLUMN_SALES_FLASH = "flash";
    public static final String DB_COLUMN_SALES_ACCESORIES = "accesories";
    public static final String DB_COLUMN_SALES_FOTO = "foto";
    public static final String DB_COLUMN_SALES_TERM = "terminal";

    public static final String DB_COLUMN_SALES_CARD_R = "cardR";
    public static final String DB_COLUMN_SALES_STP_R = "stpR";
    public static final String DB_COLUMN_SALES_PHONE_R = "phoneR";
    public static final String DB_COLUMN_SALES_FLASH_R = "flashR";
    public static final String DB_COLUMN_SALES_ACCESORIES_R = "accesoriesR";
    public static final String DB_COLUMN_SALES_FOTO_R = "fotoR";
    public static final String DB_COLUMN_SALES_TERM_R = "terminalR";
    //строка на создание таблицы пользователя
    public static String CREATE_USER_TABLE;
    //таблица для запланированых рабочих дней
    public static String CREATE_USER_FUTURE_DAYS;
    public static final String FUTURE_DAYS = "FutureDays";

    public DB_seller(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DB_seller(Context context, String _seller){
        super(context, DB_NAME, null, DB_VERSION);
        DB_TABLE_NAME = _seller;
        CREATE_USER_TABLE = "create table if not exists " + DB_TABLE_NAME + " ("
                + BaseColumns._ID + " integer primary key autoincrement, "
                + DB_COLUMN_MONTH + " text, "   //месяц в который считать з/п
                + DB_COLUMN_DATE + " integer not null UNIQUE, "
                + DB_COLUMN_TRADE_POINT + " text, "
                + DB_COLUMN_SALES_CARD + " real, "
                + DB_COLUMN_SALES_STP + " real, "
                + DB_COLUMN_SALES_PHONE + " real, "
                + DB_COLUMN_SALES_FLASH + " real, "
                + DB_COLUMN_SALES_ACCESORIES + " real, "
                + DB_COLUMN_SALES_FOTO + " real, "
                + DB_COLUMN_SALES_TERM + " real, "
                + DB_COLUMN_SALES_CARD_R + " real, "
                + DB_COLUMN_SALES_STP_R + " real, "
                + DB_COLUMN_SALES_PHONE_R + " real, "
                + DB_COLUMN_SALES_FLASH_R + " real, "
                + DB_COLUMN_SALES_ACCESORIES_R + " real, "
                + DB_COLUMN_SALES_FOTO_R + " real, "
                + DB_COLUMN_SALES_TERM_R + " real);";

        CREATE_USER_FUTURE_DAYS = "create table if not exists " + DB_TABLE_NAME + FUTURE_DAYS + " ("
                +BaseColumns._ID + " integer primary key autoincrement, "
                +DB_COLUMN_DATE + " integer not null UNIQUE, "
                +DB_COLUMN_TRADE_POINT + " text);";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_USER_FUTURE_DAYS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite ", "обновляемся с версии " + oldVersion + " на версию " + newVersion);
        db.execSQL("DROP TABLE IF IT EXISTS " + DB_TABLE_NAME);
        //db.execSQL("DROP TABLE IF IT EXISTS " + DB_SETTINGS_TABLE_NAME);
        onCreate(db);
    }

    /*
    Проверка на существование даты!!!
     */
    public static boolean ifDateExist(Context __context, String userTable, Long date){
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
    }

}
