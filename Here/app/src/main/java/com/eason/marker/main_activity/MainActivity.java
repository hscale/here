package com.eason.marker.main_activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.eason.marker.R;
import com.eason.marker.about_us_activity.AboutUsActivity;
import com.eason.marker.model.ErroCode;
import com.eason.marker.model.IntentUtil;
import com.eason.marker.model.Post;
import com.eason.marker.util.WidgetUtil.GreenToast;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private android.support.v7.widget.Toolbar toolbar;

    private MainMapFragment mainMapFragment;
    private MainProfileFragment settingFragment;
    private NearUserListFragment nearUserListFragment;
    private MyPostFragment myPostFragment;

    private static final int CHANGE_TOOL_BAR_TITLE_MAIN = 0x1;
    private static final int CHANGE_TOOL_BAR_TITLE_SETTING = 0X2;
    /**
     * MainMapFragment 获取帖子为空
     */
    public static final int NONE_VALID_POST = 0x3;
    private static final int CHANGE_TOOL_BAR_TITLE_NEAR = 0X4;
    private static final int CHANGE_TOOL_BAR_TITLE_MINE = 0X5;

    private static long REFRESH_POST_DATA_TIME = 0;

    /**
     * 判断toolbar是否要显示刷新按键
     */
    private boolean isShowRefreshView = true;

    /**
     * 标记当前显示的是哪个fragment
     */
    public static int FRAGMENT_TAG = 0;

    public static List<Post> postListItem;

    private  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_TOOL_BAR_TITLE_MAIN:
                    //跳转时改变Toobar相应的标题
                    toolbar.setTitle(R.string.app_name);

                    break;
                case CHANGE_TOOL_BAR_TITLE_SETTING:
                    //跳转时改变Toobar相应的标题
                    toolbar.setTitle(R.string.my_profile_page_title);
                    break;
                case CHANGE_TOOL_BAR_TITLE_NEAR:
                    //跳转时改变Toobar相应的标题
                    toolbar.setTitle(R.string.near_list_page_title);
                    break;
                case CHANGE_TOOL_BAR_TITLE_MINE:
                    //跳转时改变Toobar相应的标题
                    toolbar.setTitle(R.string.my_history_page_title);
                    break;
                case NONE_VALID_POST:
                    GreenToast.makeText(MainActivity.this, "附近还没人标记过哦，还不先码一个", Toast.LENGTH_LONG).show();
                    break;
                case ErroCode.ERROR_CODE_REQUEST_FORM_INVALID:
                    GreenToast.makeText(MainActivity.this, "获取附近的标记似乎除了点问题，但是又不知道是为什么。。。", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView(savedInstanceState);
        initParam();
    }

    /*
        初始化控件
     */
    private void initView(Bundle savedInstanceState) {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.universal_white));
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_refresh:

                        mainMapFragment.getPost();
                        break;

                    case R.id.action_about_us:
                        Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });

        //将toolbar与Drawerlayout绑定起来
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.action_settings) {

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (isShowRefreshView) {
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else {
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_main_without_refresh, menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 初始化参数，及相应的布局设置
     */
    private void initParam() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        mainMapFragment = new MainMapFragment();
        settingFragment = new MainProfileFragment();
        nearUserListFragment = new NearUserListFragment();
        myPostFragment = new MyPostFragment();
        transaction.replace(R.id.main_fragment_frame_layout, mainMapFragment);
        transaction.commit();
    }

    public Handler getHandler() {
        return handler;
    }

    /**
     * 切换fragment,切换到fragmentIndex相应的fragment
     *
     * @param fragmentIndex
     */
    public void setFragmentTransaction(int fragmentIndex) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Message msg = new Message();

        switch (fragmentIndex) {

            //切换到主页
            case IntentUtil.MAIN_MAP_FRAGMENT:
                if (mainMapFragment == null) {
                    mainMapFragment = new MainMapFragment();
                }
                transaction.replace(R.id.main_fragment_frame_layout, mainMapFragment);
                msg.what = CHANGE_TOOL_BAR_TITLE_MAIN;
                isShowRefreshView = true;
                FRAGMENT_TAG = IntentUtil.MAIN_MAP_FRAGMENT;
                handler.sendEmptyMessage(msg.what);
                break;

            //切换到设置页面
            case IntentUtil.PROFLIE_FRAGMENT:

                if (settingFragment == null) {
                    settingFragment = new MainProfileFragment();
                }
                transaction.replace(R.id.main_fragment_frame_layout, settingFragment);
                msg.what = CHANGE_TOOL_BAR_TITLE_SETTING;
                isShowRefreshView = false;
                FRAGMENT_TAG = IntentUtil.PROFLIE_FRAGMENT;
                handler.sendEmptyMessage(msg.what);
                break;

            //切换到附近事件列表显示页面
            case IntentUtil.NEAR_USER_FRAGMENT:
                if (nearUserListFragment == null) {
                    nearUserListFragment = new NearUserListFragment();
                }
                transaction.replace(R.id.main_fragment_frame_layout, nearUserListFragment);
                FRAGMENT_TAG = IntentUtil.NEAR_USER_FRAGMENT;
                msg.what = CHANGE_TOOL_BAR_TITLE_NEAR;
                handler.sendEmptyMessage(msg.what);
                isShowRefreshView = false;
                break;

            case IntentUtil.MAIN_TO_LOGIN_PAGE:
                /**
                 * 在这里什么都不需要做，因为只需要关闭Drawerlayout,具体的跳转操作在MainLeftFragment中
                 */
                break;

            case IntentUtil.MY_POST_FRAGMENT:
                if (myPostFragment == null) {
                    myPostFragment = new MyPostFragment();
                }
                transaction.replace(R.id.main_fragment_frame_layout, myPostFragment);
                FRAGMENT_TAG = IntentUtil.MY_POST_FRAGMENT;
                handler.sendEmptyMessage(msg.what);
                msg.what = CHANGE_TOOL_BAR_TITLE_MINE;
                break;
        }

        drawerLayout.closeDrawers();//点击Item后关闭Drawerlayout
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }


        switch (requestCode) {

            case IntentUtil.TO_PUBLISH_PAGE:
                mainMapFragment.getPost();
                break;
        }

    }

    private static long touchTime = 0;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (FRAGMENT_TAG != IntentUtil.MAIN_MAP_FRAGMENT) {//在不是地图fragment的时候按返回键回到地图fragment
            setFragmentTransaction(IntentUtil.MAIN_MAP_FRAGMENT);
            return;
        } else if (currentTime - touchTime > 2000) {
            GreenToast.makeText(MainActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
            touchTime = System.currentTimeMillis();
            return;
        } else {
            System.exit(0);
        }

    }
}