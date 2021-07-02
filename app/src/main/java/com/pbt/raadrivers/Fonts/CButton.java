package com.pbt.raadrivers.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Lenovo on 22-03-2018.
 */

public class CButton extends androidx.appcompat.widget.AppCompatButton
{
    public CButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // TODO Auto-generated constructor stub
    }
    public CButton(Context context) {
        super(context);
        init();
        // TODO Auto-generated constructor stub
    }
    public CButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        // TODO Auto-generated constructor stub
    }
    private void init(){
        Typeface font_type= Typeface.createFromAsset(getContext().getAssets(), "fonts/Nunito-Bold.ttf");
        setTypeface(font_type);
    }
}
