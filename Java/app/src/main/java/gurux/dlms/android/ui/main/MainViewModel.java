package gurux.dlms.android.ui.main;

import androidx.lifecycle.ViewModel;

import gurux.dlms.android.GXDevice;
import gurux.dlms.ui.IGXActionListener;

public class MainViewModel extends ViewModel {

    private GXDevice mDevice;

    private IGXActionListener mListener;

    public GXDevice getDevice() {
        return mDevice;
    }

    public void setDevice(GXDevice device) {
        mDevice = device;
    }

    public IGXActionListener getListener() {
        return mListener;
    }

    public void setListener(final IGXActionListener value) {
        mListener = value;
    }
}