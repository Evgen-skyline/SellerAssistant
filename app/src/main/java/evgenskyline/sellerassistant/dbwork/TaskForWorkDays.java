package evgenskyline.sellerassistant.dbwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import evgenskyline.sellerassistant.MainActivity;

/**
 * Created by evgen on 01.06.2016.
 */
public class TaskForWorkDays extends AsyncTask<String, Integer, ArrayList<ResultsOfTheDay>> {

    private ProgressDialog pDialog;
    private Context context;
    private DB_seller db_seller;    //БД с таблицей юзера(userName)
    private SQLiteDatabase sl_db;
    private String userName;
    private ArrayList<ResultsOfTheDay> dayContainer;
    private OnWorkDaysTaskComplite listener;

    public TaskForWorkDays(String userName, Context context){
        this.context = context;
        this.userName = userName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = ProgressDialog.show(context, "", "Подождите", true);
        pDialog.show();
    }

    @Override
    protected ArrayList<ResultsOfTheDay> doInBackground(String... params) {
        //userName = params[0];
        dayContainer = new ArrayList<ResultsOfTheDay>();
        try {
            db_seller = new DB_seller(context, userName);
            sl_db = db_seller.getReadableDatabase();
        }catch (Exception e){
            Log.e(MainActivity.TAG, "ОШИБКА ОТКРЫТИЯ БАЗЫ: " + e.toString());
            return null;
        }
        String query = "select * from " + userName;
        Cursor mCursor = null;
        try {
            mCursor = sl_db.rawQuery(query, null);
        }catch (SQLiteException e){
            Log.e(MainActivity.TAG, "Из базы получена фигня: " + e.toString());
            return null;
        }
        mCursor.moveToFirst();
        while (mCursor.isAfterLast() == false) {
            ResultsOfTheDay unitFromDB = new ResultsOfTheDay(context);
            unitFromDB.setId(mCursor.getInt(mCursor.getColumnIndex(
                    BaseColumns._ID)));
            unitFromDB.setNameOfTradePoint(mCursor.getString(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_TRADE_POINT)));
            unitFromDB.setDate(mCursor.getLong(
                    mCursor.getColumnIndex(DB_seller.DB_COLUMN_DATE)));
            unitFromDB.setMonth(mCursor.getString(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_MONTH)));
            unitFromDB.setCardSum(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_CARD)));
            unitFromDB.setStpSum(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_STP)));
            unitFromDB.setPhoneSum(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_PHONE)));
            unitFromDB.setFlashSum(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_FLASH)));
            unitFromDB.setAccesSum(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_ACCESORIES)));
            unitFromDB.setFotoSum(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_FOTO)));
            unitFromDB.setTermSum(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_TERM)));

            unitFromDB.setCardZP(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_CARD_R)));
            unitFromDB.setStpZP(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_STP_R)));
            unitFromDB.setPhoneZP(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_PHONE_R)));
            unitFromDB.setFlashZP(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_FLASH_R)));
            unitFromDB.setAccesZP(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_ACCESORIES_R)));
            unitFromDB.setFotoZP(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_FOTO_R)));
            unitFromDB.setTermZP(mCursor.getDouble(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_SALES_TERM_R)));

            dayContainer.add(unitFromDB);
            mCursor.moveToNext();
        }
        mCursor.close();
        sl_db.close();
        db_seller.close();

        return dayContainer;
    }

    @Override
    protected void onPostExecute(ArrayList<ResultsOfTheDay> resultsOfTheDays) {
        super.onPostExecute(resultsOfTheDays);
        pDialog.dismiss();
        if (resultsOfTheDays != null) {
            listener.onTaskComplite(resultsOfTheDays);
        }else {
            Log.e(MainActivity.TAG, "ЛИСТЕНЕР ПРОШЁЛ МИМО!!!");
        }
    }

    public void setOnTaskCompliteListener(OnWorkDaysTaskComplite listener){
        this.listener = listener;
    }
}
