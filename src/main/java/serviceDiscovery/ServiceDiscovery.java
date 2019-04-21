package serviceDiscovery;

import java.util.List;
import java.util.Set;

/**
 * @author jijngbao
 * @date 19-4-20
 */
public class ServiceDiscovery {
    /* *
     *@describe 获取服务列表
     */
    public Set<String> getServerList(){
        return ZookeeperWatcher.SERVER_CLASS.keySet();
    }

    public static void main(String[] args) {
        ServiceDiscovery discovery=new ServiceDiscovery();
        for (String ele:discovery.getServerList()){
            System.out.println(ele);
        }
        System.out.println(ZookeeperWatcher.SERVER_CLASS.isEmpty());
    }

}
