package proyecto1;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import magick.PixelPacket;

public class ventP extends JFrame implements ActionListener {
    
    JButton bAbrir, bHDR, bSalir;
    JLabel estado;
    
    int limiteX,limiteY,xi,yi;
    String[][] matriz;
    
    MagickImage imag1,imag2,imag3;
    ImageInfo rutaimg;
    
    int dimX, dimY;
    
    public ventP() {
        super("Cobolstagram");
        
        bAbrir = new JButton("Abrir");
        bAbrir.addActionListener(this);
        bHDR = new JButton("HDR");
        bHDR.addActionListener(this);
        bSalir = new JButton("Salir");
        bSalir.addActionListener(this);
        
        estado = new JLabel("Estado");
        
        this.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
        
        this.add(bAbrir);
        this.add(bHDR);
        this.add(bSalir);
    }   // termina constructor de la clase
    
    public void HDR() {
        
        int contador, sumR, sumG, sumB;
        byte[] arreglo;
        
        try {
            dimX = imag1.getDimension().width;  // Las 3 imagenes deben tener el mismo tamaño
            dimY = imag1.getDimension().height;
            
            arreglo = new byte[(dimX*dimY)*3];
            
            PixelPacket actual1, actual2, actual3;
            
            contador = 0;
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    
                    // Obtiene los pixeles de las tres imagenes
                    actual1 = imag1.getOnePixel(i, j);
                    actual2 = imag2.getOnePixel(i, j);
                    actual3 = imag3.getOnePixel(i, j);
                    
                    sumR = 0;
                    sumG = 0;
                    sumB = 0;
                    
                    // Suma todos los valores
                    sumR = actual1.getRed() + actual2.getRed() + actual3.getRed();
                    sumG = actual1.getGreen() + actual2.getGreen() + actual3.getGreen();
                    sumB = actual1.getBlue() + actual2.getBlue() + actual3.getBlue();
                    
                    // Hace el promedio
                    sumR = sumR / 3;
                    sumG = sumG / 3;
                    sumB = sumB / 3;
                    
                    // los guarda en el arreglo
                    arreglo[3*contador] = (byte) sumR;
                    arreglo[3*contador+1] = (byte) sumG;
                    arreglo[3*contador+2] = (byte) sumB;
                    
                    contador++;
                }
            }
            
            // Crea un nuevo visor sin la opcion de "volver a opcion original"
            
