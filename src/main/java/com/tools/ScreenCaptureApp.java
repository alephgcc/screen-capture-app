package com.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase util para guardar explicaciones en meetings
 */
public class ScreenCaptureApp extends JFrame {

    private boolean isCapturing = false;
    private Timer timer;
    private JTextField intervalField;
    private JTextField subfolderField;
    private JToggleButton toggleButton;
    private static final String BASE_PATH = "C:/screenshots";
    private static final String DEFAULT_FOLDER = "default";
    private static String subfolder = "c";
    private BufferedImage lastCapturedImage = null;  // Guardar la última captura

    public ScreenCaptureApp() {
        // Configuración de la ventana
        setTitle("Screen Capture App");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Campo de texto para el subfolder
        JLabel subfolderLabel = new JLabel("Carpeta: ");
        subfolderField = new JTextField(DEFAULT_FOLDER, 20); 
        add(subfolderLabel);
        add(subfolderField);

        // Campo de texto para el intervalo
        JLabel intervalLabel = new JLabel("Intervalo (ms): ");
        intervalField = new JTextField("1000", 10); // Valor predeterminado: 1000 ms
        add(intervalLabel);
        add(intervalField);

        // Botón de encendido/apagado
        toggleButton = new JToggleButton("Comenzar Capturas");
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toggleButton.isSelected()) {
                    toggleButton.setText("Detener Capturas");
                    startCapturing();
                } else {
                    toggleButton.setText("Comenzar Capturas");
                    stopCapturing();
                }
            }
        });
        add(toggleButton);

        // Mostrar la ventana
        setVisible(true);
    }

    // Método para iniciar las capturas
    protected void startCapturing() {
        int interval;
        try {
            interval = Integer.parseInt(intervalField.getText()); // Leer el intervalo del campo de texto
            subfolder = subfolderField.getText();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un intervalo válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        init(subfolder);

        isCapturing = true;
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isCapturing) {
                    captureScreen();
                }
            }
        }, 0, interval); // Iniciar captura con el intervalo dado
    }

    // Método para detener las capturas
    protected void stopCapturing() {
        isCapturing = false;
        if (timer != null) {
            timer.cancel();
        }
    }

    // Método para capturar la pantalla
    private void captureScreen() {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage currentImage = robot.createScreenCapture(screenRect);

            // Crear el nombre del archivo con el timestamp
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String fileName = BASE_PATH + "/" + subfolder + "/capture_" + timeStamp + ".png";

            // Compara la imagen actual con la última captura
            if (lastCapturedImage != null && imagesAreEqual(lastCapturedImage, currentImage)) {
                System.out.println("No hubo cambios en la pantalla. Saltando captura.");
                return;
            }

            lastCapturedImage = currentImage;

            // Guardar la captura en un archivo
            File file = new File(fileName);
            ImageIO.write(currentImage, "png", file);

            System.out.println("Captura guardada en: " + fileName);
        } catch (AWTException | IOException ex) {
            ex.printStackTrace();
        }
    }

    // Método para comparar si dos imágenes son iguales
    private boolean imagesAreEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false; // Las imágenes tienen diferentes dimensiones
        }

        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false; // Hay al menos un píxel diferente
                }
            }
        }

        return true; // Las imágenes son iguales
    }

    public static void main(String[] args) {
        
        // Iniciar la aplicación
        SwingUtilities.invokeLater(() -> {
            new ScreenCaptureApp();
        });
    }

    public boolean isCapturing(){
        return this.isCapturing;
    }

    private void init(String subFolder){

        // Verificar si existe la carpeta C:/captures
        File captureDir = new File(BASE_PATH + "/" + subFolder);
        if (!captureDir.exists()) {
            captureDir.mkdirs(); // Crear la carpeta si no existe
        }

    }
}