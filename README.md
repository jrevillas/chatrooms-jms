# Chatrooms con JMS

## Autores
* Melero Chaves, Daniel
* Núñez Díaz-Montes, Miguel
* Revillas García, Javier
* Ruiz Calle, Javier

## Tabla de Contenidos
1. [Arquitectura de Comunicación](#Arquitectura-de-Comunicación)
  11. [Partes de la Arquitectura](#Partes-de-la-Arquitectura)
  12. [Métodos de Comunicación](#Métodos-de-Comunicación)
2. [Despliegue Remoto del Servidor](#Despliegue-Remoto-del-Servidor)
3. [Interfaz de Terminal](#Interfaz-de-Terminal)
4. [Interfaz Gráfica](#Interfaz-Gráfica)
5. [Base de Datos](#Base-de-Datos)
  51. [Modelo E/R](#Modelo-E/R)
  52. [Operaciones y Consultas](#Operaciones-y-Consultas)
  53. [Aspectos de Seguridad](#Aspectos-de-Seguridad)

## Arquitectura de Comunicación

Para poder realizar un buen desarrollo de la práctica, hemos establecido cuál va a ser la arquitectura de comunicación que van a seguir los mensajes que haya en nuestro sistema.

En la siguiente imagen se puede ver un esquema simple de la arquitectura a usar.

![alt text](http://i.imgur.com/9OcJVOe.png "Comunication Schema")

La arquitectura de comunicación se puede analizar desde dos perspectivas: La partes que la componen y los métodos de comunicación usados.

### Partes de la Arquitectura

La aquitectura de comunicación se divide en dos parte: _Sibyl_ y los usuarios que estan usando la aplicación.

#### Sibyl

Es el sistema que se encarga de gestionar todas las operaciones que se realizan en el sistema, tanto la comunicación con los usuarios como la comunicación con la base de datos.

_Sibyl_ esta compuesto de cinco partes:
* **_BotLogic:_** Es la parte se que encarga de acceder a la base de datos.
* **_SibylQueueManager:_** Se encarga de la gestión de las _Queue_ de comunicación con los usuarios.
* **_Types:_** Define los tipos de mensajes que procesa _Sibyl_.
* **_UserConnection:_** Define las comunicaciones que van a usar los usuarios para comunicarse con _Sibyl_.
* **_Launcher:_** Se encarga de comunicarse con las otras partes de _Sibyl_ y con los usuarios que usen el sistema.

#### Los Usuarios

Los usuarios son aquellas personas que interactuan con el sistema de chat desarrollado, según desde donde se conecte el usuario, intefaz de terminal o gráfica, el modo de interacción es diferente. El modo de funcionamiento de cada interfaz se explicará posteriormente.

### Métodos de Comunicación

Desde la perspectiva de métodos de comunicación, se puede dividir el esquema en dos partes: La comunicación mediante topics y la comunicación mediante queues.

#### La comunicación por Topics

La cominicación mediante _Topics_ se usa cuando el usuario quiere enviar un mensaje normal o un mensaje con menciones a un chatroom. La manera por la cual _Sibyl_ es consciente de todos los mensajes que se intercambian en todas las chatrooms es porque _Sibyl_ esta suscripto de forma oculta en todas las chatrooms que hay en el sistema.

De esta manera puede guardar los mensajes en la base de datos y adicionamente, en el caso de que los mensajes tengan menciones avisar de manera especial a cada usuario.

#### La comunicación por Queues

La comunicación mediante _Queues_ se usa cuando el usuario quiere realizar cualquier operación menos la de enviar mensajes a un chatroom. Dichos mensajes son mandados a _Sibyl_, el cual leerá por la _Queue_ _Request_ y realizará las operaciones necesarias que el usuario le ha solicitado y una vez finalizado, _Sibyl_ le enviará al usuario por la _Queue_ _Response_ el resultado de la operación solicitada. Para saber que operación tiene que hacer, hay usa serie de tipos de mensajes que estan definido en _Types_.

_Sibyl_ utiliza la clase _SibylQueueManager_ para poder gestionar ambas _Queues_ de cada usuario. Dicha asignación de _Queues_ a los usuarios la realiza _Sibyl_ por medio de la clase _UserConnection_.

En el caso de que _Sibyl_ necesite acceder a la base de datos, tanto para realizar operaciones o consultas, lo hará mediante el _BotLogic_.

## Despliegue Remoto del Servidor

## Interfaz de Terminal

La interfaz de terminal consiste en mostrar al usuario un vector de Strings, el cual al visualizarlo en una terminal de tamaño 120x30 simula la interfaz de un chat.

En esta interfaz se muestran en el lateral izquierdo la lista de chatrooms disponibles en el sistema, a la derecha los mensajes que hay en el chatroom seleccionado y en la parte inferior de la terminal se encuenta un _prompt_ ( **_>_** ) en el que el usuario podrá realizar las operaciones que desee, como por ejemplos crear una nueva chatroom o cambiar la contraseña, y enviar los mensajes correspondientes para dialogar en las chatrooms.

<!--
En la siguiente imagen se puede ver la apariencia de la interfaz de terminal.

![alt text](screenshots/example-terminal.png "Terminal Interface")
-->

## Interfaz Gráfica

<!--
![alt text]("Graphic Interface")
-->

## Base de Datos
Para el desarrollo de esta práctica, hemos decidido implementar una base de datos para tener una persistencia de toda la información que estemos manejando, mientras el sistema de chatrooms este en funcionamiento.

### Modelo E/R
El modelo de la base de datos esta compuesto de tres entidades y tres relaciones. Las entidades representan a los usuarios, los chatrooms y los mensajes entre usuarios mediante los chatrooms.

En la siguiente imagen se puede ver el modelo entidad/relación que define a la base de datos a utilizar.

![alt text](http://i.imgur.com/TQ6eLPB.png "Database Schema")

### Operaciones y Consultas
Las operaciones y cunsultas que se le pueden realizar a la base de datos son las siguientes:

* Se pueden realizar las operaciones básicas de inserción de elementos, eliminación de una suscripción y actualuzación de algunos atributos, como por ejemplo la contraseña de los usuarios.

* Las queries que se pueden realizar sobre las **_chatroom_** son las siguientes: Obtener todas las chatrooms, a partir del id o del nombre y las suscripciones a una chatroom.

* Las queries que se pueden realizar sobre los **_usuarios_** son las siguientes: Obtener todos los usuarios o solo uno, las suscripciones de un usuario y obtener los usuarios de una chatroom.

* Las queries que se pueden realizar sobre los **_mensajes_** son las siguientes: Obtener todos los mensajes, a partir de un usuario o una chatroom, obtener los mensajes con menciones con varios filtros entre otras queries.

### Aspectos de Seguridad
Al haber decido tener una persistencia de la información que va a utilizar nuestro sistema de chat, hay que llevar a cabo una serie de medidas de seguridad para garantizar el correcto funcionamiento de nuestro sistema.

#### Contraseñas
Al tener información delicada de los usuarios almacenada en la base de datos, hemos decidido cifrar sus contraseñas.

El método de cifrado que hemos decidido usar para cifrar las contraseñas es **_BCrypt_**. Este método de cifrado tiene implementada una clase en java, que es la que vamos a usar para llevar a cabo nuestro objetivo.

A continuación, se muestra un ejemplo de como se utiliza la clase **_BCrypt_** para el cifrado de contraseñas.

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
