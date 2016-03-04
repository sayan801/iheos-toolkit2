package gov.nist.toolkit.toolkitServicesCommon.resource;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 */
@XmlRootElement
public class PatientErrorResource implements Serializable {
    String patientId;
    String errorCode;

    public PatientErrorResource() {}

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return patientId + " ==> " + errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientErrorResource that = (PatientErrorResource) o;

        if (!patientId.equals(that.patientId)) return false;
        return errorCode.equals(that.errorCode);

    }

    @Override
    public int hashCode() {
        int result = patientId.hashCode();
        result = 31 * result + errorCode.hashCode();
        return result;
    }
}
