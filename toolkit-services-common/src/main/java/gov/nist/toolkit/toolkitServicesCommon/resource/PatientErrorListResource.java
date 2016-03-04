package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.configDatatypes.client.Pid;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@XmlRootElement
public class PatientErrorListResource implements Serializable {
    List<PatientErrorResource> list = new ArrayList<>();

    public PatientErrorListResource() {}

    public void add(PatientErrorResource pe) {
        if (list.contains(pe)) return;
        list.add(pe);
    }

    public void remove(PatientErrorResource pe) {
        for (PatientErrorResource e : list) {
            if (e.equals(pe)) {
                list.remove(pe);
                return;
            }
        }
    }

    public List<PatientErrorResource> values() { return list; }

    public String getErrorName(Pid pid) {
        for (PatientErrorResource pe : list) {
            if (pe.patientId.equals(pid)) return pe.errorCode;
        }
        return null;
    }

    public boolean isEmpty() { return list.isEmpty(); }

    @Override
    public String toString() {
        return list.toString();
    }
}
