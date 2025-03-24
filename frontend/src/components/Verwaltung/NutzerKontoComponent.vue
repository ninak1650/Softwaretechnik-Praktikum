<template>
  <section class="section">
    <div class="container">

      <div class="control">
        <input v-model="searchQuery" type="text" class="input" placeholder="Nach Namen suchen..." />
      </div>
      <div style="display: flex; gap: 20px; align-items: flex-end; margin: 15px 0;">
        <div class="field" style="flex: 1; margin-bottom: 10px;">
            <label class="label">Rolle auswählen</label>
            <b-select v-model="selectedRole" placeholder="Rolle auswählen" @input="handleRoleChange">
                <option value="">Bitte wählen...</option>
                <option value="VERWALTUNG">Verwaltung</option>
                <option value="STANDORTLEITUNG">Standortleitung</option>
                <option value="KÜCHE">Küchenleitung</option>
                <option value="GRUPPENLEITUNG">Gruppenleitung</option>
            </b-select>
        </div>

        <div class="field" v-if="showLocationFilter" style="flex: 1; margin-bottom: 10px;">
            <label class="label">Standort auswählen</label>
            <b-select v-model="selectedLocation" placeholder="Standort auswählen" @change="fetchGruppen">
                <option value="">Bitte wählen...</option>
                <option v-for="standort in standorte">
                    {{ standort }}
                </option>
            </b-select>
        </div>

        <div class="field" v-if="showGroupFilter" style="flex: 1; margin-bottom: 10px;">
            <label class="label">Gruppe auswählen</label>
            <b-select v-model="selectedGroup" placeholder="Gruppe auswählen">
                <option value="">Bitte wählen...</option>
                <option v-for="gruppe in gruppen">
                    {{ gruppe.gruppenName }}
                </option>
            </b-select>
        </div>
      </div>

      <div class="control">
        <button class="button is-primary" @click="applyFilters">Filtern</button>
      </div>

      <table class="table is-fullwidth">
        <thead>
          <tr>
            <th>Name</th>
            <th>Rolle</th>
            <th>Standort</th>
            <th>Gruppe</th>
            <th>Aktionen</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="account in filteredAccounts">
            <td>{{ account.name }}</td>
            <td>{{ account.role }}</td>
            <td>{{ account.standort }}</td>
            <td>{{ account.gruppe.gruppenName || "" }}</td>
            <td class="actions">
              <button class="button is-small is-primary action" @click="navigateTo(`/konto/${account.id}`)">
                Bearbeiten
              </button>
              <button class="button is-small is-danger action" @click="navigateTo(`/konto/${account.id}/passwort`)">
                Passwort zurücksetzen
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="floating-button">
      <b-button class="button is-medium is-link mt-4" :style="{
        position: 'fixed',
        bottom: '20px',
        right: '40px',
        zIndex: '1000'
      }" icon-left="account-box-plus-outline" @click="navigateTo(`/konto?standort=${standortName}`)">
        <span class="has-text-weight-bold">Konto erstellen</span>
      </b-button>
    </div>

  </section>
</template>

<script>
import { api } from '@/api/api';



export default {
  name: 'NutzerKontoComponent',

  mounted() {
    this.fetchStandorte();
    this.fetchGruppen();
    this.fetchAccounts();

  },


  data() {
    return {
      standorte: [],
      accounts: [],
      gruppen: [],
      searchQuery: '',
      selectedRole: '',
      selectedLocation: '',
      selectedGroup: '',
      filteredAccounts: [],
      showLocationFilter: false,
      showGroupFilter: false,
    };

  },
  _methods: {
    navigateTo(route) {
      if (route) {
        this.$router.push(route);
      }
    },
    async fetchStandorte() {
      try {
        console.log("fetching");
        const response = await api.standorte.getStandorte();

        if (!response.ok) {
          throw new Error("Fehler beim Aufrufen der Standorte");
        }
        const data = await response.json();
        this.standorte = data;
      } catch (error) {
        console.error("Fehler beim Laden der Standorte", error);
      }
    },
    async fetchGruppen() {
      if (!this.selectedLocation) return;
      
      try {
        this.gruppen = [];
        const response = await api.gruppen.getGruppenForStandort(this.selectedLocation);
        if (!response.ok) {
          throw new Error(
            `Error fetching Gruppen: ${response.statusText}`
          );
        }
        this.gruppen = await response.json();
        await this.fetchAccounts();
        const relevantacc = this.filteredAccounts.filter(
          (acc) => acc.role === Rolle.GRUPPENLEITUNG || acc.role === Rolle.KÜCHE);
        for (const gruppe of this.gruppen) {
          gruppe.account = relevantacc.find((acc) => acc.grupe.gruppenNummer == gruppe.gruppenNummer);
        }
        console.log(this.gruppen);
      } catch (error) {
        console.log("Gruppen konnten nicht geladen werden", error)

      }


    },

    async fetchAccounts() {

      try {
        const response = await api.accounts.getAccounts();

        if (!response.ok) {
          throw new Error(`Error fetching accounts: ${response.statusText}`);
        }

        this.accounts = await response.json();

        this.accounts = this.accounts.map(account => ({
          id: account.benutzername,
          name: account.nachname,
          role: account.rolle,
          standort: account.standort,
          gruppe: account.gruppe || "",
        }));

        this.filteredAccounts = this.accounts;

      } catch (error) {
        console.error("accountdaten konnten nicht geladen werden");

      }
    },




    handleRoleChange() {

      if (this.selectedRole === 'verwaltung') {
        this.showLocationFilter = false;
        this.showGroupFilter = false;
      } else if (this.selectedRole === 'standortleitung') {
        this.showLocationFilter = true;
        this.showGroupFilter = false;
      } else {
        this.showLocationFilter = true;
        this.showGroupFilter = true;
      }

      this.selectedLocation = '';
      this.selectedGroup = '';
    },

    applyFilters() {


      this.filteredAccounts = this.accounts.filter((account) => {
        const searchLower = this.searchQuery.toLowerCase().trim();


        const fullName = account.name.toLowerCase();
        const matchesSearch = !this.searchQuery ||
          fullName.includes(searchLower) ||
          (account.firstName && account.firstName.toLowerCase().includes(searchLower)) ||
          (account.lastName && account.lastName.toLowerCase().includes(searchLower));

        const matchesRole = !this.selectedRole || account.role === this.selectedRole;
        const matchesLocation = !this.selectedLocation || account.standort === this.selectedLocation;
        const matchesGroup = !this.selectedGroup || account.gruppe.gruppenName === this.selectedGroup;

        return matchesSearch && matchesRole && matchesLocation && matchesGroup;
      });
    },

    editAccount(id) {
      console.log(`Bearbeiten des Kontos mit ID: ${id}`);
    },
    deleteAccount(id) {
      console.log(`Löschen des Kontos mit ID: ${id}`);
    },
  },
  get methods() {
    return this._methods;
  },
  set methods(value) {
    this._methods = value;
  },

};
</script>

<style scoped>
.action {
  margin: 0 5px;
}
</style>