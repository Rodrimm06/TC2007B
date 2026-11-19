from datetime import date, datetime
from sqlalchemy import Boolean, Date, DateTime, ForeignKey, Integer, Text, UniqueConstraint
from sqlalchemy.orm import Mapped, mapped_column
from database import Base


# Tabla de estancias
class Estancia(Base):
    __tablename__ = "estancias"

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    nombre: Mapped[str] = mapped_column(Text, unique=True)
    ubicacion: Mapped[str] = mapped_column(Text, default="")


# Tabla de usuarios de direccion y coordinacion
class Usuario(Base):
    __tablename__ = "usuarios"

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    nombre: Mapped[str] = mapped_column(Text)
    correo: Mapped[str] = mapped_column(Text, unique=True)
    password: Mapped[str] = mapped_column(Text)
    rol: Mapped[str] = mapped_column(Text)
    estancia_id: Mapped[int | None] = mapped_column(ForeignKey("estancias.id"))


# Tabla del personal de cada estancia
class Personal(Base):
    __tablename__ = "personal"

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    nombre: Mapped[str] = mapped_column(Text)
    puesto: Mapped[str] = mapped_column(Text)
    correo: Mapped[str] = mapped_column(Text, default="")
    telefono: Mapped[str] = mapped_column(Text, default="")
    entrada: Mapped[str] = mapped_column(Text)
    salida: Mapped[str] = mapped_column(Text)
    activo: Mapped[bool] = mapped_column(Boolean, default=True)
    estancia_id: Mapped[int] = mapped_column(ForeignKey("estancias.id"))


# Una persona tiene un registro de asistencia por dia
class Asistencia(Base):
    __tablename__ = "asistencias"
    __table_args__ = (UniqueConstraint("personal_id", "fecha"),)

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    personal_id: Mapped[int] = mapped_column(ForeignKey("personal.id"))
    fecha: Mapped[date] = mapped_column(Date)
    entrada: Mapped[datetime | None] = mapped_column(DateTime)
    salida: Mapped[datetime | None] = mapped_column(DateTime)
