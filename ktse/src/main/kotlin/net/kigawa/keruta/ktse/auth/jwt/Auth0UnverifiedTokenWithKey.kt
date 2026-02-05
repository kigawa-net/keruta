package net.kigawa.keruta.ktse.auth.jwt

import com.auth0.jwt.algorithms.Algorithm
import net.kigawa.keruta.ktcp.server.auth.jwt.UnverifiedTokenWithKey

class Auth0UnverifiedTokenWithKey(unverifiedToken: Auth0UnverifiedToken, value: Algorithm): UnverifiedTokenWithKey {
}
