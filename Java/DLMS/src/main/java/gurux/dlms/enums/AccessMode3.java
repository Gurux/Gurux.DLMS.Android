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

import java.util.HashSet;
import java.util.Set;

public enum AccessMode3 {
    /**
     * No access.
     */
    NO_ACCESS(0),
    /*
     * The client is allowed only reading from the server. <p> This is used in
     * version 1. </p>
     */
    READ(1),
    /*
     * The client is allowed only writing to the server.
     */
    WRITE(2),

    /**
     * Request messages are authenticated.
     */
    AUTHENTICATED_REQUEST(4),

    /**
     * Request messages are encrypted.
     */
    ENCRYPTED_REQUEST(8),

    /**
     * Request messages are digitally signed.
     */
    DIGITALLY_SIGNED_REQUEST(16),

    /**
     * Response messages are authenticated.
     */
    AUTHENTICATED_RESPONSE(32),

    /**
     * Response messages are encrypted.
     */
    ENCRYPTED_RESPONSE(64),

    /**
     * Response messages are digitally signed.
     */
    DIGITALLY_SIGNED_RESPONSE(128);

    private final int value;
    private static java.util.HashMap<Integer, AccessMode3> mappings;

    private static java.util.HashMap<Integer, AccessMode3> getMappings() {
        synchronized (AccessMode3.class) {
            if (mappings == null) {
                mappings = new java.util.HashMap<>();
            }
        }
        return mappings;
    }

    AccessMode3(final int mode) {
        this.value = mode;
        getMappings().put(mode, this);
    }

    /*
     * Get integer value for enum.
     */
    public final int getValue() {
        return value;
    }

    /**
     * @return Get enumeration constant values.
     */
    private static AccessMode3[] getEnumConstants() {
        return new AccessMode3[]{READ, WRITE, AUTHENTICATED_REQUEST, ENCRYPTED_REQUEST, DIGITALLY_SIGNED_REQUEST,
                AUTHENTICATED_RESPONSE, ENCRYPTED_RESPONSE, DIGITALLY_SIGNED_RESPONSE};
    }

    /**
     * Converts the integer value to enumerated value.
     *
     * @param value The integer value, which is read from the device.
     * @return The enumerated value, which represents the integer.
     */
    public static java.util.Set<AccessMode3> forValue(final int value) {
        Set<AccessMode3> types = new HashSet<>();
        AccessMode3[] enums = getEnumConstants();
        for (int pos = 0; pos != enums.length; ++pos) {
            if ((enums[pos].value & value) == enums[pos].value) {
                types.add(enums[pos]);
            }
        }
        return types;
    }

    /**
     * Converts the enumerated value to integer value.
     *
     * @param value The enumerated value.
     * @return The integer value.
     */
    public static int toInteger(final Set<AccessMode3> value) {
        int tmp = 0;
        for (AccessMode3 it : value) {
            tmp |= it.getValue();
        }
        return tmp;
    }
}