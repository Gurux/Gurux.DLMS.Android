package gurux.dlms.android.ui.meterSettings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import gurux.dlms.GXDLMSClient;
import gurux.dlms.android.GXDevice;
import gurux.dlms.android.IGXSettingsChangedListener;
import gurux.dlms.android.databinding.FragmentMeterSupportedServicesBinding;
import gurux.dlms.enums.Conformance;
import gurux.dlms.ui.GXCheckList;

public class MeterSupportedServicesSettingsFragment extends Fragment {

    private GXDevice mDevice;
    private FragmentMeterSupportedServicesBinding binding;
    private IGXSettingsChangedListener mListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        var meterSettingsViewModel =
                new ViewModelProvider(requireActivity()).get(MeterSettingsViewModel.class);
        mDevice = meterSettingsViewModel.getDevice();
        binding = FragmentMeterSupportedServicesBinding.inflate(inflater, container, false);
        final String address = "https://www.gurux.fi/Gurux.DLMS.Conformance";
        binding.help.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
            startActivity(browserIntent);
        });

        View view = binding.getRoot();

        List<Conformance> value = new ArrayList<>();
        value.addAll(Conformance.forValue(mDevice.getConformance()));
        List<Conformance> list = new ArrayList<>();
        list.addAll(GXDLMSClient.getInitialConformance(meterSettingsViewModel.getDevice().isLogicalNameReferencing()));
        list.remove(Conformance.NONE);

        //Add device conformance levels.
        GXCheckList<Conformance> checkList = new GXCheckList<>(binding.properties.getContext(), value,
                list, values ->
        {
            mDevice.setConformance(Conformance.toInteger(new HashSet<>(values)));
            mListener.onDeviceSettingChanged();
        });
        binding.properties.addView(checkList);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IGXSettingsChangedListener) {
            mListener = (IGXSettingsChangedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IGXMediaChangedListener");
        }
    }
}