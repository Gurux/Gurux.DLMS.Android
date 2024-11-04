package gurux.dlms.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import gurux.dlms.android.databinding.ActivityMainBinding;
import gurux.dlms.android.ui.main.MainViewModel;
import gurux.dlms.android.ui.manufacturers.ManufacturersViewModel;
import gurux.dlms.android.ui.media.MediaViewModel;
import gurux.dlms.android.ui.meterSettings.MeterSettingsViewModel;
import gurux.dlms.enums.Authentication;
import gurux.dlms.enums.InterfaceType;
import gurux.dlms.enums.Security;
import gurux.dlms.manufacturersettings.GXAuthentication;
import gurux.dlms.manufacturersettings.GXManufacturer;
import gurux.dlms.manufacturersettings.GXManufacturerCollection;
import gurux.dlms.manufacturersettings.HDLCAddressType;
import gurux.serial.GXSerial;

public class MainActivity extends AppCompatActivity {

    GXDevice mDevice = new GXDevice();
    private AppBarConfiguration mAppBarConfiguration;
    private GXManufacturerCollection mManufacturers;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevice.setMedia(new GXSerial(this));
        mManufacturers = new GXManufacturerCollection();
        GXManufacturerCollection.readManufacturerSettings(this, mManufacturers);
        if ((mDevice.getManufacturer() == null || mDevice.getManufacturer().equals("")) &&
                !mManufacturers.isEmpty()) {
            mDevice.setManufacturer(mManufacturers.get(0).getName());
        }
        ManufacturersViewModel mManufacturersViewModel = new ViewModelProvider(this).get(ManufacturersViewModel.class);
        mManufacturersViewModel.updateManufacturers(mManufacturers);

        MainViewModel mainViewModelViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModelViewModel.setDevice(mDevice);

        MeterSettingsViewModel mMeterSettingsViewModel = new ViewModelProvider(this).get(MeterSettingsViewModel.class);
        mMeterSettingsViewModel.updateManufacturers(mManufacturers);
        mMeterSettingsViewModel.setDevice(mDevice);
        loadSettings();

        MediaViewModel mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        mediaViewModel.setDevice(mDevice);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_main, R.id.nav_meterSettings, R.id.nav_mediaSettings,
                R.id.nav_xml_translator, R.id.nav_obis_translator, R.id.nav_manufacturers)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }


    private void loadSettings() {
        SharedPreferences s = getPreferences(Context.MODE_PRIVATE);
        //Read mDevice settings.
        String man = s.getString("manufacturer", "");
        if (man != null && !man.isEmpty()) {
            mDevice.setManufacturer(man);
            try {
                mDevice.setInterfaceType(InterfaceType.values()[s.getInt("interfaceType", 0)]);
            } catch (Exception ex) {
                //Old way...
                mDevice.setInterfaceType(InterfaceType.HDLC);
            }

            mDevice.setWaitTime(s.getInt("waitTime", 5));
            mDevice.setMaximumBaudRate(s.getInt("maximumBaudRate", 0));
            try {
                String auth = s.getString("authentication", "None");
                for (GXManufacturer it : mManufacturers) {
                    if (it.getIdentification().compareTo(man) == 0) {
                        for (GXAuthentication authentication : it.getSettings()) {
                            if (auth.compareTo(authentication.toString()) == 0) {
                                mDevice.setAuthentication(authentication);
                                break;
                            }
                        }
                        break;
                    }
                }
                if (mDevice.getAuthentication() == null) {
                    mDevice.setAuthentication(new GXAuthentication(auth));
                }
            } catch (Exception ex) {
                //Old way...
                mDevice.setAuthentication(new GXAuthentication(Authentication.forValue(s.getInt("authentication", 0)).toString()));
            }
            mDevice.setPassword(s.getString("password", ""));
            mDevice.setSecurity(Security.forValue(s.getInt("security", 0)));
            mDevice.setSystemTitle(s.getString("systemTitle", ""));
            mDevice.setBlockCipherKey(s.getString("blockCipherKey", ""));
            mDevice.setAuthenticationKey(s.getString("authenticationKey", ""));
            mDevice.setClientAddress(s.getInt("clientAddress", 16));
            mDevice.setAddressType(HDLCAddressType.values()[s.getInt("addressType", 0)]);
            mDevice.setPhysicalAddress(s.getInt("physicalAddress", 1));
            mDevice.setLogicalAddress(s.getInt("logicalAddress", 0));
        }
        //Read media settings.
        String mediaSettings = s.getString("mediaSettings", null);
        mDevice.getMedia().setSettings(mediaSettings);
        try {
            mDevice.getObjects().setXml(s.getString("objects", null));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSettings() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("manufacturer", mDevice.getManufacturer());
        editor.putInt("interfaceType", mDevice.getInterfaceType().getValue());
        editor.putInt("waitTime", mDevice.getWaitTime());
        editor.putInt("maximumBaudRate", mDevice.getMaximumBaudRate());
        if (mDevice.getAuthentication() != null) {
            editor.putString("authentication", mDevice.getAuthentication().toString());
        }
        editor.putString("password", mDevice.getPassword());
        editor.putInt("security", mDevice.getSecurity().getValue());
        editor.putString("systemTitle", mDevice.getSystemTitle());
        editor.putString("blockCipherKey", mDevice.getBlockCipherKey());
        editor.putString("authenticationKey", mDevice.getAuthenticationKey());
        editor.putInt("clientAddress", mDevice.getClientAddress());
        editor.putInt("addressType", mDevice.getAddressType().getValue());
        editor.putInt("physicalAddress", mDevice.getPhysicalAddress());
        editor.putInt("logicalAddress", mDevice.getLogicalAddress());
        editor.putString("mediaSettings", mDevice.getMedia().getSettings());
        editor.putString("objects", mDevice.getObjects().getXml());
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onStop() {
        saveSettings();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        saveSettings();
        super.onDestroy();
    }
}