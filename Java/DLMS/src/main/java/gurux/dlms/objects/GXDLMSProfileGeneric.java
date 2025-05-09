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

import android.content.Context;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


import gurux.dlms.GXByteBuffer;
import gurux.dlms.GXDLMSClient;
import gurux.dlms.GXDLMSConverter;
import gurux.dlms.GXDLMSException;
import gurux.dlms.GXDLMSServerBase;
import gurux.dlms.GXDLMSSettings;
import gurux.dlms.GXDateTime;
import gurux.dlms.GXSimpleEntry;
import gurux.dlms.GXUInt64;
import gurux.dlms.R;
import gurux.dlms.ValueEventArgs;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.ErrorCode;
import gurux.dlms.enums.ObjectType;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.internal.GXDataInfo;
import gurux.dlms.objects.enums.SortMethod;

/**
 * Online help: <br>
 * https://www.gurux.fi/Gurux.DLMS.Objects.GXDLMSProfileGeneric
 */
public class GXDLMSProfileGeneric extends GXDLMSObject implements IGXDLMSBase {
    private GXProfileGenericUpdater updater = null;

    private ArrayList<Object[]> buffer = new ArrayList<Object[]>();
    private List<Entry<GXDLMSObject, GXDLMSCaptureObject>> captureObjects;
    private long capturePeriod;
    private SortMethod sortMethod;
    private GXDLMSObject sortObject;
    private int sortObjectAttributeIndex;
    private int sortObjectDataIndex;
    private int entriesInUse;
    private int profileEntries;

    /**
     * Constructor.
     */
    public GXDLMSProfileGeneric() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param ln Logical Name of the object.
     */
    public GXDLMSProfileGeneric(final String ln) {
        this(ln, 0);
    }

    /**
     * Constructor.
     *
     * @param ln Logical Name of the object.
     * @param sn Short Name of the object.
     */
    public GXDLMSProfileGeneric(final String ln, final int sn) {
        super(ObjectType.PROFILE_GENERIC, ln, sn);
        setVersion(1);
        sortMethod = SortMethod.FIFO;
        captureObjects = new ArrayList<Entry<GXDLMSObject, GXDLMSCaptureObject>>();
    }

    /**
     * @return Data of profile generic.
     */
    public final Object[] getBuffer() {
        return buffer.toArray();
    }

    /**
     * @param value Data of profile generic.
     */
    public final void setBuffer(final Object[][] value) {
        buffer.clear();
        buffer.addAll(Arrays.asList(value));
        entriesInUse = buffer.size();
    }

    /**
     * @param value Add new row to Profile Generic data buffer.
     */
    public final void addRow(final Object[] value) {
        buffer.add(value);
    }

    /**
     * @param value Data of profile generic.
     */
    public final void addBuffer(final Object[][] value) {
        buffer.addAll(Arrays.asList(value));
    }

    /**
     * @param value Data of profile generic.
     */
    public final void addBuffer(final List<Object[]> value) {
        buffer.addAll(value);
    }

    /**
     * Clear profile generic buffer.
     */
    public final void clearBuffer() {
        buffer.clear();
    }

    /**
     * Clears the buffer.
     *
     * @param client DLMS client.
     * @return Action bytes.
     * @throws NoSuchPaddingException             No such padding exception.
     * @throws NoSuchAlgorithmException           No such algorithm exception.
     * @throws InvalidAlgorithmParameterException Invalid algorithm parameter exception.
     * @throws InvalidKeyException                Invalid key exception.
     * @throws BadPaddingException                Bad padding exception.
     * @throws IllegalBlockSizeException          Illegal block size exception.
     * @throws SignatureException                 Signature exception.
     */
    public final byte[][] reset(final GXDLMSClient client) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, SignatureException {
        return client.method(this, 1, 0, DataType.INT8);
    }

    /**
     * Copies the values of the objects to capture into the buffer by reading
     * each capture object.
     *
     * @param client DLMS client.
     * @return Action bytes.
     * @throws NoSuchPaddingException             No such padding exception.
     * @throws NoSuchAlgorithmException           No such algorithm exception.
     * @throws InvalidAlgorithmParameterException Invalid algorithm parameter exception.
     * @throws InvalidKeyException                Invalid key exception.
     * @throws BadPaddingException                Bad padding exception.
     * @throws IllegalBlockSizeException          Illegal block size exception.
     * @throws SignatureException                 Signature exception.
     */
    public final byte[][] capture(final GXDLMSClient client) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, SignatureException {
        return client.method(this, 2, 0, DataType.INT8);
    }

