/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrameDBD4ToPostgres.java
 *
 * Created on 10/07/2011, 10:16:09
 */
package dbd4topostgres;

import dbd4topostgres.model.DBDesignerModel4;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import dbd4topostgres.model.DBDesignerParser;
import java.util.ResourceBundle;

/**
 *
 * @author frank
 */
public class FrameDBD4ToPostgres extends javax.swing.JFrame {

    private Preferences preferences;
    private File currentOutputDirectory = null;
    private File currentInputDirectory = null;
    private String txtFileName = null;
    BufferedImage backgroundImage = null;
    BufferedImage projectIcon = null;
    ResourceBundle resourceBundle = null;

    /** Creates new form FrameDBD4ToPostgres */
    public FrameDBD4ToPostgres() {
        //
        resourceBundle = java.util.ResourceBundle.getBundle("dbd4topostgres/resources/FrameDBD4ToPostgres");
        // create a Preferences instance (somewhere later in the code)
        this.preferences = Preferences.userNodeForPackage(this.getClass());
        //      
        // Set Background Image and Project Icon
        try {
            this.backgroundImage = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("dbd4topostgres/resources/x_centopeia.png")); //NOI18N
            this.projectIcon = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("dbd4topostgres/resources/x_centopeia_icone.png")); //NOI18N
        } catch (IOException e) {
        }


        this.initComponents();
        this.setIconImage(this.projectIcon);
        this.setTitle("DBD4ToPostgres"); //NOI18N
        this.cmdGenerateScript.setEnabled(false);
        //
        //


        // Last look and feel

        String lastLookAndFell = this.preferences.get("LAST_LOOKANDFELL", ""); //NOI18N
        if (lastLookAndFell == null || lastLookAndFell.trim().equals("")) { //NOI18N
            lastLookAndFell = "Moderate"; //NOI18N
        }
        ActionEvent action = new ActionEvent(new JRadioButton("LookAndFellAbstrato"), ActionEvent.ACTION_PERFORMED, lastLookAndFell); //NOI18N

        this.lookAndFellActionPerfomed(action);

        // Set last Charset selected on menu
        String lastCharsetSelected= this.preferences.get("LAST_CHARSET", "");
        if (lastCharsetSelected == null || lastCharsetSelected.trim().equals("")) { //NOI18N
            lastCharsetSelected = "Windows";
            this.radioWindowsCP1252.setSelected(true);
        } else {
            lastCharsetSelected = "Linux";
            this.radioLinuxUTF8.setSelected(true);
        }
        DBDesignerModel4.operationSystem =lastCharsetSelected;
        this.preferences.get("LAST_CHARSET", "Linux");//NOI18N
        // Open last file
        this.openLastModel();




    }

    private void openLastModel() {
        // Select the last open file
        try {


            String lastInputDir = this.preferences.get("LAST_INPUT_DIR", ""); //NOI18N
            if (lastInputDir != null && (!lastInputDir.trim().equals(""))) { //NOI18N

                File file = new File(lastInputDir);
                this.currentInputDirectory = file.getParentFile();


                //
                this.openModel(file);
            }

        } catch (Exception e) {
            //
        }


    }

    private void openModel(File file) throws Exception {
        String fileName = null;


        fileName = file.getPath();
        DBDesignerParser dbDesignerParser = new DBDesignerParser(file.getPath());

        // Load tables list from model
        Collection<String> tables = dbDesignerParser.getTables();
        // Sort table by names
        ArrayList arrayListTable = new ArrayList(tables);
        Collections.sort(arrayListTable);
        tables = arrayListTable;
        // Build vector from tables list
        Vector vectorTables = new Vector();

        for (String table : tables) {
            FrameDBD4ToPostgres.DBDesignerTableItem id = new FrameDBD4ToPostgres.DBDesignerTableItem(table);
            vectorTables.add(id);
        }

        this.listTableSelections.setListData(vectorTables);

        // --------------------------------------------------
        // Load data type from model
        HashMap<String, String> mapColumnsTypes = dbDesignerParser.getColumnsDataTypes();
        // Sort data type list by name
        ArrayList arrayListColumnsTypes = new ArrayList(mapColumnsTypes.values());
        Collections.sort(arrayListColumnsTypes);
        //

        // Map data types to Object[][]
        // Initially, the value is equals to source and target translation

        Object[][] arrayTipoColunas = new Object[arrayListColumnsTypes.size()][2];

        for (int i = 0; i < arrayListColumnsTypes.size(); i++) {

            arrayTipoColunas[i][0] = arrayListColumnsTypes.get(i);
            arrayTipoColunas[i][1] = arrayListColumnsTypes.get(i);

        }
        // Mov data type values to screen list
        DefaultTableModel modelTiposCampos = (DefaultTableModel) this.tableDatatypes.getModel();
        String[] jTableColumnNames = {" DBDesigner", "Script"}; //NOI18N
        modelTiposCampos.setDataVector(arrayTipoColunas, jTableColumnNames);


        this.cmdGenerateScript.setEnabled(true);
        //

        this.txtFileName = fileName;
        this.setTitle("DBD4ToPostgres - " + fileName); //NOI18N


    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupLookAndFeel = new javax.swing.ButtonGroup();
        buttonGroupObjectIdentification = new javax.swing.ButtonGroup();
        buttonGroupCharset = new javax.swing.ButtonGroup();
        panelMain = new javax.swing.JPanel();
        panelSelectOptions = new javax.swing.JPanel();
        panelChkOptions = new javax.swing.JPanel();
        chkDropTable = new javax.swing.JCheckBox();
        chkCreateTable = new javax.swing.JCheckBox();
        chkCreateView = new javax.swing.JCheckBox();
        chkAddForeignKey = new javax.swing.JCheckBox();
        chkAddForeignKeyWithRelationName = new javax.swing.JCheckBox();
        chkAddAlternateKey = new javax.swing.JCheckBox();
        chkCreateSequence = new javax.swing.JCheckBox();
        chkAddComments = new javax.swing.JCheckBox();
        chkStandardInserts = new javax.swing.JCheckBox();
        panelCmdsOptions = new javax.swing.JPanel();
        cmdSelectAllOptions = new javax.swing.JButton();
        cmdResetAllOptions = new javax.swing.JButton();
        cmdGenerateScript = new javax.swing.JButton();
        panelExtraConfigurations = new javax.swing.JPanel();
        panelOwner = new javax.swing.JPanel();
        lblOwner = new javax.swing.JLabel();
        txtOwner = new javax.swing.JTextField();
        panelObjectIdentification = new javax.swing.JPanel();
        chkObjectIdentification = new javax.swing.JCheckBox();
        radioWithOID = new javax.swing.JRadioButton();
        radioWithoutOID = new javax.swing.JRadioButton();
        panelTableSelections = new javax.swing.JPanel();
        lblTablesFromModel = new javax.swing.JLabel();
        scrollPaneTableSelections = new javax.swing.JScrollPane();
        listTableSelections = new javax.swing.JList();
        CheckListCellRenderer renderer = new CheckListCellRenderer();
        this.listTableSelections.setCellRenderer(renderer);
        this.listTableSelections.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        CheckListener lst = new CheckListener(this.listTableSelections);
        this.listTableSelections.addMouseListener(lst);
        this.listTableSelections.addKeyListener(lst);
        panelScriptResult = new javax.swing.JPanel();
        panelOptionsResultScript = new javax.swing.JPanel();
        cmdClearResult = new javax.swing.JButton();
        scrollPaneScriptResult = new RTextScrollPane();
        txtAreaScriptResult = new RSyntaxTextArea();
        ((RSyntaxTextArea) this.txtAreaScriptResult).setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);

        panelFieldsTranslate = new javax.swing.JPanel();
        lblTranslateDatatype = new javax.swing.JLabel();
        scroolPaneFieldsTranslate = new javax.swing.JScrollPane();
        tableDatatypes = new javax.swing.JTable();
        menuBarMain = new javax.swing.JMenuBar();
        javax.swing.JMenu menuFile = new javax.swing.JMenu();
        cmdOpenModel = new javax.swing.JMenuItem();
        cmdSaveScript = new javax.swing.JMenuItem();
        separatorCharset = new javax.swing.JPopupMenu.Separator();
        radioWindowsCP1252 = new javax.swing.JRadioButtonMenuItem();
        radioLinuxUTF8 = new javax.swing.JRadioButtonMenuItem();
        separatorSair = new javax.swing.JPopupMenu.Separator();
        cmdExit = new javax.swing.JMenuItem();
        menuLayout = new javax.swing.JMenu();
        rdLFAutumn = new javax.swing.JRadioButtonMenuItem();
        rdLFBusiness = new javax.swing.JRadioButtonMenuItem();
        rdLFBusinessBlackSteel = new javax.swing.JRadioButtonMenuItem();
        rdLFBusinessBlueSteel = new javax.swing.JRadioButtonMenuItem();
        rdLFChallengerDeep = new javax.swing.JRadioButtonMenuItem();
        rdLFCreme = new javax.swing.JRadioButtonMenuItem();
        rdLFCremeCoffee = new javax.swing.JRadioButtonMenuItem();
        rdLFEmeraldDusk = new javax.swing.JRadioButtonMenuItem();
        rdLFMagma = new javax.swing.JRadioButtonMenuItem();
        rdLFMistAqua = new javax.swing.JRadioButtonMenuItem();
        rdLFMistSilver = new javax.swing.JRadioButtonMenuItem();
        rdLFModerate = new javax.swing.JRadioButtonMenuItem();
        rdLFNebula = new javax.swing.JRadioButtonMenuItem();
        rdLFNebulaBrickWall = new javax.swing.JRadioButtonMenuItem();
        rdLFOfficeBlue2007 = new javax.swing.JRadioButtonMenuItem();
        rdLFOfficeSilver2007 = new javax.swing.JRadioButtonMenuItem();
        rdLFRaven = new javax.swing.JRadioButtonMenuItem();
        rdLFRavenGraphite = new javax.swing.JRadioButtonMenuItem();
        rdLFRavenGraphiteGlass = new javax.swing.JRadioButtonMenuItem();
        rdLFSahara = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JMenu menuHelp = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImages(null);
        setName("Form"); // NOI18N
        setState(JFrame.NORMAL);

        panelMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 20, 10));
        panelMain.setMinimumSize(panelMain.getPreferredSize());
        panelMain.setName("panelMain"); // NOI18N
        panelMain.setPreferredSize(new java.awt.Dimension(700, 500));
        panelMain.setRequestFocusEnabled(false);
        panelMain.setLayout(new java.awt.BorderLayout(5, 5));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(dbd4topostgres.Dbd4topostgresApp.class).getContext().getResourceMap(FrameDBD4ToPostgres.class);
        panelSelectOptions.setFont(resourceMap.getFont("panelSelectOptions.font")); // NOI18N
        panelSelectOptions.setMinimumSize(panelSelectOptions.getPreferredSize());
        panelSelectOptions.setName("panelSelectOptions"); // NOI18N
        panelSelectOptions.setPreferredSize(new java.awt.Dimension(700, 150));
        panelSelectOptions.setLayout(new java.awt.BorderLayout());

        panelChkOptions.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelChkOptions.setMinimumSize(panelChkOptions.getPreferredSize());
        panelChkOptions.setName("panelChkOptions"); // NOI18N
        panelChkOptions.setLayout(new java.awt.GridLayout(3, 3));

        chkDropTable.setText(resourceMap.getString("chkDropTable.text")); // NOI18N
        chkDropTable.setToolTipText(resourceMap.getString("chkDropTable.toolTipText")); // NOI18N
        chkDropTable.setName("chkDropTable"); // NOI18N
        chkDropTable.setPreferredSize(new java.awt.Dimension(80, 20));
        panelChkOptions.add(chkDropTable);

        chkCreateTable.setText(resourceMap.getString("chkCreateTable.text")); // NOI18N
        chkCreateTable.setToolTipText(resourceMap.getString("chkCreateTable.toolTipText")); // NOI18N
        chkCreateTable.setName("chkCreateTable"); // NOI18N
        chkCreateTable.setPreferredSize(new java.awt.Dimension(80, 20));
        panelChkOptions.add(chkCreateTable);

        chkCreateView.setText(resourceMap.getString("chkCreateView.text")); // NOI18N
        chkCreateView.setToolTipText(resourceMap.getString("chkCreateView.toolTipText")); // NOI18N
        chkCreateView.setName("chkCreateView"); // NOI18N
        chkCreateView.setPreferredSize(new java.awt.Dimension(80, 20));
        panelChkOptions.add(chkCreateView);

        chkAddForeignKey.setText(resourceMap.getString("chkAddForeignKey.text")); // NOI18N
        chkAddForeignKey.setToolTipText(resourceMap.getString("chkAddForeignKey.toolTipText")); // NOI18N
        chkAddForeignKey.setName("chkAddForeignKey"); // NOI18N
        chkAddForeignKey.setPreferredSize(new java.awt.Dimension(80, 20));
        chkAddForeignKey.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkAddForeignKeyStateChanged(evt);
            }
        });
        panelChkOptions.add(chkAddForeignKey);

        chkAddForeignKeyWithRelationName.setText(resourceMap.getString("chkAddForeignKeyWithRelationName.text")); // NOI18N
        chkAddForeignKeyWithRelationName.setToolTipText(resourceMap.getString("chkAddForeignKeyWithRelationName.toolTipText")); // NOI18N
        chkAddForeignKeyWithRelationName.setName("chkAddForeignKeyWithRelationName"); // NOI18N
        chkAddForeignKeyWithRelationName.setPreferredSize(new java.awt.Dimension(80, 20));
        chkAddForeignKeyWithRelationName.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkAddForeignKeyWithRelationNameStateChanged(evt);
            }
        });
        panelChkOptions.add(chkAddForeignKeyWithRelationName);

        chkAddAlternateKey.setText(resourceMap.getString("chkAddAlternateKey.text")); // NOI18N
        chkAddAlternateKey.setToolTipText(resourceMap.getString("chkAddAlternateKey.toolTipText")); // NOI18N
        chkAddAlternateKey.setName("chkAddAlternateKey"); // NOI18N
        chkAddAlternateKey.setPreferredSize(new java.awt.Dimension(80, 20));
        panelChkOptions.add(chkAddAlternateKey);

        chkCreateSequence.setText(resourceMap.getString("chkCreateSequence.text")); // NOI18N
        chkCreateSequence.setToolTipText(resourceMap.getString("chkCreateSequence.toolTipText")); // NOI18N
        chkCreateSequence.setName("chkCreateSequence"); // NOI18N
        chkCreateSequence.setPreferredSize(new java.awt.Dimension(80, 20));
        panelChkOptions.add(chkCreateSequence);

        chkAddComments.setText(resourceMap.getString("chkAddComments.text")); // NOI18N
        chkAddComments.setToolTipText(resourceMap.getString("chkAddComments.toolTipText")); // NOI18N
        chkAddComments.setName("chkAddComments"); // NOI18N
        chkAddComments.setPreferredSize(new java.awt.Dimension(80, 20));
        panelChkOptions.add(chkAddComments);

        chkStandardInserts.setText(resourceMap.getString("chkStandardInserts.text")); // NOI18N
        chkStandardInserts.setToolTipText(resourceMap.getString("chkStandardInserts.toolTipText")); // NOI18N
        chkStandardInserts.setName("chkStandardInserts"); // NOI18N
        chkStandardInserts.setPreferredSize(new java.awt.Dimension(80, 20));
        panelChkOptions.add(chkStandardInserts);

        panelSelectOptions.add(panelChkOptions, java.awt.BorderLayout.CENTER);

        panelCmdsOptions.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 50)));
        panelCmdsOptions.setMinimumSize(panelCmdsOptions.getPreferredSize());
        panelCmdsOptions.setName("panelCmdsOptions"); // NOI18N
        panelCmdsOptions.setPreferredSize(new java.awt.Dimension(250, 69));
        panelCmdsOptions.setLayout(new java.awt.GridLayout(3, 1, 0, 5));

        cmdSelectAllOptions.setBackground(resourceMap.getColor("cmdSelectAllOptions.background")); // NOI18N
        cmdSelectAllOptions.setIcon(resourceMap.getIcon("cmdSelectAllOptions.icon")); // NOI18N
        cmdSelectAllOptions.setText(resourceMap.getString("cmdSelectAllOptions.text")); // NOI18N
        cmdSelectAllOptions.setToolTipText(resourceMap.getString("cmdSelectAllOptions.toolTipText")); // NOI18N
        cmdSelectAllOptions.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 5, 12, 5));
        cmdSelectAllOptions.setContentAreaFilled(false);
        cmdSelectAllOptions.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cmdSelectAllOptions.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cmdSelectAllOptions.setMaximumSize(new java.awt.Dimension(100, 15));
        cmdSelectAllOptions.setMinimumSize(new java.awt.Dimension(100, 15));
        cmdSelectAllOptions.setName("cmdSelectAllOptions"); // NOI18N
        cmdSelectAllOptions.setPreferredSize(new java.awt.Dimension(150, 15));
        cmdSelectAllOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectAllOptionsActionPerformed(evt);
            }
        });
        panelCmdsOptions.add(cmdSelectAllOptions);

        cmdResetAllOptions.setBackground(resourceMap.getColor("cmdResetAllOptions.background")); // NOI18N
        cmdResetAllOptions.setIcon(resourceMap.getIcon("cmdResetAllOptions.icon")); // NOI18N
        cmdResetAllOptions.setText(resourceMap.getString("cmdResetAllOptions.text")); // NOI18N
        cmdResetAllOptions.setToolTipText(resourceMap.getString("cmdResetAllOptions.toolTipText")); // NOI18N
        cmdResetAllOptions.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 5, 12, 5));
        cmdResetAllOptions.setContentAreaFilled(false);
        cmdResetAllOptions.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cmdResetAllOptions.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cmdResetAllOptions.setMaximumSize(new java.awt.Dimension(100, 15));
        cmdResetAllOptions.setMinimumSize(new java.awt.Dimension(100, 15));
        cmdResetAllOptions.setName("cmdResetAllOptions"); // NOI18N
        cmdResetAllOptions.setPreferredSize(new java.awt.Dimension(150, 15));
        cmdResetAllOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdResetAllOptionsActionPerformed(evt);
            }
        });
        panelCmdsOptions.add(cmdResetAllOptions);

        cmdGenerateScript.setBackground(resourceMap.getColor("cmdGenerateScript.background")); // NOI18N
        cmdGenerateScript.setIcon(resourceMap.getIcon("cmdGenerateScript.icon")); // NOI18N
        cmdGenerateScript.setText(resourceMap.getString("cmdGenerateScript.text")); // NOI18N
        cmdGenerateScript.setToolTipText(resourceMap.getString("cmdGenerateScript.toolTipText")); // NOI18N
        cmdGenerateScript.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 5, 12, 5));
        cmdGenerateScript.setContentAreaFilled(false);
        cmdGenerateScript.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cmdGenerateScript.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cmdGenerateScript.setMaximumSize(new java.awt.Dimension(100, 15));
        cmdGenerateScript.setMinimumSize(new java.awt.Dimension(100, 15));
        cmdGenerateScript.setName("cmdGenerateScript"); // NOI18N
        cmdGenerateScript.setPreferredSize(new java.awt.Dimension(150, 15));
        cmdGenerateScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdGenerateScriptActionPerformed(evt);
            }
        });
        panelCmdsOptions.add(cmdGenerateScript);

        panelSelectOptions.add(panelCmdsOptions, java.awt.BorderLayout.EAST);

        panelExtraConfigurations.setMinimumSize(panelExtraConfigurations.getPreferredSize());
        panelExtraConfigurations.setName("panelExtraConfigurations"); // NOI18N
        panelExtraConfigurations.setPreferredSize(new java.awt.Dimension(906, 40));
        panelExtraConfigurations.setLayout(new java.awt.GridLayout(1, 0));

        panelOwner.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelOwner.setMinimumSize(panelOwner.getPreferredSize());
        panelOwner.setName("panelOwner"); // NOI18N
        panelOwner.setPreferredSize(new java.awt.Dimension(200, 20));
        panelOwner.setLayout(null);

        lblOwner.setText(resourceMap.getString("lblOwner.text")); // NOI18N
        lblOwner.setName("lblOwner"); // NOI18N
        panelOwner.add(lblOwner);
        lblOwner.setBounds(20, 10, 100, 20);

        txtOwner.setColumns(20);
        txtOwner.setName("txtOwner"); // NOI18N
        panelOwner.add(txtOwner);
        txtOwner.setBounds(150, 10, 170, 20);

        panelExtraConfigurations.add(panelOwner);

        panelObjectIdentification.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelObjectIdentification.setMinimumSize(panelObjectIdentification.getPreferredSize());
        panelObjectIdentification.setName("panelObjectIdentification"); // NOI18N
        panelObjectIdentification.setOpaque(false);
        panelObjectIdentification.setPreferredSize(new java.awt.Dimension(450, 20));
        panelObjectIdentification.setLayout(null);

        chkObjectIdentification.setText(resourceMap.getString("chkObjectIdentification.text")); // NOI18N
        chkObjectIdentification.setToolTipText(resourceMap.getString("chkObjectIdentification.toolTipText")); // NOI18N
        chkObjectIdentification.setName("chkObjectIdentification"); // NOI18N
        chkObjectIdentification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkObjectIdentificationActionPerformed(evt);
            }
        });
        panelObjectIdentification.add(chkObjectIdentification);
        chkObjectIdentification.setBounds(10, 10, 70, 22);

        buttonGroupObjectIdentification.add(radioWithOID);
        radioWithOID.setText(resourceMap.getString("radioWithOID.text")); // NOI18N
        radioWithOID.setEnabled(false);
        radioWithOID.setName("radioWithOID"); // NOI18N
        panelObjectIdentification.add(radioWithOID);
        radioWithOID.setBounds(230, 10, 100, 22);

        buttonGroupObjectIdentification.add(radioWithoutOID);
        radioWithoutOID.setSelected(true);
        radioWithoutOID.setText(resourceMap.getString("radioWithoutOID.text")); // NOI18N
        radioWithoutOID.setEnabled(false);
        radioWithoutOID.setName("radioWithoutOID"); // NOI18N
        panelObjectIdentification.add(radioWithoutOID);
        radioWithoutOID.setBounds(90, 10, 130, 22);

        panelExtraConfigurations.add(panelObjectIdentification);

        panelSelectOptions.add(panelExtraConfigurations, java.awt.BorderLayout.SOUTH);

        panelMain.add(panelSelectOptions, java.awt.BorderLayout.NORTH);

        panelTableSelections.setMinimumSize(panelTableSelections.getPreferredSize());
        panelTableSelections.setName("panelTableSelections"); // NOI18N
        panelTableSelections.setPreferredSize(new java.awt.Dimension(200, 350));
        panelTableSelections.setLayout(new java.awt.BorderLayout());

        lblTablesFromModel.setFont(resourceMap.getFont("lblTablesFromModel.font")); // NOI18N
        lblTablesFromModel.setForeground(resourceMap.getColor("lblTablesFromModel.foreground")); // NOI18N
        lblTablesFromModel.setText(resourceMap.getString("lblTablesFromModel.text")); // NOI18N
        lblTablesFromModel.setToolTipText(resourceMap.getString("lblTablesFromModel.toolTipText")); // NOI18N
        lblTablesFromModel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        lblTablesFromModel.setName("lblTablesFromModel"); // NOI18N
        lblTablesFromModel.setOpaque(true);
        lblTablesFromModel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        panelTableSelections.add(lblTablesFromModel, java.awt.BorderLayout.NORTH);

        scrollPaneTableSelections.setBackground(resourceMap.getColor("scrollPaneTableSelections.background")); // NOI18N
        scrollPaneTableSelections.setMaximumSize(new java.awt.Dimension(32767, 250));
        scrollPaneTableSelections.setName("scrollPaneTableSelections"); // NOI18N

        listTableSelections.setAlignmentX(1.0F);
        listTableSelections.setAlignmentY(2.0F);
        listTableSelections.setDragEnabled(true);
        listTableSelections.setFixedCellWidth(500);
        listTableSelections.setName("listTableSelections"); // NOI18N
        scrollPaneTableSelections.setViewportView(listTableSelections);

        panelTableSelections.add(scrollPaneTableSelections, java.awt.BorderLayout.CENTER);

        panelMain.add(panelTableSelections, java.awt.BorderLayout.WEST);

        panelScriptResult.setMinimumSize(panelScriptResult.getPreferredSize());
        panelScriptResult.setName("panelScriptResult"); // NOI18N
        panelScriptResult.setPreferredSize(new java.awt.Dimension(500, 300));
        panelScriptResult.setLayout(new java.awt.BorderLayout());

        panelOptionsResultScript.setName("panelOptionsResultScript"); // NOI18N

        cmdClearResult.setIcon(resourceMap.getIcon("cmdClearResult.icon")); // NOI18N
        cmdClearResult.setText(resourceMap.getString("cmdClearResult.text")); // NOI18N
        cmdClearResult.setToolTipText(resourceMap.getString("cmdClearResult.toolTipText")); // NOI18N
        cmdClearResult.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmdClearResult.setName("cmdClearResult"); // NOI18N
        cmdClearResult.setPreferredSize(new java.awt.Dimension(120, 36));
        cmdClearResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdClearResultActionPerformed(evt);
            }
        });
        panelOptionsResultScript.add(cmdClearResult);

        panelScriptResult.add(panelOptionsResultScript, java.awt.BorderLayout.SOUTH);

        scrollPaneScriptResult.setMinimumSize(scrollPaneScriptResult.getPreferredSize());
        scrollPaneScriptResult.setName("scrollPaneScriptResult"); // NOI18N
        scrollPaneScriptResult.setPreferredSize(new java.awt.Dimension(500, 350));

        txtAreaScriptResult.setColumns(20);
        txtAreaScriptResult.setRows(5);
        txtAreaScriptResult.setName("txtAreaScriptResult"); // NOI18N
        txtAreaScriptResult.setOpaque(false);
        scrollPaneScriptResult.setViewportView(txtAreaScriptResult);

        panelScriptResult.add(scrollPaneScriptResult, java.awt.BorderLayout.CENTER);

        panelMain.add(panelScriptResult, java.awt.BorderLayout.CENTER);

        panelFieldsTranslate.setMinimumSize(panelFieldsTranslate.getPreferredSize());
        panelFieldsTranslate.setName("panelFieldsTranslate"); // NOI18N
        panelFieldsTranslate.setPreferredSize(new java.awt.Dimension(200, 500));
        panelFieldsTranslate.setLayout(new java.awt.BorderLayout());

        lblTranslateDatatype.setFont(resourceMap.getFont("lblTranslateDatatype.font")); // NOI18N
        lblTranslateDatatype.setForeground(resourceMap.getColor("lblTranslateDatatype.foreground")); // NOI18N
        lblTranslateDatatype.setText(resourceMap.getString("lblTranslateDatatype.text")); // NOI18N
        lblTranslateDatatype.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        lblTranslateDatatype.setName("lblTranslateDatatype"); // NOI18N
        lblTranslateDatatype.setOpaque(true);
        lblTranslateDatatype.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        panelFieldsTranslate.add(lblTranslateDatatype, java.awt.BorderLayout.NORTH);

        scroolPaneFieldsTranslate.setBackground(resourceMap.getColor("scroolPaneFieldsTranslate.background")); // NOI18N
        scroolPaneFieldsTranslate.setMinimumSize(scroolPaneFieldsTranslate.getPreferredSize());
        scroolPaneFieldsTranslate.setName("scroolPaneFieldsTranslate"); // NOI18N

        tableDatatypes.setAutoCreateRowSorter(true);
        tableDatatypes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                " DBDesigner", "Script"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableDatatypes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableDatatypes.setDragEnabled(true);
        tableDatatypes.setName("tableDatatypes"); // NOI18N
        scroolPaneFieldsTranslate.setViewportView(tableDatatypes);

        panelFieldsTranslate.add(scroolPaneFieldsTranslate, java.awt.BorderLayout.CENTER);

        panelMain.add(panelFieldsTranslate, java.awt.BorderLayout.EAST);

        getContentPane().add(panelMain, java.awt.BorderLayout.CENTER);

        menuBarMain.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        menuBarMain.setFont(resourceMap.getFont("menuBarMain.font")); // NOI18N
        menuBarMain.setMargin(new java.awt.Insets(0, 10, 0, 10));
        menuBarMain.setName("menuBarMain"); // NOI18N
        menuBarMain.setPreferredSize(new java.awt.Dimension(700, 30));

        menuFile.setBackground(resourceMap.getColor("menuHelp.background")); // NOI18N
        menuFile.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(resourceMap.getColor("menuHelp.border.outsideBorder.lineColor"), 1, true), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5))); // NOI18N
        menuFile.setForeground(resourceMap.getColor("menuHelp.foreground")); // NOI18N
        menuFile.setText(resourceMap.getString("menuFile.text")); // NOI18N
        menuFile.setFont(resourceMap.getFont("menuHelp.font")); // NOI18N
        menuFile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuFile.setIconTextGap(10);
        menuFile.setName("menuFile"); // NOI18N
        menuFile.setPreferredSize(new java.awt.Dimension(100, 30));

        cmdOpenModel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        cmdOpenModel.setText(resourceMap.getString("cmdOpenModel.text")); // NOI18N
        cmdOpenModel.setToolTipText(resourceMap.getString("cmdOpenModel.toolTipText")); // NOI18N
        cmdOpenModel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        cmdOpenModel.setName("cmdOpenModel"); // NOI18N
        cmdOpenModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOpenModelActionPerformed(evt);
            }
        });
        menuFile.add(cmdOpenModel);

        cmdSaveScript.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        cmdSaveScript.setText(resourceMap.getString("cmdSaveScript.text")); // NOI18N
        cmdSaveScript.setToolTipText(resourceMap.getString("cmdSaveScript.toolTipText")); // NOI18N
        cmdSaveScript.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        cmdSaveScript.setName("cmdSaveScript"); // NOI18N
        cmdSaveScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveScriptActionPerformed(evt);
            }
        });
        menuFile.add(cmdSaveScript);

        separatorCharset.setName("separatorCharset"); // NOI18N
        menuFile.add(separatorCharset);

        buttonGroupCharset.add(radioWindowsCP1252);
        radioWindowsCP1252.setSelected(true);
        radioWindowsCP1252.setText(resourceMap.getString("radioWindowsCP1252.text")); // NOI18N
        radioWindowsCP1252.setToolTipText(resourceMap.getString("radioWindowsCP1252.toolTipText")); // NOI18N
        radioWindowsCP1252.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        radioWindowsCP1252.setName("radioWindowsCP1252"); // NOI18N
        radioWindowsCP1252.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioWindowsCP1252ActionPerformed(evt);
            }
        });
        menuFile.add(radioWindowsCP1252);

        buttonGroupCharset.add(radioLinuxUTF8);
        radioLinuxUTF8.setText(resourceMap.getString("radioLinuxUTF8.text")); // NOI18N
        radioLinuxUTF8.setToolTipText(resourceMap.getString("radioLinuxUTF8.toolTipText")); // NOI18N
        radioLinuxUTF8.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        radioLinuxUTF8.setName("radioLinuxUTF8"); // NOI18N
        radioLinuxUTF8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioLinuxUTF8ActionPerformed(evt);
            }
        });
        menuFile.add(radioLinuxUTF8);

        separatorSair.setName("separatorSair"); // NOI18N
        separatorSair.setOpaque(true);
        menuFile.add(separatorSair);

        cmdExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        cmdExit.setText(resourceMap.getString("cmdExit.text")); // NOI18N
        cmdExit.setToolTipText(resourceMap.getString("cmdExit.toolTipText")); // NOI18N
        cmdExit.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        cmdExit.setName("cmdExit"); // NOI18N
        cmdExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdExitActionPerformed(evt);
            }
        });
        menuFile.add(cmdExit);

        menuBarMain.add(menuFile);

        menuLayout.setBackground(resourceMap.getColor("menuHelp.background")); // NOI18N
        menuLayout.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(resourceMap.getColor("menuHelp.border.outsideBorder.lineColor"), 1, true), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5))); // NOI18N
        menuLayout.setForeground(resourceMap.getColor("menuHelp.foreground")); // NOI18N
        menuLayout.setText(resourceMap.getString("menuLayout.text")); // NOI18N
        menuLayout.setToolTipText(resourceMap.getString("menuLayout.toolTipText")); // NOI18N
        menuLayout.setFont(resourceMap.getFont("menuHelp.font")); // NOI18N
        menuLayout.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuLayout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuLayout.setName("menuLayout"); // NOI18N
        menuLayout.setPreferredSize(new java.awt.Dimension(100, 30));

        buttonGroupLookAndFeel.add(rdLFAutumn);
        rdLFAutumn.setText(resourceMap.getString("rdLFAutumn.text")); // NOI18N
        rdLFAutumn.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFAutumn.setName("rdLFAutumn"); // NOI18N
        rdLFAutumn.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFAutumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFAutumnActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFAutumn);

        buttonGroupLookAndFeel.add(rdLFBusiness);
        rdLFBusiness.setText(resourceMap.getString("rdLFBusiness.text")); // NOI18N
        rdLFBusiness.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFBusiness.setName("rdLFBusiness"); // NOI18N
        rdLFBusiness.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFBusiness.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFBusinessActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFBusiness);

        buttonGroupLookAndFeel.add(rdLFBusinessBlackSteel);
        rdLFBusinessBlackSteel.setText(resourceMap.getString("rdLFBusinessBlackSteel.text")); // NOI18N
        rdLFBusinessBlackSteel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFBusinessBlackSteel.setName("rdLFBusinessBlackSteel"); // NOI18N
        rdLFBusinessBlackSteel.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFBusinessBlackSteel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFBusinessBlackSteelActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFBusinessBlackSteel);

        buttonGroupLookAndFeel.add(rdLFBusinessBlueSteel);
        rdLFBusinessBlueSteel.setText(resourceMap.getString("rdLFBusinessBlueSteel.text")); // NOI18N
        rdLFBusinessBlueSteel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFBusinessBlueSteel.setName("rdLFBusinessBlueSteel"); // NOI18N
        rdLFBusinessBlueSteel.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFBusinessBlueSteel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFBusinessBlueSteelActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFBusinessBlueSteel);

        buttonGroupLookAndFeel.add(rdLFChallengerDeep);
        rdLFChallengerDeep.setText(resourceMap.getString("rdLFChallengerDeep.text")); // NOI18N
        rdLFChallengerDeep.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFChallengerDeep.setName("rdLFChallengerDeep"); // NOI18N
        rdLFChallengerDeep.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFChallengerDeep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFChallengerDeepActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFChallengerDeep);

        buttonGroupLookAndFeel.add(rdLFCreme);
        rdLFCreme.setText(resourceMap.getString("rdLFCreme.text")); // NOI18N
        rdLFCreme.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFCreme.setName("rdLFCreme"); // NOI18N
        rdLFCreme.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFCreme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFCremeActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFCreme);

        buttonGroupLookAndFeel.add(rdLFCremeCoffee);
        rdLFCremeCoffee.setText(resourceMap.getString("rdLFCremeCoffee.text")); // NOI18N
        rdLFCremeCoffee.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFCremeCoffee.setName("rdLFCremeCoffee"); // NOI18N
        rdLFCremeCoffee.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFCremeCoffee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFCremeCoffeeActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFCremeCoffee);

        buttonGroupLookAndFeel.add(rdLFEmeraldDusk);
        rdLFEmeraldDusk.setText(resourceMap.getString("rdLFEmeraldDusk.text")); // NOI18N
        rdLFEmeraldDusk.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFEmeraldDusk.setName("rdLFEmeraldDusk"); // NOI18N
        rdLFEmeraldDusk.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFEmeraldDusk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFEmeraldDuskActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFEmeraldDusk);

        buttonGroupLookAndFeel.add(rdLFMagma);
        rdLFMagma.setText(resourceMap.getString("rdLFMagma.text")); // NOI18N
        rdLFMagma.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFMagma.setName("rdLFMagma"); // NOI18N
        rdLFMagma.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFMagma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFMagmaActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFMagma);

        buttonGroupLookAndFeel.add(rdLFMistAqua);
        rdLFMistAqua.setText(resourceMap.getString("rdLFMistAqua.text")); // NOI18N
        rdLFMistAqua.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFMistAqua.setName("rdLFMistAqua"); // NOI18N
        rdLFMistAqua.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFMistAqua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFMistAquaActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFMistAqua);

        buttonGroupLookAndFeel.add(rdLFMistSilver);
        rdLFMistSilver.setText(resourceMap.getString("rdLFMistSilver.text")); // NOI18N
        rdLFMistSilver.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFMistSilver.setName("rdLFMistSilver"); // NOI18N
        rdLFMistSilver.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFMistSilver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFMistSilverActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFMistSilver);

        buttonGroupLookAndFeel.add(rdLFModerate);
        rdLFModerate.setText(resourceMap.getString("rdLFModerate.text")); // NOI18N
        rdLFModerate.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFModerate.setName("rdLFModerate"); // NOI18N
        rdLFModerate.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFModerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFModerateActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFModerate);

        buttonGroupLookAndFeel.add(rdLFNebula);
        rdLFNebula.setText(resourceMap.getString("rdLFNebula.text")); // NOI18N
        rdLFNebula.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFNebula.setName("rdLFNebula"); // NOI18N
        rdLFNebula.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFNebula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFNebulaActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFNebula);

        buttonGroupLookAndFeel.add(rdLFNebulaBrickWall);
        rdLFNebulaBrickWall.setText(resourceMap.getString("rdLFNebulaBrickWall.text")); // NOI18N
        rdLFNebulaBrickWall.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFNebulaBrickWall.setName("rdLFNebulaBrickWall"); // NOI18N
        rdLFNebulaBrickWall.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFNebulaBrickWall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFNebulaBrickWallActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFNebulaBrickWall);

        buttonGroupLookAndFeel.add(rdLFOfficeBlue2007);
        rdLFOfficeBlue2007.setText(resourceMap.getString("rdLFOfficeBlue2007.text")); // NOI18N
        rdLFOfficeBlue2007.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFOfficeBlue2007.setName("rdLFOfficeBlue2007"); // NOI18N
        rdLFOfficeBlue2007.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFOfficeBlue2007.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFOfficeBlue2007ActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFOfficeBlue2007);

        buttonGroupLookAndFeel.add(rdLFOfficeSilver2007);
        rdLFOfficeSilver2007.setText(resourceMap.getString("rdLFOfficeSilver2007.text")); // NOI18N
        rdLFOfficeSilver2007.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFOfficeSilver2007.setName("rdLFOfficeSilver2007"); // NOI18N
        rdLFOfficeSilver2007.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFOfficeSilver2007.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFOfficeSilver2007ActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFOfficeSilver2007);

        buttonGroupLookAndFeel.add(rdLFRaven);
        rdLFRaven.setText(resourceMap.getString("rdLFRaven.text")); // NOI18N
        rdLFRaven.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFRaven.setName("rdLFRaven"); // NOI18N
        rdLFRaven.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFRaven.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFRavenActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFRaven);

        buttonGroupLookAndFeel.add(rdLFRavenGraphite);
        rdLFRavenGraphite.setText(resourceMap.getString("rdLFRavenGraphite.text")); // NOI18N
        rdLFRavenGraphite.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFRavenGraphite.setName("rdLFRavenGraphite"); // NOI18N
        rdLFRavenGraphite.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFRavenGraphite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFRavenGraphiteActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFRavenGraphite);

        buttonGroupLookAndFeel.add(rdLFRavenGraphiteGlass);
        rdLFRavenGraphiteGlass.setText(resourceMap.getString("rdLFRavenGraphiteGlass.text")); // NOI18N
        rdLFRavenGraphiteGlass.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFRavenGraphiteGlass.setName("rdLFRavenGraphiteGlass"); // NOI18N
        rdLFRavenGraphiteGlass.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFRavenGraphiteGlass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFRavenGraphiteGlassActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFRavenGraphiteGlass);

        buttonGroupLookAndFeel.add(rdLFSahara);
        rdLFSahara.setText(resourceMap.getString("rdLFSahara.text")); // NOI18N
        rdLFSahara.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rdLFSahara.setName("rdLFSahara"); // NOI18N
        rdLFSahara.setPreferredSize(new java.awt.Dimension(200, 25));
        rdLFSahara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdLFSaharaActionPerformed(evt);
            }
        });
        menuLayout.add(rdLFSahara);

        menuBarMain.add(menuLayout);

        menuHelp.setBackground(resourceMap.getColor("menuHelp.background")); // NOI18N
        menuHelp.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(resourceMap.getColor("menuHelp.border.outsideBorder.lineColor"), 1, true), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5))); // NOI18N
        menuHelp.setForeground(resourceMap.getColor("menuHelp.foreground")); // NOI18N
        menuHelp.setText(resourceMap.getString("menuHelp.text")); // NOI18N
        menuHelp.setFont(resourceMap.getFont("menuHelp.font")); // NOI18N
        menuHelp.setPreferredSize(new java.awt.Dimension(100, 30));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(dbd4topostgres.Dbd4topostgresApp.class).getContext().getActionMap(FrameDBD4ToPostgres.class, this);
        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setToolTipText(resourceMap.getString("aboutMenuItem.toolTipText")); // NOI18N
        aboutMenuItem.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        menuHelp.add(aboutMenuItem);

        menuBarMain.add(menuHelp);

        setJMenuBar(menuBarMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkAddForeignKeyStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkAddForeignKeyStateChanged

        if (!this.chkAddForeignKey.isSelected()) {
            this.chkAddForeignKeyWithRelationName.setSelected(false);
        }
}//GEN-LAST:event_chkAddForeignKeyStateChanged

    private void chkAddForeignKeyWithRelationNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkAddForeignKeyWithRelationNameStateChanged

        if (this.chkAddForeignKeyWithRelationName.isSelected()) {
            this.chkAddForeignKey.setSelected(true);
        }
}//GEN-LAST:event_chkAddForeignKeyWithRelationNameStateChanged

    private void cmdSelectAllOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectAllOptionsActionPerformed

        this.chkAddAlternateKey.setSelected(true);
        this.chkAddForeignKey.setSelected(true);
        this.chkAddForeignKeyWithRelationName.setSelected(true);
        this.chkCreateSequence.setSelected(true);
        this.chkCreateTable.setSelected(true);
        this.chkCreateView.setSelected(true);
        this.chkDropTable.setSelected(true);
        this.chkAddComments.setSelected(true);
        this.chkStandardInserts.setSelected(true);
    }//GEN-LAST:event_cmdSelectAllOptionsActionPerformed

    private void cmdResetAllOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdResetAllOptionsActionPerformed
        this.chkAddAlternateKey.setSelected(false);
        this.chkAddForeignKey.setSelected(false);
        this.chkAddForeignKeyWithRelationName.setSelected(false);
        this.chkCreateSequence.setSelected(false);
        this.chkCreateTable.setSelected(false);
        this.chkCreateView.setSelected(false);
        this.chkDropTable.setSelected(false);
        this.chkAddComments.setSelected(false);
        this.chkStandardInserts.setSelected(false);
}//GEN-LAST:event_cmdResetAllOptionsActionPerformed

    private void cmdGenerateScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdGenerateScriptActionPerformed

        if (this.txtFileName.length() > 0) {
            try {
                DBDesignerParser dbdesignerParser = new DBDesignerParser(this.txtFileName);
                HashSet setTabelasSelecionadas = new HashSet();
                FrameDBD4ToPostgres.DBDesignerTableItem idTable = null;
                for (int i = 0; i < this.listTableSelections.getModel().getSize(); i++) {
                    idTable = (FrameDBD4ToPostgres.DBDesignerTableItem) this.listTableSelections.getModel().getElementAt(i);
                    if (idTable.isSelected()) {
                        setTabelasSelecionadas.add(idTable.getName());
                    }
                }

                // OID
                String descriptionOID = null;
                if (this.chkObjectIdentification.isSelected()) {
                    if (this.radioWithOID.isSelected()) {
                        descriptionOID = "WITH (OIDS=TRUE)"; //NOI18N
                    } else {
                        descriptionOID = "WITH (OIDS=FALSE)"; //NOI18N
                    }
                }
                //
                //
                StringBuilder scriptSql = new StringBuilder();
                if (this.chkCreateTable.isSelected() || this.chkAddComments.isSelected() || (this.chkCreateTable.isSelected() && this.chkDropTable.isSelected())) {
                    // Create a list of data types translations configured in screen
                    DefaultTableModel modelDatatypes = (DefaultTableModel) this.tableDatatypes.getModel();
                    Vector vectorDatatypes = modelDatatypes.getDataVector();
                    // Convert to hashmap
                    HashMap<String, String> mapDatatypesTranslation = new HashMap();
                    String sourceDatatype = null;
                    String targetDatatype = null;
                    for (int i = 0; i < vectorDatatypes.size(); i++) {
                        sourceDatatype = (String) ((Vector) vectorDatatypes.elementAt(i)).elementAt(0);
                        sourceDatatype = sourceDatatype.replaceAll(" ", ""); //NOI18N
                        //
                        targetDatatype = (String) ((Vector) vectorDatatypes.elementAt(i)).elementAt(1);
                        targetDatatype = targetDatatype.replaceAll(" ", ""); //NOI18N
                        //
                        mapDatatypesTranslation.put(sourceDatatype, targetDatatype);
                    }
                    // Create Table
                    //
                    scriptSql.append(dbdesignerParser.sqlCreateTable(setTabelasSelecionadas, mapDatatypesTranslation, this.txtOwner.getText(), descriptionOID, this.chkCreateTable.isSelected(), this.chkAddComments.isSelected(), this.chkDropTable.isSelected()));
                    scriptSql.append("\r\n"); //NOI18N
                }
                if (this.chkCreateView.isSelected() || (this.chkCreateView.isSelected() && this.chkDropTable.isSelected())) {
                    scriptSql.append(dbdesignerParser.sqlCreateView(setTabelasSelecionadas, this.txtOwner.getText(), this.chkCreateView.isSelected(), this.chkDropTable.isSelected()));
                    scriptSql.append("\r\n"); //NOI18N
                }
                if (this.chkAddForeignKey.isSelected()) {
                    scriptSql.append(dbdesignerParser.sqlCreateForeingKey(setTabelasSelecionadas, this.chkAddForeignKeyWithRelationName.isSelected()));
                    scriptSql.append("\r\n"); //NOI18N
                }
                if (this.chkAddAlternateKey.isSelected() || (this.chkAddAlternateKey.isSelected() && this.chkDropTable.isSelected())) {
                    scriptSql.append(dbdesignerParser.sqlCreateAlternatingKey(setTabelasSelecionadas, this.chkAddAlternateKey.isSelected(), this.chkDropTable.isSelected()));
                    scriptSql.append("\r\n"); //NOI18N
                }
                if (this.chkCreateSequence.isSelected() || (this.chkCreateSequence.isSelected() && this.chkDropTable.isSelected())) {
                    scriptSql.append(dbdesignerParser.sqlCreateSequence(setTabelasSelecionadas, this.txtOwner.getText(), this.chkCreateSequence.isSelected(), this.chkDropTable.isSelected()));
                    scriptSql.append("\r\n"); //NOI18N
                    if (this.chkCreateSequence.isSelected()) {
                        // Insert the sequence as default value
                        scriptSql.append(dbdesignerParser.sqlSetDefault(setTabelasSelecionadas));
                        scriptSql.append("\r\n"); //NOI18N
                    }
                }


                if (this.chkStandardInserts.isSelected()) {
                    scriptSql.append(dbdesignerParser.sqlCreateTableStandardInserts(setTabelasSelecionadas));
                    scriptSql.append("\r\n"); //NOI18N
                }

                this.txtAreaScriptResult.setText(scriptSql.toString());
                this.txtAreaScriptResult.setCaretPosition(0);

            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, resourceBundle.getString("fail.to.generate.sql.script") + e1.getMessage());
            }
        }
}//GEN-LAST:event_cmdGenerateScriptActionPerformed

    private void chkObjectIdentificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkObjectIdentificationActionPerformed
        if (this.chkObjectIdentification.isSelected()) {
            this.radioWithOID.setEnabled(true);
            this.radioWithoutOID.setEnabled(true);
        } else {
            this.radioWithOID.setEnabled(false);
            this.radioWithoutOID.setEnabled(false);
        }
}//GEN-LAST:event_chkObjectIdentificationActionPerformed

    private void cmdOpenModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOpenModelActionPerformed
        // Select last directory opened to save
        JFileChooser fc = null;
        //
        if (this.currentInputDirectory != null) {
            fc = new JFileChooser(this.currentInputDirectory);
        } else {
            String lastInputDir = this.preferences.get("LAST_INPUT_DIR", ""); //NOI18N
            if (lastInputDir != null && (!lastInputDir.trim().equals(""))) { //NOI18N
                fc = new JFileChooser(lastInputDir);
            } else {
                fc = new JFileChooser();
            }

        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileFilter fileFilter = new FileNameExtensionFilter("DB Designer Model 4 XML", "xml", "XML"); //NOI18N
        fc.addChoosableFileFilter(fileFilter);

        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);


        fc.setFileFilter(fileFilter);
        int res = fc.showDialog(this, resourceBundle.getString("open.dbdesigner4.model"));
        this.currentInputDirectory = fc.getCurrentDirectory();



        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();

                //
                this.preferences.put("LAST_INPUT_DIR", file.getAbsolutePath()); //NOI18N
                //
                this.openModel(file);

            } catch (FileNotFoundException e1) {
                JOptionPane.showMessageDialog(this, resourceBundle.getString("file.not.found"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, resourceBundle.getString("fail.in.read.file"));
            }


        } else {
            JOptionPane.showMessageDialog(this, resourceBundle.getString("file.not.selected"));
        }

    }//GEN-LAST:event_cmdOpenModelActionPerformed

    private void cmdSaveScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSaveScriptActionPerformed

        try {
            JFileChooser fc = null;
            //           
            if (this.currentOutputDirectory != null) {
                fc = new JFileChooser(this.currentOutputDirectory);
            } else {
                String lastOutputDir = this.preferences.get("LAST_OUTPUT_DIR", ""); //NOI18N
                if (lastOutputDir != null && (!lastOutputDir.trim().equals(""))) { //NOI18N
                    fc = new JFileChooser(lastOutputDir);
                } else {
                    if (this.currentInputDirectory != null) {
                        fc = new JFileChooser(this.currentInputDirectory.getPath());
                    } else {
                        fc = new JFileChooser();
                    }
                }

            }
            //

            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileFilter fileFilter = new FileNameExtensionFilter("Script SQL", "sql"); //NOI18N
            fc.addChoosableFileFilter(fileFilter);

            fc.setAcceptAllFileFilterUsed(false);
            fc.setMultiSelectionEnabled(false);


            fc.setFileFilter(fileFilter);
            int res = fc.showDialog(this, resourceBundle.getString("save.as"));

            this.currentOutputDirectory = fc.getCurrentDirectory();


            if (res == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                //
                this.preferences.put("LAST_OUTPUT_DIR", this.currentOutputDirectory.getAbsolutePath()); //NOI18N
                //

                String fileName = file.getPath();
                if (!fileName.endsWith(".sql")) { //NOI18N
                    fileName = fileName + ".sql"; //NOI18N
                    file = new File(fileName);
                }
                int saveOption = JOptionPane.YES_OPTION;
                if (file.exists()) {
                    //default icon, custom title
                    saveOption = JOptionPane.showConfirmDialog(this, resourceBundle.getString("file.already.exists.overwrite.it"), resourceBundle.getString("file.already.exists"), JOptionPane.YES_NO_OPTION);

                }
                if (saveOption == JOptionPane.YES_OPTION) {

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(this.txtAreaScriptResult.getText().getBytes());
                    fos.close();
                }


            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }//GEN-LAST:event_cmdSaveScriptActionPerformed

    private void cmdExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdExitActionPerformed

        this.dispose();
    }//GEN-LAST:event_cmdExitActionPerformed

    private void lookAndFellActionPerfomed(java.awt.event.ActionEvent evt) {
        try {
            String look = evt.getActionCommand();

            if (look.equals("Autumn")) { //NOI18N
                if (!this.rdLFAutumn.isSelected()) {
                    this.rdLFAutumn.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceAutumnLookAndFeel"); //NOI18N
                }
            } else if (look.equals("BusinessBlackSteel")) { //NOI18N
                if (!this.rdLFBusinessBlackSteel.isSelected()) {
                    this.rdLFBusinessBlackSteel.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel"); //NOI18N
                }
            } else if (look.equals("BusinessBlueSteel")) { //NOI18N
                if (!this.rdLFBusinessBlueSteel.isSelected()) {
                    this.rdLFBusinessBlueSteel.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel"); //NOI18N
                }
            } else if (look.equals("Business")) { //NOI18N
                if (!this.rdLFBusiness.isSelected()) {
                    this.rdLFBusiness.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessLookAndFeel"); //NOI18N
                }

            } else if (look.equals("ChallengerDeep")) { //NOI18N
                if (!this.rdLFChallengerDeep.isSelected()) {
                    this.rdLFChallengerDeep.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceChallengerDeepLookAndFeel"); //NOI18N
                }

            } else if (look.equals("CremeCoffee")) { //NOI18N
                if (!this.rdLFCremeCoffee.isSelected()) {
                    this.rdLFCremeCoffee.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel"); //NOI18N
                }
            } else if (look.equals("Creme")) { //NOI18N
                if (!this.rdLFCreme.isSelected()) {
                    this.rdLFCreme.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceCremeLookAndFeel"); //NOI18N
                }
            } else if (look.equals("EmeraldDusk")) { //NOI18N
                if (!this.rdLFEmeraldDusk.isSelected()) {
                    this.rdLFEmeraldDusk.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel"); //NOI18N
                }
            } else if (look.equals("Magma")) { //NOI18N
                if (!this.rdLFMagma.isSelected()) {
                    this.rdLFMagma.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceMagmaLookAndFeel"); //NOI18N
                }
            } else if (look.equals("MistAqua")) { //NOI18N
                if (!this.rdLFMistAqua.isSelected()) {
                    this.rdLFMistAqua.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel"); //NOI18N
                }
            } else if (look.equals("MistSilver")) { //NOI18N
                if (!this.rdLFMistSilver.isSelected()) {
                    this.rdLFMistSilver.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel"); //NOI18N
                }
            } else if (look.equals("Moderate")) { //NOI18N
                if (!this.rdLFModerate.isSelected()) {
                    this.rdLFModerate.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceModerateLookAndFeel"); //NOI18N
                }
            } else if (look.equals("NebulaBrickWall")) { //NOI18N
                if (!this.rdLFNebulaBrickWall.isSelected()) {
                    this.rdLFNebulaBrickWall.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceNebulaBrickWallLookAndFeel"); //NOI18N
                }
            } else if (look.equals("Nebula")) { //NOI18N
                if (!this.rdLFNebula.isSelected()) {
                    this.rdLFNebula.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceNebulaLookAndFeel"); //NOI18N
                }
            } else if (look.equals("OfficeBlue2007")) { //NOI18N
                if (!this.rdLFOfficeBlue2007.isSelected()) {
                    this.rdLFOfficeBlue2007.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel"); //NOI18N
                }
            } else if (look.equals("OfficeSilver2007")) { //NOI18N
                if (!this.rdLFOfficeSilver2007.isSelected()) {
                    this.rdLFOfficeSilver2007.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel"); //NOI18N
                }
            } else if (look.equals("RavenGraphiteGlass")) { //NOI18N
                if (!this.rdLFRavenGraphiteGlass.isSelected()) {
                    this.rdLFRavenGraphiteGlass.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel"); //NOI18N
                }
            } else if (look.equals("RavenGraphite")) { //NOI18N
                if (!this.rdLFRavenGraphite.isSelected()) {
                    this.rdLFRavenGraphite.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel"); //NOI18N
                }
            } else if (look.equals("Raven")) { //NOI18N
                if (!this.rdLFRaven.isSelected()) {
                    this.rdLFRaven.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenLookAndFeel"); //NOI18N
                }

            } else if (look.equals("Sahara")) { //NOI18N
                if (!this.rdLFSahara.isSelected()) {
                    this.rdLFSahara.doClick();
                } else {
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceSaharaLookAndFeel"); //NOI18N
                }

            }
            SwingUtilities.updateComponentTreeUI(this);



            this.preferences.put("LAST_LOOKANDFELL", look); //NOI18N
            // Set Background Image
            ((RSyntaxTextArea) this.txtAreaScriptResult).setBackgroundImage((Image) this.backgroundImage);


        } catch (Exception e) {
            //ignore
        }
    }

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed

        (new FrameHelp()).setVisible(true);
}//GEN-LAST:event_aboutMenuItemActionPerformed

    private void rdLFAutumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFAutumnActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFAutumnActionPerformed

    private void rdLFBusinessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFBusinessActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFBusinessActionPerformed

    private void rdLFBusinessBlackSteelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFBusinessBlackSteelActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFBusinessBlackSteelActionPerformed

    private void rdLFBusinessBlueSteelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFBusinessBlueSteelActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFBusinessBlueSteelActionPerformed

    private void rdLFChallengerDeepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFChallengerDeepActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFChallengerDeepActionPerformed

    private void rdLFCremeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFCremeActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFCremeActionPerformed

    private void rdLFCremeCoffeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFCremeCoffeeActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFCremeCoffeeActionPerformed

    private void rdLFEmeraldDuskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFEmeraldDuskActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFEmeraldDuskActionPerformed

    private void rdLFMagmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFMagmaActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFMagmaActionPerformed

    private void rdLFMistAquaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFMistAquaActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFMistAquaActionPerformed

    private void rdLFMistSilverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFMistSilverActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFMistSilverActionPerformed

    private void rdLFModerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFModerateActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFModerateActionPerformed

    private void rdLFNebulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFNebulaActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFNebulaActionPerformed

    private void rdLFNebulaBrickWallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFNebulaBrickWallActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFNebulaBrickWallActionPerformed

    private void rdLFOfficeBlue2007ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFOfficeBlue2007ActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFOfficeBlue2007ActionPerformed

    private void rdLFOfficeSilver2007ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFOfficeSilver2007ActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFOfficeSilver2007ActionPerformed

    private void rdLFRavenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFRavenActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFRavenActionPerformed

    private void rdLFRavenGraphiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFRavenGraphiteActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFRavenGraphiteActionPerformed

    private void rdLFRavenGraphiteGlassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFRavenGraphiteGlassActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFRavenGraphiteGlassActionPerformed

    private void rdLFSaharaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdLFSaharaActionPerformed
        this.lookAndFellActionPerfomed(evt);
    }//GEN-LAST:event_rdLFSaharaActionPerformed

    private void radioWindowsCP1252ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioWindowsCP1252ActionPerformed
      
        DBDesignerModel4.operationSystem = "Windows";//NOI18N
        this.preferences.put("LAST_CHARSET", "Windows"); //NOI18N
    }//GEN-LAST:event_radioWindowsCP1252ActionPerformed

    private void radioLinuxUTF8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioLinuxUTF8ActionPerformed

        DBDesignerModel4.operationSystem = "Linux";//NOI18N
        this.preferences.put("LAST_CHARSET", "Linux");//NOI18N
    }//GEN-LAST:event_radioLinuxUTF8ActionPerformed

