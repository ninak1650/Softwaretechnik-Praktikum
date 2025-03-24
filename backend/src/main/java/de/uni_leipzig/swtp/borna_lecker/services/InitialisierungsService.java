package de.uni_leipzig.swtp.borna_lecker.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import de.uni_leipzig.swtp.borna_lecker.repositories.AccountRepository;
import jakarta.annotation.PostConstruct;

@Service
public class InitialisierungsService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private Logger logger = LoggerFactory.getLogger(InitialisierungsService.class);

    @PostConstruct
    public void init() {
        logger.warn("Erstelle Testdaten...");
        initialAccount();
    }

    public void initialAccount() {

        // Verwaltungsaccount erstellen
        Optional<Account> verwaltung = accountRepository.findById("Admin");
        if (verwaltung.isEmpty()) {
            accountService.createAccount(
                    "Admin",
                    "Admin",
                    "Admin",
                    null,
                    null,
                    Rolle.VERWALTUNG);
            verwaltung = accountRepository.findById("Admin");
        } else {
        }

    }
}
