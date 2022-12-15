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
package gurux.dlms.enums;

/**
 * Used DLMS standard.
 */
public enum Standard {
    /**
     * Meter uses default DLMS IEC 62056 standard. https://dlms.com
     */
    DLMS(0),
    /**
     * Meter uses India DLMS standard IS 15959-2. https://www.standardsbis.in
     */
    INDIA(1),
    /**
     * Meter uses Italy DLMS standard UNI/TS 11291-11-2. https://uni.com
     */
    ITALY(2),
    /**
     * Meter uses Saudi Arabia DLMS standard.
     */
    SAUDI_ARABIA(3),
    /**
     * Meter uses IDIS DLMS standard. https://www.idis-association.com/
     */
    IDIS(4),
    /**
     * Meter uses Spain DLMS standard.
     */
    SPAIN(5);

    private int value;
    private static java.util.HashMap<Integer, Standard> mappings;

    private static java.util.HashMap<Integer, Standard> getMappings() {
        synchronized (Standard.class) {
            if (mappings == null) {
                mappings = new java.util.HashMap<Integer, Standard>();
            }
        }
        return mappings;
    }

    Standard(final int mode) {
        this.value = mode;
        getMappings().put(mode, this);
    }

    /*
     * Get integer value from enumerated value.
     */
    public final int getValue() {
        return value;
    }

    /*
     * Convert integer for enumerated value.
     */
    public static Standard forValue(final int value) {
        return getMappings().get(value);
    }

    @Override
    public String toString() {
        String str;
        Standard value = Standard.forValue(getValue());
        switch (value) {
        case DLMS:
            str = "DLMS";
            break;
        case INDIA:
            str = "INDIA";
            break;
        case ITALY:
            str = "ITALY";
            break;
        case SAUDI_ARABIA:
            str = "SAUDI_ARABIA";
            break;
        case IDIS:
            str = "IDIS";
            break;
        default:
            throw new IllegalArgumentException("Standard");
        }
        return str;
    }

    public static Standard valueOfString(final String value) {
        Standard v;
        if ("DLMS".equalsIgnoreCase(value)) {
            v = Standard.DLMS;
        } else if ("INDIA".equalsIgnoreCase(value)) {
            v = Standard.INDIA;
        } else if ("ITALY".equalsIgnoreCase(value)) {
            v = Standard.ITALY;
        } else if ("SAUDI_ARABIA".equalsIgnoreCase(value)) {
            v = Standard.SAUDI_ARABIA;
        } else if ("IDIS".equalsIgnoreCase(value)) {
            v = Standard.IDIS;
        } else {
            throw new IllegalArgumentException(value);
        }
        return v;
    }
}