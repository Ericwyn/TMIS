package Utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * Created by Ericwyn on 17-5-23.
 */
public class Web {
    private static String stuNum;
    //所有方法公用的CloseableHttpClient
    private static CloseableHttpClient client= HttpClients.createDefault();


    /**
     * 模拟登录的方法，处理之后CloseableHttpClient不会关闭
     * @param stuNum    学生学号
     * @param password  登录密码
     * @return          返回cookie
     * @throws Exception    异常
     */
    public static String login(String stuNum,String password) throws Exception{
        String Cookie = "";
        //记录学生学号
        Web.stuNum=stuNum;
        //获取验证码
        HttpGet secretCodeGet=new HttpGet(WebConfig.SECRETCODE_URL);
//        CloseableHttpClient client= HttpClients.createDefault();
        CloseableHttpResponse responseSecret=client.execute(secretCodeGet);
        //获取验证码的Cookie
        Cookie =responseSecret.getFirstHeader("Set-Cookie").getValue();

        String viewState=get__VIEWSTATE(WebConfig.Main_Url,0);
        getSecret(responseSecret.getEntity().getContent(),"secretCode","secCode");
        Scanner sc=new Scanner(System.in);
        System.out.println("请输入验证码");

        String secret=sc.next().trim();

        HttpPost loginPost=new HttpPost(WebConfig.LOGIN_URL);


        List<NameValuePair> nameValuePairLogin = new ArrayList<NameValuePair>();// 封装Post提交参数
        nameValuePairLogin
                .add(new BasicNameValuePair("__VIEWSTATE", viewState));// 隐藏表单值
        nameValuePairLogin.add(new BasicNameValuePair("tbYHM", stuNum));// 学号
        nameValuePairLogin.add(new BasicNameValuePair("tbPSW", password));// 密码
        nameValuePairLogin.add(new BasicNameValuePair("TextBox3", secret));// 验证码
        nameValuePairLogin.add(new BasicNameValuePair("RadioButtonList1","%D1%A7%C9%FA"));
        nameValuePairLogin.add(new BasicNameValuePair("imgDL.x","198"));    //+(int)(Math.random()*300+3)));
        nameValuePairLogin.add(new BasicNameValuePair("imgDL.y","23"));     //+(int)(Math.random()*30+3)));
        loginPost.setHeader("Cookie",Cookie);
//        loginPost.setHeader("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                nameValuePairLogin, "GB2312");
        loginPost.setEntity(entity);

        HttpResponse responseLogin = client.execute(loginPost);

        if (responseLogin.getStatusLine().getStatusCode() == 302) {
//            System.out.println(responseLogin.toString());
//            HttpGet mainGet = new HttpGet(WebConfig.CHECK_SCORE_URL+stuNum);
//            mainGet.setHeader("Cookie", Cookie);
//            mainGet.setHeader("Referer", WebConfig.Main_Url+"/xsleft.aspx?flag=xxcx");
//            HttpResponse responseMain = client.execute(mainGet);
//            InputStream is = responseMain.getEntity().getContent();
//            String html = "";
//            try {
//                html = getHtml(is, "GB2312");
//                System.out.println(html);
//            } catch (Exception e) {
//                System.out.println("解析html失败！");
//                e.printStackTrace();
//            }
            return Cookie;
        } else {
            System.out.println("登录失败！");
            return null;
        }
    }

    /**
     * 使用同一个CloseableHttpClient，带上Cookie查询成绩
     * @param cookie    由模拟登录得来的cookie
     * @throws Exception    IO异常
     */
    public static void getScore(String cookie) throws Exception{
        HttpGet mainGet = new HttpGet(WebConfig.CHECK_SCORE_URL+stuNum);
        mainGet.setHeader("Cookie", cookie);
        mainGet.setHeader("Referer", WebConfig.Main_Url+"/xsleft.aspx?flag=xxcx");
        HttpResponse responseMain = client.execute(mainGet);
        InputStream is = responseMain.getEntity().getContent();
        String html = "";
        try {
            html = getHtml(is, "GB2312");
            String chengji= Jsoup.parse(html).getElementById("DataGrid1").text().replace("学年 学期 课程名称 课程类型 任课教师 考核方式 总评成绩 补考成绩 重修成绩 重修成绩2 重修成绩3 绩点 应得学分 ","");
            String zhongxuefen= Jsoup.parse(html).getElementById("Table1").text();
            System.out.println(chengji);
            System.out.println(zhongxuefen);
//            System.out.println(chengji);
        } catch (Exception e) {
            System.out.println("解析html失败！");
            e.printStackTrace();
        }
    }

    /**
     * 单独的client关闭方法
     */
    public static void closeClient(){
        try {
            client.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String get__VIEWSTATE(String url,int val) throws IOException {
        HttpGet httpGet=new HttpGet(url);
        HttpResponse response=client.execute(httpGet);
        Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity(), "utf-8")); //jsoup 也可以去找相应的包
        //查找 __VIEWSTATE这个的值
        String __VIEWSTATE = doc.select("input[name=__VIEWSTATE]").val();
        //释放get
        httpGet.abort();
        return __VIEWSTATE;
    }

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