    /*
     * Add new capture object (column) to the profile generic.
     */
    public final void addCaptureObject(final GXDLMSObject item, final int attributeIndex,
                                       final int dataIndex) {
        if (item == null) {
            throw new RuntimeException("Invalid Object");
        }
        if (attributeIndex < 1) {
            throw new RuntimeException("Invalid attribute index");
        }
        if (dataIndex < 0) {
            throw new RuntimeException("Invalid data index");
        }
        GXDLMSCaptureObject co = new GXDLMSCaptureObject(attributeIndex, dataIndex);
        captureObjects.add(new GXSimpleEntry<GXDLMSObject, GXDLMSCaptureObject>(item, co));
    }

    /**
     * @return Captured Objects.
     */
    public final List<Entry<GXDLMSObject, GXDLMSCaptureObject>> getCaptureObjects() {
        return captureObjects;
    }

    /**
     * @return How often values are captured.
     */
    public final long getCapturePeriod() {
        return capturePeriod;
    }

    /**
     * @param value How often values are captured.
     */
    public final void setCapturePeriod(final long value) {
        capturePeriod = value;
    }

    /**
     * @return How columns are sorted.
     */
    public final SortMethod getSortMethod() {
        return sortMethod;
    }

    /**
     * @param value How columns are sorted.
     */
    public final void setSortMethod(final SortMethod value) {
        sortMethod = value;
    }

    /**
     * @return Column that is used for sorting.
     */
    public final GXDLMSObject getSortObject() {
        return sortObject;
    }

    /**
     * @param value Column that is used for sorting.
     */
    public final void setSortObject(final GXDLMSObject value) {
        sortObject = value;
    }

    /**
     * @return Attribute index of sort object.
     */
    public final int getSortObjectAttributeIndex() {
        return sortObjectAttributeIndex;
    }

    /**
     * @param value Attribute index of sort object.
     */
    public final void setSortObjectAttributeIndex(final int value) {
        sortObjectAttributeIndex = value;
    }

    /**
     * @return Data index of sort object.
     */
    public final int getSortObjectDataIndex() {
        return sortObjectDataIndex;
    }

    /**
     * @param value Data index of sort object.
     */
    public final void setSortObject(final int value) {
        sortObjectDataIndex = value;
    }

    /**
     * @return Entries (rows) in Use.
     */
    public final int getEntriesInUse() {
        return entriesInUse;
    }

    /**
     * @param value Entries (rows) in Use.
     */
    public final void setEntriesInUse(final int value) {
        entriesInUse = value;
    }

    /**
     * @return Maximum Entries (rows) count.
     */
    public final int getProfileEntries() {
        return profileEntries;
    }

    /**
     * @param value Maximum Entries (rows) count.
     */
    public final void setProfileEntries(final int value) {
        profileEntries = value;
    }

    @Override
    public final Object[] getValues() {
        return new Object[]{getLogicalName(), getBuffer(), getCaptureObjects(),
                getCapturePeriod(), getSortMethod(), getSortObject(), getEntriesInUse(),
                getProfileEntries()};
    }

    @Override
    public final byte[] invoke(final GXDLMSSettings settings, final ValueEventArgs e) {
        if (e.getIndex() == 1) {
            // Reset.
            reset();
        } else if (e.getIndex() == 2) {
            // Capture.
            try {
                capture(e.getServer());
            } catch (Exception e1) {
                e.setError(ErrorCode.HARDWARE_FAULT);
            }
        } else {
            e.setError(ErrorCode.READ_WRITE_DENIED);
        }
        return null;
    }

    /*
     * Returns collection of attributes to read. If attribute is static and
     * already read or device is returned HW error it is not returned.
     */
    @Override
    public final int[] getAttributeIndexToRead(final boolean all) {
        java.util.ArrayList<Integer> attributes = new java.util.ArrayList<Integer>();
        // LN is static and read only once.
        if (all || getLogicalName() == null || getLogicalName().compareTo("") == 0) {
            attributes.add(1);
        }
        // CaptureObjects
        if (all || !isRead(3)) {
            attributes.add(3);
        }
        // CapturePeriod
        if (all || !isRead(4)) {
            attributes.add(4);
        }
        // SortMethod
        if (all || !isRead(5)) {
            attributes.add(5);
        }
        // SortObject
        if (all || !isRead(6)) {
            attributes.add(6);
        }
        // Buffer
        if (all || !isRead(2)) {
            attributes.add(2);
        }
        // EntriesInUse
        if (all || !isRead(7)) {
            attributes.add(7);
        }
        // ProfileEntries
        if (all || !isRead(8)) {
            attributes.add(8);
        }
        return GXDLMSObjectHelpers.toIntArray(attributes);
    }

