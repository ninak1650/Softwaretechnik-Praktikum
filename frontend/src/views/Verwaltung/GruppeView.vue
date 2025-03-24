<template>
    <BareboneNavbar />

    <br>
    <b-button class="button is-light" @click="goBack" :style="{ marginLeft: '20px' }">Zurück</b-button>

    <div class="block">
        <h1 class="has-text-weight-bold is-size-4 has-text-centered">
            <span v-if="!gruppenNummer">Gruppe Hinzufügen:</span>
            <span v-else>Gruppe Bearbeiten:</span>
        </h1>
    </div>

    <div class="block has-text-centered input-field" v-if="gruppenNummer">
        <b-field label="Gruppennummer" label-position="on-border">
            <b-input v-model="gruppe.gruppenNummer" aria-label="Gruppennummer" disabled /></b-field>
    </div>
    <div class="block has-text-centered input-field" v-else>
        <b-field label="Gruppennummer" label-position="on-border">
            <b-input v-model="gruppe.gruppenNummer" aria-label="Gruppennummer"
                placeholder="Bitte neue Gruppennummer eingeben" /></b-field>
    </div>

    <div class="block has-text-centered input-field">
        <b-field label="Name" label-position="on-border">
            <b-input v-model="gruppe.gruppenName" aria-label="Name"
                placeholder="Geben sie den vollen Namen ein" /></b-field>
    </div>

    <div class="block has-text-centered input-field">
        <b-field label="Standort" label-position="on-border">
            <b-select v-model="gruppe.standort" aria-label="Standort" placeholder="Wählen sie den Standort aus"
                expanded>
                <option v-for="standort in standorte" :key="standort" :value="standort">
                    {{ standort }}
                </option>
            </b-select>
        </b-field>
    </div>

    <div class="floating-button m-5 has-text-centered">
        <b-button v-if="gruppenNummer" class="button is-small is-link" icon-left="check" @click="save">
            <span class="has-text-weight-bold">Änderungen Speichern</span>
        </b-button>
        <b-button v-else class="button is-small is-link" icon-left="plus" @click="save">
            <span class="has-text-weight-bold">Hinzufügen</span>
        </b-button>
    </div>

    <div class="floating-button">
        <b-button class="button is-small is-link" :style="{
            position: 'fixed',
            bottom: '20px',
            right: '20px',
            zIndex: '1000',
        }" icon-left="delete" @click="deleteGruppe">
            <span class="has-text-weight-bold">Gruppe Löschen</span>
        </b-button>
    </div>

</template>

<script>
import { api } from '@/api/api';
import BareboneNavbar from '../../components/navbars/BareboneNavbar.vue';

export default {
    name: 'Gruppe',

    data() {
        return {
            gruppe: {},
            standorte: [],
        }
    },

    mounted() {
        this.readStandorte();
        this.readGruppe();

        const { standort } = this.$route.query;
        if (standort) {
            this.gruppe.standort = standort;
        }
    },

    props: ["gruppenNummer"],

    components: {
        BareboneNavbar,
    },

    methods: {
        goBack() {
            this.$router.go(-1);
        },

        async save() {
            if (this.gruppenNummer) {
                await this.updateGruppe();
            } else {
                await this.createGruppe();
            }
        },

        async updateGruppe() {
            const response = await api.gruppen.updateGruppe(this.gruppe.gruppenNummer, this.gruppe.gruppenName, this.gruppe.standort, null);

            if (response.ok) {
                this.$router.push(`/standort/${this.gruppe.standort}`);
            }
        },

        async createGruppe() {
            const response = await api.gruppen.createGruppe(this.gruppe.gruppenNummer, this.gruppe.gruppenName, this.gruppe.standort, null);

            if (response.ok) {
                this.$router.push(`/standort/${this.gruppe.standort}`);
            }
        },

        async deleteGruppe() {
            // confirm dialog
            if (!confirm('Sind Sie sicher, dass Sie dise Gruppe löschen möchten?')) {
                return;
            }

            const response = await api.gruppen.deleteGruppe(this.gruppenNummer);

            if (response.ok) {
                this.$router.push(`/standort/${this.gruppe.standort}`);
            }
        },

        async readGruppe() {
            if (!this.gruppenNummer) {
                return;
            }
            const response = await api.gruppen.getGruppe(this.gruppenNummer);
            if (response.ok) {
                this.gruppe = await response.json();
            }
        },

        async readStandorte() {
            const response = await api.standorte.getStandorte();
            if (response.ok) {
                this.standorte = await response.json();
            }
        },
    },
};

</script>

<style scoped>
.input-field {
    margin: 0 auto;
    width: 33%;
}
</style>