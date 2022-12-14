package gurux.dlms;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import gurux.dlms.internal.AutoResetEvent;

class GXDLMSConverterAsyncUpdater extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    Context mContext;
    Exception mException;

    /**
     * Is task completed.
     */
    private AutoResetEvent completed = new AutoResetEvent(false);

    public GXDLMSConverterAsyncUpdater(Context context)
    {
        mContext = context;
    }

    public Exception getException()
    {
        return mException;
    }

    /**
     * Wait operation to complete.
     * @param timeout Timeout in ms.
     * @throws InterruptedException Interrupted exception.
     */
    public void waitComplete(int timeout) throws InterruptedException {
        completed.wait(timeout);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            mException = new Exception("Failed to read OBIS codes from the server.");
            String line, newline;
            String path = "obiscodes.txt";
            URL url = new URL("https://www.gurux.fi/obis/obiscodes.txt");
            URLConnection c = url.openConnection();
            try (InputStream io = c.getInputStream()) {
                try (InputStreamReader r = new InputStreamReader(io, StandardCharsets.UTF_8)) {
                    BufferedReader reader = new BufferedReader(r);
                    try (FileOutputStream writer = mContext.openFileOutput(path, MODE_PRIVATE)) {
                        newline = System.getProperty("line.separator");
                        while ((line = reader.readLine()) != null) {
                            writer.write(line.getBytes());
                            writer.write(newline.getBytes());
                        }
                    }
                }
            }
            mException = null;
        } catch (Exception e) {
            mException = e;
        }
        completed.set();
        return null;
    }
}