    /*
     * Returns amount of attributes.
     */
    @Override
    public final int getAttributeCount() {
        return 8;
    }

    /*
     * Returns amount of methods.
     */
    @Override
    public final int getMethodCount() {
        return 2;
    }

    /**
     * @return Returns captured columns.
     */
    private byte[] getColumns() {
        int cnt = captureObjects.size();
        GXByteBuffer data = new GXByteBuffer();
        data.setUInt8(DataType.ARRAY.getValue());
        // Add count
        GXCommon.setObjectCount(cnt, data);
        // CHECKSTYLE:OFF
        for (Entry<GXDLMSObject, GXDLMSCaptureObject> it : captureObjects) {
            // CHECKSTYLE:ON
            data.setUInt8(DataType.STRUCTURE.getValue());
            data.setUInt8(4); // Count
            // ClassID
            GXCommon.setData(null, data, DataType.UINT16, it.getKey().getObjectType().getValue());
            // LN
            GXCommon.setData(null, data, DataType.OCTET_STRING,
                    GXCommon.logicalNameToBytes(it.getKey().getLogicalName()));
            // Attribute Index
            GXCommon.setData(null, data, DataType.INT8, it.getValue().getAttributeIndex());
            // Data Index
            GXCommon.setData(null, data, DataType.UINT16, it.getValue().getDataIndex());
        }
        return data.array();
    }

    private byte[] getData(final GXDLMSSettings settings, final ValueEventArgs e,
                           final Object[] table, final List<Entry<GXDLMSObject, GXDLMSCaptureObject>> columns) {
        GXByteBuffer data = new GXByteBuffer();
        if (settings.getIndex() == 0) {
            data.setUInt8((byte) DataType.ARRAY.getValue());
            if (e.getRowEndIndex() != 0) {
                GXCommon.setObjectCount((int) (e.getRowEndIndex() - e.getRowBeginIndex()), data);
            } else {
                GXCommon.setObjectCount(table.length, data);
            }
        }

        DataType[] types = new DataType[captureObjects.size()];
        int pos = 0;
        for (Entry<GXDLMSObject, GXDLMSCaptureObject> it : captureObjects) {
            types[pos] = it.getKey().getDataType(it.getValue().getAttributeIndex());
            ++pos;
        }
        DataType tp;
        for (Object row : table) {
            Object[] items = (Object[]) row;
            data.setUInt8(DataType.STRUCTURE.getValue());
            if (columns == null || columns.isEmpty()) {
                GXCommon.setObjectCount(items.length, data);
            } else {
                GXCommon.setObjectCount(columns.size(), data);
            }
            pos = 0;
            for (Object value : items) {
                if (columns == null || columns.contains(captureObjects.get(pos))) {
                    tp = types[pos];
                    if (tp == DataType.NONE) {
                        tp = GXDLMSConverter.getDLMSDataType(value);
                        types[pos] = tp;
                    }
                    GXCommon.setData(settings, data, tp, value);
                }
                ++pos;
            }
            settings.setIndex(settings.getIndex() + 1);
        }
        if (e.getRowEndIndex() != 0) {
            e.setRowBeginIndex(e.getRowBeginIndex() + table.length);
        } else {
            settings.setIndex(0);
        }
        return data.array();

    }

    /**
     * Get selected columns.
     *
     * @param cols selected columns.
     * @return Selected columns.
     */
    private List<Entry<GXDLMSObject, GXDLMSCaptureObject>> getColumns(final List<?> cols) {
        List<Entry<GXDLMSObject, GXDLMSCaptureObject>> columns = null;
        if (cols != null && cols.size() != 0) {
            columns = new ArrayList<Entry<GXDLMSObject, GXDLMSCaptureObject>>();
            for (Object it : cols) {
                List<?> tmp = (List<?>) it;
                ObjectType ot = ObjectType.forValue(((Number) tmp.get(0)).intValue());
                String ln = GXCommon.toLogicalName((byte[]) tmp.get(1));
                byte attributeIndex = ((Number) tmp.get(2)).byteValue();
                int dataIndex = ((Number) tmp.get(3)).intValue();
                // Find columns and update only them.
                // CHECKSTYLE:OFF
                for (Entry<GXDLMSObject, GXDLMSCaptureObject> c : captureObjects) {
                    // CHECKSTYLE:ON
                    if (c.getKey().getObjectType() == ot
                            && c.getValue().getAttributeIndex() == attributeIndex
                            && c.getValue().getDataIndex() == dataIndex
                            && c.getKey().getLogicalName().compareTo(ln) == 0) {
                        columns.add(c);
                        break;
                    }
                }
            }
        } else {
            // Return all rows.
            List<Entry<GXDLMSObject, GXDLMSCaptureObject>> colums =
                    new ArrayList<Entry<GXDLMSObject, GXDLMSCaptureObject>>();
            colums.addAll(captureObjects);
            return colums;
        }
        return columns;
    }

