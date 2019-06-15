package pk;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class HiloDistribuirMensaje extends Thread {
	private Socket cliente;
	private ArrayList<Socket> clientes;
	private String nombreCliente;
	private HashSet<String> nombresUsuarios;
	PrintWriter out;
	Scanner in;

	public HiloDistribuirMensaje(Socket cliente, ArrayList<Socket> clientes, HashSet<String> nombresUsuario) {
		this.cliente = cliente;
		this.clientes = clientes;
		this.nombresUsuarios = nombresUsuario;

		try {
			out = new PrintWriter(cliente.getOutputStream(), true);
			in = new Scanner(cliente.getInputStream());
		} catch (IOException e) {
			System.out.println("Error al conectarse con la entrada/salida del cliente: " + e);
		}
	}

	@Override
	public void run() {
		String nombreUsuario = null;

		while (true) {
			out.println("/INGRESENOMBRE");
			nombreUsuario = in.nextLine();

			if (!nombresUsuarios.contains(nombreUsuario.toLowerCase())) {
				nombresUsuarios.add(nombreUsuario.toLowerCase());
				break;
			}
		}

		distribuirMensaje(nombreUsuario + " ha ingresado al chat.");
		
		while (!cliente.isClosed()) {
			String mensaje = null;

			try {
				// LEE EL INPUT DEL CLIENTE
				mensaje = in.nextLine();

				// SI ES /salir, ENVIA UN MENSAJE PARA CERRAR EL CLIENTE Y LO COMUNICA AL RESTO
				if (mensaje.toLowerCase().contains("/salir")) {
					System.out.println(nombreUsuario + " ha salido.");
					distribuirMensaje(nombreUsuario + " ha salido del chat.");

					clientes.remove(cliente);
					nombresUsuarios.remove(nombreUsuario);

					out.println("/salir");

					return;
				}

				// ENVIA EL MENSAJE AL RESTO DE CLIENTES
				distribuirMensaje(mensaje);
			} catch (NoSuchElementException e) {
				System.out.println(nombreCliente + " ha salido.");
			}
		}
	}

	private void distribuirMensaje(String mensaje) {
		PrintWriter output;
		for (Socket cliente : clientes) {
			if (cliente != this.cliente) {
				try {
					output = new PrintWriter(cliente.getOutputStream(), true);
					output.println(mensaje);
				} catch (IOException e) {
					System.out.println("Problema al mandar mensajes: " + e);
				}
			}
		}
	}
}
