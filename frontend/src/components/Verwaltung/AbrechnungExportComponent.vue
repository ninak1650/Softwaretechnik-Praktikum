<template>
    <section class="container">
        <h1 class="title">Abrechnung Export</h1>

        <!-- Auswahl-Felder für Zeitraum, Standort, Gruppe und Mitarbeiter -->
        <table class="table is-fullwidth">
            <thead>
                <tr>
                    <th>Zeitraum auswählen</th>
                    <th>Standort auswählen</th>
                    <th>Gruppe auswählen</th>
                    <th>Mitarbeiter auswählen</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <!-- Zeitraum Auswahl -->
                    <td>
                        <b-dropdown aria-role="list">
                            <template #trigger="{ active }">
                                <b-button :label="selectedZeitraum ? selectedZeitraum : 'Zeitraum wählen'"
                                    type="is-primary" :icon-right="active ? 'menu-up' : 'menu-down'" />
                            </template>
                            <b-dropdown-item v-for="months in availableMonths" :key="months.value"
                                @click="selectZeitraum(months.value)">
                                {{ months.label }}
                            </b-dropdown-item>
                        </b-dropdown>
                    </td>

                    <!-- Standort Auswahl -->
                    <td>
                        <b-dropdown aria-role="list" :disabled="standorteList.length === 0">
                            <template #trigger="{ active }">
                                <b-button :label="selectedStandort ? ` ${selectedStandort}` : 'Standort wählen'"
                                    type="is-primary" :icon-right="active ? 'menu-up' : 'menu-down'" />
                            </template>
                            <b-dropdown-item @click="selectStandort(null)">Alle Standorte</b-dropdown-item>
                            <b-dropdown-item v-for="standort in standorteList" :key="standort"
                                @click="selectStandort(standort)">
                                {{ standort }}
                            </b-dropdown-item>
                        </b-dropdown>
                    </td>

                    <!-- Gruppe Auswahl -->
                    <td>
                        <b-dropdown aria-role="list" :disabled="!selectedStandort">
                            <template #trigger="{ active }">
                                <b-button
                                    :label="selectedGruppe ? `${selectedGruppe.gruppenNummer} - ${selectedGruppe.gruppenName}` : 'Gruppe wählen'"
                                    type="is-primary" :icon-right="active ? 'menu-up' : 'menu-down'" />
                            </template>
                            <b-dropdown-item @click="selectGruppe(null)">Alle Gruppen</b-dropdown-item>
                            <b-dropdown-item v-for="gruppe in gruppenList" :key="gruppe.gruppenNummer"
                                @click="selectGruppe(gruppe)">
                                {{ gruppe.gruppenNummer }} - {{ gruppe.gruppenName }}
                            </b-dropdown-item>
                        </b-dropdown>

                    </td>

                    <!-- Mitarbeiter Auswahl -->
                    <td>
                        <b-dropdown aria-role="list" :disabled="mitarbeiterList.length === 0">
                            <template #trigger="{ active }">
                                <b-button
                                    :label="selectedMitarbeiter ? `${selectedMitarbeiter.mitarbeiterName}` : 'Mitarbeiter wählen'"
                                    type="is-primary" :icon-right="active ? 'menu-up' : 'menu-down'" />
                            </template>
                            <b-dropdown-item @click="selectMitarbeiter(null)">Alle Mitarbeiter</b-dropdown-item>
                            <b-dropdown-item v-for="mitarbeiter in mitarbeiterList" :key="mitarbeiter.mitarbeiterNummer"
                                @click="selectMitarbeiter(mitarbeiter)">
                                {{ mitarbeiter.mitarbeiterNummer }} - {{ mitarbeiter.mitarbeiterName }}
                            </b-dropdown-item>
                        </b-dropdown>
                    </td>
                </tr>
            </tbody>
        </table>

        <!-- Bestellübersicht -->
        <div v-if="selectedZeitraum">
            <h2 class="subtitle">Standort: {{ selectedStandort || 'Alle' }}</h2>
            <h2 class="subtitle">Monat: {{ selectedZeitraum }}</h2>
            <h2 class="subtitle" v-if="selectedGruppe != 'Alle' && selectedGruppe">
                Gruppe: {{ selectedGruppe ? selectedGruppe.gruppenNummer : 'Unbekannt' }}</h2>
            <h2 class="subtitle" v-if="selectedMitarbeiter && selectedMitarbeiter !== 'Alle'">
                Name/ID: {{ selectedMitarbeiter.mitarbeiterName }}
            </h2>

            <table class="table is-fullwidth">
                <thead>
                    <tr>
                        <!-- Dynamische Spalten je nach Auswahl -->
                        <th v-if="!selectedStandort">Standort</th>
                        <th v-if="selectedStandort && !selectedGruppe">Gruppe</th>
                        <th v-if="selectedGruppe && !selectedMitarbeiter">Mitarbeiter</th>

                        <!-- Immer die Essensübersicht anzeigen -->
                        <th>Essen 1</th>
                        <th>Essen 2</th>
                        <th>Salat</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(entry, index) in previewData" :key="index">
                        <td v-if="!selectedStandort">{{ entry.standort }}</td>
                        <td v-if="selectedStandort && !selectedGruppe">{{ entry.gruppe }}</td>
                        <td v-if="selectedGruppe && !selectedMitarbeiter">{{ entry.name }}</td>
                        <td>{{ entry.essen1 }}</td>
                        <td>{{ entry.essen2 }}</td>
                        <td>{{ entry.salat }}</td>
                    </tr>


                    <!-- Keine Daten verfügbar -->
                    <tr v-if="keineDatenVerfuegbar">
                        <td colspan="5" class="has-text-centered">Keine Daten verfügbar</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- PDF Exportieren -->
        <div class="has-text-centered mt-6">
            <b-button type="is-primary" icon-left="file-pdf-box" size="is-large" @click="generatePDF" :disabled="keineDatenVerfuegbar">PDF Exportieren</b-button>
        </div>

    </section>