    /**
     * Get selected columns from parameters.
     *
     * @param selector   Is read by entry or range.
     * @param parameters Received parameters where columns information is found.
     * @return Selected columns.
     */
    public final List<Entry<GXDLMSObject, GXDLMSCaptureObject>>
    getSelectedColumns(final int selector, final Object parameters) {
        if (selector == 0) {
            // Return all rows.
            List<Entry<GXDLMSObject, GXDLMSCaptureObject>> colums =
                    new ArrayList<Entry<GXDLMSObject, GXDLMSCaptureObject>>();
            colums.addAll(captureObjects);
            return colums;
        } else if (selector == 1) {
            return getColumns((List<?>) ((List<?>) parameters).get(3));
        } else if (selector == 2) {
            List<?> arr = (List<?>) parameters;
            int colStart = 1;
            int colCount = 0;
            if (arr.size() > 2) {
                colStart = ((Number) arr.get(2)).intValue();
            }
            if (arr.size() > 3) {
                colCount = ((Number) arr.get(3)).intValue();
            } else if (colStart != 1) {
                colCount = captureObjects.size();
            }
            if (colStart != 1 || colCount != 0) {
                return captureObjects.subList(colStart - 1, colStart + colCount - 1);
            }
            // Return all rows.
            List<Entry<GXDLMSObject, GXDLMSCaptureObject>> colums =
                    new ArrayList<Entry<GXDLMSObject, GXDLMSCaptureObject>>();
            colums.addAll(captureObjects);
            return colums;
        } else {
            throw new IllegalArgumentException("Invalid selector.");
        }
    }

    final byte[] getProfileGenericData(final GXDLMSSettings settings, final ValueEventArgs e) {
        List<Entry<GXDLMSObject, GXDLMSCaptureObject>> columns = null;
        // If all data is read.
        if (e.getSelector() == 0 || e.getParameters() == null || e.getRowEndIndex() != 0) {
            return getData(settings, e, getBuffer(), columns);
        }
        List<?> arr = (List<?>) e.getParameters();
        columns = getSelectedColumns(e.getSelector(), arr);
        ArrayList<Object[]> table = new ArrayList<Object[]>();
        // Read by range
        if (e.getSelector() == 1) {
            GXDataInfo info = new GXDataInfo();
            info.setType(DataType.DATETIME);
            java.util.Date start = ((GXDateTime) GXCommon.getData(settings,
                    new GXByteBuffer((byte[]) arr.get(1)), info)).getMeterCalendar().getTime();
            info.clear();
            info.setType(DataType.DATETIME);
            java.util.Date end = ((GXDateTime) GXCommon.getData(settings,
                    new GXByteBuffer((byte[]) arr.get(2)), info)).getMeterCalendar().getTime();
            for (Object row : getBuffer()) {
                java.util.Date tm;
                Object tmp = ((Object[]) row)[getSortObjectDataIndex()];
                if (tmp instanceof GXDateTime) {
                    tm = ((GXDateTime) tmp).getMeterCalendar().getTime();
                } else {
                    tm = (java.util.Date) tmp;
                }
                if (tm.compareTo(start) >= 0 && tm.compareTo(end) <= 0) {
                    table.add((Object[]) row);
                }
            }
        } else if (e.getSelector() == 2) {
            // Read by entry.
            int start = ((Number) arr.get(0)).intValue();
            if (start == 0) {
                start = 1;
            }
            int count = ((Number) arr.get(1)).intValue();
            if (count == 0) {
                count = getBuffer().length;
            }
            if (start + count > getBuffer().length + 1) {
                count = getBuffer().length;
            }
            // Starting index is 1.
            for (int pos = 0; pos < count; ++pos) {
                if (pos + start - 1 == getBuffer().length) {
                    break;
                }
                table.add((Object[]) getBuffer()[start + pos - 1]);
            }
        } else {
            throw new IllegalArgumentException("Invalid selector.");
        }
        return getData(settings, e, table.toArray(), columns);
    }

