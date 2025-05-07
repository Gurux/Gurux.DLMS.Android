package gurux.dlms.ui.internal;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.GXDateTime;
import gurux.dlms.GXStructure;
import gurux.dlms.enums.DataType;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.ui.IGXActionListener;

public class GXAttributeView {


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
            List<Object> values = GXHelpers.getEnumValues(value);
            ArrayAdapter<Object> adapter = new ArrayAdapter<>(
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

    protected static String getDirtyLabel(TextView value) {
        String str = value.getText().toString();
        if (!str.endsWith("\uD83C\uDF1F")) {
            str += " " + "\uD83C\uDF1F";
        }
        return str;
    }

    private static void addListener(final Context context,
                                    final IGXActionListener listener,
                                    final GXDLMSObject object,
                                    final int index,
                                    final Object input,
                                    final TextView label,
                                    final IGXGet get,
                                    final IGXSet set) {
        if (set != null) {
            if (input instanceof EditText) {
                EditText e = (EditText) input;
                e.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus) {
                        try {
                            Object value = get.run(object, index);
                            if (!e.getText().toString().equals(String.valueOf(value))) {
                                //If value has changed.
                                label.setText(getDirtyLabel(label));
                                object.setDirty(index, true);
                                if (listener != null && !listener.isProgress()) {
                                    listener.onObjectChanged(object, index);
                                }
                                set.run(object, index, e.getText().toString());
                            }
                        } catch (Exception ex) {
                            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (input instanceof Spinner) {
                Spinner spinner = (Spinner) input;
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    boolean first = true;

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (first) {
                            first = false;
                            return;
                        }
                        try {
                            Object newValue = parent.getAdapter().getItem(position);
                            Object value = get.run(object, index);
                            if (!String.valueOf(newValue).equals(String.valueOf(value))) {
                                //If value has changed.
                                label.setText(getDirtyLabel(label));
                                object.setDirty(index, true);
                                if (listener != null && !listener.isProgress()) {
                                    listener.onObjectChanged(object, index);
                                }
                                set.run(object, index, newValue);
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing.
                    }
                });
            }
        }
    }

    public static View create(final Context context,
                              final IGXActionListener listener,
                              final GXDLMSObject object,
                              final int index,
                              final DataType type,
                              final TextView label,
                              final IGXGet get,
                              final IGXSet write) {
        View view;
        if (get == null) {
            throw new RuntimeException("Invalid get.");
        }
        Object value = get.run(object, index);
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
            List<Object> values = GXHelpers.getEnumValues(value);
            ArrayAdapter<Object> adapter = new ArrayAdapter<>(
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
        addListener(context, listener, object, index, view, label, get, write);
        return view;
    }
}
