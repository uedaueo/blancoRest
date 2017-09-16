package blanco.rest.common;

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

        ResponseHeader info = null;
        String status = null;
        ArrayList<ErrorItem> errors = null;
        JsonNode response = null;

        Iterator<Map.Entry<String, JsonNode>> fieldIte = node.fields();
        while (fieldIte.hasNext()) {
            Map.Entry<String, JsonNode> fieldEntry = fieldIte.next();
            JsonNode value = fieldEntry.getValue();
            if (value != null) {
                if ("info".equalsIgnoreCase(fieldEntry.getKey())) {
                    info = this.parseResponseHeaser(value);
                } else if ("status".equalsIgnoreCase(fieldEntry.getKey())) {
                    status = value.asText();
                } else if ("errors".equalsIgnoreCase(fieldEntry.getKey())) {
                    errors = this.parseErrors(value);
                } else if ("telegram".equalsIgnoreCase(fieldEntry.getKey())) {
                    response = value;
                }
            }
        }


        Util.infoPrintln(LogLevel.LOG_DEBUG,"deserialize");
        CommonResponse cr = new CommonResponse();
        cr.setinfo(info);
        cr.setstatus(status);

        ObjectMapper mapper = new ObjectMapper();

        ApiTelegram responseClassInstance = null;
        if (response != null) {
            responseClassInstance = mapper.convertValue(response, this.responseClass.getClass());
        }

        cr.settelegram(responseClassInstance);

        return cr;
    }

    private ResponseHeader parseResponseHeaser(JsonNode node) {
        ResponseHeader header = new ResponseHeader();

        String token = null;
        String lang = null;

        Iterator<Map.Entry<String,JsonNode>> fieldIte = node.fields();
        while(fieldIte.hasNext()){
            Map.Entry<String, JsonNode>fieldEntry = fieldIte.next();
            JsonNode value = fieldEntry.getValue();
            if(value != null){
                if("token".equalsIgnoreCase(fieldEntry.getKey())){
                    token = value.asText();
                } else if ("lang".equalsIgnoreCase(fieldEntry.getKey())){
                    lang = value.asText();
                }
            }
        }

        header.settoken(token);
        header.setlang(lang);

        return header;
    }

    private ArrayList<ErrorItem> parseErrors(JsonNode node) {
        ArrayList<ErrorItem> errors = null;

        if (node.isArray()) {
            errors = new ArrayList<>();
            for (JsonNode itemNode : node) {
                ErrorItem errorItem = parseErrorItem(itemNode);
                errors.add(errorItem);
            }
        }

        return errors;
    }

    private ErrorItem parseErrorItem(JsonNode node) {
        ErrorItem errorItem = new ErrorItem();

        String code = "";
        ArrayList<String> messages = new ArrayList<>();

        Iterator<Map.Entry<String, JsonNode>> fieldIte = node.fields();
        while (fieldIte.hasNext()) {
            Map.Entry<String, JsonNode> fieldEntry = fieldIte.next();
            JsonNode value = fieldEntry.getValue();
            if (value != null) {
                if ("code".equalsIgnoreCase(fieldEntry.getKey())) {
                    code = value.asText();
                } else if ("messages".equalsIgnoreCase(fieldEntry.getKey())) {
                    for (JsonNode m : value) {
                        messages.add(m.asText());
                    }
                }
            }
        }

        errorItem.setcode(code);
        errorItem.setmessages(messages);

        return errorItem;
    }

    public ApiTelegram getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(ApiTelegram responseClass) {
        this.responseClass = responseClass;
    }
}

