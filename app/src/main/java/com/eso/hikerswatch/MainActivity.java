package com.eso.hikerswatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView mLatitude,mLongitude,mAccuracy,mAltitude,mAddress;
    String address = "Could not find address :(";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLatitude = findViewById(R.id.latitude);
        mLongitude = findViewById(R.id.longitude);
        mAccuracy = findViewById(R.id.accuracy);
        mAltitude = findViewById(R.id.altitude);
        mAddress = findViewById(R.id.address);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateLocationInfo(lastKnownLocation);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateLocationInfo(Location location) {
        mLatitude.setText("Latitude: "+ location.getLatitude());
        mLongitude.setText("Longitude: "+ location.getLongitude());
        mAccuracy.setText("Accuracy: "+ location.getAccuracy());
        mAltitude.setText("Altitude: "+ location.getAltitude());
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try{
            List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if (listAddress != null && listAddress.size() >0){
                address = "Address:\n";
                if (listAddress.get(0).getThoroughfare() != null)
                    address += listAddress.get(0).getThoroughfare()+"\n";
                if (listAddress.get(0).getLocality() != null)
                    address += listAddress.get(0).getLocality()+" ";
                if (listAddress.get(0).getPostalCode() != null)
                    address += listAddress.get(0).getPostalCode()+" ";
                if (listAddress.get(0).getAdminArea() != null)
                    address += listAddress.get(0).getAdminArea();

            }
        }catch (Exception e){e.printStackTrace();}
        mAddress.setText(address);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}
