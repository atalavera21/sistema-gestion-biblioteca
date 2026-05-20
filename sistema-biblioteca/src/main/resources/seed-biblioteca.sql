-- MySQL dump 10.13  Distrib 8.4.8, for Win64 (x86_64)
--
-- Host: localhost    Database: biblioteca_db
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

TRUNCATE TABLE `valoraciones`;
TRUNCATE TABLE `devoluciones`;
TRUNCATE TABLE `prestamos`;
TRUNCATE TABLE `libros`;
TRUNCATE TABLE `usuarios`;
TRUNCATE TABLE `categorias`;

--
-- Dumping data for table `categorias`
--

LOCK TABLES `categorias` WRITE;
/*!40000 ALTER TABLE `categorias` DISABLE KEYS */;
INSERT INTO `categorias` (`id`, `nombre`) VALUES (3,'Algoritmos'),(4,'Base de Datos'),(6,'Gestión'),(2,'Ingeniería de Software'),(1,'Programación'),(5,'Redes');
/*!40000 ALTER TABLE `categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `libros`
--

LOCK TABLES `libros` WRITE;
/*!40000 ALTER TABLE `libros` DISABLE KEYS */;
INSERT INTO `libros` (`id`, `autor`, `isbn`, `stockTotal`, `titulo`, `categoria_id`, `imagen`, `descripcion`, `editorial`, `anioPublicacion`, `paginas`, `idioma`) VALUES (1,'Robert C. Martin','9780132350884',0,'Clean Code',1,'https://covers.openlibrary.org/b/isbn/9780132350884-L.jpg','Even bad code can function. But if code isn\'t clean, it can bring a development organization to its knees. This book teaches you how to write clean, readable, and maintainable code through principles, patterns, and practices drawn from real-world case studies.','Prentice Hall',2008,464,'Inglés'),(2,'Andrew Hunt, David Thomas','9780201616224',4,'The Pragmatic Programmer',1,'https://covers.openlibrary.org/b/isbn/9780201616224-L.jpg','A guide to becoming a better programmer through practical advice, technical excellence, and professional development. Covers topics from design to testing, from automation to team dynamics.','Addison-Wesley',1999,352,'Inglés'),(3,'Erich Gamma et al.','9780201633610',3,'Design Patterns',1,'https://covers.openlibrary.org/b/isbn/9780201633610-L.jpg','The seminal work on object-oriented design patterns. Describes 23 classic patterns with real-world examples, UML diagrams, and implementation guidance.','Addison-Wesley',1994,395,'Inglés'),(4,'Martin Fowler','9780134757599',0,'Refactoring',1,'https://covers.openlibrary.org/b/isbn/9780134757599-L.jpg','The definitive guide to improving the design of existing code. Learn how to recognize code smells, apply systematic refactorings, and build tests that protect your changes.','Addison-Wesley',2018,448,'Inglés'),(5,'Eric Evans','9780321125217',2,'Domain-Driven Design',2,'https://covers.openlibrary.org/b/isbn/9780321125217-L.jpg','Tackles the complexity of software by connecting the implementation to an evolving model of the core business concepts. Introduced ubiquitous language, bounded contexts, and strategic design.','Addison-Wesley',2003,560,'Inglés'),(6,'Steve McConnell','9780735619678',4,'Code Complete',2,'https://covers.openlibrary.org/b/isbn/9780735619678-L.jpg','A comprehensive handbook covering every aspect of software construction. Packed with checklists, code examples, and battle-tested advice from decades of industry experience.','Microsoft Press',2004,960,'Inglés'),(7,'Frederick P. Brooks Jr.','9780201835953',2,'The Mythical Man-Month',6,'https://covers.openlibrary.org/b/isbn/9780201835953-L.jpg','The classic book on software project management. Explains why adding manpower to a late project makes it later, the tar pit of large-system programming, and the joys of the craft.','Addison-Wesley',1995,322,'Inglés'),(8,'Gayle Laakmann McDowell','9780984782857',6,'Cracking the Coding Interview',3,'https://covers.openlibrary.org/b/isbn/9780984782857-L.jpg','189 programming interview questions and solutions ranging from arrays to dynamic programming. Includes behind-the-scenes stories of tech company interviews.','CareerCup',2015,706,'Inglés'),(9,'Cormen, Leiserson, Rivest','9780262033848',3,'Introduction to Algorithms',3,'https://covers.openlibrary.org/b/isbn/9780262033848-L.jpg','The most widely used algorithms textbook worldwide. Rigorous yet accessible, covering sorting, graphs, dynamic programming, NP-completeness, and many other fundamental algorithmic concepts.','MIT Press',2009,1312,'Inglés'),(10,'Joshua Bloch','9780134685991',5,'Effective Java',1,'https://covers.openlibrary.org/b/isbn/9780134685991-L.jpg','Best practices for the Java platform, updated for Java 9. Covers lambdas, streams, generics, concurrency, serialization, and more. Each chapter consists of actionable items.','Addison-Wesley',2018,416,'Inglés'),(11,'Eric Freeman, Elisabeth Robson','9780596007126',4,'Head First Design Patterns',1,'https://covers.openlibrary.org/b/isbn/9780596007126-L.jpg','A visually rich, brain-friendly guide to design patterns that makes learning fun and effective. Uses engaging visuals, puzzles, and real-world scenarios.','O\'Reilly Media',2004,694,'Español'),(12,'Douglas Crockford','9780596517748',3,'JavaScript: The Good Parts',1,'https://covers.openlibrary.org/b/isbn/9780596517748-L.jpg','Unearths the excellence within JavaScript, revealing the reliable, readable, and maintainable subset hidden beneath a veneer of questionable features.','O\'Reilly Media',2008,176,'Inglés'),(13,'Marijn Haverbeke','9781593279509',4,'Eloquent JavaScript',1,'https://covers.openlibrary.org/b/isbn/9781593279509-L.jpg','An eloquent introduction to programming with JavaScript. Covers values, structure, functions, objects, asynchronous programming, and Node.js.','No Starch Press',2018,472,'Inglés'),(14,'Eric Matthes','9781593279288',5,'Python Crash Course',1,'https://covers.openlibrary.org/b/isbn/9781593279288-L.jpg','A fast-paced, no-nonsense introduction to Python. Builds projects including a Space Invaders-style arcade game, data visualizations, and a full web application with Django.','No Starch Press',2019,544,'Inglés'),(15,'Donald E. Knuth','9780201896831',2,'The Art of Computer Programming',3,'https://covers.openlibrary.org/b/isbn/9780201896831-L.jpg','The definitive work on algorithms and data structures. Volume 1 covers fundamental algorithms including arbitrary precision arithmetic and linked lists.','Addison-Wesley',1997,672,'Inglés'),(16,'Robert Sedgewick, Kevin Wayne','9780321573513',3,'Algorithms',3,'https://covers.openlibrary.org/b/isbn/9780321573513-L.jpg','The definitive guide to fundamental algorithms and data structures with full implementations in Java. Covers searching, sorting, graph processing, and string processing.','Addison-Wesley',2011,976,'Inglés'),(17,'Aditya Y. Bhargava','9781617292231',4,'Grokking Algorithms',3,'https://covers.openlibrary.org/b/isbn/9781617292231-L.jpg','An illustrated guide that teaches common algorithms through visual examples and Python code. Makes complex concepts like dynamic programming and hash tables accessible to everyone.','Manning Publications',2016,256,'Inglés'),(18,'Robert C. Martin','9780134494166',4,'Clean Architecture',2,'https://covers.openlibrary.org/b/isbn/9780134494166-L.jpg','A blueprint for building systems that are testable, independent of frameworks, UI, and databases. Covers SOLID principles, component cohesion, and architectural patterns.','Prentice Hall',2017,432,'Inglés'),(19,'Ian Sommerville','9780133943030',3,'Software Engineering',2,'https://covers.openlibrary.org/b/isbn/9780133943030-L.jpg','The best-selling introduction to software engineering, widely adopted in universities worldwide. Covers agile methods, security, dependability, and modern development practices.','Pearson',2015,816,'Inglés'),(20,'Sam Newman','9781492034025',3,'Building Microservices',2,'https://covers.openlibrary.org/b/isbn/9781492034025-L.jpg','A practical guide to designing fine-grained, distributed systems. Covers integration, deployment, testing, security, and organizational alignment for microservices.','OReilly Media',2021,616,'Inglés'),(21,'Tom DeMarco, Timothy Lister','9780321934116',3,'Peopleware',6,'https://covers.openlibrary.org/b/isbn/9780321934116-L.jpg','The classic book on the human side of software development. Shows that major problems in software are sociological, not technical. How to build productive teams.','Addison-Wesley',2013,272,'Inglés'),(22,'Jeff Sutherland','9780385346450',4,'Scrum: The Art of Doing Twice the Work in Half the Time',6,'https://covers.openlibrary.org/b/isbn/9780385346450-L.jpg','The co-creator of Scrum reveals the principles behind the framework that revolutionized productivity across industries.','Currency',2014,256,'Inglés'),(23,'Gene Kim, Kevin Behr, George Spafford','9780988262508',3,'The Phoenix Project',6,'https://covers.openlibrary.org/b/isbn/9780988262508-L.jpg','A business novel about IT, DevOps, and helping your business win. Follows an IT manager transforming a failing project using flow, feedback, and continuous improvement.','IT Revolution Press',2013,432,'Inglés'),(24,'Daniel H. Pink','9781594484803',2,'Drive',6,'https://covers.openlibrary.org/b/isbn/9781594484803-L.jpg','Reveals the secret to high performance: autonomy, mastery, and purpose. Challenges traditional motivation theories with compelling research.','Riverhead Books',2011,260,'Inglés'),(25,'Abraham Silberschatz et al.','9780078022159',3,'Database System Concepts',4,'https://covers.openlibrary.org/b/isbn/9780078022159-L.jpg','The standard textbook for database courses. Covers relational model, SQL, database design, storage, indexing, query processing, and NoSQL systems.','McGraw-Hill Education',2019,1376,'Inglés'),(26,'Markus Winand','9783950307825',2,'SQL Performance Explained',4,'https://covers.openlibrary.org/b/isbn/9783950307825-L.jpg','Helps developers write efficient SQL by understanding how indexes and query execution plans work. Covers PostgreSQL, MySQL, Oracle, and SQL Server.','Markus Winand',2012,204,'Inglés'),(27,'Martin Kleppmann','9781449373320',4,'Designing Data-Intensive Applications',4,'https://covers.openlibrary.org/b/isbn/9781449373320-L.jpg','Explores distributed data systems: replication, partitioning, transactions, consistency, batch and stream processing, and trade-offs in building reliable systems.','OReilly Media',2017,616,'Inglés'),(28,'Alan Beaulieu','9781492057611',5,'Learning SQL',4,'https://covers.openlibrary.org/b/isbn/9781492057611-L.jpg','A gentle introduction to SQL. Covers queries, joins, subqueries, views, transactions, and database design with exercises and MySQL examples.','OReilly Media',2020,384,'Inglés'),(29,'Baron Schwartz, Peter Zaitsev','9781449314286',2,'High Performance MySQL',4,'https://covers.openlibrary.org/b/isbn/9781449314286-L.jpg','The ultimate guide to fast, reliable MySQL systems. Covers schema optimization, indexing, query performance, replication, backup, and security.','OReilly Media',2012,826,'Inglés'),(30,'James F. Kurose, Keith W. Ross','9780133594140',3,'Computer Networking: A Top-Down Approach',5,'https://covers.openlibrary.org/b/isbn/9780133594140-L.jpg','The most popular networking textbook worldwide. Uses a top-down approach starting with the application layer. Features Wireshark labs and Internet protocol examples.','Pearson',2017,864,'Inglés'),(31,'Andrew S. Tanenbaum','9780132126953',3,'Computer Networks',5,'https://covers.openlibrary.org/b/isbn/9780132126953-L.jpg','The classic reference on computer networking. Covers physical to application layer with clear explanations, diagrams, and practical protocol examples.','Prentice Hall',2010,960,'Inglés'),(32,'W. Richard Stevens','9780321336316',2,'TCP/IP Illustrated, Volume 1',5,'https://covers.openlibrary.org/b/isbn/9780321336316-L.jpg','The definitive guide to TCP/IP protocols. Uses actual packet traces to explain ARP, IP, ICMP, DHCP, TCP, UDP, DNS, and HTTP with remarkable depth.','Addison-Wesley',2011,1056,'Inglés'),(33,'David Gourley, Brian Totty','9781565925090',3,'HTTP: The Definitive Guide',5,'https://covers.openlibrary.org/b/isbn/9781565925090-L.jpg','Everything about HTTP, the protocol at the heart of the web. Covers headers, caching, proxies, authentication, content negotiation, and HTTP/1.1.','OReilly Media',2002,656,'Inglés'),(34,'Gary A. Donahue','9781449387860',2,'Network Warrior',5,'https://covers.openlibrary.org/b/isbn/9781449387860-L.jpg','A practical guide to real-world networking. Covers switches, VLANs, routers, firewalls, load balancers, and troubleshooting from a network engineer perspective.','OReilly Media',2011,788,'Inglés'),(35,'AUTOR DE PRUEBA','123123123123123',20,'LIBRO DE PRUEBA',2,'https://pdlibrosper.cdnstatics2.com/usuaris/libros/thumbs/de40e0ce-e54d-40e5-bd5f-bb47329bcd8d/d_360_620/380034_portada_proyecto-karon_ana-b-nieto_202305231240.webp','Este es un resumen de mentira de como se hace esto en realidad me falta leer bastanteEste es un resumen de mentira de como se hace esto en realidad me falta leer bastanteEste es un resumen de mentira de como se hace esto en realidad me falta leer bastanteEste es un resumen de mentira de como se hace esto en realidad me falta leer bastanteEste es un resumen de mentira de como se hace esto en realidad me falta leer bastante ','PALOMINO',2022,4,'Español');
/*!40000 ALTER TABLE `libros` ENABLE KEYS */;
UNLOCK TABLES;

