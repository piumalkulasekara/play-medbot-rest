package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;

import java.util.List;

public class ResponseController extends Controller {


    public String generateResponse(String type, List<String> passingList) {

        if (type.equals("doctor")) {

            ObjectNode result = Json.newObject();
            String doc = result.put("doctor", passingList.get(0)).toString();

            return doc;
        } else {
            ObjectNode result = Json.newObject();
            for (String symptom : passingList) {
                result.put("symptoms", symptom);
            }
            String sym = result.toString();
            return sym;
        }

    }
}
