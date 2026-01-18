-- Inserts de las Comunidades Autónomas, ignora si se produce un error en la insercción
INSERT IGNORE INTO regions (id, code, name) VALUES
(1, '01', 'ANDALUCÍA'),
(2, '02', 'ARAGÓN'),
(3, '03', 'ASTURIAS'),
(4, '04', 'BALEARES'),
(5, '05', 'CANARIAS'),
(6, '06', 'CANTABRIA'),
(7, '07', 'CASTILLA Y LEÓN'),
(8, '08', 'CASTILLA-LA MANCHA'),
(9, '09', 'CATALUÑA'),
(10, '10', 'COMUNIDAD VALENCIANA'),
(11, '11', 'EXTREMADURA'),
(12, '12', 'GALICIA'),
(13, '13', 'MADRID'),
(14, '14', 'MURCIA'),
(15, '15', 'NAVARRA'),
(16, '16', 'PAÍS VASCO'),
(17, '17', 'LA RIOJA'),
(18, '18', 'CEUTA Y MELILLA');

-- Inserts de las Provincias, ignora si se produce un error en la insercción
INSERT IGNORE INTO provinces (id, code, name) VALUES
(1, '01', 'ÁLAVA'),
(2, '02', 'ALBACETE'),
(3, '03', 'ALICANTE'),
(4, '04', 'ALMERÍA'),
(5, '05', 'ÁVILA'),
(6, '06', 'BADAJOZ'),
(7, '07', 'ISLAS BALEARES'),
(8, '08', 'BARCELONA'),
(9, '09', 'BURGOS'),
(10, '10', 'CÁCERES');