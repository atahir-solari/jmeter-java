import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateJmxFile {

    private static JFrame updateJmxFileFrame;

    private static int loops = 0;
    private static int numThreads = 0;
    private static int rampTime = 0;

    private static String jmxFilePath = ""; // original jmx file
    private static String jmxUpdatedFilePath = ""; // jmx file with updates

    public static void setJmxFilePath(String path) {
        jmxFilePath = path;
        System.out.println("set new jmx file: " + path);
    }

    public static void setJmxUpdatedFilePath(String path) {
        jmxUpdatedFilePath = path;
    }

    public static String getValidJmxFilePath() {
        if (!jmxUpdatedFilePath.isEmpty())
            return jmxUpdatedFilePath;

        return jmxFilePath;
    }

    public static void openUpdateJmxFileFrame(String filePath) {
        updateJmxFileFrame = new JFrame("Update jmx file");
        Container pane = updateJmxFileFrame.getContentPane();
        pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        pane.setLayout(new GridBagLayout());
        updateJmxFileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridy = 0;

        JLabel loopsLabel = new JLabel("Loops");
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;
        pane.add(loopsLabel, gbc);

        JSlider loopsSlider = new JSlider(0, 3000, loops);
        loopsSlider.setPaintTicks(true);
        loopsSlider.setMinorTickSpacing(100);
        loopsSlider.setPaintTrack(true);
        loopsSlider.setPaintLabels(true);
        loopsSlider.setMajorTickSpacing(1000);
        gbc.gridx = 1;
        pane.add(loopsSlider, gbc);

        JLabel selectedLoopsLabel = new JLabel("selected: " + loopsSlider.getValue());
        gbc.gridx = 2;
        pane.add(selectedLoopsLabel, gbc);

        loopsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                JSlider source = (JSlider) event.getSource();
                if (!source.getValueIsAdjusting())
                    selectedLoopsLabel.setText("selected: " + source.getValue());
                loops = source.getValue();
            }
        });

        JLabel numThreadsLabel = new JLabel("Num threads");
        gbc.gridx = 0;
        gbc.gridy++;
        pane.add(numThreadsLabel, gbc);

        JSlider numThreadsSlider = new JSlider(0, 3000, numThreads);
        numThreadsSlider.setPaintTicks(true);
        numThreadsSlider.setMinorTickSpacing(100);
        numThreadsSlider.setPaintTrack(true);
        numThreadsSlider.setPaintLabels(true);
        numThreadsSlider.setMajorTickSpacing(1000);
        gbc.gridx = 1;
        pane.add(numThreadsSlider, gbc);

        JLabel selectedNumThreadsLabel = new JLabel("selected: " + numThreadsSlider.getValue());
        gbc.gridx = 2;
        pane.add(selectedNumThreadsLabel, gbc);

        numThreadsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                JSlider source = (JSlider) event.getSource();
                if (!source.getValueIsAdjusting())
                    selectedNumThreadsLabel.setText("selected: " + source.getValue());
                numThreads = source.getValue();
            }
        });

        JLabel rampTimeLabel = new JLabel("Ramp time");
        gbc.gridx = 0;
        gbc.gridy++;
        pane.add(rampTimeLabel, gbc);

        JSlider rampTimeSlider = new JSlider(0, 30000, rampTime);
        rampTimeSlider.setPaintTicks(true);
        rampTimeSlider.setMinorTickSpacing(1500);
        rampTimeSlider.setPaintTrack(true);
        rampTimeSlider.setPaintLabels(true);
        rampTimeSlider.setMajorTickSpacing(10000);
        gbc.gridx = 1;
        pane.add(rampTimeSlider, gbc);

        JLabel selectedRampTimeLabel = new JLabel("selected: " + rampTimeSlider.getValue());
        gbc.gridx = 2;
        pane.add(selectedRampTimeLabel, gbc);

        rampTimeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                JSlider source = (JSlider) event.getSource();
                if (!source.getValueIsAdjusting())
                    selectedRampTimeLabel.setText("selected: " + source.getValue());
                rampTime = source.getValue();
            }
        });

        JLabel jmxUpdatedFileDestinationLabel = new JLabel("Jmx updated file destination");
        gbc.gridx = 0;
        gbc.gridy++;
        pane.add(jmxUpdatedFileDestinationLabel, gbc);

        JButton jmxUpdatedFileDestinationButton = new JButton("Choose");
        jmxUpdatedFileDestinationButton.setEnabled(false);
        gbc.gridx++;
        pane.add(jmxUpdatedFileDestinationButton, gbc);

        JLabel selectedJmxUpdatedFileDestinationLabel = new JLabel("No path selected");
        selectedJmxUpdatedFileDestinationLabel.setEnabled(false);
        gbc.gridwidth = 2;
        gbc.gridx++;
        pane.add(selectedJmxUpdatedFileDestinationLabel, gbc);

        jmxUpdatedFileDestinationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JFileChooser fileChooser = new JFileChooser();
                FileFilter filter = new FileNameExtensionFilter("JMeter Project File (.jmx)", "jmx");
                fileChooser.setFileFilter(filter);
                fileChooser.setAcceptAllFileFilterUsed(false);
                int response = fileChooser.showSaveDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    jmxUpdatedFilePath = selectedFile.toPath().toString();
                    System.out.println("jmx updated file destination: " + jmxUpdatedFilePath);

                    // check that selected file finish with .jmx
                    if (!jmxUpdatedFilePath.endsWith(".jmx")) {
                        System.out.println("not a valid file name, adding '.jmx' extension...");
                        jmxUpdatedFilePath = jmxUpdatedFilePath + ".jmx";
                    }

                    selectedJmxUpdatedFileDestinationLabel.setText(jmxUpdatedFilePath);
                    System.out.println("jmx updated file destination: " + jmxUpdatedFilePath);
                }
            }
        });

        JCheckBox overwriteFileCheckBox = new JCheckBox("Overwrite original file?", true);
        gbc.gridx = 0;
        gbc.gridy++;
        pane.add(overwriteFileCheckBox, gbc);

        JButton saveButton = new JButton("Save");
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        pane.add(saveButton, gbc);

        // save changes to file
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!overwriteFileCheckBox.isSelected() && jmxUpdatedFilePath.isEmpty()) {
                    Utility.showErrorMessage(
                            "attenzione, non si è specficato il percorso del nuovo file oppure se si desidera sovrascrivere l'originale");
                    return;
                }

                if (overwriteFileCheckBox.isSelected()) {
                    System.out.println("checkbox is selected");
                    System.out.println("jmxFilePath: " + jmxFilePath);
                    System.out.println("jmxUpdatedFilePath: " + jmxUpdatedFilePath);
                    if (jmxUpdatedFilePath.isEmpty()) {
                        jmxUpdatedFilePath = jmxFilePath;
                        System.out.println("jmxUpdatedFilePath is empty");
                        System.out.println("jmxUpdatedFilePath = jmxFilePath: " + jmxFilePath);
                    }
                }

                // create new file if not exists and copy the original content
                if (!new File(getValidJmxFilePath()).exists()) {
                    try {
                        System.out.println("create a copy of " + jmxFilePath + " in " + jmxUpdatedFilePath);
                        Files.copy(new File(jmxFilePath).toPath(), new File(jmxUpdatedFilePath).toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("updating " + getValidJmxFilePath() + "...");
                System.out.println("loops: " + loops);
                System.out.println("num threads: " + numThreads);
                System.out.println("ramp time: " + rampTime);

                updateLoops(jmxUpdatedFilePath, loops);
                updateNumThreads(jmxUpdatedFilePath, numThreads);
                updateRampTime(jmxUpdatedFilePath, rampTime);

                Main.updateJmxSelectedFileLabel(jmxUpdatedFilePath);
                jmxFilePath = jmxUpdatedFilePath;

                closeUpdateJmxFileFrame();
            }
        });

        overwriteFileCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                // disable file picker if overwrite is selected
                boolean isCheckBoxSelected = (event.getStateChange() == ItemEvent.SELECTED);
                jmxUpdatedFileDestinationButton.setEnabled(!isCheckBoxSelected);
                selectedJmxUpdatedFileDestinationLabel.setEnabled(!isCheckBoxSelected);

            }
        });

        JButton closeButton = new JButton("Close");
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        pane.add(closeButton, gbc);

        // close frame
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                closeUpdateJmxFileFrame();
            }
        });

        updateJmxFileFrame.pack();
        updateJmxFileFrame.setVisible(true);
    }

    public static void closeUpdateJmxFileFrame() {
        System.out.println("closing frame...");

        jmxUpdatedFilePath = "";
        updateJmxFileFrame.dispose();
    }

    static DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    static TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public static void updateRampTime(String path, int newValue) {
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(path);
            Node threadGroupNode = doc.getElementsByTagName("ThreadGroup").item(0);

            NodeList nodeList = threadGroupNode.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);
                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element currentElement = (Element) currentNode;
                    if ("stringProp".equals(currentElement.getNodeName()))
                        if ("ThreadGroup.ramp_time".equals(currentElement.getAttribute("name")))
                            currentElement.setTextContent(String.valueOf(newValue));
                }
            }
            writeToFile(path, doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateNumThreads(String path, int newValue) {
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(path);
            Node threadGroupNode = doc.getElementsByTagName("ThreadGroup").item(0);

            NodeList nodeList = threadGroupNode.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);
                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element currentElement = (Element) currentNode;
                    if ("stringProp".equals(currentElement.getNodeName()))
                        if ("ThreadGroup.num_threads".equals(currentElement.getAttribute("name")))
                            currentElement.setTextContent(String.valueOf(newValue));
                }
            }
            writeToFile(path, doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLoops(String path, int newValue) {
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(path);
            Node threadGroupNode = doc.getElementsByTagName("ThreadGroup").item(0);

            NodeList nodeList = threadGroupNode.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);
                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element currentElement = (Element) currentNode;
                    if ("elementProp".equals(currentElement.getNodeName())) {
                        NodeList subNodeList = currentElement.getChildNodes();
                        for (int j = 0; j < subNodeList.getLength(); j++) {
                            Node subCurrentNode = subNodeList.item(j);
                            if (subCurrentNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element subCurrentElement = (Element) subCurrentNode;
                                if ("stringProp".equals(subCurrentElement.getNodeName()))
                                    subCurrentElement.setTextContent(String.valueOf(newValue));
                            }
                        }
                    }
                }
            }
            writeToFile(path, doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeToFile(String path, Document doc) throws Exception {
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        FileWriter writer = new FileWriter(new File(path));
        StreamResult result = new StreamResult(writer);
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(source, result);
    }
}
