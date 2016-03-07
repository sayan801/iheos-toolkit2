package gov.nist.toolkit.toolkitServicesCommon.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Maps between TransactionType name and list of PatientErrors
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientErrorMapResource  {
    List<ErrorMappingResource> list = new ArrayList<>();

    public PatientErrorMapResource() {}

    public String toString() {
        return list.toString();
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public PatientErrorListResource get(String transaction) {
        PatientErrorListResource res = new PatientErrorListResource();
        for (ErrorMappingResource r : list) {
            if (transaction.equals(r.getTransaction())) {
                PatientErrorResource pr = new PatientErrorResource();
                pr.setErrorCode(r.getError());
                pr.setPatientId(r.getPid());
                res.add(pr);
            }
        }
        return res;
    }

    public void put(String transaction, PatientErrorListResource value) {
        for (PatientErrorResource per : value.values()) {
            ErrorMappingResource mapping = new ErrorMappingResource();
            mapping.setTransaction(transaction);
            mapping.setError(per.getErrorCode());
            mapping.setPid(per.getPatientId());
            list.add(mapping);
        }
    }

    public Set<String> keySet() {
        Set<String> set = new HashSet<>();
        for (ErrorMappingResource  res : list) {
            set.add(res.getTransaction());
        }
        return set;
    }
}
