package com.infotel.projetfinal.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookImporter {

	public List<Book> importBook() throws Exception{
		
		List<Book> books = new ArrayList<Book>();
		String sql = "Select book_id, title, description, price, popularBook, bookImage from book";
		try(
				Connection cn = getConnexion();
				PreparedStatement ps = cn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				) {
			while(rs.next()) {
				Book b = new Book();
				b.setId(rs.getInt(1));
				b.setTitle(rs.getString(2));
				b.setDescription(rs.getString(3));
				b.setPrice(rs.getDouble(4));
				b.setPopularBook(rs.getBoolean(5));
				b.setBookImage(rs.getString(6));
				books.add(b);
				
				System.out.println(b);
			}
			
		}
		return books;
	}
	
	private Connection getConnexion() throws Exception{
		
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/projetfinal";
		Connection cn = DriverManager.getConnection(url, "root","");
		return cn;
	}
}
