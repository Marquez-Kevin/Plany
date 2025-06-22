-- PostgreSQL database dump for Railway import
-- Converted from COPY statements to INSERT statements for compatibility

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- Create function for logging
CREATE OR REPLACE FUNCTION public.log_creacion_tarea() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO Log_Tarea_Creada(id_tarea, titulo, id_usuario)
    VALUES (NEW.id_tarea, NEW.titulo, NEW.id_usuario);
    RETURN NEW;
END;
$$;

-- Create sequences
CREATE SEQUENCE IF NOT EXISTS public.categoria_cod_cat_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.estado_cod_est_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.log_tarea_creada_id_log_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.prioridad_cod_prio_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.recordatorio_cod_recor_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.tarea_id_tarea_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.tipo_cod_tipo_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.usuario_id_usuario_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create tables
CREATE TABLE IF NOT EXISTS public.categoria (
    cod_cat integer NOT NULL DEFAULT nextval('public.categoria_cod_cat_seq'::regclass),
    nombre_categoria character varying(30)
);

CREATE TABLE IF NOT EXISTS public.estado (
    cod_est integer NOT NULL DEFAULT nextval('public.estado_cod_est_seq'::regclass),
    nombre_estado character varying(30)
);

CREATE TABLE IF NOT EXISTS public.log_tarea_creada (
    id_log integer NOT NULL DEFAULT nextval('public.log_tarea_creada_id_log_seq'::regclass),
    id_tarea integer NOT NULL,
    titulo character varying(30),
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    id_usuario integer
);

CREATE TABLE IF NOT EXISTS public.prioridad (
    cod_prio integer NOT NULL DEFAULT nextval('public.prioridad_cod_prio_seq'::regclass),
    nombre_prioridad character varying(30)
);

CREATE TABLE IF NOT EXISTS public.recordatorio (
    cod_recor integer NOT NULL DEFAULT nextval('public.recordatorio_cod_recor_seq'::regclass),
    mensaje character varying(30),
    fecha_hora date NOT NULL
);

CREATE TABLE IF NOT EXISTS public.tarea (
    id_tarea integer NOT NULL DEFAULT nextval('public.tarea_id_tarea_seq'::regclass),
    titulo character varying(30) NOT NULL,
    descripcion character varying(50),
    fecha_creacion date NOT NULL,
    fecha_fin date NOT NULL,
    cod_recor integer,
    cod_tipo integer,
    cod_prio integer,
    cod_cat integer,
    cod_est integer,
    id_usuario integer
);

CREATE TABLE IF NOT EXISTS public.tipo (
    cod_tipo integer NOT NULL DEFAULT nextval('public.tipo_cod_tipo_seq'::regclass),
    nombre_tipo character varying(30)
);

CREATE TABLE IF NOT EXISTS public.usuario (
    id_usuario integer NOT NULL DEFAULT nextval('public.usuario_id_usuario_seq'::regclass),
    nombre_usu character varying(30) NOT NULL,
    correo_usu character varying(35) NOT NULL,
    contrasena character varying(100) DEFAULT ''::character varying NOT NULL
);

-- Set sequence ownership
ALTER SEQUENCE public.categoria_cod_cat_seq OWNED BY public.categoria.cod_cat;
ALTER SEQUENCE public.estado_cod_est_seq OWNED BY public.estado.cod_est;
ALTER SEQUENCE public.log_tarea_creada_id_log_seq OWNED BY public.log_tarea_creada.id_log;
ALTER SEQUENCE public.prioridad_cod_prio_seq OWNED BY public.prioridad.cod_prio;
ALTER SEQUENCE public.recordatorio_cod_recor_seq OWNED BY public.recordatorio.cod_recor;
ALTER SEQUENCE public.tarea_id_tarea_seq OWNED BY public.tarea.id_tarea;
ALTER SEQUENCE public.tipo_cod_tipo_seq OWNED BY public.tipo.cod_tipo;
ALTER SEQUENCE public.usuario_id_usuario_seq OWNED BY public.usuario.id_usuario;

