package voxxrin.companion.domain;

import com.google.common.collect.ImmutableSet;
import org.joda.time.DateTime;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;
import restx.security.RestxPrincipal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User implements RestxPrincipal {

    @Id
    @ObjectId
    private String id;

    private String login;

    private String twitterId;

    private String emailAddress;

    private String lastName;

    private String firstName;

    private String displayName;

    private String avatarUrl;

    private Map<String, String> providerInfo;

    private Set<String> roles = new HashSet<>();

    private DateTime lastLoginTime;

    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return login;
    }

    public String getLogin() {
        return login;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public DateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public User setId(final String id) {
        this.id = id;
        return this;
    }

    public User setLogin(final String login) {
        this.login = login;
        return this;
    }

    public User setTwitterId(final String twitterId) {
        this.twitterId = twitterId;
        return this;
    }

    public User setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public User setLastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public User setFirstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public User setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Map<String, String> getProviderInfo() {
        return providerInfo;
    }

    public User setProviderInfo(final Map<String, String> providerInfo) {
        this.providerInfo = providerInfo;
        return this;
    }

    public User setLastLoginTime(final DateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        return this;
    }

    public User setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    @Override
    public ImmutableSet<String> getPrincipalRoles() {
        return ImmutableSet.copyOf(roles);
    }

    public boolean isAdmin() {
        ImmutableSet<String> principalRoles = getPrincipalRoles();
        return principalRoles.contains("ADM") || principalRoles.contains("restx-admin");
    }

    public User setRoles(final Set<String> roles) {
        this.roles = roles;
        return this;
    }
}
