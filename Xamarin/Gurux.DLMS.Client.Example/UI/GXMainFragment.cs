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
using Android.Views;
using Android.Widget;
using AndroidX.Fragment.App;
using Gurux.Common;
using Gurux.Common.Enums;
using Gurux.DLMS.Objects;
using Gurux.DLMS.Secure;
using Gurux.Serial;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System;
using System.Diagnostics;
using System.Threading;
using System.IO.Ports;
using System.Text;
using System.Drawing;

namespace Gurux.DLMS.Client.Example.UI
{
    /// <summary>
    /// This fragment implements the reader.
    /// </summary>
    public class GXMainFragment : Fragment
    {
        private readonly GXDevice _device;
        private ListView _objects;
        private GXDLMSSecureClient _client;
        private EditText _attributes;
        private Button _open;
        private Button _read;
        private Button _refresh;
        private SearchView _search;
        private List<GXDLMSObject> _cosemObjects = new List<GXDLMSObject>();
        private CheckBox _showTrace;
        private EditText _trace;

        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="device"></param>
        public GXMainFragment(GXDevice device)
        {
            _device = device;
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View view = inflater.Inflate(Resource.Layout.fragment_main, container, false);
            try
            {
                _device.Media.OnMediaStateChange += OnMediaStateChange;
                _objects = (ListView)view.FindViewById(Resource.Id.objects);
                _attributes = (EditText)view.FindViewById(Resource.Id.attributes);
                _open = (Button)view.FindViewById(Resource.Id.open);
                _read = (Button)view.FindViewById(Resource.Id.read);
                _refresh = (Button)view.FindViewById(Resource.Id.refresh);
                _search = (SearchView)view.FindViewById(Resource.Id.search);
                _showTrace = (CheckBox)view.FindViewById(Resource.Id.showTrace);
                _trace = (EditText)view.FindViewById(Resource.Id.trace);
                _trace.Visibility = ViewStates.Gone;
                int serverAddress = GXDLMSClient.GetServerAddress(_device.LogicalAddress, _device.PhysicalAddress);
                _client = new GXDLMSSecureClient(true, _device.ClientAddress,
                    serverAddress,
                    _device.Authentication.Type,
                    _device.Password,
                    Enums.InterfaceType.HDLC);

                _showTrace.CheckedChange += (sender, e) =>
                {
                    if (e.IsChecked)
                    {
                        _trace.Visibility = ViewStates.Visible;
                        _attributes.Visibility = ViewStates.Gone;
                    }
                    else
                    {
                        _trace.Visibility = ViewStates.Gone;
                        _attributes.Visibility = ViewStates.Visible;
                    }
                };
                _open.Click += (sender, e) =>
                {
                    try
                    {
                        //Clear trace.
                        _trace.Text = "";
                        if (_device.Media.IsOpen)
                        {
                            //Close connection.
                            Task.Run(() =>
                            {
                                try
                                {
                                    Close();
                                    Activity.RunOnUiThread(() =>
                                    {
                                        Toast.MakeText(Activity, "Disconnected.", ToastLength.Short).Show();
                                    });
                                }
                                catch (Exception e)
                                {
                                    GXGeneral.ShowError(Activity, e, GetString(Resource.String.error));
                                }
                            });
                        }
                        else
                        {
                            //Open connection.
                            Task.Run(() =>
                            {
                                try
                                {
                                    InitializeConnection();
                                    Activity.RunOnUiThread(() =>
                                    {
                                        Toast.MakeText(Activity, "Connected.", ToastLength.Short).Show();
                                    });
                                }
                                catch (Exception e)
                                {
                                    GXGeneral.ShowError(Activity, e, GetString(Resource.String.error));
                                }
                            });
                        }
                    }
                    catch (Exception ex)
                    {
                        GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                    }
                };
                _read.Click += (sender, e) =>
                {
                    try
                    {
                        //Clear trace.
                        _trace.Text = "";
                        //Read selected item.
                        int pos = _objects.CheckedItemPosition;
                        if (pos != -1)
                        {
                            GXDLMSObject obj = _cosemObjects[pos];
                            Task.Run(() =>
                            {
                                try
                                {
                                    Read(obj);
                                    Activity.RunOnUiThread(() =>
                                    {
                                        ShowObject(obj);
                                        Toast.MakeText(Activity, "Read done.", ToastLength.Short).Show();
                                    });
                                }
                                catch (Exception e)
                                {
                                    GXGeneral.ShowError(Activity, e, GetString(Resource.String.error));
                                }
                            });
                        }
                    }
                    catch (Exception ex)
                    {
                        GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                    }

                };

                _refresh.Click += (sender, e) =>
                {
                    try
                    {
                        //Clear trace.
                        _trace.Text = "";
                        Task.Run(() =>
                        {
                            try
                            {
                                Refresh();
                                Activity.RunOnUiThread(() =>
                                {
                                    ShowObjects("");
                                    //Read is enabled only when there are object.
                                    _read.Enabled = _cosemObjects.Any();
                                    Toast.MakeText(Activity, "Refresh done.", ToastLength.Short).Show();
                                });
                            }
                            catch (Exception e)
                            {
                                GXGeneral.ShowError(Activity, e, GetString(Resource.String.error));
                            }
                        });
                    }
                    catch (Exception ex)
                    {
                        GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                    }
                };


                _objects.ItemClick += (sender, e) =>
                {
                    try
                    {
                        ShowObject(_device.Objects[e.Position]);
                    }
                    catch (Exception ex)
                    {
                        GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                    }
                };
                ShowObjects("");

                if (_device != null)
                {
                    if (_device.Media != null)
                    {
                        EnableUI(_device.Media.IsOpen);
                    }
                    if (_device.Manufacturer == null || !_device.Manufacturer.Any())
                    {
                        Toast.MakeText(Activity, Resource.String.invalidManufacturer, ToastLength.Short).Show();
                        _open.Enabled = false;
                    }
                    if (_device.Media is GXSerial && ((GXSerial)_device.Media).Port == null)
                    {
                        Toast.MakeText(Activity, Resource.String.invalidSerialPort, ToastLength.Short).Show();
                        _open.Enabled = false;
                    }
                }
                _search.QueryTextChange += (sender, e) =>
                {
                    try
                    {
                        ShowObjects(e.NewText);
                    }
                    catch (Exception ex)
                    {
                        GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                    }
                };
            }
            catch (Exception e)
            {
                GXGeneral.ShowError(Activity, e, GetString(Resource.String.error));
            }
            return view;
        }

