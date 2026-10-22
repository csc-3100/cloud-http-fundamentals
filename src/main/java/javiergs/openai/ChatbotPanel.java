package javiergs.openai;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * ChatbotPanel class
 * It is a JPanel that contains a text area for the chatbot output, a text field for the user input,
 * and a button to send the input.
 *
 * @version 1.0
 * @author javiergs
 */
public class ChatbotPanel extends JPanel implements PropertyChangeListener, ActionListener {
	
	private JTextPane outputArea;
	private JTextField inputField;
	private JButton sendButton;
	
	public ChatbotPanel() {
		// output text area
		outputArea = new JTextPane();
		outputArea.setEditable(false);
		outputArea.setContentType("text/html");
		outputArea.setFocusable(false);
		JScrollPane scrollPane = new JScrollPane(outputArea);
		scrollPane.setBorder(BorderFactory.createTitledBorder("ChatGPT:"));
		// user input field
		inputField = new JTextField();
		inputField.addActionListener(this);
		sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		// assembly of components
		setLayout(new BorderLayout());
		JPanel south = new JPanel(new BorderLayout());
		south.add(inputField, BorderLayout.CENTER);
		south.add(sendButton, BorderLayout.EAST);
		add(scrollPane, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);
		inputField.requestFocus();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String input = inputField.getText();
		if (input.isBlank()) return;
		inputField.setText("");
		// disable sending
		sendButton.setText("Sending...");
		sendButton.setEnabled(false);
		// create a new ChatbotBrain object
		ChatbotBrain chatbotBrain = new ChatbotBrain(input);
		chatbotBrain.addPropertyChangeListener(event -> SwingUtilities.invokeLater(() -> {
			String output = (String) event.getNewValue();
			appendConversation(input, output);
		}));
		Thread thread = new Thread(chatbotBrain);
		thread.start();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Updates from worker threads must be dispatched to Swing's event thread.
		if ("output".equals(evt.getPropertyName()))
			SwingUtilities.invokeLater(() -> appendConversation("", (String) evt.getNewValue()));
	}

	private void appendConversation(String input, String output) {
		StyledDocument doc = outputArea.getStyledDocument();
		Style userStyle = outputArea.addStyle("User", null);
		StyleConstants.setForeground(userStyle, new Color(50, 100, 0));
		Style replyStyle = outputArea.addStyle("Reply", null);
		StyleConstants.setForeground(replyStyle, Color.GRAY);
		try {
			doc.insertString(doc.getLength(), "You -> " + input + "\n", userStyle);
			doc.insertString(doc.getLength(), "ChatGPT -> " + output + "\n", replyStyle);
		} catch (BadLocationException ex) {
			System.err.println(ex.getMessage());
		}
		sendButton.setText("Send");
		sendButton.setEnabled(true);
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 400);
	}
	
}