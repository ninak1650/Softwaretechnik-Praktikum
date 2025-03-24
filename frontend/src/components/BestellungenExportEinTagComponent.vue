<template>
  <div class="container">
    <!-- Überschrift mit Datum -->
    <h2 class="title is-2">Bestellübersicht:</h2>
    <h2 class="title is-3">{{ formattedDate }}</h2>
    <!-- Bestellstatistik -->
    <div class="box">
      <p class="order-text"><strong>🟥 Essen 1:&nbsp; </strong> {{ orderCount.essen1 }}x</p>
      <p class="order-text"><strong>🟦 Essen 2:&nbsp; </strong> {{ orderCount.essen2 }}x</p>
      <p class="order-text"><strong>🥗 Salat:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong> {{ orderCount.salat }}x</p>
    </div>

    <!-- PDF-Button -->
    <b-button class="mt-6" type="is-primary" size="is-large" icon-left="file-pdf-box" @click="generatePDF">
      PDF erstellen
    </b-button>
  </div>
</template>

<script>
import { api } from "@/api/api";
import { Firmenlogo } from "@/assets/Firmenlogo.js";
import { getStandortFromToken } from "@/util/jwt";
import { decodeEssen } from "@/util/misc";
import jsPDF from "jspdf";

export default {
  data() {
    return {
      orderCount: { essen1: 0, essen2: 0, salat: 0 },
      standort: getStandortFromToken(),
    };
  },
  computed: {
    formattedDate() {
      return new Date().toLocaleDateString("de-DE", {
        weekday: "long", year: "numeric", month: "long", day: "numeric"
      });
    }
  },
  methods: {
    // TODO: Hier müssen die Daten aus dem Backend rein (anstelle Randomwerte)...
    async fetchOrders() {
      const resp = await api.bestellungen.getBestellungenForStandortAndDate(this.standort, new Date().toISOString().split("T")[0]);
      const bestellungen = await resp.json();
      for (const bestellung of bestellungen) {
        const order = decodeEssen(bestellung.essenswahl);
        console.log(order);
        if (order.salat === "Ja") {
          this.orderCount.salat++;
        }
        if (order.essen === "Essen 1") {
          this.orderCount.essen1++;
        }
        if (order.essen === "Essen 2") {
          this.orderCount.essen2++;
        }
      }
    },
    generatePDF() {
      const doc = new jsPDF();
      const currentTime = new Date();
      const formattedTime = currentTime.toLocaleTimeString("de-DE", { hour: "2-digit", minute: "2-digit" });
      const formattedDate = currentTime.toLocaleDateString("de-DE");

      // Firmenlogo einfügen (Logo muss als Base64-String vorliegen)
      doc.addImage(Firmenlogo, "PNG", 10, 10, 39.5, 11.3); // X, Y, Breite, Höhe

      // Titel setzen
      doc.setFont("helvetica", "bold");
      doc.setFontSize(25);
      doc.text("Bestellübersicht", 105, 35, { align: "center" });
      doc.setFont("helvetica", "normal");
      doc.setFontSize(20);
      doc.text(`für ${this.formattedDate}`, 105, 45, { align: "center" });

      // Standort
      doc.setFontSize(16);
      doc.setFont("helvetica", "normal");
      doc.text(`Standort: ${this.standort}`, 20, 65);

      // Horizontale Linie
      doc.line(20, 70, 190, 70);

      // Bestellübersicht
      doc.setFontSize(16);
      doc.text(`Essen 1:`, 20, 85);
      doc.setFont("helvetica", "bold");
      doc.text(`${this.orderCount.essen1}x`, 170, 85);

      doc.setFont("helvetica", "normal");
      doc.text(`Essen 2:`, 20, 100);
      doc.setFont("helvetica", "bold");
      doc.text(`${this.orderCount.essen2}x`, 170, 100);

      doc.setFont("helvetica", "normal");
      doc.text(`Salat:`, 20, 115);
      doc.setFont("helvetica", "bold");
      doc.text(`${this.orderCount.salat}x`, 170, 115);

      // Horizontale Linie
      doc.setFont("helvetica", "normal");
      doc.line(20, 125, 190, 125);

      // Stand (Datum & Uhrzeit)
      doc.setFontSize(12);
      doc.text(`Stand: ${formattedDate}, ${formattedTime} Uhr`, 135, 135);

      // PDF speichern mit dynamischem Namen
      doc.save(`Bestelluebersicht_${formattedDate}.pdf`);
    }
  },
  mounted() {
    this.fetchOrders();

  }
};
</script>

<style scoped>
.container {
  max-width: 500px;
  margin: 0 auto;
  text-align: center;
}

.calendar-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.calendar-label {
  font-weight: bold;
  margin-bottom: 10px;
}

/* Bestellstatistik */
.order-text {
  font-size: 1.6rem;
  font-weight: bold;
  margin-top: 10px;
}

/* Kalender */
.custom-calendar {
  font-size: 1.2rem;
  width: 100%;
  margin-bottom: 20px;
}
</style>