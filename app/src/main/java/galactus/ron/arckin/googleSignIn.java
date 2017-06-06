package galactus.ron.arckin;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.FileOutputStream;
import java.io.IOException;

public class googleSignIn extends AppCompatActivity {

    public EditText deviceIdEditText;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    public String deviceID;
    private FirebaseAuth mAuth;
    private static final String TAG= "random";
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference root=database.getReference("Devices");
    String filename="DeviceID.txt",userid,UserName,UserEmail;
    Uri ProfilePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        ImageView imageView=(ImageView)findViewById(R.id.img_logo);
        ViewCompat.animate(imageView)
                .translationY(-250)
                .setStartDelay(500)
                .setDuration(1000).setInterpolator(
                new DecelerateInterpolator(1.2f)).start();

        SignInButton googleSignInButton = (SignInButton) findViewById(R.id.googleSignInButton);
        deviceIdEditText= (EditText) findViewById(R.id.deviceIDEditText);
        deviceID="null";

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        if (googleSignInButton != null) {
            googleSignInButton.startAnimation(myAnim);
        }
        TextView appname = (TextView) findViewById(R.id.nameTextView);

        final Animation textAnim =AnimationUtils.loadAnimation(this,R.anim.blink);
        if (appname != null) {
            appname.startAnimation(textAnim);
        }

        mAuth= FirebaseAuth.getInstance();
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        userid= user.getUid();
                        UserEmail= user.getEmail();
                        UserName= user.getDisplayName();
                        ProfilePicture=user.getPhotoUrl();
                    }
                    startActivity(new Intent(googleSignIn.this, MainActivity.class).putExtra("message", deviceID).putExtra("UserId",userid).putExtra("DisplayName",UserName).putExtra("EmailId",UserEmail).putExtra("ProfilePicture",ProfilePicture.toString()));
                }
            }
        };
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
        @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
            Toast.makeText(googleSignIn.this,"Error Sign In",Toast.LENGTH_LONG).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        if (googleSignInButton != null) {
            googleSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signIn() {
        deviceID=deviceIdEditText.getText().toString().trim();
        if(deviceID.equals(""))
        {
            deviceID="null";
        }
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(deviceID))
                {
                    Toast.makeText(getApplicationContext(),"Device Exists",Toast.LENGTH_SHORT).show();
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    signInIntent.putExtra("message", deviceID);
                    String value = deviceIdEditText.getText().toString();

                    try {
                        FileOutputStream fileOutputStream=openFileOutput(filename,MODE_PRIVATE);
                        fileOutputStream.write(value.getBytes());
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
                else if(deviceID.equals("null"))
                {
                    Toast.makeText(getApplicationContext(),"blank ID",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Device Doesn't exits",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                deviceID=deviceIdEditText.getText().toString();
                Intent main= new Intent(this, MainActivity.class);
                main.putExtra("message", deviceID);
                String value = deviceIdEditText.getText().toString();
                try {
                    FileOutputStream fileOutputStream=openFileOutput(filename,MODE_PRIVATE);
                    fileOutputStream.write(value.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivity(main);
            } else {
                Toast.makeText(this,"Google Sign In Failed",Toast.LENGTH_LONG).show();
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(googleSignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }
}
