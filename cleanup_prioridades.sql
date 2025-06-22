-- Script para limpiar duplicados en la tabla prioridad
-- Paso 1: Ver duplicados actuales
SELECT nombre_prioridad, COUNT(*) as cantidad
FROM prioridad
GROUP BY nombre_prioridad
HAVING COUNT(*) > 1
ORDER BY nombre_prioridad;

-- Paso 2: Ver todas las prioridades con sus IDs
SELECT cod_prio, nombre_prioridad
FROM prioridad
ORDER BY nombre_prioridad, cod_prio;

-- Paso 3: Crear tabla temporal con las prioridades únicas (mantener el ID más bajo)
CREATE TEMP TABLE prioridades_unicas AS
SELECT MIN(cod_prio) as cod_prio, nombre_prioridad
FROM prioridad
GROUP BY nombre_prioridad;

-- Paso 4: Verificar qué se va a eliminar
SELECT p.cod_prio, p.nombre_prioridad
FROM prioridad p
WHERE p.cod_prio NOT IN (SELECT cod_prio FROM prioridades_unicas)
ORDER BY p.nombre_prioridad, p.cod_prio;

-- Paso 5: Eliminar duplicados (mantener solo el registro con ID más bajo)
DELETE FROM prioridad
WHERE cod_prio NOT IN (SELECT cod_prio FROM prioridades_unicas);

-- Paso 6: Verificar resultado final
SELECT cod_prio, nombre_prioridad
FROM prioridad
ORDER BY cod_prio;

-- Paso 7: Limpiar tabla temporal
DROP TABLE prioridades_unicas; 