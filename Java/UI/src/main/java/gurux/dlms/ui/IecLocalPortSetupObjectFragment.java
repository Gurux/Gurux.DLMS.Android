package gurux.dlms.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.DataType;
import gurux.dlms.objects.GXDLMSIECLocalPortSetup;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.objects.enums.BaudRate;
import gurux.dlms.objects.enums.LocalPortResponseTime;
import gurux.dlms.objects.enums.OpticalProtocolMode;
import gurux.dlms.ui.databinding.IecLocalPortSetupFragmentBinding;
import gurux.dlms.ui.internal.GXAttributeView;

public class IecLocalPortSetupObjectFragment extends BaseObjectFragment {

    private IecLocalPortSetupFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSIECLocalPortSetup target = objectViewModel.getObject(GXDLMSIECLocalPortSetup.class);
        mMedia = objectViewModel.getMedia();
        binding = IecLocalPortSetupFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        AccessMode am;
        TextView lbl;
        View editText;
        //Add Default mode.
        lbl = new TextView(requireContext());
        lbl.setText(names[1]);
        binding.attributes.addView(lbl);
        am = target.getAccess(2);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 2, DataType.ENUM,
                lbl,
                (object, index) -> target.getDefaultMode(),
                (object, index, value) ->
                {
                    target.setDefaultMode(OpticalProtocolMode.forValue((int) value));
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Default baud rate.
        lbl = new TextView(requireContext());
        lbl.setText(names[2]);
        binding.attributes.addView(lbl);
        am = target.getAccess(3);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 3, DataType.ENUM,
                lbl,
                (object, index) -> target.getDefaultBaudrate(),
                (object, index, value) ->
                {
                    target.setDefaultBaudrate(BaudRate.values()[(int) value]);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Proposed baud rate.
        lbl = new TextView(requireContext());
        lbl.setText(names[3]);
        binding.attributes.addView(lbl);
        am = target.getAccess(4);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.ENUM,
                lbl,
                (object, index) -> target.getProposedBaudrate(),
                (object, index, value) ->
                {
                    target.setProposedBaudrate(BaudRate.values()[(int) value]);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Response time.
        lbl = new TextView(requireContext());
        lbl.setText(names[4]);
        binding.attributes.addView(lbl);
        am = target.getAccess(5);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 5, DataType.ENUM,
                lbl,
                (object, index) -> target.getResponseTime(),
                (object, index, value) ->
                {
                    target.setResponseTime(LocalPortResponseTime.forValue((int) value));
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Device address.
        lbl = new TextView(requireContext());
        lbl.setText(names[5]);
        binding.attributes.addView(lbl);
        am = target.getAccess(6);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 6, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getDeviceAddress(),
                (object, index, value) ->
                {
                    target.setDeviceAddress((String) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Password 1.
        lbl = new TextView(requireContext());
        lbl.setText(names[6]);
        binding.attributes.addView(lbl);
        am = target.getAccess(7);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 7, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getPassword1(),
                (object, index, value) ->
                {
                    target.setPassword1((String) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Password 2.
        lbl = new TextView(requireContext());
        lbl.setText(names[7]);
        binding.attributes.addView(lbl);
        am = target.getAccess(8);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 8, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getPassword2(),
                (object, index, value) ->
                {
                    target.setPassword2((String) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Password 5.
        lbl = new TextView(requireContext());
        lbl.setText(names[8]);
        binding.attributes.addView(lbl);
        am = target.getAccess(9);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 9, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getPassword5(),
                (object, index, value) ->
                {
                    target.setPassword5((String) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
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