-- Add primary keys
ALTER TABLE ONLY public.categoria ADD CONSTRAINT categoria_pkey PRIMARY KEY (cod_cat);
ALTER TABLE ONLY public.estado ADD CONSTRAINT estado_pkey PRIMARY KEY (cod_est);
ALTER TABLE ONLY public.log_tarea_creada ADD CONSTRAINT log_tarea_creada_pkey PRIMARY KEY (id_log);
ALTER TABLE ONLY public.prioridad ADD CONSTRAINT prioridad_pkey PRIMARY KEY (cod_prio);
ALTER TABLE ONLY public.recordatorio ADD CONSTRAINT recordatorio_pkey PRIMARY KEY (cod_recor);
ALTER TABLE ONLY public.tarea ADD CONSTRAINT tarea_pkey PRIMARY KEY (id_tarea);
ALTER TABLE ONLY public.tipo ADD CONSTRAINT tipo_pkey PRIMARY KEY (cod_tipo);
ALTER TABLE ONLY public.usuario ADD CONSTRAINT usuario_pkey PRIMARY KEY (id_usuario);

-- Add foreign keys
ALTER TABLE ONLY public.tarea ADD CONSTRAINT tarea_cod_cat_fkey FOREIGN KEY (cod_cat) REFERENCES public.categoria(cod_cat);
ALTER TABLE ONLY public.tarea ADD CONSTRAINT tarea_cod_est_fkey FOREIGN KEY (cod_est) REFERENCES public.estado(cod_est);
ALTER TABLE ONLY public.tarea ADD CONSTRAINT tarea_cod_prio_fkey FOREIGN KEY (cod_prio) REFERENCES public.prioridad(cod_prio);
ALTER TABLE ONLY public.tarea ADD CONSTRAINT tarea_cod_recor_fkey FOREIGN KEY (cod_recor) REFERENCES public.recordatorio(cod_recor);
ALTER TABLE ONLY public.tarea ADD CONSTRAINT tarea_cod_tipo_fkey FOREIGN KEY (cod_tipo) REFERENCES public.tipo(cod_tipo);
ALTER TABLE ONLY public.tarea ADD CONSTRAINT tarea_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);

-- Create trigger
CREATE TRIGGER trg_log_creacion_tarea AFTER INSERT ON public.tarea FOR EACH ROW EXECUTE FUNCTION public.log_creacion_tarea();

-- Insert data using INSERT statements instead of COPY
-- Categorías
INSERT INTO public.categoria (cod_cat, nombre_categoria) VALUES (1, 'Laboral') ON CONFLICT DO NOTHING;
INSERT INTO public.categoria (cod_cat, nombre_categoria) VALUES (2, 'Colegio') ON CONFLICT DO NOTHING;
INSERT INTO public.categoria (cod_cat, nombre_categoria) VALUES (3, 'Universidad') ON CONFLICT DO NOTHING;
INSERT INTO public.categoria (cod_cat, nombre_categoria) VALUES (4, 'Personal') ON CONFLICT DO NOTHING;
INSERT INTO public.categoria (cod_cat, nombre_categoria) VALUES (5, 'General') ON CONFLICT DO NOTHING;
INSERT INTO public.categoria (cod_cat, nombre_categoria) VALUES (6, 'Urgente') ON CONFLICT DO NOTHING;
INSERT INTO public.categoria (cod_cat, nombre_categoria) VALUES (7, 'Importante') ON CONFLICT DO NOTHING;

-- Estados
INSERT INTO public.estado (cod_est, nombre_estado) VALUES (1, 'Completada') ON CONFLICT DO NOTHING;
INSERT INTO public.estado (cod_est, nombre_estado) VALUES (2, 'Pendiente') ON CONFLICT DO NOTHING;

-- Prioridades
INSERT INTO public.prioridad (cod_prio, nombre_prioridad) VALUES (1, 'Alta') ON CONFLICT DO NOTHING;
INSERT INTO public.prioridad (cod_prio, nombre_prioridad) VALUES (2, 'Media') ON CONFLICT DO NOTHING;
INSERT INTO public.prioridad (cod_prio, nombre_prioridad) VALUES (3, 'Baja') ON CONFLICT DO NOTHING;

-- Tipos
INSERT INTO public.tipo (cod_tipo, nombre_tipo) VALUES (1, 'Trabajo') ON CONFLICT DO NOTHING;
INSERT INTO public.tipo (cod_tipo, nombre_tipo) VALUES (2, 'Examen') ON CONFLICT DO NOTHING;
INSERT INTO public.tipo (cod_tipo, nombre_tipo) VALUES (3, 'Taller') ON CONFLICT DO NOTHING;
INSERT INTO public.tipo (cod_tipo, nombre_tipo) VALUES (4, 'Extracurricular') ON CONFLICT DO NOTHING;
INSERT INTO public.tipo (cod_tipo, nombre_tipo) VALUES (5, 'Personal') ON CONFLICT DO NOTHING;
INSERT INTO public.tipo (cod_tipo, nombre_tipo) VALUES (7, 'Estudio') ON CONFLICT DO NOTHING;
INSERT INTO public.tipo (cod_tipo, nombre_tipo) VALUES (8, 'Hogar') ON CONFLICT DO NOTHING;

