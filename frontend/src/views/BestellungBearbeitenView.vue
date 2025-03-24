<template>
  <LeiterfunktionenComponent />

  <section class="hero is-medium-with-navbar">
    <div class="hero-body">
      <div class="container has-text-centered">
        <!-- Fehleranzeige -->
        <div v-if="errorMessage" class="notification is-danger">
          <strong>Fehler:</strong> {{ errorMessage }}
        </div>

        <!-- Bilder in einer Reihe -->
        <div class="columns is-centered is-vcentered">
          <div class="column is-one-third">
            <figure class="image">
              <img :src="farbImage" :alt="getFarbImageAlt" :class="{ faded: abholstatus }">
            </figure>
          </div>
          <div class="column is-one-third">
            <figure class="image">
              <img :src="salatImage" :alt="getSalatImageAlt" :class="{ faded: abholstatus }">
            </figure>
          </div>
        </div>

        <!-- Buttons darunter -->
        <div class="buttons is-centered">
          <b-button class="button is-danger is-rounded"
            style="margin-right: 20px; width: 350px; height: 120px; font-size: 4rem; display: flex; align-items: center; justify-content: center;"
            @click="goToScan">
            <b-icon icon="arrow-left" size="is-large"></b-icon>
          </b-button>

          <!-- Abholbutton, deaktiviert wenn keine Bestellung oder bereits abgeholt -->
          <b-button v-if="!abholstatus && !errorMessage" class="button is-success is-large is-rounded"
            style="margin-left: 20px; width: 350px; height: 120px; font-size: 4rem; display: flex; align-items: center; justify-content: center;"
            @click="BestellungAbholen">
            <b-icon icon="check-circle-outline" size="is-large"></b-icon>
          </b-button>

        </div>
      </div>
    </div>
  </section>
</template>



<script>
import { api } from "@/api/api";
import LeiterfunktionenComponent from "@/components/LeiterfunktionenComponent.vue";
import blauBild from "../assets/blue.png";
import grauBild from "../assets/grey.png";
import keinSalatbild from "../assets/KeinSalat.png";
import rotBild from "../assets/red.png";
import salatbild from "../assets/Salat.png";

export default {
  name: "BestellungBearbeitenView",
  components: {
    LeiterfunktionenComponent
  },
  props: {
    ID: String,  // Empfängt die ID als Prop
  },
  data() {
    return {
      labelPosition: 'on-border',
      detectedCodes: "",
      loadedSrc1: '',
      loadedSrc2: '',
      // Dynamische Bildquellen
      salatImage: '',  // Dynamisches Bild für Salat
      getSalatImageAlt: '', //Dynamischer Alt-Text
      farbImage: '',    // Dynamisches Bild für Farbe der Essensbestellung
      getFarbImageAlt: '', //Dynamischer Alt-Text
      bestellung: null,
      bestell_id: null,
      datum: null,
      essenswahl: null,
      abholstatus: null,
      errorMessage: '', // Fehlernachricht für UI
    };
  },
  created() {
    if (this.ID) {
      this.fetchBestellungen();
    }
  },
  methods: {
    async fetchBestellungen() {
      try {
        let id = this.ID.replace("bornalecker-", "").replace("kunde-", "").replace("account-", "");
        const response = await api.bestellungen.getBestellung(id);

        if (!response.ok) {
          // Spezielle Behandlung für Fehler 404 und 204
          if (response.status === 404) {
            this.errorMessage = "Keine tagesaktuelle Bestellung gefunden!";
            console.log(this.errorMessage);
          } else if (response.status === 204) {
            this.errorMessage = "Es wurde keine heutige Bestellung für die ID gefunden.";
            console.log(this.errorMessage);
          } else {
            throw new Error(`HTTP-Error: ${response.status}`);
          }
          return;
        }

        // Erfolgreiche Antwort
        this.bestellung = await response.json();
        this.bestell_id = this.bestellung.id;
        this.datum = this.bestellung.datum;
        this.essenswahl = this.bestellung.essenswahl;
        this.abholstatus = this.bestellung.bestellung_abholstatus;
        console.log("Erfolgreich eingelesen" + this.abholstatus);

        // Setzen der Farben und Salat
        if (this.essenswahl % 2) {
          this.salatImage = salatbild;
          this.getSalatImageAlt = "Es wurde Salat bestellt."
        } else {
          this.salatImage = keinSalatbild;
          this.getSalatImageAlt = "Es wurde kein Salat bestellt."
        }

        // Setze die Essensfarbe basierend auf der Essenswahl
        if (Math.floor(this.essenswahl / 2) === 1) {
          this.farbImage = rotBild;
          this.getFarbImageAlt = "Ein rotes Bild. Es wurde Essen 1 bestellt.";
        } else if (Math.floor(this.essenswahl / 2) === 2) {
          this.farbImage = blauBild;
          this.getFarbImageAlt = "Ein blaues Bild. Es wurde Essen 2 bestellt.";
        } else {
          this.farbImage = grauBild;
          this.getFarbImageAlt = "Ein graues Bild. Es wurde kein Essen bestellt.";
        }
        if (this.abholstatus) {
          this.errorMessage = "Die Bestellung wurde bereits abgeholt!"
        }
      } catch (err) {
        this.errorMessage = `Fehler beim Abrufen der Bestellungen: ${err.message}`;
        console.error(err);
      }
    },

    async BestellungAbholen() {
      try {
        const response = await api.bestellungen.setBestellStatus(this.bestell_id, true);

        if (response.status === 404) {
          this.errorMessage = "Die ID existiert nicht!";
          console.log(this.errorMessage);
        } else if (response.status === 200) {
          this.$router.push('/BestellungAufnehmen');
        } else {
          throw new Error(`HTTP-Error: ${response.status}`);
        }
      } catch (err) {
        this.errorMessage = `Fehler beim Ändern des Status: ${err.message}`;
        console.error(err);
      }
    },

    // Methode zum zurückkehren zum Scannen
    goToScan() {
      console.log("Zurück-Button wurde geklickt");
      this.$router.push('/BestellungAufnehmen');
    },
  }
};

</script>

<style>
.faded {
  opacity: 0.3;
  /* Stärker durchsichtig für abgeholtes Essen */
  transition: opacity 0.3s ease-in-out;
  /* Sanfter Übergang */
}
</style>