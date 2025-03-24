export const Rolle = {
    KÜCHE: "KÜCHE",
    GRUPPENLEITUNG: "GRUPPENLEITUNG",
    VERWALTUNG: "VERWALTUNG",
    STANDORTLEITUNG: "STANDORTLEITUNG",
};

export function stringToRolle(rolle) {
    switch (rolle) {
        case "KÜCHE":
            return Rolle.KÜCHE;
        case "GRUPPENLEITUNG":
            return Rolle.GRUPPENLEITUNG;
        case "VERWALTUNG":
            return Rolle.VERWALTUNG;
        case "STANDORTLEITUNG":
            return Rolle.STANDORTLEITUNG;
        default:
            return null;
    }
}
