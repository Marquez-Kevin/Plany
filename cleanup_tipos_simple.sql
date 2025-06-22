-- Versión simplificada para limpiar la tabla tipo

-- Eliminar duplicados y dejar solo las primeras 9 filas únicas
DELETE FROM public.tipo 
WHERE cod_tipo NOT IN (
    SELECT cod_tipo FROM (
        SELECT DISTINCT ON (nombre_tipo) cod_tipo
        FROM public.tipo
        ORDER BY nombre_tipo, cod_tipo ASC
        LIMIT 9
    ) AS tipos_unicos
);

-- Verificar el resultado
SELECT * FROM public.tipo ORDER BY cod_tipo ASC; 