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

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Date;

import gurux.dlms.GXDLMSConverter;
import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.GXDateTime;
import gurux.dlms.enums.DataType;

/**
 * Save COSEM object to the file.
 */
public class GXXmlWriter implements AutoCloseable{

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
    public GXXmlWriter(final String filename)
            throws FileNotFoundException, XMLStreamException {
        throw new UnsupportedOperationException("XML writer is not supported at the moment.");
    }

    /**
     * Constructor.
     * 
     * @param s
     *            Stream.
     * @throws XMLStreamException
     *             Invalid XML stream.
     */
    public GXXmlWriter(final OutputStream s) throws XMLStreamException {
        throw new UnsupportedOperationException("XML writer is not supported at the moment.");
    }

    @Override
    public final void close()  {
    }

    /**
     * Append spaces to the buffer.
     * 
     * @throws XMLStreamException
     */
    private void appendSpaces() throws XMLStreamException {
        throw new UnsupportedOperationException("XML writer is not supported at the moment.");
    }

    public final void writeStartDocument() throws XMLStreamException {
        throw new UnsupportedOperationException("XML writer is not supported at the moment.");
    }

    public final void writeStartElement(final String name)
            throws XMLStreamException {
        throw new UnsupportedOperationException("XML writer is not supported at the moment.");
    }

    public final void writeStartElement(final String elementName,
            final String attributeName, final String value,
            final boolean newLine) throws XMLStreamException {
        throw new UnsupportedOperationException("XML writer is not supported at the moment.");
    }

    public final void writeElementString(final String name, final long value)
            throws XMLStreamException {
        if (value != 0) {
            writeElementString(name, value, 0);
        }
    }

    public final void writeElementString(final String name, final double value)
            throws XMLStreamException {
        writeElementString(name, value, 0);
    }

    public final void writeElementString(final String name, final double value,
            final double defaultValue) throws XMLStreamException {
        if (value != defaultValue) {
            writeElementString(name, String.valueOf(value));
        }
    }

    public final void writeElementString(final String name, final int value)
            throws XMLStreamException {
        if (value != 0) {
            writeElementString(name, String.valueOf(value));
        }
    }

    public final void writeElementString(final String name, final String value)
            throws XMLStreamException {
        throw new UnsupportedOperationException("XML writer is not supported at the moment.");
    }

    public final void writeElementString(final String name, final boolean value)
            throws XMLStreamException {
        if (value) {
            writeElementString(name, "1");
        }
    }

    public final void writeElementString(final String name,
            final GXDateTime value) throws XMLStreamException {
        if (value != null && value.getMeterCalendar()
                .getTime() != new java.util.Date(0)) {
            writeElementString(name, value.toFormatString());
        }
    }

    public final void writeElementString(final String name,
                                         final Date value) throws XMLStreamException {
        if (value != null && value.compareTo(new java.util.Date(0)) != 0) {
            writeElementString(name, value.toString());
        }
    }

    private void writeArray(final Object data) throws XMLStreamException {
        if (data instanceof Object[]) {
            Object[] arr = (Object[]) data;
            for (int pos = 0; pos != arr.length; ++pos) {
                Object tmp = arr[pos];
                if (tmp instanceof byte[]) {
                    writeElementObject("Item", tmp, false);
                } else if (tmp instanceof Object[]) {
                    writeStartElement("Item", "Type",
                            String.valueOf(DataType.ARRAY.getValue()), true);
                    writeArray(tmp);
                    writeEndElement();
                } else {
                    writeElementObject("Item", tmp);
                }
            }
        }
    }

    public final void writeElementObject(final String name, final Object value)
            throws XMLStreamException {
        writeElementObject(name, value, true);
    }

    public final void writeElementObject(final String name, final Object value,
            final DataType type, final DataType uiType)
            throws XMLStreamException {
        if (type != DataType.NONE && value instanceof String) {
            if (type == DataType.OCTET_STRING) {
                if (uiType == DataType.STRING) {
                    writeElementObject(name, ((String) value).getBytes(), true);
                    return;
                } else if (uiType == DataType.OCTET_STRING) {
                    writeElementObject(name,
                            GXDLMSTranslator.hexToBytes((String) value), true);
                    return;
                }
            } else if (!(value instanceof GXDateTime)) {
                writeElementObject(name,
                        GXDLMSConverter.changeType(value, type), true);
                return;
            }
        }
        writeElementObject(name, value, true);
    }

    /**
     * Write object value to file.
     * 
     * @param name
     *            Object name.
     * @param value
     *            Object value.
     * @param skipDefaultValue
     *            Is default value serialized.
     * @throws XMLStreamException
     *             Invalid XML stream.
     */
    public final void writeElementObject(final String name, final Object value,
            final boolean skipDefaultValue) throws XMLStreamException {
        throw new UnsupportedOperationException("XML writer is not supported at the moment.");
    }

    private void writeEndElement(final boolean addSpaces)
            throws XMLStreamException {
        throw new UnsupportedOperationException("XML writer is not supported at the moment.");
    }

    /**
     * Write End Element tag.
     * 
     * @throws XMLStreamException
     *             Invalid XML stream.
     */
    public final void writeEndElement() throws XMLStreamException {
        writeEndElement(true);
    }

    /**
     * Write End document tag.
     * 
     * @throws XMLStreamException
     *             Invalid XML stream.
     */
    public final void writeEndDocument() throws XMLStreamException {
        throw new UnsupportedOperationException("XML writer is not supported at the moment.");
    }

    /**
     * Write any cached data to the stream.
     * 
     * @throws XMLStreamException
     *             Invalid XML stream.
     */
    public final void flush() throws XMLStreamException {
        throw new UnsupportedOperationException("XML writer is not supported at the moment.");
    }
}
