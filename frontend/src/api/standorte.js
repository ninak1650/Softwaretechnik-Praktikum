import { ENDPOINTS } from "@/api/endpoints";
import { getDefaultHeaders } from "@/util/misc";
export const standorte = {
    getStandorte: async function () {
        const response = await fetch(ENDPOINTS.STANDORTE(), {
            method: "GET",
            headers: getDefaultHeaders(),
        });
        return response;
    },
};
