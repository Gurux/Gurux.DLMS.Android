package gurux.dlms.android.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import gurux.dlms.GXByteBuffer;
import gurux.dlms.GXDLMSClient;
import gurux.dlms.GXDLMSConverter;
import gurux.dlms.GXDLMSException;
import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.GXReplyData;
import gurux.dlms.GXSimpleEntry;
import gurux.dlms.android.GXDevice;
import gurux.dlms.android.GXGeneral;
import gurux.dlms.android.IGXSettingsChangedListener;
import gurux.dlms.android.R;
import gurux.dlms.android.databinding.FragmentMainBinding;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.ErrorCode;
import gurux.dlms.enums.InterfaceType;
import gurux.dlms.enums.ObjectType;
import gurux.dlms.enums.RequestTypes;
import gurux.dlms.enums.Security;
import gurux.dlms.objects.GXDLMSData;
import gurux.dlms.objects.GXDLMSDemandRegister;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSObjectCollection;
import gurux.dlms.objects.GXDLMSProfileGeneric;
import gurux.dlms.objects.GXDLMSRegister;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.secure.GXDLMSSecureClient;
import gurux.dlms.ui.BaseObjectFragment;
import gurux.dlms.ui.GXDLMSUi;
import gurux.dlms.ui.IGXActionListener;
import gurux.dlms.ui.IGXResultHandler;
import gurux.dlms.ui.ObjectViewModel;
import gurux.io.BaudRate;
import gurux.io.Parity;
import gurux.io.StopBits;
import gurux.serial.GXSerial;

public class MainFragment extends Fragment implements IGXMediaListener, IGXActionListener {

    GXDevice mDevice;
    GXDLMSSecureClient mClient;
    List<GXDLMSObject> mCosemObjects = new ArrayList<>();
    private IGXSettingsChangedListener mListener;
    private FragmentMainBinding binding;

    private ObjectViewModel mObjectViewModel;


    private static boolean isAdded(final GXDLMSObject it, final String newText) {
        return it.getObjectType().toString().toLowerCase().contains(newText) ||
                (it.getLogicalName() != null && it.getLogicalName().toLowerCase().contains(newText)) ||
                (it.getDescription() != null && it.getDescription().toLowerCase().contains(newText));
    }

