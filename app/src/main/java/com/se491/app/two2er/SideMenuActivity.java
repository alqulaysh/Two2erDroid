package com.se491.app.two2er;

import android.app.Activity;
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
import android.support.v4.app.Fragment;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
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
import com.se491.app.two2er.Activities.AdditionalProfileActivity;
import com.se491.app.two2er.Activities.Bookings.BookingsActivity;
import com.se491.app.two2er.Activities.Bookings.CreateBooking;
import com.se491.app.two2er.Activities.Help.HelpActivity;
import com.se491.app.two2er.Activities.Payment.PaymentActivity;
import com.se491.app.two2er.Activities.SchduleActivity;
import com.se491.app.two2er.Activities.Settings.SettingsActivity;
import com.se491.app.two2er.Activities.StartPage.LoginActivity;
import com.se491.app.two2er.Activities.UserProfile.UserProfileActivity;
import com.se491.app.two2er.GetUsers.DistanceRefreshStrategy;
import com.se491.app.two2er.GetUsers.GetUsers;
import com.se491.app.two2er.GetUsers.RefreshStrategyBase;
import com.se491.app.two2er.GetUsers.RefreshUsersBySubjectFilter;
import com.se491.app.two2er.HelperObjects.CurrentUser;
import com.se491.app.two2er.HelperObjects.MyGoogleApiClient_Singleton;
import com.se491.app.two2er.HelperObjects.UserObject;
import com.se491.app.two2er.SearchView.MyFloatingSearchView;
import com.se491.app.two2er.Services.LocationRefreshService;
import com.stormpath.sdk.Stormpath;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
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

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    public static int RESULT_USER_PROFILE = 1;
    SupportMapFragment sMapFragment;
    //GoogleApi Client Services:
    GoogleApiClient mGoogleApiClient;

    //Google Map Variable:
    GoogleMap mGoogleMap;
    View mapView;

    //Booking fragment
    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

    //Main Fragments
    android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();

    //Location Variables:
    LocationRequest mLocationReq;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    //Get list of users around me:
    HashMap<String, UserObject> usersAround = new HashMap<>();
    //Markers with userinfo
    HashMap<Marker, UserObject> hashMapUserMarkers = new HashMap<>();


    //MyUser Profile:
    UserObject myUserProfile;
    //User side menu profile image:
    private ImageView nav_userImage;
    private TextView nav_username;
    private TextView nav_title_tutor;
    private TextView nav_title_tutor_sub;

    //Book TutorObject Button:
    // Custom view
    private Button bookingButton;
    private RelativeLayout lContainerLayout;

    //Search EditText
    private FloatingSearchView searchView;

    private String TAG = "SideMenuActivity";
    private static volatile Date lastRefreshedAt = new Date();

    private Thread automatedMapRefresh;
    private static volatile boolean isRunningAutomatedRefresh = true;

    private RefreshStrategyBase refreshStrategy = new DistanceRefreshStrategy();

    public HashMap<String, UserObject> getUsersAround() { return usersAround; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SetupForApplication();
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sMapFragment = SupportMapFragment.newInstance();
        setContentView(R.layout.activity_sidemenu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myUserProfile = CurrentUser.getCurrentUser();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Called when a drawer's position changes.
                if (bookingButton != null)
                    bookingButton.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Called when a drawer has settled in a completely open state.
                //The drawer is interactive at this point.
                // If you have 2 drawers (left and right) you can distinguish
                // them by using id of the drawerView. int id = drawerView.getId();
                // id will be your layout's id: for example R.id.left_drawer
                if (bookingButton != null)
                    bookingButton.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Called when a drawer has settled in a completely closed state.

                Fragment myFragment = sFm.findFragmentByTag("ContentFrag");
                if (myFragment != null && myFragment.isVisible()) {
                    if (bookingButton != null)
                        bookingButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
            }
        });

        toggle.syncState();

        //NAVIGATION HEADER VIEW:
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        nav_userImage = (ImageView) hView.findViewById(R.id.circleImageView);
        nav_username = (TextView) hView.findViewById(R.id.UserName_nav);
        nav_title_tutor = (TextView) hView.findViewById(R.id.nav_title_tutor);
        nav_title_tutor_sub = (TextView) hView.findViewById(R.id.nav_title_tutor_sub);

        nav_username.setText(myUserProfile.getUserFullName());

        if (!myUserProfile.userImage.isEmpty() || Objects.equals(myUserProfile.userImage, "")) {
            try {
                new DownloadImageTask(nav_userImage)
                        .execute(myUserProfile.userImage).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        //Set the onclick listener for the UserProfile Image on the SideMenuActivity
        nav_userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(SideMenuActivity.this, UserProfileActivity.class));
                startActivityForResult(new Intent(SideMenuActivity.this, UserProfileActivity.class), RESULT_USER_PROFILE);
            }
        });

        //If the student does not have a Tutor profile give them option to set one up:
        Menu MainMenu = navigationView.getMenu();

        MenuItem switchProfile = MainMenu.findItem(R.id.switchprofile);

        //If user has less than 2 profiles dont show the switch button:
        if(myUserProfile.userGroups.size() < 2){
            //If user is not a Tutor give the side menu option of becoming a tutor:
            if(!myUserProfile.userGroupsContains("Tutor")) {
                nav_title_tutor_sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(SideMenuActivity.this, AdditionalProfileActivity.class));
                    }
                });
                nav_title_tutor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(SideMenuActivity.this, AdditionalProfileActivity.class));
                    }
                });
            }
            //Dont display the switch button since user only has 1 profile:
            switchProfile.setVisible(false);
        }
        //Otherwise just hide the buttons become tutor buttons but display the Switch button:
        else{
            nav_title_tutor.setVisibility(View.GONE);
            nav_title_tutor_sub.setVisibility(View.GONE);
            switchProfile.setVisible(true);
        }


        //Set our nav view Item Selected listener(its implemented by this activity):
        navigationView.setNavigationItemSelectedListener(this);
        //botDrawerView.setNavigationItemSelectedListener(this);

        //Get our search EditText:
        searchView = (FloatingSearchView) findViewById(R.id.searchView);

        new MyFloatingSearchView(this, searchView);

        //Set our map fragment to our content view and tag it as "ContentFrag":
        sFm.beginTransaction().replace(R.id.map, sMapFragment, "ContentFrag").commit();

        refreshUsersList();

        sMapFragment.getMapAsync(this);

        automatedMapRefresh = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunningAutomatedRefresh) {
                    try {
                        Date now = new Date();
                        if ((now.getTime() - lastRefreshedAt.getTime()) / 60000 > 1) {
                            Thread.sleep(60000);
                            refreshUsersList();
                        }
                    }
                    catch (Exception ex) {
                        Log.e(TAG, ex.toString());
                        ex.printStackTrace();
                    }
                }
            }
        });
        automatedMapRefresh.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunningAutomatedRefresh = false;
        try {
            automatedMapRefresh.join();
        }
        catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            if (bookingButton != null)
                bookingButton.setVisibility(View.VISIBLE);

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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();

        int id = item.getItemId();



        if (id == R.id.nav_userprofile) {
            if (myUserProfile.fname != null) {
                //startActivity(new Intent(SideMenuActivity.this, UserProfileActivity.class));
                startActivityForResult(new Intent(SideMenuActivity.this, UserProfileActivity.class), RESULT_USER_PROFILE);
            }
//        } else if (id == R.id.nav_map) {
//            if (!sMapFragment.isAdded())
//                sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
         else
                sFm.beginTransaction().show(sMapFragment).commit();

//        } else if (id == R.id.nav_schedule) {
//            startActivity(new Intent(SideMenuActivity.this, SchduleActivity.class));
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(SideMenuActivity.this, BookingsActivity.class));
        } else if (id == R.id.nav_logout) {
            Stormpath.logout();
            CurrentUser.Clear();
            startActivity(new Intent(SideMenuActivity.this, LoginActivity.class));
            finish();
//        } else if (id == R.id.nav_help) {
//            startActivity(new Intent(SideMenuActivity.this, UserProfileActivity.class));
        }
        else if (id == R.id.nav_schedule) {
            startActivity(new Intent(SideMenuActivity.this, SchduleActivity.class));
        }
        else if (id == R.id.nav_payment) {
            startActivity(new Intent(SideMenuActivity.this, PaymentActivity.class));
        }
        else if (id == R.id.nav_help) {
            startActivity(new Intent(SideMenuActivity.this, HelpActivity.class));
        }
        else if (id == R.id.nav_settings) {
            startActivity(new Intent(SideMenuActivity.this, SettingsActivity.class));
        }


        else if (id == R.id.switchprofile) {
            SessionState.toggleUserMode();
            // TODO fix to go back to previous view
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (bookingButton != null)
            bookingButton.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //Move map camera
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

        if (mGoogleMap != null) {
            //Set the listener for the Camera
            mGoogleMap.setOnCameraIdleListener(this);

            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);
                    Log.i(TAG, "This is inside getInfoContents");
                    ImageView infoCircleImage = (ImageView) v.findViewById(R.id.circleImageView);

                    UserObject myMarkerUser = hashMapUserMarkers.get(marker);

                    if(myMarkerUser.userImageBitMap == null){
                        myMarkerUser.xDownloadUserBitMap();
                    }

                    if(myMarkerUser.userImageBitMap != null)
                        infoCircleImage.setImageBitmap(myMarkerUser.userImageBitMap);

                    TextView userName = (TextView) v.findViewById(R.id.user_name);
                    userName.setText(marker.getTitle());

                    TextView user_availability = (TextView) v.findViewById(R.id.user_availability);
                    user_availability.setText("Availability: " + "Now");

                    TextView userSchool = (TextView) v.findViewById(R.id.userSchool);
                    if(!myMarkerUser.Education.School.isEmpty()) {
                        userSchool.setText(myMarkerUser.Education.getSchoolAndDegree());
                    }

                    TextView user_major = (TextView) v.findViewById(R.id.user_major);
                    if(!myMarkerUser.Education.Degree.isEmpty())
                        user_major.setText("Major: " + myMarkerUser.Education.Field);

                    RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
                    ratingBar.setRating((float) myMarkerUser.Tutor.Rating);

                    return v;
                }
            });
        }

        // Create booking button
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnInfoWindowCloseListener(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        //Move My Position button to bottom left:
        mapView = sMapFragment.getView();
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        //Position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);

        mGoogleApiClient = MyGoogleApiClient_Singleton.getInstance(null).get_GoogleApiClient();

        mLocationReq = LocationRequest.create();
        mLocationReq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationReq.setInterval(2000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.i(TAG, "Second isConnected: " + mGoogleApiClient.isConnected());
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationReq, this);

        LatLng latLng = new LatLng(LocationRefreshService.getLatitude(), LocationRefreshService.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        refreshMap();
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
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (!CurrentUser.getCurrentUser().IsValidTimekitUser) {
            return true;
        }

        // Fake empty container layout
        lContainerLayout = new RelativeLayout(this);
        lContainerLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        // Custom view
        bookingButton = new Button(this);
        bookingButton.setText("Make Booking");
        RelativeLayout.LayoutParams lButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bookingButton.setLayoutParams(lButtonParams);
        lContainerLayout.addView(bookingButton);

        // Adding full screen container
        addContentView(lContainerLayout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        bookingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("uID", marker.getSnippet());
                System.out.println("this is my id: " + marker.getSnippet());
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
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void refreshUsersList() {
        usersAround = runRefreshStrategy(new DistanceRefreshStrategy(100));
    }

    public void refreshMap() {
        HashMap<String, UserObject> users = runRefreshStrategy(refreshStrategy);
        addUsersToMap(users);
    }

    private HashMap<String, UserObject> runRefreshStrategy(RefreshStrategyBase strat) {
        GetUsers usersRefreshTask = new GetUsers(strat);
        usersRefreshTask.start();

        try {
            usersRefreshTask.join();
        }
        catch (Exception ex) {
            Log.e(TAG, "Error in refreshUsersList: " + ex.toString());
            ex.printStackTrace();
        }

        return usersRefreshTask.getUsersList();
    }

    private void addUsersToMap(HashMap<String, UserObject> users) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.loction));
        Iterator it = users.entrySet().iterator();

        mGoogleMap.clear();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            UserObject user = (UserObject) pair.getValue();
            LatLng lNewLocation = new LatLng(user.dLat, user.dLong);

            //Get the users name:
            String sTitle = user.fname + " " + user.lname;

            //We store the users id on the marker snippet.
            markerOptions.position(lNewLocation).title(sTitle).snippet(user.id);

            //Add the markers on the map:
            Marker mNewMarker = mGoogleMap.addMarker(markerOptions);
            hashMapUserMarkers.put(mNewMarker, user);
        }
    }

    public void setDistanceSearchStrategy(double distance) {
        Log.i(TAG, "Switching to distance users filter: " + distance);
        refreshStrategy = new DistanceRefreshStrategy(distance);
    }

    public void setFilterSearchStrategy(String filter) {
        Log.i(TAG, "Switching to subject users filter: " + filter);
        refreshStrategy = new RefreshUsersBySubjectFilter(usersAround, filter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "Im inside onActivityResult");
        Log.i(TAG, "Im inside onActivityResult requestCode" + requestCode);
        Log.i(TAG, "Im inside onActivityResult RESULT_USER_PROFILE" + RESULT_USER_PROFILE);
        Log.i(TAG, "Im inside onActivityResult resultCode" + resultCode);
        Log.i(TAG, "Im inside onActivityResult RESULT_OK" + RESULT_OK);
        if (requestCode ==  RESULT_USER_PROFILE
                && resultCode == Activity.RESULT_OK
                //&& null != data
                ){
            //Update the side Menu UserName:
            nav_username.setText(CurrentUser.getCurrentUser().getUserFullName());
            //Update the user side menu profile pic:
            CurrentUser.updateProfilePics(nav_userImage);
        }

    }


    private void SetupForApplication() {
        Log.i(TAG, "Running SetupForApplication");

        CurrentUser.Init();

        Intent intent = new Intent(this, LocationRefreshService.class);
        if (intent != null) {
            this.startService(intent);
        }
    }
}
