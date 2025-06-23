#!/bin/bash

# Script para probar el login en Plany
echo "=== Probando login en Plany ==="

# URL base de la API
API_BASE="https://plany.onrender.com/api"

echo "1. Probando endpoint de debug de login..."
curl -s -X POST "$API_BASE/auth/debug-login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joaco@gmail.com",
    "password": "12345"
  }' | jq '.'

echo ""
echo "2. Probando login real..."
curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joaco@gmail.com",
    "password": "12345"
  }' | jq '.'

echo ""
echo "3. Probando con credenciales incorrectas..."
curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joaco@gmail.com",
    "password": "wrongpassword"
  }' | jq '.'

echo ""
echo "=== Fin de las pruebas ==="
echo ""
echo "Credenciales de prueba:"
echo "Email: joaco@gmail.com"
echo "Password: 12345" 