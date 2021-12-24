package dashboardforsensordata;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
/**
 *
 * @author kgl
 */
public class OurButton{
    
    class myButton extends JButton{
        
        private Color hoverBackgroundColor;
        private Color pressedBackgroundColor;
        
        public myButton(){
            this(null);
        }
        public myButton(String text) {
            super(text);
            super.setContentAreaFilled(false);
        }
        @Override
        protected void paintComponent(Graphics g){
            if (getModel().isPressed()) {
                g.setColor(pressedBackgroundColor);
            } else if (getModel().isRollover()) {
                g.setColor(hoverBackgroundColor);
            } else {
                g.setColor(getBackground());
            }
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
        public void setContentAreaFilled(boolean b) {
        }

        public Color getHoverBackgroundColor() {
            return hoverBackgroundColor;
        }

        public void setHoverBackgroundColor(Color hoverBackgroundColor) {
            this.hoverBackgroundColor = hoverBackgroundColor;
        }

        public Color getPressedBackgroundColor() {
            return pressedBackgroundColor;
        }

        public void setPressedBackgroundColor(Color pressedBackgroundColor) {
            this.pressedBackgroundColor = pressedBackgroundColor;
        }
        
    }
    protected void createAndShowGUI() {
        JFrame frame = new JFrame("Test button");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final myButton btnSave = new myButton("............");
        
        btnSave.setForeground(Color.WHITE);
        btnSave.setHorizontalTextPosition(SwingConstants.CENTER);
        btnSave.setBorder(null);
        btnSave.setBackground(new Color(23, 23, 69));
        btnSave.setHoverBackgroundColor(new Color(48, 48, 145));
        btnSave.setPressedBackgroundColor(new Color(23, 23, 69).darker());
        
        frame.add(btnSave);
        frame.setSize(200, 200);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OurButton().createAndShowGUI();
            }
        });

    }
}
