package evgenskyline.sellerassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by evgen on 12.04.2016.
 */
public class DB_seller extends SQLiteOpenHelper implements BaseColumns {

    private String DB_TABLE_NAME = "";
    private static final String DB_NAME = "SellersData.db";
    private static final int DB_VERSION = 1;

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

    public String CREATE_SCRIPT = "create table if not exists " + DB_TABLE_NAME + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + DB_COLUMN_DATE + " integer not null, "
            + DB_COLUMN_TRADE_POINT + " text not null, "
            + DB_COLUMN_SALES_CARD + " real not null, "
            + DB_COLUMN_SALES_STP + " real not null, "
            + DB_COLUMN_SALES_PHONE + " real not null, "
            + DB_COLUMN_SALES_FLASH + " real not null, "
            + DB_COLUMN_SALES_ACCESORIES + " real not null, "
            + DB_COLUMN_SALES_FOTO + " real not null, "
            + DB_COLUMN_SALES_TERM + " real not null, "
            + DB_COLUMN_SALES_CARD_R + " real not null, "
            + DB_COLUMN_SALES_STP_R + " real not null, "
            + DB_COLUMN_SALES_PHONE_R + " real not null, "
            + DB_COLUMN_SALES_FLASH_R + " real not null, "
            + DB_COLUMN_SALES_ACCESORIES_R + " real not null, "
            + DB_COLUMN_SALES_FOTO_R + " real not null, "
            + DB_COLUMN_SALES_TERM_R + " real not null);";

    public DB_seller(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DB_seller(Context context, String _seller){
        super(context, DB_NAME, null, DB_VERSION);
        DB_TABLE_NAME = _seller;
    }

    public DB_seller(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(!(DB_TABLE_NAME.equals("")) ) {
            db.execSQL(CREATE_SCRIPT);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite ", "обновляемся с версии " + oldVersion + " на версию " + newVersion);
        db.execSQL("DROP TABLE IF IT EXISTS " + DB_TABLE_NAME);
        onCreate(db);
    }
}
