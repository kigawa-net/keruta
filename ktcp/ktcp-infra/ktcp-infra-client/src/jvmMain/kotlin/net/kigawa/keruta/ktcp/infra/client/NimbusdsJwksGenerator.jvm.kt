package net.kigawa.keruta.ktcp.infra.client

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import net.kigawa.keruta.ktcp.base.auth.key.JavaKeyPairInitializer
import net.kigawa.keruta.ktcp.model.auth.key.PemKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*


@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class NimbusdsJwksGenerator(
    private val javaKeyPairInitializer: JavaKeyPairInitializer,
): NimbusdsJwksGeneratorBase() {

    actual override fun platformGenerate(key: PemKey): Map<String?, Any?> {
        val keyPair = javaKeyPairInitializer.initialize(key)


        // 2. NimbusのRSAKeyビルダーでJWKを作成
        val jwks = RSAKey.Builder(keyPair.public as RSAPublicKey)
            .privateKey(keyPair.private as RSAPrivateKey) // 署名に使う場合は秘密鍵もセット
            .keyUse(KeyUse.SIGNATURE)                    // 用途：署名
            .algorithm(JWSAlgorithm.RS256)               // アルゴリズム：RS256
            .keyID(UUID.randomUUID().toString())         // 鍵の識別子（重要！）
            .build()
        val jwkSet = JWKSet(jwks)

        // toJSONObject(true) の引数 true は「公開鍵のみ（public only）」という意味
        // これを忘れると秘密鍵が漏洩するリスクがあるため、.toPublicJWKSet() を使うのがより安全です
        return jwkSet.toPublicJWKSet().toJSONObject()
    }

}
