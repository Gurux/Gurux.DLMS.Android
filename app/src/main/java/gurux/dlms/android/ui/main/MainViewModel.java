package gurux.dlms.android.ui.main;

import androidx.lifecycle.ViewModel;

import gurux.dlms.android.GXDevice;

public class MainViewModel extends ViewModel {

    private GXDevice mDevice;

    public GXDevice getDevice()
    {
        return mDevice;
    };

    public void setDevice(GXDevice device)
    {
        mDevice = device;
    };
}