package kahlua.KahluaProject.global.security.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import kahlua.KahluaProject.global.security.google.dto.GoogleProfile;
import kahlua.KahluaProject.global.security.google.dto.GoogleToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class GoogleClient {

    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.create();

    @Value("${spring.security.oauth2.client.registration.google-prod.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google-prod.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google-prod.authorization-grant-type}")
    private String googleGrantType;

    @Value("${spring.security.oauth2.client.provider.google-prod.token-uri}")
    private String googleTokenUri;

    @Value("${spring.security.oauth2.client.provider.google-prod.user-info-uri}")
    private String googleUserInfoUri;

    public GoogleToken getGoogleAccessToken(String code, String redirectUri) {
        String response = restClient.post()
                .uri(googleTokenUri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body("grant_type=" + googleGrantType +
                        "&client_id=" + googleClientId +
                        "&redirect_uri=" + redirectUri +
                        "&code=" + code +
                        "&client_secret=" + googleClientSecret)
                .retrieve()
                .body(String.class);

        try {
            return objectMapper.readValue(response, GoogleToken.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Google access token response", e);
        }
    }

    public GoogleProfile getMemberInfo(GoogleToken googleToken) {
        String response = restClient.get()
                .uri(googleUserInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + googleToken.access_token())
                .retrieve()
                .body(String.class);

        try {
            return objectMapper.readValue(response, GoogleProfile.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Google user info response", e);
        }
    }
}
