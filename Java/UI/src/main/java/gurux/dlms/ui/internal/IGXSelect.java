package gurux.dlms.ui.internal;

/**
 * This interface is used to notify when user selects item from the selection list.
 */
public interface IGXSelect<T> {
    void run(T value, Integer index) throws Exception;
}
