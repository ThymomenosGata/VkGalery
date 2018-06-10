package com.ilatis.vkgalery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.ilatis.vkgalery.Adapters.FriendsAdapter;
import com.ilatis.vkgalery.StructClass.Friend;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class FriendsActivity extends AppCompatActivity {

    private ArrayList<Friend> arrayList = new ArrayList<>();
    private FriendsAdapter adapter;
    //UI
    private ListView mListView;
    private View mProgress;
    //For cash
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mListView = (ListView)findViewById(R.id.friends_container);
        mProgress = (View)findViewById(R.id.progressBar);
        getPhoto();
    }


    public void getPhoto(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        VKRequest request = VKApi.friends().
                get(VKParameters.from(VKApiConst.FIELDS,"photo"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKList list =  (VKList) response.parsedModel;
                showProgress(true);
                new DownloadImageTask(list).execute();
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mListView.setVisibility(show ? View.GONE : View.VISIBLE);
        mListView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mListView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    /*Вообще AnsyncTask'и использовать для сетевого взаимодействия,
    как я читал, вроде не очень хорошо, но увы:
     */
    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, ArrayList<Friend>> {
        VKList mList;

        DownloadImageTask(VKList list) {
            mList = list;
        }

        protected ArrayList<Friend> doInBackground(String... urls) {

            Bitmap mIcon11 = null;
            for(int i = 0; i<mList.size(); i++){
                try {
                    String photo = mList.get(i).fields.getString("photo");
                    if(getBitmapFromMemCache(photo) != null)
                        mIcon11 = getBitmapFromMemCache(photo);
                    else {
                        InputStream in = new java.net.URL(photo).openStream();
                        mIcon11 = BitmapFactory.decodeStream(in);
                        addBitmapToMemoryCache(photo, mIcon11);
                    }
                    arrayList.add(new Friend(mList.get(i).fields.getString("id"),
                            mList.get(i).fields.getString("first_name") + " "
                                    + mList.get(i).fields.getString("last_name"), mIcon11));
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            return arrayList;
        }

        protected void onPostExecute(ArrayList<Friend> arrayLists) {
            showProgress(false);
            mListView.setVerticalScrollBarEnabled(false);
            adapter = new FriendsAdapter(getBaseContext(),arrayLists, FriendsActivity.this);
            mListView.setAdapter(adapter);
        }
    }

    //Methods for cash
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
