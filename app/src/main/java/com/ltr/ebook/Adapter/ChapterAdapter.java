package com.ltr.ebook.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ltr.ebook.MyApplication;
import com.ltr.ebook.R;
import com.ltr.ebook.UpdateUIEvent;
import com.ltr.ebook.model.Book;
import com.ltr.ebook.model.Chapter;
import com.ltr.ebook.model.database.AppDatabase;
import com.ltr.ebook.model.database.User;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder>{
    private List<Chapter> chapters;
    private Activity activity;
    private Book book;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_chapter;

        public ViewHolder(View view) {
            super(view);
            item_chapter=view.findViewById(R.id.item_chapter);
        }
    }
    public ChapterAdapter(Book book,List<Chapter> chapters,Activity activity){
        this.chapters=chapters;
        this.activity=activity;
        this.book=book;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chapter, parent, false);
        final ViewHolder holder=new ChapterAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterAdapter.ViewHolder holder, int position) {
        String s=chapters.get(position).getTitle();
        holder.item_chapter.setText(s);
        holder.item_chapter.setOnClickListener(v->{
            Chapter chapter=chapters.get(position);
            fictionContentByChapter(activity,book,chapter,position);
        });
    }

    public static List<String> fictionContentByChapter(final Activity activity,Book book,Chapter chapter,int position){
        List<String> contents=new ArrayList<>();
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("https://api.pingcc.cn/fictionContent/search/"+chapter.getChapterId())
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onFailure(call, new IOException("Unexpected Code: " + response));
                } else {
                    try {

                        String responseBody = response.body().string();
                        // 解析JSON数据
                        try {
                            JSONTokener tokener = new JSONTokener(responseBody);
                            JSONObject json = (JSONObject) tokener.nextValue();
                            JSONArray data = json.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                contents.add(data.get(i).toString());
                            }
                            setPosition(activity,book,position,chapter.getTitle());
                            activity.runOnUiThread(()->{
                                RecyclerView recyclerView = activity.findViewById(R.id.book_content);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                                recyclerView.setLayoutManager(layoutManager);
                                ContentAdapter adapter=new ContentAdapter(contents);
                                recyclerView.setAdapter(adapter);
                                TextView title= activity.findViewById(R.id.toolbar_title);
                                title.setText(chapter.getTitle());
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return contents;
    }
    public static void setPosition(Activity activity,Book book,int position,String readChapter){
        MyApplication app = (MyApplication) activity.getApplication();
        AppDatabase db = app.getDatabase();
        User user=db.userDao().get(book.getFictionId(),1);
        //如果没有记录，则新增一条记录
        if(user==null){
            db.userDao().insertAll(new User(1,book.getFictionId(), book.getTitle(), book.getAuthor(), book.getFictionType(), book.getDescs(), book.getCover(),book.getUpdateTime(),position,readChapter,false));
        }else{
            db.userDao().update(user.getId(),position,readChapter);
        }
        EventBus.getDefault().post(new UpdateUIEvent());
    }
    @Override
    public int getItemCount() {
        return chapters.size();
    }
}
