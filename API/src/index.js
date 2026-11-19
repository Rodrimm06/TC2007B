require('dotenv/config');

if (!process.env.DATABASE_URL || !process.env.JWT_SECRET) {
  console.error('Faltan DATABASE_URL o JWT_SECRET en el archivo .env');
  process.exit(1);
}
const express = require('express');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const prisma = require('./prisma');
const { verificarToken, soloDireccion, soloCoordinacion } = require('./middleware/auth');

const app = express();
app.use(express.json());

// Convierte el id de una ruta a numero
function leerId(valor) {
  const id = Number(valor);
  if (!Number.isInteger(id) || id <= 0) return null;
  return id;
}

// Valida horas con formato de 24 horas
function horaValida(hora) {
  return typeof hora === 'string' && /^([01]\d|2[0-3]):[0-5]\d$/.test(hora);
}

// Fecha de hoy para la estancia, usando la hora de Mexico
function fechaHoy() {
  const texto = new Intl.DateTimeFormat('en-CA', {
    timeZone: 'America/Mexico_City', year: 'numeric', month: '2-digit', day: '2-digit',
  }).format(new Date());
  return new Date(texto + 'T00:00:00.000Z');
}

// Convierte YYYY-MM-DD a una fecha para la base de datos
function leerFecha(texto) {
  if (typeof texto !== 'string' || !/^\d{4}-\d{2}-\d{2}$/.test(texto)) return null;
  const fecha = new Date(texto + 'T00:00:00.000Z');
  if (Number.isNaN(fecha.getTime()) || fecha.toISOString().slice(0, 10) !== texto) return null;
  return fecha;
}

// Ruta para comprobar que funciona el servidor
app.get('/', (req, res) => {
  res.json({ mensaje: 'API de Sonrisas de Cristal funcionando' });
});

// Iniciar sesion y obtener un token
app.post('/login', async (req, res) => {
  const { correo, password } = req.body || {};
  if (typeof correo !== 'string' || typeof password !== 'string' || !correo.trim() || !password) {
    return res.status(400).json({ error: 'Faltan correo o password' });
  }
  const usuario = await prisma.usuario.findUnique({
    where: { correo: correo.trim().toLowerCase() },
    omit: { password: false },
  });
  if (!usuario || !(await bcrypt.compare(password, usuario.password))) {
    return res.status(401).json({ error: 'Correo o contrasena incorrectos' });
  }
  const token = jwt.sign({ id: usuario.id, rol: usuario.rol }, process.env.JWT_SECRET, { expiresIn: '1h' });
  res.json({ token });
});

// Las rutas de abajo necesitan el token
app.use(verificarToken);

app.get('/perfil', async (req, res) => {
  res.json(req.usuario);
});

// Direccion puede ver todas las estancias y coordinacion solo la suya
app.get('/estancias', async (req, res) => {
  const filtro = req.usuario.rol === 'coordinacion' ? { id: req.usuario.estancia_id || -1 } : {};
  const estancias = await prisma.estancia.findMany({ where: filtro, orderBy: { id: 'asc' } });
  res.json(estancias);
});

// Crear una estancia
app.post('/estancias', soloDireccion, async (req, res) => {
  const { nombre, ubicacion } = req.body || {};
  if (typeof nombre !== 'string' || !nombre.trim() || (ubicacion !== undefined && typeof ubicacion !== 'string')) {
    return res.status(400).json({ error: 'Datos de estancia no validos' });
  }
  const estancia = await prisma.estancia.create({
    data: { nombre: nombre.trim(), ubicacion: ubicacion ? ubicacion.trim() : '' },
  });
  res.status(201).json(estancia);
});

// Solo direccion puede ver y crear usuarios
app.get('/usuarios', soloDireccion, async (req, res) => {
  const usuarios = await prisma.usuario.findMany({ orderBy: { id: 'asc' } });
  res.json(usuarios);
});

