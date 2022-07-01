package vending_machine_IT08;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;

public class V_kihara {
	public static int p_id, p_price, p_sale, p_stock;
	public static String p_name;
	public static String p_yn;
	public static String url = "jdbc:mysql://localhost/vending_machine";
	public static String user = "root";
	public static String password = "";

	public static String message[] = new String[6];

	public static void main(String[] args) {

	}

	//*  商品追加  *//

	public static void p_update() {
		int n = 0;
		int cnt = 0;
		Connection con = null;
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			con = DriverManager.getConnection(url, user, password); //データベースへ接続
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(*) cnt FROM product_table");
			rs.next();
			cnt = rs.getInt("cnt"); //レコード数取得

			con.close(); //切断

		} catch (SQLException e) {
			System.out.println("DB接続に失敗しました");
		}

		if (cnt == 10) { //商品数が１０個ならば追加できないようにする
			message[5] = "既に商品数が上限です。先に商品削除をしてください。";
		} else if (cnt < 10) { //商品数が１０個以下ならば商品追加を行う

			while (n != 1) {
				try {
					message[0] = "追加する新商品のIDを入力してください(0で終了)";
					message[1] = "";
					message[2] = "";
					message[3] = "";
					message[4] = "";
					V_takaishi.display_manage(message);
					String str1 = V_main.scan.nextLine(); //入力     V_main

					n = V_aizawa.p_id_research(str1); //str1が条件に適するか調べ、結果をnへ返す      V_aizawa

					if (n != 1)
						continue;

					if (p_id != 0) { //p_idが0でなければ

						//商品名入力
						message[0] = "商品名を入力してください";
						message[1] = "価格を入力してください";
						message[2] = "在庫を入力してください";
						message[3] = "";
						message[4] = "";
						message[5] = "";
						V_takaishi.display_manage(message);

						p_name = V_main.scan.nextLine(); //入力     V_main

						if (V_takaishi.count_char(p_name) > 20) { //全角10文字以内  V_takaishi
							message[5] = "商品名は全角10文字以内で入力してください";
							n = -1;
							continue;
						}

						//価格入力
						p_price = V_main.scan.nextInt(); //入力     V_main

						if (100 > p_price || p_price > 1990 || p_price % 10 != 0) {
							message[5] = "価格は100円から1990円の範囲で入力してください";
							n = -1;
							continue;
						}

						//残数入力
						p_stock = V_main.scan.nextInt(); //入力     V_main

						if (p_stock < 0) {
							message[5] = "残数は0以上にしてください";
							n = -1;
							continue;
						}

						try {
							con = DriverManager.getConnection(url, user, password); //データベースへ接続
							String sql = "INSERT INTO product_table VALUES (" + p_id + ",'" + p_name + "'," + p_price
									+ ",0," + p_stock + ",'購入不可')";
							pstmt = con.prepareStatement(sql);
							pstmt.executeUpdate(); //SQL文実行  商品管理テーブルへ追加

							message[0] = "商品名を追加しました。操作を選択してください。";
							message[1] = "1 : 在庫追加";
							message[2] = "2 : 商品追加";
							message[3] = "3 : 商品削除";
							message[4] = "4 : 売り上げ初期化";
							message[5] = "5 : 商品管理終了";

							pstmt.close(); //切断
							con.close();

						} catch (SQLException e) {
							System.out.println("DB接続に失敗しました");

						}
					}
				} catch (InputMismatchException e) {
					message[5] = "入力が不正です";
				}
			}
		}

		V_takaishi.display_manage(message); //管理者画面表示   要確認
	}

	// * 商品削除  *//

	public static void p_delete() {
		int n = 1;
		Connection con = null;
		PreparedStatement pstmt = null;

		while (n != 0) {
			try {
				message[0] = "削除する新商品のIDを入力してください";
				message[1] = "";
				message[2] = "";
				message[3] = "";
				message[4] = "";
				message[5] = "";
				V_takaishi.display_manage(message);
				String str = V_main.scan.nextLine(); //入力     V_main 

				n = V_aizawa.p_id_research(str); //strが条件に適するか調べ、結果をnへ返す     V_aizawa

				if (n == 0) {
					p_id = Integer.parseInt(str);

					try {
						con = DriverManager.getConnection(url, user, password); //データベースへ接続
						String sql = "DELETE FROM product_table WHERE p_id = " + p_id;
						pstmt = con.prepareStatement(sql);
						pstmt.executeUpdate(); //SQL文実行  商品管理テーブルから削除

						message[0] = "商品を削除しました。操作を選択してください。";
						message[1] = "1 : 在庫追加";
						message[2] = "2 : 商品追加";
						message[3] = "3 : 商品削除";
						message[4] = "4 : 売り上げ初期化";
						message[5] = "5 : 商品管理終了";

						pstmt.close(); //切断
						con.close();

					} catch (SQLException e) {
						System.out.println("DB接続に失敗しました");
					}
				} else if (p_id == 0) {

					break;
				}
			} catch (InputMismatchException e) {
				message[0] = "入力が不正です";
			}
		}
		V_takaishi.display_manage(message); //  管理者画面表示    要確認
	}

	//* 売り上げ数初期化 *//

	public static void p_reset() {
		int n = 1;
		Connection con = null;
		PreparedStatement pstmt = null;

		while (n != 0) {
			try {
				message[0] = "売り上げ数を初期化する商品番号を入力してください。";
				message[1] = "";
				message[2] = "";
				message[3] = "";
				message[4] = "";
				message[5] = "";
				V_takaishi.display_manage(message);
				String str = V_main.scan.nextLine(); //入力     V_main

				n = V_aizawa.p_id_research(str); //strが条件に適するか調べ、結果をnへ返す   V_aizawa

				if (n == 0) {
					p_id = Integer.parseInt(str);

					try {
						con = DriverManager.getConnection(url, user, password); //データベースへ接続
						String sql = "UPDATE product_table SET p_sale = 0 WHERE p_id = " + p_id;
						pstmt = con.prepareStatement(sql);
						pstmt.executeUpdate(); //SQL文実行  売り上げ数初期化

						message[0] = "売り上げ数を初期化しました。操作を選択してください。";
						message[1] = "1 : 在庫追加";
						message[2] = "2 : 商品追加";
						message[3] = "3 : 商品削除";
						message[4] = "4 : 売り上げ初期化";
						message[5] = "5 : 商品管理終了";

						pstmt.close(); //切断
						con.close();

					} catch (SQLException e) {
						System.out.println("DB接続に失敗しました");
					}
				} else if (p_id == 0) {
					message[0] = "";
					message[1] = "1 : 在庫追加";
					message[2] = "2 : 商品追加";
					message[3] = "3 : 商品削除";
					message[4] = "4 : 売り上げ初期化";
					message[5] = "5 : 商品管理終了";

					break;
				}
			} catch (InputMismatchException e) {
				message[0] = ("入力が不正です");
			}
		}
		V_takaishi.display_manage(message); //管理者画面表示    要確認
	}

}