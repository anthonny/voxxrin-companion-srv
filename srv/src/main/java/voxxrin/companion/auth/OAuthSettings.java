package voxxrin.companion.auth;

import restx.config.Settings;
import restx.config.SettingsKey;

@Settings
public interface OAuthSettings {

    @SettingsKey(key = "oauth.secrets.token")
    String oauthSecretsToken();

    @SettingsKey(key = "oauth.twitter.apiKey")
    String oauthTwitterApiKey();

    @SettingsKey(key = "oauth.twitter.apiSecret")
    String oauthTwitterApiSecret();

    @SettingsKey(key = "oauth.twitter.accessToken")
    String oauthTwitterAccessToken();

    @SettingsKey(key = "oauth.twitter.accessTokenSecret")
    String oauthTwitterAccessTokenSecret();

    @SettingsKey(key = "oauth.linkedin.clientId")
    String oauthLinkedinClientId();

    @SettingsKey(key = "oauth.linkedin.apiSecret")
    String oauthLinkedinApiSecret();

    @SettingsKey(key = "oauth.facebook.appId")
    String oauthFacebookAppId();

    @SettingsKey(key = "oauth.facebook.appSecret")
    String oauthFacebookAppSecret();

    @SettingsKey(key = "oauth.github.appId")
    String oauthGithubAppId();

    @SettingsKey(key = "oauth.github.appSecret")
    String oauthGithubAppSecret();

    @SettingsKey(key = "oauth.google.appId")
    String oauthGoogleAppId();

    @SettingsKey(key = "oauth.google.appSecret")
    String oauthGoogleAppSecret();
}
