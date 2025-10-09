package com.example.service_one.utilits;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class genPairKey {
    //не читаемо, вообще main должен быть пустым, только с инициализацией

    /*
    @SpringBootApplication
@EnableFeignClients
@EnableRetry
@EnableAsync
public class EpassAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(EpassAdminApplication.class, args);
    }

}
     */
    //что это вообще за main? у вас же отдельный класс с ним есть
    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(256);
        KeyPair keyPair = keyGen.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        String privateKeyString = Base64.getEncoder().encodeToString(
                privateKey.getEncoded()
        );

        String publicKeyString = Base64.getEncoder().encodeToString(
                publicKey.getEncoded()
        );

        System.out.println("=== Service-two PRIVATE key (for service_two) ===");
        System.out.println(privateKeyString);
        System.out.println("\n=== Service-one PUBLIC key (for service_one) ===");
        System.out.println(publicKeyString);

        System.out.println("\n=== Verification ===");
        KeyFactory keyFactory = KeyFactory.getInstance("EC");

        PrivateKey verifiedPrivateKey = keyFactory.generatePrivate(
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString))
        );
        System.out.println("Private key verified: " + verifiedPrivateKey.getAlgorithm());

        PublicKey verifiedPublicKey = keyFactory.generatePublic(
                new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString))
        );

        System.out.println("Public key verified: " + verifiedPublicKey.getAlgorithm());
    }
}