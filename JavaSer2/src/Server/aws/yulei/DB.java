package Server.aws.yulei;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;




public class DB {
	public static String dbName = "base1"; 
	public static String userName = "user1";
	public static String password = "90289028";
	public static String hostname = "example3.cpfleo5mk9b3.us-east-1.rds.amazonaws.com";
	public static String port = "3306";
	static String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName +
			"?user=" + userName + "&password=" + password;
	static String connectionString = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName;
	public static Connection connection ;
	public static Statement command;
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		
		Class.forName("com.mysql.jdbc.Driver");
		
		
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();
		
		//command.execute("CREATE TABLE Test (Name VARCHAR(20), Content VARCHAR(10000), Latitude VARCHAR(30), Longtitude VARCHAR(30), Time VARCHAR(50), Keyword VARCHAR(30))");
		//command.execute("INSERT INTO Test (Name, Content, Latitude, Longtitude, Time, Keyword) VALUES ('Rome', 'asdfasdfs', 'adsfaswdf', 'asdfasfwaFDA', '12.30','love')");
		//command.execute("DROP TABLE Test");
		System.out.print("OK");
		 // createTable("table2");
		  insertTableWithValues("table2","'test1'","'test1'","'test1'","'test1'","'test1'","'test1'");
		  System.out.println(getValues());
	      /*ResultSet result = command.executeQuery("Select * FROM "+"table2");
	      
	      while(result.next()){
				
				
				System.out.print(result.getString(1));
				System.out.print(result.getString(2));
				System.out.print(result.getString(3));
				System.out.print(result.getString(4));
				System.out.println(result.getString(5));
				System.out.println(result.getString(6));
					
	      }    */
	      
	      //System.out.println(getNameArray());
	      //System.out.println(getNameArray()); 
	        connection.close();  
	}
	
	public static ArrayList<String> getNameArray() throws SQLException, ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");
		ArrayList<String> resultArray = new ArrayList<String>();
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();

	      ResultSet result = command.executeQuery("Select * FROM Test");	      
	      while(result.next()){
				String s = result.getString(1);			
				resultArray.add(s);
				}
	      return resultArray;
		
		
	}
	
	public static ArrayList<String> getLongtitude () throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		ArrayList<String> resultArray = new ArrayList<String>();
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();

	      ResultSet result = command.executeQuery("Select * FROM Test");	      
	      while(result.next()){
				String s = result.getString(4);			
				resultArray.add(s);
				}
	      return resultArray;						
	}
	
	
	public static ArrayList<String> getLatitude() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		ArrayList<String> resultArray = new ArrayList<String>();
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();

	      ResultSet result = command.executeQuery("Select * FROM Test");	      
	      while(result.next()){
				String s = result.getString(3);			
				resultArray.add(s);
				}
	      return resultArray;						
	}

	public static ArrayList<String> getContent() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		ArrayList<String> resultArray = new ArrayList<String>();
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();

	      ResultSet result = command.executeQuery("Select * FROM Test");	      
	      while(result.next()){
				String s = result.getString(2);			
				resultArray.add(s);
				}
	      return resultArray;						
	}
	
	public static ArrayList<String> getTimeArray() throws SQLException, ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");
		ArrayList<String> resultArray = new ArrayList<String>();
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();

	      ResultSet result = command.executeQuery("Select * FROM Test");	      
	      while(result.next()){
				String s = result.getString(5);			
				resultArray.add(s);
				}
	      return resultArray;
		
		
	}
	public static ArrayList<String> getKeywordArray() throws SQLException, ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");
		ArrayList<String> resultArray = new ArrayList<String>();
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();

	      ResultSet result = command.executeQuery("Select * FROM Test");	      
	      while(result.next()){
				String s = result.getString(6);			
				resultArray.add(s);
				}
	      return resultArray;
		
		
	}
	
	public static void createTable(String tableName) throws SQLException{
		command.execute("CREATE TABLE "+tableName+" (Name VARCHAR(20), Content VARCHAR(10000), Latitude VARCHAR(30), Longtitude VARCHAR(30), Time VARCHAR(50), Keyword VARCHAR(30))");
		
	}
	
	public static void insertTableWithValues(String tableName, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6) throws SQLException{
		command.execute("INSERT INTO "+tableName+" (Name, Content, Latitude, Longtitude, Time, Keyword) VALUES ("+arg1+", "+arg2+", "+arg3+", "+arg4+", "+arg5+", "+arg6+")");
		
		
	}
	
	public static ArrayList<String> getValues() throws SQLException{
      ResultSet result = command.executeQuery("Select * FROM "+"table2");
	  ArrayList<String> resultArray = new ArrayList<>();
	      while(result.next()){
				
	    	  resultArray.add(result.getString(1)+result.getString(2)+result.getString(3)+result.getString(4)+result.getString(5)+result.getString(6));
					
	      }  
	      return resultArray;
	}
	
	public static void DBConnect() throws ClassNotFoundException, SQLException{
	Class.forName("com.mysql.jdbc.Driver");
		
		
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();
	}
	


	public static ArrayList<String> getNameArray(String key) throws SQLException, ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");
		ArrayList<String> resultArray = new ArrayList<String>();
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();

	      ResultSet result = command.executeQuery("Select * FROM Test WHERE Content LIKE '%" + key +"%'");	      
	      while(result.next()){
				String s = result.getString(1);			
				resultArray.add(s);
				}
	      return resultArray;
		
		
	}
	
	public static ArrayList<String> getLongtitude (String key) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		ArrayList<String> resultArray = new ArrayList<String>();
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();

	      ResultSet result = command.executeQuery("Select * FROM Test WHERE Content LIKE '%" + key +"%'");	      
	      while(result.next()){
				String s = result.getString(4);			
				resultArray.add(s);
				}
	      return resultArray;						
	}
	
	
	public static ArrayList<String> getLatitude(String key) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		ArrayList<String> resultArray = new ArrayList<String>();
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();

	      ResultSet result = command.executeQuery("Select * FROM Test WHERE Content LIKE '%" + key +"%'");	      
	      while(result.next()){
				String s = result.getString(3);			
				resultArray.add(s);
				}
	      return resultArray;						
	}

	public static ArrayList<String> getContent(String key) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		ArrayList<String> resultArray = new ArrayList<String>();
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();

	      ResultSet result = command.executeQuery("Select * FROM Test WHERE Content LIKE '%" + key +"%'");	      
	      while(result.next()){
				String s = result.getString(2);			
				resultArray.add(s);
				}
	      return resultArray;						
	}
	
	public static ArrayList<String> getTimeArray(String key) throws SQLException, ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");
		ArrayList<String> resultArray = new ArrayList<String>();
		connection =DriverManager.getConnection(jdbcUrl);
		command = connection.createStatement();

	      ResultSet result = command.executeQuery("Select * FROM Test WHERE Content LIKE '%" + key +"%'");	      
	      while(result.next()){
				String s = result.getString(5);			
				resultArray.add(s);
				}
	      return resultArray;
		
		
	}  
	
	
}


