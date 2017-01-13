package Select.MyClient;

/**
 * Created by Select on 04.01.2017.
 */
public class MainController {
    public static void main(String[] args) {
        Client cl=new Client("127.192.1.24",4750);
        cl.ConnectType("chat");
        cl.SenderMassage("Igor22");
    }
}
