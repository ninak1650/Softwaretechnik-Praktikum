<template>
  <b-navbar type="is-light" shadow transparent fixed-top>
    <template #brand>
      <b-navbar-item>
        <!-- Material Design Icon anstelle des Buefy-Logos -->
        <b-icon icon="food" size="is-large" alt=""></b-icon>
      </b-navbar-item>
      <b-navbar-item v-if="showPasswordField">
        <b-field>
          <!-- v-model bindet den Wert des Inputs an die Variable password -->
          <b-input type="password" v-model="password" placeholder="Passwort eingeben..." size="is-medium"
            password-reveal icon="lock" @keyup.enter="checkPassword"></b-input>
          <b-button class="button is-primary is-light is-outlined" size="is-medium" style="margin-left: 5px;"
            @click="checkPassword">
            <b-icon icon="check-bold" size="is-small"></b-icon>
          </b-button>
        </b-field>
      </b-navbar-item>
    </template>

    <template #start>
      <b-navbar-item v-if="showPasswordField">
        <div v-if="passwordCorrect && showPasswordField" class="buttons is-centered">
          <b-field>
            <!-- v-model bindet den Wert des Inputs an die Variable manualInputText -->
            <b-input v-model="manualInputText" size="is-medium" placeholder="Manuelle Eingabe" icon="magnify"
              @keyup.enter="manualInput"></b-input>
            <!-- Button für Manuelle Eingabe -->
            <b-button class="button is-primary is-light is-outlined" size="is-medium" @click="manualInput">
              <!-- Material Design Icon für manuelle Eingabe -->
              <b-icon icon="check-bold" size="is-small"></b-icon>
            </b-button>
            <!-- Button für Zurück -->
            <b-button class="button is-primary is-light is-rounded is-outlined" size="is-medium" @click="goBack">
              <b-icon icon="exit-to-app" size="is-small"></b-icon>
              &nbsp;Ausgabe beenden
            </b-button>
          </b-field>
        </div>
      </b-navbar-item>
    </template>

    <template #end>
      <b-navbar-item>
        <b-button class="button1 is-primary is-light is-rounded is-outlined custom-button" size="is-medium"
          @click="togglePasswordField">
          Leiterfunktionen
        </b-button>
      </b-navbar-item>
    </template>
  </b-navbar>
</template>

<script>
import { api } from "@/api/api";
import { getUsernameFromToken } from "@/util/jwt";

export default {
  name: "LoginComponentBuefy",
  data() {
    return {
      password: '',  // Variable für das Passwort
      showPasswordField: false,  // Variable für die Sichtbarkeit des Passwortfeldes
      passwordCorrect: false,  // Variable, um die Korrektheit des Passworts zu überprüfen
      manualInputText: '', // Neue Variable für das manuelle Eingabefeld
    };
  },
  methods: {
    // Methode zum Toggle des Passwortfeldes
    togglePasswordField() {
      // Wenn Passwort korrekt ist und Leiterfunktionen erneut gedrückt werden, alles zurücksetzen
      if (this.passwordCorrect) {
        this.passwordCorrect = false;
        this.password = ''; // Passwort zurücksetzen
      } else {
        // Nur das Passwortfeld anzeigen, wenn das Passwortfeld noch nicht sichtbar ist
        this.showPasswordField = !this.showPasswordField;
      }
    },

    async checkPassword() {
      // Hole den Benutzernamen aus dem Token
      const username = getUsernameFromToken();

      try {
        // Sende eine Anfrage an deine API
        const response = await api.auth.validate(username, this.password);

        // Überprüfe die Antwort der API
        if (response.ok) {
          // Erfolgreiche Validierung
          this.passwordCorrect = true;
        } else if (response.status === 400) {
          // Falsches Passwort
          this.passwordCorrect = false;
          this.$buefy.toast.open({
            message: "Das Passwort ist falsch!",
            type: "is-danger",
            position: "is-top",
            duration: 3000,
          });
        } else {
          // Andere Fehler
          throw new Error('Ein Fehler ist aufgetreten');
        }
      } catch (error) {
        console.error('Fehler bei der Passwortprüfung:', error);
        alert('Es konnte keine Verbindung zur API hergestellt werden.');
      }
    },

    // Methode für den "Manuelle Eingabe" Button
    manualInput() {
      // Gib den Wert des manuellen Eingabefeldes in der Konsole aus
      console.log("Manuelle Eingabe: ", this.manualInputText);
      this.$router.push({ name: 'BestellungAufnehmen' }).then(() => {
        this.$router.push({ name: 'BestellungBearbeiten', params: { ID: this.manualInputText } });
      });
    },

    // Methode für den "Zurück" Button
    goBack() {
      console.log("Zurück-Button wurde geklickt");
      this.$router.push('/gruppenleitungKueche');
    },
  },
}
</script>

<style scoped>
/* Optional: Stil für die Buttons oder andere Anpassungen */
</style>
