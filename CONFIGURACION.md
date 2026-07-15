# Configuración del servidor

La API ya no contiene la contraseña ni la IP de PostgreSQL dentro del repositorio.
Configura estas variables antes de iniciar:

```powershell
$env:DB_URL = "jdbc:postgresql://IP_POSTGRES:5432/clincadb"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "TU_CLAVE"
$env:SERVER_ADDRESS = "0.0.0.0"
$env:SERVER_PORT = "8080"
.\mvnw.cmd spring-boot:run
```

Si la base de datos ya contiene las tablas `pacientes` y `medicos`, ejecuta una
vez `sql/migracion_estado_activo.sql`. En una instalación vacía,
`spring.jpa.hibernate.ddl-auto=update` crea las columnas automáticamente.

El cliente Swing no necesita CORS. El control de acceso de red debe realizarse
con Tailscale y el firewall del servidor.
