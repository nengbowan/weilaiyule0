package top.fwkj51.gui;

import top.fwkj51.Api;
import top.fwkj51.enums.BetMoneyType;
import top.fwkj51.enums.BetType;
import top.fwkj51.enums.LongHuBetMethod;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 未来娱乐用户界面  TODO 可以提供测试版本 混淆代码 网络验证
 */
public class MainGUI extends JFrame{
   //TODO 页面主图 高大上

    private Integer WINDOW_WIDTH = 980 ;

    private Integer WINDOW_HEIGHT = 235;
    private JPanel topPanel = new JPanel();

    private JLabel usernameLabel = new JLabel("用户名:"); //用户名标签

    private JTextField usernameField = new JTextField(8); //用户名输入框

    private JLabel passwdLabel =new JLabel("密   码:"); //密码标签

    private JTextField passwdField = new JTextField(8); //密码输入框

    private JButton startB = new JButton("开始"); // 开始按钮

    private JTextPane logPane = new JTextPane(); // 日志记录界面

    private JLabel betMoneyLabel = new JLabel("下注金额:");

    private JTextField betMoneyField = new JTextField(8); //下注金额输入框 1,3,7,15,31,65 逗号隔开

    private JRadioButton longRadio = new JRadioButton("龙",true);

    private JRadioButton huRadio = new JRadioButton("虎",false);

    private ButtonGroup longHuGroup = new ButtonGroup();

    private JRadioButton liRadio = new JRadioButton("厘",false);

    private JRadioButton fenRadio = new JRadioButton("分",false);

    private JRadioButton jiaoRadio = new JRadioButton("角",false);

    private JRadioButton yuanRadio = new JRadioButton("元",true);

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
    private JCheckBox lhwq = new JCheckBox("龙虎万千");
    private JCheckBox lhwb = new JCheckBox("龙虎万百");
    private JCheckBox lhws = new JCheckBox("龙虎万十");
    private JCheckBox lhwg = new JCheckBox("龙虎万个");
    private JCheckBox lhqb = new JCheckBox("龙虎千百");
    private JCheckBox lhqs = new JCheckBox("龙虎千十");
    private JCheckBox lhqg = new JCheckBox("龙虎千个");
    private JCheckBox lhbs = new JCheckBox("龙虎百十");
    private JCheckBox lhbg = new JCheckBox("龙虎百个");
    private JCheckBox lhsg = new JCheckBox("龙虎十个");

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
        startB.setSize(50 ,30 );
        this.add(startB ,BorderLayout.CENTER);

        logPane.setText("程序启动正常。。。");
        this.add(logPane , BorderLayout.SOUTH);
//        this.add(topPanel , BorderLayout.NORTH);
//
//        this.add(logPane , BorderLayout.SOUTH);


        //标题
        this.setTitle("未来娱乐自动投注管理界面V0.1");
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);

        //大小 400 300
        this.setSize(new Dimension(WINDOW_WIDTH ,WINDOW_HEIGHT ));

        //左上角
        this.setLocation(0 , 0 );

        this.startB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //TODO param filter
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
                    logPane.setText(logPane.getText() + "\n" + "下注金额未设置..");
                    return;
                }

                String [] betsStr = betMoney.split(",");

                int [] bets = new int [betsStr.length];
                for(int x = 0 ; x<bets.length ; x++){
                    bets[x]=Integer.valueOf(betsStr[x]);
                }

                new Api(username , password , bets , longHuBetMethodList , betType , betMoneyType ).login();

                //启用线程 不至于UI阻塞 有几率影响程序逻辑 还是选择阻塞为好  TODO 后期可以用高级方法
//                @Deprecated
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }).start();

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

        topPanel.add(all, BorderLayout.NORTH);
        //下注金额 1,3,5,7,9
        topPanel.add(betMoneyLabel , BorderLayout.CENTER);
        topPanel.add(betMoneyField , BorderLayout.SOUTH);


    }

    public static void main(String[] args) {
        new MainGUI();
    }
}
