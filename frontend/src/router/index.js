import GruppenleitungKuecheView from "@/views/GruppenleitungKuecheView.vue";
import GruppenleitungNormalView from "@/views/GruppenleitungNormalView.vue";
import LoginView from "@/views/LoginView.vue";
import PasswortView from "@/views/PasswortView.vue";
import StandortleitungView from "@/views/StandortleitungView.vue";
import GruppeView from "@/views/Verwaltung/GruppeView.vue";
import KontoView from "@/views/Verwaltung/KontoView.vue";
import KundeView from "@/views/Verwaltung/KundeView.vue";
import QRCodesErstellenView from "@/views/Verwaltung/QRCodesErstellenView.vue";
import StandortHinzufuegenView from "@/views/Verwaltung/StandortHinzufuegenView.vue";
import StandortView from "@/views/Verwaltung/StandortView.vue";
import VerwaltungView from "@/views/Verwaltung/VerwaltungView.vue";
import { createRouter, createWebHistory } from "vue-router";
import BestellungAufnehmenView from "../views/BestellungAufnehmenView.vue";
import BestellungBearbeitenView from "../views/BestellungBearbeitenView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/",
            name: "Login",
            component: LoginView,
        },
        {
            path: "/verwaltung",
            name: "Verwaltung",
            component: VerwaltungView,
        },
        {
            path: "/standortleitung",
            name: "Standortleitung",
            component: StandortleitungView,
        },
        {
            path: "/gruppenleitungNormal",
            name: "GruppenleitungNormal",
            component: GruppenleitungNormalView,
        },
        {
            path: "/gruppenleitungKueche",
            name: "GruppenleitungKueche",
            component: GruppenleitungKuecheView,
        },
        {
            path: "/BestellungAufnehmen",
            name: "BestellungAufnehmen",
            component: BestellungAufnehmenView,
        },
        {
            path: "/BestellungBearbeiten/:ID",
            name: "BestellungBearbeiten",
            component: BestellungBearbeitenView,
            props: (route) => ({ ID: route.params.ID }),
        },
        {
            path: "/standort/:standortName",
            name: "Standort",
            component: StandortView,
            props: true,
        },
        {
            path: "/standortHinzufuegen",
            name: "StandortHinzufuegen",
            component: StandortHinzufuegenView,
        },
        {
            path: "/konto",
            name: "NeuesKonto",
            component: KontoView,
            props: () => ({ username: null }),
        },
        {
            path: "/konto/:username",
            name: "Konto",
            component: KontoView,
            props: (route) => ({ username: route.params.username }),
        },
        {
            path: "/konto/:username/passwort",
            name: "Passwort",
            component: PasswortView,
            props: (route) => ({ username: route.params.username }),
        },
        {
            path: "/qr/:gruppenname",
            name: "QRerstellen",
            component: QRCodesErstellenView,
            props: (route) => ({ gruppenname: route.params.gruppenname }),
        },
        {
            path: "/kunde",
            name: "NeuerKunde",
            component: KundeView,
            props: () => ({ kundenID: null }),
        },
        {
            path: "/kunde/:kundenID",
            name: "Kunde",
            component: KundeView,
            props: (route) => ({ kundenID: route.params.kundenID }),
        },
        {
            path: "/gruppe",
            name: "NeueGruppe",
            component: GruppeView,
            props: () => ({ gruppenNummer: null }),
        },
        {
            path: "/gruppe/:gruppenNummer",
            name: "Gruppe",
            component: GruppeView,
            props: (route) => ({ gruppenNummer: route.params.gruppenNummer }),
        },
    ],
});

router.beforeEach((to, from, next) => {
    document.body.classList.remove("login-page", "home-page", "custom-view");
    if (to.name === "Login") {
        document.body.classList.add("login-page");
    }

    if (to.name === "Essensbestellung") {
        document.body.classList.add("essensbestellung");
    }

    if (to.name === "Bestelluebersicht") {
        document.body.classList.add("bestelluebersicht");
    }

    next();
});

export default router;
