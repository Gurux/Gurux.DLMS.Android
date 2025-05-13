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
// More information of Gurux products: http://www.gurux.org
//
// This code is licensed under the GNU General Public License v2.
// Full text may be retrieved at http://www.gnu.org/licenses/gpl-2.0.txt
//---------------------------------------------------------------------------

package gurux.dlms.android;

import androidx.annotation.NonNull;

import gurux.common.IGXMedia;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.InterfaceType;
import gurux.dlms.enums.Security;
import gurux.dlms.manufacturersettings.GXAuthentication;
import gurux.dlms.manufacturersettings.HDLCAddressType;
import gurux.dlms.objects.GXDLMSObjectCollection;
import gurux.dlms.objects.enums.SecuritySuite;

/**
 * DLMS meter settings.
 */
public class GXDevice {
    /*
     * Flag name of the manufacturer.
     */
    private String mManufacturer;

    /*
     * Wait time in seconds.
     */
    private int mWaitTime = 5;
    /*
     * Maximum used baud rate.
     */
    private int mMaximumBaudRate;

    /*
     * Used authentication.
     */
    private GXAuthentication authentication;

    /*
     * Is logican name referencing used.
     */
    private boolean mUseLN = true;

    /*
     * Password is used only if authentication is used.
     */
    private byte[] password;

    /*
     * Used security level.
     */
    private Security mSecurity = Security.NONE;

    /*
     * Used security suite.
     */
    private SecuritySuite mSecuritySuite = SecuritySuite.SUITE_0;

    /*
     * System Title.
     */
    private byte[] mSystemTitle;

    /*
     *Meter system Title.
     */
    private byte[] mMeterSystemTitle;

    /**
     * Block cipher key.
     */
    private byte[] mBlockCipherKey;

    /*
     * Authentication key.
     */
    private byte[] mAuthenticationKey;

    /*
     * Dedicated key.
     */
    private byte[] mDedicatedKey;

    /*
     * Challenge.
     */
    private byte[] mChallenge;

    /**
     * Logical name of the invocation counter.
     */
    private String mInvocationCounter;

    /*
     * Client address.
     */
    private int mClientAddress;

    /*
     * Physical address.
     */
    private int mPhysicalAddress = 1;
    /*
     * Logical address.
     */
    private int mLogicalAddress;

    /**
     * Interface type.
     */
    private InterfaceType interfaceType = InterfaceType.HDLC;

    /*
     * Address type.
     */
    private HDLCAddressType mAddressType = HDLCAddressType.DEFAULT;

    /**
     * Meter conformance.
     */
    private int mConformance = 0;
    /*
     * COSEM objects.
     */
    private GXDLMSObjectCollection mObjects = new GXDLMSObjectCollection();

    /*
     * Media.
     */
    private IGXMedia mMedia;

    /**
     * @return Wait time.
     */
    public final int getWaitTime() {
        return mWaitTime;
    }

    /**
     * @param value Wait time.
     */
    public final void setWaitTime(int value) {
        mWaitTime = value;
    }

    /**
     * @return Maximum used baud rate.
     */
    public final int getMaximumBaudRate() {
        return mMaximumBaudRate;
    }

    /**
     * @param value Maximum used baud rate.
     */
    public final void setMaximumBaudRate(int value) {
        mMaximumBaudRate = value;
    }

    /**
     * @return Used authentication.
     */
    public final GXAuthentication getAuthentication() {
        return authentication;
    }

    /**
     * @param value Used authentication.
     */
    public final void setAuthentication(final GXAuthentication value) {
        authentication = value;
    }

    /**
     * @return Used password.
     */
    public final byte[] getPassword() {
        return password;
    }

    /**
     * @param value Used password.
     */
    public final void setPassword(final byte[] value) {
        password = value;
    }

    /**
     * @return Used security.
     */
    public final Security getSecurity() {
        return mSecurity;
    }

    /**
     * @param value Used security.
     */
    public final void setSecurity(Security value) {
        mSecurity = value;
    }

    /**
     * @return Used system title.
     */
    public final byte[] getSystemTitle() {
        return mSystemTitle;
    }

    /**
     * @param value Used system title.
     */
    public final void setSystemTitle(final byte[] value) {
        mSystemTitle = value;
    }


    /**
     * @return Block cipher key.
     */
    public final byte[] getBlockCipherKey() {
        return mBlockCipherKey;
    }

