package gurux.dlms.ui.internal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.objects.GXDLMSAssociationLogicalName;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.ui.GXTable;
import gurux.dlms.ui.R;

public class lnObjectsFragment extends ObjectFragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final GXDLMSAssociationLogicalName target = mObjectViewModel.getObject(GXDLMSAssociationLogicalName.class);
        AccessMode am = target.getAccess(2);
        GXTable table = new GXTable(binding.attributes.getContext(), new String[]{
                "Class ID",
                requireContext().getString(R.string.version),
                requireContext().getString(R.string.logicalName),
                "Attribute access",
                "Method access"});
        if (target.getObjectList() != null) {
            for (GXDLMSObject it : target.getObjectList()) {
                table.addRow(binding.attributes.getContext(),
                        new String[]{String.valueOf(it.getObjectType()),
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
}