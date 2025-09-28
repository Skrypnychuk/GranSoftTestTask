import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * Main application class for the Single Page Swing Application.
 * <p>
 * The application provides two screens:
 * <ul>
 *     <li>Intro screen: allows the user to input the number of random values</li>
 *     <li>Sort screen: displays random numbers as buttons and visualizes a QuickSort algorithm</li>
 * </ul>
 * <p>
 * The Sort screen supports:
 * <ul>
 *     <li>Generating numbers with at least one number ≤ 30</li>
 *     <li>Clicking on a number ≤ 30 to regenerate the list</li>
 *     <li>Sorting numbers in ascending/descending order with step-by-step visualization</li>
 *     <li>Resetting to the Intro screen</li>
 * </ul>
 */
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

    /**
     * Main constructor that sets up the application window and initializes panels.
     */
    public Main() {
        configureFrame();
        initIntroPanel();
        initSortPanel();
        setContentPane(introPanel);
        setVisible(true);
    }

    /**
     * Configures the main JFrame settings including title, size, default close operation, and centering.
     */
    private void configureFrame() {
        setTitle("Swing Sort App");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    /**
     * Initializes the Intro panel where the user inputs the number of random values.
     */
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

    /**
     * Creates the "Enter" button on the Intro panel.
     *
     * @return JButton configured with action listener
     */
    private JButton createEnterButton() {
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> handleEnterClick());
        return enterButton;
    }

    /**
     * Handles the action of clicking the Enter button or pressing Enter key.
     * Parses input, generates random numbers, and switches to the Sort panel.
     */
    private void handleEnterClick() {
        try {
            n = parseNumberInput();
            generateNumbers(n);
            createNumberButtons();
            refreshNumbers();
            switchToSortPanel();
        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid number.");
        } catch (IllegalArgumentException ex) {
            showMessage(ex.getMessage());
        }
    }

    /**
     * Parses the user input from the Intro panel and validates it.
     *
     * @return integer value of user input
     *
     * @throws NumberFormatException if input is not a number
     * @throws IllegalArgumentException if number is invalid (≤0 or > MAX_NUMBER)
     */
    private int parseNumberInput() {
        String text = numberInput.getText().trim();
        int value;

        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }

        if (value <= 0) {
            throw new IllegalArgumentException("Number must be positive.");
        }

        if (value > MAX_NUMBER) {
            throw new IllegalArgumentException("Number must be less than or equal to " + MAX_NUMBER + ".");
        }

        return value;
    }

    /**
     * Switches the content pane to the Sort panel.
     */
    private void switchToSortPanel() {
        setContentPane(sortPanel);
        revalidate();
        repaint();
    }

    /**
     * Initializes the Sort panel with number buttons and action buttons (Sort, Reset).
     */
    private void initSortPanel() {
        sortPanel = new JPanel(new BorderLayout());
        numbersContainer = new JPanel();

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        sortButton = new JButton("Sort");
        resetButton = new JButton("Reset");

        sortButton.addActionListener(e -> startSorting());
        resetButton.addActionListener(e -> switchToIntroPanel());

        buttonsPanel.add(sortButton);
        buttonsPanel.add(resetButton);

        sortPanel.add(new JScrollPane(numbersContainer), BorderLayout.CENTER);
        sortPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    /**
     * Switches the content pane back to the Intro panel.
     */
    private void switchToIntroPanel() {
        setContentPane(introPanel);
        revalidate();
        repaint();
    }

    /**
     * Generates an array of random numbers with at least one number ≤ 30.
     *
     * @param count the total number of random values to generate
     */
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

    /**
     * Creates JButton instances for each number in the array and assigns click listeners.
     */
    private void createNumberButtons() {
        numberButtons = new JButton[n];
        for (int i = 0; i < n; i++) {
            int idx = i;
            numberButtons[i] = new JButton(String.valueOf(numbers[i]));
            numberButtons[i].addActionListener(e -> handleNumberClick(idx));
        }
    }

    /**
     * Handles clicking a number button:
     * - If value ≤ 30, regenerates the number array based on that value.
     * - Otherwise, shows a message dialog.
     *
     * @param index index of the clicked number button
     */
    private void handleNumberClick(int index) {
        int value = numbers[index];
        if (value <= SMALL_NUMBER_LIMIT) {
            n = value;
            generateNumbers(n);
            createNumberButtons();
            refreshNumbers();
        } else {
            showMessage("Please select a value smaller or equal to " + SMALL_NUMBER_LIMIT + ".");
        }
    }

    /**
     * Refreshes the layout of number buttons in a column-wise fashion.
     * Adds empty labels to fill grid cells if necessary.
     */
    private void refreshNumbers() {
        numbersContainer.removeAll();

        int cols = (int) Math.ceil((double) n / MAX_PER_COLUMN);
        int rows = Math.min(n, MAX_PER_COLUMN);
        numbersContainer.setLayout(new GridLayout(rows, cols, 5, 5));

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = col * MAX_PER_COLUMN + row;
                if (index < numberButtons.length) {
                    numbersContainer.add(numberButtons[index]);
                } else {
                    numbersContainer.add(new JLabel());
                }
            }
        }

        revalidate();
        repaint();
    }

    /**
     * Starts the QuickSort visualization by toggling ascending/descending order.
     * Disables action buttons during sorting.
     */
    private void startSorting() {
        ascending = !ascending;
        sortButton.setText(ascending ? "Sort ↑" : "Sort ↓");
        sortButton.setEnabled(false);
        resetButton.setEnabled(false);

        new SortingTimer().start();
    }

    /**
     * Swaps two elements in an array.
     *
     * @param arr array where elements are swapped
     * @param i first index
     * @param j second index
     */
    private void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    /**
     * Creates GridBagConstraints with standard insets.
     *
     * @return configured GridBagConstraints
     */
    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        return gbc;
    }

    /**
     * Adds a component to a JPanel with specific GridBagConstraints.
     *
     * @param panel panel to add component
     * @param comp component to add
     * @param gbc GridBagConstraints instance
     * @param x gridx position
     * @param y gridy position
     * @param gridWidth width of the component in grid cells
     */
    private void addToPanel(JPanel panel, Component comp, GridBagConstraints gbc,
                            int x, int y, int gridWidth) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = gridWidth;
        panel.add(comp, gbc);
    }

    /**
     * Displays a message dialog with a specified message.
     *
     * @param message text to display
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Inner class responsible for animating QuickSort using a Swing Timer.
     */
    private class SortingTimer {

        private final Timer timer;

        private final Deque<int[]> stack = new ArrayDeque<>();

        private final Deque<int[]> swapQueue = new ArrayDeque<>();

        private boolean isSwapping = false;

        /**
         * Constructor that initializes the stack with the full array range and sets timer delay.
         */
        SortingTimer() {
            stack.push(new int[]{0, numbers.length - 1});
            timer = new Timer(300, this::step);
        }

        /**
         * Starts the sorting timer.
         */
        void start() {
            timer.start();
        }

        /**
         * Performs a single sorting step or swap step for visualization.
         *
         * @param e ActionEvent triggered by the Timer
         */
        private void step(ActionEvent e) {
            if (isSwapping) {
                int[] pair = swapQueue.poll();
                if (pair != null) {
                    int i = pair[0];
                    int j = pair[1];
                    numberButtons[i].setBackground(null);
                    numberButtons[j].setBackground(null);
                }
                isSwapping = false;
                return;
            }

            if (!swapQueue.isEmpty()) {
                int[] pair = swapQueue.peek();
                int i = pair[0];
                int j = pair[1];

                numberButtons[i].setText(String.valueOf(numbers[i]));
                numberButtons[j].setText(String.valueOf(numbers[j]));
                numberButtons[i].setBackground(Color.YELLOW);
                numberButtons[j].setBackground(Color.YELLOW);

                isSwapping = true;
                return;
            }

            if (stack.isEmpty()) {
                timer.stop();
                sortButton.setEnabled(true);
                resetButton.setEnabled(true);
                return;
            }

            int[] range = stack.pop();
            int l = range[0];
            int h = range[1];

            if (l < h) {
                int p = partitionWithQueue(numbers, l, h);
                stack.push(new int[]{l, p - 1});
                stack.push(new int[]{p + 1, h});
            }
        }

        /**
         * Partitions the array using Lomuto partition scheme and queues swaps for animation.
         *
         * @param arr array to partition
         * @param low starting index
         * @param high ending index
         *
         * @return pivot index after partition
         */
        private int partitionWithQueue(int[] arr, int low, int high) {
            int pivot = arr[high];
            int i = low - 1;

            for (int j = low; j < high; j++) {
                if (ascending && arr[j] <= pivot || !ascending && arr[j] >= pivot) {
                    i++;
                    if (i != j) {
                        swap(arr, i, j);
                        swapQueue.add(new int[]{i, j});
                    }
                }
            }

            if (i + 1 != high) {
                swap(arr, i + 1, high);
                swapQueue.add(new int[]{i + 1, high});
            }

            return i + 1;
        }
    }

    /**
     * Entry point of the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
