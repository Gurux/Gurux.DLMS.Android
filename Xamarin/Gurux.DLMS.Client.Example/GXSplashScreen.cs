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

using Android.App;
using Android.OS;
using AndroidX.AppCompat.App;
using Android.Content;
using Android.Util;
using System.Threading.Tasks;
using Gurux.DLMS.Client.Example.UI;
using Gurux.DLMS.ManufacturerSettings;
using System;
using Android.Widget;

namespace Gurux.DLMS.Client.Example
{
    [Activity(Theme = "@style/GuruxDLMSAndroid.Splash",
        MainLauncher = true,
        NoHistory = true)]
    public class GXSplashScreen : AppCompatActivity
    {
        static readonly string TAG = "X:" + typeof(GXSplashScreen).Name;

        public override void OnCreate(Bundle savedInstanceState, PersistableBundle persistentState)
        {
            base.OnCreate(savedInstanceState, persistentState);
            Log.Debug(TAG, "GXSplashScreen.OnCreate");
        }

        // Launches the startup task
        protected override void OnResume()
        {
            base.OnResume();
            Task startupWork = new Task(() => { ReadManufacturerSettings(); });
            startupWork.Start();
        }

        /// <summary>
        /// Read manufacturer settings from Gurux server.
        /// </summary>
        private void ReadManufacturerSettings()
        {
            //Read Manufacturer settings.
            Log.Debug(TAG, "Read Manufacturer settings");
            try
            {
                EditText loading = (EditText)FindViewById(Resource.Id.loading);
                if (loading != null)
                {
                    loading.Text = "Loading manufacturer settings.";
                }
                if (GXManufacturerCollection.IsFirstRun() ||
                    GXManufacturerCollection.IsUpdatesAvailable())
                {
                    GXManufacturerCollection.UpdateManufactureSettings();
                }
            }
            catch (Exception e)
            {
                GXGeneral.ShowError(this, e, "Failed to read manufacturer settings from the server.");
            }
            Log.Debug(TAG, "Starting MainActivity.");
            StartActivity(new Intent(Application.Context, typeof(MainActivity)));
        }
    }
}

