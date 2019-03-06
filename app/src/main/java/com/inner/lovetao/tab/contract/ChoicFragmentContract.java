package com.inner.lovetao.tab.contract;

import android.content.Context;

import com.inner.lovetao.core.TaoResponse;
import com.inner.lovetao.tab.bean.BannerBean;
import com.inner.lovetao.tab.bean.FourAcBean;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;

import io.reactivex.Observable;

public interface ChoicFragmentContract {
    interface View extends IView {
        void getBannerDataSu(List<BannerBean> bannerBeanList);

        void getFourAcSu(List<FourAcBean> fourAcBeanList);

        Context getActivity();
    }

    interface Model extends IModel {
        Observable<TaoResponse<List<BannerBean>>> getBannerData(int type);

        Observable<TaoResponse<List<FourAcBean>>> getFourAcData();
    }
}
