package gurux.dlms.android.ui.meterSettings;

import androidx.lifecycle.ViewModel;

import gurux.dlms.android.GXDevice;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;

public class MeterSettingsViewModel extends ViewModel {

    private GXManufacturerCollection mManufacturers;
    private GXDevice mDevice;

    public void updateManufacturers(GXManufacturerCollection manufacturers) {
        mManufacturers = manufacturers;
    }

    public GXManufacturerCollection getManufacturers() {
        return mManufacturers;
    }

    public GXDevice getDevice() {
        return mDevice;
    }

    public void setDevice(GXDevice device) {
        mDevice = device;
    }
}