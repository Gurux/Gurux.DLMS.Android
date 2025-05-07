package gurux.dlms.ui.internal;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;


class GXCheckList extends ScrollView {
    private LinearLayout mRootLayout;

    public GXCheckList(@NonNull Context context) {
        super(context);
        mRootLayout = new LinearLayout(context);
        mRootLayout.setOrientation(LinearLayout.VERTICAL);
        addView(mRootLayout);
    }

    public void addItem(final String name, final boolean value) {
        CheckBox cb = new CheckBox(this.getContext());
        cb.setText(name);
        cb.setChecked(value);
        cb.setEnabled(false);
        mRootLayout.addView(cb);
    }
}
