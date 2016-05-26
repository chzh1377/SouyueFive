package com.zhongsou.souyue.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.zhongsou.souyue.R;

public class EmptyOrErrorFragment extends Fragment {
    public static final String VIEW_TYPE = "viewType";
    public static final String VIEW_TIP_TEXT = "viewTipText";
    /**
     * 分组下无keyword
     */
    public static final int TYPE_NO_KEYWORDS = 1;

    private int viewType;
    private int tipId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = null;

        if (getArguments() != null) {
            viewType = getArguments().getInt(VIEW_TYPE, -1);
            tipId = getArguments().getInt(VIEW_TIP_TEXT, 0);
            switch (viewType) {
                case TYPE_NO_KEYWORDS:
                    root = inflater.inflate(R.layout.nosearchresult, container, false);
                    root.findViewById(R.id.ll_nosearchresult).setVisibility(View.VISIBLE);
                    ((TextView) root.findViewById(R.id.nosearchresult_tip)).setText(getString(tipId));
                    break;

                default:
                    break;
            }
        }

        return root;
    }

}
