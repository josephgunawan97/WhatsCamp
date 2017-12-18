package com.projects.events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.application.GlobalApplication;
import com.config.Config;
import com.db.Queries;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.asynctask.MGAsyncTask.OnMGAsyncTaskListener;
import com.libraries.bitmap.MGImageUtils;
import com.libraries.dataparser.DataParser;
import com.libraries.directories.Directory;
import com.libraries.helpers.DateTimeHelper;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.Event;
import com.models.EventCategory;
import com.projects.activities.CategorySearchActivity;
import com.projects.whatscamp.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventEditActivity extends AppCompatActivity implements OnClickListener, OnMapReadyCallback {

    private Queries q;
    private ArrayList<EventCategory> selectedCategories = null;
    private ArrayList<NameValuePair> params;
    private GoogleMap googleMap;
    private boolean isMapPinAdded = false;
    private Location myLocation = null;
    private LatLng latLng = null;

    private static final int CAMERA_PIC_REQUEST_DTR = 88;
    String pfTime = "";
    private Directory dir;
    private String path = "";
    private final int RESULT_LOAD_IMAGE = 1;
    private String dtrImagePath = "";
    private Uri uris;
    private String urisList;
    private double lat, lon;
    private TextView tvCategory;
    private boolean showMap = true;
    private Event event;
    private Calendar calendarSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_add_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.edit_event);

        q = GlobalApplication.getQueriesInstance(this);

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

        selectedCategories = new ArrayList<EventCategory>();
        params = new ArrayList<NameValuePair>();
        event = (Event) this.getIntent().getSerializableExtra("event");
        calendarSelected = Calendar.getInstance(TimeZone.getDefault());

        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        btnSend.setText(R.string.save);

        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);

        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvCategory.setOnClickListener(this);

        ImageView imgViewCamera = (ImageView) findViewById(R.id.imgViewCamera);
        imgViewCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                captureDTR();
            }
        });

        ImageView imgViewGallery = (ImageView) findViewById(R.id.imgViewGallery);
        imgViewGallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });

        TextView tvEventDateTime = (TextView) findViewById(R.id.tvEventDateTime);
        tvEventDateTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        updateEventData();

        Handler h = new Handler();
        h.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                finalizeMapRenderer();
            }
        }, Config.DELAY_SHOW_ANIMATION + 500);

        GlobalApplication app = (GlobalApplication)getApplication();
        app.sendScreenView(Config.kGAIScreenNameEventEdit);
    }

    private void updateEventData() {

        Date date = DateTimeHelper.formatDateFromStringToDate("yyyy-MM-dd hh:mm:ss", event.getGmt_date_set());
        if(date != null)
            calendarSelected.setTime(date);

        EditText txtEventAddress = (EditText) findViewById(R.id.txtEventAddress);
        EditText txtEventContactNo = (EditText) findViewById(R.id.txtEventContactNo);
        TextView tvEventDateTime = (TextView) findViewById(R.id.tvEventDateTime);
        final EditText txtEventDesc = (EditText) findViewById(R.id.txtEventDesc);
        EditText txtEventEmailAddress = (EditText) findViewById(R.id.txtEventEmailAddress);
        EditText txtEventName = (EditText) findViewById(R.id.txtEventName);
        EditText txtEventTicketURL = (EditText) findViewById(R.id.txtEventTicketURL);

        txtEventAddress.setText(event.getAddress());
        txtEventContactNo.setText(event.getContact_no());

        String dateStr = DateTimeHelper.formateDateFromString("yyyy-MM-dd hh:mm:ss", "EEE, MMM dd, yyyy hh:mm a", event.getGmt_date_set());
        tvEventDateTime.setText(dateStr);

        txtEventDesc.setText(event.getEvent_desc());
        txtEventEmailAddress.setText(event.getEmail_address());
        txtEventName.setText(event.getTitle());
        txtEventTicketURL.setText(event.getTicket_url());


        final TextView tvMaxCharCount = (TextView) findViewById(R.id.tvMaxCharCount);
        String charsLeft = String.format("%d", Config.MAX_CHARS_DESC);
        tvMaxCharCount.setText(charsLeft);

        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if(source.length() >= Config.MAX_CHARS_DESC)
                    return "";

                updateDesc();
                return source;
            }
        };

        txtEventDesc.setFilters(new InputFilter[] { filter } );
        updateDesc();

        if(event.getPhoto_url() != null) {
            ImageView imgViewThumb = (ImageView) findViewById(R.id.imgViewThumb);
            GlobalApplication.getImageLoaderInstance(this)
                    .displayImage(
                            event.getPhoto_url(),
                            imgViewThumb,
                            GlobalApplication.getDisplayImageOptionsInstance());
        }

        Thread t = new Thread() {
            public void run() {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    EventCategory[] categories = mapper.readValue(event.getCategories(), EventCategory[].class);
                    if(categories != null && categories.length > 0 ) {
                        for(EventCategory cat : categories)
                            selectedCategories.add(cat);

                        formatCategories();
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private void updateDesc() {
        final EditText txtEventDesc = (EditText) findViewById(R.id.txtEventDesc);
        final TextView tvMaxCharCount = (TextView) findViewById(R.id.tvMaxCharCount);

        String charsLeft = String.format("%d", Config.MAX_CHARS_DESC - txtEventDesc.getText().toString().length());
        tvMaxCharCount.setText(charsLeft);
    }

    private void finalizeMapRenderer() {
        FragmentManager fManager = getSupportFragmentManager();
        SupportMapFragment supportMapFragment = ((SupportMapFragment) fManager.findFragmentById(R.id.googleMap));
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap _googleMap) {
        googleMap = _googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) { }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // TODO Auto-generated method stub
                latLng = marker.getPosition();
                lat = latLng.latitude;
                lon = latLng.longitude;
            }

            @Override
            public void onMarkerDrag(Marker marker) { }
        });

        if(event.getLat() != 0 && event.getLon() != 0) {
            myLocation = new Location("");
            myLocation.setLatitude(event.getLat());
            myLocation.setLongitude(event.getLon());
            createMarker(myLocation);
        }
        else if(GlobalApplication.currentLocation != null) {
            myLocation = GlobalApplication.currentLocation;
            createMarker(myLocation);
        }
        else {
            myLocation = new Location("");
            myLocation.setLatitude(Config.DEFAULT_LATITUDE_TO_SHOW);
            myLocation.setLongitude(Config.DEFAULT_LONGITUDE_TO_SHOW);
            createMarker(myLocation);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.menuHideMap:
                toggleMap();
                return true;
            case R.id.menuShowMap:
                toggleMap();
                return true;
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleMap() {
        LinearLayout linear = (LinearLayout) findViewById(R.id.linearMap);
        if(showMap) {
            linear.setVisibility(View.GONE);
        }
        else {
            linear.setVisibility(View.VISIBLE);
        }
        showMap = !showMap;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        if(showMap)
            getMenuInflater().inflate(R.menu.menu_hide_map, menu);
        else
            getMenuInflater().inflate(R.menu.menu_show_map, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        // if nav drawer is opened, hide the action items
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private Marker createMarker(Location loc) {
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title( MGUtilities.getStringFromResource(this, R.string.event_location) );
        markerOptions.snippet( MGUtilities.getStringFromResource(this, R.string.drag_to_move_around));

        markerOptions.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin));
        markerOptions.draggable(true);

        Marker mark = googleMap.addMarker(markerOptions);
        mark.setInfoWindowAnchor(Config.MAP_INFO_WINDOW_X_OFFSET, 0);

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(Config.MAP_ZOOM_LEVEL);
        googleMap.moveCamera(zoom);

        CameraUpdate center = CameraUpdateFactory.newLatLng( new LatLng(loc.getLatitude(), loc.getLongitude()));
        googleMap.animateCamera(center);

        lat = loc.getLatitude();
        lon = loc.getLongitude();
        return mark;
    }

    private void captureDTR() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pfTime = String.valueOf (System.currentTimeMillis());
        dtrImagePath = String.format("%s.jpg", pfTime);
        //Grant permission to the camera activity to write the photo.
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //saving if there is EXTRA_OUTPUT
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(dir.getPFCameraTakenPath(), dtrImagePath)));
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST_DTR);
    }

    private void getPicture() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("requestCode", ""+requestCode);
        Log.e("resultCode", ""+resultCode);
        if (requestCode == Config.RESULT_CODE_CATEGORY && resultCode == RESULT_OK) {
            selectedCategories = (ArrayList<EventCategory>) data.getSerializableExtra("categories");
            formatCategories();
        }
        if (requestCode == CAMERA_PIC_REQUEST_DTR && resultCode == RESULT_OK) {

            String newPath = String.format("%s%s", dir.getPFCameraTakenPath(), dtrImagePath);
            File f = new File(newPath);
            Uri uriFile = Uri.fromFile(f);
            uris = uriFile;
            urisList = newPath;
            setImage();
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            uris = selectedImage;
            urisList = MGImageUtils.getRealPathFromURI(selectedImage, this);
            setImage();
        }
    }

    private void setImage() {
        ImageView imgViewThumb = (ImageView) findViewById(R.id.imgViewThumb);
        imgViewThumb.setImageBitmap(null);
        imgViewThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uris);
            imgViewThumb.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void formatCategories() {
        if(selectedCategories != null) {
            String cats = "";
            for(int x = 0; x < selectedCategories.size(); x++) {
                EventCategory cat = selectedCategories.get(x);
                cats += cat.getCategory();

                if(x < selectedCategories.size() - 1)
                    cats += ", ";

            }
            tvCategory.setText(cats);
        }
    }

    private void formatData() {
        params.clear();
        if(lat == 0 && lon == 0) {
            MGUtilities.showAlertView(this, R.string.location_error, R.string.location_error_not_selected);
            return;
        }

        EditText txtEventAddress = (EditText) findViewById(R.id.txtEventAddress);
        String address = txtEventAddress.getText().toString().trim();

        EditText txtEventContactNo = (EditText) findViewById(R.id.txtEventContactNo);
        String contactNo = txtEventContactNo.getText().toString().trim();

        String dateTime = DateTimeHelper.formatDateFromDate("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm:ss", calendarSelected.getTime());

        EditText txtEventDesc = (EditText) findViewById(R.id.txtEventDesc);
        String eventDesc = txtEventDesc.getText().toString().trim();

        EditText txtEventEmailAddress = (EditText) findViewById(R.id.txtEventEmailAddress);
        String emailAddress = txtEventEmailAddress.getText().toString();

        EditText txtEventName = (EditText) findViewById(R.id.txtEventName);
        String eventName = txtEventName.getText().toString().trim();

        EditText txtEventTicketURL = (EditText) findViewById(R.id.txtEventTicketURL);
        String ticketURL = txtEventTicketURL.getText().toString().trim();

        String cats = "";
        for(int x = 0; x < selectedCategories.size(); x++) {
            EventCategory cat = selectedCategories.get(x);
            cats += String.valueOf(cat.getCategory_id());
            if (x < selectedCategories.size() - 1)
                cats += ",";
        }

        if(calendarSelected.before(Calendar.getInstance(Locale.getDefault()))) {
            MGUtilities.showAlertView(this, R.string.error_title_event_datetime, R.string.error_title_event_datetime_details);
            return;
        }

        if(eventName.length() == 0) {
            MGUtilities.showAlertView(this, R.string.error_title_event_name, R.string.error_title_event_name_details);
            return;
        }

        if(address.length() == 0) {
            MGUtilities.showAlertView(this, R.string.error_title_event_address, R.string.error_title_event_address_details);
            return;
        }

        if(emailAddress.length() == 0) {
            MGUtilities.showAlertView(this, R.string.error_title_event_email, R.string.error_title_event_email_details);
            return;
        }

        if(contactNo.length() == 0) {
            MGUtilities.showAlertView(this, R.string.error_title_event_contact_no, R.string.error_title_event_contact_no_details);
            return;
        }

        params.add(new BasicNameValuePair("address", MGUtilities.filterInvalidChars(address)) );
        params.add(new BasicNameValuePair("category_ids", cats));
        params.add(new BasicNameValuePair("contact_no", MGUtilities.filterInvalidChars(contactNo)) );
        params.add(new BasicNameValuePair("gmt_date_set", dateTime));
        params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
        params.add(new BasicNameValuePair("lon", String.valueOf(lon)));
        params.add(new BasicNameValuePair("event_desc", MGUtilities.filterInvalidChars(eventDesc)) );
        params.add(new BasicNameValuePair("email_address", MGUtilities.filterInvalidChars(emailAddress)) );
        params.add(new BasicNameValuePair("title", MGUtilities.filterInvalidChars(eventName)) );
        params.add(new BasicNameValuePair("ticket_url", MGUtilities.filterInvalidChars(ticketURL)) );
        params.add(new BasicNameValuePair("photo_url", ""));
        params.add(new BasicNameValuePair("api_key", Config.API_KEY));
        params.add(new BasicNameValuePair("event_id", String.valueOf(event.getEvent_id())));

        UserAccessSession userAccess = UserAccessSession.getInstance(this);
        UserSession userSession = userAccess.getUserSession();
        params.add(new BasicNameValuePair("user_id", String.valueOf(userSession.getUser_id())));
        params.add(new BasicNameValuePair("login_hash", String.valueOf(userSession.getLogin_hash())));
        syncToServer();
    }

    private void syncToServer() {

        MGAsyncTask task = new MGAsyncTask(this);
        task.setMGAsyncTaskListener(new OnMGAsyncTaskListener() {

            DataResponse data;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                String msg = MGUtilities.getStringFromResource(getApplicationContext(), R.string.syncing_data_to_server);
                asyncTask.dialog.setMessage(msg);
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                asyncTask.cancel(true);
                if(data != null) {
                    Toast.makeText(EventEditActivity.this, R.string.event_updated, Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
                else {
                    Toast.makeText(EventEditActivity.this, R.string.problems_encountered, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                data = syncDataToServer();
            }
        });
        task.execute();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()) {
            case R.id.btnSend:
                if(!MGUtilities.hasConnection(this)) {
                    MGUtilities.showAlertView(
                            this,
                            R.string.network_error,
                            R.string.no_network_connection);
                    return;
                }
                formatData();
                break;
            case R.id.btnDelete:
                if(!MGUtilities.hasConnection(this)) {
                    MGUtilities.showAlertView(
                            this,
                            R.string.network_error,
                            R.string.no_network_connection);
                    return;
                }
                deleteEvent();
                break;
            case R.id.tvCategory:
                Intent i = new Intent(this, CategorySearchActivity.class);
                startActivityForResult(i, Config.RESULT_CODE_CATEGORY);
                break;
        }
    }

    private DataResponse syncDataToServer() {
        try {
            File f = null;
            if(urisList != null)
                f = new File(urisList);

            DataResponse data = DataParser.uploadFileWithParams(Config.INSERT_NEW_EVENT_URL, params, f);
            if(data.getEvent() != null) {
                q.insertEvent(data.getEvent());
            }
            return data;
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void deleteEvent() {
        final ArrayList<NameValuePair> params1 = new ArrayList<NameValuePair>();
        params1.add(new BasicNameValuePair("is_deleted", "1"));
        params1.add(new BasicNameValuePair("api_key", Config.API_KEY));
        params1.add(new BasicNameValuePair("event_id", String.valueOf(event.getEvent_id())));

        UserAccessSession userAccess = UserAccessSession.getInstance(this);
        UserSession userSession = userAccess.getUserSession();
        params1.add(new BasicNameValuePair("user_id", String.valueOf(userSession.getUser_id())));
        params1.add(new BasicNameValuePair("login_hash", String.valueOf(userSession.getLogin_hash())));

        MGAsyncTask task = new MGAsyncTask(this);
        task.setMGAsyncTaskListener(new OnMGAsyncTaskListener() {

            DataResponse data;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                String msg = MGUtilities.getStringFromResource(getApplicationContext(), R.string.deleting_event);
                asyncTask.dialog.setMessage(msg);
            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                asyncTask.cancel(true);
                if(data != null && data.getStatus() != null && data.getStatus().getStatus_code() == Config.STATUS_SUCCESS) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("is_deleted", true);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
                else {
                    Toast.makeText(EventEditActivity.this, R.string.problems_encountered, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                data = DataParser.uploadFileWithParams(Config.DELETE_EVENT_JSON_URL, params1, null);
            }
        });
        task.execute();
    }

    final TimePickerDialog.OnTimeSetListener timePicker = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendarSelected.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendarSelected.set(Calendar.MINUTE, minute);
            updateDateTimeLabel();
        }
    };

    final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            calendarSelected.set(Calendar.YEAR, year);
            calendarSelected.set(Calendar.MONTH, monthOfYear);
            calendarSelected.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            showTimeDialog();
        }
    };

    private void showDateDialog() {
        new DatePickerDialog(
                this,
                datePicker,
                calendarSelected.get(Calendar.YEAR),
                calendarSelected.get(Calendar.MONTH),
                calendarSelected.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimeDialog() {
        new TimePickerDialog(
                this,
                timePicker,
                calendarSelected.get(Calendar.HOUR_OF_DAY),
                calendarSelected.get(Calendar.MINUTE),
                true).show();
    }

    private void updateDateTimeLabel() {
        TextView tvEventDateTime = (TextView) findViewById(R.id.tvEventDateTime);
        String dateStr = DateTimeHelper.formatDateFromDate("yyyy-MM-dd hh:mm:ss", "EEE, MMM dd, yyyy hh:mm a", calendarSelected.getTime());
        tvEventDateTime.setText(dateStr);
    }
}
