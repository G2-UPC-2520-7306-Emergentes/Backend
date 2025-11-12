# Dockerfile Raíz (E:\Solutions_IntelliJ\FoodChain-API\Dockerfile)

# --- ETAPA 1: CONSTRUCCIÓN (El "Constructor") ---
# Usamos una imagen de Maven que ya tiene JDK 17 para compilar nuestro proyecto.
FROM maven:3.9-eclipse-temurin-17 AS builder

# Creamos un directorio de trabajo.
WORKDIR /app

# Copiamos TODOS los archivos pom.xml primero para el caché de dependencias.
COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/
COPY identity-context/pom.xml ./identity-context/
COPY batch-management-context/pom.xml ./batch-management-context/
COPY traceability-context/pom.xml ./traceability-context/
COPY blockchain-worker-context/pom.xml ./blockchain-worker-context/
COPY shared-domain/pom.xml ./shared-domain/

# Descargamos todas las dependencias del proyecto.
# 'go-offline' es más eficiente si lo corremos para todo el proyecto una vez.
RUN mvn dependency:go-offline

# Copiamos todo el código fuente.
COPY . .

# --- ¡CAMBIO CLAVE 1! ---
# Re-declaramos el ARG aquí para que sea visible para el siguiente comando RUN.
ARG MODULE_DIR

# Construimos el módulo específico Y SUS DEPENDENCIAS DENTRO DEL PROYECTO.
RUN mvn clean install -pl ${MODULE_DIR} -am -DskipTests


# --- ETAPA 2: EJECUCIÓN (El "Producto Final") ---
# Usamos una imagen mucho más ligera que solo tiene la JRE para ejecutar.
FROM eclipse-temurin:17-jre-jammy

# --- ¡CAMBIO CLAVE 2! ---
# Re-declaramos los ARGs para esta nueva etapa.
ARG MODULE_DIR
ARG ARTIFACT_ID
ARG MODULE_VERSION=0.0.1-SNAPSHOT

WORKDIR /app

# Copiamos el JAR específico desde la etapa "builder", usando los argumentos.
COPY --from=builder /app/${MODULE_DIR}/target/${ARTIFACT_ID}-${MODULE_VERSION}.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]