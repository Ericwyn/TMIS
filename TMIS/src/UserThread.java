import Utils.Pw;
import Utils.Web;

/**
 * 单独用户的查询线程
 * Created by Ericwyn on 17-5-27.
 */
public class UserThread implements Runnable {
    private String stuNum;
    private String stuPw;
    public UserThread(String stuName,String stuPw){
        this.stuNum=stuName;
        this.stuPw=stuPw;
    }
    @Override
    public void run() {
        Web web=new Web();  //新建一个Web连接对象
        String cookie=null;
        cookie=web.login(stuNum,stuPw);
        if(cookie.equals("1")){
            System.out.println("密码错误");
        }else if(cookie.equals("2")){
            System.out.println("用户名不存在");
        }else {
//            System.out.println(cookie);
            web.getScore(cookie);
            web.closeClient();  //关闭这个Web连接
        }

    }
}
