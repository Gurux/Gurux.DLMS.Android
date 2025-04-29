package gurux.dlms.android.ui.media;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import gurux.common.IGXMedia;
import gurux.dlms.android.IGXSettingsChangedListener;
import gurux.dlms.android.R;
import gurux.dlms.android.databinding.FragmentMediaBinding;

public class MediaFragment extends Fragment {

    private FragmentMediaBinding binding;
    private IGXSettingsChangedListener listener;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MediaViewModel mediaViewModel =
                new ViewModelProvider(requireActivity()).get(MediaViewModel.class);

        binding = FragmentMediaBinding.inflate(inflater, container, false);
        List<String> items = new ArrayList<String>();
        for (IGXMedia it : mediaViewModel.getMedias()) {
            items.add(it.getMediaType());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(container.getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.media.setAdapter(adapter);
        int index = 0;
        for (IGXMedia it : mediaViewModel.getMedias()) {
            if (it.getMediaType().equals((mediaViewModel.getDevice().getMedia().getMediaType()))) {
                binding.media.setSelection(index);
                break;
            }
            ++index;
        }
        binding.media.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != -1) {
                    IGXMedia value = mediaViewModel.getMedias()[position];
                    if (!value.getMediaType().equals(mediaViewModel.getDevice().getMedia().getMediaType())) {
                        //If user has selected a new media type.
                        mediaViewModel.getDevice().setMedia(value);
                        Fragment childFragment = mediaViewModel.getDevice().getMedia().properties();
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                        transaction.replace(R.id.media_fragment_container, childFragment).commit();
                        //Notify activity that media has changed.
                        listener.onMediaChanged(value);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing.
            }
        });

        View view = binding.getRoot();
        Fragment childFragment = mediaViewModel.getDevice().getMedia().properties();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.media_fragment_container, childFragment).commit();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IGXSettingsChangedListener) {
            listener = (IGXSettingsChangedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IGXMediaChangedListener");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}