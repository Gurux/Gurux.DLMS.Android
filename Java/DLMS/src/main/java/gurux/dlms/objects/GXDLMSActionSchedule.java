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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gurux.dlms.GXByteBuffer;
import gurux.dlms.GXDLMSClient;
import gurux.dlms.GXDLMSSettings;
import gurux.dlms.GXDate;
import gurux.dlms.GXDateTime;
import gurux.dlms.GXTime;
import gurux.dlms.R;
import gurux.dlms.ValueEventArgs;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.DateTimeSkips;
import gurux.dlms.enums.ErrorCode;
import gurux.dlms.enums.ObjectType;
import gurux.dlms.enums.Standard;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.objects.enums.SingleActionScheduleType;

/**
 * Online help: <br>
 * https://www.gurux.fi/Gurux.DLMS.Objects.GXDLMSActionSchedule
 */
public class GXDLMSActionSchedule extends GXDLMSObject implements IGXDLMSBase {
    /**
     * Script to execute.
     */
    private GXDLMSScriptTable target;
    private int executedScriptSelector;
    private SingleActionScheduleType type;
    private GXDateTime[] executionTime;

    /**
     * Constructor.
     */
    public GXDLMSActionSchedule() {
        this("0.0.15.0.0.255", 0);
    }

    /**
     * Constructor.
     *
     * @param ln Logical Name of the object.
     */
    public GXDLMSActionSchedule(final String ln) {
        this(ln, 0);
    }

    /**
     * Constructor.
     *
     * @param ln Logical Name of the object.
     * @param sn Short Name of the object.
     */
    public GXDLMSActionSchedule(final String ln, final int sn) {
        super(ObjectType.ACTION_SCHEDULE, ln, sn);
        type = SingleActionScheduleType.SingleActionScheduleType1;
    }

    /**
     * @return Script to execute.
     */
    public final GXDLMSScriptTable getTarget() {
        return target;
    }

    /**
     * @param value Script to execute.
     */
    public final void setTarget(final GXDLMSScriptTable value) {
        target = value;
    }

    public final int getExecutedScriptSelector() {
        return executedScriptSelector;
    }

    public final void setExecutedScriptSelector(final int value) {
        executedScriptSelector = value;
    }

    public final SingleActionScheduleType getType() {
        return type;
    }

    public final void setType(final SingleActionScheduleType value) {
        if (value == null) {
            throw new IllegalArgumentException("Invalid SingleActionScheduleType");
        }
        type = value;
    }

    public final GXDateTime[] getExecutionTime() {
        return executionTime;
    }

    public final void setExecutionTime(final GXDateTime[] value) {
        executionTime = value;
    }

    @Override
    public final Object[] getValues() {
        String str;
        if (target != null) {
            str = target.getLogicalName();
        } else {
            str = "0.0.0.0.0.0";
        }
        return new Object[]{getLogicalName(), str + " " + executedScriptSelector, getType(),
                getExecutionTime()};
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
        // ExecutedScriptLogicalName is static and read only once.
        if (all || !isRead(2)) {
            attributes.add(2);
        }
        // Type is static and read only once.
        if (all || !isRead(3)) {
            attributes.add(3);
        }
        // ExecutionTime is static and read only once.
        if (all || !isRead(4)) {
            attributes.add(4);
        }
        return GXDLMSObjectHelpers.toIntArray(attributes);
    }

    /*
     * Returns amount of attributes.
     */
    @Override
    public final int getAttributeCount() {
        return 4;
    }

