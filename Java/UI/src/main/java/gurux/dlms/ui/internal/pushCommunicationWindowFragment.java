package gurux.dlms.ui.internal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.Map;

import gurux.dlms.GXDateTime;
import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.objects.GXDLMSPushSetup;
import gurux.dlms.ui.GXTable;
import gurux.dlms.ui.R;

public class pushCommunicationWindowFragment extends ObjectFragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final GXDLMSPushSetup target = mObjectViewModel.getObject(GXDLMSPushSetup.class);
        AccessMode am = target.getAccess(2);
        GXTable table = new GXTable(binding.attributes.getContext(), new String[]{
                getString(R.string.start),
                getString(R.string.end)});
        if (target.getCommunicationWindow() != null) {
            for (Map.Entry<GXDateTime, GXDateTime> it : target.getCommunicationWindow()) {
                table.addRow(binding.attributes.getContext(),
                        new String[]{it.getKey().toFormatString(),
                                it.getKey().toFormatString()});
            }
        }
        mComponents.add(new GXSimpleEntry<>(table, am));
        binding.attributes.addView(table);
        mMedia.addListener(this);
        updateAccessRights();
        return view;
    }
}