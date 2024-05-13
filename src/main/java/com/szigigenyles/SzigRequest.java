package com.szigigenyles;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;

@Component
public class SzigRequest {
    @Autowired
    private ZeebeClient zeebeClient;
    private final Random random = new Random();

    private HashMap<String, Boolean> generateBooleanHashMap(String s) {
        HashMap<String, Boolean> variables = new HashMap<>();
        if( this.random.nextBoolean() ){
            variables.put(s, true);
        } else {
            variables.put(s, false);
        }

        return variables;
    }


    @ZeebeWorker(type = "SubmitApplication", autoComplete = true)
    public void SubmitApplication(){
        HashMap<String, Boolean> variables = new HashMap<>();
        variables.put("photoPossible", random.nextBoolean());
        variables.put("fingerprintPossible", random.nextBoolean());
        variables.put("needTemporary", random.nextBoolean());
        variables.put("eSim", random.nextBoolean());
        variables.put("wantESign", random.nextBoolean());
        variables.put("pickUpInGO", random.nextBoolean());
        variables.put("handInNeeded", random.nextBoolean());

        zeebeClient.newPublishMessageCommand()
                .messageName("applicationIn")
                .correlationKey("")
                .variables(variables)
                .send();
    }


    @ZeebeWorker(type = "CheckApplication", autoComplete = true)
    public HashMap<String, Boolean> CheckApplication(){
         return generateBooleanHashMap("isIdentityOK");
    }

    @ZeebeWorker(type = "IdentificatePerson", autoComplete = true)
    public HashMap<String, Boolean> IdentifyPerson(){
        return generateBooleanHashMap("isGuardianIdentityOK");
    }

    @ZeebeWorker(type = "CheckDeclaration", autoComplete = true)
    public HashMap<String, Boolean> CheckDeclaration(){
        return generateBooleanHashMap("isDeclarationOK");
    }

    @ZeebeWorker(type = "CheckIds", autoComplete = true)
    public HashMap<String, Boolean> CheckIds(){
        HashMap<String, Boolean> variables = generateBooleanHashMap("isIdOK");
        String s  ="idMatch";
        if( this.random.nextBoolean() ){
            variables.put(s, true);
        } else {
            variables.put(s, false);
        }
        return variables;
    }

    @ZeebeWorker(type = "NotifyClient", autoComplete = true)
    public void NotifyClient(@Variable Boolean isIdOK){
        zeebeClient.newPublishMessageCommand()
                .messageName("NotifyClient")
                .correlationKey("notified")
                .variables(isIdOK)
                .send();
    }

    @ZeebeWorker(type = "CheckFormat", autoComplete = true)
    public HashMap<String, Boolean> CheckFormat(){
        return generateBooleanHashMap("isFormatOK");
    }

    @ZeebeWorker(type = "CheckContent", autoComplete = true)
    public HashMap<String, Boolean> CheckContent(){
        return generateBooleanHashMap("isContentOK");
    }

    @ZeebeWorker(type = "RequestInformation", autoComplete = true)
    public void RequestInformation(){
        zeebeClient.newPublishMessageCommand()
                .messageName("DeclinedRequest")
                .correlationKey("declined")
                .send();
    }

    @ZeebeWorker(type = "CheckPhoto", autoComplete = true)
    public HashMap<String, Boolean> CheckPhoto(){
        return generateBooleanHashMap("isPhotoOK");
    }

    @ZeebeWorker(type = "TakeFingerprint", autoComplete = true)
    public HashMap<String, Boolean> TakeFingerprint(){
        return generateBooleanHashMap("isFingerprintOK");
    }

    @ZeebeWorker(type = "CheckSignature", autoComplete = true)
    public HashMap<String, Boolean> CheckSignature(){
        return generateBooleanHashMap("isSignatureOK");
    }

    @ZeebeWorker(type = "ForwardRequest", autoComplete = true)
    public void ForwardRequest(@Variable String pickUpInGO){
        zeebeClient.newPublishMessageCommand()
                .messageName("ForwardRequest")
                .correlationKey("forward")
                .variables(pickUpInGO)
                .send();
    }

    @ZeebeWorker(type = "ShipIDCard", autoComplete = true)
    public void ShipIDCard(){
        zeebeClient.newPublishMessageCommand()
                .messageName("ShipIDCard")
                .correlationKey("sent")
                .send();
    }

    @ZeebeWorker(type = "DocumentArrivedToGO", autoComplete = true)
    public void DocumentArrivedToGO(){
        zeebeClient.newPublishMessageCommand()
                .messageName("DocumentArrivedToGO")
                .correlationKey("arrived")
                .send();
    }

    @ZeebeWorker(type = "Post", autoComplete = true)
    public void Post(){
        zeebeClient.newPublishMessageCommand()
                .messageName("Post")
                .correlationKey("arrived")
                .send();
    }

}
