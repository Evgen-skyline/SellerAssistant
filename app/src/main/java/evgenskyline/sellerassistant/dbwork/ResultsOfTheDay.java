package evgenskyline.sellerassistant.dbwork;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import evgenskyline.sellerassistant.MainActivity;

/**
 * Created by evgen on 01.06.2016.
 */
public class ResultsOfTheDay {
    private static final String TAG ="---!!!MY_LOG!!!---";
    private Context context;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String nameOfTradePoint;
    private String month;
    private Long date=0L;

    /**
     * касса по позициям
     */
    private double cardSum;
    private double stpSum;
    private double phoneSum;
    private double flashSum;
    private double accesSum;
    private double fotoSum;
    private double termSum;

    /**
     * итоговая з/п по позициям
     */
    private double cardZP;
    private double stpZP;
    private double phoneZP;
    private double flashZP;
    private double accesZP;
    private double fotoZP;
    private double termZP;

    /**
     * проценты на позиции
     */
    private double cardP;
    private double stpP;
    private double phoneP;
    private double flashP;
    private double accesP;
    private double fotoP;
    private double termP;

    /**
     * конструктор принимающий context
     * @param context
     */
    public ResultsOfTheDay(Context context){
        this.context = context;
        clearAll();
    }

    /**
     * set name of trade point for current day
     * @param nameOfTradePoint
     */
    public void setNameOfTradePoint(String nameOfTradePoint) {
        this.nameOfTradePoint = nameOfTradePoint;
    }

    /**
     * sets the month in which to consider the salary
     * @param month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * set date in millis.
     * date mast have: hours=8, minute=1, seconds=1, milliseconds=1.
     * it is for the correct comparison date
     * @param date
     */
    public void setDate(Long date) {
        this.date = date;
    }

    /**
     * sets sum by cards
     * @param cardSum
     */
    public void setCardSum(double cardSum) {
        this.cardSum = cardSum;
    }

    /**
     * sets sum by starting packages
     * @param stpSum
     */
    public void setStpSum(double stpSum) {
        this.stpSum = stpSum;
    }

    /**
     * sets sum by phones
     * @param phoneSum
     */
    public void setPhoneSum(double phoneSum) {
        this.phoneSum = phoneSum;
    }

    /**
     * sets sum by flashcards & microSD cards
     * @param flashSum
     */
    public void setFlashSum(double flashSum) {
        this.flashSum = flashSum;
    }

    /**
     * sets sum by accesories
     * @param accesSum
     */
    public void setAccesSum(double accesSum) {
        this.accesSum = accesSum;
    }

    /**
     * sets sum by photo products
     * @param fotoSum
     */
    public void setFotoSum(double fotoSum) {
        this.fotoSum = fotoSum;
    }

    /**
     * sets sum by prepaid terminal
     * @param termSum
     */
    public void setTermSum(double termSum) {
        this.termSum = termSum;
    }


    public double getCardZP() {
        if (cardP != 0 && cardSum != 0){
            cardZP = (cardP/100) * cardSum;
        }
        return cardZP;
    }

    public double getStpZP() {
        if (stpP != 0 && stpSum != 0){
            stpZP = stpSum * (stpP/100);
        }
        return stpZP;
    }

    public double getPhoneZP() {
        if (phoneP != 0 && phoneSum != 0){
            phoneZP = (phoneP/100) * phoneSum;
        }
        return phoneZP;
    }

    public double getFlashZP() {
        if (flashP != 0 && flashSum != 0){
            flashZP = (flashP/100) * flashSum;
        }
        return flashZP;
    }

    public double getAccesZP() {
        if (accesP != 0 && accesSum != 0){
            accesZP = (accesP/100) * accesSum;
        }
        return accesZP;
    }

    public double getFotoZP() {
        if (fotoP != 0 && fotoSum != 0){
            fotoZP = (fotoP/100) * fotoSum;
        }
        return fotoZP;
    }

    public double getTermZP() {
        if (termP != 0 && termSum != 0){
            termZP = (termP/100) * termSum;
        }
        return termZP;
    }

