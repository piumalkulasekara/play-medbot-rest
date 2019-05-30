package controllers;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedical;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedicalClient;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesResult;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;

/***
 * This class will handle all request and text data processing relevant to
 * AWS Comprehend Medical API and pre-text processing as well
 *
 * @author piumalkulasekara
 */
public class CMTextController extends Controller {

    private static final String ACCESS_KEY = "AKIA6HCX36T4FZ3VAUZU";
    private static final String SECRET_KEY = "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";
    private static final String REGION = "us-east-2";

    private static ArrayList<String> symptoms;
    private static ArrayList<String> medication;
    private static ArrayList<String> anatomy;
    private AWSComprehendMedical client;

    /***
     * initiate the class
     */
    public CMTextController() {
        initiateConnection();
    }

    /**
     * This method will initiate a connection between AWS and med-bot api
     *
     * @author piumalkulasekara
     */
    public Result initiateConnection(){
        try {
            // AWS Credential Handler
            AWSCredentialsProvider credentials
                    = new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));

            // AWS Comprehend Medical Client
            client = AWSComprehendMedicalClient.builder()
                    .withCredentials(credentials)
                    .withRegion(REGION)
                    .build();
            return ok();
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getLocalizedMessage());

        }
        // TODO: 2019-05-22 add error handling if connection was unsuccessful. Check AWS documentation.

    }

    /**
     *  This method will send text to AWS CM Api and get analysed entities as the respond
     * @param text the unprocessed text extracted from chat bot.
     * @return
     */
    public JsonNode toExtractEntities(String text){
        ArrayList<String> response = new ArrayList<>();
        //Requesting for entity analysis
        DetectEntitiesRequest request = new DetectEntitiesRequest();
        request.setText(text);

        //Response (Amazon Lambda Response)
        DetectEntitiesResult result = client.detectEntities(request);
        result.getEntities().forEach(System.out::println);
        response.add(result.getEntities().toString());

        return Json.toJson(response);
    }

}
