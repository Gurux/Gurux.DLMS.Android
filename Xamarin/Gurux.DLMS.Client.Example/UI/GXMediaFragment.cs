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
using AndroidX.Fragment.App;

namespace Gurux.DLMS.Client.Example.UI
{
    /// <summary>
    /// This fragment is used to shown media settings.
    /// </summary>
    public class GXMediaFragment : Fragment
    {
        /// <summary>
        /// Device.
        /// </summary>
        private readonly GXDevice _device;

        public GXMediaFragment(GXDevice device)
        {
            _device = device;
        }
      
        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View view = inflater.Inflate(Resource.Layout.fragment_media, container, false);
            Fragment childFragment = _device.Media.PropertiesForm;
            var transaction = ChildFragmentManager.BeginTransaction();
            transaction.Replace(Resource.Id.media_fragment_container, childFragment);
            transaction.Commit();
            return view;
        }
    }
}