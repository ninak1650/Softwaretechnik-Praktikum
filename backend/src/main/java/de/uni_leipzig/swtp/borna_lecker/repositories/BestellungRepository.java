package de.uni_leipzig.swtp.borna_lecker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.entities.Bestellung;
import de.uni_leipzig.swtp.borna_lecker.entities.Kunde;

@Repository
public interface BestellungRepository extends JpaRepository<Bestellung, Integer> {

    // Gibt die Bestellungen eines Accounts zurück
    List<Bestellung> findByAccount(Account account);

    // Gibt die Bestellungen für einen Kunden zurück
    List<Bestellung> findByKunde(Kunde kunde);

}
