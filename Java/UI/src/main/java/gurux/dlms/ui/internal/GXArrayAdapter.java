package gurux.dlms.ui.internal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gurux.dlms.GXSimpleEntry;

class GXArrayAdapter extends ArrayAdapter<Object[]> {
    public GXArrayAdapter(Context context, List<Object[]> data) {
        super(context, android.R.layout.simple_list_item_1, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout riviLayout = new LinearLayout(getContext());
        riviLayout.setOrientation(LinearLayout.HORIZONTAL);
        riviLayout.setPadding(10, 10, 10, 10);
        Object value = getItem(position);
        if (value instanceof GXSimpleEntry<?, ?>) {
            GXSimpleEntry<?, ?> se = (GXSimpleEntry<?, ?>) value;
            TextView textView = new TextView(getContext());
            textView.setText(String.valueOf(se.getKey()));
            textView.setPadding(20, 0, 20, 0);
            riviLayout.addView(textView);
            textView = new TextView(getContext());
            textView.setText(String.valueOf(se.getValue()));
            textView.setPadding(20, 0, 20, 0);
            riviLayout.addView(textView);
        } else if (value instanceof Object[]) {
            Object[] cells = getItem(position);
            for (Object cell : cells) {
                TextView textView = new TextView(getContext());
                textView.setText(String.valueOf(cell));
                textView.setPadding(20, 0, 20, 0);
                riviLayout.addView(textView);
            }
        }
        return riviLayout;
    }
}
