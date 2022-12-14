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

import java.util.HashMap;

/**
 * Enumerates account credit status modes.<br>
 * Online help:<br>
 * https://www.gurux.fi/Gurux.DLMS.Objects.GXDLMSAccount
 */
public enum AccountCreditStatus {

    /**
     * In credit.
     */
    IN_CREDIT(0x1),

    /**
     * Low credit.
     */
    LOW_CREDIT(0x2),

    /**
     * Next credit enabled.
     */
    NEXT_CREDIT_ENABLED(0x4),

    /**
     * Next credit selectable.
     */
    NEXT_CREDIT_SELECTABLE(0x8),

    /**
     * Credit reference list.
     */
    CREDIT_REFERENCE_LIST(0x10),

    /**
     * Selectable credit in use.
     */
    SELECTABLE_CREDIT_IN_USE(0x20),

    /**
     * Out of credit.
     */
    OUT_OF_CREDIT(0x40),

    /**
     * Reserved.
     */
    RESERVED(0x80);

    /**
     * Integer value of enumerator.
     */
    private int intValue;

    /**
     * Collection of enumerator values.
     */
    private static java.util.HashMap<Integer, AccountCreditStatus> mappings;

    /**
     * Returns collection of enumerator values.
     * 
     * @return Enumerator values.
     */
    private static HashMap<Integer, AccountCreditStatus> getMappings() {
        synchronized (AccountCreditStatus.class) {
            if (mappings == null) {
                mappings = new HashMap<Integer, AccountCreditStatus>();
            }
        }
        return mappings;
    }

    /**
     * Constructor.
     * 
     * @param value
     *            Integer value of enumerator.
     */
    AccountCreditStatus(final int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    /**
     * Get integer value for enumerator.
     * 
     * @return Enumerator integer value.
     */
    public int getValue() {
        return intValue;
    }

    /**
     * Returns enumerator value from an integer value.
     * 
     * @param value
     *            Integer value.
     * @return Enumeration value.
     */
    public static AccountCreditStatus forValue(final int value) {
        AccountCreditStatus ret = getMappings().get(value);
        if (ret == null) {
            throw new IllegalArgumentException(
                    "Invalid account credit status enum value.");
        }
        return ret;
    }
}