package controller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

import dao.impl.JdbcApartamentoDAO;
import model.Apartamento;
import model.Cota;

public class ApartamentoJF extends JPanel implements ListSelectionListener {

	private static final long serialVersionUID = 1L;
	private static final String updateString = "Inserir";
	private static final String deleteString = "Excluir";
	private JButton deleteButton;
	private JButton consultaButton;
	private JTextField cpfCliente;
	private JTextField addCota;

	private JdbcApartamentoDAO apartamentoDAO;
	private DefaultTableModel model;
	private JTable table;
	private ConsultaListener IncludeListener;
	private Apartamento apartamento;

	protected JLabel actionLabel;

	public ApartamentoJF(Apartamento apartamento) {
		super(new BorderLayout());

		this.apartamento = apartamento;

		createTextFields();

		try {
			apartamentoDAO = new JdbcApartamentoDAO();
			createListaApartamentos();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		consultaButton = new JButton(updateString);
		IncludeListener = new ConsultaListener(consultaButton);
		consultaButton.setActionCommand(updateString);
		consultaButton.addActionListener(IncludeListener);
		consultaButton.setEnabled(false);

		deleteButton = new JButton(deleteString);
		deleteButton.setActionCommand(deleteString);
		deleteButton.addActionListener(new DeleteListener());

		cpfCliente.addActionListener(IncludeListener);
		cpfCliente.getDocument().addDocumentListener(IncludeListener);

		addCota.addActionListener(IncludeListener);
		addCota.getDocument().addDocumentListener(IncludeListener);

		// Create a label to put messages during an action event.
		actionLabel = new JLabel("Preencha os campos para inserir cotas");

		// Create a panel that uses BoxLayout.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));

		buttonPane.add(new JLabel("Cliente:"));
		buttonPane.add(cpfCliente);
		buttonPane.add(new JLabel("Cota:"));
		buttonPane.add(addCota);

		buttonPane.add(consultaButton);
		buttonPane.add(deleteButton);

		buttonPane.add(actionLabel);

		buttonPane.setLayout(new GridLayout(4, 2, 2, 2));

		add(buttonPane, BorderLayout.PAGE_START);

	}

	public void createTextFields() {
		cpfCliente = new JTextField(14);
		addCota = new JTextField(3);
		addCota.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
					e.consume(); // ignore event
				}
			}
		});

	}

	public void createListaApartamentos() throws SQLException {

		model = new DefaultTableModel();
		model.addColumn("Cliente");
		model.addColumn("Cotas");

		table = new JTable(model);

		refreshListaApartamentos();

		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, BorderLayout.SOUTH);

	}

	public void refreshListaApartamentos() throws SQLException {

		model.setRowCount(0);

		int idPredio = apartamento.getIdPredio();
		int numero = apartamento.getNumero();

		ArrayList<Cota> listaCotas = new ArrayList<Cota>();

		listaCotas = apartamentoDAO.getListaCotas(idPredio, numero);

		for (Cota cota : listaCotas) {
			Apartamento apartamento = new Apartamento();
			apartamento.insereCota(cota);
			model.addRow(new Object[] { cota.getNomeCotista(), cota.getPercentual() });
		}

	}

	class DeleteListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			// This method can be called only if
			// there's a valid selection
			// so go ahead and remove whatever's selected.

		}
	}

	class ConsultaListener implements ActionListener, DocumentListener {

		private boolean alreadyEnabled = false;
		private JButton button;

		public ConsultaListener(JButton button) {
			this.button = button;
		}

		// Required by ActionListener.
		public void actionPerformed(ActionEvent e) {

			try {

				String cpf = cpfCliente.getText();

				if (apartamentoDAO.checkCpf(cpf)) {

					if (!addCota.getText().trim().isEmpty()) {

						int idPredio = apartamento.getIdPredio();
						int numero = apartamento.getNumero();

						int cota = Integer.parseInt(addCota.getText());

						String mensagem = apartamentoDAO.insereCota(cpf, idPredio, numero, cota);
						actionLabel.setText(mensagem);

						refreshListaApartamentos();

					} else {

						actionLabel.setText("Insira um valor para cota");
					}

				} else {

					actionLabel.setText("CPF inexistente");

				}

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

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
		}
	}

}
