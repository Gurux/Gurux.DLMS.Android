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
// More information of Gurux products: https://www.gurux.org
//
// This code is licensed under the GNU General Public License v2. 
// Full text may be retrieved at http://www.gnu.org/licenses/gpl-2.0.txt
//---------------------------------------------------------------------------

package gurux.dlms.objects;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gurux.dlms.GXArray;
import gurux.dlms.GXDLMSConverter;
import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.GXDate;
import gurux.dlms.GXDateOS;
import gurux.dlms.GXDateTime;
import gurux.dlms.GXDateTimeOS;
import gurux.dlms.GXStructure;
import gurux.dlms.GXTime;
import gurux.dlms.GXTimeOS;
import gurux.dlms.enums.DataType;

/**
 * Read serialized COSEM object from the file.
 */
public class GXXmlReader implements AutoCloseable {

    /**
     * Element name.
     */
    private String elementName;

    XmlPullParser reader;

    private FileInputStream stream;
    /**
     * Collection of read objects.
     */
    private GXDLMSObjectCollection privateObjects;

    public final GXDLMSObjectCollection getObjects() {
        return privateObjects;
    }

    private void setObjects(final GXDLMSObjectCollection value) {
        privateObjects = value;
    }

    @Override
    public final void close() throws IOException {
        if (stream != null) {
            stream.close();
            stream = null;
        }
    }

    /**
     * Constructor.
     *
     * @param s Input stream.
     * @throws XMLStreamException Invalid XML stream.
     */
    public GXXmlReader(final InputStream s) throws XmlPullParserException {
        reader = Xml.newPullParser();
        reader.setInput(s, null);
        setObjects(new GXDLMSObjectCollection());
    }

    /**
     * Constructor.
     *
     * @param filename File name.
     * @throws XMLStreamException    Invalid XML stream.
     * @throws FileNotFoundException File not found.
     */
    public GXXmlReader(final String filename)
            throws XmlPullParserException, FileNotFoundException {
        reader = Xml.newPullParser();
        stream = new java.io.FileInputStream(filename);
        reader.setInput(stream, null);
        setObjects(new GXDLMSObjectCollection());
    }

    /**
     * @return Name of current tag.
     */
    public String getName() {
        throw new RuntimeException("XML reader is not supported at the moment.");
    }

    private void getNext() throws XMLStreamException {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    public final boolean isEOF() throws XMLStreamException {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    public final boolean read() throws XMLStreamException {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    public final void readEndElement(final String name)
            throws XMLStreamException {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    public final boolean isStartElement(final String name,
                                        final boolean getNext) throws XMLStreamException {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    public final boolean isStartElement() {
        return true;
    }

    public final String getAttribute(final int index) {
        return reader.getAttributeValue(index);
    }

    public final int readElementContentAsInt(final String name)
            throws XMLStreamException {
        return readElementContentAsInt(name, 0);
    }

    public final int readElementContentAsInt(final String name,
                                             final int defaultValue) throws XMLStreamException {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    public final long readElementContentAsLong(final String name)
            throws XMLStreamException {
        return readElementContentAsLong(name, 0);
    }

    public final long readElementContentAsLong(final String name,
                                               final long defaultValue) throws XMLStreamException {
        getNext();
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    public final long readElementContentAsULong(final String name)
            throws XMLStreamException {
        return readElementContentAsULong(name, 0);
    }

    public final long readElementContentAsULong(final String name,
                                                final long defaultValue) throws XMLStreamException {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    public final double readElementContentAsDouble(final String name,
                                                   final double defaultValue) throws XMLStreamException {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    private List<Object> readArray(final DataType dt)
            throws XMLStreamException {
        java.util.ArrayList<Object> list;
        if (dt == DataType.ARRAY) {
            list = new GXArray();
        } else if (dt == DataType.STRUCTURE) {
            list = new GXStructure();
        } else {
            list = new ArrayList<Object>();
        }
        while (isStartElement("Item", false)) {
            list.add(readElementContentAsObject("Item", null, null, 0));
        }
        return list;
    }

    public final Object readElementContentAsObject(final String name,
                                                   final Object defaultValue, final GXDLMSObject obj, final int index)
            throws XMLStreamException {
        getNext();
        if (name.compareToIgnoreCase(getName()) == 0) {
            Object ret = null;
            String str = getAttribute(0);
            DataType uiType;
            DataType dt;
            if (str == null || str.isEmpty()) {
                dt = DataType.NONE;
            } else {
                dt = DataType.forValue(Integer.parseInt(str));
                if (obj != null) {
                    obj.setDataType(index, dt);
                }
            }
            if (reader.getAttributeCount() > 1) {
                str = getAttribute(1);
                uiType = DataType.forValue(Integer.parseInt(str));
            } else {
                uiType = dt;
            }
            if (obj != null && obj.getUIDataType(index) == DataType.NONE) {
                obj.setUIDataType(index, uiType);
            }
            if (dt == DataType.ARRAY || dt == DataType.STRUCTURE) {
                read();
                getNext();
                ret = readArray(dt);
                readEndElement(name);
                return ret;
            } else {
                str = getText();
                switch (uiType) {
                    case OCTET_STRING:
                        if (!str.isEmpty()) {
                            ret = GXDLMSTranslator.hexToBytes(str);
                        }
                        break;
                    case DATETIME:
                        if (!str.isEmpty()) {
                            if (dt == DataType.OCTET_STRING) {
                                ret = new GXDateTimeOS(str, Locale.ROOT);
                            } else {
                                ret = new GXDateTime(str, Locale.ROOT);
                            }
                        }
                        break;
                    case DATE:
                        if (!str.isEmpty()) {
                            if (dt == DataType.OCTET_STRING) {
                                ret = new GXDateOS(str, Locale.ROOT);
                            } else {
                                ret = new GXDate(str, Locale.ROOT);
                            }
                        }
                        break;
                    case TIME:
                        if (!str.isEmpty()) {
                            if (dt == DataType.OCTET_STRING) {
                                ret = new GXTimeOS(str, Locale.ROOT);
                            } else {
                                ret = new GXTime(str, Locale.ROOT);
                            }
                        }
                        break;
                    case NONE:
                        ret = defaultValue;
                        break;
                    default:
                        ret = GXDLMSConverter.changeType(str, uiType);
                }
            }
            return ret;
        }
        return defaultValue;
    }

    public final String readElementContentAsString(final String name)
            throws XMLStreamException {
        return readElementContentAsString(name, null);
    }

    public final GXTime readElementContentAsDateTime(final String name) {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    public final GXDate readElementContentAsDate(final String name)
            throws XMLStreamException {
        String str = readElementContentAsString(name, null);
        GXDate it = null;
        if (str != null) {
            it = new GXDate(str, Locale.ROOT);
        }
        return it;
    }

    public final GXTime readElementContentAsTime(final String name) {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    private String getText() {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    public final String readElementContentAsString(final String name,
                                                   final String defaultValue) {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");
    }

    @Override
    public final String toString() {
        return super.toString();
    }

}