    @Override
    public final DataType getDataType(final int index) {
        if (index == 1) {
            return DataType.OCTET_STRING;
        }
        if (index == 2) {
            return DataType.ARRAY;
        }
        if (index == 3) {
            return DataType.ARRAY;
        }
        if (index == 4) {
            return DataType.UINT32;
        }
        if (index == 5) {
            return DataType.ENUM;
        }
        if (index == 6) {
            return DataType.ARRAY;
        }
        if (index == 7) {
            return DataType.UINT32;
        }
        if (index == 8) {
            return DataType.UINT32;
        }
        throw new IllegalArgumentException("getDataType failed. Invalid attribute index.");
    }

    /*
     * Returns value of given attribute.
     */
    @Override
    public final Object getValue(final GXDLMSSettings settings, final ValueEventArgs e) {
        if (e.getIndex() == 1) {
            return GXCommon.logicalNameToBytes(getLogicalName());
        }
        if (e.getIndex() == 2) {
            return getProfileGenericData(settings, e);
        }
        if (e.getIndex() == 3) {
            return getColumns();
        }
        if (e.getIndex() == 4) {
            return getCapturePeriod();
        }
        if (e.getIndex() == 5) {
            return getSortMethod().getValue();
        }
        if (e.getIndex() == 6) {
            GXByteBuffer data = new GXByteBuffer();
            data.setUInt8((byte) DataType.STRUCTURE.getValue());
            data.setUInt8((byte) 4); // Count
            if (sortObject == null) {
                // ClassID
                GXCommon.setData(settings, data, DataType.UINT16, 0);
                // LN
                GXCommon.setData(settings, data, DataType.OCTET_STRING, new byte[6]);
                // Selected Attribute Index
                GXCommon.setData(settings, data, DataType.INT8, 0);
                // Selected Data Index
                GXCommon.setData(settings, data, DataType.UINT16, 0);
            } else {
                // ClassID
                GXCommon.setData(settings, data, DataType.UINT16,
                        sortObject.getObjectType().getValue());
                // LN
                GXCommon.setData(settings, data, DataType.OCTET_STRING,
                        GXCommon.logicalNameToBytes(sortObject.getLogicalName()));
                // Attribute Index
                GXCommon.setData(settings, data, DataType.INT8, sortObjectAttributeIndex);
                // Data Index
                GXCommon.setData(settings, data, DataType.UINT16, sortObjectDataIndex);
            }
            return data.array();
        }
        if (e.getIndex() == 7) {
            return getEntriesInUse();
        }
        if (e.getIndex() == 8) {
            return getProfileEntries();
        }
        e.setError(ErrorCode.READ_WRITE_DENIED);
        return null;
    }

