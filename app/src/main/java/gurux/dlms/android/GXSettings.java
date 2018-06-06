//
// --------------------------------------------------------------------------
//  Gurux Ltd
//
//
//
// Filename:        $HeadURL$
//
// Version:         $Revision$,
//                  $Date$
//                  $Author$
//
// Copyright (c) Gurux Ltd
//
//---------------------------------------------------------------------------
//
//  DESCRIPTION
//
// This file is a part of Gurux Device Framework.
//
// Gurux Device Framework is Open Source software; you can redistribute it
// and/or modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; version 2 of the License.
// Gurux Device Framework is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU General Public License for more details.
//
// More information of Gurux products: http://www.gurux.org
//
// This code is licensed under the GNU General Public License v2.
// Full text may be retrieved at http://www.gnu.org/licenses/gpl-2.0.txt
//---------------------------------------------------------------------------


package gurux.dlms.android;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gurux.dlms.enums.Authentication;
import gurux.dlms.manufacturersettings.GXAuthentication;
import gurux.dlms.manufacturersettings.GXManufacturer;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;
import gurux.dlms.manufacturersettings.GXServerAddress;
import gurux.dlms.manufacturersettings.HDLCAddressType;
import gurux.dlms.manufacturersettings.StartProtocolType;

/**
 * Meter settings.
 */
public class GXSettings extends Fragment {
    private Spinner mManufacturer;
    private Spinner mProtocol;
    private Spinner mAuthentication;
    private Spinner mAddressType;

    private EditText mPassword;
    private EditText mWaittime;
    private EditText mClientAddress;
    private EditText mPhysicalServer;
    private EditText mLocicalServer;
    private GXDevice mDevice;
    TextView mPhysicaladdressLbl;
    /**
     * Selected manufacturer.
     */
    GXManufacturer selected;

    GXManufacturerCollection mManufacturers;

    public GXSettings() {
        // Required empty public constructor
    }

