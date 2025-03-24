<template>
    <BareboneNavbar />

    <br>
    <b-button class="button is-light" @click="goBack" :style="{ marginLeft: '20px' }">Zurück</b-button>

    <div class="block">
        <h1 class="has-text-weight-bold is-size-4 has-text-centered">
            <span v-if="!kundenID">Kunde Hinzufügen:</span>
            <span v-else>Kunde Bearbeiten:</span>
        </h1>
    </div>

    <div class="block has-text-centered input-field" v-if="kundenID">
        <b-field label="Kundennummer" label-position="on-border">
            <b-input v-model="kunde.kundenNummer" aria-label="KundenNummer" disabled /></b-field>
    </div>
    <div class="block has-text-centered input-field">
        <b-field label="Name" label-position="on-border">
            <b-input v-model="kunde.name" aria-label="Name" placeholder="Geben sie den vollen Namen ein" /></b-field>
    </div>

    <div class="block has-text-centered input-field">
        <b-field label="Standort" label-position="on-border">
            <b-select v-model="kunde.standort" aria-label="Standort" placeholder="Wählen sie den Standort aus" expanded>
                <option v-for="standort in standorte" :key="standort" :value="standort">
                    {{ standort }}
                </option>
            </b-select>
        </b-field>
    </div>

    <div class="block has-text-centered input-field">
        <b-field label="Gruppe" label-position="on-border">
            <b-select v-model="kunde.gruppenNummer" aria-label="Gruppe" placeholder="Wählen sie die Gruppe aus"
                expanded>
                <option v-for="gruppe in filteredGruppen" :key="gruppe.gruppenNummer" :value="gruppe.gruppenNummer">
                    {{ gruppe.gruppenName }}
                </option>
            </b-select>
        </b-field>
    </div>

    <div class="floating-button m-5 has-text-centered">
        <b-button v-if="kundenID" class="button is-small is-link" icon-left="check" @click="save">
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
        }" icon-left="delete" @click="deleteKunde">
            <span class="has-text-weight-bold">Kunde Löschen</span>
        </b-button>
    </div>

</template>

<script>
import { api } from '@/api/api';
import BareboneNavbar from '../../components/navbars/BareboneNavbar.vue';

export default {
    name: 'Kunde',

    data() {
        return {
            kunde: {},
            standorte: [],
            gruppen: [],
        }
    },

    mounted() {
        this.readStandorte();
        this.readGruppen();
        this.readKunde();

        const { standort, gruppe } = this.$route.query;
        if (standort) {
            this.kunde.standort = standort;
        }
        if (gruppe) {
            this.kunde.gruppenNummer = gruppe;
        }
    },
    computed: {
        filteredGruppen() {
            return this.gruppen.filter(gruppe => gruppe.standort === this.kunde.standort);
        }
    },

    props: ["kundenID"],

    components: {
        BareboneNavbar,
    },

    methods: {
        goBack() {
            this.$router.go(-1);
        },

        async save() {
            if (this.kundenID) {
                await this.updateKunde();
            } else {
                await this.createKunde();
            }
        },

        async updateKunde() {
            const response = await api.kunden.updateKunde(this.kundenID, this.kunde.name, this.kunde.standort, this.kunde.gruppenNummer);

            if (response.ok) {
                this.$router.push(`/standort/${this.kunde.standort}`);
            }
        },

        async createKunde() {

            const response = await api.kunden.createKunde(this.kunde.name, this.kunde.standort, this.kunde.gruppenNummer);

            if (response.ok) {
                this.$router.push(`/standort/${this.kunde.standort}`);
            }
        },

        async deleteKunde() {
            // confirm dialog
            if (!confirm('Sind Sie sicher, dass Sie diesen Kunden löschen möchten?')) {
                return;
            }

            const response = await api.kunden.deleteKunde(this.kundenID);

            if (response.ok) {
                this.$router.push(`/standort/${this.kunde.standort}`);
            }
        },

        async readKunde() {
            if (!this.kundenID) {
                return;
            }
            const response = await api.kunden.getKunde(this.kundenID);
            if (response.ok) {
                this.kunde = await response.json();
                this.kunde.gruppenNummer = this.kunde.gruppe.gruppenNummer;
            }
        },

        async readStandorte() {
            const response = await api.standorte.getStandorte();
            if (response.ok) {
                this.standorte = await response.json();
            }
        },

        async readGruppen() {
            const response = await api.gruppen.getGruppen();
            if (response.ok) {
                this.gruppen = await response.json();
            }
        }


    },
};

</script>

<style scoped>
.input-field {
    margin: 0 auto;
    width: 33%;
}
</style>