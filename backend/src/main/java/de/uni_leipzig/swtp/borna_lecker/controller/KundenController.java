package de.uni_leipzig.swtp.borna_lecker.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import de.uni_leipzig.swtp.borna_lecker.entities.Bestellung;
import de.uni_leipzig.swtp.borna_lecker.entities.Gruppe;
import de.uni_leipzig.swtp.borna_lecker.entities.Kunde;
import de.uni_leipzig.swtp.borna_lecker.repositories.KundeRepository;
import de.uni_leipzig.swtp.borna_lecker.services.BestellService;
import de.uni_leipzig.swtp.borna_lecker.services.GruppenService;
import de.uni_leipzig.swtp.borna_lecker.services.KundenService;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/kunden")
public class KundenController {

    @Autowired
    BestellService bestellService;

    @Autowired
    KundenService kundenService;

    @Autowired
    GruppenService gruppenService;

    @Autowired
    KundeRepository kundeRepository;

    /**
     * Bekommt eine ID übergeben und gibt dazu alle Bestellungen zurück
     * 
     * @param ID       ist entweder kunde.kundeNr oder Account.benutzername
     * @param response http Meldungen für Erfolg/Misserfolg etc
     * @return gibt eine Liste aller Bestellungen zurück und httpStatus.OK
     */
    @RequireRole({ Rolle.KÜCHE, Rolle.GRUPPENLEITUNG })
    @GetMapping("/{ID}/bestellungen")
    public ResponseEntity<List<Bestellung>> getBestellungen(
            @PathVariable("ID") String ID,
            HttpServletResponse response) {

        try {
            List<Bestellung> bestellungen = bestellService.getBestellungenFürPerson(ID);
            if (bestellungen == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(bestellungen, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Bekommt für eine übergebene ID die heutige Bestellung
     * 
     * @param id       ist entweder kunde.kundeNr oder Account.benutzername
     * @param response http Meldungen für Erfolg/Misserfolg etc
     * @return Gibt wenn es eine Bestellung für den heutigen Tag geben sollte die
     *         Bestellung zurück
     */
    @RequireRole({ Rolle.KÜCHE, Rolle.GRUPPENLEITUNG })
    @GetMapping("/{ID}/bestellung")
    public ResponseEntity<Bestellung> getBestellung(
            @PathVariable("ID") String ID,
            @RequestParam(value = "datum", required = false) String datum,
            HttpServletResponse response) {

        LocalDate date = LocalDate.now();
        if (datum != null) {
            try {
                date = LocalDate.parse(datum);
            } catch (DateTimeParseException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            date = LocalDate.now();
        }

        Bestellung bestellung = bestellService.getBestellungFürPerson(ID, date);
        if (bestellung == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bestellung, HttpStatus.OK);
    }

    @GetMapping("/Testbestellung")
    public ResponseEntity<Bestellung> getTestBestellung() {
        Kunde testKunde = new Kunde();
        testKunde.setKundenNummer(10000);
        testKunde.setName("TemporärerKunde");
        testKunde.setStandort("Testbereich");
        testKunde.setGruppe(null);

        Bestellung testBestellung = new Bestellung();
        testBestellung.setDatum(LocalDate.now());
        testBestellung.setEssenswahl(1);
        testBestellung.setKunde(testKunde);
        testBestellung.setAccount(null);
        testBestellung.setBestellung_abholstatus(false);
        return new ResponseEntity<>(testBestellung, HttpStatus.OK);
    }

    @RequireRole({ Rolle.VERWALTUNG })
    @GetMapping("/{ID}")
    public ResponseEntity<Kunde> getKunde(
            @PathVariable("ID") String ID,
            HttpServletResponse response) {

        Kunde kunde = kundeRepository.findByKundenNummer(Integer.parseInt(ID));
        if (kunde == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(kunde, HttpStatus.OK);
    }

    static private class CreateKundeRequest {
        public String name;
        public String standort;
        public int gruppenNummer;
    }

    @RequireRole({ Rolle.VERWALTUNG })
    @PostMapping("")
    public ResponseEntity<Object> createKunde(@RequestBody CreateKundeRequest kundeRequest) throws Exception {
        Gruppe gruppe = null;
        if (kundeRequest.gruppenNummer != 0) {
            gruppe = gruppenService.getGruppe(kundeRequest.gruppenNummer);
            if (gruppe == null) {
                return new ResponseEntity<>("Gruppe nicht gefunden", HttpStatus.NOT_FOUND);
            }
        }
        kundenService.createKunde(kundeRequest.name, kundeRequest.standort,
                gruppe);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    static private class UpdateKundeRequest {
        public String name;
        public String standort;
        public int gruppenNummer;
    }

    @RequireRole({ Rolle.VERWALTUNG })
    @PostMapping("/{ID}")
    public ResponseEntity<Object> updateKunde(@RequestBody UpdateKundeRequest kundeRequest, @PathVariable("ID") int ID)
            throws Exception {
        Kunde kunde = kundeRepository.findByKundenNummer(ID);
        if (kunde == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Gruppe gruppe = null;
        if (kundeRequest.gruppenNummer != 0) {
            gruppe = gruppenService.getGruppe(kundeRequest.gruppenNummer);
            if (gruppe == null) {
                return new ResponseEntity<>("Gruppe nicht gefunden", HttpStatus.NOT_FOUND);
            }
        }
        kunde = kundenService.updateKunde(ID, kundeRequest.name, kundeRequest.standort,
                gruppe);
        return new ResponseEntity<>(kunde, HttpStatus.OK);
    }

    @RequireRole({ Rolle.VERWALTUNG })
    @DeleteMapping("/{ID}")
    public ResponseEntity<Object> deleteKunde(@PathVariable("ID") int kundenNummer) throws Exception {
        Kunde kunde = kundeRepository.findByKundenNummer(kundenNummer);
        if (kunde == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        kundenService.deleteKunde(kundenNummer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
