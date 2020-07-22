package annotation;



import static org.junit.Assert.assertTrue;

import java.awt.BorderLayout;
import java.awt.Color;
//import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
//import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
//import java.awt.GridLayout;
import java.awt.Image;
//import java.awt.Scrollbar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
//import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
//import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
//import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;
//import javax.swing.JTextPane;
//import javax.swing.JToggleButton;
//import javax.swing.ScrollPaneConstants;
//import javax.swing.ScrollPaneLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotatePhrase extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;

	/**construction du formulaire d'annotation à partir d'une liste de phrases à annoter*/
	final Logger logger = LoggerFactory.getLogger(AnnotatePhrase.class);
	
	/**
	 * polices utilisées
	 * TODO enumeration
	 */
	
	public Font paragraph=new Font("Times", Font.PLAIN, 16);//Font.DIALOG, Font.TRUETYPE_FONT,  14);
	public Font title=new Font("Times", Font.BOLD, 16);//Font.DIALOG, Font.TRUETYPE_FONT,  14);
	public Font subtitle=new Font("Times", Font.BOLD, 14);//Font.DIALOG, Font.TRUETYPE_FONT,  14);
	public enum Fonts 
	{
//		title(Font.DIALOG, Font.BOLD,  16),
//		subtitle(Font.DIALOG, Font.BOLD,  16),
//		paragraph(Font.DIALOG, Font.PLAIN,  16);
//		
//		private Font font;
//		private Font decoration;
//		private Integer size;
//		
//		private Fonts(Font font, Font decoration,Integer size)
//		{
//			this.font      = font;
//			this.decoration = decoration; 
//			this.size=size;
//		}
//	
//		
	};
	
	/**partie gauche : texte à annoter*/
	
	public JTextArea texte,commentaire;
	
	public JCheckBox cb;
	public ButtonGroup bg;
	
	/**
	 * partie droite 
	 * création de box de cases à cocher ou boutons radio
	 */
	
	/**
	 * Construction d'une liste de cases à cocher, boutons radio
	 * @param name Le nom a associer 
	 * @param str[] liste d'étiquettes
	 * @param Objet o ( sous classe de jComponent : jCheckBox, JRadioBox). Cet objet doit être instancié avant l'appel de la méthode. 
	 * @return box 
	 */	
	public Box buildVerticalBox(String name,String[]str, JComponent o, String color)
	{
		Box box=Box.createVerticalBox();
		this.bg=new ButtonGroup();
		JLabel lname=new JLabel(name,JLabel.LEFT);
		lname.setFont(subtitle);
		
//		box.add(lname);
//		box.add(Box.createVerticalStrut(10));
		assertTrue(o instanceof JRadioButton||o instanceof JCheckBox);
		if(o instanceof JRadioButton)
		{
			Box one=Box.createVerticalBox();
			Box two=Box.createVerticalBox();
			
			for(int i=0;i<=str.length-1;i++)
			{
				o=new JRadioButton(str[i]);
				o.setSize(25, 5);
				((JRadioButton) o).addItemListener(new RadioItemEvent((JRadioButton) o));
				o.setBackground(Color.decode(color));
				bg.add((AbstractButton) o);
				
				int check=str.length%2;
				int borne=0;
				if(check==1)
				{
					borne=str.length+1;
				}
				else
				{
					borne=str.length;
				}
				
				if(i<borne/2)
				{
					one.add(o);
				}
				else
				{
					two.add(o);
				}
				Box utils=Box.createHorizontalBox();
				
				one.setAlignmentX(LEFT_ALIGNMENT);
				one.setBackground(Color.decode(color));
				utils.add(one);

				two.setAlignmentX(RIGHT_ALIGNMENT);
				two.setBackground(Color.decode(color));
				utils.add(two);
				box.add(utils);

			}
		}
		if(o instanceof JCheckBox)
		{
			for(int i=0;i<=str.length-1;i++)
			{
				o=new JCheckBox(str[i]);
				o.setSize(25, 5);
				o.setBackground(Color.decode(color));
				box.add(o);
//				int a=str.length/2;
				if(i==str.length/2)
				{
					box.add(Box.createVerticalStrut(10));
				}
				if(i==str.length+1/2)
				{
					box.add(Box.createHorizontalStrut(10));
				}
			}
		}
		box.setBackground(Color.decode(color));
		box.setOpaque(true);
		box.setVisible(true);
		return box;
	}
	
