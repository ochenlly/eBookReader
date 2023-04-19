package com.ltr.ebook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ltr.ebook.Adapter.ChapterAdapter;
import com.ltr.ebook.Adapter.ContentAdapter;
import com.ltr.ebook.databinding.BookContentBinding;
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

public class ContentActivity extends AppCompatActivity {
    private List<String> contents=new ArrayList<>();
    private List<Chapter> chapters=new ArrayList<>();
    private BookContentBinding binding;
    private Toolbar toolbar;
    private TextView title;
    private ImageView list;
    private ImageView plus;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BookContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //获取小说内容
        Intent intent=getIntent();
        Book book=intent.getSerializableExtra("book",Book.class);
        boolean flag=intent.getBooleanExtra("flag",false);
        fictionContentByFiction(this,book);
        RecyclerView recyclerView = findViewById(R.id.book_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ContentAdapter adapter=new ContentAdapter(contents);
        recyclerView.setAdapter(adapter);
        //顶部Bar
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SearchView searchView=findViewById(R.id.search_view);
        toolbar.removeView(searchView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title=findViewById(R.id.toolbar_title);
        title.setText("加载中...");
        list=findViewById(R.id.list);
        list.setOnClickListener(v->{
            List<Chapter> stringList=new ArrayList<>();
            stringList.add(new Chapter("加载中",""));
            PopupWindow popupWindow= updateUI(this,book,stringList,0);
            fictionChapter(this,book,chapters,popupWindow);

        }


        );
        plus=findViewById(R.id.plus);
        if(flag){
            toolbar.removeView(plus);
        }else{
            plus.setOnClickListener(v->{
                new Thread(()->{
                    MyApplication app = (MyApplication) this.getApplication();
                    AppDatabase db = app.getDatabase();
                    User user=db.userDao().get(book.getFictionId(),1);
                    if(user==null){
                        db.userDao().insertAll(new User(1,book.getFictionId(), book.getTitle(), book.getAuthor(), book.getFictionType(), book.getDescs(), book.getCover(),book.getUpdateTime(),0,"暂无章节",false));
                        user=db.userDao().get(book.getFictionId(),1);
                    }
                    user.setFlag(true);
                    db.userDao().updateAll(user);
                    runOnUiThread(()->{
                        Toast.makeText(app, "已加入书架", Toast.LENGTH_SHORT).show();
                        toolbar.removeView(plus);
                    });
                    EventBus.getDefault().post(new UpdateUIEvent());
                }).start();
            });
        }
    }
    public static PopupWindow updateUI(Activity activity, Book book, List<Chapter> chapters, int position){
        PopupWindow popupWindow = new PopupWindow(activity);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        View view=View.inflate(activity,R.layout.book_chapter,null);
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        View contentView=popupWindow.getContentView();
        RecyclerView recyclerView = contentView.findViewById(R.id.book_chapters);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        ChapterAdapter adapter=new ChapterAdapter(book,chapters,activity);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(position);
        popupWindow.showAtLocation(view, Gravity.LEFT, 0, 0);
        return popupWindow;
    }
    public static List<Chapter> fictionChapter(final Activity activity,Book book,List<Chapter> chapters,PopupWindow popupWindow){
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("https://api.pingcc.cn/fictionChapter/search/"+book.getFictionId())
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
                        int position=getPosition(activity,book.getFictionId(),1);
                        String responseBody = response.body().string();
                        // 解析JSON数据
                        try {
                            JSONTokener tokener = new JSONTokener(responseBody);
                            JSONObject json = (JSONObject) tokener.nextValue();
                            JSONObject data = json.getJSONObject("data");
                            JSONArray chapterList = data.getJSONArray("chapterList");
                            for (int i = 0; i < chapterList.length(); i++) {
                                Chapter chapter = new Chapter();
                                chapter.setTitle(chapterList.getJSONObject(i).getString("title"));
                                chapter.setChapterId(chapterList.getJSONObject(i).getString("chapterId"));
                                chapters.add(chapter);
                            }
                            activity.runOnUiThread(()->{
                                //当接口访问完成后，关闭加载页面
                                popupWindow.dismiss();
                                updateChapterUI(activity,book,chapters,position);
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
        return chapters;
    }
    public static void fictionContentByFiction(final Activity activity,Book book){
        List<Chapter> chapters=new ArrayList<>();
        OkHttpClient client=new OkHttpClient.Builder().build();
        Request request=new Request.Builder()
                .url("https://api.pingcc.cn/fictionChapter/search/"+book.getFictionId())
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
                            JSONObject data = json.getJSONObject("data");
                            JSONArray chapterList = data.getJSONArray("chapterList");
                            for (int i = 0; i < chapterList.length(); i++) {
                                Chapter chapter = new Chapter();
                                chapter.setTitle(chapterList.getJSONObject(i).getString("title"));
                                chapter.setChapterId(chapterList.getJSONObject(i).getString("chapterId"));
                                chapters.add(chapter);
                            }
                            //查询用户看到了第几章
                            int position= getPosition(activity,book.getFictionId(),1);
                            //再根据得到的章节id获取章节内容
                            List<String> contents=new ArrayList<>();
                            OkHttpClient client = new OkHttpClient.Builder().build();
                            Request request = new Request.Builder()
                                    .url("https://api.pingcc.cn/fictionContent/search/"+chapters.get(position).getChapterId())
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
                                                activity.runOnUiThread(()->{
                                                    RecyclerView recyclerView = activity.findViewById(R.id.book_content);
                                                    LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                                                    recyclerView.setLayoutManager(layoutManager);
                                                    ContentAdapter adapter=new ContentAdapter(contents);
                                                    recyclerView.setAdapter(adapter);
                                                    TextView title=activity.findViewById(R.id.toolbar_title);
                                                    title.setText(chapters.get(position).getTitle());
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
    public static PopupWindow updateChapterUI(Activity activity,Book book,List<Chapter> chapters,int position){
        PopupWindow popupWindow = new PopupWindow(activity);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        View view=View.inflate(activity,R.layout.book_chapter,null);
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        View contentView=popupWindow.getContentView();
        RecyclerView recyclerView = contentView.findViewById(R.id.book_chapters);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        ChapterAdapter adapter=new ChapterAdapter(book,chapters,activity);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(position);
        popupWindow.showAtLocation(view, Gravity.LEFT, 0, 0);
        return popupWindow;
    }
    public static int getPosition(Activity activity,String fictionId,int userId){
        MyApplication app = (MyApplication) activity.getApplication();
        AppDatabase db = app.getDatabase();
        User user=db.userDao().get(fictionId,userId);
        int position=0;
        if(user!=null){
            position= user.getPosition();
        }
        return position;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
