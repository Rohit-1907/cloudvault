package com.cloudvault.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for public file sharing pages
 * Serves HTML pages for easy file access via share links
 */
@Slf4j
@Controller
public class PublicShareController {

    /**
     * Serve public file access page
     * GET /file/{shareToken}
     */
    @GetMapping("/file/{shareToken}")
    public String getFileSharePage(@PathVariable String shareToken) {
        log.info("Access to public share link: {}", shareToken);
        return "redirect:/share.html?token=" + shareToken;
    }

    /**
     * Alternative endpoint with query parameter
     * GET /share?token={shareToken}
     */
    @GetMapping("/share")
    public String getSharePageWithToken(@RequestParam String token) {
        log.info("Access to share page with token: {}", token);
        return "redirect:/share.html?token=" + token;
    }

}
