package gurux.dlms.ui.internal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.Conformance;
import gurux.dlms.enums.DataType;
import gurux.dlms.objects.GXDLMSAssociationLogicalName;
import gurux.dlms.objects.IGXDLMSBase;

public class lnxDLMSConextInfoFragment extends ObjectFragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final GXDLMSAssociationLogicalName target = mObjectViewModel.getObject(GXDLMSAssociationLogicalName.class);
        String[] names = ((IGXDLMSBase) target).getNames(getContext());

        //Add Conformance.
        AccessMode am = target.getAccess(5);
        GXAccordion a = new GXAccordion(binding.attributes.getContext(), "Conformance");
        GXCheckList conformance = new GXCheckList(a.getContext());
        mComponents.add(new GXSimpleEntry<>(conformance, am));
        a.addView(conformance);
        List<Object> values = GXHelpers.getEnumValues(Conformance.class);
        values.remove(Conformance.NONE);
        for (Object it : values) {
            conformance.addItem(((Conformance) it).name(), target.getXDLMSContextInfo().getConformance().contains(it));
        }

        mComponents.add(new GXSimpleEntry<>(a, am));
        binding.attributes.addView(a);


        TextView lbl = new TextView(requireContext());
        lbl.setText("Max receive PDU size");
        binding.attributes.addView(lbl);
        View editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 3, DataType.UINT32,
                lbl,
                (object, index) -> target.getXDLMSContextInfo().getMaxReceivePduSize(),
                (object, index, value) ->
                {
                    target.getXDLMSContextInfo().setMaxReceivePduSize((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);

        lbl = new TextView(requireContext());
        lbl.setText("Max send PDU size");
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 3, DataType.UINT32,
                lbl,
                (object, index) -> target.getXDLMSContextInfo().getMaxReceivePduSize(),
                (object, index, value) ->
                {
                    target.getXDLMSContextInfo().setMaxReceivePduSize((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);

        lbl = new TextView(requireContext());
        lbl.setText("DLMS version number");
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 3, DataType.UINT32,
                lbl,
                (object, index) -> target.getXDLMSContextInfo().getDlmsVersionNumber(),
                (object, index, value) ->
                {
                    target.getXDLMSContextInfo().setDlmsVersionNumber((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        lbl = new TextView(requireContext());

        lbl.setText("Dedicated key");
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 3, DataType.UINT32,
                lbl,
                (object, index) -> target.getXDLMSContextInfo().getCypheringInfo(),
                (object, index, value) ->
                {
                    target.getXDLMSContextInfo().setCypheringInfo((byte[]) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        updateAccessRights();
        return view;
    }
}