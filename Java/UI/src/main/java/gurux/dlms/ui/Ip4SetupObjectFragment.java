package gurux.dlms.ui;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.net.InetAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gurux.dlms.GXDLMSTranslator;
import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.MethodAccessMode;
import gurux.dlms.objects.GXDLMSIp4Setup;
import gurux.dlms.objects.GXDLMSIp4SetupIpOption;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.ui.databinding.Ip4SetupFragmentBinding;
import gurux.dlms.ui.internal.GXAttributeView;

public class Ip4SetupObjectFragment extends BaseObjectFragment {

    private Ip4SetupFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        final GXDLMSIp4Setup target = objectViewModel.getObject(GXDLMSIp4Setup.class);
        mMedia = objectViewModel.getMedia();
        binding = Ip4SetupFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        AccessMode am;
        TextView lbl;
        View editText;
        //Add Data link layer reference.
        lbl = new TextView(requireContext());
        lbl.setText(names[1]);
        binding.attributes.addView(lbl);
        am = target.getAccess(2);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 2, DataType.OCTET_STRING,
                lbl,
                (object, index) -> target.getDataLinkLayerReference(),
                (object, index, value) ->
                {
                    target.setDataLinkLayerReference(((GXDLMSObject) value).getLogicalName());
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add IP address.
        lbl = new TextView(requireContext());
        lbl.setText(names[2]);
        binding.attributes.addView(lbl);
        am = target.getAccess(3);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 3, DataType.UINT32,
                lbl,
                (object, index) -> target.getIPAddress(),
                (object, index, value) ->
                {
                    target.setIPAddress(InetAddress.getByName((String) value));
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Multicast ip address.
        lbl = new TextView(requireContext());
        lbl.setText(names[3]);
        binding.attributes.addView(lbl);
        am = target.getAccess(4);

        GXTable table = new GXTable(binding.attributes.getContext(), new String[]{requireContext().getString(R.string.ipAddress)});
        if (target.getMulticastIPAddress() != null) {
            for (InetAddress it : target.getMulticastIPAddress()) {
                table.addRow(binding.attributes.getContext(),
                        new String[]{String.valueOf(it)});
            }
        }
        mComponents.add(new GXSimpleEntry<>(table, am));
        binding.attributes.addView(table);
        //Add IP options.
        lbl = new TextView(requireContext());
        lbl.setText(names[4]);
        binding.attributes.addView(lbl);
        am = target.getAccess(5);
        table = new GXTable(binding.attributes.getContext(), new String[]{"Type", "Length", "Data"});
        if (target.getIPOptions() != null) {
            for (GXDLMSIp4SetupIpOption it : target.getIPOptions()) {
                table.addRow(binding.attributes.getContext(),
                        new String[]{String.valueOf(it.getType()),
                                String.valueOf(it.getLength()),
                                GXDLMSTranslator.toHex(it.getData())});
            }
        }
        mComponents.add(new GXSimpleEntry<>(table, am));
        binding.attributes.addView(table);
        //Add Subnet mask.
        lbl = new TextView(requireContext());
        lbl.setText(names[5]);
        binding.attributes.addView(lbl);
        am = target.getAccess(6);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 6, DataType.UINT32,
                lbl,
                (object, index) -> target.getSubnetMask(),
                (object, index, value) ->
                {
                    target.setSubnetMask((String) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Gateway ip address.
        lbl = new TextView(requireContext());
        lbl.setText(names[6]);
        binding.attributes.addView(lbl);
        am = target.getAccess(7);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 7, DataType.UINT32,
                lbl,
                (object, index) -> target.getGatewayIPAddress(),
                (object, index, value) ->
                {
                    target.setGatewayIPAddress(InetAddress.getByName((String) value));
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Use dhcp.
        lbl = new TextView(requireContext());
        lbl.setText(names[7]);
        binding.attributes.addView(lbl);
        am = target.getAccess(8);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 8, DataType.BOOLEAN,
                lbl,
                (object, index) -> target.getUseDHCP(),
                (object, index, value) ->
                {
                    target.setUseDHCP((boolean) value);
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Primary dns address.
        lbl = new TextView(requireContext());
        lbl.setText(names[8]);
        binding.attributes.addView(lbl);
        am = target.getAccess(9);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 9, DataType.UINT32,
                lbl,
                (object, index) -> target.getPrimaryDNSAddress(),
                (object, index, value) ->
                {
                    target.setPrimaryDNSAddress(InetAddress.getByName((String) value));
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        //Add Secondary dns address.
        lbl = new TextView(requireContext());
        lbl.setText(names[9]);
        binding.attributes.addView(lbl);
        am = target.getAccess(10);
        editText = GXAttributeView.create(requireContext(), objectViewModel.getListener(),
                target, 10, DataType.UINT32,
                lbl,
                (object, index) -> target.getSecondaryDNSAddress(),
                (object, index, value) ->
                {
                    target.setSecondaryDNSAddress(InetAddress.getByName((String) value));
                });
        mComponents.add(new GXSimpleEntry<>(editText, am));
        binding.attributes.addView(editText);
        ////////////////////////////////////////
        //Methods
        MethodAccessMode ma;
        //Add mc ip address
        ma = target.getMethodAccess(1);
        binding.addMcIpAddress.setText(((IGXDLMSBase) target).getMethodNames(getContext())[0]);
        mComponents.add(new GXSimpleEntry<>(binding.addMcIpAddress, ma));
        binding.addMcIpAddress.setOnClickListener(v -> {
            binding.addMcIpAddress.setEnabled(false);
            try {
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(50, 40, 50, 10);

                TextView text = new TextView(getContext());
                text.setMovementMethod(LinkMovementMethod.getInstance());
                text.setText(R.string.ipAddress);
                layout.addView(text);

                final EditText ipAddress = new EditText(getContext());
                layout.addView(ipAddress);
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle(R.string.shift_time)
                        .setView(layout)
                        .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        {
                            try {
                                InetAddress address = InetAddress.getByName(ipAddress.getText().toString());
                                objectViewModel.getListener().onInvoke(target.addMcIpAddress(objectViewModel.getClient(), address), null);
                                dialog.dismiss();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel())
                        .show();
            } catch (Exception e) {
                binding.addMcIpAddress.setEnabled(true);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Delete mc ip address
        ma = target.getMethodAccess(2);
        binding.deleteMcIpAddress.setText(((IGXDLMSBase) target).getMethodNames(getContext())[1]);
        mComponents.add(new GXSimpleEntry<>(binding.deleteMcIpAddress, ma));
        binding.deleteMcIpAddress.setOnClickListener(v -> {
            try {
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(50, 40, 50, 10);

                TextView text = new TextView(getContext());
                text.setMovementMethod(LinkMovementMethod.getInstance());
                text.setText(R.string.ipAddress);
                layout.addView(text);

                final EditText ipAddress = new EditText(getContext());
                layout.addView(ipAddress);
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle(R.string.shift_time)
                        .setView(layout)
                        .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        {
                            try {
                                InetAddress address = InetAddress.getByName((String) ipAddress.getText().toString());
                                objectViewModel.getListener().onInvoke(target.deleteMcIpAaddress(objectViewModel.getClient(), address), null);
                                dialog.dismiss();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel())
                        .show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Get count mc ip addresses
        ma = target.getMethodAccess(3);
        binding.countMcIpAddress.setText(((IGXDLMSBase) target).getMethodNames(getContext())[2]);
        mComponents.add(new GXSimpleEntry<>(binding.countMcIpAddress, ma));
        binding.countMcIpAddress.setOnClickListener(v -> {
            try {
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(50, 40, 50, 10);

                TextView text = new TextView(getContext());
                text.setMovementMethod(LinkMovementMethod.getInstance());
                text.setText("Index");
                layout.addView(text);

                final EditText indexTb = new EditText(getContext());
                layout.addView(indexTb);
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle(R.string.shift_time)
                        .setView(layout)
                        .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        {
                            try {
                                short index = Short.parseShort(indexTb.getText().toString());
                                objectViewModel.getListener().onInvoke(target.getMcIpAddressCount(objectViewModel.getClient(), index), null);
                                dialog.dismiss();
                            } catch (InvalidKeyException |
                                     NoSuchAlgorithmException | NoSuchPaddingException |
                                     InvalidAlgorithmParameterException |
                                     IllegalBlockSizeException | BadPaddingException |
                                     SignatureException e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, (d, which) -> d.cancel())
                        .show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mMedia.addListener(this);
        updateAccessRights();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}