package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.util.Collection;

/**
 *
 */
public class ResultInspector extends Place {
    private String placeName = "";
    private Collection<Result> results;
    private MetadataCollection mc;
    private SiteSpec siteSpec;

    public ResultInspector() {
        super();
        GWT.log("Build Inspector Place");
    }

    public ResultInspector(String placeName) {
        super();
        GWT.log("Build Inspector Place: " + placeName);
        this.placeName = placeName;
    }

    public static class Tokenizer implements PlaceTokenizer<ResultInspector> {
        @Override
        public ResultInspector getPlace(String s) {
            return new ResultInspector(s);
        }

        @Override
        public String getToken(ResultInspector place) {
            return place.placeName;
        }
    }
    // This is necessary!
    // The GWT place controller uses this to see if the newly requested place already exists.
    // If it exists it reuses it. Toolkit doesn't work this way. We use Places to represent
    // a tool in a tab and we want to be able to have multiple copies of a tool/tab running
    // at the same time. The easy way to accomplish this is to break the idea
    // of Place equality.  (Vote for equal rights for Places!)
    @Override
    public boolean equals(Object o) {
        return false;
    }

    public String getName() {
        return placeName;
    }



    public Collection<Result> getResults() {
        return results;
    }

    public void setResults(Collection<Result> results) {
        this.results = results;
    }

    public SiteSpec getSiteSpec() {
        return siteSpec;
    }

    public void setSiteSpec(SiteSpec siteSpec) {
        this.siteSpec = siteSpec;
    }

    public MetadataCollection getMc() {
        return mc;
    }

    public void setMc(MetadataCollection mc) {
        this.mc = mc;
    }
}
