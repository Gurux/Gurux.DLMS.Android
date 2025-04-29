package gurux.dlms.android;

import gurux.common.IGXMedia;
import gurux.dlms.objects.GXDLMSObject;

/**
 * This interface is used to communicate with the meter.
 */
public interface IGXActionListener {

    /**
     * Read selected attribute.
     * @param object Read object.
     * @param index Attribute index.
     */
    void onRead(GXDLMSObject object, int index);


    /**
     * Write selected attribute.
     * @param object Written object.
     * @param index Attribute index.
     */
    void onWrite(GXDLMSObject object, int index);

    /**
     * Invoke selected method attribute.
     * @param frames Invoke frame.
     */
    void onInvoke(byte[][] frames);

}
