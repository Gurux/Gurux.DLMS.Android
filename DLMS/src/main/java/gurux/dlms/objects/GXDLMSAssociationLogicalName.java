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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gurux.dlms.GXBitString;
import gurux.dlms.GXByteBuffer;
import gurux.dlms.GXDLMSClient;
import gurux.dlms.GXDLMSServerBase;
import gurux.dlms.GXDLMSSettings;
import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.GXSimpleEntry;
import gurux.dlms.ValueEventArgs;
import gurux.dlms.asn.GXAsn1Converter;
import gurux.dlms.asn.GXAsn1Integer;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.Conformance;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.ErrorCode;
import gurux.dlms.enums.MethodAccessMode;
import gurux.dlms.enums.ObjectType;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.objects.enums.ApplicationContextName;
import gurux.dlms.objects.enums.AssociationStatus;
import gurux.dlms.secure.GXSecure;

/**
 * Online help: <br>
 * https://www.gurux.fi/Gurux.DLMS.Objects.GXDLMSAssociationLogicalName
 */
public class GXDLMSAssociationLogicalName extends GXDLMSObject
        implements IGXDLMSBase {
    private static final Logger LOGGER =
            Logger.getLogger(GXDLMSAssociationLogicalName.class.getName());
    private GXDLMSObjectCollection objectList;
    private short clientSAP;
    private short serverSAP;
    private GXApplicationContextName applicationContextName;
    private GXxDLMSContextType xDLMSContextInfo;
    private GXAuthenticationMechanismName authenticationMechanismName;
    /**
     * Secret used in Low Level Authentication.
     */
    private byte[] secret;

    private AssociationStatus associationStatus =
            AssociationStatus.NON_ASSOCIATED;
    private String securitySetupReference;

    /**
     * User list.
     */
    private List<Entry<Byte, String>> userList;

    /**
     * Current user.
     */
    private Entry<Byte, String> currentUser;

    /**
     * Constructor.
     */
    public GXDLMSAssociationLogicalName() {
        this("0.0.40.0.0.255");
    }

    /**
     * Constructor.
     * 
     * @param ln
     *            Logical Name of the object.
     */
    public GXDLMSAssociationLogicalName(final String ln) {
        super(ObjectType.ASSOCIATION_LOGICAL_NAME, ln, 0);
        setLogicalName(ln);
        objectList = new GXDLMSObjectCollection(this);
        applicationContextName = new GXApplicationContextName();
        xDLMSContextInfo = new GXxDLMSContextType();
        authenticationMechanismName = new GXAuthenticationMechanismName();
        setUserList(new ArrayList<Entry<Byte, String>>());
        setVersion(2);
    }

    public final GXDLMSObjectCollection getObjectList() {
        return objectList;
    }

    public final void setObjectList(final GXDLMSObjectCollection value) {
        objectList = value;
    }

    /*
     * Contains the identifiers of the COSEM client APs within the physical
     * devices hosting these APs, which belong to the AA modelled by the
     * Association LN object.
     */
    public final short getClientSAP() {
        return clientSAP;
    }

    public final void setClientSAP(final short value) {
        clientSAP = value;
    }

    /*
     * Contains the identifiers of the COSEM server (logical device) APs within
     * the physical devices hosting these APs, which belong to the AA modelled
     * by the Association LN object.
     */
    public final short getServerSAP() {
        return serverSAP;
    }

    public final void setServerSAP(final short value) {
        serverSAP = value;
    }

    public final GXApplicationContextName getApplicationContextName() {
        return applicationContextName;
    }

    public final GXxDLMSContextType getXDLMSContextInfo() {
        return xDLMSContextInfo;
    }

    public final GXAuthenticationMechanismName
            getAuthenticationMechanismName() {
        return authenticationMechanismName;
    }

    /**
     * @return Secret used in Low Level Authentication.
     */
    public final byte[] getSecret() {
        return secret;
    }

    /**
     * @param value
     *            Secret used in Low Level Authentication.
     */
    public final void setSecret(final byte[] value) {
        secret = value;
    }

    public final AssociationStatus getAssociationStatus() {
        return associationStatus;
    }

    public final void setAssociationStatus(final AssociationStatus value) {
        associationStatus = value;
    }

    public final String getSecuritySetupReference() {
        return securitySetupReference;
    }

    public final void setSecuritySetupReference(final String value) {
        securitySetupReference = value;
    }

    /**
     * Updates secret.
     * 
     * @param client
     *            DLMS client.
     * @return Action bytes.
     * @throws NoSuchPaddingException
     *             No such padding exception.
     * @throws NoSuchAlgorithmException
     *             No such algorithm exception.
     * @throws InvalidAlgorithmParameterException
     *             Invalid algorithm parameter exception.
     * @throws InvalidKeyException
     *             Invalid key exception.
     * @throws BadPaddingException
     *             Bad padding exception.
     * @throws IllegalBlockSizeException
     *             Illegal block size exception.
     */
    public byte[][] updateSecret(final GXDLMSClient client)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        if (getAuthenticationMechanismName()
                .getMechanismId() == Authentication.NONE) {
            throw new IllegalArgumentException(
                    "Invalid authentication level in MechanismId.");
        }
        if (getAuthenticationMechanismName()
                .getMechanismId() == Authentication.HIGH_GMAC) {
            throw new IllegalArgumentException(
                    "HighGMAC secret is updated using Security setup.");
        }
        if (getAuthenticationMechanismName()
                .getMechanismId() == Authentication.LOW) {
            return client.write(this, 7);
        }
        // Action is used to update High authentication password.
        return client.method(this, 2, secret, DataType.OCTET_STRING);
    }

    /**
     * Add user to user list.
     * 
     * @param client
     *            DLMS client.
     * @param id
     *            User ID.
     * @param name
     *            User name.
     * @return Action bytes.
     * @throws NoSuchPaddingException
     *             No such padding exception.
     * @throws NoSuchAlgorithmException
     *             No such algorithm exception.
     * @throws InvalidAlgorithmParameterException
     *             Invalid algorithm parameter exception.
     * @throws InvalidKeyException
     *             Invalid key exception.
     * @throws BadPaddingException
     *             Bad padding exception.
     * @throws IllegalBlockSizeException
     *             Illegal block size exception.
     */
    public final byte[][] addUser(final GXDLMSClient client, final byte id,
            final String name)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        GXByteBuffer data = new GXByteBuffer();
        data.setUInt8(DataType.STRUCTURE.getValue());
        // Add structure size.
        data.setUInt8(2);
        GXCommon.setData(null, data, DataType.UINT8, id);
        GXCommon.setData(null, data, DataType.STRING, name);
        return client.method(this, 5, data.array(), DataType.STRUCTURE);
    }

    /**
     * Remove user from user list.
     * 
     * @param client
     *            DLMS client.
     * @param id
     *            User ID.
     * @param name
     *            User name.
     * @return Action bytes.
     * @throws NoSuchPaddingException
     *             No such padding exception.
     * @throws NoSuchAlgorithmException
     *             No such algorithm exception.
     * @throws InvalidAlgorithmParameterException
     *             Invalid algorithm parameter exception.
     * @throws InvalidKeyException
     *             Invalid key exception.
     * @throws BadPaddingException
     *             Bad padding exception.
     * @throws IllegalBlockSizeException
     *             Illegal block size exception.
     */
    public final byte[][] removeUser(final GXDLMSClient client, final byte id,
            final String name)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        GXByteBuffer data = new GXByteBuffer();
        data.setUInt8(DataType.STRUCTURE.getValue());
        // Add structure size.
        data.setUInt8(2);
        GXCommon.setData(null, data, DataType.UINT8, id);
        GXCommon.setData(null, data, DataType.STRING, name);
        return client.method(this, 6, data.array(), DataType.STRUCTURE);
    }

    @Override
    public final Object[] getValues() {
        return new Object[] { getLogicalName(), getObjectList(),
                clientSAP + "/" + serverSAP, getApplicationContextName(),
                getXDLMSContextInfo(), getAuthenticationMechanismName(),
                getSecret(), getAssociationStatus(),
                getSecuritySetupReference(), userList, currentUser };
    }

    @Override
    public final byte[] invoke(final GXDLMSSettings settings,
            final ValueEventArgs e) throws InvalidKeyException,
            NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, SignatureException {
        // Check reply_to_HLS_authentication
        if (e.getIndex() == 1) {
            byte[] serverChallenge = null, clientChallenge = null;
            long ic = 0;
            byte[] readSecret;
            boolean accept;
            if (settings.getAuthentication() == Authentication.HIGH_ECDSA) {
                try {
                    if (settings.getSourceSystemTitle() == null
                            || settings.getSourceSystemTitle().length != 8) {
                        throw new IllegalArgumentException(
                                "SourceSystemTitle is invalid.");
                    }
                    GXByteBuffer signature =
                            new GXByteBuffer((byte[]) e.getParameters());
                    byte[] tmp = GXAsn1Converter.toByteArray(new Object[] {
                            new GXAsn1Integer(signature.subArray(0, 32)),
                            new GXAsn1Integer(signature.subArray(32, 32)) });
                    signature.size(0);
                    signature.set(tmp);
                    Signature ver = Signature.getInstance("SHA256withECDSA");
                    ver.initVerify(settings.getCipher().getCertificates().get(0)
                            .getPublicKey());
                    GXByteBuffer bb = new GXByteBuffer();
                    bb.set(settings.getSourceSystemTitle());
                    bb.set(settings.getCipher().getSystemTitle());
                    bb.set(settings.getStoCChallenge());
                    bb.set(settings.getCtoSChallenge());
                    ver.update(bb.array());
                    accept = ver.verify(signature.array());

                } catch (Exception ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            } else {
                if (settings.getAuthentication() == Authentication.HIGH_GMAC) {
                    readSecret = settings.getSourceSystemTitle();
                    GXByteBuffer bb =
                            new GXByteBuffer((byte[]) e.getParameters());
                    bb.getUInt8();
                    ic = bb.getUInt32();
                } else if (settings
                        .getAuthentication() == Authentication.HIGH_SHA256) {
                    GXByteBuffer tmp = new GXByteBuffer();
                    tmp.set(secret);
                    tmp.set(settings.getSourceSystemTitle());
                    tmp.set(settings.getCipher().getSystemTitle());
                    tmp.set(settings.getStoCChallenge());
                    tmp.set(settings.getCtoSChallenge());
                    readSecret = tmp.array();
                } else {
                    readSecret = secret;
                }
                serverChallenge =
                        GXSecure.secure(settings, settings.getCipher(), ic,
                                settings.getStoCChallenge(), readSecret);
                clientChallenge = (byte[]) e.getParameters();
                accept = GXCommon.compare(serverChallenge, clientChallenge);
            }
            if (accept) {
                if (settings.getAuthentication() == Authentication.HIGH_GMAC
                        || settings
                                .getAuthentication() == Authentication.HIGH_ECDSA) {
                    readSecret = settings.getCipher().getSystemTitle();
                    ic = settings.getCipher().getInvocationCounter();
                } else {
                    readSecret = secret;
                }
                if (settings
                        .getAuthentication() == Authentication.HIGH_SHA256) {
                    GXByteBuffer tmp = new GXByteBuffer();
                    tmp.set(secret);
                    tmp.set(settings.getCipher().getSystemTitle());
                    tmp.set(settings.getSourceSystemTitle());
                    tmp.set(settings.getCtoSChallenge());
                    tmp.set(settings.getStoCChallenge());
                    readSecret = tmp.array();
                }
                byte[] tmp = GXSecure.secure(settings, settings.getCipher(), ic,
                        settings.getCtoSChallenge(), readSecret);
                associationStatus = AssociationStatus.ASSOCIATED;
                return tmp;
            } else {
                LOGGER.log(Level.INFO,
                        "Invalid CtoS:" + GXCommon.toHex(serverChallenge, false)
                                + "-" + GXCommon.toHex(clientChallenge, false));
                associationStatus = AssociationStatus.NON_ASSOCIATED;
            }
        } else if (e.getIndex() == 2) {
            byte[] tmp = (byte[]) e.getParameters();
            if (tmp == null || tmp.length == 0) {
                e.setError(ErrorCode.READ_WRITE_DENIED);
            } else {
                secret = tmp;
            }
        } else if (e.getIndex() == 5) {
            List<?> tmp = (List<?>) e.getParameters();
            if (tmp == null || tmp.size() != 2) {
                e.setError(ErrorCode.READ_WRITE_DENIED);
            } else {
                userList.add(new GXSimpleEntry<Byte, String>(
                        ((Number) tmp.get(0)).byteValue(),
                        (String) tmp.get(1)));
            }
        } else if (e.getIndex() == 6) {
            List<?> tmp = (List<?>) e.getParameters();
            if (tmp == null || tmp.size() != 2) {
                e.setError(ErrorCode.READ_WRITE_DENIED);
            } else {
                byte id = ((Number) tmp.get(0)).byteValue();
                String name = (String) tmp.get(1);
                for (Entry<Byte, String> it : userList) {
                    if (it.getKey() == id
                            && it.getValue().compareTo(name) == 0) {
                        userList.remove(it);
                        break;
                    }
                }
            }
        } else {
            e.setError(ErrorCode.READ_WRITE_DENIED);
        }
        return null;
    }

    /*
     * Returns collection of attributes to read. If attribute is static and
     * already read or device is returned HW error it is not returned.
     */
    @Override
    public final int[] getAttributeIndexToRead(final boolean all) {
        java.util.ArrayList<Integer> attributes =
                new java.util.ArrayList<Integer>();
        // LN is static and read only once.
        if (all || getLogicalName() == null
                || getLogicalName().compareTo("") == 0) {
            attributes.add(1);
        }
        // ObjectList is static and read only once.
        if (all || !isRead(2)) {
            attributes.add(2);
        }
        // associated_partners_id is static and read only once.
        if (all || !isRead(3)) {
            attributes.add(3);
        }
        // Application Context Name is static and read only once.
        if (all || !isRead(4)) {
            attributes.add(4);
        }
        // xDLMS Context Info
        if (all || !isRead(5)) {
            attributes.add(5);
        }
        // Authentication Mechanism Name
        if (all || !isRead(6)) {
            attributes.add(6);
        }
        // Secret
        if (all || !isRead(7)) {
            attributes.add(7);
        }
        // Association Status
        if (all || !isRead(8)) {
            attributes.add(8);
        }
        // Security Setup Reference is from version 1.
        if (getVersion() > 0 && (all || !isRead(9))) {
            attributes.add(9);
        }
        // User list and current user are in version 2.
        if (getVersion() > 1) {
            if (all || !isRead(10)) {
                attributes.add(10);
            }
            if (all || !isRead(11)) {
                attributes.add(11);
            }
        }
        return GXDLMSObjectHelpers.toIntArray(attributes);
    }

    @Override
    public final int getAttributeCount() {
        if (getVersion() > 1) {
            return 11;
        }
        // Security Setup Reference is from version 1.
        if (getVersion() > 0) {
            return 9;
        }
        return 8;
    }

    /*
     * Returns amount of methods.
     */
    @Override
    public final int getMethodCount() {
        if (getVersion() > 1)
            return 6;
        return 4;
    }

    /**
     * Returns Association View.
     */
    private byte[] getObjects(final GXDLMSSettings settings,
            final ValueEventArgs e) {
        try {
            GXByteBuffer data = new GXByteBuffer();
            // Add count only for first time.
            if (settings.getIndex() == 0) {
                settings.setCount(objectList.size());
                data.setUInt8(DataType.ARRAY.getValue());
                GXCommon.setObjectCount(objectList.size(), data);
            }
            int pos = 0;
            boolean gbt = settings.getNegotiatedConformance()
                    .contains(Conformance.GENERAL_BLOCK_TRANSFER);
            for (GXDLMSObject it : objectList) {
                ++pos;
                if (!(pos <= settings.getIndex())) {
                    data.setUInt8(DataType.STRUCTURE.getValue());
                    // Count
                    data.setUInt8(4);
                    // ClassID
                    GXCommon.setData(settings, data, DataType.UINT16,
                            it.getObjectType().getValue());
                    // Version
                    GXCommon.setData(settings, data, DataType.UINT8,
                            it.getVersion());
                    // LN
                    GXCommon.setData(settings, data, DataType.OCTET_STRING,
                            GXCommon.logicalNameToBytes(it.getLogicalName()));
                    getAccessRights(settings, it, e.getServer(), data); // Access
                                                                        // rights.
                    settings.setIndex(settings.getIndex() + 1);
                    if (settings.isServer()) {
                        // If PDU is full.
                        if (!gbt && !e.isSkipMaxPduSize()
                                && data.size() >= settings.getMaxPduSize()) {
                            break;
                        }
                    }
                }
            }
            return data.array();
        } catch (Exception ex) {
            e.setError(ErrorCode.HARDWARE_FAULT);
            return null;
        }

    }

    private void getAccessRights(final GXDLMSSettings settings,
            final GXDLMSObject item, final GXDLMSServerBase server,
            final GXByteBuffer data) throws Exception {
        data.setUInt8(DataType.STRUCTURE.getValue());
        data.setUInt8(2);
        if (server == null) {
            data.setUInt8(DataType.ARRAY.getValue());
            data.setUInt8(0);
            data.setUInt8(DataType.ARRAY.getValue());
            data.setUInt8(0);
        } else {
            data.setUInt8(DataType.ARRAY.getValue());
            int cnt = item.getAttributeCount();
            data.setUInt8(cnt);
            ValueEventArgs e = new ValueEventArgs(server, item, 0, 0, null);
            for (int pos = 0; pos != cnt; ++pos) {
                e.setIndex(pos + 1);
                AccessMode m = server.notifyGetAttributeAccess(e);
                // attribute_access_item
                data.setUInt8(DataType.STRUCTURE.getValue());
                data.setUInt8(3);
                GXCommon.setData(settings, data, DataType.INT8, pos + 1);
                GXCommon.setData(settings, data, DataType.ENUM, m.getValue());
                GXCommon.setData(settings, data, DataType.NONE, null);
            }
            data.setUInt8(DataType.ARRAY.getValue());
            cnt = item.getMethodCount();
            data.setUInt8(cnt);
            for (int pos = 0; pos != cnt; ++pos) {
                e.setIndex(pos + 1);
                // attribute_access_item
                data.setUInt8(DataType.STRUCTURE.getValue());
                data.setUInt8(2);
                GXCommon.setData(settings, data, DataType.INT8, pos + 1);
                MethodAccessMode m = server.notifyGetMethodAccess(e);
                // If version is 0.
                if (getVersion() == 0) {
                    GXCommon.setData(settings, data, DataType.BOOLEAN,
                            m.getValue() != 0);
                } else {
                    GXCommon.setData(settings, data, DataType.ENUM,
                            m.getValue());
                }
            }
        }
    }

    static final void updateAccessRights(final GXDLMSObject obj,
            final List<?> buff) {
        if (buff != null && !buff.isEmpty()) {
            for (Object access : (List<?>) buff.get(0)) {
                List<?> attributeAccess = (List<?>) access;
                int id = ((Number) attributeAccess.get(0)).intValue();
                int tmp = ((Number) attributeAccess.get(1)).intValue();
                AccessMode aMode = AccessMode.forValue(tmp);
                obj.setAccess(id, aMode);
            }
            for (Object access : (List<?>) buff.get(1)) {
                List<?> methodAccess = (List<?>) access;
                int id = ((Number) methodAccess.get(0)).intValue();
                int tmp;
                // If version is 0.
                if (methodAccess.get(1) instanceof Boolean) {
                    if (((Boolean) methodAccess.get(1)).booleanValue()) {
                        tmp = 1;
                    } else {
                        tmp = 0;
                    }
                } else {
                    // If version is 1.
                    tmp = ((Number) methodAccess.get(1)).intValue();
                }
                MethodAccessMode mode = MethodAccessMode.forValue(tmp);
                obj.setMethodAccess(id, mode);
            }
        }
    }

    /*
     * Returns User list.
     */
    private GXByteBuffer getUserList(final GXDLMSSettings settings,
            final ValueEventArgs e) {
        GXByteBuffer data = new GXByteBuffer();
        // Add count only for first time.
        if (settings.getIndex() == 0) {
            settings.setCount(userList.size());
            data.setUInt8(DataType.ARRAY.getValue());
            GXCommon.setObjectCount(userList.size(), data);
        }
        int pos = 0;
        for (Entry<Byte, String> it : userList) {
            ++pos;
            if (!(pos <= settings.getIndex())) {
                settings.setIndex(settings.getIndex() + 1);
                data.setUInt8(DataType.STRUCTURE.getValue());
                data.setUInt8(2); // Count
                GXCommon.setData(settings, data, DataType.UINT8, it.getKey()); // Id
                GXCommon.setData(settings, data, DataType.STRING,
                        it.getValue()); // Name
            }
        }
        return data;
    }

    @Override
    public final DataType getDataType(final int index) {
        if (index == 1) {
            return DataType.OCTET_STRING;
        }
        if (index == 2) {
            return DataType.ARRAY;
        }
        if (index == 3) {
            return DataType.STRUCTURE;
        }
        if (index == 4) {
            return DataType.STRUCTURE;
        }
        if (index == 5) {
            return DataType.STRUCTURE;
        }
        if (index == 6) {
            return DataType.STRUCTURE;
        }
        if (index == 7) {
            return DataType.OCTET_STRING;
        }
        if (index == 8) {
            return DataType.ENUM;
        }
        if (getVersion() > 0 && index == 9) {
            return DataType.OCTET_STRING;
        }
        if (getVersion() > 1) {
            if (index == 10) {
                return DataType.ARRAY;
            }
            if (index == 11) {
                return DataType.STRUCTURE;
            }
        }
        throw new IllegalArgumentException(
                "getDataType failed. Invalid attribute index.");
    }

    /*
     * Returns value of given attribute.
     */
    @Override
    public final Object getValue(final GXDLMSSettings settings,
            final ValueEventArgs e) {
        if (e.getIndex() == 1) {
            return GXCommon.logicalNameToBytes(getLogicalName());
        }
        if (e.getIndex() == 2) {
            return getObjects(settings, e);
        }
        if (e.getIndex() == 3) {
            GXByteBuffer data = new GXByteBuffer();
            data.setUInt8(DataType.STRUCTURE.getValue());
            // Add count
            data.setUInt8(2);
            data.setUInt8(DataType.INT8.getValue());
            data.setUInt8(clientSAP);
            data.setUInt8(DataType.UINT16.getValue());
            data.setUInt16(serverSAP);
            return data.array();
        }
        if (e.getIndex() == 4) {
            GXByteBuffer data = new GXByteBuffer();
            data.setUInt8(DataType.STRUCTURE.getValue());
            // Add count
            data.setUInt8(0x7);
            GXCommon.setData(settings, data, DataType.UINT8,
                    applicationContextName.getJointIsoCtt());
            GXCommon.setData(settings, data, DataType.UINT8,
                    applicationContextName.getCountry());
            GXCommon.setData(settings, data, DataType.UINT16,
                    applicationContextName.getCountryName());
            GXCommon.setData(settings, data, DataType.UINT8,
                    applicationContextName.getIdentifiedOrganization());
            GXCommon.setData(settings, data, DataType.UINT8,
                    applicationContextName.getDlmsUA());
            GXCommon.setData(settings, data, DataType.UINT8,
                    applicationContextName.getApplicationContext());
            GXCommon.setData(settings, data, DataType.UINT8,
                    applicationContextName.getContextId().getValue());
            return data.array();
        }
        if (e.getIndex() == 5) {
            GXByteBuffer data = new GXByteBuffer();
            data.setUInt8(DataType.STRUCTURE.getValue());
            data.setUInt8(6);
            GXCommon.setData(settings, data, DataType.BITSTRING,
                    GXBitString.toBitString(Conformance
                            .toInteger(xDLMSContextInfo.getConformance()), 24));
            GXCommon.setData(settings, data, DataType.UINT16,
                    xDLMSContextInfo.getMaxReceivePduSize());
            GXCommon.setData(settings, data, DataType.UINT16,
                    xDLMSContextInfo.getMaxSendPduSize());
            GXCommon.setData(settings, data, DataType.UINT8,
                    xDLMSContextInfo.getDlmsVersionNumber());
            GXCommon.setData(settings, data, DataType.INT8,
                    xDLMSContextInfo.getQualityOfService());
            GXCommon.setData(settings, data, DataType.OCTET_STRING,
                    xDLMSContextInfo.getCypheringInfo());
            return data.array();
        }
        if (e.getIndex() == 6) {
            GXByteBuffer data = new GXByteBuffer();
            data.setUInt8(DataType.STRUCTURE.getValue());
            // Add count
            data.setUInt8(0x7);
            GXCommon.setData(settings, data, DataType.UINT8,
                    authenticationMechanismName.getJointIsoCtt());
            GXCommon.setData(settings, data, DataType.UINT8,
                    authenticationMechanismName.getCountry());
            GXCommon.setData(settings, data, DataType.UINT16,
                    authenticationMechanismName.getCountryName());
            GXCommon.setData(settings, data, DataType.UINT8,
                    authenticationMechanismName.getIdentifiedOrganization());
            GXCommon.setData(settings, data, DataType.UINT8,
                    authenticationMechanismName.getDlmsUA());
            GXCommon.setData(settings, data, DataType.UINT8,
                    authenticationMechanismName
                            .getAuthenticationMechanismName());
            GXCommon.setData(settings, data, DataType.UINT8,
                    authenticationMechanismName.getMechanismId().getValue());
            return data.array();
        }
        if (e.getIndex() == 7) {
            return secret;
        }
        if (e.getIndex() == 8) {
            return getAssociationStatus().ordinal();
        }
        if (e.getIndex() == 9) {
            return GXCommon.logicalNameToBytes(getSecuritySetupReference());
        }
        if (e.getIndex() == 10) {
            return getUserList(settings, e).array();
        }
        if (e.getIndex() == 11) {
            GXByteBuffer data = new GXByteBuffer();
            data.setUInt8(DataType.STRUCTURE.getValue());
            // Add structure size.
            data.setUInt8(2);
            if (currentUser == null) {
                GXCommon.setData(settings, data, DataType.UINT8, 0);
                GXCommon.setData(settings, data, DataType.STRING, null);
            } else {
                GXCommon.setData(settings, data, DataType.UINT8,
                        currentUser.getKey());
                GXCommon.setData(settings, data, DataType.STRING,
                        currentUser.getValue());
            }
            return data.array();
        }
        e.setError(ErrorCode.READ_WRITE_DENIED);
        return null;
    }

    static void updateObjectList(final GXDLMSSettings settings,
            final GXDLMSObjectCollection target, final Object value) {
        target.clear();
        if (value != null) {
            for (Object tmp : (List<?>) value) {
                List<?> item = (List<?>) tmp;
                ObjectType type =
                        ObjectType.forValue(((Number) item.get(0)).intValue());
                int version = ((Number) item.get(1)).intValue();
                String ln = GXCommon.toLogicalName((byte[]) item.get(2));
                GXDLMSObject obj = settings.getObjects().findByLN(type, ln);
                if (obj == null) {
                    obj = gurux.dlms.GXDLMSClient.createObject(type);
                    obj.setLogicalName(ln);
                    obj.setVersion(version);
                }
                // Add only known objects.
                if (obj instanceof IGXDLMSBase) {
                    updateAccessRights(obj, (List<?>) item.get(3));
                    target.add(obj);
                }
            }
        }
    }

    private void updateApplicationContextName(final Object value) {
        // Value of the object identifier encoded in BER
        if (value instanceof byte[]) {
            GXByteBuffer buff = new GXByteBuffer((byte[]) value);
            if (buff.getUInt8(0) == 0x60) {
                applicationContextName.setJointIsoCtt(0);
                applicationContextName.setCountry(0);
                applicationContextName.setCountryName(0);
                buff.position(buff.position() + 3);
                applicationContextName
                        .setIdentifiedOrganization(buff.getUInt8());
                applicationContextName.setDlmsUA(buff.getUInt8());
                applicationContextName.setApplicationContext(buff.getUInt8());
                applicationContextName.setContextId(
                        ApplicationContextName.forValue(buff.getUInt8()));
            } else {
                // Get Tag and length.
                if (buff.getUInt8() != 2 && buff.getUInt8() != 7) {
                    throw new IllegalArgumentException();
                }
                // Get tag
                if (buff.getUInt8() != 0x11) {
                    throw new IllegalArgumentException();
                }
                applicationContextName.setJointIsoCtt(buff.getUInt8());
                // Get tag
                if (buff.getUInt8() != 0x11) {
                    throw new IllegalArgumentException();
                }
                applicationContextName.setCountry(buff.getUInt8());
                // Get tag
                if (buff.getUInt8() != 0x12) {
                    throw new IllegalArgumentException();
                }
                applicationContextName.setCountryName(buff.getUInt16());
                // Get tag
                if (buff.getUInt8() != 0x11) {
                    throw new IllegalArgumentException();
                }
                applicationContextName
                        .setIdentifiedOrganization(buff.getUInt8());
                // Get tag
                if (buff.getUInt8() != 0x11) {
                    throw new IllegalArgumentException();
                }
                applicationContextName.setDlmsUA(buff.getUInt8());
                // Get tag
                if (buff.getUInt8() != 0x11) {
                    throw new IllegalArgumentException();
                }
                applicationContextName.setApplicationContext(buff.getUInt8());
                // Get tag
                if (buff.getUInt8() != 0x11) {
                    throw new IllegalArgumentException();
                }
                applicationContextName.setContextId(
                        ApplicationContextName.forValue(buff.getUInt8()));
            }
        } else {
            if (value != null) {
                List<?> arr = (List<?>) value;
                applicationContextName
                        .setJointIsoCtt(((Number) arr.get(0)).intValue());
                applicationContextName
                        .setCountry(((Number) arr.get(1)).intValue());
                applicationContextName
                        .setCountryName(((Number) arr.get(2)).intValue());
                applicationContextName.setIdentifiedOrganization(
                        ((Number) arr.get(3)).intValue());
                applicationContextName
                        .setDlmsUA(((Number) arr.get(4)).intValue());
                applicationContextName.setApplicationContext(
                        ((Number) arr.get(5)).intValue());
                applicationContextName.setContextId(ApplicationContextName
                        .forValue(((Number) arr.get(6)).intValue()));
            }
        }
    }

    private void updateAuthenticationMechanismName(final Object value) {
        if (value != null) {
            // Value of the object identifier encoded in BER
            if (value instanceof byte[]) {
                GXByteBuffer buff = new GXByteBuffer((byte[]) value);
                if (buff.getUInt8(0) == 0x60) {
                    authenticationMechanismName.setJointIsoCtt(0);
                    authenticationMechanismName.setCountry(0);
                    authenticationMechanismName.setCountryName(0);
                    buff.position(buff.position() + 3);
                    authenticationMechanismName
                            .setIdentifiedOrganization(buff.getUInt8());
                    authenticationMechanismName.setDlmsUA(buff.getUInt8());
                    authenticationMechanismName
                            .setAuthenticationMechanismName(buff.getUInt8());
                    authenticationMechanismName.setMechanismId(
                            Authentication.forValue(buff.getUInt8()));
                } else {
                    // Get Tag and length.
                    if (buff.getUInt8() != 2 && buff.getUInt8() != 7) {
                        throw new IllegalArgumentException();
                    }
                    // Get tag
                    if (buff.getUInt8() != 0x11) {
                        throw new IllegalArgumentException();
                    }
                    authenticationMechanismName.setJointIsoCtt(buff.getUInt8());
                    // Get tag
                    if (buff.getUInt8() != 0x11) {
                        throw new IllegalArgumentException();
                    }
                    authenticationMechanismName.setCountry(buff.getUInt8());
                    // Get tag
                    if (buff.getUInt8() != 0x12) {
                        throw new IllegalArgumentException();
                    }
                    authenticationMechanismName
                            .setCountryName(buff.getUInt16());
                    // Get tag
                    if (buff.getUInt8() != 0x11) {
                        throw new IllegalArgumentException();
                    }
                    authenticationMechanismName
                            .setIdentifiedOrganization(buff.getUInt8());
                    // Get tag
                    if (buff.getUInt8() != 0x11) {
                        throw new IllegalArgumentException();
                    }
                    authenticationMechanismName.setDlmsUA(buff.getUInt8());
                    // Get tag
                    if (buff.getUInt8() != 0x11) {
                        throw new IllegalArgumentException();
                    }
                    authenticationMechanismName
                            .setAuthenticationMechanismName(buff.getUInt8());
                    // Get tag
                    if (buff.getUInt8() != 0x11) {
                        throw new IllegalArgumentException();
                    }
                    authenticationMechanismName.setMechanismId(
                            Authentication.forValue(buff.getUInt8()));
                }
            } else {
                List<?> arr = (List<?>) value;
                authenticationMechanismName
                        .setJointIsoCtt(((Number) arr.get(0)).intValue());
                authenticationMechanismName
                        .setCountry(((Number) arr.get(1)).intValue());
                authenticationMechanismName
                        .setCountryName(((Number) arr.get(2)).intValue());
                authenticationMechanismName.setIdentifiedOrganization(
                        ((Number) arr.get(3)).intValue());
                authenticationMechanismName
                        .setDlmsUA(((Number) arr.get(4)).intValue());
                authenticationMechanismName.setAuthenticationMechanismName(
                        ((Number) arr.get(5)).intValue());
                authenticationMechanismName.setMechanismId(Authentication
                        .forValue(((Number) arr.get(6)).intValue()));
            }
        }
    }

    /*
     * Set value of given attribute.
     */
    @Override
    public final void setValue(final GXDLMSSettings settings,
            final ValueEventArgs e) {
        switch (e.getIndex()) {
        case 1:
            setLogicalName(GXCommon.toLogicalName(e.getValue()));
            break;
        case 2:
            updateObjectList(settings, objectList, e.getValue());
            break;
        case 3:
            if (e.getValue() != null) {
                List<?> tmp = (List<?>) e.getValue();
                clientSAP = ((Number) tmp.get(0)).shortValue();
                serverSAP = ((Number) tmp.get(1)).shortValue();
            }
            break;
        case 4:
            updateApplicationContextName(e.getValue());
            break;
        case 5:
            if (e.getValue() != null) {
                List<?> arr = (List<?>) e.getValue();
                xDLMSContextInfo.setConformance(Conformance
                        .forValue(((GXBitString) arr.get(0)).toInteger()));
                xDLMSContextInfo
                        .setMaxReceivePduSize(((Number) arr.get(1)).intValue());
                xDLMSContextInfo
                        .setMaxSendPduSize(((Number) arr.get(2)).intValue());
                xDLMSContextInfo.setDlmsVersionNumber(
                        ((Number) arr.get(3)).byteValue());
                xDLMSContextInfo
                        .setQualityOfService(((Number) arr.get(4)).intValue());
                xDLMSContextInfo.setCypheringInfo((byte[]) arr.get(5));
            }
            break;
        case 6:
            updateAuthenticationMechanismName(e.getValue());
            break;
        case 7:
            secret = (byte[]) e.getValue();
            break;
        case 8:
            if (e.getValue() == null) {
                setAssociationStatus(AssociationStatus.NON_ASSOCIATED);
            } else {
                setAssociationStatus(AssociationStatus
                        .values()[((Number) e.getValue()).intValue()]);
            }
            break;
        case 9:
            setSecuritySetupReference(
                    GXCommon.toLogicalName((byte[]) e.getValue()));
            break;
        case 10:
            userList.clear();
            if (e.getValue() != null) {
                for (Object tmp : (List<?>) e.getValue()) {
                    List<?> item = (List<?>) tmp;
                    userList.add(new GXSimpleEntry<Byte, String>(
                            (Byte) item.get(0), (String) item.get(1)));
                }
            }
            break;
        case 11:
            if (e.getValue() != null) {
                List<?> tmp = (List<?>) e.getValue();
                currentUser = new GXSimpleEntry<Byte, String>(
                        ((Number) tmp.get(0)).byteValue(), (String) tmp.get(1));
            } else {
                currentUser = null;
            }
            break;
        default:
            e.setError(ErrorCode.READ_WRITE_DENIED);
        }
    }

    @Override
    public final void load(final GXXmlReader reader) throws XMLStreamException {
        clientSAP = (byte) reader.readElementContentAsInt("ClientSAP");
        serverSAP = (byte) reader.readElementContentAsInt("ServerSAP");
        if (reader.isStartElement("ApplicationContextName", true)) {
            applicationContextName.setJointIsoCtt(
                    reader.readElementContentAsInt("JointIsoCtt"));
            applicationContextName
                    .setCountry(reader.readElementContentAsInt("Country"));
            applicationContextName.setCountryName(
                    reader.readElementContentAsInt("CountryName"));
            applicationContextName.setIdentifiedOrganization(
                    reader.readElementContentAsInt("IdentifiedOrganization"));
            applicationContextName
                    .setDlmsUA(reader.readElementContentAsInt("DlmsUA"));
            applicationContextName.setApplicationContext(
                    reader.readElementContentAsInt("ApplicationContext"));
            applicationContextName.setContextId(ApplicationContextName
                    .forValue(reader.readElementContentAsInt("ContextId")));
            reader.readEndElement("ApplicationContextName");
        }

        if (reader.isStartElement("XDLMSContextInfo", true)) {
            xDLMSContextInfo.setConformance(Conformance
                    .forValue(reader.readElementContentAsInt("Conformance")));
            xDLMSContextInfo.setMaxReceivePduSize(
                    reader.readElementContentAsInt("MaxReceivePduSize"));
            xDLMSContextInfo.setMaxSendPduSize(
                    reader.readElementContentAsInt("MaxSendPduSize"));
            xDLMSContextInfo.setDlmsVersionNumber(
                    (byte) reader.readElementContentAsInt("DlmsVersionNumber"));
            xDLMSContextInfo.setQualityOfService(
                    reader.readElementContentAsInt("QualityOfService"));
            xDLMSContextInfo.setCypheringInfo(GXDLMSTranslator.hexToBytes(
                    reader.readElementContentAsString("CypheringInfo")));
            reader.readEndElement("XDLMSContextInfo");
        }
        if (reader.isStartElement("AuthenticationMechanismName", true)
                || reader.isStartElement("XDLMSContextInfo", true)) {
            authenticationMechanismName.setJointIsoCtt(
                    reader.readElementContentAsInt("JointIsoCtt"));
            authenticationMechanismName
                    .setCountry(reader.readElementContentAsInt("Country"));
            authenticationMechanismName.setCountryName(
                    reader.readElementContentAsInt("CountryName"));
            authenticationMechanismName.setIdentifiedOrganization(
                    reader.readElementContentAsInt("IdentifiedOrganization"));
            authenticationMechanismName
                    .setDlmsUA(reader.readElementContentAsInt("DlmsUA"));
            authenticationMechanismName.setAuthenticationMechanismName(reader
                    .readElementContentAsInt("AuthenticationMechanismName"));
            authenticationMechanismName.setMechanismId(Authentication
                    .forValue(reader.readElementContentAsInt("MechanismId")));
            reader.readEndElement("AuthenticationMechanismName");
            reader.readEndElement("XDLMSContextInfo");
        }
        String str = reader.readElementContentAsString("Secret");
        if (str == null) {
            secret = null;
        } else {
            secret = GXDLMSTranslator.hexToBytes(str);
        }
        associationStatus = AssociationStatus.values()[reader
                .readElementContentAsInt("AssociationStatus")];
        securitySetupReference =
                reader.readElementContentAsString("SecuritySetupReference");
    }

    @Override
    public final void save(final GXXmlWriter writer) throws XMLStreamException {
        writer.writeElementString("ClientSAP", clientSAP);
        writer.writeElementString("ServerSAP", serverSAP);
        if (applicationContextName != null) {
            writer.writeStartElement("ApplicationContextName");
            writer.writeElementString("JointIsoCtt",
                    applicationContextName.getJointIsoCtt());
            writer.writeElementString("Country",
                    applicationContextName.getCountry());
            writer.writeElementString("CountryName",
                    applicationContextName.getCountryName());
            writer.writeElementString("IdentifiedOrganization",
                    applicationContextName.getIdentifiedOrganization());
            writer.writeElementString("DlmsUA",
                    applicationContextName.getDlmsUA());
            writer.writeElementString("ApplicationContext",
                    applicationContextName.getApplicationContext());
            writer.writeElementString("ContextId",
                    applicationContextName.getContextId().getValue());
            writer.writeEndElement();
        }
        if (xDLMSContextInfo != null) {
            writer.writeStartElement("XDLMSContextInfo");
            writer.writeElementString("Conformance",
                    Conformance.toInteger(xDLMSContextInfo.getConformance()));
            writer.writeElementString("MaxReceivePduSize",
                    xDLMSContextInfo.getMaxReceivePduSize());
            writer.writeElementString("MaxSendPduSize",
                    xDLMSContextInfo.getMaxSendPduSize());
            writer.writeElementString("DlmsVersionNumber",
                    xDLMSContextInfo.getDlmsVersionNumber());
            writer.writeElementString("QualityOfService",
                    xDLMSContextInfo.getQualityOfService());
            writer.writeElementString("CypheringInfo", GXDLMSTranslator
                    .toHex(xDLMSContextInfo.getCypheringInfo()));
            writer.writeEndElement();
        }
        if (authenticationMechanismName != null) {
            writer.writeStartElement("AuthenticationMechanismName");
            writer.writeElementString("JointIsoCtt",
                    authenticationMechanismName.getJointIsoCtt());
            writer.writeElementString("Country",
                    authenticationMechanismName.getCountry());
            writer.writeElementString("CountryName",
                    authenticationMechanismName.getCountryName());
            writer.writeElementString("IdentifiedOrganization",
                    authenticationMechanismName.getIdentifiedOrganization());
            writer.writeElementString("DlmsUA",
                    authenticationMechanismName.getDlmsUA());
            writer.writeElementString("AuthenticationMechanismName",
                    authenticationMechanismName
                            .getAuthenticationMechanismName());
            writer.writeElementString("MechanismId",
                    authenticationMechanismName.getMechanismId().getValue());
            writer.writeEndElement();
        }
        writer.writeElementString("Secret", GXDLMSTranslator.toHex(secret));
        writer.writeElementString("AssociationStatus",
                associationStatus.ordinal());
        writer.writeElementString("SecuritySetupReference",
                securitySetupReference);
    }

    @Override
    public final void postLoad(final GXXmlReader reader) {
    }

    public List<Entry<Byte, String>> getUserList() {
        return userList;
    }

    public void setUserList(List<Entry<Byte, String>> userList) {
        this.userList = userList;
    }

    public Entry<Byte, String> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Entry<Byte, String> currentUser) {
        this.currentUser = currentUser;
    }
}