package gurux.dlms.android.ui.media;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import gurux.dlms.android.GXDevice;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;

public class MediaViewModel extends ViewModel {

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