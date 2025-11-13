-- --------------------------------------------------------
-- Base de datos: `muebleria_db`
-- --------------------------------------------------------

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS `muebleria_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `muebleria_db`;

--
-- Eliminar tablas si existen (en orden inverso por las claves foráneas)
--
DROP TABLE IF EXISTS `cotizacion_detalle`;
DROP TABLE IF EXISTS `cotizacion`;
DROP TABLE IF EXISTS `variacion`;
DROP TABLE IF EXISTS `mueble`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mueble` (Req. 3)
--
CREATE TABLE `mueble` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `tipo` varchar(100) DEFAULT NULL,
  `precio_base` decimal(10,2) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `estado` enum('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  `tamano` enum('GRANDE','MEDIANO','PEQUENO') NOT NULL DEFAULT 'MEDIANO',
  `material` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `variacion` (Req. 4)
--
CREATE TABLE `variacion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `aumento_precio` decimal(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_unico` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cotizacion` (Req. 5)
--
CREATE TABLE `cotizacion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha` datetime NOT NULL DEFAULT current_timestamp(),
  `estado` enum('PENDIENTE','VENDIDA') NOT NULL DEFAULT 'PENDIENTE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cotizacion_detalle` (Req. 5 - Tabla intermedia)
--
CREATE TABLE `cotizacion_detalle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cantidad` int(11) NOT NULL,
  `cotizacion_id` bigint(20) NOT NULL,
  `mueble_id` bigint(20) NOT NULL,
  `variacion_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_detalle_cotizacion` (`cotizacion_id`),
  KEY `fk_detalle_mueble` (`mueble_id`),
  KEY `fk_detalle_variacion` (`variacion_id`),
  CONSTRAINT `fk_detalle_cotizacion` FOREIGN KEY (`cotizacion_id`) REFERENCES `cotizacion` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_detalle_mueble` FOREIGN KEY (`mueble_id`) REFERENCES `mueble` (`id`),
  CONSTRAINT `fk_detalle_variacion` FOREIGN KEY (`variacion_id`) REFERENCES `variacion` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Inserción de datos de ejemplo (para facilitar las pruebas)
--
INSERT INTO `mueble` (`nombre`, `tipo`, `precio_base`, `stock`, `estado`, `tamano`, `material`) VALUES
('Silla Clásica', 'Silla', 50000.00, 20, 'ACTIVO', 'MEDIANO', 'Madera'),
('Mesa de Comedor', 'Mesa', 200000.00, 10, 'ACTIVO', 'GRANDE', 'Roble'),
('Sillón Relax', 'Sillón', 150000.00, 5, 'ACTIVO', 'GRANDE', 'Tela'),
('Estante Biblioteca', 'Estante', 80000.00, 15, 'ACTIVO', 'GRANDE', 'Melamina'),
('Cajonera Noche', 'Cajón', 35000.00, 30, 'INACTIVO', 'PEQUENO', 'Pino');

INSERT INTO `variacion` (`nombre`, `aumento_precio`) VALUES
('Barniz Premium', 15000.00),
('Cojines de Seda', 25000.00),
('Ruedas de Goma', 5000.00);

INSERT INTO `cotizacion` (`id`, `estado`) VALUES
(1, 'PENDIENTE');

INSERT INTO `cotizacion_detalle` (`cantidad`, `cotizacion_id`, `mueble_id`, `variacion_id`) VALUES
(2, 1, 1, 1), -- 2 Sillas Clásicas con Barniz Premium
(1, 1, 2, NULL); -- 1 Mesa de Comedor (normal, sin variación)