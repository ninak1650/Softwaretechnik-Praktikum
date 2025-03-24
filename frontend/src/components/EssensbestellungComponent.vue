<template>
  <div class="container">
    <div class="columns">
      <div class="column is-one-third">
        <!-- Datumsauswahl -->
        <b-field label="Datum auswählen" icon="Left">
          <b-datepicker v-model="selectedDate" inline placeholder="Klicken zur Auswahl..." :min-date="minDate"
            :max-date="maxDate" size="is-medium" indicators="bars" :events="events" :first-day-of-week=1
            :month-names="months" :day-names="days" tabindex="-1">
          </b-datepicker>
        </b-field>
      </div>

      <div class="column is-two-thirds">
        <!-- Schalter für die Anzeige der offenen Bestellungen mit Label -->
        <b-field>
          <label class="checkbox">
            <b-switch v-model="showCustomersWithCheckbox"></b-switch>
            <span>Nur offene Bestellungen anzeigen</span>
          </label>
        </b-field>

        <!-- Wenn kein Datum ausgewählt ist, werden die Tabs und Tabellen nicht angezeigt -->
        <div v-if="selectedDate">
          <!-- Tabs für Gruppen -->
          <div class="tabs is-toggle">
            <ul>
              <li v-for="(tab, index) in tabs" :key="index" :class="{ 'is-active': activeTab === index }">
                <a @click="activeTab = index">
                  <!-- Markiere die erste Gruppe als Stammgruppe und die anderen als Vertretungsgruppen -->
                  <span>{{ index === 0 ? `Stammgruppe: ${tab.label}` : `Vertretungsgruppe: ${tab.label}` }}</span>
                </a>
              </li>
            </ul>
          </div>

          <!-- Tabellen für jeden Tab -->
          <div v-for="(tab, index) in tabs" :key="index" v-show="activeTab === index">
            <table class="table is-striped is-fullwidth">
              <thead>
                <tr>
                  <th></th> <!-- Leere Zelle für die Checkbox -->
                  <th>Name</th>
                  <th>Essen</th>
                  <th>Salat</th>
                </tr>
              </thead>
              <tbody>
                <!-- Filtere die Kunden je nach Auswahl des Switches -->
                <tr v-for="(kunde, kIndex) in filteredKunden(tab.kunden)" :key="kunde.kundennummer">
                  <td>
                    <!-- Checkbox, die basierend auf den Werten von "essen" und "salat" geprüft wird -->
                    <input type="checkbox" :checked="isChecked(kunde)" disabled />
                  </td>
                  <td>{{ kunde.name }}</td>
                  <td>
                    <!-- Select Dropdown für die "Essen"-Spalte -->
                    <div class="field">
                      <div class="control">
                        <div class="select">
                          <select v-model="kunde.essen" @change="handleSelectChange(kunde, 'Essen', kunde.essen)">
                            <option value="">Bitte auswählen...</option>
                            <option value="Essen 1">🟥 Essen 1</option>
                            <option value="Essen 2">🟦 Essen 2</option>
                            <option value="Kein Essen">❌ Kein Essen</option>
                          </select>
                        </div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <!-- Select Dropdown für die "Salat"-Spalte -->
                    <div class="field">
                      <div class="control">
                        <div class="select">
                          <select v-model="kunde.salat" @change="handleSelectChange(kunde, 'Salat', kunde.salat)">
                            <option value="">Bitte auswählen...</option>
                            <option value="Ja">✅ Ja</option>
                            <option value="Nein">❌ Nein</option>
                          </select>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
import { api } from "@/api/api";
import { getUsernameFromToken } from "@/util/jwt";
import { decodeEssen, encodeEssen } from "@/util/misc";
const thisMonth = new Date().getMonth()

