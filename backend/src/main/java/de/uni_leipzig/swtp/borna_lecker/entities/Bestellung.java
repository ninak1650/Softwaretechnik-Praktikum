package de.uni_leipzig.swtp.borna_lecker.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Bestellung")
public class Bestellung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bestellung_id")
    private int id;

    @Column(name = "bestellung_datum")
    private LocalDate datum;

    @Column(name = "bestellung_essenswahl")
    private int essenswahl;

    @Column(name = "abholstatus")
    private boolean bestellung_abholstatus;

    @ManyToOne
    @JoinColumn(name = "kunde_nr")
    @JsonBackReference
    private Kunde kunde;

    @ManyToOne
    @JoinColumn(name = "account_benutzername")
    @JsonBackReference
    private Account account;

    // Explizit nur die ID von Kunde in der JSON-Antwort darstellen
    @JsonProperty("kunde_id")
    public int getKundeId() {
        return kunde != null ? kunde.getId() : 0; // Gibt die ID des Kunden zurück
    }

    // Explizit nur die ID von Account in der JSON-Antwort darstellen
    @JsonProperty("account_benutzername")
    public String getAccountBenutzername() {
        return account != null ? account.getBenutzername() : null; // Gibt den Benutzernamen des Accounts zurück
    }
}
