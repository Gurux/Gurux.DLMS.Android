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
import gurux.dlms.enums.Unit;
import gurux.dlms.objects.GXDLMSRegister;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.ui.databinding.RegisterFragmentBinding;
import gurux.dlms.ui.internal.GXAttributeView;

public class RegisterObjectFragment extends BaseObjectFragment {

    private RegisterFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSRegister target = objectViewModel.getObject(GXDLMSRegister.class);
        mMedia = objectViewModel.getMedia();
        binding = RegisterFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        DataType dt;
        //Add value.
        TextView lbl = new TextView(requireContext());
        lbl.setText(names[1]);
        binding.attributes.addView(lbl);
        dt = target.getUIDataType(2);
        if (dt == DataType.NONE) {
            dt = target.getDataType(2);
        }
        AccessMode am = target.getAccess(2);
        View editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 2, dt,
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