package com.hzjytech.operation.module.frags.group;


import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.hzjytech.operation.R;
import com.hzjytech.operation.adapters.group.MoreGroupAdapter;
import com.hzjytech.operation.base.BaseFragment;
import com.hzjytech.operation.entity.GroupInfo;
import com.hzjytech.operation.entity.GroupList;
import com.hzjytech.operation.entity.User;
import com.hzjytech.operation.http.JijiaHttpSubscriber;
import com.hzjytech.operation.http.SubscriberOnNextListener;
import com.hzjytech.operation.http.api.MachinesApi;
import com.hzjytech.operation.utils.UserUtils;
import com.hzjytech.operation.widgets.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;

/**
 * Created by hehongcan on 2017/9/19.
 */

public class FirstGroupFragment extends BaseFragment {
    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.rv_more_group)
    RecyclerView rvMoreGroup;
    @BindView(R.id.pcfl_more_group)
    PtrClassicFrameLayout pcflMoreGroup;
    private MoreGroupAdapter groupAdapter;
    private RecyclerAdapterWithHF mAdapter;
    private int groupId;
    private PtrHandler ptrDefaultHandler = new PtrDefaultHandler() {
        @Override
        public void onRefreshBegin(PtrFrameLayout frame) {
            initData();
        }
    };
    private OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void loadMore() {
            initData();
        }
    };
    private List<GroupList> subGroups = new ArrayList<>();
    private FragmentActivity mContext;

    @Override
    protected int getResId() {
        return R.layout.fragment_more_group;
    }

    @Override
    protected void initView() {
        initTitle();
        initRecyclerView();
        initPtrc();
        initData();


    }

    /**
     * 初始化下拉刷新、上拉加载更多
     */
    private void initPtrc() {
        pcflMoreGroup.setPtrHandler(ptrDefaultHandler);//设置下拉监听
        pcflMoreGroup.setOnLoadMoreListener(onLoadMoreListener);//设置上拉监听
        pcflMoreGroup.setLoadMoreEnable(false);//设置可以加载更多
    }

    /**
     * 初始化recyclerview
     */
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvMoreGroup.setLayoutManager(linearLayoutManager);
        groupAdapter = new MoreGroupAdapter(mContext, null);
        mAdapter = new RecyclerAdapterWithHF(groupAdapter);
        rvMoreGroup.setAdapter(mAdapter);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        User userInfo = UserUtils.getUserInfo();
        Intent intent = mContext.getIntent();
        groupId = intent.getIntExtra("groupId", -1);
        Observable<GroupInfo> observable = MachinesApi.getGroupInfo(userInfo.getToken(), groupId);
        JijiaHttpSubscriber subscriber = JijiaHttpSubscriber.buildSubscriber(mContext)
                .setOnNextListener(new SubscriberOnNextListener<GroupInfo>() {
                    @Override
                    public void onNext(GroupInfo groupInfo) {
                        if (groupInfo == null) {
                            return;
                        }
                        subGroups.clear();
                        for (GroupInfo.SubGroupBean subGroupBean : groupInfo.getSubGroups()) {
                            subGroups.add(new GroupList(subGroupBean.getId(),
                                    subGroupBean.getName(),
                                    false));
                        }
                        groupAdapter.setGroupData(subGroups);
                    }
                })
                .setProgressDialog(pcflMoreGroup.isRefreshing() || pcflMoreGroup.isLoadingMore()
                        ? null : mProgressDlg)
                .setPtcf(pcflMoreGroup)
                .build();
        observable.subscribe(subscriber);
    }

    /**
     * 初始化标题栏
     */
    private void initTitle() {
        mContext = getActivity();
        titleBar.setLeftImageResource(R.drawable.icon_left);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
            }
        });
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setTitleBold(true);
        titleBar.setTitle("包含分组");
        TitleBar.TextAction textAction = new TitleBar.TextAction("编辑") {
            @Override
            public void performAction(View view) {
                FragmentManager fragmentManager = mContext.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.groups_content, new RemoveGroupsFragment(), null);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };
        titleBar.addAction(textAction);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
