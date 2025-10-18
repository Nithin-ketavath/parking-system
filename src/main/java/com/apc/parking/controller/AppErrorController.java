package com.apc.parking.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCodeAttr = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object messageAttr = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        int statusCode = 0;
        if (statusCodeAttr instanceof Integer) {
            statusCode = (Integer) statusCodeAttr;
        } else if (statusCodeAttr != null) {
            try { statusCode = Integer.parseInt(statusCodeAttr.toString()); } catch (NumberFormatException ignored) {}
        }
        String reason = "Error";
        if (statusCode > 0) {
            HttpStatus hs = HttpStatus.resolve(statusCode);
            if (hs != null) {
                reason = hs.getReasonPhrase();
            }
        }
        String message = messageAttr != null ? messageAttr.toString() : "";

        model.addAttribute("status", statusCode);
        model.addAttribute("reason", reason);
        model.addAttribute("message", message);
        return "error";
    }
}