    /*
     * Returns amount of methods.
     */
    @Override
    public final int getMethodCount() {
        return 0;
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
            return DataType.ENUM;
        }
        if (index == 4) {
            return DataType.ARRAY;
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
            String str;
            if (target == null) {
                str = "0.0.0.0.0.0";
            } else {
                str = target.getLogicalName();
            }
            GXByteBuffer stream = new GXByteBuffer();
            stream.setUInt8(DataType.STRUCTURE.getValue());
            stream.setUInt8(2);
            GXCommon.setData(settings, stream, DataType.OCTET_STRING,
                    GXCommon.logicalNameToBytes(str));
            GXCommon.setData(settings, stream, DataType.UINT16, executedScriptSelector);
            return stream.array();
        }
        if (e.getIndex() == 3) {
            return getType().getValue();
        }
        if (e.getIndex() == 4) {
            GXByteBuffer bb = new GXByteBuffer();
            bb.setUInt8(DataType.ARRAY.getValue());
            if (getExecutionTime() == null) {
                GXCommon.setObjectCount(0, bb);
            } else {
                GXCommon.setObjectCount(getExecutionTime().length, bb);
                for (GXDateTime it : getExecutionTime()) {
                    bb.setUInt8(DataType.STRUCTURE.getValue());
                    bb.setUInt8(2); // Count
                    if (settings != null && settings.getStandard() == Standard.SAUDI_ARABIA) {
                        // Time
                        GXCommon.setData(settings, bb, DataType.TIME, new GXTime(it));
                        // Date
                        GXCommon.setData(settings, bb, DataType.DATE, new GXDate(it));
                    } else {
                        // Time
                        GXCommon.setData(settings, bb, DataType.OCTET_STRING, new GXTime(it));
                        // Date
                        GXCommon.setData(settings, bb, DataType.OCTET_STRING, new GXDate(it));
                    }
                }
            }
            return bb.array();
        }
        e.setError(ErrorCode.READ_WRITE_DENIED);
        return null;
    }

    /*
     * Set value of given attribute.
     */
    @Override
    public final void setValue(final GXDLMSSettings settings, final ValueEventArgs e) {
        if (e.getIndex() == 1) {
            setLogicalName(GXCommon.toLogicalName(e.getValue()));
        } else if (e.getIndex() == 2) {
            String ln = GXCommon.toLogicalName(((List<?>) e.getValue()).get(0));
            target = (GXDLMSScriptTable) settings.getObjects().findByLN(ObjectType.SCRIPT_TABLE,
                    ln);
            if (target == null) {
                target = new GXDLMSScriptTable(ln);
            }
            setExecutedScriptSelector(((Number) ((List<?>) e.getValue()).get(1)).intValue());
        } else if (e.getIndex() == 3) {
            setType(SingleActionScheduleType.forValue(((Number) e.getValue()).intValue()));
        } else if (e.getIndex() == 4) {
            setExecutionTime(null);
            if (e.getValue() != null) {
                java.util.ArrayList<GXDateTime> items = new java.util.ArrayList<GXDateTime>();
                for (Object it : (List<?>) e.getValue()) {
                    GXDateTime time =
                            (GXDateTime) GXDLMSClient.changeType((byte[]) ((List<?>) it).get(0),
                                    DataType.TIME, e.getSettings());
                    time.setSkip(DateTimeSkips.forValue(DateTimeSkips.toInteger(time.getSkip())
                            & ~(DateTimeSkips.YEAR.getValue() | DateTimeSkips.MONTH.getValue()
                            | DateTimeSkips.DAY.getValue()
                            | DateTimeSkips.DAY_OF_WEEK.getValue())));
                    GXDateTime date =
                            (GXDateTime) GXDLMSClient.changeType((byte[]) ((List<?>) it).get(1),
                                    DataType.DATE, e.getSettings());
                    date.setSkip(DateTimeSkips.forValue(DateTimeSkips.toInteger(date.getSkip())
                            & ~(DateTimeSkips.HOUR.getValue() | DateTimeSkips.MINUTE.getValue()
                            | DateTimeSkips.SECOND.getValue()
                            | DateTimeSkips.MILLISECOND.getValue())));
                    GXDateTime tmp = new GXDateTime(date);
                    tmp.getMeterCalendar().add(Calendar.HOUR_OF_DAY,
                            time.getMeterCalendar().get(Calendar.HOUR_OF_DAY));
                    tmp.getMeterCalendar().add(Calendar.MINUTE,
                            time.getMeterCalendar().get(Calendar.MINUTE));
                    tmp.getMeterCalendar().add(Calendar.SECOND,
                            time.getMeterCalendar().get(Calendar.SECOND));
                    tmp.getSkip().addAll(date.getSkip());
                    tmp.getSkip().addAll(time.getSkip());
                    items.add(tmp);
                }
                setExecutionTime(items.toArray(new GXDateTime[items.size()]));
            }
        } else {
            e.setError(ErrorCode.READ_WRITE_DENIED);
        }
    }

    @Override
    public final void load(final GXXmlReader reader) throws XMLStreamException {
        ObjectType ot = ObjectType.forValue(reader.readElementContentAsInt("ObjectType"));
        String ln = reader.readElementContentAsString("LN");
        if (ot != ObjectType.NONE && ln != null) {
            target = (GXDLMSScriptTable) reader.getObjects().findByLN(ot, ln);
            // if object is not load yet.
            if (target == null) {
                target = new GXDLMSScriptTable(ln);
            }
        }
        executedScriptSelector = reader.readElementContentAsInt("ExecutedScriptSelector");
        type = SingleActionScheduleType.forValue(reader.readElementContentAsInt("Type"));
        List<GXDateTime> list = new ArrayList<GXDateTime>();
        if (reader.isStartElement("ExecutionTime", true)) {
            while (reader.isStartElement("Time", false)) {
                GXDateTime it = reader.readElementContentAsDateTime("Time");
                list.add(it);
            }
            reader.readEndElement("ExecutionTime");
        }
        executionTime = list.toArray(new GXDateTime[list.size()]);
    }

    @Override
    public final void save(final GXXmlWriter writer) throws XMLStreamException {
        if (target != null) {
            writer.writeElementString("ObjectType", target.getObjectType().getValue());
            writer.writeElementString("LN", target.getLogicalName());
        }
        writer.writeElementString("ExecutedScriptSelector", executedScriptSelector);
        writer.writeElementString("Type", type.getValue());
        if (executionTime != null) {
            writer.writeStartElement("ExecutionTime");
            for (GXDateTime it : executionTime) {
                writer.writeElementString("Time", it);
            }
            writer.writeEndElement();
        }
    }

    @Override
    public final void postLoad(final GXXmlReader reader) {
        // Upload target after load.
        if (target != null) {
            GXDLMSScriptTable t = (GXDLMSScriptTable) reader.getObjects()
                    .findByLN(ObjectType.SCRIPT_TABLE, target.getLogicalName());
            if (t != null && target != t) {
                target = t;
            }
        }
    }

    @Override
    public String[] getNames(final Context context) {
        return new String[]{context.getString(R.string.logical_name), "Executed Script Logical Name", "Type",
                "Execution Time"};
    }

    @Override
    public String[] getMethodNames(final Context context) {
        return new String[0];
    }
}