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
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSTcpUdpSetup;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.ui.databinding.TcpUdpSetupFragmentBinding;
import gurux.dlms.ui.internal.GXAttributeView;

public class TcpUdpSetupObjectFragment extends BaseObjectFragment {

    private TcpUdpSetupFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSTcpUdpSetup target = objectViewModel.getObject(GXDLMSTcpUdpSetup.class);
        mMedia = objectViewModel.getMedia();
        binding = TcpUdpSetupFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        AccessMode am;
        TextView lbl;
        View editText;
        //Add Port.
        lbl = new TextView(requireContext());
        lbl.setText(names[1]);
        binding.attributes.addView(lbl);
        am = target.getAccess(2);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 2, DataType.UINT16,
                lbl,
                (object, index) -> target.getPort(),
                (object, index, value) ->
                {
                    target.setPort((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add IP reference.
        lbl = new TextView(requireContext());
        lbl.setText(names[2]);
        binding.attributes.addView(lbl);
        am = target.getAccess(3);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 3, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getIPReference(),
                (object, index, value) ->
                {
                    target.setIPReference(((GXDLMSObject) value).getLogicalName());
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Maximum segment size.
        lbl = new TextView(requireContext());
        lbl.setText(names[3]);
        binding.attributes.addView(lbl);
        am = target.getAccess(4);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT16,
                lbl,
                (object, index) -> target.getMaximumSegmentSize(),
                (object, index, value) ->
                {
                    target.setMaximumSegmentSize((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Maximum simultaneous connections.
        lbl = new TextView(requireContext());
        lbl.setText(names[4]);
        binding.attributes.addView(lbl);
        am = target.getAccess(5);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 5, DataType.UINT8,
                lbl,
                (object, index) -> target.getMaximumSimultaneousConnections(),
                (object, index, value) ->
                {
                    target.setMaximumSimultaneousConnections((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Inactivity timeout.
        lbl = new TextView(requireContext());
        lbl.setText(names[5]);
        binding.attributes.addView(lbl);
        am = target.getAccess(6);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 6, DataType.UINT16,
                lbl,
                (object, index) -> target.getInactivityTimeout(),
                (object, index, value) ->
                {
                    target.setInactivityTimeout((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        updateAccessRights();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}