app.get('/usuarios/:id', soloDireccion, async (req, res) => {
  const id = leerId(req.params.id);
  if (!id) return res.status(400).json({ error: 'ID no valido' });
  const usuario = await prisma.usuario.findUnique({ where: { id } });
  if (!usuario) return res.status(404).json({ error: 'Usuario no encontrado' });
  res.json(usuario);
});

app.post('/usuarios', soloDireccion, async (req, res) => {
  const { nombre, correo, password, rol, estancia_id } = req.body || {};
  // bcrypt solo toma los primeros 72 bytes de la contrasena
  if (typeof nombre !== 'string' || !nombre.trim() || typeof correo !== 'string' || !correo.trim() ||
      typeof password !== 'string' || password.length < 8 || Buffer.byteLength(password, 'utf8') > 72 ||
      !['direccion', 'coordinacion'].includes(rol)) {
    return res.status(400).json({ error: 'Datos de usuario no validos' });
  }
  let idEstancia = null;
  if (rol === 'coordinacion') {
    idEstancia = leerId(estancia_id);
    if (!idEstancia) return res.status(400).json({ error: 'La coordinacion necesita una estancia' });
    const estancia = await prisma.estancia.findUnique({ where: { id: idEstancia } });
    if (!estancia) return res.status(404).json({ error: 'Estancia no encontrada' });
  } else if (estancia_id != null) {
    return res.status(400).json({ error: 'Direccion no debe tener una estancia asignada' });
  }
  const passwordHash = await bcrypt.hash(password, 10);
  const usuario = await prisma.usuario.create({
    data: { nombre: nombre.trim(), correo: correo.trim().toLowerCase(), password: passwordHash, rol, estancia_id: idEstancia },
  });
  res.status(201).json(usuario);
});

// Actualizar un usuario
app.put('/usuarios/:id', soloDireccion, async (req, res) => {
  const id = leerId(req.params.id);
  if (!id) return res.status(400).json({ error: 'ID no valido' });
  const usuario = await prisma.usuario.findUnique({ where: { id } });
  if (!usuario) return res.status(404).json({ error: 'Usuario no encontrado' });

  const { nombre, correo, password, rol, estancia_id } = req.body || {};
  const nuevoNombre = nombre === undefined ? usuario.nombre : nombre;
  const nuevoCorreo = correo === undefined ? usuario.correo : correo;
  const nuevoRol = rol === undefined ? usuario.rol : rol;
  if (typeof nuevoNombre !== 'string' || !nuevoNombre.trim() || typeof nuevoCorreo !== 'string' || !nuevoCorreo.trim() ||
      !['direccion', 'coordinacion'].includes(nuevoRol) ||
      (password !== undefined && (typeof password !== 'string' || password.length < 8 || Buffer.byteLength(password, 'utf8') > 72))) {
    return res.status(400).json({ error: 'Datos de usuario no validos' });
  }
  if (id === req.usuario.id && nuevoRol !== 'direccion') {
    return res.status(400).json({ error: 'No puedes cambiar tu propio rol' });
  }
  let idEstancia = estancia_id === undefined ? usuario.estancia_id : estancia_id;
  if (nuevoRol === 'coordinacion') {
    idEstancia = leerId(idEstancia);
    if (!idEstancia) return res.status(400).json({ error: 'La coordinacion necesita una estancia' });
    const estancia = await prisma.estancia.findUnique({ where: { id: idEstancia } });
    if (!estancia) return res.status(404).json({ error: 'Estancia no encontrada' });
  } else {
    if (estancia_id != null) return res.status(400).json({ error: 'Direccion no debe tener una estancia asignada' });
    idEstancia = null;
  }
  const datos = { nombre: nuevoNombre.trim(), correo: nuevoCorreo.trim().toLowerCase(), rol: nuevoRol, estancia_id: idEstancia };
  if (password !== undefined) datos.password = await bcrypt.hash(password, 10);
  const actualizado = await prisma.usuario.update({ where: { id }, data: datos });
  res.json(actualizado);
});

