const API_BASE_URL = function () {
    return import.meta.env.VITE_API_BASE_URL || "http://localhost:8090";
};
export const ENDPOINTS = {
    AUTH: function () {
        return API_BASE_URL() + "/auth";
    },
    AUTH_LOGIN: function () {
        return this.AUTH() + "/login";
    },
    AUTH_VALIDATE: function () {
        return this.AUTH() + "/validate";
    },
    ACCOUNTS: function () {
        return API_BASE_URL() + "/accounts";
    },
    ACCOUNT: function (ID) {
        return this.ACCOUNTS() + `/${ID}`;
    },
    ACCOUNT_PASSWORT: function (ID) {
        return this.ACCOUNT(ID) + "/passwort";
    },
    ACCOUNT_RESET: function (ID) {
        return this.ACCOUNT(ID) + "/reset";
    },
    KUNDEN: function () {
        return API_BASE_URL() + "/kunden";
    },
    KUNDE: function (ID) {
        return this.KUNDEN() + `/${ID}`;
    },
    KUNDE_BESTELLUNG: function (ID) {
        return this.KUNDE(ID) + `/bestellung`;
    },
    BESTELLUNGEN: function () {
        return API_BASE_URL() + "/bestellungen";
    },
    BESTELLUNG: function (ID) {
        return this.BESTELLUNGEN() + `/${ID}`;
    },
    BESTELLUNG_STATUS: function (ID) {
        return this.BESTELLUNG(ID) + `/status`;
    },
    GRUPPEN: function () {
        return API_BASE_URL() + "/gruppen";
    },
    GRUPPE: function (ID) {
        return this.GRUPPEN() + `/${ID}`;
    },
    GRUPPE_MITGLIEDER: function (ID) {
        return this.GRUPPE(ID) + "/mitglieder";
    },
    GRUPPE_VERTRETUNG: function (ID) {
        return this.GRUPPE(ID) + "/vertretung";
    },
    KUNDEN: function () {
        return API_BASE_URL() + "/kunden";
    },
    CSV: function () {
        return API_BASE_URL() + "/csv";
    },
    CSV_UPLOAD: function () {
        return this.CSV() + "/upload";
    },
    STANDORTE: function () {
        return API_BASE_URL() + "/standorte";
    },
};

export function buildQuery(query) {
    return (
        "?" +
        Object.keys(query)
            .map((key) => key + "=" + query[key])
            .join("&")
    );
}
