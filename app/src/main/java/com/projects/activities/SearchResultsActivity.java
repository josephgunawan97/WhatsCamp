package com.projects.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.GlobalApplication;
import com.config.UIConfig;
import com.db.Queries;
import com.libraries.adapters.MGRecyclerAdapter;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.helpers.DateTimeHelper;
import com.libraries.utilities.MGUtilities;
import com.models.Event;
import com.models.Favorite;
import com.projects.whatscamp.MainActivity;
import com.projects.whatscamp.R;
import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    private ArrayList<Event> arrayData;
    private Queries q;
    MGAsyncTaskNoDialog task;

    SwipeRefreshLayout swipeRefresh;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.fragment_list_swipe);
        setTitle(R.string.results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        q = GlobalApplication.getQueriesInstance(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

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

        arrayData = (ArrayList<Event>) this.getIntent().getSerializableExtra("events");
        showList();
    }

    public void showRefresh(boolean show) {
        swipeRefresh.setRefreshing(show);
        swipeRefresh.setEnabled(show);
    }

    private void showList() {
        showRefresh(false);

        if(arrayData == null || arrayData.size() == 0) {
            MGUtilities.showNotifier(this, MainActivity.offsetY);
            return;
        }

        MGRecyclerAdapter adapter = new MGRecyclerAdapter(arrayData.size(), R.layout.event_entry);
        adapter.setOnMGRecyclerAdapterListener(new MGRecyclerAdapter.OnMGRecyclerAdapterListener() {

            @Override
            public void onMGRecyclerAdapterCreated(MGRecyclerAdapter adapter, MGRecyclerAdapter.ViewHolder v, int position) {
                final Event event = arrayData.get(position);
                ImageView imgViewThumb = (ImageView) v.view.findViewById(R.id.imgViewThumb);

                LinearLayout linearEvent = (LinearLayout) v.view.findViewById(R.id.linearEvent);
                linearEvent.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(SearchResultsActivity.this, DetailActivity.class);
                        i.putExtra("event", event);
                        startActivity(i);
                    }
                });

                if(event.getPhoto_url() != null) {
                    GlobalApplication.getImageLoaderInstance(SearchResultsActivity.this)
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
                        MGUtilities.getStringFromResource(SearchResultsActivity.this, R.string.km));

                tvDistance.setText(strDistance);

                String strGoing = String.format("%d %s",
                        event.getAttendees_total(),
                        MGUtilities.getStringFromResource(SearchResultsActivity.this, R.string.going));

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
    protected void onDestroy() {
        super.onDestroy();
        if(task != null)
            task.cancel(true);
    }
}
