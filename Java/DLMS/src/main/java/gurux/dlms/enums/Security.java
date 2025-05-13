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

import androidx.annotation.NonNull;

/**
 * Used security model.
 */
public enum Security {
    /**
     * Transport security is not used.
     */
    NONE(0),

    /**
     * Authentication security is used.
     */
    AUTHENTICATION(0x10),

    /**
     * Encryption security is used.
     */
    ENCRYPTION(0x20),

    /**
     * Authentication and Encryption security are used.
     */
    AUTHENTICATION_ENCRYPTION(0x30);

    private final int intValue;
    private static java.util.HashMap<Integer, Security> mappings;

    private static java.util.HashMap<Integer, Security> getMappings() {
        synchronized (Security.class) {
            if (mappings == null) {
                mappings = new java.util.HashMap<>();
            }
        }
        return mappings;
    }

    Security(final int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static Security forValue(final int value) {
        Security tmp = mappings.get(value);
        if (tmp == null) {
            throw new IllegalArgumentException("Invalid security integer value.");
        }
        return tmp;
    }

    @NonNull
    @Override
    public String toString() {
        String str;
        switch (this) {
            case NONE:
                str = "None";
                break;
            case AUTHENTICATION:
                str = "Authentication";
                break;
            case ENCRYPTION:
                str = "Encryption";
                break;
            case AUTHENTICATION_ENCRYPTION:
                str = "Authentication Encryption";
                break;
            default:
                throw new IllegalArgumentException("Security");
        }
        return str;
    }

    /**
     * Parse a string into its corresponding Security enum value.
     *
     * @param value The security level represented as a string.
     * @return Security enumeration value.
     */
    public static Security valueOfString(final String value) {
        Security v;
        if ("None".equalsIgnoreCase(value)) {
            v = Security.NONE;
        } else if ("Authentication".equalsIgnoreCase(value)) {
            v = Security.AUTHENTICATION;
        } else if ("Encryption".equalsIgnoreCase(value)) {
            v = Security.ENCRYPTION;
        } else if ("Authentication Encryption".equalsIgnoreCase(value) ||
                "AuthenticationEncryption".equalsIgnoreCase(value) ||
                "AUTHENTICATION_ENCRYPTION".equalsIgnoreCase(value)) {
            v = Security.AUTHENTICATION_ENCRYPTION;
        } else {
            throw new IllegalArgumentException(value);
        }
        return v;
    }
}