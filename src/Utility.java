import javax.swing.JOptionPane;

public class Utility {
    public static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
