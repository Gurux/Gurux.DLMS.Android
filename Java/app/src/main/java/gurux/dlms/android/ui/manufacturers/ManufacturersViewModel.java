package gurux.dlms.android.ui.manufacturers;

import androidx.lifecycle.ViewModel;

import gurux.dlms.manufacturersettings.GXManufacturerCollection;

public class ManufacturersViewModel extends ViewModel {

    private GXManufacturerCollection mManufacturers;


    public void updateManufacturers(GXManufacturerCollection manufacturers) {
        mManufacturers = manufacturers;
    }

    public GXManufacturerCollection getManufacturers() {
        return mManufacturers;
    }
}