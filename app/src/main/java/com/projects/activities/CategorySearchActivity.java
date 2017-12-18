package com.projects.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.application.GlobalApplication;
import com.config.Config;
import com.db.Queries;
import com.libraries.adapters.MGRecyclerAdapter;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.dataparser.DataParser;
import com.libraries.utilities.MGUtilities;
import com.models.DataResponse;
import com.models.EventCategory;
import com.projects.whatscamp.MainActivity;
import com.projects.whatscamp.R;

import java.util.ArrayList;

public class CategorySearchActivity extends AppCompatActivity {


    private ArrayList<EventCategory> arrayData;
    private ArrayList<EventCategory> selectedCategories;
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
        setTitle(R.string.select_categories);

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

        selectedCategories = new ArrayList<EventCategory>();
        getData();
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
            public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                showList();
            }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                arrayData = new ArrayList<EventCategory>();
                if(MGUtilities.hasConnection(CategorySearchActivity.this)) {
                    try {
                        String strUrl = String.format("%s?api_key=%s",
                                Config.GET_CATEGORIES_JSON_URL,
                                Config.API_KEY);

                        Log.d("URL", strUrl);
                        DataResponse data = DataParser.getJSONFromUrlWithPostRequest(strUrl, null);
                        if (data == null)
                            return;

                        if (data.getCategories() != null && data.getCategories().size() > 0) {
                            for (EventCategory cat : data.getCategories()) {
                                q.deleteCategory(cat.getCategory_id());
                                q.insertCategory(cat);
                                arrayData.add(cat);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    arrayData = q.getCategories();
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

        MGRecyclerAdapter adapter = new MGRecyclerAdapter(arrayData.size(), R.layout.category_check_entry);
        adapter.setOnMGRecyclerAdapterListener(new MGRecyclerAdapter.OnMGRecyclerAdapterListener() {

            @Override
            public void onMGRecyclerAdapterCreated(MGRecyclerAdapter adapter, MGRecyclerAdapter.ViewHolder v, int position) {
                final EventCategory category = arrayData.get(position);
                Spanned title = Html.fromHtml(category.getCategory());
                title = Html.fromHtml(title.toString());
                TextView tvTitle = (TextView) v.view.findViewById(R.id.tvTitle);
                tvTitle.setText(title);

                v.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                AppCompatCheckBox chkCategory = (AppCompatCheckBox) v.view.findViewById(R.id.chkCategory);
                chkCategory.setFocusable(false);
                chkCategory.setFocusableInTouchMode(false);
                chkCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        selectedCategories.remove(category);
                        if(isChecked) {
                            selectedCategories.add(category);
                        }
                    }
                });
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.menuDone:
                Intent i = new Intent();
                i.putExtra("categories", selectedCategories);
                setResult(Activity.RESULT_OK, i);
                finish();
                return true;
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_categories, menu);
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
