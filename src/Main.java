import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class Main {
    public static String jmeterDirectory = "";
    public static String logFileDirectory = "";
    public static String logFilePath;
    public static long excecutionTime;

    private static JLabel selectedJmxFileLabel;

    public static void updateJmxSelectedFileLabel(String newValue) {
        selectedJmxFileLabel.setText(newValue);
        System.out.println("jmxSelectedFileLabel value updated to: " + newValue);
    }

    public static void addComponentsToPane(Container pane) {

        pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        pane.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridy = 0;

        JLabel jmeterDirectoryLabel = new JLabel("Jmeter directory");
        gbc.gridx = 0;
        pane.add(jmeterDirectoryLabel, gbc);

        JButton jmeterDirectoryButton = new JButton("Choose");
        gbc.gridx++;
        pane.add(jmeterDirectoryButton, gbc);

        JLabel selectedJmeterDirectoryLabel = new JLabel("No directory selected");
        gbc.gridx++;
        pane.add(selectedJmeterDirectoryLabel, gbc);

        jmeterDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);

                int response = fileChooser.showOpenDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    jmeterDirectory = selectedDirectory.toPath().toString();
                    UpdatePropertiesFile.propertiesFilePath = jmeterDirectory + "\\bin\\jmeter.properties";
                    selectedJmeterDirectoryLabel.setText(jmeterDirectory);

                }
            }
        });

        JLabel jmxFileLabel = new JLabel("Jmx file");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        pane.add(jmxFileLabel, gbc);

        JButton jmxFileButton = new JButton("Choose");
        gbc.gridx++;
        pane.add(jmxFileButton, gbc);

        selectedJmxFileLabel = new JLabel("No file selected");
        gbc.gridwidth = 2;
        gbc.gridx++;
        pane.add(selectedJmxFileLabel, gbc);

        JLabel updateJmxFileLabel = new JLabel("Update jmx file");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        pane.add(updateJmxFileLabel, gbc);

        JButton updateJmxFileButton = new JButton("Update");
        updateJmxFileButton.setEnabled(false);
        gbc.gridx++;
        gbc.gridwidth = 2;
        pane.add(updateJmxFileButton, gbc);

        jmxFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("opening file chooser for .jmx file...");
                JFileChooser fileChooser = new JFileChooser();
                FileFilter filter = new FileNameExtensionFilter("JMeter Project File (.jmx)", "jmx");
                fileChooser.setFileFilter(filter);
                fileChooser.setAcceptAllFileFilterUsed(false);

                int response = fileChooser.showOpenDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    System.out.println("selected file: " + selectedFile.toPath().toString());
                    UpdateJmxFile.setJmxFilePath(selectedFile.toPath().toString());
                    updateJmxSelectedFileLabel(UpdateJmxFile.getValidJmxFilePath());

                    updateJmxFileButton.setEnabled(true);
                }
            }
        });

        updateJmxFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("opening new frame...");
                UpdateJmxFile.openUpdateJmxFileFrame(UpdateJmxFile.getValidJmxFilePath());
            }
        });

        JLabel logFormatLabel = new JLabel("Log format");
        gbc.gridx = 0;
        gbc.gridy++;
        pane.add(logFormatLabel, gbc);

        String[] logFormatOptions = { "xml", "csv", "jtl" };
        JComboBox<String> logFormatOptionsBox = new JComboBox<String>(logFormatOptions);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        pane.add(logFormatOptionsBox, gbc);

        JLabel logDirectoryDestinationLabel = new JLabel("Log directory destination");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        pane.add(logDirectoryDestinationLabel, gbc);

        JButton logDirectoryDestinationButton = new JButton("Choose");
        gbc.gridx++;
        pane.add(logDirectoryDestinationButton, gbc);

        JLabel selectedLogDirectoryDestinationLabel = new JLabel("No directory selected");
        gbc.gridwidth = 2;
        gbc.gridx++;
        pane.add(selectedLogDirectoryDestinationLabel, gbc);

        logDirectoryDestinationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                int response = fileChooser.showOpenDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    logFileDirectory = selectedDirectory.toPath().toString();
                    selectedLogDirectoryDestinationLabel.setText(logFileDirectory);
                }
            }
        });

        JButton startButton = new JButton("Start");
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        pane.add(startButton, gbc);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                if (jmeterDirectory.isEmpty()) {
                    Utility.showErrorMessage("No Jmeter installation folder was selected");
                    return;
                }

                if (UpdateJmxFile.getValidJmxFilePath().isEmpty()
                        || !UpdateJmxFile.getValidJmxFilePath().endsWith(".jmx")) {
                    Utility.showErrorMessage("No .jmx file selected");
                    return;

                }

                if (logFileDirectory.isEmpty()) {
                    Utility.showErrorMessage("No log destination folder was selected");
                    return;
                }

                String logFormat = logFormatOptionsBox.getSelectedItem().toString();

                try {
                    excecuteJmeter(logFormat);
                    Utility.showSuccessMessage("operazione completata in " + excecutionTime + " secondi.\n" + "log: " +
                            logFilePath);
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.showErrorMessage("An error occured");
                    System.exit(1);
                }
            }
        });
    }

    public static JFrame frame;

    private static void createAndShowGUI() {
        // Create and set up the window.
        frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up the content pane.
        addComponentsToPane(frame.getContentPane());

        // Display the window.
        // frame.pack();
        frame.setSize(850, 500);
        frame.setVisible(true);
    }

    public static void main(String[] argv) throws Exception {

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static void excecuteJmeter(String format) throws Exception {
        UpdatePropertiesFile.updatePropertiesFile(format);

        // JMeter Engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        // Initialize Properties, logging, locale, etc.
        JMeterUtils.loadJMeterProperties(UpdatePropertiesFile.propertiesFilePath);
        JMeterUtils.setJMeterHome(jmeterDirectory);
        // JMeterUtils.initLocale();

        // Initialize JMeter SaveService
        SaveService.loadProperties();

        // Load existing .jmx Test Plan
        HashTree testPlanTree = SaveService.loadTree(new File(UpdateJmxFile.getValidJmxFilePath()));

        // add Summarizer output to get test progress in stdout like:
        // summary = 2 in 1.3s = 1.5/s Avg: 631 Min: 290 Max: 973 Err: 0 (0.00%)
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }

        logFilePath = logFileDirectory + "\\log." + format;

        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(logFilePath);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run JMeter Test
        jmeter.configure(testPlanTree);

        long startTime = System.nanoTime();
        jmeter.run();
        long stopTime = System.nanoTime();
        excecutionTime = (stopTime - startTime) / 1000000000; // nsec to sec
    }

}