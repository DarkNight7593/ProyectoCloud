   const express = require('express');
const router = express.Router();
const { Pool } = require('pg');

// Configuración de la conexión a PostgreSQL
const pool = new Pool({
    user: 'postgres',
    host: '107.22.167.68',
    database: 'hospital',
    password: 'utec',
    port: 8007, // El puerto por defecto de PostgreSQL
});
pool.connect((err, client, release) => {
    if (err) {
        return console.error('Error al conectar a la base de datos:', err.stack);
    }
    console.log('Conexión exitosa con la base de datos PostgreSQL');
    release();
});

// Obtener todos los doctores
router.get('/', async (req, res) => {
    try {
        const result = await pool.query('SELECT * FROM Doctor');
        res.json(result.rows);
    } catch (err) {
        res.status(500).send('Error al obtener los doctores');
    }
});

// Agregar un doctor
router.post('/', async (req, res) => {
    const { dni, nombres, apellidos, especialidad } = req.body;
    try {
        await pool.query(
            'INSERT INTO Doctor (dni, nombres, apellidos, especialidad) VALUES ($1, $2, $3, $4)',
            [dni, nombres, apellidos, especialidad]
        );
        res.send('Doctor agregado con éxito');
    } catch (err) {
        res.status(500).send('Error al agregar el doctor');
    }
});

module.exports = router;