//	public ButtonGroup buildButtonGroup(String name,String[]str, JRadioButton o, String color)
//	{
//		ButtonGroup bg=new ButtonGroup();
//		JLabel lname=new JLabel(name,JLabel.LEFT);
//		lname.setFont(subtitle);
//
//		assertTrue(o instanceof JRadioButton);
//		if(o instanceof JRadioButton)
//		{
//			
//			for(int i=0;i<=str.length-1;i++)
//			{
//				o=new JRadioButton(str[i]);
//				o.setSize(25, 5);
//				o.setBackground(Color.decode(color));
//				bg.add(o);
////				
//			}
//		}
//		
//		bg.
//		bg.setBackground(Color.decode(color));
//		bg.setOpaque(true);
//		bg.setVisible(true);
//		return box;
//	}
//	
	/**
	 * BOX 1 NOMINATION (identification de la nomination, son type)
	 */
 
	public Box nomination=Box.createVerticalBox();
	public String nom;
	
	public String[]t_etiquettes= {"entité humaine","objet physique (naturel ou artefact)","entité abstraite","processus","événement","lieu"};
	public JRadioButton rbt=new JRadioButton();
	
	/**
	 * élements qui vont apparaître dans le TABLEAU D'ANNOTATION
	 */
	public JTextField nomin=new JTextField(15);// champs associé à la nomination traitée au niveau du tableau (texte annoté)
	
	public JTextField referent=new JTextField("pas de référent",20);
	public JTextField type=new JTextField(15);
	
	public void setUpNomination()
	{
		/**
		 * éléments
		 */
		String color="#e4e581";
//		nomination.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel label_nomination = new JLabel("Nomination");
		label_nomination.setFont(this.subtitle);
		label_nomination.setLayout(new BoxLayout(label_nomination,BoxLayout.Y_AXIS));
		label_nomination.setAlignmentX(RIGHT_ALIGNMENT);
		label_nomination.setSize(new Dimension(20,5));
		
//		label_nomination.setHorizontalTextPosition(WIDTH);
		JPanel boutons=new JPanel();
		
		JButton bouton_nomination=new JButton("nomination");
		bouton_nomination.addActionListener(new ValiderNominationAction());
		bouton_nomination.setSize(2, 2);
//		bouton_nomination.setPreferredSize(new Dimension(50, 100));
		bouton_nomination.setEnabled(true);
	
		
		JButton bouton_reference=new JButton("référence");
		bouton_reference.addActionListener(new ValiderReferentAction());
		bouton_reference.setSize(2, 2);
//		bouton_nomination.setPreferredSize(new Dimension(50, 100));
		bouton_reference.setEnabled(true);
		
		boutons.add(bouton_nomination);
		boutons.add(nomin);
//		boutons.add(Box.createVerticalStrut(1));
//		boutons.add(bouton_reference);
		boutons.setBackground(Color.decode(color));
		
		boutons.setSize(10,5);
//		boutons.setAlignmentX(RIGHT_ALIGNMENT);
		boutons.setLayout(new BoxLayout(boutons,BoxLayout.X_AXIS));
	

		/** ajout */
//		nomination.add(label_nomination);
		
//		nomination.add(Box.createVerticalGlue());
		nomination.add(Box.createVerticalStrut(10));
		nomination.add(boutons);
		nomination.add(Box.createVerticalStrut(10));
		nomination.add(this.buildVerticalBox("type entité",t_etiquettes,rbt,color));
		nomination.setOpaque(true);
		nomination.setBackground(Color.decode(color));
//		nomination.add(Box.createRigidArea(new Dimension(5, 10)));
//		nomination.setSize(new Dimension(50,50));
		nomination.setForeground(Color.decode("#5C5E56"));
		nomination.setBorder(new EmptyBorder(10, 10, 10, 10));
		nomination.setEnabled(true);

	}
	
	/**
	 * BOX 2 CONTEXTE (identification du segment et son annotation)
	 */
	
