package ru.lanit.dibr.utils.utils;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
//import com.jcraft.jsch.UIKeyboardInteractive;

import javax.swing.*;
import java.awt.*;

/**
 * User: VTaran
 * Date: 16.08.2010
 * Time: 16:00:38
 */
public class MyUserInfo implements UserInfo
		, UIKeyboardInteractive
{

	public MyUserInfo(String passwd) {
		this.passwd = passwd;
	}

	public String getPassword() {
		return passwd;
	}

	public boolean promptYesNo(String str) {
		return true;
//		Object[] options = {"yes", "no"};
//		int foo = JOptionPane.showOptionDialog(null,
//				str,
//				"Warning",
//				JOptionPane.DEFAULT_OPTION,
//				JOptionPane.WARNING_MESSAGE,
//				null, options, options[0]);
//		return foo == 0;
	}

	String passwd;

	public String getPassphrase() {
		return null;
	}

	public boolean promptPassphrase(String message) {
		return true;
	}

	public boolean promptPassword(String message) {
		return true;
	}

	public void showMessage(String message) {
		System.out.println("UserInfo message:" + message);
		JOptionPane.showMessageDialog(null, message);
	}

	final GridBagConstraints gbc =
			new GridBagConstraints(0, 0, 1, 1, 1, 1,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0);
	private Container panel;

	@Override
	public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
		String result[] = new String[prompt.length];
		for (int i = 0 ; i<prompt.length; i++) {
			String s = prompt[i];
			System.out.println("Server prompt: " + s);
			if(s.contains("assword")) {
				System.out.println("Putting stored password as an answer.");
				result[i] = passwd;
			} else {
				result[i] = "";
			}
		}
		return result;
	}
}
