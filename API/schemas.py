from datetime import datetime
from pydantic import BaseModel, ConfigDict


class LoginRequest(BaseModel):
    correo: str
    password: str


class TokenResponse(BaseModel):
    token: str


class EstanciaCrear(BaseModel):
    nombre: str
    ubicacion: str = ""


class EstanciaOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    nombre: str
    ubicacion: str


class UsuarioCrear(BaseModel):
    nombre: str
    correo: str
    password: str
    rol: str
    estancia_id: int | None = None


class UsuarioActualizar(BaseModel):
    nombre: str | None = None
    correo: str | None = None
    password: str | None = None
    rol: str | None = None
    estancia_id: int | None = None


# La contrasena no se incluye en las respuestas
class UsuarioOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    nombre: str
    correo: str
    rol: str
    estancia_id: int | None


class PersonalCrear(BaseModel):
    nombre: str
    puesto: str
    correo: str = ""
    telefono: str = ""
    entrada: str
    salida: str


class PersonalActualizar(BaseModel):
    nombre: str | None = None
    puesto: str | None = None
    correo: str | None = None
    telefono: str | None = None
    entrada: str | None = None
    salida: str | None = None
    activo: bool | None = None


class PersonalOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    nombre: str
    puesto: str
    correo: str
    telefono: str
    entrada: str
    salida: str
    activo: bool
    estancia_id: int


class AsistenciaCrear(BaseModel):
    personal_id: int
    tipo: str


class AsistenciaOut(BaseModel):
    id: int
    personal_id: int
    fecha: datetime
    entrada: datetime | None
    salida: datetime | None


class AsistenciaConPersonal(AsistenciaOut):
    personal: PersonalOut
