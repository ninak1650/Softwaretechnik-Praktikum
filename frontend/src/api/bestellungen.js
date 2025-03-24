import { buildQuery, ENDPOINTS } from "@/api/endpoints";
import { getDefaultHeaders } from "@/util/misc";

export const bestellungen = {
    setBestellung: async function (kundenNummer, date, essen) {
        const response = await fetch(ENDPOINTS.BESTELLUNGEN(), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: JSON.stringify({
                kundennummer: kundenNummer,
                datum: date,
                essenswahl: essen,
            }),
        });
    },
    getBestellung: async function (ID) {
        const response = await fetch(ENDPOINTS.BESTELLUNG(ID), {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    getBestellungToday: async function () {
        const response = await fetch(ENDPOINTS.BESTELLUNGEN(), {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
    getBestellungenForStandortAndDate: async function (standortID, datum) {
        const url =
            ENDPOINTS.BESTELLUNGEN() +
            buildQuery({ standort: standortID, datum: datum });
        const response = await fetch(url, {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },

    setBestellStatus: async function (ID, status) {
        const response = await fetch(ENDPOINTS.BESTELLUNG_STATUS(ID), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: status,
        });
        return response;
    },
};
