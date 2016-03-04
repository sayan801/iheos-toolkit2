package gov.nist.toolkit.toolkitServices.translators;

import gov.nist.toolkit.configDatatypes.client.PatientError;
import gov.nist.toolkit.configDatatypes.client.PatientErrorList;
import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.configDatatypes.client.PidBuilder;
import gov.nist.toolkit.toolkitServicesCommon.resource.PatientErrorListResource;
import gov.nist.toolkit.toolkitServicesCommon.resource.PatientErrorMapResource;
import gov.nist.toolkit.toolkitServicesCommon.resource.PatientErrorResource;

/**
 *
 */
public class PatientErrorMapTranslator {

    public static PatientErrorMapResource toResource(PatientErrorMap pem) {
        PatientErrorMapResource resource = new PatientErrorMapResource();
        for (String tt : pem.keySet()) {
            resource.put(tt, toResource(pem.get(tt)));
        }
        return resource;
    }

    static PatientErrorListResource toResource(PatientErrorList pem) {
        PatientErrorListResource resource = new PatientErrorListResource();
        for (PatientError pe : pem.values()) {
            resource.add(toResource(pe));
        }
        return resource;
    }

    static PatientErrorResource toResource(PatientError pe) {
        PatientErrorResource resource = new PatientErrorResource();
        resource.setErrorCode(pe.getErrorCode());
        resource.setPatientId(pe.getPatientId().asString());
        return resource;
    }

    public static PatientErrorMap fromResource(PatientErrorMapResource resource) {
        PatientErrorMap map = new PatientErrorMap();
        for (String tt : resource.keySet()) {
            map.put(tt, fromResource(resource.get(tt)));
        }
        return map;
    }

    public static PatientErrorList fromResource(PatientErrorListResource resource) {
        PatientErrorList pel = new PatientErrorList();
        for (PatientErrorResource per : resource.values()) {
            pel.add(fromResource(per));
        }
        return pel;
    }

    public static PatientError fromResource(PatientErrorResource resource) {
        PatientError pe = new PatientError();
        pe.setPatientId(PidBuilder.createPid(resource.getPatientId()));
        pe.setErrorCode(resource.getErrorCode());
        return pe;
    }
}
