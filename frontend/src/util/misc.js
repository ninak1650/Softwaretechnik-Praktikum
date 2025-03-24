import { getToken } from "./jwt";

export function getDefaultHeaders() {
    return {
        "Content-Type": "application/json",
        Authorization: `Bearer ${getToken()}`,
    };
}

export function encodeEssen(essen, salat) {
    const essen1 = essen == "Essen 1";
    const essen2 = essen == "Essen 2";
    return essen1 * 2 + essen2 * 4 + salat;
}

export function decodeEssen(essenInt) {
    let essen = "";
    let salat = Boolean(essenInt % 2);
    essenInt = Math.floor(essenInt / 2);
    switch (essenInt) {
        case 0:
            essen = "Kein Essen";
            break;
        case 1:
            essen = "Essen 1";
            break;
        case 2:
            essen = "Essen 2";
            break;
    }
    salat = salat ? "Ja" : "Nein";
    return { essen, salat };
}
