package com.ltr.ebook.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ltr.ebook.OneTypeActivity;
import com.ltr.ebook.R;
import com.ltr.ebook.Utils.HttpUtils;
import com.ltr.ebook.model.ThirdItem;

import java.util.List;

import android.widget.RelativeLayout;
import android.content.Intent;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {
    private List<ThirdItem> classList;
    private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view;
        TextView item_class;

        public ViewHolder(View view) {
            super(view);
            image_view = view.findViewById(R.id.image_view);
            item_class=view.findViewById(R.id.item_class);
        }
    }
    public ClassAdapter(List<ThirdItem> classList,Activity activity){
        this.classList=classList;
        this.activity=activity;
    }

    @NonNull
    @Override
    public ClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        final ClassAdapter.ViewHolder holder=new ClassAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClassAdapter.ViewHolder holder, int position) {
        ThirdItem s=classList.get(position);

        holder.item_class.setText(s.getText());
        holder.image_view.setImageResource(s.getImageResId());

//        holder.item_class.setOnClickListener(v->{
//            activity.runOnUiThread(()->{
//                TextView title= activity.findViewById(R.id.toolbar_title);
//                title.setText(s.getText());
//            });
//            HttpUtils.fiction(activity,"fictionType",s.getText(),3);
//        });
        holder.image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理点击事件
                Intent intent = new Intent(activity, OneTypeActivity.class);
                intent.putExtra("fictionType",s.getText());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }
}
