-- Datos iniciales para la aplicación Plany

-- Estados de tareas
INSERT INTO estado (nombre_estado) VALUES ('Completada') ON CONFLICT DO NOTHING;
INSERT INTO estado (nombre_estado) VALUES ('Pendiente') ON CONFLICT DO NOTHING;

-- Tipos de tareas
INSERT INTO tipo (nombre_tipo) VALUES ('Personal') ON CONFLICT DO NOTHING;
INSERT INTO tipo (nombre_tipo) VALUES ('Trabajo') ON CONFLICT DO NOTHING;
INSERT INTO tipo (nombre_tipo) VALUES ('Estudio') ON CONFLICT DO NOTHING;
INSERT INTO tipo (nombre_tipo) VALUES ('Hogar') ON CONFLICT DO NOTHING;

-- Prioridades
INSERT INTO prioridad (nombre_prioridad) VALUES ('Baja') ON CONFLICT DO NOTHING;
INSERT INTO prioridad (nombre_prioridad) VALUES ('Media') ON CONFLICT DO NOTHING;
INSERT INTO prioridad (nombre_prioridad) VALUES ('Alta') ON CONFLICT DO NOTHING;

-- Categorías
INSERT INTO categoria (nombre_categoria) VALUES ('General') ON CONFLICT DO NOTHING;
INSERT INTO categoria (nombre_categoria) VALUES ('Urgente') ON CONFLICT DO NOTHING;
INSERT INTO categoria (nombre_categoria) VALUES ('Importante') ON CONFLICT DO NOTHING; 