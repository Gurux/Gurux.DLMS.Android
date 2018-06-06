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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.TranslatorOutputType;

/**
 * Pdu XML translator fragment.
 */
public class GXXmlTranslator extends Fragment {

    private Button toXml;
    private Button toPdu;
    private EditText pduText;
    private EditText xmlText;
    GXDLMSTranslator translator =
            new GXDLMSTranslator(TranslatorOutputType.SIMPLE_XML);

    public GXXmlTranslator() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xml_translator, container, false);
        toXml = (Button) view.findViewById(R.id.toXml);
        toPdu = (Button) view.findViewById(R.id.toPdu);
        pduText = (EditText) view.findViewById(R.id.pdu);
        xmlText = (EditText) view.findViewById(R.id.xml);

        toXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toXml(v);
            }
        });
        toPdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPdu(v);
            }
        });
        return view;
    }

    /**
     * Remove comments
     *
     * @param data
     * @return
     */
    private static String removeComments(final String data) {
        StringBuilder sb = new StringBuilder();
        for (String it : data.split("[\n]")) {
            if (!it.startsWith("#")) {
                sb.append(it);
                sb.append("\n");
            }
        }
        if (sb.length() != 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    /**
     * Convert XML to PDU.
     */
    void toPdu(View v) {
        try {
            //Mikko translator.setHex(hexBtn.getValue());
            String xml = removeComments(xmlText.getText().toString());
            pduText.setText(translator.xmlToHexPdu(xml, true));
        } catch (Exception e) {
            GXGeneral.showError(getActivity(), e, "XML to PDU failed.");
        }
    }

    /**
     * Convert PDU to XML.
     */
    void toXml(View v) {
        try {
          //Mikko  translator.setHex(hexBtn.getValue());
            String pdu = removeComments(pduText.getText().toString());
            xmlText.setText(translator.pduToXml(pdu));
        } catch (Exception e) {
            GXGeneral.showError(getActivity(), e, "PDU to XML failed.");
        }
    }
}
