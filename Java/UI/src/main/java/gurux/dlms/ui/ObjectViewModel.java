package gurux.dlms.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import gurux.common.IGXMedia;
import gurux.dlms.GXDLMSClient;
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

    /**
     * Is read, write or action is progress.
     */
    private final MutableLiveData<Boolean> mInProgress = new MutableLiveData<>();

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

    /**
     *
     * @return Is read, write or action is progress.
     */
    public LiveData<Boolean> getInProgress() {
        return mInProgress;
    }

    /**
     *
     * @param value Is read, write or action is progress.
     */
    public void setInProgress(final boolean value) {
        mInProgress.setValue(value);
    }
}