        /// <summary>
        /// Show selected object
        /// </summary>
        /// <param name="target">Selected COSEM object.</param>
        private void ShowObject(GXDLMSObject target)
        {
            _attributes.Text = "";
            StringBuilder sb = new StringBuilder();
            sb.Append(target.LogicalName);
            if (target.ShortName != 0)
            {
                sb.Append(" (");
                sb.Append(target.ShortName);
                sb.Append(")");
            }
            sb.Append(System.Environment.NewLine);
            sb.Append(target.Description);
            sb.Append(System.Environment.NewLine);
            sb.Append("------------------------------------------------------------------");
            sb.Append(System.Environment.NewLine);
            for (int pos = 0; pos != ((IGXDLMSBase)target).GetAttributeCount(); ++pos)
            {
                sb.Append(target.GetValues()[pos] + "");
                sb.Append(System.Environment.NewLine);
                sb.Append("------------------------------------------------------------------");
                sb.Append(System.Environment.NewLine);
            }
            _attributes.Text = sb.ToString();
        }

        private void ShowObjects(string newText)
        {
            _cosemObjects.Clear();
            List<string> rows = new List<string>();
            bool addAll = string.IsNullOrEmpty(newText);
            if (!addAll)
            {
                newText = newText.ToLower();
            }
            foreach (GXDLMSObject it in _device.Objects)
            {
                if (addAll || IsAdded(it, newText))
                {
                    if (it.Description == null)
                    {
                        rows.Add(it.LogicalName);
                    }
                    else
                    {
                        rows.Add(it.LogicalName + System.Environment.NewLine + it.Description);
                    }
                    _cosemObjects.Add(it);
                }
            }
            ArrayAdapter<string> adapter = new ArrayAdapter<string>(Activity,
                                Android.Resource.Layout.SimpleListItem1, rows);
            _objects.Adapter = adapter;
        }

        private static bool IsAdded(GXDLMSObject it, string newText)
        {
            return it.ObjectType.ToString().ToLower().Contains(newText) ||
                    (it.LogicalName != null && it.LogicalName.ToLower().Contains(newText)) ||
                    (it.Description != null && it.Description.ToLower().Contains(newText));
        }

        /// <summary>
        /// Disconnect.
        /// </summary>
        public void Disconnect()
        {
            if (_device.Media != null && _client != null)
            {
                try
                {
                    WriteTrace(TraceLevel.Verbose, "Disconnecting from the meter.");
                    try
                    {
                        Release();
                    }
                    catch (Exception)
                    {
                        //All meters don't support release.
                    }
                    GXReplyData reply = new GXReplyData();
                    ReadDLMSPacket(_client.DisconnectRequest(), reply);
                }
                catch
                {
                    //All meters don't support release.
                }
            }
        }