    /**
     * Set value of given attribute.
     */
    @Override
    public final void setValue(final GXDLMSSettings settings, final ValueEventArgs e) {
        if (e.getIndex() == 1) {
            setLogicalName(GXCommon.toLogicalName(e.getValue()));
        } else if (e.getIndex() == 2) {
            setBuffer(e);
        } else if (e.getIndex() == 3) {
            captureObjects.clear();
            buffer.clear();
            entriesInUse = 0;
            if (e.getValue() != null) {
                for (Object it : (List<?>) e.getValue()) {
                    List<?> tmp = (List<?>) it;
                    if (tmp.size() != 4) {
                        throw new GXDLMSException("Invalid structure format.");
                    }
                    ObjectType type = ObjectType.forValue(((Number) tmp.get(0)).intValue());
                    String ln = GXCommon.toLogicalName((byte[]) tmp.get(1));
                    GXDLMSObject obj = null;
                    if (settings != null && settings.getObjects() != null) {
                        obj = settings.getObjects().findByLN(type, ln);
                    }
                    if (obj == null) {
                        obj = gurux.dlms.GXDLMSClient.createObject(type);
                        obj.setLogicalName(ln);
                    }
                    int index;
                    try {
                        index = ((Byte) tmp.get(2)) & 0xFF;
                    } catch (Exception ex) {
                        index = ((Number) tmp.get(2)).intValue();
                    }
                    addCaptureObject(obj, index, ((Number) tmp.get(3)).intValue());
                }
            }
        } else if (e.getIndex() == 4) {
            // Any write access to one of the attributes will automatically call
            // a reset and this call will propagate to all other profiles
            // capturing this profile.
            if (settings != null && settings.isServer()) {
                reset();
            }

            if (e.getValue() == null) {
                capturePeriod = 0;
            } else {
                capturePeriod = ((Number) e.getValue()).longValue();
            }

        } else if (e.getIndex() == 5) {
            // Any write access to one of the attributes will automatically call
            // a reset and this call will propagate to all other profiles
            // capturing this profile.
            if (settings != null && settings.isServer()) {
                reset();
            }
            if (e.getValue() == null) {
                sortMethod = SortMethod.FIFO;
            } else {
                sortMethod = SortMethod.forValue(((Number) e.getValue()).intValue());
                if (sortMethod == null) {
                    sortMethod = SortMethod.LIFO;
                    throw new IllegalArgumentException("Invalid sort method.");
                }
            }

        } else if (e.getIndex() == 6) {
            // Any write access to one of the attributes will automatically call
            // a reset and this call will propagate to all other profiles
            // capturing this profile.
            if (settings != null && settings.isServer()) {
                reset();
            }

            if (e.getValue() == null) {
                sortObject = null;
            } else {
                List<?> tmp = (List<?>) e.getValue();
                if (tmp.size() != 4) {
                    throw new IllegalArgumentException("Invalid structure format.");
                }
                ObjectType type = ObjectType.forValue(((Number) tmp.get(0)).intValue());
                if (type == ObjectType.NONE) {
                    sortObject = null;
                    sortObjectAttributeIndex = 0;
                    sortObjectDataIndex = 0;
                } else {
                    String ln = GXCommon.toLogicalName((byte[]) tmp.get(1));
                    int attributeIndex = ((Number) tmp.get(2)).intValue();
                    int dataIndex = ((Number) tmp.get(3)).intValue();
                    sortObject = settings.getObjects().findByLN(type, ln);
                    if (sortObject == null) {
                        sortObject = gurux.dlms.GXDLMSClient.createObject(type);
                        sortObject.setLogicalName(ln);
                    }
                    sortObjectAttributeIndex = attributeIndex;
                    sortObjectDataIndex = dataIndex;
                }
            }
        } else if (e.getIndex() == 7) {
            if (e.getValue() == null) {
                entriesInUse = 0;
            } else {
                entriesInUse = ((Number) e.getValue()).intValue();
            }
        } else if (e.getIndex() == 8) {
            // Any write access to one of the attributes will automatically call
            // a reset and this call will propagate to all other profiles
            // capturing this profile.
            if (settings != null && settings.isServer()) {
                reset();
            }

            if (e.getValue() == null) {
                profileEntries = 0;
            } else {
                profileEntries = ((Number) e.getValue()).intValue();
            }
        } else {
            e.setError(ErrorCode.READ_WRITE_DENIED);
        }
    }