    private void readAssociationView() {
        //Clear trace.
        binding.trace.setText("");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> handler.post(() -> {
            try {
                refresh();
                showObjects("");
                //Notify activity that association has read again.
                mListener.onAssociationChanged();
                Toast.makeText(getActivity(), "Refresh done.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                GXGeneral.showError(getActivity(), e, getString(R.string.error));
            }
        }));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.setListener(this);
        mObjectViewModel = new ViewModelProvider(this).get(ObjectViewModel.class);
        mDevice = mainViewModel.getDevice();
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mObjectViewModel.getInProgress().observe(getViewLifecycleOwner(), e ->
        {
            //Read, open and refresh are disabled when transaction is on progress,
            binding.open.setEnabled(!e);
            binding.read.setEnabled(!e);
            binding.refresh.setEnabled(!e);
        });

        try {
            binding.trace.setVisibility(View.GONE);
            int serverAddress = GXDLMSClient.getServerAddress(mDevice.getLogicalAddress(),
                    mDevice.getPhysicalAddress());
            mClient = new GXDLMSSecureClient(true, mDevice.getClientAddress(), serverAddress,
                    mDevice.getAuthentication().getType(), mDevice.getPassword(), mDevice.getInterfaceType());

            mClient.getCiphering().setSecurity(mDevice.getSecurity());
            mClient.getCiphering().setSecuritySuite(mDevice.getSecuritySuite());
            mClient.getCiphering().setSystemTitle(mDevice.getSystemTitle());
            mClient.getCiphering().setBlockCipherKey(mDevice.getBlockCipherKey());
            mClient.getCiphering().setAuthenticationKey(mDevice.getAuthenticationKey());
            mClient.getCiphering().setDedicatedKey(mDevice.getDedicatedKey());
            mClient.setCtoSChallenge(mDevice.getChallenge());
            mClient.setServerSystemTitle(mDevice.getMeterSystemTitle());

            binding.showTrace.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            binding.trace.setVisibility(View.VISIBLE);
                            binding.attributes.setVisibility(View.GONE);
                        } else {
                            binding.trace.setVisibility(View.GONE);
                            binding.attributes.setVisibility(View.VISIBLE);
                        }
                    }
            );
            binding.open.setOnClickListener(v -> {
                try {
                    //Clear trace.
                    binding.trace.setText("");
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
                        binding.trace.setText("Connecting " + media.getName());
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executor.execute(() -> handler.post(() -> {
                            try {
                                initializeConnection();
                                Toast.makeText(getActivity(), "Connected.", Toast.LENGTH_SHORT).show();
                                if (mDevice.getObjects().isEmpty()) {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("Import association view")
                                            .setMessage(R.string.readAssociationView)
                                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                                readAssociationView();
                                            })
                                            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                                            })
                                            .show();
                                }
                            } catch (Exception e) {
                                GXGeneral.showError(getActivity(), e, getString(R.string.error));
                            }
                        }));
                    }
                } catch (Exception e) {
                    GXGeneral.showError(getActivity(), e, getString(R.string.error));
                }
            });
            binding.read.setOnClickListener(v -> {
                try {
                    //Clear trace.
                    binding.trace.setText("");
                    //Read selected item.
                    int pos = binding.objects.getCheckedItemPosition();
                    if (pos != -1) {
                        GXDLMSObject obj = mCosemObjects.get(pos);
                        onRead(obj, 0);
                    }
                } catch (Exception e) {
                    GXGeneral.showError(getActivity(), e, getString(R.string.error));
                }
            });
            binding.write.setOnClickListener(v -> {
                try {
                    //Clear trace.
                    binding.trace.setText("");
                    //Write changed attributes.
                    int pos = binding.objects.getCheckedItemPosition();
                    if (pos != -1) {
                        GXDLMSObject obj = mCosemObjects.get(pos);
                        onWrite(obj, 0);
                    }
                } catch (Exception e) {
                    GXGeneral.showError(getActivity(), e, getString(R.string.error));
                }
            });
            binding.refresh.setOnClickListener(v -> {
                readAssociationView();
            });

            binding.objects.setOnItemClickListener((parent, view1, position, id) -> {
                try {
                    showObject(mCosemObjects.get(position));
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
                    binding.open.setEnabled(false);
                }
                if (mDevice.getMedia() instanceof GXSerial && ((GXSerial) mDevice.getMedia()).getPort() == null) {
                    Toast.makeText(getActivity(), R.string.invalidSerialPort, Toast.LENGTH_SHORT).show();
                    binding.open.setEnabled(false);
                }
            }

            binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    try {
                        showObjects(newText);
                        ((BaseAdapter) binding.objects.getAdapter()).notifyDataSetChanged();
                    } catch (Exception e) {
                        GXGeneral.showError(getActivity(), e, getString(R.string.error));
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        showObjects("");
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IGXSettingsChangedListener) {
            mListener = (IGXSettingsChangedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IGXMediaChangedListener");
        }
    }

    /**
     * Show selected object
     *
     * @param value Selected COSEM object.
     */
    private void showObject(final GXDLMSObject value) {
        BaseObjectFragment childFragment = GXDLMSUi.newInstance(getActivity(),
                this, mClient, mDevice.getMedia(), value);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.attributes, childFragment).commit();

    }

    private void showObjects(String newText) {
        mCosemObjects.clear();
        List<String> rows = new ArrayList<String>();
        boolean addAll = newText.isEmpty();
        if (!addAll) {
            newText = newText.toLowerCase();
        }
        String newline = System.lineSeparator();
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
        binding.objects.setAdapter(objects);
    }


    void disconnect() throws Exception {
        IGXMedia media = mDevice.getMedia();
        if (media != null && media.isOpen() && !mClient.isPreEstablishedConnection()) {
            GXReplyData reply = new GXReplyData();
            readDLMSPacket(mClient.disconnectRequest(), reply);
        }
    }

    void release() throws Exception {
        IGXMedia media = mDevice.getMedia();
        if (media != null && media.isOpen()) {
            GXReplyData reply = new GXReplyData();
            try {
                // Release is call only for secured connections.
                // All meters are not supporting Release and it's causing
                // problems.
                if (mClient.getInterfaceType() == InterfaceType.WRAPPER || (mClient.getInterfaceType() == InterfaceType.HDLC
                        && mClient.getCiphering().getSecurity() != Security.NONE)) {
                    readDataBlock(mClient.releaseRequest(), reply);
                }
            } catch (Exception e) {
                // All meters don't support release.
            }
        }
    }

    /**
     * Close connection to the meter.
     */
    private void close() throws Exception {
        IGXMedia media = mDevice.getMedia();
        GXReplyData reply = new GXReplyData();
        if (media.isOpen()) {
            try {
                // Release is call only for secured connections.
                // All meters are not supporting Release and it's causing
                // problems.
                if (mClient.getInterfaceType() == InterfaceType.WRAPPER || (mClient.getInterfaceType() == InterfaceType.HDLC
                        && mClient.getCiphering().getSecurity() != Security.NONE)) {
                    readDataBlock(mClient.releaseRequest(), reply);
                }
            } catch (Exception e) {
                //All meters don't support release. It's OK.
            }
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
        readScalerAndUnits(mDevice.getObjects());
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
     * Reads selected DLMS object with selected attribute index.
     *
     * @param item           Object to read.
     * @param attributeIndex
     * @return Read value.
     * @throws Exception Occurred exception.
     */
    public Object readObject(
            GXDLMSObject item,
            int attributeIndex)
            throws Exception {
        try {
            binding.read.setEnabled(false);
            binding.refresh.setEnabled(false);
            byte[][] data = mClient.read(item, attributeIndex);
            GXReplyData reply = new GXReplyData();
            readDataBlock(data, reply);
            // Update data type on read.
            if (item.getDataType(attributeIndex) == DataType.NONE) {
                item.setDataType(attributeIndex, reply.getValueType());
            }
            return mClient.updateValue(item, attributeIndex, reply.getValue());
        } finally {
            binding.read.setEnabled(true);
            binding.refresh.setEnabled(true);
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
                binding.trace.append(line + System.lineSeparator());
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
        GXReplyData notify = new GXReplyData();
        IGXMedia media = mDevice.getMedia();
        reply.setError((short) 0);
        Object eop = (byte) 0x7E;
        //In network connection terminator is not used.
        if (mClient.getInterfaceType() != InterfaceType.HDLC &&
                mClient.getInterfaceType() != InterfaceType.HDLC_WITH_MODE_E) {
            eop = null;
        }
        GXByteBuffer rd = new GXByteBuffer();
        int pos = 0;
        boolean succeeded = false;
        ReceiveParameters<byte[]> p =
                new ReceiveParameters<byte[]>(byte[].class);
        p.setEop(eop);
        p.setCount(mClient.getFrameSize(rd));
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
                    System.out.println("Data send failed. Try to resend " + pos + "/3");
                }
            }
            // Loop until whole DLMS packet is received.
            rd = new GXByteBuffer(p.getReply());
            int msgPos = 0;
            try {
                while (!mClient.getData(rd, reply, notify)) {
                    p.setReply(null);
                    if (notify.getData().getData() != null) {
                        // Handle notify.
                        if (!notify.isMoreData()) {
                            // Show received push message as XML.
                            GXDLMSTranslator t = new GXDLMSTranslator();
                            String xml = t.dataToXml(notify.getData());
                            System.out.println(xml);
                            notify.clear();
                            msgPos = rd.position();
                        }
                        continue;
                    }

                    if (p.getEop() == null) {
                        p.setCount(mClient.getFrameSize(rd));
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
                        System.out.println("Data send failed. Try to resend " + pos + "/3");
                    }
                    rd.position(msgPos);
                    rd.set(p.getReply());
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
     * @param data  Send data.
     * @param reply Reply data.
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
     * Read Invocation counter (frame counter) from the meter and update it.
     */
    private void updateFrameCounter(IGXMedia media) throws Exception {
        // Read frame counter if GeneralProtection is used.
        if (mDevice.getInvocationCounter() != null && mClient.getCiphering() != null
                && mClient.getCiphering().getSecurity() != Security.NONE) {
            // Media settings are saved and they are restored when HDLC with
            // mode E is used.
            String mediaSettings = media.getSettings();
            initializeOpticalHead(media);
            byte[] data;
            GXReplyData reply = new GXReplyData();
            reply.clear();
            int add = mClient.getClientAddress();
            int serverAdd = mClient.getServerAddress();
            byte[] serverSt = mClient.getServerSystemTitle();
            Authentication auth = mClient.getAuthentication();
            Security security = mClient.getCiphering().getSecurity();
            byte[] challenge = mClient.getCtoSChallenge();
            try {
                mClient.setServerSystemTitle(null);
                mClient.setClientAddress(16);
                mClient.setAuthentication(Authentication.NONE);
                mClient.getCiphering().setSecurity(Security.NONE);
                data = mClient.snrmRequest();
                if (data.length != 0) {
                    readDLMSPacket(data, reply);
                    // Has server accepted client.
                    mClient.parseUAResponse(reply.getData());
                }
                // Generate AARQ request.
                // Split requests to multiple packets if needed.
                // If password is used all data might not fit to one packet.
                try {
                    if (!mClient.isPreEstablishedConnection()) {
                        reply.clear();
                        readDataBlock(mClient.aarqRequest(), reply);
                        // Parse reply.
                        mClient.parseAareResponse(reply.getData());
                    }
                    reply.clear();
                    GXDLMSData d = new GXDLMSData(mDevice.getInvocationCounter());
                    readObject(d, 2);
                    long iv = ((Number) d.getValue()).longValue();
                    iv += 1;
                    mClient.getCiphering().setInvocationCounter(iv);
                    writeTrace("Invocation counter: " + String.valueOf(iv));
                    reply.clear();
                    disconnect();
                    // Reset media settings back to default.
                    if (mClient.getInterfaceType() == InterfaceType.HDLC_WITH_MODE_E) {
                        media.close();
                        media.setSettings(mediaSettings);
                    }
                } catch (Exception Ex) {
                    disconnect();
                    throw Ex;
                }
            } finally {
                mClient.setServerSystemTitle(serverSt);
                mClient.setClientAddress(add);
                mClient.setServerAddress(serverAdd);
                mClient.setAuthentication(auth);
                mClient.getCiphering().setSecurity(security);
                mClient.setCtoSChallenge(challenge);
            }
        }
    }

    void initializeOpticalHead(IGXMedia media) throws Exception {
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
    }

    /*
     * Initializes connection.
     */
    private void initializeConnection() throws Exception {
        IGXMedia media = mDevice.getMedia();
        media.open();
        updateFrameCounter(media);
        initializeOpticalHead(media);
        GXReplyData reply = new GXReplyData();
        byte[] data = mClient.snrmRequest();
        if (data.length != 0) {
            readDLMSPacket(data, reply);
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
        if (mClient.getAuthentication().getValue() > Authentication.LOW.getValue()) {
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
            binding.open.setText(R.string.close);
        } else {
            binding.open.setText(R.string.open);
        }
        binding.read.setEnabled(open);
        boolean dirty = open;
        if (open) {
            int pos = binding.objects.getCheckedItemPosition();
            if (pos != -1) {
                GXDLMSObject obj = mCosemObjects.get(pos);
                dirty = obj.isDirty(0);
            }
        }
        binding.write.setEnabled(dirty);
        binding.refresh.setEnabled(open);
    }

    @Override
    public void onMediaStateChange(final Object sender, final MediaStateEventArgs e) {
        try {
            if (e.getState() == MediaState.OPEN ||
                    e.getState() == MediaState.CLOSED) {
                requireActivity().runOnUiThread(() -> enableUI(e.getState() == MediaState.OPEN));
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
        } catch (Exception e) {
            GXGeneral.showError(getActivity(), e, getString(R.string.error));
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            close();
        } catch (Exception e) {
            GXGeneral.showError(getActivity(), e, getString(R.string.error));
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRead(GXDLMSObject object, int index) {
        if (object == null) {
            //Read selected item.
            int pos = binding.objects.getCheckedItemPosition();
            if (pos != -1) {
                onRead(mCosemObjects.get(pos), 0);
            }
            return;
        }

        binding.write.setEnabled(false);
        mObjectViewModel.setInProgress(true);
        Toast.makeText(getActivity(), object + " read started.", Toast.LENGTH_SHORT).show();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> handler.post(() -> {
            try {
                if (index == 0) {
                    //Read all.
                    for (int pos : ((IGXDLMSBase) object).getAttributeIndexToRead(false)) {
                        if (object instanceof GXDLMSProfileGeneric && pos == 2) {
                            //Profile generic buffer is read separately.
                            continue;
                        }
                        if (mClient.canRead(object, pos)) {
                            readObject(object, pos);
                            object.setDirty(pos, false);
                        }
                        showObject(object);
                    }
                } else {
                    if (mClient.canRead(object, index)) {
                        readObject(object, index);
                        object.setDirty(index, false);
                    }
                    showObject(object);
                }
                Toast.makeText(getActivity(), object + " read completed.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                GXGeneral.showError(getActivity(), e, getString(R.string.error));
            }
            mObjectViewModel.setInProgress(false);
        }));
    }

    @Override
    public void onWrite(GXDLMSObject object, int index) {
        binding.write.setEnabled(false);
        mObjectViewModel.setInProgress(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> handler.post(() -> {
            try {
                Toast.makeText(getActivity(), object + " write started.", Toast.LENGTH_SHORT).show();
                if (index == 0) {
                    //Write changed attributes.
                    for (int pos = 1; pos <= object.getAttributeCount(); ++pos) {
                        if (object.isDirty(pos) && mClient.canWrite(object, pos)) {
                            byte[][] data = mClient.write(object, pos);
                            GXReplyData reply = new GXReplyData();
                            readDataBlock(data, reply);
                            object.setDirty(pos, false);
                        }
                        showObject(object);
                    }
                } else {
                    if (mClient.canWrite(object, index)) {
                        byte[][] data = mClient.write(object, index);
                        GXReplyData reply = new GXReplyData();
                        readDataBlock(data, reply);
                        object.setDirty(index, false);

                    }
                }
                Toast.makeText(getActivity(), object + " write completed.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                GXGeneral.showError(getActivity(), e, getString(R.string.error));
            }
            mObjectViewModel.setInProgress(false);
        }));
    }

    @Override
    public void onInvoke(byte[][] frames, final IGXResultHandler handler) {
        mObjectViewModel.setInProgress(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler h = new Handler(Looper.getMainLooper());
        executor.execute(() -> h.post(() -> {
            try {
                GXReplyData reply = new GXReplyData();
                readDataBlock(frames, reply);
                if (handler != null) {
                    handler.run(reply.getValue());
                }
                Toast.makeText(getActivity(), "Action completed.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                GXGeneral.showError(getActivity(), e, getString(R.string.error));
            }
            mObjectViewModel.setInProgress(false);
        }));
    }

    @Override
    public void onRaw(byte[][] frames, final IGXResultHandler handler) {
        mObjectViewModel.setInProgress(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler h = new Handler(Looper.getMainLooper());
        executor.execute(() -> h.post(() -> {
            try {
                GXReplyData reply = new GXReplyData();
                readDataBlock(frames, reply);
                if (handler != null) {
                    handler.run(reply.getValue());
                }
            } catch (Exception e) {
                GXGeneral.showError(getActivity(), e, getString(R.string.error));
            }
            mObjectViewModel.setInProgress(false);
        }));
    }

    /**
     * Write is enabled only when attribute is dirty.
     */
    @Override
    public void onObjectChanged(GXDLMSObject target, int index) {
        binding.write.setEnabled(true);
    }

    @Override
    public boolean isProgress() {
        return Boolean.TRUE.equals(mObjectViewModel.getInProgress().getValue());
    }
}