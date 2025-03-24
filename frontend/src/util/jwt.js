import { jwtDecode } from "jwt-decode";
import { useRouter } from "vue-router";
import { stringToRolle } from "./rolle";

export function getGruppeFromToken() {
    const token = localStorage.getItem("token");
    if (!token) {
        useRouter().push("/login");
    }
    const decoded = jwtDecode(token);
    return decoded.gruppe;
}

export function getRoleFromToken() {
    const token = localStorage.getItem("token");
    if (!token) {
        useRouter().push("/login");
    }
    const decoded = jwtDecode(token);
    return stringToRolle(decoded.role);
}

export function getStandortFromToken() {
    const token = localStorage.getItem("token");
    if (!token) {
        useRouter().push("/login");
    }
    const decoded = jwtDecode(token);
    return decoded.standort;
}

export function getUsernameFromToken() {
    const token = localStorage.getItem("token");
    if (!token) {
        useRouter().push("/login");
    }
    const decoded = jwtDecode(token);
    return decoded.sub;
}

export function getRequirePasswordChangeFromToken() {
    const token = localStorage.getItem("token");
    if (!token) {
        useRouter().push("/login");
    }
    const decoded = jwtDecode(token);
    return decoded.requirePasswordChange;
}

export function getToken() {
    const token = localStorage.getItem("token");
    if (!token) {
        useRouter().push("/login");
    }
    return token;
}
