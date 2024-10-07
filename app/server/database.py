import motor.motor_asyncio
import os, logging
import pytz
from datetime import datetime
import httpx
from fastapi import HTTPException


def convert_to_peru_timezone(dt: datetime) -> datetime:
    peru_tz = pytz.timezone('America/Lima')
    return dt.astimezone(peru_tz)
def convert_to_sql_date_format(dt: datetime) -> str:
    return dt.strftime("%Y-%m-%d")

MONGO_HOST = os.getenv('MONGO_HOST')
MONGO_PORT = os.getenv('MONGO_PORT')
MONGO_DB = os.getenv('MONGO_DB')
MONGO_USER = os.getenv('MONGO_USER')
MONGO_PASSWORD = os.getenv('MONGO_PASSWORD')
MONGO_COLLECTION_PACIENTES = os.getenv('MONGO_COLLECTION_PACIENTES')

MONGO_URI = f"mongodb://{MONGO_USER}:{MONGO_PASSWORD}@{MONGO_HOST}:{MONGO_PORT}/"
client = motor.motor_asyncio.AsyncIOMotorClient(MONGO_URI)
database = client[MONGO_DB]
collection = database.get_collection(MONGO_COLLECTION_PACIENTES)

SERVICE_HOST = os.getenv('SERVICE_HOST')

# GET ALL
async def retrieve_pacientes():
    pacientes = []
    async for paciente in collection.find():
        pacientes.append(paciente)
    return pacientes


# PUT
async def insert_paciente(data: dict):
    try:
        # Insertar el paciente en MongoDB
        paciente = await collection.insert_one(data)
        new_paciente = await collection.find_one({"_id": paciente.inserted_id})

        # Crear la nueva historia clínica 
        async with httpx.AsyncClient() as client:
            current_time = convert_to_peru_timezone(datetime.now())

            historia_clinica_data = {
                "fechaCreacion": convert_to_sql_date_format(current_time),
                "dniPaciente": data["_id"]
            }

            response = await client.post(f"http://{SERVICE_HOST}:8080/historias-clinicas", json=historia_clinica_data)

            if response.status_code not in [200, 201]: 
                logging.error(f"Error al crear historia clínica, código de estado: {response.status_code}, detalles: {response.text}")
                raise HTTPException(status_code=500, detail="Error al crear la historia clínica en Spring Boot")

            logging.info(f"Historia clínica creada con éxito: {response.json()}")

        return new_paciente

    except Exception as e:
        logging.error(f"Error insertando paciente o creando historia clínica: {e}")
        raise HTTPException(status_code=500, detail=f"Error insertando paciente o creando historia clínica: {e}")




# GET BY ID
async def retrieve_paciente_by_id(id: str):
    paciente = await collection.find_one({"_id": id})
    if paciente:
        return paciente


# UPDATE
async def modify_paciente(id: str, data: dict):
    if len(data) < 1:
        return False
    paciente = await collection.find_one({"_id": id})
    if paciente:
        try:
            if 'fecha_nacimiento' in data:
                data['fecha_nacimiento'] = convert_to_peru_timezone(data['fecha_nacimiento'])
            if 'seguro' in data and 'vencimiento' in data['seguro']:
                data['seguro']['vencimiento'] = convert_to_peru_timezone(data['seguro']['vencimiento'])

            updated_paciente = await collection.update_one(
                {"_id": id}, {"$set": data}
            )
            if updated_paciente.modified_count > 0:
                return True
            return False
        except Exception as e:
            logging.error(f"Error actualizando paciente: {e}")
            raise e


# DELETE
async def remove_paciente(id: str):
    try:
        # Buscar el paciente
        paciente = await collection.find_one({"_id": id})
        if paciente:
            # Eliminar la historia clínica 
            async with httpx.AsyncClient() as client:
                response = await client.delete(f"http://{SERVICE_HOST}:8080/historias-clinicas/{id}")

                if response.status_code not in [200, 204]:  # Acepta 200 OK o 204 No Content
                    logging.error(
                        f"Error al eliminar la historia clínica, código de estado: {response.status_code}, detalles: {response.text}")
                    raise HTTPException(status_code=500, detail="Error al eliminar la historia clínica en Spring Boot")

                logging.info(f"Historia clínica eliminada con éxito para el paciente con DNI: {id}")

            # Eliminar el paciente de MongoDB
            await collection.delete_one({"_id": id})
            return True
        return False
    except Exception as e:
        logging.error(f"Error eliminando paciente o historia clínica: {e}")
        raise HTTPException(status_code=500, detail=f"Error eliminando paciente o historia clínica: {e}")