//	public JPanel contexte=new JPanel();
	public Box contexte=Box.createVerticalBox();
	public Box plan= Box.createVerticalBox();
	public Box procede= Box.createVerticalBox();
	public Box attitude= Box.createVerticalBox();

	public String[]s_plan= {"ontologique","langagier","indéterminé"};
	public String[]s_procede= {"introduction","ajustement","rejet"};
	public String[]s_attitude= {"prise en charge", "interaction", "cadrage"};
	
	public JButton ok=new JButton("OK");
	
	/**
	 * champs associés dans TABLEAU D'ANNOTATION
	 */
	
	public JTextField segment=new JTextField(15);
	public JTextField marqueurs=new JTextField(15);
	public JTextField tags=new JTextField(15);//annotation du segment ( du point de vue de contexte)
	
	public ButtonGroup bgc=new ButtonGroup();
	
	public void setUpContext()
	{
		JPanel boutons=new JPanel();
		/**
		 * éléments
		 */
		
		JLabel label_contexte= new JLabel("Contexte");
		label_contexte.setFont(this.title);
		
		JButton bouton_segment=new JButton("co-texte");
		bouton_segment.addActionListener(new ValiderSegmentAction());
		bouton_segment.setEnabled(true);
		
		JButton bouton_marqueurs=new JButton("marqueurs");
		bouton_marqueurs.addActionListener(new ValiderMarqueursAction());
		bouton_marqueurs.setEnabled(true);
		
		boutons.add(bouton_segment);
		boutons.add(bouton_marqueurs);
		boutons.setLayout(new BoxLayout(boutons,BoxLayout.X_AXIS));;
		
		/** PLAN */
		plan.setName("plan");
		plan.add(new JLabel("Plan"));
		plan.setOpaque(true);
		plan.setBackground(Color.decode("#c7b098"));
//		plan.setSize(new Dimension(50,50));
		
		for(int i=0;i<=s_plan.length-1;i++)
		{
			cb=new JCheckBox(s_plan[i]);
			cb.setOpaque(false);
			cb.setForeground(Color.decode("#5C5E56"));
//			cb.setSize(25, 5);
			cb.addItemListener(new ItemEvents(cb));
			plan.add(cb);
//			bgc.add(cb);
		}
//		plan.add(Box.createVerticalStrut(20));

		/** PROCEDE */
		ButtonGroup pg=new ButtonGroup();
		procede.setName("procédé");
		procede.add(new JLabel("Procédé"));
		procede.add(Box.createRigidArea(new Dimension(5, 10)));
		procede.setOpaque(true);
		procede.setBackground(Color.decode("#9ca772"));
		procede.setAlignmentY(RIGHT_ALIGNMENT);
		

		for(int i=0;i<=s_procede.length-1;i++)
		{
			cb=new JCheckBox(s_procede[i]);
			cb.setSize(25, 5);
			cb.setOpaque(false);
			cb.setForeground(Color.decode("#5C5E56"));
			cb.addItemListener(new ItemEvents(cb));
			procede.add(cb);
			pg.add(cb);
		}
//		procede.add(Box.createVerticalStrut(20));
		
		/** ATTITUDE */
		attitude.setName("attitude");
		attitude.add(new JLabel("Attitude"));
		attitude.add(Box.createRigidArea(new Dimension(5, 10)));
		attitude.setOpaque(true);
		attitude.setBackground(Color.decode("#d2b4c5"));
		attitude.setSize(50,50);
		for(int i=0;i<=s_attitude.length-1;i++)
		{
			cb=new JCheckBox(s_attitude[i]);
//			cb.setSize(25, 5);
			cb.setOpaque(false);
			cb.setForeground(Color.decode("#5C5E56"));
			cb.addItemListener(new ItemEvents(cb));
			attitude.add(cb);
//			bgc.add(cb);
		}
		
		/** BOUTON OK */
		ok.addActionListener(new OkAction());
		ok.setEnabled(true);
		
		/**
		 * ajout
		 */
//		contexte.add(label_contexte);
//		contexte.add(bouton_segment);
//		contexte.add(Box.createVerticalStrut(5));
//		contexte.add(bouton_marqueurs);
		contexte.add(boutons);
		
		
		contexte.add(procede);
		contexte.add(Box.createVerticalStrut(10));
		contexte.add(plan);
		contexte.add(Box.createVerticalStrut(10));
		contexte.add(Box.createVerticalStrut(10));
		contexte.add(attitude);
		contexte.add(Box.createVerticalStrut(10));
		contexte.add(ok);
		contexte.setAlignmentX(RIGHT_ALIGNMENT);
		
		/**
		 * mise en forme
		 */
		
//		contexte.setLayout(new BoxLayout(contexte, BoxLayout.X_AXIS));
		contexte.setBorder(new EmptyBorder(10, 10, 10, 10));
		contexte.setVisible(true);
		setResizable(true);
	}
	
	/**
	 * BOX 3 RELATIONS (identification des relations entre la nomination et le référent)
	 */
	
	public Box relations= Box.createVerticalBox();
	public JRadioButton rbr=new JRadioButton();
	public String[]r_etiquettes= {"r_association","r_temporel","r_spatial","r_causal","rs_identite", "rs_appartenance", "rs_inclusion", "rs_partie_tout", "rs_localisation", "rs_ruption"};
	public JTextField repere=new JTextField("non spécifiée",15);
	
	public void setUpRelations()
	{
		/**
		 * éléments
		 */
		
//		JLabel label_relations = new JLabel("Relations");
//		label_relations.setFont(this.subtitle);
//		label_relations.setAlignmentX(RIGHT_ALIGNMENT);

		JButton bouton_relations=new JButton("relation");
		bouton_relations.addActionListener(new ValiderNominationAction());
		bouton_relations.setEnabled(true);
		
		/**
		 * ajout
		 */
		
//		relations.add(label_relations);
		relations.add(Box.createVerticalStrut(10));
		relations.add(this.buildVerticalBox("type de relation",r_etiquettes,rbr,"#eef2e6"));
		relations.add(Box.createVerticalStrut(10));
		relations.setEnabled(true);
//		relations.add(bouton_relations);
		
	}

	/**
	 * construction du TABLEAU D'ANNOTATION
	 */
	public JPanel tab=new JPanel();
	
	/*TODO traitement des identifiants d'une annotation*/
	
	public JTextField nid=new JTextField(5);
	public JLabel l;
	
	public JTable tableau;
	public JScrollPane scrollPane;
	public DefaultTableModel dm = new DefaultTableModel(0, 0);
	public void setUpTableau()
	{
		/** entête  */
		
//		DefaultTableModel dm = new DefaultTableModel(0, 0);
		String [] labels= {"nid","nomination","type nomination","segment", "marqueurs", "annotation","référent","relation"};
		dm.setColumnIdentifiers(labels);

	
//		Object[][] data = {{0,"choisissez une nomination","non défini", "non défini", "non identifiés", "pas d'annotation","non défini", "non spécifiée"}};
//		tableau=new JTable(data,labels);
		tableau=new JTable(dm);
		tableau.setShowVerticalLines(true);
		tableau.setCellSelectionEnabled(true);
		tableau.setColumnSelectionAllowed(true);
		tableau.setBorder(new LineBorder(Color.decode("#45493b")));
		tableau.setPreferredScrollableViewportSize(new Dimension(1710, 100));
		tableau.setFont(this.subtitle);
		tableau.setVisible(true);
		
		TableColumn column = null;
		for (int i = 0; i < 7; i++) {
		    column = tableau.getColumnModel().getColumn(i);
		    if (i == 0) {
		        column.setMinWidth(20);
		    } else {
		        column.setMinWidth(100);
		    }
		}
		/*modification du modèle à mettre dans action performed*/
		
//		Vector<Object> modif = new Vector<Object>();
//		modif.add(nid);
//		modif.add(nomin.getText());
//		modif.add(type.getText());
//		modif.add(segment.getText());
//		modif.add(marqueurs.getText());
//		modif.add(tags.getText());
//		modif.add(referent.getText());
//		modif.add(relation.getName());
//		
//	    dm.addRow(modif);
 
		tableau.setFillsViewportHeight(true);
		tableau.getTableHeader().setBackground(Color.decode("#67665a"));
		tableau.getTableHeader().setForeground(Color.WHITE);
		tableau.getTableHeader().setFont(subtitle);
	    scrollPane = new JScrollPane(tableau);
	    scrollPane.setLayout(new ScrollPaneLayout());
//	    Dimension d=this.contexte.getSize();
	    scrollPane.setVisible(true);

	}
	
	/**validation, invalidation*/
	
	public JButton valider = new JButton("Valider");
	public JButton reinitialiser = new JButton("Réinitialiser");
	
	public void setUpComponents()
	{

		this.setUpNomination();
		this.setUpContext();
		this.setUpRelations();
		this.setUpTableau();
	}
	
	/*boutons à ajouter*/ 
