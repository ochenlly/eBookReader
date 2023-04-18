package com.ltr.ebook;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ltr.ebook.Utils.HttpUtils;

public class OneTypeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_type);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.removeView(findViewById(R.id.search_view));
        toolbar.removeView(findViewById(R.id.list));
        toolbar.removeView(findViewById(R.id.plus));
        title=toolbar.findViewById(R.id.toolbar_title);
        title.setWidth(500);
        title.setGravity(Gravity.CENTER);
        Intent intent=getIntent();
        HttpUtils.fiction(this,"fictionType",intent.getStringExtra("fictionType"),3);
    }
    public boolean onQueryTextSubmit(String query) {
        HttpUtils.fiction(this,"title",query,1);
        return true;
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