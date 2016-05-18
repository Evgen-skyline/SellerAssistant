package evgenskyline.sellerassistant.asynktasks;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import evgenskyline.sellerassistant.R;

/**
 * Created by evgen on 17.05.2016.
 */
public class ReportFragment extends Fragment {
    private TextView mTV_main;
    private String report;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_day_report, null);
        mTV_main = (TextView)v.findViewById(R.id.fragmentDayReportTextView);
        return v;
    }

    public void setText(String report){
        this.report = report;
    }

    @Override
    public void onResume() {
        super.onResume();
        //mTV_main.setText(report);
    }
}
