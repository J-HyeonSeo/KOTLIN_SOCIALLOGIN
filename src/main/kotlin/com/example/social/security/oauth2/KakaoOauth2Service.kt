package com.example.social.security.oauth2

import com.example.social.model.MemberDto
import com.example.social.security.TokenProvider
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class KakaoOauth2Service(
    val tokenProvider: TokenProvider,
    val httpServletResponse: HttpServletResponse,
    val objectMapper: ObjectMapper
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        val logger = KotlinLogging.logger{}

        val userNameAttributeName = userRequest!!.clientRegistration.providerDetails
            .userInfoEndpoint.userNameAttributeName
        val oAuth2User: OAuth2User =
            super.loadUser(userRequest) // Oauth2 정보를 가져옴

        val accountInfo = oAuth2User.getAttribute<Map<String, Any>>("kakao_account")!!

        val email = accountInfo["email"] as String
        val nickname = (accountInfo["profile"] as Map<*, *>?)!!["nickname"] as String
        val profileUrl = (accountInfo["profile"] as Map<*, *>?)!!["profile_image_url"] as String

        //DB에 데이터 저장 및 업데이트
        val member: MemberDto = saveOrUpdateMember(email, nickname, profileUrl)

        //JWT(AccessToken, RefreshToken) 발급해야함.
        val token: String = tokenProvider.generateJwtToken(1L, listOf("ROLE_TEST"))
        val tokenMap = HashMap<String, String>()
        tokenMap["token"] = token

        httpServletResponse.contentType = MediaType.APPLICATION_JSON_VALUE
        try {
            httpServletResponse.writer.use {
                it.print(
                    objectMapper.writeValueAsString(
                        tokenMap
                    )
                )
            }
        } catch (e: Exception) {
            logger.info("Token 응답 쓰기 오류 발생!")
        }

        return DefaultOAuth2User(null,
            oAuth2User.attributes,
            userNameAttributeName
        );
    }

    /**
     * 해당 Member가 이미 존재하면, Update, 새로운 Member이면, 새롭게 DB에 데이터를 Insert하는 메서드.
     */
    internal fun saveOrUpdateMember(email: String, nickname: String, profileUrl: String): MemberDto {

        return MemberDto(0, "test", "test", "test")

    }

}