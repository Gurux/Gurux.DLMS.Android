package gurux.dlms.android.ui.objects;

import androidx.lifecycle.ViewModel;

import gurux.common.IGXMedia;
import gurux.dlms.GXDLMSClient;
import gurux.dlms.android.IGXActionListener;
import gurux.dlms.objects.GXDLMSObject;

public class ObjectViewModel extends ViewModel {

    /*
     * Selected object.
     */
    private GXDLMSObject mObject;

    /*
     * Selected media.
     */
    private IGXMedia mMedia;

    /**
     * Used DLMS client.
     */
    private GXDLMSClient mClient;

    private IGXActionListener mListener;

    public <T> T getObject(Class<T> type) {
        return (T) mObject;
    }


    public void setObject(final GXDLMSObject value) {
        mObject = value;
    }

    public IGXMedia getMedia() {
        return mMedia;
    }

    public void setMedia(IGXMedia device) {
        mMedia = device;
    }

    public IGXActionListener getListener() {
        return mListener;
    }

    public void setListener(final IGXActionListener value) {
        mListener = value;
    }

    /**
     * @return Used DLMS client.
     */
    public GXDLMSClient getClient() {
        return mClient;
    }

    /**
     * @param value Used DLMS client.s
     */
    public void setClient(GXDLMSClient value) {
        mClient = value;
    }
}