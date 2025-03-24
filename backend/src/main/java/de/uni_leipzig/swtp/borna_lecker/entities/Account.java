package de.uni_leipzig.swtp.borna_lecker.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Account")
public class Account {

    public enum Rolle {
        VERWALTUNG,
        KÜCHE,
        STANDORTLEITUNG,
        GRUPPENLEITUNG;
    }

    @Id
    @Column(name = "account_benutzername")
    private String benutzername;

    @Column(name = "account_nachname")
    private String nachname;

    @JsonIgnore
    @Column(name = "account_passwort")
    private String passwort;

    @JsonIgnore
    @Column(name = "account_require_password_change")
    private boolean requirePasswordChange;

    @Column(name = "account_rolle")
    private Rolle rolle;

    @Column(name = "account_standort")
    private String standort;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    @JsonManagedReference
    private List<Bestellung> bestellung;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vertretung")
    @JsonBackReference // Verhindert rekursive Serialisierung
    private List<Gruppe> vertretung; 

    @OneToOne()
    @JoinColumn(name = "gruppe_nr")
    @JsonManagedReference // Verhindert rekursive Serialisierung
    private Gruppe gruppe;

    // Die ID wird explizit hinzugefügt, sodass sie im Response auftaucht
    @JsonProperty("benutzername") // Optional, um die ID explizit umzubenennen
    public String getBenutzername() {
        return benutzername;
    }

    @JsonProperty("nachname")
    public String getNachname() {
        return nachname;
    }
}
