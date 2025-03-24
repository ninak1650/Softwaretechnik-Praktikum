<template>
  <section>
    <!-- Hinweis-Box für die Benutzererklärung -->
    <b-message title="Hinweis">
      <p>
        Die ursprüngliche Gruppenleitung kann sich nicht selbst vertreten
        und erscheint daher nicht in der Auswahl.
      </p>
    </b-message>

    <!-- Tabelle zur Auswahl der Gruppe und Verwaltung der Vertretung -->
    <b-table class="custom-table" :data="[selectedRow]" bordered striped>

      <!-- Auswahl der Gruppe über Dropdown -->
      <b-table-column field="gruppenname" label="Gruppenname">
        <template #default>
          <b-dropdown aria-role="list">
            <template #trigger="{ active }">
              <b-button class="dropdown-button" :icon-right="active ? 'menu-up' : 'menu-down'">
                {{ selectedGroup?.gruppenname || "Wählen Sie eine Gruppe" }}
              </b-button>
            </template>

            <b-dropdown-item v-for="group in gruppenData" :key="group.gruppennummer"
              @click="handleGruppenNameSelect(group)">
              {{ group.gruppennummer }} - {{ group.gruppenname || 'Unbekannt' }}
            </b-dropdown-item>
          </b-dropdown>
        </template>
      </b-table-column>

      <!-- Auswahl des Gruppenleiters über Dropdown -->
      <b-table-column field="gruppenleiter" label="Vertretung auswählen">
        <template #default>
          <b-dropdown aria-role="list">
            <template #trigger="{ active }">
              <b-button class="dropdown-button" :icon-right="active ? 'menu-up' : 'menu-down'">
                {{ selectedLeader || "Wählen Sie einen Leiter" }}
              </b-button>
            </template>

            <b-dropdown-item v-for="leiter in availableLeaders" :key="leiter" @click="handleGruppenLeiterSelect(leiter)"
              :disabled="leiter === selectedGroup?.gruppenleiter" :style="getLeaderStyle(leiter)">
              {{ leiter }}
            </b-dropdown-item>
          </b-dropdown>
        </template>
      </b-table-column>

      <!-- Verwaltung: Vertretung zuweisen oder entfernen -->
      <b-table-column field="verwaltung" label="Verwaltung">
        <template #default>
          <b-button type="is-warning is-light" class="vertretung-button" @click="assignDeputy"
            :disabled="!selectedGroup || !selectedLeader">
            Vertreten
          </b-button>

          <b-button type="is-success is-light" class="entfernen-button" @click="removeDeputy"
            :disabled="!selectedGroup || !selectedGroup.vertreter">
            Entfernen
          </b-button>
        </template>
      </b-table-column>

    </b-table>
  </section>
</template>

<script>
import { api } from "@/api/api";

export default {
  props: {
    gruppenData: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      selectedGroup: null, // Speichert die aktuell ausgewählte Gruppe
      selectedLeader: null // Speichert den aktuell ausgewählten Gruppenleiter
    };
  },
  computed: {
    // Erstellt eine Zeile mit der aktuell ausgewählten Gruppe und ihrem Leiter
    selectedRow() {
      return {
        gruppenname: this.selectedGroup ? this.selectedGroup.gruppenname : "",
        gruppenleiter: this.selectedLeader || ""
      };
    },

    // Filtert die verfügbaren Gruppenleiter, sodass die ursprüngliche Gruppenleitung nicht als Vertretung wählbar ist
    availableLeaders() {
      if (!this.selectedGroup) return [];

      return this.gruppenData
        .map(group => group.gruppenleiter)
        .filter(leiter => leiter !== this.selectedGroup.gruppenleiter); // Entfernt die Gruppenleitung aus der Auswahl
    }
  },
  methods: {
    // Auswahl einer Gruppe verarbeiten
    handleGruppenNameSelect(group) {
      if (!group || !group.gruppennummer) {
        console.error("Fehler: Ungültige Gruppen-Daten", group);
        return;
      }

      this.selectedGroup = { ...group };
      this.selectedLeader = group.vertreter || "";
    },

    // Auswahl eines Gruppenleiters verarbeiten
    handleGruppenLeiterSelect(leiter) {
      this.selectedLeader = leiter;
    },

    // Bestimmt das Styling für Gruppenleiter im Dropdown
    getLeaderStyle(leiter) {
      if (!this.selectedGroup) return {};
      if (leiter === this.selectedGroup.vertreter) return { backgroundColor: "#FFF3CD" }; // Gelb: aktueller Vertreter
      return {};
    },

    // Setzt einen neuen Vertreter für die ausgewählte Gruppe
    async assignDeputy() {
      if (!this.selectedGroup || !this.selectedLeader) {
        alert("Bitte wählen Sie eine Gruppe und einen neuen Leiter aus.");
        return;
      }

      const bodyData = {
        gruppenNummer: Number(this.selectedGroup.gruppennummer),
        benutzername: this.selectedLeader
      };

      try {
        const response = await api.gruppen.setVertretung(Number(this.selectedGroup.gruppennummer), this.selectedLeader);

        if (!response.ok) throw new Error("Fehler beim Festlegen des Vertreters");

        alert(`Vertreter für ${this.selectedGroup.gruppenname} erfolgreich festgelegt.`);
        this.$emit("refreshData", true);
        this.$emit("changeTab", 0);
        this.resetForm();
      } catch (error) {
        console.error("Fehler beim Festlegen des Vertreters:", error);
        alert("Fehler beim Festlegen des Vertreters. Bitte versuchen Sie es erneut.");
      }
    },

    // Entfernt den Vertreter aus der ausgewählten Gruppe
    async removeDeputy() {
      if (!this.selectedGroup) {
        alert("Bitte wählen Sie eine gültige Gruppe.");
        return;
      }

      const bodyData = {
        gruppenNummer: Number(this.selectedGroup.gruppennummer),
        benutzername: null
      };

      try {
        const response = await api.gruppen.removeVertretung(Number(this.selectedGroup.gruppennummer));

        if (!response.ok) throw new Error("Fehler beim Entfernen des Vertreters");

        alert(`Vertreter für ${this.selectedGroup.gruppenname} erfolgreich entfernt.`);
        this.$emit("refreshData", true);
        this.$emit("changeTab", 0);
        this.resetForm();
      } catch (error) {
        console.error("Fehler beim Entfernen des Vertreters:", error);
        alert("Fehler beim Entfernen des Vertreters. Bitte versuchen Sie es erneut.");
      }
    },

    // Setzt die Auswahl zurück
    resetForm() {
      this.selectedGroup = null;
      this.selectedLeader = null;
    }
  }
};
</script>