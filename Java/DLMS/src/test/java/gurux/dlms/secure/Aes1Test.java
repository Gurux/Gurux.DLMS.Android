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

package gurux.dlms.secure;

import static org.junit.Assert.assertEquals;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gurux.dlms.internal.GXCommon;

/**
 * @author Gurux Ltd
 */
public class Aes1Test {
    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public final void setUp() {
    }

    @After
    public final void tearDown() {
    }

    static String toHex(final byte[] bytes) {
        return GXCommon.toHex(bytes).replace(" ", "");

    }

    static void test(final String kek, final String text, final String output)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException,
            NoSuchProviderException {
        assertEquals(output, toHex(GXDLMSSecureClient
                .encrypt(GXCommon.hexToBytes(kek), GXCommon.hexToBytes(text))));
        assertEquals(text,
                toHex(GXDLMSSecureClient.decrypt(GXCommon.hexToBytes(kek),
                        GXCommon.hexToBytes(output))));
    }

    /*
     * Standard tests for SHA1.
     */
    @Test
    public final void standardTest() throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException, NoSuchProviderException {
        // CHECKSTYLE:OFF
        test("000102030405060708090A0B0C0D0E0F1011121314151617",
                "00112233445566778899AABBCCDDEEFF",
                "96778B25AE6CA435F92B5B97C050AED2468AB8A17AD84E5D");
        test("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F",
                "00112233445566778899AABBCCDDEEFF",
                "64E8C3F9CE0F5BA263E9777905818A2A93C8191E7D6E8AE7");
        test("000102030405060708090A0B0C0D0E0F1011121314151617",
                "00112233445566778899AABBCCDDEEFF0001020304050607",
                "031D33264E15D33268F24EC260743EDCE1C6C7DDEE725A936BA814915C6762D2");
        test("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F",
                "00112233445566778899AABBCCDDEEFF0001020304050607",
                "A8F9BC1612C68B3FF6E6F4FBE30E71E4769C8B80A32CB8958CD5D17D6B254DA1");
        test("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F",
                "00112233445566778899AABBCCDDEEFF000102030405060708090A0B0C0D0E0F",
                "28C9F404C4B810F4CBCCB35CFB87F8263F5786E2D80ED326CBC7F0E71A99F43BFB988B9B7A02DD21");
        // CHECKSTYLE:ON
    }
}
