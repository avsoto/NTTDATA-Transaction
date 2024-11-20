# NTTDATA Bootcamp Tech Girls Power - Proyecto III

## Descripción del Proyecto

Este proyecto se enfoca en el desarrollo de un **sistema de microservicios** para gestionar el historial de **transacciones bancarias** en el contexto del negocio bancario, utilizando **Spring Boot**, **MongoDB**, **Spring Webflux** y **Lombok**. El sistema está diseñado para registrar y gestionar transacciones de tipo **Depósito**, **Retiro** y **Transferencia**, permitiendo consultar el historial de las transacciones realizadas.

## Características del Sistema

El sistema gestiona las siguientes funcionalidades:

1. **Registrar Transacciones**:
   - **Depósitos**: POST `/transacciones/deposito`
   - **Retiros**: POST `/transacciones/retiro`
   - **Transferencias**: POST `/transacciones/transferencia`

2. **Consultar Historial de Transacciones**:
   - **GET** `/transacciones/historial`: Obtiene el historial de transacciones de los clientes.

## Requisitos del Sistema

- **Spring Boot** para crear microservicios.
- **MongoDB** como base de datos no relacional para almacenar el historial de transacciones.
- **Spring Webflux** para programación reactiva.
- **Lombok** para reducir el código boilerplate.
- **Uso de APIs para Streams** para el manejo eficiente de colecciones.

## Reglas de Negocio

1. **Validaciones de Transacciones**:
   - Los depósitos y retiros se aplican a cuentas específicas.
   - Las transferencias requieren que se indique la cuenta de destino y el monto.
   - No se pueden realizar retiros o transferencias si el saldo disponible es insuficiente.

## Arquitectura del Sistema

El sistema está basado en **microservicios** y sigue una arquitectura reactiva y desacoplada, donde cada microservicio es responsable de una parte específica del proceso bancario. 

**Diagrama de Componentes**:  
El sistema consta de los siguientes microservicios:
- **Microservicio de Transacciones**: Gestiona las transacciones bancarias.
- **Microservicio de Cuentas**: Gestiona las cuentas bancarias de los clientes. 
- **Microservicio de Clientes**: Gestiona la información de los clientes. 

**Diagrama de Secuencia**:  
Cada microservicio interactúa de forma reactiva y asíncrona para procesar las transacciones bancarias. 

## Implementación y conexión con otros Microservicios

El microservicio de **Gestión de Transacciones** está integrado con otros microservicios:

- **Microservicio de Cuentas**: Conectado mediante **RestTemplate** para realizar las validaciones de saldo en el caso de depósitos, retiros y transferencias.  
  Repositorio: [NTTDATA-AccountMS](https://github.com/avsoto/NTTDATA-AccountMS)

- **Microservicio de Clientes**: Conectado mediante **WebClient** para acceder a la información de los clientes y sus cuentas.  
  Repositorio: [NTTDATA-CustomerMS](https://github.com/avsoto/NTTDATA-CustomerMS)

## Requerimientos Técnicos

1. **MongoDB**: Se utiliza para almacenar las transacciones. Cada transacción se representa como un documento en MongoDB.
2. **Spring Webflux**: Se emplea para manejar la lógica de programación reactiva, permitiendo que el sistema sea asíncrono y no bloqueante.
3. **Lombok**: Se usa para reducir el código boilerplate y hacer la implementación más limpia y mantenible.

## Uso y verificación de funcionalidades

El sistema no cuenta con interfaz gráfica. Las funcionalidades pueden ser verificadas utilizando **Postman** para enviar solicitudes HTTP a los siguientes endpoints:

- **POST** `/transacciones/deposito`: Para registrar un depósito.
- **POST** `/transacciones/retiro`: Para registrar un retiro.
- **POST** `/transacciones/transferencia`: Para registrar una transferencia.
- **GET** `/transacciones/historial`: Para obtener el historial de transacciones.

## Entregables

- **Código Fuente**: Todo el código fuente del proyecto está disponible en este repositorio.
- **Documentación OpenAPI**: La documentación de la API se encuentra utilizando el enfoque **Contract First** mediante OpenAPI.
- **Diagramas**: Se incluyen diagramas de secuencia y diagramas de componentes que detallan la arquitectura y el flujo de comunicación entre microservicios.

## Instrucciones de Ejecución

1. **Clonar el repositorio**:

   ```bash
   git clone https://github.com/tu-usuario/NTTDATA-TransactionMS.git
   cd NTTDATA-TransactionMS
   ```

2. **Instalar las dependencias**:

   Si usas Maven:

   ```bash
   mvn install
   ```

3. **Ejecutar el proyecto**:

   Para ejecutar el microservicio, simplemente corre:

   ```bash
   mvn spring-boot:run
   ```

4. **Pruebas**:
    - Usa Postman para probar los endpoints proporcionados.