package server;

/**
 * Created by andilyliao on 16-6-1.
 */

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InboundHandler1 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
         ByteBuf buf= (ByteBuf) msg;
        byte[] buff = new byte[buf.readableBytes()];
        buf.readBytes(buff);
        buf.release();
        String req= new String(buff,"utf8");
        String res= (String) doAan(req);
        ByteBuf encoded = ctx.alloc().buffer(1024);
        encoded.writeBytes(res.getBytes());
        ctx.write(encoded);
        ctx.flush();
        System.out.println("finish");
    }

    public Object doAan(String req) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String[] list=req.split("[-]");
        String type="server."+list[0];
        String method=list[1];
        Class cls = Class.forName(type);
        Constructor constructor=cls.getConstructor();
        Status status=new Status();
        String res = "";
        if (list.length>2){
            Method[] ms=cls.getMethods();
            for (Method m:ms
                 ) {
                if (m.getName().equals(method)){
                    Class []c= m.getParameterTypes();
                    Object [] args=new Object[list.length-2];
                    for (int i=0;i<args.length;i++){
                        args[i]=new Gson().fromJson(list[i+2],c[i]);
                    }
                   status.data= m.invoke(constructor.newInstance(),args);
                    res=new Gson().toJson(m.invoke(constructor.newInstance(),
                            args));
                }
            }

        }else {
            Method m=cls.getMethod(method);
            res=new Gson().toJson(m.invoke(constructor.newInstance()));
        }

        return res;
    }

}
