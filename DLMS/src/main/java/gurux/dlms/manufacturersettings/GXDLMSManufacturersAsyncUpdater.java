package gurux.dlms.manufacturersettings;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import gurux.dlms.internal.AutoResetEvent;

class GXDLMSManufacturersAsyncUpdater extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    Context mContext;
    Exception mException;
    boolean mUpdates;

    /**
     * Is task completed.
     */
    private AutoResetEvent completed = new AutoResetEvent(false);

    public GXDLMSManufacturersAsyncUpdater(Context context)
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
     * @throws InterruptedException Interrupted exception.
     */
    public void waitComplete(int timeout) throws InterruptedException {
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
        mUpdates = false;
        try {
            mException = new Exception("Failed to read OBIS codes from the server.");
            String line, newline;
            String path = "files.xml";
            URL url = new URL("https://www.gurux.fi/obis/files.xml");
            URLConnection c = url.openConnection();
            c.setDoInput(true);
            c.setDoOutput(true);
            try (InputStream io = c.getInputStream()) {
                try (InputStreamReader r = new InputStreamReader(io, StandardCharsets.UTF_8)) {
                    BufferedReader reader = new BufferedReader(r);
                    try (FileOutputStream writer = mContext.openFileOutput(path, MODE_PRIVATE)) {
                        newline = System.getProperty("line.separator");
                        while ((line = reader.readLine()) != null) {
                            writer.write(line.getBytes());
                            writer.write(newline.getBytes());
                        }
                        r.close();
                    }
                }
            }
            XmlPullParser parser = Xml.newPullParser();
            try (java.io.FileInputStream tmp = mContext.openFileInput(path)) {
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(tmp, null);
                int event;
                parser.nextTag();
                while ((event = parser.next()) != XmlPullParser.END_TAG) {
                    if (event == XmlPullParser.START_TAG) {
                        String target = parser.getName();
                        if (target.equalsIgnoreCase("file")) {
                            String file = readText(parser);
                            URL f = new URL("https://www.gurux.fi/obis/" + file);
                            c = f.openConnection();
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()))) {
                                try (FileOutputStream writer = mContext.openFileOutput(file, MODE_PRIVATE)) {
                                    while ((line = reader.readLine()) != null) {
                                        writer.write(line.getBytes());
                                        writer.write(newline.getBytes());
                                    }
                                    reader.close();
                                }
                            }
                        }
                    }
                }
            }
            mException = null;
        } catch (Exception e) {
            Log.i("gurux.dlms", e.getMessage());
            mUpdates = true;
            mException = e;
        }
        completed.set();
        return null;
    }
}
