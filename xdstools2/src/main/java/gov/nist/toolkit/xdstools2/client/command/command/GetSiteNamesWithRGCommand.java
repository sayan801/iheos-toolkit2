package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 11/1/16.
 */
public abstract class GetSiteNamesWithRGCommand extends GenericCommand<CommandContext,List<String>>{
    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices().getSiteNamesWithRG(var1,this);
    }
}
