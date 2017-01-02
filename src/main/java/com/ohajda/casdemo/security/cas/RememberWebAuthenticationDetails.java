package com.ohajda.casdemo.security.cas;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;


public class RememberWebAuthenticationDetails extends WebAuthenticationDetails {
    private final Logger log = LoggerFactory.getLogger(RememberWebAuthenticationDetails.class);

	private final String queryString;

	public RememberWebAuthenticationDetails(HttpServletRequest request) {
		super(request);

		this.queryString = request.getQueryString();
		log.debug("Remember request {}", this.queryString);
	}

	public String getQueryString() {
		log.debug("Remember request get queryString {}", this.queryString);
		return this.queryString;
	}
}
