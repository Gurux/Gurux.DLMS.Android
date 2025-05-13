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
import gurux.dlms.objects.GXDLMSHdlcSetup;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.objects.enums.BaudRate;
import gurux.dlms.ui.databinding.HdlcSetupFragmentBinding;
import gurux.dlms.ui.internal.GXAttributeView;

public class HdlcSetupObjectFragment extends BaseObjectFragment {

    private HdlcSetupFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSHdlcSetup target = objectViewModel.getObject(GXDLMSHdlcSetup.class);
        mMedia = objectViewModel.getMedia();
        binding = HdlcSetupFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        AccessMode am;
        TextView lbl;
        View editText;
        //Add Communication speed.
        lbl = new TextView(requireContext());
        lbl.setText(names[1]);
        binding.attributes.addView(lbl);
        am = target.getAccess(2);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 2, DataType.ENUM,
                lbl,
                (object, index) -> target.getCommunicationSpeed(),
                (object, index, value) ->
                {
                    target.setCommunicationSpeed(BaudRate.values()[(int) value]);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Window size transmit.
        lbl = new TextView(requireContext());
        lbl.setText(names[2]);
        binding.attributes.addView(lbl);
        am = target.getAccess(3);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 3, DataType.UINT8,
                lbl,
                (object, index) -> target.getWindowSizeTransmit(),
                (object, index, value) ->
                {
                    target.setWindowSizeTransmit((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Window size receive.
        lbl = new TextView(requireContext());
        lbl.setText(names[3]);
        binding.attributes.addView(lbl);
        am = target.getAccess(4);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT8,
                lbl,
                (object, index) -> target.getWindowSizeReceive(),
                (object, index, value) ->
                {
                    target.setInactivityTimeout((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Maximum info length transmit.
        lbl = new TextView(requireContext());
        lbl.setText(names[4]);
        binding.attributes.addView(lbl);
        am = target.getAccess(5);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 5, DataType.UINT16,
                lbl,
                (object, index) -> target.getMaximumInfoLengthTransmit(),
                (object, index, value) ->
                {
                    target.setMaximumInfoLengthTransmit((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Maximum info length receive.
        lbl = new TextView(requireContext());
        lbl.setText(names[5]);
        binding.attributes.addView(lbl);
        am = target.getAccess(6);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 6, DataType.UINT16,
                lbl,
                (object, index) -> target.getMaximumInfoLengthReceive(),
                (object, index, value) ->
                {
                    target.setMaximumInfoLengthReceive((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Inter charachter timeout.
        lbl = new TextView(requireContext());
        lbl.setText(names[6]);
        binding.attributes.addView(lbl);
        am = target.getAccess(7);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 7, DataType.UINT16,
                lbl,
                (object, index) -> target.getInterCharachterTimeout(),
                (object, index, value) ->
                {
                    target.setInterCharachterTimeout((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Inactivity timeout.
        lbl = new TextView(requireContext());
        lbl.setText(names[7]);
        binding.attributes.addView(lbl);
        am = target.getAccess(8);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 8, DataType.UINT16,
                lbl,
                (object, index) -> target.getInactivityTimeout(),
                (object, index, value) ->
                {
                    target.setInactivityTimeout((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Device address.
        lbl = new TextView(requireContext());
        lbl.setText(names[8]);
        binding.attributes.addView(lbl);
        am = target.getAccess(9);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 9, DataType.UINT16,
                lbl,
                (object, index) -> target.getDeviceAddress(),
                (object, index, value) ->
                {
                    target.setDeviceAddress((int) value);
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