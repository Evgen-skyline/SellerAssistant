package evgenskyline.sellerassistant.asynktasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import evgenskyline.sellerassistant.ReportActivity;
import evgenskyline.sellerassistant.dbwork.DB_seller;
import evgenskyline.sellerassistant.dbwork.UnitFromDB;

/**
 * Created by evgen on 03.05.2016.
 */
public class OverallReportTask extends AsyncTask<String, Integer, ArrayList<UnitFromDB>> {
    private Context context;
    private String user;
    private String month;
    private DB_seller db_seller;
    private SQLiteDatabase sl_db;
    private ArrayList<UnitFromDB> tableFromDB;

    //private UnitFromDB unitFromDB; //DEBUG FOR TEST

    public OverallReportTask(String _user, String _month, Context _context){
        super();
        context = _context;
        user = _user;
        month = _month;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ArrayList<UnitFromDB> doInBackground(String... params) {
        tableFromDB = new ArrayList<UnitFromDB>();
        db_seller = new DB_seller(context, user);
        sl_db = db_seller.getReadableDatabase();
        Cursor mCursor = sl_db.rawQuery("select * from "+user+" where "+DB_seller.DB_COLUMN_MONTH+" = "
                +"\"" +month+ "\"", null);
        mCursor.moveToFirst();
        while (mCursor.isAfterLast() == false){
            UnitFromDB unitFromDB = new UnitFromDB();
            unitFromDB.setNameOfTradePoint(mCursor.getString(mCursor.getColumnIndex(
                    DB_seller.DB_COLUMN_TRADE_POINT)));
            unitFromDB.setDateInMiliSec(Long.parseLong(mCursor.getString(
                    mCursor.getColumnIndex(DB_seller.DB_COLUMN_DATE))));
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

            tableFromDB.add(unitFromDB);
            mCursor.moveToNext();
        }
        sl_db.close();
        db_seller.close();
        return tableFromDB;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(ArrayList<UnitFromDB> dbTable) {
        super.onPostExecute(dbTable);
        try {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < dbTable.size(); i++) {
               result.append(convertUnitFromDbToString(dbTable.get(i)));
            }
            ReportActivity.mTV_Report.setText(result.toString());
        }catch (Exception e){
            ReportActivity.mTV_Report.setText(e.toString());
        }
    }
    private String convertUnitFromDbToString(UnitFromDB unit){

        String date = DateUtils.formatDateTime(context, unit.getDateInMiliSec(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
        StringBuffer result = new StringBuffer();
        result.append(unit.getNameOfTradePoint() + "\n");
        result.append("Дата: " + date + "\n");
        result.append("Карточки: " + String.valueOf(unit.getCardSum()) + "\n");
        result.append("Ст.пакеты: " + String.valueOf(unit.getStpSum()) + "\n");
        result.append("Телефоны: " + String.valueOf(unit.getPhoneSum()) + "\n");
        result.append("Флешки: " + String.valueOf(unit.getFlashSum()) + "\n");
        result.append("Аксессуары: " + String.valueOf(unit.getAccesSum()) + "\n");
        result.append("Фото: " + String.valueOf(unit.getFotoSum()) + "\n");
        result.append("Терминал: " + String.valueOf(unit.getTermSum()) + "\n");
        result.append("Касса: " + String.valueOf(unit.cashSumWithTerminal()) + "\n");
        result.append("З/П за день(с терминалом): " + String.valueOf(unit.sumZpWithTerminal()) + "\n");
        result.append("З/П за терминал: " + String.valueOf(unit.getTermZP())+"\n\n\n");
        return result.toString();
    }
}
