package gurux.dlms.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gurux.common.IGXMedia;
import gurux.dlms.GXDLMSClient;
import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.objects.GXDLMSAssociationLogicalName;
import gurux.dlms.objects.GXDLMSAssociationShortName;
import gurux.dlms.objects.GXDLMSClock;
import gurux.dlms.objects.GXDLMSData;
import gurux.dlms.objects.GXDLMSDisconnectControl;
import gurux.dlms.objects.GXDLMSExtendedRegister;
import gurux.dlms.objects.GXDLMSGprsSetup;
import gurux.dlms.objects.GXDLMSHdlcSetup;
import gurux.dlms.objects.GXDLMSIECLocalPortSetup;
import gurux.dlms.objects.GXDLMSIp4Setup;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSProfileGeneric;
import gurux.dlms.objects.GXDLMSPushSetup;
import gurux.dlms.objects.GXDLMSRegister;
import gurux.dlms.objects.GXDLMSTcpUdpSetup;
import gurux.dlms.ui.internal.IGXSelect;

public class GXDLMSUi {

    /*
    Constructor.
     */
    private GXDLMSUi() {

    }

    public static BaseObjectFragment newInstance(final FragmentActivity activity,
                                                 final IGXActionListener listener,
                                                 final GXDLMSClient client,
                                                 final IGXMedia media,
                                                 GXDLMSObject value) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(activity).get(ObjectViewModel.class);
        objectViewModel.setMedia(media);
        objectViewModel.setObject(value);
        objectViewModel.setListener(listener);
        objectViewModel.setClient(client);
        BaseObjectFragment fragment;
        if (value instanceof GXDLMSAssociationLogicalName) {
            fragment = new AssociationLogicalNameObjectFragment();
        } else if (value instanceof GXDLMSAssociationShortName) {
            fragment = new AssociationShortNameObjectFragment();
        } else if (value instanceof GXDLMSData) {
            fragment = new DataObjectFragment();
        } else if (value instanceof GXDLMSExtendedRegister) {
            fragment = new ExtendedRegisterObjectFragment();
        } else if (value instanceof GXDLMSRegister) {
            fragment = new RegisterObjectFragment();
        } else if (value instanceof GXDLMSClock) {
            fragment = new ClockObjectFragment();
        } else if (value instanceof GXDLMSGprsSetup) {
            fragment = new GprsSetupObjectFragment();
        } else if (value instanceof GXDLMSHdlcSetup) {
            fragment = new HdlcSetupObjectFragment();
        } else if (value instanceof GXDLMSIECLocalPortSetup) {
            fragment = new IecLocalPortSetupObjectFragment();
        } else if (value instanceof GXDLMSIp4Setup) {
            fragment = new Ip4SetupObjectFragment();
        } else if (value instanceof GXDLMSTcpUdpSetup) {
            fragment = new TcpUdpSetupObjectFragment();
        } else if (value instanceof GXDLMSPushSetup) {
            fragment = new PushSetupObjectFragment();
        } else if (value instanceof GXDLMSProfileGeneric) {
            fragment = new ProfileGenericObjectFragment();
        } else if (value instanceof GXDLMSDisconnectControl) {
            fragment = new DisconnectControlObjectFragment();
        } else {
            fragment = new BaseObjectFragment();
        }
        return fragment;
    }

    /**
     * Show selection list where user can select target.
     *
     * @param context  Context
     * @param title    Dialog title.
     * @param selected Selected item.
     * @param values   List of available values.
     * @param notify   Callback interface.
     * @param helpUrl  Help url.
     * @param <T>      Data type.
     */
    public static <T> void showSelection(
            @NotNull Context context,
            int title,
            final T selected,
            final List<T> values,
            @NotNull final IGXSelect<T> notify,
            final String helpUrl) {
        showSelection(context, context.getString(title),
                selected, values, notify, helpUrl);
    }

    /**
     * Show selection list where user can select target.
     *
     * @param context  Context
     * @param title    Dialog title.
     * @param selected Selected item.
     * @param values   List of available values.
     * @param notify   Callback interface.
     * @param helpUrl  Help url.
     * @param <T>      Data type.
     */
    public static <T> void showSelection(
            @NotNull Context context,
            @NotNull String title,
            final T selected,
            final List<T> values,
            @NotNull final IGXSelect<T> notify,
            final String helpUrl) {

        ArrayList<String> strValues = new ArrayList<>();
        for (T it : values) {
            strValues.add(String.valueOf(it));
        }
        int index = values.indexOf(selected);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, (d, which) -> d.dismiss())
                .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel())
                .setSingleChoiceItems(strValues.toArray(new String[0]), index, (dialog, which) -> {
                    try {
                        T newValue = (T) values.get(which);
                        notify.run(newValue, which);
                        dialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        addHelp(builder, context, helpUrl);
        builder.show();
    }


    /**
     * Show checklist where user can select multiple targets.
     *
     * @param context  Context
     * @param title    Dialog title.
     * @param selected Selected item.
     * @param values   List of available values.
     * @param notify   Callback interface.
     * @param helpUrl  Help url.
     * @param <T>      Data type.
     */
    public static <T> void showCheckListDlg(
            @NotNull Context context,
            int title,
            final T selected,
            final List<T> values,
            @NotNull final IGXSelect<T> notify,
            final String helpUrl) {
        showCheckListDlg(context, context.getString(title),
                selected, values, notify, helpUrl);
    }

    /**
     * Show checklist where user can select multiple targets.
     *
     * @param context  Context
     * @param title    Dialog title.
     * @param selected Selected item.
     * @param values   List of available values.
     * @param notify   Callback interface.
     * @param helpUrl  Help url.
     * @param <T>      Data type.
     */
    public static <T> void showCheckListDlg(
            @NotNull Context context,
            @NotNull String title,
            final T selected,
            final List<T> values,
            @NotNull final IGXSelect<T> notify,
            final String helpUrl) {
        ListView layout = new ListView(context);
        layout.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        ArrayAdapter<T> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_multiple_choice,
                values
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        layout.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, (d, which) -> d.dismiss())
                .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel());
        addHelp(builder, context, helpUrl);
        AlertDialog dialog = builder.show();

        layout.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    T newValue = (T) parent.getAdapter().getItem(position);
                    notify.run(newValue, position);
                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dialog.cancel();
            }
        });
    }


    /**
     * Show spinner where user can change target.
     *
     * @param context  Context
     * @param title    Dialog title.
     * @param selected Selected item.
     * @param values   List of available values.
     * @param notify   Callback interface.
     * @param helpUrl  Help url.
     * @param <T>      Data type.
     */
    public static <T> void showSpinnerDlg(
            @NotNull Context context,
            int title,
            final T selected,
            final List<T> values,
            @NotNull final IGXSelect<T> notify,
            final String helpUrl) {
        showSpinnerDlg(context, context.getString(title),
                selected, values, notify, helpUrl);
    }

    /**
     * Show spinner where user can change target.
     *
     * @param context  Context
     * @param title    Dialog title.
     * @param selected Selected item.
     * @param values   List of available values.
     * @param notify   Callback interface.
     * @param helpUrl  Help url.
     * @param <T>      Data type.
     */
    public static <T> void showSpinnerDlg(
            @NotNull Context context,
            @NotNull String title,
            final T selected,
            final List<T> values,
            @NotNull final IGXSelect<T> notify,
            final String helpUrl) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        Spinner spinner = new Spinner(context);
        ArrayAdapter<T> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                values
        );
        layout.addView(spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(values.indexOf(selected));

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, (d, which) -> d.dismiss())
                .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel());
        addHelp(builder, context, helpUrl);
        AlertDialog dialog = builder.show();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean first = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (first) {
                    first = false;
                    return;
                }
                try {
                    @SuppressWarnings("unchecked")
                    T newValue = (T) parent.getAdapter().getItem(position);
                    notify.run(newValue, position);
                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dialog.cancel();
            }
        });
    }

    private static void addHelp(@NotNull AlertDialog.Builder builder,
                                @NotNull Context context,
                                String helpUrl) {
        if (helpUrl != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(helpUrl));
            builder.setNeutralButton(R.string.help, (d, which) ->
            {
                context.startActivity(intent);
            });
        }
    }

    public static <T> void showByteArrayDlg(
            @NotNull Context context,
            int title,
            int caption,
            final T value,
            @NotNull final IGXSelect<T> notify,
            final String help) {
        {
            showByteArrayDlg(context,
                    context.getString(title),
                    context.getString(caption),
                    value, notify,
                    help);
        }
    }

    /**
     * Check is byte buffer ASCII string.
     *
     * @param value Byte array.
     * @return Is ASCII string.
     */
    public static boolean isAsciiString(byte[] value) {
        if (value != null) {
            for (byte it : value) {
                if ((it < 32 || it > 127) && it != '\r' && it != '\n' && it != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static <T> void showByteArrayDlg(
            @NotNull Context context,
            @NotNull String title,
            @NotNull String caption,
            final T value,
            @NotNull final IGXSelect<T> notify,
            final String helpUrl) {

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        TextView text = new TextView(context);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        text.setText(caption);
        layout.addView(text);

        final EditText edit = new EditText(context);
        layout.addView(edit);
        SwitchCompat hex = new SwitchCompat(context);
        hex.setText("Hex");
        hex.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        layout.addView(hex);
        if (value instanceof byte[]) {
            if (isAsciiString((byte[]) value)) {
                edit.setText(new String((byte[]) value));
            } else {
                edit.setText(GXDLMSTranslator.toHex((byte[]) value));
                hex.setChecked(true);
            }
        } else if (value instanceof String) {
            edit.setText((String) value);
            if (!StandardCharsets.US_ASCII.newEncoder().canEncode((String) value)) {
                hex.setChecked(true);
            }
        }
        //Set the cursor at the end of the text.
        edit.setSelection(edit.getText().length());
        edit.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (hex.isChecked()) {
                    for (int i = start; i < end; i++) {
                        char c = source.charAt(i);
                        if (c != ' ' && c != '-' && (c < 'a' || c > 'f')
                                && (c < 'A' || c > 'F')
                                && (c < '0' || c > '9')) {
                            return "";
                        }
                    }
                }
                return null;
            }
        }});
        hex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String tmp = edit.getText().toString();
                try {
                    if (isChecked) {
                        tmp = GXDLMSTranslator.toHex(tmp.getBytes());
                    } else {
                        tmp = new String(GXDLMSTranslator.hexToBytes(tmp));
                    }
                    edit.setText(tmp);
                } catch (Exception e) {
                    hex.setChecked(!isChecked);
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel());
        addHelp(builder, context, helpUrl);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            edit.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
        });
        dialog.show();
        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(v -> {
            try {
                if (value instanceof byte[]) {
                    if (hex.isChecked()) {
                        notify.run((T) GXDLMSTranslator.hexToBytes(edit.getText().toString()), 1);
                    } else {
                        notify.run((T) edit.getText().toString().getBytes(), 1);
                    }
                } else {
                    notify.run((T) edit.getText().toString(), hex.isChecked() ? 1 : 0);
                }
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void showTextDlg(
            @NotNull Context context,
            int title,
            int caption,
            final String value,
            @NotNull final IGXSelect<String> notify,
            final String help) {
        showTextDlg(context,
                context.getString(title),
                context.getString(caption),
                value, notify,
                help);
    }

    public static void showTextDlg(
            @NotNull Context context,
            @NotNull String title,
            @NotNull String caption,
            final String value,
            @NotNull final IGXSelect<String> notify,
            final String helpUrl) {

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        TextView text = new TextView(context);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        text.setText(caption);
        layout.addView(text);

        final EditText edit = new EditText(context);
        edit.setText(value);
        //Set the cursor at the end of the text.
        edit.setSelection(edit.getText().length());
        layout.addView(edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel());
        addHelp(builder, context, helpUrl);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            edit.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
        });
        dialog.show();
        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(v -> {
            try {
                if (!edit.getText().toString().equals(value)) {
                    notify.run(edit.getText().toString(), 0);
                }
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showObisCodeDlg(
            @NotNull Context context,
            int title,
            int caption,
            final String value,
            @NotNull final IGXSelect<String> notify,
            final String helpUrl) {
        {
            showObisCodeDlg(context,
                    context.getString(title),
                    context.getString(caption),
                    value, notify,
                    helpUrl);
        }
    }

    public static void showObisCodeDlg(
            @NotNull Context context,
            @NotNull String title,
            @NotNull String caption,
            final String value,
            @NotNull final IGXSelect<String> notify,
            final String helpUrl) {

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        TextView text = new TextView(context);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        text.setText(caption);
        layout.addView(text);

        final EditText edit = new EditText(context);
        edit.setText(value);
        //Set the cursor at the end of the text.
        edit.setSelection(edit.getText().length());
        layout.addView(edit);
        edit.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (c != '.' && (c < '0' || c > '9')) {
                        return "";
                    }
                }
                return null;
            }
        }});
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel());
        addHelp(builder, context, helpUrl);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            edit.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
        });
        dialog.show();
        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(v -> {
            try {
                if (!edit.getText().toString().equals(value)) {
                    notify.run(edit.getText().toString(), 0);
                }
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showTimeDlg(
            @NotNull Context context,
            int title,
            int caption,
            final Date value,
            @NotNull final IGXSelect<Date> notify,
            final String helpUrl) {
        {
            showTimeDlg(context,
                    context.getString(title),
                    context.getString(caption),
                    value, notify,
                    helpUrl);
        }
    }

    public static void showTimeDlg(
            @NotNull Context context,
            @NotNull String title,
            @NotNull String caption,
            final Date value,
            @NotNull final IGXSelect<Date> notify,
            final String helpUrl) {

        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        TextView text = new TextView(context);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        text.setText(caption);
        layout.addView(text);

        final EditText edit = new EditText(context);
        layout.addView(edit);
        edit.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
        edit.setText(sdf.format(value));
        //Set the cursor at the end of the text.
        edit.setSelection(edit.getText().length());

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel());
        addHelp(builder, context, helpUrl);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            edit.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
        });
        dialog.show();
        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(v -> {
            try {
                if (!edit.getText().toString().equals(value)) {
                    notify.run(sdf.parse(text.getText().toString()), 0);
                }
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static <T> void showNumertDlg(
            @NotNull Context context,
            int title,
            int caption,
            final T value,
            @NotNull final IGXSelect<T> notify,
            final String helpUrl) {
        {
            showNumertDlg(context,
                    context.getString(title),
                    context.getString(caption),
                    value, notify,
                    helpUrl);
        }
    }

    public static <T> void showNumertDlg(
            @NotNull Context context,
            @NotNull String title,
            @NotNull String caption,
            final T value,
            @NotNull final IGXSelect<T> notify,
            final String helpUrl) {

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        TextView text = new TextView(context);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        text.setText(caption);
        layout.addView(text);

        final EditText edit = new EditText(context);
        layout.addView(edit);
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit.setText(String.valueOf(value));
        //Set the cursor at the end of the text.
        edit.setSelection(edit.getText().length());
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel());
        addHelp(builder, context, helpUrl);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            edit.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
        });
        dialog.show();
        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(v -> {
            try {
                if (!edit.getText().toString().equals(value)) {
                    if (value instanceof Integer) {
                        notify.run((T) new Integer(Integer.parseInt(edit.getText().toString())), 0);

                    }
                }
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static void showBooleanDlg(
            @NotNull Context context,
            int title,
            int caption,
            final boolean value,
            @NotNull final IGXSelect<Boolean> notify,
            final String help) {
        showBooleanDlg(context,
                context.getString(title),
                context.getString(caption),
                value, notify,
                help);
    }

    public static void showBooleanDlg(
            @NotNull Context context,
            @NotNull String title,
            @NotNull String caption,
            final boolean value,
            @NotNull final IGXSelect<Boolean> notify,
            final String helpUrl) {

        SwitchCompat edit = new SwitchCompat(context);
        edit.setText(caption);
        edit.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        edit.setChecked(value);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(edit)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel());
        addHelp(builder, context, helpUrl);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            edit.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
        });
        dialog.show();
        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(v -> {
            try {
                notify.run(edit.isChecked(), 0);
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}