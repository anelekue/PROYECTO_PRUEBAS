package main;
import java.awt.Color;
import entidades.*;
import gui.*;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	
	// AJUSTES DE PANTALLA
	final int tamañoOriginalBaldosa = 16; // Una baldosa de 16x16 (una baldosa en la medida en pixeles para crear un
											// personaje, ncp...)
	// Como el tamaño es muy pequeño para las pantallas actuales hay que escalarlo
	final int escala = 4; // De esta manera escalara en x3 los 16 pixeles, para que se vea mas grande

	public final int tamañoBaldosa = tamañoOriginalBaldosa * escala; // Con esto escalamos el tamaño original a 64x64 y es lo
																// que aparecera en pantalla

	// Para ajustar el tamaño de la pantalla tenemos quen saber cuantas baldosas
	// pueden entrar horizontal y verticalmente
	final int maxPantallaColu = 16; // 16 baldosas en la parte de arriba cada una con un numero igual de pixeles
	final int maxPantallaFila = 12; // 12 baldosas en cada fila, lo que hara una pantalla de 16x12 con cada baldosa
									// de 64 pixeles
	// Ahora creamos dos variables para ponerle la altura y la anchura de la
	// pantalla
	final int pantallaAnchuta = maxPantallaColu * tamañoBaldosa; // 1024 pixeles de ancho
	final int pantallaAltura = maxPantallaFila * tamañoBaldosa; // 768 pixeles de alto
	
	private Inventario inventarioJugador;
	// establecemos los fps como variable
	// FPS
	int FPS = 60;

	// Ya tenemos la pantalla creada, pero todos los juegos 2d de aventuras tienen
	// tiempo, hay enemigos que se mueven etc... por eso hay que hacer un reloj (los
	// fps) para establecer como se van a mover las cosas en el jeugo
	// Para eso vamos a utilzar una clase que se llama Thread (hilos)
	// Thread es algo que se puede iniciar y parar, y una vez lo inicias, no parara
	// hasta que lo pares tu
	// Es necesario ya que necesitamos que el juego este continuamente a 60fps todo
	// el rato, tiene que dibujar la pantalla 60 veces cada segundo todo el rato
	Thread gameThread;
	// Para correr esto necesitamos implementar Runnable la clase

	// Una vez creada la clase ManejoTeclado y programar el movimineto AWSD hay que
	// instanciar la clase
	ManejoTeclado tecladoM = new ManejoTeclado();

	// Despues de crear el ManejoTeclado y los controles de movimiento del juego y
	// hacer que lo pueda leer el GamePanel, hay que crear el mapa y el jugador
	//Creo el mapa
	Mapa mapa = new Mapa(1);

	// Creo el jugador
	// TODO Pensar el esquema de herencias de la clase personaje y crear una clase
	// personajeJugable (PJ) que sea la que controla el jugador
	Jugador jugador = new Jugador(this, tecladoM);
	
	private JLabel dialogoJL;
	
	

	// Creamos un constructor de este GamePanel
	public GamePanel() {

		this.setPreferredSize(new Dimension(pantallaAnchuta, pantallaAltura)); // Esto establece el tamaño de esta clase
																				// (JPanel)
		this.setBackground(Color.black); // Para que el fondo de la pantalla sea negro (esto no importa mucho porque no
											// se va a ver)
		this.setDoubleBuffered(true); // si se establece en true, todo el dibujo de este componente se realizará en un
										// búfer de pintura fuera de la pantalla, basicamente lo que hara en mejorar la
										// renderizacion del juego
		// Una vez instaciada la clase ManejoTeclado la añadimos al GamePanel
		this.addKeyListener(tecladoM); // De esta maenera el GamePanel podra reconocer la entrada de teclas
		// Y vamos a añadir esto para que el GamePanel este "centrado" en recibir
		// entrada de teclado
		this.setFocusable(true);
		
        dialogoJL = new JLabel("");
        dialogoJL.setBounds(10, 500, 400, 40); // Ajustar posición y tamaño.
        dialogoJL.setOpaque(true); // Permitir un fondo para el texto.
        dialogoJL.setBackground(Color.BLACK); // Fondo negro.
        dialogoJL.setForeground(Color.WHITE); // Texto blanco.
        dialogoJL.setVisible(false); // Ocultar al inicio.
        add(dialogoJL); // Añadir el JLabel al panel.
	}

	// vamos a crear un nuevo metodo para iniciar el juego
	public void iniciarJuegoHilo() {

		// Vamos a instanciar el gameThread
		gameThread = new Thread(this); // Cuando ponemos this nos referimos a esta clase (GamePanel), basicamente
										// estamos pasando la clase GamePanel a este constructor de hilo para
										// instanciarlo.
		// Ahora vamos a iniciar el hilo
		gameThread.start(); // Esto automaticamente llamara al metodo run() que nos puso auto el Runnable
	}

	// Despues de implementar Runnable automaticamente nos metera este metodo
	// Nada mas iniciemos el gameThread, llamara directamente a este metodo
	@Override
	public void run() {

		// Primero creamos varibales fuera del bucle
		double dibujarIntervalo = 1000000000 / FPS; // Ese numero esta en nanosegundos por lo que es 1seg, esto
													// significa que vamos a dibujar la pantalla cada 0,016seg, por lo
													// que podemos dibujar en la pantalla 60 veces por segundo

		double tiempoDibujadoSiguiente = System.nanoTime() + dibujarIntervalo; // Con esto hacemos que despues del
																				// primer dibujado, haga el siguientte
																				// 0,016 segundos mas tarde

		// En este metodo vamos a crear un gameLoop que va a ser el nucleo de nuestro
		// juego
		while (gameThread != null) { // Esto significa que mientras el hilo exista se va a repetir el proceso que
										// este escrito en este bucle
//			System.out.println("El bucle se esta ejecurando"); //esto lo usamos para comprobar si estamos en el bucle

			// Una vez tenemos el movimiento de la caja y todo, cuando movemos el objeto con
			// la WASD el objeto desaparecera de la pantalla nada mas darle, esto se debe a
			// que los ordenadores modernos son demasiado rapidos y hacen el proceso de
			// moverlo miles de veces por segundo
			// Por eso hay que ponerle una limitacion para que no lo haga tan rapido, como
			// pueden ser 60fps
			// Para esto necesitamos saber la hora que es y tambien el tiempo que pasa desde
			// que hace el metodo update() hasta el repaint()
			// Para saber que hora es utilizamos esto
			// long horaActual = System.nanoTime(); //vamos a utilizar los nanosegundos para
			// comprobar la hora del sistema con exactitud
			// Ahora toca establecer los fps, asi que hacemos una variable
			// Despues de las variables hay que crear otras variables fuera del bucle

			// Ahora vamos a hacer dos cosas en el bucle

			// 1 ACTUALIZACION: actualizar informacion como la posicion del personaje (Si el
			// jugador esta en una cordenada y mueve el personaje hacia abajo ajustar la
			// cordenada)
			update();

			// 2 DIBUJAR: dibuajar la pantalla con la informacion actualizada (Como el
			// jugador ha movido el personaje hacia abajo hay que dibujarlo en la nueva
			// posicion)
			repaint(); // Es confuso, pero es la unica manera de llamar al metodo paintComponent

			// Ahora mientras el bucle se este ejecutando va a actualizar y repintar el
			// juego

			try {
				// Necesitamos saber cuanto tiempo nos queda despues de que se actualize y
				// repinte el juego, por lo que vamos a comprobarlo
				double tiempoRestante = tiempoDibujadoSiguiente - System.nanoTime();
				// Una putada es que hemos calculado todo en nanosegundos para mas precision,
				// pero sleep solo detecta milisegundos por lo que hay que transformarlo
				tiempoRestante = tiempoRestante / 1000000;

				// Creamos un if para que si la actu y el repin tardaron mas que el Intervalo de
				// dibujado no quede tiempo, el hilo no necesita dormir, por eso le ponemos 0
				if (tiempoRestante < 0) {
					tiempoRestante = 0;
				}

				// Como sobra tiempo, hay que hacer que el hilo "descanse", basicamente que no
				// haga nada hasta que pasen los 0,016 segundos
				Thread.sleep((long) tiempoRestante); // Sleep pausa el GameLoop, por lo que no hara nada hasta que el
														// tiempo restante haya terminado

				// Cuando se despierta el hilo, vamos a establecer un nuevo
				// tiempoDibujadoSiguiente
				tiempoDibujadoSiguiente += dibujarIntervalo; // 0.016 segundos despues del dibujado hara el siguiente

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// Para hacer las dos cosas dentro del bucle tenemos que crear dos metodos
	public void update() {
		// El personaje tiene una función movimiento a la que llamamos ahora
		jugador.movimiento(tecladoM, mapa, tamañoBaldosa);
		
		//controla si el inventario se puede abrir o no, para que no se abran mas de un inventario (controla que también se le haya dado a la I para abrirlo)
		if(tecladoM.iPulsado && !tecladoM.abrirInventario) {
			tecladoM.abrirInventario = true;
			pararMovimiento();
			inventarioJugador = new Inventario(tecladoM);
			//Se para el update mientras el inventario está abierto, el cerrar el inventario con la tecla ESC está dentro de Inventario.java
		}
		//La idea con este código es que el botón esc sea una pausa real en el juego.
		//De esta manera, cuando implementemos enemigos también se pararán
		if(tecladoM.escPulsado && !tecladoM.abrirPausa) {
			pararMovimiento();
			tecladoM.escPulsado  = false;
			tecladoM.abrirPausa = true;

			MenuPausa pausa = new MenuPausa();
			while (pausa.isOpen()==true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			tecladoM.abrirPausa = false;
		}
		
		//Después comprobamos si el personaje ha cambiado de celda
		mapa.detectarCambio(jugador);

	}

	// Esto es un metodo ya establecido en java en el JPanel
	public void paintComponent(Graphics g) { // Graphics es una clase que tiene muchas funciones para pintar objetos en
												// pantalla

		super.paintComponent(g); // Esto es un formato que tiene java, cada vez que usemos el painComponent hay
									// que poner esto
		// Vamos a usar este metodo para dibujar y la clase grafico
		// La clase de Graphics va a ser como nuestro lapiz para dibujar
		// Pero antes de usarlo hay que covertirlo en 2D

		Graphics2D g2 = (Graphics2D) g; // La clase Graphics2D extiende de la clase Graphics para proporcionar un
										// control mas sofisticado sobre la geometria, transformacion de coordenadas,
										// manejo del color y el layout de los textos
		
		// Vamos a probar dibujando un cuadrado como personaje en la pantalla
		// Este metodo dibuja un triangulo con el color que has puesto antes y puedes
		// ponerle el tamaño que quieras
		//Antes de dibujar al personaje, dibujamos el mapa para que el personaje siempre se pinte encima
		mapa.dibujarCelda(g2, tamañoBaldosa);
		jugador.dibujarPer(g2);
		jugador.dibujarVidas(g2);
		
		g2.dispose(); // Esto sirve para ahorrar memoria en el dibujado
	}
	//metodo que para todo el movimiento, de esta manera "pausando" el juego
	public void pararMovimiento() {
		tecladoM.abajoPulsado = false;
		tecladoM.derechaPulsado = false;
		tecladoM.izquierdaPulsado = false;
		tecladoM.arribaPulsado = false;
		
	}
	
	public void mostrarDialogo(String dialogo) {
		dialogoJL.setText(dialogo);
		dialogoJL.setVisible(true);
	}
	
	public void ocultarDialogo() {
		dialogoJL.setVisible(false);
		dialogoJL.setText("");
		
	}

	
}
