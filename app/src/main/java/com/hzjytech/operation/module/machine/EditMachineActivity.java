package com.hzjytech.operation.module.machine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.hzjytech.operation.R;
import com.hzjytech.operation.base.BaseActivity;
import com.hzjytech.operation.module.frags.group.AddGroupsFragment;
import com.hzjytech.operation.module.frags.group.FirstGroupFragment;
import com.hzjytech.operation.module.frags.group.RemoveGroupsFragment;
import com.hzjytech.operation.module.frags.machine.AddMachineFragment;
import com.hzjytech.operation.module.frags.machine.FirstMachineFragment;
import com.hzjytech.operation.module.frags.machine.RemoveMachineFragment;
import com.hzjytech.operation.utils.NavHelper;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hzjytech.operation.constants.Constants.GroupClick.GROUPADDMORE;
import static com.hzjytech.operation.constants.Constants.GroupClick.GROUPSETTING;
import static com.hzjytech.operation.constants.Constants.GroupClick.MACHINEADDMORE;
import static com.hzjytech.operation.constants.Constants.GroupClick.MACHINESETTING;

/**
 * Created by hehongcan on 2017/9/21.
 */

public class EditMachineActivity extends BaseActivity {

    @BindView(R.id.machines_content)
    FrameLayout mMachinesContent;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected int getResId() {
        return R.layout.activity_edit_machine;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        int editOrAdd = intent.getIntExtra("editOrAdd", -1);
        NavHelper<Object> helper = new NavHelper<>(this,R.id.machines_content,getSupportFragmentManager(),null);
        helper.add(0,new NavHelper.Tab<Object>(FirstMachineFragment.class,null));
        helper.add(1,new NavHelper.Tab<Object>(RemoveMachineFragment.class,null));
        helper.add(2,new NavHelper.Tab<Object>(AddMachineFragment.class,null));
        //编辑页面需要参数无法直接跳转
        if(editOrAdd==MACHINESETTING){
            helper.performClickMenu(0);
        }else if(editOrAdd==MACHINEADDMORE){
            helper.performClickMenu(2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
