package gurux.dlms.ui;

import gurux.dlms.objects.GXDLMSObject;

/**
 * This interface is used to communicate with the meter.
 */
public interface IGXActionListener {

    /**
     * Read selected attribute.
     *
     * @param object Read object.
     * @param index  Attribute index.
     */
    void onRead(GXDLMSObject object, int index);

    /**
     * Write selected attribute.
     *
     * @param object Written object.
     * @param index  Attribute index.
     */
    void onWrite(GXDLMSObject object, int index);

    /**
     * Invoke selected method attribute.
     *
     * @param frames Invoke frame.
     */
    void onInvoke(byte[][] frames,  IGXResultHandler handler);

    /**
     * Send raw data.
     *
     * @param frames Invoke frame.
     * @return Received data.
     */
    void onRaw(byte[][] frames, IGXResultHandler handler);

    /**
     * User has changed the attribute value.
     *
     * @param target Changed COSEM object.
     * @param index  Attribute index.
     */
    void onObjectChanged(GXDLMSObject target, int index);

    /**
     * @return True, if transaction (read or write) is in progress.
     */
    boolean isProgress();
}
