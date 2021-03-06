package com.inner.lovetao.mineassets.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.inner.lovetao.mineassets.mvp.contract.EarningsContract;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import javax.inject.Inject;


/**
 * ================================================
 * Description:
 * <p>
 * Created by feihaokui on 02/15/2019 11:18
 */
@ActivityScope
public class EarningsModel extends BaseModel implements EarningsContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public EarningsModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }
}