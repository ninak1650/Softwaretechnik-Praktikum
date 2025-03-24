import { api } from "@/api/api";
import LoginComponent from "@/components/LoginComponent.vue";
import {
    getRequirePasswordChangeFromToken,
    getRoleFromToken,
} from "@/util/jwt";
import { Rolle } from "@/util/rolle";
import { mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

vi.mock("@/api/api");
vi.mock("@/util/jwt");

describe("LoginComponent.vue", () => {
    let wrapper;
    const mockBuefy = {
        toast: {
            open: vi.fn(),
        },
    };
    const mockRouter = {
        push: vi.fn(),
    };

    beforeEach(() => {
        wrapper = mount(LoginComponent, {
            global: {
                mocks: {
                    $buefy: mockBuefy,
                    $router: mockRouter,
                },
                stubs: {
                    "b-field": {
                        template: '<div class="stubbed-field"><slot /></div>',
                        props: ["label"],
                    },
                    "b-input": {
                        template: '<input class="stubbed-input" />',
                        props: ["modelValue"],
                        emits: ["update:modelValue"],
                    },
                    "b-button": {
                        template:
                            '<button class="stubbed-button">{{ label }}</button>',
                        props: ["label", "type"],
                    },
                },
            },
            attachTo: document.body,
        });

        vi.clearAllMocks();
    });

    afterEach(() => {
        wrapper.unmount();
        vi.clearAllMocks();
    });

    it("renders login form", () => {
        // Check for the login title
        expect(wrapper.find(".login-title").text()).toBe("Login");

        // Check for input fields and button
        expect(wrapper.findAll(".stubbed-input").length).toBeGreaterThan(0);
        expect(wrapper.find(".stubbed-button").exists()).toBeTruthy();
    });

    it("shows warning toast if fields are empty", async () => {
        await wrapper.vm.handleLogin();

        expect(mockBuefy.toast.open).toHaveBeenCalledWith({
            message: "Bitte füllen Sie alle Felder aus!",
            type: "is-warning",
            position: "is-top",
            duration: 3000,
        });
    });

    it("calls login API and handles success", async () => {
        // Set up component data
        wrapper.vm.username = "testuser";
        wrapper.vm.password = "testpass";

        // Mock API response
        api.auth.login.mockResolvedValue({
            ok: true,
            text: () => Promise.resolve("mockToken"),
        });

        // Mock token roles
        getRoleFromToken.mockReturnValue(Rolle.GRUPPENLEITUNG);
        getRequirePasswordChangeFromToken.mockReturnValue(false);

        // Call the method
        await wrapper.vm.handleLogin();

        // Verify API called correctly
        expect(api.auth.login).toHaveBeenCalledWith("testuser", "testpass");

        // Check localStorage
        expect(localStorage.setItem).toHaveBeenCalledWith("token", "mockToken");

        // Check toast
        expect(mockBuefy.toast.open).toHaveBeenCalledWith({
            message: "Erfolgreich eingeloggt!",
            type: "is-success",
            position: "is-top",
            duration: 3000,
        });

        // Check navigation
        expect(mockRouter.push).toHaveBeenCalledWith("/gruppenleitungNormal");
    });

    it("handles login failure", async () => {
        wrapper.vm.username = "testuser";
        wrapper.vm.password = "wrongpass";

        api.auth.login.mockResolvedValue({
            ok: false,
            text: () => Promise.resolve("Invalid credentials"),
        });

        await wrapper.vm.handleLogin();

        expect(api.auth.login).toHaveBeenCalledWith("testuser", "wrongpass");
        expect(mockBuefy.toast.open).toHaveBeenCalledWith({
            message: "Benutzername oder Passwort ist falsch!",
            type: "is-danger",
            position: "is-top",
            duration: 3000,
        });
    });

    it("redirects to password change if required", async () => {
        wrapper.vm.username = "testuser";
        wrapper.vm.password = "testpass";

        api.auth.login.mockResolvedValue({
            ok: true,
            text: () => Promise.resolve("mockToken"),
        });

        getRequirePasswordChangeFromToken.mockReturnValue(true);

        await wrapper.vm.handleLogin();

        expect(mockRouter.push).toHaveBeenCalledWith(
            `/konto/testuser/passwort`
        );
    });
});