UPDATE `libros` SET `activo` = 1;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` (`id`, `codigoUniversitario`, `correo`, `direccion`, `nombre`, `penalizado`, `password`, `rol`, `activo`, `puntuacion`) VALUES (1,'EST-001','cmendoza@senati.pe','Av. Los Olivos 123, SMP','Carlos Mendoza Rojas',0,'$2a$12$Bo2nBAbAbFBjl4PZXfKWg.u/b9EacFJhKCSpbknNV9YkF6Pl6A11q','ESTUDIANTE',1,50),(2,'EST-002','aramirez@senati.pe','Jr. Las Flores 456, Comas','Ana Lucía Ramírez Torres',0,'$2a$12$4/t3cg8DDoYOtz0ef9iLbuRP/VzX5P.lRmPsR0P.pcilCoNOPiLvG','ESTUDIANTE',1,50),(3,'EST-003','dfernandez@senati.pe','Calle Real 789, Independencia','Diego Fernández Paredes',0,'$2a$12$DRT3c9UclMUoNj98T7w8.u57CK5im7nWIq/HskDruxqxVPuA.2OFG','ESTUDIANTE',1,50),(4,'EST-004','mvillanueva@senati.pe','Av. Universitaria 2345, SMP','María José Villanueva',0,'$2a$12$.zChz9RNbg81Mt8YTe37kO08gP0KBwDL43QofGJaCrOrV1G9UPsEe','ESTUDIANTE',1,50),(5,'EST-005','lsanchez@senati.pe','Jr. Huamachuco 567, Los Olivos','Luis Alberto Sánchez Cruz',0,'$2a$12$CcKmVSQ7lC.zJuDBb3WojOjUTSRixOcOKBQ4KN5JTc0Z9shEVBsc6','ESTUDIANTE',1,50),(6,'ADM-001','admin@senati.pe','SENATI','Administrador del Sistema',0,'$2a$12$Z/JHWZI0/PEtekbiu7tBwOCsCTwAaV3bZIbxeExA.hwtIDSlpGmO2','ADMIN',1,100);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `valoraciones`
--

LOCK TABLES `valoraciones` WRITE;
/*!40000 ALTER TABLE `valoraciones` DISABLE KEYS */;
INSERT INTO `valoraciones` (`id`, `libro_id`, `usuario_id`, `puntuacion`, `fecha`) VALUES (1,1,1,5,'2026-05-08 00:39:20'),(2,2,1,4,'2026-05-07 00:39:20'),(3,3,1,5,'2026-05-06 00:39:20'),(4,4,1,4,'2026-05-05 00:39:20'),(5,5,1,5,'2026-05-04 00:39:20'),(6,6,1,5,'2026-05-03 00:39:20'),(7,7,1,4,'2026-05-02 00:39:20'),(8,8,1,5,'2026-05-01 00:39:20'),(9,9,1,4,'2026-04-30 00:39:20'),(10,10,1,5,'2026-04-29 00:39:20'),(11,11,2,4,'2026-05-08 00:39:20'),(12,12,2,5,'2026-05-07 00:39:20'),(13,13,2,3,'2026-05-06 00:39:20'),(14,14,2,5,'2026-05-05 00:39:20'),(15,15,3,5,'2026-05-08 00:39:20'),(16,16,3,4,'2026-05-07 00:39:20'),(17,17,4,5,'2026-05-06 00:39:20'),(18,1,4,4,'2026-05-05 00:39:20'),(19,2,5,5,'2026-05-04 00:39:20'),(20,3,5,5,'2026-05-03 00:39:20');
/*!40000 ALTER TABLE `valoraciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `prestamos`
--

