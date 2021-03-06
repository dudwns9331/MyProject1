package yj.p.mymacaron.decorators;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;

import yj.p.mymacaron.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private int color;
    private HashSet<CalendarDay> dates;

    @SuppressLint("UseCompatLoadingForDrawables")
    public EventDecorator(int color, Collection<CalendarDay> dates, Activity context) {

        int r = R.drawable.more;
        drawable = context.getResources().getDrawable(r);
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
//        view.setSelectionDrawable(drawable);
        view.addSpan(new DotSpan(12, color));
//        view.addSpan(new DotSpan(5,color)); //날짜 밑에 점.
    }
}
