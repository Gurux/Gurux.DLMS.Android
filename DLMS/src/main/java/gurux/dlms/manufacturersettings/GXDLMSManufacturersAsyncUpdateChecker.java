package gurux.dlms.manufacturersettings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import gurux.dlms.internal.AutoResetEvent;

class GXDLMSManufacturersAsyncUpdateChecker extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    Context mContext;
    Exception mException;
    boolean mUpdates;

    /**
     * Is task completed.
     */
    private final AutoResetEvent completed = new AutoResetEvent(false);

    public GXDLMSManufacturersAsyncUpdateChecker(Context context)
    {
        mContext = context;
    }

    public Exception getException()
    {
        return mException;
    }

    public boolean isUpdates()
    {
        return mUpdates;
    }

    /**
     * Wait operation to complete.
     * @param timeout Timeout in ms.
     */
    public void waitComplete(int timeout) {
        completed.waitOne(timeout);
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            mException = new Exception("Failed to read manufacturer settings from the server.");
            Map<String, Date> installed = new HashMap<>();
            Map<String, Date> available = new HashMap<>();
            DateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            try (java.io.FileInputStream tmp = mContext.openFileInput("files.xml")) {
                XmlPullParser parser = Xml.newPullParser();
                URL url = new URL("https://www.gurux.fi/obis/files.xml");
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(tmp, null);
                int event;
                parser.nextTag();
                while ((event = parser.next()) != XmlPullParser.END_TAG) {
                    if (event == XmlPullParser.START_TAG) {
                        String target = parser.getName();
                        if (target.equalsIgnoreCase("file")) {
                            String data = parser.getAttributeValue(null, "Modified");
                            installed.put(readText(parser), format.parse(data));
                        }
                    }
                }
                URLConnection c = url.openConnection();
                try (InputStream io = c.getInputStream()) {
                    parser.setInput(io, null);
                    parser.nextTag();
                    while ((event = parser.next()) != XmlPullParser.END_TAG) {
                        if (event == XmlPullParser.START_TAG) {
                            String target = parser.getName();
                            if (target.equalsIgnoreCase("file")) {
                                String data = parser.getAttributeValue(null, "Modified");
                                available.put(readText(parser), format.parse(data));
                            }
                        }
                    }
                }
                for (Map.Entry<String, Date> it : available.entrySet()) {
                    // If new item is added.
                    if (!installed.containsKey(it.getKey())) {
                        mUpdates = true;
                        return null;
                    }
                    // If item is updated.
                    if (it.getValue().compareTo(installed.get(it.getKey())) != 0) {
                        mUpdates = true;
                        return null;
                    }
                }
                mException = null;
            }
        } catch (Exception e) {
            mUpdates = true;
            mException = e;
        }
        completed.set();
        return null;
    }
}
