package gurux.dlms.ui.internal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.Map;

import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.objects.GXDLMSAssociationLogicalName;
import gurux.dlms.ui.GXTable;
import gurux.dlms.ui.R;

public class lnUserListFragment extends ObjectFragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final GXDLMSAssociationLogicalName target = mObjectViewModel.getObject(GXDLMSAssociationLogicalName.class);
        AccessMode am = target.getAccess(10);
        GXTable table = new GXTable(binding.attributes.getContext(), new String[]{
                getString(R.string.id),
                getString(R.string.name)});
        if (target.getObjectList() != null) {
            for (Map.Entry<Byte, String> it : target.getUserList()) {
                table.addRow(binding.attributes.getContext(),
                        new String[]{String.valueOf(it.getKey()),
                                String.valueOf(it.getValue())
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