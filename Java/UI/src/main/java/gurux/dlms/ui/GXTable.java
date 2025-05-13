package gurux.dlms.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;

public class GXTable extends TableLayout {

    public GXTable(Context context, String[] headers) {
        this(context, Arrays.asList(headers));
    }

    public GXTable(Context context, Iterable<String> headers) {
        this(context);
        setStretchAllColumns(true);
        TableRow headerRow = new TableRow(context);
        for (String it : headers) {
            TextView header = new TextView(context);
            header.setText(it);
            header.setTextSize(18);
            header.setTypeface(null, Typeface.BOLD);
            header.setGravity(Gravity.CENTER);
            header.setPadding(16, 16, 16, 16);
            header.setBackgroundColor(Color.LTGRAY);
            headerRow.addView(header);
        }
        addView(headerRow);
    }

    public GXTable(Context context) {
        super(context);
    }

    public GXTable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addRow(Context context, Object[] values) {
        addRow(context, Arrays.asList(values));
    }

    public void addRow(Context context, Iterable<Object> values) {
        TableRow row = new TableRow(context);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        for (Object it : values) {

            TextView cell = new TextView(context);
            cell.setText(String.valueOf(it));
            cell.setPadding(16, 16, 16, 16);
            row.addView(cell);
        }
        addView(row);
    }
}