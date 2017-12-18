package com.projects.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.application.GlobalApplication;;
import com.config.Config;
import com.config.UIConfig;
import com.db.Queries;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.libraries.adapters.MGRecyclerAdapter;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.dataparser.DataParser;
import com.libraries.helpers.DateTimeHelper;
import com.libraries.location.MGLocationManagerUtils;
import com.libraries.seekbar.MGVerticalSeekBar;
import com.libraries.sliding.MGSliding;
import com.libraries.usersession.UserAccessSession;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.Event;
import com.models.EventCategory;
import com.models.Favorite;
import com.projects.activities.DetailActivity;
import com.projects.activities.EventsActivity;
import com.projects.whatscamp.MainActivity;
import com.projects.whatscamp.R;;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class HomeFragment extends Fragment implements GlobalApplication.OnLocationListener{

    private View viewInflate;
    private ArrayList<Event> arrayData;
    private MGAsyncTaskNoDialog task;
    Queries q;
    SwipeRefreshLayout swipeRefresh;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    public HomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewInflate = inflater.inflate(R.layout.fragment_list_swipe, null);
        return viewInflate;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        q = GlobalApplication.getQueriesInstance(this.getActivity());

        mRecyclerView = (RecyclerView) viewInflate.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefresh = (SwipeRefreshLayout) viewInflate.findViewById(R.id.swipe_refresh);
        swipeRefresh.setClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            swipeRefresh.setProgressViewOffset(false, 0,100);
        }

        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        arrayData = new ArrayList<Event>();

        if(!MGUtilities.isLocationEnabled(getActivity()) && GlobalApplication.currentLocation == null) {
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
                    showRefresh(false);
                }
            });
            utils.showAlertView(
                    getActivity(),
                    R.string.location_error,
                    R.string.gps_not_on,
                    R.string.go_to_settings,
                    R.string.cancel);
        }
        else {
            refetch();
        }

    }

    public void refetch() {
        showRefresh(true);
        GlobalApplication app = (GlobalApplication) getActivity().getApplication();
        app.setOnLocationListener(this, getActivity());
    }

    public void showRefresh(boolean show) {
        swipeRefresh.setRefreshing(show);
        swipeRefresh.setEnabled(show);
    }

    private void showList() {
        showRefresh(false);
        if(arrayData != null && arrayData.size() == 0) {
            MGUtilities.showNotifier(this.getActivity(), Config.OFFSET_Y);
            return;
        }

        MGRecyclerAdapter adapter = new MGRecyclerAdapter(arrayData.size(), R.layout.event_entry);
        adapter.setOnMGRecyclerAdapterListener(new MGRecyclerAdapter.OnMGRecyclerAdapterListener() {

            @Override
            public void onMGRecyclerAdapterCreated(MGRecyclerAdapter adapter, MGRecyclerAdapter.ViewHolder v, int position) {

                final Event event = arrayData.get(position);
                ImageView imgViewThumb = (ImageView) v.view.findViewById(R.id.imgViewThumb);

                LinearLayout linearEvent = (LinearLayout) v.view.findViewById(R.id.linearEvent);
                linearEvent.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(getActivity(), DetailActivity.class);
                        i.putExtra("event", event);
                        startActivity(i);
                    }
                });

                if(event.getPhoto_url() != null) {
                    GlobalApplication.getImageLoaderInstance(getActivity())
                            .displayImage(
                                    event.getPhoto_url(),
                                    imgViewThumb,
                                    GlobalApplication.getDisplayImageOptionsInstance());
                }
                else {
                    imgViewThumb.setImageResource(UIConfig.SLIDER_PLACEHOLDER);
                }

                TextView tvTitle = (TextView) v.view.findViewById(R.id.tvTitle);
                TextView tvSubtitle = (TextView) v.view.findViewById(R.id.tvSubtitle);
                TextView tvDistance = (TextView) v.view.findViewById(R.id.tvDistance);
                TextView tvGoing = (TextView) v.view.findViewById(R.id.tvGoing);

                tvTitle.setText(Html.fromHtml(event.getTitle()));
                tvSubtitle.setText(Html.fromHtml(event.getAddress()));

                String strDistance = String.format("%.1f %s",
                        event.getDistance(),
                        MGUtilities.getStringFromResource(getActivity(), R.string.km));

                tvDistance.setText(strDistance);

                String strGoing = String.format("%d %s",
                        event.getAttendees_total(),
                        MGUtilities.getStringFromResource(getActivity(), R.string.going));

                tvGoing.setText(strGoing);

                ImageView imgViewFave = (ImageView) v.view.findViewById(R.id.imgViewFave);
                Favorite fave = q.getFavoriteByEventId(event.getEvent_id());
                if(fave == null)
                    imgViewFave.setVisibility(View.GONE);

                String dateStrDay= DateTimeHelper.formateDateFromString("yyyy-MM-dd hh:mm:ss", "E", event.getGmt_date_set());
                String dateStrMonth = DateTimeHelper.formateDateFromString("yyyy-MM-dd hh:mm:ss", "MMM", event.getGmt_date_set());
                String dateStrDayNum = DateTimeHelper.formateDateFromString("yyyy-MM-dd hh:mm:ss", "dd", event.getGmt_date_set());

                TextView tvDateDayNum = (TextView) v.view.findViewById(R.id.tvDateDayNum);
                tvDateDayNum.setText(dateStrDayNum);

                TextView tvDateDayText = (TextView) v.view.findViewById(R.id.tvDateDayText);
                tvDateDayText.setText(dateStrDay);

                TextView tvDateMonth = (TextView) v.view.findViewById(R.id.tvDateMonth);
                tvDateMonth.setText(dateStrMonth);


            }

        });
        mRecyclerView.setAdapter(adapter);
    }

    public void getData() {
        task = new MGAsyncTaskNoDialog(getActivity());
        task.setMGAsyncTaskListener(new MGAsyncTaskNoDialog.OnMGAsyncTaskListenerNoDialog() {

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                showList();
                showRefresh(false);

                if(arrayData != null && arrayData.size() == 0) {
                    MGUtilities.showNotifier(getActivity(), Config.OFFSET_Y);
                    return;
                }
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                if(GlobalApplication.currentLocation!= null) {
                    try {
                        float radius = UserAccessSession.getInstance(getActivity()).getFilterDistance();
                        if(radius == 0)
                            radius = Config.DEFAULT_FILTER_DISTANCE_IN_KM;

                        radius *= Config.KM_TO_MILES;

                        String strUrl = String.format("%s?api_key=%s&lat=%s&lon=%s&radius=%s",
                                Config.GET_EVENTS_JSON_URL,
                                Config.API_KEY,
                                String.valueOf(GlobalApplication.currentLocation.getLatitude()),
                                String.valueOf(GlobalApplication.currentLocation.getLongitude()),
                                String.valueOf(radius));

                        Log.d("URL", strUrl);
                        DataResponse data = DataParser.getJSONFromUrlWithPostRequest(strUrl, null);
                        if (data == null)
                            return;

                        if (data.getEvents() != null && data.getEvents().size() > 0) {
                            for (Event event : data.getEvents()) {
                                q.deleteEvent(event.getEvent_id());
                                q.insertEvent(event);
                                arrayData.add(event);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    arrayData = sortData();
                }
            }
        });
        task.execute();
    }

    private ArrayList<Event> sortData() {
        UserAccessSession accessSession = UserAccessSession.getInstance(getActivity());
        float radius = accessSession.getFilterDistance();

        ArrayList<Event> arrayData1 = q.getEventsFeatured();
        ArrayList<Event> arrayData = new ArrayList<Event>();
        if(GlobalApplication.currentLocation != null) {
            for(Event event : arrayData1) {
                Location locStore = new Location("Store");
                locStore.setLatitude(event.getLat());
                locStore.setLongitude(event.getLon());
                double userDistanceFromStore = GlobalApplication.currentLocation.distanceTo(locStore) / 1000;
                event.setDistance(userDistanceFromStore);

                if(event.getDistance() <= radius)
                    arrayData.add(event);
            }
            Collections.sort(arrayData, new Comparator<Event>() {
                @Override
                public int compare(Event t0, Event t1) {
                    if (t0.getDistance() < t1.getDistance())
                        return -1;
                    if (t0.getDistance() > t1.getDistance())
                        return 1;
                    return 0;
                }
            });
        }
        else {
            arrayData = arrayData1;
        }

        return arrayData;
    }

    @Override
    public void onDestroyView()  {
        super.onDestroyView();
        if(task != null)
            task.cancel(true);

        if (viewInflate != null) {
            ViewGroup parentViewGroup = (ViewGroup) viewInflate.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(task != null)
            task.cancel(true);
    }

    @Override
    public void onLocationChanged(Location prevLoc, Location currentLoc) {

        if(getActivity() == null)
            return;

        if(getActivity().getApplication() == null)
            return;

        GlobalApplication app = (GlobalApplication) getActivity().getApplication();
        app.setOnLocationListener(null, getActivity());
        getData();
    }

    @Override
    public void onLocationRequestDenied() {
        showRefresh(false);
        MGUtilities.showAlertView(getActivity(), R.string.permission_error, R.string.permission_error_details_location);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.PERMISSION_REQUEST_LOCATION_SETTINGS) {
            if(MGUtilities.isLocationEnabled(getActivity()))
                refetch();
            else {
                showRefresh(false);
                Toast.makeText(getActivity(), R.string.location_error_not_turned_on, Toast.LENGTH_LONG).show();
            }
        }
    }
}
