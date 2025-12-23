-- Armadillo Challenge Database Schema
-- Sistema de Reserva de Salas de Coworking

-- Crear la base de datos (ejecutar como superusuario)
CREATE DATABASE armadillo_db;

-- Conectarse a armadillo_db antes de ejecutar lo siguiente

-- Tabla de salas
CREATE TABLE rooms (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    capacity INTEGER,
    location VARCHAR(100),
    equipment TEXT,
    status VARCHAR(50)
);

-- Tabla de reservas
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    room_id INTEGER,
    responsible_name VARCHAR(100),
    contact_email VARCHAR(100),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    purpose TEXT
);

-- Datos de ejemplo (opcional)
INSERT INTO rooms (name, capacity, location, equipment, status) VALUES
('Sala Innovación', 10, 'Piso 1', 'Proyector, Pizarra', 'disponible'),
('Sala Creatividad', 6, 'Piso 2', 'TV, Pizarra', 'disponible');
