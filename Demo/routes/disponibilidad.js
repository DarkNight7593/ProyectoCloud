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

// Obtener la disponibilidad de un doctor específico
router.get('/:dni', async (req, res) => {
    const { dni } = req.params;
    try {
        const result = await pool.query(
            'SELECT * FROM Disponibilidad WHERE dni_doctor = $1',
            [dni]
        );
        res.json(result.rows);
    } catch (err) {
        res.status(500).send('Error al obtener la disponibilidad del doctor');
    }
});

// Agregar disponibilidad para un doctor
router.post('/', async (req, res) => {
    const { dia, hora, dni_doctor } = req.body;
    try {
        await pool.query(
            'INSERT INTO Disponibilidad (dia, hora, dni_doctor) VALUES ($1, $2, $3)',
            [dia, hora, dni_doctor]
        );
        res.send('Disponibilidad agregada con éxito');
    } catch (err) {
        res.status(500).send('Error al agregar la disponibilidad');
    }
});

module.exports = router;
