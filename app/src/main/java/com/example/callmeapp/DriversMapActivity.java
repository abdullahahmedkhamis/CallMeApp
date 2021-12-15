package com.example.callmeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriversMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    private Button LogoutDriverButton;
    private Button SettingsDriverButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Boolean currentLogoutDriverStatus = false;
    private DatabaseReference AssignedCustomer, AssignedCustomerPickUp;
    private String driverID, CustomerID="";
   Marker PickUpMarker;

private  ValueEventListener AssignedCustomerPickUpListner;

    private TextView txtName, txtPhone, txtCarName;
    private CircleImageView profilePic;
    private RelativeLayout relativeLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_map);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        driverID = mAuth.getCurrentUser().getUid();

        LogoutDriverButton = (Button) findViewById(R.id.driver_logout_btn);
        SettingsDriverButton = (Button) findViewById(R.id.driver_settings_btn);


        txtName = findViewById(R.id.name_customer);
        txtPhone = findViewById(R.id.phone_customer);
        profilePic = findViewById(R.id.profile_image_customer);
        relativeLayout = findViewById(R.id.rel2);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SettingsDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriversMapActivity.this, Setting.class);
                intent.putExtra("type", "Drivers");
                startActivity(intent);
            }
        });


        LogoutDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                currentLogoutDriverStatus = true;
                DisconnectTheDriver();

                mAuth.signOut();
                LogoutDriver();

            }
        });

        GetAssignedCustomerRequest();
    }

    private void GetAssignedCustomerRequest()
    {
      AssignedCustomer = FirebaseDatabase.getInstance().getReference().child("Users")
              .child("Drivers").child(driverID).child("CustomerRideID");

      AssignedCustomer.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(dataSnapshot.exists())
              {
                  CustomerID = dataSnapshot.getValue().toString();
                  GetAssignedCustomerPickUpLocation();

                  relativeLayout.setVisibility(View.VISIBLE);
                  GetAssignedCustomerPickUpLocation();
              }
              else
              {
                  CustomerID = "";
                  if(PickUpMarker != null)
                  {
                      PickUpMarker.remove();
                  }

if(AssignedCustomerPickUpListner != null)
{
    AssignedCustomerPickUp.removeEventListener(AssignedCustomerPickUpListner);
}
relativeLayout.setVisibility(View.GONE);
              }

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });


    }

    private void GetAssignedCustomerPickUpLocation()
    {
    AssignedCustomerPickUp = FirebaseDatabase.getInstance().getReference().child("Customer Requests")
            .child(CustomerID).child("l");

        AssignedCustomerPickUpListner = AssignedCustomerPickUp.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if(dataSnapshot.exists())
            {
                List<Object> customerLocatonMap = (List<Object>) dataSnapshot.getValue();

                double LocationLat = 0;
                double LocationLng = 0;

                if(customerLocatonMap.get(0) != null)
                {
                    LocationLat = Double.parseDouble(customerLocatonMap.get(0).toString());

                }
                if(customerLocatonMap.get(1) != null)
                {
                    LocationLng = Double.parseDouble(customerLocatonMap.get(1).toString());

                }
                LatLng DriverLatLng = new LatLng(LocationLat, LocationLng);

                mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("Customer PickUp Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        buildGoogleApiClient();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);



        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

       if(getApplicationContext() != null)
       {
           lastLocation = location;
           LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
           mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
           mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

           String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


           DatabaseReference DriverAvilibiltyRef = FirebaseDatabase.getInstance().getReference().child("Drivers Avilable");
           GeoFire geoFireAvalibality = new GeoFire(DriverAvilibiltyRef);

           DatabaseReference DriverWorking = FirebaseDatabase.getInstance().getReference().child("Drivers working");
       GeoFire geoFireWorking = new GeoFire(DriverWorking);


          switch (CustomerID)
          {
              case "":
                  geoFireWorking.removeLocation(userID);
                  geoFireAvalibality.setLocation(userID, new GeoLocation(location.getLatitude(),location.getLongitude()));
                  break;

                  default:
                      geoFireAvalibality.removeLocation(userID);
                      geoFireWorking.setLocation(userID, new GeoLocation(location.getLatitude(),location.getLongitude()));
                      break;
          }


       }


    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(!currentLogoutDriverStatus)
        {
            DisconnectTheDriver();
        }


    }

    private void DisconnectTheDriver()
    {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriverAvilibiltyRef = FirebaseDatabase.getInstance().getReference().child("Drivers Avilable");
        GeoFire geoFire = new GeoFire(DriverAvilibiltyRef);
        geoFire.removeLocation(userID);

    }

     private void LogoutDriver()
    {
        Intent welcomeIntent = new Intent(DriversMapActivity.this, WelcomActivity.class);
       welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(welcomeIntent);
        finish();
    }

    private void getAssignedCustomerInformation() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Customers").child(CustomerID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();

                    txtName.setText(name);
                    txtPhone.setText(phone);

                    if (dataSnapshot.hasChild("image")) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
