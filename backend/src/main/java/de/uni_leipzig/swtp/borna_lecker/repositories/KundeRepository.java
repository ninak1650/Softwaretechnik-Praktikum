package de.uni_leipzig.swtp.borna_lecker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.uni_leipzig.swtp.borna_lecker.entities.Kunde;

@Repository
public interface KundeRepository extends JpaRepository<Kunde, Integer> {

    // Findet für kundenNummer den dazugehörigen Kunde
    public Kunde findByKundenNummer(int kundenNummer);

    // Finde den Kunden basierend auf dem Namen
    public Kunde findByName(String name);

    public boolean existsByKundenNummer(int kundenNummer);

    @Query("SELECT MAX(k.kundenNummer) FROM Kunde k")
    public Integer getMaxKundenNummer();
}
