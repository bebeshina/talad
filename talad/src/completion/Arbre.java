package completion;

//import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
//import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileWriter;
import java.io.IOException;
//import java.io.PrintWriter;
import java.sql.SQLException;
//import java.text.DateFormat;
import java.util.Enumeration;
import java.util.HashMap;

import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
//import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;

import data.TNRDataHandler;
//import data.TNREmbeddedDataHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("unused")
public class Arbre extends JFrame
{
		final Logger logger = LoggerFactory.getLogger(Arbre.class);
	
		private static final long serialVersionUID = 1L;
		private javax.swing.JPanel jContentPane = null;
		//**************
		public JTree              jTree        = null;
		
		public JPanel panel = null;
		public TreePath tp; 
		public TNRDataHandler dh=new TNRDataHandler(); 
//		public TNREmbeddedDataHandler dh=new TNREmbeddedDataHandler();
		
		public Map<String,String>em=new HashMap<String,String>();
		
		@SuppressWarnings("unused")
		private JTree getJTree() 
		{
			if (jTree == null) 
			{
				jTree = new JTree();
				
			}
			return jTree;
		}

		public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException, SQLException, ConfigurationException 
		{
			Arbre tree = new Arbre();
			tree.setVisible(true);
//			System.exit(1);
		}