    public static GXSettings newInstance(GXDevice device, GXManufacturerCollection manufacturers) {
        GXSettings fragment = new GXSettings();
        Bundle args = new Bundle();
        args.putParcelable("device", device);
        args.putParcelable("manufacturers", manufacturers);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDevice = (GXDevice) getArguments().getParcelable("device");
            mManufacturers = (GXManufacturerCollection) getArguments().getParcelable("manufacturers");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mManufacturer = (Spinner) view.findViewById(R.id.manufacturer);
        mProtocol = (Spinner) view.findViewById(R.id.protocol);
        mAuthentication = (Spinner) view.findViewById(R.id.authentication);
        mAddressType = (Spinner) view.findViewById(R.id.addressType);
        mPassword = (EditText) view.findViewById(R.id.password);
        mWaittime = (EditText) view.findViewById(R.id.waittime);

        mClientAddress = (EditText) view.findViewById(R.id.clientAddress);
        mPhysicaladdressLbl = (TextView) view.findViewById(R.id.physicaladdressLbl);
        mPhysicalServer = (EditText) view.findViewById(R.id.physicalServer);
        mLocicalServer = (EditText) view.findViewById(R.id.locicalServer);
        //Add protocols.
        List<StartProtocolType> protocols = new ArrayList<StartProtocolType>();
        protocols.add(StartProtocolType.IEC);
        protocols.add(StartProtocolType.DLMS);
        ArrayAdapter<StartProtocolType> protocolAdapter = new ArrayAdapter<StartProtocolType>(getActivity(),
                android.R.layout.simple_list_item_1, protocols);
        mProtocol.setAdapter(protocolAdapter);

        try {
            //Add manufacturers.
            if (mManufacturers != null) {
                List<String> rows = new ArrayList<String>();
                for (GXManufacturer it : mManufacturers) {
                    rows.add(it.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, rows);
                mManufacturer.setAdapter(adapter);
            }
            if (mDevice != null) {
                //Find selected manufacturer.
                if (mDevice.getManufacturer() != null && !mDevice.getManufacturer().isEmpty()) {
                    int pos = 0;
                    for (GXManufacturer it : mManufacturers) {
                        if (it.getIdentification().compareToIgnoreCase(mDevice.getManufacturer()) == 0) {
                            mManufacturer.setSelection(pos, false);
                            selected = it;
                            updateAuthentications(view, mDevice.getAuthentication());
                            updateAddressType(view, mDevice.getAddressType());
                            break;
                        }
                        ++pos;
                    }
                } else {
                    mManufacturer.setSelection(0);
                }
                //Update start protocol.
                mProtocol.setSelection(mDevice.getStartProtocol().ordinal(), false);
                mPassword.setText(String.valueOf(mDevice.getPassword()));
                mWaittime.setText(String.valueOf(mDevice.getWaitTime()));
                mClientAddress.setText(String.valueOf(mDevice.getClientAddress()));
                mPhysicalServer.setText(String.valueOf(mDevice.getPhysicalAddress()));
                mLocicalServer.setText(String.valueOf(mDevice.getLogicalAddress()));
            }
        } catch (Exception e) {
            mManufacturer.setEnabled(false);
            GXGeneral.showError(getActivity(), e, getString(R.string.error));
        }
        mWaittime.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
                if (mDevice != null && !c.toString().isEmpty()) {
                    int value = Integer.parseInt(c.toString());
                    if (value < 2) {
                        Toast.makeText(getActivity(), R.string.invalidWaitTime, Toast.LENGTH_SHORT).show();
                    } else {
                        mDevice.setWaitTime(value);
                    }
                }
            }
        });
        mManufacturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = mManufacturers.get(position);
                mDevice.setManufacturer(selected.getIdentification());
                mDevice.setLogicalNameReferencing(selected.getUseLogicalNameReferencing());
                mProtocol.setSelection(selected.getStartProtocol().ordinal());
                updateAuthentications(view, null);
                updateAddressType(view, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mAddressType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GXServerAddress s = selected.getServerSettings().get(position);
                if (s.getHDLCAddress() == HDLCAddressType.SERIAL_NUMBER) {
                    mPhysicaladdressLbl.setText(R.string.serialnumber);
                } else {
                    mPhysicaladdressLbl.setText(R.string.physicalAddress);
                }
                mDevice.setAddressType(((GXServerAddress) selected.getServerSettings().get(position)).getHDLCAddress());
                mPhysicalServer.setText(String.valueOf(s.getPhysicalAddress()));
                mLocicalServer.setText(String.valueOf(s.getLogicalAddress()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mAuthentication.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GXAuthentication a = selected.getSettings().get(position);
                mClientAddress.setText(String.valueOf(a.getClientAddress()));
                mDevice.setAuthentication(a.getType());
                mDevice.setClientAddress(a.getClientAddress());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
                mDevice.setPassword(c.toString());
            }
        });

        mClientAddress.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
                if (selected != null && !c.toString().isEmpty()) {
                    int value = Integer.parseInt(c.toString());
                    if (value < 1) {
                        Toast.makeText(getActivity(), R.string.invalidClientAddress, Toast.LENGTH_SHORT).show();
                    } else {
                        mDevice.setClientAddress(value);
                    }
                }
            }
        });
        mPhysicalServer.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
                if (selected != null && !c.toString().isEmpty()) {
                    int value = Integer.parseInt(c.toString());
                    if (value < 0) {
                        GXServerAddress s = selected.getServerSettings().get(mAddressType.getSelectedItemPosition());
                        if (s.getHDLCAddress() == HDLCAddressType.SERIAL_NUMBER) {
                            Toast.makeText(getActivity(), R.string.invalidSerialNumber, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), R.string.invalidPhysicalServerAddress, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDevice.setPhysicalAddress(value);
                    }
                }
            }
        });

        mLocicalServer.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
                if (selected != null && !c.toString().isEmpty()) {
                    int value = Integer.parseInt(c.toString());
                    if (value < 0) {
                        Toast.makeText(getActivity(), R.string.invalidLogicalServerAddress, Toast.LENGTH_SHORT).show();
                    } else {
                        mDevice.setLogicalAddress(value);
                    }
                }
            }
        });

        mProtocol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDevice.setStartProtocol((StartProtocolType) mProtocol.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    /**
     * Update manufacture type authentications.
     *
     * @param view View.
     */
    private void updateAuthentications(View view, Authentication authentication) {
        List<Authentication> authentications = new ArrayList<Authentication>();
        ArrayAdapter<Authentication> authenticationAdapter = new ArrayAdapter<Authentication>(getActivity(),
                android.R.layout.simple_list_item_1, authentications);
        int index = -1, pos = 0;
        for (GXAuthentication it : selected.getSettings()) {
            if (it.getType() == authentication) {
                index = pos;
            }
            authentications.add(it.getType());
            ++pos;
        }
        mAuthentication.setAdapter(authenticationAdapter);
        ((BaseAdapter) mAuthentication.getAdapter()).notifyDataSetChanged();
        if (index == -1) {
            mAuthentication.setSelection(0);
        } else {
            mAuthentication.setSelection(index, false);
        }
    }

    /**
     * Update manufacture type authentications.
     *
     * @param view View.
     */
    private void updateAddressType(View view, HDLCAddressType type) {
        List<HDLCAddressType> authentications = new ArrayList<HDLCAddressType>();
        ArrayAdapter<HDLCAddressType> authenticationAdapter = new ArrayAdapter<HDLCAddressType>(getActivity(),
                android.R.layout.simple_list_item_1, authentications);
        int index = -1, pos = 0;
        for (GXServerAddress it : selected.getServerSettings()) {
            authentications.add(it.getHDLCAddress());
            if (it.getHDLCAddress() == type) {
                index = pos;
            }
            ++pos;
        }
        mAddressType.setAdapter(authenticationAdapter);
        ((BaseAdapter) mAddressType.getAdapter()).notifyDataSetChanged();
        if (index == -1) {
            mAddressType.setSelection(0);
        } else {
            mAddressType.setSelection(index, false);
        }
    }
}
