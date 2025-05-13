package gurux.dlms.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class GXAccordion extends LinearLayout {

    ArrayList<View> mViews = new ArrayList<>();

    public GXAccordion(Context context, int header) {
        this(context, context.getString(header));
    }

    public GXAccordion(Context context, String header) {
        super(context);
        setOrientation(VERTICAL);
        TextView h = new TextView(context);
        h.setText(header);
        h.setTextSize(18);
        h.setTypeface(null, Typeface.BOLD);
        h.setPadding(16, 16, 16, 16);
        h.setBackgroundColor(Color.LTGRAY);
        h.setClickable(true);
        h.setFocusable(true);
        super.addView(h);
        h.setOnClickListener(v -> {
            for (View it : mViews) {
                it.setVisibility(
                        it.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public void addView(View item) {
        super.addView(item);
        mViews.add(item);
    }
}