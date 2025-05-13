package gurux.dlms.android.ui.meterSettings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import gurux.dlms.android.R;
import gurux.dlms.android.databinding.FragmentMeterSettingsBinding;
import gurux.dlms.ui.GXTab;

public class MeterSettingsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final FragmentMeterSettingsBinding binding = FragmentMeterSettingsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        GXTab tab = new GXTab(this);
        tab.addTab(getString(R.string.general), new MeterGeneralSettingsFragment());
        tab.addTab(getString(R.string.security), new MeterSecuritySettingsFragment());
        tab.addTab(getString(R.string.supported_services), new MeterSupportedServicesSettingsFragment());
        binding.properties.addView(tab);
        return view;
    }
}