LOCK TABLES `prestamos` WRITE;
/*!40000 ALTER TABLE `prestamos` DISABLE KEYS */;
INSERT INTO `prestamos` (`id`, `fechaPrestamo`, `fechaDevolucionEstimada`, `fechaDevolucionReal`, `estado`, `activo`, `libro_id`, `usuario_id`) VALUES
-- DEVUELTO a tiempo
(1,  '2026-03-20', '2026-04-03', '2026-04-01', 'DEVUELTO',   0,  2,  1),
(2,  '2026-03-25', '2026-04-08', '2026-04-07', 'DEVUELTO',   0,  3,  1),
(3,  '2026-04-01', '2026-04-15', '2026-04-14', 'DEVUELTO',   0,  5,  1),
(4,  '2026-04-05', '2026-04-19', '2026-04-18', 'DEVUELTO',   0,  6,  1),
(5,  '2026-04-08', '2026-04-22', '2026-04-20', 'DEVUELTO',   0, 11,  2),
(6,  '2026-04-12', '2026-04-26', '2026-04-25', 'DEVUELTO',   0, 12,  2),
(7,  '2026-04-15', '2026-04-29', '2026-04-28', 'DEVUELTO',   0, 15,  3),
(8,  '2026-04-20', '2026-05-04', '2026-05-02', 'DEVUELTO',   0, 16,  3),
(9,  '2026-04-25', '2026-05-09', '2026-05-08', 'DEVUELTO',   0, 17,  4),
(10, '2026-04-28', '2026-05-12', '2026-05-10', 'DEVUELTO',   0,  1,  4),
(11, '2026-04-20', '2026-05-04', '2026-05-01', 'DEVUELTO',   0,  3,  5),
(12, '2026-04-22', '2026-05-06', '2026-05-05', 'DEVUELTO',   0, 14,  2),
-- PENALIZADO (devuelto con retraso)
(13, '2026-03-15', '2026-03-29', '2026-04-10', 'PENALIZADO', 0,  1,  1),
(14, '2026-03-20', '2026-04-03', '2026-04-15', 'PENALIZADO', 0,  4,  1),
(15, '2026-04-01', '2026-04-15', '2026-04-28', 'PENALIZADO', 0, 13,  2),
(16, '2026-04-05', '2026-04-19', '2026-05-06', 'PENALIZADO', 0,  2,  5),
-- ACTIVO (en curso)
(17, '2026-05-05', '2026-05-19', NULL,          'ACTIVO',     1,  6,  4),
(18, '2026-05-08', '2026-05-22', NULL,          'ACTIVO',     1,  9,  5),
(19, '2026-05-10', '2026-05-24', NULL,          'ACTIVO',     1, 27,  1),
-- VENCIDO (atrasado, sin devolver)
(20, '2026-04-20', '2026-05-04', NULL,          'VENCIDO',    1,  7,  3);
/*!40000 ALTER TABLE `prestamos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `devoluciones`
--

LOCK TABLES `devoluciones` WRITE;
/*!40000 ALTER TABLE `devoluciones` DISABLE KEYS */;
INSERT INTO `devoluciones` (`id`, `prestamo_id`, `fechaDevolucion`, `diasRetraso`, `aTiempo`) VALUES
-- A tiempo (DEVUELTO)
(1,  1,  '2026-04-01', 0,  1),
(2,  2,  '2026-04-07', 0,  1),
(3,  3,  '2026-04-14', 0,  1),
(4,  4,  '2026-04-18', 0,  1),
(5,  5,  '2026-04-20', 0,  1),
(6,  6,  '2026-04-25', 0,  1),
(7,  7,  '2026-04-28', 0,  1),
(8,  8,  '2026-05-02', 0,  1),
(9,  9,  '2026-05-08', 0,  1),
(10, 10, '2026-05-10', 0,  1),
(11, 11, '2026-05-01', 0,  1),
(12, 12, '2026-05-05', 0,  1),
-- Con retraso (PENALIZADO)
(13, 13, '2026-04-10', 12, 0),
(14, 14, '2026-04-15', 12, 0),
(15, 15, '2026-04-28', 13, 0),
(16, 16, '2026-05-06', 17, 0);
/*!40000 ALTER TABLE `devoluciones` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-03 23:27:56
