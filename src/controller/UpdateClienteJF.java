package controller;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import dao.impl.JdbcClienteDAO;
import model.Cliente;

public class UpdateClienteJF extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private String newline = "\n";
	protected static final String textFieldString = "Insira o CPF";
	protected static final String buttonString = "JButton";

	protected JLabel actionLabel;

	JTextField jTextCpf;
	JTextPane textPane;

	JdbcClienteDAO clienteDAO;
	Cliente cliente;

	public UpdateClienteJF() {
		setLayout(new BorderLayout());

		// Create a regular text field.
		jTextCpf = new JTextField(14);
		jTextCpf.setActionCommand(textFieldString);
		jTextCpf.addActionListener(this);

		// Create some labels for the fields.
		JLabel textFieldLabel = new JLabel(textFieldString + ": ");
		textFieldLabel.setLabelFor(jTextCpf);

		// Create a label to put messages during an action event.
		actionLabel = new JLabel("Digite o CPF e aperte enter");
		actionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		// Lay out the text controls and the labels.
		JPanel textControlsPane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		textControlsPane.setLayout(gridbag);

		JLabel[] labels = { textFieldLabel };
		JTextField[] textFields = { jTextCpf };
		addLabelTextRows(labels, textFields, gridbag, textControlsPane);

		c.gridwidth = GridBagConstraints.REMAINDER; // last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		textControlsPane.add(actionLabel, c);
		textControlsPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Consulta"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// Create a text area.

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Serif", Font.ITALIC, 16));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(250, 250));
		areaScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Dados do cliente"),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)),
				areaScrollPane.getBorder()));

		// Create an editor pane.
		JEditorPane editorPane = createEditorPane();
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(250, 145));
		editorScrollPane.setMinimumSize(new Dimension(10, 10));

		// Create a text pane.
		textPane = createTextPane();
		JScrollPane paneScrollPane = new JScrollPane(textPane);
		paneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		paneScrollPane.setPreferredSize(new Dimension(250, 155));
		paneScrollPane.setMinimumSize(new Dimension(10, 10));

		// Put the editor pane and the text pane in a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorScrollPane, paneScrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.5);
		JPanel rightPane = new JPanel(new GridLayout(1, 0));
		rightPane.add(splitPane);
		rightPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Styled Text"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// Put everything together.
		JPanel leftPane = new JPanel(new BorderLayout());
		leftPane.add(textControlsPane, BorderLayout.PAGE_START);
		leftPane.add(areaScrollPane, BorderLayout.CENTER);

		add(leftPane, BorderLayout.LINE_START);
		add(rightPane, BorderLayout.LINE_END);
	}

	private void addLabelTextRows(JLabel[] labels, JTextField[] textFields, GridBagLayout gridbag,
			Container container) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		int numLabels = labels.length;

		for (int i = 0; i < numLabels; i++) {
			c.gridwidth = GridBagConstraints.RELATIVE; // next-to-last
			c.fill = GridBagConstraints.NONE; // reset to default
			c.weightx = 0.0; // reset to default
			container.add(labels[i], c);

			c.gridwidth = GridBagConstraints.REMAINDER; // end row
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			container.add(textFields[i], c);
		}
	}

	public void actionPerformed(ActionEvent e) {

		String prefix = "You typed \"";

		if (textFieldString.equals(e.getActionCommand())) {

			JTextField source = (JTextField) e.getSource();
			actionLabel.setText(prefix + source.getText() + "\"");

			try {
				clienteDAO = new JdbcClienteDAO();
				String cpf = jTextCpf.getText();
				cliente = clienteDAO.consulta(cpf);

				textPane.setText(cliente.getName() + " " + cliente.getDataNascimento());

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private JEditorPane createEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);

		return editorPane;
	}

	private JTextPane createTextPane() {
		String[] initString = { "This is an editable JTextPane, ", // regular
				"another ", // italic
				"styled ", // bold
				"text ", // small
				"component, ", // large
				"which supports embedded components..." + newline, // regular
				" " + newline, // button
				"...and embedded icons..." + newline, // regular
				" ", // icon
				newline + "JTextPane is a subclass of JEditorPane that "
						+ "uses a StyledEditorKit and StyledDocument, and provides "
						+ "cover methods for interacting with those objects." };

		String[] initStyles = { "regular", "italic", "bold", "small", "large", "regular", "button", "regular", "icon",
				"regular" };

		JTextPane textPane = new JTextPane();
		StyledDocument doc = textPane.getStyledDocument();
		addStylesToDocument(doc);

		try {
			for (int i = 0; i < initString.length; i++) {
				doc.insertString(doc.getLength(), initString[i], doc.getStyle(initStyles[i]));
			}
		} catch (BadLocationException ble) {
			System.err.println("Couldn't insert initial text into text pane.");
		}

		return textPane;
	}

	protected void addStylesToDocument(StyledDocument doc) {
		// Initialize some styles.
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");

		Style s = doc.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);

		s = doc.addStyle("bold", regular);
		StyleConstants.setBold(s, true);

		s = doc.addStyle("small", regular);
		StyleConstants.setFontSize(s, 10);

		s = doc.addStyle("large", regular);
		StyleConstants.setFontSize(s, 16);

	}

}
