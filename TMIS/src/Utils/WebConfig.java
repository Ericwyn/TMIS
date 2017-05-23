package Utils;

/**
 *
 * Created by Ericwyn on 17-5-23.
 */
public class WebConfig {
    //教务系统主页
    public static final String Main_Url="http://ericwyn.jios.org";
    //验证码请求页面
    public static final String SECRETCODE_URL=Main_Url+"/CheckCode.aspx";
    //登录页面
    public static final String LOGIN_URL = Main_Url+"/default3.aspx";
    //登录后的系统主页面
    public static final String LOGIN_SUCCESS_MAIN=Main_Url+"/xsmainfs.aspx?xh=";
    //查询成绩的页面
    public static final String CHECK_SCORE_URL=Main_Url+"/xscj.aspx?xh=";

}
