package com.example.android_final.custom;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class CustomRadioButton2 extends RadioButton {
    public CustomRadioButton2(Context context) {
        super(context);
    }

    public CustomRadioButton2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRadioButton2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomRadioButton2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    // Implement necessary constructors

    @Override
    public void toggle() {
        if(isChecked()) {
//            if(getParent() instanceof RadioGroup) {
//                ((RadioGroup)getParent()).clearCheck();
//            }
        } else {
//            setChecked(true);
        }
    }
}