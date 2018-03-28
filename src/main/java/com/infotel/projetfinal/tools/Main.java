package com.infotel.projetfinal.tools;

import java.util.List;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		List<Book> books = new BookImporter().importBook();
		
		new BookExporter().exportBook(books);
	}

}
