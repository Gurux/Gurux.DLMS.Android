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

/**
 * Online help:<br>
 * https://www.gurux.fi/Gurux.DLMS.Objects.GXDLMSCharge
 */
public class GXCommodity {
    /**
     * Executed object.<br>
     * Online help:<br>
     * https://www.gurux.fi/Gurux.DLMS.Objects.GXDLMSCharge
     */
    private GXDLMSObject target;

    /**
     * Attribute index.<br>
     * Online help:<br>
     * https://www.gurux.fi/Gurux.DLMS.Objects.GXDLMSCharge
     */
    private int index;

    /**
     * @return Executed object.
     */
    public final GXDLMSObject getTarget() {
        return target;
    }

    /**
     * @param value
     *            Executed object.
     */
    public final void setTarget(GXDLMSObject value) {
        target = value;
    }

    /**
     * @return Attribute index.
     */
    public final int getIndex() {
        return index;
    }

    /**
     * @param value
     *            Attribute index.
     */
    public final void setIndex(int value) {
        index = value;
    }

}