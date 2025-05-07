package gurux.dlms.ui.internal;

/**
 * This interface is used to get value from the COSEM object.
 */
public interface IGXGet<T> {
    Object run(T object, Integer index);
}
