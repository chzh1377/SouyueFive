package com.zhongsou.souyue.download;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
 
public class ZipExtractorTask extends AsyncTask<Void, Integer,Long> {
    private final String TAG = "ZipExtractorTask";
    private final File mInput;
    private final File mOutput;
    private int mProgress = 0;
    private final Context mContext;
    private boolean mReplaceAll;
    public ZipExtractorTask(String in, String out, Context context, boolean replaceAll){
        super();
        mInput = new File(in);
        mOutput = new File(out);
        if(!mOutput.exists()){
            if(!mOutput.mkdirs()){
                Log.e(TAG, "Failed to make directories:"+mOutput.getAbsolutePath());
            }
        }
        mContext = context;
        mReplaceAll = replaceAll;
    }
    @Override
    protected Long doInBackground(Void... params) {
        return unzip();
    }
     
    @Override
    protected void onPostExecute(Long result) {
        super.onPostExecute(result);
//        mContext.stopService(new Intent(mContext,DownloadBookZipService.class));
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
    @SuppressWarnings("unchecked")
    private long unzip(){
        long extractedSize = 0L;
        Enumeration<ZipEntry> entries;
        ZipFile zip = null;
        try {
            zip = new ZipFile(mInput);
            long uncompressedSize = getOriginalSize(zip);
            publishProgress(0, (int) uncompressedSize);
             
            entries = (Enumeration<ZipEntry>) zip.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(entry.isDirectory()){
                    continue;
                }
                File destination = new File(mOutput, entry.getName());
                if(!destination.getParentFile().exists()){
                    Log.e(TAG, "make="+destination.getParentFile().getAbsolutePath());
                    destination.getParentFile().mkdirs();
                }
                if(destination.exists()&&mContext!=null&&!mReplaceAll){
                     
                }
                ProgressReportingOutputStream outStream = new ProgressReportingOutputStream(destination);
                extractedSize+=copy(zip.getInputStream(entry),outStream);
                outStream.close();
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(zip!=null)
                zip.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
 
        return extractedSize;
    }
 
    private long getOriginalSize(ZipFile file){
        @SuppressWarnings("unchecked")
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
        long originalSize = 0l;
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if(entry.getSize()>=0){
                originalSize+=entry.getSize();
            }
        }
        return originalSize;
    }
     
    private int copy(InputStream input, OutputStream output){
        byte[] buffer = new byte[1024*8];
        BufferedInputStream in = new BufferedInputStream(input, 1024*8);
        BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
        int count =0,n=0;
        try {
            while((n=in.read(buffer, 0, 1024*8))!=-1){
                out.write(buffer, 0, n);
                count+=n;
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
     
    private final class ProgressReportingOutputStream extends FileOutputStream{
 
        public ProgressReportingOutputStream(File file)
                throws FileNotFoundException {
            super(file);
        }
 
        @Override
        public void write(byte[] buffer, int byteOffset, int byteCount)
                throws IOException {
            super.write(buffer, byteOffset, byteCount);
            mProgress += byteCount;
            publishProgress(mProgress);
        }
         
    }
}