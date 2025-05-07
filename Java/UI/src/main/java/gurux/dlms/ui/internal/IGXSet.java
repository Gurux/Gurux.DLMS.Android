package gurux.dlms.ui.internal;

/**
 * This interface is used to set value to the COSEM object.
 */
public interface IGXSet<T> {
    void run(T object, Integer index, Object value) throws Exception;
}
