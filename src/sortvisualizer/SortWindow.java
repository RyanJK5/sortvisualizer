package sortvisualizer;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SortWindow extends JPanel {
    
    private static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 500;
    private static final Font FONT = new Font(null, Font.PLAIN, 18);

    private SliderTextField listSizeControl;
    private SliderTextField msControl;
    private JComboBox<SortingType> sortingTypeDropdown;

    private int maxNum;
    private float miliDelay;

    private int[] demoArr;
    private final Timer timer;

    private Thread greenHighlightThread;
    private int greenHighlightIndex;

    private Sorter sorter;
    private Thread sortingThread;

    private Synthesizer synth;

    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(() -> {
            SortWindow window = new SortWindow();
            window.start();
        });
    }

    private SortWindow() {
        super();
        maxNum = 1000;
        miliDelay = 1;

        initSwingComponents();

        setBackground(Color.BLACK);
		setLayout(null);

        demoArr = new int[1000];
        timer = new Timer(20, e -> {
            repaint();
            EventQueue.invokeLater(() -> {
                if (sorter.highlightLow >= 0) {
                    playSound(demoArr[sorter.highlightLow]);
                }
                else if (greenHighlightIndex >= 0) {
                    playSound(greenHighlightIndex);
                }
            });
        });
        timer.start();
        
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
        } catch (MidiUnavailableException e1) {
            e1.printStackTrace();
        }

        greenHighlightThread = new Thread(() -> {
            while (greenHighlightIndex >= 0 && greenHighlightIndex < demoArr.length) {
                greenHighlightIndex++;
                playSound(greenHighlightIndex);
                sleep(miliDelay);
            }
            greenHighlightIndex = -1;
        });
        greenHighlightIndex = -1;
    }

    private void initSwingComponents() {
        JFrame frame = new JFrame();
        frame.setBounds(0, 0, 1016, SCREEN_HEIGHT);
		frame.setContentPane(this);
		frame.setFocusTraversalKeysEnabled(false);
		frame.setFocusCycleRoot(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listSizeControl.textFieldHasFocus() && !listSizeControl.textFieldContains(e.getPoint())) {
                    frame.requestFocus();
                }
                else if (listSizeControl.sliderHasFocus() && !listSizeControl.sliderContains(e.getPoint())) {
                    frame.requestFocus();
                }
            }
        });
        
        listSizeControl = new SliderTextField(10, 10000, 9999999, 1000, false);
        listSizeControl.setLocation(205, 10);
        listSizeControl.addTo(this);
        listSizeControl.addSliderChangeListener(e -> restartSort(listSizeControl.getSliderValue(), sorter.sortingType));
        listSizeControl.addTextFieldKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        int value = Integer.parseInt(listSizeControl.getText());
                        listSizeControl.setSliderValue(value);
                        if (value == listSizeControl.getSliderValue() || value > listSizeControl.getSliderMaximum()) {
                            restartSort(value, sorter.sortingType);
                        }
                        frame.requestFocus();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        listSizeControl.setText("");
                        frame.requestFocus();
                    }
                } catch (NumberFormatException e1) {
                    e1.printStackTrace();
                }
            }
        });

        msControl = new SliderTextField(0, 10f, 10f, 1f, true);
        msControl.setLocation(405, 10);
        msControl.addTo(this);
        msControl.addSliderChangeListener(e -> {
            miliDelay = (float) msControl.getSliderValue() / msControl.getConversionRatio();
            String str = Float.toString(miliDelay);
            msControl.setText(str.substring(0, str.indexOf('.') + 2));
        });
        msControl.addTextFieldKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        float value = Float.parseFloat(msControl.getText());
                        msControl.setSliderValue(value);
                        frame.requestFocus();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        msControl.setText("");
                        frame.requestFocus();
                    }
                } catch (NumberFormatException e1) {
                    e1.printStackTrace();
                }
            }
        });
        
        sortingTypeDropdown = new JComboBox<>(SortingType.values());
        sortingTypeDropdown.setSelectedIndex(SortingType.QUICK_SORT.ordinal());
        sortingTypeDropdown.setBounds(5, 10, 200, 20);
        sortingTypeDropdown.addItemListener(e -> {
            restartSort(maxNum, (SortingType) e.getItem());
        });
        add(sortingTypeDropdown);
    }

    private void restartSort(int numElements, SortingType type) {
        sorter.kill();
        try {
            sortingThread.join();
        } catch (InterruptedException e1) { }
        maxNum = numElements;
        greenHighlightIndex = -1;
        fillArr();
        greenHighlightThread = new Thread(() -> {
            while (greenHighlightIndex >= 0 && greenHighlightIndex < demoArr.length) {
                greenHighlightIndex++;
                sleep(miliDelay);
            }
            greenHighlightIndex = -1;
        });
        sorter = new Sorter(demoArr, type, () -> sleep(miliDelay), () -> {
            greenHighlightIndex = 0;
            greenHighlightThread.start();
        }, this::playSound);
        sortingThread = new Thread(sorter);
        sortingThread.start();
        listSizeControl.setText("" + numElements);
    }

    private void start() {
        fillArr();
        sorter = new Sorter(demoArr, SortingType.QUICK_SORT, () -> sleep(miliDelay), () -> {
            greenHighlightIndex = 0;
            greenHighlightThread.start();
        }, this::playSound);
        sortingThread = new Thread(sorter);
        sortingThread.start();
    }

    private void fillArr() {
        demoArr = new int[maxNum];
        for (int i = 1; i <= maxNum; i++) {
            demoArr[i - 1] = i;
        }
        shuffle();
    }

    private void shuffle() {
        Random rand = ThreadLocalRandom.current();
        for (int i = demoArr.length - 1; i > 0; i--) {
        int index = rand.nextInt(i + 1);
        int temp = demoArr[index];
        demoArr[index] = demoArr[i];
        demoArr[i] = temp;
        }
    }

    private void playSound(int num) {
        if (num < 0) {
            return;
        }
        MidiChannel channel = synth.getChannels()[5];
        final int note = (int) (100f / maxNum * num);
        channel.noteOn(note, 80);
        channel.noteOn(note, 0);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(FONT);
        final float rectWidth = (float) getWidth() / maxNum;
        for (float i = 0; i < maxNum; i += (float) maxNum / getWidth()) {
            final int rectHeight = (int) ((float) getHeight() / demoArr.length * demoArr[(int) i]);
            if (i >= sorter.highlightLow && i <= sorter.highlightHigh) {
                g.setColor(Color.RED);
            }
            else if (i <= greenHighlightIndex) {
                g.setColor(Color.GREEN);
            }
            g.fillRect((int) (rectWidth * i), getHeight() - rectHeight, 1, rectHeight);
            g.setColor(Color.WHITE);
        }
        g.drawString(sorter.comparisons + " comparisons", 0, 50);
        g.drawString(sorter.arrayAccesses + " array accesses", 0, 70);
        g.drawString("array size", 212, 45);
        g.drawString("ms Delay", 412, 45);
    }

    private void sleep(float milis) {
        try {
            Thread.sleep((int) milis, (int) (milis * 1000) % 1000);
        } catch (InterruptedException e) { }
    }
}
