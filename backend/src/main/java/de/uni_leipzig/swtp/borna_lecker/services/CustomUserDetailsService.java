package de.uni_leipzig.swtp.borna_lecker.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.uni_leipzig.swtp.borna_lecker.entities.Account;
import de.uni_leipzig.swtp.borna_lecker.repositories.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzername nicht gefunden"));

        return new User(account.getBenutzername(), account.getPasswort(),
                new ArrayList<>());
    }

    public Account getAccount(String benutzername) throws UsernameNotFoundException {
        return accountRepository.findById(benutzername)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzername nicht gefunden"));
    }

}