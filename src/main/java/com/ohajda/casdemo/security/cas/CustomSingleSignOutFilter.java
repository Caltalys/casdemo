package com.ohajda.casdemo.security.cas;

import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.session.SingleSignOutHandler;
import org.jasig.cas.client.util.AbstractConfigurationFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jgribonvald on 01/06/16.
 */

/**
 * Implements the Single Sign Out protocol.  It handles registering the session and destroying the session.
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1
 */
public final class CustomSingleSignOutFilter extends AbstractConfigurationFilter {

    private static final CustomSingleSignOutHandler HANDLER = new CustomSingleSignOutHandler();

    private AtomicBoolean handlerInitialized = new AtomicBoolean(false);

    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        if (!isIgnoreInitConfiguration()) {

            this.setArtifactParameterName(this.getString(ConfigurationKeys.ARTIFACT_PARAMETER_NAME));
            this.setLogoutParameterName(this.getString(ConfigurationKeys.LOGOUT_PARAMETER_NAME));
            this.setFrontLogoutParameterName(this.getString(ConfigurationKeys.FRONT_LOGOUT_PARAMETER_NAME));
            this.setRelayStateParameterName(this.getString(ConfigurationKeys.RELAY_STATE_PARAMETER_NAME));
//            this.setCasServerUrlPrefix(this.getString(ConfigurationKeys.CAS_SERVER_URL_PREFIX));
            HANDLER.setArtifactParameterOverPost(this.getBoolean(ConfigurationKeys.ARTIFACT_PARAMETER_OVER_POST));
            HANDLER.setEagerlyCreateSessions(this.getBoolean(ConfigurationKeys.EAGERLY_CREATE_SESSIONS));
        }
        HANDLER.init();
        handlerInitialized.set(true);
    }

    public void setArtifactParameterName(final String name) {
        HANDLER.setArtifactParameterName(name);
    }

    public void setLogoutParameterName(final String name) {
        HANDLER.setLogoutParameterName(name);
    }

    public void setFrontLogoutParameterName(final String name) {
        HANDLER.setFrontLogoutParameterName(name);
    }

    public void setRelayStateParameterName(final String name) {
        HANDLER.setRelayStateParameterName(name);
    }

    public void setCasServerUrlPrefix(final String casServerUrlPrefix) {
        HANDLER.setCasServerUrlPrefix(casServerUrlPrefix);
    }

    public void setSessionMappingStorage(final SessionMappingStorage storage) {
        HANDLER.setSessionMappingStorage(storage);
    }

    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        /**
         * <p>Workaround for now for the fact that Spring Security will fail since it doesn't call {@link #init(FilterConfig)}.</p>
         * <p>Ultimately we need to allow deployers to actually inject their fully-initialized {@link org.jasig.cas.client.session.SingleSignOutHandler}.</p>
         */
        if (!this.handlerInitialized.getAndSet(true)) {
            HANDLER.init();
        }

        if (HANDLER.isTokenRequest(request)) {
            filterChain.doFilter(servletRequest, servletResponse);
            HANDLER.process(request, response);
        } else if (HANDLER.process(request, response)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void destroy() {
        // nothing to do
    }

    protected static CustomSingleSignOutHandler getSingleSignOutHandler() {
        return HANDLER;
    }
}

