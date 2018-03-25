package pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Ec2InstancePoolManager {
    private Ec2InstancePool pool;

    @Value("${amazon.ec2.instance.max.count}")
    private int ec2MaxInstanceCount;

    public Ec2InstancePool getPool() {
        return pool;
    }

    public void setPool(Ec2InstancePool pool) {
        this.pool = pool;
    }

    @PostConstruct
    public void initializeInstancePool() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(1);
        config.setMaxTotal(this.ec2MaxInstanceCount);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        pool = new Ec2InstancePool(new Ec2InstanceFactory(), config);
    }

    public Ec2InstancePoolManager() {
        initializeInstancePool();
    }
}
