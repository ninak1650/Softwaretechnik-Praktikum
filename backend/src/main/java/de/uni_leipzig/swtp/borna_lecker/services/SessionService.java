package de.uni_leipzig.swtp.borna_lecker.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.exceptions.IDNotFoundException;
import jakarta.servlet.http.HttpSession;

@Service
public class SessionService {
    @Autowired
    private AccountService accountService;

    private Logger log = LoggerFactory.getLogger(SessionService.class);

    public Account validateSessionAndReturnAccount(HttpSession session) {
        try {
            if (session.getAttribute("username") != null) {
                try {
                    return accountService.getAccount((String) session.getAttribute("username"));
                } catch (IDNotFoundException e) {
                    log.info("account %s not found", (String) session.getAttribute("username"));
                    return null;
                }
            } else {
                log.info("no authenticated session");
                return null;
            }
        } catch (IllegalStateException e) {
            log.info("invalid session");
            return null;
        }
    }
}
