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

using System;
using Android.App;
using Android.OS;
using Android.Runtime;
using Android.Views;
using AndroidX.AppCompat.App;
using AndroidX.AppCompat.Widget;
using AndroidX.Core.View;
using AndroidX.DrawerLayout.Widget;
using Google.Android.Material.FloatingActionButton;
using Google.Android.Material.Navigation;
using Google.Android.Material.Snackbar;
using Android.Content.PM;
using static Android.Views.View;
using Gurux.DLMS.ManufacturerSettings;
using Android.Content;
using Gurux.DLMS.Enums;
using Gurux.Serial;
using Gurux.DLMS.Client.Example.UI;
using System.Linq;
using Gurux.Bluetooth;
using Gurux.DLMS.Objects;
using System.IO;
using System.Text;
using System.Security.Cryptography;
using System.Xml;

namespace Gurux.DLMS.Client.Example
{
    [Activity(Label = "@string/app_name")]
    public class MainActivity : AppCompatActivity, NavigationView.IOnNavigationItemSelectedListener
    {
        private readonly ManufacturersFragment _manufacturersFragment;
        private readonly GXXmlTranslatorFragment _xmlTranslatorFragment;
        private readonly GXObisTranslatorFragment _obisTranslatorFragment;
        private readonly GXMeterSettingsFragment _meterSettingsFragment;
        private readonly GXMediaFragment _mediaFragment;
        private readonly GXMainFragment _mainFragment;
        private GXManufacturerCollection _manufacturers;
        private readonly GXDevice _device = new GXDevice();

        public MainActivity()
        {
            _manufacturers = new GXManufacturerCollection();
            _manufacturersFragment = new ManufacturersFragment();
            _xmlTranslatorFragment = new GXXmlTranslatorFragment();
            _obisTranslatorFragment = new GXObisTranslatorFragment();
            _meterSettingsFragment = new GXMeterSettingsFragment(_manufacturers, _device);
            _mediaFragment = new GXMediaFragment(_device);
            _mainFragment = new GXMainFragment(_device);
        }

        private void ShowFragment(AndroidX.Fragment.App.Fragment fragment)
        {
            var transaction = SupportFragmentManager.BeginTransaction();
            transaction.Replace(Resource.Id.fragmentContainer, fragment);
            transaction.Commit();
        }

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            // Initial fragment
            ShowFragment(_mainFragment);
            LoadSettings();
            //Order manufacturers by name.
            GXManufacturerCollection manufacturers = new GXManufacturerCollection();
            GXManufacturerCollection.ReadManufacturerSettings(manufacturers);
            _manufacturers.AddRange(manufacturers.OrderBy(o => o.Name));           
            if (string.IsNullOrEmpty(_device.Manufacturer) &&
                _manufacturers.Any())
            {
                //Select Gurux as a default manufacturer.
                _device.Manufacturer = "grx";
            }
            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            SetContentView(Resource.Layout.activity_main);
            Toolbar toolbar = FindViewById<Toolbar>(Resource.Id.toolbar);

            DrawerLayout drawer = FindViewById<DrawerLayout>(Resource.Id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, Resource.String.navigation_drawer_open, Resource.String.navigation_drawer_close);
            drawer.AddDrawerListener(toggle);
            toggle.SyncState();

            NavigationView navigationView = FindViewById<NavigationView>(Resource.Id.nav_view);
            navigationView.SetNavigationItemSelectedListener(this);
        }

        public override void OnBackPressed()
        {
            DrawerLayout drawer = FindViewById<DrawerLayout>(Resource.Id.drawer_layout);
            if (drawer.IsDrawerOpen(GravityCompat.Start))
            {
                drawer.CloseDrawer(GravityCompat.Start);
            }
            else
            {
                base.OnBackPressed();
            }
        }

        public override bool OnCreateOptionsMenu(IMenu menu)
        {
            MenuInflater.Inflate(Resource.Menu.menu_main, menu);
            return true;
        }

        public override bool OnOptionsItemSelected(IMenuItem item)
        {
            int id = item.ItemId;
            if (id == Resource.Id.action_settings)
            {
                return true;
            }
            return base.OnOptionsItemSelected(item);
        }

