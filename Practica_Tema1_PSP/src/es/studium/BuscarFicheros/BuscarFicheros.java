package es.studium.BuscarFicheros;

//Librerías
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BuscarFicheros extends JFrame {
	//Componentes
    private JTextField extensionField; //Cuadro de texto resultados
    private JTextArea resultArea;
    private JButton searchButton;

    public BuscarFicheros() { //Constructor
        setTitle("Mis Ficheros"); //Título
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Cerrar ventana
        setSize(400, 300); // Tamaño ventana
        setLocationRelativeTo(null); //Ventana centrada
        setLayout(new BorderLayout()); 

        //Área de texto para mostrar resultados
        resultArea = new JTextArea(); //Crea el área de texto
        resultArea.setEditable(false); //Hace que no sea editable
        resultArea.setLineWrap(true); //Ajusta las líneas al ancho
        resultArea.setWrapStyleWord(true); //Hace el ajuste palabra por palabra

        //Cuadro de texto
        extensionField = new JTextField(15); //Crea el campo de texto con 15 columnas
        searchButton = new JButton("Buscar"); //Crea el botón buscar
        
        //Cuando presionar el botón buscar llama al método BuscarArchivos()
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarArchivos();
            }
        });

        //Panel para el campo de texto y el botón en la parte inferior
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(extensionField, BorderLayout.CENTER);
        bottomPanel.add(searchButton, BorderLayout.EAST);

        // Añadir componentes a la ventana
        add(new JScrollPane(resultArea), BorderLayout.CENTER); //Área de resultados ocupa el centro
        add(bottomPanel, BorderLayout.SOUTH); //Campo de texto y botón en la parte inferior

        //Añadir listener para el doble clic en el área de texto. si el usuario hace doble clic, llama al método ejecutarArchivo()
        resultArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ejecutarArchivo();
                }
            }
        });
    }

    // Método para buscar archivos
    private void buscarArchivos() {
        resultArea.setText(""); // Limpiar el área de texto
        String extension = extensionField.getText().trim(); //elimina los espacios innecessarios

        if (extension.isEmpty()) { //Si el campo de texto está vacío, muestra un error y se detiene la busqueda
            JOptionPane.showMessageDialog(this, "Por favor, ingresa una extensión.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<File> foundFiles = new ArrayList<>(); //Crea una lista donde se almacenarán los archivos encontrados

        // Buscar archivos en todas las unidades de disco
        for (File root : File.listRoots()) {
            buscarEnDirectorio(root, extension, foundFiles);
        }
        
        
        //si no encuentran archivos, manda un mensaje
        if (foundFiles.isEmpty()) {
            resultArea.append("No se encontraron archivos con la extensión: " + extension);
        } else {
            for (File file : foundFiles) {
                resultArea.append(file.getAbsolutePath() + "\n");
            }
        }
    }

    // Método recursivo para buscar en directorios
    private void buscarEnDirectorio(File dir, String extension, ArrayList<File> foundFiles) {
    	//verifica si exixte el directorio y obtiene la lista de archivos dentro
        if (dir != null && dir.isDirectory() && dir.canRead()) {
        	
        	//Si es un directorio llama al método. si es un archivo lo agrega a la lista
            File[] files = dir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        buscarEnDirectorio(file, extension, foundFiles);
                    } else if (file.getName().endsWith("." + extension)) {
                        foundFiles.add(file);
                    }
                }
            }
        }
    }

    //Método para ejecutar un archivo .exe
    private void ejecutarArchivo() {
        String selectedLine = resultArea.getSelectedText();
        if (selectedLine != null && selectedLine.endsWith(".exe")) {
            try {
                System.out.println("Intentando ejecutar: " + selectedLine);
                ProcessBuilder pb = new ProcessBuilder("\"" + selectedLine + "\"");
                pb.start();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "No se pudo ejecutar el archivo: " + selectedLine, "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }


    
    //Método principal main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BuscarFicheros().setVisible(true);
            }
        });
    }
}
