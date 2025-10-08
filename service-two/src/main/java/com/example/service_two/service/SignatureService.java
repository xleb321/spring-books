package com.example.service_two.service;

import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Base64;

@Service
public class SignatureService {
    private static final String PRIVATE_KEY_BASE64  = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCB3voSrW7cZbtfw81oLAOfiKiQYqrmJn9c2QmHbINZksg==";

    public byte[] signData(byte[] data) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(PRIVATE_KEY_BASE64);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PrivateKey privateKey = keyFactory.generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(privateKeyBytes));

        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(privateKey);

        return signature.sign();
    }

    public boolean verifySignature(byte[] data, byte[] signatureBytes, String publicKeyBase64) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(new java.security.spec.X509EncodedKeySpec(publicKeyBytes));

        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initVerify(publicKey);
        signature.update(data);

        return signature.verify(signatureBytes);
    }
}
