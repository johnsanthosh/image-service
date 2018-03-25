package pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class Ec2InstancePool extends GenericObjectPool {

    public Ec2InstancePool(PooledObjectFactory factory) {
        super(factory);
    }

    public Ec2InstancePool(PooledObjectFactory factory, GenericObjectPoolConfig config) {
        super(factory, config);
    }
}
