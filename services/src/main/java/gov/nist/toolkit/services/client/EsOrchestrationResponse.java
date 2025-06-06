package gov.nist.toolkit.services.client;

import gov.nist.toolkit.simcommon.client.SimulatorConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestration Response for Edge Server SUT
 */
public class EsOrchestrationResponse extends AbstractOrchestrationResponse {

    private static final long serialVersionUID = 1L;

    private List<SimulatorConfig> simulatorConfigs = new ArrayList<>();
    private SimulatorConfig rrConfig;

    /**
     * @return the {@link #simulatorConfigs} value.
     */
    public List <SimulatorConfig> getSimulatorConfigs() {
        return simulatorConfigs;
    }

    /**
     * @param simulatorConfigs the {@link #simulatorConfigs} to set
     */
    public void setSimulatorConfigs(List <SimulatorConfig> simulatorConfigs) {
        this.simulatorConfigs = simulatorConfigs;
    }

    /**
     * @return the {@link #rrConfig} value.
     */
    public SimulatorConfig getRRConfig() {
        return rrConfig;
    }

    /**
     * @param rrConfig the {@link #rrConfig} to set
     */
    public void setRRConfig(SimulatorConfig rrConfig) {
        this.rrConfig = rrConfig;
    }

    /* (non-Javadoc)
     * @see gov.nist.toolkit.services.client.AbstractOrchestrationResponse#isExternalStart()
     */
    @Override
    public boolean isExternalStart() {
        return false;
    }



}
