package com.pbt.raadrivers.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Lenovo on 22-03-2018.
 */

public class CEditText extends androidx.appcompat.widget.AppCompatEditText
{
    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public CEditText(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public CEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        this.attrs=attrs;
        init();
    }

    public CEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        this.attrs=attrs;
        this.defStyle=defStyle;
        init();
    }

    private void init() {
        Typeface font= Typeface.createFromAsset(getContext().getAssets(), "fonts/Nunito-Bold.ttf");
        this.setTypeface(font);
    }
    @Override
    public void setTypeface(Typeface tf, int style) {
        tf= Typeface.createFromAsset(getContext().getAssets(), "fonts/Nunito-Bold.ttf");
        super.setTypeface(tf, style);
    }

    @Override
    public void setTypeface(Typeface tf) {
        tf= Typeface.createFromAsset(getContext().getAssets(), "fonts/Nunito-Bold.ttf");
        super.setTypeface(tf);
    }
}
