# CarritoGT

Migración de un carrito de compra básico que estaba en PHP y ahora estara en una API REST con Spring Boot, Java 8 organizada en capas, con un frontend en JavaScript.

Autor: Pablo Merida (Pablo502M)
Proyecto original: https://github.com/mvelasquezo/carrito-compra-basico

## Tecnologías utilizadas
- Backend: Java 8, Spring Boot 2.7.18, Spring Data JPA / Hibernate (con `EntityManager`), Maven, Lombok
- Base de datos: MariaDB (XAMPP), base `cc`
- Frontend: HTML, CSS (reutilizado del proyecto PHP) y JavaScript (`fetch`)
- Entorno: VirtualBox con Lubuntu, XAMPP, Git y GitHub

## Requisitos para ejecución
- JDK 8 y Maven 3.9
- XAMPP para Linux con MariaDB
- Python 3 (para servir el frontend) y un navegador

## Instalación
1. Clonar el repositorio:
```bash
   git clone https://github.com/Pablo502M/CarritoGT.git
   cd CarritoGT
```
2. Iniciar XAMPP e importar la base de datos:
```bash
   sudo /opt/lampp/lampp start
   /opt/lampp/bin/mysql -u root -p < database/cc.sql
```
3. Definir la contraseña de MariaDB en la variable de entorno `DB_PASSWORD` y ejecutar el backend:
   - En IntelliJ: Run > Edit Configurations > Environment variables y escribir `DB_PASSWORD=su_contraseña`, luego ejecutar `CarritoGtApplication`.
   - O desde la terminal:
```bash
     export DB_PASSWORD=su_contraseña
     cd backend
     mvn spring-boot:run
```
   La API queda en `http://localhost:8080`.
4. frontend:
```bash
   cd frontend
   python3 -m http.server 5500
```
   Abrir `http://localhost:5500` en el navegador.

## Configuración
- `backend/src/main/resources/application.properties`: conexión a la base (la contraseña se lee de `DB_PASSWORD`, no está escrita en el código).
- `backend/.../config/CorsConfig.java`: orígenes permitidos del frontend (`http://localhost:5500` y `http://carrito-gt.localhost`).
- `frontend/assets/js/app.js`: constante `API` con la dirección del backend.

## Endpoints principales
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/productos` | Lista los productos |
| GET | `/api/carrito/{codUsuario}` | Lista el carrito con subtotales |
| POST | `/api/carrito/{codUsuario}` | Agrega un producto. Cuerpo: `{"idProducto":1,"cantidad":2}` |
| DELETE | `/api/carrito/item/{idShoppingCart}` | Elimina un item del carrito |
| DELETE | `/api/carrito/{codUsuario}` | Vacía el carrito |

Errores: 400 si los datos son inválidos, 404 si el producto o el item no existe, con un mensaje en JSON.

## Problemas conocidos o limitaciones
- No hay autenticación: el carrito se asocia al identificador guardado en el navegador.
- Se usa Spring Boot 2.7 porque se utiliza Java 8; esta versión ya no recibe actualizaciones.
- Los orígenes de CORS están fijos en el código.
