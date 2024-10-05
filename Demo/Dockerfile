# Usa una imagen base de Node.js
FROM node:14

# Establece el directorio de trabajo en el contenedor
WORKDIR /usr/src/app

# Copia el archivo package.json y package-lock.json al directorio de trabajo
COPY package*.json ./

# Instala las dependencias
RUN npm install

# Copia el resto de tu código fuente al contenedor
COPY . .

# Expone el puerto en el que corre tu aplicación (por defecto el 3000 en Express)
EXPOSE 3000

# Define el comando para correr tu aplicación
CMD ["npm", "start"]
