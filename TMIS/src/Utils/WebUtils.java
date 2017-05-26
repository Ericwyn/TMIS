package Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 包含了一些工具方法，包括获取VIEWSTATUS方法，以及解析Html方法
 * Created by Ericwyn on 17-5-26.
 */
public class WebUtils {

    /**
     * 获取VIEWSTATE参数
     * @param client
     * @param url
     * @param val
     * @return
     * @throws IOException
     */
    public static String get__VIEWSTATE(CloseableHttpClient client,String url, int val) throws IOException {
        HttpGet httpGet=new HttpGet(url);
        HttpResponse response=client.execute(httpGet);
        Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity(), "utf-8")); //jsoup 也可以去找相应的包
        //查找 __VIEWSTATE这个的值
        String __VIEWSTATE = doc.select("input[name=__VIEWSTATE]").val();
        //释放get
        httpGet.abort();
        return __VIEWSTATE;
    }

    /**
     * 获取验证码参数
     * @param is    输入文件流
     * @param filename  输出验证码文件的名字
     * @param savePath  文件的保存路径
     * @throws Exception
     */
    public static void getSecret(InputStream is, String filename,
                                 String savePath) throws Exception {
        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        File sf = new File(savePath);
        if (!sf.exists()) {
            sf.mkdirs();
        }
        OutputStream os = new FileOutputStream(sf.getPath() + "/" + filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
            os.flush();
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }


    public static String getHtml(InputStream is, String encoding)
            throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
            bos.flush();
        }
        is.close();
        return new String(bos.toByteArray(), encoding);
    }
}
