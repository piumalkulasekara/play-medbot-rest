package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.*;

import java.util.HashMap;
import java.util.Map;

/*
* This class will handle all the incoming requests and redirect them accoridingly.
*   @author - Piumal Kulasekara
* */
public class RequestController extends Controller {

    public Result getDataFromChat(String data){
        CMTextController cmTextController = new CMTextController();
        JsonNode js = cmTextController.toExtractEntities(data);
        System.out.println(js);
        return ok(Json.toJson(js));
    }
}
