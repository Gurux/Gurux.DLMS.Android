package gurux.dlms.ui.internal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.Map;

import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.objects.GXDLMSCaptureObject;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSPushSetup;
import gurux.dlms.ui.GXTable;
import gurux.dlms.ui.R;

public class profileGenericObjectsFragment extends ObjectFragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final GXDLMSPushSetup target = mObjectViewModel.getObject(GXDLMSPushSetup.class);
        AccessMode am = target.getAccess(2);
        GXTable table = new GXTable(binding.attributes.getContext(), new String[]{
                getString(R.string.type),
                getString(R.string.logicalName),
                getString(R.string.attribute_index),
                getString(R.string.data_index),});
        if (target.getPushObjectList() != null) {
            for (Map.Entry<GXDLMSObject, GXDLMSCaptureObject> it : target.getPushObjectList()) {
                table.addRow(binding.attributes.getContext(),
                        new String[]{String.valueOf(it.getKey().getObjectType()),
                                it.getKey().getLogicalName(),
                                String.valueOf(it.getValue().getAttributeIndex()),
                                String.valueOf(it.getValue().getDataIndex()),
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