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

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gurux.dlms.GXByteBuffer;
import gurux.dlms.asn.GXAsn1BitString;
import gurux.dlms.asn.GXAsn1Converter;
import gurux.dlms.asn.GXAsn1Integer;
import gurux.dlms.asn.GXAsn1Sequence;
import gurux.dlms.enums.Security;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.objects.enums.SecuritySuite;

/**
 * @author Gurux Ltd
 */
public class GXSymmetricTest {
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

	/*
	 * Standard test for authentication using test data from Blue Book.
	 */
	@Test
	public final void authenticationTest() throws Exception {
		String expected = "06 72 5D 91 0F 92 21 D2 63 87 75 16";
		String data = "C0 01 00 00 08 00 00 01 00 00 FF 02 00";
		AesGcmParameter p = new AesGcmParameter(null, 0x10, Security.AUTHENTICATION, SecuritySuite.SUITE_0, 0x01234567,
				GXCommon.hexToBytes("4D4D4D0000BC614E"), GXCommon.hexToBytes("000102030405060708090A0B0C0D0E0F"),
				GXCommon.hexToBytes("D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF"));
		p.setType(CountType.TAG);
		String actual = GXCommon.toHex(GXSecure.encryptAesGcm(true, p, GXCommon.hexToBytes(data)));
		assertEquals(expected, actual);
		actual = GXCommon.toHex(GXSecure.encryptAesGcm(false, p, GXCommon.hexToBytes(data + expected)));
		assertEquals(data, actual);
	}

	/*
	 * Standard test for encryption using test data from Blue Book.
	 */
	@Test
	public final void encryptionTest() throws Exception {
		String expected = "41 13 12 FF 93 5A 47 56 68 27 C4 67 BC";
		String data = "C0 01 00 00 08 00 00 01 00 00 FF 02 00";
		AesGcmParameter p = new AesGcmParameter(null, 0x20, Security.ENCRYPTION, SecuritySuite.SUITE_0, 0x01234567,
				GXCommon.hexToBytes("4D4D4D0000BC614E"), GXCommon.hexToBytes("000102030405060708090A0B0C0D0E0F"),
				GXCommon.hexToBytes("D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF"));
		p.setType(CountType.DATA);
		byte[] actual = GXSecure.encryptAesGcm(true, p, GXCommon.hexToBytes(data));
		assertEquals(expected, GXCommon.toHex(actual));
		actual = GXSecure.encryptAesGcm(false, p, actual);
		assertEquals(data, GXCommon.toHex(actual));
	}

	/*
	 * Standard test for authenticated encryption using test data from Blue Book.
	 */
	@Test
	public final void authenticatedEncryptionTest() throws Exception {
		String expected = "41 13 12 FF 93 5A 47 56 68 27 C4 67 BC 7D 82 5C 3B E4 A7 7C 3F CC 05 6B 6B";
		String data = "C0 01 00 00 08 00 00 01 00 00 FF 02 00";
		AesGcmParameter p = new AesGcmParameter(null, 0x30, Security.AUTHENTICATION_ENCRYPTION, SecuritySuite.SUITE_0,
				0x01234567, GXCommon.hexToBytes("4D4D4D0000BC614E"),
				GXCommon.hexToBytes("000102030405060708090A0B0C0D0E0F"),
				GXCommon.hexToBytes("D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF"));
		p.setType(CountType.DATA);
		byte[] actual = GXSecure.encryptAesGcm(true, p, GXCommon.hexToBytes(data));
		assertEquals(expected, GXCommon.toHex(actual));
		actual = GXSecure.encryptAesGcm(false, p, actual);
		assertEquals(data, GXCommon.toHex(actual));
	}

	@Test
	public final void testVectorForKeyAgreementUsingTheStaticUnifiedModelTest() throws Exception {
		// PDU is in page 484 in Table 133 – Example: ACCESS service
		// without block transfer
		@SuppressWarnings("unused")
		byte[] data = GXCommon.hexToBytes(
				"D94000000000040100010000600100FF020100080000010000FF0202001400000D0000FF0702001400000D0000FF0804000001040203090100090CFFFFFFFFFFFFFF00000000000901FF0203090101090CFFFFFFFFFFFFFF00000000000901FF0203090102090CFFFFFFFFFFFFFF00000000000901FF0203090103090CFFFFFFFFFFFFFF00000000000901FF0104020809010011FF11FF11FF11FF11FF11FF11FF02080901011102110111011101110111011101020809010211FF11FF11FF11FF11FF11FF11FF02080901031101110211021102110211021102");
		// Page 502
		@SuppressWarnings("unused")
		byte[] expected = GXCommon.hexToBytes(
				"607581D83815281B561904E6A72B83BF3FB0B1F2A0A23A82A804B39911F6CB1B4EF9B6F76C6338ED058014FFBF4A13A162E0EED11EEB8F597757A18215F28E1D3FC2D44586C4B8AFE4500B535B579506CDB925CBEFF7F1CF6BF96C583B9CF588FE8F6B01A574824D7CEC597F057FFA8700AF12AD63A7FA72040439F4392C089B265F9EB1AC308239906DC04E1A8712ABE383CF92349842EBA166EF2E9EB2F53D0DBA025D79875463398BBC3007BC4A6FC12780C21EE1ABF080BF0FA926242834C0AC30D55A1EF8856E7DED48621F17FDF4D5B54EDDADD874119251049B6AEE111C12FBC2FEAF");
		AesGcmParameter p = new AesGcmParameter(0x31, null, Security.AUTHENTICATION_ENCRYPTION, SecuritySuite.SUITE_1,
				0x0102030405060708L,
				// Clock cipher key.
				GXCommon.hexToBytes("56C46B57DF675515C31025455822514A"),
				// Authentication key.
				GXCommon.hexToBytes("D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF"),
				// Originator system title.
				GXCommon.hexToBytes("4D4D4D0000BC614E"),
				// recipient system title.
				GXCommon.hexToBytes("4D4D4D0000000001"),
				// Date time
				null,
				// Other information.
				null);
		p.setType(CountType.DATA | CountType.TAG);
		// assertEquals(GXCommon.toHex(expected),
		// GXCommon.toHex(GXSymmetric.encryptAesGcm(false, p, data)));
	}