    /**
     * Update buffer.
     *
     * @param e Received data.
     */
    @SuppressWarnings("unchecked")
    private void setBuffer(final ValueEventArgs e) {
        List<Entry<GXDLMSObject, GXDLMSCaptureObject>> cols =
                (List<Entry<GXDLMSObject, GXDLMSCaptureObject>>) e.getParameters();
        if (cols == null) {
            cols = captureObjects;
        }
        if (cols == null || cols.size() == 0) {
            throw new RuntimeException("Read capture objects first.");
        }
        if (e.getValue() != null) {
            java.util.Calendar lastDate = java.util.Calendar.getInstance();
            DataType[] types = new DataType[cols.size()];
            int colIndex = -1;
            // CHECKSTYLE:OFF
            for (Entry<GXDLMSObject, GXDLMSCaptureObject> it : cols) {
                // CHECKSTYLE:ON
                types[++colIndex] = it.getKey().getUIDataType(it.getValue().getAttributeIndex());
            }
            for (Object it : (List<?>) e.getValue()) {
                List<Object> row = (List<Object>) it;
                if (row.size() != cols.size()) {
                    throw new RuntimeException("Number of columns do not match.");
                }
                for (colIndex = 0; colIndex < row.size(); ++colIndex) {
                    Object data = row.get(colIndex);
                    DataType type = types[colIndex];
                    if (type != DataType.NONE && type != null && data instanceof byte[]) {
                        data = GXDLMSClient.changeType((byte[]) data, type, e.getSettings());
                        if (data instanceof GXDateTime) {
                            GXDateTime dt = (GXDateTime) data;
                            lastDate.setTime(dt.getMeterCalendar().getTime());
                        }
                        row.set(colIndex, data);
                    } else if (type == DataType.DATETIME && data == null && capturePeriod != 0) {
                        if (lastDate.getTimeInMillis() == 0 && !buffer.isEmpty()) {
                            lastDate.setTime(((GXDateTime) buffer.get(buffer.size() - 1)[colIndex])
                                    .getMeterCalendar().getTime());
                        }
                        if (lastDate.getTimeInMillis() != 0) {
                            lastDate.add(java.util.Calendar.SECOND, (int) capturePeriod);
                            row.set(colIndex, new GXDateTime(lastDate.getTime()));
                        }
                    } else if (type == DataType.DATETIME && row.get(colIndex) instanceof Number) {
                        if (row.get(colIndex) instanceof GXUInt64) {
                            row.set(colIndex, GXDateTime.fromHighResolutionTime(
                                    ((Number) row.get(colIndex)).longValue()));
                        } else {
                            row.set(colIndex, GXDateTime
                                    .fromUnixTime(((Number) row.get(colIndex)).longValue()));
                        }
                    }

                    Entry<GXDLMSObject, GXDLMSCaptureObject> item = cols.get(colIndex);
                    if (item.getKey() instanceof GXDLMSRegister
                            && item.getValue().getAttributeIndex() == 2) {
                        double scaler = ((GXDLMSRegister) item.getKey()).getScaler();
                        if (scaler != 1 && data != null) {
                            try {
                                data = ((Number) data).doubleValue() * scaler;
                                row.set(colIndex, data);
                            } catch (Exception ex) {
                                Logger.getLogger(GXDLMSProfileGeneric.class.getName()).log(
                                        Level.SEVERE, "Scalar failed for: {0}",
                                        item.getKey().getLogicalName());
                                // Skip error
                            }
                        }
                    } else if (item.getKey() instanceof GXDLMSDemandRegister
                            && (item.getValue().getAttributeIndex() == 2
                            || item.getValue().getAttributeIndex() == 3)) {
                        double scaler = ((GXDLMSDemandRegister) item.getKey()).getScaler();
                        if (scaler != 1 && data != null) {
                            try {
                                data = ((Number) data).doubleValue() * scaler;
                                row.set(colIndex, data);
                            } catch (Exception ex) {
                                Logger.getLogger(GXDLMSProfileGeneric.class.getName()).log(
                                        Level.SEVERE, "Scalar failed for: {0}",
                                        item.getKey().getLogicalName());
                                // Skip error
                            }
                        }
                    }
                }
                buffer.add(row.toArray(new Object[0]));
            }
            if (e.getSettings().isServer()) {
                entriesInUse = buffer.size();
            }
        }
    }

    /**
     * Clears the buffer.
     */
    public final void reset() {
        synchronized (this) {
            buffer.clear();
            entriesInUse = 0;
        }
    }

    /*
     * Copies the values of the objects to capture into the buffer by reading
     * capture objects.
     */
    public final void capture(final Object server) throws Exception {
        synchronized (this) {
            GXDLMSServerBase srv = (GXDLMSServerBase) server;
            Object[] values = new Object[captureObjects.size()];
            int pos = 0;
            ValueEventArgs[] args =
                    new ValueEventArgs[]{new ValueEventArgs(srv, this, 2, 0, null)};
            srv.notifyPreGet(args);
            if (!args[0].getHandled()) {
                // CHECKSTYLE:OFF
                for (Entry<GXDLMSObject, GXDLMSCaptureObject> it : captureObjects) {
                    // CHECKSTYLE:ON
                    values[pos] = it.getKey().getValues()[it.getValue().getAttributeIndex() - 1];
                    ++pos;
                }
                synchronized (this) {
                    // Remove first items if buffer is full.
                    if (getProfileEntries() != 0 && getProfileEntries() == getBuffer().length) {
                        --entriesInUse;
                        buffer.remove(0);
                    }
                    buffer.add(values);
                    ++entriesInUse;
                }
            }
            srv.notifyPostGet(args);
            srv.notifyAction(args);
            srv.notifyPostAction(args);
        }
    }

    @Override
    public final void start(final GXDLMSServerBase server) {
        if (getCapturePeriod() > 0) {
            updater = new GXProfileGenericUpdater(server, this);
            updater.start();
        }
    }

