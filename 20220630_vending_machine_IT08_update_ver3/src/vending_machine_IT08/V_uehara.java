package vending_machine_IT08;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class V_uehara {

	public static Random rand = new Random();

	public static boolean Debugging = false;

	public static final String DATABASE_NAME = "vending_machine";
	public static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME;
	public static final String USER = "root";
	public static String PASS = "";
	public static final String coin_table_name = "change_table";
	public static final String product_table_name = "product_table";

	public static String change_result = "";
	public static String purchase_result = "";
	public static String roulette_result = "";
	public static int roulett_num = 0;
	public static boolean hit;
	public static int m_total = 0;

	public static int refulesh_m_total(int pattern, int total) {
		if (pattern != 1)
			total = 0;
		return total;
	}

	public static int[] refulesh_m_memory(int pattern, int[] memory) {
		if (pattern != 1) {
			for (int i = 0; i < memory.length; i++)
				memory[i] = 0;
		}
		return memory;
	}

	public static int kounyuu(int total, int[] memory) throws IOException {

		hit = false;
		m_total = 0;

		String[] start_messege = new String[6];
		start_messege[0] = "商品IDを入力してください。";
		start_messege[1] = "-1を入力すると投入画面に戻ります。";

		String[] END1_messege = new String[6];
		END1_messege[0] = "金額を入力してください。";

		String[] END2_messege = new String[6];
		END2_messege[4] = "金額を入力してください。";

		String[] END3_messege = new String[6];
		END3_messege[1] = "金額を入力してください。";

		String[] reent1_messege = new String[6];
		reent1_messege[5] = "その商品IDは存在しません。";
		reent1_messege[0] = "もう一度入力してください。";

		String[] reent2_messege = new String[6];

		String[] bonus_messege = new String[6];
		bonus_messege[4] = "商品IDを入力してください。";
		// System.out.print(">");

		// Start画面
		V_takaishi.display_main(total, start_messege);

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			String str = br.readLine();
			int cs = 1;
			if (V_aizawa.p_id_research(str) == 0) {
				cs = check_state(str, total);
			}

			if (cs == 1)
				reent2_messege[5] = "投入金額が不足しています。";
			if (cs == 2)
				reent2_messege[5] = "その商品は売り切れです。";
			reent2_messege[0] = "もう一度入力してください。";

			String out = "-1";
			if (str.equals(out)) {
				V_takaishi.display_main(total, END1_messege);
				return 1;
			} else if (V_aizawa.p_id_research(str) != 0) {
				V_takaishi.display_main(total, reent1_messege);
			} else if (cs != 0) {
				V_takaishi.display_main(total, reent2_messege);
			} else if (hit == true) {
				int[] a = new int[memory.length];

				for (int i = 0; i < memory.length; i++) {
					a[i] = 0;
				}

				purchase(str, total, a);

				END3_messege[0] = purchase_result;

				refulesh_DB();
				V_takaishi.display_main(0, END3_messege);

				return 3;
			} else {
				purchase(str, total, memory);
				cul_change(memory);

				roulette();

				if (Debugging == true)
					hit = true;

				if (hit == false) {
					END2_messege[0] = purchase_result;
					END2_messege[1] = change_result;
					END2_messege[2] = "ルーレット：" + String.valueOf(roulett_num);
					END2_messege[3] = roulette_result;
					refulesh_DB();
					V_takaishi.display_main(0, END2_messege);

					return 2;

				} else {
					bonus_messege[0] = purchase_result;
					bonus_messege[1] = change_result;
					bonus_messege[2] = String.valueOf(roulett_num);
					bonus_messege[3] = roulette_result;

					V_takaishi.display_main(0, bonus_messege);
				}
			}
			// System.out.print(">");
		}

	}

	public static void refulesh_DB() {
		Connection con = null;
		PreparedStatement stmt1_take_data = null;
		ResultSet rs = null;

		PreparedStatement stmt2_updete = null;

		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
			// 情報を持ってくるSQLの作成
			String sql_take_data = "SELECT * FROM " + product_table_name;

			con = DriverManager.getConnection(URL, USER, PASS);
			con.setAutoCommit(false);

			stmt1_take_data = con.prepareStatement(sql_take_data);
			rs = stmt1_take_data.executeQuery();

			while (rs.next()) {

				String id = rs.getString("p_id");
				int stock = rs.getInt("p_stock");
				stmt2_updete = con
						.prepareStatement("UPDATE " + product_table_name + " SET p_yn='購入不可' " + " WHERE p_id=" + id);

				stmt2_updete.executeUpdate();
				con.commit();

				if (stock == 0) {
					stmt2_updete = con.prepareStatement(
							"UPDATE " + product_table_name + " SET p_yn='売り切れ' " + " WHERE p_id=" + id);

					stmt2_updete.executeUpdate();
					con.commit();
				}

			}

		} catch (ClassNotFoundException e) {
			System.out.print("JDBCドライバのロードでエラーが発生しました");
		} catch (SQLException e) {
			System.out.println("データベースへのアクセスでエラーが発生しました。1");
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				System.out.println("データベースへのアクセスでエラーが発生しました。2");
			}
		}
	}

	public static int check_state(String str, int total) {

		int result = 0;

		Connection con = null;
		PreparedStatement stmt1_takedata = null;
		ResultSet rs = null;

		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
			// 情報を持ってくるSQLの作成
			String sql_take = "SELECT * FROM " + product_table_name + " WHERE p_id=" + str;

			con = DriverManager.getConnection(URL, USER, PASS);
			con.setAutoCommit(false);

			stmt1_takedata = con.prepareStatement(sql_take);
			rs = stmt1_takedata.executeQuery();

			while (rs.next()) {

				int stock = rs.getInt("p_stock");

				int price = rs.getInt("p_price");

				if (price > total)
					result = 1;
				if (stock == 0)
					result = 2;

			}

		} catch (ClassNotFoundException e) {
			System.out.print("JDBCドライバのロードでエラーが発生しました");
		} catch (SQLException e) {
			System.out.println("データベースへのアクセスでエラーが発生しました。1");
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				System.out.println("データベースへのアクセスでエラーが発生しました。2");
			}
		}

		return result;

	}

	public static void roulette() {
		roulett_num = rand.nextInt(10000);

		if (roulett_num % 1111 == 0) {
			roulette_result = "あたりです! 商品を一つ選択してください";
			hit = true;
		} else {
			roulette_result = "はずれです";
			hit = false;
		}
	}

	public static void purchase(String str, int total, int[] memory) {
		Connection con = null;
		PreparedStatement stmt1_takedata = null;
		ResultSet rs = null;

		PreparedStatement stmt2_updete = null;

		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
			// 情報を持ってくるSQLの作成
			String sql_disadd = "SELECT * FROM " + product_table_name + " WHERE p_id=" + str;

			con = DriverManager.getConnection(URL, USER, PASS);
			con.setAutoCommit(false);

			stmt1_takedata = con.prepareStatement(sql_disadd);
			rs = stmt1_takedata.executeQuery();

			while (rs.next()) {

				int sale = rs.getInt("p_sale");
				int stock = rs.getInt("p_stock");

				int price = rs.getInt("p_price");

				String name = rs.getString("p_name");

				purchase_result = name + "を購入しました。";
				m_total = total - price;
				stmt2_updete = con.prepareStatement("UPDATE " + product_table_name + " SET p_stock="
						+ String.valueOf(stock - 1) + " WHERE p_id=" + str);
				stmt2_updete.executeUpdate();
				stmt2_updete = con.prepareStatement("UPDATE " + product_table_name + " SET p_sale="
						+ String.valueOf(sale + 1) + " WHERE p_id=" + str);
				stmt2_updete.executeUpdate();
				con.commit();

			}

		} catch (ClassNotFoundException e) {
			System.out.print("JDBCドライバのロードでエラーが発生しました");
		} catch (SQLException e) {
			System.out.println("データベースへのアクセスでエラーが発生しました。1");
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				System.out.println("データベースへのアクセスでエラーが発生しました。2");
			}
		}
	}

	public static int rt[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static void cul_change(int[] memory) {
		change_result = "";

		int total = m_total;

		Connection con = null;
		PreparedStatement stmt1_getdata = null;
		ResultSet rs = null;

		PreparedStatement stmt2_updete = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			// 情報を持ってくるSQLの作成
			String sql_n_selent = "SELECT * FROM " + coin_table_name + " ORDER BY c_price DESC";
			con = DriverManager.getConnection(URL, USER, PASS);
			con.setAutoCommit(false);

			stmt1_getdata = con.prepareStatement(sql_n_selent);
			rs = stmt1_getdata.executeQuery();

			if (hit != true) {
				while (rs.next()) {
					int id = rs.getInt("c_id");
					int stock = rs.getInt("c_stock");

					stmt2_updete = con.prepareStatement("UPDATE " + coin_table_name + " SET c_stock="
							+ String.valueOf(stock + memory[id - 1]) + " WHERE c_id=" + String.valueOf(id));
					stmt2_updete.executeUpdate();
					con.commit();
				}
			}

			stmt1_getdata = con.prepareStatement(sql_n_selent);
			rs = stmt1_getdata.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("c_id");
				int price = rs.getInt("c_price");
				int stock = rs.getInt("c_stock");

				// System.out.println("2回目："+price+"-->"+stock);

				rt[id] = total / price;
				total %= price;
				if (rt[id] > stock) {
					total += (rt[id] - stock) * price;
					rt[id] = stock;
				}

				stmt2_updete = con.prepareStatement("UPDATE " + coin_table_name + " SET c_stock="
						+ String.valueOf(stock - rt[id]) + " WHERE c_id=" + String.valueOf(id));
				stmt2_updete.executeUpdate();
				con.commit();
				if (rt[id] != 0) {
					if ((price != 1000) && (price != 2000) && (price != 5000) && (price != 10000))
						change_result += String.valueOf(price) + "円硬貨を" + String.valueOf(rt[id]) + "枚,";
					else
						change_result += String.valueOf(price) + "円札を" + String.valueOf(rt[id]) + "枚,";
				}
			}

		} catch (ClassNotFoundException e) {
			System.out.print("JDBCドライバのロードでエラーが発生しました");
		} catch (SQLException e) {
			System.out.println("データベースへのアクセスでエラーが発生しました。1");
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				System.out.println("データベースへのアクセスでエラーが発生しました。2");
			}
		}

		change_result += "返却します。";
		if (m_total == 0) {
			change_result = "お釣りはありません。";
		}
	}

}
