package gurux.dlms;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import gurux.dlms.enums.DataType;
import gurux.dlms.enums.ObjectType;
import gurux.dlms.enums.Standard;
import gurux.dlms.internal.AutoResetEvent;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSObjectCollection;

public class GXDLMSConverter {
    /**
     * Collection of standard OBIS codes.
     */
    private final GXStandardObisCodeCollection codes = new GXStandardObisCodeCollection();

    private Standard standard;

    /**
     * Constructor.
     */
    public GXDLMSConverter() {

    }

    /**
     * Constructor.
     *
     * @param value
     *            Used standard.
     */
    public GXDLMSConverter(Standard value) {
        standard = value;
    }

    /**
     * @param context Context.
     * @return True, if this is the first run.
     */
    public static boolean isFirstRun(final Context context) {
        File dir = context.getFilesDir();
        String[] files = dir.list();
        if (files != null)
        {
            for (String it : files) {
                if (it.equalsIgnoreCase("obiscodes.txt")) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Update OBIS codes.
     *
     * @param context Context.
     */
    public void update(final Context context) throws Exception {
        if (isFirstRun(context))
        {
            AutoResetEvent completed = new AutoResetEvent(false);
            final Exception[] mException = {null};
            Thread thread = new Thread(() -> {
                try {
                    String line, newline;
                    String path = "obiscodes.txt";
                    URL url = new URL("https://www.gurux.fi/obis/obiscodes.txt");
                    URLConnection c = url.openConnection();
                    try (InputStream io = c.getInputStream()) {
                        try (InputStreamReader r = new InputStreamReader(io, StandardCharsets.UTF_8)) {
                            BufferedReader reader = new BufferedReader(r);
                            try (FileOutputStream writer = context.openFileOutput(path, MODE_PRIVATE)) {
                                newline = System.getProperty("line.separator");
                                while ((line = reader.readLine()) != null) {
                                    writer.write(line.getBytes());
                                    writer.write(newline.getBytes());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    mException[0] = e;
                }
                completed.set();
            });

            thread.start();
            completed.waitOne(60000);
            if (mException[0] != null)
            {
                throw mException[0];
            }
        }
        readStandardObisInfo(context, codes);
    }

    /**
     * Get OBIS code description.
     *
     * @param logicalName Logical name (OBIS code).
     * @return Array of descriptions that match given OBIS code.
     */
    public final String[] getDescription(final String logicalName) {
        return getDescription(logicalName, ObjectType.NONE);
    }

    /**
     * Get OBIS code description.
     * 
     * @param logicalName
     *            Logical name (OBIS code).
     * @param description
     *            Description filter.
     * @return Array of descriptions that match given OBIS code.
     */
    public final String[] getDescription(final String logicalName,
            final String description) {
        return getDescription(logicalName, ObjectType.NONE, description);
    }

    /**
     * Get OBIS code description.
     * 
     * @param logicalName
     *            Logical name (OBIS code).
     * @param type
     *            Object type.
     * @return Array of descriptions that match given OBIS code.
     */
    public final String[] getDescription(final String logicalName,
            final ObjectType type) {
        return getDescription(logicalName, type, null);
    }

    /**
     * Get OBIS code description.
     * 
     * @param logicalName
     *            Logical name (OBIS code).
     * @param type
     *            Object type.
     * @param description
     *            Description filter.
     * @return Array of descriptions that match given OBIS code.
     */
    public final String[] getDescription(final String logicalName,
            final ObjectType type, final String description) {
        if (codes.size() == 0) {
            throw new RuntimeException("Read OBIS codes first.");
        }
        List<String> list = new ArrayList<>();
        boolean all = logicalName == null || logicalName.isEmpty();
        for (GXStandardObisCode it : codes.find(logicalName, type)) {
            if (description != null && !description.isEmpty()
                    && !it.getDescription().toLowerCase()
                            .contains(description.toLowerCase())) {
                continue;
            }
            if (all) {
                list.add("A=" + it.getOBIS()[0] + ", B=" + it.getOBIS()[1]
                        + ", C=" + it.getOBIS()[2] + ", D=" + it.getOBIS()[3]
                        + ", E=" + it.getOBIS()[4] + ", F=" + it.getOBIS()[5]
                        + "\r\n" + it.getDescription());
            } else {
                list.add(it.getDescription());
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * Update OBIS code information.
     * 
     * @param codes
     *            COSEM objects.
     * @param it
     *            COSEM object.
     * @param standard
     *            used DLMS standard.
     */
    private static void updateOBISCodeInfo(
            final GXStandardObisCodeCollection codes, final GXDLMSObject it,
            final Standard standard) {
        String ln = it.getLogicalName();
        GXStandardObisCode[] list = codes.find(ln, it.getObjectType());
        GXStandardObisCode code = list[0];
        if (code != null) {
            if (it.getDescription() == null || it.getDescription().equals("")) {
                it.setDescription(code.getDescription());
            }
            // Update data type from DLMS standard.
            if (standard != Standard.DLMS) {
                GXStandardObisCode d = list[list.length - 1];
                code.setDataType(d.getDataType());
            }
            if (code.getUIDataType() == null) {
                // If string is used
                if (code.getDataType().contains("10")) {
                    code.setUIDataType("10");
                } else if (code.getDataType().contains("25")
                        || code.getDataType().contains("26")) {
                    // If date time is used.
                    code.setUIDataType("25");
                } else if (code.getDataType().contains("9")) {
                    // Time stamps of the billing periods objects (first
                    // scheme
                    // if there are two)
                    if ((GXStandardObisCodeCollection
                            .equalsMask("0.0-64.96.7.10-14.255", ln)
                            // Time stamps of the billing periods objects
                            // (second scheme)
                            || GXStandardObisCodeCollection
                                    .equalsMask("0.0-64.0.1.5.0-99,255", ln)
                            // Time of power failure
                            || GXStandardObisCodeCollection
                                    .equalsMask("0.0-64.0.1.2.0-99,255", ln)
                            // Time stamps of the billing periods
                            // objects (first
                            // scheme if there are two)
                            || GXStandardObisCodeCollection
                                    .equalsMask("1.0-64.0.1.2.0-99,255", ln)
                            // Time stamps of the billing periods
                            // objects
                            // (second scheme)
                            || GXStandardObisCodeCollection
                                    .equalsMask("1.0-64.0.1.5.0-99,255", ln)
                            // Time expired since last end of
                            // billing
                            // period
                            || GXStandardObisCodeCollection
                                    .equalsMask("1.0-64.0.9.0.255", ln)
                            // Time of last reset
                            || GXStandardObisCodeCollection
                                    .equalsMask("1.0-64.0.9.6.255", ln)
                            // Date of last reset
                            || GXStandardObisCodeCollection
                                    .equalsMask("1.0-64.0.9.7.255", ln)
                            // Time expired since last end of
                            // billing
                            // period
                            // (Second billing period scheme)
                            || GXStandardObisCodeCollection
                                    .equalsMask("1.0-64.0.9.13.255", ln)
                            // Time of last reset (Second billing
                            // period
                            // scheme)
                            || GXStandardObisCodeCollection
                                    .equalsMask("1.0-64.0.9.14.255", ln)
                            // Date of last reset (Second billing
                            // period
                            // scheme)
                            || GXStandardObisCodeCollection
                                    .equalsMask("1.0-64.0.9.15.255", ln))) {
                        code.setUIDataType("25");
                    } else if (GXStandardObisCodeCollection
                            .equalsMask("1.0-64.0.9.1.255", ln)) {
                        // Local time
                        code.setUIDataType("27");
                    } else if (GXStandardObisCodeCollection
                            .equalsMask("1.0-64.0.9.2.255", ln)) {
                        // Local date
                        code.setUIDataType("26");
                    }
                    // Active firmware identifier
                    else if (GXStandardObisCodeCollection
                            .equalsMask("1.0.0.2.0.255", ln)) {
                        code.setUIDataType("10");
                    }
                }
                // Unix time
                else if (it.getObjectType() == ObjectType.DATA
                        && GXStandardObisCodeCollection
                                .equalsMask("0.0.1.1.0.255", ln)) {
                    code.setUIDataType("25");
                }
            }
            if (!code.getDataType().equals("*")
                    && !code.getDataType().equals("")
                    && !code.getDataType().contains(",")) {
                DataType type =
                        DataType.forValue(Integer.parseInt(code.getDataType()));
                ObjectType objectType = it.getObjectType();
                if (objectType == ObjectType.DATA
                        || objectType == ObjectType.REGISTER
                        || objectType == ObjectType.REGISTER_ACTIVATION
                        || objectType == ObjectType.EXTENDED_REGISTER) {
                    it.setDataType(2, type);
                }
            }
            if (code.getUIDataType() != null
                    && !code.getUIDataType().isEmpty()) {
                DataType type = DataType
                        .forValue(Integer.parseInt(code.getUIDataType()));
                ObjectType objectType = it.getObjectType();
                if (objectType == ObjectType.DATA
                        || objectType == ObjectType.REGISTER
                        || objectType == ObjectType.REGISTER_ACTIVATION
                        || objectType == ObjectType.EXTENDED_REGISTER) {
                    it.setUIDataType(2, type);
                }
            }
        } else {
            System.out.println("Unknown OBIS Code: " + it.getLogicalName()
                    + " Type: " + it.getObjectType());
        }
    }

    /**
     * Update standard OBIS codes description and type if defined.
     *
     * @param object COSEM object.
     */
    public final void updateOBISCodeInformation(final Context context, final GXDLMSObject object) {
        synchronized (codes) {
            if (codes.size() == 0) {
                /* OBIS codes are not updated if context is unknown. */
                if (context == null){
                    return;
                }
                readStandardObisInfo(context, codes);
            }
            updateOBISCodeInfo(codes, object, standard);
        }
    }

    /**
     * Update standard OBIS codes descriptions and type if defined.
     *
     * @param objects Collection of COSEM objects to update.
     */
    public final void
    updateOBISCodeInformation(final Context context, final GXDLMSObjectCollection objects) {
        synchronized (codes) {
            if (codes.size() == 0) {
                if (context == null){
                    return;
                }
                readStandardObisInfo(context, codes);
            }
            for (GXDLMSObject it : objects) {
                updateOBISCodeInfo(codes, it, standard);
            }
        }
    }

    /**
     * Read standard OBIS code information from the file.
     *
     * @param codes Collection of standard OBIS codes.
     */
    private static void
    readStandardObisInfo(final Context context, final GXStandardObisCodeCollection codes) {
        try (java.io.FileInputStream stream = context.openFileInput("obiscodes.txt")) {
            if (stream == null) {
                return;
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1000];
            try {
                while ((nRead = stream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            String str = buffer.toString();
            String newline = System.getProperty("line.separator");
            List<String> rows = GXCommon.split(str, newline);
            for (String it : rows) {
                if (!it.isEmpty()) {
                    List<String> items = GXCommon.split(it, ';');
                    List<String> obis = GXCommon.split(items.get(0), '.');
                    GXStandardObisCode code = new GXStandardObisCode(
                            obis.toArray(new String[0]),
                            items.get(3) + "; " + items.get(4) + "; " + items.get(5)
                                    + "; " + items.get(6) + "; " + items.get(7),
                            items.get(1), items.get(2));
                    codes.add(code);
                }
            }
        } catch (Exception ex) {
            Log.e("gurux.dlms.android", ex.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    public static Class getDataType(final DataType value) {
        if (value == DataType.NONE) {
            return null;
        }
        if (value == DataType.OCTET_STRING) {
            return byte[].class;
        }
        if (value == DataType.ENUM) {
            return GXEnum.class;
        }
        if (value == DataType.INT8) {
            return Byte.class;
        }
        if (value == DataType.INT16) {
            return Short.class;
        }
        if (value == DataType.INT32) {
            return Integer.class;
        }
        if (value == DataType.INT64) {
            return Long.class;
        }
        if (value == DataType.UINT8) {
            return GXUInt8.class;
        }
        if (value == DataType.UINT16) {
            return GXUInt16.class;
        }
        if (value == DataType.UINT32) {
            return GXUInt32.class;
        }
        if (value == DataType.UINT64) {
            return GXUInt64.class;
        }
        if (value == DataType.TIME) {
            return GXTime.class;
        }
        if (value == DataType.DATE) {
            return GXDate.class;
        }
        if (value == DataType.DATETIME) {
            return GXDateTime.class;
        }
        if (value == DataType.ARRAY) {
            return Object[].class;
        }
        if (value == DataType.STRING) {
            return String.class;
        }
        if (value == DataType.BOOLEAN) {
            return Boolean.class;
        }
        if (value == DataType.FLOAT32) {
            return Float.class;
        }
        if (value == DataType.FLOAT64) {
            return Double.class;
        }
        if (value == DataType.BITSTRING) {
            return GXBitString.class;
        }
        throw new IllegalArgumentException("Invalid value.");
    }

    public static DataType getDLMSDataType(final Object value) {
        if (value == null) {
            return DataType.NONE;
        }
        if (value instanceof byte[]) {
            return DataType.OCTET_STRING;
        }
        if (value instanceof Enum) {
            return DataType.ENUM;
        }
        if (value instanceof Byte) {
            return DataType.INT8;
        }
        if (value instanceof Short) {
            return DataType.INT16;
        }
        if (value instanceof Integer) {
            return DataType.INT32;
        }
        if (value instanceof Long) {
            return DataType.INT64;
        }
        if (value instanceof GXDateTimeOS) {
            return DataType.OCTET_STRING;
        }
        if (value instanceof GXTime) {
            return DataType.TIME;
        }
        if (value instanceof GXDate) {
            return DataType.DATE;
        }
        if (value instanceof java.util.Date || value instanceof GXDateTime) {
            return DataType.DATETIME;
        }
        if (value.getClass().isArray()) {
            return DataType.ARRAY;
        }
        if (value instanceof String) {
            return DataType.STRING;
        }
        if (value instanceof Boolean) {
            return DataType.BOOLEAN;
        }
        if (value instanceof Float) {
            return DataType.FLOAT32;
        }
        if (value instanceof Double) {
            return DataType.FLOAT64;
        }
        if (value instanceof BigInteger) {
            return DataType.UINT64;
        }
        if (value instanceof GXArray) {
            return DataType.ARRAY;
        }
        if (value instanceof GXStructure) {
            return DataType.STRUCTURE;
        }
        if (value instanceof GXBitString) {
            return DataType.BITSTRING;
        }
        if (value instanceof GXEnum) {
            return DataType.ENUM;
        }
        if (value instanceof GXByteBuffer) {
            return DataType.OCTET_STRING;
        }
        if (value instanceof GXUInt8) {
            return DataType.UINT8;
        }
        if (value instanceof GXUInt16) {
            return DataType.UINT16;
        }
        if (value instanceof GXUInt32) {
            return DataType.UINT32;
        }
        if (value instanceof GXUInt64) {
            return DataType.UINT64;
        }
        throw new IllegalArgumentException("Invalid value.");
    }

    public static Object changeType(final Object value, final DataType type) {
        if (getDLMSDataType(value) == type) {
            return value;
        }
        switch (type) {
        case ARRAY:
            throw new IllegalArgumentException("Can't change array types.");
        case BCD:
            break;
        case BOOLEAN:
            if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
            return ((Number) value).longValue() != 0;
        case COMPACT_ARRAY:
            throw new IllegalArgumentException(
                    "Can't change compact array types.");
        case DATE:
            return new GXDate((String) value);
        case DATETIME:
            return new GXDateTime((String) value);
        case ENUM:
            if (value instanceof String) {
                return new GXEnum(Short.parseShort((String) value));
            }
            return new GXEnum((byte) value);
        case FLOAT32:
            if (value instanceof String) {
                try {
                    return Float.parseFloat((String) value);
                } catch (NumberFormatException e) {
                    return Float.parseFloat(((String) value).replace(",", "."));
                }
            }
            return ((Number) value).floatValue();
        case FLOAT64:
            if (value instanceof String) {
                try {
                    return Double.parseDouble((String) value);
                } catch (NumberFormatException e) {
                    return Double
                            .parseDouble(((String) value).replace(",", "."));
                }
            }
            return ((Number) value).doubleValue();
        case INT16:
            if (value instanceof String) {
                return Short.parseShort((String) value);
            }
            return ((Number) value).shortValue();
        case INT32:
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
            return ((Number) value).intValue();
        case INT64:
            if (value instanceof String) {
                return Long.parseLong((String) value);
            }
            return ((Number) value).longValue();
        case INT8:
            if (value instanceof String) {
                return Byte.parseByte((String) value);
            }
            return (char) ((Number) value).byteValue();
        case NONE:
            return null;
        case OCTET_STRING:
            if (value instanceof String) {
                return GXCommon.hexToBytes((String) value);
            }
            throw new IllegalArgumentException(
                    "Can't change octet string type.");
        case STRING:
            case STRING_UTF8:
                return String.valueOf(value);
        case BITSTRING:
            return new GXBitString((String) value);
        case STRUCTURE:
            throw new IllegalArgumentException("Can't change structure types.");
        case TIME:
            return new GXTime((String) value);
        case UINT8:
            if (value instanceof String) {
                return new GXUInt8(Short.parseShort((String) value));
            }
            return new GXUInt8(((Number) value).shortValue());
        case UINT16:
            if (value instanceof String) {
                return new GXUInt16(Integer.parseInt((String) value));
            }
            return new GXUInt16(((Number) value).intValue());
        case UINT32:
            if (value instanceof String) {
                return new GXUInt32(Long.parseLong((String) value));
            }
            return new GXUInt32(((Number) value).longValue());
        case UINT64:
            if (value instanceof String) {
                return BigInteger.valueOf(Long.parseLong((String) value));
            }
            return BigInteger.valueOf(((Number) value).longValue());
        default:
            break;
        }
        throw new IllegalArgumentException("Invalid data type: " + type);
    }
}
