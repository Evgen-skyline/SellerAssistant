package evgenskyline.sellerassistant.exchangerates;

/**
 * Created by evgen on 16.05.2016.
 */
public class CurrencyRate {
    private String currencyCode;
    private String name;
    private double rate;
    private String shortName;
    private String date;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //for ArrayAdapter
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.getShortName() + "  " + this.getName());
        return strBuilder.toString();
    }
}
