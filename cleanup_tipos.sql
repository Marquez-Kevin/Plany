-- Código SQL para limpiar la tabla tipo y dejar solo las primeras 9 filas únicas

-- Paso 1: Crear una tabla temporal con los tipos únicos ordenados
CREATE TEMP TABLE temp_tipos AS
SELECT DISTINCT ON (nombre_tipo) cod_tipo, nombre_tipo
FROM public.tipo
ORDER BY nombre_tipo, cod_tipo ASC
LIMIT 9;

-- Paso 2: Eliminar todos los registros de la tabla original
DELETE FROM public.tipo;

-- Paso 3: Insertar solo los tipos únicos de la tabla temporal
INSERT INTO public.tipo (cod_tipo, nombre_tipo)
SELECT cod_tipo, nombre_tipo FROM temp_tipos
ORDER BY cod_tipo ASC;

-- Paso 4: Verificar el resultado
SELECT * FROM public.tipo ORDER BY cod_tipo ASC;

-- Paso 5: Limpiar la tabla temporal
DROP TABLE temp_tipos; 