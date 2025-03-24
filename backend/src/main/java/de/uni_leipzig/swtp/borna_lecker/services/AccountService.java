package de.uni_leipzig.swtp.borna_lecker.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import de.uni_leipzig.swtp.borna_lecker.entities.Gruppe;
import de.uni_leipzig.swtp.borna_lecker.exceptions.IDNotFoundException;
import de.uni_leipzig.swtp.borna_lecker.repositories.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder encoder;

    public AccountService() {
    }

    public Account getAccount(String benutzername) throws UsernameNotFoundException {
        return accountRepository.findById(benutzername)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzername nicht gefunden"));
    }

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public void createAccount(String benutzername, String passwort, String account_nachname, String standort,
            Gruppe gruppe, Rolle rolle) {
        Account account = new Account();
        account.setNachname(account_nachname);
        account.setStandort(standort);
        account.setGruppe(gruppe);
        account.setBenutzername(benutzername);
        account.setRolle(rolle);
        account.setPasswort(encoder.encode(passwort));
        account.setRequirePasswordChange(true);
        account = accountRepository.save(account);
    }

    /*
     * Ändert das Passwort des Benutzers durch den Benutzer (Initialanmeldung)
     * 
     * @param benutzername Der Benutzername des Benutzers
     * 
     * @param password Das neue Passwort
     */
    public void changePassword(String benutzername, String password) throws UsernameNotFoundException {
        Account account = accountRepository.findById(benutzername)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzername nicht gefunden"));
        account.setPasswort(encoder.encode(password));
        account.setRequirePasswordChange(false);
        accountRepository.save(account);
    }

    public void resetPassword(String benutzername, String password) throws UsernameNotFoundException {
        Account account = accountRepository.findById(benutzername)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzername nicht gefunden"));
        account.setPasswort(encoder.encode(password));
        account.setRequirePasswordChange(true);
        accountRepository.save(account);
    }

    public void updateAccount(String benutzername, String account_nachname, String standort,
            Gruppe gruppe, Rolle rolle) throws IDNotFoundException {
        Account account = accountRepository.findById(benutzername)
                .orElseThrow(() -> new IDNotFoundException("Benutzername existiert nicht"));
        account.setNachname(account_nachname);
        account.setStandort(standort);
        account.setGruppe(gruppe);
        account.setRolle(rolle);
        accountRepository.save(account);
    }

    public void deleteAccount(String benutzername) {
        accountRepository.deleteById(benutzername);
    }

    public void updatePasswort(String benutzername, String passwort) {
        Account account = accountRepository.findById(benutzername)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzername nicht gefunden"));
        account.setPasswort(encoder.encode(passwort));
        account.setRequirePasswordChange(false);
        accountRepository.save(account);
    }

}
