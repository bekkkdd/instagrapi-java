package kz.biz.galamat.qabanbay;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import javax.xml.bind.annotation.XmlAnyAttribute;
import java.util.Map;

public class RegistrationResponse {
    private Map<String, String> properties;

    @XmlAnyAttribute
    public Map<String, String> getProperties() {
        return properties;
    }
}
