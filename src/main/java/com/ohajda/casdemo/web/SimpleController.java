package com.ohajda.casdemo.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohajda.casdemo.config.CasProperties;
import com.ohajda.casdemo.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.ohajda.casdemo.domain.Authority;
import com.ohajda.casdemo.domain.User;
import com.ohajda.casdemo.service.UserService;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/app")
public class SimpleController {
    private final Logger log = LoggerFactory.getLogger(SimpleController.class);

    @Inject
    private UserService userService;

    @Inject
    private CasProperties casProperties;


    @RequestMapping("/login")
    public Object login(Model model, HttpServletRequest request) throws IOException {

        log.debug("=========> Login Call ");


        // retrieve the user
        User user = userService.getUserWithAuthorities();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Set<String> roles = new HashSet<String>();
        for (Authority authority : user.getAuthorities()) {
            roles.add(authority.getName());
        }
        UserDTO responseUser = new UserDTO(
            user.getLogin(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getActivated(),
            user.getLangKey(),
            roles);




        log.debug("UserDetails {}", responseUser);
        log.debug("#########   RedirectURL is present ? {}", getRedirectUrl(request));
        String uri = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        log.debug("#########   Requested url {}", uri);

        String jsUser = new ObjectMapper().writeValueAsString(responseUser);
        String content, type;
        if (request.getParameter("postMessage") != null) {
            log.debug("Going on postMessage");
            type = "text/html";
            content = "Login success, please wait...\n<script>\n (window.opener ? (window.opener.postMessage ? window.opener : window.opener.document) : window.parent).postMessage('loggedUser=' + JSON.stringify("
                + jsUser + "), '*');\n</script>";
        } else if (request.getParameter("callback") != null) {
            log.debug("Going on callback");
            type = "application/x-javascript";
            content = request.getParameter("callback") + "(" + jsUser + ")";
        } else {
            log.debug("Going on else");
            type = "application/json";
            content = jsUser;
        }
        log.debug("content : {}", content);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.valueOf(type));
        return new ResponseEntity<String>(content, responseHeaders, HttpStatus.OK);
    }


     @RequestMapping("/logout")
     public String logout() {

     log.debug("/logout redirect:"+casProperties.getUrl().getLogout() + "?service="+ casProperties.getService().getHome());

     return "redirect:"+casProperties.getUrl().getLogout() + "?service="+ casProperties.getService().getHome();
     }

    @RequestMapping(value = { "/", "" }, method = RequestMethod.GET)
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth.getName();
        log.info("passing in /");
        model.addAttribute("user", user);

        return "index";

    }

    @RequestMapping(value = "/secure", method = RequestMethod.GET)
    public String secure(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth.getName();
        log.info("passing in /");
        model.addAttribute("user", user);

        return "secure/index";
    }

    @RequestMapping(value = "/filtered", method = RequestMethod.GET)
    public String filtered(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth.getName();
        log.info("passing in /");
        model.addAttribute("user", user);

        return "secure/admin/index";
    }

    protected String getRedirectUrl(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, null);
            if (savedRequest != null) {
                return savedRequest.getRedirectUrl();
            }
        }

		/* return a sane default in case data isn't there */
        return request.getContextPath() + "/";
    }
}
