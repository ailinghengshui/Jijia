package com.hzjytech.operation.module.frags.group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.hzjytech.operation.R;
import com.hzjytech.operation.adapters.group.AddGroupAdapter;
import com.hzjytech.operation.base.BaseFragment;
import com.hzjytech.operation.entity.AddGroupInfo;
import com.hzjytech.operation.http.JijiaHttpSubscriber;
import com.hzjytech.operation.http.RxBus;
import com.hzjytech.operation.http.SubscriberOnNextListener;
import com.hzjytech.operation.http.api.GroupApi;
import com.hzjytech.operation.utils.UserUtils;
import com.hzjytech.operation.widgets.TitleBar;

import java.util.List;

import butterknife.BindView;
import rx.Observable;

import static com.hzjytech.operation.constants.Constants.GroupClick.GROUPSETTING;
import static com.hzjytech.operation.constants.Constants.GroupRefresh.GROUPREFRESHADD;

/**
 * Created by hehongcan on 2017/9/20.
 */

public class AddGroupsFragment extends BaseFragment {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.rv_add_group)
    RecyclerView mRvAddGroup;
    private FragmentActivity mContext;
    private AddGroupAdapter mAdapter;
    private int groupId;
    private int vmId;
    private String mString;

    @Override
    protected int getResId() {
        return R.layout.fragment_add_groups;
    }

    @Override
    protected void initView() {
        initTitle();
        initRecyclerView();
        initData();
    }


    private void initData() {
        Intent intent = mContext.getIntent();
        groupId = intent.getIntExtra("groupId", -1);
        vmId = intent.getIntExtra("vmId", -1);
        Observable<List<AddGroupInfo>> observable = GroupApi.getAddGroups(UserUtils.getUserInfo()
                .getToken(), groupId, vmId);
        JijiaHttpSubscriber subscriber = JijiaHttpSubscriber.buildSubscriber(mContext)
                .setOnNextListener(new SubscriberOnNextListener<List<AddGroupInfo>>() {
                    @Override
                    public void onNext(List<AddGroupInfo> groupList) {
                        mAdapter.setData(groupList);
                    }
                })
                .setProgressDialog(mProgressDlg)
                .build();
        observable.subscribe(subscriber);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvAddGroup.setLayoutManager(linearLayoutManager);
        mAdapter = new AddGroupAdapter(mContext, null);
        mRvAddGroup.setAdapter(mAdapter);
    }

    private void initTitle() {
        final int editOrAdd = getActivity().getIntent().getIntExtra("editOrAdd", -1);
        mContext = getActivity();
        mTitleBar.setLeftTextColor(Color.WHITE);
        mTitleBar.setLeftText(R.string.cancel);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editOrAdd==GROUPSETTING){
                    mContext.getSupportFragmentManager().popBackStack();
                }else{
                    mContext.finish();
                }

            }
        });
        mTitleBar.setTitleColor(Color.WHITE);
        mTitleBar.setTitleBold(true);
        mTitleBar.setTitle("添加分组");
        TitleBar.TextAction textAction = new TitleBar.TextAction("添加") {
            @Override
            public void performAction(View view) {
                mTitleBar.setLeftText("");
                mTitleBar.setLeftImageResource(R.drawable.icon_left);
                commitAdd();
            }
        };
        mTitleBar.addAction(textAction);
    }

    /**
     * 点击确定添加分组
     */
    private void commitAdd() {
        mString = "";
        List<Integer> checkedIds = mAdapter.getCheckedGroup();
        if (checkedIds == null || checkedIds.size() < 1) {
            return;
        }
        for (Integer id : checkedIds) {
            mString += id + ","; }
        if(mString.equals("")){
            return;
        }
            String substring = mString.substring(0, mString.length() - 1);
        Log.e("addsubstring",substring);
            Observable<Object> observable = GroupApi.addGroup(UserUtils.getUserInfo()
                    .getToken(), groupId, substring);
            JijiaHttpSubscriber subscriber = JijiaHttpSubscriber.buildSubscriber(mContext)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            showTip(R.string.add_success);
                            initData();
                            RxBus.getDefault().send(GROUPREFRESHADD);
                            mContext.setResult(Activity.RESULT_OK,new Intent());
                        }
                    })
                    .setProgressDialog(mProgressDlg)
                    .build();
            observable.subscribe(subscriber);

    }

        @Override
        public void onDestroyView () {
            super.onDestroyView();
        }
    }
