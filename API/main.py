from datetime import date, datetime, time, timezone
from zoneinfo import ZoneInfo
from fastapi import Depends, FastAPI, HTTPException, Response
from sqlalchemy import select, update
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session
from auth import crear_token, guardar_password, revisar_password, solo_coordinacion, solo_direccion, verificar_token
from database import Base, engine, get_db
from models import Asistencia, Estancia, Personal, Usuario
from schemas import (
    AsistenciaConPersonal, AsistenciaCrear, AsistenciaOut,
    EstanciaCrear, EstanciaOut, LoginRequest, PersonalActualizar, PersonalCrear,
    PersonalOut, TokenResponse, UsuarioActualizar, UsuarioCrear, UsuarioOut,
)

app = FastAPI()
Base.metadata.create_all(bind=engine)


# Guarda los cambios y avisa si un dato ya existe
def guardar(db: Session):
    try:
        db.commit()
    except IntegrityError:
        db.rollback()
        raise HTTPException(409, "El dato o registro ya existe")


# Revisa un horario con formato de 24 horas
def hora_valida(hora: str) -> bool:
    try:
        return len(hora) == 5 and datetime.strptime(hora, "%H:%M").strftime("%H:%M") == hora
    except ValueError:
        return False


# Convierte las horas guardadas en UTC para enviarlas a la app
def datos_asistencia(asistencia: Asistencia) -> dict:
    entrada = asistencia.entrada
    salida = asistencia.salida
    if entrada:
        entrada = entrada.replace(tzinfo=timezone.utc)
    if salida:
        salida = salida.replace(tzinfo=timezone.utc)
    return {
        "id": asistencia.id,
        "personal_id": asistencia.personal_id,
        "fecha": datetime.combine(asistencia.fecha, time.min, tzinfo=timezone.utc),
        "entrada": entrada,
        "salida": salida,
    }


@app.get("/")
def inicio():
    return {"mensaje": "API de Sonrisas de Cristal funcionando"}


# Iniciar sesion y obtener un token
@app.post("/login", response_model=TokenResponse)
def login(datos: LoginRequest, db: Session = Depends(get_db)):
    correo = datos.correo.strip().lower()
    if not correo or not datos.password:
        raise HTTPException(400, "Faltan correo o password")
    usuario = db.scalar(select(Usuario).where(Usuario.correo == correo))
    if not usuario or not revisar_password(datos.password, usuario.password):
        raise HTTPException(401, "Correo o contrasena incorrectos")
    return {"token": crear_token(usuario)}


@app.get("/perfil", response_model=UsuarioOut)
def perfil(usuario: Usuario = Depends(verificar_token)):
    return usuario


# Direccion ve todas las estancias y coordinacion solo la suya
@app.get("/estancias", response_model=list[EstanciaOut])
def listar_estancias(usuario: Usuario = Depends(verificar_token), db: Session = Depends(get_db)):
    consulta = select(Estancia).order_by(Estancia.id)
    if usuario.rol == "coordinacion":
        consulta = consulta.where(Estancia.id == usuario.estancia_id)
    return db.scalars(consulta).all()


@app.post("/estancias", response_model=EstanciaOut, status_code=201)
def crear_estancia(datos: EstanciaCrear, usuario: Usuario = Depends(solo_direccion), db: Session = Depends(get_db)):
    if not datos.nombre.strip():
        raise HTTPException(400, "Falta el nombre")
    estancia = Estancia(nombre=datos.nombre.strip(), ubicacion=datos.ubicacion.strip())
    db.add(estancia)
    guardar(db)
    db.refresh(estancia)
    return estancia


# Solo direccion puede administrar los usuarios
@app.get("/usuarios", response_model=list[UsuarioOut])
def listar_usuarios(usuario: Usuario = Depends(solo_direccion), db: Session = Depends(get_db)):
    return db.scalars(select(Usuario).order_by(Usuario.id)).all()


@app.get("/usuarios/{id}", response_model=UsuarioOut)
def obtener_usuario(id: int, usuario: Usuario = Depends(solo_direccion), db: Session = Depends(get_db)):
    encontrado = db.get(Usuario, id)
    if not encontrado:
        raise HTTPException(404, "Usuario no encontrado")
    return encontrado


@app.post("/usuarios", response_model=UsuarioOut, status_code=201)
def crear_usuario(datos: UsuarioCrear, usuario: Usuario = Depends(solo_direccion), db: Session = Depends(get_db)):
    if not datos.nombre.strip() or not datos.correo.strip() or datos.rol not in ["direccion", "coordinacion"]:
        raise HTTPException(400, "Datos de usuario no validos")
    if datos.rol == "coordinacion":
        if not datos.estancia_id:
            raise HTTPException(400, "La coordinacion necesita una estancia")
        if not db.get(Estancia, datos.estancia_id):
            raise HTTPException(404, "Estancia no encontrada")
    elif datos.estancia_id is not None:
        raise HTTPException(400, "Direccion no debe tener una estancia asignada")
    nuevo = Usuario(
        nombre=datos.nombre.strip(), correo=datos.correo.strip().lower(),
        password=guardar_password(datos.password), rol=datos.rol, estancia_id=datos.estancia_id,
    )
    db.add(nuevo)
    guardar(db)
    db.refresh(nuevo)
    return nuevo


