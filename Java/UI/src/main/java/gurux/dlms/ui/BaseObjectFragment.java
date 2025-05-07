package gurux.dlms.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Map;

import gurux.common.IGXMedia;
import gurux.common.IGXMediaListener;
import gurux.common.MediaStateEventArgs;
import gurux.common.PropertyChangedEventArgs;
import gurux.common.ReceiveEventArgs;
import gurux.common.TraceEventArgs;
import gurux.common.enums.MediaState;
import gurux.dlms.GXDLMSClient;
import gurux.dlms.GXSimpleEntry;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.AccessMode3;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.MethodAccessMode;
import gurux.dlms.enums.MethodAccessMode3;
import gurux.dlms.objects.GXDLMSAssociationLogicalName;
import gurux.dlms.objects.GXDLMSAssociationShortName;
import gurux.dlms.objects.GXDLMSClock;
import gurux.dlms.objects.GXDLMSData;
import gurux.dlms.objects.GXDLMSGprsSetup;
import gurux.dlms.objects.GXDLMSHdlcSetup;
import gurux.dlms.objects.GXDLMSIECLocalPortSetup;
import gurux.dlms.objects.GXDLMSIp4Setup;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSProfileGeneric;
import gurux.dlms.objects.GXDLMSPushSetup;
import gurux.dlms.objects.GXDLMSRegister;
import gurux.dlms.objects.GXDLMSTcpUdpSetup;
import gurux.dlms.objects.IGXDLMSBase;
import gurux.dlms.ui.databinding.ObjectBaseBinding;
import gurux.dlms.ui.internal.GXAttributeView;

public class BaseObjectFragment extends Fragment implements IGXMediaListener {

    protected ArrayList<Map.Entry<View, Object>> mComponents = new ArrayList<>();
    protected ObjectBaseBinding binding;
    protected IGXMedia mMedia;

    static boolean CanWrite(final AccessMode access) {
        return (access.getValue() & AccessMode.WRITE.getValue()) != 0;
    }

    static boolean CanWrite(final AccessMode3 access) {
        return (access.getValue() & AccessMode3.WRITE.getValue()) != 0;
    }

    static boolean CanInvoke(final MethodAccessMode access) {
        return (access.getValue() & MethodAccessMode.ACCESS.getValue()) != 0;
    }

    static boolean CanInvoke(final MethodAccessMode3 access) {
        return (access.getValue() & MethodAccessMode3.ACCESS.getValue()) != 0;
    }

    protected String getLabel(String[] values, GXDLMSObject target, int index) {
        if (target.isDirty(index)) {
            //Add star to indicate that the user has change the value.
            return values[index - 1] + "\uD83C\uDF1F";
        }
        return values[index - 1];
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        objectViewModel.getInProgress().observe(getViewLifecycleOwner(), e1 ->
        {
            MediaStateEventArgs e = new MediaStateEventArgs();
            e.setState(!e1 && mMedia.isOpen() ? MediaState.OPEN : MediaState.CLOSED);
            onMediaStateChange(mMedia, e);
        });
        final GXDLMSObject target = objectViewModel.getObject(GXDLMSObject.class);
        mMedia = objectViewModel.getMedia();
        binding = ObjectBaseBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        String[] names = ((IGXDLMSBase) target).getNames(getContext());
        DataType dt;
        for (int pos = 2; pos <= target.getAttributeCount(); ++pos) {
            TextView lbl = new TextView(requireContext());
            if (pos > names.length) {
                lbl.setText(R.string.unknown);
            } else {
                lbl.setText(names[pos - 1]);
            }
            binding.attributes.addView(lbl);
            Object value = target.getValues()[pos - 1];
            dt = target.getUIDataType(pos);
            if (dt == DataType.NONE) {
                dt = target.getDataType(pos);
            }
            AccessMode am = target.getAccess(pos);
            View editText = GXAttributeView.create(requireContext(), value, dt);
            mComponents.add(new GXSimpleEntry<>(editText, am));
            binding.attributes.addView(editText);
        }
        mMedia.addListener(this);
        updateAccessRights();
        return view;
    }

    /**
     * Has user modified the value.
     *
     * @param target COSEM oject.
     * @param index  Attribute index.
     * @return True, if user has modified the value.
     */
    protected boolean isDirty(GXDLMSObject target, int index) {
        return target.isDirty(index);
    }

    protected void updateAccessRights() {
        MediaStateEventArgs e = new MediaStateEventArgs();
        e.setState(mMedia.isOpen() ? MediaState.OPEN : MediaState.CLOSED);
        onMediaStateChange(mMedia, e);
    }


    public static BaseObjectFragment newInstance(final FragmentActivity activity,
                                                 final IGXActionListener listener,
                                                 final GXDLMSClient client,
                                                 final IGXMedia media,
                                                 final GXDLMSObject value) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(activity).get(ObjectViewModel.class);
        objectViewModel.setMedia(media);
        objectViewModel.setObject(value);
        objectViewModel.setListener(listener);
        objectViewModel.setClient(client);
        BaseObjectFragment fragment;
        if (value instanceof GXDLMSAssociationLogicalName) {
            fragment = new AssociationLogicalNameObjectFragment();
        } else if (value instanceof GXDLMSAssociationShortName) {
            fragment = new AssociationShortNameObjectFragment();
        } else if (value instanceof GXDLMSData) {
            fragment = new DataObjectFragment();
        } else if (value instanceof GXDLMSRegister) {
            fragment = new RegisterObjectFragment();
        } else if (value instanceof GXDLMSClock) {
            fragment = new ClockObjectFragment();
        } else if (value instanceof GXDLMSGprsSetup) {
            fragment = new GprsSetupObjectFragment();
        } else if (value instanceof GXDLMSHdlcSetup) {
            fragment = new HdlcSetupObjectFragment();
        } else if (value instanceof GXDLMSIECLocalPortSetup) {
            fragment = new IecLocalPortSetupObjectFragment();
        } else if (value instanceof GXDLMSIp4Setup) {
            fragment = new Ip4SetupObjectFragment();
        } else if (value instanceof GXDLMSTcpUdpSetup) {
            fragment = new TcpUdpSetupObjectFragment();
        } else if (value instanceof GXDLMSPushSetup) {
            fragment = new PushSetupObjectFragment();
        } else if (value instanceof GXDLMSProfileGeneric) {
            fragment = new ProfileGenericObjectFragment();
        } else {
            fragment = new BaseObjectFragment();
        }
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMedia != null) {
            mMedia.removeListener(this);
        }
        binding = null;
    }

    @Override
    public void onError(Object sender, RuntimeException ex) {

    }

    @Override
    public void onReceived(Object sender, ReceiveEventArgs e) {

    }

    @Override
    public void onMediaStateChange(Object sender, MediaStateEventArgs e) {
        try {
            if (e.getState() == MediaState.OPEN ||
                    e.getState() == MediaState.CLOSED) {
                getActivity().runOnUiThread(() ->
                {
                    boolean access = false;
                    for (Map.Entry<View, Object> it : mComponents) {

                        if (it.getValue() instanceof AccessMode) {
                            access = CanWrite((AccessMode) it.getValue());
                        } else if (it.getValue() instanceof AccessMode3) {
                            access = CanWrite((AccessMode3) it.getValue());
                        } else if (it.getValue() instanceof MethodAccessMode) {
                            access = CanInvoke((MethodAccessMode) it.getValue());
                        } else if (it.getValue() instanceof MethodAccessMode3) {
                            access = CanInvoke((MethodAccessMode3) it.getValue());
                        }
                        it.getKey().setEnabled(e.getState() == MediaState.OPEN && access);
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
}