    @Override
    public final void stop(final GXDLMSServerBase server) throws InterruptedException {
        if (updater != null) {
            updater.getReceivedEvent().set();
            updater.join(10000);
        }
    }

    @Override
    public final void load(final GXXmlReader reader) throws XMLStreamException {
        buffer.clear();
        if (reader.isStartElement("Buffer", true)) {
            while (reader.isStartElement("Row", true)) {
                List<Object> row = new ArrayList<Object>();
                while (reader.isStartElement("Cell", false)) {
                    row.add(reader.readElementContentAsObject("Cell", null, null, 0));
                }
                this.addRow(row.toArray(new Object[row.size()]));
            }
            reader.readEndElement("Buffer");
        }
        captureObjects.clear();
        if (reader.isStartElement("CaptureObjects", true)) {
            while (reader.isStartElement("Item", true)) {
                ObjectType ot = ObjectType.forValue(reader.readElementContentAsInt("ObjectType"));
                String ln = reader.readElementContentAsString("LN");
                int ai = reader.readElementContentAsInt("Attribute");
                int di = reader.readElementContentAsInt("Data");
                GXDLMSCaptureObject co = new GXDLMSCaptureObject(ai, di);
                GXDLMSObject obj = reader.getObjects().findByLN(ot, ln);
                if (obj == null) {
                    obj = GXDLMSClient.createObject(ot);
                    obj.setLogicalName(ln);
                }
                GXSimpleEntry<GXDLMSObject, GXDLMSCaptureObject> o =
                        new GXSimpleEntry<GXDLMSObject, GXDLMSCaptureObject>(obj, co);
                captureObjects.add(o);
            }
            reader.readEndElement("CaptureObjects");
        }
        capturePeriod = reader.readElementContentAsInt("CapturePeriod");
        sortMethod = SortMethod.forValue(reader.readElementContentAsInt("SortMethod"));
        if (reader.isStartElement("SortObject", true)) {
            capturePeriod = reader.readElementContentAsInt("CapturePeriod");
            ObjectType ot = ObjectType.forValue(reader.readElementContentAsInt("ObjectType"));
            String ln = reader.readElementContentAsString("LN");
            sortObject = reader.getObjects().findByLN(ot, ln);
            reader.readEndElement("SortObject");
        }
        entriesInUse = reader.readElementContentAsInt("EntriesInUse");
        profileEntries = reader.readElementContentAsInt("ProfileEntries");
    }

    @Override
    public final void save(final GXXmlWriter writer) throws XMLStreamException {
        if (buffer != null && writer.isSerialized(ObjectType.PROFILE_GENERIC, 2)) {
            writer.writeStartElement("Buffer");
            for (Object[] row : buffer) {
                writer.writeStartElement("Row");
                for (Object it : row) {
                    writer.writeElementObject("Cell", it);
                }
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }
        if (captureObjects != null && writer.isSerialized(ObjectType.PROFILE_GENERIC, 3)) {
            writer.writeStartElement("CaptureObjects");
            for (Entry<GXDLMSObject, GXDLMSCaptureObject> it : captureObjects) {
                writer.writeStartElement("Item");
                writer.writeElementString("ObjectType", it.getKey().getObjectType().getValue());
                writer.writeElementString("LN", it.getKey().getLogicalName());
                writer.writeElementString("Attribute", it.getValue().getAttributeIndex());
                writer.writeElementString("Data", it.getValue().getDataIndex());
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }
        writer.writeElementString("CapturePeriod", capturePeriod);
        writer.writeElementString("SortMethod", sortMethod.getValue());
        if (sortObject != null) {
            writer.writeStartElement("SortObject");
            writer.writeElementString("ObjectType", sortObject.getObjectType().getValue());
            writer.writeElementString("LN", sortObject.getLogicalName());
            writer.writeEndElement();
        }
        writer.writeElementString("EntriesInUse", entriesInUse);
        writer.writeElementString("ProfileEntries", profileEntries);
    }

    @Override
    public final void postLoad(final GXXmlReader reader) {
    }

    @Override
    public String[] getNames(final Context context) {
        return new String[]{context.getString(R.string.logical_name), "Buffer", "CaptureObjects", "Capture Period",
                "Sort Method", "Sort Object", "Entries In Use", "Profile Entries"};
    }

    @Override
    public String[] getMethodNames(final Context context) {
        return new String[]{"Reset", "Capture"};
    }

}