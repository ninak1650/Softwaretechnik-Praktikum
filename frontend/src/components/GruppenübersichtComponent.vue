<template>
  <section>
    <!-- Schalter zur Filterung der Gruppen mit Vertretern -->
    <b-field>
      <b-switch v-model="showOnlyWithDeputies">
        <template #default>
          Nur Gruppen mit Vertretern anzeigen
        </template>
      </b-switch>
    </b-field>

    <!-- Tabelle zur Anzeige der Gruppen -->
    <b-table class="custom-table" :data="filteredGruppen" bordered striped :paginated="true" :per-page="10"
      :current-page.sync="currentPage">
      <!-- Spalte für Gruppennummer -->
      <b-table-column field="gruppennummer" label="Gruppennummer" sortable>
        <template #default="props">
          {{ props.row.gruppennummer }}
        </template>
      </b-table-column>

      <!-- Spalte für Gruppenname -->
      <b-table-column field="gruppenname" label="Gruppenname" sortable>
        <template #default="props">
          {{ props.row.gruppenname }}
        </template>
      </b-table-column>

      <!-- Spalte für Gruppenleiter -->
      <b-table-column field="gruppenleiter" label="Gruppenleiter" sortable>
        <template #default="props">
          {{ props.row.gruppenleiter }}
        </template>
      </b-table-column>

      <!-- Spalte für Vertreter (falls vorhanden) -->
      <b-table-column field="vertreter" label="Vertreter" sortable>
        <template #default="props">
          <span v-if="props.row.vertreter" class="tag is-success" style="font-size: 1rem; padding: 0.25em 0.75em;">
            {{ props.row.vertreter }}
          </span>
        </template>
      </b-table-column>

      <!-- Anzeige für den Fall, dass keine Daten vorhanden sind -->
      <template #empty>
        <section class="section">
          <div class="content has-text-centered">
            Keine Daten verfügbar.
          </div>
        </section>
      </template>
    </b-table>
  </section>
</template>

<script>
import { api } from "@/api/api";
import { getStandortFromToken } from "@/util/jwt";
import { Rolle, stringToRolle } from "@/util/rolle";

export default {
  props: {
    tagClass: {
      type: String,
      default: "tag is-success",
    },
  },
  data() {
    return {
      gruppen: [], // Speichert die Gruppendaten
      isLoading: false, // Ladezustand für API-Anfragen
      showOnlyWithDeputies: false, // Option zur Filterung von Gruppen mit Vertretern
      currentPage: 1, // Aktuelle Seite der Tabelle
    };
  },
  computed: {
    // Filtert die Gruppen basierend auf der Option "Nur Gruppen mit Vertretern anzeigen"
    filteredGruppen() {
      if (this.showOnlyWithDeputies) {
        return this.gruppen.filter((gruppe) => gruppe.vertreter !== null);
      }
      return this.gruppen;
    },

    // Sortiert die Gruppen nach Gruppennummer
    sortedGruppenData() {
      return [...this.gruppen].sort((a, b) => a.gruppennummer - b.gruppennummer);
    },
  },
  methods: {
    // Ruft die Gruppendaten basierend auf dem Standort des eingeloggten Nutzers ab
    async fetchGruppenData() {
      console.log("`fetchGruppenData()` wurde aufgerufen.");
      this.isLoading = true;

      const standort = getStandortFromToken();
      console.log("Extrahierter Standort:", standort);

      try {
        const response = await api.gruppen.getGruppenForStandort(standort);

        // Überprüfung des Serverstatus
        console.log("Antwortstatus:", response.status);
        if (!response.ok) {
          const errorText = await response.text();
          console.error("Fehlerhafte Antwort:", errorText);
          throw new Error(`Fehler beim Abrufen der Gruppen: ${response.status}`);
        }



        // Verarbeiten der JSON-Daten
        let data = await response.json();
        console.log("Antwort des Servers:", data);
        const accs = await api.accounts.getAccounts();
        let accounts = await accs.json();
        for (let group of data) {
          group.account = accounts.filter((acc) => stringToRolle(acc.rolle) == Rolle.KÜCHE || stringToRolle(acc.rolle) == Rolle.GRUPPENLEITUNG).find((acc) => acc.gruppe.gruppenNummer === group.gruppenNummer);
        }
        // Konvertierung der Daten für die Anzeige in der Tabelle
        this.gruppen = data.map((gruppe) => ({
          gruppennummer: gruppe.gruppenNummer,
          gruppenname: gruppe.gruppenName,
          gruppenleiter: gruppe.account?.benutzername || "Kein Leiter",
          vertreter: gruppe.vertretung?.benutzername || null, // Falls kein Vertreter vorhanden ist, bleibt der Wert null
        }));
        console.log("Verarbeitete Gruppendaten für die Tabelle:", this.gruppen);

        // Event auslösen, um die Daten an das Eltern-Element zu senden
        this.$emit("updateGruppenData", this.gruppen);
        console.log("Event `updateGruppenData` wurde ausgelöst:", this.gruppen);
      } catch (error) {
        console.error("Fehler beim Abrufen der Gruppen:", error);
      } finally {
        this.isLoading = false;
      }
    },
  },
  mounted() {
    this.fetchGruppenData();// Beim Laden der Komponente werden die Gruppendaten abgerufen
  },
};
</script>
