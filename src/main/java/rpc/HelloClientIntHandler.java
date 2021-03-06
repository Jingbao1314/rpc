package rpc;


import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HelloClientIntHandler extends ChannelInboundHandlerAdapter {
    public String res;
    public String type;
    public String method;
    public Object[] args;
    @Override
    // 读取服务端的信息
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf result = (ByteBuf) msg;
        byte[] result1 = new byte[result.readableBytes()];
        result.readBytes(result1);
        result.release();
        res=new String(result1);
        ctx.close();
    }
    @Override
    // 当连接建立的时候向服务端发送消息 ，channelActive 事件当连接建立的时候会触发
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("send");
        String msg = type+"-"+method;
        if (args!=null){
            for (Object ele:args
                 ) {
                msg=msg+"-"+new Gson().toJson(ele);
            }
        }
        ByteBuf encoded = ctx.alloc().buffer(4 * msg.length());
        encoded.writeBytes(msg.getBytes());
        ctx.write(encoded);
        ctx.flush();
    }
}

