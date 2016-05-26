package com.zhongsou.souyue.ui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SRPActivity;

import java.util.List;

public class SelfCreatePublishInView extends LinearLayout {

    private Context context;
    private List<String> listKeyword;
    private int count;
    private List<String> listSrpId;

    public SelfCreatePublishInView(Context context) {
        super(context);
        init(context);
    }

    public SelfCreatePublishInView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SelfCreatePublishInView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setOrientation(VERTICAL);
    }

    /**
     * @param list  keyWord
     * @param list2 srpID
     */
    public void setData(List<String> list, List<String> list2) {
        if (list == null || list2 == null) {
            return;
        }
        this.listKeyword = list;
        this.listSrpId = list2;
        count = (int) Math.ceil(list.size() / 3.0);
        for (int i = 0; i < count; i++) {
            addView(createLine(new LinearLayout(context), i));
        }
    }

    private View createLine(LinearLayout row, int i) {
        row.setOrientation(HORIZONTAL);
        row.addView(createItemView(i * 3, Gravity.LEFT));
        row.addView(createItemView(i * 3 + 1, Gravity.CENTER));
        row.addView(createItemView(i * 3 + 2, Gravity.RIGHT));
        return row;
    }

    /**
     * @param text   keyword
     * @param string srpID
     * @return
     */
    private View createItemView(int position, int gravity) {

        View view =
                LayoutInflater.from(context).inflate(R.layout.self_create_detail_publish_item, null);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
        ((RelativeLayout) view).setGravity(gravity);
        if (position >= listKeyword.size()) {
            view.setVisibility(View.INVISIBLE);
        } else {
            final String text = listKeyword.get(position);
            final String id = listSrpId.get(position);
            TextView textView = (TextView) view.findViewById(R.id.self_create_detail_pub_item_text);
            textView.setText(text);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SRPActivity.class);
                    intent.putExtra("keyword", text);
                    intent.putExtra("srpId", id);
                    context.startActivity(intent);
                }
            });
        }

        return view;
    }
}