        public bool OnNavigationItemSelected(IMenuItem item)
        {
            int id = item.ItemId;
            if (id == Resource.Id.nav_manufacturers)
            {
                ShowFragment(_manufacturersFragment);
            }
            else if (id == Resource.Id.nav_xml_translator)
            {
                ShowFragment(_xmlTranslatorFragment);
            }
            else if (id == Resource.Id.nav_obis_translator)
            {
                ShowFragment(_obisTranslatorFragment);
            }
            else if (id == Resource.Id.nav_meterSettings)
            {
                ShowFragment(_meterSettingsFragment);
            }
            else if (id == Resource.Id.nav_mediaSettings)
            {
                ShowFragment(_mediaFragment);
            }
            else if (id == Resource.Id.nav_main)
            {
                ShowFragment(_mainFragment);
            }
            else
            {
                throw new NotImplementedException();
            }
            DrawerLayout drawer = FindViewById<DrawerLayout>(Resource.Id.drawer_layout);
            drawer.CloseDrawer(GravityCompat.Start);
            return true;
        }
        public override void OnRequestPermissionsResult(int requestCode, string[] permissions, [GeneratedEnum] Permission[] grantResults)
        {
            Xamarin.Essentials.Platform.OnRequestPermissionsResult(requestCode, permissions, grantResults);

            base.OnRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        protected override void OnPause()
        {
            SaveSettings();
            base.OnPause();
        }

        protected override void OnStop()
        {
            SaveSettings();
            base.OnStop();
        }

        protected override void OnDestroy()
        {
            SaveSettings();
            base.OnDestroy();
        }

        private void LoadSettings()
        {
            ISharedPreferences s = GetPreferences(FileCreationMode.Private);
            //Read _device settings.
            string man = s.GetString("manufacturer", "");
            if (!string.IsNullOrEmpty(man))
            {
                _device.Manufacturer = man;
                _device.InterfaceType = (InterfaceType)s.GetInt("interfaceType", 0);
                _device.WaitTime = s.GetInt("waitTime", 5);
                _device.MaximumBaudRate = s.GetInt("maximumBaudRate", 0);
                try
                {
                    string auth = s.GetString("authentication", "None");
                    foreach (GXManufacturer it in _manufacturers)
                    {
                        if (it.Identification == man)
                        {
                            foreach (GXAuthentication authentication in it.Settings)
                            {
                                if (auth == authentication.ToString())
                                {
                                    _device.Authentication = authentication;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if (_device.Authentication == null)
                    {
                        _device.Authentication = new GXAuthentication(Authentication.None, 16);
                    }
                }
                catch (Exception)
                {
                }
                _device.Password = s.GetString("password", "");
                _device.Security = (Security)s.GetInt("security", 0);
                _device.SystemTitle = s.GetString("systemTitle", "");
                _device.BlockCipherKey = s.GetString("blockCipherKey", "");
                _device.AuthenticationKey = s.GetString("authenticationKey", "");
                _device.ClientAddress = s.GetInt("clientAddress", 16);
                _device.AddressType = (HDLCAddressType)s.GetInt("addressType", 0);
                _device.PhysicalAddress = s.GetInt("physicalAddress", 1);
                _device.LogicalAddress = s.GetInt("logicalAddress", 0);
            }
            string mediaType = s.GetString("mediaType", "Serial");
            if (mediaType == "Serial")
            {
                _device.Media = new GXSerial(this);
            }
            else if (mediaType == "Bluetooth")
            {
                _device.Media = new GXBluetooth(this);
            }
            //Read media settings.
            _device.Media.Settings = s.GetString("mediaSettings", null);
            try
            {
                string xml = s.GetString("objects", null);
                if (!string.IsNullOrEmpty(xml))
                {
                    MemoryStream ms = new MemoryStream();
                    ms.Write(ASCIIEncoding.ASCII.GetBytes(xml), 0, xml.Length);
                    ms.Position = 0;
                    _device.Objects = GXDLMSObjectCollection.Load(ms);
                }
            }
            catch (Exception e)
            {
                GXGeneral.ShowError(this, e, GetString(Resource.String.error));
            }
        }

        private void SaveSettings()
        {
            ISharedPreferences sharedPref = GetPreferences(FileCreationMode.Private);
            var editor = sharedPref.Edit();
            editor.PutString("manufacturer", _device.Manufacturer);
            editor.PutInt("interfaceType", (int)_device.InterfaceType);
            editor.PutInt("waitTime", _device.WaitTime);
            editor.PutInt("maximumBaudRate", _device.MaximumBaudRate);
            if (_device.Authentication != null)
            {
                editor.PutString("authentication", _device.Authentication.ToString());
            }
            editor.PutString("password", _device.Password);
            editor.PutInt("security", (int)_device.Security);
            editor.PutString("systemTitle", _device.SystemTitle);
            editor.PutString("blockCipherKey", _device.BlockCipherKey);
            editor.PutString("authenticationKey", _device.AuthenticationKey);
            editor.PutInt("clientAddress", _device.ClientAddress);
            editor.PutInt("addressType", (int)_device.AddressType);
            editor.PutInt("physicalAddress", _device.PhysicalAddress);
            editor.PutInt("logicalAddress", _device.LogicalAddress);
            editor.PutString("mediaType", _device.Media.MediaType);
            editor.PutString("mediaSettings", _device.Media.Settings);
            if (_device.Objects != null && _device.Objects.Any())
            {
                MemoryStream ms = new MemoryStream();
                using (TextWriter writer = new StreamWriter(ms))
                {
                    _device.Objects.Save(ms, null);
                    ms.Position = 0;
                    using (TextReader reader = new StreamReader(ms))
                    {
                        editor.PutString("objects", reader.ReadToEnd());
                    }
                }
            }

            editor.Apply();
        }
    }
}

