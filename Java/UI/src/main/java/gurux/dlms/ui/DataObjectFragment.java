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
import gurux.dlms.objects.GXDLMSData;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.ui.databinding.DataFragmentBinding;
import gurux.dlms.ui.internal.GXAttributeView;

public class DataObjectFragment extends BaseObjectFragment {
    private DataFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSData target = objectViewModel.getObject(GXDLMSData.class);
        mMedia = objectViewModel.getMedia();
        binding = DataFragmentBinding.inflate(inflater, container, false);
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