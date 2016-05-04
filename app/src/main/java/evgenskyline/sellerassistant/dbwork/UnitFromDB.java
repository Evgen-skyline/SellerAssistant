package evgenskyline.sellerassistant.dbwork;

/**
 * Created by evgen on 04.05.2016.
 */
public class UnitFromDB {
    private String nameOfTradePoint;
    private String month;
    private long dateInMiliSec;
    private double cardSum;
    private double stpSum;
    private double phoneSum;
    private double flashSum;
    private double accesSum;
    private double fotoSum;
    private double termSum;

    private double cardZP;
    private double stpZP;
    private double phoneZP;
    private double flashZP;
    private double accesZP;
    private double fotoZP;
    private double termZP;

    public UnitFromDB(){}

    public String getMonth() {
        return month;
    }
    //месяц в который считать з/п
    public void setMonth(String month) {
        this.month = month;
    }

    public String getNameOfTradePoint() {
        return nameOfTradePoint;
    }

    public void setNameOfTradePoint(String nameOfTradePoint) {
        this.nameOfTradePoint = nameOfTradePoint;
    }

    public long getDateInMiliSec() {
        return dateInMiliSec;
    }

    public void setDateInMiliSec(long dateInMiliSec) {
        this.dateInMiliSec = dateInMiliSec;
    }
    /*public void setDateInMiliSec(String dateInMiliSec){
        this.dateInMiliSec = Long.parseLong(dateInMiliSec);
    }*/

    public double getCardSum() {
        return cardSum;
    }

    public void setCardSum(double cardSum) {
        this.cardSum = cardSum;
    }
    /*public void setCardSum(String cardSum){
        this.cardSum = Double.parseDouble(cardSum);
    }*/

    public double getStpSum() {
        return stpSum;
    }

    public void setStpSum(double stpSum) {
        this.stpSum = stpSum;
    }

    public double getPhoneSum() {
        return phoneSum;
    }

    public void setPhoneSum(double phoneSum) {
        this.phoneSum = phoneSum;
    }

    public double getFlashSum() {
        return flashSum;
    }

    public void setFlashSum(double flashSum) {
        this.flashSum = flashSum;
    }

    public double getAccesSum() {
        return accesSum;
    }

    public void setAccesSum(double accesSum) {
        this.accesSum = accesSum;
    }

    public double getFotoSum() {
        return fotoSum;
    }

    public void setFotoSum(double fotoSum) {
        this.fotoSum = fotoSum;
    }

    public double getTermSum() {
        return termSum;
    }

    public void setTermSum(double termSum) {
        this.termSum = termSum;
    }

    public double getCardZP() {
        return cardZP;
    }

    public void setCardZP(double cardZP) {
        this.cardZP = cardZP;
    }

    public double getStpZP() {
        return stpZP;
    }

    public void setStpZP(double stpZP) {
        this.stpZP = stpZP;
    }

    public double getPhoneZP() {
        return phoneZP;
    }

    public void setPhoneZP(double phoneZP) {
        this.phoneZP = phoneZP;
    }

    public double getFlashZP() {
        return flashZP;
    }

    public void setFlashZP(double flashZP) {
        this.flashZP = flashZP;
    }

    public double getAccesZP() {
        return accesZP;
    }

    public void setAccesZP(double accesZP) {
        this.accesZP = accesZP;
    }

    public double getFotoZP() {
        return fotoZP;
    }

    public void setFotoZP(double fotoZP) {
        this.fotoZP = fotoZP;
    }

    public double getTermZP() {
        return termZP;
    }

    public void setTermZP(double termZP) {
        this.termZP = termZP;
    }

    public double sumZpWithoutTerminal(){
        double result = 0;
        result = cardZP + stpZP + phoneZP + flashZP + accesZP + fotoZP;
        return result;
    }

    public double sumZpWithTerminal(){
        double result = 0;
        result = cardZP + stpZP + phoneZP + flashZP + accesZP + fotoZP + termZP;
        return result;
    }

    public double cashSumWithoutTerminal(){
        double result = 0;
        result = cardSum + stpSum + phoneSum + flashSum + accesSum + fotoSum;
        return result;
    }

    public double cashSumWithTerminal(){
        double result = 0;
        result = cardSum + stpSum + phoneSum + flashSum + accesSum + fotoSum + termSum;
        return result;
    }
}
   /* public static final String DB_COLUMN_MONTH = "month";
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
    public static final String DB_COLUMN_SALES_TERM_R = "terminalR";*/