import { ENDPOINTS } from "@/api/endpoints";
import { getDefaultHeaders } from "@/util/misc";

export const kunden = {
    getKunden: async function () {
        const response = await fetch(ENDPOINTS.KUNDEN(), {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    getKunde: async function (kundenNummer) {
        const response = await fetch(ENDPOINTS.KUNDE(kundenNummer), {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    createKunde: async function (name, standort, gruppenNummer) {
        const response = await fetch(ENDPOINTS.KUNDEN(), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: JSON.stringify({
                name: name,
                standort: standort,
                gruppenNummer: gruppenNummer,
            }),
        });
        return response;
    },
    updateKunde: async function (kundenNummer, name, standort, gruppenNummer) {
        const response = await fetch(ENDPOINTS.KUNDE(kundenNummer), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: JSON.stringify({
                name: name,
                standort: standort,
                gruppenNummer: gruppenNummer,
            }),
        });
        return response;
    },
    deleteKunde: async function (kundenNummer) {
        const response = await fetch(ENDPOINTS.KUNDE(kundenNummer), {
            method: "DELETE",
            headers: getDefaultHeaders(),
        });
        return response;
    },
};
