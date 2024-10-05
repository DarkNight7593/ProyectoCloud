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

# mongodb://mongo:utec@107.22.167.68:8008/
MONGO_URI = "mongodb://localhost:27017/"
client = motor.motor_asyncio.AsyncIOMotorClient(MONGO_URI)
database = client.hospital
collection = database.get_collection("Pacientes")


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

        # Crear la nueva historia clínica en Spring Boot
        async with httpx.AsyncClient() as client:
            current_time = convert_to_peru_timezone(datetime.now())

            historia_clinica_data = {
                "fechaCreacion": convert_to_sql_date_format(current_time),
                "dniPaciente": data["_id"]
            }

            response = await client.post("http://localhost:8080/historias-clinicas", json=historia_clinica_data)

            if response.status_code not in [200, 201]:  # Acepta 200 OK o 201 Created
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
    student = await collection.find_one({"_id": id})
    if student:
        await collection.delete_one({"_id": id})
        return True
    return False
