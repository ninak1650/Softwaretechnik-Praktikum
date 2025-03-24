package de.uni_leipzig.swtp.borna_lecker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.entities.Gruppe;

public interface GruppeRepository extends JpaRepository<Gruppe, Integer> {

    // Finde die Gruppe basierend auf dem Account-Benutzernamen
    Gruppe findByAccount(Account account);

    // Finde die Gruppe basierend auf dem GruppenNamen
    Gruppe findByGruppenName(String gruppenName);

    // Finde die Gruppe basierend auf der Vertretung
    Gruppe findByVertretung(Account vertretung);

    // Finde die Gruppe basierend auf dem standort
    List<Gruppe> findByStandort(String standort);

    Gruppe findByGruppenNummer(int gruppenNummer);

    @Query("SELECT DISTINCT g.standort FROM Gruppe g")
    List<String> getStandorte();

    @Query("SELECT EXISTS(SELECT 1 FROM Account a WHERE a.gruppe = ?1)")
    boolean hasGruppenleiter(Gruppe gruppe);
}
