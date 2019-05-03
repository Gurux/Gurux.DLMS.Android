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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import gurux.dlms.manufacturersettings.GXManufacturer;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;

/**
 * Manufacturers fragment.
 */
public class GXManufacturers extends Fragment {
    private Spinner mManufacturers;
    private EditText mManufacturer;
    private GXManufacturerCollection mMans;

    public GXManufacturers() {
        // Required empty public constructor
    }

    public static GXManufacturers newInstance(GXManufacturerCollection manufacturers) {
        GXManufacturers fragment = new GXManufacturers();
        Bundle args = new Bundle();
        args.putParcelable("manufacturers", manufacturers);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMans = (GXManufacturerCollection) getArguments().getParcelable("manufacturers");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manufacturers, container, false);
        mManufacturers = (Spinner) view.findViewById(R.id.manufacturers);
        mManufacturer = (EditText) view.findViewById(R.id.manufacturer);
        mManufacturers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GXManufacturer man = mMans.get(position);
                mManufacturer.setText(man.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        try {
            List<String> rows = new ArrayList<String>();
            for (GXManufacturer it : mMans) {
                rows.add(it.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, rows);
            mManufacturers.setAdapter(adapter);
        } catch (Exception e) {
            mManufacturers.setEnabled(false);
            GXGeneral.showError(getActivity(), e, getString(R.string.error));
        }
        return view;
    }
}
