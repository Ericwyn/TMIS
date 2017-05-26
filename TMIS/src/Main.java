import Utils.Pw;
import Utils.Web;

/**
 *
 * Created by Ericwyn on 17-5-23.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        new Thread(new UserThread(Pw.stuNum,Pw.stuPw)).start();
    }
}
