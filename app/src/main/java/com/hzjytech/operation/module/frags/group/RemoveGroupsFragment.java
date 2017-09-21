package com.hzjytech.operation.module.frags.group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.hzjytech.operation.R;
import com.hzjytech.operation.adapters.group.RemoveGroupAdapter;
import com.hzjytech.operation.base.BaseFragment;
import com.hzjytech.operation.entity.GroupInfo;
import com.hzjytech.operation.entity.GroupList;
import com.hzjytech.operation.entity.User;
import com.hzjytech.operation.http.JijiaHttpSubscriber;
import com.hzjytech.operation.http.RxBus;
import com.hzjytech.operation.http.SubscriberOnNextListener;
import com.hzjytech.operation.http.api.GroupApi;
import com.hzjytech.operation.http.api.MachinesApi;
import com.hzjytech.operation.inter.OnInfoClickListener;
import com.hzjytech.operation.utils.CommonUtil;
import com.hzjytech.operation.utils.UserUtils;
import com.hzjytech.operation.widgets.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import static com.hzjytech.operation.constants.Constants.GroupRefresh.GROUPREFRESHADD;

/**
 * Created by hehongcan on 2017/9/19.
 *
 */

public class RemoveGroupsFragment extends BaseFragment implements  OnInfoClickListener {
    private static final String ARGUMENT = "argument";
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.rv_edit_group)
    RecyclerView mRvEditGroup;
    @BindView(R.id.pcfl_more_group)
    PtrClassicFrameLayout mPcflMoreGroup;
    private FragmentActivity mContext;
    private RemoveGroupAdapter editAdapter;
    private RecyclerAdapterWithHF mAdapter;
    private PtrHandler mPtrHandler = new PtrHandler() {
        @Override
        public boolean checkCanDoRefresh(
                PtrFrameLayout frame, View content, View header) {
            return false;
        }

        @Override
        public void onRefreshBegin(PtrFrameLayout frame) {

        }
    };
    private int groupId;
    private JijiaHttpSubscriber mSubscriber;
    private ArrayList<GroupList> subGroups;
    private Subscription mSubscription;
    private JijiaHttpSubscriber mInitSubscriber;
    private String mS;

    @Override
    protected int getResId() {
        return R.layout.fragment_edit_groups;
    }
    @Override
    protected void initView() {
        initTitle();
        initRecyclerView();
        initPtrc();
        initData();
        initRxbus();
    }

    private void initRxbus() {
        if(mSubscription==null){
            mSubscription = RxBus.getDefault()
                    .toObservable()
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            if (o.equals(GROUPREFRESHADD) ) {
                                initData();
                            }
                        }
                    });
        }
    }

    private void initData() {
        User userInfo = UserUtils.getUserInfo();
        Intent intent = mContext.getIntent();
        groupId = intent.getIntExtra("groupId", -1);
        Observable<GroupInfo> observable = MachinesApi.getGroupInfo(userInfo.getToken(), groupId);
        mInitSubscriber = JijiaHttpSubscriber.buildSubscriber(mContext)
                .setOnNextListener(new SubscriberOnNextListener<GroupInfo>() {
                    @Override
                    public void onNext(GroupInfo groupInfo) {
                        Log.e("groupInfo",groupInfo.toString());
                        if (groupInfo == null) {
                            return;
                        }
                        subGroups.clear();
                        for (GroupInfo.SubGroupBean subGroupBean : groupInfo.getSubGroups()) {
                            subGroups.add(new GroupList(subGroupBean.getId(),
                                    subGroupBean.getName(),
                                    false));
                        }
                        Log.e("removeLeftGroups",subGroups.size()+"");
                        editAdapter.setData(subGroups);
                    }
                })
                .setProgressDialog(mProgressDlg)
                .build();
        observable.subscribe(mInitSubscriber);

    }

    private void initPtrc() {
        mPcflMoreGroup.setPtrHandler(mPtrHandler);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvEditGroup.setLayoutManager(linearLayoutManager);
        editAdapter = new RemoveGroupAdapter(mContext, null);
        mAdapter = new RecyclerAdapterWithHF(editAdapter);
        mRvEditGroup.setAdapter(mAdapter);
        editAdapter.setOnItemClickListener(this);
        subGroups=new ArrayList<>();
    }

    /**
     * 初始化标题栏
     */
    private void initTitle() {
        mContext = getActivity();
        mTitleBar.setLeftText("取消");
        mTitleBar.setLeftTextColor(Color.WHITE);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveGroupsFragment.this.getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        mTitleBar.setTitleColor(Color.WHITE);
        mTitleBar.setTitleBold(true);
        mTitleBar.setTitle("包含分组");
        TitleBar.TextAction textAction = new TitleBar.TextAction("移除") {
            @Override
            public void performAction(View view) {
                mTitleBar.setLeftText("");
                mTitleBar.setLeftImageResource(R.drawable.icon_left);
                commitEditInfo();
            }
        };
        mTitleBar.addAction(textAction);

    }

    /**
     * 真实联网修改数据
     */
    private void commitEditInfo() {
        mS = "";
        if(subGroups==null||subGroups.size()<1){
            return;
        }
        for (GroupList subGroup : subGroups) {
            if(subGroup.isCheck()){
                mS += subGroup.getId()+",";
            }
        }
        if(mS.equals("")){
            return;
        }
        String substring = mS.substring(0, mS.length() - 1);
        Log.e("removesubstring",substring);
        Observable<Object> observable = GroupApi.detachGroup(UserUtils.getUserInfo()
                .getToken(), groupId, substring);
        mSubscriber = JijiaHttpSubscriber.buildSubscriber(mContext)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        initData();
                        mContext.setResult(Activity.RESULT_OK,new Intent());
                        showTip(R.string.delete_success);
                    }
                })
                .setProgressDialog(mProgressDlg)
                .build();
        observable.subscribe(mSubscriber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(mSubscriber,mSubscription,mInitSubscriber);
    }


    @Override
    public void click(int type) {
        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.groups_content, new AddGroupsFragment(), null);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
