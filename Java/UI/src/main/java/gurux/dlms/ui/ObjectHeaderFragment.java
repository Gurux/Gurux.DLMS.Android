package gurux.dlms.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import gurux.dlms.GXDLMSConverter;
import gurux.dlms.ui.databinding.ObjectHeaderBinding;
import gurux.dlms.objects.GXDLMSObject;

public class ObjectHeaderFragment extends Fragment {

    protected ObjectHeaderBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        GXDLMSObject mTarget = objectViewModel.getObject(GXDLMSObject.class);
        binding = ObjectHeaderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.objectType.setText(GXDLMSConverter.toCamelCase(mTarget.getObjectType().toString()));
        binding.description.setText(mTarget.getDescription());
        binding.logicalName.setText(mTarget.getLogicalName());
        if (mTarget.getShortName() == 0) {
            binding.shortNameLbl.setVisibility(View.GONE);
            binding.shortName.setVisibility(View.GONE);
        } else {
            binding.shortName.setText(mTarget.getShortName());
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}