        /// <summary>
        /// Release.
        /// </summary>
        public void Release()
        {
            if (_device.Media != null && _client != null)
            {
                try
                {
                    WriteTrace(TraceLevel.Info, "Release from the meter.");
                    //Release is call only for secured connections.
                    //All meters are not supporting Release and it's causing problems.
                    if (_client.InterfaceType == Enums.InterfaceType.WRAPPER ||
                        (_client.Ciphering.Security != Enums.Security.None &&
                        !_client.PreEstablishedConnection))
                    {
                        GXReplyData reply = new GXReplyData();
                        ReadDataBlock(_client.ReleaseRequest(), reply);
                    }
                }
                catch (Exception ex)
                {
                    //All meters don't support Release.
                    WriteTrace(TraceLevel.Info,
                        "Release failed. " + ex.Message);
                }
            }
        }

        /// <summary>
        /// Close connection to the meter.
        /// </summary>
        public void Close()
        {
            if (_device.Media.IsOpen && _client != null)
            {
                try
                {
                    WriteTrace(TraceLevel.Info,
                        "Disconnecting from the meter.");
                    try
                    {
                        Release();
                    }
                    catch (Exception)
                    {
                    }
                    GXReplyData reply = new GXReplyData();
                    ReadDLMSPacket(_client.DisconnectRequest(), reply);
                }
                catch (Exception ex)
                {
                    GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
                }
                try
                {
                    _device.Media.Close();
                }
                catch (Exception e)
                {
                    GXGeneral.ShowError(Activity, e, GetString(Resource.String.error));
                }
            }
        }

        /// <summary>
        /// Refresh COSEM objects.
        /// </summary>
        public void Refresh()
        {
            // Get Association view from the meter.
            GXReplyData reply = new GXReplyData();
            ReadDataBlock(_client.GetObjectsRequest(), reply);
            _device.Objects = _client.ParseObjects(reply.Data, true);
            // Get description of the objects.
            GXDLMSConverter converter = new GXDLMSConverter();
            converter.UpdateOBISCodeInformation(_device.Objects);
        }

        /*
         * Read Scalers and units from the register objects.
         */
        void ReadScalerAndUnits(GXDLMSObjectCollection objects)
        {
            GXDLMSObjectCollection objs = objects.GetObjects(new Enums.ObjectType[]{
                Enums.ObjectType.Register, Enums.ObjectType.DemandRegister,
                Enums.ObjectType.ExtendedRegister});

            try
            {
                List<KeyValuePair<GXDLMSObject, int>> list =
                        new List<KeyValuePair<GXDLMSObject, int>>();
                foreach (GXDLMSObject it in objs)
                {
                    if (it is GXDLMSRegister)
                    {
                        list.Add(new KeyValuePair<GXDLMSObject, int>(it, 3));
                    }
                    if (it is GXDLMSDemandRegister)
                    {
                        list.Add(new KeyValuePair<GXDLMSObject, int>(it, 4));
                    }
                }
                ReadList(list);
            }
            catch (Exception)
            {
                foreach (GXDLMSObject it in objs)
                {
                    try
                    {
                        if (it is GXDLMSRegister)
                        {
                            ReadObject(it, 3);
                        }
                        else if (it is GXDLMSDemandRegister)
                        {
                            ReadObject(it, 4);
                        }
                    }
                    catch (Exception e)
                    {
                        //                    traceLn(logFile, "Err! Failed to read scaler and unit value: " + e.getMessage());
                        // Continue reading.
                    }
                }
            }
        }

        /// <summary>
        /// Read list of attributes.
        /// </summary>
        /// <param name="list"></param>
        public void ReadList(List<KeyValuePair<GXDLMSObject, int>> list)
        {
            var data = _client.ReadList(list);
            GXReplyData reply = new GXReplyData();
            ReadDataBlock(data, reply);
            _client.UpdateValues(list, (List<object>)reply.Value);
        }

        /// <summary>
        /// Reads profile generic using start and end time.
        /// </summary>
        /// <param name="pg">Profile Generic to be read.</param>
        /// <param name="start">Start time.</param>
        /// <param name="end">End time.</param>
        public void ReadRowsByRange(GXDLMSProfileGeneric pg,
                                    DateTime start, DateTime end)
        {
            GXReplyData reply = new GXReplyData();
            byte[][] data = _client.ReadRowsByRange(pg, start, end);
            ReadDataBlock(data, reply);
            _client.UpdateValue(pg, 2, reply.Value);
        }

