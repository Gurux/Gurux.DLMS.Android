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
 * The AssociationResult enumerates the answers, which the server can give to
 * client's association request.
 */
public enum AssociationResult {
    /**
     * Association request is accepted.
     */
    ACCEPTED(0),
    /**
     * Association request is permanently rejected.
     */
    PERMANENT_REJECTED(1),
    /**
     * Association request is transiently rejected.
     */
    TRANSIENT_REJECTED(2);

    private final int intValue;
    private static java.util.HashMap<Integer, AssociationResult> mappings;

    private static java.util.HashMap<Integer, AssociationResult> getMappings() {
        synchronized (AssociationResult.class) {
            if (mappings == null) {
                mappings = new java.util.HashMap<Integer, AssociationResult>();
            }
        }
        return mappings;
    }

    AssociationResult(final int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    /*
     * Get integer value for enumerator.
     */
    public int getValue() {
        return intValue;
    }

    /*
     * Convert integer for enum value.
     */
    public static AssociationResult forValue(final int value) {
        return getMappings().get(value);
    }
}