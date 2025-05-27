package com.apple.team_prometheus.global.jwt


import com.apple.team_prometheus.domain.auth.entity.AuthUser
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

    // 토큰 생성 (액세스/리프레시 공통)
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
            .claim("role", user.role.getAuthority())
            .claim("type", if (isAccessToken) "Access" else "Refresh")
            .claim("id", user.id)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    // 액세스 토큰용 인증 객체 생성
    fun getAuthentication(token: String?): Authentication {
        requireNotNull(token) { "Token cannot be null" }

        val claims = getClaims(token)

        val tokenType = claims["type"]?.toString()
            ?: throw IllegalArgumentException("Claims에서 토큰 유형을 찾지 못하였습니다.")
        if (tokenType != "Access") {
            throw IllegalArgumentException("RefreshToken은 인증에 사용할 수 없습니다")
        }

        val authorities = listOf(SimpleGrantedAuthority("ROLE_${claims["role"] ?: "USER"}"))
        val userDetails = User(
            claims.subject ?: throw IllegalArgumentException("Subject not found in token"),
            "",
            authorities
        )

        return UsernamePasswordAuthenticationToken(userDetails, token, authorities)
    }

    // 토큰에서 Claims 추출
    fun getClaims(token: String?): Claims {
        try {
            requireNotNull(token) {
                "Token cannot be null"
            }

            return parser.parseClaimsJws(token).body
        } catch (e: JwtException) {
            throw IllegalArgumentException("Invalid JWT token: ${e.message}")
        }
    }

    // 토큰 유효성 검사
    fun isValidToken(token: String?): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    // 사용자 ID 추출 (리프레시 토큰 검증용)
    fun getUserIdFromToken(token: String?): Long {
        val claims = getClaims(token)
        return claims["id"]?.toString()?.toLong()
            ?: throw IllegalArgumentException("ID not found in token")
    }

    // 토큰 타입 확인
    fun getTokenType(token: String?): String {
        val claims = getClaims(token)
        return claims["type"]?.toString()
            ?: throw IllegalArgumentException("Token type not found in claims")
    }
}