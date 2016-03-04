package gov.nist.toolkit.configDatatypes.server;

import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 *
 */
public class PatientErrorMapIO {
    static private ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    public static String marshal(PatientErrorMap patientErrorMap) throws IOException, EncoderException {
        return Base64.encodeBase64String(mapper.writeValueAsString(patientErrorMap).getBytes());
//        return mapper.writeValueAsString(patientErrorMap);
    }

    public static PatientErrorMap unmarshal(String stream) throws IOException {
        return mapper.readValue(new String(Base64.decodeBase64(stream)), PatientErrorMap.class);
//        return mapper.readValue(stream, PatientErrorMap.class);
    }
}
