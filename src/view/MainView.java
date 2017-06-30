package view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.CadastroClienteJF;
import controller.CondominioJF;
 
public class MainView extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try 
				{
					MainView frame = new MainView();
					frame.setVisible(true);					
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
 
	public MainView()
	{
		
		setTitle("CondoTech");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnCadastroCliente = new JButton("Cadastro de clientes");
		btnCadastroCliente.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
		btnCadastroCliente.setBounds(10, 100, 200, 25);
		contentPane.add(btnCadastroCliente);

		btnCadastroCliente.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
		        JFrame frame = new JFrame("Cadastro de clientes");
		        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		 
		        JComponent newContentPane = new CadastroClienteJF();
		        newContentPane.setOpaque(true);
		        frame.setContentPane(newContentPane);
		 
		        frame.pack();
		        frame.setVisible(true);
			}
		});
		
		JButton btnCadastroCondominio = new JButton("Cadastro de Condominios");
		btnCadastroCondominio.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 12));
		btnCadastroCondominio.setBounds(220, 100, 200, 25);
		contentPane.add(btnCadastroCondominio);

		btnCadastroCondominio.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
		        JFrame frame = new JFrame("Cadastro de Condominios");
		        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		 
		        JComponent newContentPane = new CondominioJF();
		        newContentPane.setOpaque(true);
		        frame.setContentPane(newContentPane);
		 
		        frame.pack();
		        frame.setVisible(true);
			}
		});
		
		JLabel lblMain = new JLabel("CondoTech");
		lblMain.setForeground(Color.BLUE);
		lblMain.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 24));
		lblMain.setBounds(160, 32, 239, 39);
		contentPane.add(lblMain);
	}
}
