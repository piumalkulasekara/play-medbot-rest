package controllers;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedical;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedicalClient;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesResult;
import play.Logger;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AWSTextController extends Controller {

    private static final String ACCESS_KEY = "AKIA6HCX36T4FZ3VAUZU";
    private static final String SECRET_KEY = "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";
    private static final String REGION = "us-east-1";
    private ArrayList<String> symptoms;
    private ArrayList<String> medication;
    private ArrayList<String> anatomy;
    private HashMap<String, ArrayList> analysedResultsHashMap;

    /**
     * Getter for analysed results
     *
     * @return HashMap with analysed results
     */
    public HashMap<String, ArrayList> getAnalysedResultsHashMap() {
        return analysedResultsHashMap;
    }

    public AWSTextController getClassifiedText(String rawText) {
        // AWS Credential Handler
        AWSCredentialsProvider credentials
                = new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));

        // AWS Comprehend Medical Client
        AWSComprehendMedical client = AWSComprehendMedicalClient.builder()
                .withCredentials(credentials)
                .withRegion(REGION)
                .build();

        //Requesting for entity analysis
        DetectEntitiesRequest request = new DetectEntitiesRequest();
        request.setText(rawText);

        //Response (Amazon Lambda Response)
        DetectEntitiesResult result = client.detectEntities(request);
        result.getEntities().forEach(System.out::println);
        analyseTexts(result);

        return this;
    }

    /***
     * Analysing extracted text and clustering them into seperate lists
     *
     * @param response get the detected entity response as input
     * @return analysed HashMap with Medical conditions, Anatomy or Medications
     */
    private HashMap<String, ArrayList> analyseTexts(DetectEntitiesResult response) {
        //initializing array lists
        symptoms = new ArrayList<>();
        medication = new ArrayList<>();
        anatomy = new ArrayList<>();


        //Clustering response data into lists
        for (int i = 0; i < response.getEntities().size(); i++) {
            if (response.getEntities().get(i).getCategory().equalsIgnoreCase("MEDICAL_CONDITION")
                    || response.getEntities().get(i).getCategory().equalsIgnoreCase("ANATOMY")) {

                if (response.getEntities().get(i).getCategory().equalsIgnoreCase("ANATOMY")) {

                    anatomy.add(response.getEntities().get(i).getText());
                } else {

                    symptoms.add(response.getEntities().get(i).getText());
                }

            } else if (response.getEntities().get(i).getCategory().equalsIgnoreCase("MEDICATION")) {

                medication.add(response.getEntities().get(i).getText());
            }
        }
        String[] keys = {"anatomy", "symptoms", "medication"};
        ArrayList[] values = {anatomy, symptoms, medication};
        analysedResultsHashMap = new HashMap<>();
        Logger.debug("Anatomy: " + anatomy);
        Logger.debug("Medication: " + medication);
        Logger.debug("Symptoms: " + symptoms);

        for (int i = 0; i < 3; i++) {
            analysedResultsHashMap.put(keys[i], values[i]);
        }
        Logger.debug("Hash Map:" + analysedResultsHashMap);
        return analysedResultsHashMap;
    }

    /***
     * Updating for Query friendly values. Replace all spaces with underscores
     * @return updated values with underscore
     */
    public List<String> passingValuesForQuering() {

        List<String> symptoms = new ArrayList<>();
        List<String> updatedValues = new ArrayList<>();
        HashMap<String, ArrayList> hashMap = new HashMap<>();

        hashMap = getAnalysedResultsHashMap();
        Logger.debug("Hash Before: " + hashMap);

        symptoms = hashMap.get("symptoms");
        Logger.debug("Symptom List B4: " + symptoms);

        for (String item : symptoms) {
            String changed = item.replaceAll(" ", "_").toLowerCase();
            updatedValues.add(changed);
        }
        Logger.debug("Symps: " + updatedValues);

        return updatedValues;

    }

}
