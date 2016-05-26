package com.zhongsou.souyue.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

//import org.beetl.core.Configuration;
//import org.beetl.core.GroupTemplate;
//import org.beetl.core.Template;
//import org.beetl.core.resource.FileResourceLoader;

import com.zhongsou.souyue.MainApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author YanBin
 * @version V5.2.0
 * @project trunk
 * @Description 模板工具类
 * @date 2016/3/26
 */
public class TemplateUtils {
    public static final String TAG = "TemplateUtils";

    public static final String DEFAULT_TEMPLATE_ZIP_NAME = "detail_content_1.zip";   //默认模板名称 ， 存放在assets目录
    public static final String MODULE_TEMPLATE_NAME = "template.html";
    public static final String TEMPLATE_INTEREST = "interest.html";
    public static final String TEMPLATE_SRP = "srp.html";
    public static final String TEMPLATE_DEFAULT = "detail_content_1";

    /**
     * 调试使用，模板文件优先存放在/data/data/<packName>/file下
     *
     * @param context 上下文
     * @return 路径
     */
    public static String getTemplatePath(Context context) {
        String dirName = "template";
        String path = null;
        if (context != null) {
            path = context.getFilesDir().getPath() + File.separator + dirName;
        } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || !Environment.isExternalStorageRemovable()) {
            path = Environment.getExternalStorageDirectory() + File.separator + "souyue" + File.separator + dirName;
            //for example : path = /data/data/<package-name>/files/template
        }
        return path;
    }

    /**
     * 调试使用，模板文件优先存放在sd卡
     *
     * @param context 上下文
     * @return 路径
     */
    public static String getTemplateTestPath(Context context) {
        String dirName = "template";
        String path = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || !Environment.isExternalStorageRemovable()) {
            path = Environment.getExternalStorageDirectory() + File.separator + "souyue" + File.separator + dirName;
        } else if (context != null) {
            path = context.getFilesDir().getPath() + File.separator + dirName;
        }
        return path;
    }

    /**
     * 测试、联调使用
     *
     * @param strHtml  待处理字符串 比如html文件
     * @param fileName 文件名
     */
    public static void writeHtmlToSD(String strHtml, String fileName) {
        FileWriter fw = null;
        try {
            String path = getTemplateTestPath(MainApplication.getInstance()) + File.separator + "output";
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, fileName);
            if (!file.exists()) file.createNewFile();
            fw = new FileWriter(file);
            fw.write(strHtml);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 解压zip
     *
     * @param inputStream     zip的inputStream
     * @param destinationPath 目标路径
     */
    public static void unZipTemplate(InputStream inputStream, String destinationPath) {
        if (inputStream == null) return;
        Log.d("Template", "start unzip template");
//      服务端格式：  "templateVersion": "modle_0!interest_content_000"
        if (StringUtils.isEmpty(destinationPath)) {
            return;
        }
        if (!destinationPath.endsWith(File.separator)) {
            destinationPath = destinationPath + File.separator;
        }
        FileOutputStream fileOut = null;
        ZipInputStream zipIn = null;
        ZipEntry zipEntry = null;
        File file = null;
        int readLength = 0;
        byte buf[] = new byte[4096];
        try {
            zipIn = new ZipInputStream(new BufferedInputStream(inputStream));
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                file = new File(destinationPath + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    // 如果指定文件的目录不存在,则创建之.
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    fileOut = new FileOutputStream(file);
                    while ((readLength = zipIn.read(buf)) > 0) {
                        fileOut.write(buf, 0, readLength);
                    }
                    fileOut.close();
                }
                zipIn.closeEntry();
            }
        } catch (IOException ioe) {
            Log.d("Template", "unzip template failed");
            ioe.printStackTrace();
        }
    }

    /**
     * 解压文件
     *
     * @param templatePath    zip文件路径
     * @param destinationPath 解压的目标路径
     */
    public static void unZipTemplate(String templatePath, String destinationPath) {
        try {
            InputStream inputStream = new FileInputStream(templatePath);
            unZipTemplate(inputStream, destinationPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 过滤掉html标签
     *
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

//    private static String filterHtmlTag(String str, String tag) {
//        String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";
//        Pattern pattern = Pattern.compile(regxp);
//        Matcher matcher = pattern.matcher(str);
//        StringBuffer sb = new StringBuffer();
//        boolean result1 = matcher.find();
//        while (result1) {
//            matcher.appendReplacement(sb, "\n");
//            result1 = matcher.find();
//        }
//        matcher.appendTail(sb);
//        return sb.toString();
//    }
//
//    public static String delHTML(String html){
//        String result = filterHtmlTag(html, "\\p");
//        result = delHTMLTag(result);
//        return result;
//    }
}
