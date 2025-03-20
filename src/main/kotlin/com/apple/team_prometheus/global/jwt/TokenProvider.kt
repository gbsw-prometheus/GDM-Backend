package com.apple.team_prometheus.global.jwt


import com.apple.team_prometheus.domain.auth.AuthUser
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*
import javax.crypto.SecretKey

@Service
class TokenProvider(private val jwtProperties: JwtProperties) {
    private val key: SecretKey = Keys.hmacShaKeyFor(
        Base64.getUrlDecoder().decode(jwtProperties.secretKey)
    )

    private val parser: JwtParser = Jwts.parserBuilder()
        .setSigningKey(key)
        .requireIssuer(jwtProperties.issuer)
        .setAllowedClockSkewSeconds(60)
        .build()

    fun generateToken(user: AuthUser, expiredAt: Duration, isAccessToken: Boolean): String {
        val now = Date()
        val expiry = Date(now.time + expiredAt.toMillis())

        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setHeaderParam("alg", "HS256")
            .setIssuer(jwtProperties.issuer)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .setSubject(user.name)
            .claim("type", if (isAccessToken) "Access" else "Refresh")
            .claim("id", user.id)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getAuthentication(token: String?): Authentication {
        requireNotNull(token) { "Token cannot be null" }

        val claims = getClaims(token)

        val tokenType = claims["type"]?.toString()
            ?: throw IllegalArgumentException("Claims에서 토큰 유형을 찾지 못하였습니다.")
        if (tokenType != "Access") {
            throw IllegalArgumentException("RefreshToken은 인증에 사용 할 수없습니다")
        }

        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        val userDetails = User(
            claims.subject ?: throw IllegalArgumentException("Subject not found in token"),
            "",
            authorities
        )

        return UsernamePasswordAuthenticationToken(userDetails, token, authorities)
    }

    fun getClaims(token: String?): Claims {
        try {
            requireNotNull(token) { "Token cannot be null" }
            return parser.parseClaimsJws(token).body
        } catch (e: JwtException) {
            throw IllegalArgumentException("Invalid JWT token: ${e.message}")
        }
    }


    fun isValidToken(token: String?): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}