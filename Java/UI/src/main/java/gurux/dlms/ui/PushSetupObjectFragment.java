package gurux.dlms.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import gurux.dlms.ui.databinding.PushSetupFragmentBinding;
import gurux.dlms.ui.internal.pushCommunicationWindowFragment;
import gurux.dlms.ui.internal.pushGeneralFragment;
import gurux.dlms.ui.internal.pushObjectsFragment;

public class PushSetupObjectFragment extends BaseObjectFragment {

    private PushSetupFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        binding = gurux.dlms.ui.databinding.PushSetupFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        GXTab tab = new GXTab(this);
        binding.attributes.addView(tab);
        tab.addTab(getString(R.string.general), new pushGeneralFragment());
        tab.addTab(getString(R.string.objects), new pushObjectsFragment());
        tab.addTab(getString(R.string.communicationWindow), new pushCommunicationWindowFragment());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}