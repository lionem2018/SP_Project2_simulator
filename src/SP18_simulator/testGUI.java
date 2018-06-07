package SP18_simulator;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JList;

public class testGUI
{

	private JFrame frame;
	private JTextField textFileName;
	private JTextField textProgName;
	private JTextField textStartAddrObProg;
	private JTextField textLengthProg;
	private JTextField textFirstInstAddr;
	private JTextField textStartAddrMemory;
	private JTextField textADec;
	private JTextField textAHex;
	private JTextField textXDec;
	private JTextField textXHex;
	private JTextField textLDec;
	private JTextField textLHex;
	private JTextField textPCDec;
	private JTextField textPCHex;
	private JTextField textSW;
	private JTextField textTargetAddr;
	private JTextField textBDec;
	private JTextField textBHex;
	private JTextField textSDec;
	private JTextField textSHex;
	private JTextField textTDec;
	private JTextField textTHex;
	private JTextField textF;
	private JTextField textDevice;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					testGUI window = new testGUI();
					window.frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public testGUI()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 720, 1100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel labelFilename = new JLabel("FileName:");
		labelFilename.setBounds(22, 20, 78, 21);
		
		textFileName = new JTextField();
		textFileName.setBounds(105, 17, 156, 27);
		textFileName.setColumns(10);
		
		JButton btnOpen = new JButton("open");
		btnOpen.setBounds(266, 16, 73, 29);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(labelFilename);
		frame.getContentPane().add(textFileName);
		frame.getContentPane().add(btnOpen);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "H (Header Record)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(17, 60, 322, 180);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblProgramName = new JLabel("Program name:");
		lblProgramName.setBounds(17, 33, 136, 21);
		panel_1.add(lblProgramName);
		
		JLabel lblStartAddressOf = new JLabel("Start Address of");
		lblStartAddressOf.setBounds(17, 69, 136, 21);
		panel_1.add(lblStartAddressOf);
		
		JLabel lblObjectProgram = new JLabel("Object Program:");
		lblObjectProgram.setBounds(17, 91, 136, 21);
		panel_1.add(lblObjectProgram);
		
		JLabel lblLengthOfProgram = new JLabel("Length of Program:");
		lblLengthOfProgram.setBounds(17, 141, 157, 21);
		panel_1.add(lblLengthOfProgram);
		
		textProgName = new JTextField();
		textProgName.setBounds(170, 30, 136, 27);
		panel_1.add(textProgName);
		textProgName.setColumns(10);
		
		textStartAddrObProg = new JTextField();
		textStartAddrObProg.setBounds(170, 85, 136, 27);
		panel_1.add(textStartAddrObProg);
		textStartAddrObProg.setColumns(10);
		
