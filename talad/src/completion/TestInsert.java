package completion;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.text.DateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import java.io.*;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import data.TNRDataHandler;
import data.TNREmbeddedDataHandler;

import data.TNRRelType;
@SuppressWarnings("unused")

public class TestInsert extends JFrame implements ActionListener
{
	
	private static final long serialVersionUID = 1L;
	/**
	 * membres 
	 */
	final Logger logger = LoggerFactory.getLogger(TestInsert.class);
	public Date now = new Date();
	/**
	 * gestion de la base de données TNR
	 */
	public TNREmbeddedDataHandler dh=new TNREmbeddedDataHandler(); 

	/**
	 * élements de la fenêtre
	 */
	public JTextField field;
	public JLabel label;
	public static final String DB_URL = "jdbc:h2:./tnr";  
		
	  /**
	 * credentials
	 */
	public static final String USER="root";
	public static final String PASS="praxi";
	
	/**
	 * éléments entrée
	 */
	
	public Integer tid; //term id
	public String name; //term string
	
	public JPanel form;
	public JTextField nom,domaine,hyperonyme;
	public JTextArea definition, example,definition_courte,definition_wikipedia,variante;
	
	public JLabel id;
	public JLabel def=new JLabel("Définition");
	public JLabel ex=new JLabel("Exemple");
	public JLabel var=new JLabel("Variante ou synonyme");
	public JLabel defc=new JLabel("Définition courte");
	public JLabel defwiki=new JLabel("Définition Wikipedia");
	public JLabel hyper=new JLabel("Générique");
	public JLabel dom=new JLabel("Domaine");
	
	public JButton valider = new JButton("Valider");
	public JButton creer = new JButton("Créer l'entrée");
	public JButton attente = new JButton("En attente");
	public JButton reinitialiser = new JButton("Réinitialiser");
	public JButton retour = new JButton("Retour");
	
//	public FileWriter fw=new FileWriter(f);
//	public PrintWriter pw = new PrintWriter(fw);
	
	/**
	 * Constructor : terme inconnu, création du ....
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws ConfigurationException
	 */
	public TestInsert() throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException, SQLException, ConfigurationException
	{
		super();
	}
	
	public void start(String name)throws Exception 
	{
		Container container = getContentPane();
		this.name=name;
		this.form=this.createForm();
		container.add(this.form);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(600, 600);
        setVisible(true);
	}
	
