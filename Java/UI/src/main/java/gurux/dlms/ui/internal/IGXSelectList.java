package gurux.dlms.ui.internal;

import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * This interface is used to notify when user selects item from the selection list.
 */
public interface IGXSelectList<T> {
    void run(Collection<T> value) throws Exception;
}
