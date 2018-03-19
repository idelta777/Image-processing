package proyecto1;

import javax.swing.JFrame;

public class main {

    public static void main(String[] args) {
        
        // Crea la ventana principal para abrir imagenes o HDR
        ventP v1 = new ventP();
        v1.setVisible(true);
        v1.pack();
        v1.setLocation(200,100);
        v1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}

