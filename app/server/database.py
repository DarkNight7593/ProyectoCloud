from http.client import HTTPException
import motor.motor_asyncio

MONGO_URI = "mongodb://mongo:utec@107.22.167.68:8008/"
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
    paciente = await collection.insert_one(data)
    new_paciente = await collection.find_one({"_id": paciente.inserted_id})
    return new_paciente


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
        updated_paciente = await collection.update_one(
            {"_id": id}, {"$set": data}
        )
        if updated_paciente:
            return True
        return False


# DELETE
async def remove_paciente(id: str):
    student = await collection.find_one({"_id": id})
    if student:
        await collection.delete_one({"_id": id})
        return True
    return False


