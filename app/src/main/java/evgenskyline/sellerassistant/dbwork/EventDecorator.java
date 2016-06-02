package evgenskyline.sellerassistant.dbwork;

import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

import evgenskyline.sellerassistant.R;


/**
 * Created by evgen on 01.06.2016.
 */
public class EventDecorator implements DayViewDecorator {
    //private final int color;
    private final HashSet<CalendarDay> dates;
    private Drawable drawable;

    public EventDecorator(/*int color,*/ Collection<CalendarDay> dates, Drawable drawable){
        //this.color = color;
        this.dates = new HashSet<>(dates);
        this.drawable = drawable;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawable);
    }
}
