package de.uni_leipzig.swtp.borna_lecker.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Gruppe")
public class Gruppe {

    @Id
    @Column(name = "gruppe_nr")
    private int gruppenNummer;

    @Column(name = "gruppe_gruppenname")
    private String gruppenName;

    @Column(name = "gruppe_standort")
    private String standort;

    @ManyToOne()
    @JoinColumn(name = "vertretung_benutzername", referencedColumnName = "account_benutzername")
    @JsonManagedReference // Verhindert rekursive Serialisierung
    private Account vertretung;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gruppe")
    @JsonBackReference
    private List<Kunde> kunden;

    @OneToOne(mappedBy = "gruppe")
    @JsonBackReference // Verhindert rekursive Serialisierung
    private Account account;
}
