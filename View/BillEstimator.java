package View;

import javax.swing.*;

public class BillEstimator extends JFrame {

    private final JTextField numberField;
    private final JLabel errorLabel;

    public BillEstimator() {
        numberField = new JTextField(10);
        errorLabel = new JLabel();
        errorLabel.setIcon(null);

        
        add(new JLabel("Enter a number (1 - 100):"));
        add(numberField);
        add(errorLabel);
    }
}
