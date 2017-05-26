import Utils.Pw;
import Utils.Web;

/**
 *
 * Created by Ericwyn on 17-5-23.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        Web web=new Web();
        String cookie=null;
        cookie=web.login(Pw.stuNum,Pw.stuPw);
        System.out.println(cookie);
        web.getScore(cookie);
        web.closeClient();
    }
}
