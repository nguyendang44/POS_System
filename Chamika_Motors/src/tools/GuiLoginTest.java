package tools;

import gui.SignIn;
import java.lang.reflect.Field;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Automated test to programmatically fill the SignIn form and click Sign In.
 * This helps verify DB-backed authentication without manual GUI interaction.
 */
public class GuiLoginTest {
    public static void main(String[] args) throws Exception {
        final SignIn[] frameHolder = new SignIn[1];

        // Create the SignIn frame on the EDT
        SwingUtilities.invokeAndWait(() -> {
            SignIn f = new SignIn();
            f.setVisible(true);
            frameHolder[0] = f;
        });

        SignIn signIn = frameHolder[0];
        if (signIn == null) {
            System.err.println("Failed to create SignIn frame");
            System.exit(2);
        }

        // Use reflection to access private fields: jTextField1, jPasswordField1, jButton1
        Field tfField = SignIn.class.getDeclaredField("jTextField1");
        Field pwField = SignIn.class.getDeclaredField("jPasswordField1");
        Field btnField = SignIn.class.getDeclaredField("jButton1");
        tfField.setAccessible(true);
        pwField.setAccessible(true);
        btnField.setAccessible(true);

        JTextField mobileField = (JTextField) tfField.get(signIn);
        JPasswordField passwordField = (JPasswordField) pwField.get(signIn);
        JButton signButton = (JButton) btnField.get(signIn);

        // Fill with admin credentials created earlier
        mobileField.setText("0701234567");
        passwordField.setText("admin123");

        // Trigger the button click on the EDT
        SwingUtilities.invokeLater(() -> {
            System.out.println("Attempting automated Sign In...");
            signButton.doClick();
        });

        // Give UI some time to perform the login and open Home (if successful)
        Thread.sleep(3000);

        // Report whether SignIn was disposed/hidden
        System.out.println("SignIn isVisible=" + signIn.isVisible() + ", isDisplayable=" + signIn.isDisplayable());

        // Check if Home frame opened
        boolean foundHome = false;
        for (java.awt.Frame f : java.awt.Frame.getFrames()) {
            if (f.getClass().getName().equals("gui.Home")) {
                System.out.println("Found Home window: " + f.getTitle());
                foundHome = true;
            }
        }
        if (!foundHome) {
            System.out.println("Home window not found after sign-in");
        }

        System.out.println("GuiLoginTest finished");
        // Keep program alive a bit longer so windows remain visible
        Thread.sleep(2000);
        System.exit(0);
    }
}
