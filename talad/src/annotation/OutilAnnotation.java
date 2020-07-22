package annotation;

import static org.junit.Assert.assertTrue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

//import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OutilAnnotation  extends JFrame implements ActionListener
{
	
	private static final long serialVersionUID = 3648853522368089146L;
	public static final String annotator="C"; //B,C etc

	/*logger*/
	
	final Logger logger = LoggerFactory.getLogger(OutilAnnotation.class);
	
	/*membres*/
	
	/*conteneurs*/
	public String date;
	public void getDate()
	{
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
		Date d = new Date();
		date=dateFormat.format(d); //2016/11/16 12:08:43
	}
	

//	public Integer getLastNid()throws IOException
//	{
//		int last=0;
//
//		ReversedLinesFileReader rlr=new  ReversedLinesFileReader(this.logfile,Charset.forName("UTF-8"));
//		String lastline="";
//		lastline=rlr.readLine();
//		
//		if(lastline!=null)
//		{
//			
//		}
//		
//		return last;
//		
//
//		
//	}
	public String txt;
	public JPanel panel, form, contenu, etiquettes, nomination, choix_plan, upanel, marqueurs_attitude, marqueurs_procede, p_relations;
	public JSplitPane splitPane;
	public JViewport vp;
	
	/*composants pour les listes/cases à cocher*/
	
	public JRadioButton rb=new JRadioButton();
	public JCheckBox cb=new JCheckBox();
	public ButtonGroup bg;
	
	/*tableau de résultats*/
	
	public JTable tableau;
	public JScrollPane scrollPane,textPane;
//	public DefaultTableModel dm = new DefaultTableModel(0, 0);
	
	/*texte et boutons*/
	
	public JTextPane texte,p_cotexte,m_procede,m_attitude;
	public JTextArea log, cotexte;
	public StyledDocument styledText;
	public JTextField f_nomination, f_marqueurs_pr, f_marqueurs_at,f_cotexte,f_repere;
	
	public JButton valider,reinitialiser,ok,bouton_nomination, bouton_repere, tout_valider, sauvegarder,suivant;
	
	/*stockage des informations dans des variables/collections/fichiers */
	public Integer nid;
	
	public File input;
	public  Map<Integer,File> inputMap=new HashMap<Integer,File>();
//	public File file=new File("output/annotations.txt");	
//	public File logfile=new File("output/log.txt");	
	public File file=new File("./annotations.txt");	
	public File logfile=new File("./log.txt");	
	
	/*éléments pour la gestion de la sélection*/
	
	public Set<Integer[]> coord=new HashSet<Integer[]>();
	public Map<String,Set<String>> map=new HashMap<String,Set<String>>();
	
	/*elements de mise en forme : layout, police etc*/

	public Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public  Map<String,Font> font_map=new HashMap<String,Font>();
	
	/**Fonctions génériques*/
	
	
	/*lecture dans le file*/
	public  String dirpath="./corpus_campagne";
//	public String dirpath="corpus/brut/campagne";
	public  void openFileDirectory()
	{
		File[] dir=new File(dirpath).listFiles();
		int i=0;
		while(i<dir.length-1)
		{
			for(File f:dir)
			{
				inputMap.put(i,f);
				i++;
			}
			
		}
		System.out.println("Files in the input directory "+inputMap.size());
	}
	public static  File current;
	public  Integer filecount=0;

	/**
	 * map pour stocker le coordonnées des morceaux sélectionnés 
	 */
	public Map<Integer[], String> lexmap=new HashMap<Integer[], String>();
	
	/**
	 * map pour stocker texte + les listes de termes et leurs coordonnées pour ce texte
	 */
	public Map<String, Map<Integer[], String>> docmap=new HashMap<String, Map<Integer[], String>>();
	
	/**
	 * 
	 * @throws FileNotFoundException
	 */
	
	public  void nextFile() throws FileNotFoundException
	{
		current=inputMap.get(filecount);
		
		corpusref=current.getName().replaceAll("\\.txt", "");
		System.out.println("CURRENT FILE "+current);
		this.setTitle("TALAD - WP2 - "+current.getAbsolutePath());
		nid=1;
		if(filecount<inputMap.size()-1)
		{
			filecount++;
		}
		else
		{
			filecount=0;
		}
	}
	
	public String corpusref;
	public void showText() throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(current.getAbsolutePath()));
		doctext=new String(encoded, Charset.forName("UTF-8"));
	}
	
	/*écriture fichiers */
	
	public void appendAnnotatedData(String s) throws IOException 
	{
		OutputStream os = null;
		
		OutputStreamWriter osw = null;
		try 
		{
			/**information de l'utilisateur */
			
			this.logger.info("Saving annotated segment : {}",s);
			this.logger.info("Segment length : {} ",s.length());
			this.logger.info("Destination file : {}",this.file.getPath());
			
			os = new FileOutputStream(this.file,true);
			osw=new OutputStreamWriter(os);
			osw.write(s, 0, s.length());
			osw.write("\r\n");
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
	/**
	 * sauvegarde des logs, écriture à chaque validation de l'annotation
	 * @param segment
	 * @throws IOException
	 */
	public void logAnnotatedData(String segment) throws IOException 
	{
		OutputStream os = null;
		
		OutputStreamWriter osw = null;
		try 
		{
			/**information de l'utilisateur */
			
			this.logger.info("Saving annotated segment : {}",segment);
			this.logger.info("Segment length : {} ",segment.length());
			this.logger.info("Destination file : {}",this.logfile.getPath());
			
			os = new FileOutputStream(this.logfile,true);
			osw=new OutputStreamWriter(os);
			osw.write(segment, 0, segment.length());
			osw.write("\n");
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
	/**
	 * reset
	 */
	public void done()
	{
		texte.setSelectionStart(0);
		texte.setSelectionEnd(0);
		for(AbstractButton b:cb_array)
		{
			b.setSelected(false);
		}
		cb_array.clear();
		coord.clear();
		selection="";
	}
	
	/*surlignage*/
	
	protected Highlighter h;
	
	public void highlight(JTextPane text,int start,int end, String couleur) throws BadLocationException
	{
		this.h = new DefaultHighlighter();
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.decode(couleur));
		text.setHighlighter(h);
		h.addHighlight(start, end, painter);
	}
	public void unhighlight(JTextPane text) throws BadLocationException
	{
		this.h = new DefaultHighlighter();
		text.setHighlighter(h);
		h.removeAllHighlights();
	}
	
	public String selection="";
	
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
	      StringBuilder sb=new StringBuilder();
	      for(String str:set)
	      {
	    	  sb.append(str+"; ");
	      }
	      	selection=sb.toString().replaceAll(",(?=$)","");
	      	coord.clear();
	      	
	      	try 
	      	{
	      		unhighlight(texte);
	      	} 
	      	catch (BadLocationException e2) 
	      	{
				// TODO Auto-generated catch block
				e2.printStackTrace();
	      	}
	} 
	class SelectionAction implements CaretListener
	{
		final Logger logger = LoggerFactory.getLogger(SelectionAction.class); 
		
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
				 highlight(texte, select[0], select[1], "#63c6f6");
			 } 
			 catch (BadLocationException e1) 
			 {
				e1.printStackTrace();
			 }
		}
	}
	
	class NominationAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			
			getMarks();
			selection=texte.getSelectedText();
			s_nomination=selection;
			f_nomination.setText(selection);
			f_nomination.setVisible(true);
			s_nomination=f_nomination.getText();

			log.append("\n"+"Nomination en cours de traitement :"+s_nomination+"\n");
			try  
			{
				unhighlight(texte);	
			} 
			catch (BadLocationException e) 
			{
				e.printStackTrace();
			}
			
		}
	}
	public String cotexte_coord;
	class CotexteAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			getMarks();
			
			selection=texte.getSelectedText();
			p_cotexte.setText(selection);
			p_cotexte.setEnabled(true);
			p_cotexte.setVisible(true);
			s_cotexte=p_cotexte.getText();
			
			//pour récupérer les coordonnées du cotexte
			
			/*référence fichier en cours*/
			
			current.getName();
			int start =texte.getSelectionStart();
			int end=texte.getSelectionEnd();
			cotexte_coord= start+","+end;
			////////////////////////////////////////////
			
			log.append("\n"+"Cotexte :"+s_cotexte+"\n");
			try 
			{
				unhighlight(texte);	
			} 
			catch (BadLocationException e) 
			{
				e.printStackTrace();
			}
			
			
			/************ test ***************/
			
			texte.setCaretPosition(0);
			done();
		}
	
	}
	
	class MarqueursProcedeAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			//s_type, s_cotexte, s_marqueurs_procede, s_marqueurs_attitude, s_annotation_pr, s_annotation_pl, s_annotation_at, s_repere,s_relation;
