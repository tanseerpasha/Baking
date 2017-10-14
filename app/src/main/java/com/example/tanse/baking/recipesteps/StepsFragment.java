package com.example.tanse.baking.recipesteps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tanse.baking.R;
import com.example.tanse.baking.Utility.Utility;
import com.example.tanse.baking.data.Contract;
import com.example.tanse.baking.recipedetails.DetailActivity;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by Tanseer on 6/7/2017.
 */

public class StepsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String LOG_TAG = StepsFragment.class.getSimpleName();
    private static final String[] RECIPESTEPS_COLUMNS_PROJECTION = {    //projection
            Contract.RecipeDetailsEntry.COLUMN_STEP_ID,
            Contract.RecipeDetailsEntry.COLUMN_STEP_SHORTDESCRIPTION,
            Contract.RecipeDetailsEntry.COLUMN_STEP_DESCRIPTION,
            Contract.RecipeDetailsEntry.COLUMN_STEP_VIDEOURL,
            Contract.RecipeDetailsEntry.COLUMN_STEP_THUMBNAILURL,
    };

    public static final int COL_STEP_ID = 0;
    public static final int COL_STEP_SHORTDESCRIPTION = 1;
    public static final int COL_STEP_DESCRIPTION = 2;
    public static final int COL_STEP_VIDEOURL = 3;
    public static final int COL_STEP_THUMBNAILURL = 4;

    private static final int RECIPE_STEPS_LOADER = 0;
    private Context context = getActivity();

    private SimpleExoPlayerView mPlayerView;
    private ImageView mRecipeImageView;
    private TextView mRecipeDetailsView;
    private TextView mNoNetworkView;
    private Button mButtonPreviousView;
    private Button mButtonNextView;
    private SimpleExoPlayer mExoPlayerView;
    private long mPosition = 0;
    private String videoURLforResume;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    public static final String STEPS_URI = "URI";
    public static final String  STEPS_SHORT_DESCRIPTION = "String";
    public static final String  MAIN_RECIPE_NAME = "recipe";
    private static final String VIDEO_SEEK_POSITION = "position";
    private static final String VIDEO_URL = "videourl";

    private String mStepsId;
    private String mStepsShortDescription;
    private String mStepsDescription;
    private String mStepsVideoURL;
    private String mStepsThumbnailURL;
    private Uri recipeStepSelectUri;
    private int recipeTotalStepsCount;
    private String mainRecipeName;
    private String stepShortDescription;
    private String thisActivityTitle;
    private final String ACTIVITY_TILTE = "title";



    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
    }

    public StepsFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if(arguments !=null){
            //Uri uriForSteps = Contract.RecipeDetailsEntry.buildStepsIDUri(stepId);
            recipeStepSelectUri = arguments.getParcelable(StepsFragment.STEPS_URI);
            stepShortDescription = arguments.getString(StepsFragment.STEPS_SHORT_DESCRIPTION);
            mainRecipeName = arguments.getString(StepsFragment.MAIN_RECIPE_NAME);
            thisActivityTitle = mainRecipeName + "-" + stepShortDescription;

        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ACTIVITY_TILTE)) {
                thisActivityTitle = savedInstanceState.getString(ACTIVITY_TILTE);
                getActivity().setTitle(thisActivityTitle);
            }
        }

        initializeMediaSession();

        // -2 because first row is for ingredients and index starts from 0
        recipeTotalStepsCount = Utility.getRecipeStepsCount()-2;


        View rootView = inflater.inflate(R.layout.fragment_recipe_steps, container, false);
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);
        mRecipeDetailsView = (TextView) rootView.findViewById(R.id.step_description);
        mNoNetworkView = (TextView) rootView.findViewById(R.id.no_internet_connectivity);
        mButtonPreviousView = (Button) rootView.findViewById(R.id.buttonPrevious);
        mButtonNextView = (Button) rootView.findViewById(R.id.buttonNext);
        mRecipeImageView = (ImageView) rootView.findViewById(R.id.recipe_image);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(VIDEO_SEEK_POSITION) ) {
                mPosition = savedInstanceState.getLong(VIDEO_SEEK_POSITION);
            }

            if (savedInstanceState.containsKey(VIDEO_URL) ) {
                videoURLforResume = savedInstanceState.getString(VIDEO_URL);
            }
            //initializePlayer(Uri.parse(videoURLforResume),mPosition);
        }
        return rootView;
    }





    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RECIPE_STEPS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mExoPlayerView != null) {
            mPosition = mExoPlayerView.getCurrentPosition();
            outState.putLong(VIDEO_SEEK_POSITION, mPosition);
            outState.putString(VIDEO_URL, videoURLforResume);
        }
        outState.putString(ACTIVITY_TILTE, thisActivityTitle);
        super.onSaveInstanceState(outState);
    }





    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle){
        if(null != recipeStepSelectUri){

//            Log.d("TANSEERSTEPS_URI","" + new CursorLoader(getActivity(),
//                    recipeStepSelectUri,
//                    RECIPESTEPS_COLUMNS_PROJECTION,
//                    null,
//                    null,
//                    null) );
            return new CursorLoader(getActivity(),
                    recipeStepSelectUri,
                    RECIPESTEPS_COLUMNS_PROJECTION,
                    null,
                    null,
                    null);


        }
        return  null;
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data){
        //releasePlayer();
        if(data.moveToFirst()){

            mStepsId = data.getString(COL_STEP_ID);
            mStepsShortDescription = data.getString(COL_STEP_SHORTDESCRIPTION);
            mStepsDescription = data.getString(COL_STEP_DESCRIPTION);
            mStepsVideoURL = data.getString(COL_STEP_VIDEOURL);
            mStepsThumbnailURL = data.getString(COL_STEP_THUMBNAILURL);

            mRecipeDetailsView.setText(mStepsDescription);
            checkVideoExists(mStepsVideoURL,mStepsThumbnailURL,mPosition);

            mButtonPreviousView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer stepIndex = Integer.valueOf(mStepsId);
                    if(stepIndex == 0){
                        Toast.makeText(getContext(), "You are at the first step, there is no previous step",Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(getContext(), "previous step",Toast.LENGTH_SHORT).show();
                        releasePlayer();
                        recipeStepSelectUri = Contract.RecipeDetailsEntry.buildStepsIDUri(Integer.toString(stepIndex-1));
                        getLoaderManager().restartLoader(RECIPE_STEPS_LOADER,null,loaderCallbacks);
                    }

                }
            });

            mButtonNextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer stepIndex = Integer.valueOf(mStepsId);
                    if(stepIndex == recipeTotalStepsCount){
                        Toast.makeText(getContext(), "You are at the final step, there is no next step",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "next step",Toast.LENGTH_SHORT).show();
                        releasePlayer();
                        recipeStepSelectUri = Contract.RecipeDetailsEntry.buildStepsIDUri(Integer.toString(stepIndex+1));
                        getLoaderManager().restartLoader(RECIPE_STEPS_LOADER,null,loaderCallbacks);
                    }



                }
            });


        }
    }

    private void setTitle(String title){
        if(! DetailActivity.checkTwoPane()){
            getActivity().setTitle(title);

        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        //loader.forceLoad();
        loader.abandon();
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if(null != recipeStepSelectUri){
//                Log.d("TANSEERSTEPSCLCK_URI","" + new CursorLoader(getActivity(),
//                        recipeStepSelectUri,
//                        RECIPESTEPS_COLUMNS_PROJECTION,
//                        null,
//                        null,
//                        null) );
                return new CursorLoader(getActivity(),
                        recipeStepSelectUri,
                        RECIPESTEPS_COLUMNS_PROJECTION,
                        null,
                        null,
                        null);


            }
            return  null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if(data.moveToFirst()) {
                mStepsId = data.getString(COL_STEP_ID);
                mStepsShortDescription = data.getString(COL_STEP_SHORTDESCRIPTION);
                mStepsDescription = data.getString(COL_STEP_DESCRIPTION);
                mStepsVideoURL = data.getString(COL_STEP_VIDEOURL);
                mStepsThumbnailURL = data.getString(COL_STEP_THUMBNAILURL);

                mRecipeDetailsView.setText(mStepsDescription);
                checkVideoExists(mStepsVideoURL, mStepsThumbnailURL, mPosition);

                thisActivityTitle = mainRecipeName + "-" + mStepsShortDescription;
                getActivity().setTitle(thisActivityTitle);

            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

    };

    private void checkVideoExists(String videourl, String thumbnailURL, long videoPosition){

        if (!Utility.isNetworkAvailable(getActivity()) ) {
            mNoNetworkView.setVisibility(View.VISIBLE);
            mPlayerView.setVisibility(View.GONE);
            mRecipeImageView.setVisibility(View.GONE);
            mRecipeDetailsView.setVisibility(View.VISIBLE);

        }else {
            mNoNetworkView.setVisibility(View.GONE);
            if (! videourl.isEmpty()) {
                mPlayerView.setVisibility(View.VISIBLE);
                mRecipeImageView.setVisibility(View.GONE);
                videoURLforResume = videourl;
                initializePlayer(Uri.parse(videourl), videoPosition);
                if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ){
                    mRecipeDetailsView.setVisibility(View.GONE);
                    //mRecipeDetailsView.setVisibility(View.VISIBLE);
                }
            }else if (! thumbnailURL.isEmpty()) {
                // assume to display pic
                mPlayerView.setVisibility(View.GONE);
                mRecipeImageView.setVisibility(View.VISIBLE);
//                videoURLforResume = thumbnailURL;
//                initializePlayer(Uri.parse(thumbnailURL), videoPosition);
//                if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ){
//                    mRecipeDetailsView.setVisibility(View.GONE);
//                    //mRecipeDetailsView.setVisibility(View.VISIBLE);
//                }


                Glide.with(getContext()).load(thumbnailURL)
                        .override(800, 800)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mRecipeImageView);
            }
            else{
                mPlayerView.setVisibility(View.GONE);
                mRecipeImageView.setVisibility(View.GONE);
                if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ){
                    mRecipeDetailsView.setVisibility(View.VISIBLE);
                }


            }
        }
    }

    private void initializePlayer(Uri mediaUri, long videoPosition){

        if(mExoPlayerView == null){
            TrackSelector trackSlector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayerView = ExoPlayerFactory.newSimpleInstance(getContext(), trackSlector, loadControl);
            mPlayerView.setPlayer(mExoPlayerView);
            String userAgent = Util.getUserAgent(getContext(), "MyExoPlayer");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent),new DefaultExtractorsFactory(), null, null);
            mExoPlayerView.prepare(mediaSource);
            mExoPlayerView.setPlayWhenReady(true);
            mExoPlayerView.seekTo(videoPosition);
        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getActivity(), LOG_TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Do not let MediaButtons restart the player when the app is not visible.

        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayerView.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayerView.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayerView.seekTo(0);
        }

    }



    /**

     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.

     */

    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mExoPlayerView != null) {
            mExoPlayerView.setPlayWhenReady(true);
            mExoPlayerView.seekTo(mPosition);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        Utility.setVideoUrl(getContext(), videoURLforResume);
        releasePlayer();
    }

    @Override
    public void onStart(){
        super.onStart();
        videoURLforResume = Utility.getVideoUrl(getContext());
        if(videoURLforResume != null) {
            initializePlayer(Uri.parse(videoURLforResume), 0);
        }
    }



    @Override
    public void onPause(){
        super.onPause();
        if(mExoPlayerView != null) {
            mExoPlayerView.setPlayWhenReady(false);
        }
    }

    private void releasePlayer(){
        if(mExoPlayerView != null) {
            mPosition = 0;
            mExoPlayerView.stop();
            mExoPlayerView.release();
            mExoPlayerView = null;
        }
    }




}
