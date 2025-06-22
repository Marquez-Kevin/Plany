-- Script para limpiar duplicados en la tabla categoria
-- Paso 1: Ver duplicados actuales
SELECT nombre_categoria, COUNT(*) as cantidad
FROM categoria
GROUP BY nombre_categoria
HAVING COUNT(*) > 1
ORDER BY nombre_categoria;

-- Paso 2: Ver todas las categorías con sus IDs
SELECT cod_cat, nombre_categoria
FROM categoria
ORDER BY nombre_categoria, cod_cat;

-- Paso 3: Crear tabla temporal con las categorías únicas (mantener el ID más bajo)
CREATE TEMP TABLE categorias_unicas AS
SELECT MIN(cod_cat) as cod_cat, nombre_categoria
FROM categoria
GROUP BY nombre_categoria;

-- Paso 4: Verificar qué se va a eliminar
SELECT c.cod_cat, c.nombre_categoria
FROM categoria c
WHERE c.cod_cat NOT IN (SELECT cod_cat FROM categorias_unicas)
ORDER BY c.nombre_categoria, c.cod_cat;

-- Paso 5: Eliminar duplicados (mantener solo el registro con ID más bajo)
DELETE FROM categoria
WHERE cod_cat NOT IN (SELECT cod_cat FROM categorias_unicas);

-- Paso 6: Verificar resultado final
SELECT cod_cat, nombre_categoria
FROM categoria
ORDER BY cod_cat;

-- Paso 7: Limpiar tabla temporal
DROP TABLE categorias_unicas; 