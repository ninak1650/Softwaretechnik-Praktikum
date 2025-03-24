<template>

    <BareboneNavbar />

    <br>
    <b-button class="button is-light" @click="goBack" :style="{ marginLeft: '20px' }">Zurück</b-button>

    <div class="block">
        <h1 class="has-text-weight-bold is-size-4 has-text-centered">
            <span v-if="!username">Konto Hinzufügen:</span>
            <span v-else>Konto Bearbeiten:</span>
        </h1>
    </div>

    <div class="block has-text-centered input-field">
        <b-field label="Nutzername" label-position="on-border" v-if="!username">
            <b-input v-model="account.benutzername" aria-label="Nutzername"
                placeholder="Geben Sie einen neuen Nutzernamen ein" />
        </b-field>
        <b-field label="Nutzername" label-position="on-border" v-else>
            <b-input v-model="account.benutzername" aria-label="Nutzername" disabled />
        </b-field>
    </div>

    <div class="block has-text-centered input-field" v-if="!username">
        <b-field label="Initialpasswort" label-position="on-border">
            <b-input v-model="account.password" aria-label="Password"
                placeholder="Geben Sie ein neues Passwort ein" /></b-field>
    </div>

    <div class="block has-text-centered input-field">
        <b-field label="Name" label-position="on-border">
            <b-input v-model="account.nachname" aria-label="Name"
                placeholder="Geben Sie den vollen Namen ein" /></b-field>
    </div>

    <div class="block has-text-centered input-field">
        <b-field label="Rolle" label-position="on-border">
            <b-select v-model="account.rolle" aria-label="Rolle" placeholder="Wählen Sie die Rolle des Accounts aus"
                expanded>
                <option v-for="rolle in rollen" :key="rolle" :value="rolle">
                    {{ rolle }}
                </option>
            </b-select>
        </b-field>
    </div>

    <div class="block has-text-centered input-field" v-show="canSelectStandort">
        <b-field label="Standort" label-position="on-border">
            <b-select v-model="account.standort" aria-label="Standort" placeholder="Wählen Sie einen Standort aus"
                expanded>
                <option v-for="standort in standorte" :key="standort" :value="standort">
                    {{ standort }}
                </option>
            </b-select>
        </b-field>
    </div>

    <div class="block has-text-centered input-field" v-show="canSelectGruppe">
        <b-field label="Gruppe" label-position="on-border">
            <b-select v-model="account.gruppenNummer" aria-label="Gruppe" placeholder="Wählen Sie eine Gruppe aus"
                expanded>
                <option v-for="gruppe in filteredGruppen" :key="gruppe.gruppenNummer" :value="gruppe.gruppenNummer">
                    {{ gruppe.gruppenName }}
                </option>
            </b-select>
        </b-field>
    </div>

    <div class="block has-text-centered input-field" v-if="error">
        <b-message type="is-danger">
            {{ error }}
        </b-message>
    </div>


    <div class="floating-button m-5 has-text-centered">
        <b-button v-if="username" class="button is-small is-link" icon-left="check" @click="save">
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
        }" icon-left="delete" @click="deleteAccount">
            <span class="has-text-weight-bold">Konto Löschen</span>
        </b-button>
    </div>


</template>

<script>
import { api } from '@/api/api';
import { Rolle } from '@/util/rolle';
import BareboneNavbar from '../../components/navbars/BareboneNavbar.vue';


export default {
    name: 'KontoHinzufuegen',

    components: {
        BareboneNavbar,
    },

    props: ["username"],

    data() {
        return {
            account: {},
            standorte: [],
            gruppen: [],
            rollen: Rolle,
            canSelectGruppe: false,
            canSelectStandort: false,
            error: '',
        };
    },

    mounted() {
        this.readStandorte();
        this.readGruppen();
        this.readAccount();


        const { standort, gruppe, rolle } = this.$route.query;
        if (rolle) {
            this.account.rolle = Rolle[rolle];
        }
        if (standort) {
            this.account.standort = standort;
        }
        if (gruppe) {
            this.account.gruppenNummer = gruppe;
        }

    },

    computed: {
        filteredGruppen() {
            let gruppen = []
            if (this.canSelectGruppe) {
                gruppen = this.gruppen.filter(gruppe => gruppe.standort === this.account.standort);
            }
            return gruppen;
        },
    },

    watch: {
        'account.rolle': function (newVal) {
            this.canSelectGruppe = newVal === Rolle.GRUPPENLEITUNG || newVal === Rolle.KÜCHE;
            this.canSelectStandort = newVal !== Rolle.VERWALTUNG;

        },
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
        },

        async save() {
            if (!this.canSelectGruppe) {
                this.account.gruppenNummer = 0;
            }
            if (!this.canSelectStandort) {
                this.account.standort = '';
            }
            if (this.account.benutzername === '') {
                this.error = 'Nutzername darf nicht leer sein';
                return
            }
            if (this.account.nachname === '') {
                this.error = 'Name darf nicht leer sein';
                return
            }
            if (!this.username && this.account.password === '') {
                this.error = 'Passwort darf nicht leer sein';
                return
            }
            if (!this.username && this.account.password?.length < 8) {
                this.error = 'Passwort muss mindestens 8 Zeichen lang sein';
                return
            } else {
                this.error = '';
            }
            if (this.username) {
                await this.updateAccount();
            } else {
                await this.createAccount();
            }
        },

        async createAccount() {
            const response = await api.accounts.createAccount(this.account.benutzername, this.account.password, this.account.nachname, this.account.rolle, this.account.standort, this.account.gruppenNummer);

            if (response.ok) {
                this.goBack();
            } else {
                this.error = await response.text();
            }
        },

        async updateAccount() {
            const response = await api.accounts.updateAccount(this.account.benutzername, this.account.nachname, this.account.rolle, this.account.standort, this.account.gruppenNummer);

            if (response.ok) {
                this.goBack();
            } else {
                this.error = await response.text();
            }
        },

        async deleteAccount() {

            if (!confirm('Sind Sie sicher, dass Sie dieses Konto löschen möchten?')) {
                return;
            }

            const response = await api.accounts.deleteAccount(this.account.benutzername);

            if (response.ok) {
                this.$router.push(`/standort/${this.account.standort}`)
            } else {
                this.error = await response.text();
            }
        },

        async readAccount() {
            if (!this.username) {
                this.account = {
                    benutzername: '',
                    password: crypto.randomUUID().split('-')[0],
                    nachname: '',
                    rolle: Rolle.VERWALTUNG,
                    standort: '',
                    gruppenNummer: 0,
                };
                return;
            }
            const response = await api.accounts.getAccount(this.username);
            if (response.ok) {
                this.account = await response.json();
                this.account.gruppenNummer = this.account.gruppe.gruppenNummer;
                this.canSelectGruppe = this.account.rolle === Rolle.GRUPPENLEITUNG || this.account.rolle === Rolle.KÜCHE;
                this.canSelectStandort = this.account.rolle !== Rolle.VERWALTUNG;
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