export default {
  data() {
    const now = new Date();
    const today = new Date();
    const maxDate = new Date(today);
    maxDate.setDate(today.getDate() + 21); // Setzt das maximale Datum auf 14 Tage in der Zukunft

    // Bestimmen, ob der aktuelle Tag bis 8 Uhr morgens noch verfügbar sein soll
    if (now.getHours() < 8) {
      today.setDate(today.getDate() - 1); // Erlaubt Änderungen für den Vortag
    }

    return {
      tabs: [], // Hier werden die Tabs mit den API-Daten gefüllt
      activeTab: 0, // Aktive Tab-Index
      selectedDate: null, // Setze selectedDate auf null statt "" (leerer String)
      minDate: today, // Mindestdatum auf berechneten Wert setzen
      maxDate: maxDate, // Maximales Datum auf 14 Tage in der Zukunft setzen
      showCustomersWithCheckbox: false, // Schalter für die Anzeige der Kunden mit ausgefüllter Checkbox
      events: [
      ],
      months: ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September"
        , "Oktober", "November", "Dezember"],
      days: ["So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"],
    };
  },

  methods: {
    formatLocalDate(date) {
      const now = new Date();
      const isBefore8AM = now.getHours() < 8;
      const adjustedDate = new Date(date);

      // Wenn der ausgewählte Tag heute ist und es vor 8 Uhr morgens ist
      if (isBefore8AM && adjustedDate.toDateString() === now.toDateString()) {
        adjustedDate.setDate(adjustedDate.getDate() - 1); // Ändere auf den Vortag
      }

      const year = adjustedDate.getFullYear();
      const month = String(adjustedDate.getMonth() + 1).padStart(2, "0");
      const day = String(adjustedDate.getDate()).padStart(2, "0");

      return `${year}-${month}-${day}`;
    },

    async kalenderFuellen() {
      try {
        const response = await api.gruppen.getGruppenForAccount(getUsernameFromToken());

        if (!response.ok) {
          throw new Error("Fehler beim Laden der Daten");
        }

        let data = await response.json();

        const orderedDates = new Set();

        for (let group of data) {
          group.kunden = [];
          const response = await api.gruppen.getGruppenMitglieder(group.gruppenNummer);
          if (!response.ok) {
            throw new Error("Fehler beim Laden der Gruppenmitglieder" + response.statusText);
          }
          group.kunden = await response.json();
        }

        for (const group of data) {
          for (const customer of group.kunden) {
            for (const order of customer.bestellung) {
              const date = order.datum;
              if (this.allCustomersOrderedForDate(date, data)) {
                orderedDates.add(date);
              }
            }
          }
        }


        // Konvertiere das Set in ein Array von Event-Objekten mit "is-danger"
        this.events = [...orderedDates].map(date => ({
          date: new Date(date),
          type: "is-success"
        }));

      } catch (error) {
        console.error("Fehler beim Laden der Gruppen:", error);
      }
    },


    // Hilfsmethode, die prüft, ob alle Kunden an diesem Datum bestellt haben
    allCustomersOrderedForDate(date, groups) {
      return groups.every(group => {
        return group.kunden.every(customer => {
          // Prüfe, ob der Kunde an diesem Datum eine Bestellung hat
          return customer.bestellung.some(order => order.datum === date);
        });
      });
    },



    async fetchGruppenData() {
      try {
        const response = await api.gruppen.getGruppenForAccount(getUsernameFromToken());

        if (!response.ok) {
          throw new Error("Fehler beim Laden der Daten");
        }

        let data = await response.json();
        for (let group of data) {
          group.kunden = [];
          const response = await api.gruppen.getGruppenMitglieder(group.gruppenNummer);
          if (!response.ok) {
            throw new Error("Fehler beim Laden der Gruppenmitglieder" + response.statusText);
          }
          group.kunden = await response.json();
          group.account = {}
          const response2 = await api.accounts.getAccount(getUsernameFromToken());
          if (!response2.ok) {
            throw new Error("Fehler beim Laden des Accounts" + response2.statusText);
          }
          group.account = await response2.json();
        }


        if (data && Array.isArray(data)) {
          for (let i = 0; i < data.length; i++) {
            let gruppe = data[i];

            const currentUser = getUsernameFromToken();

            if (gruppe.account) {
              let gruppenleiter = {
                name: gruppe.account.nachname || "Gruppenleiter",
                essen: "",
                salat: "",
                kundennummer: gruppe.account.benutzername,
                bestellung: gruppe.account.bestellung,
              }

              if ( i === 0 && gruppe.account.benutzername === currentUser) {
                if(!gruppe.kunden.some(k => k.kundenNummer === currentUser)){
                  gruppe.kunden.unshift(gruppenleiter);
                }
                
              }
            }
            for (let kunde of gruppe.kunden) {
              kunde.essen = "";
              kunde.salat = "";
              kunde.kundennummer = kunde.kundenNummer || gruppe.account.benutzername;
              const bestellungen = kunde.bestellung.filter(
                (bestellung) =>
                  bestellung.datum === this.formatLocalDate(this.selectedDate)
              );
              if (bestellungen.length > 0) {
                for (let bestellung of bestellungen) {
                  const decoded = decodeEssen(bestellung.essenswahl);
                  kunde.essen = decoded.essen;
                  kunde.salat = decoded.salat == "Ja" ? "Ja" : "Nein";
                };
              }
            };
          };
          this.tabs = data.map((gruppe) => ({
            label: gruppe.gruppenName,
            kunden: gruppe.kunden.map((kunde) => ({
              name: kunde.name,
              essen: kunde.essen,
              salat: kunde.salat,
              kundennummer: kunde.kundennummer,
            })),
          }));
        }
      } catch (error) {
        console.error("Fehler beim Laden der Gruppen:", error);
      }
    },

    isChecked(kunde) {
      return (
        kunde.essen !== "" &&
        kunde.essen !== "Keine Wahl" &&
        kunde.salat !== "" &&
        kunde.salat !== "Keine Wahl"
      );
    },

    filteredKunden(kunden) {
      if (this.showCustomersWithCheckbox) {
        return kunden.filter((kunde) => !this.isChecked(kunde));
      } else {
        return kunden;
      }
    },

    async handleSelectChange(kunde, field, value) {
      if (field === "Essen") {
        console.log(
          `Änderung bei ${kunde.name} (Kundennummer: ${kunde.kundennummer}): Essen wurde auf "${value}" gesetzt. Salat ist "${kunde.salat}".`
        );
      } else if (field === "Salat") {
        console.log(
          `Änderung bei ${kunde.name} (Kundennummer: ${kunde.kundennummer}): Salat wurde auf "${value}" gesetzt. Essen ist "${kunde.essen}".`
        );
      }

      if (kunde.salat !== "" && kunde.essen !== "") {
        let Salatboolean = kunde.salat === "Ja";
        const formattedDate = this.formatLocalDate(this.selectedDate);
        try {
          const response = await api.bestellungen.setBestellung(kunde.kundennummer, formattedDate, encodeEssen(kunde.essen, Salatboolean));
          if (!response.ok) {
            console.error(
              "Fehler beim Hinzufügen der Bestellung: ",
              await response.text
            );
          } else {
            console.log(`Bestellung erfolgreich eingefügt für ${formattedDate}`);
            await this.kalenderFuellen();
          }
        } catch (error) {
          console.error("Fehler beim Hinzufügen der Bestellung: ", error);
        }
      }
    },
  },

  watch: {
    async selectedDate(newDate, oldDate) {
      if (newDate !== oldDate) {
        if (newDate !== null) {
          await this.fetchGruppenData();
          // Wenn das Datum geändert wurde, rufe kalenderFuellen auf, um das Event hinzuzufügen
          await this.kalenderFuellen();
        }
      }
    },
  },

  created() {
    this.kalenderFuellen();

  },
};
</script>
