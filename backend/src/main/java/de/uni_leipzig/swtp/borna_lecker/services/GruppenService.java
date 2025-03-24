package de.uni_leipzig.swtp.borna_lecker.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.entities.Bestellung;
import de.uni_leipzig.swtp.borna_lecker.entities.Gruppe;
import de.uni_leipzig.swtp.borna_lecker.entities.Kunde;
import de.uni_leipzig.swtp.borna_lecker.exceptions.IDNotFoundException;
import de.uni_leipzig.swtp.borna_lecker.repositories.AccountRepository;
import de.uni_leipzig.swtp.borna_lecker.repositories.GruppeRepository;

@Service
public class GruppenService {

    @Autowired
    private GruppeRepository gruppeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    BestellService bestellService;

    // TODO: Vertretung festlegen

    // Übersicht aller Mitglieder (Als Liste)
    public List<Kunde> getMitglieder(int id) throws IDNotFoundException {
        // Gruppe mit der gegebenen ID finden
        Gruppe gruppe = gruppeRepository.findById(id)
                .orElseThrow(() -> new IDNotFoundException("Gruppe nicht gefunden"));
        return gruppe.getKunden();
    }

    public Gruppe getGruppe(int id) throws IDNotFoundException {
        return gruppeRepository.findById(id)
                .orElseThrow(() -> new IDNotFoundException("Gruppe nicht gefunden"));
    }

    public List<Gruppe> getGruppen() {
        return gruppeRepository.findAll();
    }

    // Bekomme Tagesaktuelle Bestellungen der Gruppenmitglieder
    public List<Bestellung> getGruppenbestellungenFuerGruppe(int id_gruppe, LocalDate date) {
        List<Kunde> gruppenmitglieder = getMitglieder(id_gruppe);
        List<Bestellung> TagesaktuelleBestellungen = new ArrayList<>();

        for (Kunde mitglied : gruppenmitglieder) {
            Bestellung tagesaktuelleBestellung = bestellService
                    .getBestellungFürPerson(String.valueOf(mitglied.getKundenNummer()), date);

            if (tagesaktuelleBestellung != null) {
                TagesaktuelleBestellungen.add(tagesaktuelleBestellung);
            }
        }

        return TagesaktuelleBestellungen;
    }

    public Gruppe getGruppeForGruppenleitung(String gruppenleiter) {
        return gruppeRepository.findByAccount(accountRepository.findByBenutzername(gruppenleiter));
    }

    public void removeGruppenleitung(int gruppenNummer) {
        Gruppe gruppe = getGruppe(gruppenNummer);
        gruppe.setAccount(null);
        gruppeRepository.save(gruppe);
    }

    public void setGruppenleitung(int gruppenNummer, String benutzername) {
        Gruppe gruppe = getGruppe(gruppenNummer);
        Optional<Account> account = accountRepository.findById(benutzername);
        if (account.isPresent()) {
            gruppe.setAccount(account.get());
        } else {
            throw new UsernameNotFoundException("Benutzername nicht gefunden: " + benutzername);
        }
        gruppeRepository.save(gruppe);
    }

    // Bekomme zu einer Gruppenleitung alle Gruppen
    public List<Gruppe> getGruppenForGruppenleitung(String gruppenleiter) {
        List<Gruppe> gruppen = new ArrayList<>();
        Gruppe hinzufuegen;
        // Füge die Gruppen hinzu von denen er Gruppenleiter ist
        gruppen.add(gruppeRepository.findByAccount(accountRepository.findByBenutzername(gruppenleiter)));

        // Füge die Gruppen hinzu die vertreten werden
        hinzufuegen = gruppeRepository.findByVertretung(accountRepository.findByBenutzername(gruppenleiter));
        if (hinzufuegen != null) {
            gruppen.add(hinzufuegen);
        }
        return gruppen;
    }

    public List<Gruppe> getGruppenForStandort(String standort) {
        List<Gruppe> gruppen = gruppeRepository.findByStandort(standort);
        return gruppen;
    }

    public void setGruppenVertretung(int gruppenNummer, String benutzername) {
        Gruppe gruppe = getGruppe(gruppenNummer);
        if (benutzername == null) {
            gruppe.setVertretung(null);
        } else {
            Optional<Account> vertretung = accountRepository.findById(benutzername);
            if (vertretung.isPresent()) {
                gruppe.setVertretung(vertretung.get());
            } else {
                throw new UsernameNotFoundException("Benutzername nicht gefunden: " + benutzername);
            }
        }
        gruppeRepository.save(gruppe);
    }

    public List<String> getStandorte() {
        return gruppeRepository.getStandorte();
    }

    public void createGruppe(int gruppenNummer, String gruppenName, String standort, Account gruppenleiter) {
        Gruppe gruppe = new Gruppe();
        gruppe.setGruppenNummer(gruppenNummer);
        gruppe.setGruppenName(gruppenName);
        gruppe.setStandort(standort);
        gruppe.setAccount(gruppenleiter);
        gruppe = gruppeRepository.save(gruppe);
    }

    public void deleteGruppe(int gruppenNummer) {
        gruppeRepository.deleteById(gruppenNummer);
    }

    public void updateGruppe(int gruppenNummer, String gruppenName, String standort, Account gruppenleiter) {
        Gruppe gruppe = getGruppe(gruppenNummer);
        gruppe.setGruppenName(gruppenName);
        gruppe.setStandort(standort);
        gruppe.setAccount(gruppenleiter);
        gruppeRepository.save(gruppe);
    }

    public boolean hasGruppenleiter(Gruppe gruppe) {
        return gruppeRepository.hasGruppenleiter(gruppe);
    }
}
