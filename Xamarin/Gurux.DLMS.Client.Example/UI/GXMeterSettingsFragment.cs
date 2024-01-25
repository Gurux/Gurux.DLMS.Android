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

using Android.OS;
using Android.Text;
using Android.Views;
using Android.Widget;
using AndroidX.AppCompat.App;
using AndroidX.Fragment.App;
using Gurux.Bluetooth;
using Gurux.DLMS.Enums;
using Gurux.DLMS.ManufacturerSettings;
using Gurux.Serial;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;

namespace Gurux.DLMS.Client.Example.UI
{
    public class GXMeterSettingsFragment : Fragment
    {
        private ListView listView;
        private List<string> rows = new List<string>();

        /// <summary>
        /// Available manufacturers.
        /// </summary>
        private GXManufacturerCollection _manufacturers;

        /// <summary>
        /// Device.
        /// </summary>
        private GXDevice _device;

        public GXMeterSettingsFragment(
            GXManufacturerCollection manufacturers,
            GXDevice device)
        {
            _manufacturers = manufacturers;
            _device = device;
        }

        /// <summary>
        /// Update view after user change the value.
        /// </summary>
        private void Update()
        {
            ArrayAdapter<string> adapter = new ArrayAdapter<string>(Activity,
                                Android.Resource.Layout.SimpleListItem1, rows);
            listView.Adapter = adapter;
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.fragment_meter_settings, container, false);
            listView = (ListView)view.FindViewById(Resource.Id.properties);
            rows.Clear();
            rows.Add(GetManufacturer());
            rows.Add(GetInterface());
            rows.Add(GetAuthentication());
            rows.Add(GetPassword());
            rows.Add(GetWaitTime());
            rows.Add(GetClientAddress());
            rows.Add(GetAddressType());
            rows.Add(GetPhysicalAddress());
            rows.Add(GetLogicalAddress());
            rows.Add(GetMedia());
            rows.Add(GetTraceLevel());
            ArrayAdapter<string> adapter = new ArrayAdapter<string>(Activity,
                    Android.Resource.Layout.SimpleListItem1, rows);
            listView.Adapter = adapter;
            listView.ItemClick += (sender, e) =>
            {
                switch (e.Position)
                {
                    case 0:
                        UpdateManufacturer();
                        break;
                    case 1:
                        UpdateInterface();
                        break;
                    case 2:
                        UpdateAuthentication();
                        break;
                    case 3:
                        UpdatePassword();
                        break;
                    case 4:
                        UpdateWaitTime();
                        break;
                    case 5:
                        UpdateClientAddress();
                        break;
                    case 6:
                        UpdateAddressType();
                        break;
                    case 7:
                        UpdatePhysicalAddress();
                        break;
                    case 8:
                        UpdateLogicalAddress();
                        break;
                    case 9:
                        UpdateMedia();
                        break;
                    case 10:
                        UpdateTraceLevel();
                        break;
                    default:
                        break;
                        //Do nothing.
                }
            };
            return view;
        }

        private string GetManufacturer()
        {
            string name = "";
            GXManufacturer man = GetManufacturer(_device);
            if (man != null)
            {
                name = man.Name;
            }
            return GetString(Resource.String.manufacturer) + System.Environment.NewLine + name;
        }

        private string GetInterface()
        {
            return GetString(Resource.String.interfaceType) + System.Environment.NewLine + _device.InterfaceType;
        }

        private string GetAuthentication()
        {
            return GetString(Resource.String.authentication) + System.Environment.NewLine + _device.Authentication;
        }
        private string GetPassword()
        {
            return GetString(Resource.String.password) + System.Environment.NewLine + _device.Password;
        }
        private string GetWaitTime()
        {
            return GetString(Resource.String.waittime) + System.Environment.NewLine + TimeSpan.FromSeconds(_device.WaitTime).ToString();
        }

        private string GetClientAddress()
        {
            return GetString(Resource.String.clientAddress) + System.Environment.NewLine + _device.ClientAddress;
        }
        private string GetAddressType()
        {
            return GetString(Resource.String.addressType) + System.Environment.NewLine + _device.AddressType;
        }
        private string GetPhysicalAddress()
        {
            return GetString(Resource.String.physicalAddress) + System.Environment.NewLine + _device.PhysicalAddress;
        }
        private string GetLogicalAddress()
        {
            return GetString(Resource.String.logicalAddress) + System.Environment.NewLine + _device.LogicalAddress;
        }
        private string GetMedia()
        {
            return GetString(Resource.String.media) + System.Environment.NewLine +
                _device.Media.MediaType;
        }
        private string GetTraceLevel()
        {
            return GetString(Resource.String.traceLevel) + System.Environment.NewLine +
                _device.Trace;
        }

