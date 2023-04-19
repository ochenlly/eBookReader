package com.ltr.ebook;


import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ltr.ebook.Adapter.FirstAdapter;
import com.ltr.ebook.databinding.ActivityFirstPageBinding;
import com.ltr.ebook.model.Book;
import com.ltr.ebook.model.database.AppDatabase;
import com.ltr.ebook.model.database.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class FirstPageActivity extends AppCompatActivity{
    private ActivityFirstPageBinding binding;
    private Toolbar toolbar;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定布局文件
        binding = ActivityFirstPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //顶部Bar
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //主页中不显示后退按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //找到搜索栏和list和添加标签并去掉只显示text
        SearchView searchView=findViewById(R.id.search_view);
        ImageView list=findViewById(R.id.list);
        ImageView plus=findViewById(R.id.plus);
        toolbar.removeView(searchView);
        toolbar.removeView(list);
        toolbar.removeView(plus);
        //获取书架里的书的信息
        getList(this,1);
        RecyclerView recyclerView = findViewById(R.id.first_recyclerview);
        // RecyclerView 中实现线性布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //加入适配器
        FirstAdapter adapter=new FirstAdapter(new ArrayList<>(),true,this);
        recyclerView.setAdapter(adapter);
        //注册事件
        EventBus.getDefault().register(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateUIEvent(UpdateUIEvent event) {
        getList(this,1);
    }
    @Override
    protected void onDestroy() {
        // 销毁EventBus
        EventBus.getDefault().unregister(this);
        //释放当前 Activity 所持有的资源和对象
        super.onDestroy();
    }
    public static void getList(Activity activity, int userId){
        new Thread(()->{
            //获取当前应用程序对象
            MyApplication app = (MyApplication) activity.getApplication();
            AppDatabase db = app.getDatabase();
            //获取用户id等于1且加入了书架的
            List<User> userList=db.userDao().getBookList(1);
            List<Book> bookList=new ArrayList<>();
            //将数据库的书的数据赋值给bookList
            for(User user:userList){
                bookList.add(new Book(user.getFictionId(),user.getTitle(),user.getAuthor(),user.getFictionType(),user.getDescs(),user.getCover(),user.getUpdateTime(),user.getReadChapter()));
            }
            //UI 线程,对ui的操作必须通过这个线程
            activity.runOnUiThread(()->{
                RecyclerView recyclerView = activity.findViewById(R.id.first_recyclerview);
                LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                recyclerView.setLayoutManager(layoutManager);
                FirstAdapter adapter=new FirstAdapter(bookList,true,activity);
                recyclerView.setAdapter(adapter);
                //将文本内容改成 加载中...
                TextView title=activity.findViewById(R.id.toolbar_title);
                title.setText("我的书架");
                //标题文本在 ToolBar 控件中居中显示
                title.setGravity(Gravity.CENTER);
                title.setWidth(800);
            });
        }).start();
    }

}
