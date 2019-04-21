package client;

import com.google.gson.Gson;
import rpc.RpcGetObject;
import server.Status;

import java.net.InetSocketAddress;

/**
 * @author jijngbao
 * @date 19-4-16
 */
public class LoginInit implements Login{
    @Override
    public boolean login() {
        Login login= RpcGetObject.getRemoteProxyObj(Login.class,new
        InetSocketAddress("localhost", 7721));
        return login.login();

    }
}
