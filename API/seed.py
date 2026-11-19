import os
from sqlalchemy import select
from auth import guardar_password
from database import Base, SessionLocal, engine
from models import Usuario

# Crea el primer usuario de direccion si todavia no existe
def main():
    nombre = os.getenv("ADMIN_NOMBRE", "").strip()
    correo = os.getenv("ADMIN_CORREO", "").strip().lower()
    password = os.getenv("ADMIN_PASSWORD", "")
    if not nombre or not correo or not password:
        raise SystemExit("Configura ADMIN_NOMBRE, ADMIN_CORREO y ADMIN_PASSWORD en .env")

    Base.metadata.create_all(bind=engine)
    db = SessionLocal()
    try:
        existe = db.scalar(select(Usuario).where(Usuario.correo == correo))
        if existe:
            print("El usuario inicial ya existe")
            return
        usuario = Usuario(nombre=nombre, correo=correo, password=guardar_password(password), rol="direccion")
        db.add(usuario)
        db.commit()
        print("Usuario inicial de direccion creado")
    finally:
        db.close()


if __name__ == "__main__":
    main()