		textLengthProg = new JTextField();
		textLengthProg.setBounds(180, 138, 126, 27);
		panel_1.add(textLengthProg);
		textLengthProg.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(null, "E (End Record)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(356, 60, 322, 104);
		frame.getContentPane().add(panel);
		
		JLabel lblAddressOfFirst = new JLabel("Address of First instruction");
		lblAddressOfFirst.setBounds(17, 33, 218, 21);
		panel.add(lblAddressOfFirst);
		
		JLabel lblInIbjectProgram = new JLabel("in Object Program:");
		lblInIbjectProgram.setBounds(17, 65, 157, 21);
		panel.add(lblInIbjectProgram);
		
		textFirstInstAddr = new JTextField();
		textFirstInstAddr.setColumns(10);
		textFirstInstAddr.setBounds(180, 62, 126, 27);
		panel.add(textFirstInstAddr);
		
		JLabel lblStartAddressIn = new JLabel("Start Address In Memory");
		lblStartAddressIn.setBounds(356, 179, 229, 21);
		frame.getContentPane().add(lblStartAddressIn);
		
		textStartAddrMemory = new JTextField();
		textStartAddrMemory.setBounds(512, 213, 166, 27);
		frame.getContentPane().add(textStartAddrMemory);
		textStartAddrMemory.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new TitledBorder(null, "Register", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(17, 255, 322, 250);
		frame.getContentPane().add(panel_2);
		
		JLabel lblA = new JLabel("A (#0)");
		lblA.setBounds(17, 54, 65, 21);
		panel_2.add(lblA);
		
		textADec = new JTextField();
		textADec.setColumns(10);
		textADec.setBounds(85, 51, 103, 27);
		panel_2.add(textADec);
		
		textAHex = new JTextField();
		textAHex.setColumns(10);
		textAHex.setBounds(203, 51, 103, 27);
		panel_2.add(textAHex);
		
		JLabel lblDec = new JLabel("Dec");
		lblDec.setBounds(121, 15, 43, 21);
		panel_2.add(lblDec);
		
		JLabel lblHex = new JLabel("Hex");
		lblHex.setBounds(238, 15, 43, 21);
		panel_2.add(lblHex);
		
		JLabel lblX = new JLabel("X (#1)");
		lblX.setBounds(17, 93, 65, 21);
		panel_2.add(lblX);
		
		textXDec = new JTextField();
		textXDec.setColumns(10);
		textXDec.setBounds(85, 90, 103, 27);
		panel_2.add(textXDec);
		
		textXHex = new JTextField();
		textXHex.setColumns(10);
		textXHex.setBounds(203, 90, 103, 27);
		panel_2.add(textXHex);
		
		JLabel lblL = new JLabel("L (#2)");
		lblL.setBounds(17, 132, 65, 21);
		panel_2.add(lblL);
		
		textLDec = new JTextField();
		textLDec.setColumns(10);
		textLDec.setBounds(85, 129, 103, 27);
		panel_2.add(textLDec);
		
		textLHex = new JTextField();
		textLHex.setColumns(10);
		textLHex.setBounds(203, 129, 103, 27);
		panel_2.add(textLHex);
		
		JLabel lblPc = new JLabel("PC(#8)");
		lblPc.setBounds(17, 171, 65, 21);
		panel_2.add(lblPc);
		
		textPCDec = new JTextField();
		textPCDec.setColumns(10);
		textPCDec.setBounds(85, 168, 103, 27);
		panel_2.add(textPCDec);
		
		textPCHex = new JTextField();
		textPCHex.setColumns(10);
		textPCHex.setBounds(203, 168, 103, 27);
		panel_2.add(textPCHex);
		
		JLabel lblSw = new JLabel("SW(#9)");
		lblSw.setBounds(17, 210, 65, 21);
		panel_2.add(lblSw);
		
		textSW = new JTextField();
		textSW.setColumns(10);
		textSW.setBounds(85, 207, 221, 27);
		panel_2.add(textSW);
		
		JLabel lblTargetAddress = new JLabel("Target Address:");
		lblTargetAddress.setBounds(361, 266, 136, 21);
		frame.getContentPane().add(lblTargetAddress);
		
		textTargetAddr = new JTextField();
		textTargetAddr.setColumns(10);
		textTargetAddr.setBounds(512, 263, 166, 27);
		frame.getContentPane().add(textTargetAddr);
		
		JPanel panel_3 = new JPanel();
		panel_3.setLayout(null);
		panel_3.setBorder(new TitledBorder(null, "Register(for XE)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(17, 520, 322, 211);
		frame.getContentPane().add(panel_3);
		
		JLabel lblB = new JLabel("B (#3)");
		lblB.setBounds(17, 54, 65, 21);
		panel_3.add(lblB);
		
		textBDec = new JTextField();
		textBDec.setColumns(10);
		textBDec.setBounds(85, 51, 103, 27);
		panel_3.add(textBDec);
		
		textBHex = new JTextField();
		textBHex.setColumns(10);
		textBHex.setBounds(203, 51, 103, 27);
		panel_3.add(textBHex);
		
		JLabel label_2 = new JLabel("Dec");
		label_2.setBounds(121, 15, 43, 21);
		panel_3.add(label_2);
		
		JLabel label_3 = new JLabel("Hex");
		label_3.setBounds(238, 15, 43, 21);
		panel_3.add(label_3);
		
		JLabel lblS = new JLabel("S (#4)");
		lblS.setBounds(17, 93, 65, 21);
		panel_3.add(lblS);
		
		textSDec = new JTextField();
		textSDec.setColumns(10);
		textSDec.setBounds(85, 90, 103, 27);
		panel_3.add(textSDec);
		
		textSHex = new JTextField();
		textSHex.setColumns(10);
		textSHex.setBounds(203, 90, 103, 27);
		panel_3.add(textSHex);
		
		JLabel lblT = new JLabel("T (#5)");
		lblT.setBounds(17, 132, 65, 21);
		panel_3.add(lblT);
		
		textTDec = new JTextField();
		textTDec.setColumns(10);
		textTDec.setBounds(85, 129, 103, 27);
		panel_3.add(textTDec);
		
		textTHex = new JTextField();
		textTHex.setColumns(10);
		textTHex.setBounds(203, 129, 103, 27);
		panel_3.add(textTHex);
		
		JLabel lblF = new JLabel("F (#6)");
		lblF.setBounds(17, 171, 65, 21);
		panel_3.add(lblF);
		
		textF = new JTextField();
		textF.setColumns(10);
		textF.setBounds(85, 168, 221, 27);
		panel_3.add(textF);
		
		JLabel lblInstructions = new JLabel("Instructions:");
		lblInstructions.setBounds(361, 302, 112, 21);
		frame.getContentPane().add(lblInstructions);
		
		JList listInst = new JList();
		listInst.setBounds(356, 338, 174, 392);
		frame.getContentPane().add(listInst);
		
		JLabel label_1 = new JLabel("사용중인 장치");
		label_1.setBounds(547, 338, 131, 21);
		frame.getContentPane().add(label_1);
		
		textDevice = new JTextField();
		textDevice.setBounds(571, 374, 107, 27);
		frame.getContentPane().add(textDevice);
		textDevice.setColumns(10);
		
		JButton btnRunOneStep = new JButton("실행(1 Step)");
		btnRunOneStep.setBounds(542, 614, 136, 29);
		frame.getContentPane().add(btnRunOneStep);
		
		JButton btnRunAll = new JButton("실행 (All)");
		btnRunAll.setBounds(542, 658, 136, 29);
		frame.getContentPane().add(btnRunAll);
		
		JButton btnQuit = new JButton("종료");
		btnQuit.setBounds(542, 702, 136, 29);
		frame.getContentPane().add(btnQuit);
		
		JLabel lblLog = new JLabel("Log (명령어 수행 관련):");
		lblLog.setBounds(17, 758, 204, 21);
		frame.getContentPane().add(lblLog);
		
		JList listLog = new JList();
		listLog.setBounds(17, 782, 664, 247);
		frame.getContentPane().add(listLog);
		
	}
}
