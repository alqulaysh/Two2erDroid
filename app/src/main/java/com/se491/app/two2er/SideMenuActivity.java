package com.se491.app.two2er;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.se491.app.two2er.Fragments.Bookings.BookingsFragment;
import com.se491.app.two2er.Fragments.Bookings.CreateBooking;
import com.se491.app.two2er.Fragments.ScheduleFragment;
import com.se491.app.two2er.Fragments.UserProfile.UserProfileFragment;
import com.stormpath.sdk.Stormpath;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SideMenuActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowCloseListener {

        SupportMapFragment sMapFragment;
        private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

        //GoogleApi Client Services:
        GoogleApiClient mGoogleApiClient;

        //Google Map Variable:
        GoogleMap mGoogleMap;
        View mapView;

        //Booking fragment
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        //Location Variables:
        LocationRequest mLocationReq;
        Location mLastLocation;
        Marker mCurrLocationMarker;

        //Get list of users around me:
        HashMap<String, UserObject> usersAround;
        HashMap<String, UserObject> tempRecUsers = new HashMap<String, UserObject>();

        //MyUser Profile:
        UserObject myUserProfile;

        //Book Tutor Button:
        // Custom view
        private Button bookingButton;
        private RelativeLayout lContainerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        sMapFragment = SupportMapFragment.newInstance();
        setContentView(R.layout.activity_sidemenu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        usersAround = new HashMap<String, UserObject>();
        myUserProfile = new UserObject();
        System.out.println(myUserProfile.fname + " 1++++++++++++++++++++++++++");

        //Wait until we get our User Info before continuing:
        try {
            new GetUsers(this).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //Set the sidemenu image to users profile picture:
        View hView =  navigationView.getHeaderView(0);
        ImageView nav_user = (ImageView)hView.findViewById(R.id.Nav_imageView);

        if(!myUserProfile.userImage.isEmpty() || myUserProfile.userImage == "") {
            try {
                new DownloadImageTask(nav_user)
                        .execute(myUserProfile.userImage).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getFragmentManager();

        android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();
        sFm.beginTransaction().replace(R.id.map, sMapFragment).commit();

        sMapFragment.getMapAsync(this);

        Intent intent = new Intent(this, LocationRefreshService.class);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();

        int id = item.getItemId();


        if (sMapFragment.isAdded())
            sFm.beginTransaction().hide(sMapFragment).commit();

        if (id == R.id.nav_userprofile) {
            Bundle bundle = new Bundle();
            if(myUserProfile.fname != null){
                bundle.putString("fname", myUserProfile.fname);
                bundle.putString("lname", myUserProfile.lname);
                bundle.putString("email", myUserProfile.email);
                bundle.putString("userImage", myUserProfile.userImage);
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                userProfileFragment.setArguments(bundle);

                fm.beginTransaction().replace(R.id.content_frame,userProfileFragment).commit();
            }
        } else if (id == R.id.nav_map) {

            if (!sMapFragment.isAdded())
                sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
            else
                sFm.beginTransaction().show(sMapFragment).commit();

        } else if (id == R.id.nav_schedule) {
            fm.beginTransaction().replace(R.id.content_frame, new ScheduleFragment()).commit();
        } else if (id == R.id.nav_manage) {
            fm.beginTransaction().replace(R.id.content_frame, new BookingsFragment()).commit();
            //fm.beginTransaction().replace(R.id.content_framePad, new BookingsFragment()).commit();
        } else if (id == R.id.nav_logout) {
            Stormpath.logout();
            startActivity(new Intent(SideMenuActivity.this, LoginActivity.class));
            finish();
        }
//        else if (id == R.id.nav_uploadimage) {
//            startActivity(new Intent(SideMenuActivity.this, UploadImageActivity.class));
//            finish();
//        } else if (id == R.id.nav_changepassword) {
//            startActivity(new Intent(SideMenuActivity.this, ChangePasswordActivity.class));
//            finish();
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLocationChanged(Location location){
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //Loop through the user array that we got from the webservice/api of Two2er
        //addUsersAroundMe(); //Moved to CameraMove

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        //stop location updates.36

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if(mGoogleMap != null){

            //Set the listener for the Camera
            mGoogleMap.setOnCameraIdleListener(this);

            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView userName = (TextView) v.findViewById(R.id.user_name);
                    userName.setText(marker.getTitle());


                    return v;
                }
            });
        }
        // Create booking button
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnInfoWindowCloseListener(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        //Move My Position button to bottom left:
        mapView = sMapFragment.getView();
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);


        mGoogleApiClient = MyGoogleApiClient_Singleton.getInstance(null).get_GoogleApiClient();
        Log.e("MyGoogleApiClient", "isConnected: " + mGoogleApiClient.isConnected());
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//        mGoogleApiClient.connect();

        mLocationReq = LocationRequest.create();
        mLocationReq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationReq.setInterval(2000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.e("MyGoogleApiClient", "Second isConnected: " + mGoogleApiClient.isConnected());
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationReq,this);

    }

    public void handleFindTutor(){
        addUsersAroundMe();
    }

    //Helper Function To Get Users Around Me:
    private void addUsersAroundMe(){

        //mGoogleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.genuser));
        Log.e("Inside onCameraIdle", "Json parsing usersAround: " + usersAround.size());
        Log.e("Inside onCameraIdle", "Json parsing tempRecUsers: " + tempRecUsers.size());

        if(tempRecUsers.size() != usersAround.size()) {
            tempRecUsers.putAll(usersAround);
            Iterator it = tempRecUsers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                UserObject user = (UserObject) pair.getValue();
                LatLng lNewLocation = new LatLng(user.dLat, user.dLong);
                //Get the users name:
                String sTitle = user.fname + " " + user.lname;


                markerOptions.position(lNewLocation).title(sTitle);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("genuser", 100, 100))); //icon and size of tutors icons inside Google Map

                float nCameraZoom = mGoogleMap.getCameraPosition().zoom;

                mGoogleMap.addMarker(markerOptions);
            }
        }
    }

    // This method for changing the size of the tutors icons inside Google Map
    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }



    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraIdle() {
        CameraPosition fMyCameraPostion = mGoogleMap.getCameraPosition();
        LatLng ltMyCameraCoords = fMyCameraPostion.target;
        double myCameraLong = ltMyCameraCoords.longitude;
        double myCameraLat = ltMyCameraCoords.latitude;
        float fMyCameraZoom = fMyCameraPostion.zoom;
        new GetUsers(this, 5.0, myCameraLong, myCameraLat).execute();
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Fake empty container layout
        lContainerLayout = new RelativeLayout(this);
        lContainerLayout.setLayoutParams(new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT , RelativeLayout.LayoutParams.MATCH_PARENT ));
        // Custom view
        bookingButton = new Button(this);
        bookingButton.setText("Book Tutor");
        RelativeLayout.LayoutParams lButtonParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT , RelativeLayout.LayoutParams.WRAP_CONTENT );
        lButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bookingButton.setLayoutParams(lButtonParams);
        lContainerLayout.addView(bookingButton);

        // Adding full screen container
        addContentView(lContainerLayout, new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT , RelativeLayout.LayoutParams.MATCH_PARENT ) );


        bookingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(SideMenuActivity.this, marker.getSnippet(), Toast.LENGTH_LONG).show();

                Bundle args = new Bundle();
                args.putString("uID", marker.getSnippet());
                CreateBooking bFragment = new CreateBooking();
                // Show DialogFragment
                bFragment.setArguments(args);
                bFragment.show(fm, "Dialog Fragment");

            }
        });

        return false;
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        // Custom view
        Log.e("InfoWindow", "Closed InfoWindow: " + usersAround.size());
        //lContainerLayout.setVisibility(View.GONE);
        bookingButton.setVisibility(View.GONE);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
