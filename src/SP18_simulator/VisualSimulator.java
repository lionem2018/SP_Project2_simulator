package SP18_simulator;

import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * VisualSimulator는 사용자와의 상호작용을 담당한다.<br>
 * 즉, 버튼 클릭등의 이벤트를 전달하고 그에 따른 결과값을 화면에 업데이트 하는 역할을 수행한다.<br>
 * 실제적인 작업은 SicSimulator에서 수행하도록 구현한다.
 */
public class VisualSimulator extends JFrame{
	ResourceManager resourceManager = new ResourceManager();
	SicLoader sicLoader = new SicLoader(resourceManager);
	SicSimulator sicSimulator = new SicSimulator(resourceManager);
	
	// GUI 컴포넌트
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
	private JList<String> listInst;
	private JList<String> listLog; 
	
	// 불러온 파일의 경로를 저장하는 String 변수
	private String filePath;
	
	public VisualSimulator()
	{
		
		super("SIC/XE Simulator");
		setBounds(100, 100, 720, 1100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		
		/*
		frame = new JFrame();
		frame.setBounds(100, 100, 720, 1100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		*/
		JLabel labelFilename = new JLabel("FileName:");
		labelFilename.setBounds(22, 20, 78, 21);
		add(labelFilename);
		
		textFileName = new JTextField();
		textFileName.setBounds(105, 17, 156, 27);
		textFileName.setColumns(10);
		add(textFileName);
		
		JButton btnOpen = new JButton("open");
		btnOpen.setBounds(266, 16, 73, 29);
		btnOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				FileDialog fileD = new FileDialog(new JFrame(), "열기", FileDialog.LOAD);
				fileD.setVisible(true);
				
				filePath = fileD.getDirectory() + fileD.getFile();
				textFileName.setText(fileD.getFile());
				File file = new File(filePath);
				load(file);
				update();
			}
		});
		add(btnOpen);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "H (Header Record)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(17, 60, 322, 180);
		add(panel_1);
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
		add(panel);
		
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
		add(lblStartAddressIn);
		
		textStartAddrMemory = new JTextField();
		textStartAddrMemory.setBounds(512, 213, 166, 27);
		add(textStartAddrMemory);
		textStartAddrMemory.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new TitledBorder(null, "Register", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(17, 255, 322, 250);
		add(panel_2);
		
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
		add(lblTargetAddress);
		
		textTargetAddr = new JTextField();
		textTargetAddr.setColumns(10);
		textTargetAddr.setBounds(512, 263, 166, 27);
		add(textTargetAddr);
		
		JPanel panel_3 = new JPanel();
		panel_3.setLayout(null);
		panel_3.setBorder(new TitledBorder(null, "Register(for XE)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(17, 520, 322, 211);
		add(panel_3);
		
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
		add(lblInstructions);
		
		JScrollPane instScroll = new JScrollPane();
		instScroll.setBounds(356, 338, 174, 392);
		add(instScroll);
		listInst = new JList<>();
		instScroll.setViewportView(listInst);
		
		JLabel label_1 = new JLabel("사용중인 장치");
		label_1.setBounds(547, 338, 131, 21);
		add(label_1);
		
		textDevice = new JTextField();
		textDevice.setBounds(571, 374, 107, 27);
		add(textDevice);
		textDevice.setColumns(10);
		
		JButton btnRunOneStep = new JButton("실행(1 Step)");
		btnRunOneStep.setBounds(542, 614, 136, 29);
		btnRunOneStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				oneStep();
				update();
			}
		});
		add(btnRunOneStep);
		
		JButton btnRunAll = new JButton("실행 (All)");
		btnRunAll.setBounds(542, 658, 136, 29);
		btnRunAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				allStep();
				update();
			}
		});
		add(btnRunAll);
		
		JButton btnQuit = new JButton("종료");
		btnQuit.setBounds(542, 702, 136, 29);
		add(btnQuit);
		
		JLabel lblLog = new JLabel("Log (명령어 수행 관련):");
		lblLog.setBounds(17, 758, 204, 21);
		add(lblLog);
		
		JScrollPane logScroll = new JScrollPane();
		logScroll.setBounds(17, 782, 664, 247);
		add(logScroll);
		listLog = new JList<>();
		logScroll.setViewportView(listLog);
		
		setVisible(true);
	}
	
	/**
	 * 프로그램 로드 명령을 전달한다.
	 */
	public void load(File program){
		//...
		sicLoader.load(program);
		sicSimulator.load(program);
	};

	/**
	 * 하나의 명령어만 수행할 것을 SicSimulator에 요청한다.
	 */
	public void oneStep(){
		sicSimulator.oneStep();
	};

	/**
	 * 남아있는 모든 명령어를 수행할 것을 SicSimulator에 요청한다.
	 */
	public void allStep(){
		sicSimulator.allStep();
	};
	
	/**
	 * 화면을 최신값으로 갱신하는 역할을 수행한다.
	 */
	public void update(){
		String[] instStringList = sicSimulator.getInstList().toArray(new String[sicSimulator.getInstList().size()]);
		listInst.setListData(instStringList);
		
		String[] logStringList = sicSimulator.getLogList().toArray(new String[sicSimulator.getLogList().size()]);
		listLog.setListData(logStringList);
		
		textProgName.setText(resourceManager.getProgName(0));
		
		textStartAddrObProg.setText(String.valueOf(resourceManager.getProgStartAddr(0)));
		
		int progLength = 0;
		for(int i = 0; i < resourceManager.getProgCount(); i++)
			progLength += resourceManager.getProgLength(i);
		textLengthProg.setText(String.valueOf(progLength));
		
		textFirstInstAddr.setText(String.valueOf(resourceManager.getProgStartAddr(0)));
		
		textStartAddrMemory.setText(String.valueOf(resourceManager.getProgStartAddr(0)));
		
		textADec.setText(String.format("%d", resourceManager.getRegister(SicSimulator.A_REGISTER)));
		textAHex.setText(String.format("%X", resourceManager.getRegister(SicSimulator.A_REGISTER)));

		textXDec.setText(String.format("%d", resourceManager.getRegister(SicSimulator.X_REGISTER)));
		textXHex.setText(String.format("%X", resourceManager.getRegister(SicSimulator.X_REGISTER)));
		
		textLDec.setText(String.format("%d", resourceManager.getRegister(SicSimulator.L_REGISTER)));
		textLHex.setText(String.format("%X", resourceManager.getRegister(SicSimulator.L_REGISTER)));
		
		textPCDec.setText(String.format("%d", resourceManager.getRegister(SicSimulator.PC_REGISTER)));
		textPCHex.setText(String.format("%X", resourceManager.getRegister(SicSimulator.PC_REGISTER)));
		
		textSW.setText(String.format("%d", resourceManager.getRegister(SicSimulator.SW_REGISTER)));
		
		textTargetAddr.setText(String.valueOf(sicSimulator.getTargetAddr()));
		
		textBDec.setText(String.format("%d", resourceManager.getRegister(SicSimulator.B_REGISTER)));
		textBHex.setText(String.format("%X", resourceManager.getRegister(SicSimulator.B_REGISTER)));
		
		textSDec.setText(String.format("%d", resourceManager.getRegister(SicSimulator.S_REGISTER)));
		textSHex.setText(String.format("%X", resourceManager.getRegister(SicSimulator.S_REGISTER)));
		
		textTDec.setText(String.format("%d", resourceManager.getRegister(SicSimulator.T_REGISTER)));
		textTHex.setText(String.format("%X", resourceManager.getRegister(SicSimulator.T_REGISTER)));

		textF.setText(String.format("%f", resourceManager.getFRegister()));
		
		textDevice.setText(sicSimulator.getDevice()); /////////////////////////////////////////////////////////////
	};
	

	public static void main(String[] args) {
		new VisualSimulator();
	}
}
