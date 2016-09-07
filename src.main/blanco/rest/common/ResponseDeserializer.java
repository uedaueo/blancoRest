package blanco.rest.common;

import blanco.rest.common.LogLevel;
import blanco.rest.common.Util;
import blanco.rest.valueobject.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by haino on 2016/09/03.
 */
public class ResponseDeserializer extends StdDeserializer<CommonResponse> {

    private ApiTelegram responseClass = null;

    public ResponseDeserializer(){
        this(null);
    }

    public ResponseDeserializer(Class<?> vc){
        super(vc);
    }

    @Override
    public CommonResponse deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        String token = null;
        String lang = null;
        String status = null;
        int code = 0;
        String msg = null;
        JsonNode response = null;

        Iterator<Map.Entry<String, JsonNode>> fieldIte = node.fields();
        while (fieldIte.hasNext()) {
            Map.Entry<String, JsonNode> fieldEntry = fieldIte.next();
            JsonNode value = fieldEntry.getValue();
            if (value != null) {
                if ("token".equalsIgnoreCase(fieldEntry.getKey())) {
                    token = value.asText();
                } else if ("lang".equalsIgnoreCase(fieldEntry.getKey())) {
                    lang = value.asText();
                } else if ("status".equalsIgnoreCase(fieldEntry.getKey())) {
                    status = value.asText();
                } else if ("code".equalsIgnoreCase(fieldEntry.getKey())) {
                    code = value.asInt();
                } else if ("msg".equalsIgnoreCase(fieldEntry.getKey())) {
                    msg = value.asText();
                } else if ("response".equalsIgnoreCase(fieldEntry.getKey())) {
                    response = value;
                }
            }
        }


        Util.infoPrintln(LogLevel.LOG_DEBUG,"deserialize");
        CommonResponse cr = new CommonResponse();
        cr.settoken(token);
        cr.setlang(lang);
        cr.setstatus(status);
        cr.setcode(code);
        cr.setmessage(msg);
        ObjectMapper mapper = new ObjectMapper();

        ApiTelegram responseClassInstance = null;
        if (response != null) {
            responseClassInstance = mapper.convertValue(response, this.responseClass.getClass());
        }

        cr.setresponse(responseClassInstance);


        return cr;
    }

    public ApiTelegram getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(ApiTelegram responseClass) {
        this.responseClass = responseClass;
    }
}

