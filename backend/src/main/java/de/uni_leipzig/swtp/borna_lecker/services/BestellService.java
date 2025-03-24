package de.uni_leipzig.swtp.borna_lecker.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.entities.Bestellung;
import de.uni_leipzig.swtp.borna_lecker.entities.Kunde;
import de.uni_leipzig.swtp.borna_lecker.exceptions.IDNotFoundException;
import de.uni_leipzig.swtp.borna_lecker.repositories.AccountRepository;
import de.uni_leipzig.swtp.borna_lecker.repositories.BestellungRepository;
import de.uni_leipzig.swtp.borna_lecker.repositories.KundeRepository;

@Service
public class BestellService {

    @Autowired
    private BestellungRepository bestellungRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KundeRepository kundeRepository;

    /*
     * Ermittelt alle Bestellungen für eine Person.
     * 
     * @param ID Benutzername oder Kundennummer der Person
     * 
     * @return Liste aller Bestellungen für die Person
     */
    public List<Bestellung> getBestellungenFürPerson(String ID) throws IDNotFoundException {
        Account account = accountRepository.findByBenutzername(ID);
        if (account != null) {
            return bestellungRepository.findByAccount(account);
        }
        Kunde kunde;
        try {
            kunde = kundeRepository.findByKundenNummer(Integer.parseInt(ID));
        } catch (NumberFormatException e) {
            return null;
        }

        if (kunde == null) {
            return null;
        }

        return bestellungRepository.findByKunde(kunde);

    }

    /*
     * Ermittelt die Bestellung für eine Person an einem bestimmten Datum.
     * 
     * @param ID Benutzername oder Kundennummer der Person
     * 
     * @param datum Datum der Bestellung
     * 
     * @return Bestellung für die Person an dem Datum
     */
    public Bestellung getBestellungFürPerson(String ID, LocalDate datum) {
        List<Bestellung> bestellungen = getBestellungenFürPerson(ID);
        if (bestellungen == null) {
            return null;
        }
        return bestellungen.stream()
                .filter(bestellung -> bestellung.getDatum().equals(datum))
                .findFirst()
                .orElse(null);

    }

    public void updateBestellStatus(int id, boolean status) throws IDNotFoundException {
        Bestellung bestellung = bestellungRepository.findById(id)
                .orElseThrow(() -> new IDNotFoundException("ID existiert nicht"));
        bestellung.setBestellung_abholstatus(status);
        bestellungRepository.save(bestellung);
    }

    public Bestellung getBestellung(int id) throws IDNotFoundException {
        return bestellungRepository.findById(id).orElseThrow(() -> new IDNotFoundException("ID existiert nicht"));
    }

    public void setBestellungFürKunde(String kundennummer, LocalDate datum, int essenswahl) {
        Bestellung bestellung = getBestellungFürPerson(kundennummer, datum);
        if (bestellung == null) {
            Optional<Kunde> existingKunde = kundeRepository.findById(Integer.parseInt(kundennummer));
            if (existingKunde.isPresent()) {
                Bestellung newBestellung = new Bestellung();
                newBestellung.setKunde(existingKunde.get());
                newBestellung.setDatum(datum);
                newBestellung.setEssenswahl(essenswahl);
                newBestellung.setAccount(null);
                newBestellung.setBestellung_abholstatus(false);
                bestellungRepository.save(newBestellung);
            }

        } else {
            bestellung.setEssenswahl(essenswahl);
            bestellungRepository.save(bestellung);
        }

    }

    public void setBestellungFürAccount(String benutzername, LocalDate datum, int essenswahl) {
        Bestellung bestellung = getBestellungFürPerson(benutzername, datum);
        if (bestellung == null) {
            Optional<Account> existingAccount = accountRepository.findById(benutzername);
            if (existingAccount.isPresent()) {
                Bestellung newBestellung = new Bestellung();
                newBestellung.setKunde(null);
                newBestellung.setDatum(datum);
                newBestellung.setEssenswahl(essenswahl);
                newBestellung.setAccount(existingAccount.get());
                newBestellung.setBestellung_abholstatus(false);
                bestellungRepository.save(newBestellung);
            }

        } else {
            bestellung.setEssenswahl(essenswahl);
            bestellungRepository.save(bestellung);
        }

    }

    public List<Bestellung> getBestellungen() {
        return bestellungRepository.findAll();
    }

    // TODO: Es fehlt noch eine Bestellübersicht nach Standort: Für einen bestimmten
    // Tag und Standort die Anzahl der Essen 1, Essen 2 und Salate (für
    // BestellungenExportComponent).
}
