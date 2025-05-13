package gurux.dlms.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.MethodAccessMode;
import gurux.dlms.objects.GXDLMSDisconnectControl;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.objects.enums.ControlMode;
import gurux.dlms.objects.enums.ControlState;
import gurux.dlms.ui.databinding.DisconnectControlFragmentBinding;
import gurux.dlms.ui.internal.GXAttributeView;

public class DisconnectControlObjectFragment extends BaseObjectFragment {

    private DisconnectControlFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSDisconnectControl target = objectViewModel.getObject(GXDLMSDisconnectControl.class);
        mMedia = objectViewModel.getMedia();
        binding = DisconnectControlFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        AccessMode am;
        TextView lbl;
        View editText;
        //Add Output state.
        lbl = new TextView(requireContext());
        lbl.setText(names[1]);
        binding.attributes.addView(lbl);
        am = target.getAccess(2);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 0, DataType.BOOLEAN,
                lbl,
                (object, index) -> target.getOutputState(),
                (object, index, value) ->
                {
                    target.setOutputState((Boolean) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Control state.
        lbl = new TextView(requireContext());
        lbl.setText(names[2]);
        binding.attributes.addView(lbl);
        am = target.getAccess(3);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 1, DataType.ENUM,
                lbl,
                (object, index) -> target.getControlState(),
                (object, index, value) ->
                {
                    target.setControlState((ControlState) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Control mode.
        lbl = new TextView(requireContext());
        lbl.setText(names[3]);
        binding.attributes.addView(lbl);
        am = target.getAccess(4);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 2, DataType.ENUM,
                lbl,
                (object, index) -> target.getControlMode(),
                (object, index, value) ->
                {
                    target.setControlMode((ControlMode) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        ////////////////////////////////////////
        //Methods
        MethodAccessMode ma;
        //Remote disconnect
        ma = target.getMethodAccess(1);
        binding.disconnect.setText(((IGXDLMSBase) target).getMethodNames(getContext())[0]);
        mComponents.add(new GXSimpleEntry<>(binding.disconnect, ma));
        binding.disconnect.setOnClickListener(v -> {
            try {
                objectViewModel.getListener().onInvoke(target.remoteDisconnect(objectViewModel.getClient()), null);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Remote reconnect
        ma = target.getMethodAccess(2);
        binding.reconnect.setText(((IGXDLMSBase) target).getMethodNames(getContext())[1]);
        mComponents.add(new GXSimpleEntry<>(binding.reconnect, ma));
        binding.reconnect.setOnClickListener(v -> {
            try {
                objectViewModel.getListener().onInvoke(target.remoteReconnect(objectViewModel.getClient()), null);
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