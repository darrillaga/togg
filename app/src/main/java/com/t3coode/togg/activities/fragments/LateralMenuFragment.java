package com.t3coode.togg.activities.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.t3coode.togg.R;
import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.LoginActivity;
import com.t3coode.togg.activities.OnLateralMenuClickListener;
import com.t3coode.togg.activities.adapters.MenuAdapter;
import com.t3coode.togg.activities.adapters.MenuAdapter.MenuItem;
import com.t3coode.togg.activities.utils.Resources;
import com.t3coode.togg.activities.utils.Resources.ResourceCallback;
import com.t3coode.togg.services.dtos.UserDTO;
import com.t3coode.ui.DrawerLayoutActivity;

public class LateralMenuFragment extends BaseFragment implements
        ResourceCallback, OnLateralMenuClickListener {

    private UserDTO mCurrentUser;
    private ImageView mUserAvatar;

    private ListView mMenuListView;
    private MenuAdapter mAdapter;
    private OnLateralMenuClickListener mListener;
    private List<MenuItem> mMenuList = new ArrayList<MenuItem>();

    public void initMenuItems() {
        addItemIfShould(R.drawable.ic_settings, R.string.settings,
                R.id.settings);
        addItemIfShould(R.drawable.ic_logout, R.string.logout, R.id.logout);
    }

    private void addItemIfShould(int menuIconId, int menuLabelId, int menuId) {
        MenuItem menuItem = new MenuItem(menuIconId, menuLabelId, menuId);
        mMenuList.add(menuItem);
        if (mListener == null || mListener.shouldSelectItem(menuId)) {
            menuItem.setSelected(true);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof OnLateralMenuClickListener) {
            this.mListener = (OnLateralMenuClickListener) activity;
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_lateral_menu, container,
                false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {

        this.mCurrentUser = ToggApp.getApplication().getCurrentUser();

        this.mMenuListView = (ListView) v.findViewById(R.id.menu_list);

        initMenuItems();

        this.mAdapter = new MenuAdapter(getActivity(), mMenuList);
        this.mUserAvatar = (ImageView) getView().findViewById(R.id.user_avatar);

        mMenuListView.addFooterView(LayoutInflater.from(getActivity()).inflate(
                R.layout.layout_menu_separator, mMenuListView, false));
        mMenuListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(
                R.layout.layout_menu_separator, mMenuListView, false));
        mMenuListView.setAdapter(mAdapter);

        mMenuListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                    int position, long id) {
                onMenuItemClick(adapter, view, position, id);
            }
        });

        ((TextView) v.findViewById(R.id.user_name)).setText(mCurrentUser
                .getFullname());
        super.onViewCreated(v, savedInstanceState);
    }

    @Override
    public void onResume() {
        if (mUserAvatar.getBackground() == null
                && mCurrentUser.getImageUrl() != null) {
            setUserAvatar(Resources.getInstance().loadAsync(this,
                    mCurrentUser.getImageUrl()));
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        Resources.getInstance().removeBindIfExists(mCurrentUser.getImageUrl(),
                this);

        super.onStop();
    }

    public void onMenuItemClick(AdapterView<?> adapter, View view,
            int position, long id) {

        MenuItem menuItem = ((MenuItem) adapter.getItemAtPosition(position));

        if (mListener == null || !mListener.onMenuOptionClick(menuItem, view)) {
            onMenuOptionClick(menuItem, view);
        }

        ((DrawerLayoutActivity) getActivity()).closeDrawer();
    }

    @Override
    public boolean onMenuOptionClick(MenuItem menuItem, View view) {
        int itemId = menuItem.getId();

        switch (itemId) {
        case R.id.settings:
            settings();
            break;
        case R.id.logout:
            logout();
            break;
        }
        return true;
    }

    private void settings() {
        Fragment fragment = getFragmentManager().findFragmentByTag(
                SettingsPreferencesFragment.class.getName());

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        if (fragment == null) {
            transaction.replace(R.id.layout_container,
                    new SettingsPreferencesFragment(),
                    SettingsPreferencesFragment.class.getName())
                    .addToBackStack(null);
        } else {
            transaction.show(fragment);
        }
        transaction.commit();
    }

    private void logout() {
        Intent i = new Intent();
        ToggApp.getApplication().resetUser();
        i.setClass(getActivity(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        getActivity().finish();
    }

    @Override
    public void onLoadedResource(String url, Drawable resource) {
        setUserAvatar(resource);
    }

    private void setUserAvatar(Drawable avatar) {
        if (avatar != null) {

            mUserAvatar.setBackgroundDrawable(avatar);
            getView().findViewById(R.id.user_avatar_loader).setVisibility(
                    View.GONE);
        }
    }

    @Override
    public boolean shouldSelectItem(int itemId) {
        return false;
    }

}
