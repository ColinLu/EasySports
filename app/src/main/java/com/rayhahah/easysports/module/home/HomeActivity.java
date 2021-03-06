package com.rayhahah.easysports.module.home;

import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.rayhahah.easysports.R;
import com.rayhahah.easysports.common.BaseActivity;
import com.rayhahah.easysports.common.C;
import com.rayhahah.easysports.databinding.ActivityHomeBinding;
import com.rayhahah.easysports.module.forum.mvp.ForumFragment;
import com.rayhahah.easysports.module.live.mvp.LiveFragment;
import com.rayhahah.easysports.module.match.mvp.MatchFragment;
import com.rayhahah.easysports.module.mine.mvp.MineFragment;
import com.rayhahah.easysports.module.news.mvp.NewsFragment;
import com.rayhahah.rbase.base.RBaseFragment;
import com.rayhahah.rbase.utils.base.ToastUtils;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity<HomePresenter, ActivityHomeBinding>
        implements HomeContract.IHomeView {

    private long exitTime = 0;
    private ArrayList<RBaseFragment> mFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState) {

        initFragment();
        initBnb();
    }

    private void initFragment() {
        MatchFragment matchFragment = new MatchFragment();
        NewsFragment newsFragment = new NewsFragment();
        LiveFragment liveFragment = new LiveFragment();
        ForumFragment forumFragment = new ForumFragment();
        MineFragment mineFragment = new MineFragment();
        mFragmentList = new ArrayList<>();
        mFragmentList.add(matchFragment);
        mFragmentList.add(newsFragment);
        mFragmentList.add(liveFragment);
        mFragmentList.add(forumFragment);
        mFragmentList.add(mineFragment);
        showFragment(mFragmentList.get(0), 0);
    }

    @Override
    protected HomePresenter getPresenter() {
        return new HomePresenter(this);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }

    @Override
    protected int setFragmentContainerResId() {
        return R.id.fl_home_container;
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtils.showShort("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }

    }

    /**
     * 初始化底部导航栏
     */
    private void initBnb() {
        mBinding.bnbHome
                .addItem(getBnbItem(getResources().getString(R.string.match), R.drawable.ic_svg_match_white_24))
                .addItem(getBnbItem(getResources().getString(R.string.news), R.drawable.ic_svg_news_white_24))
                .addItem(getBnbItem(getResources().getString(R.string.live), R.drawable.ic_svg_live_white_24))
                .addItem(getBnbItem(getResources().getString(R.string.forum), R.drawable.ic_svg_forum_white_24))
                .addItem(getBnbItem(getResources().getString(R.string.mine), R.drawable.ic_svg_mine_white_24))
                .setBarBackgroundColor(R.color.colorDayBg)
                .setMode(BottomNavigationBar.MODE_SHIFTING)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .initialise();

        mBinding.bnbHome.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                showFragment(mFragmentList.get(position), position);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    private BottomNavigationItem getBnbItem(String title, int resId) {
        return new BottomNavigationItem(resId, title).setActiveColor(mThemeColorMap.get(C.ATTRS.COLOR_PRIMARY));
    }

    @Override
    public void showViewLoading() {

    }

    @Override
    public void showViewError(Throwable t) {

    }
}