    /**
     * initialization percentage for current trade point, from default SharedPreferences
     */
    public void initializePercentageFromSharedPreference(){
        SharedPreferences mSPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            cardP = Double.parseDouble(mSPreferences.getString(nameOfTradePoint + MainActivity.TP_CARD, null));
            stpP = Double.parseDouble(mSPreferences.getString(nameOfTradePoint + MainActivity.TP_STP, null));
            phoneP = Double.parseDouble(mSPreferences.getString(nameOfTradePoint + MainActivity.TP_PHONE, null));
            flashP = Double.parseDouble(mSPreferences.getString(nameOfTradePoint + MainActivity.TP_FLASH, null));
            accesP = Double.parseDouble(mSPreferences.getString(nameOfTradePoint + MainActivity.TP_ACCESORIES, null));
            fotoP = Double.parseDouble(mSPreferences.getString(nameOfTradePoint + MainActivity.TP_FOTO, null));
            termP = Double.parseDouble(mSPreferences.getString(nameOfTradePoint + MainActivity.TP_TERM, null));
        }catch (NumberFormatException ne){
            Toast.makeText(context, "Не правильно сохранены %" + "\n"
                    + ne.toString(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Не правильно сохранены % в SharedPreference \n" + ne.toString());
        }
        catch (Exception e){
            Toast.makeText(context, "Проблеммы с чтением настроек %" + "\n"
                    + e.toString(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Ошибка чтения настроек из SharedPreference " + e.toString());
        }
    }

    /**
     * return salary sum for current day
     * @return sum
     */
    public double getSumOfZp(){
        return getCardZP()+getStpZP()+getPhoneZP()+getFlashZP()+getAccesZP()+getFotoZP()+getTermZP();
    }

    public ContentValues getContentValuesForDbSeller(){
        ContentValues values = new ContentValues();

        values.put(DB_seller.DB_COLUMN_MONTH, month);//месяц з/п
        values.put(DB_seller.DB_COLUMN_TRADE_POINT, nameOfTradePoint);
        values.put(DB_seller.DB_COLUMN_DATE, date);//дата
        values.put(DB_seller.DB_COLUMN_SALES_CARD, cardSum);
        values.put(DB_seller.DB_COLUMN_SALES_STP, stpSum);
        values.put(DB_seller.DB_COLUMN_SALES_PHONE, phoneSum);
        values.put(DB_seller.DB_COLUMN_SALES_FLASH, flashSum);
        values.put(DB_seller.DB_COLUMN_SALES_ACCESORIES, accesSum);
        values.put(DB_seller.DB_COLUMN_SALES_FOTO, fotoSum);
        values.put(DB_seller.DB_COLUMN_SALES_TERM, termSum);

        //кладём в базу з/п от этих сумм
        values.put(DB_seller.DB_COLUMN_SALES_CARD_R, getCardZP());
        values.put(DB_seller.DB_COLUMN_SALES_STP_R, getStpZP());
        values.put(DB_seller.DB_COLUMN_SALES_PHONE_R, getPhoneZP());
        values.put(DB_seller.DB_COLUMN_SALES_FLASH_R, getFlashZP());
        values.put(DB_seller.DB_COLUMN_SALES_ACCESORIES_R, getAccesZP());
        values.put(DB_seller.DB_COLUMN_SALES_FOTO_R, getFotoZP());
        values.put(DB_seller.DB_COLUMN_SALES_TERM_R, getTermZP());

        return values;
    }

    public void clearZP(){
        cardZP = 0;
        stpZP=0;
        phoneZP=0;
        flashZP=0;
        accesZP=0;
        fotoZP=0;
        termZP=0;
    }

    public void clearPercentage(){
        cardP = 0;
        stpP = 0;
        phoneP = 0;
        flashP = 0;
        accesP = 0;
        fotoP = 0;
        termP = 0;
    }

    public void clearAll(){
        clearPercentage();
        clearZP();
        cardSum=0;
        stpSum=0;
        phoneSum=0;
        flashSum=0;
        accesSum=0;
        fotoSum=0;
        termSum=0;
    }

    @Override
    public String toString() {
        String dateStr = DateUtils.formatDateTime(this.context, date,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

        StringBuffer result = new StringBuffer();
        result.append(this.nameOfTradePoint + "\n");
        result.append("Дата: " + dateStr + "\n");
        result.append("Карточки: " + String.valueOf(cardSum) + "\n");
        result.append("Ст.пакеты: " + String.valueOf(stpSum) + "\n");
        result.append("Телефоны: " + String.valueOf(phoneSum) + "\n");
        result.append("Флешки: " + String.valueOf(flashSum) + "\n");
        result.append("Аксессуары: " + String.valueOf(accesSum) + "\n");
        result.append("Фото: " + String.valueOf(fotoSum) + "\n");
        result.append("Терминал: " + String.valueOf(termSum) + "\n");
        result.append("Касса: " + String.valueOf(cashSumWithTerminal()) + "\n");
        result.append("З/П за день(без терминала): " + String.valueOf(sumZpWithoutTerminal()) + "\n");
        result.append("З/П за терминал: " + String.valueOf(getTermZP())+"\n");
        result.append("Всего: " +  String.valueOf(sumZpWithTerminal()) + "\n");
        return result.toString();
    }

    public double cashSumWithTerminal(){
        double result = 0;
        result = cardSum + stpSum + phoneSum + flashSum + accesSum + fotoSum + termSum;
        return result;
    }

    public double sumZpWithoutTerminal(){
        double result = 0;
        result = getCardZP()+getStpZP()+getPhoneZP()+getFlashZP()+getAccesZP()+getFotoZP();
        return result;
    }

    public double sumZpWithTerminal(){
        double result = 0;
        result = getCardZP()+getStpZP()+getPhoneZP()+getFlashZP()+getAccesZP()+getFotoZP()+getTermZP();
        return result;
    }

    public void setCardZP(double cardZP) {
        this.cardZP = cardZP;
    }

    public void setStpZP(double stpZP) {
        this.stpZP = stpZP;
    }

    public void setPhoneZP(double phoneZP) {
        this.phoneZP = phoneZP;
    }

    public void setFlashZP(double flashZP) {
        this.flashZP = flashZP;
    }

    public void setAccesZP(double accesZP) {
        this.accesZP = accesZP;
    }

    public void setFotoZP(double fotoZP) {
        this.fotoZP = fotoZP;
    }

    public void setTermZP(double termZP) {
        this.termZP = termZP;
    }

    public Long getDate() {
        return date;
    }

    public double getCardSum() {
        return cardSum;
    }

    public double getStpSum() {
        return stpSum;
    }

    public double getPhoneSum() {
        return phoneSum;
    }

    public double getFlashSum() {
        return flashSum;
    }

    public double getAccesSum() {
        return accesSum;
    }

    public double getFotoSum() {
        return fotoSum;
    }

    public double getTermSum() {
        return termSum;
    }

    public String getNameOfTradePoint() {
        return nameOfTradePoint;
    }
}
