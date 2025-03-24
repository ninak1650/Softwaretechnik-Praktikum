<template>
  <BareboneNavbar />
  <div class="hero is-small">
    <div class="hero-body">
      <b-button class="button is-light" @click="navigateTo('/verwaltung')"
        :style="{ marginLeft: '20px' }">Zurück</b-button>
      <section class="has-text-centered">

        <h1 class="tag is-link has-text-weight-bold is-size-3">
          Standort: {{ standortName }}
        </h1>
        <br>
        <br>
        <br>
        <br>

        <div class="box">
          <span class="has-text-weight-bold is-size-5">Gruppenleiter aus {{ standortName }}</span>
          <table class="table is-fullwidth">
            <thead>
              <tr>
                <th></th>
                <th>Gruppennummer</th>
                <th>Gruppenname</th>
                <th>Name</th>
                <th>Benutzername</th>
                <th>QR-Code</th>
                <th>Aktionen</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="gruppe in gruppen" :key="gruppe.gruppenNummer">
                <td v-if="gruppe.account"></td>
                <td v-else><b-icon icon="alert" style="color: orange;"
                    title="Dieses Benutzerkonto wurde noch nicht erstellt" /></td>
                <td>{{ gruppe.gruppenNummer }}</td>
                <td>{{ gruppe.gruppenName }}</td>
                <td>{{ gruppe.account?.nachname }}</td>
                <td>{{ gruppe.account?.benutzername }}</td>
                <td>
                  <b-icon icon="qrcode" type="is-left is-link" />
                  <a @click="triggerExport(gruppe, gruppe.account?.nachname)"> QR-Code</a>
                </td>
                <td v-if="gruppe.account">
                  <b-icon icon="account-cog" type="is-left is-link" />
                  <a @click="navigateTo(`/konto/${gruppe.account?.benutzername}`)"> Konto bearbeiten</a>
                </td>
                <td v-else>
                  <b-icon icon="account-plus" type="is-left is-link" />
                  <a
                    @click="navigateTo(`/konto?standort=${standortName}&gruppe=${gruppe.gruppenNummer}&rolle=GRUPPENLEITUNG`)">
                    Neues Konto hinzufügen</a>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <br>
        <br>
        <h1 class="has-text-weight-bold is-size-4 has-text-centered">
          Gruppen
        </h1>
        <br>
        <br>

        <div v-for="gruppe in gruppen" :key="gruppe.gruppenNummer" class="box">
          <span class="has-text-weight-bold is-size-5">{{ gruppe.gruppenName }}</span>
          <br>
          <span class="has-text-weight-bold is-size-5" v-if="gruppe.account">Gruppenleitung: {{
            gruppe.account?.nachname }}</span>
          <span class="has-text-weight-bold is-size-5" v-else><b-icon icon="alert" style="color: orange;"
              title="Dieses Benutzerkonto wurde noch nicht erstellt" /> Gruppenleitung: Kein Account</span>
          <br>
          <span class="has-text-weight-bold is-size-5">Gruppennummer: {{ gruppe.gruppenNummer }}</span>
          <br>
          <table class="table is-fullwidth">
            <thead>
              <tr>
                <th>Kundennummer</th>
                <th>Name</th>
                <th>QR-Code</th>
                <th>Aktionen</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="kunde in gruppe.kunden" :key="kunde.kundennummer">
                <td>{{ kunde.kundenNummer }}</td>
                <td>{{ kunde.name }}</td>
                <td>
                  <b-icon icon="qrcode" type="is-left is-link" />
                  <a @click="triggerExport(kunde, kunde.name)"> QR-Code</a>
                </td>
                <td>
                  <b-icon icon="account-cog" type="is-left is-link" />
                  <a @click="navigateTo(`/kunde/${kunde.kundenNummer}`)">
                    Kundendaten Bearbeiten/Löschen</a>
                </td>
              </tr>
            </tbody>
          </table>

          <div class="group-actions">
            <div class="action">
              <b-button class="button is-small is-link" icon-left="account-plus"
                @click="navigateTo(`/kunde?standort=${standortName}&gruppe=${gruppe.gruppenNummer}`)">
                <span class="has-text-weight-bold">Kunde hinzufügen</span>
              </b-button>
            </div>

            <div class="action">
              <b-button class="button is-small is-link" icon-left="qrcode"
                @click="triggerExport(gruppe, gruppe.gruppenName)">
                <span class="has-text-weight-bold">QR-Codes Gruppe</span>
              </b-button>
            </div>

            <div class="action">
              <b-button class="button is-small is-link" icon-left="account-group"
                @click="navigateTo(`/gruppe/${gruppe.gruppenNummer}`)">
                <span class="has-text-weight-bold">Gruppe Bearbeiten</span>
              </b-button>
            </div>
            <br>
          </div>
        </div>

        <br>

        <div class="floating-button">
          <b-button class="button is-small is-link" :style="{
            position: 'fixed',
            bottom: '20px',
            right: '20px',
            zIndex: '1000',
          }" icon-left="account-multiple-plus" @click="navigateTo(`/gruppe?standort=${standortName}`)">
            <span class="has-text-weight-bold">Gruppe hinzufügen</span>
          </b-button>
        </div>

      </section>
    </div>
  </div>




