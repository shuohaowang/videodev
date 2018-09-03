package com.aaron;

import com.aaron.config.ResourceConfig;
import com.aaron.enums.BGMOperatorTypeEnum;
import com.aaron.pojo.Bgm;
import com.aaron.service.BgmService;
import com.aaron.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/*@Component*/
public class ZKCuratorClient {

    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);
    @Autowired
    private BgmService bgmService;
    // public static final String ZOOKEEPER_SERVER = "47.100.120.162:2181";

    @Autowired
    private ResourceConfig resourceConfig;

    public void init(){
        if(client != null){
            return;
        }
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,5);
        //创建客户端
        client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin").build();
        client.start();

        try {
          //  String testNodeData =
                    new String(client.getData().forPath("/bgm/180704ATXZC27C94"));
          // log.info(testNodeData);
            addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 对zknode监听
     * @param nodePath 节点路径
     */
    public void addChildWatch(String nodePath) throws Exception {
        //注册一次持久监听
        final PathChildrenCache cache = new PathChildrenCache(client,nodePath,true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client,
                                   PathChildrenCacheEvent event) throws Exception {
                if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
                     log.info("监听到事件CHILD_ADDED");
                     //1.从数据库查询bgm对象，获取路径path
                    String path = event.getData().getPath();
                    String operatorObjStr = new String(event.getData().getData());
                    Map<String,String> map = JsonUtils.jsonToPojo(operatorObjStr,Map.class);
                    String operatorType = map.get("operType");
                    String songPath = map.get("path");

                    String[] split = path.split("/");
                    String bgmId = split[split.length - 1];
                   /* Bgm bgm = bgmService.queryBgmById(bgmId);
                    if(bgm == null){
                        return;
                    }*/
                    //1.1Bgm所在的相对路径
                //    String songPath = bgm.getPath();
                    //2.定义保存到本地的bgm路径
                    String filePath = resourceConfig.getFileSpace() + songPath;
                    //3.定义下载路径(播放Url)
                    String[] arrPath = songPath.split("\\\\");
                    String finalPath = "";
                    //3.1处理Url斜杠以及编码
                    for(int i = 0; i < arrPath.length; i++){
                        if(StringUtils.isNotBlank(arrPath[i])){
                            finalPath += "/";
                            finalPath += URLEncoder.encode(arrPath[i],"UTF-8");
                        }
                    }
                    String bgmUrl = resourceConfig.getBgmServer() + finalPath;
                    if(operatorType.equals(BGMOperatorTypeEnum.ADD.type)){
                        URL url = new URL(bgmUrl); //下载bgm到Springboot服务器
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url,file);
                        client.delete().forPath(path);
                    }else if(operatorType.equals(BGMOperatorTypeEnum.DELETE.type)){
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }

                }

            }
        });

    }



}