	/**
	 * Constructor : entry view when an existing term is provided by user
	 * @param name
	 * @throws Exception
	 */
	public TestInsert(String name)throws Exception 
	{
		super(name);
		Container container = getContentPane();
		this.name=name;
		this.tid=0;
		try
		{
//			this.dh=new TNREmbeddedDataHandler();
			this.tid=dh.termeExiste(name);
			this.form=this.createForm();
			container.add(this.form);
//			dh.connection.close();
		}
		catch(Exception e)
		{
			System.err.println("not connected!");
		}
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(600, 900);
        setVisible(true);
	}
	
	
	public JPanel createEmptyForm() throws Exception
	{
		JPanel form=new JPanel();
		this.label=new JLabel();
		label.setText("Créer l'entrée");
		setVisible(true);
		form.setLayout(new FlowLayout(FlowLayout.LEFT));
		form.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		form.add(this.label);
		
		/** nom (chaîne de caractères)*/
		
		this.nom=new JTextField("Créer un nouveau terme",50);
		
		/**champ définition */
		
		String d="Créer une définition pour cette entrée.";
		this.definition=new JTextArea(10,50);
		this.definition.setText(d);
		this.definition.setLineWrap(true);
		
		/** champ définition courte*/
		
		String dc="Créer une définition courte pour cette entrée.";
		this.definition_courte=new JTextArea(5,50);
		definition_courte.setText(dc);
		
		/** variante, synonyme*/
		
		String v="Variante ou synonyme pour cette entrée.";
		this.variante=new JTextArea(5,50);
		this.variante.setText(v);
				
		/** exemple */
		
		String e="Il n'y a pas d'exemple pour cette entrée";
		this.example=new JTextArea(10,50);
		this.example.setText(e);
		
		/** hyperonyme*/
		
		String h="Hyperonyme pour cette entrée";
		hyperonyme=new JTextField(h,50);
		
		/** domaine*/
		
		String dom="Domaine ";
		domaine=new JTextField(dom,50);
		
		/**
		 * boutons
		 */
		
		creer.addActionListener(new ValiderAction());
		creer.setEnabled(true);
				
		reinitialiser.addActionListener(new ReinitialiserAction());
		reinitialiser.setEnabled(true);
		
		/**
		 * retour à l'arborescence
		 */
		retour.addActionListener(new RetourAction());
		retour.setEnabled(true);
	
		/**ajout des champs */
		
		form.add(this.id);
		form.add(this.label);
		form.add(this.nom);
		form.add(this.def);
		form.add(this.definition);
		form.add(this.defc);
		form.add(this.definition_courte);
		form.add(this.ex);
		form.add(this.example);
		form.add(this.hyper);
		form.add(this.hyperonyme);
		form.add(this.dom);
		form.add(this.domaine);
		form.add(this.creer);
		form.add(this.attente);
		form.add(this.reinitialiser);
		form.add(this.retour);
		
		return form;
	}
	protected Map<String,String> map=new HashMap<String,String>();
	//terme à compléter en paramètre et recherche des informations 
	public JPanel createForm() throws Exception
	{
		JPanel form=new JPanel();
		form.setLayout(new FlowLayout(FlowLayout.LEFT));
		form.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		/**entête*/
		
		this.label=new JLabel();
		label.setText("Editer l'entrée");
		setVisible(true);
		
		/**affichage tid*/
		
		this.id=new JLabel("TID : "+String.valueOf(dh.termeExiste(name)));
		
		/** nom (chaîne de caractères qui correspond au terme : mot, définition, patron lexical... */
		
		this.nom=new JTextField(this.name,50);
		
		
		//nom.addActionListener(new NameAction());
		
//		if(this.label!=null)
//		{
//			form.add(this.label);
//		}
//		else 
//		{
//			System.err.println("label==null!");
//			System.exit(-1);
//		}
//		
		
		/**lecture des informations de l'entrée dans un map (ensemble clé-valeur)*/
		
//		Map<String,String> map=new HashMap<String,String>();
		map=dh.EntryData(name, dh.psGetRelations, dh.psShowRelations);
		
		//si définition existe montrer la définition existante 

		/**définition*/
		
		this.definition=new JTextArea(10,50);
		this.definition.setLineWrap(true);
		String d="";
		if(map.containsKey("définition"))
		{
			d=map.get("définition");
		}
		definition.setText(d);
		
//		definition.addComponentListener(new DefinitionAction());	
		
		/**définition courte*/
		
		String dc="";
		this.definition_courte=new JTextArea(5,50);
		definition_courte.setText(dc);
		if(map.containsKey("définition courte"))
		{
			dc=map.get("définition courte");
		}
		definition_courte.setText(dc);
		
		/**définition wikipedia*/

		String wiki="";
		if(map.containsKey("définition Wikipedia"))
		{
			wiki=map.get("définition Wikipedia");
			/**
			 * TODO 
			 * Affichage des caractères unicode dans les définitions récupérées depuis wikipedia
			 */
		}
		this.definition_wikipedia=new JTextArea(10,50);
		this.definition_wikipedia.setText(wiki);
		this.definition_wikipedia.setBackground(Color.lightGray);
		this.definition_wikipedia.setLocale(Locale.FRANCE);
		this.definition_wikipedia.setLineWrap(true);
//		this.definition_wikipedia.addComponentListener(new DefinitionAction());	
		
		/**variante, synonyme*/
		
		String v="";
		this.variante=new JTextArea(5,50);
		if(map.containsKey("variante"))
		{
			v=map.get("variante");
		}
		this.variante.setText(v);
		
		/**exemple*/ 
		
		String e="";
		this.example=new JTextArea(10,50);
		if(map.containsKey("exemple"))
		{
			e=map.get("exemple");
		}
		this.example.setText(e);
//		example.addComponentListener(new ExampleAction());
		
		/**hyperoyme*/ 
		
		String h="";
		if(map.containsKey("hyperonyme"))
		{
			h=map.get("hyperonyme");
		}
		hyperonyme=new JTextField(h,50);
		
		/**domaine*/ 
		
		String dom="";
		if(map.containsKey("domaine"))
		{
			dom=map.get("domaine");
		}
		domaine=new JTextField(dom,50);
	
		/**
		 * boutons
		 */
		
		valider.addActionListener(new ValiderAction());
		valider.setEnabled(true);
		
		attente.addActionListener(new AttenteAction());
		attente.setEnabled(true);
		
		reinitialiser.addActionListener(new ReinitialiserAction());
		reinitialiser.setEnabled(true);
		
		retour.addActionListener(new RetourAction());
		retour.setEnabled(true);
	
		/**TODO
		 * Lien vers les données contenues dans les différents thesaurus
		 */
//		JPanel tpane=new JPanel();
//		JButton theso=new JButton("Voir dans le thesaurus");
		
		
//		 for (String string :this.thes) 
//		 {
//	            this.dlm.add(0, string);
//	     }
		 
//		JList<String> list = new JList<String>(dlm);
//		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//		list.addListSelectionListener(new SharedListSelectionHandler());
//		tpane.add(new JLabel("Voir dans le thesaurus"));
//		tpane.add(new JScrollPane(list)); 
//		tpane.add(theso);
//		tval.addActionListener(new RechercheThesaurus(name));
//		tval.setVisible(true);
		
		/**
		 * ajout des éléments au formulaire
		 */

		form.add(this.id);
		form.add(this.label);
		form.add(this.nom);
		
		form.add(this.def);
		form.add(this.definition);
		form.add(this.defc);
		form.add(this.definition_courte);
		form.add(this.defwiki);
		form.add(this.definition_wikipedia);
		
		form.add(this.var);
		form.add(this.variante);
		
		form.add(this.ex);
		form.add(this.example);

		form.add(this.hyper);
		form.add(this.hyperonyme);
		
		form.add(this.dom);
		form.add(this.domaine);
		
//		form.add(tpane);
		
//		form.add(tval);
		form.add(this.valider);
		form.add(this.attente);
		form.add(this.reinitialiser);
		form.add(this.retour);
		
		
//		getContentPane().add(form, BorderLayout.NORTH);
//		setVisible(true);
        return form;
	}
	
