package vending_machine_IT08;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;

public class V_takaishi {

	public static String driver = "com.mysql.cj.jdbc.Driver";
	public static String url = "jdbc:mysql://localhost:3306/vending_machine";
	public static String user = "root";
	public static String driver_password = "";

	public static Connection con = null;
	public static PreparedStatement ps = null;
	public static ResultSet rs = null;

	public static final String SQL1 = "select * from product_table;";
	public static final String SQL2 = "select * from change_table;";

	// DBから取得した値の格納用変数
	public static int p_id[] = new int[10];
	public static String p_name[] = new String[10];
	public static int p_price[] = new int[10];
	public static int p_sale[] = new int[10];
	public static int p_stock[] = new int[10];
	public static String p_yn[] = new String[10];
	public static int c_id[] = new int[5];
	public static int c_price[] = new int[5];
	public static int c_stock[] = new int[5];

	// 投入金額表示用変数
	public static int m_total = 0;

	// メッセージ表示用変数
	public static String message[] = new String[6];

	// テスト用(後で消す)
	public static void main(String[] args) {

		display_main(m_total, message);
//		display_manage(message);
//		manage_mode(message);

	}

	/* 以下、参照用関数 */

	// メイン画面を表示する関数
	public static void display_main(int m_total, String message[]) {

		// DBから情報を取得
		load_db();

		// 商品IDが0の要素数をカウント
		int count_0_array = 0;
		for (int i = 0; i < p_id.length; i++) {

			if (p_id[i] == 0)
				count_0_array++;
		}
		int p_id_num = p_id.length - count_0_array;
		if (p_id.length > 10) {
			print("エラー");
		}

		else if (m_total >= 0) {

			// スクリーンクリア(コマンドプロンプト上)
			var cc = new ConsoleControl("cmd", "/c", "cls");
			try {
				cc.cls();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}

			// メイン画面を描画
			frame_1();
			frame_2();
			frame_11("メイン画面");
			frame_2();
			int cp_id_num = 0;
			if (p_id_num > 5) {
				cp_id_num = p_id_num;
				p_id_num = 5;
			}
			frame_main(p_id_num, p_name, p_price, p_yn);
			if (cp_id_num != 0)
				frame_main(cp_id_num, p_name, p_price, p_yn);
			frame_2();
			frame_2();
			frame_welcome();
			frame_2();
			frame_2();
			frame_2();
			frame_7();
			for (int i = 0; i < 3; i++)
				frame_19(message[i]);
			frame_20(message[3]);
			frame_8(message[4]);
			frame_9(m_total, message[5]);
			frame_10();
			frame_2();
			frame_1();
			print("ここに入力->");
		}
	}

