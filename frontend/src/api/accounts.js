import { buildQuery, ENDPOINTS } from "@/api/endpoints";
import { getDefaultHeaders } from "@/util/misc";
export const accounts = {
    getAccount: async function (username) {
        const response = await fetch(ENDPOINTS.ACCOUNT(username), {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    getAccountsWithRolle: async function (rolle) {
        const url = ENDPOINTS.ACCOUNTS() + buildQuery({ rolle: rolle });
        const response = await fetch(url, {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    getAccounts: async function () {
        const response = await fetch(ENDPOINTS.ACCOUNTS(), {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    createAccount: async function (
        benutzername,
        passwort,
        name,
        rolle,
        standort,
        gruppenNummer
    ) {
        const response = await fetch(ENDPOINTS.ACCOUNTS(), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: JSON.stringify({
                benutzername: benutzername,
                passwort: passwort,
                name: name,
                rolle: rolle,
                standort: standort,
                gruppenNummer: gruppenNummer,
            }),
        });
        return response;
    },
    updateAccount: async function (
        benutzername,
        name,
        rolle,
        standort,
        gruppenNummer
    ) {
        const response = await fetch(ENDPOINTS.ACCOUNT(benutzername), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: JSON.stringify({
                name: name,
                rolle: rolle,
                standort: standort,
                gruppenNummer: gruppenNummer,
            }),
        });
        return response;
    },

    deleteAccount: async function (username) {
        const response = await fetch(ENDPOINTS.ACCOUNT(username), {
            method: "DELETE",
            headers: getDefaultHeaders(),
        });
        return response;
    },

    resetPassword: async function (username, passwort) {
        const response = await fetch(ENDPOINTS.ACCOUNT_RESET(username), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: JSON.stringify({ passwort: passwort }),
        });
        return response;
    },

    changePassword: async function (username, passwort) {
        const response = await fetch(ENDPOINTS.ACCOUNT_PASSWORT(username), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: JSON.stringify({ passwort: passwort }),
        });
        return response;
    },
};