//	public JButton invalider = new JButton("Invalider");
//	public JButton undo = new JButton("Effacer la sélection");	
//	public JButton suivant = new JButton("Suivant >>");
//	public JButton precedent = new JButton("<< Précédent");
	
	public File file=new File("./output.txt");	

	/**TODO à améliorer le rendu, stocker le texte de départ dans une variable*/
	
	public void appendAnnotatedSegment(String segment) throws IOException 
	{
		OutputStream os = null;
		
		OutputStreamWriter osw = null;
		try 
		{
			/**information de l'utilisateur */
			
			this.logger.info("Saving annotated segment : {}",segment);
			this.logger.info("Segment length : {} ",segment.length());
			this.logger.info("Destination file : {}",this.file.getPath());
			
			os = new FileOutputStream(this.file,true);
			osw=new OutputStreamWriter(os);
			osw.write(segment, 0, segment.length());
			osw.flush();	

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				osw.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	/**éléments de mise en forme
	 * TODO ajouter une ou des fonctions de mise en forme par type de composant : ensembe de cases à cocher, étiquette, bouton*/
	

	
	/**gestion de la nomination en cours de traitement*/
	
	class ImagePanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private Image img;
		  public ImagePanel(String img) {
		    this(new ImageIcon(img).getImage());
		  }
		  public ImagePanel(Image img) {
		    this.img = img;
		    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		    setPreferredSize(size);
		    setMinimumSize(size);
		    setMaximumSize(size);
		    setSize(size);
		    setLayout(null);
		  }
		  @Override
		  public void paintComponent(Graphics g) {
		    g.drawImage(img, 0, 0, null);
		  }
		}
	/**création de la fenêtre */

	
	public JPanel createForm(String s) throws BadLocationException
	{
		/**mise en page du formulaire*/
		JPanel form=new JPanel();
		setLocationRelativeTo(null);
		form.setBackground(Color.decode("#EAF4D3"));
		form.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//		form.setPreferredSize(new Dimension(900, 600));

		/**étiquettes
		 * TODO fonction de création des jeux d'étiquettes à partir d'un fichier de paramètres (annotation sur mesure)
		 * !! pour l'instant annotation "ensembliste", structure en graphe avec possibilité d'annoter les arcs est à réfléchir*/
		
		JPanel etiquettes=new JPanel();
		
		this.setUpComponents();
		
		JPanel boutons=new JPanel();
		valider.addActionListener(new ValiderAction());
		valider.setEnabled(true);
		
		/*TODO réinitialiser, check listeners*/
		
//		valider.addActionListener(new ValiderAction());
//		valider.setEnabled(true);
		
		boutons.add(valider);
		boutons.add(Box.createHorizontalStrut(20));
		boutons.add(reinitialiser);
		boutons.add(Box.createHorizontalStrut(20));
		
//		suivant.addActionListener(new SuivantAction());
//		suivant.setEnabled(true);
//		boutons.add(suivant);
		
		boutons.setLayout(new BoxLayout(boutons, BoxLayout.X_AXIS));
		boutons.setBackground(Color.decode("#EAF4D3"));
//		setResizable(true);
		
		
//		JLabel label = new JLabel("Jeux d'étiquettes");
//		label.setFont(this.title);
//		etiquettes.add(label);
		etiquettes.add(new JLabel("NOMINATION"));
		etiquettes.add(Box.createVerticalStrut(5));
		etiquettes.add(this.nomination);
		etiquettes.add(Box.createVerticalStrut(5));
		etiquettes.add(new JLabel("CONTEXTE"));
		etiquettes.add(Box.createVerticalStrut(5));
		etiquettes.add(this.contexte);
		etiquettes.add(Box.createVerticalStrut(5));
		etiquettes.add(new JLabel("RELATIONS"));
		etiquettes.add(Box.createVerticalStrut(5));
		etiquettes.add(this.relations);
		
		etiquettes.setLayout(new BoxLayout(etiquettes, BoxLayout.Y_AXIS));
		
		etiquettes.add(Box.createVerticalStrut(5));
		etiquettes.add(boutons);
		etiquettes.setSize(200,50);
		
		/**TEXTE*/
		
		/**
		 * TODO transformer texte/txt en JScrollPane, JTexteEditorPane ou similaire
		 */
		JPanel txt=new JPanel();
		this.texte=new JTextArea(40,90);
		
		this.texte.setEditable(true);
		this.texte.setText(s);
		this.texte.setLineWrap(true);
		this.texte.setToolTipText("sélectionnez un ou plusieurs segments et choisissez une étiquette pour ceux-ci");
		this.texte.setFont(paragraph);
		
		/**TEST */
		
		texte.getDocument().addDocumentListener(new InsertionAction());
		
		/**listeners*/

//		this.texte.add(new Scrollbar(Scrollbar.VERTICAL));
		this.texte.addCaretListener(new CaretEvents());
		this.texte.addCaretListener(new NominationAction());
		this.texte.addCaretListener(new SegmentAction());
		this.texte.addCaretListener(new RefentAction());
//		this.texte.addCaretListener(new MarquersAction());
		this.texte.setLayout(new BorderLayout(0,2));
		setResizable(true);
		txt.add(texte);
		
		/**Construction d'un champs de suivi des annotations*/
		
		this.commentaire=new JTextArea(8,150);
		this.commentaire.setEditable(true);
		this.commentaire.setLineWrap(true);
		this.commentaire.setText(
				"Commentaire :"
						+"\n");
		this.commentaire.setToolTipText("Champ de suivi des annotations en cours, vous pouvez ajouter un commentaire dans ce champ");
		this.commentaire.setLayout(new BorderLayout(0,5));
		this.commentaire.setBackground(Color.decode("#67665a"));
		this.commentaire.setForeground(Color.WHITE);
		
//		txt.add(Box.createVerticalStrut(10));
//		txt.add(commentaire);
		

		JPanel suivi=new JPanel();
//		Image img = Toolkit.getDefaultToolkit().createImage("./resources/papier.jpg");
//		ImageIcon ic=new ImageIcon(img);
//		JLabel li=new JLabel(ic);

//		ImagePanel panel = new ImagePanel(new ImageIcon(getClass()
//                .getResource("resources/papier.jpg"))
//                .getImage());
//		suivi.add(panel);
//		this.commentaire.add(li);
//		suivi.add(li);
		suivi.add(this.commentaire);
		setResizable(true);
		
		
//		txt.setLayout(new BoxLayout(txt,BoxLayout.Y_AXIS));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                txt, etiquettes);
//		splitPane.setResizeWeight(0.5);
		splitPane.setResizeWeight(WIDTH);
		form.add(splitPane);
		form.add(this.scrollPane);
		form.add(suivi);
//		form.add(boutons);
		setResizable(true);
		
		return form;
	}
	
	public Set<Integer[]> coord=new HashSet<Integer[]>();
	public Map<String,Set<String>> map=new HashMap<String,Set<String>>();
	
	public Set<JCheckBox> cb_array=new HashSet<JCheckBox>();
	
	/**suite au click coordonnées converties en chaines de caractère, puis stockage clé-valeur*/
	
	/** EVENEMENTS */
	
	/**
	 * éléments nécessaires pour récupérer les modifications
	 */
	public Integer s_nid=0;
	public String s_nomination;
	public String s_type="";
	public String s_segment="";
	public String s_marqueurs="";
	public String s_annotation="";
	public String s_referent="";
	public String s_relation="";
	
	
	/** Evénements de la souris */
	
	protected class MouseEvents implements MouseListener 
	{
		final Logger logger = LoggerFactory.getLogger(MouseEvents.class); 
		
		public void mousePressed(MouseEvent e) 
		{
			this.logger.info("Mouse presssed {} of clicks: ", e.getClickCount(), e);
		}

	    public void mouseReleased(MouseEvent e) 
	    {
	    	this.logger.info("Mouse released {} of clicks: ", e.getClickCount(), e);
	    }
		@Override
		public void mouseClicked(MouseEvent arg0) 
		{
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	protected class MouseInput extends MouseInputAdapter implements MouseInputListener
	{
		public MouseInput()
		{
			super();
		}
		final Logger logger = LoggerFactory.getLogger(MouseEvents.class); 
		
		public void mousePressed(MouseEvent e) 
		{
			this.logger.info("Mouse presssed {} of clicks: ", e.getClickCount(), e);
		}

	    public void mouseReleased(MouseEvent e) 
	    {
	    	this.logger.info("Mouse released {} of clicks: ", e.getClickCount(), e);
	    }
	    
//	    public void mouseMoved(MouseEvent e) 
//	    {
//	    	this.logger.info("Mouse moved: x={} y={}",e.getX(), e.getY(),e);
//	     }

	     public void mouseDragged(MouseEvent e) 
	     {
	    	 this.logger.info("Mouse dragged: {}",e);
	     }
	     
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	/** Evénements du curseur */
	
	protected class CaretEvents  implements CaretListener
	{
		final Logger logger = LoggerFactory.getLogger(CaretEvents.class); 
		
		@Override
		public void caretUpdate(CaretEvent e) 
		{
			 this.logger.info("Caret update: {} {}",e.getMark(),e.getDot());
			 Integer[] select=new Integer[2];
			 if(e.getMark()<=e.getDot())
			 {
				 select[0]=e.getMark();
				 select[1]=e.getDot();
			 }
			 else
			 {
				 select[0]=e.getDot();
				 select[1]=e.getMark();
			 }
			 try
			 { 
				 coord.add(select);
				 highlight(texte, select[0], select[1], "#bebfbd");
			 } 
			 catch (BadLocationException e1) 
			 {
				e1.printStackTrace();
			 }
		}
	}
	public String liste;
	/** Evénements cases à cocher */
	public boolean item_checked;
	
	
	/**
	 * 
	 * @author bebeshina
	 * suite a une selection multiple, récupérer la liste des éléments selectionnés et vider lec coordonnées
	 */
	
	public void getMarks()
	{
		Set<String> set=new HashSet<String>();
	      for(Integer[] in:coord)
	      {
	    	  int len=in[1]-in[0];
	    	  this.logger.info("len {}",len);
	    	  char[] dest=new char[len];
	    	  
	    	  if(len>0)
	    	  {
	    		  texte.getText().getChars(in[0],in[1],dest,0);
	    		  String s=new String(dest);
	    		  set.add(s);
	    		 
	    	  } 
	      }
      	try 
      	{
      		unhighlight(texte);
      	} 
      	catch (BadLocationException e2) 
      	{
			// TODO Auto-generated catch block
			e2.printStackTrace();
      	}
	      
	  StringBuilder sb=new StringBuilder();
      for(String s:set)
      {
    	  sb.append(s+", ");
      }
	      liste=sb.toString().replaceAll(", (?=$)","");
	      coord.clear();
	}
	
	/**
	 * 
	 * @author bebeshina
	 *
	 */
	protected class RadioItemEvent implements ItemListener
	{
		private JRadioButton rb;
		
		protected RadioItemEvent(JRadioButton rb)
		{
			this.rb=rb;
		}
		final Logger logger = LoggerFactory.getLogger(RadioItemEvent.class);
		public void itemStateChanged(ItemEvent e) 
		{
			if (e.getStateChange() == ItemEvent.SELECTED) 
			{
				this.logger.info("Item name {}",rb.getText());
				System.out.println(rb.getParent());
				if(rb.getText().startsWith("r"))
				{
					s_relation=rb.getText();
				}
				else
				{
					s_type=rb.getText();
				}
//				
			}
			if (e.getStateChange() == ItemEvent.DESELECTED) 
			{ 
				this.logger.info("item deselected");
				try 
				{
					unhighlight(texte);
//					item_checked=false;
					
				} 
				catch (BadLocationException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
	}
	protected class ItemEvents  implements ItemListener
	{
		private JCheckBox cbox;
		
		protected ItemEvents(JCheckBox cb)
		{
			this.cbox=cb;
		}
		
		final Logger logger = LoggerFactory.getLogger(ItemEvents.class);
		
		@Override
		public void itemStateChanged(ItemEvent e) 
		{
			if (e.getStateChange() == ItemEvent.SELECTED) 
			{
				/**test */
				
				cb_array.add(cbox);
				
				/*********/
				
				item_checked=true;	
				this.logger.info("item selected {} {}",cbox.getText(),cbox.getParent().getName());
				Set<String> set=new HashSet<String>();
		      for(Integer[] in:coord)
		      {
		    	  int len=in[1]-in[0];
		    	  this.logger.info("len {}",len);
		    	  char[] dest=new char[len];
		    	  
		    	  if(len>0)
		    	  {
		    		  texte.getText().getChars(in[0],in[1],dest,0);
		    		  String s=new String(dest);
		    		  set.add(s);
		    		  this.logger.info("segment associé {} ",s);
		    	  } 
		      }
		      	try 
		      	{
		      		unhighlight(texte);
		      	} catch (BadLocationException e2) 
		      	{
					// TODO Auto-generated catch block
					e2.printStackTrace();
		      	}
			      map.put(cbox.getParent().getName()+" "+cbox.getText(),set);
//			      commentaire.append("\n"+cbox.getParent().getName()+" > "+cbox.getText()+" : " +Arrays.asList(set));
			      StringBuilder sb=new StringBuilder();
			      for(String s:set)
			      {
			    	  sb.append(s+", ");
			      }
//			      String liste=sb.toString().replaceAll(", (?=$)","");
			      liste=sb.toString().replaceAll(", (?=$)","");
			      
			      commentaire.append("\n"+Etiquettes.valeur(cbox.getParent().getName()+" "+cbox.getText())+" : " +liste);
//			      coord.clear();
				
				if (e.getStateChange() == ItemEvent.DESELECTED) 
				{ 
					this.logger.info("item deselected");
					try 
					{
						unhighlight(texte);
//						coord.clear();
						item_checked=false;
						
					} 
					catch (BadLocationException e1) 
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}	
		}
	}
	
	/**prévoir une fonction getNext(pour récup segment suivant)*/
	
	class Insertion //extends AnnotatePhrase
	{

		public Insertion() throws Exception 
		{
			
		}
	}

	 
	
	protected class InsertionAction implements DocumentListener
	{
		final Logger logger = LoggerFactory.getLogger(InsertionAction.class);
		
		@Override
        public void insertUpdate(DocumentEvent e) 
        {
			try 
			{
				this.logger.info("{}",e.getDocument().toString());
//				String newText=texte.getText();
				this.logger.info("nouveau texte : {}",texte.getText());
//				new Insertion(newText);
				
			} 
			catch (Exception e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
		public void insertUpdate(Event e) 
        {
			try 
			{
				
//				String newText=texte.getText();
				this.logger.info("nouveau texte : {}",texte.getText());
//				new Insertion(newText);
				
			} 
			catch (Exception e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
		@Override
        public void removeUpdate(DocumentEvent e) {}

        @Override
        public void changedUpdate(DocumentEvent arg0) {}
		
	}
	/*********************gestion du choix de la nomination en cours de traitement ***********************///////
	
	protected class Nomination 
	{
		public Nomination(String s) 
		{
			
		}
	}
	class NominationAction implements CaretListener
	{
		final Logger logger = LoggerFactory.getLogger(NominationAction.class); 
		@Override
		public void caretUpdate(CaretEvent e) 
		{
			 this.logger.info("Caret update: {} {}",e.getMark(),e.getDot());
			 Integer[] select=new Integer[2];
			 if(e.getMark()<=e.getDot())
			 {
				 select[0]=e.getMark();
				 select[1]=e.getDot();
			 }
			 else
			 {
				 select[0]=e.getDot();
				 select[1]=e.getMark();
			 }
			 try
			 { 
				 s_nomination=texte.getText().substring(select[0], select[1]);
				 nom=texte.getText().substring(select[0], select[1]);
				 
				 this.logger.info("nomination {}",s_nomination);
				 highlight(texte, select[0], select[1], "#bebfbd");
				 /******????????******/
//				 coord.clear();
			 } 
			 catch (BadLocationException e1) 
			 {
				e1.printStackTrace();
			 }
			
		}
	}

	/**
	 * 
	 * @author bebeshina
	 * récupération du segment sélectionné
	 */
	
	public String seg;
	class SegmentAction implements CaretListener
	{
		final Logger logger = LoggerFactory.getLogger(SegmentAction.class); 
		@Override
		public void caretUpdate(CaretEvent e) 
		{
			 this.logger.info("Caret update: {} {}",e.getMark(),e.getDot());
			 Integer[] select=new Integer[2];
			 if(e.getMark()<=e.getDot())
			 {
				 select[0]=e.getMark();
				 select[1]=e.getDot();
			 }
			 else
			 {
				 select[0]=e.getDot();
				 select[1]=e.getMark();
			 }
			 try
			 { 
				 seg=texte.getText().substring(select[0], select[1]);
				 this.logger.info("segment {}",seg);
				 highlight(texte, select[0], select[1], "#bebfbd");
			 } 
			 catch (BadLocationException e1) 
			 {
				e1.printStackTrace();
			 }
		}
	}
	class RefentAction implements CaretListener
	{
		final Logger logger = LoggerFactory.getLogger(RefentAction.class); 
		@Override
		public void caretUpdate(CaretEvent e) 
		{
			 this.logger.info("Caret update: {} {}",e.getMark(),e.getDot());
			 Integer[] select=new Integer[2];
			 if(e.getMark()<=e.getDot())
			 {
				 select[0]=e.getMark();
				 select[1]=e.getDot();
			 }
			 else
			 {
				 select[0]=e.getDot();
				 select[1]=e.getMark();
			 }
			 try
			 { 
				 s_referent=texte.getText().substring(select[0], select[1]);
				 this.logger.info("référent {}",seg);
				 highlight(texte, select[0], select[1], "#bebfbd");
			 } 
			 catch (BadLocationException e1) 
			 {
				e1.printStackTrace();
			 }
		}
	}
	class MarquersAction implements CaretListener
	{
		final Logger logger = LoggerFactory.getLogger(SegmentAction.class); 
		@Override
		public void caretUpdate(CaretEvent e) 
		{
			 this.logger.info("Caret update: {} {}",e.getMark(),e.getDot());
			 Integer[] select=new Integer[2];
			 if(e.getMark()<=e.getDot())
			 {
				 select[0]=e.getMark();
				 select[1]=e.getDot();
			 }
			 else
			 {
				 select[0]=e.getDot();
				 select[1]=e.getMark();
			 }
			 try
			 { 
				 s_referent=texte.getText().substring(select[0], select[1]);
				 this.logger.info("référent {}",seg);
				 highlight(texte, select[0], select[1], "#bebfbd");
			 } 
			 catch (BadLocationException e1) 
			 {
				e1.printStackTrace();
			 }
		}
	}
	class ValiderNomination 
	{
		public ValiderNomination()
		{
			
		}
	}
	class ValiderNominationAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
//			 nomin.setText(nom);
//			 setVisible(true);
			 commentaire.append("Nomination en cours de traitement :"+s_nomination);
			 nomin.setText(nom);
			 modif=new Vector<Object>();
			 modif.add(s_nid);
			 modif.add(s_nomination);
			 try 
			 {
				unhighlight(texte);
				
			} 
			 catch (BadLocationException e) 
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 coord.clear();
			 
		}
		
	}
	
	class ValiderSegmentAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
//			 segment.setText(seg);
			 s_segment=seg;
			 setVisible(true);
			 coord.clear();
			 
			 try 
			 {
				unhighlight(texte);
			} 
			 catch (BadLocationException e) 
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 commentaire.append("\n"+"Segment en cours de traitement :"+"\n"+s_segment+"\n");
		}
		
	}
	
	
	class ValiderMarqueursAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			getMarks();
			marqueurs.setText(liste);
			s_marqueurs=liste;
			setVisible(true);
			coord.clear();
			commentaire.append("\n"+"Marqueurs :"+"\n"+liste);
			try 
			 {
				unhighlight(texte);
			} 
			 catch (BadLocationException e) 
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	class ValiderReferentAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			getMarks();
			referent.setText(liste);
			modif.add(6, liste);
			s_referent=liste;
//			setVisible(true);
			coord.clear();
			commentaire.append("\n"+"Référent :"+"\n"+liste);
			try 
			 {
				unhighlight(texte);
			} 
			 catch (BadLocationException e) 
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	class Ok 
	{
		public Ok(){}
	}
	class OkAction implements ActionListener
	{
		private JCheckBox cbox;
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			/**
			 *  récolter les changements d'état, construire une annotation mixte
			 */
			Map<Integer,String> lmap=new HashMap<Integer,String>();
			
			for(JCheckBox c:cb_array)
			{
				cbox=c;
			
				String parent=cbox.getParent().getName();

				switch(parent)
				{
					case "plan": 
						
						lmap.put(1,Etiquettes.valeur(parent+" "+cbox.getText()));
						break;
				
					case "procédé":
						
						lmap.put(2,Etiquettes.valeur(parent+" "+cbox.getText()));
						break;
				
					case "attitude":
			
						lmap.put(3,Etiquettes.valeur(parent+" "+cbox.getText()));
						break;
				}
				if(!(lmap.containsKey(1)))
				{
					lmap.put(1, " ");
				}
				if(!(lmap.containsKey(2)))
				{
					lmap.put(2, " ");
				}
				if(!(lmap.containsKey(3)))
				{
					lmap.put(3, " ");
				}
			}
			System.out.println("["+lmap.get(3)+"["+lmap.get(2)+"["+lmap.get(1)+"]]]");
			s_annotation="["+lmap.get(3)+"["+lmap.get(2)+"["+lmap.get(1)+"]]]";
			
			commentaire.append("\n"+"["+lmap.get(3)+"["+lmap.get(2)+"["+lmap.get(1)+"]]]"+"\t"+liste);
			try 
			{
				unhighlight(texte);
			} 
			catch (BadLocationException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			coord.clear();
		}
		
	}
	class Valider extends AnnotatePhrase
	{
		private static final long serialVersionUID = 1L;

		public Valider(String s) throws Exception 
		{
			super(s);
		}
		
	}
	public Vector<Object> modif;
	class ValiderAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{			

				StringBuilder sb=new StringBuilder();
				sb.append("[");
				for(String k:map.keySet())
				{
					if(sb.length()==0)
					{
						sb.insert(0,k);
					}
					else
					{
						sb.append(Etiquettes.valeur(k)+" ");
					}
				}
				
				sb.append("]");
				try
				{
					sb.append("\t");
					sb.append(texte.getText());
					sb.append("\r\n");
					sb.append(commentaire.getText().replaceAll("Commentaire\\s\\:\n", "").replaceAll("\r\n", "\n"));
					sb.append("\n#\n");
					String segment = sb.toString().replaceAll(" \\]", "]");
					appendAnnotatedSegment(segment);
		
				}
				catch(IOException io)
				{
					
				}
				try 
				{
					/**test tableau*/
//					modif = new Vector<Object>();
					
//					modif.add(s_nomination);
					modif.add(s_type);
					modif.add(s_segment);
					modif.add(s_marqueurs);
					modif.add(s_annotation);
//					modif.add(s_referent);
					modif.add(s_relation);
					
				    dm.addRow(modif);
				    s_nid++;
//				    s_type="";
//				    s_segment="";
//				    s_marqueurs="";
//				    s_annotation="";
//				    s_referent="";
//				    s_relation="";
				    
				} catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try 
				 {
					unhighlight(texte);
//					bg.clearSelection();
//					bgc.clearSelection();
				} 
				 catch (BadLocationException e) 
				 {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			
		}
	
	
	class Suivant extends AnnotatePhrase
	{
		private static final long serialVersionUID = 1L;

		public Suivant(String s) throws Exception 
		{
			super("");
		}
	}
	
	class SuivantAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			// TODO Auto-generated method stub
			
			try 
			{
				
				String newText="Voici un nouveau segment (lecture à partir d'un fichier en cours de développement)";
				new Valider(newText);
				
			} 
			catch (Exception e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	protected Highlighter h;
	
	public void highlight(JTextArea text,int start,int end, String couleur) throws BadLocationException
	{
		this.h = new DefaultHighlighter();
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.decode(couleur));
		text.setHighlighter(h);
		h.addHighlight(start, end, painter);
	}
	public void unhighlight(JTextArea text) throws BadLocationException
	{
		this.h = new DefaultHighlighter();
		text.setHighlighter(h);
		h.removeAllHighlights();
	}
	
	
	/**sera remplacé par le nom du corpus utilisé et le numéro de la phrase*/
	
	public static String id="corpus#123";
	
	/**constructeur*/
	
	public AnnotatePhrase(String s) throws BadLocationException
	{
		super("Annotation ("+id+")");
		
		Container container = getContentPane();
		container.add(this.createForm(s));

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		/**centrage */
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//		setSize(new Dimension(500,200));
		setSize(dim);
		setState(JFrame.NORMAL);
		setLocation(dim.width/2 -this.getWidth()/2, dim.height/2 - this.getHeight()/2);
		setResizable(true);
        setVisible(true);

		
	}

	
	/** BROUILLONS ET NOTES */
	
	/**
	 * 
	 * 	--	récupérer les coordonnées (mouse event between click and release) et les communiquer à highlighter
	 * 	--	récupérer le click sur le checkbox
	 * 	-- 	associer les deux et afficher la sélection avec la couleur de l'étiquette choisie
	 * 	-- 	stocker dans une structure de données
	 * 	-- reverser vers le fichier de texte modifié (annoté)
	 
	 */
	/**formattage String */
	
//	Font f = new Font("LucidaSans", Font.BOLD, 14);
//	AttributedString as= new AttributedString(sb.toString().toUpperCase(Locale.FRANCE));
//	as.addAttribute(TextAttribute.FONT,f);
	
	/**setting up a background image for a *component**/
	
//	ImageIcon icon = new ImageIcon(""); 
//	component.setIcon(icon);
	
	/**layout testing */
	
//	LayoutManager overlay = new OverlayLayout(etiquettes);
//	etiquettes.setLayout(overlay);
//	
	
//	Object settings[][] = {
//	        { "Small", new Dimension(25, 25), Color.white },
//	        { "Medium", new Dimension(35, 55), Color.gray },
//	        { "Large", new Dimension(45, 85), Color.black } };
	
	
	
	
//	GridLayout gl = new GridLayout(1,2,10,0);
//	String gapList[] = {"0", "10", "15", "20"};
////	JComboBox horGapComboBox=new JComboBox(gapList);
//	Dimension buttonSize = bouton_nomination.getPreferredSize();
//	boutons.setLayout(gl);
	public static void main(String[]args) throws BadLocationException
	{
		new AnnotatePhrase("");
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}



}
