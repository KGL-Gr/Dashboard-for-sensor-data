/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dashboardforsensordata;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Random;

/**
 *
 * @author panos
 */
public class background extends JPanel implements ActionListener{
        
    final int PANEL_WIDTH = 1600;
    final int PANEL_HEIGHT = 900;
    private BufferedImage image;
    Timer timer;
    int x[] = new int[8];
    int i=0;
    int y[] = new int[8];
    int yMove[]= new int[8];
    Random rand = new Random();
    public background(){
        try { 
            image = ImageIO.read(this.getClass().getResource("bar.png"));//need to put the correct path here
            }
        catch (IOException ex) {
            System.out.println("Image not found");
                }
        x[0]=0;
        y[0]=rand.nextInt(800);
        yMove[0]=rand.nextInt(2+2)-2;
        for(i=1;i<8;i++)
        {
            x[i]=x[i-1]+200;
            y[i]=rand.nextInt(800);
            yMove[i]=rand.nextInt(2+2)-2;
            if(yMove[i]==0)
            {
                yMove[i]=2;
            }
        }
        timer = new Timer(10, this);
        timer.start();
    }
    //called every time to paint the components
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        Color color1 = Color.decode("#4f3cde");
        Color color2 = Color.decode("#3285a8");
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
        for(i=0;i<8;i++)
        {
            g.drawImage(image, x[i], y[i], null);
        }
    }
    
        @Override
	public void actionPerformed(ActionEvent e) {
            for(i=0;i<8;i++)
            {
                if(y[i]>=PANEL_HEIGHT || y[i]<0) {
                yMove[i] = yMove[i] * -1;
            }
               y[i] = y[i] + yMove[i]; 
            }
            repaint();
	}
}
