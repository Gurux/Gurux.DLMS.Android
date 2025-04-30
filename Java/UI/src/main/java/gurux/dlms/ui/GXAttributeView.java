package gurux.dlms.ui;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

import gurux.dlms.GXDLMSConverter;
import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.GXDateTime;
import gurux.dlms.GXStructure;
import gurux.dlms.enums.DataType;

public class GXAttributeView {

    private static List<String> getEnumValues(Object value) {
        List<String> list = new ArrayList<>();
        if (value != null && value.getClass().getEnumConstants() != null) {
            for (Object it : value.getClass().getEnumConstants()) {
                list.add(GXDLMSConverter.toCamelCase(it.toString()));
            }
        }
        return list;
    }


    private static boolean toBoolean(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof String) {
            return ((String) value).equalsIgnoreCase("true");
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }

        return false;
    }

    public static View create(final Context context,
                              final Object value,
                              final DataType type) {
        View view;
        if (type == DataType.STRING || type == DataType.NONE) {
            EditText input = new EditText(context);
            if (value != null) {
                input.setText(String.valueOf(value));
            }
            view = input;
        } else if (type == DataType.OCTET_STRING ||
                type == DataType.STRING_UTF8) {
            EditText input = new EditText(context);
            if (value instanceof byte[]) {
                input.setText(GXDLMSTranslator.toHex((byte[]) value));
            } else if (value != null) {
                input.setText(String.valueOf(value));
            } else {
                input.setText("");
            }
            view = input;
        } else if (type == DataType.DATE || type == DataType.DATETIME ||
                type == DataType.TIME) {
            EditText input = new EditText(context);
            if (value != null) {
                GXDateTime dt = (GXDateTime) value;
                input.setText(dt.toFormatMeterString(context.getResources().getConfiguration().getLocales().get(0)));
            }
            view = input;
        } else if (type == DataType.BOOLEAN) {
            SwitchCompat input = new SwitchCompat(context);
            input.setChecked(toBoolean(value));
            view = input;
        } else if (type == DataType.UINT8 ||
                type == DataType.UINT16 ||
                type == DataType.UINT32 ||
                type == DataType.UINT64 ||
                type == DataType.INT8 ||
                type == DataType.INT16 ||
                type == DataType.INT32 ||
                type == DataType.INT64 ||
                type == DataType.FLOAT32 ||
                type == DataType.FLOAT64) {
            EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setText(String.valueOf(value));
            view = input;
        } else if (type == DataType.ENUM) {
            Spinner spinner = new Spinner(context);
            List<String> values = getEnumValues(value);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_spinner_item,
                    values
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            view = spinner;
        } else if (type == DataType.STRUCTURE && value instanceof GXStructure) {
            List<String> rows = new ArrayList<>();
            for (Object it : (GXStructure) value) {
                if (it instanceof byte[]) {
                    rows.add(GXDLMSTranslator.toHex((byte[]) it));
                } else {
                    rows.add(String.valueOf(it));
                }
            }
            ListView listView = new ListView(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_list_item_1, rows);
            listView.setAdapter(adapter);
            view = listView;
        } else if ((type == DataType.ARRAY) && (value instanceof List<?>)) {
            GXArrayAdapter adapter = new GXArrayAdapter(context, (List<Object[]>) value);
            ListView listView = new ListView(context);
            listView.setAdapter(adapter);
            view = listView;
        } else {
            EditText input = new EditText(context);
            if (value != null) {
                input.setText(String.valueOf(value));
            }
            view = input;
            if (value != null) {
                Toast.makeText(context, "Unsupported value type: " + value.getClass().getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Unsupported value type: " + type, Toast.LENGTH_SHORT).show();
            }
        }
        return view;
    }
}