	@Test
	public final void testVectorForKeyAgreementUsingTheStaticUnifiedModelTest2() throws Exception {
		// PDU is in page 484 in Table 133 – Example: ACCESS service
		// without block transfer
		byte[] data = GXCommon.hexToBytes(
				"D94000000000040100010000600100FF020100080000010000FF0202001400000D0000FF0702001400000D0000FF0804000001040203090100090CFFFFFFFFFFFFFF00000000000901FF0203090101090CFFFFFFFFFFFFFF00000000000901FF0203090102090CFFFFFFFFFFFFFF00000000000901FF0203090103090CFFFFFFFFFFFFFF00000000000901FF0104020809010011FF11FF11FF11FF11FF11FF11FF02080901011102110111011101110111011101020809010211FF11FF11FF11FF11FF11FF11FF02080901031101110211021102110211021102");
		// Page 502
		// Tag 51 04 9B 6A EE 11 1C 12 FB C2 FE AF
		@SuppressWarnings("unused")
		byte[] expected = GXCommon.hexToBytes(
				"607581D83815281B561904E6A72B83BF3FB0B1F2A0A23A82A804B39911F6CB1B4EF9B6F76C6338ED058014FFBF4A13A162E0EED11EEB8F597757A18215F28E1D3FC2D44586C4B8AFE4500B535B579506CDB925CBEFF7F1CF6BF96C583B9CF588FE8F6B01A574824D7CEC597F057FFA8700AF12AD63A7FA72040439F4392C089B265F9EB1AC308239906DC04E1A8712ABE383CF92349842EBA166EF2E9EB2F53D0DBA025D79875463398BBC3007BC4A6FC12780C21EE1ABF080BF0FA926242834C0AC30D55A1EF8856E7DED48621F17FDF4D5B54EDDADD874119251049B6AEE111C12FBC2FEAF");
		AesGcmParameter p = new AesGcmParameter(0x31, null, Security.AUTHENTICATION_ENCRYPTION, SecuritySuite.SUITE_1,
				0,
				// Clock cipher key.
				GXCommon.hexToBytes("56C46B57DF675515C31025455822514A"),
				// Authentication key.
				GXCommon.hexToBytes("D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF"),
				// Originator system title.
				GXCommon.hexToBytes("4D4D4D0000BC614E"),
				// recipient system title.
				GXCommon.hexToBytes("4D4D4D0000000001"),
				// Date time
				null,
				// Other information.
				null);

		GXByteBuffer data2 = new GXByteBuffer();
		data2.setUInt8(0x31);
		data2.set(p.getAuthenticationKey());
		// transaction-id
		data2.setHexString("080102030405060708");
		// originator-system-title
		data2.setHexString("084D4D4D0000BC614E");
		// recipient-system-title
		data2.setHexString("084D4D4D0000000001");
		// date-time not present
		data2.setUInt8(0);
		// other-information not present
		data2.setUInt8(0);
		GXSecure.encryptAesGcm(true, p, data);

		// Cipher c = GXSymmetric.getCipher(p, true);
		// c.updateAAD(data2.array());
		// @SuppressWarnings("unused")
		// byte[] actual = c.doFinal(data);
		// assertEquals(GXCommon.toHex(expected), GXCommon.toHex(actual));
	}

