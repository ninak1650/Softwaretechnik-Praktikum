<template>
    <b-navbar type="is-light" shadow transparent fixed-top>
        <template #brand>
            <b-navbar-item>
                <!-- Material Design Icon anstelle des Buefy-Logos -->
                <b-icon icon="food" size="is-large" alt=""></b-icon>
            </b-navbar-item>
        </template>

        <!--
        <template #start>
            <b-navbar-item active>
                <div class="buttons">
                    <b-button
                        class="button1 is-primary is-light is-rounded is-outlined custom-button"
                        size="is-medium"
                        tag="router-link" :to="{ path: '/' }"
                    >
                        home
                    </b-button>
                </div>
            </b-navbar-item>
        </template>
        -->

        <template #end>
            <b-navbar-item>
                <b-navbar-dropdown :label="username" class="plsstayonscreen">
                    <b-navbar-item @click="navigateTo(`/konto/${username}/passwort`)" class="navbar-item-flex" tabindex="0"><b-icon
                            icon="key" size="is-small" style="margin-right: 4px;" />
                        Passwort ändern</b-navbar-item>
                    <b-navbar-item @click="logout" class="navbar-item-flex" tabindex="0"><b-icon icon="logout" size="is-small"
                            style="margin-right: 4px;" />
                        Logout</b-navbar-item>
                </b-navbar-dropdown>
            </b-navbar-item>
        </template>
    </b-navbar>
</template>

<script>
import { getUsernameFromToken } from '@/util/jwt';

export default {
    name: "GruppenleitungNavbar",

    data() {
        return {
            username: getUsernameFromToken(),
        };
    },

    methods: {
        navigateTo(path) {
            this.$router.push(path);
        },

        logout() {
            localStorage.removeItem('token');
            this.$router.push('/');
        },
    },
};
</script>

<style>
.navbar-item-flex {
    display: flex;
    align-items: center;
}

.navbar-dropdown {
    right: 0;
    left: auto !important;
}
</style>
