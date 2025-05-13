package gurux.dlms.android.ui.meterSettings;

import android.app.AlertDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gurux.dlms.GXByteBuffer;
import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.android.GXDevice;
import gurux.dlms.android.GXGeneral;
import gurux.dlms.android.IGXSettingsChangedListener;
import gurux.dlms.android.R;
import gurux.dlms.android.databinding.SettingsFragmentBinding;
import gurux.dlms.enums.InterfaceType;
import gurux.dlms.manufacturersettings.GXAuthentication;
import gurux.dlms.manufacturersettings.GXManufacturer;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;
import gurux.dlms.manufacturersettings.GXServerAddress;
import gurux.dlms.ui.GXDLMSUi;

public class MeterGeneralSettingsFragment extends Fragment {

    private final List<String> rows = new ArrayList<>();
    GXManufacturerCollection mManufacturers;
    private GXDevice mDevice;
    private SettingsFragmentBinding binding;
    private IGXSettingsChangedListener mListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MeterSettingsViewModel meterSettingsViewModel =
                new ViewModelProvider(requireActivity()).get(MeterSettingsViewModel.class);
        mDevice = meterSettingsViewModel.getDevice();
        mManufacturers = meterSettingsViewModel.getManufacturers();
        binding = SettingsFragmentBinding.inflate(inflater, container, false);

