package gurux.dlms.android.ui.objects;

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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gurux.dlms.GXSimpleEntry;
import gurux.dlms.android.GXGeneral;
import gurux.dlms.android.R;
import gurux.dlms.android.databinding.ObjectRegisterBinding;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.MethodAccessMode;
import gurux.dlms.objects.GXDLMSRegister;
import gurux.dlms.objects.IGXDLMSBase;

public class RegisterObjectFragment extends BaseObjectFragment {

    private ObjectRegisterBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSRegister target = objectViewModel.getObject(GXDLMSRegister.class);
        mMedia = objectViewModel.getMedia();
        binding = ObjectRegisterBinding.inflate(inflater, container, false);
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
        Object value = target.getValue();
        dt = target.getUIDataType(2);
        if (dt == DataType.NONE) {
            dt = target.getDataType(2);
        }
        AccessMode am = target.getAccess(2);
        View editText = GXAttributeView.create(requireContext(), value, dt);
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add register scaler and unit.
        am = target.getAccess(3);

        lbl = new TextView(requireContext());
        lbl.setText(names[2]);
        binding.attributes.addView(lbl);
        value = target.getScaler();
        editText = GXAttributeView.create(requireContext(), value, DataType.FLOAT64);
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        value = target.getUnit();
        editText = GXAttributeView.create(requireContext(), value, DataType.ENUM);
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);

        MethodAccessMode ma = target.getMethodAccess(1);
        //Localized method name is read from the register object.
        binding.reset.setText(((IGXDLMSBase) target).getMethodNames(getContext())[0]);
        mComponents.add(new GXSimpleEntry<>(binding.reset, ma));

        binding.reset.setOnClickListener(v -> {
            try {
                binding.reset.setEnabled(false);
                objectViewModel.getListener().onInvoke(target.reset(objectViewModel.getClient()));
                Toast.makeText(getActivity(), R.string.actionCompleted, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                GXGeneral.showError(getActivity(), e, getString(R.string.error));
            }
        });
        updateAccessRights();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}