-- Usuarios
INSERT INTO public.usuario (id_usuario, nombre_usu, correo_usu, contrasena) VALUES (1, 'Daniel Ramirez', 'daniel.ramirez@example.com', '') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario (id_usuario, nombre_usu, correo_usu, contrasena) VALUES (2, 'Sofia Herrera', 'sofia.herrera@example.com', '') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario (id_usuario, nombre_usu, correo_usu, contrasena) VALUES (3, 'Miguel Ruiz', 'miguel.ruiz@example.com', '') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario (id_usuario, nombre_usu, correo_usu, contrasena) VALUES (4, 'Valeria Lopez', 'valeria.lopez@example.com', '') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario (id_usuario, nombre_usu, correo_usu, contrasena) VALUES (5, 'Andres Castro', 'andres.castro@example.com', '') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario (id_usuario, nombre_usu, correo_usu, contrasena) VALUES (6, 'Kevin', 'kevinmarquez@gmail.com', '$2a$10$WFQbRXgiwi7PRCfjhvd1XuYp2XeKmCYgcOY8YKpUL6HPPwPKCEMXy') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario (id_usuario, nombre_usu, correo_usu, contrasena) VALUES (7, 'Camilo Gomez', 'camiloalonsogoca@gmail.com', '$2a$10$s7mYDs9BFeHIQXOE4X/C/uoeJSr0VfEg4V.wX9jQfaz6PzS.R8.TG') ON CONFLICT DO NOTHING;

-- Recordatorios
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (1, 'Revisar tarea de fisica', '2025-05-10') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (2, 'Entregar informe final', '2025-05-15') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (3, 'Hacer resumen de proyecto', '2025-05-18') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (4, 'Preparar presentación', '2025-05-20') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (5, 'Que no se me olvide', '2025-06-20') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (6, 'fdsfssf', '2025-06-20') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (7, 'Que no se me olvide', '2025-06-20') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (8, 'Que no se me olvide', '2025-06-20') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (9, 'Que no se me olvide', '2025-06-20') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (10, 'Por favor ve a comer pollo', '2025-06-19') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (11, 'fsdf', '2025-06-19') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (12, 'Que no se me olvide', '2025-06-22') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (13, 'Que no se me olvide', '2025-06-25') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (14, 'Que no se me olvide', '2025-06-25') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (15, 'Que no se me olvide', '2025-06-25') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (16, 'Que no se me olvide', '2025-06-25') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (17, 'Que no se me olvide', '2025-06-26') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (18, 'Que no se me olvide', '2025-06-22') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (19, 'fgjhjg', '2025-06-27') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (20, 'fsddfgg', '2025-06-29') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (21, 'Que no se me olvide', '2025-06-22') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (22, 'fsddfgg', '2025-06-29') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (23, 'fgjhjg', '2025-06-27') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (24, 'fsddfgg', '2025-06-29') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (26, 'fsddfgg', '2025-06-29') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (28, 'Que no se me olvide', '2025-07-20') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (29, 'Que no se me olvide', '2025-07-20') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (30, 'Que no se me olvide', '2025-07-20') ON CONFLICT DO NOTHING;
INSERT INTO public.recordatorio (cod_recor, mensaje, fecha_hora) VALUES (31, 'Bombas', '2025-06-23') ON CONFLICT DO NOTHING;

