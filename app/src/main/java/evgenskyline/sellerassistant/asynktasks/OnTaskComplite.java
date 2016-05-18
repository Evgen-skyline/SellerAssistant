package evgenskyline.sellerassistant.asynktasks;

import java.util.ArrayList;

import evgenskyline.sellerassistant.dbwork.UnitFromDB;

/**
 * Created by evgen on 18.05.2016.
 */
public interface OnTaskComplite {
    void onTaskComplite(ArrayList<UnitFromDB> dbTable, double termSum, double termCash, int countWorkDay);
}
