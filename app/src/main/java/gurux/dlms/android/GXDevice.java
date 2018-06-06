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

import android.hardware.usb.UsbDevice;
import android.os.Parcel;
import android.os.Parcelable;

import gurux.common.IGXMedia;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.Security;
import gurux.dlms.manufacturersettings.HDLCAddressType;
import gurux.dlms.manufacturersettings.StartProtocolType;
import gurux.dlms.objects.GXDLMSObjectCollection;

/**
 * DLMS meter settings.
 */
public class GXDevice implements Parcelable {
    public static final Creator<UsbDevice> CREATOR = null;
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
    private Authentication authentication = Authentication.NONE;

    /*
     * Is logican name referencing used.
     */
    private boolean mUseLN = true;

    /*
     * Password is used only if authentication is used.
     */
    private String password = "";

    /*
     * Used communication security.
     */
    private Security security = Security.NONE;

    /*
     * System Title.
     */
    private String mSystemTitle;
    /**
     * Block cipher key.
     */
    private String mBlockCipherKey;

    /*
     * Authentication key.
     */
    private String mAuthenticationKey;

    /*
     * Client address.
     */
    private int mClientAddress;

    /*
     * Physical address.
     */
    private int mPhysicalAddress;
    /*
     * Logical address.
     */
    private int mLogicalAddress;
    /*
     * Start protocol type.
     */
    private StartProtocolType mStartProtocol = StartProtocolType.IEC;

    /*
     * Address type.
     */
    private HDLCAddressType mAddressType = HDLCAddressType.DEFAULT;

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
    public final Authentication getAuthentication() {
        return authentication;
    }

    /**
     * @param value Used authentication.
     */
    public final void setAuthentication(Authentication value) {
        authentication = value;
    }

    /**
     * @return Used password.
     */
    public final String getPassword() {
        return password;
    }

    /**
     * @param value Used password.
     */
    public final void setPassword(String value) {
        password = value;
    }

    /**
     * @return Used security.
     */
    public final Security getSecurity() {
        return security;
    }

    /**
     * @param value Used security.
     */
    public final void setSecurity(Security value) {
        security = value;
    }

    /**
     * @return Used system title.
     */
    public final String getSystemTitle() {
        return mSystemTitle;
    }

    /**
     * @param value Used system title.
     */
    public final void setSystemTitle(String value) {
        mSystemTitle = value;
    }


    /**
     * @return Block cipher key.
     */
    public final String getBlockCipherKey() {
        return mBlockCipherKey;
    }

    /**
     * @param value Block cipher key.
     */
    public final void setBlockCipherKey(String value) {
        mBlockCipherKey = value;
    }

    /**
     * @return Authentication key.
     */
    public final String getAuthenticationKey() {
        return mAuthenticationKey;
    }

    /**
     * @param value Authentication key.
     */
    public final void setAuthenticationKey(String value) {
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
     * @return Start protocol.
     */
    public StartProtocolType getStartProtocol() {
        return mStartProtocol;
    }

    /**
     * @param value Start protocol.
     */
    public void setStartProtocol(StartProtocolType value) {
        mStartProtocol = value;
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
     *
     * @return Is logican name referencing used.
     */
    public boolean isLogicalNameReferencing() {
        return mUseLN;
    }

    /**
     *
     * @param value Is logican name referencing used.
     */
    public void setLogicalNameReferencing(boolean value ) {
        mUseLN = value ;
    }

    @Override
    public int describeContents() {
       return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
