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

package gurux.dlms;

import static org.junit.Assert.assertEquals;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gurux.dlms.enums.AccessServiceCommandType;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.InterfaceType;
import gurux.dlms.enums.Priority;
import gurux.dlms.enums.ServiceClass;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.objects.GXDLMSActivityCalendar;
import gurux.dlms.objects.GXDLMSClock;
import gurux.dlms.objects.GXDLMSData;

/**
 * Object attribute and method access tests.
 */
public class AccessServiceTest {
    private GXDLMSClient target = null;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public final void setUp() {
        target = new GXDLMSClient(true, 16, 1, Authentication.NONE, null,
                InterfaceType.PDU);
    }

    @After
    public final void tearDown() {
    }

    /**
     * Access service request test.
     * 
     */
    @Test
    public final void accessRequestTest() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, SignatureException, InvalidKeyException {
        target.setPriority(Priority.NORMAL);
        target.setServiceClass(ServiceClass.UN_CONFIRMED);
        String expected =
                "D90000000100040100010000600100FF020100080000010000FF0202001400000D0000FF0702001400000D0000FF0804000001040203090100090CFFFFFFFFFFFFFF000080000009000203090101090CFFFFFFFFFFFFFF000080000009000203090102090CFFFFFFFFFFFFFF000080000009000203090103090CFFFFFFFFFFFFFF000080000009000104020809010011FF11FF11FF11FF11FF11FF11FF02080901011102110111011101110111011101020809010211FF11FF11FF11FF11FF11FF11FF02080901031101110211021102110211021102";
        java.util.ArrayList<GXDLMSAccessItem> list =
                new java.util.ArrayList<>();

        GXDLMSActivityCalendar ac =
                new GXDLMSActivityCalendar("0.0.13.0.0.255");
        GXByteBuffer data = new GXByteBuffer(GXCommon.hexToBytes(
                "01040203090100090CFFFFFFFFFFFFFF000080000009000203090101090CFFFFFFFFFFFFFF000080000009000203090102090CFFFFFFFFFFFFFF000080000009000203090103090CFFFFFFFFFFFFFF00008000000900"));
        target.updateValue(ac, 7, GXDLMSClient.getValue(data));

        data = new GXByteBuffer(GXCommon.hexToBytes(
                "0104020809010011FF11FF11FF11FF11FF11FF11FF02080901011102110111011101110111011101020809010211FF11FF11FF11FF11FF11FF11FF02080901031101110211021102110211021102"));
        target.updateValue(ac, 8, GXDLMSClient.getValue(data));
        list.add(new GXDLMSAccessItem(AccessServiceCommandType.GET,
                new GXDLMSData("0.0.96.1.0.255"), 2));
        list.add(new GXDLMSAccessItem(AccessServiceCommandType.GET,
                new GXDLMSClock("0.0.1.0.0.255"), 2));
        list.add(new GXDLMSAccessItem(AccessServiceCommandType.SET, ac, 7));
        list.add(new GXDLMSAccessItem(AccessServiceCommandType.SET, ac, 8));
        byte[] actual = target.accessRequest(null, list)[0];
        assertEquals(expected, GXCommon.toHex(actual, false));
    }

    /*
     * Access service response test.
     */
    @Test
    public final void accessResponseTest() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, SignatureException, InvalidKeyException {
        GXDLMSActivityCalendar ac =
                new GXDLMSActivityCalendar("0.0.13.0.0.255");
        List<GXDLMSAccessItem> list = new ArrayList<>();
        list.add(new GXDLMSAccessItem(AccessServiceCommandType.GET,
                new GXDLMSData("0.0.96.1.0.255"), 2));
        list.add(new GXDLMSAccessItem(AccessServiceCommandType.GET,
                new GXDLMSClock("0.0.1.0.0.255"), 2));
        list.add(new GXDLMSAccessItem(AccessServiceCommandType.SET, ac, 7));
        list.add(new GXDLMSAccessItem(AccessServiceCommandType.SET, ac, 8));
        GXReplyData reply = new GXReplyData();
        byte[] expected = GXCommon.hexToBytes(
                "DA4000000000000409083030303030303031090C07DC030C07161E0000FF88800000040100010002000200");
        target.getData(expected, reply);
        target.parseAccessResponse(list, reply.getData());
    }

}
