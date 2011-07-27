package brainfuck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Interpreter {
	int pc;
	int ptr;
	int memory_usege;
	int code_usege;
	byte[] memory;
	byte[] code;

	BufferedReader br;

	Interpreter() {
		init();
	}

	public void run() {
		String buf;
		while (true) {
			System.out.print(">");
			try {
				buf = br.readLine();
			} catch (IOException e) {
				buf = null;
			}
			if (buf.equals("exit")) {
				return;
			} else if (buf.equals("reset")) {
				init();
				continue;
			} else if (buf.equals("dump")) {
				dump();
				continue;
			} else if (buf.equals("run")) {
				memory = new byte[100];
				memory_usege = 0;
				pc = 0;
				ptr = 0;
				process();
				continue;
			}
			putCode(buf);
			process();
		}
	}

	private void init() {
		pc = 0;
		ptr = 0;
		memory_usege = 0;
		code_usege = 0;
		memory = new byte[1000];
		code = new byte[1000];
		br = new BufferedReader(new InputStreamReader(System.in));
	}

	private void process() {
		boolean outFlag = false;
		char[] c;
		while (code[pc] > 0) {
			switch (code[pc]) {
			case '>':
				ptr++;
				if (memory_usege < ptr)
					memory_usege = ptr;
				break;
			case '<':
				if (ptr > 0)
					ptr--;
				break;
			case '+':
				memory[ptr]++;
				break;
			case '-':
				memory[ptr]--;
				break;
			case '.':
				System.out.print((char) memory[ptr]);
				outFlag = true;
				break;
			case ',':
				try {
					// TODO バッファを用意して複数文字が入力された時の処理も考える
					c = br.readLine().toCharArray();
					if (c.length > 0)
						memory[ptr] = (byte) c[0];
				} catch (IOException e) {
				}
				break;
			case '[':
				if (!isLoopContinue()) {
					toLoopEnd();
				}
				break;
			case ']':
				toLoopHead();
				break;
			}
			pc++;
		}
		if (outFlag)
			System.out.print("\n");
	}

	private boolean isLoopContinue() {
		if (memory[ptr] == 0) {
			return false;
		}
		return true;
	}

	private void toLoopEnd() {
		int nest = 1;
		while (nest > 0) {
			switch (code[++pc]) {
			case '[':
				nest++;
				break;
			case ']':
				nest--;
				break;
			}
		}
	}

	private void toLoopHead() {
		int nest = 1;
		while (nest > 0) {
			switch (code[--pc]) {
			case '[':
				nest--;
				break;
			case ']':
				nest++;
				break;
			}
		}
		pc--;
	}

	private void dump() {
		code_dump();
		memory_dump();
	}

	private void code_dump() {
		System.out.println("code dump");
		for (byte b : code) {
			if (b == 0)
				break;
			System.out.print((char) b);
		}
		System.out.print("\n");
	}

	private void memory_dump() {
		System.out.println("memory dump(number)");
		for (int i = 0; i < memory_usege + 1; i++) {
			System.out.print(memory[i]);
			if ((i + 1) % 8 == 0 && i != 0) {
				System.out.print("\n");
			} else
				System.out.print("	");
		}
		System.out.print("\n");
		System.out.println("memory dump(char)");
		for (int i = 0; i < memory_usege + 1; i++) {
			System.out.print((char) memory[i]);
		}
		System.out.print("\n");
	}

	private void putCode(String buf) {
		byte[] data = buf.getBytes();
		for (int i = 0; i < data.length; i++) {
			if (data[i] == '>' || data[i] == '<' || data[i] == '+'
					|| data[i] == '-' || data[i] == '.' || data[i] == ','
					|| data[i] == '[' || data[i] == ']') {
				code[code_usege] = data[i];
				code_usege++;
			}
		}
	}
}
