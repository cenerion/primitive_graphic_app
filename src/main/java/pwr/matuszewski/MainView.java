package pwr.matuszewski;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainView implements ChangeListener, ActionListener {
    private JPanel control_panel;
    private JPanel resources_pane;
    private JPanel content_pane;
    private JPanel main_panel;
    private JButton button_rotate_small_inc;
    private JPanel color_control;
    private JSlider slider_red;
    private JSlider slider_green;
    private JSlider slider_blue;
    private JPanel color_indicator;
    private JSpinner spinner_red;
    private JSpinner spinner_green;
    private JSpinner spinner_blue;
    private CanvasPanel canva_panel;
    private ListaElementow images_list;
    private ListaElementow shapes_list;
    private JButton button_rotate_small_dec;
    private JButton button_move_;
    private JButton a1pxLeftButton;
    private JButton layerDownButton;
    private JButton a1pxUpButton;
    private JButton a15degButton;
    private JButton a1pxDownButton;
    private JButton a15degButton1;
    private JButton layerUpButton;


    public static void main(String[] args) {
        System.out.println("Hello, World!");

        JFrame frame = new JFrame("App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new MainView().main_panel);
        frame.pack();
        frame.setVisible(true);


    }

    MainView() {
        var init_color = new Color(128,128,128);

        spinner_red.setModel(new SpinnerNumberModel(init_color.getRed(), 0, 255, 1));
        spinner_green.setModel(new SpinnerNumberModel(init_color.getGreen(), 0, 255, 1));
        spinner_blue.setModel(new SpinnerNumberModel(init_color.getBlue(), 0, 255, 1));

        slider_red.setValue(init_color.getRed());
        slider_red.setValue(init_color.getGreen());
        slider_red.setValue(init_color.getBlue());

        color_indicator.setBackground(init_color);

        spinner_red.addChangeListener(this);
        spinner_green.addChangeListener(this);
        spinner_blue.addChangeListener(this);

        slider_red.addChangeListener(this);
        slider_green.addChangeListener(this);
        slider_blue.addChangeListener(this);

        layerUpButton.addActionListener(this);
        layerDownButton.addActionListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        var source = e.getSource();

        if (source == slider_red || source == slider_green || source == slider_blue) {

            var red = slider_red.getValue();
            var green = slider_green.getValue();
            var blue = slider_blue.getValue();
            spinner_red.setValue(red);
            spinner_green.setValue(green);
            spinner_blue.setValue(blue);
            color_indicator.setBackground(new Color(red, green, blue));
        }
        else if (source == spinner_red || source == spinner_green || source == spinner_blue) {
            var red = (Number) spinner_red.getValue();
            var green = (Number) spinner_green.getValue();
            var blue = (Number) spinner_blue.getValue();
            slider_red.setValue(red.intValue());
            slider_green.setValue(green.intValue());
            slider_blue.setValue(blue.intValue());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == layerDownButton) {canva_panel.selectedLayerDown();}
        else if(e.getSource() == layerUpButton) {canva_panel.selectedLayerUp();}
    }
}
