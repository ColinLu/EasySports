package com.rayhahah.easysports.module.news.business;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.rayhahah.easysports.R;
import com.rayhahah.easysports.common.BaseFragment;
import com.rayhahah.easysports.common.C;
import com.rayhahah.easysports.databinding.FragmentNewslistBinding;
import com.rayhahah.easysports.module.news.bean.NewsIndex;
import com.rayhahah.easysports.module.news.bean.NewsItem;
import com.rayhahah.easysports.view.DividerItemDecoration;
import com.rayhahah.rbase.utils.base.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a on 2017/6/7.
 */

public class NewsListFragment extends BaseFragment<NewsListPresenter, FragmentNewslistBinding>
        implements NewsListContract.INewsListView, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemClickListener {

    //Fragment在Tab中位置
    private String mTabIndex;
    //Fragment类型
    private String mTabType;
    private List<String> indexList = new ArrayList<>();
    //数据查询起始位置
    private int start = 0;
    //每次加载条目数
    private int pageNum = 10;
    private NewsListAdapter mNewsListAdapter;

    @Override
    protected int setFragmentLayoutRes() {
        return R.layout.fragment_newslist;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mTabIndex = getParmValueFormPrePage(C.NEWS.TAB_INDEX);
        mTabType = getParmValueFormPrePage(C.NEWS.TAB_TYPE);

        initRv();
        initProgressLayout();

        mPresenter.getNewsIndex(mTabType);
    }


    @Override
    protected NewsListPresenter getPresenter() {
        return new NewsListPresenter(this);
    }

    @Override
    public void getNewsIndex(NewsIndex newsIndex) {
//        mBinding.tvNewsListTest.setText(newsIndex.toString());
        List<NewsIndex.DataBean> data = newsIndex.getData();
        indexList.clear();
        for (NewsIndex.DataBean indexInfo : data) {
            indexList.add(indexInfo.getId());
        }
        start = 0;
        mPresenter.getNewsItem(mTabType, parseIndexs(indexList), C.STATUS.INIT);
    }

    @Override
    public void getNewsItem(List<NewsItem.DataBean.ItemInfo> data, int status) {
        switch (status) {
            case C.STATUS.INIT:
            case C.STATUS.REFRESH:
                mNewsListAdapter.setNewData(data);
                if (mBinding.srlNewsList.isRefreshing()) {
                    mBinding.srlNewsList.setRefreshing(false);
                }
                break;
            case C.STATUS.LOAD_MORE:
                mNewsListAdapter.addData(data);
                mNewsListAdapter.loadMoreComplete();
                break;
        }
        showContent(mBinding.srlNewsList, mBinding.pl);
    }

    @Override
    public void getNewsError(Throwable t, int status) {
        switch (status) {
            case C.STATUS.INIT:
            case C.STATUS.REFRESH:
                if (mBinding.srlNewsList.isRefreshing()) {
                    mBinding.srlNewsList.setRefreshing(false);
                }
                break;
            case C.STATUS.LOAD_MORE:
                mNewsListAdapter.loadMoreFail();
                break;
        }
        showError(mBinding.srlNewsList, mBinding.pl);
    }

    @Override
    public void showViewLoading() {
        showLoading(mBinding.srlNewsList, mBinding.pl);
    }

    @Override
    public void showViewError(Throwable t) {


    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.getNewsItem(mTabType, parseIndexs(indexList), C.STATUS.LOAD_MORE);
    }

    @Override
    public void onRefresh() {
        mPresenter.getNewsIndex(mTabType);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        List<NewsItem.DataBean.ItemInfo> data = adapter.getData();
        ToastUtils.showShort(data.get(position).getNewsId());
    }


    /**
     * 初始化条目列表
     */
    private void initRv() {
        mBinding.rvNewsList.setLayoutManager(new LinearLayoutManager(getActivity()
                , LinearLayoutManager.VERTICAL, false));
        mNewsListAdapter = new NewsListAdapter();
        mBinding.rvNewsList.setAdapter(mNewsListAdapter);
        mBinding.rvNewsList.addItemDecoration(
                new DividerItemDecoration(getActivity()
                        , DividerItemDecoration.HORIZONTAL_LIST
                        , 3
                        , mThemeColorMap.get(C.ATTRS.COLOR_BG_DARK)));
        mNewsListAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mNewsListAdapter.isFirstOnly(true);
        mNewsListAdapter.setOnLoadMoreListener(this, mBinding.rvNewsList);
        mNewsListAdapter.setOnItemClickListener(this);
        mBinding.srlNewsList.setOnRefreshListener(this);
        mBinding.srlNewsList.setColorSchemeColors(mThemeColorMap.get(C.ATTRS.COLOR_PRIMARY));
    }

    /**
     * 初始化ProgressLayout
     */
    private void initProgressLayout() {
        mBinding.pl.setColor(mThemeColorMap.get(C.ATTRS.COLOR_TEXT_LIGHT)
                , mThemeColorMap.get(C.ATTRS.COLOR_PRIMARY));
        mBinding.pl.setRefreshClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    /**
     * 获取当前位置后10条数据
     *
     * @param indexs 新闻索引集合
     * @return id组合字符串   example:1234,234,5665
     */
    private String parseIndexs(List<String> indexs) {
        int size = indexs.size();
        String articleIds = "";
        for (int i = start, j = 0; i < size && j < pageNum; i++, j++, start++) {
            articleIds += indexs.get(i) + ",";
        }
        if (!TextUtils.isEmpty(articleIds))
            articleIds = articleIds.substring(0, articleIds.length() - 1);
        return articleIds;
    }
}
