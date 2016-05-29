package evgenskyline.sellerassistant.asynktasks;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import evgenskyline.sellerassistant.R;
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
    private long startDate;//начало месяца для посчёта терминала
    private long endDate;//конец месяца для посчёта терминала
    private double termSum = 0.0;
    private double termCash = 0.0;
    private int countWorkDay = 0;
    private OnTaskComplite listener;

    private ProgressDialog pDialog;

    private boolean flagForExeptionInSql = false;
    private String forDebug = "";
    private static final String TAG = "---!!!MY_LOG!!!--- ";

    public OverallReportTask(String _user, String _month, Context _context){
        super();
        context = _context;
        user = _user;
        month = _month;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        getDateRangeForTerminal();
        pDialog = ProgressDialog.show(context, "", "Downloading...", true);
        pDialog.show();
    }

    @Override
    protected ArrayList<UnitFromDB> doInBackground(String... params) {
        tableFromDB = new ArrayList<UnitFromDB>();
        try {
            db_seller = new DB_seller(context, user);
            sl_db = db_seller.getReadableDatabase();
        }catch (Exception e){
            forDebug = e.toString();
            //Log.e(TAG, "ОШИБКА ОТКРЫТИЯ БАЗЫ: " + e.toString());
        }
        Cursor mCursor = null;
        try {
            String query = "select * from " + user + " where " + DB_seller.DB_COLUMN_MONTH + " = "
                    + "\"" + month + "\"";
            mCursor = sl_db.rawQuery(query, null);
            //Log.w(TAG, "query = " + query);
        }catch (SQLiteException e){
            flagForExeptionInSql = true;
            Log.e(TAG, "Некорретный ответ из БД");
            return tableFromDB;
        }
        mCursor.moveToFirst();
        while (mCursor.isAfterLast() == false){
            UnitFromDB unitFromDB = new UnitFromDB(context);
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
            countWorkDay++;
            //Log.w(TAG, "unitFromDB #" + String.valueOf(countWorkDay));

            tableFromDB.add(unitFromDB);
            mCursor.moveToNext();
        }

        Cursor cursorTerm = sl_db.rawQuery("SELECT * FROM " + user
                + " WHERE " + DB_seller.DB_COLUMN_DATE + " > " + String.valueOf(startDate)
                + " AND " + DB_seller.DB_COLUMN_DATE + " < " + String.valueOf(endDate), null);
        cursorTerm.moveToFirst();
        while (cursorTerm.isAfterLast() == false){
            termSum += cursorTerm.getDouble(cursorTerm.getColumnIndex(DB_seller.DB_COLUMN_SALES_TERM_R));
            termCash += cursorTerm.getDouble(cursorTerm.getColumnIndex(DB_seller.DB_COLUMN_SALES_TERM));
            cursorTerm.moveToNext();
        }
        mCursor.close();
        cursorTerm.close();
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
        pDialog.dismiss();
        if (flagForExeptionInSql){
            ReportActivity.mTV_Report.setText(R.string.mEmptySellerReport);
            return;
        }
        if (!forDebug.equals("")){
            ReportActivity.mTV_Report.setText(forDebug);
            return;
        }
        listener.onTaskComplite(dbTable, termSum, termCash, countWorkDay);
    }

    public void setOnTaskCompliteListener(OnTaskComplite listener){
        this.listener = listener;
    }

    private void getDateRangeForTerminal(){
        Calendar monthBegin = Calendar.getInstance();
        Calendar monthEnd = Calendar.getInstance();
        //это конечно не инкапсуаляция, но так удобней, по другому не придумал
        int selectedYear = Integer.parseInt(ReportActivity.yearSpinner.getSelectedItem().toString());
        int selectedMonth = ReportActivity.arrayAdapterMonth.getPosition(
                ReportActivity.mSpinnerMonths.getSelectedItem().toString());
        monthBegin.set(Calendar.YEAR, selectedYear);
        monthBegin.set(Calendar.MONTH, selectedMonth);
        monthBegin.set(Calendar.DAY_OF_MONTH, 1);
        monthBegin.set(Calendar.HOUR, 8);
        monthBegin.set(Calendar.MINUTE, 1);
        monthBegin.set(Calendar.SECOND, 1);
        monthBegin.set(Calendar.MILLISECOND, 0);

        monthEnd.set(Calendar.YEAR, selectedYear);
        monthEnd.set(Calendar.MONTH, selectedMonth +1);
        monthEnd.set(Calendar.DAY_OF_MONTH, 1);
        monthEnd.set(Calendar.HOUR, 8);
        monthEnd.set(Calendar.MINUTE, 1);
        monthEnd.set(Calendar.SECOND, 1);
        monthEnd.set(Calendar.MILLISECOND, -10);

        startDate = monthBegin.getTimeInMillis();
        endDate = monthEnd.getTimeInMillis();
    }
}