package gurux.dlms.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import gurux.common.IGXMedia;
import gurux.common.IGXMediaListener;
import gurux.common.MediaStateEventArgs;
import gurux.common.PropertyChangedEventArgs;
import gurux.common.ReceiveEventArgs;
import gurux.common.TraceEventArgs;
import gurux.dlms.GXDLMSTranslator;
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
import gurux.dlms.objects.enums.SecuritySuite;
import gurux.net.GXNet;
import gurux.serial.GXSerial;

public class MainActivity extends AppCompatActivity implements IGXMediaListener, IGXSettingsChangedListener {

    GXDevice mDevice = new GXDevice();
    private AppBarConfiguration mAppBarConfiguration;
    private GXManufacturerCollection mManufacturers;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevice.setMedia(new GXSerial(this));
        GXSerial serial = (GXSerial) mDevice.getMedia();
        if (serial.getPorts().length != 0) {
            serial.setPort(serial.getPorts()[0]);
        }
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
        mDevice.getMedia().addListener(this);
        MediaViewModel mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);

        IGXMedia[] medias = new IGXMedia[]{new GXSerial(this), new GXNet(this)};
        mediaViewModel.setMedias(medias);

        mediaViewModel.setDevice(mDevice);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fabRead.setOnClickListener((View.OnClickListener) view -> {
            //Start read.
            mainViewModelViewModel.getListener().onRead(null, 0);
        });
        binding.appBarMain.fabWrite.setOnClickListener((View.OnClickListener) view -> {
            //Start write.
            mainViewModelViewModel.getListener().onWrite(null, 0);
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_main, R.id.nav_meterSettings, R.id.nav_mediaSettings,
                R.id.nav_xml_translator, R.id.nav_obis_translator, R.id.nav_manufacturers,
                R.id.nav_info)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            //FAB is visible only read page.
            View fabRead = findViewById(R.id.fabRead);
            View fabWrite = findViewById(R.id.fabWrite);
            if (id == R.id.nav_main) {
                fabRead.setVisibility(View.VISIBLE);
                fabWrite.setVisibility(View.VISIBLE);
            } else {
                fabRead.setVisibility(View.GONE);
                fabWrite.setVisibility(View.GONE);
            }
            if (id == R.id.nav_info) {
                try {
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    LinearLayout layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setPadding(50, 40, 50, 10);

                    final TextView copyright = new EditText(this);
                    copyright.setMovementMethod(LinkMovementMethod.getInstance());
                    copyright.setText(R.string.copyright);
                    copyright.setTextIsSelectable(false);
                    copyright.setFocusable(false);
                    copyright.setClickable(false);
                    layout.addView(copyright);

                    final TextView version = new EditText(this);
                    version.setMovementMethod(LinkMovementMethod.getInstance());
                    version.setText(String.format("Version: %s", packageInfo.versionName));
                    version.setTextIsSelectable(false);
                    version.setFocusable(false);
                    version.setClickable(false);
                    layout.addView(version);

                    final TextView url = new EditText(this);
                    url.setMovementMethod(LinkMovementMethod.getInstance());
                    url.setText(HtmlCompat.fromHtml("<a href='https://www.gurux.fi'>More info</a>", HtmlCompat.FROM_HTML_MODE_LEGACY));
                    url.setLinksClickable(true);
                    url.setFocusable(false);
                    url.setTextIsSelectable(false);
                    layout.addView(url);
                    new AlertDialog.Builder(this)
                            .setTitle("About Gurux DLMS component")
                            .setView(layout)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawer.closeDrawer(GravityCompat.START);
            }
            return handled;
        });
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
            mDevice.setPassword(GXDLMSTranslator.hexToBytes(s.getString("password", null)));
            mDevice.setClientAddress(s.getInt("clientAddress", 16));
            mDevice.setAddressType(HDLCAddressType.values()[s.getInt("addressType", 0)]);
            mDevice.setPhysicalAddress(s.getInt("physicalAddress", 1));
            mDevice.setLogicalAddress(s.getInt("logicalAddress", 0));
        }
        mDevice.setConformance(s.getInt("conformance", -1));

        //Security
        mDevice.setSecurity(Security.forValue(s.getInt("security", 0)));
        mDevice.setSecuritySuite(SecuritySuite.forValue(s.getInt("securitySuite", 0)));
        mDevice.setSystemTitle(GXDLMSTranslator.hexToBytes(s.getString("systemTitle", null)));
        mDevice.setMeterSystemTitle(GXDLMSTranslator.hexToBytes(s.getString("meterSystemTitle", null)));
        mDevice.setBlockCipherKey(GXDLMSTranslator.hexToBytes(s.getString("blockCipherKey", "00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F")));
        mDevice.setAuthenticationKey(GXDLMSTranslator.hexToBytes(s.getString("authenticationKey", "D0  D1  D2  D3 D4  D5  D6  D7  D8  D9 DA  DB  DC  DD  DE  DF")));
        mDevice.setDedicatedKey(GXDLMSTranslator.hexToBytes(s.getString("dedicatedKey", null)));
        mDevice.setChallenge(GXDLMSTranslator.hexToBytes(s.getString("challenge", null)));
        mDevice.setInvocationCounter(s.getString("invocationCounter", ""));

        String type = s.getString("mediaType", mDevice.getMedia().getMediaType());
        if (type.equals("Net")) {
            mDevice.setMedia(new GXNet(this));
        }
        //Serial media is set as a default.
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
        editor.putString("password", GXDLMSTranslator.toHex(mDevice.getPassword()));
        editor.putInt("clientAddress", mDevice.getClientAddress());
        editor.putInt("addressType", mDevice.getAddressType().getValue());
        editor.putInt("physicalAddress", mDevice.getPhysicalAddress());
        editor.putInt("logicalAddress", mDevice.getLogicalAddress());

        editor.putInt("conformance", mDevice.getConformance());
        //Security.
        editor.putInt("security", mDevice.getSecurity().getValue());
        editor.putInt("securitySuite", mDevice.getSecuritySuite().getValue());
        editor.putString("systemTitle", GXDLMSTranslator.toHex(mDevice.getSystemTitle()));
        editor.putString("meterSystemTitle", GXDLMSTranslator.toHex(mDevice.getMeterSystemTitle()));
        editor.putString("blockCipherKey", GXDLMSTranslator.toHex(mDevice.getBlockCipherKey()));
        editor.putString("authenticationKey", GXDLMSTranslator.toHex(mDevice.getAuthenticationKey()));
        editor.putString("dedicatedKey", GXDLMSTranslator.toHex(mDevice.getDedicatedKey()));
        editor.putString("challenge", GXDLMSTranslator.toHex(mDevice.getChallenge()));
        editor.putString("invocationCounter", mDevice.getInvocationCounter());

        editor.putString("mediaType", mDevice.getMedia().getMediaType());
        editor.putString("mediaSettings", mDevice.getMedia().getSettings());
        editor.putString("objects", mDevice.getObjects().getXml());
        editor.apply();
        Toast.makeText(this, "Settings saved.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onError(Object sender, RuntimeException ex) {

    }

    @Override
    public void onReceived(Object sender, ReceiveEventArgs e) {

    }

    @Override
    public void onMediaStateChange(Object sender, MediaStateEventArgs e) {
    }

    @Override
    public void onTrace(Object sender, TraceEventArgs e) {

    }

    /**
     * User has change media properties.
     */
    @Override
    public void onPropertyChanged(Object sender, PropertyChangedEventArgs e) {
        saveSettings();
    }

    @Override
    public void onMediaChanged(IGXMedia value) {
        mDevice.getMedia().removeListener(this);
        saveSettings();
        mDevice.getMedia().addListener(this);
    }

    @Override
    public void onAssociationChanged() {
        saveSettings();
    }

    @Override
    public void onDeviceSettingChanged() {
        saveSettings();
    }
}