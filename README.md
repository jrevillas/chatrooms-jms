# chatrooms-jms

## Autores
* Melero Chaves, Daniel
* Núñez Díaz-Montes, Miguel
* Revillas García, Javier
* Ruiz Calle, Javier

## Tabla de contenidos
1. [Arquitectura de comunicación](#Arquitectura-de-comunicación)
  11. [Elementos de la arquitectura](#Elementos-de-la-arquitectura)
  12. [Métodos de comunicación](#Métodos-de-comunicación)
2. [Despliegue remoto del entorno](#Despliegue-remoto-del-entorno)
3. [Interfaz de texto](#Interfaz-de-texto)
4. [Interfaz gráfica](#Interfaz-gráfica)
5. [Base de datos](#Base-de-datos)
  51. [Modelo E/R](#Modelo-E/R)
  52. [Operaciones y consultas](#Operaciones-y-consultas)
  53. [Aspectos de seguridad](#Aspectos-de-seguridad)

## Arquitectura de comunicación

Para poder realizar un buen desarrollo de la práctica, hemos establecido cuál va a ser la arquitectura de comunicación que van a seguir los mensajes que haya en nuestro sistema.

En la siguiente imagen se puede ver un esquema simple de la arquitectura a usar.

![alt text](http://i.imgur.com/9OcJVOe.png "Comunication Schema")

La arquitectura de comunicación se puede analizar desde dos perspectivas: La partes que la componen y los métodos de comunicación usados.

### Elementos de la arquitectura

La aquitectura de comunicación se divide en dos parte: _Sibyl_ y los usuarios que estan usando la aplicación.

#### Sibyl

Es un sistema que actúa como intermediario y se encarga de gestionar algunas de las operaciones que se realizan en el sistema, como la comunicación privada con los usuarios y la gestión de la base de datos.

_Sibyl_ esta compuesto de cinco partes:
* **_BotLogic:_** es la parte se que encarga de acceder a la base de datos.
* **_SibylQueueManager:_** se encarga de la gestión de las _queues_ de comunicación con los usuarios.
* **_Types:_** define los tipos de mensajes que procesa _Sibyl_.
* **_UserConnection:_** define las comunicaciones que van a usar los usuarios para comunicarse con _Sibyl_.
* **_Launcher:_** se encarga de comunicarse con las otras partes de _Sibyl_ y con los usuarios que usen el sistema.

#### Los usuarios

Los usuarios son aquellas personas que interactuan con el sistema de chat desarrollado, según desde donde se conecte el usuario, intefaz de terminal o gráfica, el modo de interacción es diferente. El modo de funcionamiento de cada interfaz se explicará posteriormente.

### Métodos de Comunicación

Desde la perspectiva de métodos de comunicación, se puede dividir el esquema en dos partes: La comunicación mediante topics y la comunicación mediante queues.

#### La comunicación mediante temas (topics)

La cominicación mediante _topics_ se usa cuando el usuario quiere enviar un mensaje normal o un mensaje con menciones a un chatroom. La manera por la cual _Sibyl_ es consciente de todos los mensajes que se intercambian en todas las chatrooms es porque _Sibyl_ esta suscripto de forma oculta en todas las chatrooms que hay en el sistema.

De esta manera puede guardar los mensajes en la base de datos y adicionamente, en el caso de que los mensajes tengan menciones avisar de manera especial a cada usuario.

#### La comunicación mediante colas de mensajes (queues)

La comunicación mediante _queues_ se usa cuando el usuario quiere realizar cualquier operación menos la de enviar mensajes a una chatroom. Dichos mensajes son mandados a _Sibyl_, el cual leerá de la cola de peticiones y realizará las operaciones necesarias que el usuario le ha solicitado y una vez finalizado, _Sibyl_ le enviará al usuario por la cola de respuestas el resultado de la operación solicitada. Para saber qué operación tiene que hacer, hay usa serie de tipos de mensajes que estan definido en la clase _Types_/_MessageType_.

_Sibyl_ utiliza la clase _SibylQueueManager_ para poder gestionar las dos colas de mensajes que le conectan con cada usuario.

En el caso de que _Sibyl_ necesite acceder a la base de datos, tanto para realizar operaciones o consultas, lo hará mediante la clase _BotLogic_.

## Despliegue remoto del entorno

## Interfaz de texto

La interfaz de texto renderiza los componentes de la aplicación mediante una máquina de estados. Gestiona las interrupciones referidas a los mensajes entrantes y gestiona los recursos compartidos mediante controles de concurrencia.

En esta interfaz se muestra la lista de _chatrooms_ disponibles en el sistema, a la derecha los mensajes que hay en el _chatroom_ seleccionado y en la parte inferior de la terminal se encuenta un _prompt_ ( **_>_** ) en el que el usuario podrá realizar las operaciones de envío de mensajes y ejecución de comandos.

<!--
En la siguiente imagen se puede ver la apariencia de la interfaz de terminal.

![alt text](screenshots/example-terminal.png "Terminal Interface")
-->

## Interfaz gráfica

<!--
![alt text]("Graphic Interface")
-->

## Base de datos
Para el desarrollo de esta práctica, hemos decidido implementar una base de datos para tener una persistencia de toda la información que estemos manejando, mientras el sistema de chatrooms esté en funcionamiento.

### Modelo E/R
El modelo de la base de datos esta compuesto de tres entidades y tres relaciones. Las entidades representan a los usuarios, las chatrooms y los mensajes entre usuarios mediante las chatrooms.

En la siguiente imagen se puede ver el modelo entidad/relación que define a la base de datos a utilizar.

![alt text](http://i.imgur.com/TQ6eLPB.png "Database Schema")

### Operaciones y consultas
Las operaciones y cunsultas que se le pueden realizar a la base de datos son las siguientes:

* Se pueden realizar las operaciones básicas de inserción de elementos, eliminación de una suscripción y actualuzación de algunos atributos, como por ejemplo la contraseña de los usuarios.

* Las consultas que se pueden realizar sobre las **_chatroom_** son las siguientes: Obtener todas las chatrooms, a partir del id o del nombre y las suscripciones a una chatroom.

* Las consultas que se pueden realizar sobre los **_usuarios_** son las siguientes: Obtener todos los usuarios o solo uno, las suscripciones de un usuario y obtener los usuarios de una chatroom.

* Las consultas que se pueden realizar sobre los **_mensajes_** son las siguientes: Obtener todos los mensajes, a partir de un usuario o una chatroom, obtener los mensajes con menciones con varios filtros entre otras queries.

### Aspectos de seguridad
Al haber decido tener una persistencia de la información que va a utilizar nuestro sistema de chat, hay que llevar a cabo una serie de medidas de seguridad para garantizar el correcto funcionamiento de nuestro sistema.

#### Contraseñas
Al tener información delicada de los usuarios almacenada en la base de datos, hemos decidido cifrar sus contraseñas.

El método de cifrado que hemos decidido usar para cifrar las contraseñas es **_BCrypt_**. Este método de cifrado tiene implementada una clase en Java, que es la que vamos a usar para llevar a cabo nuestro objetivo.

A continuación, se muestra un ejemplo de cómo se utiliza la clase **_BCrypt_** para el cifrado de contraseñas.

```java
public static void insertUser(User user) {

        //Generación de la operación/query
        String query = "INSERT INTO `chatrooms`.`USER` (`handle`, `password`) ";
        query += "VALUES (?,?)";

        // Cifrado de la contraseña
        String hash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(BCRYPT_COST));

        try {
            PreparedStatement sentence = connection.prepareStatement(query);
            // Comprobación de los parámetros de la operación/query
            sentence.setString(1, user.getHandle());
            sentence.setString(2, hash);
            // Ejecución de la operación/query
            sentence.execute();
            sentence.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
}
```