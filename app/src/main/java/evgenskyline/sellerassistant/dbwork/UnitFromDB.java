package evgenskyline.sellerassistant.dbwork;

import android.content.Context;
import android.text.format.DateUtils;

/**
 * Created by evgen on 04.05.2016.
 */
public class UnitFromDB {
    private String nameOfTradePoint;
    private String month;
    private Long date=0L;
    private double cardSum=0;
    private double stpSum=0;
    private double phoneSum=0;
    private double flashSum=0;
    private double accesSum=0;
    private double fotoSum=0;
    private double termSum=0;

    private double cardZP = 0;
    private double stpZP=0;
    private double phoneZP=0;
    private double flashZP=0;
    private double accesZP=0;
    private double fotoZP=0;
    private double termZP=0;
    private Context context;

    public UnitFromDB(Context context){this.context = context;}

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

    public Long getDate() {
        return date;
    }
    public void setDate(Long date){this.date = date;}

    public double getCardSum() {
        return cardSum;
    }

    public void setCardSum(double cardSum) {
        this.cardSum = cardSum;
    }

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

    @Override
    public String toString() {
        String dateStr = DateUtils.formatDateTime(this.context, this.getDate(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

        StringBuffer result = new StringBuffer();
        result.append(this.getNameOfTradePoint() + "\n");
        result.append("Дата: " + dateStr + "\n");
        result.append("Карточки: " + String.valueOf(this.getCardSum()) + "\n");
        result.append("Ст.пакеты: " + String.valueOf(this.getStpSum()) + "\n");
        result.append("Телефоны: " + String.valueOf(this.getPhoneSum()) + "\n");
        result.append("Флешки: " + String.valueOf(this.getFlashSum()) + "\n");
        result.append("Аксессуары: " + String.valueOf(this.getAccesSum()) + "\n");
        result.append("Фото: " + String.valueOf(this.getFotoSum()) + "\n");
        result.append("Терминал: " + String.valueOf(this.getTermSum()) + "\n");
        result.append("Касса: " + String.valueOf(this.cashSumWithTerminal()) + "\n");
        result.append("З/П за день(без терминала): " + String.valueOf(this.sumZpWithoutTerminal()) + "\n");
        result.append("З/П за терминал: " + String.valueOf(this.getTermZP())+"\n");
        result.append("Всего: " +  String.valueOf(this.sumZpWithTerminal()) + "\n\n\n");
        return result.toString();
    }
}