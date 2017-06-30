package controller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

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

import dao.impl.JdbcCondominioDAO;
import model.Condominio;
import model.Predio;

public class CondominioJF extends JPanel implements ListSelectionListener {

	private static final long serialVersionUID = 1L;
	private static final String includeString = "Consultar";
	private static final String deleteString = "Excluir";
	private JButton deleteButton;
	private JButton consultaButton;
	private JTextField idCondominio;
	private JTextField nomeCondominio;
	private JTextField cpfCondominio;

	private JdbcCondominioDAO condominioDAO;
	private Condominio condominio;
	private DefaultTableModel model;
	private JTable table;
	private ConsultaListener IncludeListener;

	protected JLabel actionLabel, emptyLabel;

	public CondominioJF() {
		super(new BorderLayout());

		createTextFields();
		createListaPredios();

		try {
			condominioDAO = new JdbcCondominioDAO();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		consultaButton = new JButton(includeString);
		IncludeListener = new ConsultaListener(consultaButton);
		consultaButton.setActionCommand(includeString);
		consultaButton.addActionListener(IncludeListener);
		consultaButton.setEnabled(false);

		deleteButton = new JButton(deleteString);
		deleteButton.setActionCommand(deleteString);
		deleteButton.addActionListener(new DeleteListener());

		idCondominio.addActionListener(IncludeListener);
		idCondominio.getDocument().addDocumentListener(IncludeListener);

		nomeCondominio.addActionListener(IncludeListener);
		nomeCondominio.getDocument().addDocumentListener(IncludeListener);

		cpfCondominio.addActionListener(IncludeListener);
		cpfCondominio.getDocument().addDocumentListener(IncludeListener);

		// Create a panel that uses BoxLayout.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));

		buttonPane.add(new JLabel("Código:"));
		buttonPane.add(idCondominio);
		buttonPane.add(new JLabel("Nome:"));
		buttonPane.add(nomeCondominio);
		buttonPane.add(new JLabel("CPF responsável:"));
		buttonPane.add(cpfCondominio);

		actionLabel = new JLabel("");
		buttonPane.add(actionLabel);
		
		emptyLabel = new JLabel("");
		buttonPane.add(emptyLabel);
		
		buttonPane.add(consultaButton);
		buttonPane.add(deleteButton);

		buttonPane.setLayout(new GridLayout(5, 2, 2, 2));

		add(buttonPane, BorderLayout.PAGE_START);

	}

	public void createTextFields() {
		idCondominio = new JTextField(5);
		idCondominio.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
					e.consume(); // ignore event
				}
			}
		});

		nomeCondominio = new JTextField(30);
		cpfCondominio = new JTextField(14);
	}

	public void createListaPredios() {

		model = new DefaultTableModel();
		model.addColumn("Id_Predio");
		model.addColumn("Nome");
		model.addColumn("Nome_Síndico");
		model.addColumn("Numero");

		table = new JTable(model);
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {

				int row = table.rowAtPoint(evt.getPoint());

				Predio predio = new Predio();
				predio = condominio.getPredioAt(row);

				JFrame frame = new JFrame("Predio: " + predio.getNome());
				frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

				JComponent newContentPane = new PredioJF(predio);
				newContentPane.setOpaque(true);
				frame.setContentPane(newContentPane);

				frame.pack();
				frame.setVisible(true);

				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						try {
							refreshListaPredios();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				});

			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, BorderLayout.SOUTH);

	}

	public void refreshListaPredios() throws SQLException {

		model.setRowCount(0);

		int idCondo = Integer.parseInt(idCondominio.getText());
		condominio = condominioDAO.consulta(idCondo);

		nomeCondominio.setText(condominio.getNome());
		cpfCondominio.setText(condominio.getCpfResponsavel());

		for (Predio predio : condominio.listaPredios()) {

			model.addRow(new Object[] { predio.getIdPredio(), predio.getNome(), predio.getNomeSindico(),
					predio.getNumero() });
		}

		actionLabel.setText(String.valueOf(condominioDAO.get_percentual_vendido(idCondo)) + "% vendido");

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
				refreshListaPredios();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
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
