package de.uni_leipzig.swtp.borna_lecker.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Kunde")
public class Kunde {

    @Id
    @Column(name = "kunde_nr")
    private int kundenNummer;

    @Column(name = "kunde_name")
    private String name;

    @Column(name = "kunde_standort")
    private String standort;

    @ManyToOne
    @JoinColumn(name = "gruppe_nr")
    @JsonBackReference
    private Gruppe gruppe;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "kunde")
    @JsonManagedReference
    private List<Bestellung> bestellung;

    // Die ID wird explizit hinzugefügt, sodass sie im Response auftaucht
    @JsonProperty("kunde_id") // Optional, um die ID explizit umzubenennen
    public int getId() {
        return kundenNummer;
    }

}
