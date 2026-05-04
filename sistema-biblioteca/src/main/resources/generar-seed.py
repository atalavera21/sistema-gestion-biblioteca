import random
from urllib.parse import quote

random.seed(42)

categorias = [
    "Programación", "Base de Datos", "Desarrollo Web", "Inteligencia Artificial",
    "Redes y Telecomunicaciones", "Ciberseguridad", "Sistemas Operativos",
    "Matemáticas Aplicadas", "Estadística", "Ingeniería de Software",
    "Arquitectura de Computadoras", "Cloud Computing", "DevOps y Metodologías Ágiles",
    "Machine Learning", "Internet de las Cosas"
]

autores = [
    "Robert C. Martin", "Martin Fowler", "Bjarne Stroustrup", "Donald Knuth",
    "Andrew S. Tanenbaum", "Brian W. Kernighan", "Dennis Ritchie", "Linus Torvalds",
    "James Gosling", "Guido van Rossum", "Kent Beck", "Erich Gamma",
    "Steve McConnell", "Frederick P. Brooks", "Alan Turing", "John von Neumann",
    "Edsger W. Dijkstra", "Tim Berners-Lee", "Vint Cerf", "Grace Hopper",
    "Margaret Hamilton", "Ada Lovelace", "Claude Shannon", "Niklaus Wirth",
    "Barbara Liskov", "Leslie Lamport", "Tony Hoare", "John McCarthy",
    "Marvin Minsky", "Douglas Crockford", "Addy Osmani", "Kyle Simpson",
    "David Flanagan", "Marijn Haverbeke", "Nicholas C. Zakas", "Eric Evans",
    "Vaughn Vernon", "Scott Chacon", "Ben Straub", "Jon Bentley",
    "Michael Feathers", "David Thomas", "Andrew Hunt", "Ian Sommerville",
    "Roger S. Pressman", "Alistair Cockburn", "Tom DeMarco", "Gerald Weinberg",
    "Grady Booch", "Ivar Jacobson"
]

patterns = [
    "Introducción a {tema}",
    "Fundamentos de {tema}",
    "{tema} Avanzado",
    "Guía Completa de {tema}",
    "{tema}: Teoría y Práctica",
    "Principios de {tema}",
    "Dominando {tema}",
    "El Arte de {tema}",
    "{tema} para Principiantes",
    "{tema} en Profundidad",
    "Aplicaciones Prácticas de {tema}",
    "Diseño y Arquitectura de {tema}",
    "{tema} Moderno",
    "Patrones de {tema}",
    "Desarrollo Ágil con {tema}",
    "Programación con {tema}",
    "{tema} desde Cero",
    "Manual de {tema}",
    "{tema} Esencial",
    "Ingeniería de {tema}",
]

