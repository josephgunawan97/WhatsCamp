package com.projects.posts;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.GlobalApplication;
import com.config.Config;
import com.db.Queries;
import com.libraries.adapters.MGRecyclerAdapter;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.dataparser.DataParser;
import com.libraries.helpers.DateTimeHelper;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.Event;
import com.models.Post;
import com.projects.whatscamp.MainActivity;
import com.projects.whatscamp.R;

import java.util.ArrayList;

public class PostsActivity extends AppCompatActivity {


    private ArrayList<Post> arrayData;
    private Queries q;
    MGAsyncTaskNoDialog task;
    Event event;

    SwipeRefreshLayout swipeRefresh;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    int reviewCount;
    int requestCount;
    int returnCount;
    int totalRowCount;
    int lastMaxCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.fragment_list_swipe);
        setTitle(R.string.posts);

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

        reviewCount = 0;
        lastMaxCount = Config.MAX_POSTS_COUNT_PER_LISTING;
        arrayData = new ArrayList<Post>();

        event = (Event) this.getIntent().getSerializableExtra("event");
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showRefresh(boolean show) {
        swipeRefresh.setRefreshing(show);
        swipeRefresh.setEnabled(show);
    }

    public void getData() {
        showRefresh(true);
        task = new MGAsyncTaskNoDialog(this);
        task.setMGAsyncTaskListener(new MGAsyncTaskNoDialog.OnMGAsyncTaskListenerNoDialog() {

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) {

            }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                showList();
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                if(MGUtilities.hasConnection(PostsActivity.this)) {
                    try {
                        String strUrl = String.format("%s?api_key=%s&event_id=%s&min_count=%s&max_count=%s",
                                Config.GET_POSTS_JSON_URL,
                                Config.API_KEY,
                                String.valueOf(event.getEvent_id()),
                                String.valueOf(reviewCount),
                                String.valueOf(lastMaxCount));

                        Log.d("URL", strUrl);
                        DataResponse data = DataParser.getJSONFromUrlWithPostRequest(strUrl, null);
                        if (data == null)
                            return;

                        returnCount = data.getResult_count();
                        totalRowCount = data.getTotal_no_of_rows();
                        requestCount = data.getMin_count();

                        if (data.getPosts() != null && data.getPosts().size() > 0) {
                            for (Post post : data.getPosts()) {
                                arrayData.add(post);
                            }

                            if((requestCount + returnCount) < totalRowCount) {
                                Post post = new Post();
                                post.setPost(MGUtilities.getStringFromResource(PostsActivity.this, R.string.load_more));
                                arrayData.add(post);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        task.execute();
    }

    private void showList() {
        showRefresh(false);

        if(arrayData == null || arrayData.size() == 0) {
            MGUtilities.showNotifier(this, MainActivity.offsetY);
            return;
        }

        MGRecyclerAdapter adapter = new MGRecyclerAdapter(arrayData.size(), R.layout.post_entry);
        adapter.setOnMGRecyclerAdapterListener(new MGRecyclerAdapter.OnMGRecyclerAdapterListener() {

            @Override
            public void onMGRecyclerAdapterCreated(MGRecyclerAdapter adapter, MGRecyclerAdapter.ViewHolder v, int position) {
                final Post post = arrayData.get(position);

                FrameLayout frameLoadMore = (FrameLayout) v.view.findViewById(R.id.frameLoadMore);
                LinearLayout linearPost = (LinearLayout) v.view.findViewById(R.id.linearPost);

                frameLoadMore.setVisibility(View.GONE);
                linearPost.setVisibility(View.GONE);

                if(post.getFull_name() != null && !post.getFull_name().isEmpty()) {
                    TextView tvDate = (TextView) v.view.findViewById(R.id.tvDate);
                    TextView tvTitle = (TextView) v.view.findViewById(R.id.tvTitle);
                    TextView tvSubtitle = (TextView) v.view.findViewById(R.id.tvSubtitle);
                    ImageView imgViewThumb = (ImageView) v.view.findViewById(R.id.imgViewThumb);

                    tvTitle.setText(Html.fromHtml(post.getFull_name()));

                    Spanned postStr = Html.fromHtml(post.getPost());
                    postStr = Html.fromHtml(postStr.toString());
                    tvSubtitle.setText(postStr);

                    String dateStr = DateTimeHelper.formateDateFromStringUTC("yyyy-MM-dd hh:mm:ss", "dd EEE hh:mm a", post.getGmt_date_added());
                    tvDate.setText(dateStr);

                    GlobalApplication.getImageLoaderInstance(PostsActivity.this).displayImage(
                            post.getThumb_url(),
                            imgViewThumb,
                            GlobalApplication.getDisplayImageOptionsThumbInstance());

                    linearPost.setVisibility(View.VISIBLE);
                }
                else {
                    frameLoadMore.setVisibility(View.VISIBLE);
                    frameLoadMore.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reviewCount += Config.MAX_POSTS_COUNT_PER_LISTING;
                            arrayData.remove(arrayData.size() - 1);
                            getData();
                        }
                    });
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.menuNewPost:
                Intent i = new Intent(this, NewPostActivity.class);
                i.putExtra("event", event);
                i.putExtra("maxCount", lastMaxCount);
                startActivityForResult(i, Config.RESULT_CODE_NEW_POST);
                return true;
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_posts, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Config.RESULT_CODE_NEW_POST && resultCode == Activity.RESULT_OK)
            getData();
    }
}
