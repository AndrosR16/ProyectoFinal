# Guia de despliegue

La aplicacion se publica como imagen Docker (ver `Dockerfile` en la raiz).
Es agnostica de plataforma: estas instrucciones cubren Railway (recomendada)
y una alternativa con Render + base de datos externa.

## Railway (recomendado)

### 1. Crear el proyecto

1. En [Railway](https://railway.app), **New Project → Deploy from GitHub repo**.
2. Selecciona el repositorio del proyecto. Railway detecta el `Dockerfile` y
   construye la imagen automaticamente (no hace falta configurar buildpacks).

### 2. Agregar la base de datos

1. Dentro del mismo proyecto, **New → Database → Add MySQL**.
2. Railway crea el plugin de MySQL y expone variables propias
   (`MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD`).

### 3. Variables de entorno del servicio (la app)

En el servicio de la aplicacion (no en el de MySQL), pestaña **Variables**,
agrega:

- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQLDATABASE}`
- `DB_USERNAME=${MYSQLUSER}`
- `DB_PASSWORD=${MYSQLPASSWORD}`
- `SEED_ADMIN_PASSWORD=<contraseña fuerte>`
- `SEED_EMPLEADO_PASSWORD=<contraseña fuerte>`

La sintaxis `${VARIABLE}` es la de referencias de Railway: al escribirla en
la UI, Railway la resuelve automaticamente contra las variables del plugin
de MySQL del mismo proyecto (no hace falta copiar los valores a mano, y si
Railway rota la contraseña la referencia sigue funcionando).

No es necesario definir `PORT`: Railway la inyecta sola y
`application-prod.properties` ya la respeta (`server.port=${PORT:8080}`).

**Nota de seguridad**: defina `SEED_ADMIN_PASSWORD` y `SEED_EMPLEADO_PASSWORD`
con contraseñas fuertes antes del primer despliegue. Si no las define, la
aplicación genera contraseñas temporales en el primer arranque y las escribe
en el log; búsquelas ahí y cámbielas desde /perfil tras iniciar sesión. Ningún
valor de contraseña se guarda en el repositorio.

### 4. Dominio publico

En el servicio de la app, pestaña **Settings → Networking → Generate Domain**.
Railway entrega una URL publica tipo `https://<servicio>.up.railway.app`
sin configuracion adicional.

### 5. Deploy

Cada push a la rama configurada dispara un build y deploy automaticos.
El primer build tarda mas porque descarga dependencias de Maven; builds
posteriores reutilizan cache de capas si el `pom.xml` no cambio.

## Alternativa: Render + base de datos externa (Aiven free)

1. Crea una base MySQL gratuita en [Aiven](https://aiven.io/) (plan free tier)
   y copia host, puerto, base de datos, usuario y password.
2. En [Render](https://render.com), **New → Web Service**, conecta el repo y
   elige **Environment: Docker** (usa el mismo `Dockerfile`).
3. En **Environment Variables** del servicio agrega:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `SPRING_DATASOURCE_URL=jdbc:mysql://<host-aiven>:<puerto>/<basedatos>`
   - `DB_USERNAME=<usuario-aiven>`
   - `DB_PASSWORD=<password-aiven>`
4. Render tambien inyecta `PORT` automaticamente; no requiere ajuste extra.
5. Aiven suele exigir SSL: si la conexion falla por certificado, agrega a la
   URL `?useSSL=true&requireSSL=true` (o el parametro equivalente que indique
   el panel de Aiven para tu instancia).

## Troubleshooting

- **Cold start / la primera carga tarda**: en planes gratuitos el contenedor
  puede dormirse por inactividad (mas comun en Render que en Railway). La
  primera peticion tras el sueño puede tardar varios segundos mientras
  arranca la JVM y Spring Boot; es esperable, no es un error.
- **Logs**: en Railway, pestaña **Deployments → View Logs** del servicio;
  en Render, pestaña **Logs** del servicio. Ahi se ve si el fallo es de
  conexion a la base de datos (credenciales/URL mal referenciadas) o de la
  aplicacion.
- **Datos de prueba**: `DataSeeder` (`src/main/java/.../config/DataSeeder.java`)
  puebla datos de ejemplo (medicamentos, clientes, proveedores, usuarios) en
  el primer arranque contra una base vacia. Es idempotente: en reinicios
  posteriores detecta que ya existen datos y no los duplica.
- **`ddl-auto=update` no crea el esquema esperado**: si la base externa no
  esta realmente vacia (por ejemplo, quedaron tablas de una prueba previa
  con un esquema distinto), Hibernate puede fallar al intentar alterarlas.
  En ese caso, limpia la base antes del primer deploy.
