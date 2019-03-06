package com.inner.lovetao.tab.tabfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inner.lovetao.R;
import com.inner.lovetao.config.ConfigInfo;
import com.inner.lovetao.tab.bean.BannerBean;
import com.inner.lovetao.tab.bean.FourAcBean;
import com.inner.lovetao.tab.bean.ProductItemBean;
import com.inner.lovetao.tab.contract.ChoicFragmentContract;
import com.inner.lovetao.tab.di.component.DaggerChoiceFragmentComponent;
import com.inner.lovetao.tab.mvp.ChoiceFragmentPresenter;
import com.inner.lovetao.tab.view.ChoiceBannerView;
import com.inner.lovetao.tab.view.RecommendTwoView;
import com.inner.lovetao.tab.view.RecommendView;
import com.inner.lovetao.weight.LoadMoreFooterView;
import com.inner.lovetao.weight.PullToRefreshDefaultHeader;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * desc:精选
 * Created by xcz
 * on 2019/1/22.
 */
public class ChoiceFragment extends BaseFragment<ChoiceFragmentPresenter> implements ChoicFragmentContract.View {
    @BindView(R.id.pull_to_refresh_layout)
    PtrFrameLayout ptrFrameLayout;
    @BindView(R.id.fm_recyclerView)
    RecyclerView recyclerView;
    @Inject
    Dialog mDialog;
    private List<ProductItemBean> datas = new ArrayList<>();
    private ChoiceBannerView bannerView;
    private HeaderAndFooterWrapper headerAndFooterWrapper;
    private LinearLayoutManager layoutManager;
    private boolean isRefreshing;//是否正在加载
    private boolean mPullDown = true;
    private boolean noMoredata;//是否已经没有更多
    private int pageNum=1;
    private LoadMoreFooterView loadMoreFooterView;
    private RecommendTwoView recommendTwoView;
    private RecommendView recommendView;


    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerChoiceFragmentComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);

    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choice, container, false);
        return view;
    }


    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initPullToRefresh();
        initRecycleView();
        testAddProduct();
        mPresenter.getBanner(1);
        mPresenter.getFourAc();
        mPresenter.getJingPinData(pageNum,5);
testAddProduct();
    }


    @Override
    public void setData(@Nullable Object data) {

    }

    private void initPullToRefresh() {
        PullToRefreshDefaultHeader header = new PullToRefreshDefaultHeader(mContext);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //下拉刷新回调
                testAddProduct();
                mPresenter.getBanner(1);
                mPresenter.getFourAc();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount() - 1) && !isRefreshing && !noMoredata) {
                    pullUpRequest();
                }
            }
        });
    }

    private void initRecycleView() {
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        CommonAdapter<ProductItemBean> adapter = new CommonAdapter<ProductItemBean>(mContext, R.layout.item_home_choice, datas) {
            @Override
            protected void convert(ViewHolder holder, ProductItemBean productItemBean, int position) {
                holder.setText(R.id.tv_product_name, productItemBean.getTitle());
                holder.setText(R.id.tv_product_prise, productItemBean.getZkFinalPrice());
                holder.setText(R.id.tv_product_quan, productItemBean.getCouponStartFee());
                holder.setText(R.id.tv_product_already_num, productItemBean.getZkFinalPrice());
                holder.setText(R.id.tv_product_quan_after, productItemBean.getZkFinalPrice());
            }
        };
        headerAndFooterWrapper = new HeaderAndFooterWrapper(adapter);
        loadMoreFooterView = new LoadMoreFooterView(mContext);
        loadMoreFooterView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bannerView = new ChoiceBannerView(mContext);
        recommendView = new RecommendView(mContext);
        recommendView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        recommendTwoView = new RecommendTwoView(mContext);
        headerAndFooterWrapper.addHeaderView(bannerView);
        headerAndFooterWrapper.addHeaderView(recommendView);
        headerAndFooterWrapper.addHeaderView(recommendTwoView);
        headerAndFooterWrapper.addFootView(loadMoreFooterView);
        recyclerView.setAdapter(headerAndFooterWrapper);
        headerAndFooterWrapper.notifyDataSetChanged();
    }

    /**
     * 模拟下拉刷新
     */
    private void testAddProduct() {
        if (!mPullDown) {
            return;
        }
        pageNum = 1;
        mPresenter.getJingPinData(pageNum,5);
        noMoredata = false;
        isRefreshing = true;
    }

    /**
     * 模拟上拉加载
     */
    private void pullUpRequest() {
        if (noMoredata) {
            return;
        }
        mPullDown = false;
        pageNum++;
        isRefreshing = true;
        mPresenter.getJingPinData(pageNum,5);

    }


    @Override
    public void showMessage(@NonNull String message) {
        ArmsUtils.makeText(getContext(), message);
    }

    @Override
    public void showLoading() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void getBannerDataSu(List<BannerBean> bannerBeanList) {
        bannerView.setData(bannerBeanList);
    }

    @Override
    public void getFourAcSu(List<FourAcBean> fourAcBeanList) {
        if (fourAcBeanList != null) {
            recommendTwoView.setData(fourAcBeanList);
        }
    }

    @Override
    public void getJPdataSu(List<ProductItemBean> jingPingList) {
        if (jingPingList.size()< ConfigInfo.PAGE_SIZE){
            noMoredata=true;
        }
        if (pageNum==1){
            datas.clear();
        }
        if (jingPingList!=null){
            datas.addAll(jingPingList);
        }
        headerAndFooterWrapper.notifyDataSetChanged();
        isRefreshing = false;
        mPullDown=true;
        ptrFrameLayout.refreshComplete();

    }
}