		public Arbre()throws IllegalAccessException,InstantiationException,SQLException,ClassNotFoundException,ConfigurationException 
		{
			super();
			try 
			{
				initialize();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		

		public TreePath find(DefaultMutableTreeNode root, String s) {
		    Enumeration<TreeNode> e = root.depthFirstEnumeration();
		    DefaultMutableTreeNode candidate=null;
		    while (e.hasMoreElements()) 
		    {
		       candidate=(DefaultMutableTreeNode) e.nextElement();
		       System.out.println(candidate.toString());
		      
		    }
		    return null;
		}
		public void getMutableJTree() 
		{
			if (jTree == null) 
			{
				DefaultMutableTreeNode racine = new DefaultMutableTreeNode("TNR : catégories");
				DefaultMutableTreeNode noeud;
		
				for(String s:dh.categories())
				{
					noeud = new DefaultMutableTreeNode(s);
					
					DefaultMutableTreeNode snoeud;
//					System.out.println("taille de l'ensemble des sous-catégories "+dh.tree_map.size());
					for(Entry<String,String> e:dh.tree_map.entrySet())
					{
						if(e.getValue().equals(s))
						{
							DefaultMutableTreeNode entry;
							//rechercher les is_a de snoeud (des termes de tête) + vérifier le catégories
							snoeud=new DefaultMutableTreeNode(e.getKey());
							for(String str:dh.stree_map.get(e.getKey()))
							{
								entry=new DefaultMutableTreeNode(str);
								snoeud.add(entry);
								
							}
							noeud.add(snoeud);
						}
					}
					racine.add(noeud);
				}
				
				jTree = new JTree(racine);
				jTree.addTreeSelectionListener(new TreeSelectionListener()
						{
					 		public void valueChanged(TreeSelectionEvent e)
					 		{
					 			 DefaultMutableTreeNode lenoeud = 
					 				     (DefaultMutableTreeNode)jTree.getLastSelectedPathComponent();
					 			 			String s=lenoeud.toString();
					 			 			//******************************
					 			 			
					 			 			try {
//					 			 				ti.start(s);
												new TestInsert(s);
											} catch (ClassNotFoundException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											} catch (InstantiationException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											} catch (IllegalAccessException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											} catch (ConfigurationException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											} catch (SQLException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											} catch (IOException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
//					 			 			transit(s);
//					 			 			JFrame edit=new JFrame();
					 			 			catch (Exception e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
					 			 		
					 		}
						});
				
				
			}
			//ajouter un bouton quitter qui charge tout en base
			

			
//			jTree.add(new JButton("Quitter")).setEnabled(false);
			
//			TreeCellRenderer renderer = new TreeCellRenderer();
//			
//			jTree.setCellRenderer(renderer);
//			setDefaultCloseOperation(EXIT_ON_CLOSE);
			
		}
		
		class ExportAction implements ActionListener
		{
			
//			public File dir=new File("export_base");
//			mkdir()
			public File ft=new File("./termes.csv");
			public File fr=new File("./relations.csv");
			
			public void actionPerformed(ActionEvent e)
			{
//				try {
//					dh.exportTermes(ft);
//				} catch (IOException | SQLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				try {
//					dh.exportRelations(fr);
//				} catch (IOException | SQLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				
			}
		}
			
			
		
		public JPanel transition;
		public JFrame entry;
		public JButton tb;
		public JTextPane def;
		public JTextField ex;
		public JTextField var;
		public JTextField hyper;
		public JTextField dom;
		public JLabel l_hyper;
		public JLabel l_dom;
		public JLabel l_def;
		public JLabel l_ex;
		public JLabel l_var;
		public StyledDocument sdef;
		
		public JButton export;
		
		public void getDefinition(String s)
		{
			def=new JTextPane();
			Style defaut = def.getStyle("default");
			Style style_def = def.addStyle("style_def", defaut);
			SimpleAttributeSet align = new SimpleAttributeSet();
			StyleConstants.setAlignment(align,StyleConstants.ALIGN_JUSTIFIED);
		    StyleConstants.setFontFamily(style_def, "Arial");
		    StyleConstants.setForeground(style_def, Color.DARK_GRAY);
		    StyleConstants.setFontSize(style_def, 14);
		    Style style_tit = def.addStyle("style_tit", style_def);
		    StyleConstants.setBold(style_tit, true);
		    
		}

		public JFrame test(String s)
		{
			entry=new JFrame();
			
			entry=new JFrame();
			entry.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			entry.setSize(350, 450);
			entry.setLocationRelativeTo(null);
		
			
			
			def=new JTextPane();
			/**
			 * définition du style
			 */
			Style defaut = def.getStyle("default");
			Style style_def = def.addStyle("style_def", defaut);
			SimpleAttributeSet align = new SimpleAttributeSet();
			StyleConstants.setAlignment(align,StyleConstants.ALIGN_JUSTIFIED);
		    StyleConstants.setFontFamily(style_def, "Arial");
		    StyleConstants.setForeground(style_def, Color.DARK_GRAY);
		    StyleConstants.setFontSize(style_def, 14);
		    Style style_tit = def.addStyle("style_tit", style_def);
		    StyleConstants.setBold(style_tit, true);
		    
		    try
			{
				
				Map<String,String> entry=new HashMap<String,String>();
				entry=dh.EntryData(s, dh.psGetRelations, dh.psShowRelations);
				if(entry.size()>0)
				{
					if(entry.containsKey("hyperonyme"))
					{
						l_hyper=new JLabel();
						l_hyper.setText("Hyperonyme"+"\n");
						hyper.setText(entry.get("hyperonyme"));
						hyper.isEditable();
					}
					else
					{
						hyper.setText("pas d'hyperonyme");
					}
					if(entry.containsKey("définition"))
					{
						l_def.setText("Définition \r\n");
						sdef=(StyledDocument) def.getStyledDocument();
						try
						{
							int pos=0;
							sdef.insertString(pos,"Définition",style_tit);
							sdef.insertString(pos, entry.get("définition"),style_def);
						}
						catch (BadLocationException e) { }
					}
					else
					{
						def.setText("pas de définition");
					}
					if(entry.containsKey("exemple"))
					{
						ex.setText(entry.get("exemple"));
					}
					else
					{
						ex.setText("pas d'exemple");
					}
					if(entry.containsKey("domaine"))
					{
						dom.setText(entry.get("domaine"));
					}
					else
					{
						dom.setText("domaine inconnu");
					}	
				}
			}
			catch(Exception ex)
			{
				
			}
		    
			entry.add((Component)sdef);
		
			entry.setVisible(true);
			return entry;
		}
		
		public TestInsert ti;
		
		public JPanel transition(String s)
		{
			transition=new JPanel();
			
			dom=new JTextField();
			ex=new JTextField();
			var=new JTextField();
			hyper=new JTextField();
			
			def=new JTextPane();
			Style defaut = def.getStyle("default");
			Style style_def = def.addStyle("style_def", defaut);
			SimpleAttributeSet align = new SimpleAttributeSet();
			StyleConstants.setAlignment(align,StyleConstants.ALIGN_JUSTIFIED);
		    StyleConstants.setFontFamily(style_def, "Arial");
		    StyleConstants.setForeground(style_def, Color.DARK_GRAY);
		    StyleConstants.setFontSize(style_def, 14);
		    Style style_tit = def.addStyle("style_tit", style_def);
		    StyleConstants.setBold(style_tit, true);
			/**
			 * définition des champs de texte d'une entrée
			 */
			try
			{
//				TNREmbeddedDataHandler dh=new TNREmbeddedDataHandler();
				Map<String,String> entry=new HashMap<String,String>();
				entry=dh.EntryData(s, dh.psGetRelations, dh.psShowRelations);
				if(entry.size()>0)
				{
					if(entry.containsKey("hyperonyme"))
					{
						l_hyper=new JLabel();
						l_hyper.setText("Hyperonyme"+"\n");
						hyper.setText(entry.get("hyperonyme"));
						hyper.isEditable();
					}
					else
					{
						hyper.setText("pas d'hyperonyme");
					}
					if(entry.containsKey("définition"))
					{
						l_def.setText("Définition \r\n");
						sdef=(StyledDocument) def.getStyledDocument();
						try
						{
							int pos=0;
							sdef.insertString(pos,"Définition",style_tit);
							sdef.insertString(pos, entry.get("définition"),style_def);
						}
						catch (BadLocationException e) { }
						
						
					}
					else
					{
						def.setText("pas de définition");
					}
					if(entry.containsKey("exemple"))
					{
						ex.setText(entry.get("exemple"));
					}
					else
					{
						ex.setText("pas d'exemple");
					}
					if(entry.containsKey("domaine"))
					{
						dom.setText(entry.get("domaine"));
					}
					else
					{
						dom.setText("domaine inconnu");
					}	
				}
			}
			catch(Exception ex)
			{
				
			}
			tb=new JButton();
			tb.setPreferredSize(new Dimension(200,25));
			tb.setText("Valider");
//			tb.addItemListener(this);
			
			
			
//			transition.add(dom);
//			transition.add(l_hyper);
//			transition.add(hyper);
			transition.add(def);
//			transition.add(ex);
//			transition.add(tb);
////			transition.setVisible(true);
			return transition;
//			return entry;
		}
		
		public void transit(String s)
		{
			this.setContentPane(transition(s));
			this.setContentPane(test(s));
			this.setTitle(s); //On donne un titre à l’application
			this.setSize(600,300); //On donne une taille à notre fenêtre
			this.setLocationRelativeTo(null); //On centre la fenêtre sur l’écran
			this.setResizable(true) ; //On interdit la redimensionnement de la fenêtre
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //On dit à l’application de se fermer lors du clic sur la croix	
		}
		
		public void createContributionDirectory() throws IOException
		{
			File dir=new File("./contrib");
			dir.mkdir();
			
		}
		
		private void initialize() throws IOException 
		{
			this.createContributionDirectory();
			
			this.setSize(600, 900);
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(dim.width/2 - this.getWidth()/2, dim.height/2 - this.getHeight()/2);
			this.getMutableJTree();
			this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//EXIT_ON_CLOSE
			
//			this.panel=new JPanel(new GridLayout(0, 3));
//			export=new JButton("Exporter les données");
//			export.addActionListener(new ExportAction());
//			export.setEnabled(true);
//			panel.add(export);
//			this.add(panel, BorderLayout.SOUTH);
			this.setContentPane(getJContentPane());
			this.setTitle("TNR : arbre");
			
		}
		
		private javax.swing.JPanel getJContentPane() 
		{
			if (jContentPane == null) 
			{
				jContentPane = new javax.swing.JPanel();
				jContentPane.setLayout(new java.awt.BorderLayout());
				jContentPane.add(jTree, java.awt.BorderLayout.CENTER);
			}
			return jContentPane;
		}
}