        final String address = "https://www.gurux.fi/GXDLMSDirector.DeviceProperties";
        binding.help.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
            startActivity(browserIntent);
        });

        View view = binding.getRoot();
        rows.add(getManufacturer());
        rows.add(getNameReference());
        rows.add(getInterface());
        rows.add(getAuthentication());
        rows.add(getPassword());
        rows.add(getWaitTime());
        rows.add(getClientAddress());
        rows.add(getAddressType());
        rows.add(getPhysicalAddress());
        rows.add(getLogicalAddress());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, rows);
        binding.properties.setAdapter(adapter);
        binding.properties.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position) {
                case 0:
                    updateManufacturer();
                    break;
                case 1:
                    updateReference();
                    break;
                case 2:
                    updateInterface();
                    break;
                case 3:
                    updateAuthentication();
                    break;
                case 4:
                    updatePassword();
                    break;
                case 5:
                    updateWaitTime();
                    break;
                case 6:
                    updateClientAddress();
                    break;
                case 7:
                    updateAddressType();
                    break;
                case 8:
                    updatePhysicalAddress();
                    break;
                case 9:
                    updateLogicalAddress();
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

    private String getManufacturer() {
        return getString(R.string.manufacturer) + System.lineSeparator() + mDevice.getManufacturer();
    }

    private String getNameReference() {
        if (mDevice.isLogicalNameReferencing()) {
            return getString(R.string.reference) + System.lineSeparator() + getString(gurux.dlms.R.string.logical_name);
        }
        return getString(R.string.reference) + System.lineSeparator() + getString(R.string.short_name);
    }


    private String getInterface() {
        return getString(R.string.interfaceType) + System.lineSeparator() + mDevice.getInterfaceType();
    }

    private String getAuthentication() {
        return getString(R.string.authentication) + System.lineSeparator() + mDevice.getAuthentication();
    }

    private String getPassword() {
        if (GXByteBuffer.isAsciiString(mDevice.getPassword())) {
            return getString(R.string.password) + System.lineSeparator() + new String(mDevice.getPassword());
        }
        return getString(R.string.password) + System.lineSeparator() + GXDLMSTranslator.toHex(mDevice.getPassword());
    }

    private String getWaitTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        return getString(R.string.waittime) + System.lineSeparator() + sdf.format(mDevice.getWaitTime());
    }

    private String getClientAddress() {
        return getString(R.string.clientAddress) + System.lineSeparator() + mDevice.getClientAddress();
    }

    private String getAddressType() {
        return getString(R.string.addressType) + System.lineSeparator() + mDevice.getAddressType();
    }

    private String getPhysicalAddress() {
        return getString(R.string.physicalAddress) + System.lineSeparator() + mDevice.getPhysicalAddress();
    }

    private String getLogicalAddress() {
        return getString(R.string.logicalAddress) + System.lineSeparator() + mDevice.getLogicalAddress();
    }

    private GXManufacturer getManufacturer(GXDevice device) {
        for (GXManufacturer it : mManufacturers) {
            if (it.getIdentification().compareToIgnoreCase(device.getManufacturer()) == 0) {
                return it;
            }
        }
        return null;
    }

    /**
     * Update manufacturers.
     */
    private void updateManufacturer() {
        try {
            List<String> values = new ArrayList<>();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String actual = mDevice.getManufacturer();
            int selected = -1;
            int pos = 0;
            for (GXManufacturer it : mManufacturers) {
                values.add(it.getName());
                //Get selected item.
                if (actual.equals(it.getIdentification())) {
                    selected = pos;
                }
                ++pos;
            }
            builder.setTitle(R.string.manufacturer)
                    .setSingleChoiceItems(values.toArray(new String[0]), selected, (dialog, which) -> {
                        try {
                            mDevice.setManufacturer(mManufacturers.get(which).getIdentification());
                            rows.set(0, getManufacturer());
                            // update interface.
                            GXManufacturer man = getManufacturer(mDevice);
                            values.clear();
                            for (InterfaceType it : man.getSupporterdInterfaces()) {
                                values.add(it.name());
                            }
                            if (values.isEmpty()) {
                                values.add(InterfaceType.HDLC.toString());
                            }
                            String actual2;
                            if (mDevice.getInterfaceType() != null) {
                                actual2 = mDevice.getInterfaceType().toString();
                                for (String it : values) {
                                    //Get selected item.
                                    if (actual2.equals(it)) {
                                        mDevice.setInterfaceType(InterfaceType.valueOf(it));
                                        break;
                                    }
                                }
                            } else {
                                mDevice.setInterfaceType(InterfaceType.valueOf(values.get(0)));
                            }
                            mDevice.setLogicalNameReferencing(man.getUseLogicalNameReferencing());
                            rows.set(1, getNameReference());
                            rows.set(2, getInterface());
                            // update authentication.
                            actual2 = mDevice.getAuthentication().toString();
                            mDevice.setAuthentication(null);
                            for (GXAuthentication it : man.getSettings()) {
                                //Get selected item.
                                if (actual2.equals(it.toString())) {
                                    mDevice.setAuthentication(it);
                                    //Update client address.
                                    mDevice.setClientAddress(it.getClientAddress());
                                    break;
                                }
                            }
                            if (mDevice.getAuthentication() == null) {
                                GXAuthentication it = man.getSettings().get(0);
                                mDevice.setAuthentication(it);
                                //Update client address.
                                mDevice.setClientAddress(it.getClientAddress());
                            }
                            rows.set(3, getAuthentication());
                            rows.set(6, getClientAddress());
                            ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                            dialog.dismiss();
                        } catch (Exception ex) {
                            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
                        }
                    }).show();
        } catch (Exception ex) {
            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
        }
    }

    /**
     * Update reference.
     */
    private void updateReference() {
        GXDLMSUi.showBooleanDlg(requireContext(),
                R.string.general, gurux.dlms.android.R.string.logical_name_referencing,
                mDevice.isLogicalNameReferencing(),
                (value, index) ->
                {
                    mDevice.setLogicalNameReferencing(value);
                    rows.set(1, getNameReference());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }


    /**
     * Update interface.
     */
    private void updateInterface() {
        GXManufacturer man = getManufacturer(mDevice);
        List<InterfaceType> values = new ArrayList<>(man.getSupporterdInterfaces());
        if (values.size() > 1) {
            GXDLMSUi.showSelection(requireContext(), R.string.interfaceType,
                    mDevice.getInterfaceType(),
                    values, (value, index) ->
                    {
                        mDevice.setInterfaceType(value);
                        rows.set(2, getInterface());
                        ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                        mListener.onDeviceSettingChanged();
                    },
                    "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
        }
    }

    /**
     * Update authentication.
     */
    private void updateAuthentication() {
        GXManufacturer man = getManufacturer(mDevice);
        List<GXAuthentication> values = new ArrayList<>(man.getSettings());
        if (values.size() > 1) {
            GXDLMSUi.showSelection(requireContext(), R.string.authentication,
                    mDevice.getAuthentication(),
                    values, (value, index) ->
                    {
                        mDevice.setAuthentication(value);
                        rows.set(3, getAuthentication());
                        //Update client address.
                        mDevice.setClientAddress(value.getClientAddress());
                        rows.set(6, getClientAddress());
                        ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                        mListener.onDeviceSettingChanged();
                    },
                    "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
        }
    }

    /**
     * Update password.
     */
    private void updatePassword() {
        GXDLMSUi.showByteArrayDlg(requireContext(), gurux.dlms.ui.R.string.general,
                gurux.dlms.ui.R.string.password,
                mDevice.getPassword(), (value, ishex) ->
                {
                    mDevice.setPassword(value);
                    rows.set(4, getPassword());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }

    /**
     * Update wait time.
     */

    private void updateWaitTime() {
        Date dt = new Date(mDevice.getWaitTime());
        GXDLMSUi.showTimeDlg(requireContext(), gurux.dlms.ui.R.string.general,
                R.string.waittime,
                dt,
                (value, ishex) ->
                {
                    mDevice.setWaitTime((int) value.getTime());
                    rows.set(5, getWaitTime());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }

    /**
     * Update client address.
     */
    private void updateClientAddress() {
        GXDLMSUi.showNumertDlg(requireContext(), gurux.dlms.ui.R.string.general,
                R.string.clientAddress,
                mDevice.getClientAddress(),
                (value, ishex) ->
                {
                    mDevice.setClientAddress(value);
                    rows.set(6, getClientAddress());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }

    /**
     * Update address type.
     */
    private void updateAddressType() {
        try {
            List<GXServerAddress> values = new ArrayList<>();
            GXManufacturer man = getManufacturer(mDevice);
            values.addAll(man.getServerSettings());
            int selected = -1;
            int pos = 0;
            String actual = mDevice.getAddressType().toString();
            String[] names = new String[values.size()];
            for (GXServerAddress it : values) {
                names[pos] = it.getHDLCAddress().toString();
                //Get selected item.
                if (actual.equals(names[pos])) {
                    selected = pos;
                }
                ++pos;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.addressType)
                    .setSingleChoiceItems(names, selected, (dialog, which) -> {
                        try {
                            mDevice.setAddressType(values.get(which).getHDLCAddress());
                            rows.set(7, getAddressType());
                            ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                            dialog.dismiss();
                        } catch (Exception ex) {
                            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
                        }
                    }).show();
        } catch (Exception ex) {
            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
        }
    }

    /**
     * Update physical address.
     */
    private void updatePhysicalAddress() {
        GXDLMSUi.showNumertDlg(requireContext(), gurux.dlms.ui.R.string.general,
                R.string.physicalAddress,
                mDevice.getPhysicalAddress(),
                (value, ishex) ->
                {
                    mDevice.setPhysicalAddress(value);
                    rows.set(8, getPhysicalAddress());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }

    /**
     * Update logical address.
     */
    private void updateLogicalAddress() {
        GXDLMSUi.showNumertDlg(requireContext(), gurux.dlms.ui.R.string.general,
                R.string.logicalAddress,
                mDevice.getLogicalAddress(),
                (value, ishex) ->
                {
                    mDevice.setLogicalAddress(value);
                    rows.set(9, getLogicalAddress());
                    ((BaseAdapter) binding.properties.getAdapter()).notifyDataSetChanged();
                    mListener.onDeviceSettingChanged();
                },
                "https://www.gurux.fi/GXDLMSDirector.DeviceProperties");
    }
}