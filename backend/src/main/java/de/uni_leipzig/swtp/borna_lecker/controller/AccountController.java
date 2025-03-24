package de.uni_leipzig.swtp.borna_lecker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.uni_leipzig.swtp.borna_lecker.annotations.RequireRole;
import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import de.uni_leipzig.swtp.borna_lecker.entities.Gruppe;
import de.uni_leipzig.swtp.borna_lecker.repositories.AccountRepository;
import de.uni_leipzig.swtp.borna_lecker.services.AccountService;
import de.uni_leipzig.swtp.borna_lecker.services.GruppenService;
import de.uni_leipzig.swtp.borna_lecker.util.PermissionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private GruppenService gruppenService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private PermissionUtil permissionUtil;

    @RequireRole({ Rolle.VERWALTUNG, Rolle.KÜCHE, Rolle.STANDORTLEITUNG, Rolle.GRUPPENLEITUNG })
    @GetMapping("/{benutzername}")
    public ResponseEntity<Account> getAccount(@PathVariable("benutzername") String benutzername, HttpSession session) {
        try {
            Account account = accountService.getAccount(benutzername);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("")
    public ResponseEntity<Object> getAccounts(HttpSession session) {
        return new ResponseEntity<>(accountService.getAccounts(), HttpStatus.OK);
    }

    static private class CreateAccountRequest {
        public String benutzername;
        public String name;
        public String passwort;
        public String standort;
        public int gruppenNummer;
        public Rolle rolle;
    }

    @RequireRole({ Rolle.VERWALTUNG })
    @PostMapping("")
    public ResponseEntity<Object> createAccount(@RequestBody CreateAccountRequest request) throws Exception {
        Account account = accountRepository.findByBenutzername(request.benutzername);
        if (account != null) {
            return new ResponseEntity<>("Der Benutzername ist bereits vergeben", HttpStatus.BAD_REQUEST);
        }

        Gruppe gruppe = null;
        if (request.gruppenNummer != 0) {
            gruppe = gruppenService.getGruppe(request.gruppenNummer);
            if (gruppe == null) {
                return new ResponseEntity<>("Die angegebene Gruppe wurde nicht gefunden", HttpStatus.NOT_FOUND);
            }
            if (gruppenService.hasGruppenleiter(gruppe)) {
                return new ResponseEntity<>("Diese Gruppe hat bereits einen Gruppenleiter", HttpStatus.BAD_REQUEST);
            }
        }
        accountService.createAccount(request.benutzername, request.passwort,
                request.name, request.standort, gruppe, request.rolle);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    static private class UpdateAccountRequest {
        public String name;
        public String standort;
        public int gruppenNummer;
        public Rolle rolle;
    }

    @RequireRole({ Rolle.VERWALTUNG })
    @PostMapping("/{benutzername}")
    public ResponseEntity<Object> updateAccount(@PathVariable("benutzername") String benutzername,
            @RequestBody UpdateAccountRequest request) throws Exception {
        Gruppe gruppe = null;
        if (request.gruppenNummer != 0) {
            gruppe = gruppenService.getGruppe(request.gruppenNummer);
            if (gruppe == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        Account account = accountRepository.findByBenutzername(benutzername);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        accountService.updateAccount(benutzername, request.name,
                request.standort, gruppe, request.rolle);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequireRole({ Rolle.VERWALTUNG })
    @DeleteMapping("/{benutzername}")
    public ResponseEntity<Object> deleteAccount(@PathVariable("benutzername") String benutzername) throws Exception {
        Account account = accountRepository.findByBenutzername(benutzername);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        accountService.deleteAccount(benutzername);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public static class UpdatePasswortRequest {
        public String passwort;
    }

    @PostMapping("/{benutzername}/passwort")
    public ResponseEntity<Object> updatePasswort(@PathVariable("benutzername") String benutzername,
            @RequestBody UpdatePasswortRequest body, HttpServletRequest request) throws Exception {
        permissionUtil.DenyFremdeAccounts(request, benutzername);
        Account account = accountRepository.findByBenutzername(benutzername);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        accountService.updatePasswort(benutzername, body.passwort);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequireRole({ Rolle.VERWALTUNG })
    @PostMapping("/{benutzername}/reset")
    public ResponseEntity<Object> resetPasswort(@PathVariable("benutzername") String benutzername,
            @RequestBody UpdatePasswortRequest body, HttpServletRequest request) throws Exception {
        Account account = accountRepository.findByBenutzername(benutzername);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        accountService.resetPassword(benutzername, body.passwort);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