        /**
         * Reads selected DLMS object with selected attribute index.
         *
         * @param item Object to read.
         * @param attributeIndex
         * @return Read value.
         * @ Occurred exception.
         */
        public object ReadObject(GXDLMSObject item, int attributeIndex)
        {
            var data = _client.Read(item.Name, item.ObjectType, attributeIndex)[0];
            GXReplyData reply = new GXReplyData();
            ReadDataBlock(data, reply);
            // Update data type on read.
            if (item.GetDataType(attributeIndex) == Enums.DataType.None)
            {
                item.SetDataType(attributeIndex, reply.DataType);
            }
            return _client.UpdateValue(item, attributeIndex, reply.Value);
        }

        /// <summary>
        /// Read selected object.
        /// </summary>
        /// <param name="obj">Object to be read.</param>
        public void Read(GXDLMSObject obj)
        {
            foreach (int it in ((IGXDLMSBase)obj).GetAttributeIndexToRead(true))
            {
                ReadObject(obj, it);
            }
        }

        /// <summary>
        /// Write trace.
        /// </summary>
        /// <param name="trace">Minimum trace level.</param>
        /// <param name="text">Trace text.</param>
        void WriteTrace(TraceLevel trace, string text)
        {
            if (trace >= _device.Trace)
            {
                Activity.RunOnUiThread(() =>
                {
                    _trace.Append(DateTime.Now.ToLongTimeString() + "\t" + text + System.Environment.NewLine);
                });
            }
        }

        /// <summary>
        /// Read DLMS Data from the device.
        /// </summary>
        /// <param name="data">Data to send.</param>
        /// <returns>Received data.</returns>
        public void ReadDLMSPacket(byte[] data, GXReplyData reply)
        {
            if (data == null && !reply.IsStreaming())
            {
                return;
            }
            GXReplyData notify = new GXReplyData();
            reply.Error = 0;
            object eop = (byte)0x7E;
            //In network connection terminator is not used.
            if (_client.InterfaceType != Enums.InterfaceType.HDLC &&
                _client.InterfaceType != Enums.InterfaceType.HdlcWithModeE)
            {
                eop = null;
            }
            int pos = 0;
            bool succeeded = false;
            GXByteBuffer rd = new GXByteBuffer();
            ReceiveParameters<byte[]> p = new ReceiveParameters<byte[]>()
            {
                Eop = eop,
                Count = _client.GetFrameSize(rd),
                AllData = true,
                WaitTime = _device.WaitTime * 1000,
            };
            lock (_device.Media.Synchronous)
            {
                while (!succeeded && pos != 3)
                {
                    if (!reply.IsStreaming())
                    {
                        WriteTrace(TraceLevel.Verbose,
                            "TX:\t" + DateTime.Now.ToLongTimeString() + "\t" +
                            GXCommon.ToHex(data, true));
                        p.Reply = null;
                        _device.Media.Send(data, null);
                    }
                    succeeded = _device.Media.Receive(p);
                    if (!succeeded)
                    {
                        if (++pos > _device.RetryCount)
                        {
                            throw new Exception("Failed to receive reply from the device in given time.");
                        }
                        //If Eop is not set read one byte at time.
                        if (p.Eop == null)
                        {
                            p.Count = 1;
                        }
                        //Try to read again...
                        System.Diagnostics.Debug.WriteLine("Data send failed. Try to resend " + pos.ToString() + "/3");
                    }
                }
                rd = new GXByteBuffer(p.Reply);
                try
                {
                    pos = 0;
                    //Loop until whole COSEM packet is received.
                    while (!_client.GetData(rd, reply, notify))
                    {
                        p.Reply = null;
                        if (notify.IsComplete && notify.Data.Data != null)
                        {
                            //Handle notify.
                            if (!notify.IsMoreData)
                            {
                                if (notify.PrimeDc != null)
                                {
                                    WriteTrace(TraceLevel.Info,
                                        notify.PrimeDc.ToString());
                                }
                                else
                                {
                                    //Show received push message as XML.
                                    string xml;
                                    GXDLMSTranslator t = new GXDLMSTranslator(Enums.TranslatorOutputType.SimpleXml);
                                    t.DataToXml(notify.Data, out xml);
                                    WriteTrace(TraceLevel.Info, xml);
                                }
                                notify.Clear();
                                continue;
                            }
                        }
                        if (p.Eop == null)
                        {
                            p.Count = _client.GetFrameSize(rd);
                        }
                        while (!_device.Media.Receive(p))
                        {
                            if (++pos >= _device.RetryCount)
                            {
                                throw new Exception("Failed to receive reply from the device in given time.");
                            }
                            p.Reply = null;
                            _device.Media.Send(data, null);
                            //Try to read again...
                            System.Diagnostics.Debug.WriteLine("Data send failed. Try to resend " + pos.ToString() + "/3");
                        }
                        rd.Set(p.Reply);
                    }
                }
                catch (Exception ex)
                {
                    WriteTrace(TraceLevel.Verbose, "RX:\t" + DateTime.Now.ToLongTimeString() + "\t" + rd);
                    throw;
                }
            }
            WriteTrace(TraceLevel.Verbose, "RX:\t" + DateTime.Now.ToLongTimeString() + "\t" + rd);
            if (reply.Error != 0)
            {
                if (reply.Error == (short)Enums.ErrorCode.Rejected)
                {
                    Thread.Sleep(1000);
                    ReadDLMSPacket(data, reply);
                }
                else
                {
                    throw new GXDLMSException(reply.Error);
                }
            }
        }

