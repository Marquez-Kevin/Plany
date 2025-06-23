// Configuración de la API
const API_CONFIG = {
    // Detectar automáticamente el entorno
    getBaseUrl: function() {
        const hostname = window.location.hostname;
        const protocol = window.location.protocol;
        const host = window.location.host;
        
        // Desarrollo local
        if (hostname === 'localhost' || hostname === '127.0.0.1') {
            return 'http://localhost:8080/api';
        }
        
        // Producción - usar la URL de Render (sin puerto, Render maneja el mapeo)
        if (hostname === 'plany.onrender.com') {
            return 'https://plany.onrender.com/api';
        }
        
        // Otros dominios de producción
        return `${protocol}//${host}/api`;
    },
    
    // URL específica para producción (descomenta si necesitas una URL diferente)
    // productionUrl: 'https://tu-backend-api.com/api',
    
    // Obtener la URL base actual
    get currentUrl() {
        return this.getBaseUrl();
    }
};

// Exportar para uso global
window.API_CONFIG = API_CONFIG; 