            visor3 v2 = new visor3(dimX, dimY, arreglo);
            v2.setLocation(360,this.getY()+25);
            v2.pack();
            v2.setVisible(true);
            v2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            v2.toolsWindow();
            
        } catch (MagickException e) {
            e.printStackTrace();
        }
    }
    
    public void actionPerformed(ActionEvent e) {      // abrir archivo
        
        if(e.getSource().equals(bAbrir)) {
            // Abre una imagen normal
            File imagen;
            
            JFileChooser FC = new JFileChooser();
            
            FileNameExtensionFilter filtroPGM = new FileNameExtensionFilter("Imagenes PGM","pgm");
            FC.addChoosableFileFilter(filtroPGM);
            
            FileNameExtensionFilter filtroPNG = new FileNameExtensionFilter("Imagenes PNG","png");
            FC.addChoosableFileFilter(filtroPNG);
            
            FileNameExtensionFilter filtroGIF = new FileNameExtensionFilter("Imagenes GIF","gif");
            FC.addChoosableFileFilter(filtroGIF);
            
            FileNameExtensionFilter filtroJPG = new FileNameExtensionFilter("Imagenes JPG","jpg","jpeg");
            FC.addChoosableFileFilter(filtroJPG);
                        
            int opcion = FC.showOpenDialog(this);
            
            if(opcion == JFileChooser.APPROVE_OPTION){
                imagen = FC.getSelectedFile();
                visor3 v2 = new visor3(imagen.getAbsolutePath());
                v2.setLocation(360,this.getY()+25);
                v2.pack();
                v2.setVisible(true);
                v2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                v2.toolsWindow();
            }
        }
        
        if(e.getSource().equals(bHDR)) {
            // Abre tres imagenes para el HDR
            File imagen;
            
            JFileChooser FC = new JFileChooser();
            FC.setDialogTitle("Imagen 1 de 3");
            
            FileNameExtensionFilter filtroPGM = new FileNameExtensionFilter("Imagenes PGM","pgm");
            FC.addChoosableFileFilter(filtroPGM);
            
            FileNameExtensionFilter filtroPNG = new FileNameExtensionFilter("Imagenes PNG","png");
            FC.addChoosableFileFilter(filtroPNG);
            
            FileNameExtensionFilter filtroGIF = new FileNameExtensionFilter("Imagenes GIF","gif");
            FC.addChoosableFileFilter(filtroGIF);
            
            FileNameExtensionFilter filtroJPG = new FileNameExtensionFilter("Imagenes JPG","jpg","jpeg");
            FC.addChoosableFileFilter(filtroJPG);
                        
            int opcion = FC.showOpenDialog(this);
            
            if(opcion == JFileChooser.APPROVE_OPTION){  // Si selecciona la primer imagen
                
                imagen = FC.getSelectedFile();
                
                try {
                    rutaimg = new ImageInfo(imagen.getAbsolutePath());
                    imag1 = new MagickImage(rutaimg);
                } catch (MagickException f) {
                    f.printStackTrace();
                }
                
                FC = new JFileChooser();
                FC.setDialogTitle("Imagen 2 de 3");
                
                filtroPGM = new FileNameExtensionFilter("Imagenes PGM","pgm");
                FC.addChoosableFileFilter(filtroPGM);
                
                filtroPNG = new FileNameExtensionFilter("Imagenes PNG","png");
                FC.addChoosableFileFilter(filtroPNG);
                
                filtroGIF = new FileNameExtensionFilter("Imagenes GIF","gif");
                FC.addChoosableFileFilter(filtroGIF);
                
                filtroJPG = new FileNameExtensionFilter("Imagenes JPG","jpg","jpeg");
                FC.addChoosableFileFilter(filtroJPG);
                            
                opcion = FC.showOpenDialog(this);
                
                if(opcion == JFileChooser.APPROVE_OPTION){  // Si selecciona la segunda imagen
                    
                    imagen = FC.getSelectedFile();
                    
                    try {
                        rutaimg = new ImageInfo(imagen.getAbsolutePath());
                        imag2 = new MagickImage(rutaimg);
                    } catch (MagickException f) {
                        f.printStackTrace();
                    }
                    
                    FC = new JFileChooser();
                    FC.setDialogTitle("Imagen 3 de 3");
                    
                    filtroPGM = new FileNameExtensionFilter("Imagenes PGM","pgm");
                    FC.addChoosableFileFilter(filtroPGM);
                    
                    filtroPNG = new FileNameExtensionFilter("Imagenes PNG","png");
                    FC.addChoosableFileFilter(filtroPNG);
                    
                    filtroGIF = new FileNameExtensionFilter("Imagenes GIF","gif");
                    FC.addChoosableFileFilter(filtroGIF);
                    
                    filtroJPG = new FileNameExtensionFilter("Imagenes JPG","jpg","jpeg");
                    FC.addChoosableFileFilter(filtroJPG);
                                
                    opcion = FC.showOpenDialog(this);
                    
                    if(opcion == JFileChooser.APPROVE_OPTION) { // Si selecciona la tercer imagen
                        
                        imagen = FC.getSelectedFile();
                        
                        try {
                            rutaimg = new ImageInfo(imagen.getAbsolutePath());
                            imag3 = new MagickImage(rutaimg);
                            HDR();
                            estado.setText("Completado!");
                        } catch (MagickException f) {
                            f.printStackTrace();
                            estado.setText("Ha ocurrido un error");
                        }
                    }
                }
            }
        }
        
        if(e.getSource().equals(bSalir)) {
            System.exit(0);
        }
        
    }   // actionPerformed
    
}   // clase ventP
