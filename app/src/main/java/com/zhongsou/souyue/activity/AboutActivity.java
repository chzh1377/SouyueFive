package com.zhongsou.souyue.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.LayoutApi;
import com.zhongsou.souyue.utils.IntentUtil;

/**
 * 关于我们
 * 
 * @author huanglb@zhongsou.com
 * 
 */
public class AboutActivity extends RightSwipeActivity implements
		OnClickListener,OnItemClickListener {

	private TextView about_permission;
	private TextView version;
	private ListView about_body;
	private AboutAdapter aboutAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//add by trade start
		setContentView(LayoutApi.getLayoutResourceId(R.layout.about_souyue));
		//add by trade end
		version = findView(R.id.about_version);
		about_permission = (TextView) findViewById(R.id.about_permission);
		about_body=(ListView) findViewById(R.id.about_body);
		about_body.setOnItemClickListener(this);
		aboutAdapter=new AboutAdapter();
		about_body.setAdapter(aboutAdapter);
//		website = findView(R.id.about_website);
		about_permission.setOnClickListener(this);
		//add by trade start
//		if(ConfigApi.isSouyue()){
//		    website.setOnClickListener(this);
//		}
		version.setText(CommonStringsApi.getShowVersion(this, version.getText().toString()));
		//add by trade end
        ((TextView)findViewById(R.id.activity_bar_title)).setText(getString(R.string.settingActivity_about));
		this.setCanRightSwipe(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_permission:
//			Intent intent = new Intent();
//			intent.setClass(AboutActivity.this, UserAgreementActivity.class);
//			startActivity(intent);
//			overridePendingTransition(R.anim.left_in, R.anim.left_out);

            IntentUtil.gotoUserAgreement(this); //替换为H5页面 -- YanBin

			break;
//		case R.id.about_website:
//			Intent webViewIntent = new Intent();
//			webViewIntent.setClass(AboutActivity.this, WebSrcViewActivity.class);
//			webViewIntent.putExtra(WebSrcViewActivity.PAGE_URL, "http://souyue.mobi");
//			startActivity(webViewIntent);
//			overridePendingTransition(R.anim.left_in, R.anim.left_out);
//			break;

		default:
			break;
		}
	}
	
	private class AboutAdapter extends BaseAdapter{
	    private String[] aboutNames= getResources().getStringArray(R.array.about_names);
	    private int[] aboutTypes=getResources().getIntArray(R.array.about_types);
        @Override
        public int getCount() {
            return aboutNames==null?0:aboutNames.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return aboutTypes[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(AboutActivity.this).inflate(R.layout.about_item, parent, false);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_title.setText(aboutNames[position]);
            return convertView;
        }
        public  class ViewHolder{
            TextView tv_title;
        }
	}

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        int type=(Integer) aboutAdapter.getItem(position);
        switch (type) {
          /*  case 0: //欢迎页面还没有提供
                IntentUtil.StartFirstActivity(this);
                break;*/
            case 1://功能介绍 h5 url李鹏提供
                IntentUtil.gotoWeb(this, UrlConfig.function, "nopara");

                break;
//            case 2: //意见反馈 搜小悦 有问题
//                IntentUtil.StartIMSouYueActivity(this);
//                break;
            case 3: //联系我们
                IntentUtil.gotoContactUS(this);
                break;

            default:
                break;
        }
    }

}
