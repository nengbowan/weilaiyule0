package top.fwkj51.gui;

import sun.awt.AWTAccessor;
import top.fwkj51.Api;
import top.fwkj51.enums.BetMoneyType;
import top.fwkj51.enums.BetType;
import top.fwkj51.enums.LongHuBetMethod;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InvocationEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 未来娱乐用户界面  TODO 可以提供测试版本 混淆代码 网络验证
 */
public class MainGUI extends JFrame{
   //TODO 页面主图 高大上

    private Integer WINDOW_WIDTH = 965 ;

    private Integer WINDOW_HEIGHT = 640;
    private JPanel topPanel = new JPanel();

    private JLabel usernameLabel = new JLabel("用户名:"); //用户名标签

    private JTextField usernameField = new JTextField(null , "" , 8); //用户名输入框

    private JLabel passwdLabel =new JLabel("密   码:"); //密码标签

    private JTextField passwdField = new JTextField(  null , "" , 8); //密码输入框

    private JButton startB = new JButton("开始"); // 开始按钮

    private JTextPane logContentPane = new JTextPane(); // 日志记录界面

    private JScrollPane logScrollPane = new JScrollPane(logContentPane);

    private JLabel betMoneyLabel = new JLabel("下注金额:");

    private JTextField betMoneyField = new JTextField(null , "1,3,7,15,31,63,129,219" , 8); //下注金额输入框 1,3,7,15,31,65 逗号隔开

    private JRadioButton longRadio = new JRadioButton("龙",true);

    private JRadioButton huRadio = new JRadioButton("虎",false);

    private ButtonGroup longHuGroup = new ButtonGroup();

    private JRadioButton liRadio = new JRadioButton("厘",true);

    private JRadioButton fenRadio = new JRadioButton("分",false);

    private JRadioButton jiaoRadio = new JRadioButton("角",false);

    private JRadioButton yuanRadio = new JRadioButton("元",false);

    private ButtonGroup liFenJiaoYuanGroup = new ButtonGroup();
//    LHWQ("lhwq","龙虎万千"),
//    LHWB("lhwb","龙虎万百"),
//    LHWS("lhws","龙虎万十"),
//    LHWG("lhwg","龙虎万个"),
//    LHQB("lhqb","龙虎千百"),
//    LHQS("lhqs","龙虎千十"),
//    LHQG("lhqg","龙虎千个"),
//    LHBS("lhbs","龙虎百十"),
//    LHBG("lhbg","龙虎百个"),
//    LHSG("lhsg","龙虎十个");
    private JCheckBox lhwq = new JCheckBox("龙虎万千" , false);
    private JCheckBox lhwb = new JCheckBox("龙虎万百" , true);
    private JCheckBox lhws = new JCheckBox("龙虎万十" , true);
    private JCheckBox lhwg = new JCheckBox("龙虎万个" , true);
    private JCheckBox lhqb = new JCheckBox("龙虎千百" , true);
    private JCheckBox lhqs = new JCheckBox("龙虎千十" , true);
    private JCheckBox lhqg = new JCheckBox("龙虎千个" , true);
    private JCheckBox lhbs = new JCheckBox("龙虎百十" , true);
    private JCheckBox lhbg = new JCheckBox("龙虎百个" , false);
    private JCheckBox lhsg = new JCheckBox("龙虎十个" , false);

    //add for 增加多网址可用功能 by fushiyong at 2018-11-03 start

    private JLabel baseUrlLabel = new JLabel("网   址:");
    private JTextField baseUrlField = new JTextField( null , "http://www.fwkj51.top" ,8);

    private Thread handleThread = null;
    //add for 增加多网址可用功能 by fushiyong at 2018-11-03 end

