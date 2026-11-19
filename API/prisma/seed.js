require('dotenv/config');
const bcrypt = require('bcryptjs');
const prisma = require('../src/prisma');

// Crea el primer usuario de direccion si todavia no existe
async function main() {
  const { ADMIN_NOMBRE, ADMIN_CORREO, ADMIN_PASSWORD } = process.env;
  // bcrypt solo toma los primeros 72 bytes de la contrasena
  if (!ADMIN_NOMBRE || !ADMIN_CORREO || !ADMIN_PASSWORD || ADMIN_PASSWORD.length < 8 || Buffer.byteLength(ADMIN_PASSWORD, 'utf8') > 72) {
    throw new Error('Configura ADMIN_NOMBRE, ADMIN_CORREO y ADMIN_PASSWORD (8 a 72 bytes) en .env');
  }
  const correo = ADMIN_CORREO.trim().toLowerCase();
  const existe = await prisma.usuario.findUnique({ where: { correo } });
  if (existe) {
    console.log('El usuario inicial ya existe');
    return;
  }
  const password = await bcrypt.hash(ADMIN_PASSWORD, 10);
  await prisma.usuario.create({
    data: { nombre: ADMIN_NOMBRE.trim(), correo, password, rol: 'direccion' },
  });
  console.log('Usuario inicial de direccion creado');
}

main()
  .catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  })
  .finally(() => prisma.$disconnect());
