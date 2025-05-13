package gurux.dlms.android.ui.meterSettings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import gurux.dlms.GXByteBuffer;
import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.android.GXDevice;
import gurux.dlms.android.IGXSettingsChangedListener;
import gurux.dlms.android.R;
import gurux.dlms.android.databinding.SettingsFragmentBinding;
import gurux.dlms.enums.Security;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.enums.SecuritySuite;
import gurux.dlms.ui.GXDLMSUi;

public class MeterSecuritySettingsFragment extends Fragment {
    private final List<String> rows = new ArrayList<String>();
    private GXDevice mDevice;
    private SettingsFragmentBinding binding;
    private IGXSettingsChangedListener mListener;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MeterSettingsViewModel meterSettingsViewModel =
                new ViewModelProvider(requireActivity()).get(MeterSettingsViewModel.class);
        mDevice = meterSettingsViewModel.getDevice();
        binding = SettingsFragmentBinding.inflate(inflater, container, false);

        final String address = "https://www.gurux.fi/GXDLMSDirector.DeviceProperties";
        binding.help.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
            startActivity(browserIntent);
        });

        View view = binding.getRoot();
        rows.add(getSecurity());
        rows.add(getSecuritySuite());
        rows.add(getSystemTitle());
        rows.add(getBlockCipherKey());
        rows.add(getAuthenticationKey());
        rows.add(getMeterSystemTitle());
        rows.add(getDedicatedKey());
        rows.add(getChallenge());
        rows.add(getInvocationCounter());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_list_item_1, rows);
        binding.properties.setAdapter(adapter);
        binding.properties.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position) {
                case 0:
                    updateSecurity();
                    break;
                case 1:
                    updateSecuritySuite();
                    break;
                case 2:
                    updateSystemTitle();
                    break;
                case 3:
                    updateBlockCipherKey();
                    break;
                case 4:
                    updateAuthenticationKey();
                    break;
                case 5:
                    updateMeterSystemTitle();
                    break;
                case 6:
                    updateDedicatedKey();
                    break;
                case 7:
                    updateChallenge();
                    break;
                case 8:
                    updateInvocationCounter();
                    break;
                default:
                    //Do nothing.
            }
        });

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

    private String getSecurity() {
        return getString(gurux.dlms.R.string.security_policy) + System.lineSeparator() + mDevice.getSecurity();
    }

    private String getSecuritySuite() {
        return getString(gurux.dlms.ui.R.string.suite) + System.lineSeparator() + mDevice.getSecuritySuite();
    }

    private String getSystemTitle() {
        if (GXByteBuffer.isAsciiString(mDevice.getPassword())) {
            return getString(gurux.dlms.ui.R.string.clientSystemTitle) + System.lineSeparator() + new String(mDevice.getSystemTitle());
        }
        return getString(gurux.dlms.ui.R.string.clientSystemTitle) + System.lineSeparator() + GXDLMSTranslator.toHex(mDevice.getSystemTitle());
    }

    private String getBlockCipherKey() {
        if (GXByteBuffer.isAsciiString(mDevice.getBlockCipherKey())) {
            return getString(R.string.block_cipher_key) + System.lineSeparator() + new String(mDevice.getBlockCipherKey());
        }
        return getString(R.string.block_cipher_key) + System.lineSeparator() + GXDLMSTranslator.toHex(mDevice.getBlockCipherKey());
    }

    private String getAuthenticationKey() {
        if (GXByteBuffer.isAsciiString(mDevice.getAuthenticationKey())) {
            return getString(R.string.authentication_key) + System.lineSeparator() + new String(mDevice.getAuthenticationKey());
        }
        return getString(R.string.authentication_key) + System.lineSeparator() + GXDLMSTranslator.toHex(mDevice.getAuthenticationKey());
    }

    private String getDedicatedKey() {
        if (GXByteBuffer.isAsciiString(mDevice.getDedicatedKey())) {
            return getString(gurux.dlms.ui.R.string.dedicatedKey) + System.lineSeparator() + new String(mDevice.getDedicatedKey());
        }
        return getString(gurux.dlms.ui.R.string.dedicatedKey) + System.lineSeparator() + GXDLMSTranslator.toHex(mDevice.getDedicatedKey());
    }

    private String getChallenge() {
        if (GXByteBuffer.isAsciiString(mDevice.getChallenge())) {
            return getString(R.string.challenge) + System.lineSeparator() + new String(mDevice.getChallenge());
        }
        return getString(R.string.challenge) + System.lineSeparator() + GXDLMSTranslator.toHex(mDevice.getChallenge());
    }

    private String getMeterSystemTitle() {
        if (GXByteBuffer.isAsciiString(mDevice.getMeterSystemTitle())) {
            return getString(gurux.dlms.R.string.server_system_title) + System.lineSeparator() + new String(mDevice.getMeterSystemTitle());
        }
        return getString(gurux.dlms.R.string.server_system_title) + System.lineSeparator() + GXDLMSTranslator.toHex(mDevice.getMeterSystemTitle());
    }

    private String getInvocationCounter() {
        return "Invocation counter" + System.lineSeparator() + mDevice.getInvocationCounter();
    }


    /**
     * Update security.
     */
    private void updateSecurity() {
        List<Security> values = new ArrayList<>();
        values.add(Security.NONE);
        values.add(Security.AUTHENTICATION);
        values.add(Security.ENCRYPTION);
        values.add(Security.AUTHENTICATION_ENCRYPTION);
        GXDLMSUi.showSelection(requireContext(), "Security", mDevice.getSecurity(),
                values, (value, index) ->
                {
                    mDevice.setSecurity(value);
                    rows.set(0, getSecurity());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }

    /**
     * Update security suite.
     */
    private void updateSecuritySuite() {
        List<SecuritySuite> values = new ArrayList<>();
        values.add(SecuritySuite.SUITE_0);
        values.add(SecuritySuite.SUITE_1);
        values.add(SecuritySuite.SUITE_2);
        GXDLMSUi.showSelection(requireContext(), "Security suite", mDevice.getSecuritySuite(),
                values, (value, index) ->
                {
                    mDevice.setSecuritySuite(value);
                    rows.set(1, getSecuritySuite());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }

    /**
     * Update system title.
     */
    private void updateSystemTitle() {
        GXDLMSUi.showByteArrayDlg(requireContext(), R.string.security_setup,
                gurux.dlms.ui.R.string.clientSystemTitle,
                mDevice.getSystemTitle(), (value, ishex) ->
                {
                    if (value.length != 0 && value.length != 8) {
                        throw new IllegalArgumentException(getString(R.string.invalid_value));
                    }
                    mDevice.setSystemTitle(value);
                    rows.set(2, getSystemTitle());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }


    /**
     * Update block cipher key.
     */
    private void updateBlockCipherKey() {
        GXDLMSUi.showByteArrayDlg(requireContext(), R.string.security_setup,
                R.string.block_cipher_key,
                mDevice.getBlockCipherKey(), (value, ishex) ->
                {
                    if (value.length != 0 && value.length != 16) {
                        throw new IllegalArgumentException(getString(R.string.invalid_value));
                    }
                    mDevice.setBlockCipherKey(value);
                    rows.set(3, getBlockCipherKey());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }

    /**
     * Update authentication key.
     */
    private void updateAuthenticationKey() {
        GXDLMSUi.showByteArrayDlg(requireContext(), R.string.security_setup,
                R.string.authentication_key,
                mDevice.getAuthenticationKey(), (value, ishex) ->
                {
                    if (value.length != 0 && value.length != 16) {
                        throw new IllegalArgumentException(getString(R.string.invalid_value));
                    }
                    mDevice.setAuthenticationKey(value);
                    rows.set(4, getAuthenticationKey());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }

    /**
     * Update meter system title.
     */
    private void updateMeterSystemTitle() {
        GXDLMSUi.showByteArrayDlg(requireContext(), R.string.security_setup,
                gurux.dlms.ui.R.string.serverSystemTitle,
                mDevice.getMeterSystemTitle(), (value, ishex) ->
                {
                    if (value.length != 0 && value.length != 8) {
                        throw new IllegalArgumentException(getString(R.string.invalid_value));
                    }
                    mDevice.setMeterSystemTitle(value);
                    rows.set(5, getMeterSystemTitle());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }

    /**
     * Update dedicated key.
     */
    private void updateDedicatedKey() {
        GXDLMSUi.showByteArrayDlg(requireContext(), R.string.security_setup,
                R.string.dedicated_key,
                mDevice.getDedicatedKey(), (value, ishex) ->
                {
                    if (value.length != 0 && value.length != 16) {
                        throw new IllegalArgumentException(getString(R.string.invalid_value));
                    }
                    mDevice.setDedicatedKey(value);
                    rows.set(6, getDedicatedKey());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }

    /**
     * Update challenge.
     */
    private void updateChallenge() {
        GXDLMSUi.showByteArrayDlg(requireContext(), R.string.security_setup,
                R.string.challenge,
                mDevice.getChallenge(), (value, ishex) ->
                {
                    if (value.length != 0 && value.length != 16) {
                        throw new IllegalArgumentException(getString(R.string.invalid_value));
                    }
                    mDevice.setChallenge(value);
                    rows.set(7, getChallenge());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }

    /**
     * Update Invocation Counter.
     */
    private void updateInvocationCounter() {
        GXDLMSUi.showObisCodeDlg(requireContext(), R.string.security_setup,
                R.string.invocation_counter,
                mDevice.getInvocationCounter(), (value, index) ->
                {
                    GXDLMSObject.validateLogicalName(value);
                    mDevice.setInvocationCounter(value);
                    rows.set(8, getInvocationCounter());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }
}
