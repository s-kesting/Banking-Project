import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
// Add this import for path resolution:
import path from "path";

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // Tell Vite that “@” → the “src” folder
      "@": path.resolve(__dirname, "src"),
    },
  },
  server: {
    port: 5173, // keep this as you have it
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        rewrite: (pathStr) => pathStr.replace(/^\/api/, "/api"),
      },
    },
  },
});
