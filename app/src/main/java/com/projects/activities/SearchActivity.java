package com.projects.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.application.GlobalApplication;
import com.config.Config;
import com.db.Queries;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.dataparser.DataParser;
import com.libraries.location.MGLocationManagerUtils;
import com.libraries.utilities.MGUtilities;
import com.models.EventCategory;
import com.models.Event;
import com.models.DataResponse;
import com.models.Status;
import com.projects.whatscamp.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by mg on 19/07/16.
 */
public class SearchActivity extends AppCompatActivity implements GlobalApplication.OnLocationListener{

    private MGAsyncTaskNoDialog task;
    SeekBar seekBarRadius;
    SwipeRefreshLayout swipeRefresh;
    Queries q;
    Button btnSearch;
    ArrayList<EventCategory> selectedCategories;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_list_swipe_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.search);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            swipeRefresh.setProgressViewOffset(false, 0,100);
        }

        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        showRefresh(false);
        q = GlobalApplication.getQueriesInstance(this);

        seekBarRadius = (SeekBar) findViewById(R.id.seekBarRadius);
        seekBarRadius.setMax(Config.SEARCH_RADIUS_MAX_VALUE);
        seekBarRadius.setProgress(Config.SEARCH_RADIUS_DEFAULT_VALUE);
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) { }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) { }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                updateRadius(progress);
            }
        });
        updateRadius(seekBarRadius.getProgress());

        selectedCategories = new ArrayList<EventCategory>();

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        FrameLayout frameCategory = (FrameLayout) findViewById(R.id.frameCategory);
        frameCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, CategorySearchActivity.class);
                startActivityForResult(i, Config.RESULT_CODE_CATEGORY);
            }
        });

        seekBarRadius.setEnabled(false);
        btnSearch.setEnabled(false);
        if(!MGUtilities.isLocationEnabled(this) && GlobalApplication.currentLocation == null) {
            MGLocationManagerUtils utils = new MGLocationManagerUtils();
            utils.setOnAlertListener(new MGLocationManagerUtils.OnAlertListener() {
                @Override
                public void onPositiveTapped() {
                    startActivityForResult(
                            new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                            Config.PERMISSION_REQUEST_LOCATION_SETTINGS);
                }

                @Override
                public void onNegativeTapped() {
                    Toast.makeText(SearchActivity.this, R.string.location_error_not_turned_on_search, Toast.LENGTH_LONG).show();
                }
            });
            utils.showAlertView(
                    this,
                    R.string.location_error,
                    R.string.gps_not_on,
                    R.string.go_to_settings,
                    R.string.cancel);
        }
        else {
            refetch();
        }

        GlobalApplication app = (GlobalApplication)getApplication();
        app.sendScreenView(Config.kGAIScreenNameSearch);
    }

    public void refetch() {
        GlobalApplication app = (GlobalApplication) getApplication();
        app.setOnLocationListener(this, this);
    }

    @Override
    public void onLocationChanged(Location prevLoc, Location currentLoc) {
        GlobalApplication app = (GlobalApplication) getApplication();
        app.setOnLocationListener(null, this);
        seekBarRadius.setEnabled(true);
        btnSearch.setEnabled(true);
    }

    @Override
    public void onLocationRequestDenied() {
        MGUtilities.showAlertView(this, R.string.permission_error, R.string.permission_error_details_location);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Config.RESULT_CODE_CATEGORY && resultCode == RESULT_OK) {
            selectedCategories = (ArrayList<EventCategory>) data.getSerializableExtra("categories");
            if(selectedCategories != null && selectedCategories.size() > 0) {
                TextView tvCategory = (TextView) findViewById(R.id.tvCategory);
                String cats = "";
                for(int x = 0; x < selectedCategories.size(); x++) {
                    EventCategory cat = selectedCategories.get(x);
                    cats += cat.getCategory();

                    if(x < selectedCategories.size() - 1)
                        cats += ", ";

                }

                if(!cats.isEmpty()) {
                    tvCategory.setText(cats);
                }
                else {
                    tvCategory.setText(R.string.all_categories);
                }
            }
        }
        if (requestCode == Config.PERMISSION_REQUEST_LOCATION_SETTINGS) {
            if(MGUtilities.isLocationEnabled(this))
                refetch();
            else {
                Toast.makeText(this, R.string.location_error_not_turned_on_search, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void updateRadius(int progress){
        String strSeekVal = String.format("%d %s",
                progress,
                MGUtilities.getStringFromResource(SearchActivity.this, R.string.km));

        TextView tvRadius = (TextView) findViewById(R.id.tvRadius);
        tvRadius.setText(strSeekVal);
    }

    public void showRefresh(boolean show) {
        swipeRefresh.setRefreshing(show);
        swipeRefresh.setEnabled(show);
    }

    public void search() {
        showRefresh(true);
        beginParsing();
    }

    public ArrayList<Event> searchLocal() {
        EditText txtSearch = (EditText) findViewById(R.id.txtSearch);
        String filterSearch = Html.toHtml(new SpannableString(txtSearch.getText().toString()));
        String searchStr = MGUtilities.filterInvalidChars(filterSearch);

        int radius = seekBarRadius.getProgress();
        int countParams = 0;
        countParams += searchStr.length() > 0 ? 1 : 0;
        countParams += radius > 0 ? 1 : 0;
        countParams += selectedCategories != null && selectedCategories.size() > 0 ? 1 : 0;

        ArrayList<Event> array = q.getEvents();
        ArrayList<Event> results = new ArrayList<>();
        for(Event event : array) {
            try {
                int qualifyCount = 0;
                if(searchStr.length() > 0) {
                    boolean isFound = searchStr.contains(event.getTitle()) ||
                            searchStr.contains(event.getAddress()) ||
                            searchStr.contains(event.getEvent_desc()) ||
                            event.getTitle().contains(searchStr) ||
                            event.getAddress().contains(searchStr) ||
                            event.getEvent_desc().contains(searchStr);

                    if(isFound)
                        qualifyCount += 1;
                }
                if(GlobalApplication.currentLocation != null) {
                    Location loc1 = new Location("Loc1");
                    loc1.setLatitude(event.getLat());
                    loc1.setLongitude(event.getLon());

                    Location loc2 = new Location("Loc2");
                    loc2.setLatitude(GlobalApplication.currentLocation.getLatitude());
                    loc2.setLongitude(GlobalApplication.currentLocation.getLongitude());

                    double distance = loc1.distanceTo(loc2) * Config.METERS_TO_KM;
                    event.setDistance(distance);
                    if(distance <= radius)
                        qualifyCount += 1;
                }

                ObjectMapper mapper = new ObjectMapper();
                DataResponse categories = mapper.readValue(event.getCategories(), DataResponse.class);
                if(categories != null && categories.getCategories() != null) {
                    for(EventCategory cat : categories.getCategories()) {
                        boolean isFound = false;
                        for(EventCategory selectedCat : selectedCategories) {
                            if(selectedCat.getCategory_id() == cat.getCategory_id()) {
                                isFound = true;
                                break;
                            }
                        }
                        if(isFound) {
                            qualifyCount += 1;
                            break;
                        }
                    }
                }
                if(qualifyCount == countParams)
                    results.add(event);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public void beginParsing() {
        if(task != null)
            task.cancel(true);

        task = new MGAsyncTaskNoDialog(this);
        task.setMGAsyncTaskListener(new MGAsyncTaskNoDialog.OnMGAsyncTaskListenerNoDialog() {

            DataResponse response;
            ArrayList<Event> events;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                updateData(response, events);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                if(MGUtilities.hasConnection(SearchActivity.this)) {
                    response = syncData();
                }
                else{
                    events = searchLocal();
                }
            }
        });
        task.execute();
    }

    public DataResponse syncData() {
        String cats = "";
        for(int x = 0; x < selectedCategories.size(); x++) {
            EventCategory cat = selectedCategories.get(x);
            cats += String.valueOf(cat.getCategory_id());
            if (x < selectedCategories.size() - 1)
                cats += ",";
        }

        EditText txtSearch = (EditText) findViewById(R.id.txtSearch);
        String filterSearch = Html.toHtml(new SpannableString(txtSearch.getText().toString()));
        String searchStr = MGUtilities.filterInvalidChars(filterSearch);

        double radius = seekBarRadius.getProgress() * Config.KM_TO_MILES;

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("keywords", searchStr ));
        params.add(new BasicNameValuePair("lat", String.valueOf(GlobalApplication.currentLocation.getLatitude()) ));
        params.add(new BasicNameValuePair("lon", String.valueOf(GlobalApplication.currentLocation.getLongitude()) ));
        params.add(new BasicNameValuePair("category_ids", cats) );
        params.add(new BasicNameValuePair("radius", String.valueOf(radius)) );
        params.add(new BasicNameValuePair("api_key", Config.API_KEY ));
        GlobalApplication.params = params;
        DataResponse response = DataParser.getJSONFromUrlWithPostRequest(Config.SEARCH_EVENTS_URL, params);
        return response;
    }

    public void updateData(DataResponse response, ArrayList<Event> events) {
        showRefresh(false);
        if(response != null) {
            Status status = response.getStatus();
            if(status.getStatus_code() == -1 && response.getEvents() != null ) {
                if(response.getEvents().size() == 0) {
                    Toast.makeText(SearchActivity.this, R.string.no_results_found, Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent(this, SearchResultsActivity.class);
                i.putExtra("events", response.getEvents());
                startActivity(i);
            }
            else {
                MGUtilities.showAlertView(this, R.string.network_error, status.getStatus_text());
            }
        }
        else if(events != null && events.size() > 0) {
            GlobalApplication.params = null;
            Intent i = new Intent(this, SearchResultsActivity.class);
            i.putExtra("events", events);
            startActivity(i);
        }
        else {
            MGUtilities.showAlertView(this, R.string.network_error, R.string.problems_encountered_while_syncing);
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
}
