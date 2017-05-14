//package comunicaciondedatos;

import java.net.*;

class TestInetAddress {
  public static void main( String[] args ) {
    try {
      System.out.println( "-> Direccion IP de una URL, por nombre" );
      InetAddress address = InetAddress.getByName( "wwww.ubiobio.cl" );
      System.out.println( address );

      // Extrae la direccion IP a partir de la cadena que se
      // encuentra a la derecha de la barra /, luego proporciona
      // esta direccion IP como argumento de llamada al metodo getByName()

      System.out.println( "-> Nombre a partir de la direccion" );
      int temp = address.toString().indexOf( '/' );
      address = InetAddress.getByName( address.toString().substring(temp+1) );
      System.out.println( address );
       
      System.out.println( "-> Direccion IP actual de LocalHost" );
      address = InetAddress.getLocalHost();
      System.out.println( address );

      System.out.println( "-> Nombre de LocalHost a partir de la direccion" );
      temp = address.toString().indexOf( '/' );
      address = InetAddress.getByName( address.toString().substring(temp+1) );
      System.out.println( address );

      System.out.println( "-> Nombre actual de LocalHost" );
      System.out.println( address.getHostName() );

      System.out.println( "-> Direccion IP actual de LocalHost" );
      // Obtiene la direccion IP como un arreglo de bytes
      byte[] bytes = address.getAddress();
      // Convierte los bytes de la direccion IP a valores sin
      // signo y los presenta separados por espacios
      for( int cnt=0; cnt < bytes.length; cnt++ ) {
        int uByte = bytes[cnt] < 0 ? bytes[cnt]+256 : bytes[cnt];
        System.out.print( uByte+" " );
      }
      System.out.println();
    } 
    catch( UnknownHostException e ) {
      System.out.println( e );
      System.out.println( "Debes estar conectado para que esto funcione bien." );
    }
  }
}
