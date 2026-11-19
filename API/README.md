# API de Sonrisas de Cristal

API para la app de TC2007B. Usa Node.js, Express, Prisma, PostgreSQL y JWT, como el ejemplo de la clase.

La API guarda usuarios, estancias, personal y registros de asistencia. Los datos de prueba de la app todavia estan en memoria: **la app aun no esta conectada a esta API**.

## Archivos

- `src/index.js`: rutas de la API e inicio del servidor.
- `src/prisma.js`: conexion con la base de datos.
- `src/middleware/auth.js`: revisa el token y el rol.
- `prisma/schema.prisma`: modelos de la base de datos.
- `prisma/migrations/`: tablas de la base de datos.
- `prisma/seed.js`: crea el primer usuario de direccion.
- `pruebas.http`: ejemplos de peticiones.

## Requisitos

- Node.js 20.19 o superior.
- Una base de datos PostgreSQL separada para este proyecto.
- Los datos de conexion de esa base.

## Preparar la API

Abre una terminal en la carpeta `API`:

1. Copia `.env.example` a un archivo llamado `.env`.
2. En `.env`, cambia `DATABASE_URL` por la conexion de tu base de datos. Configura tambien un `JWT_SECRET` largo y unico, y los datos `ADMIN_NOMBRE`, `ADMIN_CORREO` y `ADMIN_PASSWORD` para el primer usuario de direccion. La contraseña debe tener al menos 8 caracteres y como maximo 72 bytes.
3. Ejecuta:

```powershell
npm install
npx prisma migrate deploy
npm run seed
npm run dev
```

Abre `http://localhost:3000/` para comprobar que el servidor responde. `npm run seed` solo crea el usuario inicial si no existe; no cambia su contraseña cuando ya existe.

**No subas `.env` a GitHub**: contiene la conexion y el secreto. La carpeta `node_modules` tampoco se sube.

## Rutas

| Metodo | Ruta | Uso | Acceso |
| --- | --- | --- | --- |
| GET | `/` | Comprobar la API | Publico |
| POST | `/login` | Enviar `correo` y `password`; devuelve `token` | Publico |
| GET | `/perfil` | Ver el usuario de la sesion | Direccion y coordinacion |
| GET | `/estancias` | Ver estancias | Direccion ve todas; coordinacion ve la suya |
| POST | `/estancias` | Crear una estancia | Direccion |
| GET, POST | `/usuarios` | Consultar o crear usuarios | Direccion |
| GET, PUT, DELETE | `/usuarios/:id` | Consultar, editar o borrar un usuario | Direccion |
| GET | `/personal` | Consultar personal; admite `?estancia_id=1` | Direccion ve todo; coordinacion ve su estancia |
| GET | `/personal/:id` | Consultar una persona | Direccion o coordinacion de la estancia |
| POST | `/personal` | Agregar personal a la estancia de la coordinadora | Coordinacion |
| PUT | `/personal/:id` | Editar una persona de su estancia | Coordinacion |
| DELETE | `/personal/:id` | Dar de baja sin borrar asistencias | Coordinacion |
| GET | `/asistencias` | Consultar registros; admite `?fecha=AAAA-MM-DD` y `?estancia_id=1` | Direccion ve todo; coordinacion ve su estancia |
| POST | `/asistencias` | Registrar entrada o salida del dia | Coordinacion de la estancia |

Las rutas protegidas necesitan el encabezado `Authorization: Bearer TU_TOKEN`. El token vence en una hora. Las contraseñas se guardan con bcrypt y no aparecen en las respuestas.

Para crear un usuario de coordinacion, primero crea su estancia y envia su `estancia_id`. Un usuario de direccion no tiene una estancia asignada. Las operaciones para agregar o modificar personal usan la estancia del usuario que inicio sesion, aunque el cliente mande otra.

Para registrar asistencia, envia `personal_id` y `tipo`, con `tipo` igual a `entrada` o `salida`. La API usa la fecha actual de Ciudad de Mexico y la hora del servidor. Primero debe existir la entrada; no se permiten entradas o salidas duplicadas el mismo dia. La consulta solo devuelve registros existentes: una persona sin entrada no aparece en esa lista.

La baja de personal conserva el historial y cambia `activo` a `false`; no elimina el registro de la base.

## Conectar la app despues

La API todavia no tiene una URL publica y no modifica la app automaticamente. Para usarla desde Android se necesita configurar Retrofit y adaptar los campos. La app actual tiene nombres como `name` y `residence_id`; la API responde nombres como `nombre` y `estancia_id`. En el emulador Android, una API que corre en la misma computadora se alcanza con `http://10.0.2.2:3000/`; el trafico HTTP sin cifrar es solo para pruebas locales.

## Pruebas

El archivo `pruebas.http` contiene peticiones de ejemplo para probar la API, como en el material de clase. Usa una base de datos de prueba y cambia los datos y tokens del archivo antes de enviar las peticiones.
