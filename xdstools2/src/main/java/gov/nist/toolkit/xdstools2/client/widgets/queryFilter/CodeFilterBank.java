package gov.nist.toolkit.xdstools2.client.widgets.queryFilter;

import com.google.gwt.user.client.ui.FlexTable;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.CodesResult;
import gov.nist.toolkit.xdstools2.client.command.command.GetCodesConfiguration;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.CodeEditButtonSelector;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class CodeFilterBank  {
    List<CodeFilter> codeFilters = new ArrayList<>();
    public CodesConfiguration codesConfiguration = null;
    public int codeBoxSize = 2;
    StatusDisplay statusDisplay;


    public CodeFilterBank(StatusDisplay statusDisplay) {
        this.statusDisplay = statusDisplay;
        bind();
    }

    private void bind(){
        new GetCodesConfiguration(){
            @Override
            public void onComplete(CodesResult result) {
                for (AssertionResult a : result.result.assertions.assertions) {
                    if (!a.status) {
                        statusDisplay.getResultPanel().add(GenericQueryTab.addHTML("<font color=\"#FF0000\">" + a.assertion + "</font>"));
                    }
                }
                codesConfiguration = result.codesConfiguration;
                for (CodeFilter codeFilter : codeFilters) {
                    codeFilter.editButton.addClickHandler(
                            new CodeEditButtonSelector(
                                    statusDisplay,
                                    codesConfiguration.getCodeConfiguration(codeFilter.codeName),
                                    codeFilter.selectedCodes
                            )

                    );
                }
                enableCodeEditButtons();
            }
        }.run(ClientUtils.INSTANCE.getCommandContext());
    }

    public CodeFilter addFilter(FlexTable paramGrid, int prow, int col, String filterName) {
        CodeFilter codeFilter = new CodeFilter(paramGrid, prow, col, CodesConfiguration.getTitle(filterName), filterName, codeBoxSize);
        addCodeFilter(codeFilter);
        prow++;
        return codeFilter;
    }

    public int addCodeFiltersByName(List<String> names, FlexTable paramGrid, int startingRow, int col, int boxSize) {
        for (String name : names) {
            CodeFilter codeFilter;
            addCodeFilter(codeFilter = new CodeFilter(paramGrid, startingRow++, col, name));
            codeFilter.setCodeBoxSize(boxSize);
        }
        return startingRow;
    }

    public void addCodeFilter(CodeFilter codeFilter) {
        codeFilters.add(codeFilter);
    }

    public CodeFilter getCodeFilter(String codeConfigurationName) {
        for (CodeFilter codeFilter : codeFilters) {
            if (codeFilter.codeName.equals(codeConfigurationName)) {
                return codeFilter;
            }
        }
        return null;
    }

    public void addToCodeSpec(Map<String, List<String>> codeSpec) {
        for (CodeFilter codeFilter : codeFilters) {
            List<String> codeList = codeSpec.get(codeFilter.codeName);
            if (codeList == null) {
                codeList = new ArrayList<>();
                codeSpec.put(codeFilter.codeName, codeList);
            }
            codeList.addAll(codeFilter.getSelected());
        }
    }

    void enableCodeEditButtons() {
        for (CodeFilter codeFilter : codeFilters) {
            codeFilter.editButton.setEnabled(true);
        }
    }

}
