package controller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import dao.impl.JdbcClienteDAO;
import model.Cliente;

public class CadastroClienteJF extends JPanel implements ListSelectionListener {

	private static final long serialVersionUID = 1L;
	private static final String includeString = "Incluir";
	private static final String deleteString = "Excluir";
	private JButton deleteButton;
	private JTextField clientName;
	private JTextField clientCPF;
	private JTextField clientTelefone;
	private JTextField clientDataNasc;

	private JdbcClienteDAO clienteDAO;
	private DefaultTableModel model;
	private JTable table;

	public CadastroClienteJF() {
		super(new BorderLayout());

		try {
			clienteDAO = new JdbcClienteDAO();
			createListaClientes();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JButton includeButton = new JButton(includeString);
		IncludeListener IncludeListener = new IncludeListener(includeButton);
		includeButton.setActionCommand(includeString);
		includeButton.addActionListener(IncludeListener);
		includeButton.setEnabled(false);

		deleteButton = new JButton(deleteString);
		DeleteListener deleteListener = new DeleteListener(deleteButton);
		deleteButton.setActionCommand(deleteString);
		deleteButton.addActionListener(deleteListener);
		deleteButton.setEnabled(false);

		clientName = new JTextField(30);
		clientCPF = new JTextField(14);
		clientTelefone = new JTextField(14);
		clientDataNasc = new JTextField(20);

		clientName.addActionListener(IncludeListener);
		clientName.getDocument().addDocumentListener(IncludeListener);

		clientCPF.addActionListener(IncludeListener);
		clientCPF.getDocument().addDocumentListener(IncludeListener);

		clientCPF.addActionListener(deleteListener);
		clientCPF.getDocument().addDocumentListener(deleteListener);

		clientTelefone.addActionListener(IncludeListener);
		clientTelefone.getDocument().addDocumentListener(IncludeListener);

		clientDataNasc.addActionListener(IncludeListener);
		clientDataNasc.getDocument().addDocumentListener(IncludeListener);

		// Create a panel that uses BoxLayout.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));

		buttonPane.add(new JLabel("Nome:"));
		buttonPane.add(clientName);
		buttonPane.add(new JLabel("CPF:"));
		buttonPane.add(clientCPF);
		buttonPane.add(new JLabel("Telefone:"));
		buttonPane.add(clientTelefone);
		buttonPane.add(new JLabel("Data de Nascimento:"));
		buttonPane.add(clientDataNasc);

		buttonPane.add(includeButton);
		buttonPane.add(deleteButton);

		buttonPane.setLayout(new GridLayout(5, 2, 2, 2));

		// add(listScrollPane, BorderLayout.SOUTH);
		add(buttonPane, BorderLayout.PAGE_START);

	}

	public void createListaClientes() {

		model = new DefaultTableModel();
		model.addColumn("Nome");
		model.addColumn("CPF");
		model.addColumn("Telefone");
		model.addColumn("Data_Nascimento");

		table = new JTable(model);
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {

				int row = table.rowAtPoint(evt.getPoint());

				clientName.setText((String) table.getValueAt(row, 0));
				clientCPF.setText((String) table.getValueAt(row, 1));
				clientTelefone.setText((String) table.getValueAt(row, 2));
				clientDataNasc.setText((String) table.getValueAt(row, 3));

			}
		});

		refresh();

		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, BorderLayout.SOUTH);

	}

	public void refresh() {

		model.setRowCount(0);

		ArrayList<Cliente> listaCLientes = new ArrayList<Cliente>();

		try {
			listaCLientes = clienteDAO.retornaClientes();
			for (Cliente cliente : listaCLientes) {
				model.addRow(new Object[] { cliente.getName(), cliente.getCPF(), cliente.getTelefone(),
						cliente.getDataNascimento() });
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void clearFields() {
		clientName.setText(null);
		clientCPF.setText(null);
		clientTelefone.setText(null);
		clientDataNasc.setText(null);
	}

	class DeleteListener implements ActionListener, DocumentListener {

		private boolean alreadyEnabled = false;
		private JButton button;

		public DeleteListener(JButton button) {
			this.button = button;
		}

		public void actionPerformed(ActionEvent e) {

			String cpf = clientCPF.getText();

			try {
				clienteDAO.delete(cpf);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			refresh();
			clearFields();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			if (!handleEmptyTextField(e)) {
				enableButton();
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			enableButton();

		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			handleEmptyTextField(e);

		}

		private void enableButton() {
			if (!alreadyEnabled) {
				button.setEnabled(true);
			}
		}

		private boolean handleEmptyTextField(DocumentEvent e) {
			if (e.getDocument().getLength() <= 0) {
				button.setEnabled(false);
				alreadyEnabled = false;
				return true;
			}
			return false;
		}
	}

	class IncludeListener implements ActionListener, DocumentListener {

		private boolean alreadyEnabled = false;
		private JButton button;

		public IncludeListener(JButton button) {
			this.button = button;
		}

		// Required by ActionListener.
		public void actionPerformed(ActionEvent e) {

			String name = clientName.getText();
			String cpf = clientCPF.getText();
			String telefone = clientTelefone.getText();
			String dataNasc = clientDataNasc.getText();

			try {
				clienteDAO.insere(name, cpf, telefone, dataNasc);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			refresh();

			// Reset the text field.
			clientName.requestFocusInWindow();
			clientName.setText("");

			clientCPF.requestFocusInWindow();
			clientCPF.setText("");

			// Reset the text field.
			clientTelefone.requestFocusInWindow();
			clientTelefone.setText("");

			clientDataNasc.requestFocusInWindow();
			clientDataNasc.setText("");

		}

		// Required by DocumentListener.
		public void insertUpdate(DocumentEvent e) {
			enableButton();
		}

		// Required by DocumentListener.
		public void removeUpdate(DocumentEvent e) {
			handleEmptyTextField(e);
		}

		// Required by DocumentListener.
		public void changedUpdate(DocumentEvent e) {
			if (!handleEmptyTextField(e)) {
				enableButton();
			}
		}

		private void enableButton() {
			if (!alreadyEnabled) {
				button.setEnabled(true);
			}
		}

		private boolean handleEmptyTextField(DocumentEvent e) {
			if (e.getDocument().getLength() <= 0) {
				button.setEnabled(false);
				alreadyEnabled = false;
				return true;
			}
			return false;
		}
	}

	// This method is required by ListSelectionListener.
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {

			if ((table.getRowCount() == 0) || (clientCPF.getText() == null)) {
				deleteButton.setEnabled(false);
			} else {
				deleteButton.setEnabled(true);
			}
		}
	}

}
