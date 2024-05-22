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

package gurux.dlms.manufacturersettings;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;

import gurux.dlms.enums.InterfaceType;
import gurux.dlms.enums.ObjectType;
import gurux.dlms.internal.AutoResetEvent;
import gurux.dlms.internal.GXCommon;

public class GXManufacturerCollection
        extends java.util.ArrayList<GXManufacturer> implements Parcelable {
    public static final Creator<UsbDevice> CREATOR = null;

    /**
     * * Find manufacturer settings by manufacturer id.
     *
     * @param id Manufacturer id.
     * @return found manufacturer or null.
     */
    public final GXManufacturer findByIdentification(final String id) {
        for (GXManufacturer it : this) {
            if (it.getIdentification().compareToIgnoreCase(id) == 0) {
                return it;
            }
        }
        return null;
    }

    /**
     * @param context Context.
     * @return True, if this is the first run.
     */
    public static boolean isFirstRun(final Context context) {
        File dir = context.getFilesDir();
        String[] files = dir.list();
        if (files != null) {
            for (String it : files) {
                if (it.equalsIgnoreCase("files.xml")) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if there are any updates available in Gurux www server.
     *
     * @param context Context.
     * @return Returns true if there are any updates available.
     */
    public boolean isUpdatesAvailable(final Context context) throws Exception {
        if (isFirstRun(context)) {
            return true;
        }
        final Exception[] mException = {null};
        final boolean[] mUpdates = {false};
        AutoResetEvent completed = new AutoResetEvent(false);
        Thread thread = new Thread(() -> {
            try {
                Map<String, Date> installed = new HashMap<>();
                Map<String, Date> available = new HashMap<>();
                DateFormat format = new SimpleDateFormat("MM-dd-yyyy");
                try (java.io.FileInputStream tmp = context.openFileInput("files.xml")) {
                    XmlPullParser parser = Xml.newPullParser();
                    URL url = new URL("https://www.gurux.fi/obis/files.xml");
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(tmp, null);
                    int event;
                    parser.nextTag();
                    while ((event = parser.next()) != XmlPullParser.END_TAG) {
                        if (event == XmlPullParser.START_TAG) {
                            String target = parser.getName();
                            if (target.equalsIgnoreCase("file")) {
                                String data = parser.getAttributeValue(null, "Modified");
                                installed.put(readText(parser), format.parse(data));
                            }
                        }
                    }
                    URLConnection c = url.openConnection();
                    try (InputStream io = c.getInputStream()) {
                        parser.setInput(io, null);
                        parser.nextTag();
                        while ((event = parser.next()) != XmlPullParser.END_TAG) {
                            if (event == XmlPullParser.START_TAG) {
                                String target = parser.getName();
                                if (target.equalsIgnoreCase("file")) {
                                    String data = parser.getAttributeValue(null, "Modified");
                                    available.put(readText(parser), format.parse(data));
                                }
                            }
                        }
                    }
                    for (Map.Entry<String, Date> it : available.entrySet()) {
                        // If new item is added.
                        if (!installed.containsKey(it.getKey())) {
                            mUpdates[0] = true;
                            return;
                        }
                        // If item is updated.
                        if (it.getValue().compareTo(installed.get(it.getKey())) != 0) {
                            mUpdates[0] = true;
                            return;
                        }
                    }
                    mException[0] = null;
                }
            } catch (Exception e) {
                mUpdates[0] = true;
                mException[0] = e;
            }
            completed.set();
        });
        thread.start();
        completed.waitOne(60000);
        if (mException[0] != null) {
            throw mException[0];
        }
        return mUpdates[0];
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Update manufacturer settings from the Gurux www server.
     *
     * @param context Context.
     */
    public static void updateManufactureSettings(final Context context) throws Exception {
        final Exception[] exception = {null};
        AutoResetEvent completed = new AutoResetEvent(false);
        Thread thread = new Thread(() -> {
            try {
                String line, newline;
                String path = "files.xml";
                URL url = new URL("https://www.gurux.fi/obis/files.xml");
                URLConnection c = url.openConnection();
                c.setDoInput(true);
                c.setDoOutput(true);
                try (InputStream io = c.getInputStream()) {
                    try (InputStreamReader r = new InputStreamReader(io, StandardCharsets.UTF_8)) {
                        BufferedReader reader = new BufferedReader(r);
                        try (FileOutputStream writer = context.openFileOutput(path, MODE_PRIVATE)) {
                            newline = System.getProperty("line.separator");
                            while ((line = reader.readLine()) != null) {
                                writer.write(line.getBytes());
                                writer.write(newline.getBytes());
                            }
                            r.close();
                        }
                    }
                }
                XmlPullParser parser = Xml.newPullParser();
                try (java.io.FileInputStream tmp = context.openFileInput(path)) {
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(tmp, null);
                    int event;
                    parser.nextTag();
                    while ((event = parser.next()) != XmlPullParser.END_TAG) {
                        if (event == XmlPullParser.START_TAG) {
                            String target = parser.getName();
                            if (target.equalsIgnoreCase("file")) {
                                String file = readText(parser);
                                URL f = new URL("https://www.gurux.fi/obis/" + file);
                                c = f.openConnection();
                                try (BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()))) {
                                    try (FileOutputStream writer = context.openFileOutput(file, MODE_PRIVATE)) {
                                        while ((line = reader.readLine()) != null) {
                                            writer.write(line.getBytes());
                                            writer.write(newline.getBytes());
                                        }
                                        reader.close();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("gurux.dlms", e.getMessage());
                exception[0] = e;
            }
            completed.set();
        });
        thread.start();
        completed.waitOne(60000);
        if (exception[0] != null) {
            throw exception[0];
        }
    }

    public static void readManufacturerSettings(
            final Context context,
            final GXManufacturerCollection manufacturers) {
        manufacturers.clear();
        File dir = context.getFilesDir();
        String[] files = dir.list();
        // Either directory does not exist or is not a directory
        if (files != null) {
            for (String it : files) {
                if (it.endsWith(".obx")) {
                    try {
                        try (java.io.FileInputStream in = context.openFileInput(it)) {
                            manufacturers.add(parse(in));
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        Collections.sort(manufacturers);
    }

    /**
     * Serialize manufacturer from the xml.
     *
     * @param in Input stream.
     * @return Serialized manufacturer.
     * @throws Exception
     */
    private static GXManufacturer parse(final InputStream in) throws Exception {
        GXManufacturer man = null;
        Attributes attributes = new Attributes();
        GXAuthentication authentication = null;
        GXServerAddress serveraddress = null;
        GXObisCode obisCode = null;
        GXDLMSAttribute att = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        int event;
        while ((event = parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                attributes.clear();
                String target = parser.getName();
                if (target.equalsIgnoreCase("GXManufacturer")) {
                    man = new GXManufacturer();
                } else if (target.equalsIgnoreCase("GXObisCode")) {
                    att = null;
                    obisCode = new GXObisCode();
                    man.getObisCodes().add(obisCode);
                } else if (target.equalsIgnoreCase("GXAuthentication")) {
                    att = null;
                    authentication = new GXAuthentication();
                    man.getSettings().add(authentication);
                } else if (target.equalsIgnoreCase("GXServerAddress")) {
                    serveraddress = new GXServerAddress();
                    man.getServerSettings().add(serveraddress);
                } else if (target.equalsIgnoreCase("GXDLMSAttributeSettings")) {
                    att = new GXDLMSAttribute();
                    obisCode.getAttributes().add(att);
                } else if (target.equalsIgnoreCase("Identification")) {
                    man.setIdentification(readText(parser));
                } else if (target.equalsIgnoreCase("Name")) {
                    if (att != null) {
                        att.setName(readText(parser));
                    } else if (authentication != null) {
                        authentication.setName(readText(parser));
                    } else {
                        man.setName(readText(parser));
                    }
                } else if (target.equalsIgnoreCase("UseLN")) {
                    man.setUseLogicalNameReferencing(
                            Boolean.parseBoolean(readText(parser)));
                } else if (target.equalsIgnoreCase("UseIEC47")) {
                    man.setUseIEC47(Boolean.parseBoolean(readText(parser)));
                } else if (target.equalsIgnoreCase("ClientID")) {
                    if (authentication != null) {
                        authentication.setClientAddress(Integer.parseInt(readText(parser)));
                    }
                } else if (target.equalsIgnoreCase("PhysicalAddress")) {
                    if (serveraddress != null) {
                        serveraddress
                                .setPhysicalAddress(Integer.parseInt(readText(parser)));
                    }
                } else if (target.equalsIgnoreCase("LogicalAddress")) {
                    if (serveraddress != null) {
                        serveraddress.setLogicalAddress(Integer.parseInt(readText(parser)));
                    }
                } else if (target.equalsIgnoreCase("Formula")) {
                    if (serveraddress != null) {
                        serveraddress.setFormula(readText(parser));
                    }
                } else if (target.equalsIgnoreCase("HDLCAddress")) {
                    if (serveraddress != null) {
                        serveraddress.setHDLCAddress(HDLCAddressType
                                .forValue(Integer.parseInt(readText(parser))));
                    }
                } else if (target.equalsIgnoreCase("Selected")) {
                    // Old functionality.
                    continue;
                } else if (target.equalsIgnoreCase("InactivityMode")) {
                    man.setInactivityMode(
                            gurux.dlms.manufacturersettings.InactivityMode
                                    .valueOf(readText(parser).toUpperCase()));
                } else if (target.equalsIgnoreCase("Type")) {
                    if (authentication != null) {
                        authentication.setType(gurux.dlms.enums.Authentication
                                .valueOfString(readText(parser)));
                    } else {
                        String str = readText(parser);
                        if (str.equals("BinaryCodedDesimal")) {
                            att.setType(gurux.dlms.enums.DataType.BCD);
                        } else if (str.equals("OctetString")) {
                            att.setType(gurux.dlms.enums.DataType.OCTET_STRING);
                        } else {
                            att.setType(gurux.dlms.enums.DataType
                                    .valueOf(str.toUpperCase()));
                        }
                    }
                } else if (target.equalsIgnoreCase("UIType")) {
                    String str = readText(parser);
                    if (str.equals("BinaryCodedDesimal")) {
                        att.setType(gurux.dlms.enums.DataType.BCD);
                    } else if (str.equals("OctetString")) {
                        att.setType(gurux.dlms.enums.DataType.OCTET_STRING);
                    } else {
                        att.setUIType(gurux.dlms.enums.DataType
                                .valueOf(str.toUpperCase()));
                    }
                } else if (target.equalsIgnoreCase("GXAuthentication")) {
                    authentication = null;
                } else if (target.equalsIgnoreCase("LogicalName")) {
                    obisCode.setLogicalName(readText(parser));
                } else if (target.equalsIgnoreCase("Description")) {
                    obisCode.setDescription(readText(parser));
                } else if (target.equalsIgnoreCase("ObjectType")) {
                    obisCode.setObjectType(
                            ObjectType.forValue(Integer.parseInt(readText(parser))));
                } else if (target.equalsIgnoreCase("Interface")) {
                    // Old way. ObjectType is used now.
                    continue;
                } else if (target.equalsIgnoreCase("Index")) {
                    att.setIndex(Integer.parseInt(readText(parser)));
                } else if (target.equalsIgnoreCase("StartProtocol")) {
                    man.setStartProtocol(
                            StartProtocolType.valueOf(readText(parser).toUpperCase()));
                } else if (target.equalsIgnoreCase("WebAddress")) {
                    man.setWebAddress(readText(parser));
                } else if (target.equalsIgnoreCase("Info")) {
                    man.setInfo(readText(parser));
                } else if (target.equalsIgnoreCase("SystemTitle")) {
                    man.setSystemTitle(GXCommon.hexToBytes(readText(parser)));
                } else if (target.equalsIgnoreCase("BlockCipherKey")) {
                    man.setBlockCipherKey(GXCommon.hexToBytes(readText(parser)));
                } else if (target.equalsIgnoreCase("AuthenticationKey")) {
                    man.setAuthenticationKey(GXCommon.hexToBytes(readText(parser)));
                } else if (target.equalsIgnoreCase("SupporterdInterfaces")) {
                    man.getSupporterdInterfaces().addAll(InterfaceType.getInterfaceTypes(Integer.parseInt(readText(parser))));
                }
            }
        }
        return man;
    }

    @Override
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        throw new RuntimeException("Stub!");
    }
}