package com.zhongsou.souyue.im.ac;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tuita.sdk.im.db.module.Group;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.FileUtil;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.im.util.ScreenShot;
import com.zhongsou.souyue.service.SaveImageTask;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.FileNotFoundException;
import java.util.Hashtable;

/**
 * Created by zoulu
 * on 14-8-28
 * Description:群二维码
 */
public class GroupQRCodeActivity extends IMBaseActivity implements View.OnClickListener{
    private TextView groupname;
    private TextView title_name;
    private ImageView iv_two_dimen_code_pic;
    private Group group;
    private Bitmap qrCodeBitmap;
    private RelativeLayout save;
    private RelativeLayout sharetoFriend;
    private ImageView image;
    private TextView groupnumber;
//    private AQuery aQuery;
    private ImageLoader imageLoader;
    private RelativeLayout relativeshoot;
    private DisplayImageOptions options;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.groupqrcode);
        options = new DisplayImageOptions.Builder().cacheOnDisk(true).displayer(new RoundedBitmapDisplayer(10)).build();
        init();
    }

    private void init() {
//        aQuery = new AQuery(this);
        imageLoader = ImageLoader.getInstance();
        group = (Group) getIntent().getSerializableExtra("qrcode");
        save = (RelativeLayout) findViewById(R.id.re_savetocard);
        groupnumber = (TextView) findViewById(R.id.groupnumber);
        image = (ImageView) findViewById(R.id.image);
        sharetoFriend = (RelativeLayout) findViewById(R.id.sharetofriend);
        groupname = (TextView) findViewById(R.id.groupname);
        title_name = (TextView) findViewById(R.id.title_name);
        iv_two_dimen_code_pic = (ImageView) findViewById(R.id.iv_two_dimen_code_pic);
        relativeshoot = (RelativeLayout) findViewById(R.id.forshoot);
        if(null != group)
            title_name.setText("群名片/二维码");
        if(group.getGroup_nick_name() != null)
            groupname.setText(group.getGroup_nick_name());
        groupnumber.setText(group.getMemberCount()+"名成员");
//        aQuery.id(image).image(group.getGroup_avatar(), true, true);
        imageLoader.displayImage(group.getGroup_avatar(),image,options);
        save.setOnClickListener(this);
        sharetoFriend.setOnClickListener(this);

        DisplayMetrics display = getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int height = display.heightPixels;
        int smallerDimension = width < height ? width : height;
        smallerDimension = (int) ((smallerDimension - 40 * display.density) * 7 / 8);
        String contentString = "http://souyue.mobi/?t=group&uid="+group.getGroup_id()+"&iid="+ SYUserManager.getInstance().getUserId();
        if(null != group) {
            try {
                qrCodeBitmap = createQRCode(contentString, smallerDimension);
                iv_two_dimen_code_pic.setImageBitmap(qrCodeBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 生成二维码图片
     * @param str
     * @param widthAndHeight
     * @return
     * @throws com.google.zxing.WriterException
     * @throws java.io.FileNotFoundException
     */
    public Bitmap createQRCode(String str,int widthAndHeight) throws WriterException, FileNotFoundException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.re_savetocard:
                try {
                    Bitmap bitmap = ScreenShot.getViewBitmap(relativeshoot);
                    if (bitmap != null)
                        new SaveImageTask(this).execute(FileUtil.saveImg(bitmap, System.currentTimeMillis()+".png"));
                    else
                        Toast.makeText(GroupQRCodeActivity.this,"图片保存失败",Toast.LENGTH_SHORT).show();
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.sharetofriend:
                IMIntentUtil.gotoShowGroupCardToContactList(this,group);
                break;
        }

    }
}
