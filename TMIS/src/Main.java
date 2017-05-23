import Utils.Pw;
import Utils.Web;

/**
 *
 * Created by Ericwyn on 17-5-23.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        Web.login(Pw.stuNum,Pw.stuPw);
        YanZhenUtil.getAllOcr("/media/ericwyn/Work/Chaos/IntiliJ Java Project/GdpuCheck/secCode/secretCode");
    }

}
