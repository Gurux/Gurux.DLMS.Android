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
import gurux.dlms.objects.GXDLMSPushSetup;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.objects.enums.MessageType;
import gurux.dlms.objects.enums.ServiceType;
import gurux.dlms.ui.GXAccordion;
import gurux.dlms.ui.R;

public class pushGeneralFragment extends ObjectFragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final GXDLMSPushSetup target = mObjectViewModel.getObject(GXDLMSPushSetup.class);
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        //Add Send destination and method.
        GXAccordion a = new GXAccordion(binding.attributes.getContext(), R.string.sendDestinationAndMethod);
        binding.attributes.addView(a);

        TextView lbl = new TextView(requireContext());
        lbl.setText(R.string.service);
        a.addView(lbl);
        AccessMode am = target.getAccess(3);
        View editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 3, DataType.ENUM,
                lbl,
                (object, index) -> target.getService(),
                (object, index, value) ->
                {
                    target.setService((ServiceType) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);

        //Add destination.
        lbl = new TextView(requireContext());
        lbl.setText(R.string.destination);
        a.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 3, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getDestination(),
                (object, index, value) ->
                {
                    target.setDestination((String) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);

        //Add message
        lbl = new TextView(requireContext());
        lbl.setText(R.string.service);
        a.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 3, DataType.ENUM,
                lbl,
                (object, index) -> target.getMessage(),
                (object, index, value) ->
                {
                    target.setMessage((MessageType) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);


        //Add Randomisation start interval.
        a = new GXAccordion(binding.attributes.getContext(), R.string.randomisation);
        binding.attributes.addView(a);
        lbl = new TextView(requireContext());
        lbl.setText(R.string.randomisation);
        a.addView(lbl);
        am = target.getAccess(5);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 5, DataType.UINT16,
                lbl,
                (object, index) -> target.getRandomisationStartInterval(),
                (object, index, value) ->
                {
                    target.setRandomisationStartInterval((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);
        //Add Number of retries.
        lbl = new TextView(requireContext());
        lbl.setText(R.string.numberOfRetries);
        a.addView(lbl);
        am = target.getAccess(6);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 5, DataType.UINT8,
                lbl,
                (object, index) -> target.getNumberOfRetries(),
                (object, index, value) ->
                {
                    target.setNumberOfRetries((short) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Repetition delay.
        lbl = new TextView(requireContext());
        lbl.setText(R.string.repetitionDelay);
        binding.attributes.addView(lbl);
        editText = GXAttributeView.create(requireContext(), mObjectViewModel.getListener(),
                target, 5, DataType.UINT8,
                lbl,
                (object, index) -> target.getRepetitionDelay(),
                (object, index, value) ->
                {
                    target.setRepetitionDelay((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        mMedia.addListener(this);
        updateAccessRights();
        return view;
    }
}