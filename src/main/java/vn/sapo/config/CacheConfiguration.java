package vn.sapo.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    @PreDestroy
    public void destroy() {
        log.info("Closing Cache Manager");
        Hazelcast.shutdownAll();
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        log.info("Starting HazelcastCacheManager");
        return new HazelcastCacheManager(hazelcastInstance);
    }

    @Bean
    public Config config() {
        log.info("Configuring Hazelcast");
        Config config = new Config();
        config.setInstanceName("hazelcast-instance");
        config.getNetworkConfig().setPort(5701);
        config.getNetworkConfig().setPortAutoIncrement(true);

        // local
        System.setProperty("hazelcast.local.localAddress", "127.0.0.1");
        config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("localhost").addMember("127.0.0.1").addMember("192.168.30.69").setEnabled(true);

        config.getMapConfigs().put("default", initializeDefaultMapConfig());
        config.setManagementCenterConfig(initializeDefaultManagementCenterConfig());
        return config;
    }

    public HazelcastInstance hazelcastInstance() {
        ClientConfig clientConfig = new ClientConfig();
        HazelcastInstance instance = HazelcastClient.newHazelcastClient(clientConfig);
        return instance;
    }

    private MapConfig initializeDefaultMapConfig() {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setBackupCount(1);
        mapConfig.setEvictionPolicy(EvictionPolicy.LRU);
        mapConfig.setMaxSizeConfig(new MaxSizeConfig(0, MaxSizeConfig.MaxSizePolicy.USED_HEAP_SIZE));
        mapConfig.setTimeToLiveSeconds(3600);
        return mapConfig;
    }

    private ManagementCenterConfig initializeDefaultManagementCenterConfig() {
        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
        managementCenterConfig.setEnabled(true);
        managementCenterConfig.setUrl("http://localhost:8080/hazelcast-mancenter");
        managementCenterConfig.setUpdateInterval(3);
        return managementCenterConfig;
    }

}
