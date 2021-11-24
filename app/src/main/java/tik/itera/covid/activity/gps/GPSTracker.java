package tik.itera.covid.activity.gps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.Service;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import tik.itera.covid.activity.DrawerActivity;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean isWifiEnabled = false;
    boolean canGetLocation = false;

    Location location;
    double latitude = 0.0;
    double longitude = 0.0;
    public String sLocation = "0", sAlamatLengkap = "Tidak Ada", sCity = "Tidak Ada";

    //Address
    private List<Address> addressList;
    private Geocoder geocoder;

    //The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //10 Meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; //10 Seconds

    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        geocoder = new Geocoder(mContext, Locale.getDefault());
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            isWifiEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled && !isWifiEnabled) {
                showSettingsAlert();
            } else {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) +
                        ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                }
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network Enabled : ", "Network Enabled...");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled : ", "GPS Enabled...");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }

                if (isWifiEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("WiFi Enabled : ", "WiFi Enabled...");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                            }
                        }
                    }
                }

                if (String.valueOf(latitude).equalsIgnoreCase("0.0") || String.valueOf(longitude).equalsIgnoreCase("0.0")) {
                    showSettingsAlert();
                    canGetLocation = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception GPS : ", e.getMessage());
        }
        return location;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) +
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
            }
            try {
                locationManager.removeUpdates(this);
                Log.d("Location : ","Remove Location Manager !!");
            } catch (Exception ex) {
                Log.i("Location : ", "Fail Remove Location Listners !! " +ex.getMessage());
            }
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public String getAlamatLengkap(){
        String result = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && !addressList.isEmpty()) {
                String address = addressList.get(0).getAddressLine(0);
                String sKecamatan = addressList.get(0).getLocality();
                String sDesa = addressList.get(0).getSubLocality();
                String sKabupaten = addressList.get(0).getSubAdminArea();
                String sProvinsi = addressList.get(0).getAdminArea();
                String sNegara = addressList.get(0).getCountryName();
                String postalCode = addressList.get(0).getPostalCode();
                String knownName = addressList.get(0).getFeatureName();
                result = address;
            }else{
                result = "Unable Connect Geocoder !!";
            }
        } catch (IOException e) {
            Log.e("Excetion Address : ", "Unable Connect Geocoder !!", e);
        }
        return result;
    }

    public String getCity(){
        String result = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && !addressList.isEmpty()) {
                String address = addressList.get(0).getAddressLine(0);
                String sKecamatan = addressList.get(0).getLocality();
                String sDesa = addressList.get(0).getSubLocality();
                String sKabupaten = addressList.get(0).getSubAdminArea();
                String sProvinsi = addressList.get(0).getAdminArea();
                String sNegara = addressList.get(0).getCountryName();
                String postalCode = addressList.get(0).getPostalCode();
                String knownName = addressList.get(0).getFeatureName();
                result = sKabupaten;
            }  else {
                result = "Unable Connect Geocoder !!";
            }
        } catch (IOException e) {
            Log.e("Excetion Address : ", "Unable Connect Geocoder !!", e);
        }
        return result;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Pocket Notification");
        alertDialog.setMessage("Please Enabled GPS From Settings...");
        alertDialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callActivity();
                dialog.cancel();
            }
        });
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }

    private void callActivity(){
        Intent i = new Intent(mContext, DrawerActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(i);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        sLocation = latitude +" / "+ longitude;
        sAlamatLengkap = getAlamatLengkap();
        sCity = getCity();
        Log.d("My Location : ",sLocation+"\n"+
                    "City : "+sCity+"\n"+
                "Alamat Lengkap : "+sAlamatLengkap);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