tema_keywords = {
    0: ["Python", "Java", "C++", "Algoritmos", "Estructuras de Datos", "Programación Funcional",
        "Programación Orientada a Objetos", "TypeScript", "Rust", "Go", "Kotlin", "Swift"],
    1: ["SQL", "MySQL", "PostgreSQL", "MongoDB", "Modelado de Datos", "Bases NoSQL",
        "Big Data", "Data Warehousing", "Redis", "Neo4j", "GraphQL", "SQL Server"],
    2: ["HTML5", "CSS3", "JavaScript", "React", "Angular", "Vue.js", "Node.js",
        "APIs REST", "Django", "Spring Boot", "Laravel", "Ruby on Rails"],
    3: ["Python para IA", "TensorFlow", "Redes Neuronales", "Procesamiento de Lenguaje Natural",
        "Visión Artificial", "Agentes Inteligentes", "Deep Learning", "Sistemas Expertos",
        "Aprendizaje Supervisado", "PyTorch", "Keras", "OpenAI"],
    4: ["TCP/IP", "Protocolos de Red", "Redes Inalámbricas", "SDN", "Enrutamiento",
        "Cisco CCNA", "Seguridad en Redes", "Redes 5G", "Virtualización de Redes",
        "Ethernet", "MPLS", "DNS y DHCP"],
    5: ["Hacking Ético", "Criptografía", "Seguridad Informática", "Análisis Forense",
        "Pentesting", "ISO 27001", "Firewalls", "Seguridad en la Nube",
        "Autenticación y Autorización", "Zero Trust", "OWASP Top 10"],
    6: ["Linux", "Windows Server", "macOS", "Kernel", "Administración de Sistemas",
        "Shell Scripting", "Docker", "Kubernetes", "Virtualización",
        "Gestión de Memoria", "Sistemas de Archivos", "Procesos y Threads"],
    7: ["Cálculo", "Álgebra Lineal", "Matemáticas Discretas", "Lógica Matemática",
        "Teoría de Grafos", "Optimización", "Teoría de Números",
        "Métodos Numéricos", "Geometría Computacional", "Criptografía Matemática"],
    8: ["Probabilidad", "Inferencia Estadística", "Regresión", "Análisis Multivariante",
        "Estadística Bayesiana", "Muestreo", "Series Temporales", "Diseño de Experimentos",
        "Bioestadística", "Estadística Computacional"],
    9: ["Arquitectura de Software", "Testing", "Calidad de Software", "Patrones de Diseño",
        "Microservicios", "Refactorización", "UML", "Integración Continua",
        "Documentación Técnica", "Requisitos de Software", "Mantenibilidad",
        "Clean Code", "DDD", "TDD"],
    10: ["Microprocesadores", "Sistemas Embebidos", "Circuitos Digitales",
         "Arquitectura x86", "Arquitectura ARM", "Compiladores",
         "Sistemas Operativos Embebidos", "VLSI", "FPGA", "RISC-V"],
    11: ["AWS", "Azure", "Google Cloud", "Arquitectura Cloud", "Serverless",
         "Terraform", "Cloud Native", "SaaS", "Infraestructura como Código",
         "Cloud Storage", "Cloud Security", "Multi-Cloud"],
    12: ["Scrum", "Kanban", "CI/CD", "Jenkins", "Ansible", "Terraform",
         "Monitorización", "Infraestructura Ágil", "Lean", "SRE",
         "GitOps", "Observabilidad"],
    13: ["Redes Neuronales", "Machine Learning", "Deep Learning", "NLP",
         "Computer Vision", "Reinforcement Learning", "Transfer Learning",
         "TensorFlow", "PyTorch", "Modelos Generativos", "MLOps", "AutoML"],
    14: ["IoT con Arduino", "IoT con Raspberry Pi", "Sensores y Actuadores",
         "Protocolos IoT (MQTT, CoAP)", "Edge Computing con IoT",
         "Smart Cities", "IoT Industrial", "Wearables",
         "Bluetooth LE y Zigbee", "Seguridad en IoT", "Plataformas IoT Cloud"],
}

books_per_cat = 340
total_libros = books_per_cat * len(categorias)

print("-- ============================================================")
print("-- SEED v2: Categorías +", total_libros, "Libros (usando /cover/)")
print("-- ============================================================")
print("SET FOREIGN_KEY_CHECKS = 0;")
print("TRUNCATE TABLE prestamos;")
print("TRUNCATE TABLE libros;")
print("TRUNCATE TABLE categorias;")
print("SET FOREIGN_KEY_CHECKS = 1;")
print()

print("-- Categorías")
for i, cat in enumerate(categorias):
    print(f"INSERT INTO categorias (id, nombre) VALUES ({i+1}, '{cat}');")
print()

print(f"-- {total_libros} Libros")
isbn_counter = 1000000000000
sql_values = []

for cat_id, cat_name in enumerate(categorias):
    temas = tema_keywords[cat_id]
    for i in range(books_per_cat):
        pattern = random.choice(patterns)
        tema = random.choice(temas)
        titulo = pattern.replace("{tema}", tema)
        autor = random.choice(autores)
        isbn = str(9780000000000 + isbn_counter)
        isbn_counter += 1
        stock = random.randint(1, 15)

        # Usar el servlet /cover/ para generar portada
        img_url = f"/sistema-gestion-biblioteca/cover/{cat_id}/{quote(titulo)}/{quote(autor)}"

        safe_titulo = titulo.replace("'", "''")
        safe_autor = autor.replace("'", "''")

        sql_values.append(
            f"({cat_id+1}, '{safe_titulo}', '{safe_autor}', '{isbn}', {stock}, '{img_url}')"
        )

batch_size = 100
for start in range(0, len(sql_values), batch_size):
    batch = sql_values[start:start+batch_size]
    print(f"INSERT INTO libros (categoria_id, titulo, autor, isbn, stockTotal, imagen) VALUES")
    print("  " + ",\n  ".join(batch) + ";")

print()
print(f"-- Total: {len(categorias)} categorías, {len(sql_values)} libros")
