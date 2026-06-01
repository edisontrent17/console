const { contextBridge, ipcRenderer } = require("electron");

contextBridge.exposeInMainWorld("data360Desktop", {
    isDesktop: true,
    appInfo: () => ipcRenderer.invoke("desktop:app-info"),
    getSettings: () => ipcRenderer.invoke("desktop:get-settings"),
    saveSettings: (settings) => ipcRenderer.invoke("desktop:save-settings", settings),
    exportJson: (request) => ipcRenderer.invoke("desktop:export-json", request),
    apiHeaders: () => ipcRenderer.invoke("desktop:api-headers")
});