private void cmdClearResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdClearResultActionPerformed
    this.txtAreaScriptResult.setText("");
}//GEN-LAST:event_cmdClearResultActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);

            java.awt.EventQueue.invokeLater(new Runnable() {

                public void run() {
                    new FrameDBD4ToPostgres().setVisible(true);
                }
            });
        } catch (Exception e) {
            // ignored
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupCharset;
    private javax.swing.ButtonGroup buttonGroupLookAndFeel;
    private javax.swing.ButtonGroup buttonGroupObjectIdentification;
    private javax.swing.JCheckBox chkAddAlternateKey;
    private javax.swing.JCheckBox chkAddComments;
    private javax.swing.JCheckBox chkAddForeignKey;
    private javax.swing.JCheckBox chkAddForeignKeyWithRelationName;
    private javax.swing.JCheckBox chkCreateSequence;
    private javax.swing.JCheckBox chkCreateTable;
    private javax.swing.JCheckBox chkCreateView;
    private javax.swing.JCheckBox chkDropTable;
    private javax.swing.JCheckBox chkObjectIdentification;
    private javax.swing.JCheckBox chkStandardInserts;
    private javax.swing.JButton cmdClearResult;
    private javax.swing.JMenuItem cmdExit;
    private javax.swing.JButton cmdGenerateScript;
    private javax.swing.JMenuItem cmdOpenModel;
    private javax.swing.JButton cmdResetAllOptions;
    private javax.swing.JMenuItem cmdSaveScript;
    private javax.swing.JButton cmdSelectAllOptions;
    private javax.swing.JLabel lblOwner;
    private javax.swing.JLabel lblTablesFromModel;
    private javax.swing.JLabel lblTranslateDatatype;
    private javax.swing.JList listTableSelections;
    private javax.swing.JMenuBar menuBarMain;
    private javax.swing.JMenu menuLayout;
    private javax.swing.JPanel panelChkOptions;
    private javax.swing.JPanel panelCmdsOptions;
    private javax.swing.JPanel panelExtraConfigurations;
    private javax.swing.JPanel panelFieldsTranslate;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelObjectIdentification;
    private javax.swing.JPanel panelOptionsResultScript;
    private javax.swing.JPanel panelOwner;
    private javax.swing.JPanel panelScriptResult;
    private javax.swing.JPanel panelSelectOptions;
    private javax.swing.JPanel panelTableSelections;
    private javax.swing.JRadioButtonMenuItem radioLinuxUTF8;
    private javax.swing.JRadioButtonMenuItem radioWindowsCP1252;
    private javax.swing.JRadioButton radioWithOID;
    private javax.swing.JRadioButton radioWithoutOID;
    private javax.swing.JRadioButtonMenuItem rdLFAutumn;
    private javax.swing.JRadioButtonMenuItem rdLFBusiness;
    private javax.swing.JRadioButtonMenuItem rdLFBusinessBlackSteel;
    private javax.swing.JRadioButtonMenuItem rdLFBusinessBlueSteel;
    private javax.swing.JRadioButtonMenuItem rdLFChallengerDeep;
    private javax.swing.JRadioButtonMenuItem rdLFCreme;
    private javax.swing.JRadioButtonMenuItem rdLFCremeCoffee;
    private javax.swing.JRadioButtonMenuItem rdLFEmeraldDusk;
    private javax.swing.JRadioButtonMenuItem rdLFMagma;
    private javax.swing.JRadioButtonMenuItem rdLFMistAqua;
    private javax.swing.JRadioButtonMenuItem rdLFMistSilver;
    private javax.swing.JRadioButtonMenuItem rdLFModerate;
    private javax.swing.JRadioButtonMenuItem rdLFNebula;
    private javax.swing.JRadioButtonMenuItem rdLFNebulaBrickWall;
    private javax.swing.JRadioButtonMenuItem rdLFOfficeBlue2007;
    private javax.swing.JRadioButtonMenuItem rdLFOfficeSilver2007;
    private javax.swing.JRadioButtonMenuItem rdLFRaven;
    private javax.swing.JRadioButtonMenuItem rdLFRavenGraphite;
    private javax.swing.JRadioButtonMenuItem rdLFRavenGraphiteGlass;
    private javax.swing.JRadioButtonMenuItem rdLFSahara;
    private javax.swing.JScrollPane scrollPaneScriptResult;
    private javax.swing.JScrollPane scrollPaneTableSelections;
    private javax.swing.JScrollPane scroolPaneFieldsTranslate;
    private javax.swing.JPopupMenu.Separator separatorCharset;
    private javax.swing.JPopupMenu.Separator separatorSair;
    private javax.swing.JTable tableDatatypes;
    private javax.swing.JTextArea txtAreaScriptResult;
    private javax.swing.JTextField txtOwner;
    // End of variables declaration//GEN-END:variables

    class CheckListCellRenderer extends JCheckBox
            implements ListCellRenderer {

        protected Border m_noFocusBorder = new EmptyBorder(1, 1, 1, 1);

        public CheckListCellRenderer() {
            setOpaque(true);
            setBorder(this.m_noFocusBorder);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.toString());

            setBackground(isSelected ? list.getSelectionBackground()
                    : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground()
                    : list.getForeground());

            FrameDBD4ToPostgres.DBDesignerTableItem data = (FrameDBD4ToPostgres.DBDesignerTableItem) value;
            setSelected(data.isSelected());

            setFont(list.getFont());
            setBorder(cellHasFocus
                    ? UIManager.getBorder("List.focusCellHighlightBorder") //NOI18N
                    : this.m_noFocusBorder);

            return this;
        }
    }

    class DBDesignerTableItem {

        protected String m_name;
        protected boolean m_selected;

        public DBDesignerTableItem(String name) {
            this.m_name = name;
            this.m_selected = false;
        }

        public String getName() {
            return this.m_name;
        }

        public void setSelected(boolean selected) {
            this.m_selected = selected;
        }

        public void invertSelected() {
            this.m_selected = (!this.m_selected);
        }

        public boolean isSelected() {
            return this.m_selected;
        }

        @Override
        public String toString() {
            return this.m_name;
        }
    }

    class CheckListener implements MouseListener, KeyListener {

        protected JList tableList;

        public CheckListener(JList tableList) {
            this.tableList = tableList;
        }

        public void mouseClicked(MouseEvent e) {
            doCheck();
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == ' ') {
                doCheck();
            }
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
        }

        protected void doCheck() {
            int index[] = this.tableList.getSelectedIndices();
            if (index.length == 0) {
                return;
            }
            FrameDBD4ToPostgres.DBDesignerTableItem data = null;

            for (int i = 0; i < index.length; i++) {
                data = (FrameDBD4ToPostgres.DBDesignerTableItem) this.tableList.getModel().getElementAt(index[i]);
                data.invertSelected();
            }
            this.tableList.repaint();
        }
    }
}
