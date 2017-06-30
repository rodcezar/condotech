package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
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
import dao.impl.JdbcClienteDAO;
import model.Apartamento;
import model.Predio;

public class PredioJF extends JPanel implements ListSelectionListener {

	private static final long serialVersionUID = 1L;
	private static final String includeString = "Consultar";
	private static final String deleteString = "Excluir";
	private static final String updateString = "Atualizar";
	private JButton deleteButton;
	private JButton consultaButton;
	private JButton updateButton;
	private JTextField idPredio;
	private JTextField nomePredio;
	private JTextField nomeSindico;
	private JTextField cpfSindico;

	private JdbcApartamentoDAO apartamentoDAO;
	private JdbcClienteDAO clienteDAO;
	private DefaultTableModel model;
	private JTable table;
	private ConsultaListener includeListener;
	private UpdateListener updateListener;
	private Predio predio;

	protected JLabel actionLabel;

	public PredioJF(Predio predio) {
		super(new BorderLayout());

		this.predio = predio;

		createTextFields();
		try {
			apartamentoDAO = new JdbcApartamentoDAO();
			createListaApartamentos();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		consultaButton = new JButton(includeString);
		includeListener = new ConsultaListener(consultaButton);
		consultaButton.setActionCommand(includeString);
		consultaButton.addActionListener(includeListener);
		consultaButton.setEnabled(false);

		deleteButton = new JButton(deleteString);
		DeleteListener deleteListener = new DeleteListener(deleteButton);
		deleteButton.setActionCommand(deleteString);
		deleteButton.addActionListener(deleteListener);
	//	deleteButton.setEnabled(false);

		updateButton = new JButton(updateString);
		updateListener = new UpdateListener(updateButton);
		updateButton.setActionCommand(deleteString);
		updateButton.addActionListener(updateListener);

		idPredio.addActionListener(includeListener);
		idPredio.getDocument().addDocumentListener(includeListener);

		nomePredio.addActionListener(includeListener);
		nomePredio.getDocument().addDocumentListener(includeListener);

		nomeSindico.addActionListener(includeListener);
		nomeSindico.getDocument().addDocumentListener(includeListener);

		cpfSindico.addActionListener(updateListener);
		cpfSindico.getDocument().addDocumentListener(updateListener);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));

		buttonPane.add(new JLabel("C�digo:"));
		buttonPane.add(idPredio);
		buttonPane.add(new JLabel("Nome:"));
		buttonPane.add(nomePredio);
		buttonPane.add(new JLabel("Nome sindico:"));
		buttonPane.add(nomeSindico);
		buttonPane.add(new JLabel("CPF sindico:"));
		buttonPane.add(cpfSindico);

		// Create a label to put messages during an action event.
		actionLabel = new JLabel("");
		buttonPane.add(actionLabel);

		buttonPane.setLayout(new GridLayout(5, 2, 2, 2));
		add(buttonPane, BorderLayout.PAGE_START);

		JPanel buttonPane2 = new JPanel();
		buttonPane2.setLayout(new BoxLayout(buttonPane2, BoxLayout.LINE_AXIS));

		buttonPane2.add(consultaButton);
		buttonPane2.add(deleteButton);
		buttonPane2.add(updateButton);

		buttonPane2.setLayout(new GridLayout(1, 3, 2, 2));

		add(buttonPane2);
		
		
		
	}

	public void createTextFields() {
		idPredio = new JTextField(5);
		idPredio.setText(Integer.toString(predio.getIdPredio()));
		nomePredio = new JTextField(30);
		nomePredio.setText(predio.getNome());
		nomeSindico = new JTextField(30);
		nomeSindico.setText(predio.getNomeSindico());
		cpfSindico = new JTextField(14);
		cpfSindico.setText(predio.getCpfSindico());
	}

	public void createListaApartamentos() throws SQLException {

		model = new DefaultTableModel();
		model.addColumn("Apartamentos");
		model.addColumn("Nome_Proprietário");

		table = new JTable(model);

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = table.rowAtPoint(evt.getPoint());

				Apartamento apartamento = new Apartamento();
				apartamento = predio.getApartamentoAt(row);

				JFrame frame = new JFrame("Apartamento: " + apartamento.getNumero());
				frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

				JComponent newContentPane = new ApartamentoJF(apartamento);
				newContentPane.setOpaque(true);
				frame.setContentPane(newContentPane);

				frame.pack();
				frame.setVisible(true);

				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						try {
							refreshListaApartamentos();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				});

			}
		});

		refreshListaApartamentos();

		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, BorderLayout.SOUTH);

	}

	public void refreshListaApartamentos() throws SQLException {

		model.setRowCount(0);

		int idPredio = predio.getIdPredio();

		ArrayList<Apartamento> listaApartamentos = new ArrayList<Apartamento>();

		listaApartamentos = apartamentoDAO.getListaApartamento(idPredio);

		for (Apartamento apartamento : listaApartamentos) {
			apartamento.insereCota(null);
			predio.insereApartamento(apartamento);
			model.addRow(new Object[] { apartamento.getNumero(), apartamento.getNomeProprietario() });
		}

	}

	public void refreshFields() throws SQLException {
		Predio newPredio = apartamentoDAO.getPredio(predio.getIdPredio(), predio.getIdComdominio());

		idPredio.setText(Integer.toString(newPredio.getIdPredio()));
		nomePredio.setText(newPredio.getNome());
		nomeSindico.setText(newPredio.getNomeSindico());
		cpfSindico.setText(newPredio.getCpfSindico());
	}

	class DeleteListener implements ActionListener, DocumentListener {

		private boolean alreadyEnabled = false;
		private JButton button;

		public DeleteListener(JButton button) {
			this.button = button;
		}

		public void actionPerformed(ActionEvent e) {


			try {
				
				if (apartamentoDAO.deletePredio(predio.getIdPredio())){
					setVisible(false);
				}
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

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

	class ConsultaListener implements ActionListener, DocumentListener {

		private boolean alreadyEnabled = false;
		private JButton button;

		public ConsultaListener(JButton button) {
			this.button = button;
		}

		// Required by ActionListener.
		public void actionPerformed(ActionEvent e) {

			model.setRowCount(0);
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

	class UpdateListener implements ActionListener, DocumentListener {

		private boolean alreadyEnabled = false;
		private JButton button;

		public UpdateListener(JButton button) {
			this.button = button;
		}

		// Required by ActionListener.
		public void actionPerformed(ActionEvent e) {

			try {

				String cpf = cpfSindico.getText();

				if (apartamentoDAO.checkCpf(cpf)) {

					model.setRowCount(0);

					clienteDAO = new JdbcClienteDAO();

					String mensagem = clienteDAO.setSindico(predio.getIdPredio(), cpf);
					actionLabel.setForeground(Color.BLUE);
					actionLabel.setText(mensagem);

					refreshListaApartamentos();
					refreshFields();
					
				} else {

					actionLabel.setText("ERRO: CPF inexistente");
					actionLabel.setForeground(Color.RED);
					actionLabel.setFont(new Font(actionLabel.getName(), Font.PLAIN, 12));

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
