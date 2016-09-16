package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.NoSimulatorException;

import java.util.List;

/**
 *
 */
public class HttpActorFactory extends AbstractActorFactory {
    @Override
    protected Simulator buildNew(SimManager simm, SimId simId, boolean configureBase) throws Exception {
        ActorType actorType = ActorType.TEST_HTTP_SIM;
        SimulatorConfig sc;
        if (configureBase)
            sc = configureBaseElements(actorType, simId);
        else
            sc = new SimulatorConfig();
        return new Simulator(sc);
    }

    @Override
    protected void verifyActorConfigurationOptions(SimulatorConfig config) {

    }

    @Override
    public Site getActorSite(SimulatorConfig asc, Site site) throws NoSimulatorException {
        return null;
    }

    @Override
    public List<TransactionType> getIncomingTransactions() {
        return null;
    }
}
