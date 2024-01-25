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
using System;
using System.Text;

namespace Gurux.DLMS.Client.Example.UI
{
    public class GXObisTranslatorFragment : Fragment
    {
        private Button _search;
        private GXDLMSConverter _converter = new GXDLMSConverter();
        private EditText _obiscode;
        private EditText _obisResult;
        private EditText _filter;

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View view = inflater.Inflate(Resource.Layout.fragment_obis_translator, container, false);
            _search = (Button)view.FindViewById(Resource.Id.search);
            _obiscode = (EditText)view.FindViewById(Resource.Id.obiscode);
            _filter = (EditText)view.FindViewById(Resource.Id.filter);
            _obisResult = (EditText)view.FindViewById(Resource.Id.obisResult);
            _search.Click += (sender, args) =>
            {
                try
                {
                    StringBuilder sb = new StringBuilder();
                    _obisResult.Text = "";
                    string[] res = _converter.GetDescription(_obiscode.Text, _filter.Text);
                    foreach (string it in res)
                    {
                        sb.Append(it);
                        sb.Append(System.Environment.NewLine);
                    }
                    _obisResult.Text = sb.ToString();
                }
                catch (Exception e)
                {
                   // GXGeneral.showError(getActivity(), e, getString(R.string.error));
                }
            };
            try
            {
                //            mConverter.update(getActivity());
            }
            catch (Exception e)
            {
                //          GXGeneral.showError(getActivity(), e, getString(R.string.error));
            }
            return view;
        }
    }
}