        /// <summary>
        /// Send data block(s) to the meter.
        /// </summary>
        /// <param name="data">Send data block(s).</param>
        /// <param name="reply">Received reply from the meter.</param>
        /// <returns>Return false if frame is rejected.</returns>
        bool ReadDataBlock(byte[][] data, GXReplyData reply)
        {
            if (data == null)
            {
                return true;
            }
            foreach (byte[] it in data)
            {
                reply.Clear();
                ReadDataBlock(it, reply);
            }
            return reply.Error == 0;
        }

        /// <summary>
        /// Read data block from the device.
        /// </summary>
        /// <param name="data">data to send</param>
        /// <param name="text">Progress text.</param>
        /// <param name="multiplier"></param>
        /// <returns>Received data.</returns>
        public void ReadDataBlock(byte[] data, GXReplyData reply)
        {
            ReadDLMSPacket(data, reply);
            lock (_device.Media.Synchronous)
            {
                while (reply.IsMoreData &&
                    (_client.ConnectionState != Enums.ConnectionState.None ||
                    _client.PreEstablishedConnection))
                {
                    if (reply.IsStreaming())
                    {
                        data = null;
                    }
                    else
                    {
                        data = _client.ReceiverReady(reply);
                    }
                    ReadDLMSPacket(data, reply);
                }
            }
        }

        /// <summary>
        /// Send SNRM Request to the meter.
        /// </summary>
        public void SNRMRequest()
        {
            GXReplyData reply = new GXReplyData();
            byte[] data;
            data = _client.SNRMRequest();
            if (data != null)
            {
                WriteTrace(TraceLevel.Info,
                    "Send SNRM request." + GXCommon.ToHex(data, true));
                ReadDataBlock(data, reply);
                if (_device.Trace == TraceLevel.Verbose)
                {
                    WriteTrace(TraceLevel.Info,
                        "Parsing UA reply." + reply.ToString());
                }
                //Has server accepted client.
                _client.ParseUAResponse(reply.Data);
                WriteTrace(TraceLevel.Info,
                    "Parsing UA reply succeeded.");
            }
        }

        /// <summary>
        /// Send AARQ Request to the meter.
        /// </summary>
        public void AarqRequest()
        {
            GXReplyData reply = new GXReplyData();
            //Generate AARQ request.
            //Split requests to multiple packets if needed.
            //If password is used all data might not fit to one packet.
            var aarq = _client.AARQRequest();
            //AARQ is not used for pre-established connections.
            if (aarq.Length != 0)
            {
                foreach (byte[] it in aarq)
                {
                    WriteTrace(TraceLevel.Info,
                        "Send AARQ request " + GXCommon.ToHex(it, true));
                    reply.Clear();
                    ReadDataBlock(it, reply);
                }
                WriteTrace(TraceLevel.Info,
                    "Parsing AARE reply" + reply.ToString());
                //Parse reply.
                _client.ParseAAREResponse(reply.Data);
                reply.Clear();
                //Get challenge Is HLS authentication is used.
                if (_client.Authentication > Enums.Authentication.Low)
                {
                    foreach (byte[] it in _client.GetApplicationAssociationRequest())
                    {
                        reply.Clear();
                        ReadDataBlock(it, reply);
                    }
                    _client.ParseApplicationAssociationResponse(reply.Data);
                }
                WriteTrace(TraceLevel.Info,
                    "Parsing AARE reply succeeded.");
            }
        }

