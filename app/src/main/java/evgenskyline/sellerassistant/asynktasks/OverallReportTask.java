package evgenskyline.sellerassistant.asynktasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.TextView;

import java.util.ArrayList;
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

    private UnitFromDB unitFromDB; //DEBUG FOR TEST

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
        //while (mCursor.isAfterLast() == false){
            unitFromDB = new UnitFromDB();
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
        //}



        sl_db.close();
        db_seller.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(ArrayList<UnitFromDB> dbTable) {
        super.onPostExecute(dbTable);
        String stringBuilder;
        //UnitFromDB unitFromDB = new UnitFromDB();
        //unitFromDB = dbTable.get(0);
        stringBuilder = unitFromDB.getNameOfTradePoint() + "\n"
                +"Date: " + String.valueOf(unitFromDB.getDateInMiliSec()) + "\n"
                +"Card: " + String.valueOf(unitFromDB.getCardSum()) + "\n"
                +"STP: " + String.valueOf(unitFromDB.getStpSum())+"\n"
                +"Phone: " + String.valueOf(unitFromDB.getPhoneSum())+"\n"
                +"Flash: " + String.valueOf(unitFromDB.getFlashSum())+"\n"
                +"Accesories: "+String.valueOf(unitFromDB.getAccesSum()) +"\n"
                +"Foto: " + String.valueOf(unitFromDB.getFotoSum())+"\n"
                +"Term: " + String.valueOf(unitFromDB.getTermSum())+"\n"
                +"CASH-SUM: " + String.valueOf(unitFromDB.cashSumWithTerminal())+"\n"
                +"SUM-ZP: " + String.valueOf(unitFromDB.sumZpWithTerminal());
        ReportActivity.mTV_Report.setText(stringBuilder);

    }
}
