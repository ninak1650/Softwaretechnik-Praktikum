package de.uni_leipzig.swtp.borna_lecker.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.uni_leipzig.swtp.borna_lecker.entities.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    //Methode, um basierend vom Benutzernamen einen Account zu finden
    Account findByBenutzername(String benutzername);

}
