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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import gurux.dlms.GXDLMSConverter;
import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.GXDate;
import gurux.dlms.GXDateTime;
import gurux.dlms.GXTime;
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
     * @param s
     *            Input stream.
     * @throws XMLStreamException
     *             Invalid XML stream.
     */
    public GXXmlReader(final InputStream s) throws XmlPullParserException {
        reader = Xml.newPullParser();
        reader.setInput(s, null);
        setObjects(new GXDLMSObjectCollection());
    }

    /**
     * Constructor.
     * 
     * @param filename
     *            File name.
     * @throws XMLStreamException
     *             Invalid XML stream.
     * @throws FileNotFoundException
     *             File not found.
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
    public String getName()throws XmlPullParserException {
       throw new UnsupportedOperationException("XML reader is not supported at the moment.");
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

    private Object[] readArray() throws XMLStreamException {
        java.util.ArrayList<Object> list = new java.util.ArrayList<Object>();
        while (isStartElement("Item", false)) {
            list.add(readElementContentAsObject("Item", null));
        }
        return list.toArray(new Object[0]);
    }

    public final Object readElementContentAsObject(final String name,
            final Object defaultValue) throws XMLStreamException {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");

    }

    public final String readElementContentAsString(final String name)
            throws XMLStreamException {
        return readElementContentAsString(name, null);
    }

    private String getText() throws XMLStreamException {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");

    }

    public final String readElementContentAsString(final String name,
            final String defaultValue) throws XMLStreamException {
        throw new UnsupportedOperationException("XML reader is not supported at the moment.");

    }
}