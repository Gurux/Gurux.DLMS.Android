package gurux.dlms.ui;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gurux.dlms.GXDateTime;
import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.ClockStatus;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.MethodAccessMode;
import gurux.dlms.objects.GXDLMSClock;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.objects.enums.ClockBase;
import gurux.dlms.ui.databinding.ClockFragmentBinding;
import gurux.dlms.ui.internal.GXAttributeView;

public class ClockObjectFragment extends BaseObjectFragment {

    private ClockFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSClock target = objectViewModel.getObject(GXDLMSClock.class);
        mMedia = objectViewModel.getMedia();
        binding = ClockFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        AccessMode am;
        TextView lbl;
        View editText;
        //Add Time.
        lbl = new TextView(requireContext());
        lbl.setText(getLabel(names, target, 2));
        binding.attributes.addView(lbl);
        am = target.getAccess(2);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 2, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getTime(),
                (object, index, value) ->
                {
                    target.setTime((GXDateTime) value);
                }
        );
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Time zone.
        lbl = new TextView(requireContext());
        lbl.setText(getLabel(names, target, 3));
        binding.attributes.addView(lbl);
        am = target.getAccess(3);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 3, DataType.INT16,
                lbl,
                (object, index) -> target.getTimeZone(),
                (object, index, value) ->
                {
                    target.setTimeZone((int) value);
                });

        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Status.
        lbl = new TextView(requireContext());
        lbl.setText(getLabel(names, target, 4));
        binding.attributes.addView(lbl);
        am = target.getAccess(4);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.ENUM,
                lbl,
                (object, index) -> target.getStatus(),
                (object, index, value) ->
                {
                    if (value instanceof java.util.Set) {
                        target.setStatus((java.util.Set<ClockStatus>) value);
                    } else if (value instanceof ClockStatus) {
                        target.getStatus().clear();
                        target.getStatus().add((ClockStatus) value);
                    }
                });

        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Begin.
        lbl = new TextView(requireContext());
        lbl.setText(getLabel(names, target, 5));
        binding.attributes.addView(lbl);
        am = target.getAccess(5);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 5, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getBegin(),
                (object, index, value) ->
                {
                    target.setBegin(((GXDateTime) value));
                });

        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add End.
        lbl = new TextView(requireContext());
        lbl.setText(getLabel(names, target, 6));
        binding.attributes.addView(lbl);
        am = target.getAccess(6);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 6, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getEnd(),
                (object, index, value) ->
                {
                    target.setEnd(((GXDateTime) value));
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Deviation.
        lbl = new TextView(requireContext());
        lbl.setText(getLabel(names, target, 7));
        binding.attributes.addView(lbl);
        am = target.getAccess(7);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 7, DataType.INT8,
                lbl,
                (object, index) -> target.getDeviation(),
                (object, index, value) ->
                {
                    target.setDeviation(((int) value));
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Enabled.
        lbl = new TextView(requireContext());
        lbl.setText(getLabel(names, target, 8));
        binding.attributes.addView(lbl);
        am = target.getAccess(8);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 8, DataType.BOOLEAN,
                lbl,
                (object, index) -> target.getEnabled(),
                (object, index, value) ->
                {
                    target.setEnabled(((boolean) value));
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Clock base.
        lbl = new TextView(requireContext());
        lbl.setText(getLabel(names, target, 9));
        binding.attributes.addView(lbl);
        am = target.getAccess(9);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 8, DataType.ENUM,
                lbl,
                (object, index) -> target.getClockBase(),
                (object, index, value) ->
                {
                    target.setClockBase((ClockBase) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);

        ////////////////////////////////////////
        //Methods
        //Set the currect time
        am = target.getAccess(2);
        mComponents.add(new GXSimpleEntry<>(binding.currentTime, am));
        binding.currentTime.setOnClickListener(v -> {
            try {
                GXDateTime now = new GXDateTime(java.util.Calendar.getInstance().getTime());
                target.setTime(now);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MethodAccessMode ma;
        //Adjust to quarter
        ma = target.getMethodAccess(1);
        binding.quarter.setText(((IGXDLMSBase) target).getMethodNames(getContext())[0]);
        mComponents.add(new GXSimpleEntry<>(binding.quarter, ma));
        binding.quarter.setOnClickListener(v -> {
            try {
                objectViewModel.getListener().onInvoke(target.adjustToQuarter(objectViewModel.getClient()), null);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Adjust to measuring period
        ma = target.getMethodAccess(2);
        binding.measuringPeriod.setText(((IGXDLMSBase) target).getMethodNames(getContext())[1]);
        mComponents.add(new GXSimpleEntry<>(binding.measuringPeriod, ma));
        binding.measuringPeriod.setOnClickListener(v -> {
            try {
                objectViewModel.getListener().onInvoke(target.adjustToMeasuringPeriod(objectViewModel.getClient()), null);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Adjust to minute
        ma = target.getMethodAccess(3);
        binding.adjustToMinute.setText(((IGXDLMSBase) target).getMethodNames(getContext())[2]);
        mComponents.add(new GXSimpleEntry<>(binding.adjustToMinute, ma));
        binding.adjustToMinute.setOnClickListener(v -> {
            try {
                objectViewModel.getListener().onInvoke(target.adjustToMinute(objectViewModel.getClient()), null);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Adjust to preset time
        ma = target.getMethodAccess(4);
        binding.adjustToPresetTime.setText(((IGXDLMSBase) target).getMethodNames(getContext())[3]);
        mComponents.add(new GXSimpleEntry<>(binding.adjustToPresetTime, ma));
        binding.adjustToPresetTime.setOnClickListener(v -> {
            try {
                objectViewModel.getListener().onInvoke(target.adjustToPresetTime(objectViewModel.getClient()), null);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Preset adjusting time
        ma = target.getMethodAccess(5);
        binding.presetAdjustingTime.setText(((IGXDLMSBase) target).getMethodNames(getContext())[4]);
        mComponents.add(new GXSimpleEntry<>(binding.presetAdjustingTime, ma));
        binding.presetAdjustingTime.setOnClickListener(v -> {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 40, 50, 10);

            TextView text = new TextView(getContext());
            text.setMovementMethod(LinkMovementMethod.getInstance());
            text.setText(R.string.preset_time);
            layout.addView(text);

            final EditText presetTime = new EditText(getContext());
            layout.addView(presetTime);

            text = new TextView(getContext());
            text.setMovementMethod(LinkMovementMethod.getInstance());
            text.setText(R.string.interval_start);
            layout.addView(text);

            final EditText intervalStart = new EditText(getContext());
            layout.addView(intervalStart);

            text = new TextView(getContext());
            text.setText(R.string.interval_end);
            layout.addView(text);

            final EditText intervalEnd = new EditText(getContext());
            layout.addView(intervalEnd);

            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.preset_adjusting_time)
                    .setView(layout)
                    .setPositiveButton(android.R.string.ok, (dialog, which) ->
                    {
                        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
                        try {
                            Date time = df.parse(presetTime.getText().toString());
                            Date start = df.parse(presetTime.getText().toString());
                            Date end = df.parse(presetTime.getText().toString());
                            objectViewModel.getListener().onInvoke(target.presetAdjustingTime(
                                    objectViewModel.getClient(), time, start, end
                            ), null);
                        } catch (ParseException | InvalidKeyException |
                                 NoSuchAlgorithmException | NoSuchPaddingException |
                                 InvalidAlgorithmParameterException |
                                 IllegalBlockSizeException | BadPaddingException |
                                 SignatureException e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel())
                    .show();
        });
        //Shift time
        ma = target.getMethodAccess(6);
        binding.shiftTime.setText(((IGXDLMSBase) target).getMethodNames(getContext())[5]);
        mComponents.add(new GXSimpleEntry<>(binding.shiftTime, ma));
        binding.shiftTime.setOnClickListener(v -> {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 40, 50, 10);

            TextView text = new TextView(getContext());
            text.setMovementMethod(LinkMovementMethod.getInstance());
            text.setText(R.string.shift_time);
            layout.addView(text);

            final EditText time = new EditText(getContext());
            layout.addView(time);
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle(R.string.shift_time)
                    .setView(layout)
                    .setPositiveButton(android.R.string.ok, (dialog, which) ->
                    {
                        try {
                            int val = Integer.parseInt(time.getText().toString());
                            objectViewModel.getListener().onInvoke(target.shiftTime(
                                    objectViewModel.getClient(), val), null);
                        } catch (InvalidKeyException |
                                 NoSuchAlgorithmException | NoSuchPaddingException |
                                 InvalidAlgorithmParameterException |
                                 IllegalBlockSizeException | BadPaddingException |
                                 SignatureException e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel())
                    .show();
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