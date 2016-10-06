CREATE TABLE PROYECTO (_ID INTEGER PRIMARY KEY AUTOINCREMENT,TITULO TEXT);
CREATE TABLE USUARIOS (_ID INTEGER PRIMARY KEY AUTOINCREMENT,NOMBRE TEXT,CORREO_ELECTRONICO TEXT );
CREATE TABLE TAREA (_ID INTEGER PRIMARY KEY AUTOINCREMENT,DESCRIPCION TEXT NOT NULL,HORAS_PLANIFICADAS INTEGER, MINUTOS_TRABAJDOS INTEGER,ID_PRIORIDAD INTEGER NOT NULL DEFAULT 4, ID_RESPONSABLE INTEGER NOT NULL,ID_PROYECTO INTEGER NOT NULL,FINALIZADA INTEGER DEFAULT 0);
CREATE TABLE PRIORIDAD (_ID INTEGER,TITULO TEXT);
INSERT INTO PRIORIDAD (_ID,TITULO) VALUES (1,'Urgente');
INSERT INTO PRIORIDAD (_ID,TITULO) VALUES (2,'Alta');
INSERT INTO PRIORIDAD (_ID,TITULO) VALUES (3,'Media');
INSERT INTO PRIORIDAD (_ID,TITULO) VALUES (4,'Baja');
INSERT INTO PROYECTO (TITULO) VALUES ("TP Integrador");
INSERT INTO USUARIOS (NOMBRE,CORREO_ELECTRONICO) VALUES ("martin","mdomingu@gmail.com");
INSERT INTO USUARIOS (NOMBRE,CORREO_ELECTRONICO) VALUES ("lucas","lucas@gmail.com");
INSERT INTO TAREA (DESCRIPCION ,HORAS_PLANIFICADAS , MINUTOS_TRABAJDOS ,ID_PRIORIDAD ,ID_RESPONSABLE ,ID_PROYECTO ) VALUES ("Tarea 1 ydyjkw",3,0,2,1,1);
INSERT INTO TAREA (DESCRIPCION ,HORAS_PLANIFICADAS , MINUTOS_TRABAJDOS ,ID_PRIORIDAD ,ID_RESPONSABLE ,ID_PROYECTO ) VALUES ("Tarea 2 afasdf",6,0,3,1,1);
INSERT INTO TAREA (DESCRIPCION ,HORAS_PLANIFICADAS , MINUTOS_TRABAJDOS ,ID_PRIORIDAD ,ID_RESPONSABLE ,ID_PROYECTO ,FINALIZADA ) VALUES ("Tarea 3 neos4js",8,520,1,1,1,1);
INSERT INTO TAREA (DESCRIPCION ,HORAS_PLANIFICADAS , MINUTOS_TRABAJDOS ,ID_PRIORIDAD ,ID_RESPONSABLE ,ID_PROYECTO ) VALUES ("Tarea 4 pynergxy",5,0,4,1,1);
INSERT INTO TAREA (DESCRIPCION ,HORAS_PLANIFICADAS , MINUTOS_TRABAJDOS ,ID_PRIORIDAD ,ID_RESPONSABLE ,ID_PROYECTO ) VALUES ("Tarea 5 bexigmer",3,0,1,1,1);
INSERT INTO TAREA (DESCRIPCION ,HORAS_PLANIFICADAS , MINUTOS_TRABAJDOS ,ID_PRIORIDAD ,ID_RESPONSABLE ,ID_PROYECTO ) VALUES ("Tarea 6 cutynerf",2,0,2,1,1);