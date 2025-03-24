package de.uni_leipzig.swtp.borna_lecker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.uni_leipzig.swtp.borna_lecker.entities.Gruppe;
import de.uni_leipzig.swtp.borna_lecker.entities.Kunde;
import de.uni_leipzig.swtp.borna_lecker.exceptions.IDNotFoundException;
import de.uni_leipzig.swtp.borna_lecker.repositories.KundeRepository;

@Service
public class KundenService {

    @Autowired
    private KundeRepository kundeRepository;

    public void createKunde(String name, String standort, Gruppe gruppe) {
        Kunde kunde = new Kunde();
        kunde.setName(name);
        kunde.setStandort(standort);
        kunde.setGruppe(gruppe);
        kunde.setKundenNummer(generateNewKundenNummer());
        kunde = kundeRepository.save(kunde);
    }

    private int generateNewKundenNummer() {
        Integer maxKundenNummer = kundeRepository.getMaxKundenNummer();
        if (maxKundenNummer == null) {
            return 1;
        }
        return maxKundenNummer + 1;
    }

    public Kunde updateKunde(int kundenNummer, String name, String standort,
            Gruppe gruppe) throws IDNotFoundException {
        Kunde kunde = kundeRepository.findById(kundenNummer)
                .orElseThrow(() -> new IDNotFoundException("Benutzername existiert nicht"));
        kunde.setName(name);
        kunde.setStandort(standort);
        kunde.setGruppe(gruppe);
        kundeRepository.save(kunde);
        return kunde;
    }

    public void deleteKunde(int kundenNummer) {
        kundeRepository.deleteById(kundenNummer);
    }

}
