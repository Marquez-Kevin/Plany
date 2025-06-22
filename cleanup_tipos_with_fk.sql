-- Código SQL para limpiar la tabla tipo manejando foreign key constraints

-- Paso 1: Identificar los tipos únicos que queremos mantener
CREATE TEMP TABLE tipos_a_mantener AS
SELECT DISTINCT ON (nombre_tipo) cod_tipo, nombre_tipo
FROM public.tipo
ORDER BY nombre_tipo, cod_tipo ASC
LIMIT 9;

-- Paso 2: Identificar el tipo por defecto (el primero de los que mantenemos)
CREATE TEMP TABLE tipo_por_defecto AS
SELECT cod_tipo FROM tipos_a_mantener ORDER BY cod_tipo ASC LIMIT 1;

-- Paso 3: Actualizar todas las tareas que usan tipos que vamos a eliminar
-- Asignarles el tipo por defecto
UPDATE public.tarea 
SET cod_tipo = (SELECT cod_tipo FROM tipo_por_defecto)
WHERE cod_tipo NOT IN (SELECT cod_tipo FROM tipos_a_mantener);

-- Paso 4: Verificar que no hay tareas usando tipos que vamos a eliminar
SELECT COUNT(*) as tareas_con_tipos_a_eliminar
FROM public.tarea 
WHERE cod_tipo NOT IN (SELECT cod_tipo FROM tipos_a_mantener);

-- Paso 5: Si el conteo es 0, proceder a eliminar los tipos
-- Si no es 0, revisar el paso 3
DELETE FROM public.tipo 
WHERE cod_tipo NOT IN (SELECT cod_tipo FROM tipos_a_mantener);

-- Paso 6: Verificar el resultado final
SELECT * FROM public.tipo ORDER BY cod_tipo ASC;

-- Paso 7: Limpiar tablas temporales
DROP TABLE tipos_a_mantener;
DROP TABLE tipo_por_defecto; 