	// 管理者用画面を表示する関数
	public static void display_manage(String message[]) {

		// DBから情報を取得
		load_db();

		// 商品IDが0の要素数をカウント
		int count_0_array = 0;
		for (int i = 0; i < p_id.length; i++) {

			if (p_id[i] == 0)
				count_0_array++;
		}

		int p_id_num = p_id.length - count_0_array;

		// スクリーンクリア(コマンドプロンプト上)
		var cc = new ConsoleControl("cmd", "/c", "cls");
		try {
			cc.cls();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		// 管理者用画面を描画
		frame_1();
		frame_2();
		frame_11("管理者用画面");
		frame_2();
		int cp_id_num = p_id_num;
		if (p_id_num > 5)
			p_id_num = 5;
		frame_manage_p(p_id_num, p_name, p_price, p_stock, p_sale, c_stock);
		if (cp_id_num > 5)
			frame_manage_p(cp_id_num, p_name, p_price, p_stock, p_sale, c_stock);
		frame_2();
		frame_14();
		frame_15(cp_id_num, p_price, p_sale);
		frame_14();
		frame_2();
		frame_manage_c(c_stock);
		frame_2();
		frame_17();
		frame_18(message);
		frame_17();
		frame_2();
		frame_1();
		print("ここに入力->");
	}

	// 管理者モード関数
	public static void manage_mode(String manage_message[]) {

		// 管理者モードのループを抜けるための変数
		int end = 0;

		int manage_select = 0;
		manage_message[0] = "何を管理しますか？";
		manage_message[1] = "1：商品管理";
		manage_message[2] = "2：釣銭管理";
		manage_message[3] = "3：管理者モード終了";
		manage_message[4] = "";
		manage_message[5] = "";

		display_manage(manage_message);

		// 管理者モードのループ開始
		while (true) {

			try {
				manage_select = V_main.scan.nextInt();
				manage_message[5] = "";
				switch (manage_select) {
				case 1:
					product_manage(manage_message);
					break;

				case 2:
					change_manage(manage_message);
					break;

				case 3:
					end = 1;
					break;

				default:
					manage_message[5] = "不正な入力です";
					display_manage(manage_message);

				}
			} catch (InputMismatchException ex) {
				manage_message[5] = "不正な入力です";
				display_manage(manage_message);
				V_main.scan.next();
			}

			if (end == 1)
				break;
		}
	}
	
	//DBの商品IDを1から順番に振り直す関数
	public static void renumber_p_id() {

		try {
			try {
				Class.forName(driver);
				con = DriverManager.getConnection(url, user, driver_password);
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			
			ps = con.prepareStatement(SQL1);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				p_id[i] = rs.getInt(1);
				i++;
			}

			for (int j = 0; j < i; j++) {
				ps = con.prepareStatement("update product_table set p_id = ? where p_id = " + p_id[j] + ";");
				ps.setInt(1, j + 1);
				ps.executeUpdate();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		try {
			if (con != null)
				con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	// 画面表示における、文字列のByte数を返す関数
	public static int count_char(String str) {

		int count = 0;
		try {
			count = ((str.length() + str.getBytes("UTF-8").length) / 2);
		} catch (Exception ex) {
			count = 0;
		}
		return count;
	}

	/* 以下、参照用ではないが、メンテナンスが必要になりそうな関数 */

	// DBの情報を取得する関数
	public static void load_db() {

		try {
			try {
				Class.forName(driver);
				con = DriverManager.getConnection(url, user, driver_password);
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			ps = con.prepareStatement(SQL1);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				p_id[i] = rs.getInt(1);
				p_name[i] = rs.getString(2);
				p_price[i] = rs.getInt(3);
				p_sale[i] = rs.getInt(4);
				p_stock[i] = rs.getInt(5);
				p_yn[i] = rs.getString(6);
				i++;
			}
			i = 0;
			ps = con.prepareStatement(SQL2);
			rs = ps.executeQuery();
			while (rs.next()) {
				c_id[i] = rs.getInt(1);
				c_price[i] = rs.getInt(2);
				c_stock[i] = rs.getInt(3);
				i++;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		try {
			if (con != null)
				con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	// manage_mode()内で使う関数(商品管理)
	public static void product_manage(String manage_message[]) {

		int end = 0;
		int manage_select = 0;
		manage_message[0] = "操作を選択";
		manage_message[1] = "1：在庫追加";
		manage_message[2] = "2：商品追加";
		manage_message[3] = "3：商品削除";
		manage_message[4] = "4：売上初期化";
		manage_message[5] = "5：商品管理終了";

		display_manage(manage_message);
		while (true) {
			try {
				manage_select = V_main.scan.nextInt();

				switch (manage_select) {
				case 1:
					 V_aizawa.p_Add();
					break;

				case 2:
					 V_kihara.p_update();
					break;

				case 3:
					 V_kihara.p_delete();
					break;

				case 4:
					 V_kihara.p_reset();
					break;

				case 5:
					end = 1;
					break;

				default:
					manage_message[0] = "不正な入力のため、もう一度操作を選択";
					display_manage(manage_message);

				}
			} catch (InputMismatchException ex) {
				manage_message[0] = "不正な入力のため、もう一度操作を選択";
				display_manage(manage_message);
				V_main.scan.next();
			}

			if (end == 1)
				break;
		}
	}

	// manage_mode()内で使う関数(釣銭管理)
	public static void change_manage(String manage_message[]) {

		int end = 0;
		int manage_select = 0;
		manage_message[0] = "操作を選択";

		manage_message[1] = "1：釣銭追加";
		manage_message[2] = "2：釣銭取り出し";
		manage_message[3] = "3：釣銭管理終了";

		display_manage(manage_message);

		while (true) {

			try {
				manage_select = V_main.scan.nextInt();

				switch (manage_select) {
				case 1:
					 V_aizawa.c_Add();
					break;

				case 2:
					 V_aizawa.c_collect();
					break;

				case 3:
					end = 1;
					break;

				default:
					manage_message[0] = "不正な入力のため、もう一度操作を選択";
					display_manage(manage_message);

				}
			} catch (InputMismatchException ex) {
				manage_message[0] = "不正な入力のため、もう一度操作を選択";
				display_manage(manage_message);
				V_main.scan.next();
			}

			if (end == 1)
				break;
		}
	}

	/* 以下、画面描画用関数 */

	public static void frame_main(int id, String p_name[], int p_price[], String p_yn[]) {
		frame_3(id);
		frame_4(id, " 商品ID ");
		frame_3(id);
		frame_5(id, " 商品名 ", p_name);
		frame_3(id);
		frame_6(id, " 価  格 ", p_price);
		frame_3(id);
		frame_5(id, "        ", p_yn);
		frame_3(id);
	}

	public static void frame_manage_p(int p_id_num, String p_name[], int p_price[], int p_stock[], int p_sale[],
			int c_stock[]) {
		frame_3(p_id_num);
		frame_4(p_id_num, " 商品ID ");
		frame_3(p_id_num);
		frame_5(p_id_num, " 商品名 ", p_name);
		frame_3(p_id_num);
		frame_12(p_id_num, " 在  庫 ", p_stock);
		frame_3(p_id_num);
		frame_12(p_id_num, " 売上数 ", p_sale);
		frame_3(p_id_num);
		frame_13(p_id_num, " 売上額 ", p_price, p_sale);
		frame_3(p_id_num);
	}

	public static void frame_manage_c(int c_stock[]) {
		String c_name[] = { "1000円札", "500円玉", "100円玉", "50円玉", "10円玉" };
		frame_3(5);
		frame_5(5, " 釣  銭 ", c_name);
		frame_3(5);
		frame_16(5, " 残  数 ", c_stock);
		frame_3(5);
	}

	public static void frame_1() {
		print("+");
		for (int i = 0; i < 120; i++)
			print("-");
		print("+\n");
	}

	public static void frame_2() {
		print("|");
		for (int i = 0; i < 120; i++)
			print(" ");
		print("|\n");
	}

	public static void frame_3(int p_id_num) {
		print("|        ");
		if (p_id_num > 0)
			print("+");
		for (int i = (p_id_num / 6) * 5; i < p_id_num; i++)
			print("---------------------+");
		for (int i = 0; i < 111 - (p_id_num - (p_id_num / 6) * 5) * 22; i++)
			print(" ");
		if (p_id_num == 0)
			print(" ");
		print("|\n");
	}

	public static void frame_4(int p_id_num, String str) {
		print("|" + str);
		if (p_id_num > 0)
			print("|");
		if (p_id_num == 10) {
			for (int i = (p_id_num / 6) * 5; i < 9; i++)
				print("          " + p_id[i] + "          |");
			print("         " + 1 + " " + 0 + "         |");
		} else {
			for (int i = (p_id_num / 6) * 5; i < p_id_num; i++)
				print("          " + p_id[i] + "          |");
		}
		for (int i = 0; i < 111 - (p_id_num - (p_id_num / 6) * 5) * 22; i++)
			print(" ");
		if (p_id_num == 0)
			print(" ");
		print("|\n");

	}

	public static void frame_5(int p_id_num, String str, String name[]) {
		print("|" + str);
		if (p_id_num > 0)
			print("|");

		for (int i = (p_id_num / 6) * 5; i < p_id_num; i++) {
			if (name[i] == null)
				name[i] = "";
			for (int j = 0; j < 21 - count_char(name[i]); j++)
				print(" ");
			print(name[i]);
			print("|");

		}

		for (int i = 0; i < 111 - (p_id_num - (p_id_num / 6) * 5) * 22; i++)
			print(" ");
		if (p_id_num == 0)
			print(" ");
		print("|\n");
	}

	public static void frame_6(int p_id_num, String str, int price[]) {
		print("|" + str);
		if (p_id_num > 0)
			print("|");
		for (int i = (p_id_num / 6) * 5; i < p_id_num; i++) {
			System.out.printf("%19d円", price[i]);
			print("|");
		}
		for (int i = 0; i < 111 - (p_id_num - (p_id_num / 6) * 5) * 22; i++)
			print(" ");
		if (p_id_num == 0)
			print(" ");
		print("|\n");

	}

	public static void frame_7() {
		print("|        +");
		for (int i = 0; i < 90; i++)
			print("-");
		print("+");
		for (int i = 0; i < 5; i++)
			print(" ");
		print("        ");
		for (int i = 0; i < 7; i++)
			print(" ");
		print("|\n");
	}

	public static void frame_8(String message) {
		print("|        |");
		if (message == null)
			message = "";
		print(message);
		for (int i = 0; i < 90 - count_char(message); i++)
			print(" ");
		print("|");
		for (int i = 0; i < 4; i++)
			print(" ");
		print("+--------+");
		for (int i = 0; i < 6; i++)
			print(" ");
		print("|\n");
	}

	public static void frame_9(int m_total, String message) {
		print("|        |");
		if (message == null)
			message = "";
		print(message);
		for (int i = 0; i < 90 - count_char(message); i++)
			print(" ");
		print("|");
		for (int i = 0; i < 4; i++)
			print(" ");
		System.out.printf("|%8d|円", m_total);
		for (int i = 0; i < 4; i++)
			print(" ");
		print("|\n");
	}

	public static void frame_10() {
		print("|        +");
		for (int i = 0; i < 90; i++)
			print("-");
		print("+");
		for (int i = 0; i < 4; i++)
			print(" ");
		print("+--------+");
		for (int i = 0; i < 6; i++)
			print(" ");
		print("|\n");
	}

	public static void frame_11(String str) {
		print("| " + str);
		for (int i = 0; i < 119 - count_char(str); i++)
			print(" ");
		print("|\n");
	}

	public static void frame_12(int p_id_num, String str, int p_stock[]) {

		print("|" + str);
		if (p_id_num > 0)
			print("|");
		for (int i = (p_id_num / 6) * 5; i < p_id_num; i++) {
			System.out.printf("%19d個", p_stock[i]);
			print("|");
		}
		for (int i = 0; i < 111 - (p_id_num - (p_id_num / 6) * 5) * 22; i++)
			print(" ");
		if (p_id_num == 0)
			print(" ");
		print("|\n");
	}

	public static void frame_13(int p_id_num, String str, int p_price[], int p_stock[]) {

		print("|" + str);
		if (p_id_num > 0)
			print("|");
		for (int i = (p_id_num / 6) * 5; i < p_id_num; i++) {
			System.out.printf("%19d円", p_price[i] * p_stock[i]);
			print("|");
		}
		for (int i = 0; i < 111 - (p_id_num - (p_id_num / 6) * 5) * 22; i++)
			print(" ");
		if (p_id_num == 0)
			print(" ");
		print("|\n");
	}

	public static void frame_14() {
		print("|        +----------+");
		for (int i = 0; i < 100; i++)
			print(" ");
		print("|\n");
	}

	public static void frame_15(int p_id_num, int p_price[], int p_sale[]) {
		print("| 総売上 |");
		int sale_sum = 0;
		for (int i = 0; i < p_id_num; i++)
			sale_sum = sale_sum + p_price[i] * p_sale[i];
		System.out.printf("%10d|円", sale_sum);
		for (int i = 0; i < 98; i++)
			print(" ");
		print("|\n");

	}

	public static void frame_16(int p_id_num, String str, int c_stock[]) {

		print("|" + str);
		if (p_id_num > 0)
			print("|");
		for (int i = (p_id_num / 6) * 5; i < p_id_num; i++) {
			System.out.printf("%19d枚", c_stock[i]);
			print("|");
		}
		for (int i = 0; i < 111 - (p_id_num - (p_id_num / 6) * 5) * 22; i++)
			print(" ");
		if (p_id_num == 0)
			print(" ");
		print("|\n");
	}

	public static void frame_17() {
		print("|        +");
		for (int i = 0; i < 109; i++)
			print("-");
		print("+ |\n");
	}

	public static void frame_18(String message[]) {
		for (int i = 0; i < 6; i++) {
			print("|        |");
			if (message[i] != null)
				print(message[i]);
			else
				print("");
			for (int j = 0; j < 109 - count_char(message[i]); j++)
				print(" ");
			print("| |\n");
		}
	}

	public static void frame_19(String message) {
		print("|        |");
		if (message == null)
			message = "";
		print(message);
		for (int i = 0; i < 90 - count_char(message); i++)
			print(" ");
		print("|");
		for (int i = 0; i < 4; i++)
			print(" ");
		print("          ");
		for (int i = 0; i < 6; i++)
			print(" ");
		print("|\n");
	}

	public static void frame_20(String message) {
		print("|        |");
		if (message == null)
			message = "";
		print(message);
		for (int i = 0; i < 90 - count_char(message); i++)
			print(" ");
		print("|");
		for (int i = 0; i < 4; i++)
			print(" ");
		print(" 投入金額 ");
		for (int i = 0; i < 6; i++)
			print(" ");
		print("|\n");
	}

	public static void frame_welcome() {

		print("|                                       ___                                                                   ___        |\n");
		print("|         システム開発実習2            |   |               メイテック 入社研修サポートセンター               |   |       |\n");
		print("|         自動販売機シミュレータ       |   |               IT08   相澤、上原、木原、髙石、松本               |   |       |\n");
		print("|                                      |   |                                                                 |   |       |\n");
		print("|      __          __     _________    |   |    ___________     _________     __   __   __     _________     |   |       |\n");
		print("|     |  |        |  |  ／ _______ ＼  |   |  ／ __________|  ／ _______ ＼  |  |／  |／  |  ／ _______ ＼   |   |       |\n");
		print("|     |  |  ／＼  |  | |  |_______|  | |   | |  |            |  |       |  | |            | |  |_______|  |  |___|       |\n");
		print("|     |  |／    ＼|  | |   __________| |   | |  |            |  |       |  | |  ／|  ／|  | |   __________|   ___        |\n");
		print("|     |     ／＼     | |  |__________  |   | |  |__________  |  |_______|  | |  | |  | |  | |  |__________   |   |       |\n");
		print("|      ＼_／    ＼_／   ＼__________／ |___|  ＼__________／  ＼_________／  |__| |__| |__|  ＼__________／  |___|       |\n");
	}

	// System.out.printを書くのが面倒なのでprintに省略するための関数
	public static void print(String str) {
		System.out.print(str);
	}

}

//スクリーンクリア用のクラス(/https://qiita.com/yocto_mns/items/c0b5ab527d105bc63d6bの情報をほぼコピペ)
//ちなみに髙石はよく分かっていませんが、とりあえずコマンドプロンプト上ではできています。
class ConsoleControl {
	private ProcessBuilder pb;

	/**
	 * ConsoleControlクラスのコンストラクタです。 指定したコマンドを実行する新しいプロセスを実行する環境を構築します。
	 * 
	 * @param command 実行するコマンド
	 */
	public ConsoleControl(String... command) {
		pb = new ProcessBuilder(command);
	}

	/**
	 * コマンドプロンプトの画面をクリアするメソッド。
	 */
	public void cls() throws IOException, InterruptedException {
		pb.inheritIO().start().waitFor();
		/*
		 * // ProcesserBuildのコンストラクタ引数で指定した外部コマンドを // コマンドプロンプトで実行できるように変換
		 * ProcessBuilder pbInheritIO = pb.inheritIO(); // 外部コマンドで実行 Process pro =
		 * pbInheritIO.start(); // 他のスレッドで動いているプロセスが終わるまで待機 pro.waitFor();
		 */
	}
}