        /// <summary>
        /// Read attribute value.
        /// </summary>
        /// <param name="it">COSEM object to read.</param>
        /// <param name="attributeIndex">Attribute index.</param>
        /// <returns>Read value.</returns>
        public object Read(GXDLMSObject it, int attributeIndex)
        {
            if (_client.CanRead(it, attributeIndex))
            {
                GXReplyData reply = new GXReplyData();
                if (!ReadDataBlock(_client.Read(it, attributeIndex), reply))
                {
                    if (reply.Error != (short)Enums.ErrorCode.Rejected)
                    {
                        throw new GXDLMSException(reply.Error);
                    }
                    reply.Clear();
                    Thread.Sleep(1000);
                    if (!ReadDataBlock(_client.Read(it, attributeIndex), reply))
                    {
                        throw new GXDLMSException(reply.Error);
                    }
                }
                //Update data type.
                if (it.GetDataType(attributeIndex) == Enums.DataType.None)
                {
                    it.SetDataType(attributeIndex, reply.DataType);
                }
                return _client.UpdateValue(it, attributeIndex, reply.Value);
            }
            else
            {
                WriteTrace(TraceLevel.Error,
                    "Can't read " + it.ToString() + ". Not enought acccess rights.");
            }
            return null;
        }

        /// <summary>
        /// Read Invocation counter (frame counter) from the meter and update it.
        /// </summary>
        private void UpdateFrameCounter()
        {
            //Read frame counter if GeneralProtection is used.
            if (!string.IsNullOrEmpty(_device.InvocationCounter) &&
                _client.Ciphering != null &&
                _client.Ciphering.Security != Enums.Security.None)
            {
                //Media settings are saved and they are restored when HDLC with mode E is used.
                string mediaSettings = _device.Media.Settings;
                InitializeOpticalHead();
                byte[] data;
                GXReplyData reply = new GXReplyData();
                int add = _client.ClientAddress;
                int serverAddress = _client.ServerAddress;
                Enums.Authentication auth = _client.Authentication;
                Enums.Security security = _client.Ciphering.Security;
                Enums.Signing signing = _client.Ciphering.Signing;
                byte[] challenge = _client.CtoSChallenge;
                byte[] serverSystemTitle = _client.ServerSystemTitle;
                try
                {
                    _client.ServerSystemTitle = null;
                    _client.ClientAddress = 16;
                    _client.Authentication = Enums.Authentication.None;
                    _client.Ciphering.Security = Enums.Security.None;
                    _client.Ciphering.Signing = Enums.Signing.None;
                    data = _client.SNRMRequest();
                    if (data != null)
                    {
                        WriteTrace(TraceLevel.Info,
                            "Send SNRM request." + GXCommon.ToHex(data, true));
                        ReadDataBlock(data, reply);
                        if (_device.Trace == TraceLevel.Verbose)
                        {
                            WriteTrace(TraceLevel.Info,
                                "Parsing UA reply." + reply.ToString());
                        }
                        //Has server accepted client.
                        _client.ParseUAResponse(reply.Data);
                        WriteTrace(TraceLevel.Info,
                            "Parsing UA reply succeeded.");
                    }
                    //Generate AARQ request.
                    //Split requests to multiple packets if needed.
                    //If password is used all data might not fit to one packet.
                    foreach (byte[] it in _client.AARQRequest())
                    {
                        WriteTrace(TraceLevel.Info,
                            "Send AARQ request " +
                            GXCommon.ToHex(it, true));
                        reply.Clear();
                        ReadDataBlock(it, reply);
                    }
                    WriteTrace(TraceLevel.Info,
                        "Parsing AARE reply" + reply.ToString());
                    try
                    {
                        //Parse reply.
                        _client.ParseAAREResponse(reply.Data);
                        reply.Clear();
                        GXDLMSData d = new GXDLMSData(_device.InvocationCounter);
                        Read(d, 2);
                        _client.Ciphering.InvocationCounter = 1 + Convert.ToUInt32(d.Value);
                        WriteTrace(TraceLevel.Info,
                            "Invocation counter: " + Convert.ToString(_client.Ciphering.InvocationCounter));
                        reply.Clear();
                        Disconnect();
                        //Reset media settings back to default.
                        if (_client.InterfaceType == Enums.InterfaceType.HdlcWithModeE)
                        {
                            _device.Media.Close();
                            _device.Media.Settings = mediaSettings;
                        }
                    }
                    catch (Exception)
                    {
                        Disconnect();
                        throw;
                    }
                }
                finally
                {
                    _client.ServerSystemTitle = serverSystemTitle;
                    _client.ClientAddress = add;
                    _client.ServerAddress = serverAddress;
                    _client.Authentication = auth;
                    _client.Ciphering.Security = security;
                    _client.CtoSChallenge = challenge;
                    _client.Ciphering.Signing = signing;
                    if (_client.PreEstablishedConnection)
                    {
                        _client.NegotiatedConformance |= Enums.Conformance.GeneralProtection;
                    }
                }
            }
        }

