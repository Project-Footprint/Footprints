package com.footprints.footprints.appintro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.footprints.footprints.R;
import com.footprints.footprints.activities.MapsActivity;
import com.footprints.footprints.rest.ApiClient;
import com.footprints.footprints.rest.callbacks.UsersInterface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private SignInButton signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Initializing the Views
        signInButton = findViewById(R.id.google_signin_btn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(Splash.this, gso);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();

        if(mUser!=null){
            Log.d(TAG,"Already Logged In");
            startActivity(new Intent(Splash.this, MapsActivity.class));
            finish();
        }




    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            mUser= mAuth.getCurrentUser();
                            String token = FirebaseInstanceId.getInstance().getToken();
                            String photoUrl = mUser.getPhotoUrl().toString();
                            //SharedPreferenceController.saveUserInfo(Splash.this,mUser.getUid(),mUser.getDisplayName(),mUser.getEmail(),photoUrl,token);

                            // Now Store the data in the mysql Database
                            UsersInterface UsersInterface = ApiClient.getApiClient().create(UsersInterface.class);
                            Call<Integer> call = UsersInterface.singIn(new UserInfoClass(mUser.getUid(),mUser.getDisplayName(),mUser.getEmail(),photoUrl,"default/cover",token));
                            call.enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(@NonNull Call<Integer> call, Response<Integer> response) {
                                    if(response.body()==1){

                                        Toast.makeText(Splash.this,"Login Successful ",Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(Splash.this, MapsActivity.class));
                                    }else{

                                        FirebaseAuth.getInstance().signOut();
                                        Toast.makeText(Splash.this,"Login Failed ",Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {
                                    Log.w(TAG, "Google sign in failed testt"+ t.getMessage());
                                    Toast.makeText(Splash.this,"Login Failed... Please Retry !",Toast.LENGTH_LONG).show();
                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Google sign in failed"+ task.toString());
                            Toast.makeText(Splash.this,"Login Failed... Please Retry !",Toast.LENGTH_LONG).show();

                        }


                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public class UserInfoClass{
        String uId;
        String name;
        String email;
        String profileUrl;
        String coverUrl;
        String userToken;

         UserInfoClass(String uId, String name, String email, String profileUrl, String coverUrl, String userToken) {
            this.uId = uId;
            this.name = name;
            this.email = email;
            this.profileUrl = profileUrl;
            this.coverUrl = coverUrl;
            this.userToken = userToken;
        }
    }
}
