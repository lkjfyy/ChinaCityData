package com.lkj.chinacitydata.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  author  lkj
 *  email   lkjfyy@gmail.com
 *  date    2018/11/22 14:33
 *  description 读取assets文件工具类
 */

public class FileUtils {

    /**
     * 逐行读取assets下的txt文件
     * @param context
     * @param path 含后缀名
     * @return 每行string的list集合
     */
    public static List<String> readFile(Context context, String path) {
        List<String> stringList = new ArrayList<String>();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            String str = "";
            is = context.getAssets().open(path);
            isr=new InputStreamReader(is);
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                if (!TextUtils.isEmpty(str)){
                    str=str.toString().trim();

                    if (str.contains("北京") || str.contains("上海") ||
                            str.contains("天津") || str.contains("重庆")){
                        str=str.substring(0,str.length()-1);
                    }
                    String str1=str.substring(0,7);
                    String str2=str.substring(7);
                    str=str.replace(str,str1.trim()+" "+str2.trim());
                    stringList.add(str);
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("找不到指定文件",e.toString());
        } catch (IOException e) {
            Log.e("读取文件失败",e.toString());
        } finally {
            try {
                br.close();
                isr.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringList;
    }

    /**
     * 读取assets下的txt文件，返回utf-8 String
     * @param context
     * @param fileName 不包括后缀
     * @return 文件内容 string
     */
    public static String readAssetsTxt(Context context, String fileName){
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(fileName+".txt");
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            
            text=replaceBlank(text);
            // Finally stick the string into the text view.
            return text;
        } catch (IOException e) {
            // Should never happen!
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return "读取错误，请检查文件名";
    }

    public static String replaceBlank(String src) {
        String dest = "";
        if (src != null) {
            Pattern pattern = Pattern.compile("\t|\r|\n|\\s*");
            Matcher matcher = pattern.matcher(src);
            dest = matcher.replaceAll("");
        }
        return dest;
    }

    /**
     * 生成.json文件
     * @param jsonString 需要格式化的json字符串
     * @param filePath 文件保存路径
     * @return 是否生成文件成功
     */
    public static boolean createJsonFile(String jsonString, String filePath) {
        boolean flag = true;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            // 格式化json字符串
            jsonString = JsonFormatTool.formatJson(jsonString);
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            Log.e("失败原因",e.toString());
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }
}
