package com.ltr.ebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ltr.ebook.Adapter.SecondAdapter;
import com.ltr.ebook.model.Book;

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

public class SearchResultActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private List<Book> bookList=new ArrayList<>();
    private Toolbar toolbar;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //顶部Bar
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        title=findViewById(R.id.toolbar_title);
        toolbar.removeView(title);

        //搜索内容访问Api，并更新UI
        Intent intent = getIntent();
        String searchText = intent.getStringExtra("search_text");
        fiction(this,"title",searchText,1);
        //先装载一个空的RecyclerView
        RecyclerView recyclerView = findViewById(R.id.search_result);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        SecondAdapter adapter=new SecondAdapter(bookList,false,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        fiction(this,"title",query,1);
        return true;
    }
    public static List<Book> fiction(final Activity activity, String option, String key, Integer adapter){
        List<Book> bookList=new ArrayList<>();
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("https://api.pingcc.cn/fiction/search"+"/"+option+"/"+key)
                .get()
                .build();
        //异步请求，回调方法更新UI
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
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {
                                Book book = gson.fromJson(data.get(i).toString(), Book.class);
                                bookList.add(book);
                            }
                            if(adapter==1){
                                activity.runOnUiThread(()->{
                                    RecyclerView recyclerView = activity.findViewById(R.id.search_result);
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                                    recyclerView.setLayoutManager(layoutManager);
                                    SecondAdapter secondAdapter=new SecondAdapter(bookList,false,activity);
                                    recyclerView.setAdapter(secondAdapter);
                                });
                            }
                            else if(adapter==3){
                                activity.runOnUiThread(()->{
                                    RecyclerView recyclerView = activity.findViewById(R.id.class_content_recyclerview);
                                    LinearLayoutManager layoutManager=new LinearLayoutManager(activity);
                                    recyclerView.setLayoutManager(layoutManager);
                                    SecondAdapter secondAdapter=new SecondAdapter(bookList,false,activity);
                                    recyclerView.setAdapter(secondAdapter);
                                    TextView title= activity.findViewById(R.id.toolbar_title);
                                    title.setText(key);
                                });
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return bookList;
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
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