	private  JEditorPane  editor ;  // наш редактор

	
	
	public DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
			DateFormat.SHORT,
			DateFormat.SHORT);
	
	public String d=shortDateFormat.format(now);
	
	/**
	 * actions/action listeners
	 *
	 */
	
	class Valider extends TestInsert
	{
		private static final long serialVersionUID = 1L;

		public Valider(String s) throws Exception 
		{
			super(s);
		}
	}
	/**
	 * action listeners utilisés
	 */
	class ValiderAction  implements ActionListener
	{

		/**
		 * Ecriture en base 
		 * @param s - chaîne de caractères qui représente l'intitulé de l'entrée
		 */
		
		public void contribution2base(String s)
		{
			Map<Integer,String>tmap=new HashMap<Integer,String>();
			String newName = nom.getText().toString();
			String newHyper = hyperonyme.getText();
			String newDomain = domaine.getText();
			String newSDefinition = definition_courte.getText();
			String newDefinition = definition.getText();
			String newExample = example.getText();
			Integer origin=6;
			try
			{
				tmap.put(0,nom.getText().toString());
				if(!(newHyper.equals(map.get("hyperonyme"))))
				{
					tmap.put(1,newHyper);
				}
				if(!(newDefinition.equals(map.get("définition"))))
				{
					tmap.put(7, newDefinition);
				}
				if(!(newSDefinition.equals(map.get("définition courte"))))
				{
					tmap.put(36, newSDefinition);
				}
				if(!(newExample.equals(map.get("exemple"))))
				{
					tmap.put(8, newExample);
				}
				if(!(newDomain.equals(map.get("domaine"))))
				{
					tmap.put(9,newDomain);
				}
		
				for(Entry<Integer,String>e:tmap.entrySet())
				{
					System.out.println(e.getKey()+" "+e.getValue());
//					dh.newRelation(nom.getText(),e.getKey(),e.getValue(),50,10);
//					System.out.println("creating a new relation..."+nom.getText()+"--"+e.getKey()+"-->"+e.getValue()+" "+33+" "+6);
//					Integer check=dh.createRelation(nom.getText(),e.getKey(), 33, 6, e.getValue(), false);
//					System.out.println(check);
				}
			
				/*vérification existence terme crée/relation créée*/	
			
				System.out.println(dh.termeExiste(newDefinition));
			} 
			
			catch (Exception e) 
			{
			  
			}
		}
		
		
		public File f=new File("./contrib/"+d.replaceAll("(\\/|\\s|\\:)", "")+".txt");
		/**
		 * Sauvegarde dans un fichier log. 
		 * @param s
		 */
		public void contribution2log(String s)
		{
			Map<Integer,String>tmap=new HashMap<Integer,String>();
			String newName = nom.getText().toString();
			String newHyper = hyperonyme.getText();
			String newDomain = domaine.getText();
			String newSDefinition = definition_courte.getText();
			String newDefinition = definition.getText();
			String newExample = example.getText();
			Integer origin=6;
			try(FileWriter fw = new FileWriter(this.f, true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(s);
			    out.flush();
			    out.close();
			    
			} catch (IOException e) 
			{
			  e.getLocalizedMessage();
			}
		}
		
		/**
		 * appel des fonctions de sauvegarde suite au clic sur le bouton
		 */
		public void actionPerformed(ActionEvent e)
		{
			logger.info("validating entry...");
			Map<Integer,String>tmap=new HashMap<Integer,String>();
			String newName = nom.getText().toString();
			String newHyper = hyperonyme.getText();
			String newDomain = domaine.getText();
			String newSDefinition = definition_courte.getText();
			String newDefinition = definition.getText();
			String newExample = example.getText();
			String newVariante = variante.getText();
			Integer origin=6;
			
			/*sauvegarde des informations saisies*/
			
			/**préparation du map*/
			
			try
			{
//				Map<Integer,String>tmap=new HashMap<Integer,String>();
				tmap.put(0,nom.getText().toString());
				
				System.out.println("dans le  map "+map.get("hyperonyme"));
				if(!(newHyper.equals(map.get("hyperonyme"))))
				{
					tmap.put(1,newHyper);
				}
				System.out.println("dans le  map "+map.get("définition"));
				if(!(newDefinition.equals(map.get("définition"))))
				{
					tmap.put(7, newDefinition);
				}
				System.out.println("dans le  map "+map.get("définition courte"));
				if(!(newSDefinition.equals(map.get("définition courte"))))
				{
					tmap.put(36, newSDefinition);
				}
				System.out.println("dans le  map "+map.get("exemple"));
				if(!(newExample.equals(map.get("exemple"))))
				{
					tmap.put(8, newExample);
				}
				System.out.println("dans le  map "+map.get("domaine"));
				if(!(newDomain.equals(map.get("domaine"))))
				{
					tmap.put(9,newDomain);
				}
				if(!(newVariante.equals(map.get("variante"))))
				{
					tmap.put(3,newVariante);
				}
//				dh=new TNREmbeddedDataHandler();//**************!!!*****************
				
				FileWriter fw = new FileWriter(this.f, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw);
					 
				for(Entry<Integer,String>en:tmap.entrySet())
				{
					System.out.println(en.getKey()+" "+en.getValue());
					if(en.getKey()!=0)
					{
						if(en.getValue().length()>0)
						{
						out.println(nom.getText()+","+en.getKey()+","+33+","+7+","+en.getValue()+","+false);
						out.flush();
						System.out.println("creating a new relation..."+nom.getText()+"--"+en.getKey()+"-->"+en.getValue()+" "+33+" "+7);
						Integer check=dh.createRelation(nom.getText(),en.getKey(), 33, 7, en.getValue(), false);
						
						
						/*vérification existence terme crée/relation créée*/	
						System.out.println("numéro de relation créée "+check);
						System.out.println("verification existence nouvelle définition "+dh.termeExiste(newDefinition));
						System.out.println("verification existence nouvelle s-définition "+dh.termeExiste(newSDefinition));
						System.out.println("verification existence nouvel hyper "+dh.termeExiste(newHyper));
						dh.connection.commit();
						}
						
					}
				}
				out.close();
				
//				dh.connection.close();
				/*******************************/
			} 
			
			catch (Exception ex) {}
			
//			this.contribution2log(tid+";"+newName+";"+newDefinition+";"+newSDefinition+";"+newHyper+";"+newDomain);
//			System.out.println("SAVING ....terme "+newName+" définition "+newDefinition+" example "+newExample);
////			this.contribution2base(newName);
			
			/**
			 * libération du bouton
			 */

				try 
				{
					
					new Valider(newName);
					
				} 
				catch (Exception e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
//				JOptionPane.showMessageDialog(TestInsert.this, 
//                        "Modifications sauvegardées");
				dispose();
		}
		
	}
	
	class AttenteAction implements ActionListener
	{
		
		public DateFormat shortDateFormat = DateFormat
				.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
		
		public String date=shortDateFormat.format(now);
		
		public File f=new File("./contrib/"+date.replaceAll("(\\/|\\s|\\:)", "")+".txt");
	
//		public File f=new File("./contrib/"+d.replaceAll("(\\/|\\s|\\:)", "")+".txt");
		/**
		 * 
		 * @param s
		 * sauvegarde uniquement dans un fichier log
		 */
		public void saveContribution(String s)
		{
			try(FileWriter fw = new FileWriter(this.f, true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(s);
			    out.flush();
			    out.close();
			    
			} catch (IOException e) {
			  //exception handling left as an exercise for the reader
			}
		}
	
		public void actionPerformed(ActionEvent e)
		{
			logger.info("data awaiting approval...");
			String newName = nom.getText();
			String newHyper = hyperonyme.getText();
			String newDomain = domaine.getText();
			String newSDefinition = definition_courte.getText();
			String newDefinition = definition.getText();
			String newExample = example.getText();
			
			/*sauvegarde des informations saisies*/
			
			this.saveContribution(tid+";"+newName+";"+newDefinition+";"+newSDefinition+";"+newHyper+";"+newDomain);
			System.out.println("SAVING ....terme "+newName+" définition "+newDefinition+" example "+newExample);
			dispose();
		}
	}
	/**réinitialisation sans message*/
	
	class Reinitialiser  extends TestInsert
	{
		private static final long serialVersionUID = 1L;

		public Reinitialiser(String s) throws Exception 
		{
			super(s);
		}
	}
	
	/**réinitialisation avec message*/
	
	class ReinitialiserAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try 
			{
				logger.info("refreshing entry...");
				 JOptionPane.showMessageDialog(TestInsert.this, 
                         "Réinitialisation de l'entrée : " + nom.getText());
				new Reinitialiser(nom.getText());
			} 
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**retour à l'arborescence*/
	class RetourAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			logger.info("returning to the tree...");
			try 
			{
				 JOptionPane.showMessageDialog(TestInsert.this, 
                         "Retour à l'arborescence.... ");
				//retourner à l'état de l'arbre avant l'action
				dispose();
			
			} 
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	/**TODO
	 * 
	 * Classes et fonctions en chantier
	 * 
	 * Listeners sur les évenements de sélection dans une liste
	 * Gestion des thesaurus
	 */
	
	class SharedListSelectionHandler implements ListSelectionListener 
	{
	    public void valueChanged(ListSelectionEvent e) 
	    {
	        
	    	ListSelectionModel lsm = (ListSelectionModel)e.getSource();

	        int firstIndex = e.getFirstIndex();
	        int lastIndex = e.getLastIndex();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        System.out.println("Event for indexes "
	                      + firstIndex + " - " + lastIndex
	                      + "; isAdjusting is " + isAdjusting
	                      + "; selected indexes:");

	        if (lsm.isSelectionEmpty()) {
	            System.out.println(" <none>");
	        } 
	        else {
	            // Find out which indexes are selected.
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                   System.out.println(" " + i);
	                }
	            }
	        }
	    }
	}
	
	
	/**
	 * 
	 * @author bebeshina
	 * Classe : évenements concernant le champ name
	 */
	class NameAction implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{
	
			String newName = nom.getText();
			
			try 
			{
				editor.setPage("Entrée modifiée "+name.toUpperCase(Locale.FRENCH)+" : "+newName);
			} 
			catch (Exception ex) {}
		}
	}
	
	
	String thesaurus=null;
	@SuppressWarnings("unused")
	private DefaultListModel<String> dlm = new DefaultListModel<String>();
	@SuppressWarnings("unused")
	private final String[] thes = { "Termsciences","SIL Glossary of Linguistic Terms"};
	
	/**
	 * 
	 * @author bebeshina
	 * Classe qui permet de valider les informations saisies par l'utilisateur 
	 * ce qui se traduit par l'écriture en base et la sortie sous forme de log (fichier texte). 
	 *
	 */
	
	class MyListSelectionListener implements ListSelectionListener 
	{
	    public void contentsChanged(ListDataEvent e) 
	    {
	        System.out.println("Ligne sélectionnée: "+e.getSource());
	    }

		@Override
		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	
		
		
	class RechercheThesaurus  extends BrowseThesaurus implements ActionListener
		{
			/**
			 * Reinitialiser
			 */
			private static final long serialVersionUID = 1L;

			public RechercheThesaurus(String s) throws Exception 
			{
				super(s);
//				super.setVisible(true);

			}

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		}
	
	
	
	class Choix implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{
		
			
			System.out.println("tesaurus choisi "+thesaurus);
		}
	}
	class DefinitionAction implements ComponentListener 
	{
		public void actionPerformed(ActionEvent e) {
			// Переход по адресу
			String newDefinition = definition.getText();
			try 
			{
				editor.setPage("Nouvelle définition pour "+name.toUpperCase(Locale.FRENCH)+" : "+newDefinition);
			} 
			catch (Exception ex) {}
		}

		@Override
		public void componentHidden(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	class ExampleAction implements ComponentListener 
	{
		public void actionPerformed(ActionEvent e) {
		
			String newExample = example.getText();
			try 
			{
				editor.setPage("Nouvel example pour "+name.toUpperCase(Locale.FRENCH)+" : "+newExample);
			} 
			catch (Exception ex) {}
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentResized(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentShown(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
//	public TestInsert()throws ConfigurationException, ClassNotFoundException, SQLException, InstantiationException,
//	IllegalAccessException,IOException 
//    {
//		//entête de la fenêtre
//        super("Nouvelle information");
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        
//        // Создание текстовых полей
//        field = new JTextField("texte du champ", 25);
//        field.setToolTipText("Définition");
//        
//        // Настройка шрифта
//        field.setFont(new Font("Dialog", Font.PLAIN, 14));
//        field.setHorizontalAlignment(JTextField.RIGHT);
//        
//        // Слушатель окончания ввода
//        field.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                
//            	// Отображение введенного текста
//                JOptionPane.showMessageDialog(TestInsert.this, 
//                               "Information saisie : " + field.getText());
//                //récupération du texte saisi
//                info=field.getText();
//               
//               //process info ici 
//                int i=0;
//                try 
//                {
//                	i=dh.termeExiste(info);definition
//                	System.out.println("terme existe "+i+" "+"écriture dans un fichier "+f.getCanonicalPath());
//                	pw.println(i+" "+info);
//                	pw.flush();
//                	pw.close();
////					dh.createTerm(info);
//				} 
//                catch (Exception e1) 
//                {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//           	
//            }
//        }
//	
//        );
//        // Поле с паролем
//        JPasswordField password = new JPasswordField(12);
//        password.setEchoChar('*');
//        
//        // Создание панели с текстовыми полями
//        JPanel contents = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        contents.add(field);
//        setContentPane(contents);
//        
//        // Определяем размер окна и выводим его на экран
//        setSize(400, 130);
//        setVisible(true);
//    }
//	public String info;
//	
//	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException, SQLException, IOException {
//		String name="indexicalité";
//		System.out.println(name);
//		new TestInsert(name); 
//    }



	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
