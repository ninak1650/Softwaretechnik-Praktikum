<template>

    <div class="login-view">
        <h1 class="appTitle">Bor'na Lecker!</h1>
        <LoginComponent />
    </div>
</template>

<script>
import LoginComponent from "@/components/LoginComponent.vue";

export default {
    name: "loginView",
    components: {
        LoginComponent
    },
};
</script>

<style scoped>
/* App-Titel */
.appTitle {
    font-size: 48px;
    font-weight: bold;
    color: #3273dc;
    margin-bottom: 24px;
}

.login-view {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    /* 100% der Bildschirmhöhe */
    background-color: #f4f4f4;
    flex-direction: column;
}
</style>
