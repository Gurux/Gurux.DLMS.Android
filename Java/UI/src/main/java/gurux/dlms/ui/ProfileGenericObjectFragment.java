package gurux.dlms.ui;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import gurux.dlms.GXDLMSConverter;
import gurux.dlms.GXDateTime;
import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.MethodAccessMode;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.objects.GXDLMSCaptureObject;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSProfileGeneric;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.objects.enums.SortMethod;
import gurux.dlms.ui.databinding.ProfileGenericFragmentBinding;
import gurux.dlms.ui.internal.GXAttributeView;
import gurux.dlms.ui.internal.GXHelpers;

public class ProfileGenericObjectFragment extends BaseObjectFragment {

    private ProfileGenericFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSProfileGeneric target = objectViewModel.getObject(GXDLMSProfileGeneric.class);
        mMedia = objectViewModel.getMedia();
        binding = ProfileGenericFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        AccessMode am;
        TextView lbl;
        View editText;

        //Add Capture period.
        lbl = new TextView(requireContext());
        lbl.setText(names[3]);
        binding.attributes.addView(lbl);
        am = target.getAccess(4);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 2, DataType.UINT32,
                lbl,
                (object, index) -> target.getCapturePeriod(),
                (object, index, value) -> target.setCapturePeriod((long) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Sort method.
        lbl = new TextView(requireContext());
        lbl.setText(names[4]);
        binding.attributes.addView(lbl);
        am = target.getAccess(5);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 3, DataType.ENUM,
                lbl,
                (object, index) -> target.getSortMethod(),
                (object, index, value) ->
                {
                    target.setSortMethod((SortMethod) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Sort object.
        lbl = new TextView(requireContext());
        lbl.setText(names[5]);
        binding.attributes.addView(lbl);
        am = target.getAccess(6);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.STRING,
                lbl,
                (object, index) -> String.valueOf(target.getSortObject()),
                (object, index, value) ->
                {
                    //TODO: target.setSortObject(value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Entries in use.
        lbl = new TextView(requireContext());
        lbl.setText(names[6]);
        binding.attributes.addView(lbl);
        am = target.getAccess(7);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 5, DataType.UINT32,
                lbl,
                (object, index) -> target.getEntriesInUse(),
                (object, index, value) ->
                {
                    target.setEntriesInUse((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Profile entries.
        lbl = new TextView(requireContext());
        lbl.setText(names[7]);
        binding.attributes.addView(lbl);
        am = target.getAccess(8);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 6, DataType.UINT32,
                lbl,
                (object, index) -> target.getProfileEntries(),
                (object, index, value) ->
                {
                    target.setProfileEntries((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);

        final Button read = new Button(requireContext());
        read.setText(R.string.read);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.gravity = Gravity.END;
        read.setLayoutParams(params);
        binding.attributes.addView(read);
        mComponents.add(new GXSimpleEntry<>(read, target.getAccess(2)));

        RadioGroup radioGroup = new RadioGroup(requireContext());
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);

        RadioButton readEntry = new RadioButton(requireContext());
        readEntry.setText(R.string.readEntry);
        readEntry.setId(View.generateViewId());
        row.addView(readEntry);

        EditText index = new EditText(requireContext());
        index.setInputType(InputType.TYPE_CLASS_NUMBER);
        index.setText("1");
        row.addView(index);
        lbl = new TextView(requireContext());
        lbl.setText(R.string.count);
        row.addView(lbl);
        EditText count = new EditText(requireContext());
        count.setInputType(InputType.TYPE_CLASS_NUMBER);
        count.setText("1");
        row.addView(count);
        radioGroup.addView(row);


        row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        RadioButton readLast = new RadioButton(requireContext());
        readLast.setText(R.string.readLast);
        readLast.setId(View.generateViewId());
        row.addView(readLast);

        EditText last = new EditText(requireContext());
        last.setInputType(InputType.TYPE_CLASS_NUMBER);
        last.setText("1");
        row.addView(last);
        lbl = new TextView(requireContext());
        lbl.setText(R.string.days);
        row.addView(lbl);
        radioGroup.addView(row);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        String start = dateTimeFormat.format(cal.getTime());
        cal = Calendar.getInstance();
        String end = dateTimeFormat.format(cal.getTime());

        row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        RadioButton readFrom = new RadioButton(requireContext());
        readFrom.setChecked(true);
        readFrom.setText(R.string.readFrom);
        readFrom.setId(View.generateViewId());
        row.addView(readFrom);
        EditText from = new EditText(requireContext());
        from.setText(start);
        row.addView(from);

        lbl = new TextView(requireContext());
        lbl.setText(R.string.to);
        row.addView(lbl);
        EditText to = new EditText(requireContext());
        to.setText(end);
        row.addView(to);

        radioGroup.addView(row);
        RadioButton readAll = new RadioButton(requireContext());
        readAll.setText(R.string.all);
        readAll.setId(View.generateViewId());
        radioGroup.addView(readAll);
        binding.attributes.addView(radioGroup);
        GXHelpers.handleRadioButtons(new RadioButton[]{readEntry, readLast, readFrom, readAll});
        read.setOnClickListener(v -> {
            try {
                if (readEntry.isChecked()) {
                    int i = Integer.parseInt(index.getText().toString());
                    int c = Integer.parseInt(count.getText().toString());
                    byte[][] data = objectViewModel.getClient().readRowsByEntry(target, i, c);
                    objectViewModel.getListener().onRaw(data, value ->
                    {
                        objectViewModel.getClient().updateValue(target, 2, value);
                    });
                } else if (readLast.isChecked()) {
                    int l = Integer.parseInt(last.getText().toString());
                    Calendar cal2 = Calendar.getInstance();
                    cal2.set(Calendar.HOUR, 0);
                    cal2.set(Calendar.MINUTE, 0);
                    cal2.set(Calendar.SECOND, 0);
                    cal2.add(Calendar.DAY_OF_MONTH, 1);
                    GXDateTime f = new GXDateTime(cal2);
                    GXDateTime t = new GXDateTime(Calendar.getInstance());
                    byte[][] data = objectViewModel.getClient().readRowsByRange(target, f, t);
                    objectViewModel.getListener().onRaw(data, value ->
                    {
                        objectViewModel.getClient().updateValue(target, 2, value);
                    });
                } else if (readFrom.isChecked()) {
                    GXDateTime f = new GXDateTime(dateTimeFormat.parse(from.getText().toString()));
                    GXDateTime t = new GXDateTime(dateTimeFormat.parse(to.getText().toString()));
                    byte[][] data = objectViewModel.getClient().readRowsByRange(target, f, t);
                    objectViewModel.getListener().onRaw(data, value ->
                    {
                        objectViewModel.getClient().updateValue(target, 2, value);
                    });
                } else {
                    //Read all.
                    objectViewModel.getListener().onRead(target, 2);
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (target.getCaptureObjects() != null &&
                !target.getCaptureObjects().isEmpty()) {
            ArrayList<String> columns = new ArrayList<>();
            for (Map.Entry<GXDLMSObject, GXDLMSCaptureObject> it : target.getCaptureObjects()) {
                String name = it.getKey().getLogicalName();
                if (it.getValue().getAttributeIndex() > 0) {
                    String[] list = ((IGXDLMSBase) it.getKey()).getNames(requireContext());
                    if (list.length >= it.getValue().getAttributeIndex()) {
                        String desc = it.getKey().getDescription();
                        if (GXCommon.isNullOrEmpty(desc)) {
                            // Get description of the objects.
                            GXDLMSConverter converter = new GXDLMSConverter();
                            converter.updateOBISCodeInformation(getActivity(), it.getKey());
                            desc = converter.getDescription(requireContext(),
                                    it.getKey().getLogicalName(), it.getKey().getObjectType())[0];
                            it.getKey().setDescription(desc);
                        }
                        name += System.lineSeparator() + desc;
                    }
                }
                columns.add(name);
            }
            GXTable table = new GXTable(binding.attributes.getContext(), columns);
            for (Object it : target.getBuffer()) {
                table.addRow(binding.attributes.getContext(), (Object[]) it);
            }
            mComponents.add(new GXSimpleEntry<>(table, am));
            binding.attributes.addView(table);
        }

        ////////////////////////////////////////
        //Methods
        MethodAccessMode ma;
        //Reset
        ma = target.getMethodAccess(1);
        binding.reset.setText(((IGXDLMSBase) target).getMethodNames(getContext())[0]);
        mComponents.add(new GXSimpleEntry<>(binding.reset, ma));
        binding.reset.setOnClickListener(v -> {
            try {
                binding.reset.setEnabled(false);
                objectViewModel.getListener().onInvoke(target.reset(objectViewModel.getClient()), null);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Capture
        ma = target.getMethodAccess(2);
        binding.capture.setText(((IGXDLMSBase) target).getMethodNames(getContext())[1]);
        mComponents.add(new GXSimpleEntry<>(binding.capture, ma));
        binding.capture.setOnClickListener(v -> {
            try {
                binding.capture.setEnabled(false);
                objectViewModel.getListener().onInvoke(target.capture(objectViewModel.getClient()), null);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mMedia.addListener(this);
        updateAccessRights();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}