// Borrar un usuario
app.delete('/usuarios/:id', soloDireccion, async (req, res) => {
  const id = leerId(req.params.id);
  if (!id) return res.status(400).json({ error: 'ID no valido' });
  if (id === req.usuario.id) return res.status(400).json({ error: 'No puedes borrar tu propio usuario' });
  const usuario = await prisma.usuario.findUnique({ where: { id } });
  if (!usuario) return res.status(404).json({ error: 'Usuario no encontrado' });
  await prisma.usuario.delete({ where: { id } });
  res.status(204).send();
});

// Consultar personal de una estancia
app.get('/personal', async (req, res) => {
  let estanciaId = null;
  if (req.query.estancia_id !== undefined) {
    estanciaId = leerId(req.query.estancia_id);
    if (!estanciaId) return res.status(400).json({ error: 'Estancia no valida' });
  }
  // La coordinadora solo puede ver el personal de su estancia
  if (req.usuario.rol === 'coordinacion') {
    if (!req.usuario.estancia_id || (estanciaId && estanciaId !== req.usuario.estancia_id)) {
      return res.status(403).json({ error: 'Solo puedes consultar tu estancia' });
    }
    estanciaId = req.usuario.estancia_id;
  }
  const filtro = {};
  if (estanciaId) filtro.estancia_id = estanciaId;
  const personal = await prisma.personal.findMany({ where: filtro, orderBy: { id: 'asc' } });
  res.json(personal);
});

app.get('/personal/:id', async (req, res) => {
  const id = leerId(req.params.id);
  if (!id) return res.status(400).json({ error: 'ID no valido' });
  const persona = await prisma.personal.findUnique({ where: { id } });
  if (!persona || (req.usuario.rol === 'coordinacion' && persona.estancia_id !== req.usuario.estancia_id)) {
    return res.status(404).json({ error: 'Persona no encontrada' });
  }
  res.json(persona);
});

// Agregar personal a la estancia de la coordinadora
app.post('/personal', soloCoordinacion, async (req, res) => {
  const { nombre, puesto, correo, telefono, entrada, salida } = req.body || {};
  if (typeof nombre !== 'string' || !nombre.trim() || typeof puesto !== 'string' || !puesto.trim() ||
      (correo !== undefined && typeof correo !== 'string') || (telefono !== undefined && typeof telefono !== 'string') ||
      !horaValida(entrada) || !horaValida(salida) || entrada >= salida) {
    return res.status(400).json({ error: 'Datos de personal no validos' });
  }
  const persona = await prisma.personal.create({
    data: { nombre: nombre.trim(), puesto: puesto.trim(), correo: correo || '', telefono: telefono || '', entrada, salida, estancia_id: req.usuario.estancia_id },
  });
  res.status(201).json(persona);
});

// Actualizar los datos o el estado de una persona
app.put('/personal/:id', soloCoordinacion, async (req, res) => {
  const id = leerId(req.params.id);
  if (!id) return res.status(400).json({ error: 'ID no valido' });
  const persona = await prisma.personal.findUnique({ where: { id } });
  if (!persona || persona.estancia_id !== req.usuario.estancia_id) {
    return res.status(404).json({ error: 'Persona no encontrada' });
  }
  const { nombre, puesto, correo, telefono, entrada, salida, activo } = req.body || {};
  const nuevoNombre = nombre === undefined ? persona.nombre : nombre;
  const nuevoPuesto = puesto === undefined ? persona.puesto : puesto;
  const nuevaEntrada = entrada === undefined ? persona.entrada : entrada;
  const nuevaSalida = salida === undefined ? persona.salida : salida;
  if (typeof nuevoNombre !== 'string' || !nuevoNombre.trim() || typeof nuevoPuesto !== 'string' || !nuevoPuesto.trim() ||
      (correo !== undefined && typeof correo !== 'string') || (telefono !== undefined && typeof telefono !== 'string') ||
      (activo !== undefined && typeof activo !== 'boolean') || !horaValida(nuevaEntrada) || !horaValida(nuevaSalida) || nuevaEntrada >= nuevaSalida) {
    return res.status(400).json({ error: 'Datos de personal no validos' });
  }
  const actualizado = await prisma.personal.update({
    where: { id },
    data: { nombre: nuevoNombre.trim(), puesto: nuevoPuesto.trim(), correo, telefono, entrada: nuevaEntrada, salida: nuevaSalida, activo },
  });
  res.json(actualizado);
});

