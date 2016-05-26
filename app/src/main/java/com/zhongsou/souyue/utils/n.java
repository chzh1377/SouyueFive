package com.zhongsou.souyue.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.MainApplication;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


public class n implements DontObfuscateInterface {

	public static String a() {
//      StringBuilder builder = new StringBuilder();
      String str = "";
      try {
          PackageManager manager = MainApplication.getInstance().getPackageManager();
          /** 通过包管理器获得指定包名包含签名的包信息 **/
          PackageInfo packageInfo = manager.getPackageInfo(MainApplication.getInstance().getPackageName(), PackageManager.GET_SIGNATURES);
          /******* 通过返回的包信息获得签名数组 *******/
          Signature[] signatures = packageInfo.signatures;
          /******* 循环遍历签名数组拼接应用签名 *******/
//          for (Signature signature : signatures) {
//              builder.append(signature.toCharsString());
//          }
          str= b(signatures[0].toByteArray());
          /************** 得到应用签名 **************/
      }catch (PackageManager.NameNotFoundException e) {
          e.printStackTrace();
      }

      return str;
  }

  public static String b(byte[] signature) {
      String pub=null;
      String p="";
      try {
          CertificateFactory certFactory = CertificateFactory
                  .getInstance("X.509");
          X509Certificate cert = (X509Certificate) certFactory
                  .generateCertificate(new ByteArrayInputStream(signature));
          String pubKey = cert.getPublicKey().toString();
          String signNumber = cert.getSerialNumber().toString();
          pub=pubKey.substring("OpenSSLRSAPublicKey".length());
          JSONObject json = new JSONObject(pub);
          p=json.getString("modulus");
//          System.out.println("pubKey:" + p);
//          System.out.println("pubKey:" + p.length());
//          System.out.println("signNumber:" + signNumber);
          return p;
      } catch (CertificateException e) {
          e.printStackTrace();
      } catch (JSONException e) {
//          e.printStackTrace();
          if(pub!=null){
              int start = pub.indexOf(':');
              int end= pub.indexOf('\n');
              p=pub.substring(start+1, end).trim();
          }
      }
      return p;
  }
}
