package com.ohajda.casdemo.config;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by robert on 15/11/2016.
 */
@ConfigurationProperties(prefix = "cas", ignoreUnknownFields = false)
public class CasProperties {

    private Service service = new Service();

    private Url url = new Url();

    public Service getService() {
        return service;
    }

    public Url getUrl() {
        return url;
    }


    public static class Service {
        @NotBlank
        private String home;

        @NotBlank
        private String security;

        public String getHome() {
            return home;
        }

        public void setHome(String home) {
            this.home = home;
        }

        public String getSecurity() {
            return security;
        }

        public void setSecurity(String security) {
            this.security = security;
        }
    }

    public static class Url {

        @NotBlank
        private String prefix;

        @NotBlank
        private String login;

        @NotBlank
        private String logout;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getLogout() {
            return logout;
        }

        public void setLogout(String logout) {
            this.logout = logout;
        }
    }
}
