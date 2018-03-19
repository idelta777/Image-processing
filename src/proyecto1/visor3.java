package proyecto1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import java.awt.Rectangle;
import java.awt.TextArea;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import magick.FilterType;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import magick.PixelPacket;
import magick.QuantizeInfo;

import magick.util.MagickCanvas;

public class visor3 extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    /*
    int anchoPantalla = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    int altoPantalla = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();*/
    
    String ruta="";
    int arriba, abajo, derecha, izquierda, i, cantidadDesp;
    MagickCanvas canvas;
    MagickImage imag;
    ImageInfo rutaimg;
    JFrame pide,contras,brill,desp,loga,expo,slide,shrink;
    JLabel lPide;
    JTextField tPide,cantDesp,valorConst,valC1,valC2,valOffset,valSMax,valSMin;
    JButton bPide, rIzq, rDer, mas, menos, mas2, menos2, barriba, babajo, bderecha, bizquierda, aceptarConst, bExpAceptar, bSlideAceptar, bShrinkAceptar;
    ImageIcon icono;
    int[][] matriz, matriz2;
    TextArea area;
    
    // variables para dibujar rectangulo
    int xi=0, yi=0, xf=0, yf=0, XI, XF, YI, YF, auxXF, auxYF, limX, limY;
    Boolean presionado = false;
    
    // variables para el raster
    byte[] arreglo;
    int dimX=0, dimY=0;
    int[] rojo, verde, azul;
    PixelPacket[][] valores;
    PixelPacket[][] mat2;
    
    // Variables de la ventana de herramientas
    JButton bSaveAs,bClose,bCut,bCut2,bGrey,bAdjust,bOriginal,bRotate,bHor,bVer,bNeg,bCont,bBright,bLogarit,bExp,bBin,bCopy,bMove;
    JButton bDetect,bAntiAliasing,bHistogram,bEqualize,bStretch,bShrink,bSlide,bSepia,bPseudo,bRemolino,bPixelear,bDensity,bMorfologico;
    JFrame buttons;
    Dimension dimensionesHerramientas = new Dimension(350, 360);
    Dimension ButtonsDimensions = new Dimension(60,35);
    Boolean Boriginal=true;
    
    // Variables de las ventanas del histograma
    histograma h1, h2, h3;
    
    // Variables de la ventana del remolino
    JFrame remolino;
    JTextField cantRem;
    JRadioButton derRem, izqRem;
    JButton aceptRemolino;
    
    // Variables de la ventana pixelear
    JFrame pixelear;
    JTextField tamPix;
    JButton aceptPix;
    
    
    public visor3(String _ruta) {
        // Este visor es el estandar, barra de herramientas y opcion de guardar activadas
        
        super(_ruta.substring(_ruta.lastIndexOf("\\")+1,_ruta.lastIndexOf(".")));
        
        // Guarda la ruta de la imagen para la opcion de restaurar imagen
        ruta = _ruta;

        try {
            rutaimg = new ImageInfo(ruta);
            imag = new MagickImage(rutaimg);
            canvas = new MagickCanvas();
            canvas.addMouseListener(this);
            canvas.addMouseMotionListener(this);
        } catch (MagickException e) {
            e.printStackTrace();
        }
        
        this.add(canvas);
        canvas.setImage(imag);
        
        //this.setMaximumSize(new Dimension(anchoPantalla, altoPantalla));
        
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                cierraTodo();
            }
        });
    }

    public visor3(int dX, int dY, byte[] arr) {
        // Para cuando no se tiene una ruta de archivo
        // Crea un visor sin la opcion de regresar a imagen original a menos de que se guarde primero
        // Llamada por HDR
        
        super("Visor de imagenes");

        canvas = new MagickCanvas();
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        imag = new MagickImage();

        try {
            imag.constituteImage(dX, dY, "RGB", arr);
        } catch (MagickException f) {
            f.printStackTrace();
        }
        
        this.add(canvas);
        canvas.setImage(imag);
        
        // Como no es un archivo el que esta desplegado no hay una ruta para reabrir la imagen original
        Boriginal = false;
        
        //this.setMaximumSize(new Dimension(anchoPantalla-100, altoPantalla-100));
        
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                cierraTodo();
            }
        });
    }
    
    public void cierraTodo() {
        // Este metodo intenta cerrar todas las ventana extra que esten abiertas
        buttons.dispose();
        try {
            pide.dispose();
        }
        catch(Exception ex) {
        }
        
        try {
            contras.dispose();
        }
        catch (Exception ex) {
        }
        
        try {
            brill.dispose();
        }
        catch(Exception ex) {
        }
        
        try {
            desp.dispose();
        }
        catch(Exception ex) {
        }
        
        try {
            loga.dispose();
        }
        catch(Exception ex) {
        }
        
        try {
            h1.dispose();
        }
        catch(Exception ex) {
        }
        
        try {
            h2.dispose();
        }
        catch(Exception ex) {
        }
        
        try {
            h3.dispose();
        }
        catch(Exception ex) {
        }
        
        try {
            slide.dispose();
        }
        catch(Exception ex) {
        }
        
        try {
            remolino.dispose();
        }
        catch(Exception ex) {
        }
        
        try {
            pixelear.dispose();
        }
        catch(Exception ex) {
        }
    }
    
    public void toolsWindow() {
        // Crea la ventana de herramientas
        
        buttons = new JFrame("Herramientas");
        buttons.setVisible(true);
        buttons.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        buttons.setSize(dimensionesHerramientas);
        //buttons.setAlwaysOnTop(true);
        
        ImageIcon icon;
        
        // Crea todos los botones
        
        icon = new ImageIcon("toolsIcons\\SaveAs.png");
        bSaveAs = new JButton(icon);
        bSaveAs.setToolTipText("Guardar como...");
        bSaveAs.addActionListener(this);
        bSaveAs.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\close.png");
        bClose = new JButton(icon);
        bClose.setToolTipText("Cerrar la imagen actual");
        bClose.addActionListener(this);
        bClose.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\cut1.png");
        bCut = new JButton(icon);
        bCut.setToolTipText("Borrar la seleccion");
        bCut.addActionListener(this);
        bCut.setEnabled(false);
        bCut.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\cut2.png");
        bCut2 = new JButton(icon);
        bCut2.setToolTipText("Obtiene el pedazo seleccionado y borra el resto");
        bCut2.addActionListener(this);
        bCut2.setEnabled(false);
        bCut2.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\grey.png");
        bGrey = new JButton(icon);
        bGrey.setToolTipText("Convierte a escala de grises");
        bGrey.addActionListener(this);
        bGrey.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\resize.png");
        bAdjust = new JButton(icon);
        bAdjust.setToolTipText("Ajustar ventana a la imagen");
        bAdjust.addActionListener(this);
        bAdjust.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\original.png");
        bOriginal = new JButton(icon);
        bOriginal.setToolTipText("Imagen original");
        bOriginal.addActionListener(this);
        bOriginal.setPreferredSize(ButtonsDimensions);
        if(!Boriginal) bOriginal.setEnabled(false);
        
        icon = new ImageIcon("toolsIcons\\rotate.png");
        bRotate = new JButton(icon);
        bRotate.setToolTipText("Rota la imagen");
        bRotate.addActionListener(this);
        bRotate.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\flipH.png");
        bHor = new JButton(icon);
        bHor.setToolTipText("voltea la imagen horizontalmente");
        bHor.addActionListener(this);
        bHor.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\flipV.png");
        bVer = new JButton(icon);
        bVer.setToolTipText("Voltea la imagen verticalmente");
        bVer.addActionListener(this);
        bVer.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\negative.png");
        bNeg = new JButton(icon);
        bNeg.setToolTipText("Efecto de Negativo Fotografico");
        bNeg.addActionListener(this);
        bNeg.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\contrast.png");
        bCont = new JButton(icon);
        bCont.setToolTipText("Contraste");
        bCont.addActionListener(this);
        bCont.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\bright.png");
        bBright = new JButton(icon);
        bBright.setToolTipText("Ajusta el brillo");
        bBright.addActionListener(this);
        bBright.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\logarithm.png");
        bLogarit = new JButton(icon);
        bLogarit.setToolTipText("Transformacion Logaritmica");
        bLogarit.addActionListener(this);
        bLogarit.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\exponential.png");
        bExp = new JButton(icon);
        bExp.setToolTipText("Transformacion de Potencia");
        bExp.addActionListener(this);
        bExp.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\binary.png");
        bBin = new JButton(icon);
        bBin.setToolTipText("Convierte la imagen a blanco y negro");
        bBin.addActionListener(this);
        bBin.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\copy.png");
        bCopy = new JButton(icon);
        bCopy.setToolTipText("Guarda la imagen como nuevo archivo y abre un nuevo visor");
        bCopy.addActionListener(this);
        bCopy.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\move.png");
        bMove = new JButton(icon);
        bMove.setToolTipText("Mueve la imagen");
        bMove.addActionListener(this);
        bMove.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\searchFig.png");
        bDetect = new JButton(icon);
        bDetect.setToolTipText("Busca figuras (no muy bueno)");
        bDetect.addActionListener(this);
        bDetect.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\antiAliasing.png");
        bAntiAliasing = new JButton(icon);
        bAntiAliasing.setToolTipText("Anti-Aliasing");
        bAntiAliasing.addActionListener(this);
        bAntiAliasing.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\histogram.png");
        bHistogram = new JButton(icon);
        bHistogram.setToolTipText("Muestra el histograma de la imagen actual");
        bHistogram.addActionListener(this);
        bHistogram.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\stretch.png");
        bStretch = new JButton(icon);
        bStretch.setToolTipText("Amplia el histograma");
        bStretch.addActionListener(this);
        bStretch.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\shrink.png");
        bShrink = new JButton(icon);
        bShrink.setToolTipText("Encoge el histograma");
        bShrink.addActionListener(this);
        bShrink.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\slide.png");
        bSlide = new JButton(icon);
        bSlide.setToolTipText("Recorre el histograma");
        bSlide.addActionListener(this);
        bSlide.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\equa.png");
        bEqualize = new JButton(icon);
        bEqualize.setToolTipText("Equaliza la imagen");
        bEqualize.addActionListener(this);
        bEqualize.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\sepia.png");
        bSepia = new JButton(icon);
        bSepia.setToolTipText("Convierte a tono sepia");
        bSepia.addActionListener(this);
        bSepia.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\pseudocolor.png");
        bPseudo = new JButton(icon);
        bPseudo.setToolTipText("Aplica pseudo color a la imagen");
        bPseudo.addActionListener(this);
        bPseudo.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\remolino.png");
        bRemolino = new JButton(icon);
        bRemolino.setToolTipText("Crea un efecto de remolino (Espiral)");
        bRemolino.addActionListener(this);
        bRemolino.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\creeper.png");
        bPixelear = new JButton(icon);
        bPixelear.setToolTipText("Hace un efecto de pixeleado");
        bPixelear.addActionListener(this);
        bPixelear.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\density.png");
        bDensity = new JButton(icon);
        bDensity.setToolTipText("Density Slicing, detecta osteoporosis");
        bDensity.addActionListener(this);
        bDensity.setPreferredSize(ButtonsDimensions);
        
        icon = new ImageIcon("toolsIcons\\morfologico.png");
        bMorfologico = new JButton(icon);
        bMorfologico.setToolTipText("Procesamiento morfologico, obtiene los bordes");
        bMorfologico.addActionListener(this);
        bMorfologico.setPreferredSize(ButtonsDimensions);
        
        buttons.setLayout(new FlowLayout(FlowLayout.LEFT,5,10));
        
        // Agrega todos los botones al frame
        buttons.add(bSaveAs);
        buttons.add(bClose);
        buttons.add(bCut);
        buttons.add(bCut2);
        buttons.add(bGrey);
        buttons.add(bAdjust);
        buttons.add(bOriginal);
        buttons.add(bRotate);
        buttons.add(bHor);
        buttons.add(bVer);
        buttons.add(bNeg);
        buttons.add(bCont);
        buttons.add(bBright);
        buttons.add(bLogarit);
        buttons.add(bExp);
        buttons.add(bBin);
        buttons.add(bCopy);
        buttons.add(bMove);
        buttons.add(bDetect);
        buttons.add(bAntiAliasing);
        buttons.add(bHistogram);
        buttons.add(bStretch);
        buttons.add(bShrink);
        buttons.add(bSlide);
        buttons.add(bEqualize);
        buttons.add(bPseudo);
        buttons.add(bSepia);
        buttons.add(bRemolino);
        buttons.add(bPixelear);
        buttons.add(bDensity);
        buttons.add(bMorfologico);
    }
    
    public void redimensiona() {
        // Ajusta el tamaño de la ventana al de la imagen
        
        // Se debe tomar en cuenta el tamaño de la decoracion de la imagen (marcos)
        arriba = this.getInsets().top;
        abajo = this.getInsets().bottom;
        derecha = this.getInsets().right;
        izquierda = this.getInsets().left;

        try {
            this.setSize(imag.getDimension().width + izquierda + derecha, imag.getDimension().height + arriba + abajo);
        } catch (MagickException e) {
            e.printStackTrace();
        }
    }
    
    public void rotar() {
        // Ventana para rotar la imagen
        
        lPide = new JLabel("Grados a rotar");
        tPide = new JTextField("0",5);
        bPide = new JButton("Aceptar");
        bPide.addActionListener(this);
        
        pide = new JFrame();
        pide.setResizable(false);
        pide.setLocation(25,25);
        pide.setVisible(true);
        pide.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        icono = new ImageIcon("left.png");
        rIzq = new JButton(icono);
        rIzq.addActionListener(this);
        rIzq.setToolTipText("Rotar 90° a la izquierda");
        icono = new ImageIcon("right.png");
        rDer = new JButton(icono);
        rDer.addActionListener(this);
        rDer.setToolTipText("Rotar 90° a la derecha");
        
        pide.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
        pide.add(lPide);
        pide.add(tPide);
        pide.add(bPide);
        pide.add(rIzq);
        pide.add(rDer);
    }
    
    public void contraste() {
        // Ventana para modificar el contraste de la imagen
        
        JLabel et = new JLabel("Presione los botones para editar el contraste");
        contras = new JFrame("Contraste");
        contras.setVisible(true);
        contras.setResizable(false);
        contras.setLocation(this.getX()+25, this.getY()+25);
        contras.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        icono = new ImageIcon("mas.png");
        mas = new JButton(icono);
        icono = new ImageIcon("menos.png");
        menos = new JButton(icono);
        
        mas.addActionListener(this);
        menos.addActionListener(this);
        
        contras.setLayout(new FlowLayout(FlowLayout.LEFT,20,20));
        
        contras.add(et);
        contras.add(mas);
        contras.add(menos);
    }
    
    public void brillo() {
        // Ventana para modificar el brillo de la imagen
        
        JLabel et = new JLabel("Presione los botones para editar el brillo");
        brill = new JFrame("Brillo");
        brill.setVisible(true);
        brill.setResizable(false);
        brill.setLocation(this.getX()+25, this.getY()+25);
        brill.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        icono = new ImageIcon("mas.png");
        mas2 = new JButton(icono);
        icono = new ImageIcon("menos.png");
        menos2 = new JButton(icono);
        
        mas2.addActionListener(this);
        menos2.addActionListener(this);
        
        brill.setLayout(new FlowLayout(FlowLayout.LEFT,20,20));
        
        brill.add(et);
        brill.add(mas2);
        brill.add(menos2);
    }
    
    public void desplazar() {
        // Ventana para desplazar la imagen
        
        desp = new JFrame("Desplazar");
        desp.setVisible(true);
        desp.setResizable(false);
        desp.setLocation(this.getX()+25, this.getY()+25);
        desp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        icono = new ImageIcon("arriba.png");
        barriba = new JButton(icono);
        icono = new ImageIcon("abajo.png");
        babajo = new JButton(icono);
        icono = new ImageIcon("derecha.png");
        bderecha = new JButton(icono);
        icono = new ImageIcon("izquierda.png");
        bizquierda = new JButton(icono);
        
        cantDesp = new JTextField("5",5);
        
        barriba.addActionListener(this);
        babajo.addActionListener(this);
        bizquierda.addActionListener(this);
        bderecha.addActionListener(this);
        
        desp.setLayout(new BorderLayout());
        
        desp.add(barriba,BorderLayout.NORTH);
        desp.add(babajo,BorderLayout.SOUTH);
        desp.add(bderecha, BorderLayout.EAST);
        desp.add(bizquierda, BorderLayout.WEST);
        desp.add(cantDesp,BorderLayout.CENTER);
    }
    
    public void logaritmica() {
        // Ventana para ingresar la constante de la transformacion logaritmica
        
        loga = new JFrame("Realzar");
        loga.setVisible(true);
        loga.setResizable(false);
        loga.setLocation(this.getX()+25, this.getY()+25);
        loga.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JLabel et = new JLabel("Intensidad del realce");
        
        valorConst = new JTextField("0",5);
        aceptarConst = new JButton("Aceptar");
        aceptarConst.addActionListener(this);
        
        loga.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
        
        loga.add(et);
        loga.add(valorConst);
        loga.add(aceptarConst);
    }
    
    public void exponencial() {
        // Ventana para ingresar las constantes de la transformacion de potencia
        
        expo = new JFrame("Valores");
        expo.setVisible(true);
        expo.setResizable(false);
        expo.setLocation(this.getX()+25, this.getY()+25);
        expo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JLabel et1 = new JLabel("Constante 1");
        JLabel et2 = new JLabel("Constante 2");
        
        valC1 = new JTextField("0.85",5);
        valC2 = new JTextField("0.7",5);
        
        bExpAceptar = new JButton("Aceptar");
        bExpAceptar.addActionListener(this);
        
        expo.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
        
        expo.add(et1);
        expo.add(valC1);
        expo.add(et2);
        expo.add(valC2);
        expo.add(bExpAceptar);
    }
    
    public void slideVent() {
        // Ventana para pedir el offset del slide del histograma
        
        slide = new JFrame("Slide");
        
        JLabel lb = new JLabel("Offset");
        valOffset = new JTextField("0",5);
        bSlideAceptar = new JButton("Aceptar");
        bSlideAceptar.addActionListener(this);
        
        slide.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
        
        slide.add(lb);
        slide.add(valOffset);
        slide.add(bSlideAceptar);
    }
    
    public void shrinkVent() {
        // Ventana para obtener los valores entre los cuales se encogera el histograma
        
        shrink = new JFrame("Shrink");
        
        JLabel lb1 = new JLabel("Entre ");
        valSMin = new JTextField("0",5);
        JLabel lb2 = new JLabel(" y ");
        valSMax = new JTextField("255",5);
        bShrinkAceptar = new JButton("Aceptar");
        bShrinkAceptar.addActionListener(this);
        
        shrink.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
        
        shrink.add(lb1);
        shrink.add(valSMin);
        shrink.add(lb2);
        shrink.add(valSMax);
        shrink.add(bShrinkAceptar);
    }
    /*
    JProgressBar progreso;
    JFrame progress;
    
    public void progreso() {
        // Si no se maneja con hilos (Threads) la operacion toma mucho mas tiempo
        progress = new JFrame("Progreso");
        progreso = new JProgressBar();
        
        progress.add(progreso);
    }*/
    
    public void raster() {
        // metodo que guarda los valores de los pixeles en varios arreglos
        
        int contador;
        PixelPacket actual;
        int roj, ver, az;
        
        try {
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            
            /*
            progreso();
            progress.setUndecorated(true);
            progress.setVisible(true);
            progress.pack();
            progress.setLocation(100, 100);
            
            progreso.setMinimum(0);
            progreso.setMaximum(dimX*dimY);
            progreso.setIndeterminate(true);
            */
            
            arreglo = new byte[(dimX*dimY)*3];
            valores = new PixelPacket[dimY][dimX];
            mat2 = new PixelPacket[dimY][dimX];
            rojo = new int[dimX*dimY];
            verde = new int[dimX*dimY];
            azul = new int[dimX*dimY];
            
            contador = 0;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    
                    // Se obtienen los valores solo una vez para que sea mas rapido
                    actual = imag.getOnePixel(i, j);
                    roj = actual.getRed();
                    ver = actual.getGreen();
                    az = actual.getBlue();
                    
                    // Se guardan
                    arreglo[3*contador] = (byte)roj;
                    rojo[contador] = roj;
                    arreglo[3*contador+1] = (byte) ver;
                    verde[contador] = ver;
                    arreglo[3*contador+2] = (byte) az;
                    azul[contador] = az;
                    valores[j][i] = actual;
                    mat2[j][i] = actual;
                    
                    contador++;
                    
                    /*
                    progreso.setValue(contador);
                    progreso.setStringPainted(true); 
                    
                    Rectangle progressRect = progreso.getBounds(); 
                    progressRect.x = 0; 
                    progressRect.y = 0; 
                    progreso.paintImmediately(progressRect);*/
                }
            }
            
            //progreso.setValue(dimX*dimY);
            
        } catch (MagickException e) {
            e.printStackTrace();
        }
        //progress.dispose();
    }
    
    public void antiAliasing() {
        // metodo que hace el antialiasing
        
        raster();
        
        int limiteX = dimX;
        int limiteY = dimY;
        
        int sumaR=0;
        int sumaG=0;
        int sumaB=0;
        int pixeles=1;  // sirve para saber cuantos pixeles tiene como vecinos el pixel actual
        
        
        /*
            6   7   8
            5   0   1
            4   3   2
         */
        
        for(int j=0; j<dimY; j++) {
            for(int i=0; i<dimX; i++) {
                
                // Se inicializan las sumas cada ciclo
                sumaR = 0;
                sumaG = 0;
                sumaB = 0;
                
                sumaR = sumaR + valores[j][i].getRed();
                sumaG = sumaG + valores[j][i].getGreen();
                sumaB = sumaB + valores[j][i].getBlue();
                
                pixeles = 1;    // se incluye el pixel actual
                
                // cada condicion obtiene el valor de sus vecinos en caso de que los tenga
                
                if(i+1!=limiteX) {                      // 1
                    sumaR = sumaR + valores[j][i+1].getRed();
                    sumaG = sumaG + valores[j][i+1].getGreen();
                    sumaB = sumaB + valores[j][i+1].getBlue();
                    pixeles++;  // Si encuentra este pixel aumenta el numero de vecinos
                }
                if( (i+1!=limiteX)&&(j+1!=limiteY) ) {  // 2
                    sumaR = sumaR + valores[j+1][i+1].getRed();
                    sumaG = sumaG + valores[j+1][i+1].getGreen();
                    sumaB = sumaB + valores[j+1][i+1].getBlue();
                    pixeles++;
                }
                if(j+1!=limiteY) {                      // 3
                    sumaR = sumaR + valores[j+1][i].getRed();
                    sumaG = sumaG + valores[j+1][i].getGreen();
                    sumaB = sumaB + valores[j+1][i].getRed();
                    pixeles++;
                }
                if( (i!=0)&&(j+1!=limiteY) ) {          // 4
                    sumaR = sumaR + valores[j+1][i-1].getRed();
                    sumaG = sumaG + valores[j+1][i-1].getGreen();
                    sumaB = sumaB + valores[j+1][i-1].getBlue();
                    pixeles++;
                }
                if(i!=0) {                              // 5
                    sumaR = sumaR + valores[j][i-1].getRed();
                    sumaG = sumaG + valores[j][i-1].getGreen();
                    sumaB = sumaB + valores[j][i-1].getBlue();
                    pixeles++;
                }
                if( (i!=0)&&(j!=0) ) {                  // 6
                    sumaR = sumaR + valores[j-1][i-1].getRed();
                    sumaG = sumaG + valores[j-1][i-1].getGreen();
                    sumaB = sumaB + valores[j-1][i-1].getBlue();
                    pixeles++;
                }
                if(j!=0) {                              // 7
                    sumaR = sumaR + valores[j-1][i].getRed();
                    sumaG = sumaG + valores[j-1][i].getGreen();
                    sumaB = sumaB + valores[j-1][i].getBlue();
                    pixeles++;
                }
                if( (i+1!=limiteX)&&(j!=0) ) {          // 8
                    sumaR = sumaR + valores[j-1][i+1].getRed();
                    sumaG = sumaG + valores[j-1][i+1].getGreen();
                    sumaB = sumaB + valores[j-1][i+1].getBlue();
                    pixeles++;
                }
                
                // Pixeles en la esquina: Vecinos = 4
                // Pixeles en borde: Vecinos = 6
                // Otros pixeles: Vecinos = 9
                // *Incluyendo el actual
                
                // Se divide entre los pixeles vecinos + el actual
                sumaR = sumaR/pixeles;
                sumaG = sumaG/pixeles;
                sumaB = sumaB/pixeles;
                
                // se le da el nuevo valor al pixel en la nueva matriz para no afectar la original
                mat2[j][i].setRed(sumaR);
                mat2[j][i].setGreen(sumaG);
                mat2[j][i].setBlue(sumaB);
                
            }
        }   // Termina for
        
        // Copia la matriz en el arreglo unidimensional de bytes para poder crear la imagen
        
        int contador = 0;
        
        for(int j=0; j<dimY; j++) {
            for(int i=0; i<dimX; i++) {
                arreglo[3*contador] = (byte) mat2[j][i].getRed();
                arreglo[3*contador+1] = (byte) mat2[j][i].getGreen();
                arreglo[3*contador+2] = (byte) mat2[j][i].getBlue();
                contador++;
            }
        }
        
        try {
            imag.constituteImage(dimX, dimY, "RGB", arreglo);
            canvas.setImage(imag);
        } catch(MagickException f) {
            f.printStackTrace();
        }
        
    }
    
    public void histogramGrey() {
        // Este es solo para las imagenes en escala de grises
        
        try {
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            
            int[] frecuencia = new int[256];
            int pos=0;
            
            for(int i=0; i<256; i++) {  // Inicializa las frecuencias en 0
                frecuencia[i] = 0;
            }
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    pos = imag.getOnePixel(i, j).getRed();  // intensidad del color (funciona ya sea rojo, verde o azul)
                    
                    if(pos>255) pos = 255; // validacion por si el canal tiene mas de 8bits
                    
                    frecuencia[pos] = frecuencia[pos] + 1;  // Aumenta esa intensidad de color en 1
                }
            }
            
            int max = 0;
            
            for(int i=0; i<256; i++) {
                if(frecuencia[i]>max) max = frecuencia[i];  // Obtiene la frecuencia maxima, no el valor que mas se repite
            }
            
            int[][] grafica = new int[100][256];
            
            for(int j=0; j<100; j++) {  // Inicializa la matriz del histograma con fondo blanco
                for(int i=0; i<256; i++) {
                    grafica[j][i] = 255;
                }
            }
            
            int normalizado = 0;
            
            for(int i=0; i<256; i++) {
                normalizado = Math.round((100*frecuencia[i])/max);  // Aplica la formula para normalizar
                for(int j=99; j>(100-normalizado)&&j>=0; j--) {     // Crea las barras negras (recorre la matriz al reves)
                    grafica[j][i] = 0;  // pone el pixel en negro
                }
            }
            
            int pos2 = 0;
            byte[] finalH = new byte[(100*256)*3];
            
            for(int j=0; j<100; j++) {      // Guarda la grafica en un arreglo para crear una imagen con el
                for(int i=0; i<256; i++) {
                    finalH[3*pos2] = (byte) grafica[j][i];
                    finalH[3*pos2+1] = (byte) grafica[j][i];
                    finalH[3*pos2+2] = (byte) grafica[j][i];
                    pos2++;
                }
            }
            
            // Crea la imagen en una nueva ventana
            h1 = new histograma (ruta.substring(ruta.lastIndexOf("\\")+1,ruta.lastIndexOf(".")),256, 100, finalH);
            h1.setVisible(true);
            h1.setLocation(this.getX()+100,this.getY()+100);
            h1.pack();
            h1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
        } catch(MagickException e) {
            e.printStackTrace();
        }
    }
    
    public void histogramColor() {
        // Practicamente lo mismo que el de escala de grises
        // pero se procesa cada canal por separado
        
        // Es como crear 3 histogramas de grises
        
        try {
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            
            // Frecuencias por cada canal
            int[] frecuenciaR = new int[256];
            int[] frecuenciaG = new int[256];
            int[] frecuenciaB = new int[256];
            int pos=0;
            PixelPacket act;
            
            for(int i=0; i<256; i++) {  // Inicializa las frecuencias en 0
                frecuenciaR[i] = 0;
                frecuenciaG[i] = 0;
                frecuenciaB[i] = 0;
            }
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    // Obtiene solo una vez el PixelPacket para que no tarde tanto
                    act = imag.getOnePixel(i, j);
                    
                    pos = act.getRed();  // intensidad del color (funciona ya sea rojo, verde o azul)
                    if(pos>255) pos = 255;
                    frecuenciaR[pos] = frecuenciaR[pos] + 1;  // Aumenta esa intensidad de color en 1
                    
                    pos = act.getGreen();
                    if(pos>255) pos = 255;
                    frecuenciaG[pos] = frecuenciaG[pos] + 1;
                    
                    pos = act.getBlue();
                    if(pos>255) pos = 255;
                    frecuenciaB[pos] = frecuenciaB[pos] + 1;
                }
            }
            
            int maxR = 0;
            int maxG = 0;
            int maxB = 0;
            
            for(int i=0; i<256; i++) {
                if(frecuenciaR[i]>maxR) maxR = frecuenciaR[i];  // Obtiene la frecuencia maxima, no el valor que mas se repite
                
                if(frecuenciaG[i]>maxG) maxG = frecuenciaG[i];
                
                if(frecuenciaB[i]>maxB) maxB = frecuenciaB[i];
            }
            
            int[][] graficaR = new int[100][256];
            int[][] graficaG = new int[100][256];
            int[][] graficaB = new int[100][256];
            
            for(int j=0; j<100; j++) {  // Inicializa la matriz del histograma con fondo blanco
                for(int i=0; i<256; i++) {
                    graficaR[j][i] = 255;
                    graficaG[j][i] = 255;
                    graficaB[j][i] = 255;
                }
            }
            
            int normalizado = 0;
            
            for(int i=0; i<256; i++) {
                normalizado = Math.round((100*frecuenciaR[i])/maxR);  // Aplica la formula para normalizar
                for(int j=99; j>(100-normalizado)&&j>=0; j--) {     // Crea las barras negras (recorre la matriz al reves)
                    graficaR[j][i]=0;
                }
                
                normalizado = Math.round((100*frecuenciaG[i])/maxG);  // Aplica la formula para normalizar
                for(int j=99; j>(100-normalizado)&&j>=0; j--) {     // Crea las barras negras (recorre la matriz al reves)
                    graficaG[j][i] = 0;
                }
                
                normalizado = Math.round((100*frecuenciaB[i])/maxB);  // Aplica la formula para normalizar
                for(int j=99; j>(100-normalizado)&&j>=0; j--) {     // Crea las barras negras (recorre la matriz al reves)
                    graficaB[j][i] = 0;
                }
            }
            
            int pos2 = 0;
            byte[] finalR = new byte[(100*256)*3];
            byte[] finalG = new byte[(100*256)*3];
            byte[] finalB = new byte[(100*256)*3];
            
            for(int j=0; j<100; j++) {      // Guarda la grafica en un arreglo para crear una imagen con el
                for(int i=0; i<256; i++) {
                    if(graficaR[j][i]==0) {
                        finalR[3*pos2] = (byte) 255;
                        finalR[3*pos2+1] = 0;
                        finalR[3*pos2+2] = 0;
                    } else {
                        finalR[3*pos2] = (byte) 255;
                        finalR[3*pos2+1] = (byte) 255;
                        finalR[3*pos2+2] = (byte) 255;
                    }
                    
                    if(graficaG[j][i]==0) {
                        finalG[3*pos2] = 0;
                        finalG[3*pos2+1] = (byte) 255;
                        finalG[3*pos2+2] = 0;
                    } else {
                        finalG[3*pos2] = (byte) 255;
                        finalG[3*pos2+1] = (byte) 255;
                        finalG[3*pos2+2] = (byte) 255;
                    }
                    
                    if(graficaB[j][i]==0) {
                        finalB[3*pos2] = 0;
                        finalB[3*pos2+1] = 0;
                        finalB[3*pos2+2] = (byte) 255;
                    } else {
                        finalB[3*pos2] = (byte) 255;
                        finalB[3*pos2+1] = (byte) 255;
                        finalB[3*pos2+2] = (byte) 255;
                    }
                    
                    pos2++;
                }
            }
            
            // Crea la imagen en una nueva ventana
            if(ruta.equals("")) ruta = "\\Imagen no guardada.";
            
            h1 = new histograma (ruta.substring(ruta.lastIndexOf("\\")+1,ruta.lastIndexOf(".")),256, 100, finalR);
            h1.setVisible(true);
            h1.setLocation(this.getX()+100,this.getY()+100);
            h1.pack();
            h1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            h2 = new histograma (ruta.substring(ruta.lastIndexOf("\\")+1,ruta.lastIndexOf(".")),256, 100, finalG);
            h2.setVisible(true);
            h2.setLocation(h1.getX(),h1.getY()+h1.getHeight());
            h2.pack();
            h2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            h3 = new histograma (ruta.substring(ruta.lastIndexOf("\\")+1,ruta.lastIndexOf(".")),256, 100, finalB);
            h3.setVisible(true);
            h3.setLocation(h2.getX(),h2.getY()+h1.getHeight());
            h3.pack();
            h3.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
        } catch(MagickException e) {
            e.printStackTrace();
        }
    }
    
    public void stretchGrey() {
        // Amplia el histograma
        
        try {
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            
            int pos=0;
            
            int max = 0, min = 255;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    pos = imag.getOnePixel(i, j).getRed();  // intensidad del color (funciona ya sea rojo, verde o azul)
                    
                    if(pos>max) max = pos;      // Obtiene el maximo y minimo valor de intensidad presente en la imagen
                    if(pos<min) min = pos;
                }
            }
            
            if(max == min) {
                // Esto pasa cuando se tienen niveles constantes. Ejemplo: una imagen completamente negra
                JOptionPane.showMessageDialog(this, "No se puede expandir el histograma", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            byte[] arr = new byte[(dimX*dimY)*3];
            int val = 0, actual = 0, cont = 0;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    actual = imag.getOnePixel(i, j).getRed();
                    val = ((actual-min)*255/(max-min)); // Aplica la formula para ampliar el histograma
                    
                    arr[3*cont] = (byte) val;
                    arr[3*cont+1] = (byte) val;
                    arr[3*cont+2] = (byte) val;
                    cont++;
                }
            }
            
            imag.constituteImage(dimX, dimY, "RGB", arr);
            canvas.setImage(imag);
            histogramGrey();
            
        } catch(MagickException e) {
            e.printStackTrace();
        }
    }
    
    public void stretchColor() {
        // Igual que el de escala de grises, pero se analiza cada canal por separado
        
        try {
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            
            int pos=0;
            
            int maxR = 0, maxG = 0, maxB = 0;
            int minR = 255, minG = 255, minB = 255;
            PixelPacket act;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    act = imag.getOnePixel(i, j);
                    
                    pos = act.getRed();  // intensidad del color
                    if(pos>maxR) maxR = pos;
                    if(pos<minR) minR = pos;
                    
                    pos = act.getGreen();
                    if(pos>maxG) maxG = pos;
                    if(pos<minG) minG = pos;
                    
                    pos = act.getBlue();
                    if(pos>maxB) maxB = pos;
                    if(pos<minB) minB = pos;
                }
            }
            
            if((maxR == minR)||(maxG == minG)||(maxB == minB)) {
                // Esto pasa cuando se tienen niveles constantes. Ejemplo: una imagen completamente roja
                JOptionPane.showMessageDialog(this, "No se puede expandir el histograma", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            byte[] arr = new byte[(dimX*dimY)*3];
            int val = 0, actual = 0, cont = 0;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    act = imag.getOnePixel(i, j);
                    
                    actual = act.getRed();
                    val = ((actual-minR)*255/(maxR-minR));
                    arr[3*cont] = (byte) val;
                    
                    actual = act.getGreen();
                    val = ((actual-minG)*255/(maxG-minG));
                    arr[3*cont+1] = (byte) val;
                    
                    actual = act.getBlue();
                    val = ((actual-minB)*255/(maxB-minB));
                    arr[3*cont+2] = (byte) val;
                    
                    cont++;
                }
            }
            
            imag.constituteImage(dimX, dimY, "RGB", arr);
            canvas.setImage(imag);
            histogramColor();
            
        } catch(MagickException e) {
            e.printStackTrace();
        }
    }
    
    public void shrinkGrey(int shrinkMin, int shrinkMax) {
        // Recibe dos valores entre los que se encogera el histograma
        
        try {
            int aux = 0;
            
            if(shrinkMax < shrinkMin) { // Intercambia valores si fueron proporcionado al reves
                // Esto evita que ocurran errores de ejecucion
                aux = shrinkMin;
                shrinkMin = shrinkMax;
                shrinkMax = aux;
            }
            
            int min=255, max=0;
            int actual;
            
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            
            for(int j=0; j<dimY; j++) {     // saca el maximo y el minimo
                for(int i=0; i<dimX; i++) {
                    actual = imag.getOnePixel(i, j).getRed();
                    
                    if(actual>max) max = actual;
                    if(actual<min) min = actual;
                }
            }
            
            if(max==min) {
                // Esto pasa cuando se tienen niveles constantes. Ejemplo: una imagen completamente roja
                JOptionPane.showMessageDialog(this, "No se puede encoger el histograma", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            byte[] arr = new byte[(dimX*dimY)*3];
            
            int cont = 0;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    actual = imag.getOnePixel(i, j).getRed();
                    
                    // Aplica la formula
                    actual = ((shrinkMax-shrinkMin)*(actual-min)/(max-min))+shrinkMin;
                    
                    arr[3*cont] = (byte) actual;
                    arr[3*cont+1] = (byte) actual;
                    arr[3*cont+2] = (byte) actual;
                    cont++;
                }
            }
            
            imag.constituteImage(dimX, dimY, "RGB", arr);
            canvas.setImage(imag);
            
            histogramGrey();
            
        } catch(MagickException e) {
            e.printStackTrace();
        }
    }
    
    public void shrinkColor(int shrinkMin, int shrinkMax) {
        // Igual que el de la escala de grises pero se analizan los colores por separado
        
        try {
            int aux = 0;
            
            if(shrinkMax < shrinkMin) { // Intercambia valores si fueron proporcionados al reves
                aux = shrinkMin;
                shrinkMin = shrinkMax;
                shrinkMax = aux;
            }
            
            int minR=255, maxR=0;
            int minG=255, maxG=0;
            int minB=255, maxB=0;
            
            int actual;
            
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            PixelPacket act;
            
            for(int j=0; j<dimY; j++) {     // saca el maximo y el minimo
                for(int i=0; i<dimX; i++) {
                    act = imag.getOnePixel(i, j);
                    
                    actual = act.getRed();
                    if(actual>maxR) maxR = actual;
                    if(actual<minR) minR = actual;
                    
                    actual = act.getGreen();
                    if(actual>maxG) maxG = actual;
                    if(actual<minG) minG = actual;
                    
                    actual = act.getBlue();
                    if(actual>maxB) maxB = actual;
                    if(actual<minB) minB = actual;
                }
            }
            
            if((maxR == minR)||(maxG == minG)||(maxB == minB)) {
                // Esto pasa cuando se tienen niveles constantes. Ejemplo: una imagen completamente roja
                JOptionPane.showMessageDialog(this, "No se puede encoger el histograma", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            byte[] arr = new byte[(dimX*dimY)*3];
            
            int cont = 0;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    act = imag.getOnePixel(i, j);
                    
                    actual = act.getRed();
                    actual = ((shrinkMax-shrinkMin)*(actual-minR)/(maxR-minR))+shrinkMin;
                    arr[3*cont] = (byte) actual;
                    
                    actual = act.getGreen();
                    actual = ((shrinkMax-shrinkMin)*(actual-minG)/(maxG-minG))+shrinkMin;
                    arr[3*cont+1] = (byte) actual;
                    
                    actual = act.getBlue();
                    actual = ((shrinkMax-shrinkMin)*(actual-minB)/(maxB-minB))+shrinkMin;
                    arr[3*cont+2] = (byte) actual;
                    
                    cont++;
                }
            }
            
            imag.constituteImage(dimX, dimY, "RGB", arr);
            canvas.setImage(imag);
            histogramColor();
            
        } catch(MagickException e) {
            e.printStackTrace();
        }
    }
    
    public void slideGrey(int offset) {
        try {
            // offset: Un valor positivo lo mueve a la izquierda, un negativo a la derecha
            
            /*
             * Debido a la forma en la que se hacen las operaciones se debe multiplicar por -1 para que
             * el programa actue con cierta logica esperada por el usuario. Se asume que el usuario pensara
             * que si introduce un valor positivo el histograma se recorrera en sentido positivo (a la derecha).
             */
            offset = offset*(-1);
            
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            
            byte[] arr = new byte[(dimX*dimY)*3];
            
            int contador = 0;
            int nuevo = 0;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    nuevo = imag.getOnePixel(i, j).getRed();
                    nuevo = nuevo - offset;
                    // Si llega a algun borde horizontal los valores se empiezan a acumular en el
                    if(nuevo<0) nuevo = 0;
                    if(nuevo>255) nuevo = 255;
                    arr[3*contador] = (byte) nuevo;
                    arr[3*contador+1] = (byte) nuevo;
                    arr[3*contador+2] = (byte) nuevo;
                    contador++;
                }
            }
            
            imag.constituteImage(dimX, dimY, "RGB", arr);
            canvas.setImage(imag);
            histogramGrey();
            
        } catch(MagickException e) {
            e.printStackTrace();
        }
    }
    
    public void slideColor(int offset) {
        try {
            // offset: Un valor positivo lo mueve a la izquierda, un negativo a la derecha
            
            offset = offset*(-1);
            
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            
            byte[] arr = new byte[(dimX*dimY)*3];
            PixelPacket actual;
            
            int contador = 0;
            int nuevo = 0;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    actual = imag.getOnePixel(i, j);
                    
                    nuevo = actual.getRed();
                    nuevo = nuevo - offset;
                    if(nuevo<0) nuevo = 0;
                    if(nuevo>255) nuevo = 255;
                    arr[3*contador] = (byte) nuevo;
                    
                    nuevo = actual.getGreen();
                    nuevo = nuevo - offset;
                    if(nuevo<0) nuevo = 0;
                    if(nuevo>255) nuevo = 255;
                    arr[3*contador+1] = (byte) nuevo;
                    
                    nuevo = actual.getBlue();
                    nuevo = nuevo - offset;
                    if(nuevo<0) nuevo = 0;
                    if(nuevo>255) nuevo = 255;
                    arr[3*contador+2] = (byte) nuevo;
                    contador++;
                }
            }
            
            imag.constituteImage(dimX, dimY, "RGB", arr);
            canvas.setImage(imag);
            histogramColor();
            
        } catch(MagickException e) {
            e.printStackTrace();
        }
    }
    
    public void buscar() {
        
        // Por ahora este codigo analiza las vecindades D8 para encontrar pixeles solitarios
        // (un solo pixel negro rodeado de blanco)
        
        int j,i,iI=0,iJ=0,auxI=0,auxJ=0,figuras=0,maxX=0,maxY=0;
        long pixeles;
        Boolean b1,b2,b3,b4,b5,b6,b7,b8,arriba,abajo,derecha,izquierda,pix,vecinos,primera;
        int limiteX = dimX;
        int limiteY = dimY;
        
        matriz2 = new int[dimY][dimX];
        
        pixeles=0;
        
        b1=false;
        b2=false;
        b3=false;
        b4=false;
        b5=false;
        b6=false;
        b7=false;
        b8=false;
        pix=false;
        vecinos=false;
        primera=false;
        
        // arriba y abajo se inicializan como verdaderos ya que la imagen se recorre de arriba hacia abajo y de izquierda a derecha
        
        arriba=false;
        abajo=true;
        derecha=true;
        izquierda=false;
        
        for(j=0; j<limiteY; j++) {      // se inicializa como un "lienzo" en blanco
            for(i=0; i<limiteX; i++) {
                matriz2[j][i]=255;
            }
        }
        
        for(j=0; j<limiteY; j++) {          // Cuenta solo los pixeles solitarios
            for(i=0; i<limiteX; i++) {
                if(matriz[j][i]==0&&matriz2[j][i]==255) {
                    
                    if(i+1!=limiteX) {
                        if(matriz[j][i+1]==255) {
                            b1=true;
                        } else b1=false;
                    }
                    if( (i+1!=limiteX)&&(j+1!=limiteY) ) {
                        if(matriz[j+1][i+1]==255) {
                            b2=true;
                        } else b2=false;
                    }
                    if(j+1!=limiteY) {
                        if(matriz[j+1][i]==255) {
                            b3=true;
                        } else b3=false;
                    }
                    if( (i!=0)&&(j+1!=limiteY) ) {
                        if(matriz[j+1][i-1]==255) {
                            b4=true;
                        } else b4=false;
                    }
                    if(i!=0) {
                        if(matriz[j][i-1]==255) {
                            b5=true;
                        } else b5=false;
                    }
                    if( (i!=0)&&(j!=0) ) {
                        if(matriz[j-1][i-1]==255) {
                            b6=true;
                        } else b6=false;
                    }
                    if(j!=0) {
                        if(matriz[j-1][i]==255) {
                            b7=true;
                        } else b7=false;
                    }
                    if( (i+1!=limiteX)&&(j!=0) ) {
                        if(matriz[j-1][i+1]==255) {
                            b8=true;
                        } else b8=false;
                    }
                    if(b1&&b2&&b3&&b4&&b5&&b6&&b7&&b8) {
                        pixeles++;
                        matriz2[j][i]=0;
                    }
                }
                
                b1=false;
                b2=false;
                b3=false;
                b4=false;
                b5=false;
                b6=false;
                b7=false;
                b8=false;
            }
        }     // Cuenta solo los pixeles solitarios
        
        for(j=0; j<limiteY; j++) {      // se inicializa como un "lienzo" en blanco
            for(i=0; i<limiteX; i++) {
                matriz2[j][i]=0;
            }
        }
        
        j=0;
        i=0;
        while(j<limiteY) {
            i=0;
            while(i<limiteX) {
                if(matriz[j][i]==0&&matriz2[j][i]==255) {
                    iI=i;
                    iJ=j;
                    auxI=0;
                    matriz2[iJ][iI]=0;
                    while(matriz[iJ][iI]==0) {     // se encuentran los maximos y minimos
                        b5=false;
                        b1=false;
                        matriz2[iJ][iI]=0;
                        iI=i-auxI;
                        while(matriz[iJ][iI]==0) {
                            matriz2[iJ][iI]=0;
                            if(iI>maxX) maxX=iI;
                            iI++;
                            if(iI==limiteX||iJ==limiteY)break;
                        }
                        if(iI==limiteX||iJ==limiteY) break;
                        matriz2[iJ][iI]=0;
                        if(iJ>maxY) maxY=iJ;
                        iJ++;
                        if(iI==limiteX||iJ==limiteY) break;
                        matriz2[iJ][iI]=0;
                        iI=i-auxI;
                        
                        if(iI+1!=limiteX) {
                            if(matriz[iJ][iI+1]==0) {
                                b1=true;
                            } else b1=false;
                        }
                        
                        if(iJ+1!=limiteY) {
                            if(matriz[iJ+1][iI]==0) {
                                b3=true;
                            } else b3=false;
                        }
                        
                        if(iI!=0) {
                            if(matriz[iJ][iI-1]==0) {
                                b5=true;
                            } else b5=false;
                        }
                        
                        if(iJ!=0) {
                            if(matriz[iJ-1][iI]==0) {
                                b7=true;
                            } else b7=false;
                        }
                        
                        if(b1&&b3&&!b5&&!b7) auxI++;
                        if(b1&&!b3&&!b5&&b7) auxI--;
                    }
                    figuras++;
                    i=0;
                    j=0;
                }
                i++;
            }
            j++;
        }
        
        reporte r = new reporte();
        r.setVisible(true);
        r.setLocation(this.getX()+100,this.getY()+100);
        r.setSize(500,300);
        r.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        area.setText("Pixeles solitarios: "+String.valueOf(pixeles)+"\n");
        area.append("Figuras: "+String.valueOf(figuras-pixeles)+"\n");
    }
    
    public void sepia() {
        
        /*
            http://stackoverflow.com/questions/1061093/how-is-a-sepia-tone-created
            http://stackoverflow.com/questions/9448478/what-is-wrong-with-this-sepia-tone-conversion-algorithm/9448635#9448635
         */
        
        try {
            
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            
            byte[] arr = new byte[(dimX*dimY)*3];
            PixelPacket aux;
            
            int contador = 0, rojo, verde, azul;
            int rojo2, verde2, azul2;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    aux = imag.getOnePixel(i, j);
                    rojo = aux.getRed();
                    verde = aux.getGreen();
                    azul = aux.getBlue();
                    
                    /*
                    outputRed = (inputRed * .393) + (inputGreen *.769) + (inputBlue * .189)
                    outputGreen = (inputRed * .349) + (inputGreen *.686) + (inputBlue * .168)
                    outputBlue = (inputRed * .272) + (inputGreen *.534) + (inputBlue * .131)
                     */
                    
                    // Los valores constantes son recomendados por microsoft (sin referencia)
                    
                    rojo2 = (int) ((rojo*0.393)+(verde*0.769)+(azul*0.189));
                    if(rojo2>255) rojo2 = 255;
                    
                    verde2 = (int) ((rojo*0.349)+(verde*0.686)+(azul*0.168));
                    if(verde2>255) verde2 = 255;
                    
                    azul2 = (int) ((rojo*0.272)+(verde*0.534)+(azul*0.131));
                    if(azul2>255) azul2 = 255;
                    
                    arr[3*contador] = (byte)rojo2;
                    arr[3*contador+1] = (byte)verde2;
                    arr[3*contador+2] = (byte)azul2;
                    
                    contador++;
                }
            }
            imag.constituteImage(dimX, dimY, "RGB", arr);
            canvas.setImage(imag);
        } catch (MagickException f) {
            f.printStackTrace();
        }
    }
    
    public void remolinoVent() {
        remolino = new JFrame("Remolino");
        remolino.setVisible(true);
        remolino.setResizable(false);
        remolino.setLocation(this.getX()+25, this.getY()+25);
        remolino.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        remolino.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
        
        cantRem = new JTextField("0.03",5);
        
        derRem = new JRadioButton("Hacia la derecha");
        derRem.setSelected(true);
        derRem.addActionListener(this);
        
        izqRem = new JRadioButton("Hacia la izquierda");
        izqRem.setSelected(false);
        izqRem.addActionListener(this);
        
        aceptRemolino = new JButton("Aceptar");
        aceptRemolino.addActionListener(this);
        
        remolino.add(new JLabel("Cantidad a rotar"));
        remolino.add(cantRem);
        remolino.add(derRem);
        remolino.add(izqRem);
        remolino.add(aceptRemolino);
    }
    
    public void pixVent() {
        pixelear = new JFrame("Pixelear imagen");
        pixelear.setVisible(true);
        pixelear.setResizable(false);
        pixelear.setLocation(this.getX()+25, this.getY()+25);
        pixelear.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        pixelear.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
        
        tamPix = new JTextField("10",5);
        
        aceptPix = new JButton("Aceptar");
        aceptPix.addActionListener(this);
        
        pixelear.add(new JLabel("Tamaño de los pixeles"));
        pixelear.add(tamPix);
        pixelear.add(aceptPix);
    }
    
    public void remolino(double cantidad) {
        /* Pagina de donde se sacaron los pasos para el algoritmo
         * 
         * http://supercomputingblog.com/openmp/image-twist-and-swirl-algorithm/
         */
        
        // Con valores muy altos en cantidad se pierde el efecto
        
        // Hace un efecto de remolino con la imagen
        
        try {
            
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            
            //Ubicacion de los pixeles en el centro de la imagen
            double centroX = dimX/2;
            double centroY = dimY/2;
            
            // Se usaran para almacenar la distancia del pixel actual a la imagen
            double distX;
            double distY;
            
            // matriz en la que se guardaran las imagenes
            PixelPacket[][] matrix = new PixelPacket[dimY][dimX];
            
            // Se copian los valores de la imagen original en la matriz resultante para los pixeles no modificados
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    matrix[j][i] = imag.getOnePixel(i, j);
                }
            }
            
            double angulo, radio;
            
            // nuevas coordenadas
            int x=0, y=0;
            
            for(int j=0; j<dimY; j++) {
                // Se obtiene la distancia en Y
                distY = j-centroY;
                
                for(int i=0; i<dimX; i++) {
                    
                    // Se convierten las coordenadas a polares para saber donde ira el pixel actual
                    
                    // Se obtiene la distancia en X
                    distX = i-centroX;
                    
                    // se obtiene el angulo original en radianes
                    angulo = Math.atan2(distY, distX)+Math.PI;
                    
                    // Se obtiene el radio original
                    radio = Math.sqrt(distX*distX + distY+distY);
                    
                    // Se obtiene el nuevo angulo
                    angulo = angulo + radio*cantidad;
                    
                    // Se obtienen las nuevas coordenadas
                    x = (int) Math.floor(radio*Math.cos(angulo)+0.5);
                    y = (int) Math.floor(radio*Math.sin(angulo)+0.5);
                    
                    // Se traspasa eso al centro ya que se inicia desde 0,0
                    x = (int)(x+centroX);
                    y = (int)(y+centroY);
                    
                    // Si se quita la siguiente condicion la rotacion de la espiral es a la izquierda
                    if(derRem.isSelected())
                        y = dimY - y;
                    
                    // Validaciones
                    
                    if(x<0) x = 0;
                    if(x>=dimX) x = dimX-1;
                    
                    if(y<0) y = 0;
                    if(y>=dimY) y = dimY-1;
                    
                    matrix[y][x] = imag.getOnePixel(i, j);
                }
            }
            
            PixelPacket actual;
            byte[] arr = new byte[(dimX*dimY)*3];
            int contador = 0;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    actual = matrix[j][i];
                    
                    arr[3*contador] = (byte) actual.getRed();
                    arr[3*contador+1] = (byte) actual.getGreen();
                    arr[3*contador+2] = (byte) actual.getBlue();
                    contador++;
                }
            }
            
            imag.constituteImage(dimX, dimY, "RGB", arr);
            canvas.setImage(imag);
            
        } catch(MagickException f) {
            f.printStackTrace();
        }
    }
    
    public void pixelear(int tam) {
        // http://tech-algorithm.com/articles/nearest-neighbor-image-scaling/
        // http://stackoverflow.com/questions/4047031/help-with-the-theory-behind-a-pixelate-algorithm
        
        try {
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            PixelPacket actual;
            PixelPacket[][] matrix = new PixelPacket[dimY][dimX];
            int x=0, y=0;
            
            // Se copian los valores de la imagen original en la matriz resultante para los pixeles no modificados
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    matrix[j][i] = imag.getOnePixel(i, j);
                }
            }
            
            // Se crea la imagen que con valores pixeleados
            for(int j=0; j<dimY; j+=tam) {
                for(int i=0; i<dimX; i+=tam) {
                    // Se obtiene el valor del pixel agrandado
                    actual = imag.getOnePixel(i, j);
                    
                    // se coloca el color del pixel agrandado en el rectangulo del tamaño indicado por el usuario
                    y=j;
                    while(y<(tam+j)) {
                        x=i;
                        while(x<(tam+i)) {
                            if(x>=dimX) x = dimX-1;
                            if(y>=dimY) y = dimY-1;
                            
                            matrix[y][x] = actual;
                            
                            x++;
                            if(x==dimX) break;
                        }
                        y++;
                        
                        // Cuando es el ultimo renglon se cumple esto
                        if(y==dimY) break;
                    }
                }
            }
            
            byte[] arr = new byte[(dimX*dimY)*3];
            int contador = 0;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    arr[3*contador] = (byte)matrix[j][i].getRed();
                    arr[3*contador+1] = (byte)matrix[j][i].getGreen();
                    arr[3*contador+2] = (byte)matrix[j][i].getBlue();
                    
                    contador++;
                }
            }
            
            imag.constituteImage(dimX, dimY, "RGB", arr);
            canvas.setImage(imag);
        } catch(MagickException f) {
            f.printStackTrace();
        }
    }
    
    public void pseudocolor() {
        // No representan los canales RGB, sino colores cualquiera que se pondran a la imagen
        // Se pueden agregar o cambiar estos colores modificando los valores que se asignan en al arreglo byte[]
        
        int minB = 0, maxB = 63;
        int minG = 64, maxG = 126;
        int minN = 127, maxN = 189;
        int minR = 190, maxR = 255;
        
        int val = 0;
        
        try {
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            MagickImage imag2 = new MagickImage();
            imag2 = imag;
            
            if(!imag.isGrayImage()) {   // Se tranforma a escala de grises si no lo es
                QuantizeInfo info = new QuantizeInfo();
                info = new QuantizeInfo();
                info.setColorspace(2);
                imag.quantizeImage(info);
            }
            
            int contador = 0;
            byte[] arr = new byte[dimX*dimY*3];
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    val = imag.getOnePixel(i, j).getRed();
                    
                    if(val>=minR&&val<=maxR) {       // rojo
                        arr[3*contador] = (byte) 255;
                        arr[3*contador+1] = 0;
                        arr[3*contador+2] = 0;
                        contador++;
                        continue;
                    }
                    
                    if(val>=minN&&val<=maxN) {       // naranja
                        arr[3*contador] = (byte) 255;
                        arr[3*contador+1] = (byte)128;
                        arr[3*contador+2] = 0;
                        contador++;
                        continue;
                    }
                    
                    if(val>=minG&&val<=maxG) {       // verde
                        arr[3*contador] = 0;
                        arr[3*contador+1] = (byte) 255;
                        arr[3*contador+2] = 0;
                        contador++;
                        continue;
                    }
                    
                    if(val>=minB&&val<=maxB) {       // azul
                        arr[3*contador] = 0;
                        arr[3*contador+1] = 0;
                        arr[3*contador+2] = (byte) 255;
                        contador++;
                        continue;
                    }
                    
                    // Si no cae en ninguna, en esta implentacion no deberia de pasar pero se deja como validacion extra
                    
                    arr[3*contador] = (byte) 255;
                    arr[3*contador+1] = (byte) 255;
                    arr[3*contador+2] = (byte) 255;
                    contador++;
                    
                }
            }
            
            imag2.constituteImage(dimX, dimY, "RGB", arr);
            imag = imag2;
            canvas.setImage(imag);
            
        } catch(MagickException f) {
            f.printStackTrace();
        }
    }
    
    public void densitySlicing() {
        
        // https://globaljournals.org/GJRE_Volume12/3-Identification-of-Early-Osteoporosis-Using.pdf
        
        try {
            dimX = imag.getDimension().width;
            dimY = imag.getDimension().height;
            
            // Se convierte a escala de grises
            QuantizeInfo info = new QuantizeInfo();
            info.setColorspace(2);
            imag.quantizeImage(info);
            
            int val = 0;
            int prom = 0;
            
            int[] frecuencia = new int[256];
            int pos=0;
            
            for(int i=0; i<256; i++) {  // Inicializa las frecuencias en 0
                frecuencia[i] = 0;
            }
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    pos = imag.getOnePixel(i, j).getRed();  // intensidad del color (funciona ya sea rojo, verde o azul)
                    
                    if(pos>255) pos = 255; // validacion por si el canal tiene mas de 8bits
                    
                    frecuencia[pos] = frecuencia[pos] + 1;  // Aumenta esa intensidad de color en 1
                    prom += pos;
                }
            }
            
            prom = prom/(dimX*dimY);
            
            int max = 0;
            int aux = 0;
            
            for(int j=0; j<256; j++) {
                // Saca la intensidad con mayor frecuencia
                if(frecuencia[j]>aux) {
                    aux = frecuencia[j];
                    max = j;
                }
            }
            
            int contador = 0;
            byte[] arr = new byte[(dimX*dimY)*3];
            
            // Se aplica el algoritmo del diagrama de flujo encontrado en la referencia a la implementacion
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    // Obtiene el valor de intensidad acutal
                    val = imag.getOnePixel(i, j).getRed();
                    
                    if(val>10) {
                        if(val>prom) {
                            if(val<max-10&&val>max-30) {
                                arr[3*contador] = (byte)255;
                                arr[3*contador+1] = 0;
                                arr[3*contador+2] = 0;
                            } else {
                                arr[3*contador] = (byte)val;
                                arr[3*contador+1] = (byte)val;
                                arr[3*contador+2] = (byte)val;
                            }
                        } else {
                            arr[3*contador] = (byte)val;
                            arr[3*contador+1] = (byte)val;
                            arr[3*contador+2] = (byte)val;
                        }
                    } else {
                        arr[3*contador] = (byte)val;
                        arr[3*contador+1] = (byte)val;
                        arr[3*contador+2] = (byte)val;
                    }
                    
                    contador++;
                }
            }
            
            imag.constituteImage(dimX, dimY, "RGB", arr);
            canvas.setImage(imag);
            
        } catch(MagickException f) {
            f.printStackTrace();
        }
    }
    
    public void procMorfologico() {
        // Hace el procesamiento morfologico para obtener los bordes de la imagen
        
        /*
         * Primero debe erosionarse la imagen con un kernel de 3x3 con todos los valores en uno
         * Si la vecindad del pixel analizado esta hecha con puros 1's entonces el pixel actual sera 1
         * Si no son completamente 1's entonces el pixel sera 0
         * 
         * Luego se hace la resta entre la imagen original y la resultante de la erosion
         * 
         * Se despliega la imagen final
         */
        
        // Primero se hace la imagen binaria
        QuantizeInfo info;
        long suma=0;
        double umbral=0;
        
        try {
            info = new QuantizeInfo();
            info.setColorspace(2);
            imag.quantizeImage(info);
            raster();
            
            for(int i=0; i<(dimX*dimY); i++) {
                suma+=arreglo[3*i];
            }
            
            umbral = suma / (dimX*dimY);
            
            for(int i=0; i<(dimX*dimY); i++) {
                if(arreglo[3*i]<(byte)umbral) {
                    arreglo[3*i] = 0;
                } else {
                    arreglo[3*i] = (byte)255;
                }
                
                if(arreglo[3*i+1]<(byte)umbral) {
                    arreglo[3*i+1] = 0;
                } else {
                    arreglo[3*i+1] = (byte)255;
                }
                
                if(arreglo[3*i+2]<(byte)umbral) {
                    arreglo[3*i+2] = 0;
                } else {
                    arreglo[3*i+2] = (byte)255;
                }
            }
            
            imag.constituteImage(dimX, dimY, "RGB", arreglo);
        } catch (MagickException f) {
            f.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se ha podido convertir la imagen a binario, no se continuara","Error",JOptionPane.ERROR_MESSAGE);
            return;
        } // Se termina la transformacion binaria
        
        try {
            int blanco = 255;
            int negro = 0;
            int sum = 0;
            // En la matriz 1 se guarda la imagen original
            int[][] mat1 = new int[dimY][dimX];
            // En la matriz 2 se guarda la imagen erosionada
            int[][] mat2 = new int[dimY][dimX];
            
            // Se copia la imagen en una matriz para su analisis
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    mat1[j][i] = imag.getOnePixel(i, j).getRed();
                }
            }
            
            // Se hace la erosion
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    sum = 0;
                    
                    if(mat1[j][i] == blanco) {
                        sum++;
                    }
                    if(i+1!=dimX) {
                        if(mat1[j][i+1] == blanco)
                            sum++;
                    }
                    if( (i+1!=dimX)&&(j+1!=dimY) ) {
                        if(mat1[j+1][i+1] == blanco)
                            sum++;
                    }
                    if(j+1!=dimY) {
                        if(mat1[j+1][i] == blanco)
                            sum++;
                    }
                    if( (i!=0)&&(j+1!=dimY) ) {
                        if(mat1[j+1][i-1] == blanco)
                            sum++;
                    }
                    if(i!=0) {
                        if(mat1[j][i-1] == blanco)
                            sum++;
                    }
                    if( (i!=0)&&(j!=0) ) {
                        if(mat1[j-1][i-1] == blanco)
                            sum++;
                    }
                    if(j!=0) {
                        if(mat1[j-1][i] == blanco)
                            sum++;
                    }
                    if( (i+1!=dimX)&&(j!=0) ) {
                        if(mat1[j-1][i+1] == blanco)
                            sum++;
                    }
                    
                    // Si el pixel esta rodeado de blancos (mas el actual)
                    if(sum==9) {
                        mat2[j][i] = blanco;
                    } else { // Cualquier otro caso
                        mat2[j][i] = negro;
                    }
                }
            }
            
            // Se hace la resta de la imagen original menos la erosionada
            
            byte[] arr = new byte[(dimX*dimY)*3];
            int contador = 0;
            
            for(int j=0; j<dimY; j++) {
                for(int i=0; i<dimX; i++) {
                    // Si la ambos son iguales
                    if(mat1[j][i]==mat2[j][i]) {
                        arr[3*contador] = (byte) negro;
                        arr[3*contador+1] = (byte) negro;
                        arr[3*contador+2] = (byte) negro;
                    } else {
                        arr[3*contador] = (byte) blanco;
                        arr[3*contador+1] = (byte) blanco;
                        arr[3*contador+2] = (byte) blanco;
                    }
                    contador++;
                }
            }
            
            imag.constituteImage(dimX, dimY, "RGB", arr);
            canvas.setImage(imag);
        } catch (MagickException e) {
            e.printStackTrace();
        }
    }
    
    public class reporte extends JFrame {
        // Despliega los resultados de la busqueda de figuras
        public reporte() {
            super("Reporte del analisis");
            area = new TextArea();
            area.setEditable(false);
            add(area);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource().equals(bMorfologico)) {
            procMorfologico();
        }
        
        if(e.getSource().equals(bAdjust)) {
            redimensiona();
        }
        
        if(e.getSource().equals(bDensity)) {
            densitySlicing();
        }
        
        if(e.getSource().equals(bRemolino)) {
            remolinoVent();
            remolino.pack();
        }
        
        if(e.getSource().equals(derRem)) {
            if(izqRem.isSelected()) {
                derRem.setSelected(true);
                izqRem.setSelected(false);
            } else {
                derRem.setSelected(true);
            }
        }
        
        if(e.getSource().equals(izqRem)) {
            if(derRem.isSelected()) {
                derRem.setSelected(false);
                izqRem.setSelected(true);
            } else {
                izqRem.setSelected(true);
            }
        }
        
        if(e.getSource().equals(aceptRemolino)) {
            remolino(Double.valueOf(cantRem.getText()));
            remolino.dispose();
        }
        
        if(e.getSource().equals(bPixelear)) {
            pixVent();
            pixelear.pack();
        }
        
        if(e.getSource().equals(aceptPix)) {
            pixelear(Integer.valueOf(tamPix.getText()));
            pixelear.dispose();
        }
        
        if(e.getSource().equals(bSaveAs)) {
            String ruta1="",formato="";
            JFileChooser FC = new JFileChooser();
            
            /* Anque JMagick soporta mas de 100 formatos de imagenes, e incluso puede abrir imagenes con extensiones nunca
             * definidas como "archivo.extensiondejosemanuel" (al parecer las abre basado en la estructura del archivo y no
             * la extension) se ha decidido limitar el programa a los formatos PGM, JPG (o JPEG), PNG y GIF, para agregar un
             * poco de validaciones, en caso de que se quieran mas formatos solo se necesita agregar mas filtros o en todo caso
             * eliminar todos los filtros */
            
            FC.setAcceptAllFileFilterUsed(false);
            
            FileNameExtensionFilter filtroPGM = new FileNameExtensionFilter("Imagenes PGM","pgm");
            FC.addChoosableFileFilter(filtroPGM);
            
            FileNameExtensionFilter filtroPNG = new FileNameExtensionFilter("Imagenes PNG","png");
            FC.addChoosableFileFilter(filtroPNG);
            
            FileNameExtensionFilter filtroGIF = new FileNameExtensionFilter("Imagenes GIF","gif");
            FC.addChoosableFileFilter(filtroGIF);
            
            FileNameExtensionFilter filtroJPG = new FileNameExtensionFilter("Imagenes JPG","jpg","jpeg");
            FC.addChoosableFileFilter(filtroJPG);
            
            try{ 
                if(FC.showSaveDialog(null)==FC.APPROVE_OPTION) { 
                    ruta1 = FC.getSelectedFile().getAbsolutePath(); 
                    
                    if (FC.getFileFilter() == filtroJPG)
                        ruta1+=".jpg";formato="jpg"; 
                    if (FC.getFileFilter() == filtroPNG)
                        ruta1+=".png";formato="PNG"; 
                    if (FC.getFileFilter() == filtroPGM)
                        ruta1+=".pgm";formato="PGM"; 
                    if (FC.getFileFilter() == filtroGIF)
                        ruta1+=".gif";formato="GIF"; 
                                      
                    ruta = ruta1;
                    
                    ImageInfo origInfo = new ImageInfo(ruta1); //load image info
                    imag.setImageFormat(formato);
                    imag.setFileName(ruta1); //give new location
                    imag.writeImage(origInfo); //save*/
                    
                    if(!bOriginal.isEnabled()) {
                        rutaimg = new ImageInfo(ruta1);
                        bOriginal.setEnabled(true);
                    }
                } 
            }catch (Exception ex) {
                ex.printStackTrace();
            } 
        }
        
        if(e.getSource().equals(bCopy)) {/*
            String ruta1="",formato="";
            JFileChooser FC = new JFileChooser();
            
            FC.setAcceptAllFileFilterUsed(false);
            
            FileNameExtensionFilter filtroPGM = new FileNameExtensionFilter("Imagenes PGM","pgm");
            FC.addChoosableFileFilter(filtroPGM);
            
            FileNameExtensionFilter filtroPNG = new FileNameExtensionFilter("Imagenes PNG","png");
            FC.addChoosableFileFilter(filtroPNG);
            
            FileNameExtensionFilter filtroGIF = new FileNameExtensionFilter("Imagenes GIF","gif");
            FC.addChoosableFileFilter(filtroGIF);
            
            FileNameExtensionFilter filtroJPG = new FileNameExtensionFilter("Imagenes JPG","jpg","jpeg");
            FC.addChoosableFileFilter(filtroJPG);
            
            JOptionPane.showMessageDialog(this, "A continuacion debera\nnombrar el archivo");
            
            try{ 
                if(FC.showSaveDialog(null)==FC.APPROVE_OPTION) { 
                    ruta1 = FC.getSelectedFile().getAbsolutePath(); 
                    
                    if (FC.getFileFilter() == filtroJPG)
                        ruta1+=".jpg";formato="jpg"; 
                    if (FC.getFileFilter() == filtroPNG)
                        ruta1+=".png";formato="PNG"; 
                    if (FC.getFileFilter() == filtroPGM)
                        ruta1+=".pgm";formato="PGM"; 
                    if (FC.getFileFilter() == filtroGIF)
                        ruta1+=".gif";formato="GIF"; 
                    
                    ImageInfo origInfo = new ImageInfo(ruta1); //load image info
                    imag.setImageFormat(formato);
                    imag.setFileName(ruta1); //give new location
                    imag.writeImage(origInfo); //save
                    
                    visor3 v2 = new visor3(ruta1);
                    v2.setLocation(this.getX()+25,this.getY()+25);
                    v2.pack();
                    v2.setVisible(true);
                    v2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                } 
            }catch (Exception ex) {
                ex.printStackTrace();
            }*/
            
            // Ya no guarda un archivo, ahora abre un nuevo visor con la imagen actual
            
            try {
                PixelPacket actual;
                
                dimX = imag.getDimension().width;
                dimY = imag.getDimension().height;
                
                byte[] arr = new byte[(dimX*dimY)*3];
                //visor3(int dX, int dY, byte[] arr)
                
                int contador = 0;
                
                for(int j=0; j<dimY; j++) {
                    for(int i=0; i<dimX; i++) {
                        actual = imag.getOnePixel(i, j);
                        
                        arr[3*contador] = (byte) actual.getRed();
                        arr[3*contador+1] = (byte) actual.getGreen();
                        arr[3*contador+2] = (byte) actual.getBlue();
                        contador++;
                    }
                }
                
                visor3 v2 = new visor3(dimX, dimY, arr);
                v2.setLocation(this.getX()+25,this.getY()+25);
                v2.pack();
                v2.setVisible(true);
                v2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                v2.toolsWindow();
            } catch(MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bCut)) {
            raster();
            int cont1=0, cont2=0; // para simular i y j en 2 for anidados
            
            for(int i=0; i<(dimX*dimY); i++) {
                
                if(cont2 == dimX) {
                    cont1++;
                    cont2 = 0;
                }
                
                if(cont2>=XI&&cont2<=XF+XI&&cont1>=YI&&cont1<=YF+YI) {
                    arreglo[3*i] = (byte)255;
                    arreglo[3*i+1] = (byte)255;
                    arreglo[3*i+2] = (byte)255;
                }
                
                cont2++;
            }
            
            try {
                imag.constituteImage(dimX, dimY, "RGB", arreglo);
                canvas.setImage(imag);
                canvas.repaint();
            } catch(MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bCut2)) {
            try {
                imag = imag.cropImage(new Rectangle(XI,YI,XF,YF));
                canvas.setImage(imag);
            } catch(MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bGrey)) {
            QuantizeInfo info;
            try {
                info = new QuantizeInfo();
                info.setColorspace(2);
                imag.quantizeImage(info);
                canvas.setImage(imag);
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bOriginal)) {
            try {
                imag = new MagickImage(rutaimg);
            } catch (MagickException f) {
                f.printStackTrace();
            }
            canvas.setImage(imag);
        }
        
        if(e.getSource().equals(bRotate)) {
            rotar();
            pide.pack();
        }
        
        if(e.getSource().equals(bPide)) {
            try {
                imag = imag.rotateImage(Double.valueOf(tPide.getText()));
                canvas.setImage(imag);
                pide.dispose();
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bHor)) {
            try {
                imag = imag.flopImage();
                canvas.setImage(imag);
            } catch( MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bVer)) {
            try {
                imag = imag.flipImage();
                canvas.setImage(imag);
            } catch( MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bNeg)) {
            QuantizeInfo info;
            try {
                info = new QuantizeInfo();
                info.setColorspace(2);
                imag.quantizeImage(info);
                imag.negateImage(1);
                canvas.setImage(imag);
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(rIzq)) {
            // rota -90 grados

            try {
                imag = imag.rotateImage(-90);
                canvas.setImage(imag);
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(rDer)) {
            // rota 90 grados

            try {
                imag = imag.rotateImage(90);
                canvas.setImage(imag);
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bCont)) {
            contraste();
            contras.pack();
        }
        
        if(e.getSource().equals(mas)) {
            try {
                imag.contrastImage(false);
                canvas.setImage(imag);
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(menos)) {
            try {
                imag.contrastImage(true);
                canvas.setImage(imag);
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bBright)) {
            brillo();
            brill.pack();
        }
        
        if(e.getSource().equals(mas2)) {
            try {
                imag.modulateImage("110,100,100");  // Brillo, saturacion, hue (todos en porcentajes)
                canvas.setImage(imag);
            } catch(Exception f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(menos2)) {
            try {
                imag.modulateImage("90,100,100");
                canvas.setImage(imag);
            } catch(MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bLogarit)) {
            logaritmica();
            loga.pack();
        }
        
        if(e.getSource().equals(aceptarConst)) {
            raster();
            
            int max=255,constante=0;
            double res=0;
            
            try {
                constante = Integer.valueOf(valorConst.getText());
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Debe ser un numero entero.\nSe asumira valor de 50 (Predeterminado)");
                constante = 50;
            }
            
            for(int i=0; i<(dimX*dimY); i++) {
                res = ((byte)Math.log(1+rojo[i]))*constante;
                if(res>max) res=max;
                arreglo[3*i] = (byte) res;
                
                res = ((byte)Math.log(1+(int)verde[i]))*constante;
                if(res>max) res=max;
                arreglo[3*i+1] = (byte) res;
                
                res = ((byte)Math.log(1+(int)azul[i]))*constante;
                if(res>max) res=max;
                arreglo[3*i+2] = (byte) res;
            }

            try {
                imag.constituteImage(dimX, dimY, "RGB", arreglo);
                canvas.setImage(imag);
            } catch (MagickException f) {
                f.printStackTrace();
            }
            
            loga.dispose();
        }
        
        if(e.getSource().equals(bExp)) {
            exponencial();
            expo.pack();
        }
        
        if(e.getSource().equals(bExpAceptar)) {
            raster();
            
            int max=255;
            //float constante = (float)0.85;
            float constante = Float.valueOf(valC1.getText());
            //float constante2 = (float)0.7;
            float constante2 = Float.valueOf(valC2.getText());
            double res=0;
            
            for(int i=0; i<(dimX*dimY); i++) {
                res = ((byte)Math.pow(rojo[i],constante2))*constante;
                if(res>max) res=max;
                    arreglo[3*i] = (byte) res;

                res = ((byte)Math.pow(verde[i],constante2))*constante;
                if(res>max) res=max;
                    arreglo[3*i+1] = (byte) res;

                res = ((byte)Math.pow(azul[i],constante2))*constante;
                if(res>max) res=max;
                    arreglo[3*i+2] = (byte) res;
            }

            try {
                imag.constituteImage(dimX, dimY, "RGB", arreglo);
                canvas.setImage(imag);
            } catch (MagickException f) {
                f.printStackTrace();
            } 
            
            expo.dispose();
        }
        
        if(e.getSource().equals(bBin)) {
            QuantizeInfo info;
            long suma=0;
            double umbral=0;
            
            try {
                info = new QuantizeInfo();
                info.setColorspace(2);
                imag.quantizeImage(info);
                raster();
                
                for(int i=0; i<(dimX*dimY); i++) {
                    suma+=arreglo[3*i];
                }
                
                umbral = suma / (dimX*dimY);
                
                for(int i=0; i<(dimX*dimY); i++) {
                    if(arreglo[3*i]<(byte)umbral) {
                        arreglo[3*i] = 0;
                    } else {
                        arreglo[3*i] = (byte)255;
                    }
                    
                    if(arreglo[3*i+1]<(byte)umbral) {
                        arreglo[3*i+1] = 0;
                    } else {
                        arreglo[3*i+1] = (byte)255;
                    }
                    
                    if(arreglo[3*i+2]<(byte)umbral) {
                        arreglo[3*i+2] = 0;
                    } else {
                        arreglo[3*i+2] = (byte)255;
                    }
                }
                
                imag.constituteImage(dimX, dimY, "RGB", arreglo);
                canvas.setImage(imag);
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bDetect)) {
            JOptionPane.showMessageDialog(this, "¡Advertencia!\nEsta funcion aun se encuentra limitada a figuras geometricas sencillas");
            
            QuantizeInfo info;
            int suma=0,umbral=0;
            
            try {
                dimX = imag.getDimension().width;
                dimY = imag.getDimension().height;
                MagickImage imag2 = new MagickImage();
                byte[] arr = new byte[(dimX*dimY)*3];
                PixelPacket actual;
                int contador = 0;
                
                for(int j=0; j<dimY; j++) {
                    for(int i=0; i<dimX; i++) {
                        actual = imag.getOnePixel(i, j);
                        
                        arr[3*contador] = (byte) actual.getRed();
                        arr[3*contador+1] = (byte) actual.getGreen();
                        arr[3*contador+2] = (byte) actual.getBlue();
                        contador++;
                    }
                }
                
                info = new QuantizeInfo();
                info.setColorspace(2);
                imag.quantizeImage(info);
                raster();
                
                for(int i=0; i<(dimX*dimY); i++) {
                    suma+=arreglo[3*i];
                }
                
                umbral = suma / (dimX*dimY);
                
                int cont1=0, cont2=0; // para simular i y j en 2 for anidados
                
                matriz = new int[dimY][dimX];
                
                for(int i=0; i<(dimX*dimY); i++) {
                    
                    if(cont2 == dimX) {
                        cont1++;
                        cont2 = 0;
                    }
                    
                    if(arreglo[3*i]<(byte)umbral) {
                        matriz[cont1][cont2] = 0;
                    } else {
                        matriz[cont1][cont2] = 255;
                    }
                    
                    cont2++;
                }
                
                buscar();
                
                imag2.constituteImage(dimX, dimY, "RGB", arr);
                imag = imag2;
                
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bAntiAliasing)) {
            antiAliasing();
        }
        
        if(e.getSource().equals(bMove)) {
            desplazar();
            desp.pack();
        }
        
        if(e.getSource().equals(barriba)) {
            
            try{
                cantidadDesp = Integer.valueOf(cantDesp.getText());
                
                try {
                    imag = imag.rollImage(0, -cantidadDesp);
                    canvas.setImage(imag);
                } catch (MagickException f) {
                    f.printStackTrace();
                }
            } catch (Exception exp){
                JOptionPane.showMessageDialog(this, "Debe ser un numero entero");  
            }
        }
        
        if(e.getSource().equals(babajo)) {
            
            try{
                cantidadDesp = Integer.valueOf(cantDesp.getText());
                
                try {
                    imag = imag.rollImage(0, cantidadDesp);
                    canvas.setImage(imag);
                } catch (MagickException f) {
                    f.printStackTrace();
                }
            } catch (Exception exp){
                JOptionPane.showMessageDialog(this, "Debe ser un numero entero");  
            }
        }
        
        if(e.getSource().equals(bizquierda)) {
            
            try{
                cantidadDesp = Integer.valueOf(cantDesp.getText());
                
                try {
                    imag = imag.rollImage(-cantidadDesp, 0);
                    canvas.setImage(imag);
                } catch (MagickException f) {
                    f.printStackTrace();
                }
            } catch (Exception exp){
                JOptionPane.showMessageDialog(this, "Debe ser un numero entero");  
            }
        }
        
        if(e.getSource().equals(bderecha)) {
            
            try{
                cantidadDesp = Integer.valueOf(cantDesp.getText());
                
                try {
                    imag = imag.rollImage(cantidadDesp, 0);
                    canvas.setImage(imag);
                } catch (MagickException f) {
                    f.printStackTrace();
                }
            } catch (Exception exp){
                JOptionPane.showMessageDialog(this, "Debe ser un numero entero");  
            }
        }
        
        if(e.getSource().equals(bHistogram)) {
            try {
                if(imag.isGrayImage()) {
                    histogramGrey();
                } else {
                    histogramColor();
                }
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bStretch)) {
            try {
                if(imag.isGrayImage()) {
                    stretchGrey();
                } else {
                    stretchColor();
                }
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bShrink)) {
            shrinkVent();
            shrink.setVisible(true);
            shrink.pack();
            shrink.setLocation(this.getX()+100, this.getY()+100);
            shrink.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        
        if(e.getSource().equals(bSlide)) {
            slideVent();
            slide.setVisible(true);
            slide.pack();
            slide.setLocation(this.getX()+100, this.getY()+100);
            slide.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        
        if(e.getSource().equals(bSlideAceptar)) {
            int off;
            
            off = Integer.valueOf(valOffset.getText());
            
            try {
                if(imag.isGrayImage()) {
                    slideGrey(off);
                } else {
                    slideColor(off);
                }
                
                slide.dispose();
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bShrinkAceptar)) {
            int min = Integer.valueOf(valSMin.getText());
            int max = Integer.valueOf(valSMax.getText());
            try {
                if(imag.isGrayImage()) {
                    shrinkGrey(min, max);
                } else {
                    shrinkColor(min, max);
                }
                shrink.dispose();
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bEqualize)) {
            try {
                imag.equalizeImage();
                canvas.setImage(imag);
                if(imag.isGrayImage())
                    histogramGrey();
                else
                    histogramColor();
            } catch (MagickException f) {
                f.printStackTrace();
            }
        }
        
        if(e.getSource().equals(bSepia)) {
            sepia();
        }
        
        if(e.getSource().equals(bPseudo)) {
            pseudocolor();
        }
        
        if(e.getSource().equals(bClose)) {
            cierraTodo();
            
            this.dispose();
        }
        
        this.pack();
    }
    
    @Override
    public void paint(Graphics g) {
        g = canvas.getGraphics();
        
        // Estas variable no tienen uso cuando se implementa el MagickCanvas, pero
        // se dejan por si en algun momento su valor es necesario
        
        arriba = 0;
        abajo = 0;
        derecha = 0;
        izquierda = 0;

        try {
            limX = imag.getDimension().width;
            limY = imag.getDimension().height;
        } catch (MagickException e) {
            e.printStackTrace();
        }
        
        if (presionado) { // se esta arrastrando el raton?
            g.setColor(Color.RED);
            
            if(xi<=xf) {
                XI = xi + izquierda;
                XF = xf - xi;
            }
            else {
                XI = xf + izquierda;
                XF = xi - XI + izquierda;
            }
                    
            if(yi<=yf) {
                YI = yi + arriba;
                YF = yf - yi;
            }
            else {
                YI = yf + arriba;
                YF = yi - YI + arriba;
            }
            
            
            // Intento de que el cuadro de recorte no se agrande al llegar al borde izquierdo o superior
            
            //if(XI==izquierda) {
            //    auxXF = XF;
            //}
            
            //if(YI==arriba) {
            //    auxYF = YF;
            //}
            
            if(XI<izquierda) {
                XI = izquierda;
                //XF = auxXF;
            }
            
            if(YI<arriba) {
                YI = arriba;
                //YF = auxYF;
            }
            
            if(XF>(limX-XI+izquierda)) {
                XF = limX-XI+izquierda-1;
            }
            
            if(YF>(limY-YI+arriba)) {
                YF = limY-YI+arriba-1;
            }
            
            g.drawRect(XI, YI, XF, YF);
            canvas.repaint(XI+1, YI+1, XF-1, YF-1);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Invoked when the mouse button has been clicked (pressed and released) on a component.
        xi = 0;
        yi = 0;
        xf = 0;
        yf = 0;
        bCut.setEnabled(false);
        bCut2.setEnabled(false);
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Invoked when a mouse button has been pressed on a component.
        
        presionado = true;
        xi = e.getX();
        yi = e.getY();
        xf = xi;
        yf = yi;
        canvas.repaint();
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Invoked when a mouse button has been released on a component.
        
        presionado = false;
        bCut.setEnabled(true);
        bCut2.setEnabled(true);
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        // Invoked when the mouse enters a component.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Invoked when the mouse exits a component.
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Invoked when a mouse button is pressed on a component and then dragged.
        // finArrastre = new Point(e.getX(), e.getY());
        
        xf = e.getX();
        yf = e.getY();
        
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
    }
}
