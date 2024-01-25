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
package gurux.dlms.plc.enums;

/**
 * PLC HDLC Source address enumerations.
 */
public enum PlcHdlcSourceAddress {
    /**
     * Initiator.
     */
    INITIATOR(0xC01);

    private int value;
    private static java.util.HashMap<Integer, PlcHdlcSourceAddress> mappings;

    private static java.util.HashMap<Integer, PlcHdlcSourceAddress>
            getMappings() {
        synchronized (PlcHdlcSourceAddress.class) {
            if (mappings == null) {
                mappings =
                        new java.util.HashMap<Integer, PlcHdlcSourceAddress>();
            }
        }
        return mappings;
    }

    PlcHdlcSourceAddress(final int mode) {
        this.value = mode;
        getMappings().put(mode, this);
    }

    /*
     * Get integer value for enum.
     */
    public final int getValue() {
        return value;
    }

    /*
     * Convert integer for enum value.
     */
    public static PlcHdlcSourceAddress forValue(final int value) {
        return getMappings().get(value);
    }
}