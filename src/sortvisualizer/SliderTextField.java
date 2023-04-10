package sortvisualizer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.KeyListener;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.text.PlainDocument;

public class SliderTextField {
    
    private JTextField textField;
    private JSlider slider;
    private int conversionRatio;

    public SliderTextField(float min, float sliderMax, float textFieldMax, float defaultValue, boolean floatSlider) {
        if (min > 0) {
            conversionRatio = floatSlider ? (int) (sliderMax / min) : 1;
        }
        else {
            conversionRatio = floatSlider ? (int) sliderMax : 1;
        }
        slider = new JSlider(JSlider.HORIZONTAL, (int) (min * conversionRatio), (int) (sliderMax * conversionRatio), (int) (defaultValue * conversionRatio));
        textField = new JTextField("" + slider.getValue() / conversionRatio);
        slider.setBounds(0, 0, 100, 20);
        slider.setBackground(new Color(0, 0, 0, 0));
        
        PlainDocument doc = (PlainDocument) textField.getDocument();
        doc.setDocumentFilter(new NumberFilter(textFieldMax, floatSlider));
        textField.setBounds(0, 0, 50, 20);
    }

    public void setLocation(int x, int y) {
        slider.setLocation(x, y);
        textField.setLocation(x + 105, y);
    }

    public int getSliderValue() {
        return slider.getValue();
    }

    public void setSliderValue(float value) {
        slider.setValue((int) (value * conversionRatio));
    }

    public int getSliderMaximum() {
        return slider.getMaximum();
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String str) {
        textField.setText(str);
    }

    public boolean textFieldHasFocus() {
        return textField.hasFocus();
    }

    public boolean textFieldContains(Point p) {
        return textField.getBounds().contains(p);
    }

    public boolean sliderContains(Point p) {
        return slider.getBounds().contains(p);
    }

    public boolean sliderHasFocus() {
        return slider.hasFocus();
    }

    public void addSliderChangeListener(ChangeListener l) {
        slider.addChangeListener(l);
    }

    public void addTextFieldKeyListener(KeyListener l) {
        textField.addKeyListener(l);
    }

    public int getConversionRatio() {
        return conversionRatio;
    }

    public void addTo(Container container) {
        container.add(textField);
        container.add(slider);
    }
}
