package serviceDiscovery;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author jijngbao
 * @date 19-4-20
 */
public class ServiceRegistry implements Watcher{
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();


    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        //zookeeper配置数据存放路径
            String path = Constant.ZK_REGISTRY_PATH+ServiceRegistry.class.getSimpleName();
        //连接zookeeper并且注册一个默认的监听器
        ServiceRegistry registry=new ServiceRegistry();
        zk = new ZooKeeper("127.0.0.1:2181", 5000, //
                registry);
        connectedSemaphore.await();
//        zk.create(path,registry.getClass().getName().getBytes(), ZooDefs.Ids
//                .OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        registry.register(path,ServiceRegistry.class.getName());
        System.out.println(new String(zk.getData(path, true, stat)));
    }

    public void register(String path,String data) {
        if (data != null) {
            if (zk != null) {
//                AddRootNode(Constant.ZK_REGISTRY_PATH); // Add root node if not exist
//                path=Constant.ZK_REGISTRY_PATH+path;
                createNode(path, data);
            }
        }
    }

    public void delete(String path) throws KeeperException, InterruptedException {
        Stat s = zk.exists(path, false);
        if (s != null) {
            zk.delete(path,-1);//忽略所有版本
        }

    }

//    private void AddRootNode(String path){
//        try {
//            Stat s = zk.exists(path, false);
//            if (s == null) {
//                zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//            }
//        } catch (KeeperException e) {
//            logger.error(e.toString());
//        } catch (InterruptedException e) {
//            logger.error(e.toString());
//        }
//    }

    private void createNode(String path,String data) {
        try {
            byte[] bytes = data.getBytes();
            zk.create(path,bytes, ZooDefs.Ids
                    .OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException e) {
            logger.error("", e);
        }
        catch (InterruptedException ex){
            logger.error("", ex);
        }
    }

    public void process(WatchedEvent event) {
        if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {  //zk连接成功通知事件
            Watcher.Event.EventType eventType=event.getType();
            switch (eventType){
                case None:
                    connectedSemaphore.countDown();
                    break;
                case NodeCreated:
                    try {
                        System.out.println("创建了节点"+event.getPath()+"    " +
                                ""+"数据是:"+zk.getData(event.getPath(),true,stat));
                        break;
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                case NodeDeleted:
                    System.out.println("节点"+event.getPath()+"被删除");
                    break;
                case NodeDataChanged:
                    try {
                        System.out.println("数据已修改，新值为：" + new String(zk
                                .getData(event.getPath(), true, stat)));
                        break;
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                case NodeChildrenChanged:
                    System.out.println("ChildrenChanged被修改");
                    break;
            }
        }else {
            System.out.println("else");
        }
    }
}
