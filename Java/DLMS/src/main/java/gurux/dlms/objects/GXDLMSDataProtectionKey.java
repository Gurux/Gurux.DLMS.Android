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

import gurux.dlms.objects.enums.DataProtectionKeyType;

/**
 * Data protection Key.
 */
public class GXDLMSDataProtectionKey {
    /**
     * Data protectionKey type.
     */
    private DataProtectionKeyType dataProtectionKeyType = DataProtectionKeyType.IDENTIFIED;

    /**
     * Identified key parameters.
     */
    private GXDLMSDataProtectionIdentifiedKey identifiedKey = new GXDLMSDataProtectionIdentifiedKey();

    /**
     * Wrapped key parameters.
     */
    private GXDLMSDataProtectionWrappeddKey wrappedKey = new GXDLMSDataProtectionWrappeddKey();
    /**
     * Agreed key parameters.
     */
    private GXDLMSDataProtectionAgreedKey agreedKey = new GXDLMSDataProtectionAgreedKey();

    /**
     * @return Data protectionKey type.
     */
    public final DataProtectionKeyType getDataProtectionKeyType() {
        return dataProtectionKeyType;
    }

    /**
     * @param value
     *            Data protectionKey type.
     */
    public final void setDataProtectionKeyType(DataProtectionKeyType value) {
        dataProtectionKeyType = value;
    }

    /**
     * @return Identified key parameters.
     */
    public final GXDLMSDataProtectionIdentifiedKey getIdentifiedKey() {
        return identifiedKey;
    }

    /**
     * @param value
     *            Identified key parameters.
     */
    public final void setIdentifiedKey(final GXDLMSDataProtectionIdentifiedKey value) {
        identifiedKey = value;
    }

    /**
     * @return Wrapped key parameters.
     */
    public final GXDLMSDataProtectionWrappeddKey getWrappedKey() {
        return wrappedKey;
    }

    /**
     * @param value
     *            Wrapped key parameters.
     */
    public final void setWrappedKey(final GXDLMSDataProtectionWrappeddKey value) {
        wrappedKey = value;
    }

    /**
     * @return Agreed key parameters.
     */
    public final GXDLMSDataProtectionAgreedKey getAgreedKey() {
        return agreedKey;
    }

    /**
     * @param value
     *            Agreed key parameters.
     */
    public final void setAgreedKey(final GXDLMSDataProtectionAgreedKey value) {
        agreedKey = value;
    }
}