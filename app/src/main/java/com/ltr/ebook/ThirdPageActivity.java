package com.ltr.ebook;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ltr.ebook.Adapter.ClassAdapter;
import com.ltr.ebook.Adapter.SecondAdapter;
import com.ltr.ebook.databinding.ActivityFirstPageBinding;
import com.ltr.ebook.databinding.ActivityThirdPageBinding;
import com.ltr.ebook.model.Book;
import com.ltr.ebook.model.ThirdItem;

import java.util.ArrayList;
import java.util.List;

import com.ltr.ebook.model.ThirdItem;



public class ThirdPageActivity extends AppCompatActivity {
    private ActivityThirdPageBinding binding;
    private Toolbar toolbar;
    private TextView title;
    private List<ThirdItem> classList=new ArrayList<>();
    private List<Book> bookList=new ArrayList<>();
    private List<Integer> mImageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThirdPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //顶部Bar，取消home键，去掉搜索栏,去掉list,设置title居中
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        SearchView searchView=findViewById(R.id.search_view);
        ImageView list=findViewById(R.id.list);
        toolbar.removeView(list);
        toolbar.removeView(searchView);
        title=findViewById(R.id.toolbar_title);
        title.setText("分类");
        title.setGravity(Gravity.CENTER);
        title.setWidth(1000);
        ImageView plus=findViewById(R.id.plus);
        toolbar.removeView(plus);
        //装载分类的列表
        initList();
        // 创建 RecyclerView 对象
        RecyclerView recyclerView1 = findViewById(R.id.class_recyclerview);
        // 创建 GridLayoutManager 对象，将 spanCount 属性设置为 3
        GridLayoutManager  layoutManager = new GridLayoutManager(this,3);
        recyclerView1.setLayoutManager(layoutManager);

        // 创建 RecyclerViewAdapter 对象并设置给 RecyclerView
        ClassAdapter adapter=new ClassAdapter(classList,this);
        recyclerView1.setAdapter(adapter);



    }
    public void initList(){
        // 初始化数据源
        classList.add(new ThirdItem(R.drawable.fantasy,"玄幻小说"));
        classList.add(new ThirdItem(R.drawable.fight,"武侠仙侠"));
        classList.add(new ThirdItem(R.drawable.scary,"科幻灵异"));
        classList.add(new ThirdItem(R.drawable.history,"历史军事"));
        classList.add(new ThirdItem(R.drawable.city,"都市小说"));
        classList.add(new ThirdItem(R.drawable.game,"网游小说"));
        classList.add(new ThirdItem(R.drawable.girl,"女生小说"));
        classList.add(new ThirdItem(R.drawable.people,"同人小说"));

    }
}
