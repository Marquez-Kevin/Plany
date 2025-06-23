#!/bin/bash

# Script para probar la creación de usuarios y login en Plany
echo "=== Probando creación de usuarios y login en Plany ==="

# URL base de la API
API_BASE="https://plany.onrender.com/api"

echo "1. Verificando si hay usuarios en la base de datos..."
curl -s "$API_BASE/auth/users/count" | jq '.'

echo ""
echo "2. Creando usuario de prueba..."
curl -s -X POST "$API_BASE/auth/create-test-user" | jq '.'

echo ""
echo "3. Probando login con usuario de prueba..."
curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@plany.com",
    "password": "123456"
  }' | jq '.'

echo ""
echo "=== Fin de las pruebas ==="
echo ""
echo "Credenciales de prueba:"
echo "Email: test@plany.com"
echo "Password: 123456" 