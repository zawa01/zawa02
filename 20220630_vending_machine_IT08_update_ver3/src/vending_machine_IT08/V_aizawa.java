package vending_machine_IT08;

import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class V_aizawa {

	public static void main(String[] args) {
	}
	
	//商品追加
	static void p_Add(){
		Connection conn = null;
		PreparedStatement stmt = null;
		String url = "jdbc:mysql://localhost:3306/vending_machine?useSSL=false&serverTimezone=Japan";
		String user = "root";
		String password = "";
		int n=0;
		boolean m=true;
		String p_id,p_add;
		String manage_message[] = new String[6];
		
		do {
			manage_message[0] = "商品番号を入力してください（半角数字のみ、0で終了）";
			V_takaishi.display_manage(manage_message);
			p_id = V_main.scan.nextLine();
			n=p_id_research(p_id);
			if(n==0) {
				m=false;
			}else if(n==1){
				if(Integer.valueOf(p_id)==0)
					break;
				m=true;
				manage_message[5] = "入力された商品番号は存在しません";
				V_takaishi.display_manage(manage_message);
			}else if(n==2){
				m=true;
				manage_message[5] = "入力が適切ではありません";
				V_takaishi.display_manage(manage_message);
			}
		}while(m);
		
		if(Integer.valueOf(p_id)!=0) {
			do {
				manage_message[0] = "追加する在庫数を入力してください（半角数字のみ）";
				V_takaishi.display_manage(manage_message);
				p_add = V_main.scan.nextLine();
				//追加数が正の整数かどうか
				Pattern pattern = Pattern.compile("^[0-9]+$");
		        m = pattern.matcher(p_add).matches();
		        if(!m) {
		        	manage_message[5] = "0か正の整数を半角で入力してください";
					V_takaishi.display_manage(manage_message);
		        }
			}while(!m);
			try {
				  conn = DriverManager.getConnection(url, user, password);
				  String sql = "UPDATE product_table SET p_stock = p_stock +"+ p_add +" WHERE p_id = "+p_id;
				  stmt = conn.prepareStatement(sql);
				  stmt.executeUpdate();
			}catch (SQLException e) {
			}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException e){
				}
			}
			manage_message[0] = "追加完了しました　さらに操作を選択";
			manage_message[1] = "1：在庫追加";
			manage_message[2] = "2：商品追加";
			manage_message[3] = "3：商品削除";
			manage_message[4] = "4：売上初期化";
			manage_message[5] = "5：商品管理終了";
			V_takaishi.display_manage(manage_message);
		}
		//System.out.print("p_add end");
	}
	
	//金銭追加
	static void c_Add(){
		Connection conn = null;
		PreparedStatement stmt = null;
		String url = "jdbc:mysql://localhost:3306/vending_machine?useSSL=false&serverTimezone=Japan";
		String user = "root";
		String password = "";
		int n=0;
		boolean m=true;
		String c_id,c_add;
		String manage_message[] = new String[6];
	    
		do {
			manage_message[0] = "残数を追加する金銭番号を入力してください（半角数字のみ、0で終了）";
			V_takaishi.display_manage(manage_message);
			c_id = V_main.scan.nextLine();
			n=c_id_research(c_id);
			if(n==0) {
				m=false;
			}else if(n==1){
				if(Integer.valueOf(c_id)==0)
					break;
				m=true;
				manage_message[5] = "入力された金銭番号は存在しません";
				V_takaishi.display_manage(manage_message);
			}else if(n==2){
				m=true;
				manage_message[5] = "入力が適切ではありません";
				V_takaishi.display_manage(manage_message);
			}
		}while(m);
		
		if(Integer.valueOf(c_id)!=0) {
			do {
				manage_message[0] = "追加する残数を入力してください（半角数字のみ）";
				V_takaishi.display_manage(manage_message);
				c_add = V_main.scan.nextLine();
				//追加数が正の整数かどうか
				Pattern pattern = Pattern.compile("^[0-9]+$");
		        m = pattern.matcher(c_add).matches();
		        if(!m) {
		        	manage_message[5] = "0か正の整数を半角で入力してください";
					V_takaishi.display_manage(manage_message);
		        }
			}while(!m);
			try {
				  conn = DriverManager.getConnection(url, user, password);
				  String sql = "UPDATE change_table SET c_stock = c_stock +"+ c_add +" WHERE c_id = "+c_id;
				  stmt = conn.prepareStatement(sql);
				  stmt.executeUpdate();
			}catch (SQLException e) {
			}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException e){
				}
			}
			manage_message[0] = "追加完了しました　さらに操作を選択";
			manage_message[1] = "1：釣銭追加";
			manage_message[2] = "2：釣銭取り出し";
			manage_message[3] = "3：釣銭管理終了";
			V_takaishi.display_manage(manage_message);
		}
		//System.out.print("c_add end");
	}
	
	//金銭回収
	static void c_collect(){
		Connection conn = null;
		PreparedStatement stmt = null;
		String url = "jdbc:mysql://localhost:3306/vending_machine?useSSL=false&serverTimezone=Japan";
		String user = "root";
		String password = "";
		int n=0;
		boolean m=true;
		String c_id,c_collect;
		String manage_message[] = new String[6];
	    
		do {
			manage_message[0] = "取り出しする金銭番号を入力してください（半角数字のみ、0で終了）";
			V_takaishi.display_manage(manage_message);
			c_id = V_main.scan.nextLine();
			n=c_id_research(c_id);
			if(n==0) {
				m=false;
			}else if(n==1){
				if(Integer.valueOf(c_id)==0)
					break;
				m=true;
				manage_message[5] = "入力された金銭番号は存在しません";
				V_takaishi.display_manage(manage_message);
			}else if(n==2){
				m=true;
				manage_message[5] = "入力が適切ではありません";
				V_takaishi.display_manage(manage_message);
			}
		}while(m);
		
		if(Integer.valueOf(c_id)!=0) {
			do {
				manage_message[0] = "取り出し数を入力してください（半角数字のみ）";
				V_takaishi.display_manage(manage_message);
				c_collect = V_main.scan.nextLine();
				//回収数が正の整数かどうか
				Pattern pattern = Pattern.compile("^[0-9]+$");
		        m = pattern.matcher(c_collect).matches();
		        if(!m) {
		        	manage_message[5] = "0か正の整数を入力してください";
					V_takaishi.display_manage(manage_message);
		        }
			}while(!m);
			try {
				  conn = DriverManager.getConnection(url, user, password);
				  String sql = "UPDATE change_table SET c_stock = c_stock -"+ c_collect +" WHERE c_id = "+c_id;
				  stmt = conn.prepareStatement(sql);
				  stmt.executeUpdate();
			}catch (SQLException e) {
			}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException e){
				}
			}
			manage_message[0] = "取り出し完了しました　さらに操作を選択";
			manage_message[1] = "1：釣銭追加";
			manage_message[2] = "2：釣銭取り出し";
			manage_message[3] = "3：釣銭管理終了";
			V_takaishi.display_manage(manage_message);
		}
		//System.out.print("c_collect end");
	}
	
	//p_idが商品テーブルに存在するか
	static int p_id_research(String p_id) {
		Connection conn = null;
		PreparedStatement stmt = null;
		String url = "jdbc:mysql://localhost:3306/vending_machine?useSSL=false&serverTimezone=Japan";
		String user = "root";
		String password = "";
		
		boolean m=true;
		Pattern pattern = Pattern.compile("^[0-9]+$");
        m = pattern.matcher(p_id).matches();
        if(!m) {
        	return 2;
        }else {
        	try {
				  conn = DriverManager.getConnection(url, user, password);
				  String sql = "select * from product_table where p_id = "+p_id;
				  stmt = conn.prepareStatement(sql);
				  ResultSet rs = stmt.executeQuery();
				  //count=あったら1、なかったら0
				  int count = 0;

				  while(rs.next()){
					    count++;
					  }
				  rs.close();
				  stmt.close();
				  if (count==0) {
					  return 1;
				  }else {
					  return 0;
				  }
			}catch (SQLException e) {
			}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException e){
				}
			}
        }
		return 0;
	}
	
	//c_idが金銭テーブルに存在するか
	static int c_id_research(String c_id) {
		Connection conn = null;
		PreparedStatement stmt = null;
		String url = "jdbc:mysql://localhost:3306/vending_machine?useSSL=false&serverTimezone=Japan";
		String user = "root";
		String password = "";
		
		boolean m=true;
		Pattern pattern = Pattern.compile("^[0-9]+$");
        m = pattern.matcher(c_id).matches();
        if(!m) {
        	return 2;
        }else {
        	try {
				  conn = DriverManager.getConnection(url, user, password);
				  String sql = "select * from change_table where c_id = "+c_id;
				  stmt = conn.prepareStatement(sql);
				  ResultSet rs = stmt.executeQuery();
				  //count=あったら1、なかったら0
				  int count = 0;

				  while(rs.next()){
					    count++;
					  }
				  rs.close();
				  stmt.close();
				  if (count==0) {
					  return 1;
				  }else {
					  return 0;
				  }
			}catch (SQLException e) {
			}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException e){
				}
			}
        }
		return 0;
	}
}