        private GXManufacturer GetManufacturer(GXDevice device)
        {
            foreach (GXManufacturer it in _manufacturers)
            {
                if (it.Identification == device.Manufacturer)
                {
                    return it;
                }
            }
            return null;
        }

        /// <summary>
        /// Update manufacturers.
        /// </summary>
        private void UpdateManufacturer()
        {
            try
            {
                int selected = -1;
                List<string> values = new List<string>();
                foreach (GXManufacturer it in _manufacturers)
                {
                    if (selected == -1 &&
                        _device.Manufacturer == it.Identification)
                    {
                        selected = values.Count;
                    }
                    values.Add(it.Name);
                }
                if (selected == -1)
                {
                    selected = 0;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
                builder.SetSingleChoiceItems(values.ToArray(), selected, (dialog, ev) =>
                {
                    try
                    {
                        _device.Manufacturer = _manufacturers[ev.Which].Identification;
                        rows[0] = GetManufacturer();
                        // update interface.
                        GXManufacturer man = GetManufacturer(_device);
                        values.Clear();
                        foreach (InterfaceType it in Enum.GetValues(typeof(InterfaceType)))
                        {
                            if (((int)it & man.SupporterdInterfaces) != 0)
                            {
                                values.Add(it.ToString());
                            }
                        }
                        values.Add(InterfaceType.HDLC.ToString());
                        string actual2 = _device.InterfaceType.ToString();
                        foreach (string it in values)
                        {
                            //Get selected item.
                            if (actual2 == it)
                            {
                                _device.InterfaceType = Enum.Parse<InterfaceType>(it);
                                break;
                            }
                        }
                        rows[1] = GetInterface();
                        // update authentication.
                        if (_device.Authentication != null)
                        {
                            actual2 = _device.Authentication.ToString();
                            _device.Authentication = null;
                            foreach (GXAuthentication it in man.Settings)
                            {
                                //Get selected item.
                                if (actual2 == it.ToString())
                                {
                                    _device.Authentication = it;
                                    //Update client address.
                                    _device.ClientAddress = it.ClientAddress;
                                    break;
                                }
                            }
                        }
                        if (_device.Authentication == null)
                        {
                            GXAuthentication it = man.Settings[0];
                            _device.Authentication = it;
                            //Update client address.
                            _device.ClientAddress = it.ClientAddress;
                        }
                        rows[2] = GetAuthentication();
                        rows[5] = GetClientAddress();
                        Update();
                        (dialog as AlertDialog).Dismiss();
                    }
                    catch (Exception ex)
                    {
                        GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                    }
                });
                builder.SetTitle(Resource.String.manufacturer)
                        .SetNegativeButton(Resource.String.cancel, (dialog, ev) =>
                        {
                            (dialog as AlertDialog).Dismiss();
                        }).Show();
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }

        /// <summary>
        /// Update interface.
        /// </summary>
        private void UpdateInterface()
        {
            try
            {
                List<string> values = new List<string>();
                GXManufacturer man = GetManufacturer(_device);
                values.Add(InterfaceType.HDLC.ToString());
                foreach (InterfaceType it in Enum.GetValues(typeof(InterfaceType)))
                {
                    if (((int)it & man.SupporterdInterfaces) != 0)
                    {
                        values.Add(it.ToString());
                    }
                }
                int selected = values.IndexOf(_device.InterfaceType.ToString());
                if (selected == -1)
                {
                    selected = 0;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
                builder.SetSingleChoiceItems(values.ToArray(), selected, (dialog, ev) =>
                {
                    try
                    {
                        _device.InterfaceType = Enum.Parse<InterfaceType>(values[ev.Which]);
                        rows[1] = GetInterface();
                        Update();
                        (dialog as AlertDialog).Dismiss();
                    }
                    catch (Exception ex)
                    {
                        GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                    }
                });
                builder.SetTitle(Resource.String.interfaceType)
                        .SetNegativeButton(Resource.String.cancel, (dialog, ev) =>
                        {
                            (dialog as AlertDialog).Dismiss();
                        }).Show();
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }

        /// <summary>
        /// Update authentication.
        /// </summary>
        private void UpdateAuthentication()
        {
            try
            {
                List<string> values = new List<string>();
                GXManufacturer man = GetManufacturer(_device);
                foreach (GXAuthentication it in man.Settings)
                {
                    values.Add(it.ToString());
                }
                int selected = values.IndexOf(_device.Authentication.ToString());
                if (selected == -1)
                {
                    selected = 0;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
                builder.SetSingleChoiceItems(values.ToArray(), selected, (dialog, ev) =>
                {
                    try
                    {
                        _device.Authentication = man.Settings[ev.Which];
                        rows[2] = GetAuthentication();
                        //Update client address.
                        _device.ClientAddress = _device.Authentication.ClientAddress;
                        rows[5] = GetClientAddress();
                        Update();
                        (dialog as AlertDialog).Dismiss();
                    }
                    catch (Exception ex)
                    {
                        GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                    }
                });
                builder.SetTitle(Resource.String.interfaceType)
                        .SetNegativeButton(Resource.String.cancel, (dialog, ev) =>
                        {
                            (dialog as AlertDialog).Dismiss();
                        }).Show();
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }

        /// <summary>
        /// Update password.
        /// </summary>
        private void UpdatePassword()
        {
            try
            {
                EditText text = new EditText(Activity);
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
                builder.SetView(text);
                text.Text = _device.Password;
                builder.SetTitle(Resource.String.password)
                        .SetPositiveButton(Resource.String.ok, (dialog, ev) =>
                                {
                                    try
                                    {
                                        _device.Password = text.Text;
                                        rows[3] = GetPassword();
                                        Update();
                                        (dialog as AlertDialog).Dismiss();
                                    }
                                    catch (Exception ex)
                                    {
                                        GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                                    }
                                })
                            .SetNegativeButton(Resource.String.cancel, (dialog, ev) =>
                        {
                            (dialog as AlertDialog).Dismiss();
                        }).Show();
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }

        ///Update wait time.
        private void UpdateWaitTime()
        {
            try
            {
                EditText text = new EditText(Activity);
                text.InputType = InputTypes.ClassDatetime;
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
                builder.SetView(text);
                text.Text = TimeSpan.FromSeconds(_device.WaitTime).ToString();
                builder.SetTitle(Resource.String.waittime)
                        .SetPositiveButton(Resource.String.ok, (dialog, ev) =>
                        {
                            try
                            {
                                _device.WaitTime = TimeSpan.Parse(text.Text).Seconds;
                                rows[4] = GetWaitTime();
                                Update();
                                (dialog as AlertDialog).Dismiss();
                            }
                            catch (Exception ex)
                            {
                                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                            }
                        })
                            .SetNegativeButton(Resource.String.cancel, (dialog, ev) =>
                            {
                                (dialog as AlertDialog).Dismiss();
                            }).Show();
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }

        /// <summary>
        /// Update client address.
        /// </summary>
        private void UpdateClientAddress()
        {
            try
            {
                EditText text = new EditText(Activity);
                text.InputType = InputTypes.NumberFlagSigned;
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
                builder.SetView(text);
                text.Text = _device.ClientAddress.ToString();
                builder.SetTitle(Resource.String.clientAddress)
                        .SetPositiveButton(Resource.String.ok, (dialog, ev) =>
                        {
                            try
                            {
                                _device.ClientAddress = int.Parse(text.Text);
                                rows[5] = GetClientAddress();
                                Update();
                                (dialog as AlertDialog).Dismiss();
                            }
                            catch (Exception ex)
                            {
                                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                            }
                        })
                            .SetNegativeButton(Resource.String.cancel, (dialog, ev) =>
                            {
                                (dialog as AlertDialog).Dismiss();
                            }).Show();
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }

        /// <summary>
        /// Update address type.
        /// </summary>
        private void UpdateAddressType()
        {
            try
            {
                string actual = _device.AddressType.ToString();
                GXManufacturer man = GetManufacturer(_device);
                List<string> names = new List<string>();
                foreach (GXServerAddress it in man.ServerSettings)
                {
                    names.Add(it.HDLCAddress.ToString());
                }

                ListView lv = new ListView(Activity);
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
                builder.SetView(lv);
                builder.SetTitle(Resource.String.addressType)
                        .SetPositiveButton(Resource.String.ok, (dialog, ev) =>
                        {
                            try
                            {
                                _device.AddressType = man.ServerSettings[ev.Which].HDLCAddress;
                                rows[6] = GetAddressType();
                                Update();
                                (dialog as AlertDialog).Dismiss();
                            }
                            catch (Exception ex)
                            {
                                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                            }
                        })
                            .SetNegativeButton(Resource.String.cancel, (dialog, ev) =>
                            {
                                (dialog as AlertDialog).Dismiss();
                            }).Show();
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }

        /// <summary>
        /// Update physical address.
        /// </summary>
        private void UpdatePhysicalAddress()
        {
            try
            {
                EditText text = new EditText(Activity);
                text.InputType = InputTypes.NumberFlagSigned;
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
                builder.SetView(text);
                text.Text = _device.PhysicalAddress.ToString();
                builder.SetTitle(Resource.String.physicalAddress)
                        .SetPositiveButton(Resource.String.ok, (dialog, ev) =>
                        {
                            try
                            {
                                _device.PhysicalAddress = int.Parse(text.Text);
                                rows[7] = GetPhysicalAddress();
                                Update();
                                (dialog as AlertDialog).Dismiss();
                            }
                            catch (Exception ex)
                            {
                                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                            }
                        })
                            .SetNegativeButton(Resource.String.cancel, (dialog, ev) =>
                            {
                                (dialog as AlertDialog).Dismiss();
                            }).Show();
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }

        /// <summary>
        /// Update logical address.
        /// </summary>
        private void UpdateLogicalAddress()
        {
            try
            {
                EditText text = new EditText(Activity);
                text.InputType = InputTypes.NumberFlagSigned;
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
                builder.SetView(text);
                text.Text = _device.LogicalAddress.ToString();
                builder.SetTitle(Resource.String.logicalAddress)
                        .SetPositiveButton(Resource.String.ok, (dialog, ev) =>
                        {
                            try
                            {
                                _device.LogicalAddress = int.Parse(text.Text);
                                rows[8] = GetLogicalAddress();
                                Update();
                                (dialog as AlertDialog).Dismiss();
                            }
                            catch (Exception ex)
                            {
                                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                            }
                        })
                            .SetNegativeButton(Resource.String.cancel, (dialog, ev) =>
                            {
                                (dialog as AlertDialog).Dismiss();
                            }).Show();
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }

        /// <summary>
        /// Update media type.
        /// </summary>
        private void UpdateMedia()
        {
            try
            {
                //Add medias.
                List<string> values = new List<string>();
                values.Add("Serial");
                values.Add("Bluetooth");
                int selected = values.IndexOf(_device.Media.MediaType);
                if (selected == -1)
                {
                    selected = 0;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
                builder.SetSingleChoiceItems(values.ToArray(), selected, (dialog, ev) =>
                {
                    try
                    {
                        if (ev.Which == 0)
                        {
                            _device.Media = new GXSerial(Activity);
                        }
                        else
                        {
                            _device.Media = new GXBluetooth(Activity);
                        }
                        rows[9] = GetMedia();
                        Update();
                        (dialog as AlertDialog).Dismiss();
                    }
                    catch (Exception ex)
                    {
                        GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                    }
                });
                builder.SetTitle(Resource.String.media)
                        .SetNegativeButton(Resource.String.cancel, (dialog, ev) =>
                        {
                            (dialog as AlertDialog).Dismiss();
                        }).Show();
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }

        /// <summary>
        /// Update trace level.
        /// </summary>
        private void UpdateTraceLevel()
        {
            try
            {
                //Add medias.
                List<string> values = new List<string>
                {
                    TraceLevel.Off.ToString(),
                    TraceLevel.Error.ToString(),
                    TraceLevel.Warning.ToString(),
                    TraceLevel.Info.ToString(),
                    TraceLevel.Verbose.ToString()
                };
                int selected = values.IndexOf(_device.Trace.ToString());
                if (selected == -1)
                {
                    selected = 0;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
                builder.SetSingleChoiceItems(values.ToArray(), selected, (dialog, ev) =>
                {
                    try
                    {
                        _device.Trace = (TraceLevel)ev.Which;
                        rows[10] = GetTraceLevel();
                        Update();
                        (dialog as AlertDialog).Dismiss();
                    }
                    catch (Exception ex)
                    {
                        GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                    }
                });
                builder.SetTitle(Resource.String.traceLevel)
                        .SetNegativeButton(Resource.String.cancel, (dialog, ev) =>
                        {
                            (dialog as AlertDialog).Dismiss();
                        }).Show();
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }
    }
}