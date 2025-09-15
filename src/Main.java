import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class Main extends JFrame {

    private static final int MAX_NUMBER = 1000;

    private static final int SMALL_NUMBER_LIMIT = 30;

    private static final int MAX_PER_COLUMN = 10;

    private JPanel introPanel;

    private JPanel sortPanel;

    private JTextField numberInput;

    private JPanel numbersContainer;

    private JButton[] numberButtons;

    private JButton sortButton;

    private JButton resetButton;

    private boolean ascending = true;

    private int[] numbers;

    private int n;

    public Main() {
        configureFrame();
        initIntroPanel();
        initSortPanel();
        setContentPane(introPanel);
        setVisible(true);
    }

    private void configureFrame() {
        setTitle("Swing Sort App");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initIntroPanel() {
        introPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGbc();

        JLabel label = new JLabel("Enter number of random values:");
        numberInput = new JTextField(10);
        JButton enterButton = createEnterButton();

        numberInput.addActionListener(e -> handleEnterClick());

        addToPanel(introPanel, label, gbc, 0, 0, 1);
        addToPanel(introPanel, numberInput, gbc, 1, 0, 1);
        addToPanel(introPanel, enterButton, gbc, 0, 1, 2);
    }

    private JButton createEnterButton() {
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> handleEnterClick());
        return enterButton;
    }

    private void handleEnterClick() {
        try {
            n = parseNumberInput();
            generateNumbers(n);
            createNumberButtons();
            refreshNumbers();
            switchToSortPanel();
        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid positive integer.");
        }
    }

    private int parseNumberInput() {
        int value = Integer.parseInt(numberInput.getText());
        if (value <= 0) {
            throw new NumberFormatException();
        }
        return value;
    }

    private void switchToSortPanel() {
        setContentPane(sortPanel);
        revalidate();
        repaint();
    }

    private void initSortPanel() {
        sortPanel = new JPanel(new BorderLayout());
        numbersContainer = new JPanel(new GridLayout(0, MAX_PER_COLUMN, 5, 5));

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        sortButton = new JButton("Sort ↑");
        resetButton = new JButton("Reset");

        sortButton.addActionListener(e -> startSorting());
        resetButton.addActionListener(e -> switchToIntroPanel());

        buttonsPanel.add(sortButton);
        buttonsPanel.add(resetButton);

        sortPanel.add(new JScrollPane(numbersContainer), BorderLayout.CENTER);
        sortPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void switchToIntroPanel() {
        setContentPane(introPanel);
        revalidate();
        repaint();
    }

    private void generateNumbers(int count) {
        Random random = new Random();
        numbers = new int[count];
        int smallIndex = random.nextInt(count);

        for (int i = 0; i < count; i++) {
            if (i == smallIndex) {
                numbers[i] = random.nextInt(SMALL_NUMBER_LIMIT) + 1;
            } else {
                numbers[i] = random.nextInt(MAX_NUMBER) + 1;
            }
        }
    }

    private void createNumberButtons() {
        numberButtons = new JButton[n];
        for (int i = 0; i < n; i++) {
            int idx = i;
            numberButtons[i] = new JButton(String.valueOf(numbers[i]));
            numberButtons[i].addActionListener(e -> handleNumberClick(idx));
        }
    }

    private void handleNumberClick(int index) {
        if (numbers[index] <= SMALL_NUMBER_LIMIT) {
            generateNumbers(numbers.length);
            updateNumberButtons();
        } else {
            showMessage("Please select a value smaller or equal to " + SMALL_NUMBER_LIMIT + ".");
        }
    }

    private void refreshNumbers() {
        numbersContainer.removeAll();
        for (JButton btn : numberButtons) {
            numbersContainer.add(btn);
        }
        revalidate();
        repaint();
    }

    private void updateNumberButtons() {
        for (int i = 0; i < numbers.length; i++) {
            numberButtons[i].setText(String.valueOf(numbers[i]));
        }
        refreshNumbers();
    }

    private void startSorting() {
        ascending = !ascending;
        sortButton.setText(ascending ? "Sort ↑" : "Sort ↓");
        sortButton.setEnabled(false);
        resetButton.setEnabled(false);

        new SortingTimer().start();
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if ((ascending && arr[j] <= pivot) || (!ascending && arr[j] >= pivot)) {
                swap(arr, ++i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        return gbc;
    }

    private void addToPanel(JPanel panel, Component comp, GridBagConstraints gbc,
                            int x, int y, int gridWidth) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = gridWidth;
        panel.add(comp, gbc);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private class SortingTimer {

        private Timer timer;

        private Deque<int[]> stack = new ArrayDeque<>();

        SortingTimer() {
            stack.push(new int[]{0, numbers.length - 1});
            timer = new Timer(200, this::step);
        }

        void start() {
            timer.start();
        }

        private void step(ActionEvent e) {
            if (stack.isEmpty()) {
                timer.stop();
                sortButton.setEnabled(true);
                resetButton.setEnabled(true);
                return;
            }

            int[] range = stack.pop();
            int l = range[0], h = range[1];
            if (l < h) {
                int p = partition(numbers, l, h);
                stack.push(new int[]{l, p - 1});
                stack.push(new int[]{p + 1, h});
            }
            updateNumberButtons();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}