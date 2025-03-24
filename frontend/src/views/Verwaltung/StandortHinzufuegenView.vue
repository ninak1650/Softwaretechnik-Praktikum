<template>
  <BareboneNavbar />

  <br>

  <div class="block">
    <h1 class="has-text-weight-bold is-size-4 has-text-centered">Standort hinzufügen:</h1>
    <b-button class="button is-light" @click="goBack" :style="{ marginLeft: '20px' }">Zurück</b-button>
  </div>

  <div class="block has-text-centered">
    <input class="input is-link" type="text" placeholder="Standortname eingeben" style="width: 35%; color: #333;"
      @keydown.enter="focusNext('gruppenNameInput')" ref="standortNameInput" />
  </div>

  <br>

  <h1 class="has-text-weight-normal has-text-centered is-danger"> Um einen Standort zu erstellen muss mindestens eine
    Gruppe erstellt werden!</h1>

  <br>

  <div class="block has-text-centered">
    <form class="box" style="width: 35%; margin: 0 auto;" @submit.prevent>
      <h1 class="has-text-weight-bold">Gruppe erstellen</h1>
      <br>
      <div class="field">
        <label class="label">Gruppenname</label>
        <div class="control">
          <input class="input is-link" type="text" placeholder="Name eingeben"
            @keydown.enter="focusNext('gruppenNummerInput')" ref="gruppenNameInput" />
        </div>
      </div>
      <br>
      <button type="button" class="button is-primary is-small">Hinzufügen</button>
    </form>
  </div>

  <br>

  <div class="floating-button m-5 has-text-centered">
    <b-button class="button is-small is-link" :style="{
    }" icon-left="plus" @click="createStandort">
      <span class="has-text-weight-bold">Hinzufügen</span>
    </b-button>
  </div>


</template>

<script>
import { api } from '@/api/api';
import { Rolle } from '@/util/rolle';
import BareboneNavbar from '../../components/navbars/BareboneNavbar.vue';

export default {
  name: 'StandortHinzufuegen',

  components: {
    BareboneNavbar,
  },

  data() {
    return {
      searchQuery: '',
      gruppenleiter: [],
      selectedGruppenleiter: null,
    };
  },

  methods: {
    goBack() {
      this.$router.go(-1);
    },

    navigateTo(route) {
      if (route) {
        this.$router.push(route);
      }
    },

    focusNext(refName) {
      this.$refs[refName].focus();
    },

    async fetchGruppenleiter() {
      try {
        const reponse = await api.accounts.getAccountsWithRolle(this.gruppenleiter, Rolle.GRUPPENLEITUNG);

        if (!response.ok) {
          throw new Error(`Konnte Gruppenleiter nicht finden: ${response.statusText}`);
        }
        this.gruppenleiter = await response.json();
      } catch (error) {
        console.error("Gruppenleiter konnten nicht geladen werden", error);
      }
    },
    selectGruppenleiter(gruppenleiter) {
      this.selectedGruppenleiter = gruppenleiter;
      this.searchQuery = gruppenleiter.benutzername;

    }

  },
  mounted() {
    this.fetchGruppenleiter();
  },
};

</script>
