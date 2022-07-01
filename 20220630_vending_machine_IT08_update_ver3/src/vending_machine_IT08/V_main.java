package vending_machine_IT08;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class V_main {

	// 各関数で標準入力を使う場合は、このscanを参照してください
	public static final Scanner scan = new Scanner(System.in);
	// ↑各関数でScannerを閉じてしまうと、標準入力自体が閉じられてしまい、もう一度開くことはできないそうです

	// プログラム終了パスワード
	public static final int program_end_num = 123456789;

	// 管理者パスワード
	public static final String manage_password = "pass";

	// メイン関数
	public static void main(String[] args) throws IOException {

		// 仮変数(後でV_matsumotoのものに置き換え
		int pro_total = 0; // V_matsumoto.m_totalに置き換え予定
		int pro_memory[] = new int[5]; // V_matsumoto.m_memoryに置き換え予定

		// メッセージ表示用(数行で表示するため、配列で管理)
		String message[] = new String[6]; // メイン画面のメッセージ
		String manage_message[] = new String[6]; // 管理者用画面のメッセージ
		String purchase_message[] = new String[6];

		//初期画面メッセージ
		String ini_mes_0 = "投入する金銭を入力してください(使用可能金銭 : 1000円札、500円玉、100円玉、50円玉、10円玉)";
		String ini_mes_1 = "金銭排出の場合は0を、金銭投入終了の場合は1を入力してください";
		purchase_message[0] = "購入する商品のIDを入力してください";
		
		// 入力用
		int money = 0;
		String password = null;

		// 初期画面表示
		message[0] = ini_mes_0;
		message[1] = ini_mes_1;
		V_takaishi.display_main(pro_total, message);

		// ① 全体のループ開始
		while (true) {

			// ①を抜け出すための変数
			boolean end_1 = false;

			// ② moneyに正しい値が入力されるまでのループ開始
			while (true) {

				// ②を抜け出すための変数
				boolean end_2 = false;

				try {
					money = scan.nextInt();
					message[1] = "";
					end_2 = true;
				} catch (InputMismatchException ex) {
					message[5] = "不正な入力です";
					end_2 = false;
					V_takaishi.display_main(pro_total, message);
					scan.next();
				}

				if (end_2) {
					break;
				}
			}
			// ② 終了

			// プログラム終了へ
			if (money == program_end_num) {
				System.out.print("プログラム終了");
				end_1 = true;
			}

			// 管理者パスワード入力画面へ
			else if (money == -1 && pro_total == 0) {
				message[0] = "管理者パスワードを入力";
				message[1] = "";
				message[5] = "";
				V_takaishi.display_main(pro_total, message);
				password = scan.next();
				if (manage_password.equals(password)) {

					// 管理者モードへ
					V_takaishi.manage_mode(manage_message);
					message[5] = "";
				} else {
					message[5] = "パスワードが違います";
				}

				message[0] = ini_mes_0;
				message[1] = ini_mes_1;
				V_takaishi.display_main(pro_total, message);
			}

			else if (money == -1 && pro_total != 0) {
				message[0] = ini_mes_0;
				message[1] = ini_mes_1;
				message[5] = "金銭が投入されているため、管理者モードへ移行できません";
				V_takaishi.display_main(pro_total, message);
			}

			// 商品購入へ
			else if (money == 1) {
				V_uehara.kounyuu(pro_total, pro_memory);
//				end_1 = true; // (購入後、プログラムを終了しないのなら、この行は不要)
			}

			// 金銭投入へ
			else {
//				V_matsumoto.input_money(money);
			}

			if (end_1) {
				break;
			}
		}
		// ① 終了

		scan.close();

		System.gc();

	}

}
