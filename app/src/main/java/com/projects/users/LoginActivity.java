package com.projects.users;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.application.GlobalApplication;
import com.config.Config;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.xizzhu.simpletooltip.ToolTip;
import com.github.xizzhu.simpletooltip.ToolTipView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.dataparser.DataParser;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.Status;
import com.models.User;
import com.projects.whatscamp.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private MGAsyncTask task;
    private CallbackManager mCallbackManager;
    String _imageURL;
    String _name;
    GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnFacebook = (Button) findViewById(R.id.btnFacebook);
        btnFacebook.setOnClickListener(this);

        Button btnGooglePlus = (Button) findViewById(R.id.btnGooglePlus);
        btnGooglePlus.setOnClickListener(this);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("LoginManager", "Login Success");
                        getUserProfile(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Log.d("LoginManager", "Login Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("LoginManager", exception.getMessage());
                    }
                });

        GlobalApplication app = (GlobalApplication) getApplication();
        mGoogleApiClient = app.getGoogleApiClientInstance();

        btnFacebook.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showToolTip(v,"Login with your Facebook Account");
                return true;
            }
        });
        btnGooglePlus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showToolTip(v,"Login with your Google+ Account");
                return true;
            }
        });
    }

    private void getUserProfile(LoginResult loginResult) {
        String accessToken = loginResult.getAccessToken().getToken();
        Log.i("accessToken", accessToken);
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        syncFacebookUser(object, response);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnFacebook:
                loginToFacebook();
                break;
            case R.id.btnGooglePlus:
                loginToGooglePlus();
                break;
        }
    }

    public void loginToFacebook() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(
                this, Arrays.asList("basic_info", "email", "public_profile", "user_birthday"));
    }

    public void updateLogin(DataResponse response, String imageURL, String name) {

        if(response == null) {
            MGUtilities.showAlertView(
                    LoginActivity.this,
                    R.string.login_error,
                    R.string.problems_encountered_login);
            return;
        }

        Status status = response.getStatus();
        if(response != null && status != null) {
            if(status.getStatus_code() == -1 && response.getUser_info() != null ) {
                User user = response.getUser_info();
                if(user != null) {
                    UserAccessSession session = UserAccessSession.getInstance(this);
                    UserSession userSession = new UserSession();
                    userSession.setEmail(user.getEmail());
                    userSession.setFacebook_id(user.getFacebook_id());
                    userSession.setGoogle_id(user.getGoogle_id());
                    userSession.setFull_name(user.getFull_name());
                    userSession.setLogin_hash(user.getLogin_hash());
                    userSession.setUser_id(user.getUser_id());
                    userSession.setUsername(user.getUsername());
                    userSession.setThumb_url(user.getThumb_url());

                    session.storeUserSession(userSession);
                    finish();
                }
                else {
                    MGUtilities.showAlertView(LoginActivity.this, R.string.login_error, R.string.problems_encountered_login);
                }
            }
            else {
                MGUtilities.showAlertView(LoginActivity.this, R.string.login_error, status.getStatus_text());
            }
        }
    }



    // FACEBOOK
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart()  {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        // if nav drawer is opened, hide the action items
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroy()  {
        super.onDestroy();
        if(task != null)
            task.cancel(true);
    }

    public void syncFacebookUser(final JSONObject object, final GraphResponse response) {

        Log.i("syncFacebookUser", response.toString());
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        try {
            String id = object.getString("id");
            String imageURL = "http://graph.facebook.com/" + id + "/picture?type=large";
            _imageURL = imageURL;
            try {
                URL profile_pic = new URL(imageURL);
                Log.i("profile_pic", profile_pic + "");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String name = object.getString("name");
            _name = name;

            String email = object.getString("email");
            params.add(new BasicNameValuePair("facebook_id", id));
            params.add(new BasicNameValuePair("full_name", name));
            params.add(new BasicNameValuePair("thumb_url", imageURL ));
            params.add(new BasicNameValuePair("email", email ));
            params.add(new BasicNameValuePair("api_key", Config.API_KEY ));
            Log.e("FB IMAGE URL", imageURL);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        task = new MGAsyncTask(LoginActivity.this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            DataResponse response;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(
                        MGUtilities.getStringFromResource(LoginActivity.this, R.string.logging_in) );
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                updateLogin(response, _imageURL, _name);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                response = DataParser.getJSONFromUrlWithPostRequest(Config.REGISTER_URL, params);
            }
        });
        task.execute();
    }

    public void loginToGooglePlus() {
        if(!MGUtilities.hasConnection(this)) {
            MGUtilities.showAlertView(
                    LoginActivity.this,
                    R.string.network_error,
                    R.string.no_network_connection);
            return;
        }
        signIn();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Config.RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("GOOGLEPLUS", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
//            updateUI(true);

            String email = acct.getEmail() != null ? acct.getEmail() : "";
            String thumbUrl = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "";

            final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("google_id", acct.getId()));
            params.add(new BasicNameValuePair("full_name", acct.getDisplayName()));
            params.add(new BasicNameValuePair("thumb_url", thumbUrl ));
            params.add(new BasicNameValuePair("email",  email));
            params.add(new BasicNameValuePair("api_key", Config.API_KEY ));

            task = new MGAsyncTask(LoginActivity.this);
            task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

                DataResponse response;

                @Override
                public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

                @Override
                public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                    asyncTask.dialog.setMessage(
                            MGUtilities.getStringFromResource(LoginActivity.this, R.string.logging_in) );
                }

                @Override
                public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                    // TODO Auto-generated method stub
                    updateLogin(response, _imageURL, _name);
                }

                @Override
                public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                    // TODO Auto-generated method stub
                    response = DataParser.getJSONFromUrlWithPostRequest(Config.REGISTER_URL, params);
                }
            });
            task.execute();
        }
    }


    private void showToolTip(View v, String text)
    {
        ToolTip toolTip = new ToolTip.Builder().withText(text).withTextSize(50). withPadding(10,10,10,10)
                .build();

        ToolTipView toolTipView = new ToolTipView.Builder(this)
                .withAnchor(v)
                .withToolTip(toolTip)
                .withGravity(Gravity.BOTTOM)

                .build();
        toolTipView.show();
    }

}
