package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Clase Principal para el manejo de archivos dentro del proyecto.
 *
 * @author Milton Barrera
 * @version 1.0
 */
public class FilesManagement {

  /**
   * Create folders by a path parameter. Example: path =
   * "./folder/folder1/folder11". This create 3 folders.
   *
   * @param path : Folders that you need create.
   * @return boolean: true if the path was created. Else false if already exist
   * or occurs an error.
   */
  public static boolean newFolder(String path) {
    File directorio = new File(path);
    if (!directorio.exists()) {
      if (directorio.mkdirs()) {
        System.out.println("Directorio creado");
        return true;
      } else {
        System.out.println("Error al crear directorio");
        return false;
      }
    }
    return false;
  }

  /**
   * Crea un archivo con el nombre recibido por parámetro en la función.
   * 
   * @param pNombre la ruta y nombre del archivo que se quiere crear.
   * @return
   */
  public static boolean crearArchivo(String pNombre) {
    try {
      File file = new File(pNombre);
      // Si el archivo no existe es creado
      if (!file.exists()) {
        file.createNewFile();
      }
      return true;
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  /**
   * Escribe contenido en un archivo. Esta función específicamente
   * 
   * @param pNombre Ruta del archivo y nombre de este.
   * @param pContenido El contenido que va a ser escrito.
   */
  public static void escribirArchivo(String pNombre, String pContenido) {
    File archivo = new File(pNombre);
    try {
      PrintWriter salida = new PrintWriter(archivo);
      salida.println(pContenido);
      salida.close();
    } catch (FileNotFoundException ex) {
      ex.printStackTrace(System.out);
    }
  }

  /**
   * Agrega contenido a un archivo ya existente.
   * 
   * @param pNombre El nombre del archivo.
   * @param pContenido El contenido que se desea agregar.
   */
  public static void agregarArchivo(String pNombre, String pContenido) {
    File archivo = new File(pNombre);
    try {
      PrintWriter salida = new PrintWriter(new FileWriter(archivo, true));
      salida.println(pContenido);
      salida.close();
      //System.out.println("Se ha creado el archivo!");
    } catch (FileNotFoundException ex) {
      System.out.println(ex.getMessage());
    } catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }

  /**
   * Funcion que lee el contenido de un archivo y lo muestra en pantalla.
   * 
   * @param pNombre El nombre del archivo.
   */
  public static void leerArchivo(String pNombre) {
    File archivo = new File(pNombre);
    try {
      var contenido = new BufferedReader(new FileReader(archivo));
      var lectura = contenido.readLine();
      while (lectura != null) {
        System.out.println("Linea = " + lectura);
        lectura = contenido.readLine();
      }
      contenido.close();
    } catch (FileNotFoundException ex) {
      ex.printStackTrace(System.out);
    } catch (IOException ex) {
      ex.printStackTrace(System.out);
    }
  }

  /**
   * Retorna el contenido del archivo en un string.
   * 
   * @param filePath La ruta y nombre del archivo.
   * @return El contenido del archivo en un string.
   */
  public static String getContenido(String filePath) {
    try {
      StringBuffer fileData = new StringBuffer();
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      char[] buf = new char[1024];
      int numRead = 0;
      while ((numRead = reader.read(buf)) != -1) {
        String readData = String.valueOf(buf, 0, numRead);
        fileData.append(readData);
      }
      reader.close();
      return fileData.toString();
    } catch (IOException ex) {
      System.out.println(filePath + " does not exist.");
    }
    return "";
  }

  /**
   * Returns an array of File objects representing the files and directories in the specified directory.
   * 
   * @param path The path of the directory.
   * @return An array of File objects representing the files and directories in the specified directory.
   */
  public static File[] getDirectoryFiles(String path) {
    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();
    return listOfFiles;
  }

  /**
   * Returns the relative path of a file or directory with respect to a base directory.
   * 
   * @param baseDir The base directory.
   * @param file The file or directory.
   * @return The relative path of the file or directory.
   */
  public static String getRelativePath(File baseDir, File file) {
    String basePath = baseDir.getAbsolutePath();
    String filePath = file.getAbsolutePath();
    if (filePath.startsWith(basePath)) {
      return filePath.substring(basePath.length() + 1);
    } else {
      return filePath;
    }
  }
}