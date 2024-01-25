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

using Gurux.Common;
using Gurux.DLMS.Enums;
using Gurux.DLMS.ManufacturerSettings;
using Gurux.DLMS.Objects;
using System.Diagnostics;

namespace Gurux.DLMS.Client.Example.UI
{
    /// <summary>
    /// DLMS meter settings.
    /// </summary>
    public class GXDevice
    {
        /// <summary>
        /// Constructor.
        /// </summary>
        public GXDevice()
        {
            Authentication = new GXAuthentication(Enums.Authentication.None, 16);
        }

        /// <summary>
        /// Flag name of the manufacturer.
        /// </summary>
        public string Manufacturer
        {
            get;
            set;
        }

        /// <summary>
        /// Wait time in seconds.
        /// </summary>
        public int WaitTime
        {
            get;
            set;
        } = 5;

        /// <summary>
        /// Retry count.
        /// </summary>
        public int RetryCount
        {
            get;
            set;
        } = 1;

        /// <summary>
        /// Maximum baud rate.
        /// </summary>
        public int MaximumBaudRate
        {
            get;
            set;
        }

        /// <summary>
        /// Used authentication.
        /// </summary>
        public GXAuthentication Authentication
        {
            get;
            set;
        }

        /// <summary>
        /// Is logican name referencing used.
        /// </summary>
        public bool UseLN
        {
            get;
            set;
        } = true;

        /// <summary>
        /// Password is used only if authentication is used.
        /// </summary>
        public string Password
        {
            get;
            set;
        } = "";

        /// <summary>
        /// Used communication security.
        /// </summary>
        public Security Security
        {
            get;
            set;
        }

        /// <summary>
        /// System Title.
        /// </summary>
        public string SystemTitle
        {
            get;
            set;
        }

        /// <summary>
        /// Block cipher key.
        /// </summary>
        public string BlockCipherKey
        {
            get;
            set;
        }

        /// <summary>
        /// Authentication key.
        /// </summary>
        public string AuthenticationKey
        {
            get;
            set;
        }

        /// <summary>
        /// Client address.
        /// </summary>
        public int ClientAddress
        {
            get;
            set;
        }

        /// <summary>
        /// Physical address.
        /// </summary>
        public int PhysicalAddress
        {
            get;
            set;
        } = 1;

        /// <summary>
        /// Logical address.
        /// </summary>
        public int LogicalAddress
        {
            get;
            set;
        }

        /// <summary>
        /// Interface type.
        /// </summary>
        public InterfaceType InterfaceType
        {
            get;
            set;
        } = InterfaceType.HDLC;

        /// <summary>
        /// Address type.
        /// </summary>
        public HDLCAddressType AddressType
        {
            get;
            set;
        } = HDLCAddressType.Default;

        /// <summary>
        /// COSEM objects.
        /// </summary>
        public GXDLMSObjectCollection Objects
        {
            get;
            set;
        } = new GXDLMSObjectCollection();

        /// <summary>
        /// Media.
        /// </summary>
        public IGXMedia Media
        {
            get;
            set;
        }

        /// <summary>
        /// Trace level.
        /// </summary>
        public TraceLevel Trace
        {
            get;
            set;
        }

        /// <summary>
        /// Invocation counter.
        /// </summary>
        public string InvocationCounter
        {
            get;
            set;
        }
    }
}