# Dockerfile específico para cada módulo
# Colocar una copia de este archivo en la raíz de cada módulo de microservicio

# Usamos una imagen base ligera solo con la JRE de Java 17
FROM eclipse-temurin:17-jre-jammy

# Creamos un directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo JAR desde la carpeta 'target' de este módulo
# al contenedor, y lo renombramos a 'app.jar'
COPY ./target/*.jar app.jar

# Exponemos el puerto del servicio
EXPOSE 8083
# (Recuerda cambiar esto si el puerto por defecto no es 8080, aunque el mapeo en docker-compose lo maneja)

# El comando que se ejecutará cuando el contenedor se inicie
ENTRYPOINT ["java","-jar","/app.jar"]