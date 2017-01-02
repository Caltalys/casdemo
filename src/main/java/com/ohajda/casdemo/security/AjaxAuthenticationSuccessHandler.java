package com.ohajda.casdemo.security;

import com.ohajda.casdemo.config.CasProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security success handler, specialized for Ajax requests.
 */
@Component
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Inject
    private CasProperties casProperties;

    public AjaxAuthenticationSuccessHandler() {
        setDefaultTargetUrl("/");
        setTargetUrlParameter("spring-security-redirect");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication)
        throws IOException, ServletException {

        this.handle(request, response, authentication);
        this.clearAuthenticationAttributes(request);
    }


    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = this.determineTargetUrl(request, response);
        if(response.isCommitted()) {
            this.logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
        } else {
            if(targetUrl.contains("/app/login")){
                response.sendRedirect(casProperties.getService().getHome());
            }else{
                response.sendRedirect(targetUrl);
            }

        }
    }

}
