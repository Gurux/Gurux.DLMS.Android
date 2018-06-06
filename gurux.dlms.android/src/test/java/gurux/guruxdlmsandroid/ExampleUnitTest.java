package gurux.guruxdlmsandroid;

import org.junit.Test;

import gurux.dlms.GXDLMSClient;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.InterfaceType;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    /**
     * Basic SNRM test.
     */
    @Test
    public void snrmRequestTest() {
        GXDLMSClient cl = new GXDLMSClient(true, 1, 16, Authentication.NONE, null, InterfaceType.HDLC);
        assertEquals("7E A0 07 21 03 93 0F A7 7E", GXCommon.toHex(cl.snrmRequest()));
    }
}