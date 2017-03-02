package in.vasista.vbiz.rest.util;

import java.util.Date;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;

import com.nimbusds.jwt.SignedJWT;

import org.ofbiz.base.util.Debug;


public class JwtUtil {
    public static final String module = JwtUtil.class.getName();
    //::TODO:: move this to general properties?
    private static String secret="lpaY04p7afBGZR13D4FpQBPceaGO39fMccmtmi6lyeDQEDZ3xDIDXpEGLHzUmip";
    

    /**
     * Tries to parse specified String as a JWT token. If successful, returns User object with username (extracted from token).
     * If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
     * 
     * @param token the JWT token to parse
     * @return the User object extracted from specified token or null if a token is invalid.
     */
    public static String parseToken(String token) {
    	String username = null;
        SignedJWT jwsObject;
        try {
           
        	jwsObject = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secret);
            if (jwsObject.verify(verifier)) {
            	ReadOnlyJWTClaimsSet claims = jwsObject.getJWTClaimsSet();
            	if (!claims.getExpirationTime().after(new Date())) {
            		Debug.logError("Token expired, expiry time: " + claims.getExpirationTime(), module);            		
            		return username;
            	}
                username = jwsObject.getJWTClaimsSet().getSubject();
                Debug.log("username: " + username, module);
            }
                
        } catch (Exception e) {
    		Debug.logError("Failed to parse token: " + token + "; " + e, module);
            return username;
        }
    	return username;
    }

    /**
     * Generates a JWT token containing username as subject, and userId and role as additional claims. These properties are taken from the specified
     * User object. Tokens validity is infinite.
     * 
     * @param u the user for which the token will be generated
     * @return the JWT token
     */
    public static String generateToken(String username) {
        String token = null;

        try {

            JWSSigner signer = new MACSigner(secret);

            JWTClaimsSet claimsSet = new JWTClaimsSet();
            claimsSet.setSubject(username);
            claimsSet.setIssuer("NHDC App");
            claimsSet.setExpirationTime(new Date(new Date().getTime() + 1440 * 1000)); //::TODO:

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            signedJWT.sign(signer);
            token = signedJWT.serialize();
            Debug.log("token: " + token, module);            
        }
        catch (Exception ex) {
        		Debug.logError("Failed to generate token for: " + username + "; " + ex, module);
        }
        return token;
    }
}