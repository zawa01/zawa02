package vending_machine_IT08;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;



public class V_matsumoto {
	
	//public static void main(String[] args) {}
		
	
	int m_memory[] = { 0, 0, 0, 0, 0 };
	int m_total = 0;
	String message[] = new String[6];
	
	
	
	public static Connection con = null;
	public static PreparedStatement ps = null;
	public static ResultSet rs = null;

	public static PreparedStatement ps2 = null;
	public static ResultSet rs2 = null;

	public static final String SQL1 = "select * from product_table;";

	
	
	void input_money(int money) {

		try {
			
		
			Connection conn = null;
			PreparedStatement stmt = null;
			String url = "jdbc:mysql://localhost:3306/vending_machine?useSSL=false&serverTimezone=Japan";
			String user = "root";
			String password = "";

			message[0] = "投入する金銭を入力してください";
			message[1] = "金銭排出の際は0、入金終了の際は1を入力してください";

			switch (money) {
			case 0:
				message[0] = "金銭排出を行います";
				money = 0;

				this.m_memory[0] = 0;
				this.m_memory[1] = 0;
				this.m_memory[2] = 0;
				this.m_memory[3] = 0;
				this.m_memory[4] = 0;

				break;

			case 1000:

				this.m_memory[0]++;

				break;

			case 500:
				this.m_memory[1]++;
				break;

			case 100:
				this.m_memory[2]++;

				break;

			case 50:
				this.m_memory[3]++;
				break;

			case 10:
				this.m_memory[4]++;
				break;
				
			default:

				message[0] = "使用不可能なため返却します";

			}
			if (money == 1 && this.m_total >= 90) {
				// 90は最低金額
				message[0] = "入金終了です";
				
			} else if (money == 1 && this.m_total < 90) {
				// 90は最低金額
				message[0] = "購入できる商品がありません";
			}
			
			

			this.m_total = this.m_memory[0] * 1000 + this.m_memory[1] * 500 + this.m_memory[2] * 100 + this.m_memory[3] * 50
					+ this.m_memory[4] * 10;

			if (this.m_total >= 1990) {
				message[0] = "使用限度額を超えています";

				switch (money) {

				case 1000:
					this.m_memory[0]--;
					break;

				case 500:
					this.m_memory[1]--;
					break;

				case 100:
					this.m_memory[2]--;
					break;

				case 50:
					this.m_memory[3]--;
					break;
				case 10:
					this.m_memory[4]--;
					break;
				}
				this.m_total = this.m_total - money;
			}
			
			ps = con.prepareStatement(SQL1);
			rs = ps.executeQuery();

			con.setAutoCommit(false);

			while (rs.next()) {
				int price = rs.getInt("p_price");
				int p_id = rs.getInt("p_id");
				price = rs.getInt("p_price");

			
				if (this.m_total < price) {

					ps2 = con.prepareStatement(
							"update " + "product_table" + " set p_yn ='購入不可能'where p_id =" + p_id);
					ps2.executeUpdate();

					con.commit();
				}
			}

			ps = con.prepareStatement(SQL1);
			rs = ps.executeQuery();

			while (rs.next()) {
				int price = rs.getInt("p_price");
				String p_id = rs.getString("p_id");
				price = rs.getInt("p_price");
				if (this.m_total >= price) {
		

					ps2 = con.prepareStatement("update " + "product_table" + " set p_yn ='購入可能'where p_id=" + p_id);
					ps2.executeUpdate();
	
					con.commit();

				}

			}
			
			ps = con.prepareStatement(SQL1);
			rs = ps.executeQuery();

			while (rs.next()) {
				int price = rs.getInt("p_price");
				String p_id = rs.getString("p_id");
				price = rs.getInt("p_price");
				
					con.commit();

			}
			
			

		} catch (InputMismatchException e) {
			message[0] = "数字を入力してください";
		} catch (NumberFormatException e) {
			message[0] = "使用不可能なため返却します";
		}catch (SQLException e) {
			e.printStackTrace();
			message[0] = "error number : 03";
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					message[0] = "error number : 04";
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
					message[0] = "error number : 04";
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
					message[0] = "error number : 03";
				}

	}
		}
	}
}

