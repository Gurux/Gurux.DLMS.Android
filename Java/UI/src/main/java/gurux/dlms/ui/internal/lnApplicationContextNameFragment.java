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
import gurux.dlms.objects.enums.ApplicationContextName;
import gurux.dlms.ui.R;

public class lnApplicationContextNameFragment extends ObjectFragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final GXDLMSAssociationLogicalName target = mObjectViewModel.getObject(GXDLMSAssociationLogicalName.class);
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        //Joint ISO CTT.
        TextView lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.jointIsoCtt);
        binding.attributes.addView(lbl);
        AccessMode am = target.getAccess(6);
        View editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getApplicationContextName().getJointIsoCtt(),
                (object, index, value) -> target.getApplicationContextName().setJointIsoCtt((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);

        //Country.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.country);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getApplicationContextName().getCountry(),
                (object, index, value) -> target.getApplicationContextName().setCountry((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Country name.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.countryName);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getApplicationContextName().getCountryName(),
                (object, index, value) -> target.getApplicationContextName().setCountryName((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Identified Organization.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.identifiedOrganization);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getApplicationContextName().getIdentifiedOrganization(),
                (object, index, value) -> target.getApplicationContextName().setIdentifiedOrganization((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //DLMS Ua.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.dlmsUa);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getApplicationContextName().getDlmsUA(),
                (object, index, value) -> target.getApplicationContextName().setDlmsUA((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Application context.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.applicationContext);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getApplicationContextName().getApplicationContext(),
                (object, index, value) -> target.getApplicationContextName().setApplicationContext((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);

        //Context Id.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.contextId);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getApplicationContextName().getContextId(),
                (object, index, value) -> target.getApplicationContextName().setContextId((ApplicationContextName) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        mMedia.addListener(this);
        updateAccessRights();
        return view;
    }
}