package gurux.dlms.android.ui.meterSettings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gurux.dlms.android.GXDevice;
import gurux.dlms.android.GXGeneral;
import gurux.dlms.android.R;
import gurux.dlms.android.databinding.FragmentMeterSettingsBinding;
import gurux.dlms.enums.InterfaceType;
import gurux.dlms.manufacturersettings.GXAuthentication;
import gurux.dlms.manufacturersettings.GXManufacturer;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;
import gurux.dlms.manufacturersettings.GXServerAddress;
import gurux.dlms.manufacturersettings.HDLCAddressType;
import gurux.dlms.manufacturersettings.StartProtocolType;

public class MeterSettingsFragment extends Fragment {

    private ViewGroup mContainer;
    ListView listView;
    List<String> rows = new ArrayList<String>();

    private GXDevice mDevice;
    GXManufacturerCollection mManufacturers;

    private FragmentMeterSettingsBinding binding;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContainer = container;
        MeterSettingsViewModel meterSettingsViewModel =
                new ViewModelProvider(requireActivity()).get(MeterSettingsViewModel.class);
        mDevice = meterSettingsViewModel.getDevice();
        mManufacturers = meterSettingsViewModel.getManufacturers();
        View view = inflater.inflate(R.layout.fragment_meter_settings, container, false);
        listView = view.findViewById(R.id.properties);
        rows.add(getManufacturer());
        rows.add(getInterface());
        rows.add(getAuthentication());
        rows.add(getPassword());
        rows.add(getWaitTime());
        rows.add(getClientAddress());
        rows.add(getAddressType());
        rows.add(getPhysicalAddress());
        rows.add(getLogicalAddress());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, rows);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        updateManufacturer();
                        break;
                    case 1:
                        updateInterface();
                        break;
                    case 2:
                        updateAuthentication();
                        break;
                    case 3:
                        updatePassword();
                        break;
                    case 4:
                        updateWaitTime();
                        break;
                    case 5:
                        updateClientAddress();
                        break;
                    case 6:
                        updateAddressType();
                        break;
                    case 7:
                        updatePhysicalAddress();
                        break;
                    case 8:
                        updateLogicalAddress();
                        break;
                    default:
                        //Do nothing.
                }
            }
        });

        return view;
    }

    private String getManufacturer()
    {
        return getString(R.string.manufacturer) + "\r\n" + mDevice.getManufacturer();
    }

    private String getInterface()
    {
        return getString(R.string.interfaceType) + "\r\n" + mDevice.getInterfaceType();
    }

    private String getAuthentication()
    {
        return getString(R.string.authentication) + "\r\n" + mDevice.getAuthentication();
    }
    private String getPassword()
    {
        return getString(R.string.password) + "\r\n" + mDevice.getPassword();
    }
    private String getWaitTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        return getString(R.string.waittime) + "\r\n" + sdf.format(mDevice.getWaitTime());
    }
    private String getClientAddress()
    {
        return getString(R.string.clientAddress) + "\r\n" + mDevice.getClientAddress();
    }
    private String getAddressType()
    {
        return getString(R.string.addressType) + "\r\n" + mDevice.getAddressType();
    }
    private String getPhysicalAddress()
    {
        return getString(R.string.physicalAddress) + "\r\n" + mDevice.getPhysicalAddress();
    }
    private String getLogicalAddress()
    {
        return getString(R.string.logicalAddress) + "\r\n" + mDevice.getLogicalAddress();
    }

    private GXManufacturer getManufacturer(GXDevice device)
    {
        for(GXManufacturer it : mManufacturers) {
            if (it.getIdentification().compareToIgnoreCase(device.getManufacturer()) == 0)
            {
                return it;
            }
        }
        return null;
    }

    /**
     * Update manufacturers.
     */
    private void updateManufacturer() {
        try{
            List<String> values = new ArrayList<>();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String actual = mDevice.getManufacturer();
            int selected = -1;
            int pos = 0;
            for(GXManufacturer it : mManufacturers) {
                values.add(it.getName());
                //Get selected item.
                if (actual == it.getIdentification()) {
                    selected = pos;
                }
                ++pos;
            }
            builder.setTitle(R.string.manufacturer)
                .setSingleChoiceItems(values.toArray(new String[values.size()]), selected, (dialog, which) -> {
                    try{
                        mDevice.setManufacturer(mManufacturers.get(which).getIdentification());
                        rows.set(0, getManufacturer());
                        // update interface.
                        GXManufacturer man = getManufacturer(mDevice);
                        values.clear();
                        for(InterfaceType it : man.getSupporterdInterfaces())
                        {
                            values.add(it.name());
                        }
                        if (values.isEmpty())
                        {
                            values.add(InterfaceType.HDLC.toString());
                        }
                        String actual2;
                        if (mDevice.getInterfaceType() != null)
                        {
                            actual2 = mDevice.getInterfaceType().toString();
                            for (String it : values) {
                                //Get selected item.
                                if (actual2 == it) {
                                    mDevice.setInterfaceType(InterfaceType.valueOf(it));
                                    break;
                                }
                            }
                        }else
                        {
                            mDevice.setInterfaceType(InterfaceType.valueOf(values.get(0)));
                        }
                        rows.set(1, getInterface());
                        // update authentication.
                        actual2 = mDevice.getAuthentication().toString();
                        mDevice.setAuthentication(null);
                        for (GXAuthentication it : man.getSettings()) {
                            //Get selected item.
                            if (actual2 == it.toString()) {
                                mDevice.setAuthentication(it);
                                //Update client address.
                                mDevice.setClientAddress(it.getClientAddress());
                                break;
                            }
                        }if (mDevice.getAuthentication() == null)
                        {
                            GXAuthentication it = man.getSettings().get(0);
                            mDevice.setAuthentication(it);
                            //Update client address.
                            mDevice.setClientAddress(it.getClientAddress());
                        }
                        rows.set(2, getAuthentication());
                        rows.set(5, getClientAddress());
                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                        dialog.dismiss();
                    }
                    catch(Exception ex)
                    {
                        GXGeneral.showError(getActivity(), ex, getString(R.string.error));
                    }
                }).show();
        }
        catch(Exception ex)
        {
            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
        }
    }

    /**
     * Update interface.
     */
    private void updateInterface() {
        try{
            List<String> values = new ArrayList<>();
            GXManufacturer man = getManufacturer(mDevice);
            for(InterfaceType it : man.getSupporterdInterfaces())
            {
                values.add(it.toString());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String actual = mDevice.getInterfaceType().toString();
            int selected = -1;

            int pos = 0;
            for (String it : values) {
                //Get selected item.
                if (actual == it) {
                    selected = pos;
                }
                ++pos;
            }
            builder.setTitle(R.string.interfaceType)
                .setSingleChoiceItems(values.toArray(new String[values.size()]), selected, (dialog, which) -> {
                    try{
                    mDevice.setInterfaceType(InterfaceType.valueOfString(values.get(which)));
                    rows.set(1, getInterface());
                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                    dialog.dismiss();
                    }
                    catch(Exception ex)
                    {
                        GXGeneral.showError(getActivity(), ex, getString(R.string.error));
                    }
                }).show();
        }
        catch(Exception ex)
        {
            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
        }
    }

    /**
     * Update authentication.
     */
    private void updateAuthentication() {
        try {
            List<GXAuthentication> values = new ArrayList<>();
            GXManufacturer man = getManufacturer(mDevice);
            values.addAll(man.getSettings());
            int selected = -1;
            int pos = 0;
            String actual = mDevice.getAuthentication().toString();
            String[] names = new String[values.size()];
            for (GXAuthentication it : values) {
                names[pos] = it.toString();
                //Get selected item.
                if (it.toString().compareTo(actual) == 0) {
                    selected = pos;
                }
                ++pos;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.authentication)
                    .setSingleChoiceItems(names, selected, (dialog, which) -> {
                        try{
                        mDevice.setAuthentication(values.get(which));
                        rows.set(2, getAuthentication());
                        //Update client address.
                        mDevice.setClientAddress(values.get(which).getClientAddress());
                        rows.set(5, getClientAddress());
                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                        dialog.dismiss();
                        }
                        catch(Exception ex)
                        {
                            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
                        }
                    }).show();
        }
        catch(Exception ex)
        {
            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
        }
    }

    /**
     * Update password.
     */
    private void updatePassword() {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_text, null, false);
            EditText text = view.findViewById(R.id.value);
            text.setText(mDevice.getPassword());
            builder.setTitle(R.string.password)
                    .setView(view)
                    .setPositiveButton(R.string.ok, (dialog, id) ->
                    {
                        try{
                            mDevice.setPassword(String.valueOf(text.getText()));
                            rows.set(3, getPassword());
                            dialog.dismiss();
                        }
                        catch(Exception ex)
                        {
                            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) ->
                    {
                        dialog.dismiss();
                    }).show();
        }
        catch(Exception ex)
        {
            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
        }
    }

    /**
     * Update wait time.
     */

    private void updateWaitTime() {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_time, null, false);
            EditText text = view.findViewById(R.id.value);
            text.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
            text.setText(sdf.format(new Date(mDevice.getWaitTime())));
            builder.setTitle(R.string.waittime)
                    .setView(view)
                    .setPositiveButton(R.string.ok, (dialog, id) ->
                    {
                        try {
                            mDevice.setWaitTime((int)sdf.parse(String.valueOf(text.getText())).getTime());
                            rows.set(4, getWaitTime());
                            dialog.dismiss();
                        }
                        catch(Exception ex)
                        {
                            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) ->
                    {
                        dialog.dismiss();
                    }).show();
        }
        catch(Exception ex)
        {
            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
        }
    }

    /**
     * Update client address.
     */
    private void updateClientAddress() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_number, null, false);
            EditText text = view.findViewById(R.id.value);
            text.setText(String.valueOf(mDevice.getClientAddress()));
            builder.setTitle(R.string.waittime)
                    .setView(view)
                    .setPositiveButton(R.string.ok, (dialog, id) ->
                    {
                        try{
                            mDevice.setClientAddress(Integer.parseInt(String.valueOf(text.getText())));
                            rows.set(5, getClientAddress());
                            dialog.dismiss();
                        }
                        catch(Exception ex)
                        {
                            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) ->
                    {
                        dialog.dismiss();
                    }).show();
        }
        catch(Exception ex)
        {
            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
        }
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
                if (actual == names[pos]) {
                    selected = pos;
                }
                ++pos;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.addressType)
                    .setSingleChoiceItems(names, selected, (dialog, which) -> {
                        try{
                            mDevice.setAddressType(values.get(which).getHDLCAddress());
                            rows.set(6, getAddressType());
                            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                            dialog.dismiss();
                        }
                        catch(Exception ex)
                        {
                            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
                        }
                    }).show();
        }
        catch(Exception ex)
        {
            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
        }
    }

    /**
     * Update physical address.
     */
    private void updatePhysicalAddress() {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_number, null, false);
            EditText text = view.findViewById(R.id.value);
            text.setText(String.valueOf(mDevice.getPhysicalAddress()));
            builder.setTitle(R.string.waittime)
                    .setView(view)
                    .setPositiveButton(R.string.ok, (dialog, id) ->
                    {
                        try{
                            mDevice.setPhysicalAddress(Integer.parseInt(String.valueOf(text.getText())));
                            rows.set(7, getPhysicalAddress());
                            dialog.dismiss();
                        }
                        catch(Exception ex)
                        {
                            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) ->
                    {
                        dialog.dismiss();
                    }).show();
        }
        catch(Exception ex)
        {
            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
        }
    }

    /**
     * Update logical address.
     */
    private void updateLogicalAddress() {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_number, null, false);
            EditText text = view.findViewById(R.id.value);
            text.setText(String.valueOf(mDevice.getLogicalAddress()));
            builder.setTitle(R.string.waittime)
                    .setView(view)
                    .setPositiveButton(R.string.ok, (dialog, id) ->
                    {
                        try{
                            mDevice.setLogicalAddress(Integer.parseInt(String.valueOf(text.getText())));
                            rows.set(8, getLogicalAddress());
                            dialog.dismiss();
                        }
                        catch(Exception ex)
                        {
                            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) ->
                    {
                        dialog.dismiss();
                    }).show();
        }
        catch(Exception ex)
        {
            GXGeneral.showError(getActivity(), ex, getString(R.string.error));
        }
    }
}