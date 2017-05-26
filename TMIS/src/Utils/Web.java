package Utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Ericwyn on 17-5-23.
 */
public class Web {
    private String stuNum;
    //这个对象中，所有的方法,公用的CloseableHttpClient
    private CloseableHttpClient client;
    public Web(){

    }
    /**
     * 模拟登录的方法，处理之后CloseableHttpClient不会关闭
     * @param stuNum    学生学号
     * @param password  登录密码
     * @return          返回cookie，或者状态代码，1是代表密码错误，2是代表用户不存在
     *                      验证码错误？不存在的～会自动重新链接，然后获取一个新的验证码。
     * @throws Exception    异常
     */
    public String login(String stuNum,String password){
        String Cookie = null;
        //记录学生学号
        this.stuNum=stuNum;
        //获取验证码
        for (;;){
            try {
                newClient();    //初始化一个Client
                HttpGet secretCodeGet=new HttpGet(WebConfig.SECRETCODE_URL);
                CloseableHttpResponse responseSecret=client.execute(secretCodeGet);

                Cookie =responseSecret.getFirstHeader("Set-Cookie").getValue();

                String viewState=WebUtils.get__VIEWSTATE(client,WebConfig.Main_Url,0);
                WebUtils.getSecret(responseSecret.getEntity().getContent(),"secretCode","secCode");

                String secret=YanZhenUtil.getAllOcr("secCode/secretCode");
//                String secret="XXXX";
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

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        nameValuePairLogin, "GB2312");
                loginPost.setEntity(entity);

                HttpResponse responseLogin = client.execute(loginPost);

                if (responseLogin.getStatusLine().getStatusCode() == 302) {
                    break;
                }else {
                    String fanuhi=new BasicResponseHandler().handleResponse(responseLogin);

                    if(fanuhi.contains("密码不正确")){
//                        System.out.println("密码不正确");
                        return "1";
                    }else if (fanuhi.contains("用户不存在")){
//                        System.out.println("学号输入错误");
                        return "2";
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return Cookie;

    }

    /**
     * 使用同一个CloseableHttpClient，带上Cookie查询成绩
     * @param cookie    由模拟登录得来的cookie
     * @throws Exception    IO异常
     */
    public void getScore(String cookie){
        for (int i=0;i<5;i++){
            try {
                HttpGet mainGet = new HttpGet(WebConfig.CHECK_SCORE_URL+stuNum);
                mainGet.setHeader("Cookie", cookie);
                mainGet.setHeader("Referer", WebConfig.Main_Url+"/xsleft.aspx?flag=xxcx");
                HttpResponse responseMain = client.execute(mainGet);
                InputStream is = responseMain.getEntity().getContent();
                String html = "";
                try {
                    html = WebUtils.getHtml(is, "GB2312");
                    String chengji= Jsoup.parse(html).getElementById("DataGrid1").text().replace("学年 学期 课程名称 课程类型 任课教师 考核方式 总评成绩 补考成绩 重修成绩 重修成绩2 重修成绩3 绩点 应得学分 ","");
                    String zhongxuefen= Jsoup.parse(html).getElementById("Table1").text();

//            String[] strs=chengji.split("201[0-9]-201[0-9]");

                    System.out.println(zhongxuefen);
                    System.out.println(chengji);
                    break;
                } catch (Exception e) {
                    System.out.println("解析html失败！");
                    e.printStackTrace();
                }
            }catch (IOException e){
                continue;
            }
        }


    }

    /**
     * 单独的client关闭方法
     */
    public void closeClient(){
        try {
            client.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void newClient(){
        client= HttpClients.createDefault();
    }


}
