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

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.Security;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;
import gurux.dlms.manufacturersettings.HDLCAddressType;
import gurux.dlms.manufacturersettings.StartProtocolType;
import gurux.serial.GXSerial;
import gurux.serial.GXPort;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    GXDevice mDevice = new GXDevice();
    private GXManufacturerCollection mManufacturers = new GXManufacturerCollection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDevice.setMedia(new GXSerial(this));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
                Snackbar.make(view, "Settings Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadSettings();
        Fragment fragment = null;
        if (mDevice.getMedia() instanceof GXSerial){
            GXSerial s = ((GXSerial)mDevice.getMedia());
            GXPort port = s.getPort();
            if (!isPortAvailable(s.getPorts(), s.getPort())){
                fragment = ((GXSerial) mDevice.getMedia()).properties();
            }
        }
        if (fragment == null){
            String name = mDevice.getManufacturer();
            if (name == null || "".equals(name)){
                fragment = GXSettings.newInstance(mDevice, mManufacturers);
            }
        }
        if (fragment == null){
            fragment = GXMain.newInstance(mDevice);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, fragment);
        ft.commit();
        GXManufacturerCollection.readManufacturerSettings(this, mManufacturers);
        String name = mDevice.getManufacturer();
    }

    private void loadSettings() {
        SharedPreferences s = getPreferences(Context.MODE_PRIVATE);
        //Read mDevice settings.
        String man = s.getString("manufacturer", "");
        if (!man.isEmpty()) {
            mDevice.setManufacturer(man);
            mDevice.setStartProtocol(StartProtocolType.values()[s.getInt("protocol", 0)]);
            mDevice.setWaitTime(s.getInt("waitTime", 7000));
            mDevice.setMaximumBaudRate(s.getInt("maximumBaudRate", 0));
            mDevice.setAuthentication(Authentication.forValue(s.getInt("authentication", 0)));
            mDevice.setPassword(s.getString("password", ""));
            mDevice.setSecurity(Security.forValue(s.getInt("security", 0)));
            mDevice.setSystemTitle(s.getString("systemTitle", ""));
            mDevice.setBlockCipherKey(s.getString("blockCipherKey", ""));
            mDevice.setAuthenticationKey(s.getString("authenticationKey", ""));
            mDevice.setClientAddress(s.getInt("clientAddress", 16));
            mDevice.setAddressType(HDLCAddressType.values()[s.getInt("addressType", 0)]);
            mDevice.setPhysicalAddress(s.getInt("physicalAddress", 1));
            mDevice.setLogicalAddress(s.getInt("logicalAddress", 0));
        }
        //Read media settings.
        String mediaSettings = s.getString("mediaSettings", null);
        mDevice.getMedia().setSettings(mediaSettings);
        try {
            mDevice.getObjects().setXml(s.getString("objects", null));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSettings() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("manufacturer", mDevice.getManufacturer());
        editor.putInt("protocol", mDevice.getStartProtocol().ordinal());
        editor.putInt("waitTime", mDevice.getWaitTime());
        editor.putInt("maximumBaudRate", mDevice.getMaximumBaudRate());
        editor.putInt("authentication", mDevice.getAuthentication().getValue());
        editor.putString("password", mDevice.getPassword());
        editor.putInt("security", mDevice.getSecurity().getValue());
        editor.putString("systemTitle", mDevice.getSystemTitle());
        editor.putString("blockCipherKey", mDevice.getBlockCipherKey());
        editor.putString("authenticationKey", mDevice.getAuthenticationKey());
        editor.putInt("clientAddress", mDevice.getClientAddress());
        editor.putInt("addressType", mDevice.getAddressType().getValue());
        editor.putInt("physicalAddress", mDevice.getPhysicalAddress());
        editor.putInt("logicalAddress", mDevice.getLogicalAddress());
        editor.putString("mediaSettings", mDevice.getMedia().getSettings());
        editor.putString("objects", mDevice.getObjects().getXml());
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check is serial port available.
     * @param ports
     * @param port
     * @return
     */
    boolean isPortAvailable(final GXPort[] ports, final GXPort port){
        if (port == null){
            return false;
        }
        for(GXPort it : ports){
            if (it.getPort().equals(port.getPort())){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;//declaring fragment object
        int id = item.getItemId();
        Intent i;
        if (id == R.id.nav_read) {
            fragment = GXMain.newInstance(mDevice);
        } else if (id == R.id.nav_obis_translator) {
            fragment = new GXObisTranslator();
        } else if (id == R.id.nav_xml_translator) {
            fragment = new GXXmlTranslator();
        } else if (id == R.id.nav_manufacturers) {
            fragment = GXManufacturers.newInstance(mManufacturers);
        } else if (id == R.id.nav_read) {
        } else if (id == R.id.nav_meterSettings) {
            fragment = GXSettings.newInstance(mDevice, mManufacturers);
        } else if (id == R.id.nav_mediaSettings) {
            fragment = ((GXSerial) mDevice.getMedia()).properties();
        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_share) {

        }
        if (fragment != null) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        saveSettings();
        if (mDevice.getMedia() != null) {
            mDevice.getMedia().close();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mDevice.getMedia() != null) {
            mDevice.getMedia().close();
            mDevice.setMedia(null);
        }
        super.onStop();
    }
}
