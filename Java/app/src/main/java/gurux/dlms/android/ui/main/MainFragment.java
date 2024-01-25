package gurux.dlms.android.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gurux.common.GXCommon;
import gurux.common.IGXMedia;
import gurux.common.IGXMediaListener;
import gurux.common.MediaStateEventArgs;
import gurux.common.PropertyChangedEventArgs;
import gurux.common.ReceiveEventArgs;
import gurux.common.ReceiveParameters;
import gurux.common.TraceEventArgs;
import gurux.common.enums.MediaState;
import gurux.dlms.GXDLMSClient;
import gurux.dlms.GXDLMSConverter;
import gurux.dlms.GXDLMSException;
import gurux.dlms.GXReplyData;
import gurux.dlms.GXSimpleEntry;
import gurux.dlms.android.GXDevice;
import gurux.dlms.android.GXGeneral;
import gurux.dlms.android.R;
import gurux.dlms.android.databinding.FragmentMainBinding;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.ErrorCode;
import gurux.dlms.enums.InterfaceType;
import gurux.dlms.enums.ObjectType;
import gurux.dlms.enums.RequestTypes;
import gurux.dlms.objects.GXDLMSDemandRegister;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSObjectCollection;
import gurux.dlms.objects.GXDLMSProfileGeneric;
import gurux.dlms.objects.GXDLMSRegister;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.secure.GXDLMSSecureClient;
import gurux.io.BaudRate;
import gurux.io.Parity;
import gurux.io.StopBits;
import gurux.serial.GXSerial;

public class MainFragment extends Fragment implements IGXMediaListener {

