package com.szigigenyles;

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
    private final boolean happyPath = true;
    private final Random random = new Random();

    private final boolean errors = false;
    private boolean mainRunning = false;
    private boolean issuerRunning = false;

    private HashMap<String, Boolean> generateBooleanHashMap(String s, int successChance) {
        HashMap<String, Boolean> variables = new HashMap<>();

        if (this.happyPath || this.random.nextInt(0, 100) < successChance) {
            variables.put(s, true);
        } else {
            variables.put(s, false);
        }

        return variables;
    }


    private HashMap<String, Boolean> generateBooleanHashMap(String s, boolean b) {
        if (this.happyPath) {
            b = true;
        }
        HashMap<String, Boolean> variables = new HashMap<>();
        variables.put(s, b);
        return variables;
    }


    @ZeebeWorker(type = "SubmitApplication", autoComplete = true)
    public void SubmitApplication() {
        Sleep(200);
        HashMap<String, Boolean> variables = new HashMap<>();

        if (happyPath) {
            variables.put("isGuardian", false);
            variables.put("photoPossible", true);
            variables.put("fingerprintPossible", true);
            variables.put("needTemporary", true);
            variables.put("eSim", true);
            variables.put("wantESign", true);
            variables.put("pickUpInGO", true);
            variables.put("handInNeeded", true);
            variables.put("isDocumentRequired", true);
        } else {
            variables.put("isGuardian", random.nextInt(0, 100) < 30);
            variables.put("photoPossible", random.nextInt(0, 100) < 95);
            variables.put("fingerprintPossible", random.nextInt(0, 100) < 90);
            variables.put("needTemporary", random.nextInt(0, 100) < 50);
            variables.put("eSim", random.nextInt(0, 100) < 85);
            variables.put("wantESign", random.nextInt(0, 100) < 40);
            variables.put("pickUpInGO", random.nextInt(0, 100) < 10);
            variables.put("handInNeeded", random.nextInt(0, 100) < 90);
            variables.put("isDocumentRequired", random.nextInt(0, 100) < 50);
        }

        zeebeClient.newPublishMessageCommand()
                .messageName("applicationIn")
                .correlationKey("applicationIn")
                .variables(variables)
                .send();
        UnexpectedEvent(1);
    }


    @ZeebeWorker(type = "CheckApplication", autoComplete = true)
    public HashMap<String, Boolean> CheckApplication() {
        Sleep(200);
        mainRunning = true;
        UnexpectedEvent(1);
        return generateBooleanHashMap("isIdentityOK", 80);
    }

    @ZeebeWorker(type = "IdentificatePerson", autoComplete = true)
    public HashMap<String, Boolean> IdentifyPerson() {
        Sleep(200);
        UnexpectedEvent(1);
        return generateBooleanHashMap("isGuardianIdentityOK", 80);
    }

    @ZeebeWorker(type = "CheckDeclaration", autoComplete = true)
    public HashMap<String, Boolean> CheckDeclaration() {
        UnexpectedEvent(1);
        return generateBooleanHashMap("isDeclarationOK", 99);
    }

    @ZeebeWorker(type = "CheckIds", autoComplete = true)
    public HashMap<String, Boolean> CheckIds() {
        Sleep(200);
        HashMap<String, Boolean> variables = generateBooleanHashMap("isIdOK", 87);
        String s = "idMatch";

        if (happyPath || this.random.nextInt(0, 100) < 96) {
            variables.put(s, true);
        } else {
            variables.put(s, false);
        }
        UnexpectedEvent(2);

        return variables;
    }

    @ZeebeWorker(type = "NotifyClient", autoComplete = true)
    public void NotifyClient() {
        Sleep(200);
        zeebeClient.newPublishMessageCommand()
                .messageName("NotifyClient")
                .correlationKey("notified")
                .variables(generateBooleanHashMap("accepted", true))
                .send();
        UnexpectedEvent(1);
    }

    @ZeebeWorker(type = "CheckFormat", autoComplete = true)
    public HashMap<String, Boolean> CheckFormat() {
        Sleep(200);
        UnexpectedEvent(1);
        return generateBooleanHashMap("isFormatOK", 90);
    }

    @ZeebeWorker(type = "CheckContent", autoComplete = true)
    public HashMap<String, Boolean> CheckContent() {
        Sleep(200);
        UnexpectedEvent(0);
        return generateBooleanHashMap("isContentOK", 60);
    }

    @ZeebeWorker(type = "RequestInformation", autoComplete = true)
    public void RequestInformation() {
        Sleep(200);
        mainRunning = false;
        UnexpectedEvent(3);
        zeebeClient.newPublishMessageCommand()
                .messageName("DeclinedRequest")
                .correlationKey("declined")
                .send();
    }

    @ZeebeWorker(type = "CheckPhoto", autoComplete = true)
    public HashMap<String, Boolean> CheckPhoto() {
        Sleep(200);
        UnexpectedEvent(0);
        return generateBooleanHashMap("isPhotoOK", 20);
    }

    @ZeebeWorker(type = "TakeFingerprint", autoComplete = true)
    public HashMap<String, Boolean> TakeFingerprint() {
        Sleep(200);
        UnexpectedEvent(1);
        return generateBooleanHashMap("isFingerprintOK", 85);
    }

    @ZeebeWorker(type = "CheckSignature", autoComplete = true)
    public HashMap<String, Boolean> CheckSignature() {
        Sleep(200);
        UnexpectedEvent(0);
        return generateBooleanHashMap("isSignatureOK", 85);
    }

    @ZeebeWorker(type = "ForwardRequest", autoComplete = true)
    public void ForwardRequest(@Variable Boolean pickUpInGO) {
        Sleep(200);
        zeebeClient.newPublishMessageCommand()
                .messageName("ForwardRequest")
                .correlationKey("forward")
                .variables(generateBooleanHashMap("pickUpInGO", pickUpInGO))
                .send()
                .join();
        issuerRunning = true;
        if (!pickUpInGO) {
            mainRunning = false;
        }
        UnexpectedEvent(2);
    }

    @ZeebeWorker(type = "ShipIDCard", autoComplete = true)
    public void ShipIDCard() {
        Sleep(200);
        mainRunning = false;
        UnexpectedEvent(4);
        zeebeClient.newPublishMessageCommand()
                .messageName("ShipIDCard")
                .correlationKey("sent")
                .send();
    }

    @ZeebeWorker(type = "DocumentArrivedToGO", autoComplete = true)
    public void DocumentArrivedToGO() {
        Sleep(200);
        issuerRunning = false;
        UnexpectedEvent(1);
        zeebeClient.newPublishMessageCommand()
                .messageName("DocumentArrivedToGO")
                .correlationKey("arrived")
                .send();
    }

    @ZeebeWorker(type = "Post", autoComplete = true)
    public void Post() {
        Sleep(200);
        issuerRunning = false;
        UnexpectedEvent(4);
        zeebeClient.newPublishMessageCommand()
                .messageName("Post")
                .correlationKey("arrived")
                .send();
    }

    @ZeebeWorker(type = "InappropriateID", autoComplete = true)
    public void InappropriateID() {
        Sleep(200);
        UnexpectedEvent(1);
        zeebeClient.newPublishMessageCommand()
                .messageName("NotifyClient")
                .correlationKey("notified")
                .variables(generateBooleanHashMap("accepted",   false))
                .send();
    }

    @ZeebeWorker(type = "EndMain", autoComplete = true)
    public void EndMain() {
        if (mainRunning) {
            zeebeClient.newPublishMessageCommand()
                    .messageName("EndMain")
                    .correlationKey("EndMain")
                    .send();
        }
    }

    @ZeebeWorker(type = "EndClient", autoComplete = true)
    public void EndClient() {
        zeebeClient.newPublishMessageCommand()
                .messageName("EndClient")
                .correlationKey("EndClient")
                .send();
    }

    @ZeebeWorker(type = "EndIssuer", autoComplete = true)
    public void EndIssuer() {
        if (issuerRunning) {
            zeebeClient.newPublishMessageCommand()
                    .messageName("EndIssuer")
                    .correlationKey("EndIssuer")
                    .send();
        }
    }

    private void Sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void UnexpectedEvent(int successRate) {
        if (random.nextInt(0, 100) < successRate && errors) {

            Sleep(1000);

            System.out.println("Interrupted");
            zeebeClient.newPublishMessageCommand()
                    .messageName("UnexpectedEvent")
                    .correlationKey("UnexpectedEvent")
                    .send()
                    .join();
        }
    }
}
