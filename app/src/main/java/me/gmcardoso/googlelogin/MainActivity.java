package me.gmcardoso.googlelogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    // Components used in the login screen
    private LinearLayout profileSection;
    private Button signOut;
    private SignInButton signIn;
    private TextView userName, userEmail;
    private ImageView profilePicture;
    private GoogleApiClient googleApiClient;

    // This request code is used to know the result when the activity is finished
    private static final int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Binding the components
        profileSection = (LinearLayout) findViewById(R.id.profile_section);
        signIn = (SignInButton) findViewById(R.id.btn_login);
        signOut = (Button) findViewById(R.id.btn_logout);
        userName = (TextView) findViewById(R.id.tv_user_name);
        userEmail = (TextView) findViewById(R.id.tv_user_email);
        profilePicture = (ImageView) findViewById(R.id.iv_profile_picture);

        // We don't need to create a listener to the buttons, because the MainActivity implements it for us
        signIn.setOnClickListener(this);
        signOut.setOnClickListener(this);

        // Hides the profile section when the user is not loggedin yet
        profileSection.setVisibility(View.GONE);

        // Starts the google api with the default options
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
    }

    // Implements the Click Listener to every button in the screen/activity
    @Override
    public void onClick(View v) {
        // used to know which button was clicked
        switch (v.getId()) {
            case R.id.btn_login:
                signIn();
                break;

            case R.id.btn_logout:
                signOut();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Here you can do whatever you want when the connection fails...
    }

    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }

    // When the result of the request is 'success' you can get the values of the logged account and show it in the screen
    private void handleResult(GoogleSignInResult result) {
        if(result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String image_url = account.getPhotoUrl().toString();

            userName.setText(name);
            userEmail.setText(email);
            //Glide is a lib to show images in your app based on urls
            Glide.with(this).load(image_url).into(profilePicture);
            updateUI(true);
        } else {
            updateUI(false);
        }
    }

    // Show or hide the user profile or the Login Button
    private void updateUI(boolean isLogged) {
        if(isLogged) {
            profileSection.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.GONE);
        } else {
            profileSection.setVisibility(View.GONE);
            signIn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the request is the Google Login request (you check it using the request code created before)
        if(requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }
}
