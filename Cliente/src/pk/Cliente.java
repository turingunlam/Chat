package pk;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
	private Socket cliente;
	private static final String IP = "192.168.0.7";
	private String nombre;
	private PrintWriter out;
	private Scanner in;

	public Cliente(int puerto) throws UnknownHostException, IOException {
		cliente = new Socket(IP, puerto);
		out = new PrintWriter(cliente.getOutputStream(), true);
		in = new Scanner(cliente.getInputStream());
	}

	public String getNombre() {
		return nombre;
	}

	public void escribirMensaje(String mensaje) {
		out.println(mensaje);
	}

	public String recibirMensaje() {
		return in.nextLine();
	}

	public void cerrarSocket() {
		try {
			cliente.close();
		} catch (IOException e) {
			System.out.println("No se pudo cerrar el socket.");
		}
	}

	public boolean isSocketCerrado() {
		return cliente.isClosed();
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
