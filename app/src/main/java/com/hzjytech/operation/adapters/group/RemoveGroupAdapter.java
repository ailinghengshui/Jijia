package com.hzjytech.operation.adapters.group;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzjytech.operation.R;
import com.hzjytech.operation.adapters.group.viewholders.EditGroupHeadViewHolder;
import com.hzjytech.operation.adapters.group.viewholders.EditGroupItemViewHolder;
import com.hzjytech.operation.entity.GroupList;
import com.hzjytech.operation.inter.OnInfoClickListener;
import com.hzjytech.operation.inter.OnItemClickListener;
import com.hzjytech.operation.inter.OnItemDelClickListener;

import java.util.ArrayList;


/**
 * Created by hehongcan on 2017/9/19.
 */

public class RemoveGroupAdapter extends RecyclerView.Adapter {
    private static final int TYPE_HEAD = 0;
    private static final int TYPE_ITEM = 1;
    private final FragmentActivity context;
    private ArrayList<GroupList> mData;
    private final LayoutInflater mInflater;
    private OnItemDelClickListener mOnItemDelClickListener;
    private  OnInfoClickListener mOnItemClickListener;

    public RemoveGroupAdapter(FragmentActivity context, ArrayList<GroupList> data) {
        this.context=context;
        mInflater = LayoutInflater.from(context);
        mData=data;
    }
    public void setData(ArrayList<GroupList> data) {
        mData = data;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==TYPE_HEAD){
            View view = mInflater.inflate(R.layout.edit_group_head,parent,false);
            return new EditGroupHeadViewHolder(view);
        }else{
            View view = mInflater.inflate(R.layout.edit_group_item,parent,false);
            return new EditGroupItemViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      if(holder instanceof EditGroupHeadViewHolder){
          holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                 if(mOnItemClickListener!=null){
                     mOnItemClickListener.click(0);
                 }
              }
          });

      }else if(holder instanceof EditGroupItemViewHolder){
          ((EditGroupItemViewHolder)holder).setData(context,mData,position,this);
      }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TYPE_HEAD;
        }else{
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mData==null?0:mData.size()+1;
    }

    public void setOnItemClickListener(OnInfoClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
