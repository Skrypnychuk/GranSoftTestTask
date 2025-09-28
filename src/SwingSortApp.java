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
public class SwingSortApp extends JFrame {

    private static final int MAX_RANDOM_VALUE = 1000;

    private static final int SMALL_NUMBER_THRESHOLD = 30;

    private static final int MAX_BUTTONS_PER_COLUMN = 10;

    private JPanel introPanel;

    private JPanel sortPanel;

    private JTextField inputField;

    private JPanel numbersGridPanel;

    private JButton[] numberButtons;

    private JButton sortToggleButton;

    private JButton resetButton;

    private boolean isSortAscending = true;

    private int[] numbersArray;

    private int totalNumbers;

    /**
     * SwingSortApp constructor that sets up the application window and initializes panels.
     */
    public SwingSortApp() {
        configureWindow();
        initializeIntroPanel();
        initializeSortPanel();
        setContentPane(introPanel);
        setVisible(true);
    }

    /**
     * Configures the main JFrame settings including title, size, default close operation, and centering.
     */
    private void configureWindow() {
        setTitle("Swing Sort App");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    /**
     * Initializes the Intro panel where the user inputs the number of random values.
     */
    private void initializeIntroPanel() {
        introPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();

        JLabel inputLabel = new JLabel("Enter number of random values:");
        inputField = new JTextField(10);
        JButton enterButton = createEnterButton();

        inputField.addActionListener(e -> handleEnterAction());

        addToPanel(introPanel, inputLabel, gbc, 0, 0, 1);
        addToPanel(introPanel, inputField, gbc, 1, 0, 1);
        addToPanel(introPanel, enterButton, gbc, 0, 1, 2);
    }

    /**
     * Creates the "Enter" button on the Intro panel.
     *
     * @return JButton configured with action listener
     */
    private JButton createEnterButton() {
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> handleEnterAction());
        return enterButton;
    }

