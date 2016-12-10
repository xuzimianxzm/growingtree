package com.xuzimian.www.growingtree;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import drawer.Tree;

public class MainActivity extends Activity {
    private Boolean isFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Tree(this));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isFinish) {
            isFinish = false;
            setContentView(new Tree(this));
        }
        return super.onTouchEvent(event);
    }

    public void setFinish(Boolean finish) {
        isFinish = finish;
    }

}
