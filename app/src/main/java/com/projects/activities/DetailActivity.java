package com.projects.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.application.GlobalApplication;
import com.config.Config;
import com.db.Queries;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.plus.PlusShare;
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.asynctask.MGAsyncTask.OnMGAsyncTaskListener;
import com.libraries.dataparser.DataParser;
import com.libraries.helpers.DateTimeHelper;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.libraries.utilities.MGUtilities;
import com.models.Attendee;
import com.models.DataResponse;
import com.models.Event;
import com.models.Favorite;
import com.models.Post;
import com.projects.whatscamp.R;
import com.projects.posts.PostsActivity;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements OnClickListener {

    private Queries q;
    MGAsyncTask task;
    Event event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_event_details);

        event = (Event) this.getIntent().getSerializableExtra("event");
        Spanned eventTitle = Html.fromHtml(event.getTitle());
        eventTitle = Html.fromHtml(eventTitle.toString());

        setTitle(eventTitle);

        q = GlobalApplication.getQueriesInstance(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fabFacebook = (FloatingActionButton) findViewById(R.id.fabFacebook);
        fabFacebook.setOnClickListener(this);

        FloatingActionButton fabCall = (FloatingActionButton) findViewById(R.id.fabCall);
        fabCall.setOnClickListener(this);

        FloatingActionButton fabEmail = (FloatingActionButton) findViewById(R.id.fabEmail);
        fabEmail.setOnClickListener(this);

        FloatingActionButton fabGooglePlus = (FloatingActionButton) findViewById(R.id.fabGooglePlus);
        fabGooglePlus.setOnClickListener(this);

        FloatingActionButton fabTwitter = (FloatingActionButton) findViewById(R.id.fabTwitter);
        fabTwitter.setOnClickListener(this);

        updateView();

        GlobalApplication app = (GlobalApplication)getApplication();
        app.sendScreenView(Config.kGAIScreenNameEventDetail);

        app.sendEvent(event.getTitle(), "Event Details", Config.kGAIScreenNameLabelDetails);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(task != null)
            task.cancel(true);
    }

    private void updateView() {
        ImageView imgViewPhoto = (ImageView) findViewById(R.id.imgViewThumb);
        imgViewPhoto.setOnClickListener(this);
        if(event.getPhoto_url() != null) {
            GlobalApplication.getImageLoaderInstance(this)
                    .displayImage(
                            event.getPhoto_url(),
                            imgViewPhoto,
                            GlobalApplication.getDisplayImageOptionsInstance());
        }

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvSubtitle = (TextView) findViewById(R.id.tvSubtitle);

        Spanned title = Html.fromHtml(event.getTitle());
        title = Html.fromHtml(title.toString());

        Spanned address = Html.fromHtml(event.getAddress());
        address = Html.fromHtml(address.toString());

        tvTitle.setText(title.toString());
        tvSubtitle.setText(address.toString());

        TextView tvPostedBy = (TextView) findViewById(R.id.tvPostedBy);
        String fullName = event.getFull_name();
        if(event.getUser_id() == -1)
            fullName = MGUtilities.getStringFromResource(this, R.string.admin);

        String postedByStr =  String.format("%s %s",
                MGUtilities.getStringFromResource(this, R.string.posted_by),
                fullName);

        tvPostedBy.setText(postedByStr);

        String dateStrDay= DateTimeHelper.formateDateFromString("yyyy-MM-dd hh:mm:ss", "EEE", event.getGmt_date_set());
        String dateStrMonth = DateTimeHelper.formateDateFromString("yyyy-MM-dd hh:mm:ss", "MMM", event.getGmt_date_set());
        String dateStrDayNum = DateTimeHelper.formateDateFromString("yyyy-MM-dd hh:mm:ss", "d", event.getGmt_date_set());
        String dateStrDayTime = DateTimeHelper.formateDateFromString("yyyy-MM-dd hh:mm:ss", "hh:mm a", event.getGmt_date_set());

        String detailsDateStr = String.format("%s, %s %s %s %S",
                dateStrDay,
                dateStrMonth,
                dateStrDayNum,
                MGUtilities.getStringFromResource(this, R.string.at),
                dateStrDayTime);

        TextView tvDetailsDate = (TextView) findViewById(R.id.tvDetailsDate);
        tvDetailsDate.setText(detailsDateStr);

        TextView tvDetailsAddress = (TextView) findViewById(R.id.tvDetailsAddress);
        tvDetailsAddress.setText(address);

        String strDesc = event.getEvent_desc().replace("\\n", "[{~}]");
        strDesc = strDesc.replace("&quot;", "\"");
        Spanned details = Html.fromHtml(strDesc);
        details = Html.fromHtml(details.toString());
        strDesc = details.toString().replace("[{~}]", "\n");

        final TextView tvDetailsDesc = (TextView) findViewById(R.id.tvDetailsDesc);
        tvDetailsDesc.setText(strDesc);
        tvDetailsDesc.setVisibility(View.GONE);

        final  TextView tvDetailsDesc1Line = (TextView) findViewById(R.id.tvDetailsDesc1Line);
        tvDetailsDesc1Line.setText(strDesc);
        tvDetailsDesc1Line.setVisibility(View.VISIBLE);

        final  TextView tvDetailsShowMoreDesc = (TextView) findViewById(R.id.tvDetailsShowMoreDesc);
        final LinearLayout linearShowMoreDesc = (LinearLayout) findViewById(R.id.linearShowMoreDesc);
        linearShowMoreDesc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvDetailsDesc.getVisibility() == View.GONE) {
                    tvDetailsDesc1Line.setVisibility(View.GONE);
                    tvDetailsDesc.setVisibility(View.VISIBLE);
                    tvDetailsShowMoreDesc.setText(R.string.show_less);
                }
                else {
                    tvDetailsDesc1Line.setVisibility(View.VISIBLE);
                    tvDetailsDesc.setVisibility(View.GONE);
                    tvDetailsShowMoreDesc.setText(R.string.show_more);
                }
            }
        });


        ToggleButton toggleButtonFave = (ToggleButton) findViewById(R.id.toggleButtonFave);
        toggleButtonFave.setOnClickListener(this);

        Favorite fave = q.getFavoriteByEventId(event.getEvent_id());
        toggleButtonFave.setChecked(true);
        if(fave == null)
            toggleButtonFave.setChecked(false);

        TextView tvDetailsJoined = (TextView) findViewById(R.id.tvDetailsJoined);
        tvDetailsJoined.setOnClickListener(this);
        tvDetailsJoined.setText(R.string.join);

        ImageView imgViewRoute = (ImageView) findViewById(R.id.imgViewRoute);
        imgViewRoute.setOnClickListener(this);

        TextView tvTicketAvailable = (TextView) findViewById(R.id.tvTicketAvailable);
        if(event.getIs_ticket_available() == 0)
            tvTicketAvailable.setText(R.string.free);

        TextView tvDetailsFindTickets = (TextView) findViewById(R.id.tvDetailsFindTickets);
        tvDetailsFindTickets.setOnClickListener(this);

        Thread t = new Thread() {
            public void run() {
                try {
                    if(event.getUser_ids().length() > 2 ) {
                        ObjectMapper mapper = new ObjectMapper();
                        final Integer[] userIds = mapper.readValue(event.getUser_ids(), Integer[].class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                formatEventUsers(userIds);
                            }
                        });
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                try {
                    if(event.getAttendees().length() > 2) {
                        ObjectMapper mapper = new ObjectMapper();
                        final Attendee[] users = mapper.readValue(event.getAttendees(), Attendee[].class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                formatUsers(users);
                            }
                        });
                    }
                    else {
                        formatUsers(null);
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                    formatUsers(null);
                }

                try {
                    if(event.getPosts().length() > 2) {
                        ObjectMapper mapper = new ObjectMapper();
                        final Post[]posts = mapper.readValue(event.getPosts(), Post[].class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                formatPosts(posts);
                            }
                        });
                    }
                    else {
                        formatPosts(null);
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                    formatPosts(null);
                }
            }
        };
        t.start();
    }

    private void formatEventUsers(Integer[] userIds) {
        UserSession userSession = UserAccessSession.getInstance(this).getUserSession();
        if(userSession != null) {
            for(Integer userId : userIds) {
                if(userSession.getUser_id() == userId) {
                    TextView tvDetailsJoined = (TextView) findViewById(R.id.tvDetailsJoined);
                    tvDetailsJoined.setAlpha(0.7f);
                    tvDetailsJoined.setEnabled(false);
                    tvDetailsJoined.setText(R.string.joined);
                    break;
                }
            }
        }
    }

    private void formatUsers(Attendee[] data) {
        TextView tvDetailsThumbNames = (TextView) findViewById(R.id.tvDetailsThumbNames);
        ImageView imgViewDetailsThumb1 = (ImageView) findViewById(R.id.imgViewDetailsThumb1);
        ImageView imgViewDetailsThumb2 = (ImageView) findViewById(R.id.imgViewDetailsThumb2);
        ImageView imgViewDetailsThumb3 = (ImageView) findViewById(R.id.imgViewDetailsThumb3);
        ImageView imgViewDetailsThumb4 = (ImageView) findViewById(R.id.imgViewDetailsThumb4);

        imgViewDetailsThumb1.setVisibility(View.GONE);
        imgViewDetailsThumb2.setVisibility(View.GONE);
        imgViewDetailsThumb3.setVisibility(View.GONE);
        imgViewDetailsThumb4.setVisibility(View.GONE);

        String _namesStr = "";
        if(data != null) {
            int max = data.length;
            for(int x = 0 ; x < max; x++) {
                Attendee user = data[x];
                String[] split = user.getFull_name().split(" ");
                if(split.length > 0)
                    _namesStr += split[0];

                if(x < max && x < 2) {
                    if(data.length == 1) { }
                    else if(data.length == 2) {
                        if(x == 0) {
                            _namesStr += " ";
                            _namesStr += "and";
                            _namesStr += " ";
                        }
                    }
                    else {
                        _namesStr += ", ";
                    }
                }

                if(x == 0) {
                    imgViewDetailsThumb1.setVisibility(View.VISIBLE);
                    GlobalApplication.getImageLoaderInstance(this).displayImage(
                            user.getThumb_url(),
                            imgViewDetailsThumb1,
                            GlobalApplication.getDisplayImageOptionsThumbInstance());
                }
                else if(x == 1) {
                    imgViewDetailsThumb2.setVisibility(View.VISIBLE);
                    GlobalApplication.getImageLoaderInstance(this).displayImage(
                            user.getThumb_url(),
                            imgViewDetailsThumb2,
                            GlobalApplication.getDisplayImageOptionsThumbInstance());
                }
                else if(x == 2) {
                    imgViewDetailsThumb3.setVisibility(View.VISIBLE);
                    GlobalApplication.getImageLoaderInstance(this).displayImage(
                            user.getThumb_url(),
                            imgViewDetailsThumb3,
                            GlobalApplication.getDisplayImageOptionsThumbInstance());
                }
                else if(x == 3) {
                    imgViewDetailsThumb4.setVisibility(View.VISIBLE);
                    GlobalApplication.getImageLoaderInstance(this).displayImage(
                            user.getThumb_url(),
                            imgViewDetailsThumb4,
                            GlobalApplication.getDisplayImageOptionsThumbInstance());
                }
            }

            if(data.length == 0) {
                String format = String.format("%s %s %s %s.",
                        MGUtilities.getStringFromResource(this, R.string.details_no),
                        MGUtilities.getStringFromResource(this, R.string.details_users),
                        MGUtilities.getStringFromResource(this, R.string.details_currently),
                        MGUtilities.getStringFromResource(this, R.string.details_going));

                tvDetailsThumbNames.setText(format);
            }
            else if(data.length == 1) {
                String format = String.format("%s %s.",
                        _namesStr,
                        MGUtilities.getStringFromResource(this, R.string.details_is_going));

                tvDetailsThumbNames.setText(format);
            }
            else if(data.length == 2) {
                String format = String.format("%s %s.",
                        _namesStr,
                        MGUtilities.getStringFromResource(this, R.string.details_are_going));

                tvDetailsThumbNames.setText(format);
            }
            else {
                String format = String.format("%s %s %s %s.",
                        _namesStr,
                        MGUtilities.getStringFromResource(this, R.string.details_and),
                        MGUtilities.getStringFromResource(this, R.string.details_others),
                        MGUtilities.getStringFromResource(this, R.string.details_are_going));

                tvDetailsThumbNames.setText(format);
            }
        }
        else {
            String format = String.format("%s %s %s %s.",
                    MGUtilities.getStringFromResource(this, R.string.details_no),
                    MGUtilities.getStringFromResource(this, R.string.details_users),
                    MGUtilities.getStringFromResource(this, R.string.details_currently),
                    MGUtilities.getStringFromResource(this, R.string.details_going));

            tvDetailsThumbNames.setText(format);
        }

    }

    private void formatPosts(Post[] data) {
        LinearLayout linearShowMoreComment = (LinearLayout) findViewById(R.id.linearShowMoreComment);
        linearShowMoreComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, PostsActivity.class);
                i.putExtra("event", event);
                startActivity(i);
            }
        });

        LinearLayout linearDetailsComment1 = (LinearLayout) findViewById(R.id.linearDetailsComment1);
        LinearLayout linearDetailsComment2 = (LinearLayout) findViewById(R.id.linearDetailsComment2);

        linearDetailsComment1.setVisibility(View.GONE);
        linearDetailsComment2.setVisibility(View.GONE);

        TextView tvDetailsCommentDate1 = (TextView) findViewById(R.id.tvDetailsCommentDate1);
        TextView tvDetailsCommentName1 = (TextView) findViewById(R.id.tvDetailsCommentName1);
        TextView tvDetailsCommentText1 = (TextView) findViewById(R.id.tvDetailsCommentText1);
        ImageView imgViewDetailsCommentThumb1 = (ImageView) findViewById(R.id.imgViewDetailsCommentThumb1);

        TextView tvDetailsCommentDate2 = (TextView) findViewById(R.id.tvDetailsCommentDate2);
        TextView tvDetailsCommentName2 = (TextView) findViewById(R.id.tvDetailsCommentName2);
        TextView tvDetailsCommentText2 = (TextView) findViewById(R.id.tvDetailsCommentText2);
        ImageView imgViewDetailsCommentThumb2 = (ImageView) findViewById(R.id.imgViewDetailsCommentThumb2);

        if(data != null) {
            int max = data.length;
            for(int x = 0; x < max; x++) {
                Post post = data[0];
                if(x == 0) {
                    linearDetailsComment1.setVisibility(View.VISIBLE);
                    tvDetailsCommentName1.setText(Html.fromHtml(post.getFull_name()));

                    Spanned postStr = Html.fromHtml(post.getPost());
                    postStr = Html.fromHtml(postStr.toString());
                    tvDetailsCommentText1.setText(postStr);

                    String dateStr = DateTimeHelper.formateDateFromStringUTC("yyyy-MM-dd hh:mm:ss", "dd EEE hh:mm a", post.getGmt_date_added());
                    tvDetailsCommentDate1.setText(dateStr);

                    GlobalApplication.getImageLoaderInstance(this).displayImage(
                            post.getThumb_url(),
                            imgViewDetailsCommentThumb1,
                            GlobalApplication.getDisplayImageOptionsThumbInstance());
                }

                else if(x == 0) {
                    linearDetailsComment2.setVisibility(View.VISIBLE);
                    tvDetailsCommentName2.setText(Html.fromHtml(post.getFull_name()));

                    Spanned postStr = Html.fromHtml(post.getPost());
                    postStr = Html.fromHtml(postStr.toString());
                    tvDetailsCommentText2.setText(postStr);

                    String dateStr = DateTimeHelper.formateDateFromStringUTC("yyyy-MM-dd hh:mm:ss", "dd EEE hh:mm a", post.getGmt_date_added());
                    tvDetailsCommentDate2.setText(dateStr);

                    GlobalApplication.getImageLoaderInstance(this).displayImage(
                            post.getThumb_url(),
                            imgViewDetailsCommentThumb2,
                            GlobalApplication.getDisplayImageOptionsThumbInstance());
                    break;
                }
            }
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
    public void onClick(View v) {
        // TODO Auto-generated method stub
        FloatingActionMenu famSocialMenus = (FloatingActionMenu) findViewById(R.id.famSocialMenus);
        famSocialMenus.close(true);
        Intent i;
        switch(v.getId()) {
            case R.id.fabCall:
                call();
                break;
            case R.id.fabEmail:
                email();
                break;
            case R.id.imgViewRoute:
                route();
                break;
            case R.id.tvDetailsFindTickets:
                findTickets();
                break;
            case R.id.tvDetailsJoined:
                join();
                break;
            case R.id.fabFacebook:
                shareFB();
                break;
            case R.id.fabTwitter:
                shareTwitter();
                break;
            case R.id.fabGooglePlus:
                shareGooglePlus();
                break;
            case R.id.toggleButtonFave:
                checkFave(v);
                break;
            case R.id.imgViewThumb:
                i = new Intent(this, ImageViewerActivity.class);
                i.putExtra("photoUrl", event.getPhoto_url());
                startActivity(i);
                break;
        }
    }

    private void checkFave(View view) {
        Favorite fave = q.getFavoriteByEventId(event.getEvent_id());
        if(fave != null) {
            q.deleteFavorite(event.getEvent_id());
            ((ToggleButton) view).setChecked(false);
        }
        else {
            fave = new Favorite();
            fave.setEvent_id(event.getEvent_id());
            q.insertFavorite(fave);
            ((ToggleButton) view).setChecked(true);
        }
    }

    private void call() {
        if( event.getContact_no() == null || event.getContact_no().length() == 0 ) {
            MGUtilities.showAlertView(
                    this,
                    R.string.action_error,
                    R.string.cannot_proceed);
            return;
        }
        PackageManager pm = this.getBaseContext().getPackageManager();
        boolean canCall = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        if(!canCall) {
            MGUtilities.showAlertView(
                    this,
                    R.string.action_error,
                    R.string.cannot_proceed);
            return;
        }

        checkPermissionCall(Config.REQUEST_CODE_PHONE_CALL);
    }

    private void route() {
        if(event.getLat() == 0 || event.getLon() == 0) {
            MGUtilities.showAlertView(
                    this,
                    R.string.action_error,
                    R.string.cannot_proceed);
            return;
        }

        String geo = String.format("http://maps.google.com/maps?f=d&daddr=%s&dirflg=d", event.getAddress());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geo));
        intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
        this.startActivity(intent);
    }

    private void email() {
        if(event.getEmail_address() == null || event.getEmail_address().length() == 0) {
            MGUtilities.showAlertView(
                    this,
                    R.string.action_error,
                    R.string.cannot_proceed);
            return;
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ event.getEmail_address() } );
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, MGUtilities.getStringFromResource(this, R.string.email_subject) );
        emailIntent.putExtra(Intent.EXTRA_TEXT, MGUtilities.getStringFromResource(this, R.string.email_body) );
        emailIntent.setType("message/rfc822");
        this.startActivity(Intent.createChooser(emailIntent,
                MGUtilities.getStringFromResource(this, R.string.choose_email_client)) );

        GlobalApplication app = (GlobalApplication)getApplication();
        app.sendEvent(event.getTitle(), "Email", Config.kGAIScreenNameLabelDetailsEmail);
    }

    private void findTickets() {
        if(event.getTicket_url() == null || event.getTicket_url().length() == 0) {
            MGUtilities.showAlertView(
                    this,
                    R.string.action_error,
                    R.string.cannot_proceed);
            return;
        }
        String strUrl = event.getTicket_url();
        if(!strUrl.contains("http")) {
            strUrl = "http://" + strUrl;
        }
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse(strUrl));
        this.startActivity(Intent.createChooser(webIntent,
                MGUtilities.getStringFromResource(this, R.string.choose_browser)));
    }

    private void shareFB() {
        ShareDialog shareDialog = new ShareDialog(this);
        String desc = String.format("%s", event.getTitle() );
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(MGUtilities.getStringFromResource(this, R.string.app_name))
                    .setContentDescription(desc)
                    .setContentUrl(Uri.parse(Config.SERVER_URL_DEFAULT_PAGE_FOR_FACEBOOK))
                    .setQuote(MGUtilities.getStringFromResource(this, R.string.download_app))
                    .build();

            shareDialog.show(linkContent);
        }

        GlobalApplication app = (GlobalApplication)getApplication();
        app.sendEvent(event.getTitle(), "Facebook", Config.kGAIScreenNameLabelDetailsFacebook);
    }

    private void shareTwitter() {
        String tweet = String.format("%s %s",
                MGUtilities.getStringFromResource(this, R.string.download_app),
                Config.SERVER_URL_DEFAULT_PAGE_FOR_TWITTER);

        Intent tweetIntent = new Intent();
        tweetIntent.setType("text/plain");
        tweetIntent.putExtra(Intent.EXTRA_TEXT, tweet);
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        String p = null;
        for (ResolveInfo resolveInfo : list) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (packageName != null && packageName.startsWith("com.twitter.android")) {
                p = packageName;
                tweetIntent.setPackage(p);
                startActivity(tweetIntent);
                break;
            }
        }

        if(p == null) {
            MGUtilities.showAlertView(this, R.string.twitter_app_error, R.string.twitter_app_error_details);
        }

        GlobalApplication app = (GlobalApplication)getApplication();
        app.sendEvent(event.getTitle(), "Twitter", Config.kGAIScreenNameLabelDetailsTwitter);
    }

    private void checkPermissionCall(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
            if(permissionCheck == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            Config.REQUEST_CODE_PHONE_CALL);
                    return;
                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            Config.REQUEST_CODE_PHONE_CALL);

                    return;
                }
            }
        }

        if(requestCode == Config.REQUEST_CODE_PHONE_CALL) {
            String phoneNo = event.getContact_no().replaceAll("[^0-9]", "");
            String uri = "tel:" + phoneNo;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Config.REQUEST_CODE_PHONE_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    checkPermissionCall(requestCode);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    MGUtilities.showAlertView(this, R.string.permission_error, R.string.grant_permission_call);
                }
                return;
            }
        }
    }

    private void join() {
        if(!MGUtilities.hasConnection(this)) {
            MGUtilities.showAlertView(
                    this,
                    R.string.network_error,
                    R.string.no_network_connection);
            return;
        }

        UserSession userSession = UserAccessSession.getInstance(this).getUserSession();
        if(userSession == null) {
            MGUtilities.showAlertView(
                    this,
                    R.string.login_error,
                    R.string.need_login_event_details);
            return;
        }

        task = new MGAsyncTask(this);
        task.setMGAsyncTaskListener(new OnMGAsyncTaskListener() {

            DataResponse response;

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) { }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                reloadEvent(response);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
                // TODO Auto-generated method stub
                response = syncJoin();
            }
        });
        task.execute();
    }

    public DataResponse syncJoin() {
        UserAccessSession userAccess = UserAccessSession.getInstance(this);
        UserSession userSession = userAccess.getUserSession();
        try {
            String strUrl = String.format("%s?api_key=%s&event_id=%d&user_id=%d&lat=%s&lon=%s",
                    Config.JOIN_EVENT_URL,
                    Config.API_KEY,
                    event.getEvent_id(),
                    userSession.getUser_id(),
                    String.valueOf(GlobalApplication.currentLocation.getLatitude()),
                    String.valueOf(GlobalApplication.currentLocation.getLongitude()));

            Log.e("URL", strUrl);
            return DataParser.getJSONFromUrlWithPostRequest(strUrl, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void reloadEvent(DataResponse response) {
        if(response == null) {
            MGUtilities.showAlertView(
                    this,
                    R.string.action_error,
                    R.string.problems_encountered_while_syncing);
            return;
        }
        if(response.getStatus() == null) {
            MGUtilities.showAlertView(
                    this,
                    R.string.action_error,
                    R.string.problems_encountered_while_syncing);
            return;
        }
        if(response.getStatus().getStatus_code() != Config.STATUS_SUCCESS) {
            MGUtilities.showAlertView(
                    this,
                    R.string.action_error,
                    response.getStatus().getStatus_text());
            return;
        }

        if(response.getEvent() != null) {
            q.updateEvent(response.getEvent());
            event = response.getEvent();
            updateView();
        }
    }

    private void shareGooglePlus() {
        String strUrl = Config.SERVER_URL_DEFAULT_PAGE_FOR_GOOGLE_PLUS;
        Spanned storeName = Html.fromHtml(event.getTitle());
        storeName = Html.fromHtml(storeName.toString());

        PlusShare.Builder share = new PlusShare.Builder(this);
        share.setText(storeName.toString());
        share.setContentUrl(Uri.parse(strUrl));
        share.setType("text/plain");
        startActivity(share.getIntent());

        GlobalApplication app = (GlobalApplication)getApplication();
        app.sendEvent(event.getTitle(), "GooglePlus", Config.kGAIScreenNameLabelDetailsGooglePlus);
    }
}
