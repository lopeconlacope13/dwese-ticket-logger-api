-- ===============================
-- 1. REGIONES Y PROVINCIAS
-- ===============================

-- Tabla: Comunidades Autónomas
CREATE TABLE IF NOT EXISTS regions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    image VARCHAR(100)
);

-- Tabla: Provincias
CREATE TABLE IF NOT EXISTS provinces (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);


-- ===============================
-- 2. PRODUCTOS Y TICKETS
-- ===============================

-- Tabla: Productos
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

-- Tabla: Tickets
CREATE TABLE IF NOT EXISTS tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL,
    discount DECIMAL(5,2) NOT NULL,
    location_id INT
);

-- Tabla intermedia: relación N:M productos - tickets
CREATE TABLE IF NOT EXISTS product_ticket (
    product_id INT NOT NULL,
    ticket_id INT NOT NULL,
    PRIMARY KEY (product_id, ticket_id),

    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);


-- ===============================
-- 3. USUARIOS Y SEGURIDAD
-- ===============================

-- Tabla: Usuarios
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL,

    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    image VARCHAR(255),
    code VARCHAR(10),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    last_password_change_date TIMESTAMP
);

-- Tabla: Roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla intermedia: relación N:M usuarios - roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