    /**
     * @param value Block cipher key.
     */
    public final void setBlockCipherKey(final byte[] value) {
        mBlockCipherKey = value;
    }

    /**
     * @return Authentication key.
     */
    public final byte[] getAuthenticationKey() {
        return mAuthenticationKey;
    }

    /**
     * @param value Authentication key.
     */
    public final void setAuthenticationKey(final byte[] value) {
        mAuthenticationKey = value;
    }


    /**
     * @return Physical address.
     */
    public final int getPhysicalAddress() {
        return mPhysicalAddress;
    }

    /**
     * @param value Physical address.
     */
    public final void setPhysicalAddress(int value) {
        mPhysicalAddress = value;
    }

    /**
     * @return Logical address.
     */
    public final int getLogicalAddress() {
        return mLogicalAddress;
    }

    /**
     * @param value Logical address.
     */
    public final void setLogicalAddress(int value) {
        mLogicalAddress = value;
    }

    /**
     * @return Flag name of the manufacturer.
     */
    public String getManufacturer() {
        return mManufacturer;
    }

    /**
     * @param value Flag name of the manufacturer.
     */
    public void setManufacturer(String value) {
        mManufacturer = value;
    }

    /**
     * @return Client address.
     */
    public int getClientAddress() {
        return mClientAddress;
    }

    /**
     * @param value Client address.
     */
    public void setClientAddress(int value) {
        mClientAddress = value;
    }

    /**
     * @return Address type.
     */
    public HDLCAddressType getAddressType() {
        return mAddressType;
    }

    /**
     * @param value Address type.
     */
    public void setAddressType(HDLCAddressType value) {
        mAddressType = value;
    }

    /**
     * @return COSEM objects.
     */
    public GXDLMSObjectCollection getObjects() {
        return mObjects;
    }

    /**
     * @param value COSEM objects.
     */
    public void setObjects(GXDLMSObjectCollection value) {
        mObjects = value;
    }

    /**
     * @return Media.
     */
    public IGXMedia getMedia() {
        return mMedia;
    }

    /**
     * @param value Media.
     */
    public void setMedia(IGXMedia value) {
        mMedia = value;
    }

    /**
     * @return Is logican name referencing used.
     */
    public boolean isLogicalNameReferencing() {
        return mUseLN;
    }

    /**
     * @param value Is logican name referencing used.
     */
    public void setLogicalNameReferencing(boolean value) {
        mUseLN = value;
    }

    /**
     * Constructor.
     */
    public GXDevice() {
        authentication = new GXAuthentication(Authentication.NONE, 16);
    }

    /**
     * @return Interface type.
     */
    public InterfaceType getInterfaceType() {
        return interfaceType;
    }

    /**
     * @param value Interface type.
     */
    public void setInterfaceType(final InterfaceType value) {
        interfaceType = value;
    }

    /**
     * @return Logical name of the invocation counter.
     */
    public String getInvocationCounter() {
        return mInvocationCounter;
    }

    /**
     * @param value Logical name of the invocation counter.
     */
    public void setInvocationCounter(final String value) {
        mInvocationCounter = value;
    }

    /**
     * @return Security suite.
     */
    @NonNull
    public SecuritySuite getSecuritySuite() {
        return mSecuritySuite;
    }

    /**
     * @param value Security suite.
     */
    public void setSecuritySuite(@NonNull final SecuritySuite value) {
        mSecuritySuite = value;
    }

    /**
     * @return Meter system Title.
     */
    public byte[] getMeterSystemTitle() {
        return mMeterSystemTitle;
    }

    /**
     * @param value Meter system Title.
     */
    public void setMeterSystemTitle(final byte[] value) {
        mMeterSystemTitle = value;
    }

    /**
     * @return Dedicated key.
     */
    public byte[] getDedicatedKey() {
        return mDedicatedKey;
    }

    /**
     * @param value Dedicated key.
     */
    public void setDedicatedKey(final byte[] value) {
        mDedicatedKey = value;
    }

    /**
     * @return Challenge
     */
    public byte[] getChallenge() {
        return mChallenge;
    }

    /**
     * @param value Challenge
     */
    public void setChallenge(final byte[] value) {
        this.mChallenge = value;
    }

    /**
     * @return Meter conformance.
     */
    public int getConformance() {
        return mConformance;
    }

    /**
     * @param value Meter conformance.
     */
    public void setConformance(final int value) {
        mConformance = value;
    }
}
