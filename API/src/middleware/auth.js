const jwt = require('jsonwebtoken');
const prisma = require('../prisma');

// Revisa el token antes de entrar a las rutas protegidas
async function verificarToken(req, res, next) {
  const header = req.headers.authorization;
  if (!header || !header.startsWith('Bearer ')) {
    return res.status(401).json({ error: 'Falta el token' });
  }

  let datos;
  try {
    datos = jwt.verify(header.slice(7), process.env.JWT_SECRET);
  } catch (error) {
    return res.status(401).json({ error: 'Token no valido' });
  }

  const usuario = await prisma.usuario.findUnique({
    where: { id: datos.id },
  });
  if (!usuario || !['direccion', 'coordinacion'].includes(usuario.rol)) {
    return res.status(401).json({ error: 'Usuario no encontrado' });
  }
  req.usuario = usuario;
  next();
}

// Solo direccion puede administrar los usuarios
function soloDireccion(req, res, next) {
  if (req.usuario.rol !== 'direccion') {
    return res.status(403).json({ error: 'Solo direccion puede hacer esto' });
  }
  next();
}

// Solo coordinacion puede modificar el personal y la asistencia
function soloCoordinacion(req, res, next) {
  if (req.usuario.rol !== 'coordinacion' || !req.usuario.estancia_id) {
    return res.status(403).json({ error: 'Solo coordinacion puede hacer esto' });
  }
  next();
}

module.exports = { verificarToken, soloDireccion, soloCoordinacion };
