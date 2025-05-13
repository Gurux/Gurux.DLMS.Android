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
import gurux.dlms.objects.GXDLMSAssociationShortName;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.ui.databinding.AssociationShortNameFragmentBinding;
import gurux.dlms.ui.internal.GXHelpers;

public class AssociationShortNameObjectFragment extends BaseObjectFragment {

    private AssociationShortNameFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSAssociationShortName target = objectViewModel.getObject(GXDLMSAssociationShortName.class);
        mMedia = objectViewModel.getMedia();
        binding = AssociationShortNameFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        AccessMode am;
        TextView lbl;
        View editText;
        //Add Object list.
        lbl = new TextView(requireContext());
        lbl.setText(names[1]);
        binding.attributes.addView(lbl);
        am = target.getAccess(2);
        GXTable table = new GXTable(binding.attributes.getContext(), new String[]{
                "Base name",
                "Class ID",
                requireContext().getString(R.string.version),
                requireContext().getString(R.string.logicalName),
                "Attribute access",
                "Method access"});
        if (target.getObjectList() != null) {
            for (GXDLMSObject it : target.getObjectList()) {
                table.addRow(binding.attributes.getContext(),
                        new String[]{String.valueOf(it.getShortName()),
                                String.valueOf(it.getObjectType()),
                                String.valueOf(it.getVersion()),
                                it.getLogicalName(),
                                GXHelpers.getAttributeAccess(target.getVersion(), it),
                                GXHelpers.getMethodAccess(target.getVersion(), it),
                        });
            }
        }
        mComponents.add(new GXSimpleEntry<>(table, am));
        binding.attributes.addView(table);
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