    public MainGUI(){
        //设置系统兼容的软件主题
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        //设置布局
        this.setLayout(new BorderLayout());

        //设置是否可变大变小窗口
        this.setResizable(false);

        initTopPanel();



        this.add(topPanel , BorderLayout.NORTH);
        logContentPane.setSize(new Dimension(WINDOW_WIDTH , (int) ( WINDOW_HEIGHT * 0.3) ));
        logContentPane.setText("\r程序启动正常。。。");
        this.add(logScrollPane , BorderLayout.CENTER);
//        this.add(topPanel , BorderLayout.NORTH);
//
//        this.add(logPane , BorderLayout.SOUTH);


        //标题
        this.setTitle("未来娱乐自动投注管理界面优化版-V0.2");
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);

        //大小 400 300
        this.setSize(new Dimension(WINDOW_WIDTH ,WINDOW_HEIGHT ));

        //左上角
        this.setLocation(0 , 0 );

        this.startB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(((JButton)e.getSource()).getText().equals("开始")){
                    logContentPane.setText(logContentPane.getText() + "\n程序启动成功。。");
                    //TODO param filter
                    handleThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String username = usernameField.getText().trim();
                            String password = passwdField.getText().trim();
                            BetType betType = longRadio.isSelected() ? BetType.LONG : BetType.HU;

                            java.util.List<LongHuBetMethod> longHuBetMethodList = getSelectedMethods();

                            BetMoneyType betMoneyType = getSelectedMoneyType();

                            if(betMoneyType == null){
                                betMoneyType = BetMoneyType.LI;
                            }
                            String betMoney = betMoneyField.getText().trim();

                            if(betMoney == null || betMoney.equals("")){
                                logContentPane.setText(logContentPane.getText() + "\n" + "下注金额未设置..");
                                return;
                            }

                            String [] betsStr = betMoney.split(",");

                            int [] bets = new int [betsStr.length];
                            for(int x = 0 ; x<bets.length ; x++){
                                bets[x]=Integer.valueOf(betsStr[x]);
                            }

                            //如果日期大于等于2018/11/9 10:42:24 程序停止运行
                            if(System.currentTimeMillis() >=1541731344000l){
                                logContentPane.setText(logContentPane.getText() + "\n" +  "自动识别登录图片的账号已过期");
                                return;
                            }
                            new Api(logContentPane , baseUrlField.getText().trim() , username , password , bets , longHuBetMethodList , betType , betMoneyType ).login();

                        }
                    });
                    handleThread.start();
                    ((JButton)e.getSource()).setText("结束");
                }else if(((JButton)e.getSource()).getText().equals("结束")){
                    handleThread.interrupt();
                    logContentPane.setText(logContentPane.getText() + "\n已结束进程。。。等待页面不再显示下注再开始或者2分钟后再开始\n");
                    ((JButton)e.getSource()).setText("开始");
                }
            }
        });
        this.setVisible(true);


    }

    private BetMoneyType getSelectedMoneyType() {

        BetMoneyType betMoneyType = null;

        if(yuanRadio.isSelected()){
            betMoneyType = BetMoneyType.YUAN;
        }
        if(jiaoRadio.isSelected()){
            betMoneyType = BetMoneyType.JIAO;
        }
        if(fenRadio.isSelected()){
            betMoneyType = BetMoneyType.FEN;
        }
        if(liRadio.isSelected()){
            betMoneyType = BetMoneyType.LI;
        }
        return betMoneyType;

    }

    private List<LongHuBetMethod> getSelectedMethods() {
        java.util.List<LongHuBetMethod> longHuBetMethodList= new ArrayList<LongHuBetMethod>();

        if(lhwq.isSelected()){
            longHuBetMethodList.add(LongHuBetMethod.LHWQ);
        }

        if(lhwb.isSelected()){
            longHuBetMethodList.add(LongHuBetMethod.LHWB);
        }

        if(lhws.isSelected()){
            longHuBetMethodList.add(LongHuBetMethod.LHWS);
        }

        if(lhwg.isSelected()){
            longHuBetMethodList.add(LongHuBetMethod.LHWG);
        }

        if(lhqb.isSelected()){
            longHuBetMethodList.add(LongHuBetMethod.LHQB);
        }

        if(lhqs.isSelected()){
            longHuBetMethodList.add(LongHuBetMethod.LHQS);
        }

        if(lhqg.isSelected()){
            longHuBetMethodList.add(LongHuBetMethod.LHQG);
        }

        if(lhbs.isSelected()){
            longHuBetMethodList.add(LongHuBetMethod.LHBS);
        }

        if(lhbg.isSelected()){
            longHuBetMethodList.add(LongHuBetMethod.LHBG);
        }

        if(lhsg.isSelected()){
            longHuBetMethodList.add(LongHuBetMethod.LHSG);
        }
        return longHuBetMethodList;


    }

    private void initTopPanel() {
        this.topPanel.setLayout(new BorderLayout());
        //TODO 用BOX 优化布局 Grid还要操心下标索引 真心烦
        Box all = Box.createVerticalBox();

        Box baseUrlBox = Box.createHorizontalBox();
        baseUrlBox.add(baseUrlLabel);
        baseUrlBox.add(baseUrlField);
        all.add(baseUrlBox);

        //用户名
        Box usernameBox = Box.createHorizontalBox();
        usernameBox.add(usernameLabel);
        usernameBox.add(usernameField);

        all.add(usernameBox );

        //密码
        Box passwdBox = Box.createHorizontalBox();
        passwdBox.add(passwdLabel);
        passwdBox.add(passwdField);
        all.add(passwdBox );

        Box longHuBox = Box.createHorizontalBox();
        //初始化龙虎单选框
        longHuGroup.add(longRadio);
        longHuGroup.add(huRadio);
        longHuBox.add(longRadio);
        longHuBox.add(huRadio);
        all.add(longHuBox);

        //    LHWQ("lhwq","龙虎万千"),
        //    LHWB("lhwb","龙虎万百"),
        //    LHWS("lhws","龙虎万十"),
        //    LHWG("lhwg","龙虎万个"),
        //    LHQB("lhqb","龙虎千百"),
        //    LHQS("lhqs","龙虎千十"),
        //    LHQG("lhqg","龙虎千个"),
        //    LHBS("lhbs","龙虎百十"),
        //    LHBG("lhbg","龙虎百个"),
        //    LHSG("lhsg","龙虎十个");
        Box longHuWqWb = Box.createHorizontalBox();
        longHuWqWb.add(lhwq);
        longHuWqWb.add(lhwb);
        longHuWqWb.add(lhws);
        longHuWqWb.add(lhwg);
        longHuWqWb.add(lhqb);
        longHuWqWb.add(lhqs);
        longHuWqWb.add(lhqg);
        longHuWqWb.add(lhbs);
        longHuWqWb.add(lhbg);
        longHuWqWb.add(lhsg);
        all.add(longHuWqWb);

        //厘分角元单选框
        Box liFenJiaoYuanBox = Box.createHorizontalBox();
        liFenJiaoYuanGroup.add(liRadio);
        liFenJiaoYuanGroup.add(fenRadio);
        liFenJiaoYuanGroup.add(jiaoRadio);
        liFenJiaoYuanGroup.add(yuanRadio);
        liFenJiaoYuanBox.add(liRadio);
        liFenJiaoYuanBox.add(fenRadio);
        liFenJiaoYuanBox.add(jiaoRadio);
        liFenJiaoYuanBox.add(yuanRadio);
        all.add(liFenJiaoYuanBox);


        //下注金额 1,3,5,7,9
        Box betMoneyBox = Box.createVerticalBox();
        betMoneyBox.add(betMoneyLabel);
        betMoneyBox.add(betMoneyField);

        all.add(betMoneyBox);

        //开始按钮
        all.add(startB);

        topPanel.add(all, BorderLayout.NORTH);



    }

    public static void main(String[] args) {
        new MainGUI();
    }
}
