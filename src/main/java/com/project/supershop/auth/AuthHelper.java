package com.project.supershop.auth;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.List;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.JsonTokenParser;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;
import net.oauth.jsontoken.crypto.HmacSHA256Verifier;
import net.oauth.jsontoken.crypto.SignatureAlgorithm;
import net.oauth.jsontoken.crypto.Verifier;
import net.oauth.jsontoken.discovery.VerifierProvider;
import net.oauth.jsontoken.discovery.VerifierProviders;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

public class AuthHelper {
    private static final String AUDIENCE = "FROMANHTIEN";
    private static final String ISSUER = "SUPERSHOP";
    private static final String SIGNING_KEY = "SUPERSHOPSEM4KEYSUPERHARDTOGUEST@@$$";


    //Tạo 1 JsonWeb token được mã hoá dưới dạng String JWT.
    public static String createJsonWebToken(String userId, Long durationDay){
        //Current time and signing algorithm
        Calendar cal = Calendar.getInstance();

        //Gọi biến signer từ lớp hmacSHA256Signer để mã hoá chữ ký theo thuật toán của nó.
        HmacSHA256Signer signer;

        try{
            signer = new HmacSHA256Signer(ISSUER, null, SIGNING_KEY.getBytes());
        }catch (InvalidKeyException e){
            throw new RuntimeException();
        }

        //Config JSON TOKEN
        //Chuyển Chữ kỹ được mã hoá bên trên thành TOKEN.
        JsonToken token = new net.oauth.jsontoken.JsonToken(signer);
        token.setAudience(AUDIENCE);
        token.setIssuedAt(new org.joda.time.Instant(cal.getTimeInMillis()));
        token.setExpiration(new org.joda.time.Instant(cal.getTimeInMillis() + 1000L * 60L * 60L * 24L * durationDay));

        //Configure request object.
        JsonObject request = new JsonObject();
        request.addProperty("userId", userId);

        JsonObject payload = token.getPayloadAsJsonObject();
        payload.add("UserInfo", request);

        try{
            return token.serializeAndSign();
        }catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    //Decode JWT Token để kiểm tra danh tính và role.
    public static TokenInfo VerifyToken(String token){
        try {
            final Verifier hmacVerifier = new HmacSHA256Verifier(SIGNING_KEY.getBytes());

            VerifierProvider hmacLocator = new VerifierProvider() {

                @Override
                public List<Verifier> findVerifier(String id, String key){
                    return Lists.newArrayList(hmacVerifier);
                }
            };
            VerifierProviders locators = new VerifierProviders();
            locators.setVerifierProvider(SignatureAlgorithm.HS256, hmacLocator);
            net.oauth.jsontoken.Checker checker = new net.oauth.jsontoken.Checker(){

                @Override
                public void check(JsonObject payload) throws SignatureException {
                    // don't throw - allow anything
                }

            };
            //Ignore Audience does not mean that the Signature is ignored
            JsonTokenParser parser = new JsonTokenParser(locators,
                    checker);
            JsonToken jt;
            try {
                jt = parser.verifyAndDeserialize(token);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            }
            JsonObject payload = jt.getPayloadAsJsonObject();
            TokenInfo t = new TokenInfo();
            String issuer = payload.getAsJsonPrimitive("iss").getAsString();
            String userIdString =  payload.getAsJsonObject("info").getAsJsonPrimitive("userId").getAsString();
            if (issuer.equals(ISSUER) && !StringUtils.isBlank(userIdString))
            {
                t.setUserId(new ObjectId(userIdString));
                t.setIssued(new DateTime(payload.getAsJsonPrimitive("iat").getAsLong()));
                t.setExpires(new DateTime(payload.getAsJsonPrimitive("exp").getAsLong()));
                return t;
            }
            else
            {
                return null;
            }
        } catch (InvalidKeyException e1) {
            throw new RuntimeException(e1);
        }
    }
    }


