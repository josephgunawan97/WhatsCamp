package com.projects.users;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.application.GlobalApplication;
import com.config.Config;
import com.config.UIConfig;
import com.facebook.login.LoginManager;
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.bitmap.MGImageUtils;
import com.libraries.dataparser.DataParser;
import com.libraries.directories.Directory;
import com.libraries.imageview.RoundedImageView;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.Status;
import com.models.User;
import com.projects.whatscamp.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import twitter4j.auth.AccessToken;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private MGAsyncTask task;
    private Directory dir;
    private String path = "";
    Uri uri;
    String uriStr;
    private final int RESULT_LOAD_IMAGE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.update_profile);

        path = Environment.getExternalStorageDirectory() + "/" + Config.SD_CARD_PATH;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = new Directory(path);
            if(!dir.isExist()){
                dir.createDir();
                dir.createSubDirCameraTaken();
                dir.createSubDirData();
            }
        }

        Button btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        RoundedImageView imgViewThumb = (RoundedImageView) findViewById(R.id.imgViewThumb);
        imgViewThumb.setCornerRadius(R.dimen.corner_radius_profile_50dp);
        imgViewThumb.setOnClickListener(this);
        imgViewThumb.setBorderWidth(UIConfig.BORDER_WIDTH);
        imgViewThumb.setBorderColor(getResources().getColor(R.color.colorAccent));

        UserSession userSession = UserAccessSession.getInstance(this).getUserSession();

        Spanned fullName = Html.fromHtml(userSession.getFull_name());

        EditText txtFullName = (EditText) findViewById(R.id.txtFullName);
        txtFullName.setText(fullName);

        if(userSession.getThumb_url() != null) {
            GlobalApplication.getImageLoaderInstance(this)
                    .displayImage(
                            userSession.getThumb_url(),
                            imgViewThumb,
                            GlobalApplication.getDisplayImageOptionsThumbInstance());
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnUpdate:
                login();
                break;
            case R.id.imgViewThumb:
                getPicture();
                break;
            case R.id.btnLogout:
                showLogoutAlertDialog();
                break;
        }
    }

    private void getPicture() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void login() {
        EditText txtFullName = (EditText) findViewById(R.id.txtFullName);
        String fullName = txtFullName.getText().toString();

        if(fullName.isEmpty()) {
            MGUtilities.showAlertView(
                    this,
                    R.string.empty_fields,
                    R.string.empty_fields_register);
            return;
        }

        if(!MGUtilities.hasConnection(ProfileActivity.this)) {
            MGUtilities.showAlertView(
                    ProfileActivity.this,
                    R.string.network_error,
                    R.string.no_network_connection);
            return;
        }

        task = new MGAsyncTask(ProfileActivity.this);
        task.setMGAsyncTaskListener(new MGAsyncTask.OnMGAsyncTaskListener() {

            DataResponse response;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                asyncTask.dialog.setMessage(MGUtilities.getStringFromResource(ProfileActivity.this, R.string.updating_profile));
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                updateRegistration(response);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                response = syncDataRegistration();
            }
        });
        task.execute();
    }

    public DataResponse syncDataRegistration() {
        EditText txtFullName = (EditText) findViewById(R.id.txtFullName);

        String fullName = Html.toHtml(new SpannableString(txtFullName.getText().toString()));
        fullName = MGUtilities.filterInvalidChars(fullName);

        UserSession userSession = UserAccessSession.getInstance(this).getUserSession();
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("full_name", fullName ));
        params.add(new BasicNameValuePair("user_id", String.valueOf(userSession.getUser_id()) ));
        params.add(new BasicNameValuePair("login_hash", String.valueOf(userSession.getLogin_hash()) ));
        params.add(new BasicNameValuePair("api_key", Config.API_KEY ));

        File f = null;
        if(uriStr != null)
            f = new File(uriStr);

        DataResponse response = DataParser.uploadFileWithParams(Config.UPDATE_USER_PROFILE_URL, params, f);
        return response;
    }

    public void updateRegistration(DataResponse response) {
        if(response == null) {
            MGUtilities.showAlertView(
                    ProfileActivity.this,
                    R.string.login_error,
                    R.string.problems_encountered_login);
            return;
        }

        Status status = response.getStatus();
        if(response != null && status != null) {
            if(status.getStatus_code() == -1 && response.getUser_info() != null ) {
                User user = response.getUser_info();
                UserAccessSession session = UserAccessSession.getInstance(this);
                UserSession userSession = new UserSession();
                userSession.setEmail(user.getEmail());
                userSession.setFacebook_id(user.getFacebook_id());
                userSession.setGoogle_id(user.getGoogle_id());
                userSession.setFull_name(user.getFull_name());
                userSession.setUser_id(user.getUser_id());
                userSession.setUsername(user.getUsername());
                userSession.setThumb_url(user.getThumb_url());
                session.storeUserSession(userSession);
                finish();
            }
            else {
                MGUtilities.showAlertView(ProfileActivity.this, R.string.network_error, status.getStatus_text());
            }
        }
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
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            uri = data.getData();
            uriStr = MGImageUtils.getRealPathFromURI(uri, this);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imgViewThumb = (ImageView) this.findViewById(R.id.imgViewThumb);
                imgViewThumb.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getPathFromCameraData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
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

    private void showLogoutAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(this.getResources().getString(R.string.alert_logout_user_title));
        alert.setMessage(this.getResources().getString(R.string.alert_logout_user_title_details));
        alert.setPositiveButton(this.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        logoutUser();
                    }
                });
        alert.setNegativeButton(this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        alert.create();
        alert.show();
    }

    private void logoutUser() {
        UserAccessSession accessSession = UserAccessSession.getInstance(this);
        if(accessSession != null)
            accessSession.clearUserSession();

        LoginManager.getInstance().logOut();

        finish();
    }
}
