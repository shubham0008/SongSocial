package com.songsocial.songsocial;

import android.widget.ScrollView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {
    public static String TAG="myLogTag";
    public static final int LOGIN_ACTIVITY_REQUEST_CODE=1;
    public static final int LOGIN_ACTIVITY_RESULT_CODE=2;
    public static final int RC_SIGN_IN=3;
    public static FirebaseAuth mAuth;
    public static FirebaseUser user;
    public static GoogleSignInClient mGoogleSignInClient;
    public static GoogleSignInOptions gso;
    public static GoogleSignInAccount account;
    public static FirebaseDatabase database;
    public static DatabaseReference myRef;

    public class LoginSharedPref{
        public final static String SHARED_PREF_NAME="loginInfo";
        public final static String PREVIOUSLY_STARTED="previouslyStarted";
        public final static String LOGIN_EMAIL="loginEmail";
        public final static String LOGIN_URENAME="loginUsername";
        public final static String PROFILE_URL="loginProfileUrl";
        public final static String LOGGED_IN="is_logged_in";
        //  public final static String LOGGED_IN_WITH_GOOGLE="is_logged_in_with_google";
        public final static String IS_EMAIL_VERIFIED="isEmailVerified";
        //        public final static String FIREBASE_NAME="firebaseNAME";
        public final static String LOGIN_UID="loginUid";

    }

}
