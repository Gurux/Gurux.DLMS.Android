package gurux.dlms;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import gurux.dlms.enums.DataType;
import gurux.dlms.enums.ObjectType;
import gurux.dlms.enums.Standard;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSObjectCollection;

import static android.content.Context.MODE_PRIVATE;

public class GXDLMSConverter {
    /**
     * Collection of standard OBIS codes.
     */
    private GXStandardObisCodeCollection codes =
            new GXStandardObisCodeCollection();

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
        for (String it : files) {
            if (it.equalsIgnoreCase("obiscodes.txt")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Update OBIS codes.
     *
     * @param context
     */
    public void update(final Context context) {
        if (isFirstRun(context)) {
            String line, newline;
            try {
                String path = "obiscodes.txt";
                URL url = new URL("http://www.gurux.fi/obis/obiscodes.txt");
                URLConnection c = url.openConnection();
                try (InputStream io = c.getInputStream()) {
                    try (InputStreamReader r = new InputStreamReader(io, "utf-8")) {
                        BufferedReader reader = new BufferedReader(r);
                        try (FileOutputStream writer = context.openFileOutput(path, MODE_PRIVATE)) {
                            newline = System.getProperty("line.separator");
                            while ((line = reader.readLine()) != null) {
                                writer.write(line.getBytes());
                                writer.write(newline.getBytes());
                            }
                            r.close();
                            writer.close();
                        }
                    }
                    io.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
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
     * @param logicalName Logical name (OBIS code).
     * @param description Description filter.
     * @return Array of descriptions that match given OBIS code.
     */
    public final String[] getDescription(final String logicalName,
                                         final String description) {
        return getDescription(logicalName, ObjectType.NONE, description);
    }

    /**
     * Get OBIS code description.
     *
     * @param logicalName Logical name (OBIS code).
     * @param type        Object type.
     * @return Array of descriptions that match given OBIS code.
     */
    public final String[] getDescription(final String logicalName,
                                         final ObjectType type) {
        return getDescription(logicalName, type, null);
    }

    /**
     * Get OBIS code description.
     *
     * @param logicalName Logical name (OBIS code).
     * @param type        Object type.
     * @param description Description filter.
     * @return Array of descriptions that match given OBIS code.
     */
    public final String[] getDescription(final String logicalName,
                                         final ObjectType type, final String description) {
        if (codes.size() == 0) {
            throw new RuntimeException("Read OBIS codes first.");
        }
        List<String> list = new ArrayList<String>();
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
        return list.toArray(new String[list.size()]);
    }

    /**
     * Update OBIS code information.
     *
     * @param it COSEM object.
     */
    private static void updateOBISCodeInfo(
            final GXStandardObisCodeCollection codes, final GXDLMSObject it) {
        if (!(it.getDescription() == null || it.getDescription().equals(""))) {
            return;
        }
        String ln = it.getLogicalName();
        GXStandardObisCode code = codes.find(ln, it.getObjectType())[0];
        if (code != null) {
            it.setDescription(code.getDescription());
            // If string is used
            if (code.getDataType().contains("10")) {
                code.setDataType("10");
            } else if (code.getDataType().contains("25")
                    || code.getDataType().contains("26")) {
                // If date time is used.
                code.setDataType("25");
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
                    code.setDataType("25");
                } else if (GXStandardObisCodeCollection
                        .equalsMask("1.0-64.0.9.1.255", ln)) {
                    // Local time
                    code.setDataType("27");
                } else if (GXStandardObisCodeCollection
                        .equalsMask("1.0-64.0.9.2.255", ln)) {
                    // Local date
                    code.setDataType("26");
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
                readStandardObisInfo(context, codes);
            }
            updateOBISCodeInfo(codes, object);
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
                updateOBISCodeInfo(codes, it);
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
            return Enum.class;
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
            throw new IllegalArgumentException(
                    "Can't change enumeration types.");
        case FLOAT32:
            if (value instanceof String) {
                return Float.parseFloat((String) value);
            }
            return ((Number) value).floatValue();
        case FLOAT64:
            if (value instanceof String) {
                return Double.parseDouble((String) value);
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
            return ((Number) value).byteValue();
        case NONE:
            return null;
        case OCTET_STRING:
            if (value instanceof String) {
                return GXCommon.hexToBytes((String) value);
            }
            throw new IllegalArgumentException(
                    "Can't change octect string type.");
        case STRING:
        case BITSTRING:
            return String.valueOf(value);
        case STRING_UTF8:
            return String.valueOf(value);
        case STRUCTURE:
            throw new IllegalArgumentException("Can't change structure types.");
        case TIME:
            return new GXTime((String) value);
        case UINT16:
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
            return ((Number) value).intValue();
        case UINT32:
            if (value instanceof String) {
                return Long.parseLong((String) value);
            }
            return ((Number) value).longValue();
        case UINT64:
            if (value instanceof String) {
                return Long.parseLong((String) value);
            }
            return ((Number) value).longValue();
        case UINT8:
            if (value instanceof String) {
                return new Short(
                        (short) (Short.parseShort((String) value) & 0xFF));
            }
            return ((Number) value).byteValue();
        default:
            break;
        }
        throw new IllegalArgumentException(
                "Invalid data type: " + type.toString());
    }
}
