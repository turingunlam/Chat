package graficos;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.NoSuchElementException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import pk.Cliente;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;

public class VentanaCliente extends Thread {
	private Cliente cliente;
	private JFrame ventana;
	private JTextField textFieldEscribir;
	private JScrollPane scrollPane;
	private JTextArea textAreaMensajes;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaCliente ventana = new VentanaCliente();
					ventana.getVentana().setVisible(true);
					ventana.start();
				} catch (Exception e) {
					System.out.println("No se pudo conectar al server.");
					return;
				}
			}
		});
	}

	public VentanaCliente() throws UnknownHostException, IOException {
		cliente = new Cliente(500);

		ventana = new JFrame("Chat");
		ventana.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cliente.escribirMensaje(cliente.getNombre() + ": /salir");
			}
		});

		scrollPane = new JScrollPane();
		ventana.getContentPane().add(scrollPane, BorderLayout.CENTER);

		textAreaMensajes = new JTextArea();
		scrollPane.setViewportView(textAreaMensajes);
		textAreaMensajes.setEditable(false);

		textFieldEscribir = new JTextField("Escriba su mensaje...");
		textFieldEscribir.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textFieldEscribir.setText("");
				textFieldEscribir.setEditable(true);
			}

			@Override
			public void focusLost(FocusEvent e) {
				textFieldEscribir.setText("Escriba su mensaje...");
				textFieldEscribir.setEditable(false);
			}
		});

		hiloEscribirMensaje();

		ventana.getContentPane().add(textFieldEscribir, BorderLayout.SOUTH);
		textFieldEscribir.setColumns(10);

		ventana.setResizable(false);
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setBounds(100, 100, 703, 434);
	}

	private String leerNombre(boolean repetido) {
		if (repetido) {
			return JOptionPane.showInputDialog(ventana, "Nombre ya utilizado! Ingrese otro nombre de usuario:",
					"Ingreso de usuario", JOptionPane.PLAIN_MESSAGE);
		}

		return JOptionPane.showInputDialog(ventana, "Ingrese un nombre de usuario:", "Ingreso de usuario",
				JOptionPane.PLAIN_MESSAGE);
	}

	// HILO ESCRIBIR MENSAJE
	private void hiloEscribirMensaje() {
		textFieldEscribir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String mensaje = textFieldEscribir.getText();

				if (!mensaje.isBlank()) {
					cliente.escribirMensaje(cliente.getNombre() + ": " + mensaje);
					textAreaMensajes.append("Yo: " + mensaje + "\n");
					textFieldEscribir.setText("");
					textFieldEscribir.requestFocus();
				}
			}
		});
	}

	// HILO RECIBIR MENSAJE
	@Override
	public void run() {
		boolean nombreRepetido = false;

		try {
			while (!cliente.isSocketCerrado()) {
				String mensaje = cliente.recibirMensaje();

				if (mensaje.startsWith("/INGRESENOMBRE")) {
					String nombre = leerNombre(nombreRepetido);

					while (nombre == null || nombre.isEmpty()) {
						nombre = leerNombre(nombreRepetido);
					}
					
					nombreRepetido = true;
					cliente.escribirMensaje(nombre);
					cliente.setNombre(nombre);
				} else if (mensaje.toLowerCase().contains("/salir")) {
					cliente.cerrarSocket();
					textAreaMensajes.append(mensaje + "\n");
					return;
				} else {
					textAreaMensajes.append(mensaje + "\n");
				}
			}
		} catch (NoSuchElementException e) {
		} finally {
			System.out.println("Saliste del chat.");
			ventana.setVisible(false);
			ventana.dispose();
		}
	}

	private JFrame getVentana() {
		return ventana;
	}

}
