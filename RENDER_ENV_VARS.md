# Variables de Entorno para Render - Plany

## Variables Requeridas

### Configuración Básica
| Variable | Valor | Descripción |
|----------|-------|-------------|
| `PORT` | `8081` | Puerto interno de la aplicación (Render lo mapea automáticamente) |
| `SPRING_PROFILES_ACTIVE` | `prod` | Perfil de Spring Boot para producción |
| `JAVA_VERSION` | `21` | Versión de Java |

### Configuración de Java
| Variable | Valor | Descripción |
|----------|-------|-------------|
| `JAVA_OPTS` | `-Xmx512m -Xms256m` | Opciones de memoria para JVM |

### Base de Datos Railway
| Variable | Valor | Descripción |
|----------|-------|-------------|
| `DATABASE_URL` | `jdbc:postgresql://yamanote.proxy.rlwy.net:40166/railway` | URL completa de conexión a PostgreSQL (formato JDBC) |
| `DB_USERNAME` | `postgres` | Usuario de la base de datos |
| `DB_PASSWORD` | `pAiBYcudZpLsxtirnCwPmiHzSPTVhlsn` | Contraseña de la base de datos |

### Configuración de Spring Boot
| Variable | Valor | Descripción |
|----------|-------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://yamanote.proxy.rlwy.net:40166/railway` | URL de la base de datos (formato JDBC) |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Usuario de la base de datos |
| `SPRING_DATASOURCE_PASSWORD` | `pAiBYcudZpLsxtirnCwPmiHzSPTVhlsn` | Contraseña de la base de datos |
| `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | `org.postgresql.Driver` | Driver de PostgreSQL |
| `SPRING_JPA_DATABASE_PLATFORM` | `org.hibernate.dialect.PostgreSQLDialect` | Dialecto de Hibernate |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Estrategia de actualización de esquema |
| `SPRING_JPA_SHOW_SQL` | `false` | No mostrar SQL en logs de producción |

### Configuración de Logging
| Variable | Valor | Descripción |
|----------|-------|-------------|
| `LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB` | `INFO` | Nivel de log para Spring Web |
| `LOGGING_LEVEL_ORG_HIBERNATE_SQL` | `WARN` | Nivel de log para SQL de Hibernate |
| `LOGGING_LEVEL_COM_ZAXXER_HIKARI` | `INFO` | Nivel de log para pool de conexiones |

### Configuración de CORS
| Variable | Valor | Descripción |
|----------|-------|-------------|
| `SPRING_WEB_CORS_ALLOWED_ORIGINS` | `*` | Orígenes permitidos para CORS |
| `SPRING_WEB_CORS_ALLOWED_METHODS` | `GET,POST,PUT,DELETE,OPTIONS` | Métodos HTTP permitidos |
| `SPRING_WEB_CORS_ALLOWED_HEADERS` | `*` | Headers permitidos |

## Configuración en Render Dashboard

### Pasos para configurar:
1. Ve a tu servicio en Render Dashboard
2. Navega a "Environment" en el menú lateral
3. Agrega cada variable de entorno con su valor correspondiente
4. Guarda los cambios
5. Render automáticamente hará un nuevo despliegue

### Variables Opcionales (para desarrollo)
| Variable | Valor | Descripción |
|----------|-------|-------------|
| `SPRING_JPA_SHOW_SQL` | `true` | Mostrar SQL en logs (solo para debug) |
| `LOGGING_LEVEL_ROOT` | `DEBUG` | Nivel de log general (solo para debug) |

## Notas Importantes

1. **Formato JDBC**: Las URLs de la base de datos DEBEN usar el formato `jdbc:postgresql://` no `postgresql://`

2. **Seguridad**: Las credenciales de la base de datos están hardcodeadas en este archivo. En un entorno de producción real, deberías usar variables de entorno secretas.

3. **Puerto**: Render automáticamente mapea el puerto interno (8081) al puerto externo (80/443).

4. **Health Check**: La aplicación expone un endpoint de health check en `/` para que Render pueda verificar el estado.

5. **CORS**: La configuración permite todas las orígenes (`*`). En producción, deberías especificar solo los dominios permitidos.

6. **Memoria**: La aplicación está configurada para usar máximo 512MB de RAM. Ajusta según tus necesidades.

## Troubleshooting

### Si la aplicación no inicia:
- Verifica que todas las variables de entorno estén configuradas
- Revisa los logs en Render Dashboard
- Asegúrate de que la base de datos Railway esté activa

### Si hay errores de conexión a la base de datos:
- **IMPORTANTE**: Verifica que las URLs usen el formato `jdbc:postgresql://` no `postgresql://`
- Verifica que las credenciales de Railway sean correctas
- Asegúrate de que la base de datos esté accesible desde Render
- Revisa los logs de conexión en Railway

### Si el frontend no se conecta al backend:
- Verifica que la URL en `config.js` sea correcta
- Asegúrate de que CORS esté configurado correctamente
- Revisa la consola del navegador para errores de red 