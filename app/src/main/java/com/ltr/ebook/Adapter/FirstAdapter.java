package com.ltr.ebook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ltr.ebook.ContentActivity;
import com.ltr.ebook.MyApplication;
import com.ltr.ebook.R;
import com.ltr.ebook.UpdateUIEvent;
import com.ltr.ebook.model.Book;
import com.ltr.ebook.model.database.AppDatabase;
import com.ltr.ebook.model.database.User;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class FirstAdapter extends RecyclerView.Adapter<FirstAdapter.ViewHolder> {
    private List<Book> bookList;
    private Context context;
    private boolean flag;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView book_cover;
        TextView book_title;
        TextView book_author;
        ImageView book_delete;
        TextView read_chapter;

        public ViewHolder(View view) {
            super(view);
            book_cover = view.findViewById(R.id.book_cover);
            book_title = view.findViewById(R.id.book_title);
            book_author = view.findViewById(R.id.book_author);
            book_delete=view.findViewById(R.id.delete);
            read_chapter=view.findViewById(R.id.read_chapter);
        }
    }

    public FirstAdapter(List<Book> items, boolean flag, Context context) {
        this.bookList = items;
        this.context = context;
        this.flag = flag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.first, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        // 设置点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过适配器找位置信息显示对应位置的bookList数组
                int position = holder.getAdapterPosition();
                Book book = bookList.get(position);
                //Intent 是 Android 中用于在不同组件之间传递数据和启动组件的一种机制
                //将context即 activity传入的booklist 再 传入ContentActivity
                Intent intent = new Intent(context, ContentActivity.class);
                // Intent 中添加book和flag
                intent.putExtra("book", book);
                intent.putExtra("flag", flag);
                //启动ContentActivity
                context.startActivity(intent);
            }
        });
        return holder;
    }

    //更新列表项的数据时被调用
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.book_title.setText("<<"+book.getTitle()+">>");
        holder.book_author.setText(book.getAuthor());
        holder.read_chapter.setText(book.getReadChapter());
        holder.book_delete.setOnClickListener(v->{
            new Thread(() -> {
                MyApplication app = (MyApplication) context.getApplicationContext();
                AppDatabase db = app.getDatabase();
                //点击删除后,将flag标记为false,表示去掉收藏再更新表
                User user = db.userDao().get(book.getFictionId(), 1);
                user.setFlag(false);
                db.userDao().updateAll(user);
                //更新视图
                EventBus.getDefault().post(new UpdateUIEvent());
            }).start();
        });
        // 用glide通过地址快速、高效地加载图片
        Glide.with(holder.itemView).load(book.getCover()).into(holder.book_cover);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}
