const test = require('node:test');
const assert = require('node:assert/strict');

// Las pruebas necesitan una base separada y configurada por el usuario
const urlPrueba = process.env.TEST_DATABASE_URL;

test('login, roles, personal y asistencia', { skip: !urlPrueba }, async () => {
  process.env.DATABASE_URL = urlPrueba;
  process.env.JWT_SECRET = 'secreto-usado-solamente-en-la-prueba';
  const bcrypt = require('bcryptjs');
  const prisma = require('../src/prisma');
  const app = require('../src/app');
  const servidor = app.listen(0, '127.0.0.1');
  await new Promise((resolve) => servidor.once('listening', resolve));
  const base = `http://127.0.0.1:${servidor.address().port}`;
  const clave = 'prueba123';
  const identificador = Date.now().toString();
  const estancias = [];
  const usuarios = [];
  const personas = [];

  async function pedir(metodo, ruta, datos, token) {
    const headers = {};
    if (datos !== undefined) headers['Content-Type'] = 'application/json';
    if (token) headers.Authorization = `Bearer ${token}`;
    const respuesta = await fetch(base + ruta, {
      method: metodo,
      headers,
      body: datos === undefined ? undefined : JSON.stringify(datos),
    });
    let cuerpo = null;
    if (respuesta.status !== 204) cuerpo = await respuesta.json();
    return { status: respuesta.status, cuerpo };
  }

  try {
    const direccion = await prisma.usuario.create({
      data: {
        nombre: 'Direccion de prueba',
        correo: `direccion-${identificador}@example.com`,
        password: await bcrypt.hash(clave, 10),
        rol: 'direccion',
      },
    });
    usuarios.push(direccion.id);
    const usuarioGuardado = await prisma.usuario.findUnique({
      where: { id: direccion.id }, omit: { password: false },
    });
    assert.notEqual(usuarioGuardado.password, clave);
    assert.equal(await bcrypt.compare(clave, usuarioGuardado.password), true);

    let respuesta = await pedir('GET', '/perfil');
    assert.equal(respuesta.status, 401);
    respuesta = await pedir('POST', '/login', { correo: direccion.correo, password: 'incorrecta' });
    assert.equal(respuesta.status, 401);
    respuesta = await pedir('POST', '/login', { correo: direccion.correo, password: clave });
    assert.equal(respuesta.status, 200);
    const tokenDireccion = respuesta.cuerpo.token;
    respuesta = await pedir('GET', '/perfil', undefined, tokenDireccion);
    assert.equal(respuesta.cuerpo.rol, 'direccion');
    assert.equal('password' in respuesta.cuerpo, false);

    for (let numero = 1; numero <= 2; numero++) {
      respuesta = await pedir('POST', '/estancias', { nombre: `Prueba ${identificador}-${numero}` }, tokenDireccion);
      assert.equal(respuesta.status, 201);
      estancias.push(respuesta.cuerpo.id);
    }

    const tokens = [];
    for (let numero = 0; numero < 2; numero++) {
      const correo = `coordinacion-${identificador}-${numero}@example.com`;
      respuesta = await pedir('POST', '/usuarios', {
        nombre: `Coordinacion ${numero}`,
        correo,
        password: clave,
        rol: 'coordinacion',
        estancia_id: estancias[numero],
      }, tokenDireccion);
      assert.equal(respuesta.status, 201);
      assert.equal('password' in respuesta.cuerpo, false);
      usuarios.push(respuesta.cuerpo.id);
      respuesta = await pedir('POST', '/login', { correo, password: clave });
      assert.equal(respuesta.status, 200);
      tokens.push(respuesta.cuerpo.token);
    }

    respuesta = await pedir('GET', '/usuarios', undefined, tokens[0]);
    assert.equal(respuesta.status, 403);
    respuesta = await pedir('PUT', `/usuarios/${usuarios[1]}`, { nombre: 'Coordinacion editada' }, tokenDireccion);
    assert.equal(respuesta.status, 200);
    assert.equal(respuesta.cuerpo.nombre, 'Coordinacion editada');
    assert.equal('password' in respuesta.cuerpo, false);
    respuesta = await pedir('POST', '/personal', {
      nombre: 'Personal de prueba', puesto: 'Educadora', entrada: '08:00', salida: '16:00',
    }, tokens[0]);
    assert.equal(respuesta.status, 201);
    const personaId = respuesta.cuerpo.id;
    personas.push(personaId);
    assert.equal(respuesta.cuerpo.estancia_id, estancias[0]);

    respuesta = await pedir('GET', `/personal/${personaId}`, undefined, tokens[1]);
    assert.equal(respuesta.status, 404);
    respuesta = await pedir('GET', `/personal?estancia_id=${estancias[0]}`, undefined, tokens[1]);
    assert.equal(respuesta.status, 403);
    respuesta = await pedir('PUT', `/personal/${personaId}`, { entrada: '18:00' }, tokens[0]);
    assert.equal(respuesta.status, 400);
    respuesta = await pedir('PUT', `/personal/${personaId}`, { puesto: 'Cuidadora' }, tokens[0]);
    assert.equal(respuesta.status, 200);
    assert.equal(respuesta.cuerpo.puesto, 'Cuidadora');
    respuesta = await pedir('POST', '/asistencias', { personal_id: personaId, tipo: 'entrada' }, tokens[1]);
    assert.equal(respuesta.status, 404);
    respuesta = await pedir('POST', '/asistencias', { personal_id: personaId, tipo: 'salida' }, tokens[0]);
    assert.equal(respuesta.status, 409);
    respuesta = await pedir('POST', '/asistencias', { personal_id: personaId, tipo: 'entrada' }, tokens[0]);
    assert.equal(respuesta.status, 201);
    respuesta = await pedir('POST', '/asistencias', { personal_id: personaId, tipo: 'entrada' }, tokens[0]);
    assert.equal(respuesta.status, 409);
    respuesta = await pedir('POST', '/asistencias', { personal_id: personaId, tipo: 'salida' }, tokens[0]);
    assert.equal(respuesta.status, 200);
    assert.ok(respuesta.cuerpo.salida);
    respuesta = await pedir('POST', '/asistencias', { personal_id: personaId, tipo: 'salida' }, tokens[0]);
    assert.equal(respuesta.status, 409);
    respuesta = await pedir('GET', `/asistencias?estancia_id=${estancias[0]}`, undefined, tokenDireccion);
    assert.ok(respuesta.cuerpo.some((registro) => registro.personal_id === personaId));
    respuesta = await pedir('GET', '/asistencias', undefined, tokens[1]);
    assert.equal(respuesta.cuerpo.some((registro) => registro.personal_id === personaId), false);
    respuesta = await pedir('GET', '/asistencias?fecha=2026-02-30', undefined, tokenDireccion);
    assert.equal(respuesta.status, 400);

    respuesta = await pedir('POST', '/personal', {
      nombre: 'Segunda persona de prueba', puesto: 'Guardia', entrada: '08:00', salida: '16:00',
    }, tokens[0]);
    assert.equal(respuesta.status, 201);
    const otraPersona = respuesta.cuerpo.id;
    personas.push(otraPersona);
    const dosEntradas = await Promise.all([
      pedir('POST', '/asistencias', { personal_id: otraPersona, tipo: 'entrada' }, tokens[0]),
      pedir('POST', '/asistencias', { personal_id: otraPersona, tipo: 'entrada' }, tokens[0]),
    ]);
    assert.deepEqual(dosEntradas.map((registro) => registro.status).sort(), [201, 409]);

    respuesta = await pedir('DELETE', `/personal/${personaId}`, undefined, tokens[0]);
    assert.equal(respuesta.status, 204);
    respuesta = await pedir('POST', '/asistencias', { personal_id: personaId, tipo: 'entrada' }, tokens[0]);
    assert.equal(respuesta.status, 404);
    respuesta = await pedir('DELETE', `/usuarios/${usuarios[2]}`, undefined, tokenDireccion);
    assert.equal(respuesta.status, 204);
    respuesta = await pedir('GET', '/perfil', undefined, tokens[1]);
    assert.equal(respuesta.status, 401);
  } finally {
    await prisma.asistencia.deleteMany({ where: { personal_id: { in: personas } } });
    await prisma.personal.deleteMany({ where: { id: { in: personas } } });
    await prisma.usuario.deleteMany({ where: { id: { in: usuarios } } });
    await prisma.estancia.deleteMany({ where: { id: { in: estancias } } });
    await new Promise((resolve) => servidor.close(resolve));
    await prisma.$disconnect();
  }
});
