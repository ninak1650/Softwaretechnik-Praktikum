<template>
  <div class="container">
    <h2 class="title is-3">Bestellübersicht für die nächsten 14 Tage</h2>
    <table>
      <thead>
        <tr>
          <th>📅 Datum</th>
          <th>🟥 Essen 1</th>
          <th>🟦 Essen 2</th>
          <th>🥗 Salat</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(order, index) in orders" :key="index">
          <td>{{ order.date }}</td>
          <td>{{ order.essen1 }}</td>
          <td>{{ order.essen2 }}</td>
          <td>{{ order.salat }}</td>
        </tr>
      </tbody>
    </table>
    <!-- PDF-Button -->
    <b-button class="mt-5" type="is-primary" size="is-large" icon-left="file-pdf-box" @click="generatePDF">
      PDF erstellen
    </b-button>
  </div>
</template>

<script>
import { api } from "@/api/api";
import { Firmenlogo } from "@/assets/Firmenlogo.js"; // Logo importieren
import { getStandortFromToken } from "@/util/jwt";
import { decodeEssen } from "@/util/misc";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

export default {
  data() {
    return {
      orders: [],
      standort: getStandortFromToken(),
    };
  },
  mounted() {
    this.fetchOrders(); // Direkt beim Laden generieren
  },
  methods: {
    generateOrders() {
      let orders = [];
      let currentDate = new Date();

      for (let i = 0; i < 14; i++) {
        const formattedDate = currentDate.toLocaleDateString("de-DE", {
          weekday: "long",
          day: "2-digit",
          month: "2-digit",
          year: "numeric"
        });

        // Dummy-Daten für Testzwecke (Backend-Anbindung später notwendig)
        orders.push({
          date: formattedDate,
          essen1: Math.floor(Math.random() * 10),
          essen2: Math.floor(Math.random() * 10),
          salat: Math.floor(Math.random() * 5)
        });

        currentDate.setDate(currentDate.getDate() + 1);
      }

      this.orders = orders;
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString("de-DE", {
        weekday: "long",
        day: "2-digit",
        month: "2-digit",
        year: "numeric"
      });
    },


    async fetchOrders() {
      let date = new Date();
      for (let i = 0; i < 14; i++) {
        let orderCount = { date: this.formatDate(date), essen1: 0, essen2: 0, salat: 0 };
        const resp = await api.bestellungen.getBestellungenForStandortAndDate(this.standort, date.toISOString().split("T")[0]);
        const bestellungen = await resp.json();
        for (const bestellung of bestellungen) {
          const order = decodeEssen(bestellung.essenswahl);
          console.log(order);
          if (order.salat === "Ja") {
            orderCount.salat++;
          }
          if (order.essen === "Essen 1") {
            orderCount.essen1++;
          }
          if (order.essen === "Essen 2") {
            orderCount.essen2++;
          }
        }
        this.orders.push(orderCount);
        date.setDate(date.getDate() + 1);
      }
    },

    generatePDF() {
      const doc = new jsPDF();
      const currentTime = new Date();
      const formattedTime = currentTime.toLocaleTimeString("de-DE", { hour: "2-digit", minute: "2-digit" });
      const formattedDate = currentTime.toLocaleDateString("de-DE");

      // Firmenlogo hinzufügen
      doc.addImage(Firmenlogo, "PNG", 10, 10, 39.5, 11.3);

      // Titel
      doc.setFont("helvetica", "bold");
      doc.setFontSize(22);
      doc.text("Bestellübersicht", 105, 35, { align: "center" });
      doc.setFont("helvetica", "normal");
      doc.setFontSize(18);
      doc.text(`für die nächsten 14 Tage`, 105, 45, { align: "center" });

      // Standort
      doc.setFontSize(14);
      doc.text(`Standort: ${this.standort}`, 20, 60);

      // Horizontale Linie
      doc.line(20, 65, 190, 65);

      // **TABELLE**
      autoTable(doc, {
        startY: 70, // Startposition der Tabelle
        head: [["Datum", "Essen 1", "Essen 2", "Salat"]], // Tabellenkopf
        body: this.orders.map(order => [
          order.date,
          order.essen1,
          order.essen2,
          order.salat,
        ]),
        theme: "grid",
        styles: {
          fontSize: 10,
          cellPadding: 3.5,
          halign: "center",
        },
        alternateRowStyles: {
          fillColor: [224, 221, 221], // **Jede zweite Zeile leicht grau**
        },
        headStyles: {
          fillColor: [51, 51, 51], // Dunkelgrau für Tabellenkopf
          textColor: [255, 255, 255], // Weißer Text
          fontSize: 12, // Größere Schrift für Überschriften
        },
      });

      // Stand (Datum & Uhrzeit)
      doc.setFontSize(12);
      doc.text(`Stand: ${formattedDate}, ${formattedTime} Uhr`, 135, doc.lastAutoTable.finalY + 15);

      // PDF speichern
      doc.save(`Bestelluebersicht_${formattedDate}.pdf`);
    }
  }
};
</script>

<style scoped>
.container {
  text-align: center;
  padding: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th,
td {
  border: 1px solid #000;
  padding: 10px;
  text-align: center;
}

th {
  background-color: #333;
  color: white;
}

tbody tr:nth-child(even) {
  background-color: #e0dddd;
}

button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
