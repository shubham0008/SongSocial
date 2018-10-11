package com.songsocial.songsocial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    Button googleLogin;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        googleLogin=findViewById(R.id.login_button);
        sharedPreferences=getSharedPreferences(Constants.LoginSharedPref.SHARED_PREF_NAME,MODE_PRIVATE);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

    }
    private void signInWithGoogle() {
        if(Constants.gso==null) {
            Log.d(Constants.TAG,"gso new Instance");
            Constants.gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail().requestProfile()
                    .build();
        }
        // Build a GoogleSignInClient with the options specified by gso.
        if(Constants.mGoogleSignInClient==null) {
            Log.d(Constants.TAG,"mGoogleSignInClient new Instance");
            Constants.mGoogleSignInClient = GoogleSignIn.getClient(this, Constants.gso);
        }
        Intent signInIntent = Constants.mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
        
        //Firebase Login Activity in Song Social
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == Constants.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else{
            Toast.makeText(this,"Authentication failed. Please check your network connection",Toast.LENGTH_LONG).show();

        }

    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            Constants.account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(Constants.account);
            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            Toast.makeText(this,"Authentication failed. Please check your network connection",Toast.LENGTH_LONG).show();

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(Constants.TAG, "google signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Log.d(Constants.TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        if(Constants.mAuth==null){
            Log.d(Constants.TAG,"mAuth new Instance");
            Constants.mAuth= FirebaseAuth.getInstance();
        }
        Constants.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Constants.TAG, "signInResult:google success");
//                            Toast.makeText(LoginActivity.this, "you are signed in successfully. ",
//                                    Toast.LENGTH_SHORT).show();
//                            progressBar.setVisibility(View.GONE);
                            Constants.user=Constants.mAuth.getCurrentUser();
//                                Intent intent = new Intent();
//                                setResult(Constants.LOGIN_ACTIVITY_RESULT_CODE);
//                                finish();
                            updateUI(Constants.user);
//                            Intent intent=new Intent();
//                            setResult(Constants.LOGIN_ACTIVITY_RESULT_CODE);
//                            finish();
                        } else {
                            // If sign in fails, display a message to the user.

                            Log.d(Constants.TAG, "signInResult:google with firebase failure", task.getException());
                            Toast.makeText(LoginActivity.this, "An error occured while signing in. Check your network connection ",
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String uid=currentUser.getUid();
            // Check if user's email is verified
            boolean isEmailVerified = currentUser.isEmailVerified();
            Uri photoUrl=currentUser.getPhotoUrl();
            SharedPreferences.Editor editor=sharedPreferences.edit();
            if(!isEmailVerified){
                editor.putBoolean(Constants.LoginSharedPref.IS_EMAIL_VERIFIED,false);
                //Toast.makeText(LoginActivity.this,"Verify email to accesss all the features...",Toast.LENGTH_SHORT).show();
//                isEmailVerified=false;
                //emailVerification.setVisibility(View.VISIBLE);

            }
            else{
                if(photoUrl!=null){
                    Log.d(Constants.TAG," Photo Uri send to db: "+ photoUrl.toString());
                    editor.putString(Constants.LoginSharedPref.PROFILE_URL,photoUrl.toString());
                }
                editor.putBoolean(Constants.LoginSharedPref.LOGGED_IN,true);
                editor.putBoolean(Constants.LoginSharedPref.IS_EMAIL_VERIFIED,true);
                editor.putString(Constants.LoginSharedPref.LOGIN_EMAIL,email);
                editor.putString(Constants.LoginSharedPref.LOGIN_URENAME,name);
                editor.putString(Constants.LoginSharedPref.LOGIN_UID,uid);
                editor.commit();
                Toast.makeText(LoginActivity.this, "Logged in successfully .",
                        Toast.LENGTH_LONG).show();
                Intent intent=new Intent();
                setResult(Constants.LOGIN_ACTIVITY_RESULT_CODE);
                finish();
            }
//            loggedIn=true;
//            logInOrSignUp.setText("Log Out!");
        }else {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(Constants.LoginSharedPref.LOGGED_IN,false);
            editor.putBoolean(Constants.LoginSharedPref.IS_EMAIL_VERIFIED,false);
            editor.putString(Constants.LoginSharedPref.LOGIN_EMAIL,"");
            editor.putString(Constants.LoginSharedPref.LOGIN_URENAME,"");
            editor.putString(Constants.LoginSharedPref.PROFILE_URL,"");
            editor.clear();
            editor.putBoolean(Constants.LoginSharedPref.PREVIOUSLY_STARTED,true);
            Toast.makeText(LoginActivity.this, "An error occured while signing in. Use reset password in case you forgot your password.",
                    Toast.LENGTH_LONG).show();
            editor.commit();
        }
    }
}
