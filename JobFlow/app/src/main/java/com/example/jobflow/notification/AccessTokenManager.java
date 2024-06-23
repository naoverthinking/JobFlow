package com.example.jobflow.notification;

import android.util.Log;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccessTokenManager {
    private static final String FIREBASE_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    public String getAccessToken(){
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"jobflow-36524\",\n" +
                    "  \"private_key_id\": \"94206f8c1b887094f9b9ed6480b18b9d716c8726\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCzN8qzvLk3C9in\\nttVHgjfdS8rDurY9+1kYcVCZNM6unmCl74d0Wxeqjs7TFjAbPizoE8Z+SXZpU4Dv\\nHKhNc+5ywdlZGr5e9DTUS9NhvYOqa4dyqCPVOTg5IEoAzfsojBagomDjlwTEvl19\\nEm+EwoSis1lJl7EE3U983VjJiUmhnk4nNp1VzlQFfhsfHAtHDRWmIO1VSnVpTM5R\\nbvVxY12ygD2d68/t4Mid0DNgLfr1Q7mN6aoQ/gBklnGo5os54Zh1rxFt03Wj9+LX\\nJRLj2ZEO4RG7cNujJVVej1p2g7zcW0jPbrdLz31Yna3EBMJugjlb2EhUlLFyHhrX\\naqbiWLMBAgMBAAECggEADESkM1gWrLI6p0ZwhKfhF5y92VaxbX5Ee9Qb8pTnFhSA\\n5LMIKHqo3NPQTCpfaioALHXYF2KDgNPGxneFfOHT/5aOAJfAUuMwL50uL+lndzXL\\nrJledJTpaJ2xeyseh4jJbr8W11WJ7cbjQvHNYP7o0fNpiAxigYT90TwcvRURDYdZ\\nmkelI8+d20APKZYJgZ3u63DGlgHU5H3CqENG9yxuGqIpbPpqrnLGAT4KZMcqpNL1\\nAgPZCzPm/7ZxxdxpUGQVam3f4F7Qqs7kD90XsR+wWjYOHoUIxEPkE5U1zBnS6+es\\nIm6jzHCv8kumOAGX82NA3wgLfg2PAaTgL9V6cXStbQKBgQDXjvhPrFAMaYiVXX1m\\nJm8xt2MRYiGhL5yABShTk/P7HBgMTgvtq4GzQ+plVloO0IsWVKMLW6ACI8MV1Bnj\\nE7Z7GCaMBryoSWvqnaAg6NdYcgHF+mjSd6daKJPFEUFdcViIOHGymW9fQTbqNgZ1\\n8yM+DDHT+6oRXqs4ZAdj+hUORQKBgQDU121BuV7p9jWcB/7vGeMBkeN6A17CboVB\\n5k7AMmiB5blY8t7VU+9pzcpg0L8y3A9IkOtCIn1LjZfbWaV8uJHs2LsE1uhoWD8k\\nMRCs+otyAoV88DMd0u9q9NtzFTFPPeWa4RkNQG9qvhsycr36Bs7EoNd8FoyNmrz6\\nAYIzzbVrjQKBgQC7+dqCqDJ6UIGiN5AGR5gKE/FZlGjdRkbEwTqkf85w+dPizyDJ\\n1xlY+e3rBz2r06KnC5HPcsx7cTJL09XrVyJ3/9yFmc5lNvnb9C9S45n/cBQp0LAe\\ncefH0MDkRqLo2NIqoIZOCE2+FIHOBuTlcO0Xy8ycHsf1M8g+ecSUI96+vQKBgBlT\\nQMbPPcHs79gPA4CDvgUTzNNZz06nQ+fhdYFc/h5mrTUuQOF1SUl3mRsH+5gbox3p\\nUHTgEbSJtoX9FrnOZF6gMzCSObkgtvKhe/B5J1p7flKtagekb6R0WiBJZ0KchNN2\\n8Toobplu4WGRUZWMddHjqYA0VqkKvm2o/EsZsPqJAoGBAIDp/3D64WjDKFugAcYN\\nTyiH8jSWVSM/gCNQxrnBqDmsUtR41FTL1ObUQ1AHMfZC2Wt4/hlPG6wjIxOmnspJ\\nWt2Zkt8bbsyZ44w2v8MoM/gbQ+g3cRaaf7dMBqX1cLm8XaEblmAXcG6Hz/HFL35V\\nR7A6ugpvy5pZXqGhaTdPQ2iH\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-zrvrl@jobflow-36524.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"115430199653420111805\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-zrvrl%40jobflow-36524.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(Lists.newArrayList(FIREBASE_SCOPE));
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
