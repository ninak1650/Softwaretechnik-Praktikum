<template>
  <div class="login-container">
    <!-- Logo oben -->
    <div class="login-title">
      Login
    </div>

    <!-- Anmeldeformular -->
    <div class="login-box">
      <b-field label="Benutzername" label-position="on-border">
        <b-input v-model="username" placeholder="Geben Sie den Benutzernamen ein" aria-label="Benutzername"
          @keyup.enter="handleLogin"></b-input>
      </b-field>

      <b-field label="Passwort" label-position="on-border">
        <b-input type="password" v-model="password" placeholder="Geben Sie Ihr Passwort ein" aria-label="Passwort"
          @keyup.enter="handleLogin"></b-input>
      </b-field>

      <!-- Anmeldebutton -->
      <b-field>
        <b-button type="is-primary" label="Anmelden" fullwidth @click="handleLogin"></b-button>
      </b-field>
    </div>
  </div>
</template>


<script>
import { api } from "@/api/api";
import { getRequirePasswordChangeFromToken, getRoleFromToken } from "@/util/jwt";
import { Rolle } from "@/util/rolle";

export default {
  name: "LoginComponentBuefy",
  data() {
    return {
      username: '',  // Variable für den Benutzernamen
      password: '',  // Variable für das Passwort
      labelPosition: 'on-border',
    };
  },
  mounted() {
    // Beim Laden der Komponente den Token aus dem localStorage entfernen
    localStorage.removeItem("token");
  },
  methods: {
    goToView() {
      const role = getRoleFromToken();

      switch (role) {
        case Rolle.GRUPPENLEITUNG:
          this.$router.push("/gruppenleitungNormal");
          break;
        case Rolle.KÜCHE:
          this.$router.push("/gruppenleitungKueche");
          break;
        case Rolle.STANDORTLEITUNG:
          this.$router.push("/standortleitung");
          break;
        case Rolle.VERWALTUNG:
          this.$router.push("/verwaltung");
          break;
      }
    },
    async handleLogin() {
      if (!this.username || !this.password) {
        this.$buefy.toast.open({
          message: "Bitte füllen Sie alle Felder aus!",
          type: "is-warning",
          position: "is-top",
          duration: 3000,
        });
        return;
      }

      try {
        const response = await api.auth.login(this.username, this.password);
        if (!response.ok) {
          throw new Error(await response.text());
        }

        localStorage.setItem("token", await response.text());


        this.$buefy.toast.open({
          message: "Erfolgreich eingeloggt!",
          type: "is-success",
          position: "is-top",
          duration: 3000,
        });

        const requireChange = getRequirePasswordChangeFromToken();
        if (requireChange) {
          this.$router.push(`/konto/${this.username}/passwort`);
        } else {
          this.goToView();
        }

      } catch (err) {
        this.errorMessage = `Fehler beim Login: ${err.message}`;
        this.$buefy.toast.open({
          message: "Benutzername oder Passwort ist falsch!",
          type: "is-danger",
          position: "is-top",
          duration: 3000,
        });
        console.error(err);
      }
    },
  },
};
</script>

<style scoped>
/* Login-Box: Zentrierung und Styling */
.login-container {
  width: 100%;
  max-width: 450px;
  padding: 40px;
  background: white;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  text-align: center;
}

/* App-Titel */
.appTitle {
  font-size: 48px;
  font-weight: bold;
  color: #3273dc;
  margin-bottom: 5px;
}

.login-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 32px;
}

/* Barrierefreie große Schriften für bessere Lesbarkeit */
b-field label {
  font-size: 1.2em;
  font-weight: bold;
}

/* Größere Eingabefelder */
b-input {
  font-size: 1.1em;
}

/* Anmeldebutton */
b-button {
  font-size: 1.2em;
  padding: 10px;
}
</style>