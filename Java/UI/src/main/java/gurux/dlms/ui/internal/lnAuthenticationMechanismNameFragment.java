package gurux.dlms.ui.internal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.DataType;
import gurux.dlms.objects.GXDLMSAssociationLogicalName;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.ui.R;

public class lnAuthenticationMechanismNameFragment extends ObjectFragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final GXDLMSAssociationLogicalName target = mObjectViewModel.getObject(GXDLMSAssociationLogicalName.class);
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        //Joint ISO CTT.
        TextView lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.jointIsoCtt);
        binding.attributes.addView(lbl);
        AccessMode am = target.getAccess(7);
        View editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getAuthenticationMechanismName().getJointIsoCtt(),
                (object, index, value) -> target.getAuthenticationMechanismName().setJointIsoCtt((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);

        //Country.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.country);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getAuthenticationMechanismName().getCountry(),
                (object, index, value) -> target.getAuthenticationMechanismName().setCountry((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Country name.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.countryName);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getAuthenticationMechanismName().getCountryName(),
                (object, index, value) -> target.getAuthenticationMechanismName().setCountryName((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Identified Organization.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.identifiedOrganization);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getAuthenticationMechanismName().getIdentifiedOrganization(),
                (object, index, value) -> target.getAuthenticationMechanismName().setIdentifiedOrganization((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //DLMS Ua.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.dlmsUa);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getAuthenticationMechanismName().getDlmsUA(),
                (object, index, value) -> target.getAuthenticationMechanismName().setDlmsUA((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Authentication name.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.authenticationName);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getAuthenticationMechanismName().getAuthenticationMechanismName(),
                (object, index, value) -> target.getAuthenticationMechanismName().setAuthenticationMechanismName((int) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);

        //Mechanism Id.
        lbl = new TextView(binding.attributes.getContext());
        lbl.setText(R.string.mechanismId);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 1, DataType.INT32,
                lbl,
                (object, index) -> target.getAuthenticationMechanismName().getMechanismId(),
                (object, index, value) -> target.getAuthenticationMechanismName().setMechanismId((Authentication) value));
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        mMedia.addListener(this);
        updateAccessRights();
        return view;
    }
}