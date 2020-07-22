package annotation;


import java.awt.Color;

import java.awt.Container;
import java.awt.Dimension;
//import java.awt.FlowLayout;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectCorpus extends JFrame implements ActionListener
{

	private static final long serialVersionUID = 1L;

	final Logger logger = LoggerFactory.getLogger(SelectCorpus.class);
	
	/**lister les  corpus */
			
	public Map<String,String> corpus_brut=new HashMap<String,String>();
	public Map<String,String> corpus_prean=new HashMap<String,String>();
	
	public void prepareMap() throws FileNotFoundException
	{
		File[] dir_b= new File("./corpus/brut/").listFiles();
		if(dir_b.length==0)
		{
			this.logger.info("Pas de corpus brut fourni.");
		}
		else 
		{
			for(File f:dir_b)
			{
				String key=f.getName().replaceAll("\\.(txt|csv|tsv)", "");
				this.corpus_brut.put(key, f.getAbsolutePath());
			}
			
			/**vérification*/
			
			this.logger.info("Corpus bruts disponibles...");
			for(Entry<String,String>e:corpus_brut.entrySet())
			{
				this.logger.info("{} {}",e.getKey(),e.getValue());
			}
		}
		
		File[] dir_pa= new File("./corpus/preannote/").listFiles();
		if(dir_pa.length==0)
		{
			this.logger.info("Pas de corpus pré-annoté fourni.");
		}
		else 
		{
			for(File f:dir_pa)
			{
				String key=f.getName().replaceAll("\\.(txt|csv|tsv)", "");
				this.corpus_prean.put(key, f.getAbsolutePath());
			}
			
			/**vérification*/
			
			this.logger.info("Corpus pré-annotés disponibles...");
			for(Entry<String,String>e:corpus_prean.entrySet())
			{
				this.logger.info("{} {}",e.getKey(),e.getValue());
			}
		}
	}
	

	
	/**entête*/
	JLabel select;
	
	
	/**valider le choix */
	
	public JButton commencer = new JButton("Commencer");
	
	public String path;
	public JLabel cb,ca;
	
	public JPanel createForm() throws FileNotFoundException
	{
		JPanel form=new JPanel();
		
		/**regroupement des boutons*/
		
		form.setBackground(Color.decode("#EAF4D3"));
		Box btest = Box.createHorizontalBox();
		Box corpus_b = Box.createVerticalBox();
		Box corpus_pa = Box.createHorizontalBox();
		
		JCheckBox check;
		this.prepareMap();
		for(Entry<String,String>e:corpus_brut.entrySet())
		{
			check=new JCheckBox(e.getKey());
			check.setOpaque(false);
			check.setForeground(Color.decode("#5C5E56"));
			corpus_b.add(check);
		}
		
		for(Entry<String,String>e:corpus_prean.entrySet())
		{
			
			check=new JCheckBox(e.getKey());
			check.setOpaque(false);
			check.setForeground(Color.decode("#5C5E56"));
			corpus_pa.add(check);
		}
		
		/** réfléchir à propos de l'alignement **/
		
		corpus_b.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		corpus_b.setOpaque(false);
		corpus_b.add(Box.createRigidArea(new Dimension(50, 25)));
		corpus_b.add(Box.createVerticalGlue());
		corpus_b.setAlignmentY(WIDTH);
		
		
		corpus_pa.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		corpus_pa.setOpaque(false);
		corpus_pa.setAlignmentY(RIGHT_ALIGNMENT);
		corpus_pa.add(Box.createRigidArea(new Dimension(50, 25)));
		corpus_pa.add(Box.createHorizontalGlue());
		
		
		form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
//		form.setLayout(new GridLayout(2,0));
		form.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.cb=new JLabel("Corpus bruts");
		cb.setForeground(Color.decode("#5C5E56"));
		btest.add(cb);
		form.add(btest);
		form.add(corpus_b);
		
		this.ca=new JLabel("Corpus pré-annotés");
		ca.setForeground(Color.decode("#5C5E56"));
		form.add(ca);
		form.add(corpus_pa);
		
		commencer.setBackground(Color.decode("#5C5E56"));
		commencer.setForeground(Color.WHITE);
		commencer.setEnabled(true);
		form.add(commencer);
		
		return form;
	}
	
	
	
	public SelectCorpus() throws Exception
	{
		super("Choix du corpus");
		Container container = getContentPane();
		container.add(this.createForm());
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300, 300);
		
		/**centrage */
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2 -this.getWidth()/2, dim.height/2 - this.getHeight()/2);
		setResizable(true);
        setVisible(true);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// TODO Auto-generated method stub
		
	} 
	

	
	public static void main(String[] args) throws Exception
	{
		new SelectCorpus();
	}
}


