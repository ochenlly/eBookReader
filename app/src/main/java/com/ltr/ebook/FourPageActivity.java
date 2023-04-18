package com.ltr.ebook;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ltr.ebook.Adapter.FourAdapter;
import com.ltr.ebook.databinding.ActivityFourPageBinding;
import com.ltr.ebook.model.FourItem;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

public class FourPageActivity extends AppCompatActivity {
    private List<FourItem> fourItemList=new ArrayList<>();
    private ActivityFourPageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFourPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initList();
        RecyclerView recyclerView = findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FourAdapter adapter = new FourAdapter(fourItemList);
        recyclerView.setAdapter(adapter);
        // 获取 CircleImageView 控件的实例
        CircleImageView imageView = findViewById(R.id.avatar);
        // 加载图像资源
        imageView.setImageResource(R.drawable.avatar);

        //字体改为宋体
//        TextView myTextView = (TextView) findViewById(R.id.fourTextView);
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/simyou.ttf");
//        myTextView.setTypeface(typeface);
    }
    public void initList(){
        fourItemList.add(new FourItem(R.drawable.user,"个人资料"));
        fourItemList.add(new FourItem(R.drawable.eye,"阅读记录"));
        fourItemList.add(new FourItem(R.drawable.setting,"设置"));
        fourItemList.add(new FourItem(R.drawable.question,"帮助与反馈"));
        fourItemList.add(new FourItem(R.drawable.info,"关于我们"));
    }
}