    /**
     * Handles the action of clicking the Enter button or pressing Enter key.
     * Parses input, generates random numbers, and switches to the Sort panel.
     */
    private void handleEnterAction() {
        try {
            totalNumbers = parseInputValue();
            generateRandomNumbers(totalNumbers);
            createNumberButtons();
            layoutNumberButtons();
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
     * @throws IllegalArgumentException if number is invalid (≤0 or > MAX_RANDOM_VALUE)
     */
    private int parseInputValue() {
        String text = inputField.getText().trim();
        int value;

        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }

        if (value <= 0) {
            throw new IllegalArgumentException("Number must be positive.");
        }

        if (value > MAX_RANDOM_VALUE) {
            throw new IllegalArgumentException("Number must be less than or equal to " + MAX_RANDOM_VALUE + ".");
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
    private void initializeSortPanel() {
        sortPanel = new JPanel(new BorderLayout());
        numbersGridPanel = new JPanel();

        JPanel actionButtonsPanel = new JPanel(new FlowLayout());
        sortToggleButton = new JButton("Sort");
        resetButton = new JButton("Reset");

        sortToggleButton.addActionListener(e -> startSorting());
        resetButton.addActionListener(e -> switchToIntroPanel());

        actionButtonsPanel.add(sortToggleButton);
        actionButtonsPanel.add(resetButton);

        sortPanel.add(new JScrollPane(numbersGridPanel), BorderLayout.CENTER);
        sortPanel.add(actionButtonsPanel, BorderLayout.SOUTH);
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
     * Generates an array of random numbers with at least one number ≤ SMALL_NUMBER_THRESHOLD.
     *
     * @param count the total number of random values to generate
     */
    private void generateRandomNumbers(int count) {
        Random random = new Random();
        numbersArray = new int[count];
        int smallNumberIndex = random.nextInt(count);

        for (int i = 0; i < count; i++) {
            if (i == smallNumberIndex) {
                numbersArray[i] = random.nextInt(SMALL_NUMBER_THRESHOLD) + 1;
            } else {
                numbersArray[i] = random.nextInt(MAX_RANDOM_VALUE) + 1;
            }
        }
    }

    /**
     * Creates JButton instances for each number in the array and assigns click listeners.
     */
    private void createNumberButtons() {
        numberButtons = new JButton[totalNumbers];
        for (int i = 0; i < totalNumbers; i++) {
            int index = i;
            numberButtons[i] = new JButton(String.valueOf(numbersArray[i]));
            numberButtons[i].addActionListener(e -> handleNumberButtonClick(index));
        }
    }

    /**
     * Handles clicking a number button:
     * - If value ≤ SMALL_NUMBER_THRESHOLD, regenerates the number array based on that value.
     * - Otherwise, shows a message dialog.
     *
     * @param index index of the clicked number button
     */
    private void handleNumberButtonClick(int index) {
        int value = numbersArray[index];
        if (value <= SMALL_NUMBER_THRESHOLD) {
            totalNumbers = value;
            generateRandomNumbers(totalNumbers);
            createNumberButtons();
            layoutNumberButtons();
        } else {
            showMessage("Please select a value smaller or equal to " + SMALL_NUMBER_THRESHOLD + ".");
        }
    }

    /**
     * Refreshes the layout of number buttons in a column-wise fashion.
     * Adds empty labels to fill grid cells if necessary.
     */
    private void layoutNumberButtons() {
        numbersGridPanel.removeAll();

        int columns = (int) Math.ceil((double) totalNumbers / MAX_BUTTONS_PER_COLUMN);
        int rows = Math.min(totalNumbers, MAX_BUTTONS_PER_COLUMN);
        numbersGridPanel.setLayout(new GridLayout(rows, columns, 5, 5));

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int index = col * MAX_BUTTONS_PER_COLUMN + row;
                if (index < numberButtons.length) {
                    numbersGridPanel.add(numberButtons[index]);
                } else {
                    numbersGridPanel.add(new JLabel());
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
        isSortAscending = !isSortAscending;
        sortToggleButton.setText(isSortAscending ? "Sort ↑" : "Sort ↓");
        sortToggleButton.setEnabled(false);
        resetButton.setEnabled(false);

        new QuickSortAnimator().start();
    }

    /**
     * Swaps two elements in an array.
     *
     * @param array array where elements are swapped
     * @param i first index
     * @param j second index
     */
    private void swapArrayElements(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Creates GridBagConstraints with standard insets.
     *
     * @return configured GridBagConstraints
     */
    private GridBagConstraints createGridBagConstraints() {
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
    private class QuickSortAnimator {

        private final Timer timer;

        private final Deque<int[]> rangeStack = new ArrayDeque<>();

        private final Deque<int[]> swapQueue = new ArrayDeque<>();

        private boolean isSwapping = false;

        /**
         * Constructor that initializes the stack with the full array range and sets timer delay.
         */
        QuickSortAnimator() {
            rangeStack.push(new int[]{0, numbersArray.length - 1});
            timer = new Timer(300, this::performSortingStep);
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
        private void performSortingStep(ActionEvent e) {
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

                numberButtons[i].setText(String.valueOf(numbersArray[i]));
                numberButtons[j].setText(String.valueOf(numbersArray[j]));
                numberButtons[i].setBackground(Color.YELLOW);
                numberButtons[j].setBackground(Color.YELLOW);

                isSwapping = true;
                return;
            }

            if (rangeStack.isEmpty()) {
                timer.stop();
                sortToggleButton.setEnabled(true);
                resetButton.setEnabled(true);
                return;
            }

            int[] range = rangeStack.pop();
            int low = range[0];
            int high = range[1];

            if (low < high) {
                int pivotIndex = partitionAndQueueSwaps(numbersArray, low, high);
                rangeStack.push(new int[]{low, pivotIndex - 1});
                rangeStack.push(new int[]{pivotIndex + 1, high});
            }
        }

        /**
         * Partitions the array using Lomuto partition scheme and queues swaps for animation.
         *
         * @param array array to partition
         * @param low starting index
         * @param high ending index
         *
         * @return pivot index after partition
         */
        private int partitionAndQueueSwaps(int[] array, int low, int high) {
            int pivot = array[high];
            int i = low - 1;

            for (int j = low; j < high; j++) {
                if (isSortAscending && array[j] <= pivot || !isSortAscending && array[j] >= pivot) {
                    i++;
                    if (i != j) {
                        swapArrayElements(array, i, j);
                        swapQueue.add(new int[]{i, j});
                    }
                }
            }

            if (i + 1 != high) {
                swapArrayElements(array, i + 1, high);
                swapQueue.add(new int[]{i + 1, high});
            }

            return i + 1;
        }
    }

    /**
     * Entry point of the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingSortApp::new);
    }
}
