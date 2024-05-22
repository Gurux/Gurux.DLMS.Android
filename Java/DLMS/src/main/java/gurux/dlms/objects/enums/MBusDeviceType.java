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

package gurux.dlms.objects.enums;

/**
 * M-Bus device type enumerations.
 */
public enum MBusDeviceType {
    /**
     * Other.
     */
    OTHER,
    /**
     * Oil meter.
     */
    OIL,
    /**
     * Electricity meter.
     */
    ELECTRICITY,
    /**
     * Gas meter.
     */
    GAS,
    /**
     * Heat meter.
     */
    HEAT,
    /**
     * Steam meter.
     */
    STEAM,
    /**
     * Hot water meter.
     */
    HOT_WATER,
    /**
     * Water meter.
     */
    WATER,
    /**
     * Heat cost allocator meter.
     */
    HEAT_COST_ALLOCATOR,
    /**
     * Reserved.
     */
    RESERVED,
    /**
     * Gas mode 2 meter.
     */
    GAS_MODE_2,
    /**
     * Heat mode 2 meter.
     */
    HEAT_MODE_2,
    /**
     * Hot water mode 2 meter.
     */
    HOT_WATER_MODE_2,
    /**
     * Water mode 2 meter.
     */
    WATER_MODE_2,
    /**
     * Heat cost allocator mode 2 meter.
     */
    HEAT_COST_ALLOCATOR_MODE_2,
    /**
     * Reserver.
     */
    RESERVED2;

    /**
     * Get enemerator's integer value.
     *
     * @return Integer value of enumerator.
     */
    public int getValue() {
        return this.ordinal();
    }

    /**
     * Get enumerator from integer value.
     *
     * @param value integer value.
     * @return Enumerator value.
     */
    public static MBusDeviceType forValue(int value) {
        return values()[value];
    }
}