//			public Vector<Object> modif=new Vector(11,1);
			selection="";
			 
			getMarks();
			m_procede.setText(selection);
			s_marqueurs_procede=m_procede.getText();
			log.append("\n"+"Marqueurs procede :"+s_marqueurs_procede+"\n");
			
			try
			{
				unhighlight(texte);	
			} 
			catch (BadLocationException e) 
			{
				e.printStackTrace();
			}
			
			done();
		}
	}
	
	class MarqueursAttitudeAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			selection="";
			System.out.println("selection avant getMarks() "+selection);
			getMarks();
			System.out.println("selection après getMarks() "+selection);
			
			m_attitude.setText(selection);
			s_marqueurs_attitude=selection;
			log.append("\n"+"Marqueurs attitude :"+s_marqueurs_attitude+"\n");
			
			
				try 
				{
					unhighlight(texte);	
				} 
				catch (BadLocationException e) 
				{
					e.printStackTrace();
				}
			
			done();
		}
	}
	
	class RepereAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			getMarks();
			selection=texte.getSelectedText();
			
			f_repere.setText(selection);
			s_repere=selection;
			log.append("\n"+"repere :"+s_repere+"\n");
			try 
			{
				unhighlight(texte);	
			} 
			catch (BadLocationException e) 
			{
				e.printStackTrace();
			}
			
			done();
		}
	}
	
	public Set<AbstractButton> cb_array=new HashSet<AbstractButton>();

	protected class ItemEvents  implements ItemListener
	{
		private AbstractButton b;
		
		protected ItemEvents(AbstractButton cb)
		{
			this.b=cb;
		}
		
		final Logger logger = LoggerFactory.getLogger(ItemEvents.class);
		
		@Override
		public void itemStateChanged(ItemEvent e) 
		{
			if (e.getStateChange() == ItemEvent.SELECTED) 
			{
				/**test */
				if(b instanceof JRadioButton)
				{
					if (e.getStateChange() == ItemEvent.SELECTED) 
					{
						this.logger.info("Item name (radio button) {}",b.getText());
						System.out.println(b.getParent());
						if(b.getText().startsWith("r"))
						{
							s_relation=b.getText();
						}
						else
						{
							s_type=b.getText();
						}
					}
					if(e.getStateChange()==ItemEvent.DESELECTED)
					{
						if(b.getText().startsWith("r"))
						{
							s_relation="";
						}
						else
						{
							s_type="";
						}
						
					}
				}
				if(b instanceof JCheckBox)
				{
				cb_array.add(b);

					this.logger.info("item selected {} {}",b.getText(),b.getParent().getName());
					
					if (e.getStateChange() == ItemEvent.DESELECTED) 
					{ 
						
						try 
						{
							unhighlight(texte);
						} 
						catch (BadLocationException e1) 
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
			coord.clear();
		}
		
	}
	public String s_nomination;

	public String s_type="";
	public String s_cotexte="";
	public String s_annotation_pr="";
	public String s_annotation_pl="";
	public String s_annotation_at="";
	public String s_relation="";
	public String s_marqueurs_procede="";
	public String s_marqueurs_attitude="";
	public String s_repere="";
	
	public String ref_corpus;
	
	class OkAction implements ActionListener
	{
		private AbstractButton b;
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			/**
			 *  récolter les changements d'état, construire une annotation mixte
			 *  */
			
			for(AbstractButton c:cb_array)
			{
				b=c;
			
				String parent=b.getParent().getName();
				String name=b.getText();
				String etiquette=Etiquettes.valeur(parent+" "+name);
				
//				s_annotation_pr="";
//				s_annotation_pl="";
//				s_annotation_at="";
//				s_type="";
//				s_relation="";
				
				switch(parent)
				{
					case "plan": 
						
						s_annotation_pl=s_annotation_pl+","+etiquette;
						log.append("plan "+s_annotation_pl);
						
						break;
				
					case "procédé":
						
						s_annotation_pr=s_annotation_pr+","+etiquette;
						log.append("procédé "+s_annotation_pr);
						break;
				
					case "attitude":
			
						s_annotation_at=s_annotation_at+","+etiquette;
						log.append("attitude "+s_annotation_at);
						
						break;
						
					case "type":
						s_type=etiquette;
						log.append("type "+s_type);
						
						break;
						
					case "relations":
						s_relation=etiquette;
						
						log.append("relation "+s_relation);
						break;
				}
			}
			
			try 
			{
				unhighlight(texte);
			} 
			catch (BadLocationException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			done();
		}
	}
	
	class SauvegarderAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			// TODO Auto-generated method stub
			String data="";
			StringBuilder sb=new StringBuilder();
			@SuppressWarnings("unchecked")
			Vector<Vector> vec = dm.getDataVector();
			
			for(int count=0; count<vec.size();count++)
			{
				sb.append(vec.get(count).toString()+"\t");
			}
			
//			for (int count = 0; count <= dm.getRowCount(); count++)
//			{
//				  sb.append(dm.getValueAt(count, 0).toString()+"\t");
//				  System.out.println(dm.getValueAt(count, 0).toString());
//			}
			data=sb.toString().replaceAll("\t(?=$)", "");
			System.out.println(data);
			try 
			{
				appendAnnotatedData(data+"\n");
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		dm.setRowCount(0);
//		texte.setSelectionStart(0);
//		texte.setSelectionEnd(0);
//		coord.clear();
//		selection="";
		
		done();
		}
		
	}
	
	class ValiderAction implements ActionListener
	{
		public void reinit()
		{
			s_nomination="";
			s_type="";
			s_cotexte="";
			s_annotation_pl="";
			s_marqueurs_procede="";
			s_annotation_pr="";
			s_marqueurs_attitude="";
			s_annotation_at="";
			s_repere="";
			s_relation="";
			f_nomination.setText("");
			setVisible(true);
			p_cotexte.setText("");
			setVisible(true);
			m_procede.setText("");
			setVisible(true);
			m_attitude.setText("");
			setVisible(true);
			f_repere.setText("");
			setVisible(true);
			texte.setSelectionStart(0);
			texte.setSelectionEnd(0);
			selection="";
			cotexte_coord="";
			coord.clear();
			
		}
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{			
				try 
				{
					/**test tableau*/	
					
					
					LinkedList<Object> modifs= new LinkedList<Object>();
//					modifs.add(0,corpusref);
//					modifs.add(1,annotator);
//					modifs.add(2,date);
//					modifs.add(3,nid);
//					modifs.add(4,s_nomination);
//					modifs.add(5,s_type);
//					modifs.add(6,s_cotexte);
//					modifs.add(7,s_annotation_pl);
//					modifs.add(8,s_marqueurs_procede);
//					modifs.add(9,s_annotation_pr);
//					modifs.add(10,s_marqueurs_attitude);
//					modifs.add(11,s_annotation_at);
//					modifs.add(12,s_repere);
//					modifs.add(13,s_relation);
					
					modifs.add(0,corpusref);
					modifs.add(1,annotator);
					modifs.add(2,date);
					modifs.add(3,nid);
					modifs.add(4,f_nomination.getText());
					modifs.add(5,s_type);
					if(p_cotexte.getText()!=null)
					{
						modifs.add(6,p_cotexte.getText());
					}
					else
					{
						modifs.add(6,"");
					}
					
//					modifs.add(7,s_annotation_pl);
//					modifs.add(8,s_marqueurs_procede);
//					modifs.add(9,s_annotation_pr);
//					modifs.add(10,s_marqueurs_attitude);
//					modifs.add(11,s_annotation_at);
//					modifs.add(12,s_repere);
//					modifs.add(13,s_relation);
//					
					modifs.add(7,s_annotation_pl);
					modifs.add(8,m_procede.getText());
					modifs.add(9,s_annotation_pr);
					modifs.add(10,m_attitude.getText());
					modifs.add(11,s_annotation_at);
					modifs.add(12,f_repere.getText());
					modifs.add(13,s_relation);
					modifs.add(14,cotexte_coord);
					/**
					 * TODO rajouter le numéro de fichier
					 */
					
					log.append(String.valueOf(modifs));
					System.out.println(modifs);
				    dm.addRow(new Vector<Object>(modifs));
				    
				    logAnnotatedData("ANNOT "+String.valueOf(modifs));
				    
				    nid++;
				    cb_array.clear();
				    coord.clear();
				    modifs.clear();
				    this.reinit();
				} 
				catch (Exception e) 
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
	
	class SuivantAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			try 
			{
				nextFile();
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try 
			{
				showText();
				update();
				coord.clear();
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadLocationException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//				container.setName("Annotation - TALAD WP2"+" - "+current.getName().replaceAll(".txt", ""));
//				setVisible(true);
			
		}
	}
	protected class MouseEvents implements MouseListener 
	{
		final Logger logger = LoggerFactory.getLogger(MouseEvents.class); 
		
		public void mousePressed(MouseEvent e) 
		{
//			this.logger.info("Mouse presssed {} of clicks: ", e.getClickCount(), e);
		}

	    public void mouseReleased(MouseEvent e) 
	    {
//	    	this.logger.info("Mouse released {} of clicks: ", e.getClickCount(), e);
	    }
		@Override
		public void mouseClicked(MouseEvent arg0) 
		{
			texte.setSelectionStart(0);
			texte.setSelectionEnd(0);
			coord.clear();
			
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
	
	
	public Box build3FoldVerticalBox(String name,String[]str, AbstractButton o, String color,boolean multiple_selection)
	{
		Box box=Box.createVerticalBox();
		box.setName(name);
		assertTrue(o instanceof JRadioButton||o instanceof JCheckBox);
		if(o instanceof JRadioButton)
		{
			Box one=Box.createVerticalBox();
			Box two=Box.createVerticalBox();
			Box three=Box.createVerticalBox();
			one.setName(name);
			two.setName(name);
			three.setName(name);
			this.bg=new ButtonGroup();
		
			for(int i=0;i<=str.length-1;i++)
			{
				o=new JRadioButton(str[i]);
				o.setSize(25, 5);
				o.addItemListener(new ItemEvents(o));
				o.setBackground(Color.decode(color));
				o.setFont(Polices.intitule.getFont());
				
				if(!multiple_selection)
				{
					
					bg.add((AbstractButton) o);
				}
				int check=str.length%3;
				
				int borne=0;
				
				if(check==1)
				{
					borne=str.length;
				}
				else
				{
					borne=str.length-1;
				}
				
				
				if(i<=borne/3)
				{
					one.add(o);
					
				}
				if((i>borne/3)&&(i<=borne*2/3))
				{
					two.add(o);
					
					
				}
				if((i>borne*2/3))
				{
					three.add(o);
					
				}
			}
			Box utils=Box.createHorizontalBox();
				
				one.setAlignmentX(LEFT_ALIGNMENT);
				one.setBackground(Color.decode(color));
				utils.add(one);

				two.setAlignmentX(CENTER_ALIGNMENT);
				two.setBackground(Color.decode(color));
				utils.add(two);
				
				three.setAlignmentX(RIGHT_ALIGNMENT);
				three.setBackground(Color.decode(color));
				utils.add(three);
				
				box.add(utils);
			}
		box.setBackground(Color.decode(color));
		box.setOpaque(true);
		box.setVisible(true);
		return box;
	}
	
	public Box buildVerticalBox(String name,String[]str, AbstractButton o, String color, boolean multiple)
	{
		Box box=Box.createVerticalBox();
		this.bg=new ButtonGroup();
		JLabel lname=new JLabel(name,JLabel.LEFT);
		lname.setFont(Polices.intitule.getFont());
		
		assertTrue(o instanceof JRadioButton||o instanceof JCheckBox);
		if(o instanceof JRadioButton)
		{
			Box one=Box.createVerticalBox();
			Box two=Box.createVerticalBox();
			
			for(int i=0;i<=str.length-1;i++)
			{
				o=new JRadioButton(str[i]);
				o.setSize(25, 5);
				o.addItemListener(new ItemEvents(o));
				o.setBackground(Color.decode(color));
				if(!multiple)
				{
					bg.add((AbstractButton) o);
				}
				
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
				if(!multiple)
				{
					bg.add((AbstractButton) o);
				}
				
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
	
	
	
	public void update() throws BadLocationException
	{
		style_normal = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style_normal,"Times");
		StyleConstants.setFontSize(style_normal, 18);
		StyleConstants.setBackground(style_normal,Color.WHITE);
		StyleConstants.setForeground(style_normal,Color.DARK_GRAY);
		texte.setText("");
		this.styledText=texte.getStyledDocument();
		styledText.insertString(styledText.getLength(), doctext, style_normal);
		container.setName("Annotation - TALAD WP2 - "+current.getName());
		setVisible(true);
		coord.clear();
	}
	
	public SimpleAttributeSet style_normal;
	public void formatTextPane() throws BadLocationException
	{
		this.texte=new JTextPane();

		/** style de texte  */
		
		style_normal = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style_normal,"Times");
		StyleConstants.setFontSize(style_normal, 18);
		StyleConstants.setBackground(style_normal,Color.WHITE);
		StyleConstants.setForeground(style_normal,Color.DARK_GRAY);
		
		/** récupération de style */
		
		this.styledText=texte.getStyledDocument();

		styledText.insertString(styledText.getLength(), doctext, style_normal);
		
		this.texte.setPreferredSize(new Dimension(200, 300));
//		this.texte.setText("(7) Vous ne voyez pas comment on peut sanctionner plus fortement le harcèlement de rue, notamment vous ne pensez pas que ça sert à quelque chose une loi, vous pensez que ça ne marchera pas ? "
//				+ "Symboliquement, c'est important de faire avancer ce débat parce que c'est une vraie réalité qui est vécue par des millions de femmes dans ce pays");
		this.texte.setToolTipText("sélectionnez un ou plusieurs segments et choisissez une étiquette pour ceux-ci");
		
		this.texte.setAutoscrolls(true);
		this.texte.setPreferredSize(new Dimension((int)Math.round(2*screenSize.getWidth()/3)-10, (int)Math.round(screenSize.getHeight()*0.75)));
		
		texte.addCaretListener(new SelectionAction());
		texte.addMouseListener(new MouseEvents());
		
		
		this.texte.setEditable(true);
		this.texte.setEnabled(true);
		this.texte.setVisible(true);
		
		this.textPane=new JScrollPane(texte,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.textPane.setPreferredSize(texte.getSize());
		this.textPane.setPreferredSize(new Dimension((int)Math.round(2*screenSize.getWidth()/3)-10, (int)Math.round(screenSize.getHeight()*0.75)));
		this.textPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.textPane.setBackground(Color.WHITE);
		this.textPane.setLocation(10, 36);
		this.textPane.addMouseListener(new MouseEvents());
			
		this.vp = textPane.getViewport();
		
		/****/
		
		vp.addChangeListener(new ScrollListener());
		vp.setFont(Polices.texte.getFont());
		vp.add(texte);

	}

	class ScrollListener implements ChangeListener {
	    public void stateChanged(ChangeEvent e) 
	    {
	        done(); 
	    }
	}
	public void setUpNominationSelection()
	{
		this.nomination=new JPanel();
		
		this.setLocationByPlatform(true);
		setLocationRelativeTo(null);
		this.nomination.setBackground(Color.decode("#beccd0"));
//		this.nomination.setPreferredSize(new Dimension(50,this.etiquettes.getHeight()/3));
//		this.nomination.setLayout(new BorderLayout(5,5));
//		nomination.setBorder(new EmptyBorder(10));
		
		this.bouton_nomination=new JButton("nomination");
//		bouton_nomination.addActionListener(new ValiderNominationAction());
		bouton_nomination.setSize(2, 5);
		bouton_nomination.addActionListener(new NominationAction());
		bouton_nomination.setEnabled(true);

		f_nomination=new JTextField(25);
		f_nomination.setFont(Polices.texte.getFont());
		f_nomination.setMargin(new Insets(5,5,5,5));
		f_nomination.setEditable(true);
		f_nomination.setVisible(true);
		nomination.add(bouton_nomination,BorderLayout.WEST);
		nomination.add(f_nomination,BorderLayout.CENTER);
	}

	public void setupNomination()
	{
		choix_plan=new JPanel();
		choix_plan.setLayout(new BorderLayout(5,5));
//		JLabel n=new JLabel("<html>Nomination et son type<br /><html>");
//		n.setFont(Polices.intitule.getFont());
//		choix_plan.add(n,BorderLayout.NORTH);
		choix_plan.setBackground(Color.decode("#beccd0"));
		
		String[]t_etiquettes= {"entité humaine","objet physique (naturel ou artefact)","entité abstraite","processus","événement","lieu"};
		choix_plan.add(this.build3FoldVerticalBox("type",t_etiquettes,rb,"#beccd0",false));
		
		JPanel bouton=new JPanel();
		bouton.setLayout(new BorderLayout(5,5));
		bouton.setBackground(Color.decode("#beccd0"));
		this.valider=new JButton("Valider le type d'entité");
		this.valider.addActionListener(new OkAction());
		valider.setMargin(new Insets(5,5,5,5));
		bouton.add(valider,BorderLayout.EAST);
		choix_plan.add(bouton, BorderLayout.SOUTH);
//		choix_plan.setBorder(new EtchedBorder(10));
		choix_plan.setOpaque(true);
		choix_plan.setEnabled(true);
		nomination.add(choix_plan);
	}
	
	public JPanel plan_panel;
	
	public Box plan= Box.createHorizontalBox();
	
	public void setUpPlan()
	{
		plan_panel=new JPanel();
		plan_panel.setLayout(new BoxLayout(plan_panel,BoxLayout.Y_AXIS));
		plan_panel.setBackground(Color.decode("#9abbc5"));
		plan_panel.setPreferredSize(new Dimension(50,this.etiquettes.getHeight()/3));
		
		Box ubox=Box.createHorizontalBox();
		ubox.setPreferredSize(new Dimension(5, 5));
		ubox.setBorder(new EmptyBorder(10,10,10,10));
//		ubox.setAlignmentY(BOTTOM_ALIGNMENT);
	
		this.p_cotexte=new JTextPane();

		/** style de texte  */
		
		SimpleAttributeSet style_normal = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style_normal, "Arial");
		StyleConstants.setFontSize(style_normal, 10);
		StyleConstants.setBackground(style_normal,Color.WHITE);
		StyleConstants.setForeground(style_normal,Color.DARK_GRAY);
		
		/** récupération de style */
		
		this.styledText=p_cotexte.getStyledDocument();

		this.p_cotexte.setPreferredSize(new Dimension(1, 1));
		this.p_cotexte.setMargin(new Insets(5,5,5,5));
//		this.p_cotexte.addCaretListener(new SelectionAction());
		this.p_cotexte.setEditable(true);
		this.p_cotexte.setEnabled(true);
		this.p_cotexte.setVisible(true);
		
		this.p_cotexte.setText("pas de cotexte");
		
		this.ok=new JButton ("  cotexte  ");
		Dimension d=bouton_nomination.getSize();
		ok.setSize(d);
		ok.addActionListener(new CotexteAction());
		ubox.add(Box.createHorizontalStrut(10));
		ubox.add(ok);
		ubox.add(Box.createHorizontalStrut(10));
		ubox.add(p_cotexte);
		
		String[]s_plan= {"ontologique","langagier","indéterminé"};
		
		plan.setName("plan");
		JLabel pl=new JLabel("Plan");
		pl.setFont(Polices.titre.getFont());
		plan.add(pl);
		plan.setFont(Polices.intitule.getFont());
		plan.setOpaque(true);
		plan.setBackground(Color.decode("#9abbc5"));
		plan.add(Box.createHorizontalStrut(10));
		plan.setAlignmentX(CENTER_ALIGNMENT);
		
		for(int i=0;i<=s_plan.length-1;i++)
		{
			cb=new JCheckBox(s_plan[i]);
			cb.setOpaque(false);
			cb.setSize(25, 5);
			cb.setFont(Polices.texte.getFont());
			cb.addItemListener(new ItemEvents(cb));
			plan.add(cb);
		}
		this.valider=new JButton("Valider le plan");
		this.valider.setAlignmentX(RIGHT_ALIGNMENT);
		this.valider.addActionListener(new OkAction());
		this.valider.setMargin(new Insets(5,5,5,5));
		this.valider.setEnabled(true);
		plan.add(Box.createVerticalStrut(50));
		plan.add(valider);
		plan.add(Box.createVerticalStrut(10));
		plan_panel.add(ubox);
		plan_panel.add(plan);	
	}

	public void setUpMarqueursProcede()
	{
		marqueurs_procede=new JPanel();
		marqueurs_procede.setLayout(new BoxLayout(marqueurs_procede,BoxLayout.Y_AXIS));
		
		m_procede=new JTextPane();
		
		SimpleAttributeSet style_normal = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style_normal, "Arial");
		StyleConstants.setFontSize(style_normal, 10);
		StyleConstants.setBackground(style_normal,Color.WHITE);
		StyleConstants.setForeground(style_normal,Color.DARK_GRAY);
		
		this.styledText=m_procede.getStyledDocument();
		
		this.m_procede.setPreferredSize(new Dimension(1, 1));
		this.m_procede.setMargin(new Insets(5,5,5,5));
		this.m_procede.setEditable(true);
		this.m_procede.setEnabled(true);
		this.m_procede.setVisible(true);
		
		JButton bm_procede=new JButton("marqueurs procédé");
		bm_procede.addActionListener(new MarqueursProcedeAction());
		bm_procede.setEnabled(true);
		
		Box ubox=Box.createHorizontalBox();
		ubox.setPreferredSize(new Dimension(5, 5));
		ubox.setBorder(new EmptyBorder(5,5,5,5));
		ubox.add(Box.createHorizontalStrut(10));
		ubox.add(bm_procede);
		ubox.add(Box.createHorizontalStrut(10));
		ubox.add(m_procede);
		
		marqueurs_procede.add(ubox);
		marqueurs_procede.setVisible(true);
	}
	
	public Box procede= Box.createHorizontalBox();
	
	public void setUpChoixProcede()
	{
		String[]s_procede= {"introduction","ajustement","rejet"};
		
		/** PROCEDE */
		
		ButtonGroup pg=new ButtonGroup();
		procede.setName("procédé");
		procede.add(new JLabel("Procédé"));
		procede.add(Box.createRigidArea(new Dimension(5, 10)));
		procede.setOpaque(true);
//		procede.setBackground(Color.decode("#9ca772"));
		procede.setAlignmentY(RIGHT_ALIGNMENT);
//		

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
		procede.add(Box.createVerticalStrut(20));
		this.valider=new JButton("Valider le procédé");
		valider.setMargin(new Insets(5,5,5,5));
		this.valider.addActionListener(new OkAction());
		valider.setEnabled(true);
		procede.add(valider);
		procede.setVisible(true);
	}
	
	public void setUpMarqueursAttitude()
	{
		marqueurs_attitude=new JPanel();
		marqueurs_attitude.setLayout(new BoxLayout(marqueurs_attitude,BoxLayout.Y_AXIS));
		
		m_attitude=new JTextPane();
		
		SimpleAttributeSet style_normal = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style_normal, "Arial");
		StyleConstants.setFontSize(style_normal, 10);
		StyleConstants.setBackground(style_normal,Color.WHITE);
		StyleConstants.setForeground(style_normal,Color.DARK_GRAY);
		
		this.styledText=m_attitude.getStyledDocument();
		
		this.m_attitude.setPreferredSize(new Dimension(2, 2));
		this.m_attitude.setMargin(new Insets(10,10,10,10));
		this.m_attitude.setEditable(true);
		this.m_attitude.setEnabled(true);
		this.m_attitude.setVisible(true);
		
		JButton bm_attitude=new JButton("marqueurs attitude");
		bm_attitude.addActionListener(new MarqueursAttitudeAction());
		bm_attitude.setEnabled(true);
		
		Box ubox=Box.createHorizontalBox();
		ubox.setPreferredSize(new Dimension(5, 5));
		ubox.setBorder(new EmptyBorder(10,10,10,10));
		ubox.add(Box.createHorizontalStrut(10));
		ubox.add(bm_attitude);
		ubox.add(Box.createHorizontalStrut(10));
		ubox.add(m_attitude);
		
		marqueurs_attitude.add(ubox);
		marqueurs_attitude.setVisible(true);
		marqueurs_attitude.setEnabled(true);
	}
	
	public Box attitude= Box.createHorizontalBox();
	
	public void setUpChoixAttitude()
	{
		String[]s_attitude= {"prise en charge", "interaction", "cadrage"};
		
		/** ATTITUDE */
		attitude.setName("attitude");
		attitude.add(new JLabel("Attitude"));
		attitude.add(Box.createRigidArea(new Dimension(5, 10)));
		attitude.setOpaque(true);
//		attitude.setBackground(Color.decode("#d2b4c5"));
		attitude.setSize(50,50);
		for(int i=0;i<=s_attitude.length-1;i++)
		{
			cb=new JCheckBox(s_attitude[i]);
			cb.setSize(25, 5);
			cb.setOpaque(false);
			cb.addItemListener(new ItemEvents(cb));
			attitude.add(cb);
		
		}
		attitude.add(Box.createVerticalStrut(20));
		this.valider=new JButton("Valider attitude");
		valider.setMargin(new Insets(5,5,5,5));
		this.valider.addActionListener(new OkAction());
		valider.setEnabled(true);
		attitude.add(valider);
		attitude.setVisible(true);
	}
	
	public Box relations= Box.createVerticalBox();
	
	public void setUpRelations()
	{
		p_relations=new JPanel();
		p_relations.setLayout(new BoxLayout(p_relations,BoxLayout.X_AXIS));
		
		f_repere=new JTextField(25);

		this.f_repere.setMargin(new Insets(5,5,5,5));
//		this.f_repere.setPreferredSize(new Dimension(1, 1));
//		this.f_repere.setBounds(0,0,10,10);
		this.f_repere.setEditable(true);
		this.f_repere.setVisible(true);
		
		JButton b_repere=new JButton("entité-repère");
		b_repere.addActionListener(new RepereAction());
		b_repere.setEnabled(true);
		
		Box ubox=Box.createVerticalBox();
//		ubox.setPreferredSize(new Dimension(5, 5));
		ubox.setBorder(new EmptyBorder(10,10,10,10));
		ubox.add(Box.createHorizontalStrut(10));
		ubox.add(b_repere);
		ubox.add(Box.createVerticalStrut(10));
		ubox.add(f_repere);
		ubox.setVisible(true);
		
		p_relations.add(ubox);
		
		String[]r_etiquettes= {"r_association","r_temporelle","r_causale","rs_identite", "rs_appartenance", "rs_inclusion", "rs_partie_tout", "rs_localisation", "rs_ruption"};
//		relations.add(new JLabel("Relations possibles (présence d'un repère)"));
		relations.add(Box.createVerticalStrut(10));
		relations.add(this.build3FoldVerticalBox("relation",r_etiquettes,rb,"#e0e9ec", false));
		relations.add(Box.createVerticalStrut(10));
		
		this.valider=new JButton("Valider relations");
		valider.setMargin(new Insets(5,5,5,5));
		valider.addActionListener(new OkAction());
		valider.setEnabled(true);
		
		relations.add(valider);
		relations.setEnabled(true);
		relations.setVisible(true);
		p_relations.add(relations);
		
	}

	/**
	 * construction du TABLEAU D'ANNOTATION
	 */
	public JPanel tab=new JPanel();
	
	/*TODO traitement des identifiants d'une annotation*/
	
	
	public JLabel l;
	public DefaultTableModel dm;
	public void setUpTableau()
	{
		/** entête  */
		
		 dm= new DefaultTableModel(0, 0);
		String [] labels= {"corpus", "annotateur", "date","nid","nomination","type nomination","cotexte", "Pl", "mPr", "aPr","mAt", "aAt","repère","relation","coord cotexte"};
		dm.setColumnIdentifiers(labels);

		tableau=new JTable(dm);
		tableau.setShowVerticalLines(true);
		tableau.setCellSelectionEnabled(true);
		tableau.setColumnSelectionAllowed(true);
		tableau.setBorder(new LineBorder(Color.decode("#45493b")));
		tableau.setFont(Polices.texte.getFont());
		tableau.setVisible(true);
		
		setResizable(true);
		
		TableColumn column = null;
		for (int i = 0; i < 7; i++) {
		    column = tableau.getColumnModel().getColumn(i);
		    if (i == 0) {
		        column.setMinWidth(20);
		    } else {
		        column.setMinWidth(100);
		    }
		}
 
		tableau.setFillsViewportHeight(true);
//		tableau.getTableHeader().setBackground(Color.decode("#67665a"));
		tableau.getTableHeader().setForeground(Color.DARK_GRAY);
		tableau.getTableHeader().setFont(Polices.intitule.getFont());
		/**
		 * TODO set columns editable
		 */
	    scrollPane = new JScrollPane(tableau,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    scrollPane.setLayout(new ScrollPaneLayout());
	    scrollPane.setVisible(true);

	}
	public JScrollPane suivi;
	public void setUpSuivi()
	{
/**Construction d'un champs de suivi des annotations*/
		
		
		this.log=new JTextArea(8,80);
		this.log.setEditable(true);
		this.log.setLineWrap(true);
		this.log.setText(
				"Commentaire :"
						+"\n");
		this.log.setToolTipText("Champ de suivi des annotations en cours, vous pouvez ajouter un commentaire dans ce champ");
		this.log.setLayout(new BorderLayout(0,5));
		this.log.setBackground(Color.WHITE);
		this.log.setForeground(Color.RED);
		this.log.setVisible(true);
		this.log.setEnabled(true);
		suivi=new JScrollPane(this.log,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		suivi.add(this.log);
		
		this.vp = suivi.getViewport();
		vp.setFont(Polices.log.getFont());
		vp.add(log);

		
		setResizable(true);
	}
	
	public void formatEtiquettes()
	{
		this.etiquettes=new JPanel();
		this.etiquettes.setPreferredSize(new Dimension((int)Math.round(screenSize.getWidth()/3.5-15), (int)Math.round(screenSize.getHeight()*0.75)));
		
		this.etiquettes.setBackground(Color.decode("#e0e9ec"));
		this.setUpNominationSelection();
		this.setupNomination();
		this.setUpMarqueursProcede();
		this.setUpMarqueursAttitude();
		this.setUpChoixProcede();
		this.setUpChoixAttitude();
		this.setUpRelations();
		this.setUpPlan();
		
		this.etiquettes.setLayout(new GridLayout(5,1));
//		this.etiquettes.setLayout(new BoxLayout(etiquettes, BoxLayout.Y_AXIS));
		this.etiquettes.add(nomination);
//		this.etiquettes.add(Box.createVerticalStrut(20));
//		this.etiquettes.add(choix_plan);
		this.etiquettes.add(plan_panel);
		marqueurs_procede.add(procede);
		this.etiquettes.add(marqueurs_procede);
		marqueurs_attitude.add(attitude);
		this.etiquettes.add(marqueurs_attitude);
		etiquettes.add(p_relations);
		
	
		this.etiquettes.setVisible(true);
	}
	
	public void createForm() throws BadLocationException
	{
		
		/* ajout et placement des conteneurs */
		
		this.form=new JPanel();
		this.form.getSize();
		this.form.setBackground(Color.decode("#c3d3df"));
		this.form.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.formatTextPane();
		this.formatEtiquettes();

		/**
		 * combinaison des composants en split pane
		 */
		
		this.splitPane= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textPane, etiquettes);
		this.form.add(splitPane);
		
		this.setUpTableau();
		this.setUpSuivi();
		
		Dimension minimumSize = new Dimension(100, 50);
		scrollPane.setMinimumSize(minimumSize);
		suivi.setMinimumSize(minimumSize);
		
//		this.log.setPreferredSize(new Dimension((int)Math.round(screenSize.getWidth()/), (int)Math.round(screenSize.getHeight()*0.25)));
		JSplitPane bottom=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, suivi);
		bottom.setPreferredSize(new Dimension((int)Math.round(screenSize.getWidth()-100), (int)Math.round(screenSize.getHeight()*0.10)));
		bottom.setOneTouchExpandable(true);
		bottom.setResizeWeight(0.5);
        bottom.setContinuousLayout(true);
		bottom.setDividerLocation(1272);


		this.form.add(bottom);
		tout_valider=new JButton("Valider l'ensemble des annotations");
		tout_valider.addActionListener(new ValiderAction());
		
		sauvegarder=new JButton("Sauvegarder");
		sauvegarder.addActionListener(new SauvegarderAction());
		sauvegarder.setEnabled(true);
		
		suivant=new JButton("Suivant");
		suivant.addActionListener(new SuivantAction());
		suivant.setEnabled(true);
		
//		upanel=new JPanel();
//		upanel.setLayout(new BorderLayout());
		form.add(tout_valider,BorderLayout.CENTER);
		form.add(sauvegarder);
		form.add(suivant);
		
		
		this.form.setVisible(true);

	}
	public static String doctext;
	public Container container;
	
	public OutilAnnotation() throws BadLocationException, IOException
	{
		super("Annotation - TALAD WP2");
		this.getDate();
		this.openFileDirectory();
		this.nextFile();
		this.showText();
//		this.update();
		container = getContentPane();
		
		
		this.createForm();
		revalidate();
		container.add(form);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(screenSize);
		setLocation(screenSize.width/2 -this.getWidth()/2, screenSize.height/2 - this.getHeight()/2);
		setResizable(true);
	    setVisible(true);

	}
	
	public static void main(String[]args) throws BadLocationException, IOException
	{

		new OutilAnnotation();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
}
