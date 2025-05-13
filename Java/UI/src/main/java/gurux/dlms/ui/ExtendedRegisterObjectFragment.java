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

import gurux.dlms.GXDateTime;
import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.MethodAccessMode;
import gurux.dlms.enums.Unit;
import gurux.dlms.objects.GXDLMSExtendedRegister;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.ui.databinding.ExtendedRegisterFragmentBinding;
import gurux.dlms.ui.internal.GXAttributeView;

public class ExtendedRegisterObjectFragment extends BaseObjectFragment {

    private ExtendedRegisterFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSExtendedRegister target = objectViewModel.getObject(GXDLMSExtendedRegister.class);
        mMedia = objectViewModel.getMedia();
        binding = ExtendedRegisterFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        DataType dt;
        AccessMode am;
        TextView lbl;
        View editText;
        //Add Value.
        lbl = new TextView(requireContext());
        lbl.setText(names[1]);
        binding.attributes.addView(lbl);
        am = target.getAccess(2);
        dt = target.getUIDataType(1);
        if (dt == DataType.NONE) {
            dt = target.getDataType(1);
        }
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 0, dt,
                lbl,
                (object, index) -> target.getValue(),
                (object, index, value) ->
                {
                    target.setValue(value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add register scaler and unit.
        am = target.getAccess(3);
        lbl = new TextView(requireContext());
        lbl.setText(names[2]);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 3, DataType.FLOAT64,
                lbl,
                (object, index) -> target.getScaler(),
                (object, index, value) ->
                {
                    target.setScaler((Double) value);
                });

        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 3, DataType.ENUM,
                lbl,
                (object, index) -> target.getUnit(),
                (object, index, value) ->
                {
                    Unit unit = (Unit) value;
                    target.setUnit(unit);
                });

        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Status.
        lbl = new TextView(requireContext());
        lbl.setText(names[3]);
        binding.attributes.addView(lbl);
        am = target.getAccess(4);
        dt = target.getUIDataType(3);
        if (dt == DataType.NONE) {
            dt = target.getDataType(3);
        }
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 2, dt,
                lbl,
                (object, index) -> target.getStatus(),
                (object, index, value) ->
                {
                    target.setStatus(value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Capture time.
        lbl = new TextView(requireContext());
        lbl.setText(names[4]);
        binding.attributes.addView(lbl);
        am = target.getAccess(5);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 3, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getCaptureTime(),
                (object, index, value) ->
                {
                    target.setCaptureTime((GXDateTime) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        ////////////////////////////////////////
        //Methods
        //Reset
        MethodAccessMode ma = target.getMethodAccess(1);
        //Localized method name is read from the register object.
        binding.reset.setText(((IGXDLMSBase) target).getMethodNames(getContext())[0]);
        mComponents.add(new GXSimpleEntry<>(binding.reset, ma));
        binding.reset.setOnClickListener(v -> {
            try {
                objectViewModel.getListener().onInvoke(target.reset(objectViewModel.getClient()), null);
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