package it.valsecchi.polypserver;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.charset.*;

/**
 * Classe che fornisce metodi di utilità: il calcolo dell'hash, la generazione
 * di ID casuali.
 * 
 * @author Davide Valsecchi
 * @version 1.0
 * 
 */
public class Utility {

	/** Oggetto statico LogManager che fornisce funzionalità di log */
	public static LogManager Log = new LogManager();

	/**
	 * Metodo che cambia il flusso di output dell'oggetto di log. Se la path
	 * passata non è valida non viene cambiato l'output e non vengono lanciate
	 * eccezioni.
	 * 
	 * @param path
	 *            percorso in cui salvare il log
	 */
	public static void changeLogOut(String path) {
		try {
			Log = new LogManager(path);
		} catch (FileNotFoundException e) {
		}
	}

	/**
	 * Metodo che restituisce l'hash in formato di stringa esadecimale di una
	 * stringa.
	 * 
	 * @param stringa
	 *            stringa della quale ricavare l'hash
	 * @return ritorna l'hash di stringa in formato testuale esadecimale di 32
	 *         caratteri
	 */
	public static String getHash(String stringa) {
		// si ricavano i bytes dell'hash
		byte[] bytes = Utility.getHashByte(stringa);
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)

			str.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100)
					.substring(1, 3));

		return str.toString();
	}

	/**
	 * Metodo che restituisce l'hash in formato di array di bytes di una stringa
	 * 
	 * @param stringa
	 *            stringa della quale ricavare l'hash
	 * @return ritorna l'hash come array di bytes di 16 bytes
	 */
	public static byte[] getHashByte(String stringa) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		if (Charset.isSupported("CP1252"))

			md.update(stringa.getBytes(Charset.forName("CP1252")));
		else
			md.update(stringa.getBytes(Charset.forName("ISO-8859-1")));

		byte[] bytes = md.digest();
		return bytes;
	}

	/**
	 * Metodo che restituisce l'hash in formato di array di bytes di una stringa
	 * 
	 * @param bytes
	 *            bytes dei quali ricavare l'hash
	 * @return ritorna l'hash come array di bytes di 16 bytes
	 */
	public static byte[] getHashByte(byte[] bytes) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		md.update(bytes);
		return md.digest();
	}

	/**
	 * Metodo che restituisce l'hash di una stringa come array di bytes di 16
	 * bytes ricalcolando l'hash per un numero di volte pari a ripetizioni.
	 * 
	 * @param stringa
	 *            stringa di cui ricavare l'hash
	 * @param ripetizioni
	 *            numero di volte per il quale si ricalcola l'hash
	 * @return ritorna un array di bytes di 16 bytes che rappresenta l'hash di
	 *         stringa
	 */
	public static byte[] getHash(String stringa, int ripetizioni) {
		byte[] hash = new byte[16];
		hash = Utility.getHashByte(stringa);
		// si ripete l'hash per n-ripetizioni
		for (int i = 0; i < ripetizioni - 1; i++) {
			hash = Utility.getHashByte(hash);
		}
		return hash;
	}

	/**
	 * Metodo che genera un ID di 4 quattro byte casuale e lo restituisce come
	 * stringa esadecimale, quindi di 8 caratteri.
	 * 
	 * @return restituisce un ID testuale esadecimale di 8 caratteri.
	 */
	public static String generateID() {
		Random rnd = new Random();
		byte[] bytes = new byte[4];
		rnd.nextBytes(bytes);
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)

			str.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100)
					.substring(1, 3));

		return str.toString().toUpperCase();
	}

	/**
	 * Metodo che confronta due Calendar. Restituisce true solo se essi rappresentano lo stesso giorno.
	 * @param d1
	 * @param d2
	 * @return  Restituisce true solo se essi rappresentano lo stesso giorno.
	 */
	public static boolean equalsDate(Calendar d1, Calendar d2) {
		if (d1.get(Calendar.YEAR) == d2.get(Calendar.YEAR)) {
			if (d1.get(Calendar.DAY_OF_YEAR) == d2.get(Calendar.DAY_OF_YEAR)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Metodo che formatta un Calendar in una stringa nel formato "dd/MM/yy".
	 * @param c Calendar da formattare
	 * @return restituisce una stringa nel formato "dd/MM/yy"
	 */
	public static String formatCalendarToString(Calendar c){
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		return formatter.format(c.getTime());
	}
	
	/**
	 * Metodo che formatta una String nel formato "dd/MM/yy" in un Calendar
	 * @param string_data Stringa da formattare
	 * @return restituisce il Calendar corrispondente
	 * @throws ParseException eccezione lanciata se la String non è nel formato "dd/MM/yy"
	 */
	public static Calendar parseStringToCalendar(String string_data) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		Date d = formatter.parse(string_data);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c;
	}
	
	/**
	 * Classe che fornisce i metodi per creare un log utile per il test e il
	 * debug
	 * 
	 * @author Davide Valsecchi
	 * @version 1.0
	 * 
	 */
	public static class LogManager {

		private PrintStream writer;
		private SimpleDateFormat dateFormat = new SimpleDateFormat(
				"HH:mm:ss dd/MM/yyyy");

		/**
		 * Il costruttore predefinito imposta lo stream di uscita su quello
		 * predefinito a console
		 */
		public LogManager() {
			writer = System.out;
		}

		/**
		 * Costruttore che imposta lo stream di uscita sul file passato come
		 * parametro.
		 * 
		 * @param path
		 *            path del file su cui scrivere il log.
		 * @throws FileNotFoundException
		 */
		public LogManager(String path) throws FileNotFoundException {
			writer = new PrintStream(path);
		}

		/**
		 * Metodo che scrive nel log un messaggio di debug
		 * 
		 * @param string
		 *            messaggio da scrivere
		 */
		public void debug(String string) {
			// scrive un messaggio contrassegnato come debug
			string = "DEBUG   --- "
					+ dateFormat.format(GregorianCalendar.getInstance()
							.getTime()) + " >>> " + string;
			// si scrive nel flusso
			writer.println(string);
			writer.flush();
		}

		/**
		 * Metodo che scrive nel log un messaggio di info
		 * 
		 * @param string
		 *            messaggio da scrivere
		 */
		public void info(String string) {
			// scrive un messaggio contrassegnato come info
			string = "INFO    --- "
					+ dateFormat.format(GregorianCalendar.getInstance()
							.getTime()) + " >>> " + string;
			// si scrive nel flusso
			writer.println(string);
			writer.flush();
		}

		/**
		 * Metodo che scrive nel log un messaggio di warning
		 * 
		 * @param string
		 *            Messaggio da scrivere
		 */
		public void warning(String string) {
			// scrive un messaggio contrassegnato come warning
			string = "WARNING --- "
					+ dateFormat.format(GregorianCalendar.getInstance()
							.getTime()) + " >>> " + string;
			// si scrive nel flusso
			writer.println(string);
			writer.flush();
		}

		/**
		 * Metodo che scrive nel log un messaggio di error
		 * 
		 * @param string
		 *            Messaggio da scrivere
		 */
		public void error(String string) {
			// scrive un messaggio contrassegnato come error
			string = "ERROR   --- "
					+ dateFormat.format(GregorianCalendar.getInstance()
							.getTime()) + " >>> " + string;
			// si scrive nel flusso
			writer.println(string);
			writer.flush();
		}
	}

}