// Dar de baja sin borrar los registros de asistencia
app.delete('/personal/:id', soloCoordinacion, async (req, res) => {
  const id = leerId(req.params.id);
  if (!id) return res.status(400).json({ error: 'ID no valido' });
  const persona = await prisma.personal.findUnique({ where: { id } });
  if (!persona || persona.estancia_id !== req.usuario.estancia_id) {
    return res.status(404).json({ error: 'Persona no encontrada' });
  }
  await prisma.personal.update({ where: { id }, data: { activo: false } });
  res.status(204).send();
});

// Consultar la asistencia de un dia
app.get('/asistencias', async (req, res) => {
  let estanciaId = null;
  if (req.query.estancia_id !== undefined) {
    estanciaId = leerId(req.query.estancia_id);
    if (!estanciaId) return res.status(400).json({ error: 'Estancia no valida' });
  }
  // La coordinadora solo puede consultar la asistencia de su estancia
  if (req.usuario.rol === 'coordinacion') {
    if (!req.usuario.estancia_id || (estanciaId && estanciaId !== req.usuario.estancia_id)) {
      return res.status(403).json({ error: 'Solo puedes consultar tu estancia' });
    }
    estanciaId = req.usuario.estancia_id;
  }
  const fecha = req.query.fecha === undefined ? fechaHoy() : leerFecha(req.query.fecha);
  if (!fecha) return res.status(400).json({ error: 'Fecha no valida; usa YYYY-MM-DD' });
  const filtro = { fecha };
  if (estanciaId) filtro.personal = { estancia_id: estanciaId };
  const asistencias = await prisma.asistencia.findMany({
    where: filtro, include: { personal: true }, orderBy: { id: 'asc' },
  });
  res.json(asistencias);
});

// Registrar la entrada o salida de hoy
app.post('/asistencias', soloCoordinacion, async (req, res) => {
  const { personal_id, tipo } = req.body || {};
  const id = leerId(personal_id);
  if (!id || !['entrada', 'salida'].includes(tipo)) {
    return res.status(400).json({ error: 'Indica una persona y el tipo de registro' });
  }
  const persona = await prisma.personal.findUnique({ where: { id } });
  if (!persona || persona.estancia_id !== req.usuario.estancia_id || !persona.activo) {
    return res.status(404).json({ error: 'Persona no encontrada o inactiva' });
  }
  const fecha = fechaHoy();
  const ahora = new Date();
  if (tipo === 'entrada') {
    const asistencia = await prisma.asistencia.create({
      data: { personal_id: id, fecha, entrada: ahora },
    });
    return res.status(201).json(asistencia);
  }
  // Solo guarda la salida si todavia no se ha registrado
  const actualizados = await prisma.asistencia.updateMany({
    where: { personal_id: id, fecha, entrada: { not: null }, salida: null },
    data: { salida: ahora },
  });
  if (actualizados.count === 0) {
    return res.status(409).json({ error: 'Falta la entrada o la salida ya fue registrada' });
  }
  const asistencia = await prisma.asistencia.findUnique({ where: { personal_id_fecha: { personal_id: id, fecha } } });
  res.json(asistencia);
});

// Respuestas para errores de la base de datos
app.use((error, req, res, next) => {
  if (error.type === 'entity.parse.failed') return res.status(400).json({ error: 'JSON no valido' });
  if (error.code === 'P2002') return res.status(409).json({ error: 'El dato o registro ya existe' });
  if (error.name === 'PrismaClientValidationError') return res.status(400).json({ error: 'Datos no validos' });
  console.error(error);
  res.status(500).json({ error: 'Error interno' });
});

const PORT = Number(process.env.PORT || 3000);
app.listen(PORT, () => {
  console.log(`API en http://localhost:${PORT}`);
});
