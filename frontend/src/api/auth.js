import { ENDPOINTS } from "@/api/endpoints";
import { getDefaultHeaders } from "@/util/misc";
export const auth = {
    login: async function (username, password) {
        const response = await fetch(ENDPOINTS.AUTH_LOGIN(), {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                username: username,
                password: password,
            }),
        });
        return response;
    },
    validate: async function (username, password) {
        const response = await fetch(ENDPOINTS.AUTH_VALIDATE(), {
            method: "POST",
            headers: getDefaultHeaders(),
            body: JSON.stringify({
                username: username,
                password: password,
            }),
        });
        return response;
    },
};
