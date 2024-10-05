# Usar una imagen base de Python
FROM python:3.10-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el código de la aplicación
COPY . .

# Instala las dependencias necesarias
RUN pip install --no-cache-dir fastapi uvicorn motor pymongo
# Comando para ejecutar el servidor de FastAPI con Uvicorn
CMD ["python3", "-m", "uvicorn", "server.app:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]

