import { fileURLToPath } from "node:url";
import { configDefaults, defineConfig, mergeConfig } from "vitest/config";
import viteConfig from "./vite.config";

export default mergeConfig(
    viteConfig,
    defineConfig({
        test: {
            environment: "happy-dom",
            exclude: [...configDefaults.exclude, "e2e/**"],
            root: fileURLToPath(new URL("./", import.meta.url)),
            coverage: {
                provider: "istanbul", // or 'v8'
            },
            reporters: ["junit", "default"],
            outputFile: {
                junit: "coverage/junit.xml",
            },
            setupFiles: ["./src/test/setup.js"], // Add setup file
            globals: true,
        },
    })
);
