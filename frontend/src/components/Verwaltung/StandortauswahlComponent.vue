<template>
  <section class="section">
    <div class="container">
      <div class="columns is-centered">
        <div v-for="standort in standorte" class="column has-text-centered">
          <b-button class="box is-clickable is-large is-primary-100" icon-left="map-marker" :style="{
            width: '100%',
            height: '200px',
            maxWidth: '300px',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            margin: '0 auto',
          }" @click="navigateTo(`/standort/${standort}`)">
            <span class="has-text-weight-bold">{{ standort }}</span>
          </b-button>
          <br />
          <b-button class="button is-medium is-link mt-4" icon-left="qrcode" @click="triggerExport(standort)">
            Qr-Codes anzeigen
          </b-button>
        </div>
      </div>
    </div>
    <div class="floating-button">
      <b-button class="button is-medium is-link"
        :style="{ position: 'fixed', bottom: '20px', right: '20px', zIndex: '1000' }" icon-left="map-marker-plus"
        @click="navigateTo('/standortHinzufuegen')">
        <span class="has-text-weight-bold">Standort hinzufügen</span>
      </b-button>
    </div>
  </section>
</template>

<script>
import { api } from "@/api/api";
import { Rolle } from "@/util/rolle";
import QRCodesErstellenView from "../../views/Verwaltung/QRCodesErstellenView.vue";

export default {

  data() {
    return {
      standorte: [],
      kundennummerList: [],
      gruppen: [],
    };
  },
  mounted() {
    this.fetchStandorte();
    this.fetchGruppen();
  },
  name: 'StandortauswahlComponent',
  components: {
    QRCodesErstellenView
  },
  methods: {
    navigateTo(route) {
      if (route) {
        this.$router.push(route);
      }
    },
    async fetchStandorte() {
      console.log("fetching");
      const response = await api.standorte.getStandorte();
      this.standorte = await response.json();
    },


    async triggerExport(standort) {

      try {
        await this.fetchGruppen(standort);
        const customerList = this.gruppen.flatMap(gruppe => {

          let customerList = [];

          if (gruppe.leiterAccount) {
            customerList.push({
              kundeID: gruppe.leiterAccount.benutzername,
              name: gruppe.leiterAccount.nachname,
              qrData: `bornalecker-account-${gruppe.leiterAccount.benutzername}`,

            });
          }

          customerList = customerList.concat(gruppe.kunden.map(kunde => ({
            kundeID: kunde.kundenNummer,
            name: kunde.name,
            qrData: `bornalecker-kunde-${kunde.kundenNummer}`,
          })));
          return customerList;
        });

        this.$router.push({
          name: "QRerstellen",
          params: { gruppenname: standort },
          state: { kundennummerList: customerList }
        })


      } catch (error) {
        console.error("QR Code export fehlgeschlagen: ", error);
      }

    },

    async fetchGruppen(standort) {
      if (!standort) return;

      try {
        const response = await api.gruppen.getGruppenForStandort(standort);
        const accs = await api.accounts.getAccounts();

        if (!response.ok || !accs.ok) {
          throw new Error(`Error fetching Gruppen: ${response.statusText}`);
        }
        this.gruppen = await response.json();
        let accounts = await accs.json();
        accounts = accounts.filter((acc) => acc.rolle === Rolle.GRUPPENLEITUNG || acc.rolle === Rolle.KÜCHE);
        for (const gruppe of this.gruppen) {
          gruppe.leiterAccount = accounts.find((acc) => acc.gruppe.gruppenNummer === gruppe.gruppenNummer);
        }

        console.log("gruppen: ", this.gruppen);

        for (const gruppe of this.gruppen) {
          await this.fetchKunden(gruppe);
        }
      } catch (error) {
        console.error(`Gruppen für ${standort} konnten nicht gefunden werden: `, error);

      }

    },

    async fetchKunden(gruppe) {
      console.log(gruppe);
      try {
        const response = await api.gruppen.getGruppenMitglieder(gruppe.gruppenNummer);
        if (!response.ok) throw new Error(`Error fetching Gruppen: ${response.statusText}`);

        gruppe.kunden = await response.json();
      } catch (error) {
        console.error(`Kunden von Gruppe ${gruppe.gruppenNummer} konnten nicht gefunden werden: `, error);
      }
    },
  }

};
</script>
