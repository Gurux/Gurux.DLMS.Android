package gurux.dlms.ui.internal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.DataType;
import gurux.dlms.objects.GXDLMSAssociationLogicalName;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.objects.enums.AssociationStatus;

public class lnGeneralFragment extends ObjectFragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final GXDLMSAssociationLogicalName target = mObjectViewModel.getObject(GXDLMSAssociationLogicalName.class);
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        //Add Associated partners id.
        TextView lbl = new TextView(binding.attributes.getContext());
        lbl.setText("Client SAP");
        binding.attributes.addView(lbl);
        AccessMode am = target.getAccess(3);
        View editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getClientSAP(),
                (object, index, value) -> target.setClientSAP((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);

        lbl = new TextView(binding.attributes.getContext());
        lbl.setText("Server SAP");
        binding.attributes.addView(lbl);
        am = target.getAccess(3);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getServerSAP(),
                (object, index, value) -> target.setServerSAP((short) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);

        //Add Secret.
        lbl = new TextView(requireContext());
        lbl.setText(names[6]);
        binding.attributes.addView(lbl);
        am = target.getAccess(7);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 5, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getSecret(),
                (object, index, value) -> target.setSecret((byte[]) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Association status.
        lbl = new TextView(requireContext());
        lbl.setText(names[7]);
        binding.attributes.addView(lbl);
        am = target.getAccess(8);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 6, DataType.ENUM,
                lbl,
                (object, index) -> target.getAssociationStatus(),
                (object, index, value) -> target.setAssociationStatus((AssociationStatus) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        mMedia.addListener(this);
        updateAccessRights();
        return view;
    }
}