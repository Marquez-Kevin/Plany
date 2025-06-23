#!/bin/bash

# Script para probar la conexión a la base de datos Railway
echo "=== Probando conexión a la base de datos Railway ==="

# Variables de conexión
DB_URL="jdbc:postgresql://yamanote.proxy.rlwy.net:40166/railway"
DB_USER="postgres"
DB_PASS="pAiBYcudZpLsxtirnCwPmiHzSPTVhlsn"

echo "URL: $DB_URL"
echo "Usuario: $DB_USER"
echo "Contraseña: [OCULTA]"

# Probar conexión usando psql si está disponible
if command -v psql &> /dev/null; then
    echo "Probando conexión con psql..."
    PGPASSWORD=$DB_PASS psql -h yamanote.proxy.rlwy.net -p 40166 -U postgres -d railway -c "SELECT version();" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✅ Conexión exitosa con psql"
    else
        echo "❌ Error de conexión con psql"
    fi
else
    echo "psql no está disponible, probando con telnet..."
    timeout 5 telnet yamanote.proxy.rlwy.net 40166
    if [ $? -eq 0 ]; then
        echo "✅ Puerto accesible"
    else
        echo "❌ Puerto no accesible"
    fi
fi

echo "=== Fin de la prueba ===" 