	/*
	 * Table C. 2 – Test vector for key agreement using the One-pass Diffie-Hellman
	 * (1e, 1s, ECC CDH) scheme
	 */
	@Test
	public final void testVectorForKeyAgreementUsingTheOnePassDiffieHellmanTest() throws Exception {
		// PDU is in page 484 in Table 133 – Example: ACCESS service
		// without block transfer
		@SuppressWarnings("unused")
		byte[] data = GXCommon.hexToBytes(
				"D94000000000040100010000600100FF020100080000010000FF0202001400000D0000FF0702001400000D0000FF0804000001040203090100090CFFFFFFFFFFFFFF00000000000901FF0203090101090CFFFFFFFFFFFFFF00000000000901FF0203090102090CFFFFFFFFFFFFFF00000000000901FF0203090103090CFFFFFFFFFFFFFF00000000000901FF0104020809010011FF11FF11FF11FF11FF11FF11FF02080901011102110111011101110111011101020809010211FF11FF11FF11FF11FF11FF11FF02080901031101110211021102110211021102");
		// Page 499
		@SuppressWarnings("unused")
		byte[] expected = GXCommon.hexToBytes(
				"F435069679270C5BF4425EE5777402A6C8D51C620EED52DBB188378B836E2857D5C053E6DDF27FA87409AEF502CD9618AE47017C010224FD109CC0BEB21E742D44AB40CD11908743EC90EC8C40E221D517F72228E1A26E827F43DC18ED27B5F458D66508B05A2A4CC6FED178C881AFC3BC67064689BE8BB41C80ABB3C114A31F4CB03B8B64C7E0B4CE77B2399C93347858888F92239713B38DF01C4858245827A92EF334172EA636B31CBBDF2A96AD5D035F66AA38F1A2D97D4BBA99622E6B5F18789CECB2DFB3937D9F3E17F8B472098E6563238F37528374809836002AEA6E7012D2ADFAA7");
		AesGcmParameter p = new AesGcmParameter(0x31, null, Security.AUTHENTICATION_ENCRYPTION, SecuritySuite.SUITE_1,
				0,
				// Block cipher key.
				GXCommon.hexToBytes("59A71FD81C929A86A99438DA17A66C05"),
				GXCommon.hexToBytes("D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF"),
				// Originator system title.
				GXCommon.hexToBytes("4D4D4D0000BC614E"),
				// recipient system title.
				GXCommon.hexToBytes("4D4D4D0000000001"),
				// Date time
				null,
				// Other information.
				null);
	}

	/*
	 * Generate Ephemeral Public Key Signature Test
	 */
	@Test
	public final void generateEphemeralPublicKeySignatureTest() throws Exception {
		byte keyId = 0;

		// Signing Key Client
		PrivateKey priKc = GXAsn1Converter
				.getPrivateKey(GXCommon.hexToBytes("418073C239FA6125011DE4D6CD2E645780289F761BB21BFB0835CB5585E8B373"));
		PublicKey pubKc = GXAsn1Converter.getPublicKey(GXCommon.hexToBytes(
				"BAAFFDE06A8CB1C9DAE8D94023C601DBBB249254BA22EDD827E820BCA2BCC64362FBB83D86A82B87BB8B7161D2AAB5521911A946B97A284A90F7785CD9047D25"));

		PublicKey epubKc = GXAsn1Converter.getPublicKey(GXCommon.hexToBytes(
				"2914D60E10AB705F62ED6CC349D7CB99B9AB3F3978E59278C7AF595B3AF987941372DAB6D5AF1FA867E134167E6F23DE664A6693E05F43414611058D1B48F894"));
		@SuppressWarnings("unused")
		byte[] expected = GXCommon.hexToBytes(
				"06F0607702AA0E2435A183E2F6B1ECD19629712E389A213610C03F77B2590860EA840AF5C3FA1F2BCDF055D4744E9A01CE9A0E55026BCAA4EEBEB764CED64BB3");

		GXAsn1BitString tmp = (GXAsn1BitString) ((GXAsn1Sequence) GXAsn1Converter.fromByteArray(epubKc.getEncoded()))
				.get(1);

		// Ephemeral public key client
		GXByteBuffer epk = new GXByteBuffer(tmp.getValue());
		// First byte is 4 in Java and that is not used. We can override it.
		epk.getData()[0] = keyId;
		// Add ephemeral public key signature.
		Signature instance = Signature.getInstance("SHA256withECDSA");
		instance.initSign(priKc);
		instance.update(epk.array());
		byte[] sign = instance.sign();
		GXAsn1Sequence tmp2 = (GXAsn1Sequence) GXAsn1Converter.fromByteArray(sign);
		System.out.println(GXCommon.toHex(sign));
		GXByteBuffer data = new GXByteBuffer();
		// Truncate to 64 bytes. Remove zeros from the begin.
		byte[] arr = ((GXAsn1Integer) tmp2.get(0)).getByteArray();
		data.set(arr, arr.length - 32, 32);
		arr = ((GXAsn1Integer) tmp2.get(1)).getByteArray();
		data.set(arr, arr.length - 32, 32);

		GXAsn1Integer a = new GXAsn1Integer(data.getData(), 0, 32);
		GXAsn1Integer b = new GXAsn1Integer(data.getData(), 32, 32);
		GXAsn1Sequence s = new GXAsn1Sequence();
		s.add(a);
		s.add(b);
		sign = GXAsn1Converter.toByteArray(s);

		instance = Signature.getInstance("SHA256withECDSA");
		instance.initVerify(pubKc);
		instance.update(epk.array());
		boolean v = instance.verify(sign);
		assertEquals(true, v);
	}
}
