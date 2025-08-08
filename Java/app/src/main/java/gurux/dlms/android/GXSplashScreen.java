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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import gurux.dlms.GXDLMSConverter;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;

/**
 * Show splashscreen and load necessary content.
 */
public class GXSplashScreen extends Activity {

    private TextView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        loading = findViewById(R.id.loading);
        //Showing splashscreen while downloading necessary data before launching the app.
        Thread thread = new Thread(() -> {
            //Read OBIS codes.
            if (GXDLMSConverter.isFirstRun(this)) {
                runOnUiThread(() -> loading.setText(R.string.loading_obis_codes));
                GXDLMSConverter c = new GXDLMSConverter();
                try {
                    c.update(this);
                } catch (Exception e) {
                    GXGeneral.showError(this, e, "Failed to read OBIS codes from the server.");
                }
            }
            //Read Manufacturer settings.
            try {
                GXManufacturerCollection man = new GXManufacturerCollection();
                runOnUiThread(() -> loading.setText(R.string.loading_manufacturer_settings));
                if (GXManufacturerCollection.isFirstRun(this) ||
                        man.isUpdatesAvailable(this)) {
                    GXManufacturerCollection.updateManufactureSettings(this);
                }
            } catch (Exception e) {
                GXGeneral.showError(this, e, "Failed to read manufacturer settings from the server.");
            }
            Intent i = new Intent(GXSplashScreen.this, MainActivity.class);
            startActivity(i);
            finish();
        });

        thread.start();
    }
}
