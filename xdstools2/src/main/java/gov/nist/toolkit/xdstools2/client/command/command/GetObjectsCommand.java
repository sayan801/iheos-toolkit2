package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetObjectsRequest;

import java.util.List;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class GetObjectsCommand extends GenericCommand<GetObjectsRequest,List<Result>>{
    @Override
    public void run(GetObjectsRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getObjects(var1,this);
    }
}
