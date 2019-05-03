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

import android.os.AsyncTask;

/**
 * This class is used to execute task.
 */
class GXTask extends AsyncTask<Void, Void, Object> {
    private IGXTaskCallback mHandler;

    /**
     * Executed Task.
     */
    private Task mTask;

    /**
     * Optional parameters.
     */
    private Object mParameter;

    /**
     * Constructor.
     *
     * @param handler Settings callback.
     * @param task    Executed task.
     */
    public GXTask(final IGXTaskCallback handler, final Task task) {
        mHandler = handler;
        mTask = task;
    }

    /**
     * Constructor.
     *
     * @param handler Settings callback.
     * @param task    Executed task.
     */
    public GXTask(final IGXTaskCallback handler, final Task task, final Object parameter) {
        mHandler = handler;
        mTask = task;
        mParameter = parameter;
    }


    @Override
    protected Object doInBackground(Void... voids) {
        try {
            mHandler.onExecute(this);
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        if (result instanceof Exception) {
            mHandler.onError(this, (Exception) result);
        } else {
            mHandler.onFinish(this, result);
        }
    }

    /**
     * @return Task ID.
     */
    public Task getTask() {
        return mTask;
    }

    /**
     *
     * @return Optional parameter.
     */
    public Object getParameter() {
        return mParameter;
    }
    /**
     *
     * @return Optional parameters.
     */
    public Object[] getParameters() {
        return (Object[])mParameter;
    }
}
