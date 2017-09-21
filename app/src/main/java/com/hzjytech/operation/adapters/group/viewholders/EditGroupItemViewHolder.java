package com.hzjytech.operation.adapters.group.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzjytech.operation.R;
import com.hzjytech.operation.adapters.group.RemoveGroupAdapter;
import com.hzjytech.operation.entity.GroupList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hehongcan on 2017/9/20.
 */

public class EditGroupItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_edit_group_id)
    TextView mTvEditGroupId;
    @BindView(R.id.iv_edit_group)
    ImageView mIvEditGroup;
    @BindView(R.id.tv_edit_group_name)
    TextView mTvEditGroupName;
    @BindView(R.id.tv_edit_group_note)
    TextView mTvEditGroupNote;
    @BindView(R.id.checkbox_group_isChecked)
    CheckBox checkbox_group_isChecked;


    public EditGroupItemViewHolder(
            View view) {
        super(view);
        //R.layout.edit_group_item
        ButterKnife.bind(this, view);
    }

    public void setData(
            final Context context,
            final ArrayList<GroupList> data,
            final int position,
            final RemoveGroupAdapter editGroupAdapter) {
        mTvEditGroupId.setText(position + "");
        mTvEditGroupName.setText(data.get(position - 1)
                .getName());
        mTvEditGroupNote.setVisibility(View.GONE);
        mIvEditGroup.setImageResource(data.get(position - 1)
                .isIsSuper() ? R.drawable.icon_groups : R.drawable.icon_group);
      checkbox_group_isChecked.setChecked(data.get(position-1).isCheck());
       checkbox_group_isChecked.setOnCheckedChangeListener(new CompoundButton
               .OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               data.get(position-1).setCheck(isChecked);
           }
       });
    }
}
