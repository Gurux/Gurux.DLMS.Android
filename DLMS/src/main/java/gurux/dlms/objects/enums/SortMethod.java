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
 * Sort methods.
 */
public enum SortMethod {
    /**
     * First in first out When circle buffer is full first item is removed.
     */
    FIFO(1),

    /**
     * Last in first out. When circle buffer is full last item is removed.
     */
    LIFO(2),

    /**
     * Largest is first.
     */
    LARGEST(3),

    /**
     * Smallest is first.
     */
    SMALLEST(4),

    /**
     * Nearest to zero is first.
     */
    NEAREST_TO_ZERO(5),

    /**
     * Farest from zero is first.
     */
    FAREST_FROM_ZERO(6);

    private int intValue;
    private static java.util.HashMap<Integer, SortMethod> mappings;

    private static java.util.HashMap<Integer, SortMethod> getMappings() {
        synchronized (SortMethod.class) {
            if (mappings == null) {
                mappings = new java.util.HashMap<Integer, SortMethod>();
            }
        }
        return mappings;
    }

    SortMethod(final int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static SortMethod forValue(final int value) {
        SortMethod ret = getMappings().get(value);
        if (ret == null) {
            throw new IllegalArgumentException(
                    "Invalid sort method enum value.");
        }
        return ret;
    }
}