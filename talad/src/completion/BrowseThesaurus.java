package completion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("unused")
public class BrowseThesaurus extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String[] elements = { "Termsciences","SIL Glossary of Linguistic Terms"};
	private JComboBox<String> box;
    private DefaultComboBoxModel<String> model;
	public JLabel label;
	public JTextField field;
	public JTextField efield;
	public JButton button;
	public String result=null;
	public BrowseThesaurus(String s)
	{
		super("Parcours de thesaurus");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		label=new JLabel("Recherche de "+s);
		model = new DefaultComboBoxModel<String>();

		 for (int i = 0; i < elements.length; i++) 
		 model.addElement((String)elements[i]);
		 box=new JComboBox<String>(model);
		 model.setSelectedItem("Termsciences");
		   
		 	// Размещение компонентов в интерфейсе и вывод окна
	        JPanel contents = new JPanel();
	        contents.setBackground(Color.ORANGE);
	        contents.getCursor();
	        
	        this.field=new JTextField(s,50);
	        this.efield=new JTextField("ici le terme en anglais",50);
	        this.button=new JButton("Rechercher");
	        button.setVisible(true);
	        button.addActionListener(new ButtonAction()); 
	        
	     
	        contents.add (label);
	        contents.add (field);        
	        contents.add (box);
	        contents.add (button);
	        contents.add (label);
	        
	       
//	        Container cont	=getContentPane();
//	        cont.add(contents,BorderLayout.EAST);
//	       
//	        cont.setSize(600, 180);
	        			
	        
	        setContentPane(contents);
	        		
	        setSize(600, 180);
	        setVisible(true);
	}
	            
            class ButtonAction implements ActionListener
        	{
        		public void actionPerformed(ActionEvent e)
        		{
        			try 
        			{
        				 JOptionPane.showMessageDialog(BrowseThesaurus.this, "Recherche en cours.... ");
        				dispose();
        			
        			} 
        			catch (Exception e1) {
        				// TODO Auto-generated catch block
        				e1.printStackTrace();
        			}
        		}
        	}
	
   
}
