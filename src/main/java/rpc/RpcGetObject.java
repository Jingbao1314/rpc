package rpc;

import com.google.gson.Gson;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

/**
 * @author jijngbao
 * @date 19-4-16
 */
public class RpcGetObject {
    public static <T> T getRemoteProxyObj(final Class<?> serviceInterface, final InetSocketAddress addr) {
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[]{serviceInterface},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        HelloClient helloClient=new HelloClient();
                        String type[]=serviceInterface.getName().split("[.]");
                        helloClient.connect(addr
                                        .getHostName(),addr.getPort(),
                                type[type.length-1],method.getName(),args);
                        String name=method.getReturnType().getSimpleName();
                        switch (name){
                            case "Integer":
                                return Integer.parseInt(helloClient.handler
                                        .res);
                            case "int":
                                return Integer.parseInt(helloClient.handler
                                        .res);
                            case "boolean":
                                if (helloClient.handler
                                        .res.equals("true")){
                                    return true;
                                }else {
                                    return false;
                                }
                            case "Double":
                                return Double.parseDouble(helloClient.handler
                                        .res);
                            case "double":
                                return Double.parseDouble(helloClient.handler
                                        .res);
                            case "String":
                                return helloClient.handler.res;
                            default:
                                return new Gson().fromJson(helloClient.handler
                                        .res,method.getReturnType().getClass());
                        }

                    }
                });
    }
}
