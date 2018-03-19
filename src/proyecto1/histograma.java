package proyecto1;

import javax.swing.JFrame;

import magick.MagickException;
import magick.MagickImage;

import magick.util.MagickCanvas;

// Muestra el histograma de la imagen

public class histograma extends JFrame {
    
    public histograma(String nomArch, int dX, int dY, byte[] arr) {
        super("Histograma: "+nomArch);
        
        MagickCanvas canvas = new MagickCanvas();
        MagickImage imag = new MagickImage();

        try {
            imag.constituteImage(dX, dY, "RGB", arr);
            canvas.setImage(imag);
        } catch (MagickException f) {
            f.printStackTrace();
        }
        
        this.add(canvas);
    }
}
