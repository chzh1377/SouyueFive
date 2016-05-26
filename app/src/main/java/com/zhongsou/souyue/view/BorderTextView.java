package com.zhongsou.souyue.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.util.Map;

/**
 * @description:  带边框的
 * @auther: qubian
 * @data: 2015/12/28.
 */

public class BorderTextView extends TextView {

    private String textColor;

    public void setTextBorderColor(String textColor) {
        this.textColor = textColor;
    }
    private Context mContext;
    public BorderTextView(Context context) {
        super(context);

    }
    public BorderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private int sroke_width = 1;
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        int color = Color.RED;
        try {
            color=Color.parseColor(textColor);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        setTextColor(color);
        paint.setColor(color);
        canvas.drawLine(1, 0, 1, this.getHeight() - sroke_width, paint);
        canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);
        canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
        canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
        super.onDraw(canvas);
    }
    public void setHotViews(Map<String, String> data)
    {
        if (data == null){
            setVisibility(INVISIBLE);
            return;
        }
        for(String key :data.keySet())
        {
            setText(key);
            setTextBorderColor(data.get(key));
//            float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
            setVisibility(View.VISIBLE);

//            textView.setTextSize(fontSize - 3);
//            textView.setPadding((int) (fontSize / 6.0), 0, (int) (fontSize / 5.f), 0);
//            textView.setIncludeFontPadding(false);
        }
    }
}