@app.put("/usuarios/{id}", response_model=UsuarioOut)
def actualizar_usuario(id: int, datos: UsuarioActualizar, usuario: Usuario = Depends(solo_direccion), db: Session = Depends(get_db)):
    encontrado = db.get(Usuario, id)
    if not encontrado:
        raise HTTPException(404, "Usuario no encontrado")
    cambios = datos.model_dump(exclude_unset=True)
    nombre = cambios.get("nombre", encontrado.nombre)
    correo = cambios.get("correo", encontrado.correo)
    rol = cambios.get("rol", encontrado.rol)
    if not isinstance(nombre, str) or not nombre.strip() or not isinstance(correo, str) or not correo.strip() or rol not in ["direccion", "coordinacion"]:
        raise HTTPException(400, "Datos de usuario no validos")
    if id == usuario.id and rol != "direccion":
        raise HTTPException(400, "No puedes cambiar tu propio rol")
    estancia_id = cambios.get("estancia_id", encontrado.estancia_id)
    if rol == "coordinacion":
        if not estancia_id:
            raise HTTPException(400, "La coordinacion necesita una estancia")
        if not db.get(Estancia, estancia_id):
            raise HTTPException(404, "Estancia no encontrada")
    else:
        if cambios.get("estancia_id") is not None:
            raise HTTPException(400, "Direccion no debe tener una estancia asignada")
        estancia_id = None
    encontrado.nombre = nombre.strip()
    encontrado.correo = correo.strip().lower()
    encontrado.rol = rol
    encontrado.estancia_id = estancia_id
    if "password" in cambios:
        if cambios["password"] is None:
            raise HTTPException(400, "Contrasena no valida")
        encontrado.password = guardar_password(cambios["password"])
    guardar(db)
    db.refresh(encontrado)
    return encontrado


@app.delete("/usuarios/{id}", status_code=204)
def borrar_usuario(id: int, usuario: Usuario = Depends(solo_direccion), db: Session = Depends(get_db)):
    if id == usuario.id:
        raise HTTPException(400, "No puedes borrar tu propio usuario")
    encontrado = db.get(Usuario, id)
    if not encontrado:
        raise HTTPException(404, "Usuario no encontrado")
    db.delete(encontrado)
    guardar(db)
    return Response(status_code=204)


# Direccion ve todo el personal y coordinacion solo el de su estancia
@app.get("/personal", response_model=list[PersonalOut])
def listar_personal(estancia_id: int | None = None, usuario: Usuario = Depends(verificar_token), db: Session = Depends(get_db)):
    if estancia_id is not None and estancia_id <= 0:
        raise HTTPException(400, "Estancia no valida")
    if usuario.rol == "coordinacion":
        if not usuario.estancia_id or (estancia_id and estancia_id != usuario.estancia_id):
            raise HTTPException(403, "Solo puedes consultar tu estancia")
        estancia_id = usuario.estancia_id
    consulta = select(Personal).order_by(Personal.id)
    if estancia_id:
        consulta = consulta.where(Personal.estancia_id == estancia_id)
    return db.scalars(consulta).all()


@app.get("/personal/{id}", response_model=PersonalOut)
def obtener_personal(id: int, usuario: Usuario = Depends(verificar_token), db: Session = Depends(get_db)):
    persona = db.get(Personal, id)
    if not persona or (usuario.rol == "coordinacion" and persona.estancia_id != usuario.estancia_id):
        raise HTTPException(404, "Persona no encontrada")
    return persona


# La coordinadora agrega personal a su propia estancia
@app.post("/personal", response_model=PersonalOut, status_code=201)
def crear_personal(datos: PersonalCrear, usuario: Usuario = Depends(solo_coordinacion), db: Session = Depends(get_db)):
    if not datos.nombre.strip() or not datos.puesto.strip():
        raise HTTPException(400, "Faltan nombre o puesto")
    if not hora_valida(datos.entrada) or not hora_valida(datos.salida) or datos.entrada >= datos.salida:
        raise HTTPException(400, "Horario no valido")
    persona = Personal(
        nombre=datos.nombre.strip(), puesto=datos.puesto.strip(), correo=datos.correo,
        telefono=datos.telefono, entrada=datos.entrada, salida=datos.salida, estancia_id=usuario.estancia_id,
    )
    db.add(persona)
    guardar(db)
    db.refresh(persona)
    return persona


