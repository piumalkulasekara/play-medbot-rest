package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Symptom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class RequestController extends Controller {
    private AWSTextController awsTextController = new AWSTextController();
    private QueryController queryController = new QueryController();
    private Symptom symptom = new Symptom();
    private ResponseController responseController = new ResponseController();

    public Result userText(Http.Request request) {
        String string = null;
        JsonNode requestBody = request.body().asJson();

        if (requestBody != null) {
            string = requestBody.get("usertext").textValue();

            Logger.of("extracted from raw text: " + string);
            ObjectNode objectNode = Json.newObject();
            try {
                queryController.executeQuery(awsTextController.getClassifiedText(string).passingValuesForQuering());


            } catch (SQWRLException e) {
                return badRequest(e.getLocalizedMessage());
            } catch (SWRLParseException e) {
                return badRequest(e.getLocalizedMessage());
            } catch (OWLOntologyCreationException e) {
                return badRequest(e.getLocalizedMessage());
            }

            return ok(Json.toJson(objectNode));
        } else {
            return badRequest();
        }
//        queryController.executeQuery(awsTextController.getClassifiedText());

    }
}
