-- Versión segura para limpiar la tabla tipo con revisión previa

-- Paso 1: Ver qué tipos tenemos actualmente
SELECT 'TIPOS ACTUALES:' as info;
SELECT cod_tipo, nombre_tipo, COUNT(*) as cantidad
FROM public.tipo 
GROUP BY cod_tipo, nombre_tipo
ORDER BY cod_tipo ASC;

-- Paso 2: Identificar los tipos únicos que queremos mantener
CREATE TEMP TABLE tipos_a_mantener AS
SELECT DISTINCT ON (nombre_tipo) cod_tipo, nombre_tipo
FROM public.tipo
ORDER BY nombre_tipo, cod_tipo ASC
LIMIT 9;

-- Paso 3: Mostrar qué tipos vamos a mantener
SELECT 'TIPOS A MANTENER:' as info;
SELECT * FROM tipos_a_mantener ORDER BY cod_tipo ASC;

-- Paso 4: Mostrar qué tipos vamos a eliminar
SELECT 'TIPOS A ELIMINAR:' as info;
SELECT cod_tipo, nombre_tipo 
FROM public.tipo 
WHERE cod_tipo NOT IN (SELECT cod_tipo FROM tipos_a_mantener)
ORDER BY cod_tipo ASC;

-- Paso 5: Mostrar cuántas tareas usan cada tipo
SELECT 'TAREAS POR TIPO:' as info;
SELECT t.cod_tipo, tp.nombre_tipo, COUNT(t.id_tarea) as cantidad_tareas
FROM public.tarea t
JOIN public.tipo tp ON t.cod_tipo = tp.cod_tipo
GROUP BY t.cod_tipo, tp.nombre_tipo
ORDER BY t.cod_tipo ASC;

-- Paso 6: Mostrar tareas que usan tipos que vamos a eliminar
SELECT 'TAREAS QUE USAN TIPOS A ELIMINAR:' as info;
SELECT t.id_tarea, t.titulo, t.cod_tipo, tp.nombre_tipo
FROM public.tarea t
JOIN public.tipo tp ON t.cod_tipo = tp.cod_tipo
WHERE t.cod_tipo NOT IN (SELECT cod_tipo FROM tipos_a_mantener)
ORDER BY t.cod_tipo ASC;

-- REVISAR LOS RESULTADOS ANTERIORES ANTES DE CONTINUAR
-- Si estás de acuerdo, ejecuta el siguiente bloque:

/*
-- Paso 7: Actualizar tareas con tipos a eliminar (asignar el primer tipo disponible)
UPDATE public.tarea 
SET cod_tipo = (SELECT cod_tipo FROM tipos_a_mantener ORDER BY cod_tipo ASC LIMIT 1)
WHERE cod_tipo NOT IN (SELECT cod_tipo FROM tipos_a_mantener);

-- Paso 8: Eliminar tipos duplicados
DELETE FROM public.tipo 
WHERE cod_tipo NOT IN (SELECT cod_tipo FROM tipos_a_mantener);

-- Paso 9: Verificar resultado final
SELECT 'RESULTADO FINAL:' as info;
SELECT * FROM public.tipo ORDER BY cod_tipo ASC;

-- Paso 10: Limpiar tabla temporal
DROP TABLE tipos_a_mantener;
*/ 