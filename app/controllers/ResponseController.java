package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Symptom;
import play.libs.Json;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

public class ResponseController extends Controller {


    public String generateResponse(String type, List<String> passingList) {

        if (type.equals("doctor")) {

            ObjectNode result = Json.newObject();
            String doc = result.put("doctor", passingList.get(0)).toString();
            return Json.stringify(result);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode result = Json.newObject();

            ArrayList<Symptom>  symptoms = new ArrayList<>();



            for (String symptom : passingList) {
              symptoms.add(new Symptom(symptom, false));
            }
            try {
                String arrayToJson = objectMapper.writeValueAsString(symptoms);
                result.put("symptoms", arrayToJson);

                System.out.println(result);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
           return Json.stringify(result);
        }

    }
}
