package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.toolkitServicesCommon.MapAdapter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Maps between TransactionType name and list of PatientErrors
 */
@XmlRootElement
public class PatientErrorMapResource implements Serializable, Map<String, PatientErrorListResource> {
    @XmlJavaTypeAdapter(MapAdapter.class)
    transient private Map<String, PatientErrorListResource> config = new HashMap<>();

    public PatientErrorMapResource() {}

    @Override
    public int size() {
        return config.size();
    }

    @Override
    public boolean isEmpty() {
        return config.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return config.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return config.containsValue(value);
    }

    @Override
    public PatientErrorListResource get(Object key) {
        return config.get(key);
    }

    @Override
    public PatientErrorListResource put(String key, PatientErrorListResource value) {
        return config.put(key, value);
    }

    @Override
    public PatientErrorListResource remove(Object key) {
        return config.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends PatientErrorListResource> m) {
        config.putAll(m);
    }

    @Override
    public void clear() {
        config.clear();
    }

    @Override
    public Set<String> keySet() {
        return config.keySet();
    }

    @Override
    public Collection<PatientErrorListResource> values() {
        return config.values();
    }

    @Override
    public Set<Entry<String, PatientErrorListResource>> entrySet() {
        return config.entrySet();
    }

    public Map<String, PatientErrorListResource> getMap() { return config; }
}
