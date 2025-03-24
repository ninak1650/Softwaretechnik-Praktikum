package de.uni_leipzig.swtp.borna_lecker.controller;

import java.time.LocalDate;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.uni_leipzig.swtp.borna_lecker.annotations.RequireRole;
import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import de.uni_leipzig.swtp.borna_lecker.entities.Bestellung;
import de.uni_leipzig.swtp.borna_lecker.entities.Gruppe;
import de.uni_leipzig.swtp.borna_lecker.exceptions.IDNotFoundException;
import de.uni_leipzig.swtp.borna_lecker.services.AccountService;
import de.uni_leipzig.swtp.borna_lecker.services.GruppenService;
import de.uni_leipzig.swtp.borna_lecker.util.PermissionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/gruppen")
public class GruppenController {

    @Autowired
    private GruppenService gruppenService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PermissionUtil permissionUtil;

    @GetMapping("/{ID}")
    public ResponseEntity<Object> getGruppe(@PathVariable("ID") int ID) {
        try {
            Gruppe gruppe = gruppenService.getGruppe(ID);
            return new ResponseEntity<>(gruppe, HttpStatus.OK);
        } catch (IDNotFoundException e) {
            return new ResponseEntity<>("Gruppe nicht gefunden", HttpStatus.NOT_FOUND);
        }
    }

    @RequireRole({ Rolle.GRUPPENLEITUNG, Rolle.VERWALTUNG, Rolle.STANDORTLEITUNG, Rolle.KÜCHE })
    @GetMapping("/{ID}/mitglieder")
    public ResponseEntity<Object> getMitglieder(@PathVariable("ID") int ID, HttpServletRequest request,
            HttpServletResponse response) {

        try {
            Gruppe gruppe = gruppenService.getGruppe(ID);
            // security
            permissionUtil.DenyFremdeGruppenFürGruppenleitung(request, gruppe);
            permissionUtil.DenyFremdeStandorteFürStandortleitung(request, gruppe.getStandort());

            return new ResponseEntity<>(gruppe.getKunden(), HttpStatus.OK);
        } catch (IDNotFoundException e) {
            return new ResponseEntity<>("Gruppe nicht gefunden", HttpStatus.NOT_FOUND);
        }
    }

    @RequireRole({ Rolle.GRUPPENLEITUNG, Rolle.VERWALTUNG, Rolle.STANDORTLEITUNG, Rolle.KÜCHE })
    @GetMapping("/{ID}/bestellung")
    public ResponseEntity<List<Bestellung>> getBestellungenFuerGruppeFuerDatum(
            @PathVariable("ID") int ID,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "datum", required = true) String datum) {
        Gruppe gruppe = gruppenService.getGruppe(ID);

        permissionUtil.DenyFremdeGruppenFürGruppenleitung(request, gruppe);
        permissionUtil.DenyFremdeStandorteFürStandortleitung(request, gruppe.getStandort());

        List<Bestellung> bestellungenDerGruppe = gruppenService.getGruppenbestellungenFuerGruppe(ID,
                LocalDate.parse(datum));

        if (bestellungenDerGruppe.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Erfolgreiche Antwort mit den Bestellungen
        return new ResponseEntity<>(bestellungenDerGruppe, HttpStatus.OK);

    }

    // Gibt für einen Accountnamen alle Gruppen zurück
    @GetMapping("")
    public ResponseEntity<List<Gruppe>> getGruppen(
            @RequestParam(value = "accountname", required = false) String accountname,
            @RequestParam(value = "standort", required = false) String standort,
            HttpServletRequest request,
            HttpServletResponse response) {

        List<Gruppe> gruppen = null;
        if (accountname != null) {
            gruppen = gruppenService.getGruppenForGruppenleitung(accountname);
        } else if (standort != null) {
            gruppen = gruppenService.getGruppenForStandort(standort);
        } else {
            gruppen = gruppenService.getGruppen();
        }

        if (gruppen.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(gruppen, HttpStatus.OK);
        }
    }

    private static class SetVertretungRequest {
        public String vertretung;
    }

    @RequireRole({ Rolle.STANDORTLEITUNG })
    @PostMapping("/{ID}/vertretung")
    public ResponseEntity<Gruppe> setVertretung(@RequestBody SetVertretungRequest request,
            @PathVariable("ID") int ID) throws Exception {

        try {
            gruppenService.getGruppe(ID);
        } catch (IDNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            accountService.getAccount(request.vertretung);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        gruppenService.setGruppenVertretung(ID, request.vertretung);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequireRole({ Rolle.STANDORTLEITUNG })
    @DeleteMapping("/{ID}/vertretung")
    public ResponseEntity<Gruppe> deleteVertretung(@PathVariable("ID") int ID) throws Exception {
        try {
            gruppenService.getGruppe(ID);
        } catch (IDNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        gruppenService.setGruppenVertretung(ID, null);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    static private class CreateGruppeRequest {
        public int gruppenNummer;
        public String gruppenName;
        public String standort;
        public String gruppenLeiter;
    }

    @RequireRole({ Rolle.VERWALTUNG })
    @PostMapping("")
    public ResponseEntity<Object> createGruppe(@RequestBody CreateGruppeRequest request) throws Exception {
        Account gruppenleiter = null;
        if (request.gruppenLeiter != null) {
            gruppenleiter = accountService.getAccount(request.gruppenLeiter);
        }
        gruppenService.createGruppe(request.gruppenNummer, request.gruppenName, request.standort,
                gruppenleiter);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequireRole({ Rolle.VERWALTUNG })
    @DeleteMapping("/{ID}")
    public ResponseEntity<Object> deleteGruppe(int gruppenNummer) throws Exception {
        Gruppe gruppe = gruppenService.getGruppe(gruppenNummer);
        if (gruppe == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        gruppenService.deleteGruppe(gruppenNummer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static class UpdateGruppeRequest {
        public String gruppenName;
        public String standort;
        public String gruppenLeiter;
    }

    @RequireRole({ Rolle.VERWALTUNG })
    @PostMapping("/{ID}")
    public ResponseEntity<Object> updateGruppe(@PathVariable("ID") int ID, @RequestBody UpdateGruppeRequest request) {
        Account gruppenleiter = null;
        if (request.gruppenLeiter != null) {
            gruppenleiter = accountService.getAccount(request.gruppenLeiter);
        }
        try {
            gruppenService.updateGruppe(ID, request.gruppenName, request.standort, gruppenleiter);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IDNotFoundException e) {
            return new ResponseEntity<>("Gruppe nicht gefunden", HttpStatus.NOT_FOUND);
        }
    }

}
