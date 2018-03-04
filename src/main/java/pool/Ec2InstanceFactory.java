package pool;

import constants.ServiceConstants;
import model.Ec2Instance;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;


public class Ec2InstanceFactory extends BasePooledObjectFactory {

    @Override
    public Object create() throws Exception {
        return new Ec2Instance(ServiceConstants.AWS_ACCESS_KEY_ID, ServiceConstants.AWS_SECRET_ACCESS_KEY,
                ServiceConstants.EC2_AMI_ID);
    }

    @Override
    public PooledObject wrap(Object o) {
        return new DefaultPooledObject(o);
    }
}
