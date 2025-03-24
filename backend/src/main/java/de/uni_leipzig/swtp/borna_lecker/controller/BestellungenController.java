package de.uni_leipzig.swtp.borna_lecker.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.uni_leipzig.swtp.borna_lecker.annotations.RequireRole;
import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import de.uni_leipzig.swtp.borna_lecker.entities.Bestellung;
import de.uni_leipzig.swtp.borna_lecker.entities.Kunde;
import de.uni_leipzig.swtp.borna_lecker.exceptions.IDNotFoundException;
import de.uni_leipzig.swtp.borna_lecker.repositories.AccountRepository;
import de.uni_leipzig.swtp.borna_lecker.repositories.KundeRepository;
import de.uni_leipzig.swtp.borna_lecker.services.BestellService;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/bestellungen")
public class BestellungenController {

    @Autowired
    BestellService bestellService;

    @Autowired
    KundeRepository kundeRepository;

    @Autowired
    AccountRepository accountRepository;

    public BestellungenController() {

    }

    @RequireRole(Rolle.KÜCHE)
    @GetMapping("")
    public ResponseEntity<List<Bestellung>> getBestellungen(@RequestParam("standort") String standort,
            @RequestParam("datum") String datum) {
        List<Bestellung> bestellungen = bestellService.getBestellungen();
        if (datum != null) {
            LocalDate date = LocalDate.parse(datum);
            bestellungen.removeIf(bestellung -> !bestellung.getDatum().equals(date));
        }
        if (standort == null) {
            bestellungen.removeIf(
                    bestellung -> bestellung.getKunde() != null
                            && !bestellung.getKunde().getStandort().equals(standort));
            bestellungen.removeIf(
                    bestellung -> bestellung.getAccount() != null
                            && !bestellung.getAccount().getStandort().equals(standort));
        }
        return new ResponseEntity<>(bestellungen, HttpStatus.OK);
    }

    @RequireRole(Rolle.KÜCHE)
    @PostMapping("/{ID}/status")
    public ResponseEntity<Object> updateBestellStatus(@PathVariable("ID") int ID,
            @RequestBody boolean status,
            HttpServletResponse response) {

        bestellService.updateBestellStatus(ID, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequireRole(Rolle.KÜCHE)
    @GetMapping("/{ID}")
    public ResponseEntity<Bestellung> getBestellung(@PathVariable("ID") String ID) {
        try {
            return new ResponseEntity<>(bestellService.getBestellungFürPerson(ID, LocalDate.now()), HttpStatus.OK);
        } catch (IDNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    static private class SetBestellungRequest {
        public String kundennummer;
        public LocalDate datum;
        public int essenswahl;
    }

    @RequireRole({ Rolle.KÜCHE, Rolle.GRUPPENLEITUNG })
    @PostMapping("")
    public ResponseEntity<Bestellung> setBestellung(@RequestBody SetBestellungRequest bestellenRequest)
            throws Exception {

        int kundenNummer;
        try {
            kundenNummer = Integer.parseInt(bestellenRequest.kundennummer);
            Optional<Kunde> existingKunde = kundeRepository.findById(kundenNummer);
            if (existingKunde.isPresent()) {
                try {
                    bestellService.setBestellungFürKunde(bestellenRequest.kundennummer, bestellenRequest.datum,
                            bestellenRequest.essenswahl);

                    return new ResponseEntity<>(HttpStatus.OK);
                } catch (UsernameNotFoundException e) {
                    throw new IDNotFoundException("Benutzername nicht gefunden");
                }
            }
        } catch (NumberFormatException e) {
        }

        Optional<Account> existingAccount = accountRepository.findById(bestellenRequest.kundennummer);
        if (existingAccount.isPresent()) {
            try {
                bestellService.setBestellungFürAccount(bestellenRequest.kundennummer, bestellenRequest.datum,
                        bestellenRequest.essenswahl);

                return new ResponseEntity<>(HttpStatus.OK);
            } catch (UsernameNotFoundException e) {
                throw new IDNotFoundException("Benutzername nicht gefunden");
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
