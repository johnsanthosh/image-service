package pool;

import constants.ServiceConstants;
import model.Ec2Instance;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import utils.Utils;

import java.util.Properties;

public class Ec2InstanceFactory extends BasePooledObjectFactory {


    @Override
    public Object create() throws Exception {
        Properties properties = Utils.fetchProperties();
        return new Ec2Instance(properties.get(ServiceConstants.AWS_ACCESS_KEY).toString(),
                properties.get(ServiceConstants.AWS_SECRET_KEY).toString(),
                properties.get(ServiceConstants.AWS_AMI_ID).toString());
    }

    @Override
    public PooledObject wrap(Object o) {
        return new DefaultPooledObject(o);
    }
}
