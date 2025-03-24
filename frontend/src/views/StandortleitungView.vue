<template>
    <BareboneNavbar />
    <div class="hero is-small">
      <div class="hero-body">
        <section>
          <!-- Tab-Menü zur Navigation zwischen Gruppenübersicht und Vertretung -->
          <b-tabs v-model="activeTab" type="is-toggle-rounded" size="is-medium" position="is-centered">
            
            <!-- Gruppenübersicht -->
            <b-tab-item label="Gruppenübersicht" icon="table-account">
              <GruppenübersichtComponent
                ref="gruppenÜbersicht"
                :tagClass="'tag is-success'"
                :gruppenData="gruppenData"
                @updateGruppenData="updateGruppenData"
              />
            </b-tab-item>
  
            <!-- Vertretung eintragen -->
            <b-tab-item label="Vertretung eintragen" icon="account-switch">
              <VertretungVerwaltungComponent
                :gruppenData="gruppenData"
                @refreshData="refreshGruppenData"
                @changeTab="handleChangeTab"
              />
            </b-tab-item>
  
          </b-tabs>
        </section>
      </div>
    </div>
  </template>
  
  <script>
  import BareboneNavbar from "../components/navbars/BareboneNavbar.vue";
  import GruppenübersichtComponent from "../components/GruppenübersichtComponent.vue";
  import VertretungVerwaltungComponent from "@/components/VertretungVerwaltungComponent.vue";
  
  export default {
    name: "StandortleitungView",
    components: {
      BareboneNavbar,
      GruppenübersichtComponent,
      VertretungVerwaltungComponent
    },
    data() {
      return {
        activeTab: parseInt(localStorage.getItem("activeTab")) || -1, // Standardwert: erste Tab-Seite (Gruppenübersicht)
        gruppenData: [], // Speichert die Gruppendaten aus GruppenübersichtComponent
      };
    },
    watch: {
      // Speichert den aktiven Tab in localStorage, wenn er sich ändert
      activeTab(newTab) {
        console.log(`activeTab geändert: ${newTab}`);
        localStorage.setItem("activeTab", newTab);
      },
    },
    methods: {
      // Aktualisiert die Gruppendaten mit neuen Daten aus GruppenübersichtComponent
      updateGruppenData(newData) {
        console.log("updateGruppenData aufgerufen, gruppenData aktualisiert:", newData);
        this.gruppenData = newData;
      },
  
      // Lädt die Gruppen-Daten neu, falls erforderlich
      refreshGruppenData(shouldRefresh) {
        console.log(`refreshGruppenData aufgerufen, shouldRefresh: ${shouldRefresh}`);
  
        if (shouldRefresh) {
          console.log("`shouldRefresh` ist `true`, rufe `fetchGruppenData()` auf...");
  
          if (this.$refs.gruppenÜbersicht && typeof this.$refs.gruppenÜbersicht.fetchGruppenData === "function") {
            console.log("`fetchGruppenData()` wird aufgerufen...");
            this.$refs.gruppenÜbersicht.fetchGruppenData();
          } else {
            console.error("`fetchGruppenData()` ist nicht definiert!");
          }
        }
      },
  
      // Wechselt den aktiven Tab und aktualisiert bei Bedarf die Gruppendaten
      handleChangeTab(tabIndex) {
        if (this.activeTab !== tabIndex) {
          this.activeTab = -1; // Temporäres Zurücksetzen, um eine zuverlässige Aktualisierung zu gewährleisten
          this.$nextTick(() => {
            this.activeTab = tabIndex;
            if (tabIndex === 0) {
              this.refreshGruppenData(true);
            }
          });
        }
      },
    },
    mounted() {
      // Stellt sicher, dass der zuletzt verwendete Tab korrekt wiederhergestellt wird
      const savedTab = parseInt(localStorage.getItem("activeTab"));
      if (!isNaN(savedTab) && [0, 1].includes(savedTab)) {
        this.activeTab = savedTab;
      } else {
        this.activeTab = 0; // Standard: Gruppenübersicht
      }
    },
  };
  </script>
  
  <style scoped>
  /* Stil für die Tabellenüberschrift */
  .custom-table thead th {
    background-color: #4a4a4a; /* Dunkelgrauer Hintergrund */
    color: white; /* Weiße Schrift */
    font-weight: bold; /* Fettschrift */
  }
  
  /* Stil für den Standard-Dropdown */
  .dropdown-button {
    background-color: white !important;
    color: black !important;
    border: 1px solid #ccc;
  }
  
  /* Deaktivierte Buttons */
  .vertretung-button.disabled,
  .entfernen-button.disabled {
    background-color: #ddd !important;
    cursor: not-allowed !important;
    color: gray !important;
  }
  </style>
  