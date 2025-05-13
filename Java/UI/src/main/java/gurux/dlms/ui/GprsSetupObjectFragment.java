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
import gurux.dlms.enums.DataType;
import gurux.dlms.objects.GXDLMSGprsSetup;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.ui.databinding.GprsSetupFragmentBinding;
import gurux.dlms.ui.internal.GXAttributeView;

public class GprsSetupObjectFragment extends BaseObjectFragment {

    private GprsSetupFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSGprsSetup target = objectViewModel.getObject(GXDLMSGprsSetup.class);
        mMedia = objectViewModel.getMedia();
        binding = GprsSetupFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        AccessMode am;
        TextView lbl;
        View editText;
        //Add APN.
        lbl = new TextView(requireContext());
        lbl.setText(names[1]);
        binding.attributes.addView(lbl);
        am = target.getAccess(2);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 2, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getAPN(),
                (object, index, value) ->
                {
                    target.setAPN((String) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add PIN code.
        lbl = new TextView(requireContext());
        lbl.setText(names[2]);
        binding.attributes.addView(lbl);
        am = target.getAccess(3);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 3, DataType.UINT16,
                lbl,
                (object, index) -> target.getPINCode(),
                (object, index, value) ->
                {
                    target.setPINCode((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Default quality of service and requested quality of service.

        //getRequestedQualityOfService
        GXAccordion a = new GXAccordion(binding.attributes.getContext(), "Default quality of service");
        binding.attributes.addView(a);
        // Precedence
        lbl = new TextView(requireContext());
        lbl.setText(R.string.precedence);
        a.addView(lbl);
        am = target.getAccess(4);

        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT8,
                lbl,
                (object, index) -> target.getDefaultQualityOfService().getPrecedence(),
                (object, index, value) ->
                {
                    target.getDefaultQualityOfService().setPrecedence((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);
        //Delay
        lbl = new TextView(requireContext());
        lbl.setText(R.string.delay);
        a.addView(lbl);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT8,
                lbl,
                (object, index) -> target.getDefaultQualityOfService().getDelay(),
                (object, index, value) ->
                {
                    target.getDefaultQualityOfService().setDelay((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);

        //Reliability
        lbl = new TextView(requireContext());
        lbl.setText(R.string.reliability);
        a.addView(lbl);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT8,
                lbl,
                (object, index) -> target.getDefaultQualityOfService().getReliability(),
                (object, index, value) ->
                {
                    target.getDefaultQualityOfService().setReliability((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);

        //Peak Throughput
        lbl = new TextView(requireContext());
        lbl.setText(R.string.peakThroughput);
        a.addView(lbl);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT8,
                lbl,
                (object, index) -> target.getDefaultQualityOfService().getPeakThroughput(),
                (object, index, value) ->
                {
                    target.getDefaultQualityOfService().setPeakThroughput((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);

        //Mean throughput
        lbl = new TextView(requireContext());
        lbl.setText(R.string.meanThroughput);
        a.addView(lbl);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT8,
                lbl,
                (object, index) -> target.getDefaultQualityOfService().getMeanThroughput(),
                (object, index, value) ->
                {
                    target.getDefaultQualityOfService().setMeanThroughput((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);

        //getRequestedQualityOfService
        a = new GXAccordion(requireContext(), "Requested quality of service");
        binding.attributes.addView(a);

        // Precedence
        lbl = new TextView(requireContext());
        lbl.setText(R.string.precedence);
        a.addView(lbl);
        am = target.getAccess(4);

        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT8,
                lbl,
                (object, index) -> target.getRequestedQualityOfService().getPrecedence(),
                (object, index, value) ->
                {
                    target.getRequestedQualityOfService().setPrecedence((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);

        //Delay
        lbl = new TextView(requireContext());
        lbl.setText(R.string.delay);
        a.addView(lbl);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT8,
                lbl,
                (object, index) -> target.getRequestedQualityOfService().getDelay(),
                (object, index, value) ->
                {
                    target.getRequestedQualityOfService().setDelay((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);

        //Reliability
        lbl = new TextView(requireContext());
        lbl.setText(R.string.reliability);
        a.addView(lbl);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT8,
                lbl,
                (object, index) -> target.getRequestedQualityOfService().getReliability(),
                (object, index, value) ->
                {
                    target.getRequestedQualityOfService().setReliability((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);

        //Peak Throughput
        lbl = new TextView(requireContext());
        lbl.setText(R.string.peakThroughput);
        a.addView(lbl);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT8,
                lbl,
                (object, index) -> target.getRequestedQualityOfService().getPeakThroughput(),
                (object, index, value) ->
                {
                    target.getRequestedQualityOfService().setPeakThroughput((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);


        //Mean throughput
        lbl = new TextView(requireContext());
        lbl.setText(R.string.meanThroughput);
        a.addView(lbl);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 4, DataType.UINT8,
                lbl,
                (object, index) -> target.getRequestedQualityOfService().getMeanThroughput(),
                (object, index, value) ->
                {
                    target.getRequestedQualityOfService().setMeanThroughput((int) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        a.addView(editText);
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