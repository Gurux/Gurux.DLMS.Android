package gurux.dlms.android.ui.manufacturers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import gurux.dlms.android.GXGeneral;
import gurux.dlms.android.R;
import gurux.dlms.android.databinding.FragmentManufacturersBinding;
import gurux.dlms.manufacturersettings.GXManufacturer;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;

public class ManufacturersFragment extends Fragment {

    private GXManufacturerCollection mMans;
    private Spinner mManufacturers;
    private EditText mManufacturer;

    private FragmentManufacturersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ManufacturersViewModel manufacturersViewModel =
                new ViewModelProvider(requireActivity()).get(ManufacturersViewModel.class);
        mMans = manufacturersViewModel.getManufacturers();
        binding = FragmentManufacturersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mManufacturers = (Spinner) view.findViewById(R.id.manufacturers);
        mManufacturer = (EditText) view.findViewById(R.id.manufacturer);
        mManufacturers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GXManufacturer man = mMans.get(position);
                mManufacturer.setText(man.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        try {
            List<String> rows = new ArrayList<String>();
            for (GXManufacturer it : mMans) {
                rows.add(it.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, rows);
            mManufacturers.setAdapter(adapter);
        } catch (Exception e) {
            mManufacturers.setEnabled(false);
            GXGeneral.showError(getActivity(), e, getString(R.string.error));
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}