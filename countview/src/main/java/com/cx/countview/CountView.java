package com.cx.countview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class CountView extends LinearLayout {
    final static String TAG = "logtag_CountView";

    private int count = 0;
    private int MIN_COUNT = 0;
    private int MAX_COUNT = 9999;
    private boolean countEditAble = false;
    private String maxNotice;
    private String minNotice;
    private ImageView ivAdd;
    private ImageView ivReduce;
    private EditText etCount;
    private int imageWidth;
    private int etWidht;
    private boolean ifShowAnimator = false;

    private OnClickListener addListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(count + 1 > MAX_COUNT){
                if(!TextUtils.isEmpty(maxNotice))
                    showToast(maxNotice);
            }else if(count == MIN_COUNT){
                showView();
                showDropAnimator(0,0);
                setCount(count + 1);
            }else {
                showDropAnimator(0,0);
                setCount(count + 1);
            }
            clearEditFocus();
        }
    };

    private OnClickListener reduceListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(count - 1 <= MIN_COUNT){
                hideView();
                setCount(MIN_COUNT);
                if(!TextUtils.isEmpty(minNotice) && ifShowAnimator)
                    showToast(minNotice);
            }else {
                setCount(count - 1);
            }
            clearEditFocus();
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
        MIN_COUNT = arr.getInt(R.styleable.CountView_min_count,MIN_COUNT);
        MAX_COUNT = arr.getInt(R.styleable.CountView_max_count,MAX_COUNT);
        countEditAble = arr.getBoolean(R.styleable.CountView_count_editable,false);
        maxNotice = arr.getString(R.styleable.CountView_max_notice_str);
        minNotice = arr.getString(R.styleable.CountView_min_notice_str);
        ifShowAnimator = arr.getBoolean(R.styleable.CountView_show_hide_animator,false);
        etCount.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        TextPaint tp = etCount.getPaint();
        float textWidth = tp.measureText("0");
        etCount.setWidth((int) (textWidth * ((etCount.getText().toString().length() + 2))));
        etCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if(TextUtils.isEmpty(str)){
                    etCount.setText(MIN_COUNT+"");
                    etCount.setSelection(etCount.getText().toString().length());
                    if(!TextUtils.isEmpty(minNotice)){
                        showToast(minNotice);
                    }
                }else if(str.length() >= 2 && str.startsWith("0")){
                    etCount.setText(Integer.parseInt(str)+"");
                    etCount.setSelection(etCount.getText().toString().length());
                }else if(Integer.parseInt(str) > MAX_COUNT){
                    etCount.setText(MAX_COUNT+"");
                    etCount.setSelection(etCount.getText().toString().length());
                    if(!TextUtils.isEmpty(maxNotice)){
                        showToast(maxNotice);
                    }
                }else if(Integer.parseInt(str) < MIN_COUNT){
                    etCount.setText(MIN_COUNT+"");
                    etCount.setSelection(etCount.getText().toString().length());
                    if(!TextUtils.isEmpty(minNotice)){
                        showToast(minNotice);
                    }
                }
                count = Integer.parseInt(etCount.getText().toString());
                TextPaint tp = etCount.getPaint();
                float textWidth = tp.measureText("0");
                etCount.setWidth((int) (textWidth * ((etCount.getText().toString().length() + 2))));
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        if(heightMode == MeasureSpec.EXACTLY){
            RelativeLayout.LayoutParams addParams = (RelativeLayout.LayoutParams) ivAdd.getLayoutParams();
            RelativeLayout.LayoutParams reduceParams = (RelativeLayout.LayoutParams) ivReduce.getLayoutParams();
            RelativeLayout.LayoutParams etParams = (RelativeLayout.LayoutParams) etCount.getLayoutParams();
            addParams.height = sizeHeight;
            addParams.width = sizeHeight;
            ivAdd.setLayoutParams(addParams);
            reduceParams.height = sizeHeight;
            reduceParams.width = sizeHeight;
            ivReduce.setLayoutParams(reduceParams);
            imageWidth = sizeHeight;
            etCount.setTextSize(TypedValue.COMPLEX_UNIT_PX,sizeHeight/4*3);
            TextPaint tp = etCount.getPaint();
            float textWidth = tp.measureText("0");
            etWidht = (int) (textWidth * ((etCount.getText().toString().length() + 2)));
            etParams.height = sizeHeight;
            etParams.width = etWidht;
            etCount.setLayoutParams(etParams);
            measureChildren(widthMeasureSpec,heightMeasureSpec);
            setMeasuredDimension(sizeHeight * 2 + etWidht, sizeHeight);
        } else {
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }
    }

    private void showDropAnimator(int x, int y){
        ImageView iv = new ImageView(this.getContext());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivAdd.getLayoutParams();
        params.setMarginEnd(10);
        iv.setLayoutParams(params);
        iv.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.ic_add_animator));
        this.addView(iv);
    }

    private void showToast(String str){
        Toast.makeText(CountView.this.getContext(),str,Toast.LENGTH_SHORT).show();
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

    public void showView(){
        ObjectAnimator ivAnimator = ObjectAnimator.ofFloat(ivReduce
                ,"translationX", etWidht + imageWidth,0);
        ObjectAnimator etAnimator = ObjectAnimator.ofFloat(etCount
                ,"translationX", imageWidth,0);
        ObjectAnimator rotation1 = ObjectAnimator.ofFloat(ivReduce, "rotation", 0, 135, 0);
        ObjectAnimator reduceAlhpa = ObjectAnimator.ofFloat(ivReduce,"alpha",0,1);
        ObjectAnimator etAlhpa = ObjectAnimator.ofFloat(ivReduce,"alpha",0,1);
        ivReduce.setVisibility(View.VISIBLE);
        etCount.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(800);//动画时长
        //animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.playTogether(rotation1,ivAnimator,etAnimator,reduceAlhpa,etAlhpa);
        animatorSet.start();
    }

    public void hideView(){
        ObjectAnimator ivAnimator = ObjectAnimator.ofFloat(ivReduce
                ,"translationX", 0,etWidht + imageWidth);
        ObjectAnimator etAnimator = ObjectAnimator.ofFloat(etCount
                ,"translationX", 0,imageWidth);
        ObjectAnimator rotation1 = ObjectAnimator.ofFloat(ivReduce, "rotation", 0, 135, 0);
        ObjectAnimator reduceAlhpa = ObjectAnimator.ofFloat(ivReduce,"alpha",1,0);
        ObjectAnimator etAlhpa = ObjectAnimator.ofFloat(ivReduce,"alpha",1,0);
        ivReduce.setVisibility(View.VISIBLE);
        etCount.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(800);//动画时长
        //animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.playTogether(rotation1,ivAnimator,etAnimator,reduceAlhpa,etAlhpa);
        animatorSet.start();
    }

    private void changeEditFocus(){
        etCount.clearFocus();
        etCount.setFocusable(true);
        etCount.setFocusableInTouchMode(true);
        etCount.requestFocus();
        etCount.setSelection(etCount.length());
        InputMethodManager input = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        input.showSoftInput(etCount, InputMethodManager.SHOW_FORCED);
    }

    private void clearEditFocus(){
        etCount.clearFocus();
        this.requestFocus();
    }
}
