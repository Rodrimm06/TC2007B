import os
from datetime import datetime, timedelta, timezone
import bcrypt
import jwt
from fastapi import Depends, Header, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from models import Usuario

JWT_SECRET = os.getenv("JWT_SECRET")
if not JWT_SECRET:
    raise RuntimeError("Falta JWT_SECRET en el archivo .env")


# Guarda la contrasena protegida con bcrypt
def guardar_password(password: str) -> str:
    if len(password) < 8 or len(password.encode("utf-8")) > 72:
        raise HTTPException(400, "La contrasena debe tener al menos 8 caracteres y maximo 72 bytes")
    return bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")


def revisar_password(password: str, password_guardado: str) -> bool:
    if len(password.encode("utf-8")) > 72:
        return False
    try:
        return bcrypt.checkpw(password.encode("utf-8"), password_guardado.encode("utf-8"))
    except ValueError:
        return False


# Crea un token que dura una hora
def crear_token(usuario: Usuario) -> str:
    payload = {
        "id": usuario.id,
        "rol": usuario.rol,
        "exp": datetime.now(timezone.utc) + timedelta(hours=1),
    }
    return jwt.encode(payload, JWT_SECRET, algorithm="HS256")


# Revisa el token y busca al usuario antes de entrar a la ruta
def verificar_token(authorization: str | None = Header(default=None), db: Session = Depends(get_db)) -> Usuario:
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(401, "Falta el token")
    try:
        datos = jwt.decode(authorization[7:], JWT_SECRET, algorithms=["HS256"])
    except jwt.PyJWTError:
        raise HTTPException(401, "Token no valido")
    id_usuario = datos.get("id")
    if not isinstance(id_usuario, int):
        raise HTTPException(401, "Token no valido")
    usuario = db.get(Usuario, id_usuario)
    if not usuario or usuario.rol not in ["direccion", "coordinacion"]:
        raise HTTPException(401, "Usuario no encontrado")
    return usuario


# Solo direccion administra usuarios y estancias
def solo_direccion(usuario: Usuario = Depends(verificar_token)) -> Usuario:
    if usuario.rol != "direccion":
        raise HTTPException(403, "Solo direccion puede hacer esto")
    return usuario


# Solo coordinacion registra asistencia y modifica personal
def solo_coordinacion(usuario: Usuario = Depends(verificar_token)) -> Usuario:
    if usuario.rol != "coordinacion" or not usuario.estancia_id:
        raise HTTPException(403, "Solo coordinacion puede hacer esto")
    return usuario
