package com.hzjytech.operation.adapters.group;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzjytech.operation.R;
import com.hzjytech.operation.adapters.group.viewholders.AddGroupItemViewHolder;
import com.hzjytech.operation.entity.AddGroupInfo;
import com.hzjytech.operation.entity.GroupInfo;
import com.hzjytech.operation.entity.GroupList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hehongcan on 2017/9/20.
 */

public class AddGroupAdapter extends RecyclerView.Adapter {
    private final FragmentActivity mContext;
    private List<AddGroupInfo> mData;
    private final ArrayList<GroupList> mList;
    private final LayoutInflater mInflater;
    private  ArrayList<Integer>  mCheckedIds;

    public AddGroupAdapter(FragmentActivity context, List<AddGroupInfo> data) {
        this.mContext=context;
        this.mData=data;
        mInflater = LayoutInflater.from(context);
        mList = new ArrayList<>();
         mCheckedIds= new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_add_group, parent, false);
        return new AddGroupItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((AddGroupItemViewHolder)holder).setData(mList,position);
    }

    @Override
    public int getItemCount() {
        return mData==null?0:mList.size();
    }

    public void setData(List<AddGroupInfo> data) {
        this.mData=data;
        if(data==null){
            return;
        }
        mList.clear();
        for (AddGroupInfo info : data) {
            mList.add(new GroupList(info.getId(),info.getName(),info.isHaveSub()));
            if(info.getSubGroups()!=null&&info.getSubGroups().size()>0){
                for (AddGroupInfo.SubGroupsBean childInfo : info.getSubGroups()) {
                    mList.add(new GroupList(childInfo.getId(),childInfo.getName(),false));
                }
            }
        }
       notifyDataSetChanged();
    }

    /**
     * 获取选择的分组
     */
    public List<Integer> getCheckedGroup(){
        mCheckedIds.clear();
        if(mList!=null){
            Log.e("checkedlist",mList.toString());
            for (GroupList groupList : mList) {
                if(groupList.isCheck()){
                    mCheckedIds.add(groupList.getId());
                }
            }
            return mCheckedIds;
        }else {
            return null;
        }

    }
}
