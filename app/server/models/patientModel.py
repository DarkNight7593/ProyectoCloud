from pydantic import BaseModel, Field
from typing import Optional
from datetime import date

class SeguroModel(BaseModel):
    tipo_seguro: str
    vencimiento: date

class PacienteModel(BaseModel):
    dni: str = Field(..., alias="_id")
    nombres: str
    apellidos: str
    fecha_nacimiento: date
    seguro: Optional[SeguroModel] = None

    class Config:
        allow_population_by_field_name = True

class PacienteUpdate(BaseModel):
    nombres: Optional[str] = None
    apellidos: Optional[str] = None
    fecha_nacimiento: Optional[date] = None
    seguro: Optional[SeguroModel] = None

def response_model(data, message):
    return {
        "data": data,
        "code": 200,
        "message": message
    }

def error_response_model(data, code, message):
    return {
        "data": data,
        "code": code,
        "message": message
    }