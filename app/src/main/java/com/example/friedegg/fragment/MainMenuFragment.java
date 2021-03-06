package com.example.friedegg.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.friedegg.activity.MainActivity;
import com.example.friedegg.R;
import com.example.friedegg.activity.SettingActivity;
import com.example.friedegg.base.BaseFragment;
import com.example.friedegg.modul.MenuItem;

import java.util.ArrayList;

/**
 * Created by 123 on 2016/5/24.
 *
 */
public class MainMenuFragment extends BaseFragment {

    private MainActivity mainActivity;
    private RecyclerView recyclerView;
    private RelativeLayout relativeLayout;
    private LinearLayoutManager mLayoutManager;
    private MenuAdapter mAdapter;
    private MenuItem.FragmentType currentFragment = MenuItem.FragmentType.FreshNews;

    @Override
    //当Fragment与Activity发生关联时调用。
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        } else {
            throw new IllegalArgumentException("The activity must be a MainActivity !");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_container);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
                mainActivity.closeDrawer();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new MenuAdapter();
        addMenuItemsNoSister(mAdapter);
        recyclerView.setAdapter(mAdapter);
    }

    private class MenuAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<MenuItem> menuItems;

        public MenuAdapter() {
            menuItems = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final MenuItem menuItem = menuItems.get(position);

            holder.tv_title.setText(menuItem.getTitle());
            holder.img_menu.setImageResource(menuItem.getResourceId());
            holder.rl_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if (currentFragment != menuItem.getType()) {
                            Fragment fragment = (Fragment) Class.forName(menuItem.getFragment().getName()).newInstance();
                            mainActivity.replaceFragment(R.id.frame_container, fragment);
                            currentFragment = menuItem.getType();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mainActivity.closeDrawer();
                }
            });
        }

        @Override
        public int getItemCount() {
            return menuItems.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_menu;
        private TextView tv_title;
        private RelativeLayout rl_container;


        public ViewHolder(View itemView) {
            super(itemView);
            img_menu = (ImageView) itemView.findViewById(R.id.img_menu);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            rl_container = (RelativeLayout) itemView.findViewById(R.id.rl_container);
        }
    }

    private void addAllMenuItems(MenuAdapter mAdapter) {
        mAdapter.menuItems.clear();
        mAdapter.menuItems.add(new MenuItem("新鲜事", R.drawable.ic_explore_white_24dp, MenuItem.FragmentType.FreshNews,FreshNewsFragment.class));
        mAdapter.menuItems.add(new MenuItem("无聊图", R.drawable.ic_mood_white_24dp, MenuItem.FragmentType.BoringPicture,PictureFragment.class));
        mAdapter.menuItems.add(new MenuItem("妹子图", R.drawable.ic_local_florist_white_24dp, MenuItem.FragmentType.Sister,SisterFragment.class));
        mAdapter.menuItems.add(new MenuItem("段子", R.drawable.ic_chat_white_24dp, MenuItem.FragmentType.Joke, JokeFragment.class));
        mAdapter.menuItems.add(new MenuItem("小电影", R.drawable.ic_movie_white_24dp, MenuItem.FragmentType.Video,VideoFragment.class));
    }

    private void addMenuItemsNoSister(MenuAdapter mAdapter) {
        mAdapter.menuItems.clear();
        mAdapter.menuItems.add(new MenuItem("新鲜事", R.drawable.ic_explore_white_24dp, MenuItem.FragmentType.FreshNews,FreshNewsFragment.class));
        mAdapter.menuItems.add(new MenuItem("无聊图", R.drawable.ic_mood_white_24dp, MenuItem.FragmentType.BoringPicture,PictureFragment.class));
        mAdapter.menuItems.add(new MenuItem("段子", R.drawable.ic_chat_white_24dp, MenuItem.FragmentType.Joke, JokeFragment.class));
        mAdapter.menuItems.add(new MenuItem("小电影", R.drawable.ic_movie_white_24dp, MenuItem.FragmentType.Video,VideoFragment.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sp.getBoolean(SettingFragment.ENABLE_SISTER,false)&&mAdapter.menuItems.size() == 4){
            addAllMenuItems(mAdapter);
            mAdapter.notifyDataSetChanged();
        }else if (!sp.getBoolean(SettingFragment.ENABLE_SISTER, false) && mAdapter.menuItems.size()== 5){
            addMenuItemsNoSister(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }
}
