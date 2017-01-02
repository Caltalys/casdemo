package com.ohajda.casdemo.security.cas;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class RememberWebAuthenticationDetailsSource implements
    AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

	public WebAuthenticationDetails buildDetails(HttpServletRequest request) {
		return new RememberWebAuthenticationDetails(request);
	}
}