-- Tareas
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (1, 'Tarea de Calculo', 'Resolver integrales definidas', '2025-05-08', '2025-05-12', 1, 1, 1, 1, 1, 1) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (2, 'Informe de Redes', 'Redaccion y correccion del informe', '2025-05-09', '2025-05-14', 2, 2, 2, 2, 2, 2) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (3, 'Estudiar para examen', 'Repasar teoria de bases de datos', '2025-05-07', '2025-05-10', NULL, 1, 1, 1, 1, 3) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (4, 'Practica de SQL', 'Resolver ejercicios sobre JOINs', '2025-05-11', '2025-05-13', 3, 2, 3, 3, 2, 4) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (5, 'Exposicion final', 'Preparar diapositivas de estructura de datos', '2025-05-12', '2025-05-15', 4, 3, 2, 4, 2, 5) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (6, 'Hacer limpieza', 'Organizar escritorio y carpetas', '2025-05-10', '2025-05-11', NULL, 1, 1, 1, 1, 2) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (10, 'Programar', 'Hasta que me duerma', '2025-06-20', '2025-06-22', 8, 1, 1, 3, 1, 6) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (11, 'Fumar piedra', 'Fumar piedra hasta dormir', '2025-06-20', '2025-06-21', 9, 2, 2, 3, 1, 6) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (12, 'Comer pollo', 'Quiero comer en Guilles', '2025-06-20', '2025-06-20', 10, 5, 1, 4, 1, 6) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (13, 'Trabajos', 'dfssdfsf', '2025-06-20', '2025-06-20', 11, 1, 1, 2, 1, 6) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (7, 'Trabajos', 'Voy a fumar hierba y chupar bolo', '2025-06-19', '2025-06-26', 5, 1, 1, 3, 2, 6) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (14, 'Comer pollo', 'Bailalo don pollo', '2025-06-21', '2025-06-23', 21, 2, 1, 3, 2, 6) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (8, 'Trabajos', 'Camilo', '2025-06-20', '2025-06-27', 6, 1, 1, 1, 2, 6) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (9, 'Ir a la Universidad', 'Voy pa la U vieja', '2025-06-20', '2025-06-22', 7, 1, 1, 3, 2, 6) ON CONFLICT DO NOTHING;
INSERT INTO public.tarea (id_tarea, titulo, descripcion, fecha_creacion, fecha_fin, cod_recor, cod_tipo, cod_prio, cod_cat, cod_est, id_usuario) VALUES (17, 'Bombardear Iran', 'Recordarme de enviar aviones a dar bombazos', '2025-06-22', '2025-06-24', 31, 1, 1, 1, 1, 7) ON CONFLICT DO NOTHING;

-- Logs de tareas creadas
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (1, 1, 'Tarea de Calculo', '2025-06-19 18:06:17.633465', 1) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (2, 2, 'Informe de Redes', '2025-06-19 18:06:17.633465', 2) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (3, 3, 'Estudiar para examen', '2025-06-19 18:06:17.633465', 3) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (4, 4, 'Practica de SQL', '2025-06-19 18:06:17.633465', 4) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (5, 5, 'Exposicion final', '2025-06-19 18:06:17.633465', 5) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (6, 6, 'Hacer limpieza', '2025-06-19 18:06:17.633465', 2) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (7, 7, 'Trabajos', '2025-06-19 18:13:46.715902', 6) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (8, 8, 'Trabajos', '2025-06-19 19:04:57.770803', 6) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (9, 9, 'Ir a la Universidad', '2025-06-19 19:16:49.016357', 6) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (10, 10, 'Programar', '2025-06-19 19:17:40.925311', 6) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (11, 11, 'Fumar piedra', '2025-06-19 19:24:06.835973', 6) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (12, 12, 'Comer pollo', '2025-06-19 19:24:52.736196', 6) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (13, 13, 'Trabajos', '2025-06-19 19:27:02.836261', 6) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (14, 14, 'Comer pollo', '2025-06-21 17:58:29.595102', 6) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (15, 15, 'Trabajos', '2025-06-21 18:07:19.461427', 7) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (16, 16, 'Trabajos', '2025-06-21 18:08:41.268862', 7) ON CONFLICT DO NOTHING;
INSERT INTO public.log_tarea_creada (id_log, id_tarea, titulo, fecha_creacion, id_usuario) VALUES (17, 17, 'Bombardear Iran', '2025-06-21 20:32:07.035417', 7) ON CONFLICT DO NOTHING;

-- Set sequence values
SELECT pg_catalog.setval('public.categoria_cod_cat_seq', 34, true);
SELECT pg_catalog.setval('public.estado_cod_est_seq', 22, true);
SELECT pg_catalog.setval('public.log_tarea_creada_id_log_seq', 17, true);
SELECT pg_catalog.setval('public.prioridad_cod_prio_seq', 33, true);
SELECT pg_catalog.setval('public.recordatorio_cod_recor_seq', 31, true);
SELECT pg_catalog.setval('public.tarea_id_tarea_seq', 17, true);
SELECT pg_catalog.setval('public.tipo_cod_tipo_seq', 44, true);
SELECT pg_catalog.setval('public.usuario_id_usuario_seq', 7, true); 