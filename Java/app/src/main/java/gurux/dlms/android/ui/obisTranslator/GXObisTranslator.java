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


package gurux.dlms.android.ui.obisTranslator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import gurux.dlms.GXDLMSConverter;
import gurux.dlms.android.GXGeneral;
import gurux.dlms.android.R;

/**
 * OBIS code translator fragment.
 */

public class GXObisTranslator extends Fragment {
    private Button mSearch;
    private GXDLMSConverter mConverter = new GXDLMSConverter();
    private EditText mObiscode;
    private EditText mObisResult;
    private EditText mFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_obis_translator, container, false);
        mSearch = (Button) view.findViewById(R.id.search);
        mObiscode = (EditText) view.findViewById(R.id.obiscode);
        mFilter = (EditText) view.findViewById(R.id.filter);
        mObisResult = (EditText) view.findViewById(R.id.obisResult);
        mSearch.setOnClickListener(v -> onSearch(v));
        try {
            mConverter.update(getActivity());
        } catch (Exception e) {
            GXGeneral.showError(getActivity(), e, getString(R.string.error));
        }
        return view;
    }

    /**
     * Search obis code.
     */
    public void onSearch(View view) {
        try {
            StringBuilder sb = new StringBuilder();
            mObisResult.setText("");
            String[] res = mConverter.getDescription(requireContext(), mObiscode.getText().toString(), mFilter.getText().toString());
            for (String it : res) {
                sb.append(it);
                sb.append(System.lineSeparator());
            }
            mObisResult.setText(sb.toString());
        } catch (Exception e) {
            GXGeneral.showError(getActivity(), e, getString(R.string.error));
        }
    }
}
