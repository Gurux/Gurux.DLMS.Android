package gurux.guruxdlmsandroid;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import gurux.dlms.GXDLMSClient;
import gurux.dlms.GXDLMSConverter;
import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.TranslatorOutputType;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.InterfaceType;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("org.gurux.guruxdlmsandroid.test", appContext.getPackageName());
    }

    @Test
    public void snrmRequestTest() {
        GXDLMSClient cl = new GXDLMSClient(true, 1, 16, Authentication.NONE, null, InterfaceType.HDLC);
        assertEquals("7E A0 07 21 03 93 0F A7 7E", GXCommon.toHex(cl.snrmRequest()));
    }

    /**
     * Get manufacturers from gurux WWW server.
     */
    @Test
    public void getmanufacturersTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        GXManufacturerCollection.updateManufactureSettings(appContext);
        assertEquals(false, GXManufacturerCollection.isFirstRun(appContext));
        GXManufacturerCollection manufacturers = new GXManufacturerCollection();
        GXManufacturerCollection.readManufacturerSettings(appContext, manufacturers);
        assertEquals(false, GXManufacturerCollection.isUpdatesAvailable(appContext));
    }

    /**
     * OBIS converter test.
     */
    @Test
    public void obisConverterTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        GXDLMSConverter c = new GXDLMSConverter();
        c.update(appContext);
        assertEquals("Ch. 0 Clock object  1", c.getDescription("0.0.1.0.0.255")[0]);
    }

    /**
     * XML converter test.
     */
    @Test
    public void xmlTest() {
        String input = "0501022BC8";
        String expected = "<ReadRequest Qty=\"01\" >\r\n  <VariableName Value=\"2BC8\" />\r\n</ReadRequest>\r\n";
        GXDLMSTranslator converter =
                new GXDLMSTranslator(TranslatorOutputType.SIMPLE_XML);
        String actual = converter.pduToXml(input);
        assertEquals(expected, actual);
        String output = converter.xmlToHexPdu(actual);
        assertEquals(input, output);
    }
}