</template>


<script>
import { api } from "@/api/api";
import { Firmenlogo } from "@/assets/Firmenlogo.js"; // Logo importieren
import { getToken } from "@/util/jwt";
import jsPDF from "jspdf";

export default {
    data() {
        return {
            selectedStandort: null,
            selectedGruppe: null,
            selectedMitarbeiter: null,
            selectedZeitraum: null,
            standorteList: [],
            gruppenList: [],
            mitarbeiterList: [],
            orderData: [],
            previewData: [],
            totalEssen1: 0,
            totalEssen2: 0,
            totalSalat: 0
        };
    },
    computed: {
        filteredGruppenList() {
            if (!this.selectedStandort || this.selectedStandort === "all") return [];
            return this.gruppenList.filter(gruppe => gruppe.standortId === this.selectedStandort);
        },
        filteredMitarbeiterList() {
            if (!this.selectedGruppe || this.selectedGruppe === "all") return [];
            return this.mitarbeiterList.filter(mitarbeiter => mitarbeiter.gruppeId === this.selectedGruppe);
        },
        availableMonths() {
            const today = new Date();
            return Array.from({ length: 6 }, (_, i) => {
                const date = new Date(today);
                date.setMonth(today.getMonth() - i);
                return {
                    value: date.toISOString().slice(0, 7),
                    label: date.toLocaleDateString("de-DE", { month: "long", year: "numeric" })
                };
            });
        }
    },
    methods: {
        //Zeitraum (Monat) auswählen
        selectZeitraum(value) {
            console.log("`selectZeitraum()` aufgerufen mit Wert:", value);
            this.selectedZeitraum = value;
            console.log("`selectedZeitraum` aktualisiert auf:", this.selectedZeitraum);
            // Bestellungen erneut anfordern und Auswahl zurücksetzen
            this.selectedStandort = null;
            this.selectedGruppe = null;
            this.selectedMitarbeiter = null;

            this.fetchBestellungenData();
        },

        //Standort auswählen
        selectStandort(standort) {
            console.log("`selectStandort()` aufgerufen mit Wert:", standort);
            this.selectedStandort = standort;
            this.selectedGruppe = null;
            this.selectedMitarbeiter = null;
            this.fetchGruppenData();
            this.updatePreview();
        },

        //Gruppe auswählen
        selectGruppe(gruppe) {
            console.log("`selectGruppe()` aufgerufen mit Wert:", gruppe);
            this.selectedGruppe = gruppe;
            this.selectedMitarbeiter = null;
            this.fetchMitarbeiterData();
            this.updatePreview();
        },

        //Mitarbeiter auswählen
        selectMitarbeiter(mitarbeiter) {
            console.log("`selectMitarbeiter()` aufgerufen mit Wert:", mitarbeiter);
            this.selectedMitarbeiter = mitarbeiter;
            this.updatePreview();
        },
        async fetchBestellungenData() {
            console.log("`fetchBestellungenData()` wurde aufgerufen.");

            try {
                const token = getToken(this.$router);

                // Alle Standorte abrufen
                const standorteResponse = await api.standorte.getStandorte();

                if (!standorteResponse.ok) throw new Error(`Standorte API Fehler: ${standorteResponse.status}`);
                const standorte = await standorteResponse.json();
                console.log("Alle Standorte:", standorte);

                let alleBestellungen = [];

                // Durchläuft jeden Standort
                for (const standort of standorte) {
                    // Ruft alle Gruppen des jeweiligen Standorts ab
                    const gruppenResponse = await api.gruppen.getGruppenForStandort(standort);

                    if (!gruppenResponse.ok) throw new Error(`Gruppen API Fehler: ${gruppenResponse.status}`);
                    const gruppen = await gruppenResponse.json();
                    console.log(`Gruppen für ${standort}:`, gruppen);

                    // Durchläuft jede Gruppe
                    for (const gruppe of gruppen) {
                        // Ruft alle Mitarbeiter der jeweiligen Gruppe ab
                        const mitarbeiterResponse = await api.gruppen.getGruppenMitglieder(gruppe.gruppenNummer);

                        if (!mitarbeiterResponse.ok) throw new Error(`Mitarbeiter API Fehler: ${mitarbeiterResponse.status}`);
                        const mitarbeiterList = await mitarbeiterResponse.json();
                        console.log(`Mitarbeiter für Gruppe ${gruppe.gruppenNummer}:`, mitarbeiterList);

                        for (const mitarbeiter of mitarbeiterList) {
                            if (!mitarbeiter.bestellung || mitarbeiter.bestellung.length === 0) {
                                console.warn(`Keine Bestellungen für Mitarbeiter ${mitarbeiter.kunde_id}`);
                                continue;
                            }

                            console.log(`Bestellungen für Mitarbeiter ${mitarbeiter.kunde_id}:`, mitarbeiter.bestellung);

                            alleBestellungen.push(
                                ...mitarbeiter.bestellung.map(b => ({
                                    id: b.id,
                                    standort: standort,
                                    gruppe: gruppe.gruppenNummer,
                                    mitarbeiterNummer: mitarbeiter.kunde_id,
                                    mitarbeiterName: mitarbeiter.mitarbeiterName || `Mitarbeiter ${mitarbeiter.kunde_id}`,
                                    datum: b.datum,
                                    essenswahl: b.essenswahl,
                                }))
                            );
                        }
                    }
                }
                this.orderData = alleBestellungen;
                console.log("Gesamte Bestellungs-Datenbank:", this.orderData);
                this.fetchStandorteData();
                this.updatePreview();
            } catch (error) {
                console.error("Fehler beim Laden der Bestellungen:", error);
                this.orderData = [];
            }
        },
        async fetchStandorteData() {
            console.log("`fetchStandorteData()` wurde aufgerufen.");

            if (!this.orderData || this.orderData.length === 0) {
                console.warn("Keine Bestellungen vorhanden, daher keine Standorte.");
                this.standorteList = [];
                return;
            }

            const uniqueStandorteList = [...new Set(this.orderData.map(order => order.standort))];

            this.standorteList = [ ...uniqueStandorteList];

            console.log("Verfügbare Standorte:", this.standorteList);

            // Auswahl zurücksetzen und Gruppen anfordern
            this.selectedStandort = null;
            this.selectedGruppe = null;
            this.selectedMitarbeiter = null;
            this.gruppenList = [];
            this.mitarbeiterList = [];
        },
        async fetchGruppenData() {
            console.log("`fetchGruppenData()` wurde aufgerufen.");

            if (!this.selectedStandort || this.selectedStandort === "Alle") {
                // Wenn der Standort „Alle“ ist, sollten alle Gruppen angezeigt werden.
                this.gruppenList = [...new Map(
                    this.orderData.map(order => [order.gruppe, { gruppenNummer: order.gruppe, gruppenName: `Gruppe ${order.gruppe}` }])
                ).values()];
            } else {
                // Filtert die `Gruppe` des Standorts anhand von `orderData`
                this.gruppenList = [...new Map(
                    this.orderData
                        .filter(order => order.standort === this.selectedStandort)
                        .map(order => [order.gruppe, { gruppenNummer: order.gruppe, gruppenName: `Gruppe ${order.gruppe}` }])
                ).values()];
            }

            console.log("Verfügbare Gruppen:", this.gruppenList);

            // Als Nächstes Mitarbeiter anfordern
            this.fetchMitarbeiterData();
        },
        async fetchMitarbeiterData() {
            console.log("`fetchMitarbeiterData()` wurde aufgerufen.");

            this.mitarbeiterList = [...new Map(
                this.orderData
                    .filter(order => this.selectedGruppe === "Alle" || order.gruppe === this.selectedGruppe?.gruppenNummer)
                    .map(order => [
                        order.mitarbeiterNummer,
                        {
                            mitarbeiterNummer: order.mitarbeiterNummer,
                            mitarbeiterName: order.mitarbeiterName || `Mitarbeiter ${order.mitarbeiterNummer}`
                        }
                    ])
            ).values()];

            console.log("Verfügbare Mitarbeiter:", this.mitarbeiterList);
        },
        updatePreview() {
            console.log("`updatePreview()` wurde aufgerufen.");
            console.log("Aktuelle Auswahl:", {
                Standort: this.selectedStandort,
                Gruppe: this.selectedGruppe,
                Mitarbeiter: this.selectedMitarbeiter,
                Zeitraum: this.selectedZeitraum,
            });

            let filteredData = [...this.orderData];

            if (this.selectedZeitraum) {
                filteredData = filteredData.filter(o => o.datum?.startsWith(this.selectedZeitraum));
            }
            if (this.selectedStandort && this.selectedStandort !== "Alle") {
                filteredData = filteredData.filter(o => o.standort === this.selectedStandort);
            }
            if (this.selectedGruppe && this.selectedGruppe !== "Alle") {
                console.log("Aktuelle Gruppe:", this.selectedGruppe);
                filteredData = filteredData.filter(o => o.gruppe === this.selectedGruppe?.gruppenNummer);

            }
            if (this.selectedMitarbeiter) {
                let mitarbeiterBestellungen = filteredData.filter(o => o.mitarbeiterNummer === this.selectedMitarbeiter.mitarbeiterNummer);

                if (mitarbeiterBestellungen.length > 0) {
                    this.selectedGruppe = {
                        gruppenNummer: mitarbeiterBestellungen[0].gruppe,
                        gruppenName: `Gruppe ${mitarbeiterBestellungen[0].gruppe}`
                    };
                }

                this.previewData = [{
                    gruppe: this.selectedGruppe.gruppenName,
                    essen1: mitarbeiterBestellungen.filter(o => o.essenswahl === 2 || o.essenswahl === 3).length,
                    essen2: mitarbeiterBestellungen.filter(o => o.essenswahl === 4 || o.essenswahl === 5).length,
                    salat: mitarbeiterBestellungen.filter(o => o.essenswahl === 1 || o.essenswahl === 3 || o.essenswahl === 5).length
                }];
            }
            console.log("Gefilterte Bestellungen:", filteredData);
            //Gesamtsumme berechnen
            this.totalEssen1 = filteredData.filter(order => order.essenswahl === 2 || order.essenswahl === 3).length;
            this.totalEssen2 = filteredData.filter(order => order.essenswahl === 4 || order.essenswahl === 5).length;
            this.totalSalat = filteredData.filter(order => order.essenswahl === 1 || order.essenswahl === 3 || order.essenswahl === 5).length;

            console.log(" Berechnete Gesamtwerte:", { totalEssen1: this.totalEssen1, totalEssen2: this.totalEssen2, totalSalat: this.totalSalat });

            // **Daten in `previewData` entsprechend Gruppierung speichern**
            if (!this.selectedStandort) {
                // **Fall 1: Nur Zeit ausgewählt → Gruppierung nach Standort**
                this.previewData = Object.values(filteredData.reduce((acc, order) => {
                    acc[order.standort] = acc[order.standort] || {
                        standort: order.standort, essen1: 0, essen2: 0, salat: 0
                    };
                    acc[order.standort].essen1 += (order.essenswahl === 2 || order.essenswahl === 3 ? 1 : 0);
                    acc[order.standort].essen2 += (order.essenswahl === 4 || order.essenswahl === 5 ? 1 : 0);
                    acc[order.standort].salat += (order.essenswahl === 1 || order.essenswahl === 3 || order.essenswahl === 5 ? 1 : 0);
                    return acc;
                }, {}));
            } else if (!this.selectedStandort && !this.selectedGruppe) {
                this.previewData = [{
                    essen1: this.totalEssen1,
                    essen2: this.totalEssen2,
                    salat: this.totalSalat
                }];
            } else if (!this.selectedGruppe) {
                // **Fall 2: Zeit + Standort ausgewählt → Gruppierung nach Gruppe**
                this.previewData = Object.values(filteredData.reduce((acc, order) => {
                    acc[order.gruppe] = acc[order.gruppe] || {
                        gruppe: order.gruppe, essen1: 0, essen2: 0, salat: 0
                    };
                    acc[order.gruppe].essen1 += (order.essenswahl === 2 || order.essenswahl === 3 ? 1 : 0);
                    acc[order.gruppe].essen2 += (order.essenswahl === 4 || order.essenswahl === 5 ? 1 : 0);
                    acc[order.gruppe].salat += (order.essenswahl === 1 || order.essenswahl === 3 || order.essenswahl === 5 ? 1 : 0);
                    return acc;
                }, {}));

            } else if (!this.selectedMitarbeiter) {
                // **Fall 3: Zeit + Standort + Gruppe ausgewählt → Gruppierung nach Mitarbeiter**
                this.previewData = Object.values(filteredData.reduce((acc, order) => {
                    acc[order.mitarbeiterNummer] = acc[order.mitarbeiterNummer] || {
                        name: order.mitarbeiterName, essen1: 0, essen2: 0, salat: 0
                    };
                    acc[order.mitarbeiterNummer].essen1 += (order.essenswahl === 2 || order.essenswahl === 3 ? 1 : 0);
                    acc[order.mitarbeiterNummer].essen2 += (order.essenswahl === 4 || order.essenswahl === 5 ? 1 : 0);
                    acc[order.mitarbeiterNummer].salat += (order.essenswahl === 1 || order.essenswahl === 3 || order.essenswahl === 5 ? 1 : 0);
                    return acc;
                }, {}));
            } else if (this.selectedMitarbeiter === "Alle") {
                // Gruppierung nach Mitarbeiter
                this.previewData = Object.values(filteredData.reduce((acc, order) => {
                    acc[order.mitarbeiterNummer] = acc[order.mitarbeiterNummer] || {
                        name: order.mitarbeiterName, essen1: 0, essen2: 0, salat: 0
                    };
                    acc[order.mitarbeiterNummer].essen1 += (order.essenswahl === 2 || order.essenswahl === 3 ? 1 : 0);
                    acc[order.mitarbeiterNummer].essen2 += (order.essenswahl === 4 || order.essenswahl === 5 ? 1 : 0);
                    acc[order.mitarbeiterNummer].salat += (order.essenswahl === 1 || order.essenswahl === 3 || order.essenswahl === 5 ? 1 : 0);
                    return acc;
                }, {}));
            } else if (this.selectedMitarbeiter && this.selectedMitarbeiter !== "Alle") {
                let mitarbeiterBestellungen = filteredData.filter(o => o.mitarbeiterNummer === this.selectedMitarbeiter.mitarbeiterNummer);

                this.previewData = [{
                    essen1: mitarbeiterBestellungen.filter(o => o.essenswahl === 2 || o.essenswahl === 3).length,
                    essen2: mitarbeiterBestellungen.filter(o => o.essenswahl === 4 || o.essenswahl === 5).length,
                    salat: mitarbeiterBestellungen.filter(o => o.essenswahl === 1 || o.essenswahl === 3 || o.essenswahl === 5).length
                }];

                this.previewData = [{
                    essen1: this.totalEssen1,
                    essen2: this.totalEssen2,
                    salat: this.totalSalat
                }];

            } else {
                // **Fall 4: Alle Filter gesetzt → Zeigt Gesamtsumme für den gewählten Mitarbeiter**
                this.previewData = [{
                    essen1: this.totalEssen1,
                    essen2: this.totalEssen2,
                    salat: this.totalSalat
                }];
            }


            this.keineDatenVerfuegbar = this.previewData.length === 0 && this.totalEssen1 === 0 && this.totalEssen2 === 0 && this.totalSalat === 0;

            console.log("Aktualisierte Vorschau:", this.previewData);
        }
        ,


        async generatePDF() {
            console.log("`generatePDF()` wurde aufgerufen.");

            if (this.keineDatenVerfuegbar) {
                console.warn(" Keine Daten zum Exportieren!");
                return;
            }

            const doc = new jsPDF();

            // Firmenlogo hinzufügen
            doc.addImage(Firmenlogo, "PNG", 10, 10, 39.5, 11.3);
            doc.setFont("helvetica", "bold");
            doc.setFontSize(20);
            doc.text("Bestellübersicht", 105, 20, { align: "center" });

            doc.setFontSize(12);
            doc.setFont("helvetica", "normal");
            doc.text(`Standort: ${this.selectedStandort || "Alle"}`, 20, 35);
            doc.text(`Gruppe: ${this.selectedGruppe?.gruppenName || "Alle"}`, 20, 45);
            doc.text(`Mitarbeiter: ${this.selectedMitarbeiter?.mitarbeiterName || "Alle"}`, 20, 55);
            doc.text(`Zeitraum: ${this.selectedZeitraum || "N/A"}`, 20, 65);

            doc.line(20, 70, 190, 70);

            let yPosition = 80;

            if (this.previewData.length === 0) {
                doc.text(`Gesamtsumme`, 20, yPosition);
                doc.text(`Essen 1: ${this.totalEssen1}x`, 20, yPosition + 10);
                doc.text(`Essen 2: ${this.totalEssen2}x`, 90, yPosition + 10);
                doc.text(`Salat: ${this.totalSalat}x`, 170, yPosition + 10);
                yPosition += 20;
            } else {
                for (const entry of this.previewData) {
                    if (this.selectedGruppe === null) {
                        doc.text(`Gruppe: ${entry.gruppe}`, 20, yPosition);
                    } else if (this.selectedMitarbeiter === null) {
                        doc.text(`Mitarbeiter: ${entry.name}`, 20, yPosition);
                    }

                    doc.text(`Essen 1:  ${entry.essen1}x`, 20, yPosition + 10);
                    doc.text(`Essen 2:  ${entry.essen2}x`,90, yPosition + 10);
                    doc.text(`Salat:  ${entry.salat}x`, 170, yPosition + 10);
                    yPosition += 20;
                }
            }

            const zeitraumFormatted = this.selectedZeitraum ? this.selectedZeitraum.replace("-", "_") : "Unbekannt";
            const fileName = `Bestellübersicht_${zeitraumFormatted}.pdf`;

            doc.save(fileName);
        }

    },
    watch: {
        selectedZeitraum(newVal) {
            if (newVal) {
                this.fetchBestellungenData();
            }
        },
        selectedStandort(newVal) {
            if (newVal !== null) {
                this.fetchGruppenData();
            }
        },
        selectedGruppe(newVal) {
            if (newVal !== null) {
                this.fetchMitarbeiterData();
            }
        },
        selectedMitarbeiter() {
            this.updatePreview();
        }
    }
};
</script>
