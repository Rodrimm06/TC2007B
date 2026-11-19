# API de Sonrisas de Cristal

API para la app de TC2007B, hecha con Python, FastAPI, SQLAlchemy, PostgreSQL y JWT. La estructura sigue el ejemplo de FastAPI de la clase.

La API guarda usuarios, estancias, personal y asistencia. La app Android todavia utiliza datos de demostracion y **no esta conectada a esta API**.

## Archivos

- `main.py`: rutas de la API.
- `models.py`: tablas de la base de datos con SQLAlchemy.
- `schemas.py`: datos que reciben y regresan las rutas.
- `database.py`: conexion a PostgreSQL.
- `auth.py`: contrasenas, tokens y permisos.
- `seed.py`: crea el primer usuario de direccion.
- `requirements.txt`: dependencias de Python.
- `pruebas.http`: peticiones de ejemplo.

## Requisitos

- Python 3.10 o superior.
- Una base de datos PostgreSQL separada para este proyecto.
- Los datos de conexion de esa base.

## Preparar la API

Abre una terminal en la carpeta `API`:

1. Copia `.env.example` a un archivo llamado `.env`.
2. En `.env`, cambia `DATABASE_URL` por la conexion de tu base de datos. Configura un `JWT_SECRET` largo y unico, y los datos `ADMIN_NOMBRE`, `ADMIN_CORREO` y `ADMIN_PASSWORD` para el primer usuario de direccion. La contrasena debe tener al menos 8 caracteres y como maximo 72 bytes.
3. Crea un entorno de Python e instala las dependencias:

```powershell
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.txt
```

4. Crea el primer usuario y ejecuta la API:

```powershell
.\.venv\Scripts\python.exe seed.py
.\.venv\Scripts\python.exe -m uvicorn main:app --reload --port 3000
```

En esta laptop ya hay un entorno `.venv` preparado. Si el comando `python` de Windows no esta disponible, se puede crear el entorno con `uv venv --python 3.12 .venv` e instalar las dependencias con `uv pip install --python .venv\Scripts\python.exe -r requirements.txt`.

Abre `http://localhost:3000/` para comprobar el servidor o `http://localhost:3000/docs` para consultar las rutas. En las rutas protegidas de `/docs`, escribe `Bearer TU_TOKEN` en el campo `authorization`.

Al iniciar, SQLAlchemy crea las tablas que no existen. Como se explica en clase, `create_all` **no modifica tablas existentes** cuando se cambia el modelo; para eso se necesitan migraciones. `seed.py` no cambia la contrasena del usuario inicial si ya existe.

**No subas `.env` a GitHub**: contiene la conexion y el secreto. Tampoco se sube `.venv`.

## Rutas

| Metodo | Ruta | Uso | Acceso |
| --- | --- | --- | --- |
| GET | `/` | Comprobar la API | Publico |
| POST | `/login` | Enviar `correo` y `password`; devuelve `token` | Publico |
| GET | `/perfil` | Ver el usuario de la sesion | Direccion y coordinacion |
| GET | `/estancias` | Ver estancias | Direccion ve todas; coordinacion ve la suya |
| POST | `/estancias` | Crear una estancia | Direccion |
| GET, POST | `/usuarios` | Consultar o crear usuarios | Direccion |
| GET, PUT, DELETE | `/usuarios/{id}` | Consultar, editar o borrar un usuario | Direccion |
| GET | `/personal` | Consultar personal; admite `?estancia_id=1` | Direccion ve todo; coordinacion ve su estancia |
| GET | `/personal/{id}` | Consultar una persona | Direccion o coordinacion de la estancia |
| POST | `/personal` | Agregar personal a la estancia de la coordinadora | Coordinacion |
| PUT | `/personal/{id}` | Editar una persona de su estancia | Coordinacion |
| DELETE | `/personal/{id}` | Dar de baja sin borrar asistencias | Coordinacion |
| GET | `/asistencias` | Consultar registros; admite `?fecha=AAAA-MM-DD` y `?estancia_id=1` | Direccion ve todo; coordinacion ve su estancia |
| POST | `/asistencias` | Registrar entrada o salida del dia | Coordinacion de la estancia |

Las rutas protegidas necesitan el encabezado `Authorization: Bearer TU_TOKEN`. El token vence en una hora. Las contrasenas se guardan con bcrypt y no aparecen en las respuestas.

Para crear un usuario de coordinacion, primero crea su estancia y envia su `estancia_id`. Un usuario de direccion no tiene una estancia asignada. Las operaciones para agregar o modificar personal usan la estancia del usuario que inicio sesion.

Para registrar asistencia, envia `personal_id` y `tipo`, con `tipo` igual a `entrada` o `salida`. La API usa la fecha actual de Ciudad de Mexico y la hora del servidor. Primero debe existir la entrada; no se permiten entradas o salidas duplicadas el mismo dia. La consulta solo devuelve registros existentes: una persona sin entrada no aparece en esa lista.

La baja de personal cambia `activo` a `false` y conserva el historial.

## Pruebas y conexion con Android

El archivo `pruebas.http` contiene peticiones de ejemplo. Usa una base de prueba y cambia los datos y tokens antes de enviar las peticiones. FastAPI devuelve `422` cuando el tipo de un campo no coincide con lo que espera.

La API todavia no tiene una URL publica. Para usarla desde Android se necesita configurar Retrofit y adaptar los campos: la app actual usa nombres como `name` y `residence_id`; la API responde con `nombre` y `estancia_id`. En un emulador Android, una API que corre en la misma computadora se alcanza con `http://10.0.2.2:3000/`. El trafico HTTP sin cifrar es solo para pruebas locales.
