package gurux.dlms.ui.internal;

import android.view.Gravity;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import gurux.dlms.enums.AccessMode;
import gurux.dlms.objects.GXDLMSObject;

public class GXHelpers {

    private GXHelpers() {

    }

    public static void handleRadioButtons(RadioButton[] buttons) {
        for (RadioButton btn : buttons) {
            btn.setOnClickListener(v -> {
                for (RadioButton it : buttons) {
                    it.setChecked(it == btn);
                }
            });
        }
    }

    public static void styleNumberPicker(NumberPicker numberPicker, float textSizeSp) {
        int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            if (numberPicker.getChildAt(i) instanceof EditText) {
                EditText editText = (EditText) numberPicker.getChildAt(i);
                editText.setTextSize(textSizeSp);
                editText.setGravity(Gravity.CENTER);
                editText.setFocusable(false);
                editText.setPadding(0, 0, 0, 0);
            }
        }
    }

    public static List<Object> getEnumValues(Object value) {
        List<Object> list = new ArrayList<>();
        if (value != null) {
            if (value instanceof Class) {
                Class c = (Class) value;
                for (Object it : c.getEnumConstants()) {
                    list.add(it);
                }
            } else if (value instanceof java.util.Set) {
                java.util.Set<?> set = (java.util.Set<?>) value;
                if (!set.isEmpty()) {
                    return getEnumValues(set.iterator().next());
                }
            } else if (value.getClass().getEnumConstants() != null) {
                for (Object it : value.getClass().getEnumConstants()) {
                    list.add(it);
                }
            }
        }
        return list;
    }


    public static String getAttributeAccess(final int version,
                                            final GXDLMSObject object) {
        StringBuilder sb = new StringBuilder();
        int cnt = object.getAttributeCount();
        for (int pos = 1; pos != cnt + 1; ++pos) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            if (version < 3) {
                AccessMode mode = object.getAccess(pos);
                sb.append(pos);
                sb.append(" = ");
                sb.append(mode);
            } else {
                sb.append(pos);
                sb.append(" = ");
                sb.append(object.getAccess3(pos));
            }
        }
        return sb.toString();
    }

    public static String getMethodAccess(final int version,
                                         final GXDLMSObject object) {
        StringBuilder sb = new StringBuilder();
        int cnt = object.getMethodCount();
        for (int pos = 1; pos != cnt + 1; ++pos) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            if (version < 3) {
                sb.append(pos);
                sb.append(" = ");
                sb.append(object.getMethodAccess(pos));
            } else {
                sb.append(pos);
                sb.append(" = ");
                sb.append(object.getMethodAccess3(pos));
            }
        }
        return sb.toString();
    }
}