    ListView mObjects;
    GXDevice mDevice;
    GXDLMSSecureClient mClient;
    EditText mAttributes;
    Button mOpen;
    Button mRead;
    Button mRefresh;
    SearchView mSearch;
    List<GXDLMSObject> mCosemObjects = new ArrayList<>();
    CheckBox mShowTrace;
    EditText mTrace;
    private FragmentMainBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainViewModel mainViewModel =
                new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mDevice = mainViewModel.getDevice();
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        try {
            mObjects = view.findViewById(R.id.objects);
            mAttributes =  view.findViewById(R.id.attributes);
            mOpen = view.findViewById(R.id.open);
            mRead = view.findViewById(R.id.read);
            mRefresh = view.findViewById(R.id.refresh);
            mSearch = view.findViewById(R.id.search);
            mShowTrace =view.findViewById(R.id.showTrace);
            mTrace =  view.findViewById(R.id.trace);
            mTrace.setVisibility(View.GONE);

            int serverAddress = GXDLMSClient.getServerAddress(mDevice.getLogicalAddress(),
                    mDevice.getPhysicalAddress());
            mClient = new GXDLMSSecureClient(true, mDevice.getClientAddress(), serverAddress,
                    mDevice.getAuthentication().getType(), mDevice.getPassword(), InterfaceType.HDLC);

            mShowTrace.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mTrace.setVisibility(View.VISIBLE);
                    mAttributes.setVisibility(View.GONE);
                } else {
                    mTrace.setVisibility(View.GONE);
                    mAttributes.setVisibility(View.VISIBLE);
                }
            }
            );
            mOpen.setOnClickListener(v -> {
                try {
                    //Clear trace.
                    mTrace.setText("");
                    IGXMedia media = mDevice.getMedia();
                    if (media.isOpen()) {
                        //Close connection.
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executor.execute(() -> handler.post(() -> {
                            try {
                                close();
                                Toast.makeText(getActivity(), "Disconnected.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                GXGeneral.showError(getActivity(), e, getString(R.string.error));
                            }
                        }));
                    } else {
                        //Open connection.
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executor.execute(() -> handler.post(() -> {
                            try {
                                initializeConnection();
                                Toast.makeText(getActivity(), "Connected.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                GXGeneral.showError(getActivity(), e, getString(R.string.error));
                            }
                        }));
                    }
                } catch (Exception e) {
                    GXGeneral.showError(getActivity(), e, getString(R.string.error));
                }
            });
            mRead.setOnClickListener(v -> {
                try {
                    //Clear trace.
                    mTrace.setText("");
                    //Read selected item.
                    int pos = mObjects.getCheckedItemPosition();
                    if (pos != -1) {
                        GXDLMSObject obj = mCosemObjects.get(pos);
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executor.execute(() -> handler.post(() -> {
                            try {
                                read(obj);
                                showObject( obj);
                                Toast.makeText(getActivity(), "Read done.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                GXGeneral.showError(getActivity(), e, getString(R.string.error));
                            }
                        }));
                    }
                } catch (Exception e) {
                    GXGeneral.showError(getActivity(), e, getString(R.string.error));
                }
            });
            mRefresh.setOnClickListener(v -> {
                //Clear trace.
                mTrace.setText("");
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executor.execute(() -> handler.post(() -> {
                    try {
                    refresh();
                    showObjects("");
                    Toast.makeText(getActivity(), "Refresh done.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        GXGeneral.showError(getActivity(), e, getString(R.string.error));
                    }
                }));
            });

            mObjects.setOnItemClickListener((parent, view1, position, id) -> {
                try {
                    showObject(mDevice.getObjects().get(position));
                } catch (Exception e) {
                    GXGeneral.showError(getActivity(), e, getString(R.string.error));
                }
            });

            showObjects("");
            if (mDevice != null) {
                if (mDevice.getMedia() != null) {
                    mDevice.getMedia().addListener(this);
                    enableUI(mDevice.getMedia().isOpen());
                }
                if (mDevice.getManufacturer() == null || mDevice.getManufacturer().isEmpty()) {
                    Toast.makeText(getActivity(), R.string.invalidManufacturer, Toast.LENGTH_SHORT).show();
                    mOpen.setEnabled(false);
                }
                if (mDevice.getMedia() instanceof GXSerial && ((GXSerial) mDevice.getMedia()).getPort() == null) {
                    Toast.makeText(getActivity(), R.string.invalidSerialPort, Toast.LENGTH_SHORT).show();
                    mOpen.setEnabled(false);
                }
            }

            mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    try {
                        showObjects(newText);
                        ((BaseAdapter) mObjects.getAdapter()).notifyDataSetChanged();
                    } catch (Exception e) {
                        GXGeneral.showError(getActivity(), e, getString(R.string.error));
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    /**
     * Show selected object
     *
     * @param target Selected COSEM object.
     */
    private void showObject(GXDLMSObject target) {
        mAttributes.setText("");
        StringBuilder sb = new StringBuilder();
        String newline = System.getProperty("line.separator");
        sb.append(target.getLogicalName());
        if (target.getShortName() != 0)
        {
            sb.append(" (");
            sb.append(target.getShortName());
            sb.append(")");
        }
        sb.append(newline);
        sb.append(target.getDescription());
        sb.append(newline);
        sb.append("------------------------------------------------------------------");
        sb.append(newline);
        for (int pos = 0; pos != target.getAttributeCount(); ++pos) {
            sb.append(target.getValues()[pos]);
            sb.append(newline);
            sb.append("------------------------------------------------------------------");
            sb.append(newline);
        }
        mAttributes.setText(sb.toString());
    }

    private void showObjects(String newText) {
        mCosemObjects.clear();
        List<String> rows = new ArrayList<String>();
        boolean addAll = newText.isEmpty();
        if (!addAll) {
            newText = newText.toLowerCase();
        }
        String newline = System.getProperty("line.separator");
        for (GXDLMSObject it : mDevice.getObjects()) {
            if (addAll || isAdded(it, newText)) {
                if (it.getDescription() == null) {
                    rows.add(it.getLogicalName());
                } else {
                    rows.add(it.getLogicalName() + newline + it.getDescription());
                }
                mCosemObjects.add(it);
            }
        }
        ArrayAdapter<String> objects = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, rows);
        mObjects.setAdapter(objects);
    }

    private static boolean isAdded(final GXDLMSObject it, final String newText) {
        return it.getObjectType().toString().toLowerCase().contains(newText) ||
                (it.getLogicalName() != null && it.getLogicalName().toLowerCase().contains(newText)) ||
                (it.getDescription() != null && it.getDescription().toLowerCase().contains(newText));
    }

    /**
     * Close connection to the meter.
     */
    private void close() throws Exception {
        IGXMedia media = mDevice.getMedia();
        if (media.isOpen()) {
            try {
                readDLMSPacket2(mClient.releaseRequest());
            }
            catch(Exception e) {
                //All meters don't support release. It's OK.
            }
            GXReplyData reply = new GXReplyData();
            readDLMSPacket(mClient.disconnectRequest(), reply);
            media.close();
        }
    }

    /*
     * Refresh COSEM objects.
     */
    public void refresh() throws Exception {
        // Get Association view from the meter.
        GXReplyData reply = new GXReplyData();
        readDataBlock(mClient.getObjectsRequest(), reply);
        mDevice.setObjects(mClient.parseObjects(reply.getData(), true));
        // Get description of the objects.
        GXDLMSConverter converter = new GXDLMSConverter();
        converter.updateOBISCodeInformation(getActivity(), mDevice.getObjects());
    }

    /*
     * Read Scalers and units from the register objects.
     */
    void readScalerAndUnits(final GXDLMSObjectCollection objects) {
        GXDLMSObjectCollection objs = objects.getObjects(new ObjectType[]{
                ObjectType.REGISTER, ObjectType.DEMAND_REGISTER,
                ObjectType.EXTENDED_REGISTER});

        try {
            List<Map.Entry<GXDLMSObject, Integer>> list =
                    new ArrayList<Map.Entry<GXDLMSObject, Integer>>();
            for (GXDLMSObject it : objs) {
                if (it instanceof GXDLMSRegister) {
                    list.add(new GXSimpleEntry<GXDLMSObject, Integer>(it, 3));
                }
                if (it instanceof GXDLMSDemandRegister) {
                    list.add(new GXSimpleEntry<GXDLMSObject, Integer>(it, 4));
                }
            }
            readList(list);
        } catch (Exception ex) {
            for (GXDLMSObject it : objs) {
                try {
                    if (it instanceof GXDLMSRegister) {
                        readObject(it, 3);
                    } else if (it instanceof GXDLMSDemandRegister) {
                        readObject(it, 4);
                    }
                } catch (Exception e) {
//                    traceLn(logFile, "Err! Failed to read scaler and unit value: " + e.getMessage());
                    // Continue reading.
                }
            }
        }
    }

    /*
     * /// Read list of attributes.
     */
    public void readList(List<Map.Entry<GXDLMSObject, Integer>> list)
            throws Exception {
        byte[][] data = mClient.readList(list);
        GXReplyData reply = new GXReplyData();
        readDataBlock(data, reply);
        mClient.updateValues(list, Arrays.asList(reply.getValue()));
    }

    /**
     * Reads profile generic using start and end time.
     *
     * @param pg Profile Generic to read.
     * @param start Start time.
     * @param end End time.
     * @throws Exception Occurred exception.
     */
    public void readRowsByRange(final GXDLMSProfileGeneric pg,
                                final java.util.Date start, final java.util.Date end) throws Exception {
        GXReplyData reply = new GXReplyData();
        byte[][] data = mClient.readRowsByRange(pg, start, end);
        readDataBlock(data, reply);
        mClient.updateValue(pg, 2, reply.getValue());
    }

    /**
     * Reads selected DLMS object with selected attribute index.
     *
     * @param item Object to read.
     * @param attributeIndex
     * @return Read value.
     * @throws Exception Occurred exception.
     */
    public Object readObject(GXDLMSObject item, int attributeIndex)
            throws Exception {
        byte[] data = mClient.read(item.getName(), item.getObjectType(),
                attributeIndex)[0];
        GXReplyData reply = new GXReplyData();

        readDataBlock(data, reply);
        // Update data type on read.
        if (item.getDataType(attributeIndex) == DataType.NONE) {
            item.setDataType(attributeIndex, reply.getValueType());
        }
        return mClient.updateValue(item, attributeIndex, reply.getValue());
    }


    /*
     * Read selected object.
     */
    public void read(GXDLMSObject obj) throws Exception {
        for (int it : ((IGXDLMSBase) obj).getAttributeIndexToRead(true)) {
            readObject(obj, it);
        }
    }

    public void readDLMSPacket(byte[][] data) throws Exception {
        GXReplyData reply = new GXReplyData();
        for (byte[] it : data) {
            reply.clear();
            readDLMSPacket(it, reply);
        }
    }

    String now() {
        return new SimpleDateFormat("HH:mm:ss.SSS")
                .format(java.util.Calendar.getInstance().getTime());
    }

    void writeTrace(final String line) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTrace.append(line + System.getProperty("line.separator"));
            }
        });
    }

    /*
     * Read DLMS Data from the device. If access is denied return null.
     */
    public void readDLMSPacket2(byte[][] data) throws Exception {
        GXReplyData reply = new GXReplyData();
        for (byte[] it : data) {
            reply.clear();
            readDLMSPacket(it, reply);
        }
    }

    /*
     * Read DLMS Data from the device. If access is denied return null.
     */
    public void readDLMSPacket(byte[] data, GXReplyData reply)
            throws Exception {
        if (data == null || data.length == 0) {
            return;
        }
        IGXMedia media = mDevice.getMedia();
        reply.setError((short) 0);
        Object eop = (byte) 0x7E;
        Integer pos = 0;
        boolean succeeded = false;
        ReceiveParameters<byte[]> p =
                new ReceiveParameters<byte[]>(byte[].class);
        p.setEop(eop);
        p.setCount(5);
        p.setWaitTime(mDevice.getWaitTime() * 1000);
        synchronized (media.getSynchronous()) {
            while (!succeeded) {
                writeTrace("<- " + now() + "\t" + GXCommon.bytesToHex(data));
                media.send(data, null);
                if (p.getEop() == null) {
                    p.setCount(1);
                }
                succeeded = media.receive(p);
                if (!succeeded) {
                    // Try to read again...
                    if (pos++ == 3) {
                        throw new RuntimeException(
                                "Failed to receive reply from the device in given time.");
                    }
                    System.out.println("Data send failed. Try to resend "
                            + pos.toString() + "/3");
                }
            }
            // Loop until whole DLMS packet is received.
            try {
                while (!mClient.getData(p.getReply(), reply)) {
                    if (p.getEop() == null) {
                        p.setCount(1);
                    }
                    if (!media.receive(p)) {
                        // If echo.
                        if (reply.isEcho()) {
                            media.send(data, null);
                        }
                        // Try to read again...
                        if (++pos == 3) {
                            throw new Exception(
                                    "Failed to receive reply from the device in given time.");
                        }
                        System.out.println("Data send failed. Try to resend "
                                + pos.toString() + "/3");
                    }
                }
            } catch (Exception e) {
                writeTrace("-> " + now() + "\t"
                        + GXCommon.bytesToHex(p.getReply()));
                throw e;
            }
        }
        writeTrace("-> " + now() + "\t" + GXCommon.bytesToHex(p.getReply()));
        if (reply.getError() != 0) {
            if (reply.getError() == ErrorCode.REJECTED.getValue()) {
                Thread.sleep(1000);
                readDLMSPacket(data, reply);
            } else {
                throw new GXDLMSException(reply.getError());
            }
        }
    }

    void readDataBlock(byte[][] data, GXReplyData reply) throws Exception {
        for (byte[] it : data) {
            reply.clear();
            readDataBlock(it, reply);
        }
    }

    /**
     * Reads next data block.
     *
     * @param data
     * @return
     * @throws Exception
     */
    void readDataBlock(byte[] data, GXReplyData reply) throws Exception {
        java.util.Set<RequestTypes> rt;
        if (data.length != 0) {
            readDLMSPacket(data, reply);
            while (reply.isMoreData()) {
                rt = reply.getMoreData();
                data = mClient.receiverReady(reply);
                readDLMSPacket(data, reply);
            }
        }
    }

    /*
     * Initializes connection.
     */
    private void initializeConnection() throws Exception, InterruptedException {
        IGXMedia media = mDevice.getMedia();
        media.open();
        if (media instanceof GXSerial) {
            GXSerial serial = (GXSerial) media;
            if (mDevice.getInterfaceType() == InterfaceType.HDLC_WITH_MODE_E) {
                ReceiveParameters<byte[]> p =
                        new ReceiveParameters<byte[]>(byte[].class);
                p.setAllData(false);
                p.setEop((byte) '\n');
                p.setWaitTime(mDevice.getWaitTime() * 1000);
                String data;
                String replyStr;
                synchronized (media.getSynchronous()) {
                    data = "/?!\r\n";
                    writeTrace("<- " + now() + "\t"
                            + GXCommon.bytesToHex(data.getBytes("ASCII")));
                    media.send(data, null);
                    if (!media.receive(p)) {
                        throw new Exception("Invalid meter type.");
                    }
                    writeTrace("->" + now() + "\t"
                            + GXCommon.bytesToHex(p.getReply()));
                    // If echo is used.
                    replyStr = new String(p.getReply());
                    if (data.equals(replyStr)) {
                        p.setReply(null);
                        if (!media.receive(p)) {
                            throw new Exception("Invalid meter type.");
                        }
                        writeTrace("-> " + now() + "\t"
                                + GXCommon.bytesToHex(p.getReply()));
                        replyStr = new String(p.getReply());
                    }
                }
                if (replyStr.length() == 0 || replyStr.charAt(0) != '/') {
                    throw new Exception("Invalid responce.");
                }
                String manufactureID = replyStr.substring(1, 4);
                if (mDevice.getManufacturer().compareToIgnoreCase(manufactureID) != 0) {
                    throw new Exception("Manufacturer "
                            + mDevice.getManufacturer()
                            + " expected but " + manufactureID + " found.");
                }
                int bitrate = 0;
                char baudrate = replyStr.charAt(4);
                switch (baudrate) {
                    case '0':
                        bitrate = 300;
                        break;
                    case '1':
                        bitrate = 600;
                        break;
                    case '2':
                        bitrate = 1200;
                        break;
                    case '3':
                        bitrate = 2400;
                        break;
                    case '4':
                        bitrate = 4800;
                        break;
                    case '5':
                        bitrate = 9600;
                        break;
                    case '6':
                        bitrate = 19200;
                        break;
                    default:
                        throw new Exception("Unknown baud rate.");
                }
                // Send ACK
                // Send Protocol control character
                byte controlCharacter = (byte) '2';// "2" HDLC protocol
                // procedure (Mode E)
                // Send Baudrate character
                // Mode control character
                byte ModeControlCharacter = (byte) '2';// "2" //(HDLC protocol
                // procedure) (Binary
                // mode)
                // Set mode E.
                byte[] tmp = new byte[]{0x06, controlCharacter,
                        (byte) baudrate, ModeControlCharacter, 13, 10};
                p.setReply(null);
                synchronized (media.getSynchronous()) {
                    media.send(tmp, null);
                    writeTrace("<- " + now() + "\t" + GXCommon.bytesToHex(tmp));
                    p.setWaitTime(1000);
                    if (media.receive(p)) {
                        writeTrace("-> " + now() + "\t"
                                + GXCommon.bytesToHex(p.getReply()));
                    }
                    media.close();
                    serial.setDataBits(8);
                    serial.setParity(Parity.NONE);
                    serial.setStopBits(StopBits.ONE);
                    serial.setBaudRate(BaudRate.forValue(bitrate));
                    media.open();
                    // This sleep make sure that all meters can be read.
                    Thread.sleep(500);
                }
            }
        }
        GXReplyData reply = new GXReplyData();
        byte[] data = mClient.snrmRequest();
        if (data.length != 0) {
            readDLMSPacket(data, reply);
            // Is client accepted.
            mClient.parseUAResponse(reply.getData());
        }
        reply.clear();
        // Generate AARQ request.
        // Split requests to multiple packets if needed.
        // If password is used all data might not fit to one packet.
        for (byte[] it : mClient.aarqRequest()) {
            readDLMSPacket(it, reply);
        }
        // Parse reply.
        mClient.parseAareResponse(reply.getData());
        reply.clear();
        // Get challenge Is HLS authentication is used.
        if (mClient.getIsAuthenticationRequired()) {
            for (byte[] it : mClient.getApplicationAssociationRequest()) {
                readDLMSPacket(it, reply);
            }
            mClient.parseApplicationAssociationResponse(reply.getData());
        }
    }

    @Override
    public void onError(Object sender, RuntimeException ex) {
        GXGeneral.showError(getActivity(), ex, getString(R.string.error));
    }

    @Override
    public void onReceived(Object sender, ReceiveEventArgs e) {

    }

    private void enableUI(boolean open) {
        if (open) {
            mOpen.setText(R.string.close);
        } else {
            mOpen.setText(R.string.open);
        }
        mRead.setEnabled(open);
        mRefresh.setEnabled(open);
    }

    @Override
    public void onMediaStateChange(final Object sender, final MediaStateEventArgs e) {
        try {
            if (e.getState() == MediaState.OPEN ||
                    e.getState() == MediaState.CLOSED) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enableUI(e.getState() == MediaState.OPEN);
                    }
                });
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTrace(Object sender, TraceEventArgs e) {

    }

    @Override
    public void onPropertyChanged(Object sender, PropertyChangedEventArgs e) {

    }

    @Override
    public void onStop() {
        try {
            close();
        }
        catch (Exception e)
        {
            GXGeneral.showError(getActivity(), e, getString(R.string.error));
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            close();
        }
        catch (Exception e)
        {
            GXGeneral.showError(getActivity(), e, getString(R.string.error));
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}