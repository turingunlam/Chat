package pk;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Servidor {
	private static ArrayList<Socket> clientes = new ArrayList<Socket>();
	private HashSet<String> nombresUsuario = new HashSet<String>();
	
	public Servidor(int puerto) {
		ServerSocket servidor = null;
		try {
			servidor = new ServerSocket(puerto);

			for (int i = 0; i < 10000; i++) {
				Socket clienteSocket = null;
				try {
					clienteSocket = servidor.accept();
					System.out.println("Se ha conectado en cliente nro " + i + " exitosamente");
					clientes.add(clienteSocket);
					
					// HILO QUE LEE LOS MENSAJES ENVIADOS Y LOS DISTRIBUYE AL RESTO DE CLIENTES
					HiloDistribuirMensaje hiloCliente = new HiloDistribuirMensaje(clienteSocket, clientes, nombresUsuario);
					hiloCliente.start();
				} catch (IOException e) {
					System.out.println("Error en la conexión con el cliente nro " + i + ": " + e);
				}
			}
			
			cerrarClientes();
			servidor.close();
			System.out.println("Server cerrado.");
		} catch (IOException e) {
			System.out.println("Error en la creación del servidor: " + e);
		}

	}

	private void cerrarClientes() {
		for (Socket cliente : clientes) {
			try {
				cliente.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Servidor server = new Servidor(500);
	}
}
