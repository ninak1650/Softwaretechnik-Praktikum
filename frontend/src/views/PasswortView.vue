<template>

    <BareboneNavbar />

    <br>
    <b-button class="button is-light" @click="goBack" :style="{ marginLeft: '20px' }">Zurück</b-button>

    <div class="block">
        <h1 class="has-text-weight-bold is-size-4 has-text-centered">Passwort ändern:</h1>
    </div>

    <div class="block has-text-centered input-field" v-if="requireChange">
        <b-message type="is-warning">
            Sie müssen Ihr Passwort ändern, da Sie sich zum ersten Mal anmelden.
        </b-message>
    </div>

    <div class="block has-text-centered input-field">
        <b-field label="Nutzername" label-position="on-border">
            <b-input v-model="account.benutzername" aria-label="Nutzername" disabled />
        </b-field>
    </div>

    <div class="block has-text-centered input-field">
        <b-field label="Passwort" label-position="on-border">
            <b-input v-model="account.password" aria-label="Password" type="password" @keyup.enter="save"
                placeholder="Geben Sie ein neues Passwort ein" /></b-field>
    </div>

    <div class="block has-text-centered input-field">
        <b-field label="Passwort Wiederholen" label-position="on-border">
            <b-input v-model="account.passwordrpt" aria-label="Password" type="password" @keyup.enter="save"
                placeholder="Geben Sie ein neues Passwort ein" /></b-field>
    </div>

    <div class="block has-text-centered input-field">
        <b-message type="is-danger" v-if="iserror">
            Die Passwörter stimmen nicht überein.
        </b-message>
    </div>



    <div class="floating-button m-5 has-text-centered">
        <b-button class="button is-medium is-link" icon-left="check" @click="save">
            <span class="has-text-weight-bold">Änderungen Speichern</span>
        </b-button>
    </div>


</template>

<script>
import { api } from '@/api/api';
import BareboneNavbar from '@/components/navbars/BareboneNavbar.vue';
import { getRequirePasswordChangeFromToken, getUsernameFromToken } from '@/util/jwt';


export default {
    name: 'PasswortView',

    components: {
        BareboneNavbar,
    },

    props: ["username"],

    data() {
        return {
            account: {},
            iserror: false,
            requireChange: getRequirePasswordChangeFromToken(),
        };
    },

    mounted() {
        this.readAccount();
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


        async save() {
            if (this.account.password !== this.account.passwordrpt) {
                this.iserror = true;
                return;
            } else {
                this.iserror = false;
            }
            let user = getUsernameFromToken();
            if (user !== this.account.benutzername) {
                this.resetPassword();
            } else {
                this.changePassword();
            }
        },

        async changePassword() {
            const response = await api.accounts.changePassword(this.account.benutzername, this.account.password);
            if (response.ok) {
                this.goBack();
            }
        },

        async resetPassword() {
            const response = await api.accounts.resetPassword(this.account.benutzername, this.account.password);
            if (response.ok) {
                this.goBack();
            }
        },


        async readAccount() {
            const response = await api.accounts.getAccount(this.username);
            if (response.ok) {
                this.account = await response.json();
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