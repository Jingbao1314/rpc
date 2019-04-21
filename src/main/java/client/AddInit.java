package client;

import rpc.RpcGetObject;

import java.net.InetSocketAddress;

/**
 * @author jijngbao
 * @date 19-4-16
 */
public class AddInit implements Add{
    @Override
    public int add(int a, int b) {
        Add add= RpcGetObject.getRemoteProxyObj(Add.class,new
                InetSocketAddress("localhost", 7721));
        return add.add(a,b);
    }
}
