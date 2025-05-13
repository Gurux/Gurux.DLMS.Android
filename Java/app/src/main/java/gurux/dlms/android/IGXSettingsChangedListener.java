package gurux.dlms.android;

import gurux.common.IGXMedia;
import gurux.dlms.objects.GXDLMSObject;

/**
 * This interface is used to notify activity when device settings are changed..
 */
public interface IGXSettingsChangedListener {
    /**
     * New media type is selected.
     * @param value The new media type.
     */
    void onMediaChanged(IGXMedia value);


    /**
     * User has read association view from the meter.
     */
    void onAssociationChanged();

    /**
     * User has change device settings.
     */
    void onDeviceSettingChanged();
}