@app.put("/personal/{id}", response_model=PersonalOut)
def actualizar_personal(id: int, datos: PersonalActualizar, usuario: Usuario = Depends(solo_coordinacion), db: Session = Depends(get_db)):
    persona = db.get(Personal, id)
    if not persona or persona.estancia_id != usuario.estancia_id:
        raise HTTPException(404, "Persona no encontrada")
    cambios = datos.model_dump(exclude_unset=True)
    nombre = cambios.get("nombre", persona.nombre)
    puesto = cambios.get("puesto", persona.puesto)
    entrada = cambios.get("entrada", persona.entrada)
    salida = cambios.get("salida", persona.salida)
    if not isinstance(nombre, str) or not nombre.strip() or not isinstance(puesto, str) or not puesto.strip():
        raise HTTPException(400, "Faltan nombre o puesto")
    if not isinstance(entrada, str) or not isinstance(salida, str) or not hora_valida(entrada) or not hora_valida(salida) or entrada >= salida:
        raise HTTPException(400, "Horario no valido")
    for campo, valor in cambios.items():
        if valor is None:
            raise HTTPException(400, "Datos de personal no validos")
        setattr(persona, campo, valor)
    persona.nombre = nombre.strip()
    persona.puesto = puesto.strip()
    guardar(db)
    db.refresh(persona)
    return persona


# Dar de baja sin borrar las asistencias
@app.delete("/personal/{id}", status_code=204)
def borrar_personal(id: int, usuario: Usuario = Depends(solo_coordinacion), db: Session = Depends(get_db)):
    persona = db.get(Personal, id)
    if not persona or persona.estancia_id != usuario.estancia_id:
        raise HTTPException(404, "Persona no encontrada")
    persona.activo = False
    guardar(db)
    return Response(status_code=204)


# Consultar las asistencias de un dia
@app.get("/asistencias", response_model=list[AsistenciaConPersonal])
def listar_asistencias(fecha: str | None = None, estancia_id: int | None = None, usuario: Usuario = Depends(verificar_token), db: Session = Depends(get_db)):
    if estancia_id is not None and estancia_id <= 0:
        raise HTTPException(400, "Estancia no valida")
    if usuario.rol == "coordinacion":
        if not usuario.estancia_id or (estancia_id and estancia_id != usuario.estancia_id):
            raise HTTPException(403, "Solo puedes consultar tu estancia")
        estancia_id = usuario.estancia_id
    if fecha is None:
        fecha = datetime.now(ZoneInfo("America/Mexico_City")).date()
    else:
        try:
            if len(fecha) != 10 or fecha[4] != "-" or fecha[7] != "-":
                raise ValueError()
            fecha = date.fromisoformat(fecha)
        except ValueError:
            raise HTTPException(400, "Fecha no valida; usa AAAA-MM-DD")
    consulta = select(Asistencia, Personal).join(Personal).where(Asistencia.fecha == fecha).order_by(Asistencia.id)
    if estancia_id:
        consulta = consulta.where(Personal.estancia_id == estancia_id)
    registros = []
    for asistencia, persona in db.execute(consulta).all():
        registro = datos_asistencia(asistencia)
        registro["personal"] = persona
        registros.append(registro)
    return registros


# Registrar la entrada o salida de hoy
@app.post("/asistencias", response_model=AsistenciaOut)
def registrar_asistencia(datos: AsistenciaCrear, response: Response, usuario: Usuario = Depends(solo_coordinacion), db: Session = Depends(get_db)):
    if datos.personal_id <= 0 or datos.tipo not in ["entrada", "salida"]:
        raise HTTPException(400, "Indica una persona y el tipo de registro")
    persona = db.get(Personal, datos.personal_id)
    if not persona or persona.estancia_id != usuario.estancia_id or not persona.activo:
        raise HTTPException(404, "Persona no encontrada o inactiva")
    fecha = datetime.now(ZoneInfo("America/Mexico_City")).date()
    ahora = datetime.now(timezone.utc).replace(tzinfo=None)
    if datos.tipo == "entrada":
        asistencia = Asistencia(personal_id=persona.id, fecha=fecha, entrada=ahora)
        db.add(asistencia)
        guardar(db)
        db.refresh(asistencia)
        response.status_code = 201
        return datos_asistencia(asistencia)

    # Solo registra la salida si existe una entrada y no tiene salida
    resultado = db.execute(
        update(Asistencia)
        .where(Asistencia.personal_id == persona.id, Asistencia.fecha == fecha,
               Asistencia.entrada.is_not(None), Asistencia.salida.is_(None))
        .values(salida=ahora)
    )
    if resultado.rowcount == 0:
        raise HTTPException(409, "Falta la entrada o la salida ya fue registrada")
    guardar(db)
    asistencia = db.scalar(select(Asistencia).where(Asistencia.personal_id == persona.id, Asistencia.fecha == fecha))
    return datos_asistencia(asistencia)
