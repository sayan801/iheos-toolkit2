package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RepOrchestrationRequest;
import gov.nist.toolkit.services.client.RepOrchestrationResponse;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;

/**
 *
 */
class BuildRepTestOrchestrationButton extends AbstractOrchestrationButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private TestContext testContext;
    private TestContextDisplay testContextDisplay;
    private FlowPanel initializationResultsPanel = new FlowPanel();

    BuildRepTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextDisplay testContextDisplay, Panel initializationPanel, String label) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
        this.testContext = testContext;
        this.testContextDisplay = testContextDisplay;

        setParentPanel(initializationPanel);
        setLabel(label);
        setResetLabel("Reset");
        build();
        panel().add(initializationResultsPanel);
    }

    @Override
    public void handleClick(ClickEvent clickEvent) {
        String msg = testTab.verifyTestContext();
        if (msg != null) {
            testContextDisplay.launchDialog(msg);
            return;
        }

        initializationResultsPanel.clear();

        RepOrchestrationRequest request = new RepOrchestrationRequest();
        request.setSutSite(testContext.getSiteUnderTest().siteSpec());
        request.setUserName(testTab.getCurrentTestSession());
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingSimulator(!isResetRequested());

        ClientUtils.INSTANCE.getToolkitServices().buildRepTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, RepOrchestrationResponse.class)) return;
                RepOrchestrationResponse orchResponse = (RepOrchestrationResponse) rawResponse;
                testTab.setRepOrchestrationResponse(orchResponse);

                initializationResultsPanel.add(new HTML("Initialization Complete"));

                if (testContext.getSiteUnderTest() != null) {
                    initializationResultsPanel.add(new SiteDisplay("System Under Test Configuration", testContext.getSiteUnderTest()));
                }

                handleMessages(initializationResultsPanel, orchResponse);

                if (orchResponse.getSupportSite() != null) {
                    initializationResultsPanel.add(new SiteDisplay("Supporting Environment Configuration", orchResponse.getSupportSite()));
                }

//                initializationResultsPanel.display(new HTML("<h3>Supporting Environment</h3>"));
//                initializationResultsPanel.display(new SimSystemAnchor("System: " + orchResponse.getSupportSite().getName(), new SiteSpec(orchResponse.getSupportSite().getName())));



                initializationResultsPanel.add(new HTML("<br />"));
                initializationResultsPanel.add(new HTML("Patient ID: " + orchResponse.getPid().toString()));
                initializationResultsPanel.add(new HTML("<br />"));

                initializationResultsPanel.add(new HTML("<h3>Configure your Repository to forward Register transactions to the above Register endpoint.</h3><hr />"));


                // test will be run out of support site so pass it back to conformance test tab
                testTab.setSitetoIssueTestAgainst(orchResponse.getSupportSite().siteSpec());
            }


        });
    }

}