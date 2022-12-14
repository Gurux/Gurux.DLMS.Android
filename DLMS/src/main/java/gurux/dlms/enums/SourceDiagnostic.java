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
 * SourceDiagnostic enumerates the error codes for reasons that can cause the
 * server to reject the client.
 */
public enum SourceDiagnostic {
    /**
     * OK.
     */
    NONE(0),
    /*
     * No reason is given.
     */
    NO_REASON_GIVEN(1),
    /*
     * The application context name is not supported.
     */
    NOT_SUPPORTED(2),
    /**
     * Calling AP title not recognized.
     */
    CALLING_AP_TITLE_NOT_RECOGNIZED(3),
    /**
     * Calling AP invocation identifier not recognized.
     */
    CALLING_AP_INVOCATION_IDENTIFIER_NOT_RECOGNIZED(4),
    /**
     * Calling AE qualifier not recognized
     */
    CALLING_AE_QUALIFIER_NOT_RECOGNIZED(5),
    /**
     * Calling AE invocation identifier not recognized
     */
    CALLING_AE_INVOCATION_IDENTIFIER_NOT_RECOGNIZED(6),
    /**
     * Called AP title not recognized
     */
    CALLED_AP_TITLE_NOT_RECOGNIZED(7),
    /**
     * Called AP invocation identifier not recognized
     */
    CALLED_AP_INVOCATION_IDENTIFIER_NOT_RECOGNIZED(8),
    /**
     * Called AE qualifier not recognized
     */
    CALLED_AE_QUALIFIER_NOT_RECOGNIZED(9),
    /**
     * Called AE invocation identifier not recognized
     */
    CALLED_AE_INVOCATION_IDENTIFIER_NOT_RECOGNIZED(10),
    /*
     * The authentication mechanism name is not recognized.
     */
    NOT_RECOGNISED(11),
    /*
     * Authentication mechanism name is required.
     */
    MECHANISM_NAME_REGUIRED(12),
    /*
     * Authentication failure.
     */
    AUTHENTICATION_FAILURE(13),
    /*
     * Authentication is required.
     */
    AUTHENTICATION_REQUIRED(14);

    private int intValue;
    private static java.util.HashMap<Integer, SourceDiagnostic> mappings;

    private static java.util.HashMap<Integer, SourceDiagnostic> getMappings() {
        synchronized (SourceDiagnostic.class) {
            if (mappings == null) {
                mappings = new java.util.HashMap<Integer, SourceDiagnostic>();
            }
        }
        return mappings;
    }

    SourceDiagnostic(final int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    /*
     * Get integer value for enumeration.
     */
    public int getValue() {
        return intValue;
    }

    /*
     * Convert integer for enumeration value.
     */
    public static SourceDiagnostic forValue(final int value) {
        return getMappings().get(value);
    }
}