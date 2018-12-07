package com.cx.countview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CountView extends LinearLayout {
    final static String TAG = "logtag_CountView";

    private int count = 0;
    private int MIN_COUNT = 0;
    private int MAX_COUNT = 10;
    private boolean countEditAble = false;
    private String maxNotice;
    private String minNotice;
    private ImageView ivAdd;
    private ImageView ivReduce;
    private EditText etCount;
    private OnClickListener addListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(count + 1 > MAX_COUNT){
                Toast.makeText(getContext(),maxNotice, Toast.LENGTH_SHORT);
            }else if(count == MIN_COUNT){
                showView();
                setCount(count + 1);
            }else {
                setCount(count + 1);
            }
        }
    };

    private OnClickListener reduceListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(count - 1 <= MIN_COUNT){
                hideView();
                setCount(MIN_COUNT);
            }else {
                setCount(count - 1);
            }
        }
    };

    private OnCountChangeListener countListener;

    interface OnCountChangeListener{
        public void countChange(int count);
    }

    public CountView(Context context) {
        this(context,null);
    }

    public CountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CountView, defStyleAttr, 0);
        final View v = LayoutInflater.from(context).inflate(R.layout.layout_countview,this);
        ivAdd = v.findViewById(R.id.iv_add);
        ivAdd.setOnClickListener(addListener);
        ivReduce = v.findViewById(R.id.iv_reduce);
        ivReduce.setOnClickListener(reduceListener);
        etCount = v.findViewById(R.id.et_count);
        etCount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countEditAble){
                    changeEditFocus();
                }else {
                    etCount.clearFocus();
                    etCount.setFocusable(false);
                    etCount.setFocusableInTouchMode(false);
                }
            }
        });
        MIN_COUNT = arr.getInt(R.styleable.CountView_min_count,0);
        MAX_COUNT = arr.getInt(R.styleable.CountView_max_count,10);
        countEditAble = arr.getBoolean(R.styleable.CountView_count_editable,false);
        maxNotice = arr.getString(R.styleable.CountView_max_notice_str);
        minNotice = arr.getString(R.styleable.CountView_min_notice_str);
        etCount.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
    }

    public void setMinCount(int min){
        this.MIN_COUNT = min;
    }

    public void setCount(int count){
        this.count = count;
        etCount.setText(count+"");
        if(countListener != null){
            countListener.countChange(count);
        }
    }

    public void setMaxCount(int max){
        this.MAX_COUNT = max;
    }

    private void showView(){

    }

    private void hideView(){

    }

    private void changeEditFocus(){
        etCount.clearFocus();
        etCount.setFocusable(true);
        etCount.setFocusableInTouchMode(true);
        etCount.requestFocus();
        InputMethodManager input = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        input.showSoftInput(etCount, InputMethodManager.SHOW_FORCED);
    }
}
