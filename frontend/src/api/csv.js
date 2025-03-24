import { ENDPOINTS } from "@/api/endpoints";
import { getToken } from "@/util/jwt";

export const csv = {
    uploadCSV: async function (file) {
        const formData = new FormData();
        formData.append("file", file);
        const response = await fetch(ENDPOINTS.CSV_UPLOAD(), {
            method: "POST",
            headers: {
                Authorization: `Bearer ${getToken()}`,
            },
            body: formData,
        });
        return response;
    },
};
