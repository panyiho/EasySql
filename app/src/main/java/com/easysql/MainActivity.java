package com.easysql;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EasySql.hatch(new SqliteDBConfig.Builder().addTableMapping(Persion.class,new TestMapping()).build(this));
    }
}
