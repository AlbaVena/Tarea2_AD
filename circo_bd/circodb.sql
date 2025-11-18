-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 17-11-2025 a las 17:07:13
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `circodb`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `artistas`
--

CREATE TABLE `artistas` (
  `id_artista` bigint(20) NOT NULL,
  `apodo` varchar(25) NOT NULL,
  `id_persona` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `artistas_numeros`
--

CREATE TABLE `artistas_numeros` (
  `id_artista` bigint(20) NOT NULL,
  `id_numero` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `artista_especialidad`
--

CREATE TABLE `artista_especialidad` (
  `id_artista` bigint(20) NOT NULL,
  `id_especialidad` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `coordinadores`
--

CREATE TABLE `coordinadores` (
  `id_coordinador` bigint(20) NOT NULL,
  `senior` tinyint(1) NOT NULL DEFAULT 0,
  `fechasenior` date DEFAULT NULL,
  `id_persona` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `credenciales`
--

CREATE TABLE `credenciales` (
  `id_credenciales` bigint(20) NOT NULL,
  `nombre` varchar(25) NOT NULL,
  `password` varchar(25) NOT NULL,
  `perfil` enum('Administrador','Artista','Coordinador','') NOT NULL,
  `id_persona` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `especialidades`
--

CREATE TABLE `especialidades` (
  `id_especialidad` int(11) NOT NULL,
  `nombre` varchar(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `espectaculos`
--

CREATE TABLE `espectaculos` (
  `id_espectaculo` bigint(20) NOT NULL,
  `nombre` varchar(25) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `id_coordinador` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `numeros`
--

CREATE TABLE `numeros` (
  `nombre` varchar(25) NOT NULL,
  `duracion` int(11) NOT NULL,
  `id_numero` int(11) NOT NULL,
  `id_espectaculo` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `personas`
--

CREATE TABLE `personas` (
  `id_persona` bigint(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `nombre` varchar(25) NOT NULL,
  `nacionalidad` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `artistas`
--
ALTER TABLE `artistas`
  ADD PRIMARY KEY (`id_artista`),
  ADD KEY `artistas_ibfk_1` (`id_persona`);

--
-- Indices de la tabla `artistas_numeros`
--
ALTER TABLE `artistas_numeros`
  ADD PRIMARY KEY (`id_artista`,`id_numero`),
  ADD KEY `id_numero` (`id_numero`);

--
-- Indices de la tabla `artista_especialidad`
--
ALTER TABLE `artista_especialidad`
  ADD PRIMARY KEY (`id_artista`,`id_especialidad`),
  ADD KEY `artista_especialidad_ibfk_2` (`id_especialidad`);

--
-- Indices de la tabla `coordinadores`
--
ALTER TABLE `coordinadores`
  ADD PRIMARY KEY (`id_coordinador`),
  ADD KEY `coordinadores_ibfk_1` (`id_persona`);

--
-- Indices de la tabla `credenciales`
--
ALTER TABLE `credenciales`
  ADD PRIMARY KEY (`id_credenciales`),
  ADD KEY `credenciales_ibfk_1` (`id_persona`);

--
-- Indices de la tabla `especialidades`
--
ALTER TABLE `especialidades`
  ADD PRIMARY KEY (`id_especialidad`);

--
-- Indices de la tabla `espectaculos`
--
ALTER TABLE `espectaculos`
  ADD PRIMARY KEY (`id_espectaculo`),
  ADD KEY `espectaculos_ibfk_1` (`id_coordinador`);

--
-- Indices de la tabla `numeros`
--
ALTER TABLE `numeros`
  ADD PRIMARY KEY (`id_numero`),
  ADD KEY `idEspectaculo` (`id_espectaculo`);

--
-- Indices de la tabla `personas`
--
ALTER TABLE `personas`
  ADD PRIMARY KEY (`id_persona`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `artistas`
--
ALTER TABLE `artistas`
  MODIFY `id_artista` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `coordinadores`
--
ALTER TABLE `coordinadores`
  MODIFY `id_coordinador` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `credenciales`
--
ALTER TABLE `credenciales`
  MODIFY `id_credenciales` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `especialidades`
--
ALTER TABLE `especialidades`
  MODIFY `id_especialidad` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `espectaculos`
--
ALTER TABLE `espectaculos`
  MODIFY `id_espectaculo` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `numeros`
--
ALTER TABLE `numeros`
  MODIFY `id_numero` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `personas`
--
ALTER TABLE `personas`
  MODIFY `id_persona` bigint(20) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
