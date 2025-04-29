package gurux.dlms.android.ui.objects;

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
import gurux.dlms.android.IGXActionListener;
import gurux.dlms.android.R;
import gurux.dlms.android.databinding.ObjectBaseBinding;
import gurux.dlms.enums.AccessMode;
import gurux.dlms.enums.AccessMode3;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.MethodAccessMode;
import gurux.dlms.enums.MethodAccessMode3;
import gurux.dlms.objects.GXDLMSData;
import gurux.dlms.objects.GXDLMSObject;
import gurux.dlms.objects.GXDLMSRegister;
import gurux.dlms.objects.IGXDLMSBase;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
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

    protected void updateAccessRights() {
        MediaStateEventArgs e = new MediaStateEventArgs();
        e.setState(mMedia.isOpen() ? MediaState.OPEN : MediaState.CLOSED);
        onMediaStateChange(mMedia, e);
    }

    public static Fragment newInstance(final FragmentActivity activity,
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
        if (value instanceof GXDLMSData) {
            fragment = new DataObjectFragment();
        } else if (value instanceof GXDLMSRegister) {
            fragment = new RegisterObjectFragment();
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