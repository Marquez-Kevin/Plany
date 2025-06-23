#!/bin/bash

# Script para probar el registro en Plany
echo "=== Probando registro en Plany ==="

# URL base de la API
API_BASE="https://plany.onrender.com/api"

echo "1. Probando endpoint de debug..."
curl -s -X POST "$API_BASE/auth/debug-register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Usuario Test",
    "email": "test@example.com",
    "password": "123456"
  }' | jq '.'

echo ""
echo "2. Probando registro real..."
curl -s -X POST "$API_BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Usuario Test",
    "email": "test@example.com",
    "password": "123456"
  }' | jq '.'

echo ""
echo "3. Probando login con el usuario creado..."
curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "123456"
  }' | jq '.'

echo ""
echo "=== Fin de las pruebas ===" 