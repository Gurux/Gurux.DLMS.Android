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

package gurux.dlms.objects;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;

import gurux.dlms.GXDLMSClient;
import gurux.dlms.enums.ObjectType;

/**
 * Collection of DLMS objects.
 */
public class GXDLMSObjectCollection extends ArrayList<GXDLMSObject>
        implements java.util.List<GXDLMSObject> {
    private static final long serialVersionUID = 1L;
    private Object parent;

    /**
     * Constructor.
     */
    public GXDLMSObjectCollection() {
    }

    /**
     * Constructor.
     *
     * @param forParent Parent object.
     */
    public GXDLMSObjectCollection(final Object forParent) {
        parent = forParent;
    }

    public final Object getParent() {
        return parent;
    }

    final void setParent(final Object value) {
        parent = value;
    }

    public final GXDLMSObjectCollection getObjects(final ObjectType type) {
        GXDLMSObjectCollection items = new GXDLMSObjectCollection();
        for (GXDLMSObject it : this) {
            if (it.getObjectType() == type) {
                items.add(it);
            }
        }
        return items;
    }

    public final GXDLMSObjectCollection getObjects(final ObjectType[] types) {
        GXDLMSObjectCollection items = new GXDLMSObjectCollection();
        for (GXDLMSObject it : this) {
            for (ObjectType type : types) {
                if (type == it.getObjectType()) {
                    items.add(it);
                    break;
                }
            }
        }
        return items;
    }

    public final GXDLMSObject findByLN(final ObjectType type, final String ln) {
        for (GXDLMSObject it : this) {
            if ((type == ObjectType.NONE || it.getObjectType() == type)
                    && it.getLogicalName().trim().equals(ln)) {
                return it;
            }
        }
        return null;
    }

    public final GXDLMSObject findBySN(final int sn) {
        for (GXDLMSObject it : this) {
            if (it.getShortName() == sn) {
                return it;
            }
        }
        return null;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (GXDLMSObject it : this) {
            if (sb.length() != 1) {
                sb.append(", ");
            }
            sb.append(it.getName().toString());
        }
        sb.append(']');
        return sb.toString();
    }

    private static String readText(XmlPullParser parser) throws
            IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /*
     * Load COSEM objects from the XML.
     * @param value XML string.
     */
    public void setXml(final String value) throws XmlPullParserException, IOException {
        if (value != null && !value.isEmpty()) {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(value));
            int event;
            GXDLMSObject obj = null;
            ObjectType type;
            while ((event = parser.next()) != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG) {
                    String target = parser.getName();
                    if ("objects".compareToIgnoreCase(target) == 0) {
                        event = parser.next();
               //         event = parser.next();
                    } else if ("object".compareToIgnoreCase(target) == 0) {
                        type = ObjectType
                                .valueOf(parser.getAttributeValue(0));
                        obj = GXDLMSClient.createObject(type);
                        add(obj);
                    } else if ("SN".compareToIgnoreCase(target) == 0) {
                        String data = readText(parser);
                        obj.setShortName(Integer.parseInt(data));
                    } else if ("LN".compareToIgnoreCase(target) == 0) {
                        String data = readText(parser);
                        obj.setLogicalName(data);
                    } else if ("Description".compareToIgnoreCase(target) == 0) {
                        String data = readText(parser);
                        obj.setDescription(data);
                    }
                }
            }
        }
    }

    /**
     * Save COSEM objects from the XML.
     *
     * @return XML string.
     */
    public final String getXml() {
        StringBuffer sb = new StringBuffer();
        String nl = System.getProperty("line.separator");
        sb.append("<Objects>");
        sb.append(nl);
        for (GXDLMSObject it : this) {
            sb.append("<Object Type=\"");
            sb.append(String.valueOf(it.getObjectType()) + "\">");
            sb.append(nl);
            // Add SN
            if (it.getShortName() != 0) {
                sb.append("<SN>");
                sb.append(String.valueOf(it.getShortName()));
                sb.append("</SN>");
                sb.append(nl);
            }
            // Add LN
            sb.append("<LN>");
            sb.append(it.getLogicalName());
            sb.append("</LN>");
            sb.append(nl);
            // Add description if given.
            if (it.getDescription() != null
                    && !it.getDescription().isEmpty()) {
                sb.append("<Description>");
                sb.append(it.getDescription());
                sb.append("</Description>");
                sb.append(nl);
            }
            sb.append("</Object>");
        }
        sb.append("</Objects>");
        sb.append(nl);
        return sb.toString();
    }
}