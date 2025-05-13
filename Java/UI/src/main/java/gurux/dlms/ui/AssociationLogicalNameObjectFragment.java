package gurux.dlms.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import gurux.dlms.ui.databinding.AssociationLogicalNameFragmentBinding;
import gurux.dlms.ui.internal.lnApplicationContextNameFragment;
import gurux.dlms.ui.internal.lnAuthenticationMechanismNameFragment;
import gurux.dlms.ui.internal.lnGeneralFragment;
import gurux.dlms.ui.internal.lnObjectsFragment;
import gurux.dlms.ui.internal.lnUserListFragment;
import gurux.dlms.ui.internal.lnxDLMSConextInfoFragment;

public class AssociationLogicalNameObjectFragment extends BaseObjectFragment {

    private AssociationLogicalNameFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final ObjectViewModel objectViewModel = new ViewModelProvider(requireActivity()).get(ObjectViewModel.class);
        mMedia = objectViewModel.getMedia();
        binding = AssociationLogicalNameFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fragment childFragment = new ObjectHeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.object_header, childFragment).commit();
        GXTab tab = new GXTab(this);
        binding.attributes.addView(tab);
        tab.addTab(getString(R.string.objects), new lnObjectsFragment());
        tab.addTab(getString(R.string.general), new lnGeneralFragment());
        tab.addTab(getString(R.string.xdlmsContextInfo), new lnxDLMSConextInfoFragment());
        tab.addTab(getString(R.string.applicationContextName), new lnApplicationContextNameFragment());
        tab.addTab(getString(R.string.authenticationMechanismName), new lnAuthenticationMechanismNameFragment());
        tab.addTab(getString(R.string.userList), new lnUserListFragment());
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