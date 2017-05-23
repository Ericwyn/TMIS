import Utils.Pw;
import Utils.Web;

/**
 *
 * Created by Ericwyn on 17-5-23.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        String cookie=null;
        if((cookie=Web.login(Pw.stuNum,Pw.stuPw))!=null){
            Web.getScore(cookie);
        }

        Web.closeClient();
    }

}