        /// <summary>
        /// Send IEC disconnect message.
        /// </summary>
        void DiscIEC()
        {
            ReceiveParameters<string> p = new ReceiveParameters<string>()
            {
                AllData = false,
                Eop = (byte)0x0A,
                WaitTime = _device.WaitTime * 1000
            };
            string data = (char)0x01 + "B0" + (char)0x03 + "\r\n";
            _device.Media.Send(data, null);
            p.Eop = "\n";
            p.AllData = true;
            p.Count = 1;

            _device.Media.Receive(p);
        }
        /// <summary>
        /// Initialize optical head.
        /// </summary>
        void InitializeOpticalHead()
        {
            if (_client.InterfaceType != Enums.InterfaceType.HdlcWithModeE)
            {
                return;
            }
            GXSerial serial = _device.Media as GXSerial;
            byte Terminator = (byte)0x0A;
            _device.Media.Open();
            //Some meters need a little break.
            Thread.Sleep(1000);
            //Query device information.
            string data = "/?!\r\n";
            WriteTrace(TraceLevel.Info,
                "IEC Sending:" + data);
            ReceiveParameters<string> p = new ReceiveParameters<string>()
            {
                AllData = false,
                Eop = Terminator,
                WaitTime = _device.WaitTime * 1000
            };
            lock (_device.Media.Synchronous)
            {
                _device.Media.Send(data, null);
                if (!_device.Media.Receive(p))
                {
                    //Try to move away from mode E.
                    try
                    {
                        Disconnect();
                    }
                    catch (Exception)
                    {
                    }
                    DiscIEC();
                    string str = "Failed to receive reply from the device in given time.";
                    WriteTrace(TraceLevel.Verbose, str);
                    _device.Media.Send(data, null);
                    if (!_device.Media.Receive(p))
                    {
                        throw new Exception(str);
                    }
                }
                //If echo is used.
                if (p.Reply == data)
                {
                    p.Reply = null;
                    if (!_device.Media.Receive(p))
                    {
                        //Try to move away from mode E.
                        GXReplyData reply = new GXReplyData();
                        Disconnect();
                        if (serial != null)
                        {
                            DiscIEC();
                            serial.DtrEnable = serial.RtsEnable = false;
                            serial.BaudRate = 9600;
                            serial.DtrEnable = serial.RtsEnable = true;
                            DiscIEC();
                        }
                        data = "Failed to receive reply from the device in given time.";
                        WriteTrace(TraceLevel.Verbose, data);
                        throw new Exception(data);
                    }
                }
            }
            WriteTrace(TraceLevel.Verbose,
                "IEC received: " + p.Reply);
            int pos = 0;
            //With some meters there might be some extra invalid chars. Remove them.
            while (pos < p.Reply.Length && p.Reply[pos] != '/')
            {
                ++pos;
            }
            if (p.Reply[pos] != '/')
            {
                p.WaitTime = 100;
                _device.Media.Receive(p);
                DiscIEC();
                throw new Exception("Invalid responce.");
            }
            string manufactureID = p.Reply.Substring(1 + pos, 3);
            char baudrate = p.Reply[4 + pos];
            int BaudRate = 0;
            switch (baudrate)
            {
                case '0':
                    BaudRate = 300;
                    break;
                case '1':
                    BaudRate = 600;
                    break;
                case '2':
                    BaudRate = 1200;
                    break;
                case '3':
                    BaudRate = 2400;
                    break;
                case '4':
                    BaudRate = 4800;
                    break;
                case '5':
                    BaudRate = 9600;
                    break;
                case '6':
                    BaudRate = 19200;
                    break;
                default:
                    throw new Exception("Unknown baud rate.");
            }
            WriteTrace(TraceLevel.Info,
                "\tBaudRate is : " +
                BaudRate.ToString());
            //Send ACK
            //Send Protocol control character
            // "2" HDLC protocol procedure (Mode E)
            byte controlCharacter = (byte)'2';
            //Send Baud rate character
            //Mode control character
            byte ModeControlCharacter = (byte)'2';
            //"2" //(HDLC protocol procedure) (Binary mode)
            //Set mode E.
            byte[] arr = new byte[] { 0x06, controlCharacter, (byte)baudrate, ModeControlCharacter, 13, 10 };
            WriteTrace(TraceLevel.Verbose,
                DateTime.Now.ToLongTimeString() +
                "\tMoving to mode E." + arr);
            lock (_device.Media.Synchronous)
            {
                p.Reply = null;
                _device.Media.Send(arr, null);
                //Some meters need this sleep. Do not remove.
                Thread.Sleep(200);
                p.WaitTime = 2000;
                //Note! All meters do not echo this.
                _device.Media.Receive(p);
                if (p.Reply != null)
                {
                    WriteTrace(TraceLevel.Verbose, "Received: " + p.Reply);
                }
                if (serial != null)
                {
                    _device.Media.Close();
                    serial.BaudRate = BaudRate;
                    serial.DataBits = 8;
                    serial.Parity = Parity.None;
                    serial.StopBits = StopBits.One;
                    _device.Media.Open();
                }
                //Some meters need this sleep. Do not remove.
                Thread.Sleep(800);
            }
        }
        /// <summary>
        /// Initialize connection to the meter.
        /// </summary>
        public void InitializeConnection()
        {
            _device.Media.Open();
            WriteTrace(TraceLevel.Info,
                "Standard: " + _client.Standard);
            if (_client.Ciphering.Security != Enums.Security.None)
            {
                WriteTrace(TraceLevel.Info,
                    "Security: " + _client.Ciphering.Security);
                WriteTrace(TraceLevel.Info,
                    "System title: " + GXCommon.ToHex(_client.Ciphering.SystemTitle, true));
                WriteTrace(TraceLevel.Info,
                    "Authentication key: " + GXCommon.ToHex(_client.Ciphering.AuthenticationKey, true));
                WriteTrace(TraceLevel.Info,
                    "Block cipher key " + GXCommon.ToHex(_client.Ciphering.BlockCipherKey, true));
                if (_client.Ciphering.DedicatedKey != null)
                {
                    WriteTrace(TraceLevel.Info,
                        "Dedicated key: " + GXCommon.ToHex(_client.Ciphering.DedicatedKey, true));
                }
            }
            UpdateFrameCounter();
            InitializeOpticalHead();
            GXReplyData reply = new GXReplyData();
            SNRMRequest();
            if (!_client.PreEstablishedConnection)
            {
                //Generate AARQ request.
                //Split requests to multiple packets if needed.
                //If password is used all data might not fit to one packet.
                foreach (byte[] it in _client.AARQRequest())
                {
                    WriteTrace(TraceLevel.Verbose,
                        "Send AARQ request" + GXCommon.ToHex(it, true));
                    reply.Clear();
                    ReadDataBlock(it, reply);
                }
                WriteTrace(TraceLevel.Info,
                    "Parsing AARE reply" + reply.ToString());
                //Parse reply.
                _client.ParseAAREResponse(reply.Data);
                WriteTrace(TraceLevel.Info, "Conformance: " +
                    _client.NegotiatedConformance);
                reply.Clear();
                //Get challenge Is HLS authentication is used.
                if (_client.Authentication > Enums.Authentication.Low)
                {
                    foreach (byte[] it in _client.GetApplicationAssociationRequest())
                    {
                        reply.Clear();
                        ReadDataBlock(it, reply);
                    }
                    _client.ParseApplicationAssociationResponse(reply.Data);
                }
                WriteTrace(TraceLevel.Info,
                    "Parsing AARE reply succeeded.");
            }
        }

        private void EnableUI(bool open)
        {
            if (open)
            {
                _open.Text = GetString(Resource.String.close);
            }
            else
            {
                _open.Text = GetString(Resource.String.open);
            }
            _read.Enabled = open && _cosemObjects.Any();
            _refresh.Enabled = open;
        }

        public void OnMediaStateChange(object sender, MediaStateEventArgs e)
        {
            try
            {
                if (e.State == MediaState.Open ||
                        e.State == MediaState.Closed)
                {
                    Activity.RunOnUiThread(() =>
                    {
                        EnableUI(e.State == MediaState.Open);
                    });
                }
            }
            catch (Exception ex)
            {
                GXGeneral.ShowError(Activity, ex, GetString(Resource.String.error));
            }
        }

        public override void OnStop()
        {
            try
            {
                Close();
            }
            catch (Exception e)
            {
                GXGeneral.ShowError(Activity, e, GetString(Resource.String.error));
            }
            base.OnStop();
        }

        public override void OnDestroy()
        {
            try
            {
                Close();
                _device.Media.OnMediaStateChange -= OnMediaStateChange;
            }
            catch (Exception e)
            {
                GXGeneral.ShowError(Activity, e, GetString(Resource.String.error));
            }
            base.OnDestroy();
        }
    }
}