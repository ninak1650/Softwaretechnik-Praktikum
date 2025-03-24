import { beforeAll, vi } from "vitest";
import { createApp } from "vue";

beforeAll(() => {
    // Since we're using Vue 3, we need to create an app instance
    const app = createApp({});

    // Mock Vue plugins
    // Note: You might need to use a Vue 3 compatible version of Buefy or replace with another UI library

    // Mock localStorage
    global.localStorage = {
        getItem: vi.fn(),
        setItem: vi.fn(),
        removeItem: vi.fn(),
    };
});
