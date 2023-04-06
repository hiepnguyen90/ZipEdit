package rocks.zipcode.textedit;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ZipEdit extends JFrame implements ActionListener{
    private JTextArea area;
    private JFrame frame;
    private String filename = "untitled";
    public ZipEdit() {  }

    public static void main(String[] args) {
        ZipEdit runner = new ZipEdit();
        runner.run();
    }


    public void run() {
        frame = new JFrame(frameTitle());

        // Set the look-and-feel (LNF) of the application
        // Try to default to whatever the host system prefers
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ZipEdit.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Set attributes of the app window
        area = new JTextArea();
        //Border blackline = BorderFactory.createLineBorder(Color.black);
        //area.setBorder(blackline);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        area.setText("");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(area);
        frame.setLocationRelativeTo(null);
        frame.setSize(640, 480);

        // Build the menu
        JMenuBar menu_main = new JMenuBar();

        JMenu menu_file = new JMenu("File");

        JMenuItem menuitem_new = new JMenuItem("New");
        JMenuItem menuitem_open = new JMenuItem("Open");
        JMenuItem menuitem_save = new JMenuItem("Save");
        JMenuItem menuitem_quit = new JMenuItem("Quit");

        menuitem_new.addActionListener(this);
        menuitem_open.addActionListener(this);
        menuitem_save.addActionListener(this);
        menuitem_quit.addActionListener(this);

        menu_main.add(menu_file);

        menu_file.add(menuitem_new);
        menu_file.add(menuitem_open);
        menu_file.add(menuitem_save);
        menu_file.add(menuitem_quit);

        JMenu edit_file = new JMenu("Edit");




        JMenuItem edititem_cut = new JMenuItem("Cut");
        JMenuItem edititem_copy = new JMenuItem("Copy");
        JMenuItem edititem_paste = new JMenuItem("Paste");
        JMenuItem edititem_find = new JMenuItem("Find");

        edititem_cut.addActionListener(this);
        edititem_copy.addActionListener(this);
        edititem_paste.addActionListener(this);
        edititem_find.addActionListener(this);

        menu_main.add(edit_file);


        edit_file.add(edititem_cut);
        edit_file.add(edititem_copy);
        edit_file.add(edititem_paste);
        edit_file.add(edititem_find);

        frame.setJMenuBar(menu_main);

        frame.setVisible(true);

    }

    public String frameTitle() {
        return "Zip Edit ("+this.filename+")";
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String ingest = "";
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        String ae = e.getActionCommand();
        int returnValue;
        if (ae.equals("Open")) {
            returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File f = new File(jfc.getSelectedFile().getAbsolutePath());
                this.filename = jfc.getSelectedFile().getName();
                this.frame.setTitle(this.frameTitle());
                try {
                    FileReader read = new FileReader(f);
                    Scanner scan = new Scanner(read);
                    while (scan.hasNextLine()) {
                        String line = scan.nextLine() + "\n";
                        ingest = ingest + line;
                    }
                    area.setText(ingest);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            // SAVE
        } else if (ae.equals("Save")) {
            returnValue = jfc.showSaveDialog(null);
            this.filename = jfc.getSelectedFile().getName();
            this.frame.setTitle(this.frameTitle());
            try {
                File f = new File(jfc.getSelectedFile().getAbsolutePath());
                FileWriter out = new FileWriter(f);
                out.write(area.getText());
                out.close();
            } catch (FileNotFoundException ex) {
                Component f = null;
                JOptionPane.showMessageDialog(f, "File not found.");
            } catch (IOException ex) {
                Component f = null;
                JOptionPane.showMessageDialog(f, "Error.");
            }
        }else if (ae.equals("Cut")) {
            String selectedText = area.getSelectedText();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(selectedText);
            clipboard.setContents(selection, null);
            area.replaceSelection("");

        }else if (ae.equals("Copy")){
            String s = area.getText();
            StringSelection ss = new StringSelection(s);
            this.getToolkit().getSystemClipboard().setContents(ss, ss);

        }else if (ae.equals("Paste")) {
            Clipboard c = this.getToolkit().getSystemClipboard();
            Transferable t = c.getContents(this);
            try {
                String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                area.setText(s);
            } catch (Exception z) {
                this.getToolkit().beep();
                return;
            }
        }else if (ae.equals("Find")) {
            String searchText = JOptionPane.showInputDialog(area, "Find:");
            if(searchText != null && !searchText.isEmpty()) {
                String content = area.getText();
                int index = content.indexOf(searchText);
                if(index != -1) {
                    area.setCaretPosition(index);
                    area.moveCaretPosition(index + searchText.length());
                } else {
                    JOptionPane.showMessageDialog(area, "Text not found.");
                }
            }
        }else if (ae.equals("New")) {
            area.setText("");
        } else if (ae.equals("Quit")) {
            System.exit(0);
        }
    }
}


