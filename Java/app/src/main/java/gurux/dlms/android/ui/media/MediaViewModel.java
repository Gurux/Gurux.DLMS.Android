package gurux.dlms.android.ui.media;

import androidx.lifecycle.ViewModel;

import java.util.List;

import gurux.common.IGXMedia;
import gurux.dlms.android.GXDevice;

public class MediaViewModel extends ViewModel {

    /*
     * Selected device.
     */
    private GXDevice mDevice;

    /*
     * List of available medias.
     */
    private IGXMedia[] mMedias;

    public GXDevice getDevice() {
        return mDevice;
    }


    public void setDevice(GXDevice device) {
        mDevice = device;
    }


    public IGXMedia[] getMedias() {
        return mMedias;
    }

    public void setMedias(IGXMedia[] mMedias) {
        this.mMedias = mMedias;
    }
}