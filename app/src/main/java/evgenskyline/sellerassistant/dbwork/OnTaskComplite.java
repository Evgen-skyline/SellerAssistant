package evgenskyline.sellerassistant.dbwork;

import java.util.ArrayList;

import evgenskyline.sellerassistant.dbwork.ResultsOfTheDay;

/**
 * Created by evgen on 18.05.2016.
 */
public interface OnTaskComplite {
    void onTaskComplite(ArrayList<ResultsOfTheDay> dbTable, double termSum, double termCash, int countWorkDay);
}
