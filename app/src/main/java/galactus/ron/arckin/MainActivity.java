package galactus.ron.arckin;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import layout.DeviceFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,ConnectivityReceiver.ConnectivityReceiverListener{
    CollapsingToolbarLayout collapsingToolbarLayoutAndroid;
    CoordinatorLayout rootLayoutAndroid;
    GridView gridView;
    private static final String TAG= "random";


    static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public ImageView profilePic;
    public TextView profileName,profileEmail;
     String name,email,uid;
    public Uri photoUrl;
    ImageView mImageView;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    StorageReference storageReference = storage.getReference("image.png");



    public static String[] gridViewStrings = {
            "List",
            "IoT Box",
            "Alarm Clock",
            "Calender",
            "Adapter",
            "Custom GridView",
            "Material",
            "XML",
            "Code",

    };
    public static int[] gridViewImages = {
            R.drawable.todo,
            R.drawable.iotbox,
            R.drawable.alarm,
            R.drawable.calendar,
            R.drawable.delete,
            R.drawable.delete,
            R.drawable.delete,
            R.drawable.delete,
            R.drawable.delete
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageView= (ImageView) findViewById(R.id.mImageView);

        //

        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            Toast.makeText(this,"Connected, Good to go ! :)",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Disconnected, Please check your Internet Connection",Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        gridView = (GridView) findViewById(R.id.grid);
        gridView.setAdapter(new CustomAndroidGridViewAdapter(this, gridViewStrings, gridViewImages));
        initInstances();
        Bundle bundle = getIntent().getExtras();
        final String message = bundle.getString("message");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, notes.class);
                        intent.putExtra("id", position);
                        intent.putExtra("UserName",uid);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent1 = new Intent(MainActivity.this, iotbox.class);
                        intent1.putExtra("id", position);
                        intent1.putExtra("deviceID",message);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent3 = new Intent(MainActivity.this, Alarm.class);
                        intent3.putExtra("id", position);
                        intent3.putExtra("UserName",uid);
                        startActivity(intent3);
                        break;
                    case 3:
                        Intent intent4 = new Intent(MainActivity.this, CalenderActivity.class);
                        intent4.putExtra("id", position);
                        intent4.putExtra("UserName",uid);
                        startActivity(intent4);
                        break;
                    case 4:


                        FirebaseDatabase database= FirebaseDatabase.getInstance();
                        DatabaseReference devices= database.getReference("Devices");
                        //hardcoded id !=
                        Bundle bundle = getIntent().getExtras();
                        String message = bundle.getString("message");
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        devices.child(message).child("Name").setValue(name);
                        devices.child(message).child("Email").setValue(email);
                        devices.child(message).child("UserID").setValue(uid);

                        break;
                    case 5:
                       Intent intent6= new Intent(MainActivity.this,CalenderActivity.class);
                        startActivity(intent6);
                        break;
                    default:
                        break;
                }

            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };



        View navHeaderView= navigationView.inflateHeaderView(R.layout.nav_header_main);
        profilePic=(ImageView)navHeaderView.findViewById(R.id.nav_bar_custom_imageView);
        profileEmail=(TextView)navHeaderView.findViewById(R.id.nav_bar_custom_email);
        profileName=(TextView)navHeaderView.findViewById(R.id.nav_bar_custom_name);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
             name = user.getDisplayName();
            profileName.setText(name);

            Toast.makeText(this,"Welcome Back "+name,Toast.LENGTH_SHORT).show();

             email = user.getEmail();
            profileEmail.setText(email);
             photoUrl = user.getPhotoUrl();
            Picasso.with(this).load(photoUrl).into(profilePic);


            // Check if user's email is verified
         //   boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            uid = user.getUid();
        }



    }
/*
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

*/



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory( Intent.CATEGORY_HOME );
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main__activity_with_drawer_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.change_email) {
            //create a fragment that will give the option to change User profile
            Toast.makeText(this, "Change email fragment", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_camera) {
            Toast.makeText(this,"Camera Fragment",Toast.LENGTH_SHORT).show();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

            new AlertDialog.Builder(this)
                    .setTitle("Upload Image")
                    .setMessage("Upload Image to server?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {

                            mImageView.setDrawingCacheEnabled(true);
                            mImageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                            mImageView.layout(0, 0, mImageView.getMeasuredWidth(), mImageView.getMeasuredHeight());
                            mImageView.buildDrawingCache();
                            Bitmap bitmap = Bitmap.createBitmap(mImageView.getDrawingCache());

                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            byte[] data1 = outputStream.toByteArray();
                            UploadTask uploadTask = storageReference.putBytes(data1);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                }
                            });
                        }
                    }).create().show();

        } else if (id == R.id.nav_home) {

            Intent starterIntent=getIntent();
            finish();
            startActivity(starterIntent);

            Toast.makeText(this,"Home",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.ChangeDevice) {
            Toast.makeText(this,"Device ID Fragment",Toast.LENGTH_SHORT).show();
            DeviceFragment deviceFragment=new DeviceFragment();
            FragmentManager manager=getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.android_coordinator_layout,deviceFragment).commit();


        } else if (id == R.id.logout) {
            Toast.makeText(this,"Logged Out",Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user==null) {
                Intent intent = new Intent(this, googleSignIn.class);
                startActivity(intent);
            } else{
                Toast.makeText(this,"Log out Failed",Toast.LENGTH_SHORT).show();
            }


        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! "+name +" is using Arckin, an excellent Voice Controlled Personal Assistant with an android app for remote access");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void initInstances() {
        rootLayoutAndroid = (CoordinatorLayout) findViewById(R.id.android_coordinator_layout);
        collapsingToolbarLayoutAndroid = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_android_layout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);

        }
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            Toast.makeText(this,"Connected, Good to go ! :)",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Disconnected, Please check your Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }
}