</template>

<script>
import { api } from "@/api/api";
import { Rolle } from "@/util/rolle";
import BareboneNavbar from "../../components/navbars/BareboneNavbar.vue";
import QRCodesErstellenView from "./QRCodesErstellenView.vue";


export default {
  name: "StandortView",
  components: {
    BareboneNavbar,
    QRCodesErstellenView
  },

  props: ["standortName"],

  data() {
    return {
      gruppen: [],
      activeTab: 0,
      kundennummerList: [],
      Rolle
    };
  },

  mounted() {
    this.fetchGruppen();
    this.fetchKunden();
  },

  methods: {

    navigateTo(route) {
      if (route) {
        this.$router.push(route);
      }
    },

    async fetchGruppen() {
      if (!this.standortName) return;

      try {
        const response = await api.gruppen.getGruppenForStandort(this.standortName);
        const accs = await api.accounts.getAccounts();

        if (!response.ok || !accs.ok) {
          throw new Error(
            `Error fetching Gruppen: ${response.statusText ? response.statusText : accs.statusText}`
          );
        }

        this.gruppen = await response.json();
        let accounts = await accs.json();
        accounts = accounts.filter((acc) => acc.rolle === Rolle.GRUPPENLEITUNG || acc.rolle === Rolle.KÜCHE);
        for (const gruppe of this.gruppen) {
          gruppe.account = accounts.find((acc) => acc.gruppe.gruppenNummer === gruppe.gruppenNummer);
        }

        console.log("Gruppen:", this.gruppen);


        for (const gruppe of this.gruppen) {
          await this.fetchKunden(gruppe);
        }
      } catch (error) {
        console.error("Gruppen konnten nicht geladen werden");
        console.log(error)
      }
    },

    async fetchKunden(gruppe) {

      try {
        const response = await api.gruppen.getGruppenMitglieder(gruppe.gruppenNummer);

        if (!response.ok) {
          throw new Error(`Error fetching Kunden: ${response.statusText}`);
        }

        const kunden = await response.json();
        gruppe.kunden = kunden;



        console.log(`Kunden für Gruppe ${gruppe.gruppenNummer}:`, gruppe.kunden);
      } catch (error) {
        console.error("Kundendaten konnten nicht geladen werden");
      }
    },



    //QR code generieren und als pdf exportieren
    triggerExport(gruppe, gruppenname) {
      let customerList = [];


      if (gruppe.kunden && (!gruppe.account || gruppenname != gruppe.account.nachname)) {
        customerList = gruppe.kunden.map((kunde) => ({
          kundeID: kunde.kundenNummer,
          name: kunde.name,
          qrData: `bornalecker-kunde-${kunde.kundenNummer}`
        }));
      }
      if (gruppe.account) {
        customerList.unshift({
          kundeID: gruppe.account.benutzername,
          name: gruppe.account.nachname,
          qrData: `bornalecker-account-${gruppe.account.benutzername}`
        });
      }
      if (gruppe.kundenNummer) {
        customerList.push({
          kundeID: gruppe.kundenNummer,
          name: gruppe.name,
          qrData: `bornalecker-kunde-${gruppe.kundenNummer}`,
        });
      }

      if (customerList.length === 0) {
        return;
      }

      this.$router.push({
        name: "QRerstellen",
        params: { gruppenname },
        state: { kundennummerList: customerList },
      });
    }
  },

  watch: {
    standortName() {
      this.fetchGruppen();
    },
  },
};
</script>

<style scoped>
.table td {
  text-align: left;
}

.group-actions {
  display: flex;
  justify-content: center;
}

.action {
  margin: 0 10px;
}
</style>