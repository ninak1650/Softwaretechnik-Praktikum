import { buildQuery, ENDPOINTS } from "@/api/endpoints";
import { getDefaultHeaders } from "@/util/misc";

export const gruppen = {
    setVertretung: async function (gruppe, vertretung) {
        const response = await fetch(ENDPOINTS.GRUPPE_VERTRETUNG(gruppe), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: JSON.stringify({ vertretung }),
        });
        return response;
    },
    removeVertretung: async function (gruppe) {
        const response = await fetch(ENDPOINTS.GRUPPE_VERTRETUNG(gruppe), {
            method: "DELETE",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    getGruppe: async function (gruppe) {
        const response = await fetch(ENDPOINTS.GRUPPE(gruppe), {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    getGruppen: async function () {
        const response = await fetch(ENDPOINTS.GRUPPEN(), {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    getGruppenForAccount: async function (accountname) {
        const url =
            ENDPOINTS.GRUPPEN() +
            buildQuery({
                accountname: accountname,
            });

        const response = await fetch(url, {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    getGruppenForStandort: async function (standort) {
        const url = ENDPOINTS.GRUPPEN() + buildQuery({ standort: standort });

        const response = await fetch(url, {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    getGruppenMitglieder: async function (gruppe) {
        const response = await fetch(ENDPOINTS.GRUPPE_MITGLIEDER(gruppe), {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    createGruppe: async function (
        gruppenNummer,
        name,
        standort,
        gruppenleiter
    ) {
        const response = await fetch(ENDPOINTS.GRUPPEN(), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: JSON.stringify({
                gruppenNummer: gruppenNummer,
                gruppenName: name,
                standort: standort,
                gruppenleiter: gruppenleiter,
            }),
        });
        return response;
    },
    deleteGruppe: async function (gruppe) {
        const response = await fetch(ENDPOINTS.GRUPPE(gruppe), {
            method: "DELETE",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    updateGruppe: async function (gruppe, name, standort, gruppenleiter) {
        const response = await fetch(ENDPOINTS.GRUPPE(gruppe), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: JSON.stringify({
                gruppenName: name,
                standort: standort,
                gruppenleiter: gruppenleiter,
            }),
        });
        return response;
    },
};
