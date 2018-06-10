package com.ilatis.vkgalery.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ilatis.vkgalery.PhotoActivity;
import com.ilatis.vkgalery.R;
import com.ilatis.vkgalery.StructClass.Friend;

import java.util.ArrayList;

public class FriendsAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater inflater;
    private ArrayList<Friend> objects;
    private Activity activity;

    private static final String APP_PREFERENCES = "User";
    private static final String APP_PREFERENCES_ID = "id";
    private static final String APP_PREFERENCES_NAME = "name";
    private SharedPreferences mSettings;

    public FriendsAdapter(Context ctx, ArrayList<Friend> objects, Activity activity) {
        this.ctx = ctx;
        this.inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.objects = objects;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.friends_list, viewGroup, false);
        }

        Friend friend = getFriend(i);
        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.friend_name)).
                setText(friend.getFull_name());

        final ImageView imageView = (ImageView) view.findViewById(R.id.friend_photo);
        imageView.setImageBitmap(friend.getPhoto_130());

        //Повесил тут слушатель на картинку, честно первый раз работаю с графикой,
        //поэтому если не прав, сорре
        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                mSettings = ctx.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSettings.edit();
                String ids = objects.get(i).getId();
                String name = objects.get(i).getFull_name();
                editor.putString(APP_PREFERENCES_ID, ids);
                editor.putString(APP_PREFERENCES_NAME, name);
                editor.apply();
                Intent intent = new Intent(ctx, PhotoActivity.class);
                Friend friendSend = getFriend(i);
                intent.putExtra(Friend.class.getSimpleName(), friendSend.getPhoto_130());

                ctx.startActivity(intent);
                //TODO сделать анимацию красивой (не получилось)
                activity.overridePendingTransition(R.anim.one, R.anim.two);

            }
        });
        return view;

        //PS. Видимо надо было как-то по-другому обрабатывать клики по фотографиям//
    }

    private Friend getFriend(int position) {
        return ((Friend) getItem(position));
    }
}
